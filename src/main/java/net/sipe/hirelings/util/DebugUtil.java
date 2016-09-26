package net.sipe.hirelings.util;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks;

import java.util.Arrays;
import java.util.Set;

public class DebugUtil {

    public static String getActiveTaskListsAsString(EntityLiving entity) {
        return Arrays.toString(getActiveAITasks(entity)) + " : " + Arrays.toString(getActiveAITargetTasks(entity));

    }

    public static String[] getActiveAITasks(EntityLiving entity) {
       return getActiveTasks(entity.tasks.taskEntries);
    }

    public static String[] getActiveAITargetTasks(EntityLiving entity) {
        return getActiveTasks(entity.targetTasks.taskEntries);
    }

    private static String[] getActiveTasks(Set<EntityAITasks.EntityAITaskEntry> entrySet) {
        return entrySet.stream().filter(entry -> entry.using).map(entry -> entry.action.getClass().getSimpleName()).toArray(String[]::new);
    }
}
