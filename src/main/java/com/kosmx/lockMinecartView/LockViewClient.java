package com.kosmx.lockMinecartView;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.GsonConfigSerializer;

public class LockViewClient implements ClientModInitializer {

    //-------------system variables--------------------
    private static boolean isHeld = false;
    private static FabricKeyBinding keyBinding;
    public static LockViewConfig config;
    public static boolean enabled = true;
    public static Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "lock_minecart_view";
    public static final String MOD_NAME = "Lock Minecart view";
    //-------------calculating vars--------------------
    public static float yaw = 0f;
    //public static float pitch = 0f;
    private static boolean doCorrection;
    @Nullable
    private static Vec3d lastCoord = null;
    private static float lastYaw = 0f;
    private static float rawLastYaw;
    private static float rawYaw;
    public static boolean isFollowingDirection = false;
    public static int tickAfterLastFollow = 0;
    private static float difference;

    //-------------methods-----------------------------

    public static float sphericalFromVec3d(Vec3d vec3d){
        //float f = MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d));
        float yawF = (float)(MathHelper.atan2(-vec3d.x, vec3d.z) * 57.2957763671875D);
        //pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 57.2957763671875D);
        return yawF;
    }

    public static void onStartRiding(){
        lastCoord = null;
        tickAfterLastFollow = 100;
    }

    public static void setMinecartDirection(float yawF){
        if (config.smoothMode){
            if(tickAfterLastFollow > config.treshold){
                LockViewClient.yaw = yawF;
            }
            else if(doCorrection){
                LockViewClient.yaw = normalize(LockViewClient.yaw + 180f);
            }
            if (Math.abs(yawF - LockViewClient.yaw) < 180f){
                LockViewClient.yaw = LockViewClient.yaw/2 + yawF/2;
            }
            else{
                float tmp = LockViewClient.yaw/2 + yawF/2;
                //log(Level.INFO, Float.toString(LockViewClient.yaw));
                LockViewClient.yaw = (tmp >= 0) ? tmp - 180f : tmp + 180f;
            }
            //log(Level.INFO, Float.toString(LockViewClient.yaw));
        }
        else{
            LockViewClient.yaw = yawF;
        }
    }

    public static void setMinecartDirection(MinecartEntity minecart){
        float yawF = sphericalFromVec3d(minecart.getVelocity());
        LockViewClient.rawLastYaw = LockViewClient.rawYaw;
        LockViewClient.rawYaw = yawF;
        checkSmartCorrection(minecart);
        setMinecartDirection(yawF);
    }

    public static void setMinecartDirection(Vec3d vec3d){
        setMinecartDirection(sphericalFromVec3d(vec3d));
    }

    public static void smartCalc(MinecartEntity minecart, Float yaw){
        LockViewClient.lastYaw = LockViewClient.yaw;
        boolean update = false;
        if (minecart.getVelocity().lengthSquared()>0.000001f){
            update = true;
            setMinecartDirection(minecart);
            //log(Level.INFO, Float.toString(LockViewClient.yaw - LockViewClient.lastYaw));
        }
        if ((int)LockViewClient.tickAfterLastFollow++ > config.treshold){
            LockViewClient.lastYaw = LockViewClient.yaw;
            log(Level.INFO, "clear rotation" + Integer.toString(tickAfterLastFollow) + " : " + Boolean.toString(update));
        }
        else if(doCorrection){
            LockViewClient.lastYaw = normalize(LockViewClient.lastYaw + 180f);
            log(Level.INFO, "do smart correction");
        }
        doCorrection = false;
        if(update) LockViewClient.tickAfterLastFollow = 0;
        LockViewClient.difference = LockViewClient.yaw - LockViewClient.lastYaw;
    }

    private static boolean checkSmartCorrection(MinecartEntity minecart){
        boolean correction = false;
        if(config.smartMode){
            if (Math.abs(LockViewClient.rawLastYaw - LockViewClient.rawYaw) > 135f && Math.abs(LockViewClient.rawLastYaw - LockViewClient.rawYaw)<225){
                correction = true;
            }
            Vec3d vec3d = minecart.getPos();
            if(lastCoord != null){
                Vec3d velocity = new Vec3d(vec3d.x - lastCoord.x, 0, vec3d.z - lastCoord.z);
                log(Level.INFO, Double.toString(velocity.normalize().dotProduct(minecart.getVelocity().normalize())));
                if(
                    velocity.lengthSquared() > 0.000008f &&
                    Math.abs(velocity.normalize().dotProduct(minecart.getVelocity().normalize())) < 0.5   //vectors dot product ~0, if vectors are ~perpendicular to each other
                ){
                    correction = false;
                }
            }
            lastCoord = vec3d;
        }
        return LockViewClient.doCorrection = correction;
    }

    public static float calcYaw(float entityYaw){
        //log(Level.INFO, Float.toString(LockViewClient.difference));
        return normalize(entityYaw + LockViewClient.difference);
    }

    private static float normalize(Float f){
        return (Math.abs(f) > 180f) ? (f < 0) ? f + 360f : f-360f : f;
    }


    //-------------control key variables---------------
    

    @Override
    public void onInitializeClient() {
        log(Level.INFO, "Initializing");
        AutoConfig.register(LockViewConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(LockViewConfig.class).getConfig();
        keyBinding = FabricKeyBinding.Builder.create(
            new Identifier(MOD_ID, "toggle"), 
            net.minecraft.client.util.InputUtil.Type.KEYSYM, 
            GLFW.GLFW_KEY_F7, 
            MOD_NAME
        ).build();
        KeyBindingRegistry.INSTANCE.addCategory(MOD_NAME);
        KeyBindingRegistry.INSTANCE.register(keyBinding);
        ClientTickCallback.EVENT.register(e ->
        {
            if (keyBinding.isPressed()){
                if(isHeld)return;
                isHeld = true;
                enabled = !enabled;
            }
            else if (isHeld){
                isHeld = false;
            }
        });
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }


}