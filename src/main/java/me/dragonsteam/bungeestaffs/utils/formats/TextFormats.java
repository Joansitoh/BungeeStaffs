package me.dragonsteam.bungeestaffs.utils.formats;

import lombok.Getter;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.formats.util.ColorUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.awt.*;
import java.util.List;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 14/11/2021 - 14:05.
 */
@Getter
public enum TextFormats {

    RUN_COMMAND("run_command"),
    SUGGEST_COMMAND("suggest_command"),
    OPEN_URL("open_url"),
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
            case OPEN_URL:
                text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, param));
                break;
            case COLOR:
                // Using gradient colors.
                if (param.contains("-")) {
                    boolean bold = text.getText().startsWith(ChatColor.BOLD.toString());
                    if (bold) text.setText(text.getText().replace(ChatColor.BOLD.toString(), ""));

                    String[] colors = param.split("-");
                    Color color1 = Color.decode(colors[0]), color2 = Color.decode(colors[1]);
                    BaseComponent[] components = TextComponent.fromLegacyText(ColorUtil.rgbGradient(text.getText(), color1, color2, ColorUtil::linear));
                    text.setText("");
                    for (BaseComponent component : components) {
                        component.setClickEvent(text.getClickEvent());
                        component.setHoverEvent(text.getHoverEvent());
                        component.setBold(bold);
                        text.addExtra(component);
                    }

                } else text.setColor(ChatColor.of(param));
                break;
        }

        return text;
    }

}

