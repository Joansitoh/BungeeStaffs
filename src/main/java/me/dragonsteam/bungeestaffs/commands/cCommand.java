package me.dragonsteam.bungeestaffs.commands;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.CommandHandler;
import me.dragonsteam.bungeestaffs.loaders.DiscordHandler;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
import me.dragonsteam.bungeestaffs.utils.CommandType;
import me.dragonsteam.bungeestaffs.utils.PlayerCache;
import me.dragonsteam.bungeestaffs.utils.TimerUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.formats.RedisMessageFormat;
import net.dv8tion.jda.api.entities.TextChannel;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 09/11/2021 - 17:41.
 */
public class cCommand extends Command implements TabExecutor {

    private final CommandHandler comms;

    public static final HashMap<String, String> lastTarget = new HashMap<>();

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
            PlayerCache playerCache = new PlayerCache(player);

            if (args.length == 0) {
                if (comms.getType().equals(CommandType.CONSOLE)) {
                    setCooldown(player, comms);
                    player.sendMessage(bStaffHolder.getStaffHolder(playerCache, player, comms.getOutput(), "message"));
                    return;
                }

                player.sendMessage(bStaffHolder.getStaffHolder(playerCache, player, comms.getUsage(), null));
                return;
            }

            // Check if player has cooldown.
            if (TimerUtils.hasCooldown(player, comms)) {
                player.sendMessage(new TextComponent(LanguageHandler.HAVE_COOLDOWN.toString()
                        .replace("<cooldown>", TimerUtils.getPlayerCooldown(player, comms) + "")
                ));
                return;
            }

            bStaffs.log(player, "commands", "Executed command: " + comms.getCommand() + " " + String.join(" ", args));
            StringBuilder builder = new StringBuilder();
            if (comms.getType().equals(CommandType.SOLO)) {
                // Adding cooldown.
                setCooldown(player, comms);

                // Sending message send output to player.
                player.sendMessage(bStaffHolder.getStaffHolder(playerCache, player, comms.getUsage(), null));
                for (String arg : args) builder.append(arg).append(" ");

                String result = builder.toString();
                HashMap<String, String> map = bStaffHolder.getLinedArguments(result);

                if (player.hasPermission("bstaffs.staff") && comms.isAdministrative())
                    result = modify(result, false, map);

                // Sending the message receiver output to staffs.
                sendDiscordMessage(player, null, result, map);
                RedisMessageFormat.sendMessage(RedisMessageFormat.MessageType.COMMAND, bStaffs.isRedisPresent(),
                        comms.getCommand(), playerCache.toJson(), result);
                return;
            }

            if (comms.getType() == CommandType.PRIVATE) {
                // Adding cooldown.
                if (args.length >= 2) {
                    if (!bStaffs.INSTANCE.isOnline(args[0])) {
                        player.sendMessage(new TextComponent(LanguageHandler.PLAYER_NOT_FOUND.toString().replace("<target>", args[0])));
                        return;
                    }

                    PlayerCache targetCache = new PlayerCache(args[0], "");
                    for (int x = 1; x < args.length; x++) builder.append(args[x]).append(" ");

                    setCooldown(player, comms);
                    sendDiscordMessage(player, targetCache, builder.toString(), null);
                    player.sendMessage(ChatUtils.translate(comms.getOutput())
                            .replace("<message>", builder.toString())
                            .replace("<target>", args[0])
                            .replace("<player>", player.getName())
                    );

                    lastTarget.put(player.getName(), args[0]);
                    lastTarget.put(args[0], player.getName());
                    RedisMessageFormat.sendMessage(RedisMessageFormat.MessageType.MSG, bStaffs.isRedisPresent(),
                            comms.getCommand(), playerCache.toJson(), targetCache.toJson(), builder.toString());
                    RedisMessageFormat.sendMessage(RedisMessageFormat.MessageType.SPY_STAFF_MESSAGE, bStaffs.isRedisPresent(), playerCache.toJson(), targetCache.toJson(), builder.toString());
                    return;
                }

                player.sendMessage(bStaffHolder.getStaffHolder(playerCache, player, comms.getUsage(), null));
                return;
            }

