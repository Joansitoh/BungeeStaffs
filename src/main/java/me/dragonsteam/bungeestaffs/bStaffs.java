package me.dragonsteam.bungeestaffs;

import lombok.Getter;
import me.dragonsteam.bungeestaffs.commands.types.*;
import me.dragonsteam.bungeestaffs.listeners.PlayerAliasesListener;
import me.dragonsteam.bungeestaffs.listeners.PlayerChatListener;
import me.dragonsteam.bungeestaffs.listeners.PlayerCompleteListener;
import me.dragonsteam.bungeestaffs.listeners.ServerMovementListener;
import me.dragonsteam.bungeestaffs.loaders.AliasesHandler;
import me.dragonsteam.bungeestaffs.loaders.ChatsHandler;
import me.dragonsteam.bungeestaffs.loaders.CommandHandler;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
import me.dragonsteam.bungeestaffs.managers.HookManager;
import me.dragonsteam.bungeestaffs.managers.hooks.RedisBungeeHandler;
import me.dragonsteam.bungeestaffs.utils.UpdateChecker;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import me.dragonsteam.bungeestaffs.utils.defaults.Runnables;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.bstats.bungeecord.Metrics;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 16:00.
 */
@Getter
public final class bStaffs extends Plugin {

    public static bStaffs INSTANCE;

    ///////////////////////////////////////////////////////////////////////////

    private HashMap<String, String> commands;
    private Map<String, Command> knownCommands;

    private ConfigFile settingsFile, commandsFile, chatsFile, messagesFile, aliasesFile, logsFile;
    private HookManager hookManager;
    private String configVersion;

    private ScheduledTask task;

    private JDA jda;

    ///////////////////////////////////////////////////////////////////////////

    public static boolean isRedisPresent() {
        return bStaffs.INSTANCE.getSettingsFile().getBoolean("REDIS-INTEGRATION") &&
                bStaffs.INSTANCE.getHookManager().getHandler("RedisBungee") != null;
    }

    public static RedisBungeeHandler getRedisHandler() {
        return (RedisBungeeHandler) bStaffs.INSTANCE.getHookManager().getHandler("RedisBungee");
    }

    ///////////////////////////////////////////////////////////////////////////

    public static void logger(String message) {
        logger(message, null);
    }

    public static void logger(String message, String subMsg) {
        INSTANCE.getProxy().getConsole().sendMessage(ChatUtils.translate(LanguageHandler.PREFIX.getDef() + (subMsg != null ? (subMsg + " &f") : "") + message));
    }

    public static void log(ProxiedPlayer player, String method, String message) {
        boolean chat = INSTANCE.getSettingsFile().getBoolean("EVENTS.LOGS.CHAT"), move = INSTANCE.getSettingsFile().getBoolean("EVENTS.LOGS.MOVE");
        boolean commands = INSTANCE.getSettingsFile().getBoolean("EVENTS.LOGS.COMMANDS");
        if (method.equalsIgnoreCase("chats") && !chat) return;
        if (method.equalsIgnoreCase("move") && !move) return;
        if (method.equalsIgnoreCase("commands") && !commands) return;

        boolean console = !INSTANCE.getSettingsFile().getString("EVENTS.LOGS.LOG-METHOD").equalsIgnoreCase("FILE");
        boolean both = INSTANCE.getSettingsFile().getString("EVENTS.LOGS.LOG-METHOD").equalsIgnoreCase("BOTH");

        if (console) {
            INSTANCE.getProxy().getConsole().sendMessage(message);
            if (!both) return;
        }

        message = ChatColor.stripColor(message);

        String timeFormat = "HH:mm:ss";
        String dateFormat = "dd/MM/yyyy";

        String date = new SimpleDateFormat(dateFormat).format(new Date()), time = new SimpleDateFormat(timeFormat).format(new Date());
        String prefix = "[" + date + " " + time + "] ";

        if (INSTANCE.getLogsFile().getConfiguration().contains(player.getUniqueId().toString())) {
            List<String> logs = INSTANCE.getLogsFile().getStringList(player.getUniqueId().toString());
            logs.add(prefix + message);
            INSTANCE.getLogsFile().getConfiguration().set(player.getUniqueId().toString(), logs);
        } else INSTANCE.getLogsFile().getConfiguration().set(player.getUniqueId().toString(), Collections.singletonList(prefix + message));
        Runnables.runAsync(() -> INSTANCE.getLogsFile().save());
    }

    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;

