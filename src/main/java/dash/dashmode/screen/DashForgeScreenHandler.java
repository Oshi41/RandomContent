package dash.dashmode.screen;

import dash.dashmode.recipe.DashForgeRecipe;
import dash.dashmode.registry.DashBlocks;
import dash.dashmode.registry.DashRecipes;
import dash.dashmode.registry.DashScreens;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class DashForgeScreenHandler extends ScreenHandler {
    protected final CraftingResultInventory output = new CraftingResultInventory();
    protected final PlayerInventory playerInventory;
    protected final ScreenHandlerContext context;
    private final Property levelCost;
    protected Inventory input;

    public DashForgeScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(DashScreens.Forge, syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public DashForgeScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId);
        this.playerInventory = playerInventory;
        this.context = context;
        levelCost = Property.create();
        addProperty(levelCost);

        input = new SimpleInventory(10) {
            @Override
            public void markDirty() {
                super.markDirty();
                DashForgeScreenHandler.this.onContentChanged(this);
            }
        };

        // crafting slots
        for (int m = 0; m < 3; ++m) {
            for (int l = 0; l < 3; ++l) {
                this.addSlot(new Slot(this.input, l + m * 3, 57 + l * 18, 17 + m * 18));
            }
        }

        // catalyst
        this.addSlot(new Slot(this.input, 9, 14, 53));

        // output
        this.addSlot(new Slot(this.output, 10, 147, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeItems(PlayerEntity playerEntity) {
                return DashForgeScreenHandler.this.canTakeOutput(playerEntity, this.hasStack());
            }

            @Override
            public ItemStack onTakeItem(PlayerEntity player, ItemStack stack) {
                return DashForgeScreenHandler.this.onTakeOutput(player, stack);
            }
        });

        // Player inventory
        int k;
        for (k = 0; k < 3; ++k) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + k * 9 + 9, 8 + j * 18, 84 + k * 18));
            }
        }

        for (k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    private ItemStack onTakeOutput(PlayerEntity player, ItemStack stack) {
        if (!player.abilities.creativeMode) {
            player.addExperienceLevels(-this.levelCost.get());
        }

        levelCost.set(0);

        for (int i = 0; i < input.size(); i++) {
            ItemStack craftStack = input.getStack(i);
            craftStack.decrement(1);
        }

        return stack;
    }

    private boolean canTakeOutput(PlayerEntity player, boolean isPresent) {
        return (player.abilities.creativeMode || player.experienceLevel >= this.levelCost.get());
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        BiFunction<World, BlockPos, Boolean> func = (world, pos) -> world.getBlockState(pos).isOf(DashBlocks.Forge) && player.squaredDistanceTo(
                (double) pos.getX() + 0.5D,
                (double) pos.getY() + 0.5D,
                (double) pos.getZ() + 0.5D) <= 64.0D;

        return context.run(func).orElse(true);
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.context.run((world, blockPos) -> {
            this.dropInventory(player, world, this.input);
        });
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (inventory == this.input) {
            World world = playerInventory.player.getEntityWorld();
            RecipeManager manager = world.getRecipeManager();
            DashForgeRecipe recipe = manager.getFirstMatch(DashRecipes.DashForgeRecipeType, inventory, world).orElse(null);

            if (recipe == null) {
                levelCost.set(0);
                output.setStack(0, ItemStack.EMPTY);
            } else {
                levelCost.set(recipe.experience);
                output.setStack(0, recipe.getOutput().copy());
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public int getLevelCost() {
        return levelCost.get();
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        int lastIndex = input.size();

        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();

            if (index < lastIndex) {
                if (!this.insertItem(itemStack2, lastIndex, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index - 9 < lastIndex) {
                if (!this.insertItem(itemStack2, slots.size() - 9, slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < slots.size()) {
                if (!this.insertItem(itemStack2, lastIndex, slots.size() - 9, false)) {
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
