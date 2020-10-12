package dash.dashmode.item;

import dash.dashmode.utils.IDigItem;
import dash.dashmode.utils.PositionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class BoerItem extends MultiToolItem implements IDigItem {

    /**
     * 0 - sinle block
     * 1 - 3 * 3
     * 2 - 5 * 5
     * etc
     */
    private final int radius;
    private final int forward;

    public BoerItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings, int radius, int forward) {
        super(material, attackDamage, attackSpeed, settings);
        this.radius = radius;
        this.forward = forward;
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return super.getMiningSpeedMultiplier(stack, state) / (radius + forward + 2);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return tryPlaceBlocks(context, Items.TORCH);
    }

    private ActionResult tryPlaceBlocks(ItemUsageContext context, Item... items) {
        if (context == null || items == null || items.length == 0 || context.getPlayer() == null) {
            return ActionResult.PASS;
        }

        Set<Item> set = Arrays.stream(items).collect(Collectors.toSet());

        ItemStack stack = null;

        PlayerEntity player = context.getPlayer();
        int size = player.inventory.size();

        for (int i = 0; i < size; i++) {
            ItemStack temp = player.inventory.getStack(i);
            if (set.contains(temp.getItem())) {
                stack = temp;
                break;
            }
        }

        if (stack == null) {
            return ActionResult.PASS;
        }

        BlockHitResult hitResult = new BlockHitResult(context.getHitPos(), context.getSide(), context.getBlockPos(), context.hitsInsideBlock());
        ItemPlacementContext placementContext = new ItemPlacementContext(player, context.getHand(), stack, hitResult);
        return stack.getItem().useOnBlock(placementContext);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        return ActionResult.PASS;
    }

    @Override
    public Set<BlockPos> getBreakingPoses(BlockHitResult blockHitResult, ItemStack stack) {
        return PositionUtils.getPosesToBreak(blockHitResult, radius, forward);
    }
}
