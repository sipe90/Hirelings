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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.sipe.hirelings.util.DebugUtil;
import net.sipe.hirelings.util.NameGen;

public abstract class EntityNpcBase extends EntityCreature {

    private static final DataParameter<String> NAME = EntityDataManager.createKey(EntityNpcBase.class, DataSerializers.STRING);
    private static final DataParameter<Byte> LEVEL = EntityDataManager.createKey(EntityNpcBase.class, DataSerializers.BYTE);
    private static final DataParameter<Float> EXPERIENCE = EntityDataManager.createKey(EntityNpcBase.class, DataSerializers.FLOAT);

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("hirelings:textures/entity/npc/default.png");

    public EntityNpcBase(World worldIn) {
        super(worldIn);
        enablePersistence();
        setAlwaysRenderNameTag(true);
        setCanPickUpLoot(true);
        setLeftHanded(Math.random() >= 0.5D);
        setupAITasks();
    }

    @Override
    public void entityInit() {
        super.entityInit();
        dataManager.register(NAME, NameGen.randomName());
        dataManager.register(LEVEL, (byte)0);
        dataManager.register(EXPERIENCE,0.0F);
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
        setCustomNameTag(getNpcName() + " [" + getLevel() + "] "
                + "§4" + DebugUtil.getActiveAITasksAsString(this)
                + "§r : §6" + DebugUtil.getActiveAITargetTasksAsString(this) + "§r");
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    public void setNpcName(String name) {
        dataManager.set(NAME, name);
    }

    public String getNpcName() {
        return dataManager.get(NAME);
    }

    public void setLevel(byte level) {
        dataManager.set(LEVEL, level);
    }

    public byte getLevel() {
        return dataManager.get(LEVEL);
    }

    public void setExperience(float experience) {
        dataManager.set(EXPERIENCE, experience);
    }

    public float getExperience() {
        return dataManager.get(EXPERIENCE);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
       super.writeEntityToNBT(compound);
        compound.setString("npcName", getNpcName());
        compound.setByte("npcLevel", getLevel());
        compound.setFloat("npcExperience", getExperience());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setNpcName(compound.getString("npcName"));
        setLevel(compound.getByte("npcLevel"));
        setExperience(compound.getFloat("npcExperience"));
    }

    @Override
    public boolean isAIDisabled() { return false;}

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }
}
