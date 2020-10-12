package dash.dashmode.armor;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArmorUtils {

    /**
     * Called when entity with armor is attacking someone
     *
     * @param args     - args from net.minecraft.entity.LivingEntity.damage.damage(DamageSource, float)
     * @param supplier - entity
     */
    public static void increaseAttack(Args args, IArmorSupplier supplier) {
        Map<Identifier, ArmorDescription> map = supplier.getWearingSets();
        if (map.isEmpty()) {
            return;
        }

        List<EntityAttackCallback> callbacks = map.values().stream().filter(x -> x.attackedCallback != null).map(x -> x.attackedCallback).collect(Collectors.toList());
        handleAttack(args, callbacks);
    }


    private static void handleAttack(Args args, List<EntityAttackCallback> callbacks) {
        if (callbacks.isEmpty()) {
            return;
        }

        DamageSource source = args.get(0);
        float amount = args.get(1);

        for (EntityAttackCallback callback : callbacks) {
            amount = callback.getDamage(source, amount);
        }

        args.set(1, amount);
    }
}
