package com.builtbroken.skyislandgenerator.generator;

import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 * Created by Dark on 8/12/2015.
 */
public class FlatPlatformGenerator extends IslandGenerator
{
    Block block;
    int width = 16;
    int length = 16;
    int height = 2;

    public FlatPlatformGenerator(Block block, int width, int length, int height)
    {
        this.block = block;
        this.width = width;
        this.length = length;
        this.height = height;
    }

    @Override
    public void generate(World world, int sx, int y, int sz)
    {
        genPlatform(world, sx - (width / 2) - (width % 2), y, sz - (length / 2) - (length % 2));
    }

    public void genPlatform(World world, int sx, int sy, int sz)
    {
        for (int y = sy; y < sy + height && y < 255 && y > 5; y++)
        {
            for (int x = sx; x < sx + width; x++)
            {
                for (int z = sz; z < sz + length; z++)
                {
                    world.setBlock(x, y, z, block);
                }
            }
        }
    }
}
