package me.citizensplaceholderapi;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import me.citizensplaceholderapi.commands.CitizensPlaceholderAPICommand;
import me.citizensplaceholderapi.data.ConfigManager;
import me.citizensplaceholderapi.data.NPCDataStorage;
import net.citizensnpcs.api.event.NPCRemoveEvent;

public class CitizensPlaceholderAPI extends JavaPlugin implements Listener {

	private ConfigManager configManager;
	private FileConfiguration dataConfig;
	private NPCDataStorage npcDataStorage;
	private CitizensPlaceholderAPICommand command;
    private TaskChainFactory taskChainFactory;
    private CitizensPlaceholderAPI instance;
    
    public <T> TaskChain<T> newChain() {
        return getTaskChainFactory().newChain();
    }
    public <T> TaskChain<T> newSharedChain(String name) {
        return getTaskChainFactory().newSharedChain(name);
    }
	
	public boolean removeNPConEvent;
	
	public void onEnable() {
		this.taskChainFactory = BukkitTaskChainFactory.create(this);
		this.configManager = new ConfigManager(this);
		this.dataConfig = configManager.loadConfig("data.yml");
		this.npcDataStorage = new NPCDataStorage(this);
		this.npcDataStorage.load();
		this.command = new CitizensPlaceholderAPICommand(this);
		this.removeNPConEvent = true;
		instance = this;

		getCommand("citizensplaceholderapi").setExecutor(command);
		Bukkit.getPluginManager().registerEvents(this, this);
		getLogger().info("Enabled");
	}
	
	public int i() {
		return 0;
	}
	
	public void onDisable() {
		getLogger().info("Disable");
	}

	@EventHandler
	public void onNPCRemove(NPCRemoveEvent e) {
		if(this.removeNPConEvent)
		this.npcDataStorage.deleteNPC(e.getNPC().getUniqueId());
	}
	
	public ConfigManager getConfigManager() {
		return configManager;
	}

	public FileConfiguration getDataConfig() {
		return dataConfig;
	}

	public NPCDataStorage getNPCDataStorage() {
		return npcDataStorage;
	}

	public CitizensPlaceholderAPICommand getCommand() {
		return command;
	}

	public void setCommand(CitizensPlaceholderAPICommand command) {
		this.command = command;
	}
	
	public CitizensPlaceholderAPI getInstance() {
		return instance;
	}
	public TaskChainFactory getTaskChainFactory() {
		return taskChainFactory;
	}
	public void setTaskChainFactory(TaskChainFactory taskChainFactory) {
		this.taskChainFactory = taskChainFactory;
	}


	
}
