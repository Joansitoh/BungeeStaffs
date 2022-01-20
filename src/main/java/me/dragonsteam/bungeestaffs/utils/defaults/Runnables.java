package me.dragonsteam.bungeestaffs.utils.defaults;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.dragonsteam.bungeestaffs.bStaffs;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Runnables {

    public static ThreadFactory newThreadFactory(String name) {
        return (new ThreadFactoryBuilder()).setNameFormat(name).build();
    }

    public static void runAsync(Callable callable) {
        bStaffs.INSTANCE.getProxy().getScheduler().runAsync(bStaffs.INSTANCE, callable::call);
    }

    public static void runLater(Callable callable, long delay, TimeUnit unit) {
        bStaffs.INSTANCE.getProxy().getScheduler().schedule(bStaffs.INSTANCE, callable::call, delay, unit);
    }

    public static ScheduledTask runTimer(Callable callable, long delay, long interval) {
        return bStaffs.INSTANCE.getProxy().getScheduler().schedule(bStaffs.INSTANCE, callable::call, delay, interval, TimeUnit.SECONDS);
    }

    public static ScheduledTask runTimerAsync(Callable callable, long delay, long interval) {
        return bStaffs.INSTANCE.getProxy().getScheduler().schedule(bStaffs.INSTANCE, callable::call, delay, interval, TimeUnit.SECONDS);
    }

    public interface Callable {
        void call();
    }
}
