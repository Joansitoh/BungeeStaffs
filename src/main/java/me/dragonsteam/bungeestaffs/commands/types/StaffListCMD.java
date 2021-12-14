package me.dragonsteam.bungeestaffs.commands.types;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.loaders.Lang;
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
        for (String s : Lang.STAFF_LIST.toList()) {
            if (s.contains("<player>")) {
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (player.hasPermission("bstaffs.staff"))
                        sender.sendMessage(bStaffHolder.getStaffHolder(player, "COMMAND", s));
                }
                continue;
            }

            // Check if sender instanceof ProxiedPlayer.
            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) sender;
                sender.sendMessage(bStaffHolder.getStaffHolder(player, "COMMAND", s));
            } else sender.sendMessage(ChatUtils.translate(s));
        }
    }
}