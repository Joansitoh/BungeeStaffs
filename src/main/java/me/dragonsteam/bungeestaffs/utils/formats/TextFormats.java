package me.dragonsteam.bungeestaffs.utils.formats;

import lombok.Getter;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import me.dragonsteam.bungeestaffs.utils.formats.util.ColorUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import javax.xml.soap.Text;
import java.awt.*;
import java.util.ArrayList;
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
    FORMAT("format"),
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
                String[] s = param.split("\\\\n");
                List<TextComponent> list = new ArrayList<>();
                for (int x = 0; x < s.length; x++) {
                    TextComponent t = new TextComponent(s[x]);
                    if (x != s.length - 1) t.addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
                    list.add(t);
                }
                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, list.toArray(new TextComponent[0])));
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
            case FORMAT:
                switch (param.toLowerCase()) {
                    case "bold":
                        text.setBold(true);
                        break;
                    case "italic":
                        text.setItalic(true);
                        break;
                    case "underline":
                        text.setUnderlined(true);
                        break;
                    case "strikethrough":
                        text.setStrikethrough(true);
                        break;
                    case "obfuscated":
                        text.setObfuscated(true);
                        break;
                    case "reset":
                        text.setBold(false);
                        text.setItalic(false);
                        text.setUnderlined(false);
                        text.setStrikethrough(false);
                        text.setObfuscated(false);
                        break;
                }
                break;
            case COLOR:
                // Using gradient colors.
                if (param.contains("-")) {
                    String[] colors = param.split("-");
                    String extra = "";
                    if (colors.length == 3) {
                        if (!colors[2].contains("#")) extra = colors[2];
                    }

                    Color color1 = Color.decode(colors[0]), color2 = Color.decode(colors[1]);
                    BaseComponent[] components = TextComponent.fromLegacyText(ColorUtil.hsvGradient(text.getText(), extra, color1, color2, ColorUtil::quadratic));
                    text.setText("");
                    for (BaseComponent component : components) {
                        component.setClickEvent(text.getClickEvent());
                        component.setHoverEvent(text.getHoverEvent());
                        text.addExtra(component);
                    }
                } else text.setColor(ChatColor.of(param));
                break;
        }

        return text;
    }

}

