package me.dragonsteam.bungeestaffs.listeners;

import lombok.Getter;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Chats;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.CommandType;
import me.dragonsteam.bungeestaffs.utils.ConfigFile;
import me.dragonsteam.bungeestaffs.utils.TimerUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 16:06.
 */
public class PlayerCommandListener implements Listener {

    private final ConfigFile config = bStaffs.INSTANCE.getCommandsFile();

    @EventHandler
    public void onPlayerChat(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        if (!e.isCommand()) return;
        String command = e.getMessage().substring(1);
        String[] args = command.split(" ");

        Comms comms = null;
        for (String s : Comms.getCommsHashMap().keySet()) {
            if (s.equalsIgnoreCase(args[0])) comms = Comms.getCommandByName(s);
        }

        if (comms == null) return;
        e.setCancelled(true);
        if (!hasPermission(player, comms.getSendPermission())) {
            player.sendMessage(Lang.NO_PERMISSION.toString());
            return;
        }

        if (args.length == 1) {
            player.sendMessage(new TextComponent(comms.getUsage()));
            return;
        }

        if (TimerUtils.hasCooldown(player, comms)) {
            player.sendMessage(new TextComponent(ChatUtils.translate(Lang.HAVE_COOLDOWN.toString()
                    .replace("<cooldown>", TimerUtils.getPlayerCooldown(player, comms) + "")
            )));
            return;
        }

        player.sendMessage(new TextComponent(ChatUtils.translate(comms.getOutput())));
        if (comms.getCooldown() != 0 && !player.hasPermission("bstaffs.bypass")) TimerUtils.setPlayerCooldown(player, comms, comms.getCooldown());
        StringBuilder builder = new StringBuilder();
        switch (comms.getType()) {
            case SOLO:
                for (int x = 1; x < args.length; x++) builder.append(args[x]).append(" ");
                for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
                    if (!hasPermission(p, comms.getReceivePermission())) continue;
                    p.sendMessage(new TextComponent(comms.getPlayerFormat(player, null, builder.toString())));
                }
                break;
            case TARGET:
                if (args.length == 2) {
                    player.sendMessage(new TextComponent(comms.getUsage()));
                    return;
                }

                ProxiedPlayer target = bStaffs.INSTANCE.getProxy().getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(new TextComponent(Lang.PLAYER_NOT_FOUND.toString().replace("<target>", args[1])));
                    return;
                }

                //player.sendMessage(comms.get);
                for (int x = 2; x < args.length; x++) builder.append(args[x]).append(" ");
                for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
                    if (!hasPermission(p, comms.getReceivePermission())) continue;
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
        List<String> list = new ArrayList<>();
        if (!e.getCursor().startsWith("/")) return;
        String[] dargs = e.getCursor().substring(1).split(" ");
        String command = dargs[0];

        if (!commands.contains(command)) return;
        Comms comms = Comms.getCommandByName(command);
        String[] args = e.getCursor().substring(1).replace(command, "").split(" ");
        if (args.length == 0 || args.length == 1) {
            if (e.getSender() instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) e.getSender();
                player.sendMessage(ChatUtils.translate(comms.getUsage()));
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
