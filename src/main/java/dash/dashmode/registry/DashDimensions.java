package dash.dashmode.registry;

import dash.dashmode.DashMod;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class DashDimensions {
    public static final RegistryKey<World> PaperDim;

    static {
        PaperDim = RegistryKey.of(Registry.DIMENSION, new Identifier(DashMod.ModId, "paper"));
    }

    public static void init() {

    }
}
