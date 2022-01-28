package me.dragonsteam.bungeestaffs.commands.types;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
import me.dragonsteam.bungeestaffs.utils.PlayerCache;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffListCMD extends Command {

    public StaffListCMD() {
        super("stafflist", "bstaffs.list");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String s : LanguageHandler.STAFF_LIST.toList()) {
            if (s.contains("<player>")) {
                if (bStaffs.isRedisPresent()) {
                    for (PlayerCache cache : bStaffs.getRedisHandler().getStaffsHash().values()) {
                        sender.sendMessage(bStaffHolder.getStaffHolder(cache, null, s, ""));
                    }
                } else {
                    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                        if (player.hasPermission("bstaffs.staff")) {
                            sender.sendMessage(bStaffHolder.getStaffHolder(new PlayerCache(player), player, s, ""));
                        }
                    }
                }
                continue;
            }

            // Check if sender instanceof ProxiedPlayer.
            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) sender;
                sender.sendMessage(bStaffHolder.getStaffHolder(new PlayerCache(player), player, s, ""));
            } else sender.sendMessage(bStaffHolder.getStaffHolder(null, null, s, ""));
        }
    }
}