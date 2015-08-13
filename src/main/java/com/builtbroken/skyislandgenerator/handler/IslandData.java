package com.builtbroken.skyislandgenerator.handler;

import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

/**
 * Created by Dark on 8/12/2015.
 */
public class IslandData
{
    public final IslandLocation location;
    public final String owner;
    public final UUID ownerID;

    public IslandData(EntityPlayer player, IslandLocation location)
    {
        this.owner = player.getCommandSenderName();
        this.ownerID = player.getGameProfile().getId();
        this.location = location;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof IslandData)
        {
            return ((IslandData) obj).ownerID == ownerID;
        }
        return false;
    }

    @Override
    public String toString()
    {
        return "IslandData[" + owner + "]";
    }
}
