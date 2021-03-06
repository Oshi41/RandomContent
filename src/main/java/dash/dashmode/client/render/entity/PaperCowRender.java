package dash.dashmode.client.render.entity;

import dash.dashmode.DashMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PaperCowRender<T extends MobEntity> extends MobEntityRenderer<T, CowEntityModel<T>> {
    private final Identifier texture;

    public PaperCowRender(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new CowEntityModel<>(), 0.7F);
        texture = new Identifier(DashMod.ModId, "textures/entity/paper_cow.png");
    }

    @Override
    public Identifier getTexture(T entity) {
        return texture;
    }
}
