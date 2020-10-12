package dash.dashmode.registry;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ArmorDescription {
    private final Map<EquipmentSlot, Predicate<ItemStack>> conditions = new HashMap<>();

    public ArmorDescription withSlot(EquipmentSlot slot, Predicate<ItemStack> predicate) {
        conditions.put(slot, predicate);
        return this;
    }

    public ArmorDescription withSlot(EquipmentSlot slot, Item item) {
        return withSlot(slot, stack -> stack.getItem() == item);
    }

    /**
     * Check when entity is equipped with current armor set
     *
     * @param entity
     * @return
     */
    public boolean test(LivingEntity entity) {
        if (entity == null || conditions.isEmpty())
            return false;

        for (Map.Entry<EquipmentSlot, Predicate<ItemStack>> entry : conditions.entrySet()) {
            if (!entry.getValue().test(entity.getEquippedStack(entry.getKey())))
                return false;
        }

        return true;
    }
}
