package me.dragonsteam.bungeestaffs.utils.formats.util;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 27/12/2021 - 23:40.
 */
public class ColorUtil {

    public static String hsvGradient(String str, String extra, Color from, Color to, Interpolator interpolator) {
        // returns a float-array where hsv[0] = hue, hsv[1] = saturation, hsv[2] = value/brightness
        final float[] hsvFrom = Color.RGBtoHSB(from.getRed(), from.getGreen(), from.getBlue(), null);
        final float[] hsvTo = Color.RGBtoHSB(to.getRed(), to.getGreen(), to.getBlue(), null);

        final double[] h = interpolator.interpolate(hsvFrom[0], hsvTo[0], str.length());
        final double[] s = interpolator.interpolate(hsvFrom[1], hsvTo[1], str.length());
        final double[] v = interpolator.interpolate(hsvFrom[2], hsvTo[2], str.length());

        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            builder.append(extra).append(ChatColor.of(Color.getHSBColor((float) h[i], (float) s[i], (float) v[i]))).append(str.charAt(i));
        }
        return builder.toString();
    }

    public static String hsvGradient(String str, Color from, Color to, Interpolator interpolator) {
        return hsvGradient(str, "", from, to, interpolator);
    }

    public static String rgbGradient(String str, Color from, Color to, Interpolator interpolator) {
        // interpolate each component separately
        final double[] red = interpolator.interpolate(from.getRed(), to.getRed(), str.length());
        final double[] green = interpolator.interpolate(from.getGreen(), to.getGreen(), str.length());
        final double[] blue = interpolator.interpolate(from.getBlue(), to.getBlue(), str.length());

        final StringBuilder builder = new StringBuilder();

        // create a string that matches the input-string but has
        // the different color applied to each char
        for (int i = 0; i < str.length(); i++) {
            builder.append(ChatColor.of(new Color(
                            (int) Math.round(red[i]),
                            (int) Math.round(green[i]),
                            (int) Math.round(blue[i]))))
                    .append(str.charAt(i));
        }

        return builder.toString();
    }

    public static double[] linear(double from, double to, int max) {
        final double[] res = new double[max];
        for (int i = 0; i < max; i++) {
            res[i] = from + i * ((to - from) / (max - 1));
        }
        return res;
    }

    // mode == true: starts of "slow" and becomes "faster", see the orange curve
    // mode == false: starts of "fast" and becomes "slower", see the yellow curve
    public static double[] quadratic(double from, double to, int max) {
        final double[] results = new double[max];
        if (true) {
            double a = (to - from) / (max * max);
            for (int i = 0; i < results.length; i++)
                results[i] = a * i * i + from;
        } else {
            double a = (from - to) / (max * max);
            double b = - 2 * a * max;
            for (int i = 0; i < results.length; i++)
                results[i] = a * i * i + b * i + from;
        }
        return results;
    }

}
