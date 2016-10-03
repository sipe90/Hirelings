package net.sipe.hirelings.entity.npc;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.sipe.hirelings.network.NetworkManager;
import net.sipe.hirelings.network.message.NpcDataMessage;
import net.sipe.hirelings.util.DebugUtil;

public abstract class EntityNpcBase extends EntityCreature {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("hirelings:textures/entity/npc/default.png");

    private final NpcDataHandler dataHandler;

    public EntityNpcBase(World worldIn) {
        super(worldIn);
        dataHandler = new NpcDataHandler();
        enablePersistence();
        setAlwaysRenderNameTag(true);
        setCanPickUpLoot(true);
        setLeftHanded(Math.random() >= 0.5D);
        setupAITasks();
    }

    public ResourceLocation getTexture() {
        return DEFAULT_TEXTURE;
    }

    private void setupAITasks() {
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();

        // Self-preservation
        tasks.addTask(0, new EntityAISwimming(this));
        // TODO: Make a more clever implementation
        tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityMob.class, 8.0F, 0.6D, 0.8D));
        // Tasks

        // Idle
        tasks.addTask(8, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        tasks.addTask(9, new EntityAIWatchClosest2(this, EntityLiving.class, 6.0F, 0.02F));
        tasks.addTask(10, new EntityAILookIdle(this));
    }

    @Override
    public void updateAITasks() {
        super.updateAITasks();
        setCustomNameTag(dataHandler.getName() + " [" + dataHandler.getLevel() + "] "
                + "§4" + DebugUtil.getActiveAITasksAsString(this)
                + "§r : §6" + DebugUtil.getActiveAITargetTasksAsString(this) + "§r");
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!worldObj.isRemote) {
            if (dataHandler.isDirty()) {
                NetworkManager.NETWORK.sendToAll(new NpcDataMessage(getEntityId(), dataHandler.getDirtyAttributes()));
                dataHandler.setClean();
            }
        }
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("npcData", NpcDataHandler.NPC_DATA_STORAGE_CAPABILITY.writeNBT(dataHandler, null));
        return nbt;
    }
    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NBTBase list = nbt.getTag("npcData");
        NpcDataHandler.NPC_DATA_STORAGE_CAPABILITY.readNBT(dataHandler, null, list);
    }

    @Override
    public boolean isAIDisabled() { return false;}

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability ==  NpcDataHandler.NPC_DATA_STORAGE_CAPABILITY) {
            return NpcDataHandler.NPC_DATA_STORAGE_CAPABILITY.cast(dataHandler);
        }
        return super.getCapability(capability, facing);
    }
}
