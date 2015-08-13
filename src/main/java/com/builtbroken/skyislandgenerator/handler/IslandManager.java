package com.builtbroken.skyislandgenerator.handler;

import com.builtbroken.skyislandgenerator.SkyIslandGenerator;
import com.builtbroken.skyislandgenerator.generator.IslandGenerator;

import java.util.HashMap;

/**
 * Created by Dark on 8/12/2015.
 */
public class IslandManager
{
    public HashMap<IslandLocation, IslandData> islandMap = new HashMap();
    public HashMap<String, IslandGenerator> islandTypeMap = new HashMap();

    public static final IslandManager INSTANCE = new IslandManager();

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

    public IslandLocation getNewIslandLocation()
    {
        return null;
    }
}
