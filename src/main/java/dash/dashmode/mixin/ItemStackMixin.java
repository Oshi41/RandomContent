package dash.dashmode.mixin;

import dash.dashmode.utils.CustomStackSize;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements CustomStackSize {
    @Unique
    private Integer rc_size;

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract boolean isDamageable();

    @Shadow
    public abstract boolean isDamaged();

    @Shadow
    public abstract boolean hasEnchantments();

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    private void initInject(CompoundTag tag, CallbackInfo ci) {
        setMaxCount(getInitialStackSize());
    }

    @Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;I)V", at = @At("RETURN"))
    private void initInject2(ItemConvertible item, int count, CallbackInfo ci) {
        setMaxCount(getInitialStackSize());
    }

    @Inject(method = "getMaxCount", at = @At("HEAD"), cancellable = true)
    private void getMaxCountInject(CallbackInfoReturnable<Integer> cir) {
        if (getInitialStackSize() > 1 && (!isDamageable() || !isDamaged()) && !hasEnchantments()) {
            cir.setReturnValue(rc_size);
        }
    }

    @Inject(method = "copy", at = @At("RETURN"), cancellable = true)
    private void copyInject(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();

        if (!stack.isEmpty()) {
            ((CustomStackSize) (Object) stack).setMaxCount(rc_size);
            cir.setReturnValue(stack);
        }
    }

    @Override
    public void setMaxCount(Integer size) {
        rc_size = size;
    }

    @Override
    public int getInitialStackSize() {
        return getItem().getMaxCount();
    }
}
