package com.builtbroken.skyislandgenerator.world;

import com.builtbroken.skyislandgenerator.SkyIslandGenerator;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

/**
 * Created by Dark on 8/13/2015.
 */
public class WorldGenWrapper implements IWorldGenerator
{
    private IWorldGenerator generator;

    public WorldGenWrapper(IWorldGenerator generator)
    {
        this.generator = generator;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        if (world.provider.terrainType != SkyIslandGenerator.emptyWorldGenerator)
        {
            generator.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }
    }
}
