package com.builtbroken.skyislandgenerator.command;

import com.builtbroken.skyislandgenerator.generator.IslandGenerator;
import com.builtbroken.skyislandgenerator.handler.IslandData;
import com.builtbroken.skyislandgenerator.handler.IslandManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 * Created by Dark on 8/12/2015.
 */
public class CommandGenerate extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "sig";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return getCommandName() + " help";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args == null || args.length == 0 || args[0].equalsIgnoreCase("help"))
        {
            sender.addChatMessage(new ChatComponentText("/sig listTypes"));
            if (sender instanceof EntityPlayer)
                sender.addChatMessage(new ChatComponentText("/sig genIsland <type> <chunkX> <chunkZ>"));
            sender.addChatMessage(new ChatComponentText("/sig genIsland <type> <dim> <chunkX> <chunkZ>"));
            if (sender instanceof EntityPlayer)
            {
                sender.addChatMessage(new ChatComponentText("/sig newIsland <type> - Created a new Island and teleports you to it"));
                sender.addChatMessage(new ChatComponentText("/sig abandonIsland [True] - True will erase edited chunk"));
            }
        }
        else if (args[0].equalsIgnoreCase("abandonIsland") && sender instanceof EntityPlayer)
        {
            if (IslandManager.INSTANCE.playerToIslandMap.containsKey(((EntityPlayer) sender).getGameProfile().getId()))
            {
                IslandData data = IslandManager.INSTANCE.playerToIslandMap.get(((EntityPlayer) sender).getGameProfile().getId());
                if (data != null)
                {
                    if (args.length > 1)
                    {
                        if (args[1].equalsIgnoreCase("true"))
                        {
                            //TODO save cache of island removed in case a rollback is required
                            //TODO multi-thread chunk eraser in case it causes lag
                            data.clearArea(sender.getEntityWorld());

                            if (!((EntityPlayer) sender).capabilities.isCreativeMode && data.isWithInIslandArea((EntityPlayer) sender))
                            {
                                ((EntityPlayer) sender).setPositionAndUpdate(((EntityPlayer) sender).worldObj.getSpawnPoint().posX, ((EntityPlayer) sender).worldObj.getSpawnPoint().posY, ((EntityPlayer) sender).worldObj.getSpawnPoint().posZ);
                                sender.addChatMessage(new ChatComponentText("You have been teleported to spawn to avoid falling into the void"));
                            }
                            //TODO see if any other players are nearby && reset any players with homes at location as well
                            ((EntityPlayer) sender).setSpawnChunk(((EntityPlayer) sender).worldObj.getSpawnPoint(), true);

                            //Clear entry allowing the island location to be used again, only done if area is erased
                            IslandManager.INSTANCE.islandMap.remove(data.location);
                            sender.addChatMessage(new ChatComponentText("Island data, location, and island wiped out"));
                        }
                        else if (!args[1].equalsIgnoreCase("false"))
                        {
                            sender.addChatMessage(new ChatComponentText("Could not parse " + args[1] + " as true or false"));
                            return;
                        }
                    }
                    else
                    {
                        sender.addChatMessage(new ChatComponentText("Island data wiped out"));
                    }
                }
                else
                {
                    sender.addChatMessage(new ChatComponentText("Island data was null, only the id entry was removed"));
                }
                IslandManager.INSTANCE.playerToIslandMap.remove(((EntityPlayer) sender).getGameProfile().getId());
            }
            else
            {
                sender.addChatMessage(new ChatComponentText("Island manager doesn't show a record of an island for your user id"));
            }
        }
        else if (args[0].equalsIgnoreCase("listTypes"))
        {
            if (IslandManager.INSTANCE.islandTypeMap.size() == 0)
            {
                sender.addChatMessage(new ChatComponentText("No generators registered"));
            }
            else
            {
                sender.addChatMessage(new ChatComponentText("Listing generator types"));
                for (String name : IslandManager.INSTANCE.islandTypeMap.keySet())
                {
                    sender.addChatMessage(new ChatComponentText("  " + name));
                }
            }
        }
        else if (args[0].equalsIgnoreCase("newIsland") && sender instanceof EntityPlayer)
        {
            if (IslandManager.INSTANCE.playerToIslandMap.containsKey(((EntityPlayer) sender).getGameProfile().getId()))
            {
                IslandData data = IslandManager.INSTANCE.playerToIslandMap.get(((EntityPlayer) sender).getGameProfile().getId());
                sender.addChatMessage(new ChatComponentText("You already have an island at Chunk[" + data.location.x + ", " + data.location.z + "]"));
            }
            else if (args.length < 2)
            {
                sender.addChatMessage(new ChatComponentText("/sig newIsland <type>"));
            }
            else if (IslandManager.INSTANCE.islandTypeMap.containsKey(args[1]))
            {
                IslandGenerator generator = IslandManager.INSTANCE.islandTypeMap.get(args[1]);
                if (generator != null)
                {
                    if (!IslandManager.INSTANCE.newIsland((EntityPlayer) sender, args[1], true))
                    {
                        sender.addChatMessage(new ChatComponentText("Error: Something went wrong while generating your new island"));
                    }
                }
                else
                {
                    sender.addChatMessage(new ChatComponentText("Error: It seems the generator is missing for this type"));
                }
            }
            else
            {
                sender.addChatMessage(new ChatComponentText("Error: Unknown generator type " + args[1]));
            }
        }
        else if (args[0].equalsIgnoreCase("genIsland"))
        {
            if (args.length < 4)
            {
                sender.addChatMessage(new ChatComponentText("/sig genIsland <type> <chunkX> <chunkZ>"));
            }
            else if (IslandManager.INSTANCE.islandTypeMap.containsKey(args[1]))
            {
                IslandGenerator generator = IslandManager.INSTANCE.islandTypeMap.get(args[1]);
                if (generator != null)
                {
                    World world = sender.getEntityWorld();
                    if (args.length == 5)
                    {
                        try
                        {
                            world = DimensionManager.getWorld(Integer.parseInt(args[4]));

                        } catch (NumberFormatException e)
                        {
                            sender.addChatMessage(new ChatComponentText("Error: can't parse " + args[4] + " whole number"));
                        }
                    }
                    if (world != null)
                    {
                        try
                        {
                            generator.generate(world, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                        } catch (NumberFormatException e)
                        {
                            sender.addChatMessage(new ChatComponentText("Error: can't parse chunk coords whole numbers"));
                        }
                    }
                    else if (args.length == 5)
                    {
                        sender.addChatMessage(new ChatComponentText("Error: unknown world for dim id " + args[4]));
                    }
                    else
                    {
                        sender.addChatMessage(new ChatComponentText("Error: World returned null"));
                    }
                }
                else
                {
                    sender.addChatMessage(new ChatComponentText("Error: It seems the generator is missing for this type"));
                }

            }
            else
            {
                sender.addChatMessage(new ChatComponentText("Error: Unknown generator type " + args[1]));
            }
        }
        else
        {
            sender.addChatMessage(new ChatComponentText("Error: Unknown command " + args[0]));
        }
    }
}
