package dash.dashmode.screen;

import dash.dashmode.item.IUpgrade;
import dash.dashmode.registry.DashScreens;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;

public class InfiniteFurnaceScreenHandler extends AbstractFurnaceScreenHandler {
    public InfiniteFurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(6), new ArrayPropertyDelegate(4));
    }

    public InfiniteFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory infiniteFurnaceBlockEntity, PropertyDelegate propertyDelegate) {
        this(DashScreens.InfiniteFurnace, RecipeType.SMELTING, RecipeBookCategory.FURNACE, syncId, playerInventory, infiniteFurnaceBlockEntity, propertyDelegate);
    }

    private InfiniteFurnaceScreenHandler(ScreenHandlerType<?> type, RecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookCategory recipeBookCategory, int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(type, recipeType, recipeBookCategory, syncId, playerInventory, inventory, propertyDelegate);
        slots.clear();

        this.addSlot(new Slot(inventory, 0, 56, 17));
        this.addSlot(new CustomSlot(inventory, 1, 56, 53).enabled(false).with(x -> false));
        this.addSlot(new FurnaceOutputSlot(playerInventory.player, inventory, 2, 116, 35));

        for (int i = 0; i < 3; i++) {
            this.addSlot(new CustomSlot(inventory, 3 + i, 152, 18 * (i + 1)).with(stack -> stack.getItem() instanceof IUpgrade));
        }

        int l;
        for (l = 0; l < 3; ++l) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, 84 + l * 18));
            }
        }

        for (l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public int getCraftingSlotCount() {
        return 6;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        int lastFurnaceSlotIndex = 6;
        int maxIndex = slots.size();
        int hotBarSlots = maxIndex - 9;

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();

            // from furnace slots
            if (index < lastFurnaceSlotIndex) {
                if (!this.insertItem(itemStack2, lastFurnaceSlotIndex, maxIndex, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (isSmeltable(itemStack2)) {
                    if (!this.insertItem(itemStack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemStack2.getItem() instanceof IUpgrade) {
                    if (!this.insertItem(itemStack2, 3, lastFurnaceSlotIndex, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < hotBarSlots) {
                    if (!this.insertItem(itemStack2, hotBarSlots, maxIndex, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < maxIndex && !this.insertItem(itemStack2, lastFurnaceSlotIndex, hotBarSlots, false)) {
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
}
