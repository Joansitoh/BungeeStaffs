package me.dragonsteam.bungeestaffs.loaders;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.utils.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.ConfigFile;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Disguise List
    STAFF_JOIN("JOIN", "STAFFS", "&b<staff> &7has joined the server."),
    STAFF_LEFT("LEFT", "STAFFS", "&b<staff> &7has left the server."),
    STAFF_MOVE("MOVE", "STAFFS", "&b<staff> &7has switch to &b<server>&7."),

    HAVE_COOLDOWN("HAVE-COOLDOWN", "COOLDOWN", "&bPlease, wait &9<cooldown> &7second(s) to execute this command &bagain&7."),

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ;

    private final String path;
    private final String subPath;

    private String def;
    private List<String> defList;

    private static final ConfigFile config = bStaffs.INSTANCE.getMessagesFile();

    Lang(String path, String subPath, String... defList) {
        this.path = path;
        this.subPath = subPath;

        if (defList.length == 1) this.def = defList[0];
        else this.defList = Arrays.asList(defList);
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

        return config.getString(getFinalPath(), def);
    }

    public List<String> toList() {
        if (def != null) return Collections.singletonList(toString());
        return config.getStringListOrDefault(getFinalPath(), defList);
    }

    public String getFinalPath() {
        return subPath.equalsIgnoreCase("") ? path : subPath + "." + path;
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

}