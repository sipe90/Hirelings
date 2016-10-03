package net.sipe.hirelings.entity.npc;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.sipe.hirelings.util.NameGen;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NpcDataHandler {

    public static final String TAG_NAME                = "name";
    public static final String TAG_LEVEL               = "level";
    public static final String TAG_EXPERIENCE          = "experience";

    private String name = NameGen.randomName();
    private short level = 0;
    private double experience = 0.0D;

    private Set<String> dirtyAttributes = new HashSet<>();

    @CapabilityInject(NpcDataHandler.class)
    public static Capability<NpcDataHandler> NPC_DATA_STORAGE_CAPABILITY = null;

    public NpcDataHandler() {}

    public void updateAttributes(Map<String, Object> data) {
        data.forEach(this::setAttribute);
    }

    private void setAttribute(String tag, Object value) {
        switch (tag) {
            case TAG_NAME:
                name = (String) value;
                break;
            case TAG_LEVEL:
                level = (short) value;
                break;
            case TAG_EXPERIENCE:
                experience = (double) value;
                break;
            default:
                throw new IllegalArgumentException("Invalid tag: " + tag);
        }
    }

    private Object getAttribute(String tag) {
        switch (tag) {
            case TAG_NAME:
                return name;
            case TAG_LEVEL:
                return level;
            case TAG_EXPERIENCE:
               return experience;
            default:
                throw new IllegalArgumentException("Invalid tag: " + tag);
        }
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(NpcDataHandler.class, new Capability.IStorage<NpcDataHandler>() {

            @Override
            public NBTBase writeNBT(Capability<NpcDataHandler> capability, NpcDataHandler instance, EnumFacing side) {
                NBTTagCompound nbtCompound = new NBTTagCompound();
                nbtCompound.setString(TAG_NAME, instance.getName());
                nbtCompound.setShort(TAG_LEVEL, instance.getLevel());
                nbtCompound.setDouble(TAG_EXPERIENCE,instance.getExperience());
                return nbtCompound;
            }

            @Override
            public void readNBT(Capability<NpcDataHandler> capability, NpcDataHandler instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound nbtCompound = (NBTTagCompound) nbt;
                instance.setName(nbtCompound.getString(TAG_NAME));
                instance.setLevel(nbtCompound.getShort(TAG_LEVEL));
                instance.setExperience(nbtCompound.getDouble(TAG_EXPERIENCE));
            }
        }, NpcDataHandler::new);
    }

    public Map<String, Object> getDirtyAttributes() {
        return dirtyAttributes.stream().collect(Collectors.toMap(tag -> tag, this::getAttribute));
    }

    private void setDirty(String tag) {
        dirtyAttributes.add(tag);
    }

    public void setClean() {
        dirtyAttributes.clear();
    }

    public boolean isDirty() {
        return !dirtyAttributes.isEmpty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        setDirty(TAG_NAME);
        this.name = name;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        setDirty(TAG_LEVEL);
        this.level = level;
    }

    public double getExperience() {
        return experience;
    }

    public void setExperience(double experience) {
        setDirty(TAG_EXPERIENCE);
        this.experience = experience;
    }
}
