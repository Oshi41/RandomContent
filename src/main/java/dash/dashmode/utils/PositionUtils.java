package dash.dashmode.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

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

    public static double getReachDistance(PlayerEntity entity) {
        return 5;
    }

    @Nullable
    public static BlockHitResult rayCast(PlayerEntity entity) {

        // collect information on camera
        Vec3d cameraPos = entity.getCameraPosVec(1);
        Vec3d rotation = entity.getRotationVec(1).multiply(getReachDistance(entity));
        Vec3d combined = cameraPos.add(rotation);

        BlockHitResult raycast = entity.getEntityWorld().raycast(new RaycastContext(cameraPos, combined, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, entity));

        if (raycast != null && raycast.getType() == HitResult.Type.BLOCK)
            return raycast;

        return null;
    }
}
