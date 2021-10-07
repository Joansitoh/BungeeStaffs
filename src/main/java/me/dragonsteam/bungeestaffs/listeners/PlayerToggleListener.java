package me.dragonsteam.bungeestaffs.listeners;

import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.PlayerCommandEvent;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
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
        }
    }

    private boolean hasPermission(ProxiedPlayer player, String permission) {
        if (permission.equalsIgnoreCase("")) return true;
        return player.hasPermission(permission);
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent e) {
        if (!e.getCursor().startsWith("/")) return;
        String[] dargs = e.getCursor().substring(1).split(" ");
        String command = dargs[0];

        if (!command.equalsIgnoreCase("toggle")) return;
        String[] args = e.getCursor().substring(1).replace(command, "").split(" ");
        if (args.length == 0) {
            if (e.getSender() instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) e.getSender();
                player.sendMessage(ChatUtils.translate("&cUsage: &f/toggle <command>"));
            }
            return;
        }

        if (args.length == 2) {
            ArrayList<String> list = new ArrayList<>();
            Comms.getCommsHashMap().keySet().forEach(cmd -> {
                if (cmd.toLowerCase().startsWith(args[1].toLowerCase()))
                    list.add(cmd);
            });

            e.getSuggestions().addAll(list);
        }
    }
}
