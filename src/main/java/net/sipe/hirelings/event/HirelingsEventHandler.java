package net.sipe.hirelings.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.sipe.hirelings.HirelingsMod;
import net.sipe.hirelings.capability.provider.PlayerPropertiesProvider;
import net.sipe.hirelings.entity.npc.EntityNpcBase;
import net.sipe.hirelings.entity.npc.EntityWorker;
import net.sipe.hirelings.entity.npc.ai.EntityAIEmptyInventory;
import net.sipe.hirelings.entity.player.PlayerProperties;
import net.sipe.hirelings.network.NetworkManager;
import net.sipe.hirelings.network.message.PlayerPropertiesMessage;
import net.sipe.hirelings.util.HirelingsTextComponentString;

import static net.sipe.hirelings.capability.HirelingsCapabilities.PLAYER_PROPERTIES_CAPABILITY;

public class HirelingsEventHandler {

    private HirelingsEventHandler() {}

    public static void init() {
        HirelingsMod.PROXY.registerEventHandler(new HirelingsEventHandler());
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        addAttackTargetTask(entity);
    }


    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent.Entity event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayer && !entity.hasCapability(PLAYER_PROPERTIES_CAPABILITY, null)) {
            event.addCapability(new ResourceLocation(HirelingsMod.MOD_ID, "playerProperties"), new PlayerPropertiesProvider());
        }
    }


    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.worldObj.isRemote || !event.player.hasCapability(PLAYER_PROPERTIES_CAPABILITY, null)) {
            return;
        }
        PlayerProperties properties = event.player.getCapability(PLAYER_PROPERTIES_CAPABILITY, null);
        if (properties.isDirty()) {
            NetworkManager.NETWORK.sendTo(new PlayerPropertiesMessage(properties), (EntityPlayerMP) event.player);
            properties.setClean();
        }
    }

    /**
     * Makes zombies to target npcs (looks like priority 2 falls between player and iron golems for most (all?) mobs)
     **/
    private void addAttackTargetTask(Entity entity) {
        if (!(entity instanceof EntityZombie)) {
            return;
        }
        ((EntityCreature) entity).targetTasks.addTask(2, new EntityAINearestAttackableTarget<>((EntityCreature) entity, EntityNpcBase.class, true));
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack itemStack = event.getItemStack();
        BlockPos pos = event.getPos();
        TileEntity target = player.worldObj.getTileEntity(pos);

        if (player.worldObj.isRemote || itemStack == null || itemStack.getItem() != Items.STICK || target == null || !player.isSneaking()
                || !player.hasCapability(PLAYER_PROPERTIES_CAPABILITY, null)
                || !target.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            return;
        }

        PlayerProperties properties = player.getCapability(PLAYER_PROPERTIES_CAPABILITY, null);
        if (properties.isLinking()) {
            Entity linkEntity = player.worldObj.getEntityByID(properties.getLinkEntityId());
            if (!(linkEntity instanceof EntityWorker)) {
                // TODO: Log debug/error?
                return;
            }
            EntityAIEmptyInventory task = (EntityAIEmptyInventory) ((EntityWorker) linkEntity).getJob().getTask(EntityAIEmptyInventory.class);
            task.setInventory(target);
            properties.resetLinking();
            player.addChatMessage(new HirelingsTextComponentString("Inventory linked!"));
        }
    }
}
