package me.dragonsteam.bungeestaffs.loaders;

import lombok.Getter;
import lombok.Setter;
import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.commands.CommandManager;
import me.dragonsteam.bungeestaffs.utils.CommandType;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.*;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 03/08/2021 - 1:30.
 */
@Getter @Setter
public class CommandHandler {

    @Getter
    private static final HashMap<String, CommandHandler> commsHashMap = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////

    private boolean administrative;
    private int cooldown;
    private CommandType type;
    private DiscordHandler discordHandler;

    private String command, usage, format, output;
    private String sendPermission, receivePermission, togglePermission;

    private List<String> aliases;

    ////////////////////////////////////////////////////////////////////////////////

    public CommandHandler(CommandType type, String command) {
        this.type = type;
        this.command = command;

        // Set defaults for all variables.
        this.cooldown = 0;
        this.usage = "";
        this.format = "";
        this.output = "";

        this.sendPermission = "";
        this.receivePermission = "";
        this.togglePermission = "";

        this.aliases = new ArrayList<>();
    }

    /**
     * Loads the config file and sets all variables.
     */
    public CommandHandler() {
        // Unregister comms using CommandManager.
        for (CommandHandler comms : commsHashMap.values())
            CommandManager.unregisterCommand(comms);

        commsHashMap.clear();
        bStaffs.logger("Registering custom commands.", "[Loader]");
        ConfigFile config = bStaffs.INSTANCE.getCommandsFile();

        // Loop through all commands.
        for (String s : config.getConfiguration().getSection("COMMANDS").getKeys()) {
            Configuration section = config.getConfiguration().getSection("COMMANDS." + s);

            // Setting default permissions for corrupted configs.
            ChatUtils.setDefaultIfNotSet(section, "PERMISSION.SEND", "bstaffs.send." + s.toLowerCase());
            ChatUtils.setDefaultIfNotSet(section, "PERMISSION.RECEIVE", "bstaffs.receive." + s.toLowerCase());
            ChatUtils.setDefaultIfNotSet(section, "PERMISSION.TOGGLE", "bstaffs.toggle." + s.toLowerCase());

            try {
                CommandHandler comms = new CommandHandler(CommandType.valueOf(section.getString("TYPE")), section.getString("COMMAND"));

                /* Set comms defaults accessors with config values */

                if (section.contains("COOLDOWN")) comms.setCooldown(section.getInt("COOLDOWN"));
                else comms.setCooldown(0);

                if (section.contains("USAGE")) comms.setUsage(section.getString("USAGE"));
                if (section.contains("OUTPUT")) comms.setOutput(section.getString("OUTPUT"));

                comms.setDiscordHandler(new DiscordHandler(section.getSection("DISCORD")));

                if (section.contains("FORMAT")) {
                    if (!section.getStringList("FORMAT").isEmpty()) {
                        StringBuilder builder = new StringBuilder();
                        for (String s1 : section.getStringList("FORMAT"))
                            builder.append(s1).append("\n");
                        comms.setFormat(builder.toString());
                    } else comms.setFormat(section.getString("FORMAT"));
                }

                if (section.getString("ALIASES") != null)
                    comms.setAliases(section.getStringList("ALIASES"));

                if (section.contains("ADMINISTRATIVE"))
                    comms.setAdministrative(section.getBoolean("ADMINISTRATIVE"));

                /* Set comms permission accessors with config values */
                comms.setSendPermission(section.getString("PERMISSIONS.SEND"));
                if (section.contains("PERMISSIONS.RECEIVE")) comms.setReceivePermission(section.getString("PERMISSIONS.RECEIVE"));
                if (section.contains("PERMISSIONS.TOGGLE")) comms.setTogglePermission(section.getString("PERMISSIONS.TOGGLE"));

                CommandManager.registerCommand(comms);
                commsHashMap.put(comms.getCommand(), comms);
                bStaffs.logger("* New custom command created. (" + comms.getCommand() + ")", "[Loader]");
            } catch (Exception e) {
                bStaffs.logger("* Error on load custom command.", "[Loader]");
            }
        }
    }

    public static CommandHandler getCommandByName(String input) {
        return commsHashMap.get(input);
    }

    public String getUsage() {
        return ChatUtils.translate(this.usage);
    }

    /**
     * Replace custom command using StaffHolder.
     *
     * @param player who send the command.
     * @param target for no SOLO commands.
     * @param message of the command.
     */
    public BaseComponent[] getPlayerFormat(ProxiedPlayer player, ProxiedPlayer target, String message) {
        return bStaffHolder.getStaffHolder(player, player, format.replace("<target>", target != null ? target.getName() : ""), message);
    }

    public boolean hasPermission(CommandSender player) {
        return sendPermission.equals("") || player.hasPermission(sendPermission);
    }

}
