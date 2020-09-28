package dash.dashmode.client;

import dash.dashmode.registry.DashBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;

import java.awt.*;

@Environment(EnvType.CLIENT)
public class DashModeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        int white = Color.WHITE.getRGB();

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> white, DashBlocks.PaperGrass);
    }
}
