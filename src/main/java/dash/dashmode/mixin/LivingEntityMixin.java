package dash.dashmode.mixin;

import dash.dashmode.DashMod;
import dash.dashmode.utils.GravityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private final Set<Identifier> rc_setId = new HashSet<>();
    @Unique
    private boolean rc_isFlying;

    private boolean canApply() {
        return !rc_isFlying;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void ctorInject(EntityType<? extends LivingEntity> entityType, World world, CallbackInfo ci) {
        rc_isFlying = GravityHelper.isFlyingEntity(((LivingEntity) (Object) (this)));
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

    @Inject(method = "onEquipStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"))
    private void onEquipStackInject(ItemStack stack, CallbackInfo ci) {

        LivingEntity entity = (LivingEntity) ((Object) this);

        Set<Identifier> equipped = DashMod.ArmorSetRegistry.getEntries().stream().filter(x -> x.getValue().test(entity)).map(x -> x.getKey().getValue()).collect(Collectors.toSet());

        // Удалил уже используемые, осталось только разница
        equipped.removeAll(rc_setId);

        for (Identifier identifier : rc_setId) {
            onEquipmentChanged(identifier, !rc_setId.remove(identifier));
        }

        for (Identifier identifier : equipped) {
            onEquipmentChanged(identifier, rc_setId.add(identifier));
        }

        CompoundTag compoundTag = getData();
        ListTag listTag = new ListTag();
        rc_setId.stream().map(x -> StringTag.of(x.toString())).forEach(listTag::add);
        compoundTag.put("armorSets", listTag);

        saveData(compoundTag);
    }

    /**
     * todo call event
     *
     * @param id   - id of armor set
     * @param isOn - is armor currently on
     */
    private void onEquipmentChanged(Identifier id, boolean isOn) {

    }

    /**
     * Gets data from entity
     *
     * @return
     */
    private CompoundTag getData() {
        // todo
        return new CompoundTag();
    }

    /**
     * @param tag
     */
    private void saveData(CompoundTag tag) {
        // todo
    }
}
