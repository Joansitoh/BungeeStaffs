package me.dragonsteam.bungeestaffs.listeners;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.PlayerCommandEvent;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 17:13.
 */
public class PlayerServerListeners implements Listener {

    private final ConfigFile config = bStaffs.INSTANCE.getSettingsFile();
    private final String permission = config.getString("EVENTS.STAFFS.PERMISSION");

    @EventHandler
    public void onPlayerChat(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();

        if (!e.isCommand()) return;
        String message = e.getMessage().substring(1);

        String[] firstArgs = message.split(" ");
        String command = firstArgs[0], arguments = message.replace(command + " ", "").replace(command, "");

        if (command.length() == 0) return;
        String[] finalArgs = arguments.split(" ");
        if (!Comms.getCommsHashMap().containsKey(command) && !command.equalsIgnoreCase("toggle") && !command.equalsIgnoreCase("togglechat"))
            return;

        e.setCancelled(true);

        PlayerCommandEvent event = new PlayerCommandEvent(player, command, arguments.equals("") ? new String[0] : finalArgs);
        bStaffs.INSTANCE.getProxy().getPluginManager().callEvent(event);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {
        if (!config.getBoolean("EVENTS.STAFFS.JOIN-MESSAGE")) return;
        ProxiedPlayer player = e.getPlayer();
        if (!player.hasPermission(permission)) return;
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (!p.hasPermission(permission)) continue;
            p.sendMessage(new TextComponent(bStaffHolder.getStaffHolder(p, Lang.STAFF_JOIN.toString())));
        }
    }

    @EventHandler
    public void onChangeServer(PlayerDisconnectEvent e) {
        if (!config.getBoolean("EVENTS.STAFFS.LEFT-MESSAGE")) return;
        ProxiedPlayer player = e.getPlayer();
        if (!player.hasPermission(permission)) return;
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (!p.hasPermission(permission)) continue;
            p.sendMessage(new TextComponent(bStaffHolder.getStaffHolder(p, Lang.STAFF_LEFT.toString())));
        }
    }

    @EventHandler
    public void onChangeServer(ServerSwitchEvent e) {
        if (!config.getBoolean("EVENTS.STAFFS.MOVE-MESSAGE")) return;
        ProxiedPlayer player = e.getPlayer();
        if (!player.hasPermission(permission)) return;

        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (!p.hasPermission(permission)) continue;
            p.sendMessage(new TextComponent(bStaffHolder.getStaffHolder(p, Lang.STAFF_MOVE.toString())));
        }
    }

}
