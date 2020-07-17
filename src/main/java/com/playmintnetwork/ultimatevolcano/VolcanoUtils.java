package com.playmintnetwork.ultimatevolcano;

import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public class VolcanoUtils {

    public static void setBlockMaterial(Block block, Material material) {
        setBlockMaterial(block, material, true);
    }

    public static void setBlockMaterial(Block block, Material material, boolean applyPhysics) {
        setLocationMaterial(block.getLocation(), material, applyPhysics, true);
    }

    public static void setBlockMaterial(Block block, Material material, boolean applyPhysics, boolean updateChunk) {
        setLocationMaterial(block.getLocation(), material, applyPhysics, updateChunk);
    }

    public static void setLocationMaterial(Location location, Material material) {
        setLocationMaterial(location, material, true);
    }

    public static void setLocationMaterial(Location location, Material material, boolean applyPhysics) {
        setLocationMaterial(location, material, applyPhysics, true);
    }

    public static void setLocationMaterial(Location location, Material material, boolean applyPhysics, boolean updateChunk) {
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Chunk chunk = world.getChunkAt(x >> 4, z >> 4);

        BlockPosition blockPosition = new BlockPosition(x,y,z);

        net.minecraft.server.v1_16_R1.Block tmpBlock = CraftMagicNumbers.getBlock(material);
        IBlockData iBlockData = tmpBlock.getBlockData();

        chunk.setType(blockPosition, iBlockData, applyPhysics);

        if (updateChunk) updateChunk(location);
    }

    public static void copyBlock(Block block, Block toBlock) {
        copyBlock(block, toBlock, true);
    }

    public static void copyBlock(Block block, Block toBlock, boolean applyPhysics) {
        copyBlock(block, toBlock, applyPhysics, true);
    }

    public static void copyBlock(Block block, Block toBlock, boolean applyPhysics, boolean updateChunk) {
        WorldServer world = ((CraftWorld) block.getWorld()).getHandle();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        int toX = toBlock.getX();
        int toY = toBlock.getY();
        int toZ = toBlock.getZ();

        BlockPosition bp = new BlockPosition(x, y, z);
        IBlockData blockData = world.getType(bp);

        BlockPosition toBP = new BlockPosition(toX, toY, toZ);
        Chunk toChunk = world.getChunkAt(toX >> 4, toZ >> 4);

        toChunk.setType(toBP, blockData, applyPhysics);

        if (updateChunk) {
            updateChunk(block);
            updateChunk(toBlock);
        }
    }

    public static void moveBlock(Block block, Block toBlock) {
        moveBlock(block, toBlock, true);
    }

    public static void moveBlock(Block block, Block toBlock, boolean applyPhysics) {
        moveBlock(block, toBlock, applyPhysics, true);
    }

    public static void moveBlock(Block block, Block toBlock, boolean applyPhysics, boolean updateChunk) {
        copyBlock(block, toBlock, applyPhysics, updateChunk);
        setBlockMaterial(block, Material.AIR, applyPhysics, updateChunk);
    }

    public static void updateChunk(Block block) {
        org.bukkit.Chunk chunk = block.getChunk();
        updateChunk(chunk);
    }

    public static void updateChunk(Location loc) {
        org.bukkit.Chunk chunk = loc.getChunk();
        updateChunk(chunk);
    }

    public static void updateChunk(org.bukkit.Chunk chunk) {

        int diffx, diffz;
        int view = Bukkit.getServer().getViewDistance() << 4;

        Chunk rawChunk = ((CraftChunk) chunk).getHandle();
        World world = rawChunk.world;


        for (EntityPlayer ep : (List<EntityPlayer>) world.getPlayers()) {
            diffx = Math.abs((int) ep.locX() - chunk.getX() << 4);
            diffz = Math.abs((int) ep.locZ() - chunk.getZ() << 4);
            if (diffx <= view && diffz <= view) {
                ep.a(new ChunkCoordIntPair(chunk.getX(), chunk.getZ()));
                ep.playerConnection.sendPacket(new PacketPlayOutMapChunk(rawChunk, 20, true));

            }
        }
    }


}
