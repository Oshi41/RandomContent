package dash.dashmode.armor;

import net.minecraft.entity.damage.DamageSource;

@FunctionalInterface
public interface EntityAttackCallback {
    /**
     * Calculates damage caused by entity
     *
     * @param source
     * @param amount
     * @return
     */
    float getDamage(DamageSource source, float amount);
}
