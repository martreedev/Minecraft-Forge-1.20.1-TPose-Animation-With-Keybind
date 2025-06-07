package net.martree.marspeemod.Client;

import net.martree.marspeemod.Capability.TPoseCapability;
import net.martree.marspeemod.TPoseTutorial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import java.util.Map;


@Mod.EventBusSubscriber(
        modid = TPoseTutorial.MODID,
        bus   = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT)
public final class HideVanillaArms {
    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre evt) {//event is fired before the player entity is rendered on the client-side
        PlayerModel<AbstractClientPlayer> m =
                ((PlayerRenderer) evt.getRenderer()).getModel();

        Player p = evt.getEntity();

        if (TPoseCapability.isPlayerTPose(p) ){// check the TPose capability of the player to see if we should hide the vanilla arms
            //hide the vanilla arms of the player. (we want to hide the vanilla arms so we can render separate ones on another layer)
            m.rightArm.visible = false;
            m.leftArm.visible  = false;
            m.rightSleeve.visible = false;
            m.leftSleeve.visible  = false;
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post evt) {//client-side event that is triggered after a player entity has been rendered
        PlayerModel<AbstractClientPlayer> m =
                ((PlayerRenderer) evt.getRenderer()).getModel();
        //reset the arms to visible
        m.rightArm.visible = m.leftArm.visible =
                m.rightSleeve.visible = m.leftSleeve.visible = true;
    }


    public static void registerLayer() {
        Map<EntityType<?>, EntityRenderer<?>> map = Minecraft.getInstance().getEntityRenderDispatcher().renderers;

        for (PlayerRenderer renderer : map.values().stream()
                .filter(r -> r instanceof PlayerRenderer)
                .map(r -> (PlayerRenderer) r).toList()) {

            renderer.addLayer(new TPosedArms(renderer));//register TPose layer to renderer
        }
    }
}