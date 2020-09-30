package dash.dashmode.settings;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@FunctionalInterface
public interface ICanPlant {

    /**
     * Can current sapling grow on block
     *
     * @param floor - block to grow on
     * @param world - in current world
     * @param pos   - on current pos
     * @return
     */
    boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos);
}
