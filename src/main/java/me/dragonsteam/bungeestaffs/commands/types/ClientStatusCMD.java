package me.dragonsteam.bungeestaffs.commands.types;

import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.List;

public class ClientStatusCMD extends Command {

    public ClientStatusCMD() {
        super("clientstatus", "bstaffs.clientstatus", "cstats", "cstatus", "clientstats");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for (String s : LanguageHandler.CLIENT_STATUS.toList()) {
            if (s.contains("<players_")) {
                String[] var = s.split("<players_");
                String[] finalVar = var[1].split(">"), numbers = finalVar[0].split("-");
                int min = Integer.parseInt(numbers[0]), max = Integer.parseInt(numbers[1]);

                String target = "<players_" + finalVar[0] + ">";

                try {
                    int count = getPlayersBetweenVersion(min, max).size();
                    s = s.replace(target, String.valueOf(count));
                } catch (Exception e) {
                    s = s.replace(target, "0");
                }
                List<ProxiedPlayer> players = getPlayersBetweenVersion(min, max);
            }

            if (s.contains("<percent_")) {
                String[] var = s.split("<percent_");
                String[] finalVar = var[1].split(">"), numbers = finalVar[0].split("-");
                int min = Integer.parseInt(numbers[0]), max = Integer.parseInt(numbers[1]);

                String target = "<percent_" + finalVar[0] + ">";

                try {
                    int total = ProxyServer.getInstance().getPlayers().size(), count = getPlayersBetweenVersion(min, max).size();
                    double percent = (double) count / (double) total * 100;
                    s = s.replace(target, String.valueOf(percent));
                } catch (Exception e) {
                    s = s.replace(target, "0");
                }
            }

            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) sender;
                sender.sendMessage(bStaffHolder.getStaffHolder(player, player, s, ""));
            } else sender.sendMessage(TextComponent.toPlainText(bStaffHolder.getStaffHolder(null, null, s, "")));
        }
    }

    private List<ProxiedPlayer> getPlayersBetweenVersion(int min, int max) {
        List<ProxiedPlayer> players = new ArrayList<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            int version = player.getPendingConnection().getVersion();
            if (version >= min && version <= max)
                players.add(player);
        }

        return players;
    }
}