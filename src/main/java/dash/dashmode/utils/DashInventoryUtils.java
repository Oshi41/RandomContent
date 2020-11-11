package dash.dashmode.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.List;

public class DashInventoryUtils {

    public static CompoundTag toTag(List<ItemStack> stacks, CompoundTag source) {
        ListTag listTag = new ListTag();

        for (int i = 0; i < stacks.size(); ++i) {
            ItemStack itemStack = stacks.get(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte) i);
                itemStack.toTag(compoundTag);
                compoundTag.putInt("Count", itemStack.getCount());
                listTag.add(compoundTag);
            }
        }

        source.put("Items", listTag);

        return source;
    }

    public static void fromTag(CompoundTag source, List<ItemStack> inventory, int maxCountPerStack) {
        ListTag listTag = source.getList("Items", 10);

        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 255;
            if (j < inventory.size()) {
                ItemStack stack = ItemStack.fromTag(compoundTag);
                stack.setCount(compoundTag.getInt("Count"));

                if (maxCountPerStack > 64)
                    stack = CustomStackSize.createWithCustomStackSize(stack, maxCountPerStack);

                inventory.set(j, stack);
            }
        }
    }
}
