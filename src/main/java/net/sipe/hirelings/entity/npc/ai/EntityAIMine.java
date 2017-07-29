package net.sipe.hirelings.entity.npc.ai;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.sipe.hirelings.entity.npc.EntityNpcBase;

public class EntityAIMine extends EntityAITunnel {

    public EntityAIMine(EntityNpcBase entity, BlockPos workSite, EnumFacing workDirection) {
        super(entity, workSite, workDirection);
    }

    @Override
    public boolean shouldExecute() {
        return false;
    }

    @Override
    public void startExecuting() {
    }

    @Override
    public boolean continueExecuting() {
        return false;
    }


    @Override
    public void updateTask() {

    }

    @Override
    public void resetTask() {

    }
}
