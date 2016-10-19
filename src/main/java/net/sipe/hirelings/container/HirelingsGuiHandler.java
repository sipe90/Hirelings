package net.sipe.hirelings.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.sipe.hirelings.entity.npc.EntityNpcBase;
import net.sipe.hirelings.gui.GuiContainerNpcEntity;

public class HirelingsGuiHandler implements IGuiHandler {

    public static final int HIRELINGS_NPC_GUI = 0;

    /**
     * In case of entities, the x coordinate is used as the entity id.
     */
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == HIRELINGS_NPC_GUI) {
            EntityNpcBase entity = (EntityNpcBase) world.getEntityByID(x);
            return new ContainerNpcEntity(getPlayerInventory(player), entity);
        }
        return null;
    }

    /**
     * In case of entities, the x coordinate is used as the entity id.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == HIRELINGS_NPC_GUI) {
            EntityNpcBase entity = (EntityNpcBase) world.getEntityByID(x);
            return new GuiContainerNpcEntity(getPlayerInventory(player), entity);
        }
        return null;
    }

    private IItemHandler getPlayerInventory(EntityPlayer player) {
        return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
    }
}
