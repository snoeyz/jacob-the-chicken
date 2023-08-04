package com.snoeyz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.snoeyz.JacobEntity;
import com.snoeyz.JacobMod;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

@Mixin(NameTagItem.class)
public class NameTagItemMixin {
  
  @Inject(method = "useOnEntity(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", at = @At(value = "RETURN", ordinal = 0))
  private void useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand, CallbackInfoReturnable<ActionResult> callbackInfoReturnable) {
    if (entity instanceof ChickenEntity
      && stack.getName().getString().equalsIgnoreCase("jacob")
      && !player.getWorld().isClient) {
        World world = player.getWorld();
        JacobEntity jacob = new JacobEntity(JacobMod.JACOB, world);
        ChickenEntity chicken = (ChickenEntity)entity;
        jacob.setPosition(chicken.getPos());
        jacob.setBodyYaw(chicken.bodyYaw);
        jacob.setHeadYaw(chicken.headYaw);
        jacob.setPitch(chicken.getPitch());
        jacob.setYaw(chicken.getYaw());
        jacob.flapProgress = chicken.flapProgress;
        jacob.maxWingDeviation = chicken.maxWingDeviation;
        jacob.prevFlapProgress = chicken.prevFlapProgress;
        jacob.prevMaxWingDeviation = chicken.prevMaxWingDeviation;
        // jacob.setPose(entity.getPose());
        jacob.setCustomName(stack.getName());
        jacob.setTamed(true);
        jacob.setPersistent();
        jacob.setOwner(player);
        world.spawnEntity(jacob);
        chicken.remove(RemovalReason.DISCARDED);
    }
  }
}
