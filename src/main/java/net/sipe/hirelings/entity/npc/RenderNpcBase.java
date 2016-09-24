package net.sipe.hirelings.entity.npc;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderNpcBase<T extends EntityNpcBase> extends RenderBiped<T> {

    public RenderNpcBase(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBiped(), 0.6f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityNpcBase entity) {
        return entity.getTexture();
    }
}
