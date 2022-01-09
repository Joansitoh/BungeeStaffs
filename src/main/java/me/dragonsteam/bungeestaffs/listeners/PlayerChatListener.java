package me.dragonsteam.bungeestaffs.listeners;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.ChatsHandler;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ToggleUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 16:06.
 */
public class PlayerChatListener extends ListenerAdapter implements Listener {

    @EventHandler
    public void onPlayerChat(ChatEvent e) {
        if (!(e.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();

        ChatsHandler chats = null;
        for (String s : ChatsHandler.getChatsHashMap().keySet()) {
            if (e.getMessage().startsWith(s)) chats = ChatsHandler.getChatByInput(s);
        }

        if (chats == null) return;
        if (e.getMessage().substring(chats.getInput().length()).equalsIgnoreCase("")) return;
        if (!player.hasPermission(chats.getPermission())) return;
        e.setCancelled(true);

        e.setMessage(e.getMessage().substring(chats.getInput().length()));

        if (bStaffs.INSTANCE.getJda() != null) {
            try {
                TextChannel textChannel = bStaffs.INSTANCE.getJda().getTextChannelById(chats.getDiscordChannel());
                if (textChannel != null) {
                    textChannel.sendMessage(ChatColor.stripColor(bStaffHolder.getStaffHolderMessage(player, chats.getDiscordFormatDiscord())
                            .replace("<message>", e.getMessage()))).queue();
                }
            } catch (Exception ignored) {
            }
        }

        for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
            if (!p.hasPermission(chats.getPermission())) continue;
            if (ToggleUtils.isToggledChat(p, chats)) continue;
            p.sendMessage(chats.getPlayerFormat(player, p, e.getMessage()));
        }
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
                    p.sendMessage(ChatUtils.translate(handler.getDiscordFormatGame()
                            .replace("<player>", event.getMember().getNickname())
                            .replace("<message>", event.getMessage().getContentRaw())
                    ));
                }
            }
        }
    }
}
