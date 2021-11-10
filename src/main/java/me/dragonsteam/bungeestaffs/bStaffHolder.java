package me.dragonsteam.bungeestaffs;

import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class bStaffHolder {

    public static String getStaffHolder(ProxiedPlayer player, String s) {
        String message = s;
        if (player != null) {
            ConfigFile config = bStaffs.INSTANCE.getSettingsFile();
            String server = "", name = player.getName();

            try {
                if (config.getBoolean("USE-BUNGEE-MOTD"))
                    server = player.getServer().getInfo().getMotd();
                else server = player.getServer().getInfo().getName();
            } catch (Exception ignore) {}

            message = message.replace("<server>", server).replace("<player>", name);
        }

        message = message
                .replace("<chat_bar>", ChatUtils.CHAT_BAR)
                .replace("<medium_chat_bar>", ChatUtils.MEDIUM_CHAT_BAR)
        ;

        return ChatUtils.translate(message);
    }

}
