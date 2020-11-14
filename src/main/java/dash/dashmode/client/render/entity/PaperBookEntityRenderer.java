package dash.dashmode.client.render.entity;


import dash.dashmode.client.model.BookModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PaperBookEntityRenderer<T extends MobEntity> extends MobEntityRenderer<T, BookModel<T>> {
    private final Identifier texture;

    public PaperBookEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new BookModel<>(), 0.5F);
        texture = new Identifier("minecraft", "textures/entity/enchanting_table_book.png");
    }

    @Override
    protected void scale(T entity, MatrixStack matrices, float amount) {
        float scale = 2;
        matrices.scale(scale, scale, scale);
        matrices.translate(0, 1, 0);
    }

    @Override
    public Identifier getTexture(T entity) {
        return texture;
    }
}
