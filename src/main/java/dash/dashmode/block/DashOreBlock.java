package dash.dashmode.block;

import net.minecraft.block.OreBlock;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class DashOreBlock extends OreBlock {
    private final int min;
    private final int max;

    public DashOreBlock(Settings settings, int min, int max) {
        super(settings);
        this.min = min;
        this.max = max;
    }

    @Override
    protected int getExperienceWhenMined(Random random) {
        return MathHelper.nextInt(random, min, max);
    }
}
