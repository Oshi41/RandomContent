package dash.dashmode.client;

import dash.dashmode.DashMod;
import dash.dashmode.client.render.ItemJarOverlayRender;
import dash.dashmode.client.render.PaperCowRender;
import dash.dashmode.client.render.PaperZombieRender;
import dash.dashmode.debug.AttributesHelper;
import dash.dashmode.debug.LangHelper;
import dash.dashmode.registry.DashBlocks;
import dash.dashmode.registry.DashEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class DashModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                DashBlocks.PaperFlower, DashBlocks.PaperBirchSapling, DashBlocks.PortalCane);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(),
                DashBlocks.JarOfKeeping);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(),
                DashBlocks.PaperGrass);

        int white = 16777215;
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> white, DashBlocks.PaperGrass);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> white, DashBlocks.PaperGrass);


        Set<String> set = Arrays.stream(FabricLoader.getInstance().getLaunchArguments(true)).collect(Collectors.toSet());

        if (set.contains("langFix")) {
            new LangHelper(DashMod.ModId).fill();
        }

        if (set.contains("initMobStats")) {
            new AttributesHelper(DashMod.ModId, false).init();
        }

        registerJarRender(DashBlocks.JarOfKeeping, DashBlocks.PerfectJarOfKeeping);

        EntityRendererRegistry.INSTANCE.register(DashEntities.JarOfKeepingThrowableEntityType, (e, c) -> new FlyingItemEntityRenderer<>(e, c.getItemRenderer()));
        EntityRendererRegistry.INSTANCE.register(DashEntities.PaperZombie, (e, c) -> new PaperZombieRender<>(e));
        EntityRendererRegistry.INSTANCE.register(DashEntities.PaperCow, (e, c) -> new PaperCowRender<>(e));
    }

    private void registerJarRender(Block... blocks) {
        ItemJarOverlayRender jarRender = new ItemJarOverlayRender(blocks);
        for (Block block : blocks) {
            BuiltinItemRendererRegistry.INSTANCE.register(block, jarRender);
        }
    }

}
