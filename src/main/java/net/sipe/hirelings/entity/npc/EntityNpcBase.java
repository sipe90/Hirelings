package net.sipe.hirelings.entity.npc;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class EntityNpcBase extends EntityCreature {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("hirelings:textures/entity/npc/default.png");

    public EntityNpcBase(World worldIn) {
        super(worldIn);
        enablePersistence();
        setAlwaysRenderNameTag(true);
        setupAiTasks();
    }

    public ResourceLocation getTexture() {
        return DEFAULT_TEXTURE;
    }

    private void setupAiTasks() {
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();

        // Self-preservation
        tasks.addTask(0, new EntityAISwimming(this));
        tasks.addTask(1, new EntityAIAvoidEntity<>(this, EntityMob.class, 8.0F, 0.6D, 0.6D));

        // Tasks

        // Idle
        tasks.addTask(8, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        tasks.addTask(9, new EntityAIWatchClosest2(this, EntityLiving.class, 6.0F, 0.02F));
        tasks.addTask(10, new EntityAILookIdle(this));
    }
}
