package me.dragonsteam.bungeestaffs.loaders;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import me.dragonsteam.bungeestaffs.utils.formats.TextFormatReader;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Joansiitoh (DragonsTeam)
 * Date: 20/07/2021.
 */

@Getter
@Accessors(chain = true)
public enum LanguageHandler {

    PREFIX("PREFIX", "DEFAULT", "&7[&bBungeeStaffs&7] &f"),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    NO_PERMISSION("NO-PERMISSIONS", "DEFAULT", "&cYou don't have permission to execute this command."),
    PLAYER_NOT_FOUND("PLAYER-NOT-FOUND", "DEFAULT", "&cPlayer <target> not found."),
    ONLY_FOR_PLAYERS("ONLY-PLAYERS", "DEFAULT", "&cOnly players can execute this command."),

    BOOLEAN_TRUE("TRUE-ARGUMENT", "DEFAULT.OBJECTS", "&aenabled"),
    BOOLEAN_FALSE("FALSE-ARGUMENT", "DEFAULT.OBJECTS", "&cdisabled"),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    SEARCH("SEARCH-FORMAT", "SEARCH",
            "&b${<chat_bar>}(color=#00fff7-#00ff40)(format=strikethrough)",
            "&fPlayer &a<player> &fhas been &afound&f.",
            "&fCurrent server: &a${<server>}(show_text=&eClick to join)(run_command=/<raw_server>)",
            "&b${<chat_bar>}(color=#00fff7-#00ff40)(format=strikethrough)"
    ),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    KICKED_MESSAGE("KICKED-MESSAGE", "SERVER-KICK", "&fYou have been &ckicked &fby staff."),
    PLAYERS_KICKED("PLAYERS-KICKED", "SERVER-KICK", "&fAll players on &c<server> &fhas been kicked."),
    SERVER_NOT_FOUND("SERVER-NOT-FOUND", "SERVER-KICK", "&fServer &c<server> &fnot found."),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Control event messages
    CLIENT_STATUS("CLIENT-STATUS-FORMAT", "SERVERS",
            "&b${<chat_bar>}(color=#00fff7-#00ff40)(format=strikethrough)",
            "&b>> &fPlayers using version 1.7 - 1.7.10: &a<players_3-5> &f(&b<percent_3-5>%&f)",
            "&b>> &fPlayers using version 1.8 - 1.8.9: &a<players_47-47> &f(&b<percent_47-47>%&f)",
            "&b>> &fPlayers using version 1.9 - 1.12.2: &a<players_107-340> &f(&b<percent_107-340>%&f)",
            "&b>> &fPlayers using version 1.13 - 1.18.1: &a<players_393-757> &f(&b<percent_393-757>%&f)",
            "&b${<chat_bar>}(color=#00fff7-#00ff40)(format=strikethrough)"
    ),

    SERVER_LIST("LIST-FORMAT", "SERVERS",
            "&b${<chat_bar>}(color=#00fff7-#00ff40)(format=strikethrough)",
            "&b>> &e${BUNGEE SERVER LIST}(color=#5e2fb5-#00f2ff)(format=bold)",
            "&a[${<raw_server>}(show_text=&eClick to join)(run_command=/<raw_server>)&a] &7&l| &e[${Online}(show_text=<server_players>)&e] &f> &b<server_online>",
            "&b${<chat_bar>}(color=#00fff7-#00ff40)(format=strikethrough)"
    ),

    STAFF_LIST("LIST-FORMAT", "STAFFS",
            "&b${<chat_bar>}(color=#00fff7-#00ff40)(format=strikethrough)",
            "&b>> &fCurrent &bstaffs&f online:",
            "&f* &a<player> &7(<server>)",
            "&b${<chat_bar>}(color=#00fff7-#00ff40)(format=strikethrough)"
    ),

    STAFF_JOIN("JOIN", "STAFFS", "&b<prefix><player> &fhas joined the server."),
    STAFF_LEFT("LEFT", "STAFFS", "&b<prefix><player> &fhas left the server."),
    STAFF_MOVE("MOVE", "STAFFS", "&b<prefix><player> &fhas switch to &b<server>&f."),

