package me.dragonsteam.bungeestaffs;

import lombok.Getter;
import me.dragonsteam.bungeestaffs.commands.types.*;
import me.dragonsteam.bungeestaffs.listeners.PlayerAliasesListener;
import me.dragonsteam.bungeestaffs.listeners.PlayerChatListener;
import me.dragonsteam.bungeestaffs.listeners.PlayerCompleteListener;
import me.dragonsteam.bungeestaffs.listeners.ServerMovementListener;
import me.dragonsteam.bungeestaffs.loaders.Aliases;
import me.dragonsteam.bungeestaffs.loaders.Chats;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.managers.HookManager;
import me.dragonsteam.bungeestaffs.utils.UpdateChecker;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import me.dragonsteam.bungeestaffs.utils.defaults.Runnables;
import me.dragonsteam.bungeestaffs.utils.formats.TextFormatReader;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

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

    ///////////////////////////////////////////////////////////////////////////

    public static void logger(String message) {
        logger(message, null);
    }

    public static void logger(String message, String subMsg) {
        INSTANCE.getProxy().getConsole().sendMessage(ChatUtils.translate(Lang.PREFIX.getDef() + (subMsg != null ? (subMsg + " &f") : "") + message));
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
        messagesFile = new ConfigFile(this, "messages.yml");

        configVersion = "0.2.5";
        commands = new HashMap<>();
        knownCommands = new HashMap<>();

        Runnables.runLater(() -> hookManager = new HookManager(), 5, TimeUnit.SECONDS);

        ////////////////////////////////////////////////////////////////////////////////

        ChatUtils.setDefaultIfNotSet(commandsFile.getConfiguration(), "CONFIG-VERSION", this.configVersion);
        ChatUtils.setDefaultIfNotSet(chatsFile.getConfiguration(), "CONFIG-VERSION", this.configVersion);

        ////////////////////////////////////////////////////////////////////////////////

        new Chats(this);
        new Comms();
        Lang.load();

        bStaffs.logger("Registering custom aliases.", "[Loader]");
        for (String key : aliasesFile.getConfiguration().getSection("SERVER-ALIASES").getKeys())
            new Aliases(key);

        ////////////////////////////////////////////////////////////////////////////////

        Runnables.runLater(() -> {
            int resourceId = 95425;
            new UpdateChecker(this, resourceId).getVersion(version -> {
                if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    logger("&aThere are no updates available.");
                    logger("&aCurrent version: &f" + getDescription().getVersion());
                } else {
                    logger("&aThere is a &enew update &aavailable. (" + version + ")");
                    logger("&aDownload new version at:");
                    logger("&f* &ehttps://www.spigotmc.org/resources/" + resourceId + "/");
                }
            });
        }, 5, TimeUnit.SECONDS);

        ////////////////////////////////////////////////////////////////////////////////

        logger("Registering listeners and commands...");
        registerListeners(
                new PlayerChatListener(), new ServerMovementListener(), new PlayerCompleteListener(), new PlayerAliasesListener()
        );

        registerCommands(
                new bStaffCommand(), new SearchCMD(), new StaffListCMD(),
                new ToggleChatCMD(), new ToggleCMD(), new ServerKickCMD(),
                new RestartCMD()
        );
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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

    public String getRandomFallbackServer() {
        if (!settingsFile.getConfiguration().contains("SERVERS-CONFIG.FALLBACK-SERVERS")) return null;
        List<String> fallbackServers = settingsFile.getStringList("SERVERS-CONFIG.FALLBACK-SERVERS");
        if (fallbackServers.isEmpty()) return null;
        return fallbackServers.get(new Random().nextInt(fallbackServers.size()));
    }

}
