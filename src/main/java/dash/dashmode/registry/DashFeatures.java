package dash.dashmode.registry;

import dash.dashmode.feature.surface.ShatteredSavannaSurfaceBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class DashFeatures {

    public static void init(String modid) {
        Registry.register(Registry.SURFACE_BUILDER, new Identifier(modid, "shattered_savanna"), new ShatteredSavannaSurfaceBuilder(TernarySurfaceConfig.CODEC));
    }
}
