package dash.dashmode.client.render;

import dash.dashmode.blockentity.JarOfKeepingBlockEntity;
import dash.dashmode.mixin.EntityRenderDispatcherAccessor;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemJarOverlayRender implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final Set<ItemConvertible> jars;
    private final Map<EntityType<?>, Entity> entities;

    public ItemJarOverlayRender(Block... blocks) {
        jars = Arrays.stream(blocks).map(Block::asItem).collect(Collectors.toSet());
        entities = new HashMap<>();
    }

    @Override
    public void render(ItemStack itemStack, ModelTransformation.Mode mode, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int i1) {
        if (!jars.contains(itemStack.getItem()))
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        Map<EntityType<?>, EntityRenderer<?>> renderers = ((EntityRenderDispatcherAccessor) client.getEntityRenderDispatcher()).rc_getRenderers();

        if (renderers.isEmpty())
            return;

        CompoundTag tag = itemStack.getSubTag(JarOfKeepingBlockEntity.BlockItemTag);
        if (tag == null)
            return;

        CompoundTag entityTag = tag.getCompound(JarOfKeepingBlockEntity.EntityTag);
        if (entityTag == null)
            return;

        EntityType<?> type = EntityType.fromTag(entityTag).orElse(null);
        if (type == null)
            return;

        EntityRenderer renderer = renderers.get(type);
        Entity entity = entities.computeIfAbsent(type, x -> x.create(client.world));
        if (entity == null)
            return;

        float g = 0.093125F;
        float h = Math.max(entity.getWidth(), entity.getHeight());
        if ((double) h > 1.0D) {
            g /= h;
        }

        matrixStack.translate(0.0D, 0.08000000059604645D, 0.0D);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) MathHelper.lerp((double) 0, 0, 0) * 10.0F));
        //matrixStack.translate(0.0D, -0.20000000298023224D, 0.0D);
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-30.0F));
        matrixStack.scale(g, g, g);

        renderer.render(entity, 0, 0, matrixStack, vertexConsumerProvider, 0);
    }
}
