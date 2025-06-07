package net.martree.marspeemod.Client;

import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.martree.marspeemod.Capability.TPoseCapability;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class TPosedArms extends RenderLayer<
        AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public TPosedArms(RenderLayerParent<AbstractClientPlayer,
            PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int light,
                       AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        if (TPoseCapability.isPlayerTPose(player)){// check the TPose capability of the player to see if we should render the new arms
            PlayerModel<AbstractClientPlayer> model = getParentModel();

            // Copy current state before modifying
            poseStack.pushPose();

            // Ensure we use the correct pose (like sneaking offset, head/body rotation etc.)
            model.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            // Ensure arms are visible (temporarily)
            model.leftArm.visible = true;
            model.rightArm.visible = true;

            // Apply the T-pose manually
            model.leftArm.zRot = (float) -Math.PI / 2F;
            model.rightArm.zRot = (float) Math.PI / 2F;
            model.leftArm.xRot = model.rightArm.xRot = 0F;
            model.leftArm.yRot = model.rightArm.yRot = 0F;

            // Get player skin
            ResourceLocation skin = player.getSkinTextureLocation();
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(skin));

            // Only render the arms (not the full model)
            model.leftArm.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
            model.rightArm.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);

            poseStack.popPose();
        }
    }
}

