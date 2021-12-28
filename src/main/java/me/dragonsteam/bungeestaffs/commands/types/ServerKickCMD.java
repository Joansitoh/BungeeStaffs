package me.dragonsteam.bungeestaffs.commands.types;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class ServerKickCMD extends Command implements TabExecutor {

    public ServerKickCMD() {
        super("skick", "bstaffs.serverkick");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§fUsage: §c/skick <server>");
            return;
        }

        ServerInfo server = bStaffs.INSTANCE.getProxy().getServers().get(args[0]);
        if (server == null) {
            sender.sendMessage(LanguageHandler.SERVER_NOT_FOUND.toString(true).replace("<server>", args[0]));
            return;
        }

        // Kick all players from server.
        for (ProxiedPlayer player : server.getPlayers()) {
            if (!player.hasPermission("bstaffs.serverkick.bypass"))
                player.disconnect(LanguageHandler.KICKED_MESSAGE.toString());
        }

        sender.sendMessage(LanguageHandler.PLAYERS_KICKED.toString(true).replace("<server>", args[0]));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length == 1) {
            for (ServerInfo server : bStaffs.INSTANCE.getProxy().getServers().values()) {
                if (server.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    arguments.add(server.getName());
            }
        }
        return arguments;
    }

}