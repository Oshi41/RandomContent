package dash.dashmode.mixin;

import com.mojang.serialization.Lifecycle;
import dash.dashmode.DashMod;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelProperties.class)
public class LevelPropertiesMixin {
    @Inject(method = "getLifecycle", at = @At("RETURN"), cancellable = true)
    private void getLifecycleInject(CallbackInfoReturnable<Lifecycle> cir) {
        if (cir.getReturnValue() == Lifecycle.experimental() && DashMod.MainConfig.getConfig().fixExperimentWarning) {
            cir.setReturnValue(Lifecycle.stable());
        }
    }

}
