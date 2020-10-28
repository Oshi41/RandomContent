package dash.dashmode.utils;

import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

import java.util.Set;

public class EntityUtils {
    private static final Set<DamageSource> fireSources = Sets.newHashSet(DamageSource.ON_FIRE, DamageSource.IN_FIRE, DamageSource.LAVA);

    /**
     * Pass fire to victim
     *
     * @param zombie - burning zombie
     * @param victim - attacked victim
     */
    public static boolean passFire(LivingEntity zombie, Entity victim) {
        float f = zombie.getEntityWorld().getLocalDifficulty(zombie.getBlockPos()).getLocalDifficulty();
        if (zombie.getMainHandStack().isEmpty() && zombie.isOnFire() && zombie.getEntityWorld().random.nextFloat() < f * 0.3F) {
            victim.setOnFireFor(2 * (int) f);
        }

        return true;
    }

    public static float increaseBurnDamage(DamageSource source, float amount) {
        if (fireSources.contains(source))
            amount *= 2;

        return amount;
    }
}
