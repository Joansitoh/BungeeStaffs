package me.dragonsteam.bungeestaffs.listeners;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Chats;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.utils.CommandType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 09/11/2021 - 17:53.
 */
public class PlayerCompleteListener implements Listener {

    @EventHandler
    public void onTabComplete(TabCompleteEvent e) {
        List<String> commands = new ArrayList<>(Comms.getCommsHashMap().keySet());
        commands.addAll(bStaffs.INSTANCE.getExtraCommands());

        if (!e.getCursor().startsWith("/")) return;
        String input = e.getCursor().substring(1);

        // Get arguments with command.
        String command = input.split(" ")[0];
        String[] args = input.replace(command + " ", "").split(" ");

        if (!commands.contains(command)) {
            commands.stream().filter(s -> s.startsWith(command)).forEach(s -> e.getSuggestions().add("/" + s));
            return;
        }

        if (bStaffs.INSTANCE.getExtraCommands().contains(command)) {
            // Return if command not equals toggle or togglechat.
            if (!command.equalsIgnoreCase("toggle") && !command.equalsIgnoreCase("togglechat")) return;
            if (args.length == 1) {
                ArrayList<String> list = new ArrayList<>();

                if (command.equalsIgnoreCase("toggle")) {
                    Comms.getCommsHashMap().keySet().forEach(cmd -> {
                        if (cmd.toLowerCase().startsWith(args[0].toLowerCase()))
                            list.add(cmd);
                    });
                } else if (command.equalsIgnoreCase("togglechat")) {
                    Chats.getChatsHashMap().keySet().forEach(chat -> {
                        if (chat.startsWith(args[0]))
                            list.add(chat);
                    });
                }

                e.getSuggestions().addAll(list);
            }
            return;
        }

        Comms comms = Comms.getCommandByName(command);
        if (args.length == 0) {
            if (e.getSender() instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) e.getSender();
                if (player.getPendingConnection().getVersion() > 390) return;
                player.sendMessage(comms.getUsage());
            }
            return;
        }

        if (args.length == 1) {
            if (comms.getType().equals(CommandType.TARGET) || comms.getType().equals(CommandType.PRIVATE)) {
                ArrayList<String> players = new ArrayList<>();
                bStaffs.INSTANCE.getProxy().getPlayers().forEach(player -> {
                    if (player.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                        players.add(player.getName());
                });
                e.getSuggestions().addAll(players);
            }
        }
    }

}
