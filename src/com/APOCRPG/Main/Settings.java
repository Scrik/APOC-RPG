package com.APOCRPG.Main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {
	
	private FileConfiguration Config = null;
	private List<String> Prefixes = new ArrayList<String>();
	private List<String> Suffixes = new ArrayList<String>();
	private List<String> DisabledWorlds = new ArrayList<String>();
	
	public Settings(FileConfiguration Config) {
		this.Config = Config;
		Prefixes = Config.getStringList("RPG-Prefix");
		Suffixes = Config.getStringList("RPG-Suffix");
		DisabledWorlds = Config.getStringList("Dungeons.disable-dungeons-on-world");
	}

	public boolean getBoolean(String key) {
		return this.Config.getBoolean(key);
	}

	public int getInt(String key) {
		return this.Config.getInt(key);
	}
	
	public String getRandomPrefix() {
		return Prefixes.get(Plugin.Random.nextInt(Prefixes.size()));
	}
	
	public String getRandomSuffix() {
		return Suffixes.get(Plugin.Random.nextInt(Suffixes.size()));
	}
	
	public boolean areDungeonsEnabledInWorld(World world) {
		return !DisabledWorlds.contains(world.getName());
	}
}