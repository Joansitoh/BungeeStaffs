package me.dragonsteam.bungeestaffs.listeners.server;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Chats;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.utils.CommandType;
import me.dragonsteam.bungeestaffs.utils.PlayerCommandEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerCommandListener implements Listener {

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
        List<String> commands = new ArrayList<>(Comms.getCommsHashMap().keySet());
        commands.addAll(bStaffs.INSTANCE.getExtraCommands());

        if (!commands.contains(command)) return;
        // Return if extra command not equals toggle or togglechat.
        if (bStaffs.INSTANCE.getExtraCommands().contains(command) && !command.equalsIgnoreCase("toggle") && !command.equalsIgnoreCase("togglechat")) return;
        e.setCancelled(true);

        System.out.println(player.getName() + " has execute command: " + e.getMessage());
        PlayerCommandEvent event = new PlayerCommandEvent(player, command, arguments.equals("") ? new String[0] : finalArgs);
        bStaffs.INSTANCE.getProxy().getPluginManager().callEvent(event);
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent e) {
        List<String> commands = new ArrayList<>(Comms.getCommsHashMap().keySet());
        commands.addAll(bStaffs.INSTANCE.getExtraCommands());

        if (!e.getCursor().startsWith("/")) return;
        String[] dargs = e.getCursor().substring(1).split(" ");
        String command = dargs[0];

        String[] args = e.getCursor().substring(1).replace(command, "").split(" ");

        if (!commands.contains(command)) {
            commands.stream().filter(s -> s.startsWith(command)).forEach(s -> e.getSuggestions().add("/" + s));
            return;
        }

        if (bStaffs.INSTANCE.getExtraCommands().contains(command)) {
            // Return if command not equals toggle or togglechat.
            if (!command.equalsIgnoreCase("toggle") && !command.equalsIgnoreCase("togglechat")) return;
            if (args.length == 2) {
                ArrayList<String> list = new ArrayList<>();

                if (command.equalsIgnoreCase("toggle")) {
                    Comms.getCommsHashMap().keySet().forEach(cmd -> {
                        if (cmd.toLowerCase().startsWith(args[1].toLowerCase()))
                            list.add(cmd);
                    });
                } else if (command.equalsIgnoreCase("togglechat")) {
                    Chats.getChatsHashMap().keySet().forEach(chat -> {
                        if (chat.startsWith(args[1]))
                            list.add(chat);
                    });
                }

                e.getSuggestions().addAll(list);
            }
            return;
        }

        Comms comms = Comms.getCommandByName(command);
        if (args.length == 0 || args.length == 1) {
            if (e.getSender() instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) e.getSender();
                if (player.getPendingConnection().getVersion() > 390) return;
                player.sendMessage(comms.getUsage());
            }
            return;
        }

        if (args.length == 2) {
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
