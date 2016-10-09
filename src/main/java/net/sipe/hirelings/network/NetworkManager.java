package net.sipe.hirelings.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.sipe.hirelings.HirelingsMod;
import net.sipe.hirelings.network.message.InventoryLinkMessage;
import net.sipe.hirelings.network.message.PlayerPropertiesMessage;

public class NetworkManager {

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(HirelingsMod.MOD_ID);

    public static void init() {
        registerMessages();
    }

    private static void registerMessages() {
        int id = 0;

        NETWORK.registerMessage(PlayerPropertiesMessage.PlayerPropertiesMessageHandler.class, PlayerPropertiesMessage.class, id++, Side.CLIENT);
        NETWORK.registerMessage(InventoryLinkMessage.InventoryLinkMessageHandler.class, InventoryLinkMessage.class, id++, Side.CLIENT);
    }

}
