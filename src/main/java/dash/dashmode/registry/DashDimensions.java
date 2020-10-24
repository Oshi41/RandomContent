package dash.dashmode.registry;

import com.google.common.collect.Sets;
import dash.dashmode.DashMod;
import dash.dashmode.portal.IPortalDesciption;
import dash.dashmode.portal.PaperPortalDesciption;
import net.fabricmc.fabric.mixin.object.builder.PointOfInterestTypeAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DashDimensions {
    public static final RegistryKey<World> PaperLands;
    public final static PointOfInterestType ModdedPortals;
    private static final Map<RegistryKey<World>, IPortalDesciption> map = new HashMap<>();


    static {
        PaperLands = RegistryKey.of(Registry.DIMENSION, new Identifier(DashMod.ModId, "paper_lands"));
        map.put(PaperLands, new PaperPortalDesciption());

        ModdedPortals = PointOfInterestTypeAccessor.callCreate(DashMod.ModId + "_portals", fromBlocks(DashBlocks.PortalCane), 1, 1);
    }

    private static Set<BlockState> fromBlocks(Block... blocks) {
        Set<BlockState> set = Sets.newHashSet();

        for (Block block : blocks) {
            set.addAll(block.getStateManager().getStates());
        }

        return set;
    }

    @Nullable
    public static IPortalDesciption getPortal(RegistryKey<World> moddedId) {
        return map.get(moddedId);
    }

    public static void init() {
        PointOfInterestTypeAccessor.callSetup(ModdedPortals);
    }
}
