package dash.dashmode.utils;

import net.minecraft.entity.damage.DamageSource;

public class CustomDamageSource extends DamageSource {
    public CustomDamageSource(String name, boolean bypassArmor, boolean magic, boolean unblockable, boolean fire, boolean outOfWorld, boolean projectile, boolean explosive) {
        super(name);

        if (bypassArmor) {
            setBypassesArmor();
        }

        if (magic) {
            setUsesMagic();
        }

        if (unblockable) {
            setUnblockable();
        }

        if (fire) {
            setFire();
        }

        if (outOfWorld) {
            setOutOfWorld();
        }

        if (projectile) {
            setProjectile();
        }

        if (explosive) {
            setExplosive();
        }
    }
}
