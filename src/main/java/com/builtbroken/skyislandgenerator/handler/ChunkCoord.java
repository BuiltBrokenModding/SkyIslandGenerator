package com.builtbroken.skyislandgenerator.handler;

import com.builtbroken.skyislandgenerator.SkyIslandGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Dark on 8/12/2015.
 */
public class ChunkCoord
{
    /** Chunk location x */
    public final int x;
    /** Chunk location z */
    public final int z;

    public ChunkCoord(int x, int z)
    {
        this.x = x;
        this.z = z;
    }

    public boolean isWithInIslandArea(Entity entity)
    {
        return distance(entity) <= SkyIslandGenerator.DISTANCE_BETWEEN_ISLANDS;
    }

    public double distance(Entity entity)
    {
        double deltaX = entity.posX - (x << 4);
        double deltaZ = entity.posZ - (z << 4);
        return (double) MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
    }

    public int distanceChunks(ChunkCoord coord)
    {
        return distanceChunks(coord.x, coord.z);
    }

    public int distanceChunks(int chunkX, int chunkZ)
    {
        double deltaX = chunkX - x;
        double deltaZ = chunkZ - z;
        return (int) MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
    }

    public ChunkCoord add(ForgeDirection direction)
    {
        return new ChunkCoord(x + direction.offsetX, z + direction.offsetZ);
    }

    public boolean doesChunkExist(World world)
    {
        return world.checkChunksExist(x << 4, 1, z << 4, x << 4, 1, z << 4);
    }

    public Chunk getChunk(World world)
    {
        return world.getChunkFromChunkCoords(x, z);
    }

    public boolean isChunkEmpty(World world)
    {
        if (doesChunkExist(world))
        {
            Chunk chunk = getChunk(world);
            for (ExtendedBlockStorage eb : chunk.getBlockStorageArray())
            {
                if (eb != null && !eb.isEmpty())
                    return false;
            }
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "ChunkCoord [" + x + "," + z + "]";
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ChunkCoord)
        {
            return ((ChunkCoord) obj).x == x && ((ChunkCoord) obj).z == z;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return x ^ z;
    }

}
