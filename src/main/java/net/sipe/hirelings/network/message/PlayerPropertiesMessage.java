package net.sipe.hirelings.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.sipe.hirelings.HirelingsMod;
import net.sipe.hirelings.capability.HirelingsCapabilities;
import net.sipe.hirelings.entity.player.PlayerProperties;

public class PlayerPropertiesMessage implements IMessage {

    private PlayerProperties properties;

    public PlayerPropertiesMessage() {}

    public PlayerPropertiesMessage(PlayerProperties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("PlayerProperties cannot be null");
        }
        this.properties = properties;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(properties.getLinkEntityId());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        properties = new PlayerProperties();
        properties.setLinkEntity(buf.readInt());
    }

    public static class PlayerPropertiesMessageHandler implements IMessageHandler<PlayerPropertiesMessage, IMessage> {

        @Override
        public IMessage onMessage(PlayerPropertiesMessage message, MessageContext ctx) {
            HirelingsMod.PROXY.getThreadFromContext(ctx).addScheduledTask(() -> {
               EntityPlayer player = HirelingsMod.PROXY.getPlayerEntity(ctx);
                if (player.hasCapability(HirelingsCapabilities.PLAYER_PROPERTIES_CAPABILITY, null)) {
                    PlayerProperties properties = player.getCapability(HirelingsCapabilities.PLAYER_PROPERTIES_CAPABILITY, null);
                    properties.setLinkEntity(message.properties.getLinkEntityId());
                }
            });
            return null;
        }
    }
}
