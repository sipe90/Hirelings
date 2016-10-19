package net.sipe.hirelings.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.sipe.hirelings.entity.npc.EntityNpcBase;

import static net.sipe.hirelings.util.Constants.*;

public class ContainerNpcEntity extends Container {

    private static final int NUM_ROWS = 3;
    private static final int SLOTS_PER_ROW = 5;
    private static final int TOTAL_SLOTS = SLOTS_PER_ROW * NUM_ROWS;

    private static final int LEFT_OFFSET = 80;
    private static final int TOP_OFFSET = 17;

    private static final int PLAYER_INVENTORY_LEFT_OFFSET = 8;
    private static final int PLAYER_INVENTORY_TOP_OFFSET = 84;

    private static final int PLAYER_HOT_BAR_LEFT_OFFSET = 8;
    private static final int PLAYER_HOT_BAR_TOP_OFFSET = 142;

    private IItemHandler playerInventory;

    private EntityNpcBase entity;

    public ContainerNpcEntity(IItemHandler playerInventory, EntityNpcBase entity) {
        this.playerInventory = playerInventory;
        this.entity = entity;
        createSlots();
    }

    private void createSlots() {
        // Npc Entity, Slot 0-14, Slot IDs 0-14
        for (int y = 0; y < NUM_ROWS; ++y) {
            for (int x = 0; x < SLOTS_PER_ROW; ++x) {
                this.addSlotToContainer(buildSlot(entity.getInventoryHandler(), x, y, 0, SLOTS_PER_ROW, LEFT_OFFSET, TOP_OFFSET));
            }
        }

        // Player Inventory, Slot 9-35, Slot IDs 15-51
        for (int y = 0; y < CONTAINER_PLAYER_ROWS; ++y) {
            for (int x = 0; x < CONTAINER_PLAYER_SLOTS_PER_ROW; ++x) {
                this.addSlotToContainer(buildSlot(playerInventory, x, y, CONTAINER_PLAYER_HOT_BAR_SLOTS, CONTAINER_PLAYER_SLOTS_PER_ROW, PLAYER_INVENTORY_LEFT_OFFSET, PLAYER_INVENTORY_TOP_OFFSET));
            }
        }

        // Player Hot bar, Slot 0-8, Slot IDs 52-61
        for (int x = 0; x < 9; ++x) {
            this.addSlotToContainer(buildSlot(playerInventory, x, 0, 0, CONTAINER_PLAYER_SLOTS_PER_ROW, PLAYER_HOT_BAR_LEFT_OFFSET, PLAYER_HOT_BAR_TOP_OFFSET));
        }
    }

    private Slot buildSlot(IItemHandler handler, int x, int y, int startIndex, int slotsPerRow, int leftOffset, int topOffset) {
        return new SlotItemHandler(handler, x + y * slotsPerRow + startIndex, leftOffset + x * CONTAINER_SLOT_OUTER_SIZE, topOffset + y * CONTAINER_SLOT_OUTER_SIZE);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int fromSlot) {
        ItemStack previous = null;
        Slot slot = this.inventorySlots.get(fromSlot);

        if (slot != null && slot.getHasStack()) {
            ItemStack current = slot.getStack();
            previous = current.copy();

            if (fromSlot < TOTAL_SLOTS) {
                // From Entity Inventory to Player Inventory
                if (!this.mergeItemStack(current, CONTAINER_PLAYER_HOT_BAR_SLOTS, CONTAINER_PLAYER_INVENTORY_SLOTS + CONTAINER_PLAYER_HOT_BAR_SLOTS, true)) {
                    return null;
                }
            } else {
                // From Player Inventory to Entity Inventory
                if (!this.mergeItemStack(current, 0, TOTAL_SLOTS, false)) {
                    return null;
                }
            }

            if (current.stackSize == 0) {
                slot.putStack(null);
            }
            else {
                slot.onSlotChanged();
            }

            if (current.stackSize == previous.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(playerIn, current);
        }
        return previous;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
