package dash.dashmode.mixin;

import dash.dashmode.utils.ItemStackUtils;
import net.minecraft.block.TripwireBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TripwireBlock.class)
public class TripwireBlockMixin {
    @Redirect(method = "onBreak",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    public Item inject(ItemStack stack) {
        return ItemStackUtils.redirectIfShears(stack);
    }
}
