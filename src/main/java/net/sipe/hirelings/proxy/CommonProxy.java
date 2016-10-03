package net.sipe.hirelings.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {

    public void registerItemRenderer(Item item, int meta, String id) {}

    public <T extends Entity> void registerModelRenderer(Class<T> entityClass, IRenderFactory<T> renderFactory) {}

    public void registerEventHandler(Object eventHandler) {
        MinecraftForge.EVENT_BUS.register(eventHandler);
    }

    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity;
    }

    public IThreadListener getThreadFromContext(MessageContext ctx) {
        return ctx.getServerHandler().playerEntity.getServer();
    }
}