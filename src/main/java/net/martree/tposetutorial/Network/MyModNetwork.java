package net.martree.marspeemod.Network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Handles all network communication for the mod, including registration of custom packets
 * such as T-Pose toggling and syncing. This class sets up a {@link SimpleChannel} which
 * is used to send packets between client and server.
 */
public class MyModNetwork {

    /** The current protocol version of the network channel. Must match between client and server. */
    private static final String PROTOCOL_VERSION = "1.0";

    /**
     * The main network channel for the mod. It is registered under the mod ID and
     * will handle all custom packets.
     */
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("tposetutorial", "main"), // Replace with your mod ID
            () -> PROTOCOL_VERSION,                    // Local version supplier
            PROTOCOL_VERSION::equals,                 // Client-side check
            PROTOCOL_VERSION::equals                  // Server-side check
    );

    /** Internal counter to assign unique packet IDs. */
    private static int packetId = 0;

    /**
     * Returns the next unique packet ID. Used when registering packets.
     *
     * @return A unique integer ID for a new packet type.
     */
    private static int nextId() {
        return packetId++;
    }

    /**
     * Registers all network packets for the mod.
     * This method should be called during mod initialization (e.g., in common setup).
     */
    public static void register() {
        // Register packet sent from client to server when T-Pose key is pressed
        CHANNEL.registerMessage(
                nextId(),
                TPoseTogglePacket.class,
                TPoseTogglePacket::encode,
                TPoseTogglePacket::decode,
                TPoseTogglePacket::handle
        );

        // Register packet sent from server to all clients to sync a player's T-Pose state
        CHANNEL.registerMessage(
                nextId(),
                TPoseSyncPacket.class,
                TPoseSyncPacket::encode,
                TPoseSyncPacket::decode,
                TPoseSyncPacket::handle
        );
    }
}

