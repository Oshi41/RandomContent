package dash.dashmode.registry;

import dash.dashmode.DashMod;
import dash.dashmode.portal.IPortalDesciption;
import dash.dashmode.portal.PaperPortalDesciption;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DashDimensions {
    public static final RegistryKey<World> PaperLands;
    private static final Map<RegistryKey<World>, IPortalDesciption> map = new HashMap<>();

    static {
        PaperLands = RegistryKey.of(Registry.DIMENSION, new Identifier(DashMod.ModId, "paper_lands"));
        map.put(PaperLands, new PaperPortalDesciption());
    }

    @Nullable
    public static IPortalDesciption getPortal(RegistryKey<World> moddedId) {
        return map.get(moddedId);
    }
}
