package me.dragonsteam.bungeestaffs.listeners.player;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Chats;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.PlayerCommandEvent;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 16:06.
 */
public class PlayerToggleListener implements Listener {

    @EventHandler
    public void onPlayerCommand(PlayerCommandEvent e) {
        ProxiedPlayer player = e.getSender();
        String[] args = e.getArgs();

        if (e.getCommand().equalsIgnoreCase("toggle")) {
            if (args.length == 1) {
                Comms comms = null;
                for (String s : Comms.getCommsHashMap().keySet()) {
                    if (s.equalsIgnoreCase(args[0])) comms = Comms.getCommandByName(s);
                }

                if (comms == null) {
                    player.sendMessage(new TextComponent(Lang.COMMAND_NOT_FOUND.toString()));
                    return;
                }

                if (!hasPermission(player, comms.getTogglePermission())) {
                    player.sendMessage(new TextComponent(Lang.NO_PERMISSION.toString()));
                    return;
                }

                ChatUtils.togglePlayerCommand(player, comms);
                player.sendMessage(new TextComponent(Lang.COMMAND_TOGGLED.toString()
                        .replace("<command>", comms.getCommand())
                        .replace("<value>", ChatUtils.isToggledCommand(player, comms) ? Lang.BOOLEAN_FALSE.toString() : Lang.BOOLEAN_TRUE.toString())
                ));
                return;
            }

            player.sendMessage(ChatUtils.translate("&cUsage: &f/toggle <command>"));
            return;
        }

        if (e.getCommand().equalsIgnoreCase("togglechat")) {
            if (args.length == 1) {
                Chats chats = null;
                for (String s : Chats.getChatsHashMap().keySet()) {
                    if (s.equalsIgnoreCase(args[0])) chats = Chats.getChatByInput(s);
                }

                if (chats == null) {
                    player.sendMessage(new TextComponent(Lang.CHAT_NOT_FOUND.toString()));
                    return;
                }

                ConfigFile file = bStaffs.INSTANCE.getChatsFile();
                ChatUtils.setDefaultIfNotSet(file.getConfiguration(), "TOGGLE-CHAT-PERMISSION", "bstaffs.togglechat");
                file.save();

                if (!hasPermission(player, bStaffs.INSTANCE.getChatsFile().getString("TOGGLE-CHAT-PERMISSION"))) {
                    player.sendMessage(new TextComponent(Lang.NO_PERMISSION.toString()));
                    return;
                }

                ChatUtils.togglePlayerChat(player, chats);
                player.sendMessage(new TextComponent(Lang.CHAT_TOGGLED.toString()
                        .replace("<chat>", chats.getInput())
                        .replace("<value>", ChatUtils.isToggledChat(player, chats) ? Lang.BOOLEAN_FALSE.toString() : Lang.BOOLEAN_TRUE.toString())
                ));
                return;
            }

            player.sendMessage(ChatUtils.translate("&cUsage: &f/togglechat <input>"));
        }
    }

    private boolean hasPermission(ProxiedPlayer player, String permission) {
        if (permission.equalsIgnoreCase("")) return true;
        return player.hasPermission(permission);
    }

}
