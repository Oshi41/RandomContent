package dash.dashmode.client.render.item;

import dash.dashmode.blockentity.InfiniteShulkerBoxBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class ItemInfiniteShulkerRender implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final InfiniteShulkerBoxBlockEntity entity = new InfiniteShulkerBoxBlockEntity();

    @Override
    public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockEntityRenderDispatcher.INSTANCE.renderEntity(entity, matrices, vertexConsumers, light, overlay);
    }
}
