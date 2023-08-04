package com.snoeyz;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class JacobEntityRenderer extends MobEntityRenderer<JacobEntity, ChickenEntityModel<JacobEntity>> {
  private static final Identifier TEXTURE = new Identifier("jacobthechicken", "textures/entity/jacob.png");
  
  public JacobEntityRenderer(EntityRendererFactory.Context context) {
    super(context, new ChickenEntityModel<JacobEntity>(context.getPart(EntityModelLayers.CHICKEN)), 0.3F);
  }

  @Override
  public Identifier getTexture(JacobEntity entity) {
    return TEXTURE;
  }

   protected float getAnimationProgress(JacobEntity chickenEntity, float f) {
      float g = MathHelper.lerp(f, chickenEntity.prevFlapProgress, chickenEntity.flapProgress);
      float h = MathHelper.lerp(f, chickenEntity.prevMaxWingDeviation, chickenEntity.maxWingDeviation);
      return (MathHelper.sin(g) + 1.0F) * h;
   }
}
