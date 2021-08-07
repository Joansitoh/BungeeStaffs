package me.dragonsteam.bungeestaffs.utils;

import net.md_5.bungee.api.ChatColor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatUtils {

    public static String translate(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> translate(String... s) {
        List<String> list = new ArrayList<>();
        for (String x : s) list.add(translate(x));
        return list;
    }

    public static String formatMoney(double money) {
        BigDecimal big = new BigDecimal(money);
        if (big.compareTo(new BigDecimal(1000)) < 0) return String.valueOf(big);
        BigDecimal var2 = null, var3 = null;
        String symbol = "", format = "";
        if (big.compareTo(new BigDecimal("1000000000000000000000000000")) > 0) {
            var2 = new BigDecimal(100000000000000000L);
            var3 = new BigDecimal(1.0E10D);
            symbol = "OC";
            format = String.valueOf(big.divide(var2).divide(var3).setScale(1, RoundingMode.HALF_UP));
            return String.valueOf(String.valueOf(format)) + symbol;
        }
        if (big.compareTo(new BigDecimal("1000000000000000000000000")) > 0) {
            var2 = new BigDecimal(1000000000000000L);
            var3 = new BigDecimal(1.0E9D);
            symbol = "SP";
            format = String.valueOf(big.divide(var2).divide(var3).setScale(1, RoundingMode.HALF_UP));
            return String.valueOf(String.valueOf(format)) + symbol;
        }
        if (big.compareTo(new BigDecimal("100000000000000000000")) == 1) {
            var2 = new BigDecimal(10000000000000L);
            var3 = new BigDecimal(1.0E8D);
            symbol = "SX";
            format = String.valueOf(big.divide(var2).divide(var3).setScale(1, RoundingMode.HALF_UP));
            return String.valueOf(String.valueOf(format)) + symbol;
        }
        if (big.compareTo(new BigDecimal(1000000000000000000L)) == 1) {
            var2 = new BigDecimal(100000000000L);
            var3 = new BigDecimal(1.0E7D);
            symbol = "QT";
            format = String.valueOf(big.divide(var2).divide(var3).setScale(1, RoundingMode.HALF_UP));
            return String.valueOf(String.valueOf(format)) + symbol;
        }
        if (big.compareTo(new BigDecimal(1000000000000000L)) == 1) {
            var2 = new BigDecimal(1000000000L);
            var3 = new BigDecimal(1000000.0D);
            symbol = "Q";
            format = String.valueOf(big.divide(var2).divide(var3).setScale(1, RoundingMode.HALF_UP));
            return String.valueOf(String.valueOf(format)) + symbol;
        }
        if (big.compareTo(new BigDecimal(1000000000000L)) > 0) {
            var2 = new BigDecimal(100000000);
            var3 = new BigDecimal(10000.0D);
            symbol = "T";
            format = String.valueOf(big.divide(var2).divide(var3).setScale(1, RoundingMode.HALF_UP));
            return String.valueOf(String.valueOf(format)) + symbol;
        }
        if (big.compareTo(new BigDecimal(1000000000L)) > 0) {
            var2 = new BigDecimal(1000000);
            var3 = new BigDecimal(1000.0D);
            symbol = "B";
            format = String.valueOf(big.divide(var2).divide(var3).setScale(1, RoundingMode.HALF_UP));
            return String.valueOf(String.valueOf(format)) + symbol;
        }
        if (big.compareTo(new BigDecimal(1000000)) > 0) {
            var2 = new BigDecimal(10000);
            var3 = new BigDecimal(100.0D);
            symbol = "M";
            format = String.valueOf(big.divide(var2).divide(var3).setScale(1, RoundingMode.HALF_UP));
            return String.valueOf(String.valueOf(format)) + symbol;
        }
        if (big.compareTo(new BigDecimal(1000)) > 0) {
            var2 = new BigDecimal(100);
            var3 = new BigDecimal(10.0D);
            symbol = "k";
            format = String.valueOf(big.divide(var2).divide(var3).setScale(1, RoundingMode.HALF_UP));
            return String.valueOf(String.valueOf(format)) + symbol;
        }

        return money + "";
    }

    public static final String BLUE = ChatColor.BLUE.toString();

    public static final String AQUA = ChatColor.AQUA.toString();

    public static final String YELLOW = ChatColor.YELLOW.toString();

    public static final String RED = ChatColor.RED.toString();

    public static final String GRAY = ChatColor.GRAY.toString();

    public static final String GOLD = ChatColor.GOLD.toString();

    public static final String GREEN = ChatColor.GREEN.toString();

    public static final String WHITE = ChatColor.WHITE.toString();

    public static final String BLACK = ChatColor.BLACK.toString();

    public static final String BOLD = ChatColor.BOLD.toString();

    public static final String ITALIC = ChatColor.ITALIC.toString();

    public static final String STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();

    public static final String RESET = ChatColor.RESET.toString();

    public static final String MAGIC = ChatColor.MAGIC.toString();

    public static final String OBFUSCATED = MAGIC;

    public static final String B = BOLD;

    public static final String M = MAGIC;

    public static final String O = MAGIC;

    public static final String I = ITALIC;

    public static final String S = STRIKE_THROUGH;

    public static final String R = RESET;

    public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();

    public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();

    public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();

    public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();

    public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();

    public static final String DARK_RED = ChatColor.DARK_RED.toString();

    public static final String D_BLUE = DARK_BLUE;

    public static final String D_AQUA = DARK_AQUA;

    public static final String D_GRAY = DARK_GRAY;

    public static final String D_GREEN = DARK_GREEN;

    public static final String D_PURPLE = DARK_PURPLE;

    public static final String D_RED = DARK_RED;

    public static final String LIGHT_PURPLE = ChatColor.LIGHT_PURPLE.toString();

    public static final String L_PURPLE = LIGHT_PURPLE;

    public static final String PINK = L_PURPLE;

    public static final String B_BLUE = BLUE + B;

    public static final String B_AQUA = AQUA + B;

    public static final String B_YELLOW = YELLOW + B;

    public static final String B_RED = RED + B;

    public static final String B_GRAY = GRAY + B;

    public static final String B_GOLD = GOLD + B;

    public static final String B_GREEN = GREEN + B;

    public static final String B_WHITE = WHITE + B;

    public static final String B_BLACK = BLACK + B;

    public static final String BD_BLUE = D_BLUE + B;

    public static final String BD_AQUA = D_AQUA + B;

    public static final String BD_GRAY = D_GRAY + B;

    public static final String BD_GREEN = D_GREEN + B;

    public static final String BD_PURPLE = D_PURPLE + B;

    public static final String BD_RED = D_RED + B;

    public static final String BL_PURPLE = L_PURPLE + B;

    public static final String I_BLUE = BLUE + I;

    public static final String I_AQUA = AQUA + I;

    public static final String I_YELLOW = YELLOW + I;

    public static final String I_RED = RED + I;

    public static final String I_GRAY = GRAY + I;

    public static final String I_GOLD = GOLD + I;

    public static final String I_GREEN = GREEN + I;

    public static final String I_WHITE = WHITE + I;

    public static final String I_BLACK = BLACK + I;

    public static final String ID_RED = D_RED + I;

    public static final String ID_BLUE = D_BLUE + I;

    public static final String ID_AQUA = D_AQUA + I;

    public static final String ID_GRAY = D_GRAY + I;

    public static final String ID_GREEN = D_GREEN + I;

    public static final String ID_PURPLE = D_PURPLE + I;

    public static final String IL_PURPLE = L_PURPLE + I;

    public static final String VAPE = "§8 §8 §1 §3 §3 §7 §8 §r";

    public static final String BLANK_LINE = "§8 §8 §1 §3 §3 §7 §8 §r";

    public static final String BL = "§8 §8 §1 §3 §3 §7 §8 §r";
    public static String MENU_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------";
    public static String M_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "-------------";
    public static String CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------";
    public static String MEDIUM_CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------";

    public static String tokenSkin(int tokenAmount, boolean capitalize) {
        return (capitalize ? "T" : "t") + "oken" + (tokenAmount == 1 ? "" : "s");
    }

    public static List<String> translate(List<String> input) {
        return input.stream().map(ChatUtils::translate).collect(Collectors.toList());
    }

}
