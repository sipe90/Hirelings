package net.sipe.hirelings.entity.npc.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.sipe.hirelings.entity.npc.EntityNpcBase;
import net.sipe.hirelings.util.InventoryUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraftforge.items.ItemHandlerHelper.insertItemStacked;

public class EntityNpcAIGather extends EntityAIBase {

    private static final double PICKUP_RANGE = 2.0D;

    private static final double DEF_HORIZONTAL_RADIUS = 10.0D;
    private static final double DEF_VERTICAL_RADIUS = 5.0D;

    private final EntityNpcBase entity;

    private boolean canPickupLoot;

    private double radiusHorizontal;
    private double radiusVertical;

    private Set<Item> itemsToPickup;

    private List<EntityItem> nearbyItems = new ArrayList<>();
    private EntityItem currentTargetedItem;

    public EntityNpcAIGather(EntityNpcBase entity) {
        this(entity, new HashSet<>());
    }

    public EntityNpcAIGather(EntityNpcBase entity, Set<Item> itemsToPickup) {
        this(entity, DEF_HORIZONTAL_RADIUS, DEF_VERTICAL_RADIUS, itemsToPickup);
    }

    public EntityNpcAIGather(EntityNpcBase entity, double radiusHorizontal, double radiusVertical, Set<Item> itemsToPickup) {
        this.entity = entity;
        this.radiusHorizontal = radiusHorizontal;
        this.radiusVertical = radiusVertical;
        this.itemsToPickup = itemsToPickup;
        setMutexBits(1);
    }

    public void addItem(Item item) {
        itemsToPickup.add(item);
    }

    public void removeItem(Item item) {
        itemsToPickup.remove(item);
    }

    public void clearItems() {
        itemsToPickup.clear();
    }

    @Override
    public boolean shouldExecute() {
        if (isInventoryFull()) {
            return false;
        }
        nearbyItems = entity.worldObj.getEntitiesWithinAABB(EntityItem.class,
                entity.getEntityBoundingBox().expand(radiusHorizontal, radiusVertical, radiusHorizontal),
                (item) -> itemsToPickup.contains(item.getEntityItem().getItem()))
                .stream().sorted((itemEntity, otherItemEntity) -> (int)(itemEntity.getDistanceToEntity(entity) - otherItemEntity.getDistanceToEntity(entity)))
                .collect(Collectors.toList());
        return !nearbyItems.isEmpty();
    }

    @Override
    public void startExecuting() {
        canPickupLoot = entity.canPickUpLoot();
        entity.setCanPickUpLoot(false);
    }

    @Override
    public boolean continueExecuting() {
        boolean continueExecuting = !nearbyItems.isEmpty() && isRoomForItem(entity.getInventoryHandler(), currentTargetedItem.getEntityItem());
        if (!continueExecuting) {
           onStopExecuting();
        }
        return continueExecuting;
    }

    private void onStopExecuting() {
        entity.setCanPickUpLoot(canPickupLoot);
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
                entity.getNavigator().tryMoveToXYZ(currentTargetedItem.posX, currentTargetedItem.posY, currentTargetedItem.posZ, 1.0D);
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

    private boolean isRoomForItem(IItemHandlerModifiable handler, ItemStack stack) {
        if (stack == null) {
            return true;
        }
        ItemStack result = insertItemStacked(handler, stack, true);
        return result == null || result.stackSize == 0;
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
            ItemStack newStack = ItemHandlerHelper.insertItemStacked(entity.getInventoryHandler(), itemStack, false);
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
