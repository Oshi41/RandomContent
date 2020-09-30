package dash.dashmode.mixin;

import dash.dashmode.tags.DashTags;
import net.minecraft.block.Block;
import net.minecraft.world.gen.feature.Feature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Feature.class)
public class FeatureMixin {
    @Inject(method = "isSoil(Lnet/minecraft/block/Block;)Z", at = @At("HEAD"), cancellable = true)
    private static void isSoilInject(Block block, CallbackInfoReturnable<Boolean> cir) {
        if (block.isIn(DashTags.Soil))
            cir.setReturnValue(true);
    }

    @Inject(method = "isStone", at = @At("HEAD"), cancellable = true)
    private static void isStoneInject(Block block, CallbackInfoReturnable<Boolean> cir) {
        if (block.isIn(DashTags.Stone))
            cir.setReturnValue(true);
    }
}
