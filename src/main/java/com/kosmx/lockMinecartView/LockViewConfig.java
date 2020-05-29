package com.kosmx.lockMinecartView;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

@Config(name = "lock_minecart_view")
public class LockViewConfig implements ConfigData{

    @ConfigEntry.Gui.Tooltip
    public boolean smoothMode = true;

    @ConfigEntry.Gui.Tooltip
    public boolean smartMode = true;

    @ConfigEntry.BoundedDiscrete(min = 0, max = 80)
    @ConfigEntry.Gui.Tooltip
    public int threshold = 8;

    @ConfigEntry.Gui.Tooltip
    public boolean rollerCoasterMode = false;

    @ConfigEntry.Gui.Tooltip
    public boolean enabled = true;

    @ConfigEntry.Gui.Tooltip
    public boolean showDebug = false;

}