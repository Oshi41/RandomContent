package dash.dashmode.mixin.client;

import dash.dashmode.utils.NumberScope;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @ModifyVariable(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"), argsOnly = true)
    private String rc_setCountLabel(String origin, TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel) {
        if (stack.getCount() >= NumberScope.Hundreds.min) {
            countLabel = NumberScope.toFormatText(stack.getCount());
        }

        return countLabel;
    }
}
