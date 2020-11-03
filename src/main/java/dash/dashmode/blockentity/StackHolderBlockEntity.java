package dash.dashmode.blockentity;

import dash.dashmode.registry.DashBlockEntities;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class StackHolderBlockEntity extends LockableContainerBlockEntity implements UseBlockCallback, AttackBlockCallback {
    private final DefaultedList<ItemStack> slots;

    public StackHolderBlockEntity() {
        super(DashBlockEntities.StackHolder);

        slots = DefaultedList.ofSize(1, ItemStack.EMPTY);
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        ItemStack holding = getStack(0);
        if (holding.isEmpty())
            return ActionResult.PASS;

        if (!player.inventory.insertStack(holding)) {
            Block.dropStack(world, pos.add(world.random.nextFloat(), world.random.nextFloat(), world.random.nextFloat()), holding);
        }

        setStack(0, ItemStack.EMPTY);
        markDirty();
        return ActionResult.CONSUME;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        final ItemStack playerStack = player.getStackInHand(hand);
        final ItemStack holding = getStack(0);

        if (playerStack.isEmpty() && holding.isEmpty())
            return ActionResult.PASS;

        if (holding.isEmpty() && isValid(0, playerStack)) {
            setStack(0, playerStack.copy());
            playerStack.decrement(1);
            markDirty();
            return ActionResult.CONSUME;
        }

        return interact(player, world, hand, pos.add(world.random.nextFloat(), world.random.nextFloat(), world.random.nextFloat()), player.getHorizontalFacing());
    }

    @Override
    protected Text getContainerName() {
        return null;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    public int size() {
        return slots.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0 || slots.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return slots.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(slots, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(slots, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        slots.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void clear() {
        slots.clear();
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }
}
