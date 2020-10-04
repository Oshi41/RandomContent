package dash.dashmode.registry;

import com.google.common.collect.ImmutableList;
import dash.dashmode.DashMod;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.decorator.CountExtraDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DepthAverageDecoratorConfig;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;

import java.util.OptionalInt;

public class DashFeatures {
    public static final RuleTest AnyStoneRule;

    public static final ConfiguredFeature<TreeFeatureConfig, ?> FancyPaperOak;
    public static final ConfiguredFeature<TreeFeatureConfig, ?> FancyPaperOakBee;
    public static final ConfiguredFeature<?, ?> RandomPaperTree;

    public static final ConfiguredFeature<?, ?> PaperCoalOre;
    public static final ConfiguredFeature<?, ?> PaperIronOre;
    public static final ConfiguredFeature<?, ?> PaperGoldOre;
    public static final ConfiguredFeature<?, ?> PaperRedstoneOre;
    public static final ConfiguredFeature<?, ?> PaperDiamondOre;
    public static final ConfiguredFeature<?, ?> PaperLapisOre;
    public static final ConfiguredFeature<?, ?> PaperEmeraldOre;
    public static final ConfiguredFeature<RandomPatchFeatureConfig, ?> PaperFlowers;

    static {
        AnyStoneRule = new TagMatchRuleTest(DashTags.Stone);

        FancyPaperOak = Feature.TREE.configure(
                (new TreeFeatureConfig.Builder(new SimpleBlockStateProvider(DashBlocks.PaperOakLog.getDefaultState()),
                        new SimpleBlockStateProvider(DashBlocks.PaperLeaves.getDefaultState()),
                        new LargeOakFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(4), 4),
                        new LargeOakTrunkPlacer(3, 11, 0),
                        new TwoLayersFeatureSize(0, 0, 0,
                                OptionalInt.of(4))))
                        .ignoreVines()
                        .heightmap(Heightmap.Type.MOTION_BLOCKING)
                        .build());

        FancyPaperOakBee = Feature.TREE.configure(FancyPaperOak.getConfig()
                .setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.MORE_BEEHIVES_TREES)));

        PaperCoalOre = (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configure(
                new OreFeatureConfig(AnyStoneRule,
                        DashBlocks.PaperCoalOre.getDefaultState(), 17)).method_30377(128))
                .spreadHorizontally()).repeat(20);
        PaperIronOre = (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configure(
                new OreFeatureConfig(AnyStoneRule,
                        DashBlocks.PaperIronOre.getDefaultState(), 9))
                .method_30377(64))
                .spreadHorizontally())
                .repeat(20);
        PaperGoldOre = (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configure(
                new OreFeatureConfig(AnyStoneRule,
                        DashBlocks.PaperGoldOre.getDefaultState(), 9))
                .method_30377(32))
                .spreadHorizontally())
                .repeat(2);
        PaperRedstoneOre = (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configure(
                new OreFeatureConfig(AnyStoneRule,
                        DashBlocks.PaperRedstoneOre.getDefaultState(), 8))
                .method_30377(16))
                .spreadHorizontally()).repeat(8);
        PaperDiamondOre = (ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configure(new OreFeatureConfig(AnyStoneRule, DashBlocks.PaperDiamondOre.getDefaultState(), 8)).method_30377(16)).spreadHorizontally();
        PaperLapisOre = Feature.ORE.configure(new OreFeatureConfig(AnyStoneRule, DashBlocks.PaperLapisOre.getDefaultState(), 7)).decorate(Decorator.DEPTH_AVERAGE.configure(new DepthAverageDecoratorConfig(16, 16))).spreadHorizontally();
        PaperEmeraldOre = Feature.EMERALD_ORE.configure(new EmeraldOreFeatureConfig(DashBlocks.PaperStone.getDefaultState(), DashBlocks.PaperEmeraldOre.getDefaultState()))
                .decorate(Decorator.EMERALD_ORE.configure(DecoratorConfig.DEFAULT));

        RandomPaperTree = Feature.RANDOM_SELECTOR.configure(new RandomFeatureConfig(ImmutableList.of(
                FancyPaperOak.withChance(0.2F),
                FancyPaperOakBee.withChance(0.1F)),
                FancyPaperOak))
                .decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
                .decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)));

        PaperFlowers = Feature.RANDOM_PATCH.configure((new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(DashBlocks.PaperFlower.getDefaultState()),
                SimpleBlockPlacer.INSTANCE))
                .tries(32)
                .build());
    }

    public static void init(String modId) {
        DashMod.MainLogger.debug("Entering to feature registry");

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(modId, "feature_paper_fancy_oak"), FancyPaperOak);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(modId, "feature_paper_fancy_oak_bee"), FancyPaperOakBee);

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(modId, "feature_paper_coal_ore"), PaperCoalOre);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(modId, "feature_paper_coal_iron"), PaperIronOre);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(modId, "feature_paper_coal_gold"), PaperGoldOre);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(modId, "feature_paper_coal_redstone"), PaperRedstoneOre);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(modId, "feature_paper_coal_lapis"), PaperLapisOre);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(modId, "feature_paper_coal_diamond"), PaperDiamondOre);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(modId, "feature_paper_coal_emerald"), PaperEmeraldOre);

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(modId, "random_paper_tree"), RandomPaperTree);
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(modId, "random_paper_flowers"), PaperFlowers);
    }

}
