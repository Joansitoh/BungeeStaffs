package me.dragonsteam.bungeestaffs.commands;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

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

        ProxiedPlayer target = bStaffs.INSTANCE.getProxy().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Lang.PREFIX + Lang.PLAYER_NOT_FOUND.toString().replace("<target>", args[0]));
            return;
        }

        for (String s : Lang.SEARCH.toList()) {
            player.sendMessage(bStaffHolder.getStaffHolder(target, s));
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
