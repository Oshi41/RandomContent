package dash.dashmode.mixin;

import dash.dashmode.DashMod;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.Tag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(MatchToolLootCondition.class)
public class MatchToolLootConditionMixin {
    @Nullable
    private Tag<Item> tag;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initInject(ItemPredicate predicate, CallbackInfo ci) {
        if (!DashMod.MainConfig.getConfig().fixModdedShearsDrop)
            return;

        Item item = ((ItemPredicateAccessor) predicate).rc_getItem();
        if (item == Items.SHEARS) {
            tag = FabricToolTags.SHEARS;
        }
    }

    @Inject(method = "test", at = @At("RETURN"), cancellable = true)
    private void initInject(LootContext lootContext, CallbackInfoReturnable<Boolean> cir) {
        if (tag == null)
            return;

        if (cir.getReturnValue() == true)
            return;

        ItemStack stack = lootContext.get(LootContextParameters.TOOL);
        if (stack != null) {
            cir.setReturnValue(stack.getItem().isIn(tag));
        }
    }
}
