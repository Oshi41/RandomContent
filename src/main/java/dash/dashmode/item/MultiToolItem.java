package dash.dashmode.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

import java.util.LinkedHashSet;
import java.util.Set;

public class MultiToolItem extends PickaxeItem implements Vanishable {
    private final Set<Item> netheriteTools = new LinkedHashSet<>();

    public MultiToolItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);

        netheriteTools.add(Items.NETHERITE_AXE);
        netheriteTools.add(Items.NETHERITE_HOE);
        netheriteTools.add(Items.NETHERITE_SHOVEL);

        DispenserBlock.registerBehavior(this, new ShearsDispenserBehavior());
    }

    @Override
    public boolean isEffectiveOn(BlockState state) {
        return super.isEffectiveOn(state)
                || netheriteTools.stream().anyMatch(x -> x.isEffectiveOn(state))
                || Items.SHEARS.isEffectiveOn(state);
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        float multiplier = super.getMiningSpeedMultiplier(stack, state);

        // can mine as pickaxe
        if (multiplier != this.miningSpeed) {
            // but can mine as any of tool types
            if (netheriteTools.stream().anyMatch(x -> x.getMiningSpeedMultiplier(stack, state) == ToolMaterials.NETHERITE.getMiningSpeedMultiplier())) {
                return this.miningSpeed;
            }

            // final check - shears
            return Items.SHEARS.getMiningSpeedMultiplier(stack, state);
        }

        return multiplier;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult result = netheriteTools.stream()
                .map(x -> x.useOnBlock(context))
                .filter(x -> x != ActionResult.PASS)
                .findFirst()
                .orElse(ActionResult.PASS);

        return result;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof Shearable) {
            Shearable shearable = (Shearable) entity;

            if (!user.world.isClient() && shearable.isShearable()) {
                shearable.sheared(SoundCategory.PLAYERS);
                stack.damage(1, user, (playerEntity -> playerEntity.sendToolBreakStatus(hand)));
                user.swingHand(hand);
                return ActionResult.success(user.world.isClient);
            }
        }

        return ActionResult.PASS;
    }
}
