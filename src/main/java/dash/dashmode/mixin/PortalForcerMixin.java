package dash.dashmode.mixin;

import dash.dashmode.portal.IPortalDesciption;
import dash.dashmode.portal.IPortalForcer;
import dash.dashmode.registry.DashDimensions;
import dash.dashmode.utils.IOriented;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.class_5459;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

@Mixin(PortalForcer.class)
public class PortalForcerMixin implements IPortalForcer {
    @Shadow
    @Final
    private ServerWorld world;

    @Unique
    private boolean rc_handleBlockPlacing = false;

    @Redirect(method = "method_30482", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
    private boolean setBlockStateRedirect(ServerWorld serverWorld, BlockPos pos, BlockState state) {
        return setBlockStateRedirectWithFlags(serverWorld, pos, state, 3);
    }

    @Redirect(method = "method_30482", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private boolean setBlockStateRedirectWithFlags(ServerWorld serverWorld, BlockPos pos, BlockState state, int flags) {
        return rc_handleBlockPlacing || serverWorld.setBlockState(pos, state, flags);
    }

    @Override
    public Optional<class_5459.class_5460> tryFindOrCreate(Entity entity, IPortalDesciption desciption, BlockPos portalPosition, boolean extendedRadius) {

        Optional<class_5459.class_5460> portal = findModdedPortal(desciption, world, portalPosition, extendedRadius);

        if (portal.isPresent())
            return portal;

        // new instance to prevent multuple access
        PortalForcer forcer = new PortalForcer(world);
        PortalForcerMixin mixin = (PortalForcerMixin) ((Object) forcer);
        // handling nether portal placement
        mixin.rc_handleBlockPlacing = true;

        portal = forcer.method_30482(portalPosition, Direction.Axis.X);
        if (portal.isPresent()) {
            Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, Direction.Axis.X);
            desciption.placePortal(world, portal.get().field_25936, direction, direction.rotateYClockwise());
        }


        return portal;
    }

    private Optional<class_5459.class_5460> findModdedPortal(IPortalDesciption desciption, ServerWorld destination, BlockPos blockPos, boolean extendedRaduis) {
        PointOfInterestStorage pointOfInterestStorage = destination.getPointOfInterestStorage();
        int radius = extendedRaduis ? 16 : 128;
        pointOfInterestStorage.preloadChunks(destination, blockPos, radius);

        Optional<PointOfInterest> optional = pointOfInterestStorage.getInSquare(x -> x == DashDimensions.ModdedPortals,
                blockPos,
                radius,
                PointOfInterestStorage.OccupationStatus.ANY)
                .sorted(Comparator.comparingDouble(x -> x.getPos().getSquaredDistance(blockPos)))
                .filter(x -> Math.sqrt(x.getPos().getSquaredDistance(blockPos)) <= radius)
                .findFirst();

        if (!optional.isPresent())
            return Optional.empty();

        BlockPos position = optional.get().getPos();

        destination.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(position), 3, position);

        for (Direction.Axis axis : Arrays.asList(Direction.Axis.X, Direction.Axis.Z, Direction.Axis.Y)) {
            class_5459.class_5460 lv = class_5459.method_30574(position, axis, 21, Direction.Axis.Y, 21,
                    (blockPosx) -> desciption.isFrame(destination.getBlockState(blockPosx)));

            if (lv.field_25937 > 0 || lv.field_25938 > 0) {
                BlockPattern.Result result = desciption.testWorkingPortal(destination, lv.field_25936, axis);
                if (result != null) {
                    if (lv instanceof IOriented) {
                        ((IOriented) lv).setAxis(axis);
                    }

                    return Optional.of(lv);
                }
            }
        }

        return Optional.empty();
    }
}
