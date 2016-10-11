package net.sipe.hirelings.entity.npc.job;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.sipe.hirelings.entity.npc.EntityNpcBase;

import java.util.ArrayList;
import java.util.List;

public abstract class JobBase {

    protected BlockPos homePos;

    protected List<EntityAIBase> jobTasks = new ArrayList<>();
    protected List<EntityAIBase> jobTargetTasks = new ArrayList<>();

    public abstract void initTasks(EntityNpcBase entity);

    public abstract void readEntityFromNBT(NBTTagCompound compound);

    public abstract  void writeEntityToNBT(NBTTagCompound compound);

    // FIXME: Returns first added task in case of duplicate tasks.
    public EntityAIBase getTask(Class<? extends EntityAIBase> aiClass) {
        for (EntityAIBase task : jobTasks) {
            if (task.getClass().equals(aiClass)) {
             return task;
            }
        }
        return null;
    }

}
