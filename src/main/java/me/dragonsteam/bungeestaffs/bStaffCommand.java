package me.dragonsteam.bungeestaffs;

import me.dragonsteam.bungeestaffs.listeners.PlayerCommandListener;
import me.dragonsteam.bungeestaffs.loaders.Chats;
import me.dragonsteam.bungeestaffs.loaders.Comms;
import me.dragonsteam.bungeestaffs.loaders.Lang;
import me.dragonsteam.bungeestaffs.utils.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.ConfigFile;
import me.dragonsteam.bungeestaffs.utils.Runnables;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 07/08/2021 - 13:46.
 */
public class bStaffCommand extends Command {

    public bStaffCommand() {
        super("bungeestaffs", "bstaffs.admin", "bstaffs");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(ChatUtils.MEDIUM_CHAT_BAR);
                sender.sendMessage(ChatUtils.translate("&eReloading plugin config files..."));
                List<ConfigFile> configs = Arrays.asList(
                        bStaffs.INSTANCE.getChatsFile(),
                        bStaffs.INSTANCE.getCommandsFile(),
                        bStaffs.INSTANCE.getSettingsFile(),
                        bStaffs.INSTANCE.getMessagesFile()
                );

                for (ConfigFile config : configs) {
                    Runnables.runLater(() -> {
                        config.reloadConfig();
                        sender.sendMessage(ChatUtils.translate("&e* &fFile has been reloaded. (&a" + config.getFile().getName() + "&f)"));
                    }, new Random().nextInt(1500), TimeUnit.MILLISECONDS);
                }

                Runnables.runLater(() -> {
                    sender.sendMessage("");
                    sender.sendMessage(ChatUtils.translate("&eAll files config has been reloaded."));
                    sender.sendMessage(ChatUtils.MEDIUM_CHAT_BAR);

                    new Chats(bStaffs.INSTANCE);
                    new Comms(bStaffs.INSTANCE);
                }, 2, TimeUnit.SECONDS);
                return;
            }

            if (args[0].equalsIgnoreCase("version")) {
                sender.sendMessage(ChatUtils.MEDIUM_CHAT_BAR);
                sender.sendMessage(ChatUtils.translate("&bBungeeStaffs (bStaffs)"));
                sender.sendMessage(ChatUtils.translate("&eVersion&7: &f" + bStaffs.INSTANCE.getDescription().getVersion()));
                sender.sendMessage(ChatUtils.translate(""));
                sender.sendMessage(ChatUtils.translate("&7&oMade by @Joansiitoh"));
                sender.sendMessage(ChatUtils.MEDIUM_CHAT_BAR);
                return;
            }
        }

        sender.sendMessage(ChatUtils.MEDIUM_CHAT_BAR);
        sender.sendMessage(ChatUtils.translate("&bBungeeStaffs Commands"));
        sender.sendMessage("");
        sender.sendMessage(ChatUtils.translate("&f* &b/bstaffs reload &7| &fReload the plugin configs."));
        sender.sendMessage(ChatUtils.translate("&f* &b/bstaffs version &7| &fShow the plugin version."));
        sender.sendMessage(ChatUtils.MEDIUM_CHAT_BAR);
    }

}
