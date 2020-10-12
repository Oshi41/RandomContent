package dash.dashmode.armor;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ArmorDescription {

    public final Map<StatusEffect, StatusEffectInstance> potions = new HashMap<>();
    // region fields
    private final Map<EquipmentSlot, Predicate<ItemStack>> conditions = new HashMap<>();
    public @Nullable Consumer<LivingEntity> onTick;
    @Nullable
    public IWear wear;
    @Nullable
    public EntityAttackCallback attackedCallback;
    @Nullable
    public EntityAttackCallback beingAttackedCallback;

    // endregion
    @Nullable
    public Predicate<DamageSource> invunerable;

    public ArmorDescription withSlot(EquipmentSlot slot, Predicate<ItemStack> predicate) {
        conditions.put(slot, predicate);
        return this;
    }

    public ArmorDescription withSlot(EquipmentSlot slot, Item item) {
        return withSlot(slot, stack -> stack.getItem() == item);
    }

    /**
     * Tick callback when armor is on
     *
     * @param onTick
     * @return
     */
    public ArmorDescription onTick(@Nullable Consumer<LivingEntity> onTick) {
        this.onTick = onTick;
        return this;
    }

    /**
     * Called once when is on and once when it was taken off
     *
     * @param wear
     * @return
     */
    public ArmorDescription onWearStatusChanged(IWear wear) {
        this.wear = wear;
        return this;
    }

    /**
     * Adding posible infinite potion during wearing armor
     *
     * @param instance
     * @return
     */
    public ArmorDescription withPermanentPotion(StatusEffectInstance instance) {
        potions.put(instance.getEffectType(), instance);
        return this;
    }

    /**
     * Callback for
     *
     * @param callback
     * @return
     */
    public ArmorDescription onAttack(EntityAttackCallback callback) {
        this.attackedCallback = callback;
        return this;
    }

    /**
     * Called when armor is on and player is attacked
     *
     * @param callback
     * @return
     */
    public ArmorDescription onBeingAttacked(EntityAttackCallback callback) {
        beingAttackedCallback = callback;
        return this;
    }

    /**
     * Check when entity is equipped with current armor set
     *
     * @param entity
     * @return
     */
    public boolean test(LivingEntity entity) {
        if (entity == null || conditions.isEmpty()) {
            return false;
        }

        for (Map.Entry<EquipmentSlot, Predicate<ItemStack>> entry : conditions.entrySet()) {
            if (!entry.getValue().test(entity.getEquippedStack(entry.getKey()))) {
                return false;
            }
        }

        return true;
    }
}
