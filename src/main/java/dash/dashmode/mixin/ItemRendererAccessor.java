package dash.dashmode.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {
    @Invoker("renderBakedItemModel")
    void rc_renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices);
}
