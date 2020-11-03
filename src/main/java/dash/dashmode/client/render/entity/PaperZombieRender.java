package dash.dashmode.client.render.entity;

import dash.dashmode.DashMod;
import dash.dashmode.client.model.ZombieModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PaperZombieRender<T extends HostileEntity> extends BipedEntityRenderer<T, AbstractZombieModel<T>> {
    private final Identifier texture;

    public PaperZombieRender(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new ZombieModel<>(0.0F, false), 0.5F);
        this.addFeature(new ArmorFeatureRenderer<>(this, new ZombieModel<>(0.5F, false), new ZombieModel<>(1, false)));

        texture = new Identifier(DashMod.ModId, "textures/entity/paper_zombie.png");
    }

    @Override
    public Identifier getTexture(T entity) {
        return texture;
    }
}
