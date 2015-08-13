package com.builtbroken.skyislandgenerator.generator;

import com.builtbroken.skyislandgenerator.SkyIslandGenerator;
import net.minecraft.world.World;

/**
 * Created by Dark on 8/12/2015.
 */
public abstract class IslandGenerator
{
    public void generate(World world, int chunkX, int chunkZ)
    {
        int y = SkyIslandGenerator.DEFAULT_Y_GENERATION_LEVEL;
        if (SkyIslandGenerator.RANDOMIZE_Y_LEVEL)
            y += world.rand.nextInt(SkyIslandGenerator.MAX_RANDOM_Y) - world.rand.nextInt(SkyIslandGenerator.MIN_RANDOM_Y);
        y = Math.min(Math.max(y, 5), 200);

        generate(world, (chunkX << 4) + 8, y, (chunkZ << 4) + 8);
    }

    public void generateNoRandom(World world, int chunkX, int chunkZ)
    {
        generate(world, (chunkX << 4) + 8, SkyIslandGenerator.DEFAULT_Y_GENERATION_LEVEL, (chunkZ << 4) + 8);
    }

    public abstract void generate(World world, int x, int y, int z);
}
