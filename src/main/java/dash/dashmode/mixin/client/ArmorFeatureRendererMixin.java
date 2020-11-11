package dash.dashmode.mixin.client;

import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorFeatureRendererMixin {

    @Shadow
    @Final
    private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;

    @Inject(method = "getArmorTexture", at = @At("HEAD"), cancellable = true)
    private void getArmorTextureInject(ArmorItem armorItem, boolean bl, @Nullable String string, CallbackInfoReturnable<Identifier> cir) {
        String string2 = "textures/models/armor/" + armorItem.getMaterial().getName() + "_layer_" + (bl ? 2 : 1) + (string == null ? "" : "_" + string) + ".png";

        Identifier id = Registry.ITEM.getId(armorItem);
        if (!id.getNamespace().equals("minecraft")) {
            string2 = id.getNamespace() + ":" + string2;
        }

        cir.setReturnValue(ARMOR_TEXTURE_CACHE.computeIfAbsent(string2, Identifier::new));
    }
}
