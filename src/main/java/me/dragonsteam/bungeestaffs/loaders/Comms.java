package me.dragonsteam.bungeestaffs.loaders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.utils.CommandType;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
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
    private String command, usage, format, output, sendPermission, receivePermission, togglePermission;

    public Comms(Plugin plugin) {
        commsHashMap.clear();
        bStaffs.logger("Registering custom commands.", "[Loader]");
        ConfigFile config = bStaffs.INSTANCE.getCommandsFile();
        for (String s : config.getConfiguration().getSection("COMMANDS").getKeys()) {
            Configuration section = config.getConfiguration().getSection("COMMANDS." + s);

            ChatUtils.setDefaultIfNotSet(section, "PERMISSION.SEND", "bstaffs.send." + s.toLowerCase());
            ChatUtils.setDefaultIfNotSet(section, "PERMISSION.RECEIVE", "bstaffs.receive." + s.toLowerCase());
            ChatUtils.setDefaultIfNotSet(section, "PERMISSION.TOGGLE", "bstaffs.toggle." + s.toLowerCase());

            try {
                Comms comms = new Comms(
                        section.getInt("COOLDOWN"), CommandType.valueOf(section.getString("TYPE")),
                        section.getString("COMMAND"), section.getString("USAGE"),
                        section.getString("FORMAT"), section.getString("OUTPUT"),
                        section.getString("PERMISSIONS.SEND"), section.getString("PERMISSIONS.RECEIVE"),
                        section.getString("PERMISSIONS.TOGGLE")
                );

                commsHashMap.put(comms.getCommand(), comms);
                bStaffs.logger("* New custom command created. (" + comms.getCommand() + ")", "[Loader]");
            } catch (Exception e) {
                bStaffs.logger("* Error on load custom command.", "[Loader]");
            }
        }
    }

    public static Comms getCommandByName(String input) {
        return commsHashMap.get(input);
    }

    public String getUsage() {
        return ChatUtils.translate(this.usage);
    }

    public String getPlayerFormat(ProxiedPlayer player, ProxiedPlayer target, String message) {
        return bStaffHolder.getStaffHolder(player, format.replace("<target>", target != null ? target.getName() : ""))
                .replace("<message>", message);
    }
}
