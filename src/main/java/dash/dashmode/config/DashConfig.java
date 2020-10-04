package dash.dashmode.config;

import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;

public class DashConfig {
    @Property(category = "worldGen", commentLangKey = "rc.paperbiome.comment")
    public double PaperBiomeWeight = 0.1;

    @Property(category = "worldGen", commentLangKey = "rc.gravity_dims.comment")
    public HashMap<String, Float> DimensionsGravity = new HashMap<String, Float>() {{
        put(DimensionType.OVERWORLD_ID.toString(), 1F);
    }};
}
