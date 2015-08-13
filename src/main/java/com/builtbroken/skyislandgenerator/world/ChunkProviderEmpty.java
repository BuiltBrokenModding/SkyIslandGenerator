package com.builtbroken.skyislandgenerator.world;

import com.builtbroken.skyislandgenerator.SkyIslandGenerator;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.List;

public class ChunkProviderEmpty implements IChunkProvider
{
    private World worldObj;

    public ChunkProviderEmpty(World p_i2004_1_)
    {
        this.worldObj = p_i2004_1_;
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    @Override
    public Chunk loadChunk(int x, int z)
    {
        return this.provideChunk(x, z);
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    @Override
    public Chunk provideChunk(int chunkX, int chunkZ)
    {
        Chunk chunk = new Chunk(this.worldObj, chunkX, chunkZ);

        //Init spawn if it has not already been created
        if ((this.worldObj.getSpawnPoint().posX >> 4) == chunkX && (this.worldObj.getSpawnPoint().posZ >> 4) == chunkZ)
        {
            //Generate spawn platform
            int k = SkyIslandGenerator.DEFAULT_Y_GENERATION_LEVEL;
            int l = SkyIslandGenerator.DEFAULT_Y_GENERATION_LEVEL >> 4;
            ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[l];

            if (extendedblockstorage == null)
            {
                extendedblockstorage = new ExtendedBlockStorage(k, !this.worldObj.provider.hasNoSky);
                chunk.getBlockStorageArray()[l] = extendedblockstorage;
            }

            for (int cx = 0; cx < 16; ++cx)
            {
                for (int cz = 0; cz < 16; ++cz)
                {
                    extendedblockstorage.func_150818_a(cx, k & 15, cz, Blocks.dirt);
                    //extendedblockstorage.setExtBlockMetadata(cx, k & 15, cz, 0);
                }
            }

            //Fix spawn point
            int x = (chunkX << 4) + 8;
            int z = (chunkZ << 4) + 8;
            int y = SkyIslandGenerator.DEFAULT_Y_GENERATION_LEVEL + 2;

            this.worldObj.setSpawnLocation(x, y, z);
        }

        //Not sure if we need to call this before biome
        chunk.generateSkylightMap();

        //Set biome ID for chunk
        for (int l = 0; l < chunk.getBiomeArray().length; ++l)
        {
            chunk.getBiomeArray()[l] = (byte) 1;
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    @Override
    public boolean chunkExists(int p_73149_1_, int p_73149_2_)
    {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    @Override
    public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_)
    {

    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    @Override
    public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_)
    {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    @Override
    public void saveExtraData() {}

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    @Override
    public boolean unloadQueuedChunks()
    {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    @Override
    public boolean canSave()
    {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    @Override
    public String makeString()
    {
        return "sigVoidSource";
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    @Override
    public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_, int p_73155_4_)
    {
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(p_73155_2_, p_73155_4_);
        return biomegenbase.getSpawnableList(p_73155_1_);
    }

    @Override
    public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_)
    {
        return null;
    }

    @Override
    public int getLoadedChunkCount()
    {
        return 0;
    }

    @Override
    public void recreateStructures(int p_82695_1_, int p_82695_2_)
    {

    }
}