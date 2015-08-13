package com.builtbroken.skyislandgenerator.handler;

import com.builtbroken.skyislandgenerator.SkyIslandGenerator;
import com.builtbroken.skyislandgenerator.generator.IslandGenerator;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 * Created by Dark on 8/12/2015.
 */
public class IslandManager
{
    public HashMap<IslandLocation, IslandData> islandMap = new HashMap();
    public HashMap<UUID, IslandData> playerToIslandMap = new HashMap();
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

    }

    public void registerGenerator(String name, IslandGenerator gen)
    {
        if (!islandTypeMap.containsKey(name))
        {
            islandTypeMap.put(name, gen);
            SkyIslandGenerator.LOGGER.info("Registered new island generator " + name);
        } else
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
                System.out.println("It: " + location);
                int originX = (location.x << 4) + 8;
                int originZ = (location.z << 4) + 8;

                //Shuffle location so we avoid going in the same direction each iteration
                Collections.shuffle(dirs);

                //Iterate over sides to find locations that could be valid
                for (ForgeDirection dir : dirs)
                {
                    System.out.println("\tD: " + dir);
                    int nextX = (originX + dir.offsetX * SkyIslandGenerator.DISTANCE_BETWEEN_ISLANDS) >> 4;
                    int nextZ = (originZ + dir.offsetZ * SkyIslandGenerator.DISTANCE_BETWEEN_ISLANDS) >> 4;
                    IslandLocation newLocation = new IslandLocation(nextX, nextZ);
                    System.out.println("\tN: " + newLocation);
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
        if (!nextLocations.isEmpty())
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
            if (Math.abs((loc.x << 4) - (location.x << 4)) <= SkyIslandGenerator.DISTANCE_BETWEEN_ISLANDS && Math.abs((loc.z << 4) - (location.z << 4)) <= SkyIslandGenerator.DISTANCE_BETWEEN_ISLANDS)
            {
                return true;
            }
        }
        return false;
    }

    public boolean newIsland(EntityPlayer player, String chunkFlat, boolean random)
    {
        SkyIslandGenerator.LOGGER.info("Attempting to generate new Island[" + chunkFlat + "] for " + player.getCommandSenderName());
        IslandLocation location = getNewIslandLocation();
        if (location != null)
        {
            location = newIsland(player.worldObj, location, chunkFlat, random);
            if (location != null)
            {
                int x = (location.x << 4) + 8;
                int z = (location.z << 4) + 8;
                islandMap.put(location, new IslandData(player));
                playerToIslandMap.put(player.getGameProfile().getId(), islandMap.get(location));
                MovingObjectPosition mop = player.worldObj.rayTraceBlocks(Vec3.createVectorHelper(x + 0.5, 210, z + 0.5), Vec3.createVectorHelper(x + 0.5, 5, z + 0.5));
                if (player.capabilities.isCreativeMode)
                {
                    player.setPositionAndUpdate(x, SkyIslandGenerator.DEFAULT_Y_GENERATION_LEVEL + SkyIslandGenerator.MAX_RANDOM_Y + 1, z);
                } else if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
                {
                    player.setSpawnChunk(new ChunkCoordinates(x, mop.blockY + 2, z), true);
                    player.setPositionAndUpdate(x, mop.blockY + 2, z);
                    player.addChatComponentMessage(new ChatComponentText("Welcome to your new island"));
                } else
                {
                    player.addChatComponentMessage(new ChatComponentText("Failed to find a safe place to teleport you to the island"));
                    SkyIslandGenerator.LOGGER.info("\tFailed to raytrace a safe location to teleport the player[" + player.getCommandSenderName() + "] to the island at " + x + "x " + z + "z");
                }
                return true;
            }
        } else
        {
            SkyIslandGenerator.LOGGER.info("\tNo new locations cached to generate");
        }
        return false;
    }

    public IslandLocation newIsland(World worldObj, int x, int z, String chunkFlat, boolean random)
    {
        return newIsland(worldObj, new IslandLocation(x, z), chunkFlat, random);
    }

    public IslandLocation newIsland(World worldObj, IslandLocation location, String chunkFlat, boolean random)
    {
        SkyIslandGenerator.LOGGER.info("Generating Island[" + chunkFlat + "]");
        long start = System.nanoTime();
        if (islandTypeMap.containsKey(chunkFlat))
        {
            IslandGenerator gen = islandTypeMap.get(chunkFlat);
            if (random)
                gen.generateNoRandom(worldObj, location.x, location.z);
            else
                gen.generate(worldObj, location.x, location.z);

            islandMap.put(location, null);
            if (nextLocations.contains(location))
                nextLocations.remove(location);

            //TODO convert into seconds and mili-seconds
            SkyIslandGenerator.LOGGER.info("\tDone Generating. Took " + (System.nanoTime() - start) + " nano seconds");
            return location;
        } else
        {
            SkyIslandGenerator.LOGGER.info("\t" + chunkFlat + " is not a valid island generator type");
        }
        return null;
    }
}
