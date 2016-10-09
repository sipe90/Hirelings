package net.sipe.hirelings.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.sipe.hirelings.capability.provider.PlayerPropertiesProvider;
import net.sipe.hirelings.entity.player.PlayerProperties;

public class HirelingsCapabilities {

    @CapabilityInject(PlayerProperties.class)
    public static Capability<PlayerProperties> PLAYER_PROPERTIES_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(PlayerProperties.class, new PlayerPropertiesProvider.Storage(), PlayerProperties::new);
    }
}