    HAVE_COOLDOWN("HAVE-COOLDOWN", "COOLDOWN", "&bPlease, wait &9<cooldown> &7second(s) to execute this command &bagain&7."),

    COMMAND_TOGGLED("COMMAND-TOGGLED", "COMMANDS", "&7Command outputs of '&b<command>&7' has been <value>&7."),
    COMMAND_NOT_FOUND("COMMAND-NO-EXIST", "COMMANDS", "&7This command not exist&7."),

    CHAT_TOGGLED("CHAT-TOGGLED", "CHATS", "&7Chat outputs of '&b<chat>&7' has been <value>&7."),
    CHAT_NOT_FOUND("CHAT-NO-EXIST", "CHATS", "&7This chat input not exist&7."),
    CHAT_DISABLED("CHAT-DISABLED", "CHATS", "&7Chat input '&b<chat>&7' is &cdisabled&7."),
    CHAT_ENABLED("CHAT-ENABLED", "CHATS", "&7Chat input '&b<chat>&7' is &aenabled&7."),

    ALIASES_TELEPORT("TELEPORT", "ALIASES", "&7Connecting to &b<server>&7."),
    ALIASES_ALREADY_TELEPORT("ALREADY", "ALIASES", "&cYou already connected to this server."),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    LIMBO_NOT_SET("NOT-SET", "LIMBO", "&7The limbo not exist."),

    LIMBO_JOIN("JOIN", "LIMBO", "&7You join the limbo. Wait to reconnect to your old server."),
    LIMBO_LEAVE("LEFT", "LIMBO", "&7You left the limbo."),
    LIMBO_PREVENT_MOVE("MOVE-CANCELLED", "LIMBO", "&7You can't move while you are in Limbo."),

    LIMBO_SERVER_REACHED("SERVER-REACHED", "LIMBO", "&7Server <server> is online. In 15 seconds you will be teleported."),
    LIMBO_SERVER_NOT_REACHED("SERVER-NOT-REACHED", "LIMBO", "&7Server <server> can't be reached. Sending to fallback servers."),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String path, subPath;

    private String def;
    private List<String> defList;

    private static final ConfigFile config = bStaffs.INSTANCE.getMessagesFile();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    LanguageHandler(String path, String def) {
        this.path = path;
        this.subPath = "";
        this.def = def;
    }

    LanguageHandler(String path, String subPath, String... defList) {
        this.path = path;
        this.subPath = subPath;
        this.defList = Arrays.asList(defList);
        this.def = null;
    }

    LanguageHandler(String path, String subPath, String def) {
        this.path = path;
        this.subPath = subPath;
        this.defList = null;
        this.def = def;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String toString() {
        return toString(false);
    }

    public String toString(boolean prefix) {
        // Check if final path is a list using config.getStringList().
        if (config.getStringList(getFinalPath()) != null && config.getStringList(getFinalPath()).size() > 0) {
            // Transform list to string using "\n".
            StringBuilder sb = new StringBuilder();
            for (String s : config.getStringList(getFinalPath())) sb.append(s).append("\n");
            return sb.toString();
        }

        return ChatUtils.translate((prefix ? LanguageHandler.PREFIX.toString() : "") + config.getString(getFinalPath(), this.def));
    }

    public List<String> toList() {
        return config.getStringList(getFinalPath());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String getFinalPath() {
        return this.subPath.equalsIgnoreCase("") ? this.path : this.subPath + "." + this.path;
    }

    public static void load() {
        Configuration cfg = config.getConfiguration();
        for (LanguageHandler item : LanguageHandler.values()) {
            if (!config.getConfiguration().contains(item.getFinalPath())) {
                config.getConfiguration().set(item.getFinalPath(),
                        item.getDef() == null || item.getDef().equalsIgnoreCase("") ? item.getDefList() : item.getDef());
            }

            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, config.getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config.save();
    }

}