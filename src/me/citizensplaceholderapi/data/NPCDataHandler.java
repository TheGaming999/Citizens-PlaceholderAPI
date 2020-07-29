package me.citizensplaceholderapi.data;

import java.util.UUID;

public class NPCDataHandler {

	private UUID uniqueId;
	private String skinPlaceholder;
	private String namePlaceholder;
	private int updateID;
	
	public NPCDataHandler() {}

	public UUID getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(UUID uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getSkinPlaceholder() {
		return skinPlaceholder;
	}

	public void setSkinPlaceholder(String skinPlaceholder) {
		this.skinPlaceholder = skinPlaceholder;
	}

	public String getNamePlaceholder() {
		return namePlaceholder;
	}

	public void setNamePlaceholder(String namePlaceholder) {
		this.namePlaceholder = namePlaceholder;
	}

	public int getUpdateID() {
		return updateID;
	}

	public void setUpdateID(int updateID) {
		this.updateID = updateID;
	}
	
}
