package dash.dashmode.mixin;

import dash.dashmode.registry.DashTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(MiningToolItem.class)
public class MiningToolItemMixin {
    @Shadow
    protected float miningSpeed;

    private boolean isShovel;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void initInject(float attackDamage, float attackSpeed, ToolMaterial material, Set<Block> effectiveBlocks, Item.Settings settings, CallbackInfo ci) {
        isShovel = ShovelItem.class.isInstance(this);
    }

    @Inject(method = "getMiningSpeedMultiplier", at = @At("HEAD"), cancellable = true)
    public void getMiningSpeedMultiplierInject(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir) {
        if (isShovel && state.isIn(DashTags.FeatureSoil)) {
            cir.setReturnValue(miningSpeed);
        }
    }
}
