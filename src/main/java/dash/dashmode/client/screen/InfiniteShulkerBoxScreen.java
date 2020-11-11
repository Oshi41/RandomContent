package dash.dashmode.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dash.dashmode.screen.InfiniteShulkerBoxScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Environment(EnvType.CLIENT)
public class InfiniteShulkerBoxScreen extends HandledScreen<InfiniteShulkerBoxScreenHandler> {
    private final Identifier TEXTURE = new Identifier("textures/gui/container/shulker_box.png");

    public InfiniteShulkerBoxScreen(InfiniteShulkerBoxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        ++this.backgroundHeight;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y) {
        List<Text> tooltipFromItem = this.getTooltipFromItem(stack);

        int count = stack.getCount();

        if (count > 100) {
            String format = NumberFormat.getInstance(Locale.US).format(count);
            tooltipFromItem.add(new LiteralText(format));
        }

        this.renderTooltip(matrices, tooltipFromItem, x, y);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }
}
