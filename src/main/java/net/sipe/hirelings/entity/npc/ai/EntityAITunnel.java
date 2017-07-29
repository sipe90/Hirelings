package net.sipe.hirelings.entity.npc.ai;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.sipe.hirelings.entity.npc.EntityNpcBase;
import net.sipe.hirelings.util.inventory.InventoryUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class EntityAITunnel extends EntityAIBase {

    private final EntityNpcBase entity;
    private final BlockPos workSite;
    private final EnumFacing workDirection;

    private final int tunnelWidth, tunnelHeight, tunnelLength;

    private boolean finished = false;

    private StructureBoundingBox tunnelDimensions;
    private int currentDepth;
    private StructureBoundingBox currentSection;

    public EntityAITunnel(EntityNpcBase entity, BlockPos workSite, EnumFacing workDirection) {
        this.entity = entity;
        this.workSite = workSite;
        this.workDirection = workDirection;
        this.tunnelHeight = 2;
        this.tunnelWidth = 1;
        this.tunnelLength = 30;
        this.currentDepth = 0;
    }

    @Override
    public boolean shouldExecute() {
        return canWork();
    }

    private boolean canWork() {
        if (workSite == null || finished ||  InventoryUtil.getFreeInventorySlots(entity) == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void startExecuting() {
        tunnelDimensions = calculateTunnelDimensions();
    }

    private StructureBoundingBox calculateTunnelDimensions() {
        int startX, startY, startZ, endX, endY, endZ;

        startY = workSite.getY();
        endY = startY + tunnelHeight;

        switch (workDirection) {
            case WEST: {
                startX = workSite.getX() - 1;
                startZ = workSite.getZ() - 1;
                endX = startX - tunnelLength;
                endZ = startZ - tunnelWidth;
                return new StructureBoundingBox(startX, startY, startZ, endX, endY, endZ);
            }
            case NORTH: {
                startX = workSite.getX() + 1;
                startZ = workSite.getZ() - 1;
                endX = startX + tunnelWidth;
                endZ = startZ - tunnelLength;
                return new StructureBoundingBox(startX, startY, startZ, endX, endY, endZ);
            }
            case EAST: {
                startX = workSite.getX() + 1;
                startZ = workSite.getZ() + 1;
                endX = startX + tunnelLength;
                endZ = startZ + tunnelWidth;
                return new StructureBoundingBox(startX, startY, startZ, endX, endY, endZ);
            }
            case SOUTH: {
                startX = workSite.getX() - 1;
                startZ = workSite.getZ() + 1;
                endX = startX - tunnelWidth;
                endZ = startZ + tunnelLength;
                return new StructureBoundingBox(startX, startY, startZ, endX, endY, endZ);
            }
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Work from top left one row at a time.
     */
    private List<BlockPos> buildWorkList(StructureBoundingBox section) {
        List<BlockPos> blockQueue = new ArrayList<>();

        switch (workDirection) {
            case WEST: {
                for (int y = section.maxY; y >= section.minY; y--) {
                    for (int z = section.minZ; z >= section.maxZ; z--) {
                        blockQueue.add(new BlockPos(section.minX, y, z));
                    }
                }
                return blockQueue;
            }
            case NORTH: {
                for (int y = section.maxY; y >= section.minY; y--) {
                    for (int x = section.minX; x <= section.maxX; x++) {
                        blockQueue.add(new BlockPos(x, y, section.minZ));
                    }
                }
                return blockQueue;
            }
            case EAST: {
                for (int y = section.maxY; y >= section.minY; y--) {
                    for (int z = section.minZ; z <= section.maxZ; z++) {
                        blockQueue.add(new BlockPos(section.minX, y, z));
                    }
                }
                return blockQueue;
            }
            case SOUTH: {
                for (int y = section.maxY; y >= section.minY; y--) {
                    for (int x = section.minX; x >= section.maxZ; x--) {
                        blockQueue.add(new BlockPos(x, y, section.minZ));
                    }
                }
                return blockQueue;
            }
            default:
                throw new IllegalArgumentException();
        }
    }

    private StructureBoundingBox getSection(int depth) {
        StructureBoundingBox section = new StructureBoundingBox(tunnelDimensions);

        switch (workDirection) {
            case WEST: {
                section.maxX = section.minX - 1;
                section.offset(-depth, 0, 0);
                return section;
            }
            case NORTH: {
                section.maxZ = section.minZ - 1;
                section.offset(0, 0, -depth);
                return section;
            }
            case EAST: {
                section.maxX = section.minX + 1;
                section.offset(depth, 0, 0);
                return section;
            }
            case SOUTH: {
                section.maxZ = section.minX + 1;
                section.offset(0, 0, depth);
                return section;
            }
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean continueExecuting() {
        return canWork();
    }


    @Override
    public void updateTask() {
        if (currentSection == null) {
            currentSection = getSection(currentDepth);
        }
        BlockPos workingPosition = getWorkingPosition(currentSection);
        if (entity.getPosition().distanceSq(workingPosition) > 1.0d) {
            if (entity.getNavigator().noPath()) {
                entity.getNavigator().tryMoveToXYZ(workingPosition.getX(), workingPosition.getY(), workingPosition.getZ(), 1.0d);
            }
            return;
        }

        List<BlockPos> blocks = buildWorkList(currentSection);
        for (BlockPos block : blocks) {
            IBlockState blockState = entity.worldObj.getBlockState(block);
            if (entity.worldObj.isAirBlock(block)) {
                continue;
            }
            if (entity.canHarvestBlock(block)) {
                blockState.getBlock().dropBlockAsItem(entity.worldObj, block, blockState, 0);
            }
        }

        currentDepth++;
        if (currentDepth < tunnelLength) {
            currentSection = getSection(currentDepth);
        } else {
            finished = true;
        }
    }

    private BlockPos getWorkingPosition(StructureBoundingBox section) {
        double posX = section.minX + (section.maxX - section.minX) / 2.0d;
        double posY = section.minY;
        double posZ = section.minZ + (section.maxZ - section.minZ) / 2.0d;

        if (workDirection == EnumFacing.WEST) {
            posX += 1.0d;
        } else if (workDirection == EnumFacing.NORTH) {
            posZ -= 1.0d;
        } else if (workDirection == EnumFacing.EAST) {
            posX -= 1.0d;
        } else if (workDirection == EnumFacing.SOUTH) {
            posZ += 1.0d;
        } else {
            throw new IllegalArgumentException();
        }

        return new BlockPos(posX, posY, posZ);
    }

    @Override
    public void resetTask() {
        currentDepth = 0;
        currentSection = null;
        finished = false;
    }
}
