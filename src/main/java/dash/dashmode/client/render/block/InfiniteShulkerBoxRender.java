package dash.dashmode.client.render.block;

import dash.dashmode.DashMod;
import dash.dashmode.blockentity.InfiniteShulkerBoxBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class InfiniteShulkerBoxRender extends BlockEntityRenderer<InfiniteShulkerBoxBlockEntity> {
    private final ShulkerEntityModel<?> model;
    private final SpriteIdentifier TEXTURE;

    public InfiniteShulkerBoxRender(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
        TEXTURE = new SpriteIdentifier(TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE, new Identifier(DashMod.ModId, "entity/infinite_shulker_box"));
        model = new ShulkerEntityModel<>();
    }

    @Override
    public void render(InfiniteShulkerBoxBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction direction = Direction.UP;
        if (entity.hasWorld()) {
            BlockState blockState = entity.getWorld().getBlockState(entity.getPos());
            if (blockState.getProperties().contains(FacingBlock.FACING)) {
                direction = blockState.get(FacingBlock.FACING);
            }
        }

        matrices.push();
        matrices.translate(0.5D, 0.5D, 0.5D);
        matrices.scale(0.9995F, 0.9995F, 0.9995F);
        matrices.multiply(direction.getRotationQuaternion());
        matrices.scale(1.0F, -1.0F, -1.0F);
        matrices.translate(0.0D, -1.0D, 0.0D);
        VertexConsumer vertexConsumer = TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutoutNoCull);
        this.model.getBottomShell().render(matrices, vertexConsumer, light, overlay);
        matrices.translate(0.0D, -entity.getAnimationProgress(tickDelta) * 0.5F, 0.0D);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0F * entity.getAnimationProgress(tickDelta)));
        this.model.getTopShell().render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }
}
