package me.citizensplaceholderapi.data;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import co.aikar.taskchain.TaskChain;
import me.citizensplaceholderapi.CitizensPlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.SkinLayers;
import net.citizensnpcs.trait.SkinTrait;
import net.citizensnpcs.util.NMS;

public class NPCDataStorage {

	private CitizensPlaceholderAPI plugin;
	private Map<UUID, NPCDataHandler> npcStorage;
	private List<String> namePlaceholders;
	private List<String> skinPlaceholders;
	private ConfigurationSection mainSection;
	private int updateTime;
	private int i;
	
	
	public NPCDataStorage(CitizensPlaceholderAPI plugin) {
		this.plugin = plugin;
		this.npcStorage = new ConcurrentHashMap<>();
		this.namePlaceholders = Lists.newLinkedList();
		this.skinPlaceholders = Lists.newLinkedList();
        this.mainSection = this.plugin.getDataConfig().getConfigurationSection("npcs");
        this.updateTime = 60 * 20;
        this.i = -1;
	}
	
	public void setUpdateTime(int updateTime) {
		this.updateTime = updateTime;
	}
	
	public int getUpdateTime() {
		return updateTime;
	}
	
	public int getIncrement() {
		return i;
	}
	
	public void setIncrement(int i) {
		this.i = i;
	}
	
	void addNamePlaceholder(NPCDataHandler ndh) {
		if(ndh == null || ndh.getNamePlaceholder() == null) {
			return;
		}
		namePlaceholders.add(ndh.getNamePlaceholder());
	}
	
	void addSkinPlaceholder(NPCDataHandler ndh) {
		if(ndh == null || ndh.getSkinPlaceholder() == null) {
			return;
		}
		skinPlaceholders.add(ndh.getSkinPlaceholder());
	}
	
	public void load() {
		i = -1;
		mainSection.getKeys(false).forEach(ids -> {
			i++;
			NPCDataHandler ndh = new NPCDataHandler();
			ndh.setUniqueId(UUID.fromString(ids));
			String path = ids + ".name-placeholder";
			String path2 = ids + ".skin-placeholder";
			ndh.setNamePlaceholder(mainSection.isSet(path) ? mainSection.getString(path) : null);
			ndh.setSkinPlaceholder(mainSection.isSet(path2) ? mainSection.getString(ids + ".skin-placeholder") : null);
			ndh.setUpdateID(i);
			npcStorage.put(ndh.getUniqueId(), ndh);
			addNamePlaceholder(ndh);
			addSkinPlaceholder(ndh);
		});	
		startPlaceholderUpdater();
	}
	
	public int getNextID() {
		if(npcStorage.isEmpty()) {
			return 0;
		}
		return npcStorage.size();
	}
	
	public List<String> getNamePlaceholders() {
		return this.namePlaceholders;
	}
	
	public List<String> getSkinPlaceholders() {
		return this.skinPlaceholders;
	}
	
