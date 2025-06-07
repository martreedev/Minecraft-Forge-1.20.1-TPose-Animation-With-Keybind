package net.martree.marspeemod.Client;

import net.martree.marspeemod.TPoseTutorial;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = TPoseTutorial.MODID,
        bus   = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT)

public final class LayerRegistrar {
    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {

        // This event fires after ALL player renderers are created
        for (String skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin); // "default", "slim", etc.
            if (renderer != null) {
                renderer.addLayer(new TPosedArms(renderer));// add the layer
            }
        }
    }
}
