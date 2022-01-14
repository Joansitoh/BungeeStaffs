package me.dragonsteam.bungeestaffs.loaders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 16/12/2021 - 1:26.
 */
@Getter
@AllArgsConstructor
public class AliasesHandler {

    @Getter
    private static final HashMap<String, List<AliasesHandler>> aliasesHashMap = new HashMap<>();

    private String server, permission;
    private List<String> aliases, disabled;

    public static List<AliasesHandler> getAlias(String alias) {
        return aliasesHashMap.getOrDefault(alias, new ArrayList<>());
    }

    ///////////////////////////////////////////////////////////////////////////

    public AliasesHandler() {
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
            }
            bStaffs.logger("* New custom aliases created. (" + server + " / " + aliases.size() + ")", "[Loader]");
        }
    }

}
