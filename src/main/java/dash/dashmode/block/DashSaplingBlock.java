package dash.dashmode.block;

import dash.dashmode.settings.ICanPlant;
import dash.dashmode.settings.SaplingSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class DashSaplingBlock extends SaplingBlock {
    private ICanPlant canPlant;

    public DashSaplingBlock(SaplingGenerator generator, Settings settings) {
        super(generator, settings);

        if (settings instanceof SaplingSettings) {
            SaplingSettings saplingSettings = (SaplingSettings) settings;
            canPlant = saplingSettings.getCanPlant();
        }
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        if (canPlant == null) {
            return super.canPlantOnTop(floor, world, pos);
        }

        return canPlant.canPlantOnTop(floor, world, pos);
    }
}
