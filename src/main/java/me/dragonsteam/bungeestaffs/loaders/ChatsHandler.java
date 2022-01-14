package me.dragonsteam.bungeestaffs.loaders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.commands.CommandManager;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 16:23.
 */
@Getter @Setter
public class ChatsHandler {

    @Getter
    private static final HashMap<String, ChatsHandler> chatsHashMap = new HashMap<>();
    @Getter
    private static final HashMap<UUID, ChatsHandler> playersChatsMap = new HashMap<>();

    private String input, format, permission, toggleCommand;
    private String discordChannel, discordFormatGame, discordFormatDiscord;

    private boolean bidirectional;

    public ChatsHandler(String input, String format, String permission) {
        this.input = input;
        this.format = format;
        this.permission = permission;
    }

    public ChatsHandler() {
        chatsHashMap.clear();
        bStaffs.logger("Registering custom chats.", "[Loader]");
        ConfigFile config = bStaffs.INSTANCE.getChatsFile();
        for (String s : config.getConfiguration().getSection("CHATS").getKeys()) {
            Configuration section = config.getConfiguration().getSection("CHATS." + s);

            try {
                String format = "";

                if (!section.getStringList("FORMAT").isEmpty()) {
                    StringBuilder builder = new StringBuilder();
                    for (String s1 : section.getStringList("FORMAT"))
                        builder.append(s1).append("\n");
                    format = builder.toString();
                } else format = section.getString("FORMAT");

                ChatsHandler chats = new ChatsHandler(section.getString("INPUT"), format, section.getString("PERMISSION"));
                if (section.contains("TOGGLE-CMD")) chats.setToggleCommand(section.getString("TOGGLE-CMD"));
                if (section.contains("DISCORD") && section.getBoolean("DISCORD.ENABLED")) {
                    chats.setDiscordFormatGame(section.getString("DISCORD.FORMAT.GAME"));
                    chats.setDiscordFormatDiscord(section.getString("DISCORD.FORMAT.DISCORD"));
                    chats.setDiscordChannel(section.getString("DISCORD.CHANNEL"));
                    chats.setBidirectional(section.getBoolean("DISCORD.BIDIRECTIONAL"));
                }

                Command command = chats.getCommand();
                if (command != null) {
                    bStaffs.INSTANCE.getProxy().getPluginManager().registerCommand(bStaffs.INSTANCE, command);
                    bStaffs.INSTANCE.getKnownCommands().put(command.getName(), command);
                }

                chatsHashMap.put(chats.getInput(), chats);
                bStaffs.logger("* New custom chat created. (" + s + ")", "[Loader]");
            } catch (Exception e) {
                bStaffs.logger("* Error on load custom chat.", "[Loader]");
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    public Command getCommand() {
        if (toggleCommand == null) return null;
        return new Command(toggleCommand, permission) {
            @Override
            public void execute(CommandSender sender, String[] args) {
                if (!(sender instanceof ProxiedPlayer)) return;
                ProxiedPlayer player = (ProxiedPlayer) sender;
                if (!hasPermission(sender)) {
                    sender.sendMessage(LanguageHandler.NO_PERMISSION.toString(true));
                    return;
                }

                if (playersChatsMap.containsKey(player.getUniqueId())) {
                    ChatsHandler chats = playersChatsMap.get(player.getUniqueId());
                    if (chats.getInput().equals(input)) {
                        playersChatsMap.remove(player.getUniqueId());
                        player.sendMessage(LanguageHandler.CHAT_DISABLED.toString(true).replace("<chat>", chats.getInput()));
                        return;
                    }

                    playersChatsMap.put(player.getUniqueId(), chatsHashMap.get(chats.getInput()));
                    player.sendMessage(LanguageHandler.CHAT_ENABLED.toString(true).replace("<chat>", chats.getInput()));
                    return;
                }

                playersChatsMap.put(player.getUniqueId(), chatsHashMap.get(input));
                player.sendMessage(LanguageHandler.CHAT_ENABLED.toString(true).replace("<chat>", input));
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////

    public static ChatsHandler getChatByInput(String input) {
        return chatsHashMap.get(input);
    }

    public BaseComponent[] getPlayerFormat(ProxiedPlayer player, ProxiedPlayer viewer, String message) {
        return bStaffHolder.getStaffHolder(player, viewer, format, message);
    }

    ///////////////////////////////////////////////////////////////////////////
}
