package dash.dashmode.screen;

import dash.dashmode.utils.CustomStackSize;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public class CustomInventory extends SimpleInventory {
    protected final DefaultedList<ItemStack> stacks;
    private final List<InventoryChangedListener> listeners = new ArrayList<>();
    private int stackSize;

    public CustomInventory(int size, int stackSize) {
        this.stackSize = stackSize;
        stacks = DefaultedList.ofSize(size, CustomStackSize.createWithCustomStackSize(ItemStack.EMPTY, getMaxCountPerStack()));
    }

    @Override
    public int size() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        return stacks.stream().anyMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot >= 0 && slot < this.stacks.size()
                ? this.stacks.get(slot)
                : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Inventories.splitStack(this.stacks, slot, amount);
        if (!itemStack.isEmpty()) {
            this.markDirty();
        }

        return itemStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack itemStack = this.stacks.get(slot);

        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            setStack(slot, ItemStack.EMPTY);
            return itemStack;
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        stack = CustomStackSize.createWithCustomStackSize(stack, getMaxCountPerStack());

        stacks.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

        this.markDirty();
    }

    @Override
    public void markDirty() {
        for (InventoryChangedListener inventoryChangedListener : this.listeners) {
            inventoryChangedListener.onInventoryChanged(this);
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void provideRecipeInputs(RecipeFinder finder) {
        for (ItemStack itemStack : this.stacks) {
            finder.addItem(itemStack);
        }
    }

    @Override
    public void clear() {
        stacks.clear();
    }

    @Override
    public void addListener(InventoryChangedListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(InventoryChangedListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public ItemStack addStack(ItemStack stack) {
        return super.addStack(CustomStackSize.createWithCustomStackSize(stack, getMaxCountPerStack()));
    }

    @Override
    public int getMaxCountPerStack() {
        return stackSize;
    }
}
