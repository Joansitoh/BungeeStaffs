package me.dragonsteam.bungeestaffs.loaders;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            "<chat_bar>",
            "&fPlayer &a<player> &fhas been &afound&f.",
            "&fCurrent server: &a<server>",
            "<chat_bar>"
    ),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Control event messages
    STAFF_LIST("LIST-FORMAT", "STAFFS",
            "&b>> &fCurrent &bstaffs&f online:",
            "&f* &a<player> &7(<server>)"
    ),

    STAFF_JOIN("JOIN", "STAFFS", "&b<player> &7has joined the server."),
    STAFF_LEFT("LEFT", "STAFFS", "&b<player> &7has left the server."),
    STAFF_MOVE("MOVE", "STAFFS", "&b<player> &7has switch to &b<server>&7."),

    HAVE_COOLDOWN("HAVE-COOLDOWN", "COOLDOWN", "&bPlease, wait &9<cooldown> &7second(s) to execute this command &bagain&7."),

    COMMAND_TOGGLED("COMMAND-TOGGLED", "COMMANDS", "&7Command outputs of '&b<command>&7' has been <value>&7."),
    COMMAND_NOT_FOUND("COMMAND-NO-EXIST", "COMMANDS", "&7This command not exist&7."),

    CHAT_TOGGLED("CHAT-TOGGLED", "CHATS", "&7Chat outputs of '&b<chat>&7' has been <value>&7."),
    CHAT_NOT_FOUND("CHAT-NO-EXIST", "CHATS", "&7This chat input not exist&7."),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ;

    private static final ConfigFile config = bStaffs.INSTANCE.getMessagesFile();
    private final String path;
    private final String subPath;
    private String def;
    private List<String> defList;

    Lang(String path, String subPath, String... defList) {
        this.path = path;
        this.subPath = subPath;

        if (defList.length == 1) this.def = defList[0];
        else this.defList = Arrays.asList(defList);
    }

    public static void loadLanguage() {
        Configuration cfg = config.getConfiguration();
        for (Lang lang : values()) {
            if (!cfg.contains(lang.getFinalPath())) {
                if (lang.getDefList() != null) cfg.set(lang.getFinalPath(), lang.toList());
                else cfg.set(lang.getFinalPath(), lang.toString().replace("§", "&"));
            }
        }

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, config.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toStringDefault() {
        return config.getString(getFinalPath(), def);
    }

    public String toString() {
        if (defList != null) {
            StringBuilder builder = new StringBuilder();
            toList().forEach(text -> builder.append(text).append("\n"));
            return ChatUtils.translate(builder.toString());
        }

        return ChatUtils.translate(config.getString(getFinalPath(), def));
    }

    public List<String> toList() {
        if (def != null) return ChatUtils.translate(Collections.singletonList(toString()));
        return ChatUtils.translate(config.getStringListOrDefault(getFinalPath(), defList));
    }

    public String getFinalPath() {
        return subPath.equalsIgnoreCase("") ? path : subPath + "." + path;
    }

}