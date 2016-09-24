package net.sipe.hirelings.entity.npc;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;

public class RenderNpcBase extends RenderBiped {
    public RenderNpcBase(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBiped(), 0.6f);
    }
}
