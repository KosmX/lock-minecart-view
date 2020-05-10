package com.kosmx.lockMinecartView.mixin;

import com.kosmx.lockMinecartView.LockViewClient;
import com.mojang.authlib.GameProfile;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.MinecartEntity;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerMixin extends AbstractClientPlayerEntity{

    public ClientPlayerMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickRiding", at = @At("TAIL"))
    private void ridingTick(CallbackInfo info){
        Entity vehicle = this.getVehicle();
        if(LockViewClient.enabled && vehicle instanceof MinecartEntity){
            MinecartEntity minecart = (MinecartEntity)vehicle;
            /*Using MinecartEntity.getYaw() is unusable, becouse it's not the minecart's yaw...
             *There is NO way in mc to get the minecart's yaw... 
             *I need to create any identifier method (from the speed)
             */
            if(LockViewClient.config.rollerCoasterMode && vehicle.getVelocity().lengthSquared()>0.000001f){
                LockViewClient.setMinecartDirection(minecart.getVelocity());
                //this.setHeadYaw(LockViewClient.yaw);       //only camera
                this.yaw = LockViewClient.yaw;
                this.bodyYaw = LockViewClient.yaw;    //Only client side
                //this.pitch = LockViewClient.pitch;
            }
            else{
                LockViewClient.smartCalc(minecart, this.yaw);
                this.yaw = LockViewClient.calcYaw(this.yaw);
                this.bodyYaw = LockViewClient.calcYaw(this.bodyYaw);
            }
            //this.lastRenderYaw += LockViewClient.correction;
            //this.renderYaw += LockViewClient.correction;
            //this.sendMovementPackets();
        }
    }

    @Inject(method = "startRiding", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/client/MinecraftClient;getSoundManager()Lnet/minecraft/client/sound/SoundManager;"))
    private void startRidingInject(Entity entity, boolean force, CallbackInfoReturnable<Object> info){
        //net.minecraft.client.network.ClientPlayerEntity
        LockViewClient.log(Level.INFO, "entering minecart");
        LockViewClient.onStartRiding();
    }
    
}