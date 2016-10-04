package net.sipe.hirelings.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.sipe.hirelings.HirelingsMod;

public class NetworkManager {

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(HirelingsMod.MOD_ID);

    public static void init() {
        registerMessages();
    }

    private static void registerMessages() {
        int id = 0;

       // NETWORK.registerMessage(?.class, NpcDataMessage.class, id++, Side.CLIENT);
    }

}
