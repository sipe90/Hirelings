package net.sipe.hirelings.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.sipe.hirelings.HirelingsMod;
import net.sipe.hirelings.item.ItemModelProvider;

public class BlockBase extends Block implements ItemModelProvider {

    protected String name;

    public BlockBase(Material material, String name) {
        super(material);
        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(HirelingsMod.CREATIVE_TAB);
    }

    @Override
    public void registerItemModel(Item itemBlock) {
        HirelingsMod.PROXY.registerItemRenderer(itemBlock, 0, name);
    }

    @Override
    public BlockBase setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }
}
