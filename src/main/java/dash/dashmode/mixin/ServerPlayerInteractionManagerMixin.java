package dash.dashmode.mixin;

import dash.dashmode.utils.IDigItem;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow
    public ServerPlayerEntity player;
    @Shadow
    public ServerWorld world;

    @Shadow
    public abstract boolean tryBreakBlock(BlockPos pos);

    @Inject(method = "processBlockBreakingAction",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerInteractionManager;finishMining(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/network/packet/c2s/play/PlayerActionC2SPacket$Action;Ljava/lang/String;)V"))
    private void processBlockBreakingActionInject(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, CallbackInfo ci) {
        if (player == null) {
            return;
        }

        if (player.isSneaking()) {
            return;
        }

        ItemStack stack = player.inventory.getMainHandStack();

        if (!(stack.getItem() instanceof IDigItem)) {
            return;
        }

        String reason = "";

        if (player.isCreative()) {
            reason = "creative destroy";
        } else if (action == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
            reason = "insta mine";

            // todo Think about instant break, to op
            return;
        } else {
            reason = "destroyed";
        }


        BlockHitResult hitResult = new BlockHitResult(player.getCameraPosVec(1), direction, pos, false);
        Set<BlockPos> breakingPoses = ((IDigItem) stack.getItem()).getBreakingPoses(hitResult, stack);

        breakingPoses.remove(pos);

        for (BlockPos breakingPos : breakingPoses) {
            if (!player.isCreative()) {
                BlockState state = world.getBlockState(breakingPos);
                if (state.isAir()) {
                    continue;
                }

                // can't break
                if (state.calcBlockBreakingDelta(player, world, breakingPos) <= 0) {
                    continue;
                }
            }

            if (!tryBreakBlock(breakingPos)) {
                continue;
            }

            player.networkHandler.sendPacket(new PlayerActionResponseS2CPacket(breakingPos, world.getBlockState(breakingPos), action, true, reason));
        }
    }
}
