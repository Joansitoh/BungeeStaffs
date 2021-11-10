package me.dragonsteam.bungeestaffs;

import lombok.Getter;
import me.dragonsteam.bungeestaffs.commands.types.SearchCMD;
import me.dragonsteam.bungeestaffs.commands.types.StaffListCMD;
import me.dragonsteam.bungeestaffs.commands.types.ToggleCMD;
import me.dragonsteam.bungeestaffs.commands.types.ToggleChatCMD;
import me.dragonsteam.bungeestaffs.listeners.PlayerChatListener;
import me.dragonsteam.bungeestaffs.listeners.PlayerCompleteListener;
import me.dragonsteam.bungeestaffs.listeners.ServerMovementListener;
import me.dragonsteam.bungeestaffs.loaders.Chats;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.UpdateChecker;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import me.dragonsteam.bungeestaffs.utils.defaults.Runnables;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 16:00.
 */
@Getter
public final class bStaffs extends Plugin {

    public static bStaffs INSTANCE;

    private HashMap<String, String> commands;

    private ConfigFile settingsFile;
    private ConfigFile commandsFile;
    private ConfigFile chatsFile;
    private ConfigFile messagesFile;

    public static void logger(String message) {
        logger(message, null);
    }

    public static void logger(String message, String subMsg) {
        INSTANCE.getProxy().getConsole().sendMessage(ChatUtils.translate(Lang.PREFIX.getDef() + (subMsg != null ? (subMsg + " &f") : "") + message));
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;

        // Loading plugin metrics.
        new Metrics(this, 13287);

        ////////////////////////////////////////////////////////////////////////////////

        // Loading recipients
        settingsFile = new ConfigFile(this, "settings.yml");
        commandsFile = new ConfigFile(this, "commands.yml");
        chatsFile = new ConfigFile(this, "chats.yml");
        messagesFile = new ConfigFile(this, "messages.yml");

        commands = new HashMap<>();

        ////////////////////////////////////////////////////////////////////////////////

        new Chats(this);
        new Comms();
        Lang.loadLanguage();

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
                new PlayerChatListener(), new ServerMovementListener(), new PlayerCompleteListener()
        );

        registerCommands(new bStaffCommand(), new SearchCMD(), new StaffListCMD(), new ToggleChatCMD(), new ToggleCMD());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerListeners(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> getProxy().getPluginManager().registerListener(this, listener));
    }

    private void registerCommands(Command... commands) {
        Arrays.stream(commands).forEach(command -> getProxy().getPluginManager().registerCommand(this, command));
    }

    public List<String> getExtraCommands() {
        return Arrays.asList("stafflist", "toggle", "search", "togglechat");
    }

}
