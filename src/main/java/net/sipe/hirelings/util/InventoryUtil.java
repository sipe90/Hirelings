package net.sipe.hirelings.util;

import net.sipe.hirelings.entity.npc.EntityNpcBase;

public class InventoryUtil {

    public static int getFreeInventorySlots(EntityNpcBase entity) {
        int freeSlots = 0;
        for (int i = 0; i < entity.getInventoryHandler().getSlots(); i++) {
            if (entity.getInventoryHandler().getStackInSlot(i) == null) {
                freeSlots++;
            }
        }
        return freeSlots;
    }

    public static int getTotalInventorySlots(EntityNpcBase entity) {
        return entity.getInventoryHandler().getSlots();
    }

}
