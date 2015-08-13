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
                sender.addChatMessage(new ChatComponentText("/sig abandonIsland <True/False> - True will erase edited chunk"));
            }
        } else if (args[0].equalsIgnoreCase("listTypes"))
        {
            //TODO implement
        } else if (args[0].equalsIgnoreCase("listTypes"))
        {
            if (IslandManager.INSTANCE.islandTypeMap.size() == 0)
            {
                sender.addChatMessage(new ChatComponentText("No generators registered"));
            } else
            {
                sender.addChatMessage(new ChatComponentText("Listing generator types"));
                for (String name : IslandManager.INSTANCE.islandTypeMap.keySet())
                {
                    sender.addChatMessage(new ChatComponentText("  " + name));
                }
            }
        } else if (args[0].equalsIgnoreCase("newIsland") && sender instanceof EntityPlayer)
        {
            if (IslandManager.INSTANCE.playerToIslandMap.containsKey(((EntityPlayer) sender).getGameProfile().getId()))
            {
                IslandData data = IslandManager.INSTANCE.playerToIslandMap.get(((EntityPlayer) sender).getGameProfile().getId());
                sender.addChatMessage(new ChatComponentText("You already have an island at Chunk[" + data.location.x + ", " + data.location.z + "]"));
            } else if (args.length < 2)
            {
                sender.addChatMessage(new ChatComponentText("/sig newIsland <type>"));
            } else if (IslandManager.INSTANCE.islandTypeMap.containsKey(args[1]))
            {
                IslandGenerator generator = IslandManager.INSTANCE.islandTypeMap.get(args[1]);
                if (generator != null)
                {
                    if (!IslandManager.INSTANCE.newIsland((EntityPlayer) sender, args[1], true))
                    {
                        sender.addChatMessage(new ChatComponentText("Error: Something went wrong while generating your new island"));
                    }
                } else
                {
                    sender.addChatMessage(new ChatComponentText("Error: It seems the generator is missing for this type"));
                }
            } else
            {
                sender.addChatMessage(new ChatComponentText("Error: Unknown generator type " + args[1]));
            }
        } else if (args[0].equalsIgnoreCase("genIsland"))
        {
            if (args.length < 4)
            {
                sender.addChatMessage(new ChatComponentText("/sig genIsland <type> <chunkX> <chunkZ>"));
            } else if (IslandManager.INSTANCE.islandTypeMap.containsKey(args[1]))
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

                        }
                        catch (NumberFormatException e)
                        {
                            sender.addChatMessage(new ChatComponentText("Error: can't parse " + args[4] + " whole number"));
                        }
                    }
                    if (world != null)
                    {
                        try
                        {
                            generator.generate(world, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                        }
                        catch (NumberFormatException e)
                        {
                            sender.addChatMessage(new ChatComponentText("Error: can't parse chunk coords whole numbers"));
                        }
                    } else if (args.length == 5)
                    {
                        sender.addChatMessage(new ChatComponentText("Error: unknown world for dim id " + args[4]));
                    } else
                    {
                        sender.addChatMessage(new ChatComponentText("Error: World returned null"));
                    }
                } else
                {
                    sender.addChatMessage(new ChatComponentText("Error: It seems the generator is missing for this type"));
                }

            } else
            {
                sender.addChatMessage(new ChatComponentText("Error: Unknown generator type " + args[1]));
            }
        }
    }
}
