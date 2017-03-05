package net.sipe.hirelings.entity.npc.job;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.sipe.hirelings.entity.npc.EntityNpcBase;
import net.sipe.hirelings.entity.npc.ai.EntityAIEmptyInventory;
import net.sipe.hirelings.entity.npc.ai.EntityAIGather;
import net.sipe.hirelings.entity.npc.ai.EntityAIMoveTowardsRestrictionAdvanced;
import net.sipe.hirelings.util.inventory.AbstractFilter;
import net.sipe.hirelings.util.inventory.SimpleFilter;

import java.util.Iterator;
import java.util.Set;

public class JobCollector extends JobBase {

    private EntityAIMoveTowardsRestrictionAdvanced restrictTask;
    private EntityAIGather gatherTask;
    private EntityAIEmptyInventory dumpTask;

    private AbstractFilter<Item> itemFilter;

    public JobCollector() {
        super();
    }

    @Override
    public void initTasks(EntityNpcBase entity) {
        itemFilter = SimpleFilter.allowAllFilter();

        restrictTask = new EntityAIMoveTowardsRestrictionAdvanced(entity, 0.7F);
        gatherTask = new EntityAIGather(entity, 0.7F, itemFilter);
        dumpTask = new EntityAIEmptyInventory(entity, 0.7F, itemFilter);

        jobTasks.add(restrictTask);
        jobTasks.add(gatherTask);
        jobTasks.add(dumpTask);

        entity.tasks.addTask(2, restrictTask);
        entity.tasks.addTask(3, gatherTask);
        entity.tasks.addTask(4, dumpTask);
    }

    @Override
    public void writeJobToNBT(NBTTagCompound compound) {

        Set<Item> itemsSet = itemFilter.getItems();
        Iterator<Item> iterator = itemsSet.iterator();
        int[] items = new int[itemsSet.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = Item.getIdFromItem(iterator.next());
        }

        BlockPos inventoryPos = dumpTask.getInventoryPos();
        if (inventoryPos != null) {
            compound.setLong("inventory", inventoryPos.toLong());
        }
        compound.setIntArray("filterItems", items);
        compound.setString("filterType", itemFilter.getType().toString());
    }

    @Override
    public void readJobFromNBT(NBTTagCompound compound) {
        int[] items = compound.getIntArray("filterItems");
        for (int id : items) {
            itemFilter.getItems().add(Item.getItemById(id));
        }
        itemFilter.setType(AbstractFilter.Type.valueOf(compound.getString("filterType")));
        if (compound.hasKey("inventory")) {
            BlockPos invPos = BlockPos.fromLong(compound.getLong("inventory"));
            dumpTask.setInventoryPos(invPos);
        }
    }
}
