package me.dragonsteam.bungeestaffs.commands.types;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import me.dragonsteam.bungeestaffs.utils.defaults.Runnables;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 01/12/2021 - 3:02.
 */
public class RestartCMD extends Command implements Listener {

    private final HashMap<UUID, String> waitingPlayers = new HashMap<>();
    private final HashMap<String, Integer> waitingTicks = new HashMap<>();
    private final HashMap<String, Boolean> waitingServers = new HashMap<>();

    private final HashMap<String, ScheduledTask> tasksMap = new HashMap<>();
    private final HashMap<Integer, Boolean> portsMap = new HashMap<>();

    public RestartCMD() {
        super("svrestart", "bstaffs.svrestart");
        bStaffs.INSTANCE.getProxy().getPluginManager().registerListener(bStaffs.INSTANCE, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ConfigFile file = bStaffs.INSTANCE.getSettingsFile();
            ProxiedPlayer player = (ProxiedPlayer) sender;
            ServerInfo server = player.getServer().getInfo();

            waitingServers.put(server.getName(), true);

            Configuration config = file.getConfiguration().getSection("SERVERS-CONFIG.LIMBO");
            if (config == null) {
                player.sendMessage(Lang.LIMBO_NOT_SET.toString(true));
                return;
            }

            // Checking if configuration contains the limbo server
            if (!config.contains("NAME")) {
                player.sendMessage(Lang.LIMBO_NOT_SET.toString(true));
                return;
            }

            // Checking if limbo server exist.
            String limboName = config.getString("NAME");
            ServerInfo limbo = bStaffs.INSTANCE.getProxy().getServerInfo(limboName);
            if (bStaffs.INSTANCE.getProxy().getServerInfo(limboName) == null) {
                player.sendMessage(Lang.LIMBO_NOT_SET.toString(true));
                return;
            }

            player.chat(config.getString("STOP-COMMAND"));

            for (ProxiedPlayer online : server.getPlayers()) {
                online.connect(limbo);
                online.sendMessage(Lang.LIMBO_JOIN.toString(true));
                waitingPlayers.put(online.getUniqueId(), server.getName());
            }

            int timeout = config.contains("TIMEOUT") ? config.getInt("TIMEOUT") : 10;
            int countdown = config.contains("TP-COOLDOWN") ? config.getInt("TP-COOLDOWN") : 15;

            // Checking if limbo is online.
            Socket socket = new Socket();
            try {
                socket.connect(new InetSocketAddress("localhost", limbo.getAddress().getPort()), 40);
                // Execute the stop command by player.
                waitingTicks.put(server.getName(), 0);
                tasksMap.put(server.getName(), Runnables.runTimer(() -> {
                    if (waitingTicks.containsKey(server.getName())) {
                        if (waitingTicks.get(server.getName()) == timeout) {
                            clearServerPlayers(server.getName(), Lang.LIMBO_SERVER_NOT_REACHED.toString(true)
                                    .replace("<server>", server.getName()));
                            return;
                        }
                    }

                    waitingTicks.put(server.getName(), waitingTicks.get(server.getName()) + 1);
                    reach(server.getAddress().getPort());
                    boolean reached = portsMap.get(server.getAddress().getPort());

                    if (waitingServers.containsKey(server.getName())) {
                        if (!reached) waitingServers.remove(server.getName());
                        return;
                    }

                    if (reached) {
                        tasksMap.get(server.getName()).cancel();
                        portsMap.remove(server.getAddress().getPort());

                        for (UUID uuid : getServerPlayers(server.getName())) {
                            ProxiedPlayer online = bStaffs.INSTANCE.getProxy().getPlayer(uuid);
                            if (online != null)
                                online.sendMessage(Lang.LIMBO_SERVER_REACHED.toString(true).replace("<server>", server.getName()));
                        }

                        // Teleport all players to old server.
                        Runnables.runLater(() -> {
                            for (UUID uuid : getServerPlayers(server.getName())) {
                                ProxiedPlayer online = bStaffs.INSTANCE.getProxy().getPlayer(uuid);
                                if (online != null) online.connect(server);
                                waitingPlayers.remove(uuid);
                            }
                        }, countdown, TimeUnit.SECONDS);
                    }
                }, 1, 1));

                socket.close();
            } catch (Exception e) {
                clearServerPlayers(server.getName(), Lang.LIMBO_NOT_SET.toString(true));
            }
        }
    }

    public void clearServerPlayers(String server, String message) {
        if (tasksMap.get(server) != null) tasksMap.get(server).cancel();
        waitingServers.remove(server);
        getServerPlayers(server).forEach(uuid -> {
            waitingPlayers.remove(uuid);

            ProxiedPlayer online = bStaffs.INSTANCE.getProxy().getPlayer(uuid);
            if (online != null) {
                String fallback = bStaffs.INSTANCE.getRandomFallbackServer();
                if (fallback != null) {
                    online.connect(bStaffs.INSTANCE.getProxy().getServerInfo(fallback));
                    online.sendMessage(message);
                }
            }
        });
    }

    public List<UUID> getServerPlayers(String server) {
        return waitingPlayers.keySet().stream().filter(uuid -> waitingPlayers.get(uuid).equals(server)).collect(Collectors.toList());
    }

    @EventHandler
    public void onPlayerSwitch(ServerSwitchEvent e) {
        if (waitingPlayers.containsKey(e.getPlayer().getUniqueId())) {
            ConfigFile file = bStaffs.INSTANCE.getSettingsFile();
            Configuration config = file.getConfiguration().getSection("SERVERS-CONFIG.LIMBO");
            if (config == null) return;

            if (e.getPlayer().getServer().getInfo().getName().equals(config.getString("NAME"))) return;
            if (config.contains("PREVENT-MOVE") && config.getBoolean("PREVENT-MOVE")) {
                e.getPlayer().sendMessage(Lang.LIMBO_PREVENT_MOVE.toString(true));
                e.getPlayer().connect(e.getFrom());
                return;
            }

            waitingPlayers.remove(e.getPlayer().getUniqueId());
            e.getPlayer().sendMessage(Lang.LIMBO_LEAVE.toString(true));
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    private void reach(int port) {
        portsMap.putIfAbsent(port, true);
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("localhost", port), 40);
            socket.close();
            portsMap.put(port, true);
        } catch (IOException ignored) {
            portsMap.put(port, false);
        }
    }


}
