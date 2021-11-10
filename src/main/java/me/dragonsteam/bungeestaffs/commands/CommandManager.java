package me.dragonsteam.bungeestaffs.commands;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.CommandType;
import me.dragonsteam.bungeestaffs.utils.TimerUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 09/11/2021 - 13:56.
 */
public class CommandManager {

    private static final HashMap<Comms, Command> commands = new HashMap<>();

    public static void registerCommand(Comms comms) {
        cCommand command = new cCommand(comms);

        bStaffs.INSTANCE.getProxy().getPluginManager().registerCommand(bStaffs.INSTANCE, command);
        commands.put(comms, command);
    }

    public static void unregisterCommand(Comms comms) {
        bStaffs.INSTANCE.getProxy().getPluginManager().unregisterCommand(commands.get(comms));
        commands.remove(comms);
    }

}
