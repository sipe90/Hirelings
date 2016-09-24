package net.sipe.hirelings.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.sipe.hirelings.block.HirelingsBlocks;

public class HirelingsRecipes {

    private HirelingsRecipes() {}

    public static void init() {
        GameRegistry.addShapedRecipe(new ItemStack(HirelingsBlocks.blockRecruitingStation), "LLL", "WWW", "WWW", 'L', Items.LEATHER, 'W', Blocks.PLANKS);
    }
}
