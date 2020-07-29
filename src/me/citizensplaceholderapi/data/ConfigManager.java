package me.citizensplaceholderapi.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

	private Map<String, FileConfiguration> configs;
	private JavaPlugin plugin;
	
	public ConfigManager(JavaPlugin plugin) {
		this.configs = new ConcurrentHashMap<>();
		this.plugin = plugin;
	}
	
	private String correctConfigName(String configName) {
		StringBuilder sb = new StringBuilder(configName);
		if(!sb.toString().endsWith(".yml")) {
			return sb.append(".yml").toString();
		} else {
			return sb.toString();
		}
	}
	
	/**
	 * Creates a config file and load it if it doesn't exist. otherwise just load it.
	 * @param configName (config .yml name) (example: data.yml)
	 * @return FileConfiguration object of the config name
	 */
	public FileConfiguration loadConfig(String configName) {
        File configFile = new File(plugin.getDataFolder(), correctConfigName(configName));
        if (!configFile.exists()) { configFile.getParentFile().mkdirs(); plugin.saveResource(correctConfigName(configName), false); }
        FileConfiguration configYaml = new YamlConfiguration();
            try {
				configYaml.load(configFile);
			} catch (FileNotFoundException e) { 	System.out.print("File Not Found <?>"); e.printStackTrace(); } catch (IOException e) {
     	System.out.print("Read write Failed <!>"); e.printStackTrace(); } catch (InvalidConfigurationException e) { System.out.print("Corrupted Configuration File <?>");
     	e.printStackTrace(); }
            configs.put(configName, configYaml);
		return configYaml;
	}
	
	/**
	 * Creates a config file and load it if it doesn't exist. otherwise just load it.
	 * @param configName (config .yml name) (example: data.yml)
	 * @param copyDefaults should we copy default config sections and paths ?
	 * @return FileConfiguration object of the config name
	 */
	public FileConfiguration loadConfig(String configName, boolean copyDefaults) {
        File configFile = new File(plugin.getDataFolder(), correctConfigName(configName));
        if (!configFile.exists()) { configFile.getParentFile().mkdirs(); plugin.saveResource(correctConfigName(configName), false); }
        FileConfiguration configYaml = new YamlConfiguration();
            try {
				configYaml.load(configFile);
			} catch (FileNotFoundException e) { 	System.out.print("File Not Found <?>"); e.printStackTrace(); } catch (IOException e) {
     	System.out.print("Read write Failed <!>"); e.printStackTrace(); } catch (InvalidConfigurationException e) { System.out.print("Corrupted Configuration File <?>");
     	e.printStackTrace(); }
            if(copyDefaults) {
            	configYaml.getDefaults().options().copyDefaults(true);
            }
            configs.put(configName, configYaml);
		return configYaml;
	}
	
	/**
	 * 
	 * @param configName
	 * @return cached config from the map
	 */
	public FileConfiguration getConfig(String configName) {
		return configs.get(configName);
	}
	
	/**
	 * 
	 * @param configName
	 * @return reloads a config and returns it.
	 */
	public FileConfiguration reloadConfig(String configName) {
		String correctedName = correctConfigName(configName);
		FileConfiguration configYaml = configs.get(correctedName);
		configYaml = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), correctedName));
		return configYaml;
	}
	
	/**
	 * 
	 * @param configName
	 * @return saves the loaded config to your plugin folder and returns it.
	 */
	public FileConfiguration saveConfig(String configName) {
		String correctedName = correctConfigName(configName);
		FileConfiguration configYaml = configs.get(correctedName);
		try {
			configYaml.save(new File(plugin.getDataFolder(), correctedName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return configYaml;
	}
	
}