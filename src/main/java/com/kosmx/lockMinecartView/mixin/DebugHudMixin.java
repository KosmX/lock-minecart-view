package com.kosmx.lockMinecartView.mixin;

import com.kosmx.lockMinecartView.LockViewClient;
import com.kosmx.lockMinecartView.LockViewConfig;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
@Mixin(DebugHud.class)
public class DebugHudMixin extends DrawableHelper {

    @Inject(method="getLeftText", at = @At("RETURN"), cancellable = true)
    protected void getLeftText(CallbackInfoReturnable<List<String>> info){
        if (LockViewClient.config.showDebug) {
            List<String> list = info.getReturnValue();
            list.addAll(LockViewClient.getDebug());
            info.setReturnValue(list);
        }
    }
}
