package dash.dashmode.registry;

import dash.dashmode.DashMode;
import dash.dashmode.block.DashGrassBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;


public class DashBlocks {
    public static final Block PaperDirt;
    public static final Block PaperGrass;
    public static final Block PaperStone;

    static {
        PaperDirt = new Block(FabricBlockSettings.of(Material.SOIL, MaterialColor.WHITE).strength(0.5F).sounds(BlockSoundGroup.GRAVEL));
        PaperGrass = new DashGrassBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC, MaterialColor.WHITE).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS), () -> PaperDirt);
        PaperStone = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(1.5F, 6.0F));
    }

    public static void init(String modeName) {
        DashMode.MainLogger.info("Entering to block registry");

        Item.Settings defaultSettings = new Item.Settings().group(DashMode.DashItems);

        initBlock(new Identifier(modeName, "paper_dirt"), PaperDirt, defaultSettings);
        initBlock(new Identifier(modeName, "paper_grass_block"), PaperGrass, defaultSettings);
        initBlock(new Identifier(modeName, "paper_stone"), PaperStone, defaultSettings);
    }

    private static void initBlock(Identifier id, Block block, @Nullable Item.Settings settings) {
        Registry.register(Registry.BLOCK, id, block);

        if (settings != null)
            Registry.register(Registry.ITEM, id, new BlockItem(block, settings));

    }
}
