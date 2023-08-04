package com.snoeyz;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class JacobEntity extends TameableEntity {
  private static final Ingredient BREEDING_INGREDIENT;

  public float flapProgress;
  public float maxWingDeviation;
  public float prevMaxWingDeviation;
  public float prevFlapProgress;
  public float flapSpeed = 1.0F;
  private float field_28639 = 1.0F;

  public JacobEntity(EntityType<? extends JacobEntity> entityType, World world) {
    super(entityType, world);
    this.setTamed(false);
    this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    this.initGoals();
  }

  protected void initGoals() {
    this.goalSelector.add(0, new SwimGoal(this));
    this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4));
    this.goalSelector.add(2, new FollowOwnerGoal(this, 2.0, 10.0F, 2.0F, false));
    this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
    this.goalSelector.add(4, new TemptGoal(this, 1.0, BREEDING_INGREDIENT, false));
    this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
    this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
    this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
    this.goalSelector.add(8, new LookAroundGoal(this));
  }

  public static DefaultAttributeContainer.Builder createJacobAttributes() {
    return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
  }

  protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
    return this.isBaby() ? dimensions.height * 0.85F : dimensions.height * 0.92F;
  }

  public void tickMovement() {
    super.tickMovement();
    this.prevFlapProgress = this.flapProgress;
    this.prevMaxWingDeviation = this.maxWingDeviation;
    this.maxWingDeviation += (this.isOnGround() ? -1.0F : 4.0F) * 0.3F;
    this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
    if (!this.isOnGround() && this.flapSpeed < 1.0F) {
      this.flapSpeed = 1.0F;
    }

    this.flapSpeed *= 0.9F;
    Vec3d vec3d = this.getVelocity();
    if (!this.isOnGround() && vec3d.y < 0.0) {
      this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
    }

    this.flapProgress += this.flapSpeed * 2.0F;

   }

  protected boolean isFlappingWings() {
    return this.speed > this.field_28639;
  }

  protected void addFlapEffects() {
    this.field_28639 = this.speed + this.maxWingDeviation / 2.0F;
  }

  protected SoundEvent getAmbientSound() {
    return SoundEvents.ENTITY_CHICKEN_AMBIENT;
  }

  protected SoundEvent getHurtSound(DamageSource source) {
    return SoundEvents.ENTITY_CHICKEN_HURT;
  }

  protected SoundEvent getDeathSound() {
    return SoundEvents.ENTITY_CHICKEN_DEATH;
  }

  protected void playStepSound(BlockPos pos, BlockState state) {
    this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
  }

  @Override
  public EntityView method_48926() {
    return getWorld();
  }

  @Override
  public PassiveEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
    return (ChickenEntity)EntityType.CHICKEN.create(serverWorld);
  }

   public static boolean canSpawn(EntityType<JacobEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
    return world.getBlockState(pos.down()).isIn(BlockTags.ANIMALS_SPAWNABLE_ON) && isLightLevelValidForNaturalSpawn(world, pos);
   }

  static {
    BREEDING_INGREDIENT = Ingredient.ofItems(new ItemConvertible[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD});
  }
}
