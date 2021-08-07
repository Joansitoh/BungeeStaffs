package me.dragonsteam.bungeestaffs;

import lombok.Getter;
import me.dragonsteam.bungeestaffs.listeners.PlayerChatListener;
import me.dragonsteam.bungeestaffs.listeners.PlayerCommandListener;
import me.dragonsteam.bungeestaffs.listeners.PlayerServerListeners;
import me.dragonsteam.bungeestaffs.loaders.Chats;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.ConfigFile;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Arrays;

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

        registerListeners(
                new PlayerChatListener(), new PlayerCommandListener(), new PlayerServerListeners()
        );

        getProxy().getPluginManager().registerCommand(this, new bStaffCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void logger(String message) {
        logger(message, null);
    }

    public static void logger(String message, String subMsg) {
        INSTANCE.getProxy().getConsole().sendMessage(ChatUtils.translate(Lang.PREFIX.getDef() + (subMsg != null ? (subMsg + " &f") : "") + message));
    }

    private void registerListeners(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> getProxy().getPluginManager().registerListener(this, listener));
    }

}