        // Loading plugin metrics.
        new Metrics(this, 13287);

        ////////////////////////////////////////////////////////////////////////////////

        // Loading recipients
        aliasesFile = new ConfigFile(this, "aliases.yml");
        settingsFile = new ConfigFile(this, "settings.yml");
        commandsFile = new ConfigFile(this, "commands.yml");
        chatsFile = new ConfigFile(this, "chats.yml");
        logsFile = new ConfigFile(this, "logs.yml");

        new File(getDataFolder(), "langs").mkdir();
        String lang = "en_US";
        if (settingsFile.getConfiguration().contains("LANGUAGE")) lang = settingsFile.getString("LANGUAGE");
        messagesFile = new ConfigFile(this, "langs/" + lang + ".yml");

        configVersion = "0.2.6";
        commands = new HashMap<>();
        knownCommands = new HashMap<>();

        Runnables.runLater(() -> hookManager = new HookManager(), 3, TimeUnit.SECONDS);

        ////////////////////////////////////////////////////////////////////////////////

        ChatUtils.setDefaultIfNotSet(commandsFile.getConfiguration(), "CONFIG-VERSION", this.configVersion);
        ChatUtils.setDefaultIfNotSet(chatsFile.getConfiguration(), "CONFIG-VERSION", this.configVersion);
        ChatUtils.setDefaultIfNotSet(aliasesFile.getConfiguration(), "CONFIG-VERSION", this.configVersion);

        ////////////////////////////////////////////////////////////////////////////////

        new ChatsHandler();
        new CommandHandler();
        new AliasesHandler();
        LanguageHandler.load();

        ////////////////////////////////////////////////////////////////////////////////

        Runnables.runLater(() -> update(null), 5, TimeUnit.SECONDS);

        ////////////////////////////////////////////////////////////////////////////////

        logger("Registering listeners and commands...");
        registerListeners(
                new PlayerChatListener(), new ServerMovementListener(), new PlayerCompleteListener(), new PlayerAliasesListener()
        );

        registerCommands(
                new bStaffCommand(), new SearchCMD(), new StaffListCMD(),
                new ToggleChatCMD(), new ToggleCMD(), new ServerKickCMD(),
                new RestartCMD(), new ServerListCMD(), new ClientStatusCMD()
        );

