package me.dragonsteam.bungeestaffs.managers;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.managers.hooks.LuckPermsHandler;
import me.dragonsteam.bungeestaffs.managers.hooks.RedisBungeeHandler;

import java.util.HashMap;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 10/11/2021 - 23:39.
 */
public class HookManager {

    private final HashMap<String, HookHandler> handlerHashMap, handlersLoaded;

    public HookManager() {
        handlerHashMap = new HashMap<>();
        handlersLoaded = new HashMap<>();

        // Register all the hooks
        handlerHashMap.put("LuckPerms", new LuckPermsHandler());
        handlerHashMap.put("RedisBungee", new RedisBungeeHandler());

        for (HookHandler handler : handlerHashMap.values()) {
            if (!handler.isLoaded()) continue;

            try {
                handler.setup();
                handlersLoaded.put(handler.getName(), handler);
                bStaffs.logger("&fLuckPerms provider &aregistered&f.", "Hooks");
            } catch (Exception e) {
                bStaffs.logger("&fError on &cregister &fLuckPerms provider.", "Hooks");
            }
        }
    }

    public HookHandler getHandler(String name) {
        if (handlersLoaded.containsKey(name)) return handlersLoaded.get(name);
        return null;
    }

}
