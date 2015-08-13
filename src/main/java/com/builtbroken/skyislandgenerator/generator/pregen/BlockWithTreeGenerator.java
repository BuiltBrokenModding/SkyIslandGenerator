package com.builtbroken.skyislandgenerator.generator.pregen;

import com.builtbroken.skyislandgenerator.generator.IslandGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

/**
 * Created by Dark on 8/12/2015.
 */
public class BlockWithTreeGenerator extends IslandGenerator
{
    @Override
    public void generate(World world, int x, int y, int z)
    {
        world.setBlock(x, y, z, Blocks.grass);
        world.setBlock(x, y + 1, z, Blocks.log);
        world.setBlock(x, y + 2, z, Blocks.log);
        for (int j = y + 3; j <= y + 4; j++)
        {
            for (int i = x - 1; i <= x + 1; i++)
            {
                for (int k = z - 1; k <= z + 1; k++)
                {
                    if (i == x && k == z)
                        world.setBlock(i, j, k, Blocks.log);
                    else
                        world.setBlock(i, j, k, Blocks.leaves);
                }
            }
        }
        world.setBlock(x, y + 5, z, Blocks.leaves);
        world.setBlock(x + 1, y + 5, z, Blocks.leaves);
        world.setBlock(x - 1, y + 5, z, Blocks.leaves);
        world.setBlock(x, y + 5, z + 1, Blocks.leaves);
        world.setBlock(x, y + 5, z - 1, Blocks.leaves);
    }
}
