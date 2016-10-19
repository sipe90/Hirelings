package net.sipe.hirelings.entity.npc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import net.sipe.hirelings.HirelingsMod;
import net.sipe.hirelings.container.HirelingsGuiHandler;
import net.sipe.hirelings.entity.npc.job.JobBase;
import net.sipe.hirelings.entity.npc.job.JobCollector;
import net.sipe.hirelings.entity.player.PlayerProperties;
import net.sipe.hirelings.util.HirelingsTextComponentString;

import static net.sipe.hirelings.capability.HirelingsCapabilities.PLAYER_PROPERTIES_CAPABILITY;

public class EntityWorker extends EntityNpcBase {

    private JobBase job;

    public EntityWorker(World worldIn) {
        super(worldIn);
        npcInventoryHandler = new ItemStackHandler(15);
        hasGui = true;
        job = new JobCollector();
        job.initTasks(this);
    }


    public JobBase getJob() {
        return job;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (!player.worldObj.isRemote) {
            if (stack != null && stack.getItem() == Items.STICK && player.hasCapability(PLAYER_PROPERTIES_CAPABILITY, null)) {
                PlayerProperties properties = player.getCapability(PLAYER_PROPERTIES_CAPABILITY, null);
                properties.setLinkEntity(getEntityId());
                player.addChatMessage(new HirelingsTextComponentString("Initiated link..."));
                return true;
            }
        }
        return super.processInteract(player, hand, stack);
    }

    protected void openGui(EntityPlayer player) {
        player.openGui(HirelingsMod.INSTANCE, HirelingsGuiHandler.HIRELINGS_NPC_GUI, worldObj, this.getEntityId(), 0, 0);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        NBTTagCompound jobCompound = new NBTTagCompound();
        job.writeEntityToNBT(jobCompound);
        compound.setTag("npcJob", jobCompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        NBTTagCompound jobCompound = (NBTTagCompound) compound.getTag("npcJob");
        job.readEntityFromNBT(jobCompound);
    }

}
