package com.builtbroken.skyislandgenerator.handler;

import com.builtbroken.skyislandgenerator.SkyIslandGenerator;
import com.builtbroken.skyislandgenerator.generator.IslandGenerator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.World;

import java.util.ArrayList;
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

    private int gridIterationX = 0;
    private int gridIterationZ = 0;

    private IslandManager() {}

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
            if (Math.abs(loc.x - location.x) <= SkyIslandGenerator.DISTANCE_BETWEEN_ISLANDS || Math.abs(loc.z - location.z) <= SkyIslandGenerator.DISTANCE_BETWEEN_ISLANDS)
            {
                return true;
            }
        }
        return false;
    }

    public void newIsland(World worldObj, int x, int z, String chunkFlat, boolean random)
    {
        IslandLocation location = new IslandLocation(x, z);
        if (islandTypeMap.containsKey(chunkFlat))
        {
            IslandGenerator gen = islandTypeMap.get(chunkFlat);
            if (random)
                gen.generateNoRandom(worldObj, x, z);
            else
                gen.generate(worldObj, x, z);
        }
    }
}
