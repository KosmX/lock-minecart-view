package com.kosmx.lockMinecartView.mixin;

import com.kosmx.lockMinecartView.LockViewClient;
import com.mojang.authlib.GameProfile;

import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
public abstract class ClientPlayerMixin extends AbstractClientPlayerEntity{

    @Shadow public abstract float getYaw(float tickDelta);

    public ClientPlayerMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickRiding", at = @At("TAIL"))
    private void ridingTick(CallbackInfo info){
        Entity vehicle = this.getVehicle();
        if(LockViewClient.enabled && vehicle instanceof MinecartEntity){
            /*Using MinecartEntity.getYaw() is unusable, because it's not the minecart's yaw... I don't know what @!& is it
             *There is NO method in mc to get the minecart's real yaw...
             *I need to create my own identifier method (from the speed)
             */
            LockViewClient.update((MinecartEntity)vehicle);
            this.setYaw(LockViewClient.calcYaw(this.getYaw()));
            this.bodyYaw = LockViewClient.calcYaw(this.bodyYaw);
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