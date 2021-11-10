package me.dragonsteam.bungeestaffs.utils.defaults;

import me.dragonsteam.bungeestaffs.loaders.Chats;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 09/11/2021 - 20:37.
 */
public class ToggleUtils {

    private static final HashMap<ProxiedPlayer, HashMap<Comms, Boolean>> toggledMap = new HashMap<>();
    private static final HashMap<ProxiedPlayer, HashMap<Chats, Boolean>> toggledChatMap = new HashMap<>();

    ///////////////////////////////////////////////////////////////////////////

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

}
