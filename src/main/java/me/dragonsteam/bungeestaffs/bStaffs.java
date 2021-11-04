package me.dragonsteam.bungeestaffs;

import lombok.Getter;
import me.dragonsteam.bungeestaffs.commands.SearchCMD;
import me.dragonsteam.bungeestaffs.commands.StaffListCMD;
import me.dragonsteam.bungeestaffs.listeners.*;
import me.dragonsteam.bungeestaffs.loaders.Chats;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.UpdateChecker;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import me.dragonsteam.bungeestaffs.utils.defaults.Runnables;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 16:00.
 */
@Getter
public final class bStaffs extends Plugin {

    public static bStaffs INSTANCE;

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

        // Loading recipients
        settingsFile = new ConfigFile(this, "settings.yml");
        commandsFile = new ConfigFile(this, "commands.yml");
        chatsFile = new ConfigFile(this, "chats.yml");
        messagesFile = new ConfigFile(this, "messages.yml");

        new Chats(this);
        new Comms(this);
        Lang.loadLanguage();

        Runnables.runLater(() -> {
            int resourceId = 95425;
            System.out.println(getDescription().getVersion());

            new UpdateChecker(this, resourceId).getVersion(version -> {
                System.out.println(version);
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

        registerListeners(
                new PlayerChatListener(), new PlayerCommandListener(), new PlayerServerListeners(), new PlayerToggleListener(),
                new PlayerTabComepleListener()
        );

        getProxy().getPluginManager().registerCommand(this, new bStaffCommand());
        getProxy().getPluginManager().registerCommand(this, new SearchCMD());
        getProxy().getPluginManager().registerCommand(this, new StaffListCMD());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerListeners(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> getProxy().getPluginManager().registerListener(this, listener));
    }

}
