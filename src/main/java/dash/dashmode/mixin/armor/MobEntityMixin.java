package dash.dashmode.mixin.armor;

import dash.dashmode.armor.ArmorUtils;
import dash.dashmode.armor.IArmorSupplier;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @ModifyArgs(method = "tryAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void tryAttackModifyArgs(Args args) {
        ArmorUtils.increaseAttack(args, ((IArmorSupplier) this));
    }
}
