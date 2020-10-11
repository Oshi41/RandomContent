package dash.dashmode.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

/**
 * Interface for range breaking tool
 */
public interface IDigItem {

    /**
     * Gets amount of blocks that should be broken
     *
     * @param blockHitResult - ray trace result
     * @param stack          - stack with tool
     * @return
     */
    Set<BlockPos> getBreakingPoses(BlockHitResult blockHitResult, ItemStack stack);
}
