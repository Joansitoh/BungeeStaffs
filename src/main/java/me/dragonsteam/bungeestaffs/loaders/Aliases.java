package me.dragonsteam.bungeestaffs.loaders;

import lombok.Getter;
import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.utils.defaults.ConfigFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 16/12/2021 - 1:26.
 */
@Getter
public class Aliases {

    @Getter
    private static final HashMap<String, Aliases> aliasesHashMap = new HashMap<>();

    private final String server;
    private String permission;
    private List<String> aliases, disabled;

    public static Aliases getAlias(String alias) {
        return aliasesHashMap.get(alias);
    }

    ///////////////////////////////////////////////////////////////////////////

    public Aliases(String server) {
        ConfigFile file = bStaffs.INSTANCE.getAliasesFile();
        this.server = server;
        this.aliases = new ArrayList<>();

        if (file.getConfiguration().contains("SERVER-ALIASES." + server)) {
            this.aliases = file.getStringList("SERVER-ALIASES." + server + ".ALIASES");

            if (file.getConfiguration().contains("SERVER-ALIASES." + server + ".PERMISSION"))
                this.permission = file.getString("SERVER-ALIASES." + server + ".PERMISSION");

            if (file.getConfiguration().contains("SERVER-ALIASES." + server + ".DISABLED-SERVERS"))
                this.disabled = file.getStringList("SERVER-ALIASES." + server + ".DISABLED-SERVERS");
        }

        for (String alias : aliases) aliasesHashMap.put(alias, this);
        bStaffs.logger("* New custom aliases created. (" + server + " / " + aliases.size() + ")", "[Loader]");
    }

}
