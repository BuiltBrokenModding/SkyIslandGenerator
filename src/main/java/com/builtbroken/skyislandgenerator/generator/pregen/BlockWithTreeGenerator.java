package com.builtbroken.skyislandgenerator.generator.pregen;

import com.builtbroken.skyislandgenerator.generator.IslandGenerator;
import net.minecraft.block.BlockSapling;
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
        world.setBlock(x, y + 1, z, Blocks.sapling);
        ((BlockSapling) Blocks.sapling).func_149879_c(world, x, y + 1, z, world.rand);
    }
}
