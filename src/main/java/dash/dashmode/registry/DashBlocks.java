package dash.dashmode.registry;

import dash.dashmode.DashMod;
import dash.dashmode.block.DashFlowerBlock;
import dash.dashmode.block.DashGrassBlock;
import dash.dashmode.block.DashSaplingBlock;
import dash.dashmode.feature.PaperOakFeature;
import dash.dashmode.settings.SaplingSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;


public class DashBlocks {
    public static final Block PaperDirt;
    public static final Block PaperGrass;
    public static final Block PaperStone;
    public static final Block PaperOakLog;
    public static final Block PaperLeaves;
    public static final Block PaperBirchSapling;
    public static final Block PaperFlower;

    static {
        PaperDirt = new Block(FabricBlockSettings.of(Material.SOIL, MaterialColor.WHITE).strength(0.5F).sounds(BlockSoundGroup.GRAVEL));
        PaperGrass = new DashGrassBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC, MaterialColor.WHITE).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS), () -> PaperDirt);
        PaperStone = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(1.5F, 6.0F));
        PaperOakLog = createLogBlock(MaterialColor.WHITE, MaterialColor.LIGHT_GRAY);
        PaperLeaves = createLeavesBlock();

        PaperBirchSapling = new DashSaplingBlock(new PaperOakFeature(),
                new SaplingSettings(Material.PLANT, MaterialColor.WHITE)
                        .withCanPlantCallback((floor, world, pos) -> floor.getBlock() == PaperDirt || floor.getBlock() == PaperGrass)
                        .noCollision()
                        .ticksRandomly()
                        .breakInstantly()
                        .sounds(BlockSoundGroup.GRASS));

        PaperFlower = new DashFlowerBlock(StatusEffects.FIRE_RESISTANCE, 6, new SaplingSettings(Material.PLANT, MaterialColor.WHITE)
                .withCanPlantCallback((floor, world, pos) -> floor.getBlock() == PaperDirt || floor.getBlock() == PaperGrass)
                .noCollision()
                .breakInstantly()
                .sounds(BlockSoundGroup.GRASS));
    }

    public static void init(String modeName) {
        DashMod.MainLogger.debug("Entering to block registry");

        Item.Settings defaultSettings = new Item.Settings().group(DashMod.DashItemsTab);

        initBlock(new Identifier(modeName, "paper_dirt"), PaperDirt, defaultSettings);
        initBlock(new Identifier(modeName, "paper_grass_block"), PaperGrass, defaultSettings);
        initBlock(new Identifier(modeName, "paper_stone"), PaperStone, defaultSettings);
        initBlock(new Identifier(modeName, "paper_oak_log"), PaperOakLog, defaultSettings);
        initBlock(new Identifier(modeName, "paper_leaves"), PaperLeaves, defaultSettings);
        initBlock(new Identifier(modeName, "paper_birch_sapling"), PaperBirchSapling, defaultSettings);
        initBlock(new Identifier(modeName, "paper_flower"), PaperFlower, defaultSettings);
    }

    // region Helping methods

    private static void initBlock(Identifier id, Block block, @Nullable Item.Settings settings) {
        Registry.register(Registry.BLOCK, id, block);

        if (settings != null)
            Registry.register(Registry.ITEM, id, new BlockItem(block, settings));

    }

    private static LeavesBlock createLeavesBlock() {
        return new LeavesBlock(AbstractBlock.Settings.of(Material.LEAVES)
                .strength(0.2F)
                .ticksRandomly()
                .sounds(BlockSoundGroup.GRASS)
                .nonOpaque()
                .allowsSpawning((state, world, pos, type) -> type == EntityType.PARROT || type == EntityType.OCELOT)
                .suffocates((state, world, pos) -> false)
                .blockVision((state, world, pos) -> false));
    }

    private static PillarBlock createLogBlock(MaterialColor topMaterialColor, MaterialColor sideMaterialColor) {
        return new PillarBlock(AbstractBlock.Settings.of(Material.WOOD,
                (blockState) -> blockState.get(PillarBlock.AXIS) == Direction.Axis.Y
                        ? topMaterialColor
                        : sideMaterialColor)
                .strength(2.0F)
                .sounds(BlockSoundGroup.WOOD));
    }

    // endregion
}
