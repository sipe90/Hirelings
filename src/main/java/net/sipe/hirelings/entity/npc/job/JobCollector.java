package net.sipe.hirelings.entity.npc.job;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.sipe.hirelings.entity.npc.EntityNpcBase;
import net.sipe.hirelings.entity.npc.ai.EntityAIDumpItems;
import net.sipe.hirelings.entity.npc.ai.EntityAIGather;
import net.sipe.hirelings.util.inventory.AbstractFilter;
import net.sipe.hirelings.util.inventory.SimpleFilter;

import java.util.Iterator;
import java.util.Set;

public class JobCollector extends JobBase {

    private EntityAIGather gatherTask;
    private EntityAIDumpItems dumpTask;

    private AbstractFilter<Item> itemFilter;

    public JobCollector() {
        super();
    }

    @Override
    public void initTasks(EntityNpcBase entity) {
        itemFilter = SimpleFilter.allowAllFilter();

        gatherTask = new EntityAIGather(entity, 0.7F, itemFilter);
        dumpTask = new EntityAIDumpItems(entity, 0.7F, itemFilter);

        jobTasks.add(gatherTask);
        jobTasks.add(dumpTask);

        entity.tasks.addTask(2, gatherTask);
        entity.tasks.addTask(3, dumpTask);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {

        TileEntity inventory = dumpTask.getInventory();
        Set<Item> itemsSet = itemFilter.getItems();
        Iterator<Item> iterator = itemsSet.iterator();
        int[] items = new int[itemsSet.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = Item.getIdFromItem(iterator.next());
        }

        if (inventory != null) {
            compound.setLong("inventory", inventory.getPos().toLong());
        }
        compound.setIntArray("filterItems", items);
        compound.setString("filterType", itemFilter.getType().toString());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        int[] items = compound.getIntArray("filterItems");
        for (int id : items) {
            itemFilter.getItems().add(Item.getItemById(id));
        }
        itemFilter.setType(AbstractFilter.Type.valueOf(compound.getString("filterType")));
        if (compound.hasKey("inventory")) {
            BlockPos invPos = BlockPos.fromLong(compound.getLong("inventory"));
            dumpTask.setInventory(invPos);
        }
    }
}