            if (comms.getType() == CommandType.RESPONSE) {
                // Adding cooldown.
                if (lastTarget.containsKey(player.getName())) {
                    String name = lastTarget.get(player.getName());
                    if (!bStaffs.INSTANCE.isOnline(name)) {
                        player.sendMessage(new TextComponent(LanguageHandler.PLAYER_NOT_FOUND.toString().replace("<target>", args[0])));
                        return;
                    }

                    PlayerCache targetCache = new PlayerCache(name, "");
                    for (String arg : args) builder.append(arg).append(" ");

                    setCooldown(player, comms);
                    sendDiscordMessage(player, targetCache, builder.toString(), null);
                    player.sendMessage(ChatUtils.translate(comms.getOutput())
                            .replace("<message>", builder.toString())
                            .replace("<target>", name)
                            .replace("<player>", player.getName())
                    );

                    RedisMessageFormat.sendMessage(RedisMessageFormat.MessageType.MSG, bStaffs.isRedisPresent(),
                            comms.getCommand(), playerCache.toJson(), targetCache.toJson(), builder.toString());
                    RedisMessageFormat.sendMessage(RedisMessageFormat.MessageType.SPY_STAFF_MESSAGE, bStaffs.isRedisPresent(), playerCache.toJson(), targetCache.toJson(), builder.toString());
                    return;
                }

                player.sendMessage(bStaffHolder.getStaffHolder(playerCache, player, comms.getUsage(), null));
                return;
            }

            if (comms.getType().equals(CommandType.TARGET)) {
                @Nullable ProxiedPlayer target = bStaffs.INSTANCE.getProxy().getPlayer(args[0]);
                if (target == null) {
                    if (!comms.isIgnore()) {
                        player.sendMessage(new TextComponent(LanguageHandler.PLAYER_NOT_FOUND.toString().replace("<target>", args[0])));
                        return;
                    }
                }

                /**
                 * Get the playercache of the target.
                 * With this method we can save the information of the target into
                 * the playercache and get verified information.
                 */
                PlayerCache targetCache = null;
                if (target != null) targetCache = new PlayerCache(target);
                else targetCache = new PlayerCache(args[0], "");

                /* Send cooldown to player. */
                setCooldown(player, comms);
                for (int x = 1; x < args.length; x++) builder.append(args[x]).append(" ");
                player.sendMessage(ChatUtils.translate(comms.getOutput())
                        .replace("<message>", builder.toString())
                        .replace("<target>", target != null ? target.getName() : args[0])
                        .replace("<player>", player.getName())
                );

                String result = builder.toString();
                HashMap<String, String> map = bStaffHolder.getLinedArguments(result);

                if (player.hasPermission("bstaffs.staff") && comms.isAdministrative())
                    result = modify(result, false, map);

                sendDiscordMessage(player, targetCache, result, map);
                RedisMessageFormat.sendMessage(RedisMessageFormat.MessageType.COMMAND, bStaffs.isRedisPresent(),
                        comms.getCommand(), playerCache.toJson(), result, targetCache.toJson());
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

    public void sendDiscordMessage(ProxiedPlayer player, PlayerCache target, String message, HashMap<String, String> map) {
        if (bStaffs.INSTANCE.getJda() != null) {
            try {
                DiscordHandler handler = comms.getDiscordHandler();
                TextChannel textChannel = bStaffs.INSTANCE.getJda().getTextChannelById(handler.getChannel());
                if (textChannel != null) {
                    String format = handler.getFormat();
                    format = format
                            .replace("<server>", player.getServer().getInfo().getName())
                            .replace("<target_server>", target != null ? target.getServer() : "")
                            .replace("<message>", message)
                            .replace("<target>", target != null ? target.getName() : "Console")
                            .replace("<player>", player.getName())
                    ;

                    if (comms.isAdministrative() && map != null)
                        format = modify(format, true, map);

                    if (handler.isEmbedEnabled()) {
                        textChannel.sendMessageEmbeds(handler.build(format)).queue();
                    } else textChannel.sendMessage(format).queue();
                }
            } catch (Exception ignored) {}
        }
    }

    ////////////////////////////////////////////////////////////////////////////

    private static void setCooldown(ProxiedPlayer player, CommandHandler comms) {
        // Set cooldown if player don't have bypass.
        if (comms.getCooldown() != 0 && !player.hasPermission("bstaffs.bypass"))
            TimerUtils.setPlayerCooldown(player, comms, comms.getCooldown());
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!comms.hasPermission(sender)) return Collections.emptyList();
        if (args.length == 1) {
            if (comms.getType().equals(CommandType.TARGET) || comms.getType().equals(CommandType.PRIVATE)) {
                ArrayList<String> players = new ArrayList<>();
                if (bStaffs.isRedisPresent()) {
                    bStaffs.getRedisHandler().getApi().getHumanPlayersOnline().forEach(onlinePlayer -> {
                        if (onlinePlayer.toLowerCase().startsWith(args[0].toLowerCase()))
                            players.add(onlinePlayer);
                    });
                } else {
                    bStaffs.INSTANCE.getProxy().getPlayers().forEach(onlinePlayer -> {
                        if (onlinePlayer.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                            players.add(onlinePlayer.getName());
                    });
                }
                return players;
            }
        }

        return Collections.emptyList();
    }
}
