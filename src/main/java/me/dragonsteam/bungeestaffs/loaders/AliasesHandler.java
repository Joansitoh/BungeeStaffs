package me.dragonsteam.bungeestaffs.loaders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.commands.CommandManager;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 16/12/2021 - 1:26.
 */
@Getter
@AllArgsConstructor
public class AliasesHandler {

    private static final HashMap<String, Command> commandMap = new HashMap<>();

    @Getter
    private static final HashMap<String, List<AliasesHandler>> aliasesHashMap = new HashMap<>();

    private String server, permission;
    private List<String> aliases, disabled;

    public static List<AliasesHandler> getAlias(String alias) {
        return aliasesHashMap.getOrDefault(alias, new ArrayList<>());
    }

    ///////////////////////////////////////////////////////////////////////////

    public AliasesHandler() {
        for (Command cmd : new ArrayList<>(commandMap.values())) {
            bStaffs.INSTANCE.getProxy().getPluginManager().unregisterCommand(cmd);
            commandMap.remove(cmd.getName());
        }
        commandMap.clear();

        aliasesHashMap.clear();
        bStaffs.logger("Registering custom aliases.", "[Loader]");
        ConfigFile file = bStaffs.INSTANCE.getAliasesFile();
        for (String server : file.getConfiguration().getSection("SERVER-ALIASES").getKeys()) {
            Configuration configuration = file.getConfiguration().getSection("SERVER-ALIASES." + server);
            String permission = configuration.getString("PERMISSION");
            List<String> aliases = configuration.getStringList("ALIASES");
            List<String> disabled = configuration.getStringList("DISABLED-SERVERS");

            AliasesHandler alias = new AliasesHandler(server, permission, aliases, disabled);
            aliasesHashMap.putIfAbsent(server, new ArrayList<>());
            aliasesHashMap.get(server).add(alias);
            for (String alias1 : aliases) {
                aliasesHashMap.putIfAbsent(alias1, new ArrayList<>());
                aliasesHashMap.get(alias1).add(alias);

                Command cmd = getAliasesCommand(alias1);
                if (cmd != null)
                    bStaffs.INSTANCE.getProxy().getPluginManager().registerCommand(bStaffs.INSTANCE, cmd);
            }
            bStaffs.logger("* New custom aliases created. (" + server + " / " + aliases.size() + ")", "[Loader]");
        }
    }

    public Command getAliasesCommand(String alias) {
        if (commandMap.containsKey(alias)) return null;
        Command cmd = new Command(alias) {
            @Override
            public void execute(CommandSender sender, String[] args) {
                if (args.length != 0) return;
                if (!(sender instanceof ProxiedPlayer)) return;

                String command = getName();
                ProxiedPlayer p = (ProxiedPlayer) sender;

                ConfigFile file = bStaffs.INSTANCE.getAliasesFile();
                for (String s : file.getStringList("BLACKLIST-SERVERS")) {
                    if (p.getServer().getInfo().getName().equalsIgnoreCase(s)) return;
                }

                if (AliasesHandler.getAlias(command).isEmpty()) return;
                AliasesHandler aliasesHandler = AliasesHandler.getAlias(command).get(new Random().nextInt(AliasesHandler.getAlias(command).size()));
                if (aliasesHandler == null) return;

                if (aliasesHandler.getDisabled() != null && !aliasesHandler.getDisabled().isEmpty()) {
                    for (String s : aliasesHandler.getDisabled())
                        if (p.getServer().getInfo().getName().equalsIgnoreCase(s)) return;
                }

                if (checkPermission(p, aliasesHandler.getPermission())) {
                    if (p.getServer().getInfo().getName().equals(aliasesHandler.getServer())) {
                        p.sendMessage(LanguageHandler.ALIASES_ALREADY_TELEPORT.toString(true).replace("<server>", aliasesHandler.getServer()));
                        return;
                    }

                    p.connect(bStaffs.INSTANCE.getProxy().getServerInfo(aliasesHandler.getServer()));
                    p.sendMessage(LanguageHandler.ALIASES_TELEPORT.toString(true).replace("<server>", aliasesHandler.getServer()));
                } else p.sendMessage(LanguageHandler.NO_PERMISSION.toString(true));

            }
        };
        commandMap.put(alias, cmd);
        return cmd;
    }

    private boolean checkPermission(ProxiedPlayer p, String permission) {
        if (permission == null) return true;
        if (permission.equalsIgnoreCase("")) return true;
        return p.hasPermission(permission);
    }

}
