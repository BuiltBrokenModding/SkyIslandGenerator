package com.builtbroken.skyislandgenerator.handler;

import com.builtbroken.skyislandgenerator.SkyIslandGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dark on 8/12/2015.
 */
public class IslandData
{
    public final ChunkCoord location;
    public final String owner;
    public final UUID ownerID;

    public IslandData(String owner, ChunkCoord location)
    {
        this.owner = owner;
        this.ownerID = null;
        this.location = location;
    }

    public IslandData(EntityPlayer player, ChunkCoord location)
    {
        this.owner = player.getCommandSenderName();
        this.ownerID = player.getGameProfile().getId();
        this.location = location;
    }

    public double distance(Entity entity)
    {
        return location.distance(entity);
    }

    public boolean isWithInIslandArea(Entity entity)
    {
        return location.isWithInIslandArea(entity);
    }


    private List<ChunkCoord> pathedChunks;
    private final static ForgeDirection[] dirs = new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST};

    /**
     * Simple pathfinder that tries to locate all chunks used and reset them to default state
     *
     * @param world - world to do actions in
     */
    public void clearArea(World world)
    {
        if (world.getChunkProvider() instanceof ChunkProviderServer)
        {
            pathedChunks = new ArrayList();
            int maxDistanceToPath = SkyIslandGenerator.DISTANCE_BETWEEN_ISLANDS / 16;
            clearAndPath(world, location, maxDistanceToPath);
            pathedChunks.clear();
            pathedChunks = null;
        }
    }

    private void clearAndPath(World world, ChunkCoord coord, int maxDistance)
    {
        if (!pathedChunks.contains(coord) && location.distanceChunks(coord) < maxDistance && coord.doesChunkExist(world))
        {
            pathedChunks.add(coord);

            //Check for overlap to prevent deleting someone else's land
            List<IslandData> islands = IslandManager.INSTANCE.getIslandsContaining(coord);
            islands.remove(this);
            if (islands.size() > 0)
            {
                return;
            }

            if (!coord.isChunkEmpty(world))
            {
                for (int y = 0; y < 256; y++)
                {
                    for (int x = 0; x < 16; x++)
                    {
                        for (int z = 0; z < 16; z++)
                        {
                            if (world.getBlock(x + (coord.x << 4), y, z + (coord.z << 4)) != Blocks.air)
                                world.setBlockToAir(x + (coord.x << 4), y, z + (coord.z << 4));
                        }
                    }
                }
            }

            for (ForgeDirection direction : dirs)
            {
                clearAndPath(world, coord.add(direction), maxDistance);
            }
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof IslandData)
        {
            return ((IslandData) obj).location == location;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return location.hashCode();
    }

    @Override
    public String toString()
    {
        return "IslandData[" + owner + "," + location + "]";
    }
}
