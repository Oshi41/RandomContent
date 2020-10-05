package dash.dashmode.config;

import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

public class DashConfig {
    @Property(category = "worldGen", commentLangKey = "rc.paperbiome.comment")
    public double PaperBiomeWeight = 0.1;

    @Property(category = "worldGen", commentLangKey = "rc.paper_crystal_treeGen.comment")
    @FloatValidate(minValue = 20, maxValue = 200)
    public float PaperCrystalTreeChance = 100;

    @Property(category = "worldGen", commentLangKey = "rc.gravity_dims.comment")
    public Map<Identifier, Float> DimensionsGravity = new HashMap<Identifier, Float>() {{
        put(DimensionType.OVERWORLD_ID, 1F);
        put(DimensionType.THE_END_ID, 0.2F);
    }};
}
