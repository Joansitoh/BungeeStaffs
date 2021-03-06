package me.dragonsteam.bungeestaffs;

import de.themoep.minedown.MineDown;
import me.dragonsteam.bungeestaffs.managers.HookHandler;
import me.dragonsteam.bungeestaffs.managers.HookManager;
import me.dragonsteam.bungeestaffs.managers.hooks.LuckPermsHandler;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import me.dragonsteam.bungeestaffs.utils.formats.TextFormatReader;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class bStaffHolder {

    private static boolean tried = false;

    public static String getStaffHolderMessage(ProxiedPlayer player, String s) {
        String message = s;
        if (player != null) {
            ConfigFile config = bStaffs.INSTANCE.getSettingsFile();
            String server = "", raw_server = "", name = player.getName();
            String prefix = "", suffix = "";

            // Checking the server motd method.
            try {
                if (config.getBoolean("USE-BUNGEE-MOTD"))
                    server = player.getServer().getInfo().getMotd();
                else server = player.getServer().getInfo().getName();

                raw_server = player.getServer().getInfo().getName();
            } catch (Exception ignore) {}

            // Loading prefix and suffix using LuckPerms.
            try {
                HookHandler handler = bStaffs.INSTANCE.getHookManager().getHandler("LuckPerms");
                if (handler != null) {
                    if (!handler.isLoaded() && !tried) {
                        handler.setup();
                        tried = true;
                    }

                    LuckPermsHandler luckPermsHandler = (LuckPermsHandler) handler;
                    if (luckPermsHandler.getPrefix(player.getUniqueId()) != null)
                        prefix = luckPermsHandler.getPrefix(player.getUniqueId());

                    if (luckPermsHandler.getSuffix(player.getUniqueId()) != null)
                        suffix = luckPermsHandler.getSuffix(player.getUniqueId());
                }
            } catch (Exception e) {
                if (!tried) bStaffs.logger("LuckPerms hook is not installed or not working properly.");
            }

            message = message
                    .replace("<server>", server)
                    .replace("<raw_server>", raw_server)
                    .replace("<player>", name)
                    .replace("<prefix>", prefix)
                    .replace("<suffix>", suffix);
        }

        message = message
                .replace("<chat_bar>", ChatUtils.CHAT_BAR)
                .replace("<medium_chat_bar>", ChatUtils.MEDIUM_CHAT_BAR)
        ;

        return ChatUtils.translate(message);
    }

    public static BaseComponent[] getStaffHolder(ProxiedPlayer player, String method, String s) {
        Configuration section = bStaffs.INSTANCE.getSettingsFile().getConfiguration().getSection("COMPLEX-FORMAT");
        if (section != null && section.contains("USE-COMPLEX-FORMAT") && section.getBoolean("USE-COMPLEX-FORMAT")) {
            if (section.getStringList("WHITELIST").contains(method))
                return new BaseComponent[]{TextFormatReader.complexFormat(getStaffHolderMessage(player, s))};
        }

        return MineDown.parse(getStaffHolderMessage(player, s));
    }

    public static BaseComponent[] getStaffHolderBase(ProxiedPlayer player, String s) {
        return MineDown.parse(s);
    }

}
