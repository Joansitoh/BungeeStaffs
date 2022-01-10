package me.dragonsteam.bungeestaffs.commands;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.CommandHandler;
import me.dragonsteam.bungeestaffs.loaders.DiscordHandler;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
import me.dragonsteam.bungeestaffs.utils.CommandType;
import me.dragonsteam.bungeestaffs.utils.TimerUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ToggleUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 09/11/2021 - 17:41.
 */
public class cCommand extends Command implements TabExecutor {

    private final CommandHandler comms;

    public cCommand(CommandHandler comms) {
        super(comms.getCommand(), comms.getSendPermission(), comms.getAliases().toArray(new String[0]));
        this.comms = comms;

        // Register command and aliases.
        bStaffs.INSTANCE.getKnownCommands().put(comms.getCommand(), this);
        for (String alias : comms.getAliases())
            bStaffs.INSTANCE.getKnownCommands().put(alias, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Check if sender is a player.
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;

            if (args.length == 0) {
                player.sendMessage(new TextComponent(comms.getUsage()));
                return;
            }

            // Check if player has cooldown.
            if (TimerUtils.hasCooldown(player, comms)) {
                player.sendMessage(new TextComponent(LanguageHandler.HAVE_COOLDOWN.toString()
                        .replace("<cooldown>", TimerUtils.getPlayerCooldown(player, comms) + "")
                ));
                return;
            }

            StringBuilder builder = new StringBuilder();
            if (comms.getType().equals(CommandType.SOLO)) {
                setCooldown(player, comms);
                player.sendMessage(new TextComponent(ChatUtils.translate(comms.getOutput())));
                for (String arg : args) builder.append(arg).append(" ");

                String result = builder.toString();
                HashMap<String, String> map = bStaffHolder.getLinedArguments(result);

                if (player.hasPermission("bstaffs.staff") && comms.isAdministrative())
                    result = modify(result, false, map);

                sendDiscordMessage(player, null, result, map);
                for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
                    if (!hasPerm(p, comms.getReceivePermission())) continue;
                    if (ToggleUtils.isToggledCommand(p, comms)) continue;
                    p.sendMessage(comms.getPlayerFormat(player, null, builder.toString()));
                }
                return;
            }

            if (comms.getType().equals(CommandType.TARGET) || comms.getType().equals(CommandType.PRIVATE)) {
                if (args.length == 1) {
                    player.sendMessage(new TextComponent(comms.getUsage()));
                    return;
                }

                ProxiedPlayer target = bStaffs.INSTANCE.getProxy().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(new TextComponent(LanguageHandler.PLAYER_NOT_FOUND.toString().replace("<target>", args[0])));
                    return;
                }

                setCooldown(player, comms);
                for (int x = 1; x < args.length; x++) builder.append(args[x]).append(" ");
                player.sendMessage(ChatUtils.translate(comms.getOutput())
                        .replace("<message>", builder.toString())
                        .replace("<target>", target.getName())
                        .replace("<player>", player.getName())
                );

                String result = builder.toString();
                HashMap<String, String> map = bStaffHolder.getLinedArguments(result);

                if (player.hasPermission("bstaffs.staff") && comms.isAdministrative())
                    result = modify(result, false, map);

                sendDiscordMessage(player, target, result, map);

                if (comms.getType() == CommandType.PRIVATE) {
                    if (ToggleUtils.isToggledCommand(target, comms)) return;
                    target.sendMessage(comms.getPlayerFormat(player, target, result));
                    return;
                }

                for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
                    if (!hasPerm(p, comms.getReceivePermission())) continue;
                    if (ToggleUtils.isToggledCommand(p, comms)) continue;
                    p.sendMessage(comms.getPlayerFormat(player, target, result));
                }
                return;
            }
        }
    }

    public String modify(String message, boolean replace, HashMap<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (replace) message = message.replace("<" + entry.getKey().charAt(1) + "-arg>", entry.getValue());
            else {
                message = message.replace("<" + entry.getKey().charAt(1) + "-arg>", "").replace(entry.getValue(), "");
                message = message.replace(entry.getKey(), "");
            }
        }

        return message;
    }

    public void sendDiscordMessage(ProxiedPlayer player, ProxiedPlayer target, String message, HashMap<String, String> map) {
        if (bStaffs.INSTANCE.getJda() != null) {
            try {
                DiscordHandler handler = comms.getDiscordHandler();
                TextChannel textChannel = bStaffs.INSTANCE.getJda().getTextChannelById(handler.getChannel());
                if (textChannel != null) {
                    String format = handler.getFormat();
                    format = format
                            .replace("<message>", message)
                            .replace("<target>", target == null ? "Console" : target.getName())
                            .replace("<player>", player.getName())
                    ;

                    if (comms.isAdministrative())
                        format = modify(format, true, map);

                    if (handler.isEmbedEnabled()) {
                        textChannel.sendMessageEmbeds(handler.build(format)).queue();
                    } else textChannel.sendMessage(format).queue();
                }
            } catch (Exception ignored) {
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private static void setCooldown(ProxiedPlayer player, CommandHandler comms) {
        // Set cooldown if player don't have bypass.
        if (comms.getCooldown() != 0 && !player.hasPermission("bstaffs.bypass"))
            TimerUtils.setPlayerCooldown(player, comms, comms.getCooldown());
    }

    private static boolean hasPerm(ProxiedPlayer player, String permission) {
        if (permission.equalsIgnoreCase("")) return true;
        return player.hasPermission(permission);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!comms.hasPermission(sender)) return Collections.emptyList();
        if (args.length == 1) {
            if (comms.getType().equals(CommandType.TARGET) || comms.getType().equals(CommandType.PRIVATE)) {
                ArrayList<String> players = new ArrayList<>();
                bStaffs.INSTANCE.getProxy().getPlayers().forEach(onlinePlayer -> {
                    if (onlinePlayer.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                        players.add(onlinePlayer.getName());
                });
                return players;
            }
        }

        return Collections.emptyList();
    }
}
