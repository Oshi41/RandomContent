package dash.dashmode.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.*;
import java.util.function.Consumer;

/**
 * Interface for special armor containing other sets
 */
public interface IAbsorb {
    /**
     * Gets map with absorbed items
     *
     * @param stack - current armor stack piece
     * @return
     */
    default Map<Item, ItemStack> get(ItemStack stack) {
        Tag absorbed = stack.getOrCreateTag().get("Absorbed");
        if (!(absorbed instanceof ListTag)) {
            absorbed = new ListTag();
            stack.getOrCreateTag().put("Absorbed", absorbed);
        }

        ListTag listTag = (ListTag) absorbed;

        Map<Item, ItemStack> stacks = new HashMap<>();

        for (int i = 0; i < listTag.size(); i++) {
            ItemStack containing = ItemStack.fromTag(listTag.getCompound(i));
            stacks.put(containing.getItem(), containing);
        }

        return stacks;
    }

    /**
     * Absorb current items
     *
     * @param stack - armor piece
     * @param inner - absorbing stacks
     */
    default void set(ItemStack stack, Collection<ItemStack> inner) {
        ListTag tag = new ListTag();
        Set<Item> items = new HashSet<>();

        for (ItemStack innerStack : inner) {
            if (items.add(innerStack.getItem()))
                tag.add(innerStack.toTag(new CompoundTag()));
        }

        stack.getOrCreateTag().put("Absorbed", tag);
    }

    /**
     * Adding current item to absorb list
     *
     * @param stack    - armor piece
     * @param toAbsorb - absorbing stack
     */
    default void append(ItemStack stack, ItemStack toAbsorb) {
        Map<Item, ItemStack> map = get(stack);
        map.put(toAbsorb.getItem(), toAbsorb);
        set(stack, map.values());
    }

    /**
     * Applying damage to containig items
     *
     * @param stack         - current armor piece
     * @param amount        - amount of damage
     * @param entity        - damager
     * @param breakCallback - on break callback
     * @param <T>           - any entity type
     */
    default <T extends LivingEntity> void applyDamage(ItemStack stack, int amount, T entity, Consumer<T> breakCallback) {
        ArrayList<ItemStack> list = new ArrayList<>(get(stack).values());
        if (list.isEmpty())
            return;

        ItemStack todamage = list.get(entity.getRandom().nextInt(list.size()));
        todamage.damage(amount, entity, breakCallback);
        if (todamage.isEmpty()) {
            list.remove(todamage);
        }

        set(stack, list);
    }
}
