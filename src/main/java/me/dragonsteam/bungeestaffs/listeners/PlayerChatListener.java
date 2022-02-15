package me.dragonsteam.bungeestaffs.listeners;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.ChatsHandler;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
import me.dragonsteam.bungeestaffs.utils.PlayerCache;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ToggleUtils;
import me.dragonsteam.bungeestaffs.utils.formats.RedisMessageFormat;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 16:06.
 */
public class PlayerChatListener extends ListenerAdapter implements Listener {

    @EventHandler
    public void onPlayerFilterChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        Configuration config = bStaffs.INSTANCE.getChatsFile().getConfiguration().getSection("CHAT-FILTER");

        if (event.isCommand()) return;
        if (config != null) {
            if (config.getBoolean("ONLY-CUSTOM-CHATS")) return;
            if (config.getStringList("BLACKLIST-SERVERS").contains(player.getServer().getInfo().getName()))
                return;

            if (!player.hasPermission(config.getString("BYPASS-PERMISSION"))) {
                boolean replacer = config.getBoolean("REPLACER"), textMode = config.getString("REPLACE-MODE").equalsIgnoreCase("TEXT");

                String regex = "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
                Pattern pattern = Pattern.compile(regex);

                boolean notify = false;
                if (config.contains("NOTIFY-BLOCK")) notify = config.getBoolean("NOTIFY-BLOCK");

                if (config.getBoolean("BLOCK-LINKS")) {
                    Matcher matcher = pattern.matcher(event.getMessage());
                    if (matcher.find()) {
                        if (notify) notifier(player, matcher.group());

                        if (replacer) {
                            if (textMode)
                                event.setMessage(config.getStringList("REPLACER-TEXTS").get(new Random().nextInt(config.getStringList("REPLACER-TEXTS").size())));
                            else {
                                for (int x = 0; x < matcher.groupCount(); x++) {
                                    String random = config.getStringList("REPLACER-WORDS").get(new Random().nextInt(config.getStringList("REPLACER-WORDS").size()));
                                    event.setMessage(event.getMessage().replaceFirst(matcher.group(x), random));
                                }
                            }
                        } else {
                            player.sendMessage(LanguageHandler.CANT_SEND_LINK.toString(true));
                            event.setCancelled(true);
                            return;
                        }
                    }
                }

                for (String s : config.getStringList("BLOCKED-WORDS")) {
                    if (event.getMessage().toLowerCase().contains(s.toLowerCase())) {
                        if (notify) notifier(player, s);

                        if (replacer) {
                            if (textMode)
                                event.setMessage(config.getStringList("REPLACER-TEXTS").get(new Random().nextInt(config.getStringList("REPLACER-TEXTS").size())));
                            else {
                                while (event.getMessage().toLowerCase().contains(s.toLowerCase())) {
                                    String random = config.getStringList("REPLACER-WORDS").get(new Random().nextInt(config.getStringList("REPLACER-WORDS").size()));
                                    event.setMessage(event.getMessage().replaceFirst(s, random));
                                }
                            }
                        } else {
                            player.sendMessage(LanguageHandler.CANT_WRITE_WORD.toString(true).replace("<word>", s));
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = 0)
    public void onPlayerChat(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        PlayerCache cache = new PlayerCache(player);

        ChatsHandler chats = null;
        if (!ChatsHandler.getPlayersChatsMap().containsKey(player.getUniqueId())) {
            for (String s : ChatsHandler.getChatsHashMap().keySet()) {
                if (e.getMessage().startsWith(s)) chats = ChatsHandler.getChatByInput(s);
            }

            if (chats == null) return;
            if (e.getMessage().substring(chats.getInput().length()).equalsIgnoreCase("")) return;
            if (!player.hasPermission(chats.getPermission()) && !chats.getPermission().equals("")) return;

            e.setCancelled(true);

            e.setMessage(e.getMessage().substring(chats.getInput().length()));
        } else {
            chats = ChatsHandler.getPlayersChatsMap().get(player.getUniqueId());
            if (e.isCommand()) return;
            e.setCancelled(true);
        }

        bStaffs.log(player, "chat", TextComponent.toPlainText(chats.getPlayerFormat(cache, player, e.getMessage())));
        if (bStaffs.INSTANCE.getJda() != null) {
            try {
                TextChannel textChannel = bStaffs.INSTANCE.getJda().getTextChannelById(chats.getDiscordChannel());
                if (textChannel != null) {
                    textChannel.sendMessage(ChatColor.stripColor(bStaffHolder.getStaffHolderMessage(cache, chats.getDiscordFormatDiscord())
                            .replace("<message>", e.getMessage()))).queue();
                }
            } catch (Exception ignored) {
            }
        }

        Configuration config = bStaffs.INSTANCE.getChatsFile().getConfiguration().getSection("CHAT-FILTER");
        if (config != null) {
            if (!player.hasPermission(config.getString("BYPASS-PERMISSION"))) {
                boolean replacer = config.getBoolean("REPLACER"), textMode = config.getString("REPLACE-MODE").equalsIgnoreCase("TEXT");

                String regex = "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
                Pattern pattern = Pattern.compile(regex);

                boolean notify = false;
                if (config.contains("NOTIFY-BLOCK")) notify = config.getBoolean("NOTIFY-BLOCK");

                if (config.getBoolean("BLOCK-LINKS")) {
                    Matcher matcher = pattern.matcher(e.getMessage());
                    if (matcher.find()) {
                        if (notify) {
                            try {
                                for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
                                    if (!p.hasPermission("bstaffs.staff")) continue;
                                    p.sendMessage(LanguageHandler.CHAT_BLOCKED.toString(true)
                                            .replace("<player>", player.getName())
                                            .replace("<message>", matcher.group())
                                    );
                                }
                            } catch (Exception ignored) {}
                        }

                        if (replacer) {
                            if (textMode)
                                e.setMessage(config.getStringList("REPLACER-TEXTS").get(new Random().nextInt(config.getStringList("REPLACER-TEXTS").size())));
                            else {
                                for (int x = 0; x < matcher.groupCount(); x++) {
                                    String random = config.getStringList("REPLACER-WORDS").get(new Random().nextInt(config.getStringList("REPLACER-WORDS").size()));
                                    e.setMessage(e.getMessage().replaceFirst(matcher.group(x), random));
                                }
                            }
                        } else {
                            player.sendMessage(LanguageHandler.CANT_SEND_LINK.toString(true));
                            e.setCancelled(true);
                            return;
                        }
                    }

                    for (String s : config.getStringList("BLOCKED-WORDS")) {
                        if (e.getMessage().toLowerCase().contains(s.toLowerCase())) {
                            if (notify) {
                                try {
                                    for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
                                        if (!p.hasPermission("bstaffs.staff")) continue;
                                        p.sendMessage(LanguageHandler.CHAT_BLOCKED.toString(true)
                                                .replace("<player>", player.getName())
                                                .replace("<message>", matcher.group())
                                        );
                                    }
                                } catch (Exception ignored) {}
                            }

                            if (replacer) {
                                if (textMode)
                                    e.setMessage(config.getStringList("REPLACER-TEXTS").get(new Random().nextInt(config.getStringList("REPLACER-TEXTS").size())));
                                else {
                                    while (e.getMessage().toLowerCase().contains(s.toLowerCase())) {
                                        String random = config.getStringList("REPLACER-WORDS").get(new Random().nextInt(config.getStringList("REPLACER-WORDS").size()));
                                        e.setMessage(e.getMessage().replaceFirst(s, random));
                                    }
                                }
                            } else {
                                player.sendMessage(LanguageHandler.CANT_WRITE_WORD.toString(true).replace("<word>", s));
                                e.setCancelled(true);
                                return;
                            }
                        }
                    }
                }
            }
        }

        RedisMessageFormat.sendMessage(RedisMessageFormat.MessageType.CHAT, bStaffs.isRedisPresent(), chats.getInput(), cache.toJson(), e.getMessage());
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        // Check if autor is bot
        if (event.getAuthor().isBot()) return;
        for (ChatsHandler handler : ChatsHandler.getChatsHashMap().values()) {
            if (event.getChannel().getId().equals(handler.getDiscordChannel()) && handler.isBidirectional()) {
                for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
                    if (!p.hasPermission(handler.getPermission())) continue;
                    if (ToggleUtils.isToggledChat(p, handler)) continue;
                    if (event.getMember() == null) continue;
                    p.sendMessage(ChatUtils.translate(handler.getDiscordFormatGame()
                            .replace("<player>", event.getMember().getNickname() != null ? event.getMember().getNickname() : event.getMember().getEffectiveName())
                            .replace("<message>", event.getMessage().getContentRaw())
                    ));
                }
            }
        }
    }

    public void notifier(ProxiedPlayer player, String message) {
        try {
            for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
                if (!p.hasPermission("bstaffs.staff")) continue;
                p.sendMessage(LanguageHandler.CHAT_BLOCKED.toString(true)
                        .replace("<player>", player.getName())
                        .replace("<message>", message)
                );
            }
        } catch (Exception ignored) {}
    }

}
