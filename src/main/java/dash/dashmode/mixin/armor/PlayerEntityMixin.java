package dash.dashmode.mixin.armor;

import dash.dashmode.armor.ArmorUtils;
import dash.dashmode.armor.IArmorSupplier;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @ModifyArgs(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void attackModifyArgsLivingEntity(Args args) {
        ArmorUtils.increaseAttack(args, ((IArmorSupplier) this));
    }

    @ModifyArgs(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private void attackModifyArgsEntity(Args args) {
        ArmorUtils.increaseAttack(args, ((IArmorSupplier) this));
    }
}
