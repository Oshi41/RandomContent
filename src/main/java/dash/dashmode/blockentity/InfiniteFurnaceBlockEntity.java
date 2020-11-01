package dash.dashmode.blockentity;

import com.google.common.collect.Sets;
import dash.dashmode.DashMod;
import dash.dashmode.item.IUpgrade;
import dash.dashmode.registry.DashBlockEntities;
import dash.dashmode.registry.DashItems;
import dash.dashmode.screen.InfiniteFurnaceScreenHandler;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public class InfiniteFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    private final int[] TOP_SLOTS;
    private final int[] BOTTOM_SLOTS;
    private final int[] SIDE_SLOTS;
    private final Set<Item> upgrades;
    private final Set<RecipeType<? extends AbstractCookingRecipe>> recipeTypes = Sets.newHashSet(RecipeType.BLASTING, RecipeType.SMELTING);
    private Text text;

    public InfiniteFurnaceBlockEntity() {
        super(DashBlockEntities.InfiniteFurnace, RecipeType.SMELTING);
        text = new TranslatableText("block_entity_type.random_content.infinite_furnace");
        inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);

        TOP_SLOTS = new int[]{0};
        BOTTOM_SLOTS = new int[]{1};

        SIDE_SLOTS = new int[inventory.size() - 3];
        for (int i = 0; i < SIDE_SLOTS.length; i++) {
            SIDE_SLOTS[i] = 3 + i;
        }

        upgrades = Sets.newHashSet(DashItems.IronFurnaceSpring, DashItems.GoldFurnaceSpring, DashItems.DiamondFurnaceSpring);
    }

    @Override
    protected Text getContainerName() {
        return text;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new InfiniteFurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    protected int getFuelTime(ItemStack fuel) {
        return 200;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        switch (slot) {
            // input slot
            case 0:
                return true;

            // fuel slot
            case 1:
                return false;

            // output slot
            case 2:
                return false;

            // Ingredient slots
            default:
                return upgrades.contains(stack.getItem());
        }
    }

    @Override
    public void tick() {
        boolean bl = this.isBurning();
        boolean bl2 = false;
        if (this.isBurning()) {
            setBurnTime(getBurnTime() - 1);
        }

        if (!this.world.isClient) {
            if (!this.isBurning() && this.inventory.get(0).isEmpty()) {
                if (!this.isBurning() && getCurrentCookTime() > 0) {
                    setCookTime(MathHelper.clamp(getCurrentCookTime() - 2, 0, getCookTimeTotal()));
                }
            } else {
                Recipe<?> recipe = findRecipe();
                if (!this.isBurning() && this.canAcceptRecipeOutput(recipe)) {
                    setBurnTime(this.getFuelTime(ItemStack.EMPTY));
                    setFuelTime(getBurnTime());
                    if (this.isBurning()) {
                        bl2 = true;
                    }
                }

                if (this.isBurning() && this.canAcceptRecipeOutput(recipe)) {
                    setCookTime(getCurrentCookTime() + getCookTick());
                    if (getCurrentCookTime() >= getCookTimeTotal()) {
                        setCookTime(0);
                        setTotalCookTime(getCookTime());
                        this.craftRecipe(recipe);
                        bl2 = true;
                    }
                } else {
                    setCookTime(0);
                }
            }

            if (bl != this.isBurning()) {
                bl2 = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, this.isBurning()), 3);
            }
        }

        if (bl2) {
            this.markDirty();
        }
    }

    protected Recipe<?> findRecipe() {
        RecipeManager recipeManager = world.getRecipeManager();

        for (RecipeType<? extends AbstractCookingRecipe> recipeType : recipeTypes) {
            Optional<? extends AbstractCookingRecipe> find = recipeManager.getFirstMatch(recipeType, this, world);

            if (find.isPresent())
                return find.get();
        }

        return null;
    }

    @Override
    protected boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            ItemStack itemStack = recipe.getOutput();
            if (itemStack.isEmpty()) {
                return false;
            } else {
                ItemStack itemStack2 = this.inventory.get(2);
                if (itemStack2.isEmpty()) {
                    return true;
                } else if (!itemStack2.isItemEqualIgnoreDamage(itemStack)) {
                    return false;
                } else if (itemStack2.getCount() < this.getMaxCountPerStack() && itemStack2.getCount() < itemStack2.getMaxCount()) {
                    return true;
                } else {
                    return itemStack2.getCount() < itemStack.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public void provideRecipeInputs(RecipeFinder finder) {
        finder.addItem(getStack(0));
        finder.addItem(ItemStack.EMPTY);
        finder.addItem(getStack(1));
    }

    private void craftRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null && this.canAcceptRecipeOutput(recipe)) {
            ItemStack itemStack = this.inventory.get(0);
            ItemStack recipeResult = recipe.getOutput();
            ItemStack currentOutput = this.inventory.get(2);
            if (currentOutput.isEmpty()) {
                this.inventory.set(2, recipeResult.copy());
            } else if (currentOutput.getItem() == recipeResult.getItem()) {
                currentOutput.increment(1);
            }

            if (!this.world.isClient) {
                this.setLastRecipe(recipe);
            }

            itemStack.decrement(1);
        }
    }

    protected int getCookTimeTotal() {
        return propertyDelegate.get(3);
    }

    protected void setTotalCookTime(int time) {
        propertyDelegate.set(3, time);
    }

    protected int getCookTick() {
        int levels = inventory.stream().skip(3).filter(x -> x.getItem() instanceof IUpgrade)
                .mapToInt(x -> ((IUpgrade) x.getItem()).getLevel(x))
                .sum();

        if (levels == 0) {
            return 1;
        }

        return (int) (DashMod.MainConfig.getConfig().furnaceUpgradeScale * levels);
    }

    protected void setCookTime(int time) {
        propertyDelegate.set(2, time);
    }

    protected int getCurrentCookTime() {
        return propertyDelegate.get(2);
    }

    protected int getBurnTime() {
        return propertyDelegate.get(0);
    }

    protected void setBurnTime(int time) {
        propertyDelegate.set(0, time);
    }

    protected boolean isBurning() {
        return getBurnTime() > 0;
    }

    protected void setFuelTime(int time) {
        propertyDelegate.set(1, time);
    }
}
