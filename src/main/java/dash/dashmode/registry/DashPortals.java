package dash.dashmode.registry;

import dash.dashmode.portal.IPortalDesciption;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DashPortals {
    private static final Map<RegistryKey<World>, IPortalDesciption> map = new HashMap<>();

    @Nullable
    public static IPortalDesciption getPortal(RegistryKey<World> moddedId) {
        return map.get(moddedId);
    }

    public static void init(String modid) {

    }
}
