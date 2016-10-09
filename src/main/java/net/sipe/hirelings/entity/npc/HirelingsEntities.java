package net.sipe.hirelings.entity.npc;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.sipe.hirelings.HirelingsMod;

public class HirelingsEntities {

    private HirelingsEntities() {}

    public static void init() {

        EntityRegistry.registerModEntity(EntityWorker.class, "entityWorker", 10, HirelingsMod.INSTANCE, 80, 1, true, 0x79553A, 0x6FAE44);
        HirelingsMod.PROXY.registerModelRendering();
    }

}
