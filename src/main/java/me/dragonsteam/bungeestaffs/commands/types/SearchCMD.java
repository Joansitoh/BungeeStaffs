package me.dragonsteam.bungeestaffs.commands.types;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
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
            player.sendMessage(LanguageHandler.PREFIX + LanguageHandler.PLAYER_NOT_FOUND.toString().replace("<target>", args[0]));
            return;
        }

        String prefix = "<hover>", suffix = "</hover>";
        for (String s : LanguageHandler.SEARCH.toList()) {
            String holder = bStaffHolder.getStaffHolderMessage(target, s);

            // Get string between tags hover.
            String between = ChatUtils.substringBetween(holder, prefix, suffix);
            if (between != null && !between.equalsIgnoreCase("")) {
                // Splitting text to create hover event.
                String[] arg = holder.split(between);
                TextComponent message = new TextComponent(arg[0]
                        .replace(prefix, "")
                        .replace("<target>", target.getName())
                );

                TextComponent hover = new TextComponent(between);
                hover.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(LanguageHandler.SEARCH_HOVER.toString())));
                hover.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        ChatColor.stripColor(bStaffHolder.getStaffHolderMessage(target, LanguageHandler.SEARCH_COMMAND.toString()))));

                TextComponent message2 = new TextComponent(arg[1].replace(suffix, ""));
                player.sendMessage(message, hover, message2);
                continue;
            }

            player.sendMessage(bStaffHolder.getStaffHolder(target, "COMMAND", s));
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