	public void saveNPC(final UUID uniqueId, final String skinPlaceholder, final String namePlaceholder) {
		if(npcStorage.containsKey(uniqueId)) {
			NPCDataHandler ndh = npcStorage.get(uniqueId);
			ndh.setUniqueId(uniqueId);
			if(namePlaceholder != null) { ndh.setNamePlaceholder(namePlaceholder); mainSection.set(uniqueId.toString() + ".name-placeholder", namePlaceholder); addNamePlaceholder(ndh); }
			if(skinPlaceholder != null) { ndh.setSkinPlaceholder(skinPlaceholder); mainSection.set(uniqueId.toString() + ".skin-placeholder", skinPlaceholder); addSkinPlaceholder(ndh); }
			ndh.setUpdateID(getNextID());
			npcStorage.put(uniqueId, ndh);
		} else {
		NPCDataHandler ndh = new NPCDataHandler();
		ndh.setUniqueId(uniqueId);
		ndh.setNamePlaceholder(namePlaceholder);
		ndh.setSkinPlaceholder(skinPlaceholder);
		ndh.setUpdateID(getNextID());
		npcStorage.put(ndh.getUniqueId(), ndh);
		mainSection.set(uniqueId.toString() + ".name-placeholder", namePlaceholder);
		mainSection.set(uniqueId.toString() + ".skin-placeholder", skinPlaceholder);
		addNamePlaceholder(ndh);
		addSkinPlaceholder(ndh);
		}
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			update();
			plugin.getConfigManager().saveConfig("data.yml");
		});
	}
	
	public void deleteNPC(final UUID uniqueId) {
		NPCDataHandler ndh = new NPCDataHandler();
		ndh.setUniqueId(uniqueId);
		ndh.setNamePlaceholder(null);
		ndh.setSkinPlaceholder(null);
		namePlaceholders.remove(ndh.getUpdateID());
		npcStorage.remove(ndh.getUniqueId());
		String uuid = uniqueId.toString();
		mainSection.set(uuid + ".name-placeholder", null);
		mainSection.set(uuid + ".skin-placeholder", null);
		mainSection.set(uuid, null);
		plugin.getConfigManager().saveConfig("data.yml");
	}
	
	public void setNamePAPI(Player player, String string) {
		namePlaceholders.set(namePlaceholders.indexOf(string), PlaceholderAPI.setPlaceholders(player, string));
	}
	
	// 1
	// set (indexOf string , get from i)
	public void setSkinPAPI(Player player, String string) {
		skinPlaceholders.set(skinPlaceholders.indexOf(string), PlaceholderAPI.setPlaceholders(player, string));
	}
	
	public void startPlaceholderUpdater() {
           Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
        	   update();
           }, updateTime, updateTime);
	}
	
	public Map<UUID, NPCDataHandler> getStorage() {
		return this.npcStorage;
	}
	
	public void update() {
        i = -1;
		Bukkit.getOnlinePlayers().forEach(player -> {
			AccessibleString replaced = new AccessibleString("");
			AccessibleString replaced2 = new AccessibleString("");
			for(NPCDataHandler npcDataHandler : npcStorage.values()) {
				i++;
				NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(npcDataHandler.getUniqueId());
				if(!npc.isSpawned()) {
					return;
				}
				if(npc.getEntity() == null) {
					return;
				}
				if(npcDataHandler.getNamePlaceholder() != null) {
			    	TaskChain <?> tc = plugin.getTaskChainFactory().newSharedChain("citizensUpdateName");
				    tc
				    .async(() -> {
				    	//setNamePAPI(player, namePlaceholders.get(i));
				    	replaced.setString(PlaceholderAPI.setPlaceholders(player, npcDataHandler.getNamePlaceholder()));
			        })
				    .sync(() -> { npc.setName(replaced.getString()); })
				    .execute();
				}
				if(npcDataHandler.getSkinPlaceholder() == null) {
					return;
				}
		    	TaskChain <?> tc = plugin.getTaskChainFactory().newSharedChain("citizensUpdateSkin");
			    tc
			    .async(() -> {
			    	//setSkinPAPI(player, skinPlaceholders.get(i));
			    	replaced2.setString(PlaceholderAPI.setPlaceholders(player, npcDataHandler.getSkinPlaceholder()));
					npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, replaced2.getString());
					npc.data().set(NPC.PLAYER_SKIN_USE_LATEST, false);
		        })
			    .sync(() -> { 
					SkinnableEntity skinnableEntity = (SkinnableEntity)npc.getEntity();
					try {
					SkinTrait st = npc.getTrait(SkinTrait.class);
					st.setSkinName(replaced2.getString(), true);
					} catch (Exception ex) {
						
					}
					if(skinnableEntity != null) {
						skinnableEntity.setSkinName(replaced2.getString());
						if(skinnableEntity.getSkinTracker() != null)
						skinnableEntity.getSkinTracker().notifySkinChange(true);
					}
			    })
			    .execute();
			}
			});
	}
	
}
