package me.dragonsteam.bungeestaffs.utils.defaults;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChatUtils {

    public static String CHAT_BAR = ChatColor.STRIKETHROUGH + "------------------------------------------------";
    public static String MEDIUM_CHAT_BAR = ChatColor.STRIKETHROUGH + "------------------------------";

    public static String translate(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static List<String> translate(String... input) {
        return translate(Arrays.asList(input));
    }

    public static List<String> translate(List<String> input) {
        return input.stream().map(ChatUtils::translate).collect(Collectors.toList());
    }

    public static boolean startsWithIgnoreCase(final String string, final String prefix) {
        if (string.length() < prefix.length())
            return false;
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static String substringBetween(String str, String open, String close) {
        if (str != null && open != null && close != null) {
            int start = str.indexOf(open);
            if (start != -1) {
                int end = str.indexOf(close, start + open.length());
                if (end != -1) {
                    return str.substring(start + open.length(), end);
                }
            }

        }
        return null;
    }

    public static void setDefaultIfNotSet(Configuration section, String path, Object value) {
        try {
            if (section != null) {
                if (!section.contains(path))
                    section.set(path, value);
            }
        } catch (Exception e) {
        }
    }

}
