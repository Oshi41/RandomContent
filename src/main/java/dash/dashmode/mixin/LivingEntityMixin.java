package dash.dashmode.mixin;

import dash.dashmode.utils.GravityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    private boolean isFlying;

    private boolean canApply() {
        return !isFlying;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void ctorInject(EntityType<? extends LivingEntity> entityType, World world, CallbackInfo ci) {
        isFlying = GravityHelper.isFlyingEntity(((LivingEntity) (Object) (this)));
    }

    @ModifyArgs(method = "travel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;method_26318(Lnet/minecraft/util/math/Vec3d;F)Lnet/minecraft/util/math/Vec3d;"))
    )
    private void travelModifyArgs(Args args) {
        if (canApply()) {
            LivingEntity entity = (LivingEntity) ((Object) this);

            float multiplier = GravityHelper.getGravity(entity);
            if (multiplier != 1) {
                double gravity = entity.getVelocity().y <= 0.0D && entity.hasStatusEffect(StatusEffects.SLOW_FALLING)
                        ? 0.01
                        : 0.08;

                double gravityTick = gravity - (gravity * multiplier);
                double y = (double) args.get(1) + gravityTick;
                args.set(1, y);
            }
        }
    }

    @ModifyVariable(method = "handleFallDamage", at = @At("HEAD"), ordinal = 1)
    private float handleFallDamageInjected(float damageMultiplier) {
        if (canApply()) {
            float gravity = GravityHelper.getGravity(((Entity) ((Object) this)));
            if (gravity != 1) {
                damageMultiplier *= gravity;
            }
        }

        return damageMultiplier;
    }


}
