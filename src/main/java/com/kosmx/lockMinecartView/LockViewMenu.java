package com.kosmx.lockMinecartView;

import java.util.Optional;
import java.util.function.Supplier;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

public class LockViewMenu implements ModMenuApi{

    @Override
    public String getModId() {
        return "lock_minecart_view";
    }

    @Override
    public Optional<Supplier<Screen>> getConfigScreen(Screen screen){
        return Optional.of(AutoConfig.getConfigScreen(LockViewConfig.class, screen));
    }
    
}