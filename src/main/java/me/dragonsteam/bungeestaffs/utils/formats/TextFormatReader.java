package me.dragonsteam.bungeestaffs.utils.formats;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 14/11/2021 - 14:05.
 */
public class TextFormatReader {

    // Text format example: ${&5&lSTAFF}(show_text=&7| &eClick to join Lobby)(run_command=/server Lobby) &f:

    private static final String TAG_START = "\\$\\{";
    private static final String TAG_END = "}";

    private static final String PARM_TAG_START = "\\(";
    private static final String PARM_TAG_END = "\\)";

    // Function to read text and split parts who start with '${' and end with '}'
    // Then get the parameters of the text identified by PARM tags'

    public static TextComponent complexFormat(String format) {
        TextComponent message = new TextComponent();
        Pattern pattern = Pattern.compile(PARM_TAG_START + "(.*?)" + PARM_TAG_END, Pattern.DOTALL);

        // Get values between ${} and edit it.
        String[] split = format.split(TAG_START);
        for (String s : split) {
            if (s.contains(TAG_END)) {
                String[] tags = s.split(TAG_END);
                String tag = tags[0], after = tags[1];
                TextComponent component = new TextComponent(ChatUtils.translate(tag));

                Matcher matcher = pattern.matcher(after);
                List<String> params = new ArrayList<>();
                while (matcher.find()) {
                    String parameter = matcher.group(1);
                    params.add("(" + parameter + ")");
                    for (TextFormats formats : TextFormats.values()) {
                        if (parameter.startsWith(formats.getFormat()))
                            formats.format(component, parameter);
                    }
                }

                String extra = after;
                for (String param : params) extra = extra.replace(param, "");

                message.addExtra(component);
                message.addExtra(extra);
                continue;
            }

            message.addExtra(s);
        }

        return message;
    }

}
