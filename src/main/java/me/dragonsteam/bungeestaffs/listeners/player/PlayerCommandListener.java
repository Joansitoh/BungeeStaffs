package me.dragonsteam.bungeestaffs.listeners.player;

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

        if (bStaffs.INSTANCE.getExtraCommands().contains(e.getCommand())) return;
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
            case PRIVATE:
                if (args.length == 1) {
                    player.sendMessage(new TextComponent(comms.getUsage()));
                    return;
                }

                ProxiedPlayer target = bStaffs.INSTANCE.getProxy().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(new TextComponent(Lang.PLAYER_NOT_FOUND.toString().replace("<target>", args[0])));
                    return;
                }

                for (int x = 1; x < args.length; x++) builder.append(args[x]).append(" ");
                player.sendMessage(ChatUtils.translate(comms.getOutput())
                        .replace("<message>", builder.toString())
                        .replace("<target>", target.getName())
                        .replace("<player>", player.getName())
                );

                if (comms.getType() == CommandType.PRIVATE) {
                    if (ChatUtils.isToggledCommand(target, comms)) return;
                    target.sendMessage(comms.getPlayerFormat(player, target, builder.toString()));
                    return;
                }

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

}
