package dash.dashmode.mixin.armor;

import dash.dashmode.DashMod;
import dash.dashmode.armor.ArmorDescription;
import dash.dashmode.armor.EntityAttackCallback;
import dash.dashmode.armor.IArmorSupplier;
import dash.dashmode.utils.RangeEnchantApply;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixinArmor implements IArmorSupplier {
    @Shadow
    public abstract void equipStack(EquipmentSlot slot, ItemStack stack);

    @Unique
    private final Set<Identifier> rc_setId = new HashSet<>();

    @Override
    public Map<Identifier, ArmorDescription> getWearingSets() {
        Map<Identifier, ArmorDescription> map = new HashMap<>();

        for (Identifier id : rc_setId) {
            ArmorDescription description = DashMod.ArmorSetRegistry.get(id);
            if (description != null) {
                map.put(id, description);
            }
        }

        return map;
    }

    @Override
    public boolean isOn(Identifier id) {
        return rc_setId.contains(id);
    }

    @Inject(method = "method_30123", at = @At("RETURN"))
    private void method_30123Inject(Map<EquipmentSlot, ItemStack> map, CallbackInfo ci) {
        refresh();
    }

    @Override
    public void refresh() {
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
    }

    /**
     * On equpment changed
     *
     * @param id   - id of armor set
     * @param isOn - is armor currently on
     */
    private void onEquipmentChanged(Identifier id, boolean isOn) {
        ArmorDescription description = DashMod.ArmorSetRegistry.get(id);
        if (description != null && description.wear != null) {
            description.wear.onWearStatusChanged((LivingEntity) (Object) this, isOn);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if (rc_setId.isEmpty()) {
            return;
        }

        LivingEntity livingEntity = (LivingEntity) ((Object) this);

        long time = livingEntity.getEntityWorld().getTime();

        for (Identifier id : rc_setId) {
            ArmorDescription description = DashMod.ArmorSetRegistry.get(id);
            if (description == null) {
                continue;
            }

            // Potions
            try {
                for (Map.Entry<StatusEffect, Supplier<StatusEffectInstance>> entry : description.applyingPotions.entrySet()) {
                    StatusEffect statusEffect = entry.getKey();
                    if (!livingEntity.hasStatusEffect(statusEffect) && !description.forbiddenPotions.contains(statusEffect)) {
                        livingEntity.addStatusEffect(entry.getValue().get());
                    }
                }

                for (StatusEffect effect : description.forbiddenPotions) {
                    if (livingEntity.hasStatusEffect(effect)) {
                        livingEntity.removeStatusEffect(effect);
                    }
                }

                for (RangeEnchantApply apply : description.applyingPotionsToEntities.values()) {
                    if (time % apply.time != 0)
                        continue;

                    List<LivingEntity> entitiesByClass = livingEntity.getEntityWorld().getEntitiesByClass(
                            LivingEntity.class,
                            livingEntity.getBoundingBox().expand(apply.radius),
                            entity -> apply.entity.test(entity) && entity != livingEntity);

                    for (LivingEntity entity : entitiesByClass) {
                        entity.addStatusEffect(new StatusEffectInstance(apply.createEffect.get()));
                    }
                }
            } catch (Exception e) {
                DashMod.MainLogger.warn(e);
            }

            // on tick
            if (description.onTick != null) {
                description.onTick.accept(livingEntity);
            }
        }
    }

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float damageModifyArg(float amount, DamageSource source) {
        List<EntityAttackCallback> callbacks = getWearingSets().values().stream().filter(x -> x.beingAttackedCallback != null)
                .map(x -> x.beingAttackedCallback).collect(Collectors.toList());

        for (EntityAttackCallback callback : callbacks) {
            amount = callback.getDamage(source, amount);
        }

        return amount;
    }

}
