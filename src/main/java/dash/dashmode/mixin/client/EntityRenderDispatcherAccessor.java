package dash.dashmode.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Environment(EnvType.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public interface EntityRenderDispatcherAccessor {
    @Accessor("renderers")
    Map<EntityType<?>, EntityRenderer<?>> rc_getRenderers();
}
