package me.dragonsteam.bungeestaffs.utils;

import lombok.Getter;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.managers.HookHandler;
import me.dragonsteam.bungeestaffs.managers.hooks.LuckPermsHandler;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

/**
 * Class for project BungeeStaffs
 * Date: 27/01/2022 - 20:58.
 *
 * @author Joansiitoh
 */
@Getter
public class PlayerCache {

    private static boolean tried = false;

    public String server, rawServer;
    public String name, prefix, suffix;

    public PlayerCache(String name, String server) {
        this.name = name;
        this.server = server;
        this.prefix = "";
        this.suffix = "";
        this.rawServer = server;
    }

    public PlayerCache(ProxiedPlayer player) {
        name = player.getName();
        prefix = "";
        suffix = "";

        ConfigFile config = bStaffs.INSTANCE.getSettingsFile();
        try {
            if (config.getBoolean("USE-BUNGEE-MOTD"))
                server = player.getServer().getInfo().getMotd();
            else server = player.getServer().getInfo().getName();

            rawServer = player.getServer().getInfo().getName();
        } catch (Exception ignore) {
            server = "Unknown";
            rawServer = "Unknown";
        }

        // Loading prefix and suffix using LuckPerms.
        try {
            HookHandler handler = bStaffs.INSTANCE.getHookManager().getHandler("LuckPerms");
            if (handler != null) {
                if (!handler.isLoaded() && !tried) {
                    tried = true;
                    handler.setup();
                }

                try {
                    LuckPermsHandler luckPermsHandler = (LuckPermsHandler) handler;
                    if (luckPermsHandler.getPrefix(player.getUniqueId()) != null)
                        prefix = luckPermsHandler.getPrefix(player.getUniqueId());

                    if (luckPermsHandler.getSuffix(player.getUniqueId()) != null)
                        suffix = luckPermsHandler.getSuffix(player.getUniqueId());
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            if (!tried) bStaffs.logger("LuckPerms hook is not installed or not working properly.");
        }
    }

    public PlayerCache(Document document) {
        name = document.getString("name");
        server = document.getString("server");
        prefix = document.getString("prefix");
        suffix = document.getString("suffix");
        rawServer = document.getString("rawServer");
    }

    public String toJson() {
        Document document = new Document();
        document.put("name", name);
        document.put("server", server);
        document.put("prefix", prefix);
        document.put("suffix", suffix);
        document.put("rawServer", rawServer);
        return document.toJson();
    }

}
