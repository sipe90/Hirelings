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

public class EntityAIDumpItems extends EntityAIBase {

    private static final double MAX_INTERACT_DISTANCE = 4.0F;

    private EntityNpcBase entity;
    private TileEntity inventory;
    private AbstractFilter<Item> filter;

    private float speed;
    private int ticksLidOpen = 0;
    private boolean using = false;

    public EntityAIDumpItems(EntityNpcBase entity, float speed) {
        this(entity, speed, (AbstractFilter<Item>)null);
    }

    public EntityAIDumpItems(EntityNpcBase entity, float speed, AbstractFilter<Item> filter) {
        this(entity, speed, null, filter);
    }

    public EntityAIDumpItems(EntityNpcBase entity, float speed, TileEntity tileEntity) {
        this(entity, speed, tileEntity, null);
    }

    public EntityAIDumpItems(EntityNpcBase entity, float speed, TileEntity inventory, AbstractFilter<Item> filter) {
        if (filter == null) { filter = SimpleFilter.allowAllFilter(); }
        this.speed = speed;
        this.filter = filter;
        this.entity = entity;
        this.inventory = inventory;
        if (inventory != null) {
            setInventory(inventory);
            this.entity.setHomePosAndDistance(inventory.getPos(), 2);
        }
        setMutexBits(1);
    }

    private void validateInventory(TileEntity tileEntity) {
        if (tileEntity == null) {
            throw new IllegalArgumentException("Given inventory TileEntity was null");
        }
        if (!tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
           throw new IllegalArgumentException("Given inventory TileEntity does not have ItemHandler capability");
        }
    }

    public void setInventory(BlockPos inventoryPos) {
        TileEntity tileEntity = entity.worldObj.getTileEntity(inventoryPos);
        if (tileEntity != null) {
            setInventory(tileEntity);
        }
    }

    public void setInventory(TileEntity tileEntity) {
        validateInventory(tileEntity);
        this.inventory = tileEntity;
    }

    public TileEntity getInventory() {
        return inventory;
    }

    private IItemHandler getTargetInventory() {
      return  inventory.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    @Override
    public boolean shouldExecute() {
        if (inventory == null || filter.allowNone() || InventoryUtil.isEmpty(entity.getInventoryHandler())) {
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
    public void startExecuting() {}

    @Override
    public boolean continueExecuting() {
        return using || (inventory != null && !InventoryUtil.isEmpty(entity.getInventoryHandler()) && InventoryUtil.transferItems(entity.getInventoryHandler(), getTargetInventory(), filter, true));
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
                    if (!entity.worldObj.isRemote) {
                        entity.worldObj.addBlockEvent(inventory.getPos(), inventory.getBlockType(), 1, --((TileEntityChest) inventory).numPlayersUsing);
                    }
                    using = false;
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
                        entity.worldObj.addBlockEvent(inventory.getPos(), inventory.getBlockType(), 1, ++((TileEntityChest) inventory).numPlayersUsing);
                        using = true;
                    }
                }
            }
        } else if (entity.getNavigator().noPath()) {
            entity.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), speed);
        }
    }

    @Override
    public void resetTask() {
        if (using && inventory instanceof TileEntityChest) {
            if (!entity.worldObj.isRemote) {
                entity.worldObj.addBlockEvent(inventory.getPos(), inventory.getBlockType(), 1, --((TileEntityChest) inventory).numPlayersUsing);
            }
        }
        ticksLidOpen = 0;
        using = false;
    }
}
