package net.sipe.hirelings.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.sipe.hirelings.HirelingsMod;

public class ItemBase extends Item implements ItemModelProvider {

    protected String name;

    public ItemBase(String name) {
        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(HirelingsMod.CREATIVE_TAB);
    }

    @Override
    public void registerItemModel(Item item) {
        HirelingsMod.PROXY.registerItemRenderer(this, 0, name);
    }

    @Override
    public ItemBase setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }
}
