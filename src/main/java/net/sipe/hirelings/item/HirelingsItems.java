package net.sipe.hirelings.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class HirelingsItems {

    public static void init() {}

    private static <T extends Item> T register(T item) {
        GameRegistry.register(item);

        if (item instanceof  ItemModelProvider) {
            ((ItemModelProvider) item).registerItemModel(item);
        }

        return item;
    }
}
