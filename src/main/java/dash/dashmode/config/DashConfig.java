package dash.dashmode.config;

import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

public class DashConfig {
    @Property(category = "worldGen", commentLangKey = "random_content.gravity_dims.comment")
    public Map<Identifier, Float> DimensionsGravity = new HashMap<Identifier, Float>() {{
        put(DimensionType.OVERWORLD_ID, 1F);
    }};

    @Property(category = "environment", commentLangKey = "random_content.fixModdedShears.comment")
    public boolean fixModdedShearsDrop = true;

    @Property(category = "environment", commentLangKey = "random_content.fixExperimentWarning.comment")
    public boolean fixExperimentWarning = true;
}
