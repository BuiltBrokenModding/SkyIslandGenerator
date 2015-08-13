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
        generate_a(world, sx - 8, y, sz - 8);
    }

    public void generate_a(World world, int sx, int y, int sz)
    {
        genPlatform(world, sx, y, sz);
        //Generate cobble stone markers for spacing check
        for (int i = 0; i < 4; i++)
        {
            world.setBlock(sx + 2, y + 1, sz + 3 + (i * 4), Blocks.cobblestone);
            world.setBlock(sx + 6, y + 1, sz + 3 + (i * 4), Blocks.cobblestone);
        }

        //Generate water pool
        for (int i = 0; i < 3; i++)
        {
            world.setBlock(sx + 3 + i, y + 1, sz + 4, Blocks.flowing_water);
            world.setBlock(sx + 3 + i, y + 1, sz + 5, Blocks.flowing_water);
            world.setBlock(sx + 3 + i, y + 1, sz + 13, Blocks.dirt);
            world.setBlock(sx + 3 + i, y + 1, sz + 14, Blocks.dirt);
        }
    }
}
