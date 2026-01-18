package com.quarri6343.hwaila;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.semver.SemverRange;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class CustomHUDUtil {

    private static Class<?> multipleCustomUIHudClass;
    private static Method getCustomHudsMethod;

    //get methods across classloader
    static {
        if (PluginManager.get().hasPlugin(PluginIdentifier.fromString("Buuz135:MultipleHUD"), SemverRange.WILDCARD)) {
            Class<?> multipleHudClass = PluginManager.get()
                    .getPlugin(PluginIdentifier.fromString("Buuz135:MultipleHUD"))
                    .getClass();
            ClassLoader thirdPartyLoader = multipleHudClass.getClassLoader();

            try {
                multipleCustomUIHudClass = Class.forName("com.buuz135.mhud.MultipleCustomUIHud", true, thirdPartyLoader);
                getCustomHudsMethod = multipleCustomUIHudClass.getMethod("getCustomHuds");
                getCustomHudsMethod.setAccessible(true);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                HWaila.getInstance().getLogger().atWarning().log("MHUD version mismatch!");
                throw new RuntimeException();
            }
        }
    }

    public static void setCustomHUD(Player player, PlayerRef playerRef, String multipleHUDIdentifier, CustomUIHud hud) {
        if (PluginManager.get().hasPlugin(PluginIdentifier.fromString("Buuz135:MultipleHUD"), SemverRange.WILDCARD)) {
            MultipleHUD.getInstance().setCustomHud(player, playerRef, multipleHUDIdentifier, hud);
        }
        else {
            HudManager hudManager = player.getHudManager();
            hudManager.setCustomHud(playerRef, hud);
        }
    }

    /**
     *
     * @return current customhud instance. It can be any class regardless of identifier if MultipleHUD is not installed
     */
    @Nullable
    public static CustomUIHud getCustomHUD(Player player, String multipleHUDIdentifier) {
        HudManager hudManager = player.getHudManager();
        if (PluginManager.get().hasPlugin(PluginIdentifier.fromString("Buuz135:MultipleHUD"), SemverRange.WILDCARD)) {
            if (multipleCustomUIHudClass != null && multipleCustomUIHudClass.isInstance(hudManager.getCustomHud())
                && getCustomHudsMethod != null) {
                try {
                    HashMap<String, CustomUIHud> huds = (HashMap<String, CustomUIHud>) getCustomHudsMethod.invoke(hudManager.getCustomHud());
                    return huds.get(multipleHUDIdentifier);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    HWaila.getInstance().getLogger().atWarning().log("MHUD error!");
                    throw new RuntimeException();
                }
            }
            else {
                return null;
            }
        }
        else {
            return hudManager.getCustomHud();
        }
    }
}
