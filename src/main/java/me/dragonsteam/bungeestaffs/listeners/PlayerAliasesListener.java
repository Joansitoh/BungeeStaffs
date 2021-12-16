package me.dragonsteam.bungeestaffs.listeners;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Aliases;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 16/12/2021 - 1:46.
 */
public class PlayerAliasesListener implements Listener {

    @EventHandler
    public void onPlayerUseAliases(ChatEvent e) {
        if (!e.isCommand()) return;
        if (!(e.getSender() instanceof ProxiedPlayer)) return;
        String command = e.getMessage().substring(1);
        ProxiedPlayer p = (ProxiedPlayer) e.getSender();

        ConfigFile file = bStaffs.INSTANCE.getAliasesFile();
        for (String s : file.getStringList("BLACKLIST-SERVERS")) {
            if (p.getServer().getInfo().getName().equalsIgnoreCase(s)) return;
        }

        if (command.split(" ").length > 1) return;
        Aliases aliases = Aliases.getAlias(command);
        if (aliases == null) return;

        if (aliases.getDisabled() != null && !aliases.getDisabled().isEmpty()) {
            for (String s : aliases.getDisabled())
                if (p.getServer().getInfo().getName().equalsIgnoreCase(s)) return;
        }

        e.setCancelled(true);

        if (aliases.getPermission() == null || aliases.getPermission() != null && p.hasPermission(aliases.getPermission())) {
            if (p.getServer().getInfo().getName().equals(aliases.getServer())) {
                p.sendMessage(Lang.ALIASES_ALREADY_TELEPORT.toString(true).replace("<server>", aliases.getServer()));
                return;
            }

            p.connect(bStaffs.INSTANCE.getProxy().getServerInfo(aliases.getServer()));
            p.sendMessage(Lang.ALIASES_TELEPORT.toString(true).replace("<server>", aliases.getServer()));
        } else p.sendMessage(Lang.NO_PERMISSION.toString(true));

    }

}
