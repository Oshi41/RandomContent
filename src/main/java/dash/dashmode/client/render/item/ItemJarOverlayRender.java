package dash.dashmode.client.render.item;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dash.dashmode.blockentity.JarOfKeepingBlockEntity;
import dash.dashmode.item.JarOfKeepingItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ItemJarOverlayRender implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private static final Map<Block, BlockState> states = new HashMap<>();
    private static final Cache<EntityType<?>, JarOfKeepingBlockEntity> cache = CacheBuilder.newBuilder().softValues().build();
    private final JarOfKeepingBlockEntity empty = new JarOfKeepingBlockEntity();

    @Override
    public void render(ItemStack itemStack, ModelTransformation.Mode mode, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
        if (itemStack.getItem() instanceof BlockItem) {
            BlockState state = states.computeIfAbsent(((BlockItem) itemStack.getItem()).getBlock(), Block::getDefaultState);
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, matrixStack, vertexConsumerProvider, light, overlay);
        }

        JarOfKeepingBlockEntity blockEntity = empty;

        if (itemStack.getItem() instanceof JarOfKeepingItem) {
            EntityType<?> type = ((JarOfKeepingItem) itemStack.getItem()).getEntityType(itemStack);

            if (type != null) {
                blockEntity = cache.getIfPresent(type);
                if (blockEntity == null) {
                    blockEntity = new JarOfKeepingBlockEntity();
                    blockEntity.fromTag(null, itemStack.getSubTag(JarOfKeepingBlockEntity.BlockItemTag));
                    cache.put(type, blockEntity);
                }
            }
        }

        BlockEntityRenderDispatcher.INSTANCE.renderEntity(blockEntity, matrixStack, vertexConsumerProvider, light, overlay);
    }
}
