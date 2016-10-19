package net.sipe.hirelings;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.sipe.hirelings.block.HirelingsBlocks;
import net.sipe.hirelings.capability.HirelingsCapabilities;
import net.sipe.hirelings.container.HirelingsGuiHandler;
import net.sipe.hirelings.crafting.HirelingsRecipes;
import net.sipe.hirelings.entity.npc.HirelingsEntities;
import net.sipe.hirelings.event.HirelingsEventHandler;
import net.sipe.hirelings.item.HirelingsItems;
import net.sipe.hirelings.network.NetworkManager;
import net.sipe.hirelings.proxy.CommonProxy;

@Mod(modid = HirelingsMod.MOD_ID, version = HirelingsMod.VERSION)
public class HirelingsMod {

    public static final String MOD_ID = "hirelings";
    public static final String VERSION = "@VERSION@";

    @Mod.Instance(MOD_ID)
    public static HirelingsMod INSTANCE;

    @SidedProxy(serverSide = "net.sipe.hirelings.proxy.CommonProxy", clientSide = "net.sipe.hirelings.proxy.ClientProxy")
    public static CommonProxy PROXY;

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(MOD_ID) {
        @Override
        public Item getTabIconItem() {
            return Items.STONE_PICKAXE;
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        HirelingsItems.init();
        HirelingsBlocks.init();
        HirelingsRecipes.init();
        HirelingsEntities.init();
        HirelingsCapabilities.register();
        HirelingsEventHandler.init();
        NetworkManager.init();
        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new HirelingsGuiHandler());
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {}

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {}
}
