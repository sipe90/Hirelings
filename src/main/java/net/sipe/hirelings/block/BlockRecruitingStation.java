package net.sipe.hirelings.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockRecruitingStation extends BlockBase {

    public BlockRecruitingStation() {
        super(Material.WOOD, "blockRecruitingStation");
        setSoundType(SoundType.WOOD);
    }
}
