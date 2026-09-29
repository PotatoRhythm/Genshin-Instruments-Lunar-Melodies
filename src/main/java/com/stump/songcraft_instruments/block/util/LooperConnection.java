package com.stump.songcraft_instruments.block.util;

import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

/**
 * A player's instrument connection to a looper.
 * @param playerId The UUID of the connected player
 * @param playerName The name of the connected player, cached for when they are offline
 * @param connectionId The ID stored in the connected instrument's looper tag. An instrument
 *                     whose ID does not match its player's connection is considered disconnected.
 */
public record LooperConnection(UUID playerId, String playerName, UUID connectionId) {
    private static final String
        PLAYER_ID_TAG = "PlayerId",
        PLAYER_NAME_TAG = "PlayerName",
        CONNECTION_ID_TAG = "ConnectionId"
    ;

    public CompoundTag save() {
        final CompoundTag tag = new CompoundTag();
        tag.putUUID(PLAYER_ID_TAG, playerId);
        tag.putString(PLAYER_NAME_TAG, playerName);
        tag.putUUID(CONNECTION_ID_TAG, connectionId);
        return tag;
    }

    public static LooperConnection load(final CompoundTag tag) {
        return new LooperConnection(
            tag.getUUID(PLAYER_ID_TAG),
            tag.getString(PLAYER_NAME_TAG),
            tag.getUUID(CONNECTION_ID_TAG)
        );
    }
}
