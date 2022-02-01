package me.dragonsteam.bungeestaffs.listeners;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
import me.dragonsteam.bungeestaffs.utils.PlayerCache;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import me.dragonsteam.bungeestaffs.utils.defaults.Runnables;
import me.dragonsteam.bungeestaffs.utils.formats.RedisMessageFormat;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 17:13.
 */
public class ServerMovementListener implements Listener {

    private final ConfigFile config = bStaffs.INSTANCE.getSettingsFile();
    private final String permission = config.getString("EVENTS.STAFFS.PERMISSION");

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {
        if (e.getPlayer().hasPermission("bstaffs.admin")) {
            if (config.getBoolean("EVENTS.JOIN-UPDATE-MESSAGE"))
                Runnables.runLater(() -> bStaffs.INSTANCE.update(e.getPlayer()), 3, TimeUnit.SECONDS);
        }

        if (!config.getBoolean("EVENTS.STAFFS.JOIN-MESSAGE")) return;
        ProxiedPlayer player = e.getPlayer();
        if (!player.hasPermission(permission)) return;

        PlayerCache playerCache = new PlayerCache(player);
        bStaffs.log(player, "move", bStaffHolder.getStaffHolderMessage(playerCache, LanguageHandler.STAFF_JOIN.toString()));
        RedisMessageFormat.sendMessage(RedisMessageFormat.MessageType.STAFF_MOVE, bStaffs.isRedisPresent(), permission, playerCache.toJson(), LanguageHandler.STAFF_JOIN.toString());
    }

    @EventHandler
    public void onChangeServer(PlayerDisconnectEvent e) {
        if (!config.getBoolean("EVENTS.STAFFS.LEFT-MESSAGE")) return;
        ProxiedPlayer player = e.getPlayer();
        if (!player.hasPermission(permission)) return;

        PlayerCache playerCache = new PlayerCache(player);
        bStaffs.log(player, "move", bStaffHolder.getStaffHolderMessage(playerCache, LanguageHandler.STAFF_LEFT.toString()));
        RedisMessageFormat.sendMessage(RedisMessageFormat.MessageType.STAFF_MOVE, bStaffs.isRedisPresent(), permission, playerCache.toJson(), LanguageHandler.STAFF_LEFT.toString());
    }

    @EventHandler
    public void onChangeServer(ServerSwitchEvent e) {
        if (!config.getBoolean("EVENTS.STAFFS.MOVE-MESSAGE")) return;
        ProxiedPlayer player = e.getPlayer();
        if (!player.hasPermission(permission)) return;

        PlayerCache playerCache = new PlayerCache(player);
        bStaffs.log(player, "move", bStaffHolder.getStaffHolderMessage(playerCache, LanguageHandler.STAFF_MOVE.toString()));
        RedisMessageFormat.sendMessage(RedisMessageFormat.MessageType.STAFF_MOVE, bStaffs.isRedisPresent(), permission, playerCache.toJson(), LanguageHandler.STAFF_MOVE.toString());
    }

    @EventHandler
    public void onServerFallback(ServerKickEvent e) {
        String fallback = bStaffs.INSTANCE.getRandomFallbackServer();
        if (fallback == null) return;

        if (config.getConfiguration().contains("SERVERS-CONFIG.KICK-MESSAGE-BLACKLIST")) {
            for (String s : config.getStringList("SERVERS-CONFIG.KICK-MESSAGE-BLACKLIST")) {
                if (e.getKickReason().contains(s)) return;
            }
        }

        if (e.getPlayer().getServer() == null) return;
        if (e.getPlayer().getServer().getInfo().getName().equalsIgnoreCase(fallback)) {
            for (String fall : bStaffs.INSTANCE.getFallbackServers()) {
                if (fall.equalsIgnoreCase(e.getPlayer().getServer().getInfo().getName())) continue;
                e.setCancelled(true);
                e.setCancelServer(ProxyServer.getInstance().getServerInfo(fall));
                return;
            }
            return;
        }

        e.setCancelServer(ProxyServer.getInstance().getServerInfo(fallback));
        e.setCancelled(true);
    }

}
