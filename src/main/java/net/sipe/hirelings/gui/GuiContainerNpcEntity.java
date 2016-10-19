package net.sipe.hirelings.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.sipe.hirelings.HirelingsMod;
import net.sipe.hirelings.container.ContainerNpcEntity;
import net.sipe.hirelings.entity.npc.EntityNpcBase;

import java.io.IOException;

public class GuiContainerNpcEntity extends GuiContainer {

    private static final ResourceLocation INVENTORY_NPC_BACKGROUND = new ResourceLocation(HirelingsMod.MOD_ID, "textures/gui/container/inventory_npc.png");

    private final EntityNpcBase entity;

    /** The old x position of the mouse pointer */
    private float oldMouseX;
    /** The old y position of the mouse pointer */
    private float oldMouseY;

    public GuiContainerNpcEntity(IItemHandler playerInventory, EntityNpcBase entity) {
        super(new ContainerNpcEntity(playerInventory, entity));
        this.allowUserInput = true;
        this.entity = entity;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.oldMouseX = (float)mouseX;
        this.oldMouseY = (float)mouseY;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(INVENTORY_NPC_BACKGROUND);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);

        entity.setInventoryRendering(true);
        GuiInventory.drawEntityOnScreen(guiLeft + 47, guiTop + 75, 30, (float)(guiLeft + 51) - this.oldMouseX, (float)(guiTop + 75 - 50) - this.oldMouseY, entity);
        entity.setInventoryRendering(false);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(I18n.format(entity.getNpcName(), new Object[0]), 80, 6, 4210752);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {}
}
