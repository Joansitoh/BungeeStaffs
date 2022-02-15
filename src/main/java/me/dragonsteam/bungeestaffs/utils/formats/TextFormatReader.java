package me.dragonsteam.bungeestaffs.utils.formats;

import me.dragonsteam.bungeestaffs.utils.defaults.ChatUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
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

    private static final String MAIN_PATTERN = "\\$\\{(.+?)\\}";

    // Function to read text and split parts who start with '${' and end with '}'
    // Then get the parameters of the text identified by PARM tags'

    public static TextComponent complexFormat(ProxiedPlayer viewer, String format) {
        TextComponent message = new TextComponent();
        Pattern pattern = Pattern.compile(PARM_TAG_START + "(.*?)" + PARM_TAG_END, Pattern.DOTALL);

        // Get values between ${} and edit it.
        String[] split = format.split(TAG_START);
        String last_color = "";
        for (String s : split) {
            //System.out.println("[DEBUG] Last color: " + last_color + "color");
            //System.out.println("[DEBUG] s: " + s);
            if (s.contains(TAG_END)) {
                //System.out.println("[DEBUG] Found tag: " + s);
                String[] tags = s.split(TAG_END);
                String tag = tags[0], after = tags[1];
                //System.out.println("[DEBUG] Tag: " + tag);
                //System.out.println("[DEBUG] After: " + after);
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

                if (viewer.getPendingConnection().getVersion() < 735 && last_color.length() == 2)
                    component.setColor(ChatColor.getByChar(last_color.charAt(1)));

                message.addExtra(component);
                message.addExtra(extra);
                //System.out.println("[DEBUG] Extra: " + extra);
                continue;
            }

            last_color = getLastColor(s);
            message.addExtra(s);
        }

        return message;
    }

    public static TextComponent testNewPattern(ProxiedPlayer viewer, String format, String message) {
        /*
         * New pattern: \$\{(.+?)\}((?:\(.*?\))+)
         * Explain pattern: \$\{(.+?)\}(\S*\((.*?)\))
         * 1: \$\{(.+?)\} - Get the text between ${}
         * 2: \S*\((.*?)\) - Get the parameters between ()
         *
         */

        Pattern pattern = Pattern.compile(TAG_START + "(.+?)" + TAG_END + "((\\(.*?\\))+)");
        Matcher matcher = pattern.matcher(format);

        HashMap<String, TextComponent> tag_components = new HashMap<>();
        boolean message_replaced = false;

        /*
         * Start finding all pattern format.
         */
        while (matcher.find()) {
            String tag = matcher.group(1), parameters = matcher.group(2);
            //System.out.println("[DEBUG] Tag: " + tag + " | Parameters: " + parameters);

            Pattern param_pattern = Pattern.compile(PARM_TAG_START + "(.+?)" + PARM_TAG_END);
            Matcher param_matcher = param_pattern.matcher(parameters);

            TextComponent component = new TextComponent(tag);
            while (param_matcher.find()) {
                String param = param_matcher.group(1);
                //System.out.println("[DEBUG] Parameter found: " + param);

                // Getting type of parameter splitting by first '='.
                int end = param.indexOf("=");
                if (end != -1) {
                    String type = param.substring(0, end), value = param.substring(end + 1);
                    //System.out.println("[DEBUG] Parameter type: " + type);
                    //System.out.println("[DEBUG] Parameter value: " + value);

                    for (TextFormats formats : TextFormats.values()) {
                        if (type.equalsIgnoreCase(formats.getFormat()))
                            formats.format(component, value);
                    }
                }
            }

            format = format.replace(TAG_START.replace("\\", "") + tag + TAG_END + parameters, "{" + tag_components.size() + "}");
            tag_components.put("{" + tag_components.size() + "}", component);
        }

        /*
         * Start replacing all pattern format.
         */

        String last_color = "";
        TextComponent message_component = new TextComponent("");
        for (String s : format.split("\\{")) {
            if (s.contains("}")) {
                String tag = s.substring(0, s.indexOf("}")), extra = s.substring(s.indexOf("}") + 1);
                TextComponent component = tag_components.get("{" + tag + "}");
                if (component != null) {
                    if (viewer != null && /*viewer.getPendingConnection().getVersion() < 735 && */last_color.length() == 2)
                        component.setColor(ChatColor.getByChar(last_color.charAt(1)));

                    message_component.addExtra(component);
                }

                if (message != null && extra.contains("<message>") && !message_replaced) {
                    extra = extra.replace("<message>", message);
                    message_replaced = true;
                }

                message_component.addExtra(new TextComponent(extra));
                last_color = getLastColor(s);
                continue;
            }

            last_color = getLastColor(s);

            if (message != null && s.contains("<message>") && !message_replaced) {
                s = s.replace("<message>", message);
                message_replaced = true;
            }

            message_component.addExtra(ChatUtils.translate(s));
        }

        /*String last_color = "";
        TextComponent message_component = new TextComponent("");
        for (String s : message.split(" ")) {
            if (message_component.getExtra() != null) message_component.addExtra(" ");
            String color = getLastColor(s);
            if (!color.equalsIgnoreCase("")) {
                //System.out.println("[DEBUG] Color: " + color + " applied to: " + s);
                last_color = color;
            }
            //System.out.println("[DEBUG] Default color: " + last_color + " default color");

            //System.out.println("[DEBUG] Message: " + s);
            if (tag_components.containsKey(ChatColor.stripColor(s))) {
                //System.out.println("[DEBUG] Found tag: " + s);
                message_component.addExtra(tag_components.get(ChatColor.stripColor(s)));
                //System.out.println("[DEBUG] Message component: " + tag_components.get(ChatColor.stripColor(s)));
                continue;
            }

            message_component.addExtra(last_color + s);
        }*/

        return message_component;
    }

    private static String getLastColor(String input) {
        StringBuilder result = new StringBuilder();
        int length = input.length();

        // Search backwards from the end as it is faster
        for (int index = length - 1; index > -1; index--) {
            char section = input.charAt(index);
            if (section == ChatColor.COLOR_CHAR && index < length - 1) {
                char c = input.charAt(index + 1);
                ChatColor color = ChatColor.getByChar(c);

                //System.out.println("[DEBUG] Color: " + (color != null ? color.getName() : "null"));

                if (color != null) {
                    result.insert(0, color);

                    // Once we find a color or reset we can stop searching
                    if ((!color.equals(ChatColor.RESET)) || color.equals(ChatColor.RESET)) {
                        break;
                    }
                }
            }
        }

        return result.toString();
    }

}
