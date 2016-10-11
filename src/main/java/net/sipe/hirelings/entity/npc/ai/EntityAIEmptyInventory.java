package net.sipe.hirelings.entity.npc.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.sipe.hirelings.entity.npc.EntityNpcBase;
import net.sipe.hirelings.util.inventory.AbstractFilter;
import net.sipe.hirelings.util.inventory.InventoryUtil;
import net.sipe.hirelings.util.inventory.SimpleFilter;

public class EntityAIEmptyInventory extends EntityAIBase {

    private static final double MAX_INTERACT_DISTANCE = 4.0F;

    private EntityNpcBase entity;
    private BlockPos inventoryPos;
    private TileEntity inventory;
    private AbstractFilter<Item> filter;

    private float speed;
    private int ticksLidOpen = 0;
    private boolean using = false;

    public EntityAIEmptyInventory(EntityNpcBase entity, float speed) {
        this(entity, speed, (AbstractFilter<Item>)null);
    }

    public EntityAIEmptyInventory(EntityNpcBase entity, float speed, AbstractFilter<Item> filter) {
        this(entity, speed, null, filter);
    }

    public EntityAIEmptyInventory(EntityNpcBase entity, float speed, BlockPos inventoryPos) {
        this(entity, speed, inventoryPos, null);
    }

    public EntityAIEmptyInventory(EntityNpcBase entity, float speed, BlockPos inventoryPos, AbstractFilter<Item> filter) {
        if (filter == null) { filter = SimpleFilter.allowAllFilter(); }
        this.speed = speed;
        this.filter = filter;
        this.entity = entity;
        this.inventoryPos = inventoryPos;
        setMutexBits(1);
    }

    public void setInventory(TileEntity inventory) {
        validateInventory(inventory);
        this.inventory = inventory;
        this.inventoryPos = inventory.getPos();
    }

    public void setInventoryPos(BlockPos inventoryPos) {
        this.inventoryPos = inventoryPos;
    }

    private void loadInventoryFromPos() {
        TileEntity tileEntity = entity.worldObj.getTileEntity(inventoryPos);
        validateInventory(tileEntity);
        entity.setHomePosAndDistance(inventoryPos, (int)entity.getMaximumHomeDistance());
        this.inventory = tileEntity;
    }

    private void validateInventory(TileEntity tileEntity) {
        if (tileEntity == null) {
            throw new IllegalArgumentException("TileEntity not found at given BlockPos");
        }
        if (!tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            throw new IllegalArgumentException("Given TileEntity does not have ItemHandler capability");
        }
    }

    public BlockPos getInventoryPos() {
        return inventoryPos;
    }

    private IItemHandler getTargetInventory() {
      return  inventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    @Override
    public boolean shouldExecute() {
        if (inventoryPos == null) {
            return false;
        }
        if (inventory == null || !inventory.getPos().equals(inventoryPos)) {
            loadInventoryFromPos();
        }
        if (inventory.isInvalid()) {
            inventoryPos = null;
            inventory = null;
            return false;
        }
        if (filter.allowNone() || InventoryUtil.isEmpty(entity.getInventoryHandler())) {
            return false;
        }
        for (int i = 0; i < entity.getInventoryHandler().getSlots(); i++) {
            ItemStack itemStack = entity.getInventoryHandler().getStackInSlot(i);
            if (filter.test(itemStack.getItem()) && InventoryUtil.hasRoomForItems(getTargetInventory(), itemStack, false)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void startExecuting() {
    }

    @Override
    public boolean continueExecuting() {
        return using || (!inventory.isInvalid() && !InventoryUtil.isEmpty(entity.getInventoryHandler()) && InventoryUtil.transferItems(entity.getInventoryHandler(), getTargetInventory(), filter, true));
    }

    @Override
    public void updateTask() {
        if (inventory.isInvalid()) {
            inventory = null;
            using = false;
            return;
        }
        if (inventory instanceof TileEntityChest) {
            if (using) {
                if (ticksLidOpen >= 20D) {
                    sendChestBlockEvent();
                    ticksLidOpen = 0;
                } else {
                    ticksLidOpen++;
                }
            }
        }
        BlockPos pos = inventory.getPos();
        double distanceToInventory = entity.getDistanceSq(pos);
        if (distanceToInventory <= MAX_INTERACT_DISTANCE) {
            entity.getNavigator().clearPathEntity();
            if (InventoryUtil.transferItems(entity.getInventoryHandler(), getTargetInventory(), filter, false)) {
                if (inventory instanceof TileEntityChest) {
                    if (!using) {
                        sendChestBlockEvent();
                    }
                }
            }
        } else if (entity.getNavigator().noPath()) {
            entity.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), speed);
        }
    }

    private void sendChestBlockEvent() {
        if (entity.worldObj.isRemote) {
            return;
        }
        int eventParam = using ? --((TileEntityChest) inventory).numPlayersUsing : ++((TileEntityChest) inventory).numPlayersUsing;
        entity.worldObj.addBlockEvent(inventory.getPos(), inventory.getBlockType(), 1, eventParam);
        using = !using;
    }

    @Override
    public void resetTask() {
        if (using && inventory instanceof TileEntityChest) {
            sendChestBlockEvent();
        }
        ticksLidOpen = 0;
        using = false;
    }
}
