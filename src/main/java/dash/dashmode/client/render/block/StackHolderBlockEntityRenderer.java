package dash.dashmode.client.render.block;

import dash.dashmode.blockentity.StackHolderBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

@Environment(EnvType.CLIENT)
public class StackHolderBlockEntityRenderer extends BlockEntityRenderer<StackHolderBlockEntity> {
    public StackHolderBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(StackHolderBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack itemStack = entity.getStack(0);

        if (itemStack.isEmpty()) {
            return;
        }

        BlockPos pos = entity.getPos();
        VoxelShape shape = entity.getWorld().getBlockState(pos).getSidesShape(entity.getWorld(), pos);

        matrices.push();
        matrices.translate(0.5, shape.getMax(Direction.Axis.Y) + 0.2, 0.5);
        matrices.translate(0.0F, MathHelper.sin(entity.getWorld().getTime() / 10.0F) * 0.1F + 0.1F, 0.0F);

        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(entity.getWorld().getTime() % 360 * 2));

        float scale = 1.1F;
        matrices.scale(scale, scale, scale);
        MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);

        // matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);

        matrices.pop();
    }
}
