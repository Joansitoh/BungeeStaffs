package me.dragonsteam.bungeestaffs.commands.types;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ServerListCMD extends Command {

    public ServerListCMD() {
        super("serverlist", "bstaffs.serverlist", "glist", "svlist");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        boolean listened = false;
        for (String s : LanguageHandler.SERVER_LIST.toList()) {
            if (s.contains("<server>") || s.contains("<raw_server>") && !listened) {
                listened = true;
                for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                    StringBuilder players = new StringBuilder();
                    int index = 0;
                    for (ProxiedPlayer player : server.getPlayers())
                        players.append(player.getName()).append(index == ProxyServer.getInstance().getPlayers().size() - 1 ? "" : ", ");

                    String text = s
                            .replace("<server>", server.getMotd())
                            .replace("<raw_server>", server.getName())
                            .replace("<server_online>", server.getPlayers().size() + "")
                            .replace("<server_players>", players.toString())
                    ;

                    if (sender instanceof ProxiedPlayer) {
                        ProxiedPlayer player = (ProxiedPlayer) sender;
                        sender.sendMessage(bStaffHolder.getStaffHolder(player, player, text, ""));
                    } else sender.sendMessage(TextComponent.toPlainText(bStaffHolder.getStaffHolder(null, null, text, "")));
                }
                continue;
            }

            // Check if sender instanceof ProxiedPlayer.
            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) sender;
                sender.sendMessage(bStaffHolder.getStaffHolder(player, player, s, ""));
            } else sender.sendMessage(TextComponent.toPlainText(bStaffHolder.getStaffHolder(null, null, s, "")));
        }
    }
}