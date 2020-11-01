package dash.dashmode.screen;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.function.Predicate;

public class CustomSlot extends Slot {
    private Predicate<ItemStack> predicate;
    private boolean isEnable = true;

    public CustomSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    public CustomSlot with(Predicate<ItemStack> predicate) {
        this.predicate = predicate;
        return this;
    }

    public CustomSlot enabled(boolean isEnable) {
        this.isEnable = isEnable;
        return this;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (predicate != null && !predicate.test(stack))
            return false;

        return super.canInsert(stack);
    }

    @Override
    public boolean doDrawHoveringEffect() {
        return isEnable;
    }
}
