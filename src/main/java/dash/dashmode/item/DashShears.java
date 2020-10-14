package dash.dashmode.item;

import dash.dashmode.registry.DashItems;
import dash.dashmode.registry.DashTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DashShears extends ToolItem {
    protected final ToolMaterial material;

    public DashShears(ToolMaterial material, Settings settings) {
        super(material, settings);
        this.material = material;
    }

    @Override
    public boolean isEffectiveOn(BlockState state) {
        return Items.SHEARS.isEffectiveOn(state) && state.isIn(DashTags.Sharable);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        float speedMultiplier = Items.SHEARS.getMiningSpeedMultiplier(stack, state);

        if (speedMultiplier > 1)
            return speedMultiplier;

        if (state.isIn(DashTags.Sharable)) {
            return material.getMiningSpeedMultiplier();
        }

        return super.getMiningSpeedMultiplier(stack, state);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        return Items.SHEARS.postMine(stack, world, state, pos, miner)
                || state.isIn(DashTags.Sharable)
                || super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        ActionResult result = DashItems.NetheriteMultitool.useOnEntity(stack, user, entity, hand);

        if (result != ActionResult.PASS) {
            return result;
        }

        return super.useOnEntity(stack, user, entity, hand);
    }
}
