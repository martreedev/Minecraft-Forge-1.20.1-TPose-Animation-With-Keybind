package net.martree.marspeemod.Network;

import net.martree.marspeemod.Capability.TPoseCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import java.util.function.Supplier;

/**
 * This packet is sent from the client to the server when a player toggles their T-Pose state
 * (e.g., via a keybind). The server updates the player's TPose capability and broadcasts
 * the change to all clients using {@link TPoseSyncPacket}.
 */
public class TPoseTogglePacket {

    /** Whether the player should be in a T-Pose. */
    private final boolean isTPose;

    /**
     * Constructs a new TPoseTogglePacket.
     *
     * @param isTPose The new T-Pose state (true = enable T-Pose, false = disable).
     */
    public TPoseTogglePacket(boolean isTPose) {
        this.isTPose = isTPose;
    }

    /**
     * Encodes this packet into a {@link FriendlyByteBuf} for transmission.
     *
     * @param msg The message instance to encode.
     * @param buf The buffer to write data to.
     */
    public static void encode(TPoseTogglePacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.isTPose);
    }

    /**
     * Decodes a {@link TPoseTogglePacket} from a {@link FriendlyByteBuf}.
     *
     * @param buf The buffer containing the serialized data.
     * @return A new instance of {@link TPoseTogglePacket}.
     */
    public static TPoseTogglePacket decode(FriendlyByteBuf buf) {
        return new TPoseTogglePacket(buf.readBoolean());
    }

    /**
     * Handles this packet when it is received on the server.
     * Updates the server-side capability for the player who sent the packet,
     * and then broadcasts the updated state to all clients.
     *
     * @param msg The received packet instance.
     * @param ctx The network context from Forge.
     */
    public static void handle(TPoseTogglePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Get the player who sent this packet
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                // Update their TPose state on the server
                sender.getCapability(TPoseCapability.CAP).ifPresent(cap -> cap.setTPose(msg.isTPose));

                // Broadcast the new TPose state to all clients
                MyModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(),
                        new TPoseSyncPacket(sender.getUUID(), msg.isTPose));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

