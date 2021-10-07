package me.dragonsteam.bungeestaffs.utils.defaults;

import lombok.Getter;
import me.dragonsteam.bungeestaffs.bStaffs;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Joansiitoh (DragonsTeam && SkillTeam)
 * Date: 02/08/2021 - 16:08.
 */
@Getter
public class ConfigFile {

    @Getter
    private static final List<ConfigFile> fileConfigs = new ArrayList<>();

    private final File file;
    private Configuration configuration;

    public ConfigFile(Plugin plugin, String path) {
        this.file = new File(plugin.getDataFolder(), path);

        if (!this.file.exists()) {
            plugin.getDataFolder().mkdir();
            try {
                Files.copy(plugin.getResourceAsStream(path), this.file.toPath());
                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void reloadConfig() {
        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getDouble(String path) {
        if (configuration.contains(path))
            return configuration.getDouble(path);
        return 0;
    }

    public int getInt(String path) {
        if (configuration.contains(path))
            return configuration.getInt(path);
        return 0;
    }

    public boolean getBoolean(String path) {
        if (configuration.contains(path))
            return configuration.getBoolean(path);
        return false;
    }

    public long getLong(String path) {
        if (configuration.contains(path))
            return configuration.getLong(path);
        return 0L;
    }

    public String getString(String path) {
        if (configuration.contains(path))
            return ChatUtils.translate(configuration.getString(path));
        return "";
    }

    public String getString(String path, String callback) {
        if (configuration.contains(path))
            return ChatUtils.translate(configuration.getString(path));
        return ChatUtils.translate(callback);
    }

    public List<String> getReversedStringList(String path) {
        List<String> list = getStringList(path);
        if (list != null) {
            int size = list.size();
            List<String> toReturn = new ArrayList<>();
            for (int i = size - 1; i >= 0; i--) {
                toReturn.add(list.get(i));
            }
            return toReturn;
        }
        return Collections.singletonList("ERROR: STRING LIST NOT FOUND!");
    }

    public List<String> getStringList(String path) {
        if (configuration.contains(path)) {
            ArrayList<String> strings = new ArrayList<>();
            for (String string : configuration.getStringList(path)) {
                strings.add(ChatColor.translateAlternateColorCodes('&', string));
            }
            return strings;
        }
        return Collections.singletonList("ERROR: STRING LIST NOT FOUND!");
    }

    public List<String> getStringListOrDefault(String path, List<String> toReturn) {
        if (configuration.contains(path)) {
            ArrayList<String> strings = new ArrayList<>();
            for (String string : configuration.getStringList(path)) {
                strings.add(ChatColor.translateAlternateColorCodes('&', string));
            }
            return strings;
        }
        return toReturn;
    }

    public void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.configuration, this.file);
        } catch (IOException e) {
            bStaffs.logger("Could not save config file " + this.file.toString());
        }
    }

}
