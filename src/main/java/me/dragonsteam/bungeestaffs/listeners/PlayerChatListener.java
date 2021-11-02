package me.dragonsteam.bungeestaffs.listeners;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Chats;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 16:06.
 */
public class PlayerChatListener implements Listener {

    @EventHandler
    public void onPlayerChat(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();

        Chats chats = null;
        for (String s : Chats.getChatsHashMap().keySet()) {
            if (e.getMessage().startsWith(s)) chats = Chats.getChatByInput(s);
        }

        if (chats == null) return;
        if (e.getMessage().substring(chats.getInput().length()).equalsIgnoreCase("")) return;
        if (!player.hasPermission(chats.getPermission())) return;
        e.setCancelled(true);

        for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
            if (!p.hasPermission(chats.getPermission())) continue;
            p.sendMessage(chats.getPlayerFormat(player, e.getMessage()));
        }
    }

}
