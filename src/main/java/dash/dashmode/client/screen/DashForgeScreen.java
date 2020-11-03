package dash.dashmode.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dash.dashmode.DashMod;
import dash.dashmode.screen.DashForgeScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.BaseText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public class DashForgeScreen extends HandledScreen<DashForgeScreenHandler> implements ScreenHandlerListener {
    private final Identifier texture;

    public DashForgeScreen(DashForgeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.texture = new Identifier(DashMod.ModId, "textures/gui/container/dash_forge.png");
        this.titleX = 40;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        RenderSystem.disableBlend();
        //this.renderForeground(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.disableBlend();
        super.drawForeground(matrices, mouseX, mouseY);
        DashForgeScreenHandler handler = this.handler;

        // green
        int color = 8453920;
        int level = handler.getLevelCost();
        BaseText text = null;

        if (level > 0) {
            text = new TranslatableText("container.repair.cost", level);
            if (!handler.getSlot(10).canTakeItems(playerInventory.player)) {
                // red
                color = 16736352;
            }
        }

        if (text != null) {
            int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 2;
            int start = 70;

            fill(matrices, k - 2, start, this.backgroundWidth - 8, start + 12, 1325400064);
            this.textRenderer.drawWithShadow(matrices, text, (float) k, start + 2, color);
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(this.texture);
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        //this.drawTexture(matrices, i + 59, j + 20, 0, this.backgroundHeight + (this.handler.getSlot(0).hasStack() ? 0 : 16), 110, 16);
        if ((this.handler.getSlot(0).hasStack() || this.handler.getSlot(1).hasStack()) && !this.handler.getSlot(2).hasStack()) {
            this.drawTexture(matrices, i + 99, j + 45, this.backgroundWidth, 0, 28, 21);
        }
    }

    @Override
    public void onHandlerRegistered(ScreenHandler handler, DefaultedList<ItemStack> stacks) {

    }

    @Override
    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {

    }

    @Override
    public void onPropertyUpdate(ScreenHandler handler, int property, int value) {

    }
}
