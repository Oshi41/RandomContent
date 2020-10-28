package dash.dashmode.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dash.dashmode.DashMod;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceType;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicResourceUtils {
    private static final Map<RegistryKey<?>, List<Codec<?>>> codecs = new HashMap<RegistryKey<?>, List<Codec<?>>>() {{
        put(Registry.DIMENSION_TYPE_KEY, Arrays.asList(DimensionType.CODEC));
        put(Registry.BIOME_KEY, Arrays.asList(Biome.CODEC, Biome.field_26633));
        put(Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN, Arrays.asList(ConfiguredSurfaceBuilder.CODEC));
        put(Registry.CONFIGURED_CARVER_WORLDGEN, Arrays.asList(ConfiguredCarver.CODEC));
        put(Registry.CONFIGURED_FEATURE_WORLDGEN, Arrays.asList(ConfiguredFeature.CODEC));
        put(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN, Arrays.asList(ConfiguredStructureFeature.CODEC));
        put(Registry.PROCESSOR_LIST_WORLDGEN, Arrays.asList(StructureProcessorType.CODEC));
        put(Registry.TEMPLATE_POOL_WORLDGEN, Arrays.asList(StructurePool.CODEC));
        put(Registry.NOISE_SETTINGS_WORLDGEN, Arrays.asList(ChunkGeneratorSettings.CODEC));
    }};
    private static DynamicRegistryManager.Impl manager = null;

    @Nullable
    public static <T> T find(RegistryKey<? extends Registry<T>> key, Identifier id, DynamicRegistryManager manager) {
        if (key == null || id == null)
            return null;

        MutableRegistry<T> registry = manager.getOptional(key).orElse(null);
        if (registry == null)
            return null;

        T result = registry.get(id);
        return result;
    }

    @Nullable
    public static <T> T find(RegistryKey<? extends Registry<T>> key, Identifier id) {
        if (key == null || id == null || !codecs.containsKey(key))
            return null;

        Identifier source = key.getValue();

        String path = String.format("/data/%s/%s/%s.json", id.getNamespace(), source.getPath(), id.getPath());
        InputStream stream = DashMod.class.getResourceAsStream(path);
        if (stream == null)
            return null;

        JsonObject object;
        try {
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            object = NbtUtil.GSON.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        for (Codec<?> codec : codecs.get(key)) {
            DataResult<Pair<T, JsonElement>> result = JsonOps.INSTANCE.withDecoder(((Codec<T>) codec)).apply(object);

            Pair<T, JsonElement> pair = result.get().left().orElse(null);
            if (pair != null) {
                return pair.getFirst();
            }
        }

        return null;
    }

    @Nullable
    public static <T> T load(RegistryKey<? extends Registry<T>> key, Identifier id) {
        if (manager == null) {
            ReloadableResourceManager resourceManager = new ReloadableResourceManagerImpl(ResourceType.SERVER_DATA);
            manager = DynamicRegistryManager.create();
            RegistryOps.of(JsonOps.INSTANCE, resourceManager, manager);
        }

        return find(key, id, manager);
    }

    @Nullable
    public static Tag loadFromNbt(Identifier id, Identifier folder) {
        if (id == null || folder == null)
            return null;

        String path = String.format("/data/%s/%s/%s.json", id.getNamespace(), folder.getPath(), id.getPath());
        InputStream stream = DashMod.class.getResourceAsStream(path);
        if (stream == null)
            return null;

        try {
            String text = IOUtils.toString(stream, StandardCharsets.UTF_8);

            StringNbtReader nbtReader = new StringNbtReader(new StringReader(text));
            Tag tag = nbtReader.parseTag();
            return tag;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
