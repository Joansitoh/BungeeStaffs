package me.dragonsteam.bungeestaffs.managers.hooks;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import lombok.Getter;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.managers.HookHandler;
import me.dragonsteam.bungeestaffs.utils.PlayerCache;
import me.dragonsteam.bungeestaffs.utils.RedisMessageFormat;
import me.dragonsteam.bungeestaffs.utils.defaults.Runnables;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class for project BungeeStaffs
 * Date: 27/01/2022 - 12:14.
 *
 * @author Joansiitoh
 */
@Getter
public class RedisBungeeHandler extends HookHandler implements Listener {

    private HashMap<String, PlayerCache> staffsHash;

    private RedisBungeeAPI api;
    private ScheduledTask task;

    public final String CHANNEL = "BungeeStaffMessage";

    public RedisBungeeHandler() {
        super("RedisBungee");
    }

    @Override
    public void setup() {
        this.api = RedisBungeeAPI.getRedisBungeeApi();
        this.api.registerPubSubChannels(CHANNEL);
        bStaffs.INSTANCE.getProxy().getPluginManager().registerListener(bStaffs.INSTANCE, this);

        staffsHash = new HashMap<>();

        /**
         * Task to update the staff-list sending message through redis channel.
         */
        task = Runnables.runTimerAsync(() -> {
            HashMap<String, PlayerCache> clone = new HashMap<>(staffsHash);
            List<String> pending = new ArrayList<>();

            for (String staff : new ArrayList<>(clone.keySet())) {
                PlayerCache playerCache = clone.get(staff);

                boolean found = false;
                /**
                 * Check if the player is still in the same server and if he is still online.
                 */
                for (String server : api.getServerToPlayers().keySet()) {
                    if (playerCache.getRawServer().equals(server)) {
                        found = true;
                        break;
                    }
                }

                if (!found) pending.add(staff);
                if (!api.getHumanPlayersOnline().contains(staff) && !pending.contains(staff)) pending.add(staff);
            }

            /* We remove the pending staffs from the staffHash */
            for (String staff : pending) staffsHash.remove(staff);

            api.sendChannelMessage(CHANNEL, "staffs<split>" + getStaffPlayers());
            for (ProxiedPlayer player : bStaffs.INSTANCE.getProxy().getPlayers()) {
                if (hasPermission(player, "bstaffs.staff"))
                    staffsHash.put(player.getName(), new PlayerCache(player));
            }
        }, 1, 1);
    }

    public void disable() {
        if (task != null) task.cancel();
        this.api.unregisterPubSubChannels(CHANNEL);
    }

    ///////////////////////////////////////////////////////////////////////////

    @EventHandler
    public void onRedisBungeeMessage(PubSubMessageEvent event) {
        if (!event.getChannel().equals(CHANNEL)) return;
        String message = event.getMessage();
        String[] split = message.split("<split>");

        // Global variables
        String channel = split[0];
        if (channel.equals("staffs")) {
            for (int i = 1; i < split.length; i++) {
                String cache = split[i];
                PlayerCache playerCache = new PlayerCache(Document.parse(cache));
                staffsHash.put(playerCache.getName(), playerCache);
            }
            return;
        }

        String[] args = new String[split.length - 1];
        System.arraycopy(split, 1, args, 0, split.length - 1);

        switch (channel) {
            case "chat":
                RedisMessageFormat.sendMessage(RedisMessageFormat.MessageType.CHAT, false, args);
                break;
            case "command":
                RedisMessageFormat.sendMessage(RedisMessageFormat.MessageType.COMMAND, false, args);
                break;
            case "staff_move":
                RedisMessageFormat.sendMessage(RedisMessageFormat.MessageType.STAFF_MOVE, false, args);
                break;
        }
/*

        if (channel.equals("chat")) {
            String chat = split[1], sender = split[2], text = split[3];
            ChatsHandler chats = ChatsHandler.getChatByInput(chat);
            PlayerCache playerCache = new PlayerCache(Document.parse(sender));

            for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
                if (!hasPermission(p, chats.getPermission())) continue;
                if (ToggleUtils.isToggledChat(p, chats)) continue;
                p.sendMessage(chats.getPlayerFormat(playerCache, p, text));
            }
            return;
        }

        if (channel.equals("command")) {
            String command = split[1], cache = split[2], result = split[3], target = null;
            CommandHandler comms = CommandHandler.getCommandByName(command);
            PlayerCache playerCache = null, targetCache = null;

            if (split.length > 4) target = split[4];

            try {
                playerCache = new PlayerCache(Document.parse(cache));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (target != null) {
                try {
                    targetCache = new PlayerCache(Document.parse(target));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (ProxiedPlayer player : bStaffs.INSTANCE.getProxy().getPlayers()) {
                if (!hasPermission(player, comms.getReceivePermission())) continue;
                if (ToggleUtils.isToggledCommand(player, comms)) continue;
                player.sendMessage(comms.getPlayerFormat(playerCache, targetCache, result));
            }
            return;
        }

        if (channel.equals("staff_move")) {
            String permission = split[1], sender = split[2], language = split[3];
            PlayerCache playerCache = new PlayerCache(Document.parse(sender));

            for (ProxiedPlayer player : bStaffs.INSTANCE.getProxy().getPlayers()) {
                if (hasPermission(player, permission))
                    player.sendMessage(bStaffHolder.getStaffHolder(playerCache, player, language, ""));
            }
            return;
        }*/
    }

    private boolean hasPermission(ProxiedPlayer player, String permission) {
        if (permission == null || permission.equals("")) return true;
        return player.hasPermission(permission);
    }

    private String getStaffPlayers() {
        StringBuilder builder = new StringBuilder();
        int index = bStaffs.INSTANCE.getProxy().getPlayers().size();
        for (ProxiedPlayer player : bStaffs.INSTANCE.getProxy().getPlayers()) {
            if (hasPermission(player, "bstaffs.staff"))
                builder.append(new PlayerCache(player).toJson()).append(index-- > 1 ? "<split>" : "");
            index--;
        }

        return builder.toString();
    }


}
