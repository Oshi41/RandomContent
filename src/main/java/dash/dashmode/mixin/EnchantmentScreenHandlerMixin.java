package dash.dashmode.mixin;

import dash.dashmode.registry.DashBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin {
    @Shadow
    @Final
    private ScreenHandlerContext context;

    @ModifyVariable(method = "onContentChanged",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandlerContext;run(Ljava/util/function/BiConsumer;)V")
            ),
            at = @At(value = "STORE"),
            ordinal = 0)
    private int onContentChangedModifyVariable(int source) {
        Optional<Integer> toAdd = context.run((world, pos) -> {
            List<BlockPos> poses = new ArrayList<>();

            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    if ((j != 0 || k != 0) && world.isAir(pos.add(k, 0, j)) && world.isAir(pos.add(k, 1, j))) {
                        poses.add(pos.add(k * 2, 0, j * 2));
                        poses.add(pos.add(k * 2, 1, j * 2));

                        if (k != 0 && j != 0) {
                            poses.add(pos.add(k * 2, 0, j));
                            poses.add(pos.add(k * 2, 1, j));
                            poses.add(pos.add(k, 0, j * 2));
                            poses.add(pos.add(k, 1, j * 2));
                        }
                    }
                }
            }

            return poses.stream().mapToInt(x -> getEnchantibility(world.getBlockState(x))).sum();
        });

        if (toAdd.isPresent()) {
            source += toAdd.get();
        }

        return source;
    }

    private int getEnchantibility(BlockState state) {
        if (state.isOf(DashBlocks.PaperBookshelf)) {
            return 5;
        }

        return 0;
    }
}
