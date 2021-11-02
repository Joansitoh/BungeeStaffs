package me.dragonsteam.bungeestaffs.utils.defaults;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Chats;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ChatUtils {

    private static final HashMap<ProxiedPlayer, HashMap<Comms, Boolean>> toggledMap = new HashMap<>();
    private static final HashMap<ProxiedPlayer, HashMap<Chats, Boolean>> toggledChatMap = new HashMap<>();

    public static String MENU_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------";
    public static String M_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------";
    public static String CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------------------------------";
    public static String MEDIUM_CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "------------------------------";

    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> translate(String... s) {
        List<String> list = new ArrayList<>();
        for (String x : s) list.add(translate(x));
        return list;
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

    public static boolean isToggledCommand(ProxiedPlayer player, Comms comms) {
        toggledMap.putIfAbsent(player, new HashMap<>());
        HashMap<Comms, Boolean> hash = toggledMap.get(player);

        hash.putIfAbsent(comms, false);
        return hash.get(comms);
    }

    public static void togglePlayerCommand(ProxiedPlayer player, Comms comms) {
        toggledMap.putIfAbsent(player, new HashMap<>());
        HashMap<Comms, Boolean> hash = toggledMap.get(player);

        hash.putIfAbsent(comms, false);
        hash.put(comms, !hash.get(comms));
    }

    public static boolean isToggledChat(ProxiedPlayer player, Chats chats) {
        toggledChatMap.putIfAbsent(player, new HashMap<>());
        HashMap<Chats, Boolean> hash = toggledChatMap.get(player);

        hash.putIfAbsent(chats, false);
        return hash.get(chats);
    }

    public static void togglePlayerChat(ProxiedPlayer player, Chats chats) {
        toggledChatMap.putIfAbsent(player, new HashMap<>());
        HashMap<Chats, Boolean> hash = toggledChatMap.get(player);

        hash.putIfAbsent(chats, false);
        hash.put(chats, !hash.get(chats));
    }

    public static List<String> translate(List<String> input) {
        return input.stream().map(ChatUtils::translate).collect(Collectors.toList());
    }

}
