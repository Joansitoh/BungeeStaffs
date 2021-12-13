package me.dragonsteam.bungeestaffs.utils.formats;

import me.dragonsteam.bungeestaffs.bStaffs;
import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 14/11/2021 - 14:05.
 */
public class TextFormatReader {

    // Text format example: ${&5&lSTAFF}(show_text=&7| &eClick to join Lobby)(run_command=/server Lobby) &f:

    private static final String TAG_START = "\\$\\{";
    private static final String TAG_END = "\\}";

    private static final String PARM_TAG_START = "\\(";
    private static final String PARM_TAG_END = "\\)";

    // Function to read text and split parts who start with '${' and end with '}'
    // Then get the parameters of the text identified by PARM tags'

    public static TextComponent complexFormat(String format) {
        TextComponent message = new TextComponent();

        // Get values between ${} and edit it.
        String[] split = format.split(TAG_START);
        for (String s : split) {
            if (s.contains("}")) {
                String[] split1 = s.split(TAG_END);
                String var = split1[0];
                TextComponent component = new TextComponent(ChatUtils.translate(var));

                // Check if length is more than 1.
                if (split1.length > 1) {
                    for (int x = 1; x < split1.length; x++) {
                        String context = split1[x];

                        // Check if context have value between ().
                        String[] split2 = context.split(PARM_TAG_START);

                        // Check if length of split2 is greater than 1.
                        // That means that context has parameters of format reader.
                        // Example: show_text and run_command.
                        if (split2.length > 1) {
                            for (String s1 : split2) {
                                if (s1.contains(")")) {
                                    // Get value between ().
                                    String[] split3 = s1.split(PARM_TAG_END);
                                    String value = split3[0];

                                    // Get parameters in value split by ",".
                                    String[] split4 = value.split(",");
                                    for (String parameter : split4) {
                                        for (TextFormats formats : TextFormats.values()) {
                                            if (parameter.startsWith(formats.getFormat()))
                                                formats.format(component, parameter);
                                        }
                                    }

                                    message.addExtra(component);

                                    // Getting other context without () parameters.
                                    for (int y = 1; y < split3.length; y++) {
                                        message.addExtra(ChatUtils.translate(split3[y]));
                                    }
                                }
                            }
                            continue;
                        }

                        // Print content without ().
                        message.addExtra(ChatUtils.translate(context));
                    }
                    continue;
                }

                message.addExtra(component);
            }

            message.addExtra(s);
        }

        return message;
    }

}
