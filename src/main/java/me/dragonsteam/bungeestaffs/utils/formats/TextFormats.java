package me.dragonsteam.bungeestaffs.utils.formats;

import lombok.Getter;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 14/11/2021 - 14:05.
 */
@Getter
public enum TextFormats {

    RUN_COMMAND("run_command"),
    SUGGEST_COMMAND("suggest_command"),
    SHOW_TEXT("show_text"),
    COLOR("color"),
    ;

    private final String format;

    TextFormats(String format) {
        this.format = format;
    }

    public TextComponent format(TextComponent text, String param) {
        // Remove the format of the text.
        param = ChatUtils.translate(param.replace(format + "=", ""));

        switch (this) {
            case SHOW_TEXT:
                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(param)}));
                break;
            case RUN_COMMAND:
                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, param));
                break;
            case SUGGEST_COMMAND:
                text.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, param));
                break;
            case COLOR:
                break;
        }

        return text;
    }
}
