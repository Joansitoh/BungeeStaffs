package me.dragonsteam.bungeestaffs.commands.types;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ToggleUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 09/11/2021 - 17:48.
 */
public class ToggleCMD extends Command implements TabExecutor {

    public ToggleCMD() {
        super("toggle", "bstaffs.toggle");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Return if sender is not a player.
        if (!(sender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length == 1) {
            Comms comms = null;
            for (String s : Comms.getCommsHashMap().keySet()) {
                if (s.equalsIgnoreCase(args[0])) comms = Comms.getCommandByName(s);
            }

            if (comms == null) {
                player.sendMessage(new TextComponent(Lang.COMMAND_NOT_FOUND.toString()));
                return;
            }

            if (!hasPerm(player, comms.getTogglePermission())) {
                player.sendMessage(new TextComponent(Lang.NO_PERMISSION.toString()));
                return;
            }

            ToggleUtils.togglePlayerCommand(player, comms);
            player.sendMessage(new TextComponent(Lang.COMMAND_TOGGLED.toString()
                    .replace("<command>", comms.getCommand())
                    .replace("<value>", ToggleUtils.isToggledCommand(player, comms) ? Lang.BOOLEAN_FALSE.toString() : Lang.BOOLEAN_TRUE.toString())
            ));
            return;
        }

        player.sendMessage(ChatUtils.translate("&cUsage: &f/toggle <command>"));
    }

    private boolean hasPerm(ProxiedPlayer player, String permission) {
        if (permission.equalsIgnoreCase("")) return true;
        return player.hasPermission(permission);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> arguments = new ArrayList<>();
            Comms.getCommsHashMap().keySet().forEach(cmd -> {
                if (cmd.toLowerCase().startsWith(args[0].toLowerCase()))
                    arguments.add(cmd);
            });

            return arguments;
        }

        return Collections.emptyList();
    }

}
