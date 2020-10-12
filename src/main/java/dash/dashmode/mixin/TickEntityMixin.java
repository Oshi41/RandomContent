package dash.dashmode.mixin;

import dash.dashmode.gravity.GravityHelper;
import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({ExperienceOrbEntity.class, TntEntity.class, ItemEntity.class, FallingBlockEntity.class})
public class TickEntityMixin {
    @ModifyArgs(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    public void addModifyArgs(Args args) {
        Entity entity = (Entity) ((Object) this);
        if (!entity.hasNoGravity()) {
            float gravity = GravityHelper.getGravity(entity);

            if (gravity != 1) {
                args.set(1, (double) args.get(1) * gravity);
            }
        }
    }
}
