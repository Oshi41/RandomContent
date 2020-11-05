package dash.dashmode.client.render.entity;

import dash.dashmode.DashMod;
import dash.dashmode.entities.cosmic.CosmoGhastEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.GhastEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CosmoGhastEntityRenderer extends MobEntityRenderer<CosmoGhastEntity, GhastEntityModel<CosmoGhastEntity>> {
    private final Identifier texture;
    private final Identifier angryTexture;

    public CosmoGhastEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new GhastEntityModel<>(), 1.5F);

        texture = new Identifier(DashMod.ModId, "textures/entity/cosmo_ghast.png");
        angryTexture = new Identifier(DashMod.ModId, "textures/entity/cosmo_ghast_angry.png");
    }

    @Override
    protected void scale(CosmoGhastEntity entity, MatrixStack matrices, float amount) {
        float scale = 7;
        matrices.scale(scale, scale, scale);
    }

    @Override
    public Identifier getTexture(CosmoGhastEntity entity) {
        return entity.isAngry() ? angryTexture : texture;
    }
}
