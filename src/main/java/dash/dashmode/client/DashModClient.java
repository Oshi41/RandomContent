package dash.dashmode.client;

import dash.dashmode.DashMod;
import dash.dashmode.client.render.block.InfiniteShulkerBoxRender;
import dash.dashmode.client.render.block.JarOfKeepingBlockEntityRenderer;
import dash.dashmode.client.render.block.StackHolderBlockEntityRenderer;
import dash.dashmode.client.render.entity.CosmoGhastEntityRenderer;
import dash.dashmode.client.render.entity.PaperBookEntityRenderer;
import dash.dashmode.client.render.entity.PaperCowRender;
import dash.dashmode.client.render.entity.PaperZombieRender;
import dash.dashmode.client.render.item.ItemInfiniteShulkerRender;
import dash.dashmode.client.render.item.ItemJarOverlayRender;
import dash.dashmode.client.screen.DashForgeScreen;
import dash.dashmode.client.screen.InfiniteFurnaceScreen;
import dash.dashmode.client.screen.InfiniteShulkerBoxScreen;
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
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

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

        ClientSpriteRegistryCallback.event(TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE).register(this::registerSprite);
    }

    private void registerSprite(SpriteAtlasTexture atlas, ClientSpriteRegistryCallback.Registry registry) {
        registry.register(new Identifier(DashMod.ModId, "entity/infinite_shulker_box"));
    }

    private void registerItemJarRender(Block... blocks) {
        ItemJarOverlayRender jarRender = new ItemJarOverlayRender();
        for (Block block : blocks) {
            BuiltinItemRendererRegistry.INSTANCE.register(block, jarRender);
        }
    }

    private void renderBlocks() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                DashBlocks.PaperFlower, DashBlocks.PaperBirchSapling, DashBlocks.PortalCane, DashBlocks.Forge, DashBlocks.Pillar);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(),
                DashBlocks.JarOfKeeping, DashBlocks.PerfectJarOfKeeping);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(),
                DashBlocks.PaperGrass);
    }

    private void renderItems() {
        int white = 16777215;
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> white, DashBlocks.PaperGrass);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> white, DashBlocks.PaperGrass);

        registerItemJarRender(DashBlocks.JarOfKeeping, DashBlocks.PerfectJarOfKeeping);
        BuiltinItemRendererRegistry.INSTANCE.register(DashBlocks.InfiniteShulker, new ItemInfiniteShulkerRender());
    }

    private void renderEntities() {
        EntityRendererRegistry.INSTANCE.register(DashEntities.JarOfKeepingThrowableEntityType, (e, c) -> new FlyingItemEntityRenderer<>(e, c.getItemRenderer()));
        EntityRendererRegistry.INSTANCE.register(DashEntities.CustomFireBall, (e, c) -> new FlyingItemEntityRenderer<>(e, c.getItemRenderer()));

        EntityRendererRegistry.INSTANCE.register(DashEntities.PaperZombie, (e, c) -> new PaperZombieRender<>(e));
        EntityRendererRegistry.INSTANCE.register(DashEntities.PaperCow, (e, c) -> new PaperCowRender<>(e));
        EntityRendererRegistry.INSTANCE.register(DashEntities.CosmoGhast, (e, c) -> new CosmoGhastEntityRenderer(e));
        EntityRendererRegistry.INSTANCE.register(DashEntities.PaperBook, (e, c) -> new PaperBookEntityRenderer<>(e));
    }

    private void renderBlockEntities() {
        BlockEntityRendererRegistry.INSTANCE.register(DashBlockEntities.StackHolder, StackHolderBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(DashBlockEntities.JarOfKeepingBlockEntityType, JarOfKeepingBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(DashBlockEntities.InfiniteShulker, InfiniteShulkerBoxRender::new);
    }

    private void renderScreens() {
        ScreenRegistry.register(DashScreens.InfiniteFurnace, InfiniteFurnaceScreen::new);
        ScreenRegistry.register(DashScreens.Forge, DashForgeScreen::new);
        ScreenRegistry.register(DashScreens.InfiniteShulker, InfiniteShulkerBoxScreen::new);
    }
}
