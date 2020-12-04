package dash.dashmode.config;

import com.google.common.collect.Lists;
import dash.dashmode.DashMod;
import dash.dashmode.config.configs.YamlConfig;
import dash.dashmode.config.interfaces.IConfig;
import dash.dashmode.config.interfaces.IConfigEntry;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class ModConfig {
    public final IConfigEntry<Map<Identifier, Float>> gravityMap;
    public final IConfigEntry<Boolean> fixShears;
    public final IConfigEntry<Boolean> fixExperimentWarning;
    public final IConfigEntry<Float> paperBiomeWeight;
    public final IConfigEntry<Float> furnaceUpgradeScale;

    public final IConfig config;

    public ModConfig(Identifier id) {
        final ConfigCategory environment = new ConfigCategory("environment", null);
        final ConfigCategory worldGen = new ConfigCategory("world_gen", null);
        final ConfigCategory machines = new ConfigCategory("machines", null);


        gravityMap = new ConfigEntry<>(environment,
                "gravity",
                new TranslatableText("random_content.gravity_dims.comment"),
                null,
                new HashMap<Identifier, Float>() {{
                    put(World.OVERWORLD.getValue(), 1F);
                    put(new Identifier(DashMod.ModId, "galaxy"), 0.2F);
                }});

        paperBiomeWeight = new ConfigEntry<>(worldGen,
                "paperBiomeWeight",
                new TranslatableText("random_content.paper_lands_weight.comment")
                        .append(System.lineSeparator())
                        .append(new TranslatableText("random_content.range_restrict.comment", Float.MIN_NORMAL, 1000)),
                t -> MathHelper.clamp(t, Float.MIN_NORMAL, 1000),
                0.5F);

        fixShears = new ConfigEntry<>(environment,
                "fixShears",
                new TranslatableText("random_content.fixModdedShears.comment"),
                null,
                true);

        fixExperimentWarning = new ConfigEntry<>(environment,
                "fixExperimentWarning",
                new TranslatableText("random_content.fixExperimentWarning.comment"),
                null,
                true);

        furnaceUpgradeScale = new ConfigEntry<>(machines,
                "furnaceUpgradeScale",
                new TranslatableText("random_content.furnaceUpgradeScale.comment")
                        .append(System.lineSeparator())
                        .append(new TranslatableText("random_content.range_restrict.comment", 1, 5)),
                t -> MathHelper.clamp(t, 1, 5),
                1.7F);

        config = new YamlConfig(Lists.newArrayList(environment, worldGen, machines), id);
    }
}
