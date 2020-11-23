package dash.dashmode.mixin.client;

import com.mojang.serialization.Lifecycle;
import dash.dashmode.DashMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(LevelProperties.class)
public class LevelPropertiesMixin {
    @Inject(method = "getLifecycle", at = @At("RETURN"), cancellable = true)
    private void getLifecycleInject(CallbackInfoReturnable<Lifecycle> cir) {
        if (cir.getReturnValue() == Lifecycle.experimental() && DashMod.MainConfig.fixExperimentWarning.getValue()) {
            cir.setReturnValue(Lifecycle.stable());
        }
    }

}
