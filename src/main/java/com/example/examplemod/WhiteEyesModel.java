package com.example.examplemod;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class WhiteEyesModel<T extends WhiteEyesEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExampleMod.MODID, "white_eyes"), "main");
	private final ModelPart root;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart leg3;
	private final ModelPart leg4;
	private final ModelPart leg5;
	private final ModelPart leg6;
	private final ModelPart leg7;
	private final ModelPart leg8;
	private final ModelPart body2;
	private final ModelPart body;
	private final ModelPart head;
    
	public WhiteEyesModel(ModelPart root) {
		this.root = root.getChild("root");
		this.leg1 = this.root.getChild("leg1");
		this.leg2 = this.root.getChild("leg2");
		this.leg3 = this.root.getChild("leg3");
		this.leg4 = this.root.getChild("leg4");
		this.leg5 = this.root.getChild("leg5");
		this.leg6 = this.root.getChild("leg6");
		this.leg7 = this.root.getChild("leg7");
		this.leg8 = this.root.getChild("leg8");
		this.body2 = this.root.getChild("body2");
		this.body = this.root.getChild("body");
		this.head = this.root.getChild("head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition leg1 = root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(16, 49).addBox(0.0F, -2.0F, -1.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(52, 52).addBox(7.0F, -2.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -4.0F, 0.0F, -0.2359F, 0.422F, -0.5307F));

		PartDefinition leg2 = root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(0, 41).addBox(0.0F, -2.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(44, 48).addBox(16.0F, -2.0F, -1.0F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -4.0F, 4.0F, 0.2359F, -0.422F, -0.5307F));

		PartDefinition leg3 = root.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(38, 40).addBox(0.0F, -2.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(36, 48).addBox(16.0F, -2.0F, -1.0F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -4.0F, 2.0F, 0.0F, 0.0F, -0.48F));

		PartDefinition leg4 = root.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(52, 48).addBox(-7.0F, -2.0F, -1.0F, 7.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(16, 53).addBox(-9.0F, -2.0F, -1.0F, 2.0F, 11.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -4.0F, 0.0F, -0.2359F, -0.422F, 0.5307F));

		PartDefinition leg5 = root.addOrReplaceChild("leg5", CubeListBuilder.create().texOffs(36, 44).addBox(-16.0F, -2.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 49).addBox(-18.0F, -2.0F, -1.0F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -4.0F, 2.0F, 0.0F, 0.0F, 0.48F));

		PartDefinition leg6 = root.addOrReplaceChild("leg6", CubeListBuilder.create().texOffs(0, 45).addBox(-16.0F, -2.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 49).addBox(-18.0F, -2.0F, -1.0F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -4.0F, 4.0F, 0.2359F, 0.422F, 0.5307F));

		PartDefinition leg7 = root.addOrReplaceChild("leg7", CubeListBuilder.create().texOffs(0, 45).addBox(-16.0F, -2.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 49).addBox(-18.0F, -2.0F, -1.0F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -4.0F, 4.0F, 0.3915F, 0.8677F, 0.7652F));

		PartDefinition leg8 = root.addOrReplaceChild("leg8", CubeListBuilder.create().texOffs(0, 41).addBox(0.0F, -2.0F, -1.0F, 16.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(44, 48).addBox(16.0F, -2.0F, -1.0F, 2.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -4.0F, 4.0F, 0.3915F, -0.8677F, -0.7652F));
		
		PartDefinition body2 = root.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(22, 51).addBox(-6.0F, -12.4329F, -1.9526F, 4.0F, 13.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(32, 61).addBox(-2.0F, -11.4329F, -0.9526F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(62, 0).addBox(-2.0F, -8.4329F, -0.9526F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(32, 53).addBox(-2.0F, -5.4329F, -0.9526F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(62, 4).addBox(-2.0F, -11.4329F, -4.9526F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(62, 2).addBox(-2.0F, -8.4329F, -4.9526F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(32, 55).addBox(-2.0F, -5.4329F, -4.9526F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(62, 12).addBox(-8.001F, -11.4329F, -4.9526F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(62, 10).addBox(-8.0F, -11.4329F, -0.9526F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(62, 8).addBox(-8.0F, -8.4329F, -0.9526F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(32, 59).addBox(-8.0F, -5.4329F, -0.9526F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(62, 6).addBox(-8.001F, -8.4329F, -4.9526F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(32, 57).addBox(-8.001F, -5.4329F, -4.9526F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(54, 0).addBox(0.0F, -11.4329F, -4.9526F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(54, 6).addBox(0.0F, -8.4329F, -4.9526F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(54, 12).addBox(0.0F, -5.4329F, -4.9526F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(60, 58).addBox(-8.0F, -11.4329F, -4.9526F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(60, 52).addBox(-8.0F, -8.4329F, -4.9526F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(54, 18).addBox(-8.0F, -5.4329F, -4.9526F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -11.0F, 1.0F, 0.2182F, 0.0F, 0.0F));

		PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 24).addBox(-9.0F, -8.0F, -10.0F, 10.0F, 8.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-10.0F, -9.0F, -1.0F, 12.0F, 9.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -4.0F, 9.0F));

		PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(38, 24).addBox(-4.0F, -23.7156F, -8.5973F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

    @Override
    public void setupAnim(WhiteEyesEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);

        this.animateWalk(WhiteEyesAnimations.ANIM_WHITE_EYES_WALKING, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.animate(entity.idleAnimationState, WhiteEyesAnimations.ANIM_TRICERATOPS_IDLE, ageInTicks, 1f);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -30.0F, 30.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -25.0F, 45.0F);

        this.head.yRot = pNetHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = pHeadPitch * ((float)Math.PI / 180F);
    }

	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		root.render(poseStack, vertexConsumer, packedLight, packedOverlay);
	}

    @Override
    public ModelPart root() {
        return root;
    }
}
