package me.dragonsteam.bungeestaffs.utils.formats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.dragonsteam.bungeestaffs.bStaffHolder;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.commands.cCommand;
import me.dragonsteam.bungeestaffs.commands.types.ChatSpyCMD;
import me.dragonsteam.bungeestaffs.loaders.ChatsHandler;
import me.dragonsteam.bungeestaffs.loaders.CommandHandler;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
import me.dragonsteam.bungeestaffs.utils.PlayerCache;
import me.dragonsteam.bungeestaffs.utils.defaults.ToggleUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

/**
 * Class for project BungeeStaffs
 * Date: 28/01/2022 - 12:19.
 *
 * @author Joansiitoh
 */
public class RedisMessageFormat {

    public static String SPLIT_FORMAT = "<bs--ta-ff-s--pltt__-tt-t";

    public static void sendMessage(MessageType type, boolean redis, String... args) {
        if (redis) {
            String index = type.getName() + SPLIT_FORMAT;

            int x = 0;
            StringBuilder builder = new StringBuilder(index);
            for (String arg : args) builder.append(arg).append(x == args.length - 1 ? "" : SPLIT_FORMAT);
            bStaffs.getRedisHandler().getApi().sendChannelMessage(bStaffs.getRedisHandler().CHANNEL, builder.toString());
            return;
        }

        if (type.equals(MessageType.CHAT)) {
            String chat = args[0], sender = args[1], message = args[2];

            ChatsHandler chats = ChatsHandler.getChatByInput(chat);
            PlayerCache playerCache = new PlayerCache(Document.parse(sender));

            for (ProxiedPlayer player : bStaffs.INSTANCE.getProxy().getPlayers()) {
                if (!hasPermission(player, chats.getPermission())) continue;
                if (ToggleUtils.isToggledChat(player, chats)) continue;
                player.sendMessage(chats.getPlayerFormat(playerCache, player, message));
            }
            return;
        }

        if (type.equals(MessageType.SPY_STAFF_MESSAGE)) {
            String sender = args[0], target = args[1], message = args[2];
            PlayerCache playerCache = new PlayerCache(Document.parse(sender));
            PlayerCache targetCache = new PlayerCache(Document.parse(target));

            for (ProxiedPlayer p : bStaffs.INSTANCE.getProxy().getPlayers()) {
                if (ChatSpyCMD.getPlayerList().contains(p.getUniqueId()))
                    p.sendMessage(LanguageHandler.CHAT_SPY_PREFIX.toString()
                            .replace("<player>", playerCache.getName())
                            .replace("<target>", targetCache.getName())
                     + message);
            }
            return;
        }

        if (type.equals(MessageType.MSG)) {
            String command = args[0], sender = args[1], target = args[2], message = args[3];

            CommandHandler comms = CommandHandler.getCommandByName(command);
            PlayerCache playerCache = new PlayerCache(Document.parse(sender)), targetCache = new PlayerCache(Document.parse(target));
            ProxiedPlayer targetPlayer = bStaffs.INSTANCE.getProxy().getPlayer(targetCache.getName());
            if (targetPlayer == null) return;

            //target = bStaffs.INSTANCE.getProxy().getPlayer(lastTarget.get(player.getUniqueId()));
            if (ToggleUtils.isToggledCommand(targetPlayer, comms)) return;
            targetPlayer.sendMessage(comms.getPlayerFormat(playerCache, new PlayerCache(targetPlayer), message));
            cCommand.lastTarget.put(playerCache.getName(), targetCache.getName());
            cCommand.lastTarget.put(targetCache.getName(), playerCache.getName());
            return;
        }

        if (type.equals(MessageType.COMMAND)) {
            String command = args[0], sender = args[1], result = args[2], target = null;

            CommandHandler comms = CommandHandler.getCommandByName(command);
            PlayerCache playerCache = null, targetCache = null;

            if (args.length > 3) target = args[3];
            try { playerCache = new PlayerCache(Document.parse(sender));
            } catch (Exception ignored) {}

            if (target != null) {
                try { targetCache = new PlayerCache(Document.parse(target));
                } catch (Exception ignored) {}
            }

            for (ProxiedPlayer player : bStaffs.INSTANCE.getProxy().getPlayers()) {
                if (!hasPermission(player, comms.getReceivePermission())) continue;
                if (ToggleUtils.isToggledCommand(player, comms)) continue;
                player.sendMessage(comms.getPlayerFormat(playerCache, targetCache, result));
            }
            return;
        }

        if (type.equals(MessageType.STAFF_MOVE)) {
            String permission = args[0], sender = args[1], language = args[2];
            PlayerCache playerCache = new PlayerCache(Document.parse(sender));

            for (ProxiedPlayer player : bStaffs.INSTANCE.getProxy().getPlayers()) {
                if (hasPermission(player, permission)) {
                    player.sendMessage(bStaffHolder.getStaffHolder(playerCache, player, language, ""));
                }
            }
        }
    }

    private static boolean hasPermission(ProxiedPlayer player, String permission) {
        if (permission == null || permission.equals("")) return true;
        return player.hasPermission(permission);
    }

    @Getter @AllArgsConstructor
    public enum MessageType {

        MSG("msg"),
        CHAT("chat"),
        COMMAND("command"),
        STAFF_MOVE("staff-move"),
        SPY_STAFF_MESSAGE("spy-staff-message"),

        ;

        private String name;

    }

}
