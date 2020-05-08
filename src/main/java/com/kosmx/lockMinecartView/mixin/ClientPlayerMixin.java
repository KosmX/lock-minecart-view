package com.kosmx.lockMinecartView.mixin;

import com.kosmx.lockMinecartView.LoadMain;
import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.MinecartEntity;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerMixin extends AbstractClientPlayerEntity {

    public ClientPlayerMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tickRiding", at = @At("TAIL"))
    private void ridingTick(CallbackInfo info){
        Entity vehicle = this.getVehicle();
        if(vehicle instanceof MinecartEntity){
            MinecartEntity minecart = (MinecartEntity)vehicle;
            if(LoadMain.minecart == minecart){
                /*
                this.yaw += minecart.getYaw(1.0f) - LoadMain.yaw;
                this.setHeadYaw(this.yaw + minecart.getYaw(1.0f) - LoadMain.yaw);
                LoadMain.yaw = minecart.getYaw(1.0f);*/
                
                
                this.yaw = minecart.getYaw(1.0f);
                this.setHeadYaw(minecart.getYaw(1.0f));
                LoadMain.yaw = minecart.getYaw(1.0f);
                //net.minecraft.client.render.entity.MinecartEntityModel
                
            }
            else{
                LoadMain.minecart = minecart;
                LoadMain.yaw = minecart.getYaw(1.0f);
            }
        }
    }
    
}