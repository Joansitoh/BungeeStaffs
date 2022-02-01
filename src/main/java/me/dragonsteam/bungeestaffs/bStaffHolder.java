package me.dragonsteam.bungeestaffs;

import me.dragonsteam.bungeestaffs.utils.PlayerCache;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.formats.TextFormatReader;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class bStaffHolder {

    public static String getStaffHolderMessage(@Nullable PlayerCache player, String text) {
        if (player != null) {
            text = text
                    .replace("<server>", player.getServer())
                    .replace("<raw_server>", player.getRawServer())
                    .replace("<player>", player.getName())
                    .replace("<prefix>", player.getPrefix())
                    .replace("<suffix>", player.getPrefix());
        } else {
            if (bStaffs.isRedisPresent()) {
                for (String server : bStaffs.getRedisHandler().getApi().getServerToPlayers().keySet()) {
                    text = text.replace("<bungee_" + server + ">", bStaffs.getRedisHandler().getApi().getPlayersOnServer(server).size() + "");
                }

                text = text.replace("<bungee_online>", bStaffs.getRedisHandler().getApi().getPlayerCount() + "");
            } else {
                for (ServerInfo info : bStaffs.INSTANCE.getProxy().getServers().values()) {
                    text = text.replace("<bungee_" + info.getName() + ">", info.getPlayers().size() + "");
                }

                text = text.replace("<bungee_online>", bStaffs.INSTANCE.getProxy().getOnlineCount() + "");
            }
        }

        text = text
                .replace("<chat_bar>", ChatUtils.CHAT_BAR)
                .replace("<medium_chat_bar>", ChatUtils.MEDIUM_CHAT_BAR)
        ;

        return ChatUtils.translate(text);
    }

    public static BaseComponent[] getStaffHolder(PlayerCache playerCache, ProxiedPlayer viewer, String text, String message) {
        return new BaseComponent[]{TextFormatReader.testNewPattern(viewer, getStaffHolderMessage(playerCache, text), message)};
    }

    public static HashMap<String, String> getLinedArguments(String text) {
        HashMap<String, String> hash = new HashMap<>();

        Pattern pattern = Pattern.compile("(-[a-z]) (.*?\\S*)");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            hash.put(matcher.group(1), matcher.group(2));
        }

        return hash;
    }

}
