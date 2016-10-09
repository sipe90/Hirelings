package net.sipe.hirelings.util;

import net.minecraft.util.text.TextComponentString;

public class HirelingsTextComponentString extends TextComponentString {

    public HirelingsTextComponentString(String msg) {
        super("§e[Hirelings]§r " + msg);
    }
}
