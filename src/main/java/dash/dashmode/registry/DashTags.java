package dash.dashmode.registry;

import dash.dashmode.DashMod;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class DashTags {
    /**
     * Tag for soil blocks
     */
    public static final Tag<Block> Soil = TagRegistry.block(new Identifier(DashMod.ModId, "soil"));
    public static final Tag<Block> Stone = TagRegistry.block(new Identifier(DashMod.ModId, "stone"));
}
