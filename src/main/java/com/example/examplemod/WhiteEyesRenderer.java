package com.example.examplemod;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class WhiteEyesRenderer extends MobRenderer<WhiteEyesEntity, WhiteEyesModel<WhiteEyesEntity>> {
	
    public WhiteEyesRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new WhiteEyesModel<>(pContext.bakeLayer(WhiteEyesModel.LAYER_LOCATION)), 0.85f);
        this.addLayer(new WhiteEyesEyesLayer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteEyesEntity pEntity) {
        return ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "textures/entity/white_eyes.png");
    }

    @Override
    public void render(WhiteEyesEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
                       MultiBufferSource pBuffer, int pPackedLight) {
        if(pEntity.isBaby()) {
            pPoseStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            pPoseStack.scale(1f, 1f, 1f);
        }

        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}
