package me.dragonsteam.bungeestaffs.utils.formats.util;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 28/12/2021 - 0:36.
 */
@FunctionalInterface
public interface Interpolator {

    double[] interpolate(double from, double to, int max);

}
