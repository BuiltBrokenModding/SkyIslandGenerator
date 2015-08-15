package com.builtbroken.skyislandgenerator;

import com.builtbroken.skyislandgenerator.command.CommandGenerate;
import com.builtbroken.skyislandgenerator.generator.FlatPlatformGenerator;
import com.builtbroken.skyislandgenerator.generator.pregen.BlockWithTreeGenerator;
import com.builtbroken.skyislandgenerator.generator.pregen.StoneRectWithWaterGenerator;
import com.builtbroken.skyislandgenerator.handler.IslandManager;
import com.builtbroken.skyislandgenerator.world.ChunkProviderEmpty;
import com.builtbroken.skyislandgenerator.world.WorldGenWrapper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Dark on 8/12/2015.
 */
@Mod(modid = SkyIslandGenerator.DOMAIN, name = "Sky Island Generator", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@", acceptableRemoteVersions = "*")
public class SkyIslandGenerator
{
    public static final String DOMAIN = "skyislandgenerator";

    @SidedProxy(clientSide = "com.builtbroken.skyislandgenerator.ClientProxy", serverSide = "com.builtbroken.skyislandgenerator.ServerProxy")
    public static CommonProxy proxy;

    public static Logger LOGGER;

    @Mod.Instance(DOMAIN)
    public static SkyIslandGenerator INSTANCE;

    public static Configuration config;
    public static int DISTANCE_BETWEEN_ISLANDS = 1000;
    public static int DEFAULT_Y_GENERATION_LEVEL = 70;
    public static int MIN_RANDOM_Y = 5;
    public static int MAX_RANDOM_Y = 5;
    public static boolean RANDOMIZE_Y_LEVEL = true;
    public static boolean FORCE_EMPTY_WORLD_GENERATOR = true;
    public static boolean LOAD_WORLD_TYPE_GENERATOR = true;
    public static boolean DISABLE_WORLD_GENERATORS = true;

    public static WorldType emptyWorldGenerator;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("SkyIslandGenerator");
        config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/SkyIslandGenerator.cfg"));
        config.load();
        DISTANCE_BETWEEN_ISLANDS = config.getInt("Distance_Between_Isalands", Configuration.CATEGORY_GENERAL, DISTANCE_BETWEEN_ISLANDS, 100, 10000, "Distance to generate islands from each other, measured from center of each island");
        DEFAULT_Y_GENERATION_LEVEL = config.getInt("Default_Y_Generation_Level", Configuration.CATEGORY_GENERAL, DEFAULT_Y_GENERATION_LEVEL, 5, 200, "Y level to create new islands near");
        RANDOMIZE_Y_LEVEL = config.getBoolean("Randomize_Y_Level", Configuration.CATEGORY_GENERAL, RANDOMIZE_Y_LEVEL, "Allows the Y level to randomize during generation");
        MIN_RANDOM_Y = config.getInt("MIN_RANDOM_Y", Configuration.CATEGORY_GENERAL, MIN_RANDOM_Y, 0, 50, "Random Y level to go down by, will stop 5 blocks from bottom of map");
        MAX_RANDOM_Y = config.getInt("MAX_RANDOM_Y", Configuration.CATEGORY_GENERAL, MAX_RANDOM_Y, 0, 50, "Random Y level to go up by, will stop 55 blocks from top of map");
        FORCE_EMPTY_WORLD_GENERATOR = config.getBoolean("Force_Empty_World", Configuration.CATEGORY_GENERAL, FORCE_EMPTY_WORLD_GENERATOR, "Hacks into the world generator for the overworld causing it to be empty by default");
        LOAD_WORLD_TYPE_GENERATOR = config.getBoolean("EnableGuiWorldType", Configuration.CATEGORY_GENERAL, LOAD_WORLD_TYPE_GENERATOR, "Loads the world type into single player GUI and make it accessible from the server.cfg");
        DISABLE_WORLD_GENERATORS = config.getBoolean("DisableWorldGenerators", Configuration.CATEGORY_GENERAL, DISABLE_WORLD_GENERATORS, "If the world is a void world we want to disable world generators from mods to prevent ores from generating on islands");

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        emptyWorldGenerator = new WorldType("sigVoid")
        {
            @Override
            public IChunkProvider getChunkGenerator(World world, String generatorOptions)
            {
                return new ChunkProviderEmpty(world);
            }

            @Override
            public boolean getCanBeCreated()
            {
                return LOAD_WORLD_TYPE_GENERATOR;
            }
        };
        MinecraftForge.EVENT_BUS.register(this);
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
        config.save();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
        ServerCommandManager serverCommandManager = ((ServerCommandManager) commandManager);

        //Register commands
        serverCommandManager.registerCommand(new CommandGenerate());

        //Register generators
        IslandManager manager = IslandManager.INSTANCE;
        manager.registerGenerator("chunkFlat", new FlatPlatformGenerator(Blocks.dirt, 16, 16, 3));
        manager.registerGenerator("stonebrickPlatform", new StoneRectWithWaterGenerator());
        manager.registerGenerator("tree", new BlockWithTreeGenerator());

        FMLCommonHandler.instance().bus().register(manager);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onWorldLoad(WorldEvent.Load event)
    {
        if (FORCE_EMPTY_WORLD_GENERATOR && event.world.provider.dimensionId == 0)
        {
            event.world.provider.terrainType = emptyWorldGenerator;
        }

        if (DISABLE_WORLD_GENERATORS && event.world.provider.terrainType == emptyWorldGenerator)
        {
            try
            {
                Field worldGeneratorsField = GameRegistry.class.getDeclaredField("worldGenerators");
                worldGeneratorsField.setAccessible(true);

                Field worldGeneratorIndexField = GameRegistry.class.getDeclaredField("worldGeneratorIndex");
                worldGeneratorIndexField.setAccessible(true);

                Set<IWorldGenerator> oldSet = ((Set<IWorldGenerator>) worldGeneratorsField.get(null));
                Map<IWorldGenerator, Integer> oldSetIndex = ((Map<IWorldGenerator, Integer>) worldGeneratorIndexField.get(null));

                HashMap<IWorldGenerator, Integer> newGenerators = new HashMap();
                Iterator<IWorldGenerator> it = oldSet.iterator();
                while (it.hasNext())
                {
                    IWorldGenerator gen = it.next();
                    newGenerators.put(new WorldGenWrapper(gen), oldSetIndex.get(gen));
                    oldSetIndex.remove(gen);
                    it.remove();
                }

                for (Map.Entry<IWorldGenerator, Integer> entry : newGenerators.entrySet())
                {
                    GameRegistry.registerWorldGenerator(entry.getKey(), entry.getValue());
                }

            } catch (NoSuchFieldException e)
            {
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }
}
