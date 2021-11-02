package me.dragonsteam.bungeestaffs.listeners;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.CommandType;
import me.dragonsteam.bungeestaffs.utils.PlayerCommandEvent;
import me.dragonsteam.bungeestaffs.utils.TimerUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 16:06.
 */
public class PlayerCommandListener implements Listener {

    private final ConfigFile config = bStaffs.INSTANCE.getCommandsFile();

    @EventHandler
    public void onPlayerChat(PlayerCommandEvent e) {
        ProxiedPlayer player = e.getSender();
        String[] args = e.getArgs();

        if (e.getCommand().equalsIgnoreCase("toggle")) return;
        Comms comms = Comms.getCommandByName(e.getCommand());

        if (!hasPermission(player, comms.getSendPermission())) {
            player.sendMessage(new TextComponent(Lang.NO_PERMISSION.toString()));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(new TextComponent(comms.getUsage()));
            return;
        }

        if (TimerUtils.hasCooldown(player, comms)) {
            player.sendMessage(new TextComponent(Lang.HAVE_COOLDOWN.toString()
                    .replace("<cooldown>", TimerUtils.getPlayerCooldown(player, comms) + "")
            ));
            return;
        }
        if (comms.getCooldown() != 0 && !player.hasPermission("bstaffs.bypass"))
            TimerUtils.setPlayerCooldown(player, comms, comms.getCooldown());
        StringBuilder builder = new StringBuilder();
        switch (comms.getType()) {
            case SOLO:
                player.sendMessage(new TextComponent(ChatUtils.translate(comms.getOutput())));
                for (String arg : args) builder.append(arg).append(" ");
                for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
                    if (!hasPermission(p, comms.getReceivePermission())) continue;
                    if (ChatUtils.isToggledCommand(p, comms)) continue;
                    p.sendMessage(new TextComponent(comms.getPlayerFormat(player, null, builder.toString())));
                }
                break;
            case TARGET:
                if (args.length == 1) {
                    player.sendMessage(new TextComponent(comms.getUsage()));
                    return;
                }

                ProxiedPlayer target = bStaffs.INSTANCE.getProxy().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(new TextComponent(Lang.PLAYER_NOT_FOUND.toString().replace("<target>", args[0])));
                    return;
                }

                player.sendMessage(new TextComponent(ChatUtils.translate(comms.getOutput())));
                for (int x = 1; x < args.length; x++) builder.append(args[x]).append(" ");
                for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
                    if (!hasPermission(p, comms.getReceivePermission())) continue;
                    if (ChatUtils.isToggledCommand(p, comms)) continue;
                    p.sendMessage(new TextComponent(comms.getPlayerFormat(player, target, builder.toString())));
                }
                break;
        }
    }

    private boolean hasPermission(ProxiedPlayer player, String permission) {
        if (permission.equalsIgnoreCase("")) return true;
        return player.hasPermission(permission);
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent e) {
        List<String> commands = new ArrayList<>(Comms.getCommsHashMap().keySet());
        List<String> extras = Arrays.asList(
                "stafflist", "toggle", "search"
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
