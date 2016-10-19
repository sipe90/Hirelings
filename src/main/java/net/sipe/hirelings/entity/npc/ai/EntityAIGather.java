package net.sipe.hirelings.entity.npc.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.sipe.hirelings.entity.npc.EntityNpcBase;
import net.sipe.hirelings.util.inventory.AbstractFilter;
import net.sipe.hirelings.util.inventory.InventoryUtil;
import net.sipe.hirelings.util.inventory.SimpleFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityAIGather extends EntityAIBase {

    private static final double PICKUP_RANGE = 2.0D;
    private static final int DEF_RADIUS = 10;

    private final EntityNpcBase entity;

    private int radius;
    private AbstractFilter<Item> filter;
    private float speed;
    private List<EntityItem> nearbyItems = new ArrayList<>();
    private EntityItem currentTargetedItem;

    public EntityAIGather(EntityNpcBase entity, float speed) {
        this(entity, speed, null);
    }

    public EntityAIGather(EntityNpcBase entity, float speed, AbstractFilter<Item> filter) {
        this(entity, speed, DEF_RADIUS, filter);
    }

    public EntityAIGather(EntityNpcBase entity, float speed, int radius, AbstractFilter<Item> filter) {
        if (filter == null) { filter = SimpleFilter.allowAllFilter(); }
        this.entity = entity;
        this.speed = speed;
        this.radius = radius;
        this.filter = filter;
        entity.setHomePosAndDistance(entity.getHomePosition(), radius);
        setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!entity.hasInventory() || entity.getHomePosition() == BlockPos.ORIGIN || isInventoryFull()) {
            return false;
        }
        nearbyItems = getEntities();
        return !nearbyItems.isEmpty();
    }

    private List<EntityItem> getEntities() {
        AxisAlignedBB pickupRange = new AxisAlignedBB(entity.getHomePosition()).expand(radius, radius, radius);
         return entity.worldObj.getEntitiesWithinAABB(EntityItem.class, pickupRange,
                 (entity) -> filter.test(entity.getEntityItem().getItem()))
                .stream().sorted((itemEntity, otherItemEntity) -> (int) (itemEntity.getDistanceToEntity(entity) - otherItemEntity.getDistanceToEntity(entity)))
                .collect(Collectors.toList());
    }

    @Override
    public void startExecuting() {
    }

    @Override
    public boolean continueExecuting() {
        return !nearbyItems.isEmpty() && InventoryUtil.hasRoomForItems(entity.getInventoryHandler(), currentTargetedItem.getEntityItem(), false);
    }


    @Override
    public void updateTask() {
        if (!canPickUpItem(currentTargetedItem)) {
            nearbyItems.remove(currentTargetedItem);
            if (nearbyItems.isEmpty()) {
                return;
            }
            currentTargetedItem = nearbyItems.get(0);
        }
        if (!currentTargetedItem.cannotPickup()) {
            if (entity.getNavigator().noPath()) {
                entity.getNavigator().tryMoveToXYZ(currentTargetedItem.posX, currentTargetedItem.posY, currentTargetedItem.posZ, speed);
            }
            entity.getLookHelper().setLookPositionWithEntity(currentTargetedItem, 30.0F, 30.0F);
        }
        if (!currentTargetedItem.cannotPickup() && entity.getDistanceToEntity(currentTargetedItem) <= PICKUP_RANGE) {
            onItemPickup(currentTargetedItem);
            entity.getLookHelper().setLookPosition(entity.getLookHelper().getLookPosX(), entity.getLookHelper().getLookPosY(), entity.getLookHelper().getLookPosZ(), 0.0F, 0.0F);
        }
    }

    @Override
    public void resetTask() {
        currentTargetedItem = null;
        nearbyItems.clear();
    }

    // TODO: Only checks for empty slots
    private boolean isInventoryFull() {
       return InventoryUtil.getFreeInventorySlots(entity) == 0;
    }

    private void onItemPickup(EntityItem item) {
        if (entity.worldObj.isRemote) {
            return;
        }
        ItemStack itemstack = item.getEntityItem();
        int i = itemstack.stackSize;
        if (canPickUpItem(item)) {
            if (!item.isSilent()) {
                item.worldObj.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((entity.getRNG().nextFloat() - entity.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }
            ItemStack itemStack = item.getEntityItem();
            ItemStack newStack = InventoryUtil.insertItemStacked(entity.getInventoryHandler(), itemStack, false);
            item.setEntityItemStack(newStack);
            entity.onItemPickup(item, i);
            if (newStack == null || newStack.stackSize <= 0) {
                item.setDead();
            }
        }
    }

    private boolean canPickUpItem(EntityItem item) {
        return item != null && !item.isDead && (item.getOwner() == null || item.getOwner().equals(entity.getName()));
    }
}
