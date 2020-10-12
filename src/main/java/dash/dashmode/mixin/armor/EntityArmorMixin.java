package dash.dashmode.mixin.armor;

import dash.dashmode.armor.IArmorSupplier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityArmorMixin {
    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void isInvulnerableToInject(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (!(this instanceof IArmorSupplier))
            return;

        boolean isInvulnerable = ((IArmorSupplier) this).getWearingSets().values().stream().filter(x -> x.invunerable != null)
                .map(x -> x.invunerable).anyMatch(x -> x.test(damageSource));

        if (isInvulnerable) {
            cir.setReturnValue(true);
        }
    }
}
