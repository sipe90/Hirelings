package net.sipe.hirelings.capability.provider;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.sipe.hirelings.entity.player.PlayerProperties;

import javax.annotation.Nullable;

import static net.sipe.hirelings.capability.HirelingsCapabilities.PLAYER_PROPERTIES_CAPABILITY;

public class PlayerPropertiesProvider implements ICapabilitySerializable<NBTTagCompound> {

    private PlayerProperties properties = PLAYER_PROPERTIES_CAPABILITY.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == PLAYER_PROPERTIES_CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return PLAYER_PROPERTIES_CAPABILITY.cast(properties);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return new NBTTagCompound();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

    }

    public static class Storage implements Capability.IStorage<PlayerProperties>
    {
        @Override
        public NBTBase writeNBT(Capability<PlayerProperties> capability, PlayerProperties instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<PlayerProperties> capability, PlayerProperties instance, EnumFacing side, NBTBase nbt) {

        }
    }
}
