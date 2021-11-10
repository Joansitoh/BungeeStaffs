package me.dragonsteam.bungeestaffs.managers;

import lombok.Getter;
import lombok.Setter;
import me.dragonsteam.bungeestaffs.bStaffs;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 10/11/2021 - 23:40.
 */
@Getter @Setter
public abstract class HookHandler {

    private boolean loaded;

    public HookHandler(String name) {
        this.loaded = bStaffs.INSTANCE.getProxy().getPluginManager().getPlugin(name) != null;
    }

    public abstract void setup();

}
