package me.dragonsteam.bungeestaffs.loaders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 16:23.
 */
@Getter
@AllArgsConstructor
public class Chats {

    @Getter
    private static final HashMap<String, Chats> chatsHashMap = new HashMap<>();

    private String input, format, permission;

    public Chats(Plugin plugin) {
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

                Chats chats = new Chats(section.getString("INPUT"), format, section.getString("PERMISSION"));
                chatsHashMap.put(chats.getInput(), chats);
                bStaffs.logger("* New custom chat created. (" + s + ")", "[Loader]");
            } catch (Exception e) {
                bStaffs.logger("* Error on load custom chat.", "[Loader]");
            }
        }
    }

    public static Chats getChatByInput(String input) {
        return chatsHashMap.get(input);
    }

    public BaseComponent[] getPlayerFormat(ProxiedPlayer player, String message) {
        return bStaffHolder.getStaffHolder(player, "CHATS", format.replace("<message>", message.substring(input.length())));
    }
}
