package dash.dashmode.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dash.dashmode.registry.DashBiomes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.Arrays;
import java.util.List;

public class PaperBiomeSource extends BiomeSource {
    public static final Codec<PaperBiomeSource> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter((x) -> x.biomeRegistry),
            Codec.LONG.fieldOf("seed").stable().forGetter((x) -> x.seed),
            Codec.INT.fieldOf("squareSize").stable().forGetter((x) -> x.squareSize),
            Codec.INT.fieldOf("mountainWidth").stable().forGetter((x) -> x.mountainWidth))
            .apply(instance, instance.stable(PaperBiomeSource::new)));

    private final Registry<Biome> biomeRegistry;
    private final long seed;
    private final int squareSize;
    private final int mountainWidth;

    private final Box paperLandBox;

    public PaperBiomeSource(Registry<Biome> biomeRegistry, long seed, int squareSize, int mountainWidth) {
        this(biomeRegistry, seed, Arrays.asList(BuiltinRegistries.BIOME.get(DashBiomes.PaperLands), BuiltinRegistries.BIOME.get(DashBiomes.PaperMountains)), squareSize, mountainWidth);
    }

    public PaperBiomeSource(Registry<Biome> biomeRegistry, long seed, List<Biome> paperBiomes, int squareSize, int mountainWidth) {
        super(paperBiomes);
        this.biomeRegistry = biomeRegistry;
        this.seed = seed;
        this.squareSize = squareSize;
        this.mountainWidth = mountainWidth;

        paperLandBox = new Box(new BlockPos(mountainWidth, 0, mountainWidth), new BlockPos(squareSize + mountainWidth, 1, squareSize + mountainWidth));
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return new PaperBiomeSource(biomeRegistry, seed, biomes, squareSize, mountainWidth);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int squareSize = this.squareSize + mountainWidth * 2;
        int x = Math.abs((biomeX << 2) & squareSize);
        int z = Math.abs((biomeZ << 2) & squareSize);

        if (paperLandBox.contains(x, 0, z)) {
            return BuiltinRegistries.BIOME.get(DashBiomes.PaperLands);
        }

        return BuiltinRegistries.BIOME.get(DashBiomes.PaperMountains);
    }
}
