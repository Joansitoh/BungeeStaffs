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
import me.dragonsteam.bungeestaffs.utils.UpdateChecker;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import me.dragonsteam.bungeestaffs.utils.defaults.Runnables;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

import javax.security.auth.login.LoginException;
import java.io.File;
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

    private ConfigFile settingsFile, commandsFile, chatsFile, messagesFile, aliasesFile;
    private HookManager hookManager;
    private String configVersion;

    private JDA jda;

    ///////////////////////////////////////////////////////////////////////////

    public static void logger(String message) {
        logger(message, null);
    }

    public static void logger(String message, String subMsg) {
        INSTANCE.getProxy().getConsole().sendMessage(ChatUtils.translate(LanguageHandler.PREFIX.getDef() + (subMsg != null ? (subMsg + " &f") : "") + message));
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

        new File(getDataFolder(), "langs").mkdir();
        String lang = "en_US";
        if (settingsFile.getConfiguration().contains("LANGUAGE")) lang = settingsFile.getString("LANGUAGE");
        messagesFile = new ConfigFile(this, "langs/" + lang + ".yml");

        configVersion = "0.2.6";
        commands = new HashMap<>();
        knownCommands = new HashMap<>();

        Runnables.runLater(() -> hookManager = new HookManager(), 5, TimeUnit.SECONDS);

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

        if (!settingsFile.getBoolean("DISCORD-INTEGRATION.ENABLED")) return;

        try {
            jda = JDABuilder.createDefault(settingsFile.getString("DISCORD-INTEGRATION.BOT-TOKEN")).build();
            jda.addEventListener(new PlayerChatListener());
            logger("&aDiscord bot successfully started.");
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

}
