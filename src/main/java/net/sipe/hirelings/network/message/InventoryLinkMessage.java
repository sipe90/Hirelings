package net.sipe.hirelings.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.sipe.hirelings.HirelingsMod;
import net.sipe.hirelings.entity.npc.EntityWorker;
import net.sipe.hirelings.entity.npc.ai.EntityAIDumpItems;

public class InventoryLinkMessage implements IMessage {

    private int entityId;
    private BlockPos tileEntityLocation;

    public InventoryLinkMessage() {}

    public InventoryLinkMessage(int entityId, BlockPos tileEntityLocation) {
        this.entityId = entityId;
        this.tileEntityLocation = tileEntityLocation;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeLong(tileEntityLocation.toLong());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        tileEntityLocation = BlockPos.fromLong(buf.readLong());
    }

    public static class InventoryLinkMessageHandler implements IMessageHandler<InventoryLinkMessage, IMessage> {

        @Override
        public IMessage onMessage(InventoryLinkMessage message, MessageContext ctx) {
            HirelingsMod.PROXY.getThreadFromContext(ctx).addScheduledTask(() -> {
                World world = HirelingsMod.PROXY.getPlayerEntity(ctx).worldObj;
                    Entity entity = world.getEntityByID(message.entityId);
                    TileEntity tileEntity = world.getTileEntity(message.tileEntityLocation);
                    if (tileEntity == null || !(entity instanceof EntityWorker)) {
                        // TODO: Log debug/error?
                        return;
                    }
                EntityAIDumpItems task = (EntityAIDumpItems) ((EntityWorker) entity).getJob().getTask(EntityAIDumpItems.class);
                task.setInventory(tileEntity);
            });
            return null;
        }
    }
}
