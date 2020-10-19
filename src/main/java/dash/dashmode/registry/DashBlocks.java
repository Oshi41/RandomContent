package dash.dashmode.registry;

import dash.dashmode.DashMod;
import dash.dashmode.block.*;
import dash.dashmode.feature.PaperOakFeature;
import dash.dashmode.item.JarOfKeepingItem;
import dash.dashmode.settings.SaplingSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.ToIntFunction;


public class DashBlocks {
    public static final Block PaperDirt;
    public static final Block PaperGrass;
    public static final Block PaperStone;
    public static final Block PaperOakLog;
    public static final Block PaperLeaves;
    public static final Block PaperBirchSapling;
    public static final Block PaperFlower;
    public static final Block PaperEmeraldOre;
    public static final Block PaperQuartzOre;
    public static final Block PaperGoldOre;
    public static final Block PaperIronOre;
    public static final Block PaperCoalOre;
    public static final Block PaperLapisOre;
    public static final Block PaperRedstoneOre;
    public static final Block PaperDiamondOre;
    public static final Block PaperCrystalLog;
    public static final Block JarOfKeeping;
    public static final Block PerfectJarOfKeeping;
    public static final Block PaperOakPlank;
    public static final Block PortalCane;
    public static final Block PaperBookshelf;

    static {
        PaperDirt = new Block(FabricBlockSettings.of(Material.SOIL, MaterialColor.WHITE).strength(0.5F).sounds(BlockSoundGroup.GRAVEL)
                .requiresTool()
                .breakByTool(FabricToolTags.SHEARS, -1));
        PaperGrass = new DashGrassBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC, MaterialColor.WHITE).ticksRandomly().strength(0.6F).sounds(BlockSoundGroup.GRASS)
                .requiresTool()
                .breakByTool(FabricToolTags.SHEARS, -1), () -> PaperDirt);
        PaperStone = new Block(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(1.5F, 6.0F)
                .requiresTool()
                .breakByTool(FabricToolTags.SHEARS, -1));
        PaperOakLog = createLogBlock(MaterialColor.WHITE, MaterialColor.LIGHT_GRAY, settings -> settings.breakByTool(FabricToolTags.SHEARS, 0).requiresTool());
        PaperLeaves = createLeavesBlock(settings -> settings.breakByTool(FabricToolTags.SHEARS, 0).requiresTool());

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

        PaperIronOre = new OreBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.WHITE)
                .requiresTool()
                .breakByTool(FabricToolTags.SHEARS, 1)
                .strength(3.0F, 3.0F));
        PaperGoldOre = new OreBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.WHITE)
                .requiresTool()
                .breakByTool(FabricToolTags.SHEARS, 1)
                .strength(3.0F, 3.0F));

        PaperLapisOre = new DashOreBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.WHITE)
                .requiresTool()
                .breakByTool(FabricToolTags.SHEARS, 1)
                .strength(3.0F, 3.0F), 2, 5);

        PaperCoalOre = new DashOreBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.WHITE)
                .requiresTool()
                .breakByTool(FabricToolTags.SHEARS, 0)
                .strength(3.0F, 3.0F), 0, 2);

        PaperRedstoneOre = new RedstoneOreBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.WHITE)
                .requiresTool()
                .breakByTool(FabricToolTags.SHEARS, 2)
                .ticksRandomly()
                .luminance(createLightLevelFromBlockState(9))
                .strength(3.0F, 3.0F));

        PaperDiamondOre = new DashOreBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.WHITE)
                .requiresTool()
                .breakByTool(FabricToolTags.SHEARS, 2)
                .strength(3.0F, 3.0F), 3, 7);

        PaperEmeraldOre = new DashOreBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.WHITE)
                .requiresTool()
                .breakByTool(FabricToolTags.SHEARS, 2)
                .strength(3.0F, 3.0F), 3, 7);

        PaperQuartzOre = new DashOreBlock(FabricBlockSettings.of(Material.STONE, MaterialColor.WHITE)
                .requiresTool()
                .breakByTool(FabricToolTags.SHEARS, 0)
                .strength(3.0F, 3.0F)
                .sounds(BlockSoundGroup.NETHER_ORE), 2, 5);

        PaperCrystalLog = new DashOreBlock(FabricBlockSettings.of(Material.WOOD, MaterialColor.WHITE)
                .strength(3f)
                .breakByTool(FabricToolTags.SHEARS, 0)
                .sounds(BlockSoundGroup.WOOD), 2, 5);

        JarOfKeeping = new JarOfKeepingBlock(FabricBlockSettings.of(Material.GLASS, MaterialColor.BLACK)
                .strength(0.3F)
                .sounds(BlockSoundGroup.GLASS)
                .nonOpaque()
                .allowsSpawning((state, world, pos, type) -> false)
                .solidBlock((state, world, pos) -> false)
                .suffocates((state, world, pos) -> false)
                .blockVision((state, world, pos) -> false),
                false);

        PerfectJarOfKeeping = new JarOfKeepingBlock(FabricBlockSettings.of(Material.GLASS, MaterialColor.BLACK)
                .strength(0.3F)
                .sounds(BlockSoundGroup.GLASS)
                .nonOpaque()
                .allowsSpawning((state, world, pos, type) -> false)
                .solidBlock((state, world, pos) -> false)
                .suffocates((state, world, pos) -> false)
                .blockVision((state, world, pos) -> false),
                true);

        PaperOakPlank = new Block(FabricBlockSettings.of(Material.WOOD, MaterialColor.WHITE)
                .strength(2.0F, 3.0F)
                .requiresTool()
                .breakByTool(FabricToolTags.SHEARS, -1)
                .sounds(BlockSoundGroup.WOOD));

        PortalCane = new DashPortalBlock(AbstractBlock.Settings.of(Material.PLANT)
                .noCollision()
                .ticksRandomly()
                .strength(-1.0F)
                .sounds(BlockSoundGroup.CROP)
                .luminance((state) -> {
                    return 11;
                }), () -> DashDimensions.PaperLands);

        PaperBookshelf = new Block(FabricBlockSettings.of(Material.WOOD)
                .requiresTool()
                .breakByTool(FabricToolTags.SHEARS, -1)
                .strength(1.5F)
                .sounds(BlockSoundGroup.WOOD));
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

        initBlock(new Identifier(modeName, "paper_coal_ore"), PaperCoalOre, defaultSettings);
        initBlock(new Identifier(modeName, "paper_iron_ore"), PaperIronOre, defaultSettings);
        initBlock(new Identifier(modeName, "paper_gold_ore"), PaperGoldOre, defaultSettings);
        initBlock(new Identifier(modeName, "paper_redstone_ore"), PaperRedstoneOre, defaultSettings);
        initBlock(new Identifier(modeName, "paper_lapis_ore"), PaperLapisOre, defaultSettings);
        initBlock(new Identifier(modeName, "paper_diamond_ore"), PaperDiamondOre, defaultSettings);
        initBlock(new Identifier(modeName, "paper_emerald_ore"), PaperEmeraldOre, defaultSettings);
        initBlock(new Identifier(modeName, "paper_quartz_ore"), PaperQuartzOre, defaultSettings);

        initBlock(new Identifier(modeName, "paper_crystal_log"), PaperCrystalLog, defaultSettings);

        Identifier jarOfKeeping = new Identifier(modeName, "jar_of_keeping");

        initBlock(jarOfKeeping, JarOfKeeping, null);
        Registry.register(Registry.ITEM, jarOfKeeping, new JarOfKeepingItem(JarOfKeeping, defaultSettings, false));

        Identifier perfectJarOfKeeping = new Identifier(modeName, "perfect_jar_of_keeping");
        initBlock(perfectJarOfKeeping, PerfectJarOfKeeping, null);
        Registry.register(Registry.ITEM, perfectJarOfKeeping, new JarOfKeepingItem(PerfectJarOfKeeping, defaultSettings, true));

        initBlock(new Identifier(modeName, "paper_oak_plank"), PaperOakPlank, defaultSettings);
        initBlock(new Identifier(modeName, "portal_cane"), PortalCane, defaultSettings);
        initBlock(new Identifier(modeName, "paper_bookshelf"), PaperBookshelf, defaultSettings);
    }

    // region Helping methods

    private static void initBlock(Identifier id, Block block, @Nullable Item.Settings settings) {
        Registry.register(Registry.BLOCK, id, block);

        if (settings != null) {
            Registry.register(Registry.ITEM, id, new BlockItem(block, settings));
        }

    }

    private static LeavesBlock createLeavesBlock(Consumer<FabricBlockSettings> callback) {
        FabricBlockSettings settings = FabricBlockSettings.of(Material.LEAVES)
                .strength(0.2F)
                .ticksRandomly()
                .sounds(BlockSoundGroup.GRASS)
                .nonOpaque()
                .allowsSpawning((state, world, pos, type) -> type == EntityType.PARROT || type == EntityType.OCELOT)
                .suffocates((state, world, pos) -> false)
                .blockVision((state, world, pos) -> false);

        if (callback != null) {
            callback.accept(settings);
        }

        return new LeavesBlock(settings);
    }

    private static PillarBlock createLogBlock(MaterialColor topMaterialColor, MaterialColor sideMaterialColor, Consumer<FabricBlockSettings> callback) {

        FabricBlockSettings settings = FabricBlockSettings.copyOf(FabricBlockSettings.of(Material.WOOD,
                (blockState) -> blockState.get(PillarBlock.AXIS) == Direction.Axis.Y
                        ? topMaterialColor
                        : sideMaterialColor));

        if (callback != null) {
            callback.accept(settings);
        }

        return new PillarBlock(settings
                .strength(2.0F)
                .sounds(BlockSoundGroup.WOOD));
    }

    private static ToIntFunction<BlockState> createLightLevelFromBlockState(int litLevel) {
        return (blockState) -> (Boolean) blockState.get(Properties.LIT) ? litLevel : 0;
    }

    // endregion
}
