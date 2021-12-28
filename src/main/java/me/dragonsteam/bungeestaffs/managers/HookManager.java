package me.dragonsteam.bungeestaffs.managers;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.managers.hooks.LuckPermsHandler;

import java.util.HashMap;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 10/11/2021 - 23:39.
 */
public class HookManager {

    private final HashMap<String, HookHandler> handlerHashMap;

    public HookManager() {
        handlerHashMap = new HashMap<>();

        // Register all the hooks
        handlerHashMap.put("LuckPerms", new LuckPermsHandler());

        for (HookHandler handler : handlerHashMap.values()) {
            if (!handler.isLoaded()) continue;

            try {
                handler.setup();
                bStaffs.logger("&fLuckPerms provider &aregistered&f.", "Hooks");
            } catch (Exception e) {
                bStaffs.logger("&fError on &cregister &fLuckPerms provider.", "Hooks");
            }
        }
    }

    public HookHandler getHandler(String name) {
        if (handlerHashMap.containsKey(name) && handlerHashMap.get(name).isLoaded())
            return handlerHashMap.get(name);
        return null;
    }

}
