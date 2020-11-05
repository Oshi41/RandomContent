package dash.dashmode.client;

import dash.dashmode.DashMod;
import dash.dashmode.client.render.block.JarOfKeepingBlockEntityRenderer;
import dash.dashmode.client.render.block.StackHolderBlockEntityRenderer;
import dash.dashmode.client.render.entity.CosmoGhastEntityRenderer;
import dash.dashmode.client.render.entity.PaperCowRender;
import dash.dashmode.client.render.entity.PaperZombieRender;
import dash.dashmode.client.render.item.ItemJarOverlayRender;
import dash.dashmode.client.screen.DashForgeScreen;
import dash.dashmode.client.screen.InfiniteFurnaceScreen;
import dash.dashmode.debug.AttributesHelper;
import dash.dashmode.debug.JsonCheckDebug;
import dash.dashmode.debug.LangHelper;
import dash.dashmode.registry.DashBlockEntities;
import dash.dashmode.registry.DashBlocks;
import dash.dashmode.registry.DashEntities;
import dash.dashmode.registry.DashScreens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
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
        Set<String> set = Arrays.stream(FabricLoader.getInstance().getLaunchArguments(true)).collect(Collectors.toSet());
        String modId = DashMod.ModId;

        if (set.contains("langFix")) {
            new LangHelper(modId).fill();
        }

        if (set.contains("initMobStats")) {
            new AttributesHelper(modId, false).init();
        }

        if (set.contains("checkBlockLoot")) {
            new JsonCheckDebug(modId).init();
        }

        renderBlocks();
        renderItems();
        renderEntities();
        renderBlockEntities();
        renderScreens();
    }

    private void registerJarRender(Block... blocks) {
        ItemJarOverlayRender jarRender = new ItemJarOverlayRender();
        for (Block block : blocks) {
            BuiltinItemRendererRegistry.INSTANCE.register(block, jarRender);
        }
    }

    private void renderBlocks() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                DashBlocks.PaperFlower, DashBlocks.PaperBirchSapling, DashBlocks.PortalCane, DashBlocks.Forge, DashBlocks.Pillar);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(),
                DashBlocks.JarOfKeeping);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(),
                DashBlocks.PaperGrass);

        registerJarRender(DashBlocks.JarOfKeeping, DashBlocks.PerfectJarOfKeeping);
    }

    private void renderItems() {
        int white = 16777215;
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> white, DashBlocks.PaperGrass);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> white, DashBlocks.PaperGrass);
    }

    private void renderEntities() {
        EntityRendererRegistry.INSTANCE.register(DashEntities.JarOfKeepingThrowableEntityType, (e, c) -> new FlyingItemEntityRenderer<>(e, c.getItemRenderer()));
        EntityRendererRegistry.INSTANCE.register(DashEntities.PaperZombie, (e, c) -> new PaperZombieRender<>(e));
        EntityRendererRegistry.INSTANCE.register(DashEntities.PaperCow, (e, c) -> new PaperCowRender<>(e));
        EntityRendererRegistry.INSTANCE.register(DashEntities.CosmoGhast, (e, c) -> new CosmoGhastEntityRenderer(e));
    }

    private void renderBlockEntities() {
        BlockEntityRendererRegistry.INSTANCE.register(DashBlockEntities.StackHolder, StackHolderBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(DashBlockEntities.JarOfKeepingBlockEntityType, JarOfKeepingBlockEntityRenderer::new);
    }

    private void renderScreens() {
        ScreenRegistry.register(DashScreens.InfiniteFurnace, InfiniteFurnaceScreen::new);
        ScreenRegistry.register(DashScreens.Forge, DashForgeScreen::new);
    }
}
