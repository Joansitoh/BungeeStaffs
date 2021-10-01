package me.dragonsteam.bungeestaffs.loaders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.utils.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.ConfigFile;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.List;

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

    public static Chats getChatByInput(String input) {
        return chatsHashMap.get(input);
    }

    public Chats(Plugin plugin) {
        chatsHashMap.clear();
        bStaffs.logger("Registering custom chats.", "[Loader]");
        ConfigFile config = bStaffs.INSTANCE.getChatsFile();
        for (String s : config.getConfiguration().getSection("CHATS").getKeys()) {
            Configuration section = config.getConfiguration().getSection("CHATS." + s);

            try {
                Chats chats = new Chats(
                        section.getString("INPUT"),
                        section.getString("FORMAT"),
                        section.getString("PERMISSION")
                );
                chatsHashMap.put(chats.getInput(), chats);
                bStaffs.logger("* New custom chat created. (" + s + ")", "[Loader]");
            } catch (Exception e) {
                bStaffs.logger("* Error on load custom chat.", "[Loader]");
            }
        }
    }

    public String getPlayerFormat(ProxiedPlayer player, String message) {
        String result = format
                .replace("<staff>", player.getName())
                .replace("<server>", player.getServer().getInfo().getMotd())
                .replace("<message>", message.substring(input.length()))
                ;
        return ChatUtils.translate(result);
    }
}
