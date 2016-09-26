package net.sipe.hirelings.entity.npc;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.sipe.hirelings.util.DebugUtil;

public abstract class EntityNpcBase extends EntityCreature {

    private static final DataParameter<Integer> DATA_LEVEL = EntityDataManager.createKey(EntityNpcBase.class, DataSerializers.VARINT);

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("hirelings:textures/entity/npc/default.png");

    // Persistent data
    private int level;

    public EntityNpcBase(World worldIn) {
        super(worldIn);
        enablePersistence();
        setAlwaysRenderNameTag(true);
        setCanPickUpLoot(true);
        setLeftHanded(Math.random() >= 0.5D);
        setupAITasks();
    }

    @Override
    public void entityInit()
    {
        super.entityInit();
        dataManager.register(DATA_LEVEL, 0);
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
        setCustomNameTag(getClass().getSimpleName() + " [" + getLevel() + "] " + DebugUtil.getActiveTaskListsAsString(this));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }

    @Override
    public boolean isAIDisabled() { return false;}

    public int getLevel() {
        return level;
    }

    public int levelUp() {
        level++;
        dataManager.set(DATA_LEVEL, level);
        return level;
    }
}
