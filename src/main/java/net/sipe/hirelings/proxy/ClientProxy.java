package net.sipe.hirelings.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.sipe.hirelings.HirelingsMod;
import net.sipe.hirelings.entity.npc.EntityNpcBase;
import net.sipe.hirelings.entity.npc.RenderNpcBase;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(HirelingsMod.MOD_ID + ":" + id, "inventory"));
    }

    @Override
    public void registerModelRendering() {
        RenderingRegistry.registerEntityRenderingHandler(EntityNpcBase.class, RenderNpcBase::new);
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx));
    }

    @Override
    public IThreadListener getThreadFromContext(MessageContext ctx) {
        return (ctx.side.isClient() ?  Minecraft.getMinecraft() : super.getThreadFromContext(ctx));
    }

}
