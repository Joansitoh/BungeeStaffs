package me.dragonsteam.bungeestaffs.commands;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.CommandHandler;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 09/11/2021 - 13:56.
 */
public class CommandManager {

    private static final HashMap<CommandHandler, Command> commands = new HashMap<>();

    public static void registerCommand(CommandHandler comms) {
        cCommand command = new cCommand(comms);

        bStaffs.INSTANCE.getProxy().getPluginManager().registerCommand(bStaffs.INSTANCE, command);
        commands.put(comms, command);
    }

    public static void unregisterCommand(CommandHandler comms) {
        bStaffs.INSTANCE.getProxy().getPluginManager().unregisterCommand(commands.get(comms));
        commands.remove(comms);
    }

}
