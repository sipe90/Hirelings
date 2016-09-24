package net.sipe.hirelings.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.sipe.hirelings.item.ItemModelProvider;

public class HirelingsBlocks {

    public static BlockRecruitingStation blockRecruitingStation;

    private HirelingsBlocks() {}

    public static void init() {
        blockRecruitingStation = register(new BlockRecruitingStation());
    }

    private static <T extends Block> T register(T block) {
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        return register(block, itemBlock);
    }

    private static <T extends Block> T register (T block, ItemBlock itemBlock) {
        GameRegistry.register(block);
        if(itemBlock != null) {
            GameRegistry.register(itemBlock);
        }
        if(block instanceof ItemModelProvider) {
            ((ItemModelProvider)block).registerItemModel(itemBlock);
        }
        return block;
    }



}