        startDiscordBot();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (hookManager.getHandler("RedisBunee") != null) {
            RedisBungeeHandler redisBungeeHandler = (RedisBungeeHandler) hookManager.getHandler("RedisBungee");
            redisBungeeHandler.disable();
        }

    }

    public void update(ProxiedPlayer player) {
        int resourceId = 95425;
        new UpdateChecker(this, resourceId).getVersion(version -> {
            if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                if (player == null) {
                /*if (player != null) {
                    player.sendMessage(ChatUtils.translate(LanguageHandler.PREFIX.getDef() + "&aThere are no updates available."));
                    player.sendMessage(ChatUtils.translate(LanguageHandler.PREFIX.getDef() + "&aCurrent version: &f" + getDescription().getVersion()));
                } else {*/
                    logger("&aThere are no updates available.");
                    logger("&aCurrent version: &f" + getDescription().getVersion());
                }
            } else {
                if (player != null) {
                    player.sendMessage(ChatUtils.translate(LanguageHandler.PREFIX.getDef() + "&aThere is a &enew update &aavailable. (" + version + ")"));
                    player.sendMessage(ChatUtils.translate(LanguageHandler.PREFIX.getDef() + "&aDownload new version at:"));
                    player.sendMessage(ChatUtils.translate(LanguageHandler.PREFIX.getDef() + "&f* &ehttps://www.spigotmc.org/resources/" + resourceId + "/"));
                } else {
                    logger("&aThere is a &enew update &aavailable. (" + version + ")");
                    logger("&aDownload new version at:");
                    logger("&f* &ehttps://www.spigotmc.org/resources/" + resourceId + "/");
                }
            }
        });
    }

    private void registerListeners(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> getProxy().getPluginManager().registerListener(this, listener));
    }

    private void registerCommands(Command... commands) {
        Arrays.stream(commands).forEach(command -> {
            getProxy().getPluginManager().registerCommand(this, command);
            knownCommands.put(command.getName(), command);
        });
    }

    public List<String> getFallbackServers() {
        return settingsFile.getStringList("SERVERS-CONFIG.FALLBACK-SERVERS");
    }

    public String getRandomFallbackServer() {
        if (!settingsFile.getConfiguration().contains("SERVERS-CONFIG.FALLBACK-SERVERS")) return null;
        List<String> fallbackServers = settingsFile.getStringList("SERVERS-CONFIG.FALLBACK-SERVERS");
        if (fallbackServers.isEmpty()) return null;
        return fallbackServers.get(new Random().nextInt(fallbackServers.size()));
    }

    public void startDiscordBot() {/*
        ChatUtils.setDefaultIfNotSet(settingsFile.getConfiguration(), "DISCORD-INTEGRATION.ENABLED", false);
        ChatUtils.setDefaultIfNotSet(settingsFile.getConfiguration(), "DISCORD-INTEGRATION.BOT-TOKEN", "token");
        settingsFile.save();*/

        if (task != null) task.cancel();
        if (!settingsFile.getBoolean("DISCORD-INTEGRATION.ENABLED")) return;

        try {
            if (jda == null) {
                jda = JDABuilder.createDefault(settingsFile.getString("DISCORD-INTEGRATION.BOT-TOKEN")).build();
                jda.addEventListener(new PlayerChatListener());
            }

            if (settingsFile.getConfiguration().contains("DISCORD-INTEGRATION.ACTIVITY")) {
                Activity.ActivityType type = Activity.ActivityType.DEFAULT;
                if (settingsFile.getConfiguration().contains("DISCORD-INTEGRATION.ACTIVITY-TYPE")) {
                    try { type = Activity.ActivityType.valueOf(settingsFile.getString("DISCORD-INTEGRATION.ACTIVITY-TYPE").toUpperCase()); }
                    catch (Exception ignored) {}
                }

                if (settingsFile.getConfiguration().contains("DISCORD-INTEGRATION.UPDATE-DELAY")) {
                    int delay = settingsFile.getInt("DISCORD-INTEGRATION.UPDATE-DELAY");
                    if (delay != 0) {
                        Activity.ActivityType finalType = type;
                        task = Runnables.runTimerAsync(() -> {
                            String activity = bStaffHolder.getStaffHolderMessage(null, settingsFile.getString("DISCORD-INTEGRATION.ACTIVITY"));
                            if (settingsFile.getConfiguration().contains("DISCORD-INTEGRATION.ACTIVITY-URL")) {
                                jda.getPresence().setActivity(Activity.of(finalType, activity, settingsFile.getString("DISCORD-INTEGRATION.ACTIVITY-URL")));
                            } else jda.getPresence().setActivity(Activity.of(finalType, activity));
                        }, delay, delay);
                        return;
                    }
                }

                jda.getPresence().setActivity(Activity.of(type, bStaffHolder.getStaffHolderMessage(null, settingsFile.getString("DISCORD-INTEGRATION.ACTIVITY"))));
            }
            logger("&aDiscord bot successfully started.");
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

}
