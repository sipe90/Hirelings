package net.sipe.hirelings.network.message;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.sipe.hirelings.HirelingsMod;
import net.sipe.hirelings.entity.npc.NpcDataHandler;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class NpcDataMessage implements IMessage {

    private static final char NAME = 'a';
    private static final char EXPERIENCE = 'b';
    private static final char LEVEL = 'c';

    private static final BiMap<Character, String> MAP = HashBiMap.create();
    private static final BiMap<String, Character> INVERSE = MAP.inverse();

    static {
        MAP.put(NAME, NpcDataHandler.TAG_NAME);
        MAP.put(EXPERIENCE, NpcDataHandler.TAG_EXPERIENCE);
        MAP.put(LEVEL, NpcDataHandler.TAG_LEVEL);
    }

    private int entityId;
    private Map<String, Object> data;

    public NpcDataMessage() {}

    public NpcDataMessage(int entityId, Map<String, Object> dataToSend) {
        this.entityId = entityId;
        this.data = dataToSend;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        data = new HashMap<>();
        entityId = buf.readInt();
        while (buf.readerIndex() <  buf.readableBytes()) {
            readTag(buf);
        }
    }

    private void readTag(ByteBuf buf) {
        char tag = buf.readChar();
        switch (tag) {
            case NAME:
                data.put(MAP.get(NAME), readString(buf));
                break;
            case EXPERIENCE:
                data.put(MAP.get(EXPERIENCE), buf.readDouble());
                break;
            case LEVEL:
                data.put(MAP.get(LEVEL), buf.readShort());
                break;
            default:
                throw new IllegalArgumentException("Bad tag at byte index : " + buf.readerIndex() );
        }
    }

    /**
     * Reads a string from the buffer using UTF-8 encoding. The first byte defines the length of the String (max 255 chars).
     * @param buf The ByteBuffer to read from.
     * @return The String read from the buffer.
     */
    private String readString(ByteBuf buf) {
        short length = buf.readByte();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes, 0, bytes.length);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        data.forEach((k,v) -> writeTag(buf, INVERSE.get(k), v));
    }

    private void writeTag(ByteBuf buf, char tag, Object value) {
        buf.writeChar(tag);
        switch (tag) {
            case NAME:
                writeString(buf, (String) value);
                break;
            case EXPERIENCE:
                buf.writeDouble((Double) value);
                break;
            case LEVEL:
                buf.writeShort((Short) value);
                break;
            default:
                throw new IllegalArgumentException("Unknown tag: " + tag);
        }
    }

    /**
     * Writes a string the buffer. The first byte defines the length of the String (max 128 chars).
     * @param buf The ByteBuffer to write to.
     */
    private void writeString(ByteBuf buf, String string) {
        int length = string.length();
        if (length > 128) {
           string = string.substring(0, 128);
        }
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        buf.writeByte(bytes.length);
        buf.writeBytes(bytes);
    }


    public static class NpcDataMessageHandler implements IMessageHandler<NpcDataMessage, IMessage> {

        @Override
        public IMessage onMessage(NpcDataMessage message, MessageContext ctx) {
            HirelingsMod.PROXY.getThreadFromContext(ctx).addScheduledTask( () -> {
                Entity entity = HirelingsMod.PROXY.getPlayerEntity(ctx).worldObj.getEntityByID(message.entityId);
                if (entity == null) {
                    throw new IllegalArgumentException("Unknown entity id");
                }
                if (!(entity.hasCapability(NpcDataHandler.NPC_DATA_STORAGE_CAPABILITY, null))) {
                    throw new IllegalArgumentException("Entity does not have the NPC data storage capability");
                }
                entity.getCapability(NpcDataHandler.NPC_DATA_STORAGE_CAPABILITY, null).updateAttributes(message.data);
            });
            return null;
        }
    }
}
