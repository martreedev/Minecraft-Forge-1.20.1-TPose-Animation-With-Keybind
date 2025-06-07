package net.martree.marspeemod.Network;

import net.martree.marspeemod.Capability.TPoseCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * This packet is sent from the server to all clients to synchronize the TPose state of a specific player.
 * It ensures that all clients render the T-Pose animation for the correct player based on the current state.
 */
public class TPoseSyncPacket {

    /** The UUID of the player whose TPose state is being synchronized. */
    private final UUID playerId;

    /** Whether the player should currently be in a T-Pose. */
    private final boolean isTPose;

    /**
     * Constructs a new TPoseSyncPacket with the given player ID and TPose state.
     *
     * @param playerId The UUID of the player.
     * @param isTPose Whether the player is currently in a T-Pose.
     */
    public TPoseSyncPacket(UUID playerId, boolean isTPose) {
        this.playerId = playerId;
        this.isTPose = isTPose;
    }

    /**
     * Encodes the packet data into a buffer for network transmission.
     *
     * @param msg The packet to encode.
     * @param buf The buffer to write to.
     */
    public static void encode(TPoseSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerId);
        buf.writeBoolean(msg.isTPose);
    }

    /**
     * Decodes the packet data from a buffer received over the network.
     *
     * @param buf The buffer containing the packet data.
     * @return A new TPoseSyncPacket instance.
     */
    public static TPoseSyncPacket decode(FriendlyByteBuf buf) {
        return new TPoseSyncPacket(buf.readUUID(), buf.readBoolean());
    }

    /**
     * Handles the packet when received on the client.
     * Updates the local TPose capability state for the specified player.
     *
     * @param msg The received packet.
     * @param ctx The network context.
     */
    public static void handle(TPoseSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Get the current client world
            ClientLevel world = Minecraft.getInstance().level;
            if (world != null) {
                // Find the player by UUID
                Player player = world.getPlayerByUUID(msg.playerId);
                if (player != null) {
                    // Update their TPose capability to match the synced state
                    player.getCapability(TPoseCapability.CAP).ifPresent(cap -> cap.setTPose(msg.isTPose));

                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

