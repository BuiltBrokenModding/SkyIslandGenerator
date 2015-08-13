package com.builtbroken.skyislandgenerator.generator.pregen;

import com.builtbroken.skyislandgenerator.generator.FlatPlatformGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

/**
 * Created by Dark on 8/12/2015.
 */
public class StoneRectWithWaterGenerator extends FlatPlatformGenerator
{
    public StoneRectWithWaterGenerator()
    {
        super(Blocks.stonebrick, 9, 19, 2);
    }

    @Override
    public void generate(World world, int sx, int y, int sz)
    {
        //Generate cobble stone markers for spacing check
        for (int i = 0; i < 5; i++)
        {
            world.setBlock(sx + 3, y + 1, sz + 2 + (i * 4), Blocks.cobblestone);
            world.setBlock(sx + 7, y + 1, sz + 2 + (i * 4), Blocks.cobblestone);
        }

        //Generate water pool
        for (int i = 0; i < 3; i++)
        {
            world.setBlock(sx + 4 + i, y + 1, sz + 4, Blocks.flowing_water);
            world.setBlock(sx + 4 + i, y + 1, sz + 5, Blocks.flowing_water);
        }

        //Generate dirt pad for growing trees
        for (int i = 0; i < 3; i++)
        {
            world.setBlock(sx + 15 + i, y + 1, sz + 4, Blocks.dirt);
            world.setBlock(sx + 15 + i, y + 1, sz + 5, Blocks.dirt);
        }
    }
}
