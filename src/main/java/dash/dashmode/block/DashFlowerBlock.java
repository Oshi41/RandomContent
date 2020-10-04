package dash.dashmode.block;

import dash.dashmode.settings.ICanPlant;
import dash.dashmode.settings.SaplingSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class DashFlowerBlock extends FlowerBlock {
    private ICanPlant canPlant = null;

    public DashFlowerBlock(StatusEffect suspiciousStewEffect, int effectDuration, Settings settings) {
        super(suspiciousStewEffect, effectDuration, settings);

        if (settings instanceof SaplingSettings) {
            canPlant = ((SaplingSettings) settings).getCanPlant();
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
