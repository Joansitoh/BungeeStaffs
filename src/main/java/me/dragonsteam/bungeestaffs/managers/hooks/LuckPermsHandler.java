package me.dragonsteam.bungeestaffs.managers.hooks;

import me.dragonsteam.bungeestaffs.managers.HookHandler;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;

import java.util.UUID;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 10/11/2021 - 23:39.
 */
public class LuckPermsHandler extends HookHandler {

    private LuckPerms luckPerms;

    public LuckPermsHandler() {
        super("LuckPerms");
    }

    @Override
    public void setup() {
        this.luckPerms = LuckPermsProvider.get();
    }

    // Function to get player prefix.
    public String getPrefix(UUID uuid) {
        User user = this.luckPerms.getUserManager().getUser(uuid);
        if (user == null) return "";

        CachedMetaData meta = user.getCachedData().getMetaData();
        return meta.getPrefix();
    }

    // Function to get player suffix.
    public String getSuffix(UUID uuid) {
        User user = this.luckPerms.getUserManager().getUser(uuid);
        if (user == null) return "";

        CachedMetaData meta = user.getCachedData().getMetaData();
        return meta.getSuffix();
    }

}
