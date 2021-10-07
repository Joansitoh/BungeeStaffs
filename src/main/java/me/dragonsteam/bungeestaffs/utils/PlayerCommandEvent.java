package me.dragonsteam.bungeestaffs.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 07/10/2021 - 17:48.
 */
@Getter
@Setter
@AllArgsConstructor
public class PlayerCommandEvent extends Event {

    private final ProxiedPlayer sender;
    private final String command;
    private final String[] args;
}
