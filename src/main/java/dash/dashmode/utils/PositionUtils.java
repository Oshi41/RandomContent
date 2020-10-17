package dash.dashmode.utils;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashSet;
import java.util.Set;

public class PositionUtils {

    /**
     * amount of blocks to break
     *
     * @param blockHitResult - hit result
     * @param radius         - radius for breaking. 0 for single block, 1 for 3*3, etc...
     * @param forward        - amount of forward breaking blocks.
     * @return
     */
    public static Set<BlockPos> getPosesToBreak(BlockHitResult blockHitResult, int radius, int forward) {

        Set<BlockPos> list = new HashSet<>();

        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
            Direction forwardDirection = blockHitResult.getSide().getOpposite();
            Direction side1 = Direction.NORTH;
            Direction side2 = Direction.WEST;

            if (forwardDirection.getAxis().isHorizontal()) {
                side1 = forwardDirection.rotateYClockwise();
                side2 = Direction.UP;
            }

            BlockPos left = blockHitResult.getBlockPos()
                    .offset(side1.getOpposite(), radius)
                    .offset(side2.getOpposite(), radius);

            BlockPos right = blockHitResult.getBlockPos()
                    .offset(side1, radius)
                    .offset(side2, radius)
                    .offset(forwardDirection, forward);

            Box box = new Box(left, right);

            // all radius will increase to up
            if (forwardDirection.getAxis().isHorizontal() && radius > 1) {
                box = box.offset(0, radius - 1, 0);
            }

            for (double x = box.minX; x <= box.maxX; x++) {
                for (double y = box.minY; y <= box.maxY; y++) {
                    for (double z = box.minZ; z <= box.maxZ; z++) {
                        list.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        return list;
    }

    /**
     * Gets portal position
     *
     * @param destination
     * @param entity
     * @return
     */
    public static BlockPos getTeleportPos(ServerWorld destination, Entity entity) {
        WorldBorder worldBorder = destination.getWorldBorder();
        double d = Math.max(-2.9999872E7D, worldBorder.getBoundWest() + 16.0D);
        double e = Math.max(-2.9999872E7D, worldBorder.getBoundNorth() + 16.0D);
        double f = Math.min(2.9999872E7D, worldBorder.getBoundEast() - 16.0D);
        double g = Math.min(2.9999872E7D, worldBorder.getBoundSouth() - 16.0D);
        double h = DimensionType.method_31109(entity.getEntityWorld().getDimension(), destination.getDimension());
        Vec3d position = entity.getPos();

        BlockPos portalPosition = new BlockPos(MathHelper.clamp(position.x * h, d, f), position.y, MathHelper.clamp(position.z * h, e, g));
        return portalPosition;
    }
}
