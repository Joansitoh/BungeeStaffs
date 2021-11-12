package me.dragonsteam.bungeestaffs.listeners;

import io.netty.util.internal.StringUtil;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Chats;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.utils.CommandType;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 09/11/2021 - 17:53.
 */
public class PlayerCompleteListener implements Listener {

    private static final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", Pattern.LITERAL);

    @EventHandler
    public void onTabComplete(TabCompleteEvent e) {
        if (!e.getCursor().startsWith("/")) return;

        // Return if is not proxied player.
        if (!(e.getSender() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();

        String cmdLine = e.getCursor().substring(1);
        int spaceIndex = cmdLine.indexOf(' ');

        if (spaceIndex == -1) {
            ArrayList<String> completions = new ArrayList<>();
            Map<String, Command> knownCommands = bStaffs.INSTANCE.getKnownCommands();

            final String prefix = "/";

            for (Map.Entry<String, Command> commandEntry : knownCommands.entrySet()) {
                Command command = commandEntry.getValue();

                if (!command.hasPermission(player)) continue;
                String name = commandEntry.getKey(); // Use the alias, not command name
                if (ChatUtils.startsWithIgnoreCase(name, cmdLine)) completions.add(prefix + name);
            }

            completions.sort(String.CASE_INSENSITIVE_ORDER);
            e.getSuggestions().addAll(completions);
        }
    }

}
