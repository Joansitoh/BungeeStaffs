package me.dragonsteam.bungeestaffs.commands.types;

import lombok.Getter;
import me.dragonsteam.bungeestaffs.commands.cCommand;
import me.dragonsteam.bungeestaffs.loaders.CommandHandler;
import me.dragonsteam.bungeestaffs.loaders.LanguageHandler;
import me.dragonsteam.bungeestaffs.utils.defaults.ToggleUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Class for project BungeeStaffs
 * Date: 01/02/2022 - 15:38.
 *
 * @author Joansiitoh
 */
public class ChatSpyCMD extends Command {

    @Getter
    private static final List<UUID> playerList = new ArrayList<>();

    public ChatSpyCMD() {
        super("chatspy", "bstaffs.chatspy");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (playerList.contains(player.getUniqueId()))
                playerList.remove(player.getUniqueId());
            else playerList.add(player.getUniqueId());

            player.sendMessage(LanguageHandler.CHAT_SPY.toString(true)
                    .replace("<value>", playerList.contains(player.getUniqueId()) ? LanguageHandler.BOOLEAN_TRUE.toString() : LanguageHandler.BOOLEAN_FALSE.toString())
            );
        }
    }

}
