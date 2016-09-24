package net.sipe.hirelings.entity.npc;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class EntityNpcBase extends EntityLiving {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("hirelings:textures/entity/npc/default.png");

    public EntityNpcBase(World worldIn) {
        super(worldIn);
    }

    public ResourceLocation getTexture() {
        return DEFAULT_TEXTURE;
    }

    private void setupAITasks() {}
}
