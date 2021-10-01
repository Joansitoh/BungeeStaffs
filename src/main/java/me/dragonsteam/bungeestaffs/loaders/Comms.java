package me.dragonsteam.bungeestaffs.loaders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.utils.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.CommandType;
import me.dragonsteam.bungeestaffs.utils.ConfigFile;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 03/08/2021 - 1:30.
 */
@Getter
@AllArgsConstructor
public class Comms {

    @Getter
    private static final HashMap<String, Comms> commsHashMap = new HashMap<>();

    private int cooldown;
    private CommandType type;
    private String command, usage, format, output, sendPermission, receivePermission;

    public static Comms getCommandByName(String input) {
        return commsHashMap.get(input);
    }

    public Comms(Plugin plugin) {
        commsHashMap.clear();
        bStaffs.logger("Registering custom commands.", "[Loader]");
        ConfigFile config = bStaffs.INSTANCE.getCommandsFile();
        for (String s : config.getConfiguration().getSection("COMMANDS").getKeys()) {
            Configuration section = config.getConfiguration().getSection("COMMANDS." + s);

            try {
                Comms comms = new Comms(
                        section.getInt("COOLDOWN"), CommandType.valueOf(section.getString("TYPE")),
                        section.getString("COMMAND"), section.getString("USAGE"),
                        section.getString("FORMAT"), section.getString("OUTPUT"),
                        section.getString("PERMISSIONS.SEND"), section.getString("PERMISSIONS.RECEIVE")
                );

                commsHashMap.put(comms.getCommand(), comms);
                bStaffs.logger("* New custom command created. (" + comms.getCommand() + ")", "[Loader]");
            } catch (Exception e) {
                bStaffs.logger("* Error on load custom command.", "[Loader]");
            }
        }
    }

    public String getPlayerFormat(ProxiedPlayer player, ProxiedPlayer target, String message) {
        String result = format
                .replace("<player>", player.getName())
                .replace("<target>", target != null ? target.getName() : "")
                .replace("<server>", player.getServer().getInfo().getMotd())
                .replace("<message>", message)
                ;
        return ChatUtils.translate(result);
    }
}
