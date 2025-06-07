package net.martree.marspeemod.Capability;

import net.martree.marspeemod.TPoseTutorial;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = TPoseTutorial.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TPoseCapability {//Capability for per player state tracking
    private boolean isTPose;

    public boolean isTPose()         { return isTPose; }
    public void setTPose(boolean v)  { isTPose = v; }

    /**
     * This method returns whether the player is T-posing or not.
     * @param player Player object. The player you wish to fetch capability for
     * @return The players T-posing status Boolean.
     */
    public static boolean isPlayerTPose(Player player) {
        return player.getCapability(TPoseCapability.CAP)
                .map(TPoseCapability::isTPose)
                .orElse(false);
    }

    /**
     * This method sets the T-posing status of a specific player using capability.
     * @param player Player object. The player you wish to set capability for.
     * @param value Boolean. The value you want to set the players T-pose status to.
     */
    public static void setPlayerTPose(Player player, boolean value) {
        player.getCapability(TPoseCapability.CAP).ifPresent(cap -> cap.setTPose(value));
    }

    /* ---------- registration boilerplate ---------- */
    public static final ResourceLocation KEY = new ResourceLocation(TPoseTutorial.MODID, "tpose");
    public static final Capability<TPoseCapability> CAP =
            CapabilityManager.get(new CapabilityToken<>() {});

    /* attach to every player */
    /**
     * This method sets the T-posing status of a specific player using capability.
     * @param evt-> AttachCapabilitiesEvent< Entity >.  AttachCapabilities Entity event. Contains things like the player object Player = evt.getObject().
     */
    @SubscribeEvent
    public static void attachCaps(AttachCapabilitiesEvent<Entity> evt) {
        if (evt.getObject() instanceof Player) {// if the event object is a player
            evt.addCapability(KEY, new ICapabilityProvider() {// we want to attach a T-pose Capability to every player
                final LazyOptional<TPoseCapability> inst = LazyOptional.of(TPoseCapability::new);
                @Override public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
                    return cap == CAP ? inst.cast() : LazyOptional.empty();
                }
            });
        }
    }
}
