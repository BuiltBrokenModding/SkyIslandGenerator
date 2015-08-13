package com.builtbroken.skyislandgenerator.handler;

import com.builtbroken.skyislandgenerator.SkyIslandGenerator;
import com.builtbroken.skyislandgenerator.generator.IslandGenerator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dark on 8/12/2015.
 */
public class IslandManager
{
    public HashMap<IslandLocation, IslandData> islandMap = new HashMap();
    public HashMap<String, IslandGenerator> islandTypeMap = new HashMap();
    public List<IslandLocation> nextLocations = new ArrayList();

    public static final IslandManager INSTANCE = new IslandManager();
    private static final List<ForgeDirection> dirs = new ArrayList();

    static
    {
        dirs.add(ForgeDirection.EAST);
        dirs.add(ForgeDirection.WEST);
        dirs.add(ForgeDirection.NORTH);
        dirs.add(ForgeDirection.SOUTH);
    }

    private int ring = 0;
    private int ring_index = 0;

    private IslandManager()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void registerGenerator(String name, IslandGenerator gen)
    {
        if (!islandTypeMap.containsKey(name))
        {
            islandTypeMap.put(name, gen);
            SkyIslandGenerator.LOGGER.info("Registered new island generator " + name);
        }
        else
        {
            SkyIslandGenerator.LOGGER.error("A mod attempted to register a generator that is already registered. Name = " + name + " Clazz = " + gen);
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && event.world.provider.dimensionId == 0 && nextLocations.size() < 5)
        {
            if (islandMap.isEmpty())
            {
                islandMap.put(new IslandLocation(event.world.getSpawnPoint().posX >> 4, event.world.getSpawnPoint().posZ >> 4), null);
            }
            for (IslandLocation location : islandMap.keySet())
            {
                int originX = (location.x << 4) + 8;
                int originZ = (location.z << 4) + 8;

                Collections.shuffle(dirs);
                for (ForgeDirection dir : dirs)
                {
                    int nextX = (originX + dir.offsetX * SkyIslandGenerator.DISTANCE_BETWEEN_ISLANDS) >> 4;
                    int nextZ = (originZ + dir.offsetZ * SkyIslandGenerator.DISTANCE_BETWEEN_ISLANDS) >> 4;
                    IslandLocation newLocation = new IslandLocation(nextX, nextZ);
                    if (!isLocationAlreadyInUse(newLocation))
                    {
                        nextLocations.add(newLocation);
                    }
                }
            }
        }
    }


    public IslandLocation getNewIslandLocation()
    {
        if (nextLocations.size() != 0)
        {
            return nextLocations.get(0);
        }
        return null;
    }

    public boolean isLocationAlreadyInUse(IslandLocation location)
    {
        if (islandMap.containsKey(location)) return true;
        for (IslandLocation loc : islandMap.keySet())
        {
            if ((Math.abs(loc.x - location.x) << 4) <= SkyIslandGenerator.DISTANCE_BETWEEN_ISLANDS || (Math.abs(loc.z - location.z) << 4) <= SkyIslandGenerator.DISTANCE_BETWEEN_ISLANDS)
            {
                return true;
            }
        }
        return false;
    }

    public void newIsland(EntityPlayer player, int chunkX, int chunkZ, String chunkFlat, boolean random)
    {
        IslandLocation location = newIsland(player.worldObj, chunkX, chunkZ, chunkFlat, random);
        if (location != null)
        {
            int x = (chunkX << 4) + 8;
            int z = (chunkZ << 4) + 8;
            int y = 0;
            islandMap.put(location, new IslandData(player));
            MovingObjectPosition mop = player.worldObj.rayTraceBlocks(Vec3.createVectorHelper(x + 0.5, 255, z + 0.5), Vec3.createVectorHelper(x + 0.5, 0, z + 0.5));
            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                player.setSpawnChunk(new ChunkCoordinates(x, mop.blockY + 1, z), true);
                player.setPositionAndUpdate(x, y, z);
            }
        }
    }

    public IslandLocation newIsland(World worldObj, int x, int z, String chunkFlat, boolean random)
    {
        IslandLocation location = new IslandLocation(x, z);
        if (islandTypeMap.containsKey(chunkFlat))
        {
            IslandGenerator gen = islandTypeMap.get(chunkFlat);
            if (random)
                gen.generateNoRandom(worldObj, x, z);
            else
                gen.generate(worldObj, x, z);

            islandMap.put(location, null);
            return location;
        }
        return null;
    }
}
