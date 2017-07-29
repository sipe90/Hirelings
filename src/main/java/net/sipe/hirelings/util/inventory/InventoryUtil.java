package net.sipe.hirelings.util.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.sipe.hirelings.entity.npc.EntityNpcBase;

public class InventoryUtil extends ItemHandlerHelper {

    public static boolean isValidInventory(TileEntity tileEntity) {
        if (tileEntity == null) {
            throw new IllegalArgumentException("TileEntity was null");
        }
        return (tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null));
    }

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

    public static boolean hasRoomForItems(IItemHandler handler, ItemStack stack, boolean requireAll) {
        if (stack == null) {
            return true;
        }
        ItemStack result = InventoryUtil.insertItemStacked(handler, stack, true);
        return  result == null || (requireAll ? result.stackSize == 0 : result.stackSize < stack.stackSize);
    }

    public static boolean transferItems(IItemHandlerModifiable from, IItemHandler to, AbstractFilter filter, boolean simulate) {
        boolean couldInsert = false;
        for (int i = 0; i < to.getSlots(); i++) {
            for (int j = 0; j < from.getSlots(); j++) {
                ItemStack before = from.getStackInSlot(j);

                if (before == null || !filter.test(before.getItem())) {
                    continue;
                }

                ItemStack after = InventoryUtil.insertItemStacked(to, before, simulate);

                if (!simulate) {
                    from.setStackInSlot(j, after);
                }

                int beforeStackSize = before.stackSize;
                int afterStackSize = after != null ? after.stackSize : 0;

                if (beforeStackSize != afterStackSize) {
                    couldInsert = true;
                }
            }
        }
        return couldInsert;
    }

    public static boolean isEmpty(IItemHandler inventory) {
       for (int i = 0; i < inventory.getSlots(); i++) {
           if(inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).stackSize > 0) {
               return false;
           }
       }
       return true;
    }
}