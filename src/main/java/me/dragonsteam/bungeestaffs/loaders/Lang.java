package me.dragonsteam.bungeestaffs.loaders;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import me.dragonsteam.bungeestaffs.utils.formats.TextFormatReader;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Joansiitoh (DragonsTeam)
 * Date: 20/07/2021.
 */

@Getter
@Accessors(chain = true)
public enum Lang {

    PREFIX("PREFIX", "DEFAULT", "&7[&bBungeeStaffs&7] &f"),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    NO_PERMISSION("NO-PERMISSIONS", "DEFAULT", "&cYou don't have permission to execute this command."),
    PLAYER_NOT_FOUND("PLAYER-NOT-FOUND", "DEFAULT", "&cPlayer <target> not found."),
    ONLY_FOR_PLAYERS("ONLY-PLAYERS", "DEFAULT", "&cOnly players can execute this command."),

    BOOLEAN_TRUE("TRUE-ARGUMENT", "DEFAULT.OBJECTS", "&aenabled"),
    BOOLEAN_FALSE("FALSE-ARGUMENT", "DEFAULT.OBJECTS", "&cdisabled"),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    SEARCH("SEARCH-FORMAT", "SEARCH",
            "[<chat_bar>](rainbow)",
            "&fPlayer &a<player> &fhas been &afound&f.",
            "&fCurrent server: &a<hover><server></hover>",
            "[<chat_bar>](rainbow)"
    ),
    SEARCH_HOVER("SEARCH-HOVER", "SEARCH", "&7| &eClick to join"),
    SEARCH_COMMAND("SEARCH-COMMAND", "SEARCH", "/server <server>"),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    KICKED_MESSAGE("KICKED-MESSAGE", "SERVER-KICK", "&fYou have been &ckicked &fby staff."),
    PLAYERS_KICKED("PLAYERS-KICKED", "SERVER-KICK", "&fAll players on &c<server> &fhas been kicked."),
    SERVER_NOT_FOUND("SERVER-NOT-FOUND", "SERVER-KICK", "&fServer &c<server> &fnot found."),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Control event messages
    STAFF_LIST("LIST-FORMAT", "STAFFS",
            "[<chat_bar>](rainbow)",
            "&b>> &fCurrent &bstaffs&f online:",
            "&f* &a<player> &7(<server>)",
            "[<chat_bar>](rainbow)"
    ),

    STAFF_JOIN("JOIN", "STAFFS", "&b<prefix><player> &7has joined the server."),
    STAFF_LEFT("LEFT", "STAFFS", "&b<prefix><player> &7has left the server."),
    STAFF_MOVE("MOVE", "STAFFS", "&b<prefix><player> &7has switch to &b<server>&7."),

    HAVE_COOLDOWN("HAVE-COOLDOWN", "COOLDOWN", "&bPlease, wait &9<cooldown> &7second(s) to execute this command &bagain&7."),

    COMMAND_TOGGLED("COMMAND-TOGGLED", "COMMANDS", "&7Command outputs of '&b<command>&7' has been <value>&7."),
    COMMAND_NOT_FOUND("COMMAND-NO-EXIST", "COMMANDS", "&7This command not exist&7."),

    CHAT_TOGGLED("CHAT-TOGGLED", "CHATS", "&7Chat outputs of '&b<chat>&7' has been <value>&7."),
    CHAT_NOT_FOUND("CHAT-NO-EXIST", "CHATS", "&7This chat input not exist&7."),

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

    Lang(String path, String def) {
        this.path = path;
        this.subPath = "";
        this.def = def;
    }

    Lang(String path, String subPath, String... defList) {
        this.path = path;
        this.subPath = subPath;
        this.defList = Arrays.asList(defList);
        this.def = null;
    }

    Lang(String path, String subPath, String def) {
        this.path = path;
        this.subPath = subPath;
        this.defList = null;
        this.def = def;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void send(ProxiedPlayer sender) {
        sender.sendMessage(TextFormatReader.complexFormat(bStaffHolder.getStaffHolderMessage(sender, toString(true))));
    }

    public String toString(boolean prefix) {
        return (prefix ? Lang.PREFIX.toString(false) : "") + ChatUtils.translate(config.getString(getFinalPath(), this.def));
    }

    public String toString() {
        // Check if final path is a list using config.getStringList().
        if (config.getStringList(getFinalPath()) != null && config.getStringList(getFinalPath()).size() > 0) {
            // Transform list to string using "\n".
            StringBuilder sb = new StringBuilder();
            for (String s : config.getStringList(getFinalPath())) sb.append(s).append("\n");
            return sb.toString();
        }

        return ChatUtils.translate(config.getString(getFinalPath(), this.def));
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
        for (Lang item : Lang.values()) {
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