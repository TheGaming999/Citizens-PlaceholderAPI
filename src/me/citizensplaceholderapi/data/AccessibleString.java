package me.citizensplaceholderapi.data;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

public class AccessibleString {

	@Nonnull
	private String string;
	
	public AccessibleString() {
		this.string = "";
	}

	public AccessibleString(@Nonnull String string) {
		this.string = string;
	}
	
	public void setString(@Nonnull String string) {
		this.string = string;
	}
	
	@Nonnull
	public String getString() {
		return this.string;
	}
	
	/**
	 * 
	 * @param endIndex
	 * @return characters that the string start with till endIndex
	 */
	@Nonnull
	public String getStartsWith(int endIndex) {
		int counter = -1;
		StringBuilder sb = new StringBuilder();
		for(char character : this.string.toCharArray()) {
			counter++;
			if(counter >= endIndex)
				break;
			sb.append(character);
		}
		return sb.toString();
	}
	
	public String substring(int beginIndex) {
		return this.string.substring(beginIndex);
	}
	
	public String substring(int beginIndex, int endIndex) {
		return this.string.substring(beginIndex, endIndex);
	}
	
	public static AccessibleString createString(String string) {
		return new AccessibleString(string);
	}
	
	public static AccessibleString parseString(Object object) {
		String newString = null;
		if(object instanceof Number) {
			newString = String.valueOf(object);
			return new AccessibleString(newString);
		} else if (object instanceof Collection) {
			object = (Collection)object;
			newString = object.toString();
			return new AccessibleString(newString);
		} else if (object instanceof Map) {
			object = (Map)object;
			newString = ((Map) object).entrySet().toString();
			return new AccessibleString(newString);
		} else if (object instanceof UUID) {
			object = (UUID)object;
			newString = object.toString();
			return new AccessibleString(newString);
		}
		return new AccessibleString(String.valueOf(object));
	}
	
	public String toString() {
		return this.string;
	}
	
}
