package dash.dashmode.screen;

import dash.dashmode.mixin.ScreenHandlerAccessor;
import dash.dashmode.registry.DashScreens;
import dash.dashmode.utils.CustomStackSize;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Set;

public class InfiniteShulkerBoxScreenHandler extends ScreenHandler {
    private final PlayerInventory playerInventory;
    private final Inventory inventory;

    public InfiniteShulkerBoxScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new CustomInventory(27, Integer.MAX_VALUE));
    }

    public InfiniteShulkerBoxScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(DashScreens.InfiniteShulker, syncId);
        this.playerInventory = playerInventory;
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        int o;
        int n;
        for (o = 0; o < 3; ++o) {
            for (n = 0; n < 9; ++n) {
                this.addSlot(new Slot(inventory, n + o * 9, 8 + n * 18, 18 + o * 18));
            }
        }

        for (o = 0; o < 3; ++o) {
            for (n = 0; n < 9; ++n) {
                this.addSlot(new Slot(playerInventory, n + o * 9 + 9, 8 + n * 18, 84 + o * 18));
            }
        }

        for (o = 0; o < 9; ++o) {
            this.addSlot(new Slot(playerInventory, o, 8 + o * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();

            if (index < inventory.size()) {
                itemStack2 = CustomStackSize.withDefaultStackSize(itemStack2);

                if (!this.insertItem(itemStack2, inventory.size(), inventory.size() + (9 * 4), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                itemStack2 = CustomStackSize.createWithCustomStackSize(itemStack2, Integer.MAX_VALUE);

                if (!this.insertItem(itemStack2, 0, inventory.size(), false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    @Override
    public ItemStack onSlotClick(int i, int j, SlotActionType actionType, PlayerEntity playerEntity) {
        if (actionType == SlotActionType.CLONE && playerEntity.abilities.creativeMode && playerInventory.getCursorStack().isEmpty() && i >= 0) {
            Slot slot = this.slots.get(i);
            if (slot != null && slot.hasStack()) {
                ItemStack stack = slot.getStack().copy();
                stack.setCount(Math.min(stack.getMaxCount(), ((CustomStackSize) (Object) stack).getInitialStackSize()));
                playerInventory.setCursorStack(stack);
                return stack;
            }
        }

        if (actionType == SlotActionType.PICKUP
                && !playerInventory.getCursorStack().isEmpty()
                && i < inventory.size()
                && !slots.get(i).getStack().isEmpty()
                && slots.get(i).canInsert(playerInventory.getCursorStack())) {

            ItemStack stack = playerInventory.getCursorStack();
            Slot slot = slots.get(i);
            if (stack.isItemEqualIgnoreDamage(slot.getStack()) && ItemStack.areTagsEqual(slot.getStack(), stack)) {
                CustomStackSize.createWithCustomStackSize(stack, inventory.getMaxCountPerStack());
            }
        }

        ItemStack stack = super.onSlotClick(i, j, actionType, playerEntity);

        stack = i >= inventory.size()
                ? CustomStackSize.withDefaultStackSize(stack)
                : CustomStackSize.withDefaultStackSize(stack);

        return stack;
    }

    @Override
    protected void endQuickCraft() {
        Set<Slot> quickCraftSlots = ((ScreenHandlerAccessor) this).rc_getQuickCraftSlots();

        for (Slot slot : quickCraftSlots) {
            ItemStack slotStack = slot.getStack();

            slotStack = slot.id < inventory.size()
                    ? CustomStackSize.createWithCustomStackSize(slotStack, inventory.getMaxCountPerStack())
                    : CustomStackSize.withDefaultStackSize(slotStack);

            slot.setStack(slotStack);
            slot.markDirty();
        }

        super.endQuickCraft();
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }
}
