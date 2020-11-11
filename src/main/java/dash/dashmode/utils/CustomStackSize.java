package dash.dashmode.utils;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Custom stack size
 */
public interface CustomStackSize {

    /**
     * Creates copy of itemStack with custom size
     *
     * @param stack     - current stack
     * @param stackSize - max possible stack size
     * @return
     */
    static ItemStack createWithCustomStackSize(@Nullable ItemStack stack, int stackSize) {
        if (stack == null || stack.isEmpty()) {
            stack = ItemStack.EMPTY.copy();
        }

        if (((Object) stack) instanceof CustomStackSize) {
            ((CustomStackSize) ((Object) stack)).setMaxCount(stackSize);
        }

        return stack;
    }

    /**
     * Changes max stack size to default
     *
     * @param stack
     * @return
     */
    static ItemStack withDefaultStackSize(@Nullable ItemStack stack) {

        if (stack == null || stack.isEmpty()) {
            stack = ItemStack.EMPTY.copy();
        }

        if (((Object) stack) instanceof CustomStackSize) {
            CustomStackSize customStackSize = (CustomStackSize) ((Object) stack);
            customStackSize.setMaxCount(customStackSize.getInitialStackSize());
        }

        return stack;
    }

    /**
     * Injecting max stack size
     *
     * @param size
     */
    void setMaxCount(Integer size);

    /**
     * Get size of current item
     *
     * @return
     */
    int getInitialStackSize();
}
