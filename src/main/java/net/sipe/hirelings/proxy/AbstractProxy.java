package net.sipe.hirelings.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public abstract class AbstractProxy {

    public void registerItemRenderer(Item item, int meta, String id) {}

    public <T extends Entity> void registerModelRenderer(Class<T> entityClass, IRenderFactory<T> renderFactory) {}
}