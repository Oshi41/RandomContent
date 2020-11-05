package dash.dashmode.client.render.block;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dash.dashmode.DashMod;
import dash.dashmode.blockentity.JarOfKeepingBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

@Environment(EnvType.CLIENT)
public class JarOfKeepingBlockEntityRenderer extends BlockEntityRenderer<JarOfKeepingBlockEntity> {
    private static final Cache<EntityType<?>, Entity> entities = CacheBuilder.newBuilder().softValues().build();

    public JarOfKeepingBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(JarOfKeepingBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        EntityType<?> type = entity.getEntityType();
        if (type == null) {
            return;
        }

        Entity toRender = entities.getIfPresent(type);
        if (toRender == null) {
            toRender = type.create(MinecraftClient.getInstance().world);
            if (toRender == null) {
                DashMod.MainLogger.error("Can't create type: " + type.getName());
                return;
            }

            entities.put(type, toRender);
        }

        float g = 0.53125F;
        float h = Math.max(toRender.getWidth(), toRender.getHeight());
        if ((double) h > 1.0D) {
            g /= h;
        }

        float rotation = MinecraftClient.getInstance().world.getTime() % 360 * 2;


        matrices.translate(0.5D, 0.4000000059604645D, 0.5D);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotation));
        matrices.translate(0.0D, -0.20000000298023224D, 0.0D);
        matrices.scale(g, g, g);
        MinecraftClient.getInstance().getEntityRenderDispatcher().render(toRender, 0.0D, 0.0D, 0.0D, 0.0F, 0, matrices, vertexConsumers, light);
    }
}
