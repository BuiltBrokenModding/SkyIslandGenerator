package com.builtbroken.skyislandgenerator.handler;

/**
 * Created by Dark on 8/12/2015.
 */
public class IslandLocation
{
    /** Chunk location x */
    public final int x;
    /** Chunk location z */
    public final int z;

    public IslandLocation(int x, int z)
    {
        this.x = x;
        this.z = z;
    }

    @Override
    public String toString()
    {
        return "IslandLocation [" + x + "," + z + "]";
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof IslandLocation)
        {
            return ((IslandLocation) obj).x == x && ((IslandLocation) obj).z == z;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return x ^ z;
    }
}
