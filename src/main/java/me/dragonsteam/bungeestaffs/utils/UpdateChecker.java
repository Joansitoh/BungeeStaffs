package me.dragonsteam.bungeestaffs.utils;

import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/10/2021 - 23:41.
 */

public class UpdateChecker {

    private Plugin plugin;
    private int resourceId;

    public UpdateChecker(Plugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        Runnables.runAsync(() -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }
}