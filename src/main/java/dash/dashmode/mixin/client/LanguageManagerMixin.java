package dash.dashmode.mixin.client;

import dash.dashmode.event.LangChangeEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LanguageManager.class)
public class LanguageManagerMixin {
    @Inject(method = "apply", at = @At("RETURN"))
    private void applyInject(ResourceManager manager, CallbackInfo ci) {
        LangChangeEvent.LangChanges.invoker().onLanguageChanged(Language.getInstance());
    }
}
