package dash.dashmode.portal;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.class_5459;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DashPortalForcer extends PortalForcer {
    private final ServerWorld world;
    private final IPortalDesciption desciption;
    private final List<Direction.Axis> axises;

    public DashPortalForcer(ServerWorld world, IPortalDesciption desciption) {
        super(world);
        this.world = world;
        this.desciption = desciption;
        axises = Arrays.asList(Direction.Axis.X, Direction.Axis.Z, Direction.Axis.Y);
    }

    /**
     * Builds up a new portal
     *
     * @param blockPos
     * @param axis
     * @return
     */
    @Override
    public Optional<class_5459.class_5460> method_30482(BlockPos blockPos, Direction.Axis axis) {
        Optional<class_5459.class_5460> optional = super.method_30482(blockPos, axis);

        if (optional.isPresent()) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();

            final BlockPos blockPos2 = optional.get().field_25936;
            Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
            Direction direction2 = direction.rotateYClockwise();

            BlockState airDefaultState = Blocks.AIR.getDefaultState();

            for (int r = -1; r < 3; ++r) {
                for (int o = -1; o < 4; ++o) {
                    if (r == -1 || r == 2 || o == -1 || o == 3) {
                        mutable.set(blockPos2, r * direction.getOffsetX(), o, r * direction.getOffsetZ());
                        world.setBlockState(mutable, airDefaultState, 3);
                    }
                }
            }

            for (int o = -1; o < 2; ++o) {
                for (int p = 0; p < 2; ++p) {
                    for (int q = -1; q < 3; ++q) {
                        mutable.set(blockPos2, p * direction.getOffsetX() + o * direction2.getOffsetX(), q, p * direction.getOffsetZ() + o * direction2.getOffsetZ());
                        this.world.setBlockState(mutable, airDefaultState);
                    }
                }
            }

            desciption.placePortal(world, blockPos2, direction, direction2);
        }

        return optional;
    }

    /**
     * Searches for old portals
     *
     * @param blockPos
     * @param extendedRaduis
     * @return
     */
    @Override
    public Optional<class_5459.class_5460> method_30483(BlockPos blockPos, boolean extendedRaduis) {
        PointOfInterestStorage pointOfInterestStorage = this.world.getPointOfInterestStorage();
        int radius = extendedRaduis ? 16 : 128;
        pointOfInterestStorage.preloadChunks(this.world, blockPos, radius);

        Optional<PointOfInterest> optional = pointOfInterestStorage.getInSquare(x -> x == PointOfInterestType.NETHER_PORTAL,
                blockPos,
                radius,
                PointOfInterestStorage.OccupationStatus.ANY)
                .sorted(Comparator.comparingDouble(value -> ((PointOfInterest) value).getPos().getSquaredDistance(blockPos)).thenComparingInt(x -> ((PointOfInterest) x).getPos().getY()))
                .findFirst();

        if (!optional.isPresent())
            return Optional.empty();

        BlockPos position = optional.get().getPos();

        world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(position), 3, position);

        for (Direction.Axis axis : axises) {
            class_5459.class_5460 lv = class_5459.method_30574(position, axis, 21, Direction.Axis.Y, 21,
                    (blockPosx) -> desciption.isFrame(this.world.getBlockState(blockPosx)));

            if (lv.field_25937 > 0 || lv.field_25938 > 0) {
                BlockPattern.Result result = desciption.testWorkingPortal(world, lv.field_25936, axis);
                if (result != null) {
                    return Optional.of(lv);
                }
            }
        }

        return Optional.empty();
    }
}
