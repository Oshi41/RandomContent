package dash.dashmode.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class RangeEnchantApply {
    public final Supplier<StatusEffectInstance> createEffect;
    public final Predicate<Entity> entity;
    public final int radius;
    public final int time;

    public RangeEnchantApply(Supplier<StatusEffectInstance> createEffect, Predicate<Entity> entity, int radius, int time) {
        this.createEffect = createEffect;
        this.entity = entity;
        this.radius = radius;
        this.time = time;
    }

    public RangeEnchantApply(StatusEffect effect, Predicate<Entity> entity, int radius, int time) {
        this(() -> new StatusEffectInstance(effect, time), entity, radius, time);
    }
}
