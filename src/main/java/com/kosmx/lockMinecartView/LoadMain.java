package com.kosmx.lockMinecartView;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.vehicle.MinecartEntity;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadMain implements ClientModInitializer {

    //-------------system variables--------------------
    public static Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "lock_minecart_view";
    public static final String MOD_NAME = "Lock Minecart view";
    //-------------calculating vars--------------------
    
    @Nullable
    public static MinecartEntity minecart = null;
    public static float yaw = 0f;

    //-------------control key variables---------------
    

    @Override
    public void onInitializeClient() {
        log(Level.INFO, "Initializing");
        //TODO: Initializer
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

}