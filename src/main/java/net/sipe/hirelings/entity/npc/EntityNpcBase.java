package net.sipe.hirelings.entity.npc;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.sipe.hirelings.HirelingsMod;
import net.sipe.hirelings.util.DebugUtil;
import net.sipe.hirelings.util.NameGen;
import net.sipe.hirelings.util.inventory.InventoryUtil;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public abstract class EntityNpcBase extends EntityCreature {

    private static final DataParameter<String> NAME = EntityDataManager.createKey(EntityNpcBase.class, DataSerializers.STRING);
    private static final DataParameter<Byte> LEVEL = EntityDataManager.createKey(EntityNpcBase.class, DataSerializers.BYTE);
    private static final DataParameter<Float> EXPERIENCE = EntityDataManager.createKey(EntityNpcBase.class, DataSerializers.FLOAT);

    protected IItemHandlerModifiable npcInventoryHandler;

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(HirelingsMod.MOD_ID, "textures/entity/npc/default.png");

    protected boolean hasGui = false;

    // A hack to tell the renderer not to render name tag when rendering the model in the inventory gui screen.
    private boolean inventoryRendering = false;

    public EntityNpcBase(World worldIn) {
        super(worldIn);
        enablePersistence();
        setAlwaysRenderNameTag(true);
        setLeftHanded(Math.random() >= 0.5D);
        setupAITasks();
    }

    @Override
    public void entityInit() {
        super.entityInit();
        dataManager.register(NAME, NameGen.randomName());
        dataManager.register(LEVEL, (byte)0);
        dataManager.register(EXPERIENCE,0.0F);
    }

    public ResourceLocation getTexture() {
        return DEFAULT_TEXTURE;
    }

    protected void setupAITasks() {
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();

        // Self-preservation
        tasks.addTask(0, new EntityAISwimming(this));
        // TODO: Make a more clever implementation
        tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityMob.class, 8.0F, 0.6D, 0.8D));
        // Tasks

        // Idle
        tasks.addTask(8, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        tasks.addTask(9, new EntityAIWatchClosest2(this, EntityLiving.class, 6.0F, 0.02F));
        tasks.addTask(10, new EntityAILookIdle(this));
    }

    public void setHomePos(BlockPos pos) {
        this.setHomePosAndDistance(pos, (int)this.getMaximumHomeDistance());
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }

    @Override
    public void updateAITasks() {
        super.updateAITasks();
        setCustomNameTag(getNpcName() + " [" + InventoryUtil.getFreeInventorySlots(this) + "/" + InventoryUtil.getTotalInventorySlots(this) + "] "
                + "§4" + DebugUtil.getActiveAITasksAsString(this)
                + "§r : §6" + DebugUtil.getActiveAITargetTasksAsString(this) + "§r");
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (!worldObj.isRemote) {
            if (hasGui) {
                openGui(player);
            }
        }
        return true;
    }

    public void setInventoryRendering(boolean inventoryRendering) {
        this.inventoryRendering = inventoryRendering;
    }

    public boolean isInventoryRendering() {
        return inventoryRendering;
    }

    protected void openGui(EntityPlayer player) {
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
    }

    @Override
    public void onItemPickup(Entity entity, int amount) {
        super.onItemPickup(entity, amount);
    }

    public boolean hasInventory() {
        return npcInventoryHandler != null;
    }

    public IItemHandlerModifiable getInventoryHandler() {
        return npcInventoryHandler;
    }

    public void setNpcName(String name) {
        dataManager.set(NAME, name);
    }

    public String getNpcName() {
        return dataManager.get(NAME);
    }

    public void setLevel(byte level) {
        dataManager.set(LEVEL, level);
    }

    public byte getLevel() {
        return dataManager.get(LEVEL);
    }

    public void setExperience(float experience) {
        dataManager.set(EXPERIENCE, experience);
    }

    public float getExperience() {
        return dataManager.get(EXPERIENCE);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
       super.writeEntityToNBT(compound);

        NBTBase inventory = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(npcInventoryHandler, null);

        compound.setString("npcName", getNpcName());
        compound.setByte("npcLevel", getLevel());
        compound.setFloat("npcExperience", getExperience());
        compound.setTag("npcInventory", inventory);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        setNpcName(compound.getString("npcName"));
        setLevel(compound.getByte("npcLevel"));
        setExperience(compound.getFloat("npcExperience"));
        NBTBase inventory = compound.getTag("npcInventory");
        ITEM_HANDLER_CAPABILITY.readNBT(npcInventoryHandler, null, inventory);
    }

    @Override
    public boolean isAIDisabled() { return false;}

}
