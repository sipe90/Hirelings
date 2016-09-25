package net.sipe.hirelings.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.sipe.hirelings.HirelingsMod;
import net.sipe.hirelings.entity.npc.EntityNpcBase;
import net.sipe.hirelings.util.ReflectionUtil;

public class HirelingsEventHandler {

    private static final Class[] EXCLUDE_MOBS = {
            EntityCreeper.class,
            EntityEnderman.class,
            EntitySpider.class
    };

    private HirelingsEventHandler() {}

    public static void init() {
        HirelingsMod.PROXY.registerEventHandler(new HirelingsEventHandler());
    }

    /**
     * Makes certain hostile vanilla mobs to target npcs (looks like priority 2 falls between player and iron golems for most (all?) mobs)
     **/
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityMob) || ReflectionUtil.isAssignableTo(entity.getClass(), EXCLUDE_MOBS)) {
            return;
        }
        ((EntityCreature) entity).targetTasks.addTask(2, new EntityAINearestAttackableTarget<>((EntityCreature) entity, EntityNpcBase.class, true));
    }
}
