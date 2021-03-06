package me.dragonsteam.bungeestaffs.utils;

import me.dragonsteam.bungeestaffs.loaders.CommandHandler;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 17:55.
 */
public class TimerUtils {

    private static final HashMap<ProxiedPlayer, HashMap<CommandHandler, Long>> timerHash = new HashMap<>();

    public static boolean hasCooldown(ProxiedPlayer player, CommandHandler command) {
        if (!timerHash.containsKey(player)) return false;
        return timerHash.get(player).getOrDefault(command, 0L) > System.currentTimeMillis();
    }

    public static int getPlayerCooldown(ProxiedPlayer player, CommandHandler command) {
        HashMap<CommandHandler, Long> hash = timerHash.getOrDefault(player, new HashMap<>());
        return (int) ((hash.get(command) - System.currentTimeMillis()) / 1000);
    }

    public static void setPlayerCooldown(ProxiedPlayer player, CommandHandler command, int seconds) {
        HashMap<CommandHandler, Long> hash = timerHash.getOrDefault(player, new HashMap<>());
        hash.put(command, System.currentTimeMillis() + (seconds * 1000L));
        timerHash.put(player, hash);
    }

}
