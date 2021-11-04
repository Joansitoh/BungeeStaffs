package me.dragonsteam.bungeestaffs.listeners;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.utils.CommandType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 03/11/2021 - 3:53.
 */
public class PlayerTabComepleListener implements Listener {

    @EventHandler
    public void onTabComplete(TabCompleteEvent e) {
        List<String> commands = new ArrayList<>(Comms.getCommsHashMap().keySet());
        List<String> extras = Arrays.asList(
                "stafflist", "toggle", "search", "togglechat"
        );
        commands.addAll(extras);

        List<String> list = new ArrayList<>();
        if (!e.getCursor().startsWith("/")) return;
        String[] dargs = e.getCursor().substring(1).split(" ");
        String command = dargs[0];

        if (!commands.contains(command)) {
            commands.stream().filter(s -> s.startsWith(command)).forEach(s -> e.getSuggestions().add("/" + s));
            return;
        }

        if (extras.contains(command)) return;

        Comms comms = Comms.getCommandByName(command);
        String[] args = e.getCursor().substring(1).replace(command, "").split(" ");
        if (args.length == 0 || args.length == 1) {
            if (e.getSender() instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) e.getSender();
                if (player.getPendingConnection().getVersion() > 390) return;
                player.sendMessage(comms.getUsage());
            }
            return;
        }

        if (args.length == 2) {
            if (comms.getType().equals(CommandType.TARGET)) {
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
