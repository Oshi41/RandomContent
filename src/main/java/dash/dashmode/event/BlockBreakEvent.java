package dash.dashmode.event;

import dash.dashmode.registry.DashBlocks;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBreakEvent implements PlayerBlockBreakEvents.After {
    @Override
    public void afterBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        boolean isSilkTouch = EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, player.getActiveItem()) > 0;

        if (state.getBlock() == DashBlocks.PaperCrystalLog) {
            if (!isSilkTouch) {
                // todo spawn ent
            }
        }
    }
}
