package me.dragonsteam.bungeestaffs.commands.types;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
import me.dragonsteam.bungeestaffs.utils.PlayerCache;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SearchCMD extends Command implements TabExecutor {

    public SearchCMD() {
        super("search", "bstaffs.search");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (args.length != 1) {
            player.sendMessage("§fUsage: §c/search <player>");
            return;
        }

        if (bStaffs.isRedisPresent()) {
            for (String server : bStaffs.getRedisHandler().getApi().getServerToPlayers().keySet()) {
                for (UUID uuid : bStaffs.getRedisHandler().getApi().getServerToPlayers().get(server)) {
                    if (args[0].equalsIgnoreCase(bStaffs.getRedisHandler().getApi().getNameFromUuid(uuid))) {
                        for (String s : LanguageHandler.SEARCH.toList())
                            player.sendMessage(bStaffHolder.getStaffHolder(new PlayerCache(args[0], server), player, s, ""));
                        return;
                    }
                }
            }
        }

        ProxiedPlayer target = bStaffs.INSTANCE.getProxy().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(LanguageHandler.PREFIX + LanguageHandler.PLAYER_NOT_FOUND.toString().replace("<target>", args[0]));
            return;
        }

        for (String s : LanguageHandler.SEARCH.toList()) {
            player.sendMessage(bStaffHolder.getStaffHolder(new PlayerCache(target), target, s, ""));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length == 1) {
            for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    arguments.add(p.getName());
            }
            return arguments;
        }

        return arguments;
    }

}
