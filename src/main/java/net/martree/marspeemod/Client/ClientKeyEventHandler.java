package net.martree.marspeemod.Client;

import net.martree.marspeemod.Capability.TPoseCapability;
import net.martree.marspeemod.Keybinds;
import net.martree.marspeemod.Network.MyModNetwork;
import net.martree.marspeemod.Network.TPoseTogglePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class ClientKeyEventHandler {
    public static boolean isDown;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event){// on every client tick
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;

        isDown = Keybinds.TPOSE_KEY.isDown();// is the desired Hot key being pressed

        if (isDown){// if the hotkey is being pressed
            TPoseCapability.setPlayerTPose(player, true);// set the t-pose capability for that player to true
            MyModNetwork.CHANNEL.sendToServer(new TPoseTogglePacket(true));// send a Tpose toggle packet to the server to let it know someone is tposing
        }else { //the hotkey is not being pressed (FYI onClientTick runs every tick, you should implement some kind of check to make sure you aren't constantly sending packets to the server when the key isn't being pressed)
            TPoseCapability.setPlayerTPose(player, false);// set the t-pose capability for that player to false
            MyModNetwork.CHANNEL.sendToServer(new TPoseTogglePacket(false));// send a Tpose toggle packet to the server to let it know someone stopped tposing

        }
    }
}