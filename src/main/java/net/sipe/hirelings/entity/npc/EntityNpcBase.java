package net.sipe.hirelings.entity.npc;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.sipe.hirelings.HirelingsMod;

public abstract class EntityNpcBase extends EntityLiving implements ModelRendererProvider {

    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("hirelings:textures/entity/npc/default.png");

    public EntityNpcBase(World worldIn) {
        super(worldIn);
    }

    @Override
    public void registerModelRenderer() {
        HirelingsMod.PROXY.registerModelRenderer(EntityNpcBase.class, RenderNpcBase::new);
    }

    private void setupAITasks() {}
}
