package dash.dashmode.mixin;

import dash.dashmode.registry.DashBlocks;
import net.minecraft.block.OreBlock;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(OreBlock.class)
public class OreBlockMixin {
    @Inject(method = "getExperienceWhenMined", at = @At("HEAD"), cancellable = true)
    public void getExperienceWhenMinedInject(Random random, CallbackInfoReturnable<Integer> cir) {
        Object obj = this;
        OreBlock oreBlock = (OreBlock) obj;

        Pair<Integer, Integer> pair = DashBlocks.XpBlocks.get(oreBlock);
        if (pair != null) {
            cir.setReturnValue(MathHelper.nextInt(random, pair.getLeft(), pair.getRight()));
        }
    }
}
