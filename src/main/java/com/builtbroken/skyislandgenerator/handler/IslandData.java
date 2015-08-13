package com.builtbroken.skyislandgenerator.handler;

import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

/**
 * Created by Dark on 8/12/2015.
 */
public class IslandData
{
    String owner;
    UUID ownerID;

    public IslandData(EntityPlayer player)
    {
        owner = player.getCommandSenderName();
        ownerID = player.getGameProfile().getId();
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
