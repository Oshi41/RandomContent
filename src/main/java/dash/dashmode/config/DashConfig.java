package dash.dashmode.config;

import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

public class DashConfig {
    @Property(category = "worldGen", commentLangKey = "random_content.gravity_dims.comment")
    public Map<Identifier, Float> dimensionsGravity = new HashMap<Identifier, Float>() {{
        put(DimensionType.OVERWORLD_ID, 1F);
    }};

    @Property(category = "environment", commentLangKey = "random_content.fixModdedShears.comment")
    public boolean fixModdedShearsDrop = true;

    @Property(category = "environment", commentLangKey = "random_content.fixExperimentWarning.comment")
    public boolean fixExperimentWarning = true;

    @Property(category = "worldGen", commentLangKey = "random_content.paper_lands_weight.comment")
    @FloatValidate(minValue = Float.MIN_VALUE, maxValue = 1000)
    public float paperBiomeWeight = 0.5F;

    @Property(category = "machines", commentLangKey = "random_content.furnaceUpgradeScale.comment")
    @FloatValidate(minValue = 1, maxValue = 5)
    public float furnaceUpgradeScale = 1.7F;
}
