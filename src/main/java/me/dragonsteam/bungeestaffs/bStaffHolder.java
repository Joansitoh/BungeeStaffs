package me.dragonsteam.bungeestaffs;

import me.dragonsteam.bungeestaffs.managers.HookHandler;
import me.dragonsteam.bungeestaffs.managers.HookManager;
import me.dragonsteam.bungeestaffs.managers.hooks.LuckPermsHandler;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import me.dragonsteam.bungeestaffs.utils.formats.TextFormatReader;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class bStaffHolder {

    private static boolean tried = false;

    public static String getStaffHolderMessage(ProxiedPlayer player, String text) {
        if (player != null && player instanceof ProxiedPlayer) {
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

            text = text
                    .replace("<server>", server)
                    .replace("<raw_server>", raw_server)
                    .replace("<player>", name)
                    .replace("<prefix>", prefix)
                    .replace("<suffix>", suffix);
        }

        text = text
                .replace("<chat_bar>", ChatUtils.CHAT_BAR)
                .replace("<medium_chat_bar>", ChatUtils.MEDIUM_CHAT_BAR)
        ;

        return ChatUtils.translate(text);
    }

    public static BaseComponent[] getStaffHolder(ProxiedPlayer player, ProxiedPlayer viewer, String text, String message) {
        return new BaseComponent[]{TextFormatReader.testNewPattern(viewer, getStaffHolderMessage(player, text), message)};
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
