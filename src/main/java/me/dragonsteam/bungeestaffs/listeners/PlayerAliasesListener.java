package me.dragonsteam.bungeestaffs.listeners;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.AliasesHandler;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
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
        AliasesHandler aliasesHandler = AliasesHandler.getAlias(command);
        if (aliasesHandler == null) return;

        if (aliasesHandler.getDisabled() != null && !aliasesHandler.getDisabled().isEmpty()) {
            for (String s : aliasesHandler.getDisabled())
                if (p.getServer().getInfo().getName().equalsIgnoreCase(s)) return;
        }

        e.setCancelled(true);

        if (checkPermission(p, aliasesHandler.getPermission())) {
            if (p.getServer().getInfo().getName().equals(aliasesHandler.getServer())) {
                p.sendMessage(LanguageHandler.ALIASES_ALREADY_TELEPORT.toString(true).replace("<server>", aliasesHandler.getServer()));
                return;
            }

            p.connect(bStaffs.INSTANCE.getProxy().getServerInfo(aliasesHandler.getServer()));
            p.sendMessage(LanguageHandler.ALIASES_TELEPORT.toString(true).replace("<server>", aliasesHandler.getServer()));
        } else p.sendMessage(LanguageHandler.NO_PERMISSION.toString(true));

    }

    private boolean checkPermission(ProxiedPlayer p, String permission) {
        if (permission == null) return true;
        if (permission.equalsIgnoreCase("")) return true;
        return p.hasPermission(permission);
    }

}
