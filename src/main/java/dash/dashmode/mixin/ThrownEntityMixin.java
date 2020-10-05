package dash.dashmode.mixin;

import dash.dashmode.utils.GravityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThrownEntity.class)
public class ThrownEntityMixin {
    @Inject(method = "getGravity", at = @At("HEAD"), cancellable = true)
    public void getGravityInject(CallbackInfoReturnable<Float> cir) {
        Entity entity = (Entity) ((Object) this);
        if (entity.hasNoGravity()) {
            return;
        }

        float gravity = GravityHelper.getGravity(entity);
        if (gravity == 1) {
            return;
        }

        cir.setReturnValue(gravity * cir.getReturnValue());
    }
}
