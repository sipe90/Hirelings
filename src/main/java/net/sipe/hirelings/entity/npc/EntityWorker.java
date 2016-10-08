package net.sipe.hirelings.entity.npc;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import net.sipe.hirelings.entity.npc.ai.EntityNpcAIGather;
import net.sipe.hirelings.entity.npc.job.JobBase;

import java.util.HashSet;
import java.util.Set;

public class EntityWorker extends EntityNpcBase {

    private JobBase job;

    public EntityWorker(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void setupAITasks() {
        super.setupAITasks();

        Set<Item> itemsToPickup = new HashSet<>();
        itemsToPickup.add(ItemBlock.getItemFromBlock(Blocks.COBBLESTONE));
        tasks.addTask(2, new EntityNpcAIGather(this, itemsToPickup));
    }



}
