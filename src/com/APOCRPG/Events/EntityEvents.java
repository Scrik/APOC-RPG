package com.APOCRPG.Events;

import java.awt.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
//import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import com.APOCRPG.API.ItemAPI;
import com.APOCRPG.Main.Plugin;

public class EntityEvents implements Listener {
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent Event) {
		EntityType Type = Event.getEntityType();
		if ((Type == EntityType.CREEPER || Type == EntityType.ZOMBIE || Type == EntityType.PIG_ZOMBIE || Type == EntityType.SKELETON || Type == EntityType.SPIDER || Type == EntityType.GHAST || Type == EntityType.ENDERMAN || Type == EntityType.WITHER || Type == EntityType.ENDER_DRAGON) && Plugin.Random.nextInt(100) <= 25) {
			LivingEntity Entity = Event.getEntity();
			int Amount = Plugin.Random.nextInt(2) + 1;
			for (int i=0; i<Amount; i++) {
				int Gear = Plugin.Random.nextInt(5);
				if (Gear == 0) {
					Entity.getEquipment().setItemInHand(ItemAPI.createArmor(4));
				} else if (Gear == 1) {
					Entity.getEquipment().setHelmet(ItemAPI.createArmor(0));
				} else if (Gear == 2) {
					Entity.getEquipment().setChestplate(ItemAPI.createArmor(1));
				} else if (Gear == 3) {
					Entity.getEquipment().setLeggings(ItemAPI.createArmor(2));
				} else if (Gear == 4) {
					Entity.getEquipment().setBoots(ItemAPI.createArmor(3));
				}
			}
		}
	}
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player po = event.getPlayer();
		EffectPollingEvent devent = new EffectPollingEvent(po);
		Bukkit.getServer().getPluginManager().callEvent(devent);
		//Bukkit.getServer().broadcastMessage(devent.getMessage());
	}
	@EventHandler
	public void onItemSwitched(PlayerItemHeldEvent event) {
		Player po = event.getPlayer();
		EffectPollingEvent devent = new EffectPollingEvent(po);
		Bukkit.getServer().getPluginManager().callEvent(devent);
		//Bukkit.getServer().broadcastMessage(devent.getMessage());
	}
	
	/**
	 * This method is used to prevent a player from taking inventory items owned by another player
	 * @param event
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		// get player who did the action
		Player po= (Player) event.getWhoClicked();
		// get polling event
		EffectPollingEvent devent = new EffectPollingEvent(po);
		// get item
		ItemStack item = event.getCurrentItem();
		// get lore containing "Player bound:"
		ArrayList<String> boundPlayers = (ArrayList<String>) Plugin.getLoreContaining(item, Plugin.LORE_PLAYER_BOUND);
		// iterate through lore to make sure that the item belongs to the player
		for ( String bp : boundPlayers ) {
			if ( !bp.replace(Plugin.LORE_PLAYER_BOUND+" ", "").equals(po.getName()))  {
				// item belongs to another player
				po.sendMessage( Plugin.APOCRPG_ERROR + "This item does not belong to you!");
				event.setCancelled(true);
				return;
			}
		}
		// if still here, item is not owned or owned by player
		Bukkit.getServer().getPluginManager().callEvent(devent);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player po = (Player)event.getPlayer();
		EffectPollingEvent devent = new EffectPollingEvent(po);
		Bukkit.getServer().getPluginManager().callEvent(devent);
	}
	
	@EventHandler
	public void onEnitityDeath(EntityDeathEvent event){
		EntityType et = event.getEntityType();
		System.out.println("Dead: "+et.name().toString()+" by: "+event.getEventName());
		LivingEntity entity = event.getEntity();
		if ( entity instanceof Player ){
			ArrayList<ItemStack> is = (ArrayList<ItemStack>)event.getDrops();
			for ( ItemStack item:is){
				((Player) entity).sendMessage("Dropping: "+item.getItemMeta().getDisplayName());
			}
		}
	}
	
	/**
	 * This method is used to prevent the player from picking up items bound to another player
	 * @param event
	 */
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event ){
		//get item
		ItemStack item = event.getItem().getItemStack();
		// check item for lore
		if ( Plugin.hasLore(item) ){
			//get player
			Player player = (Player)event.getPlayer();
			//get player name
			String playerName = player.getName();
			// create lore string as it would be if the player owns the item
			String loreString = Plugin.LORE_PLAYER_BOUND + " " + playerName;
			// default lore array list
			ArrayList<String> lore = (ArrayList<String>)item.getItemMeta().getLore();
			// iterate through lore 
			for ( String s : lore ) {
				// if lore starts with "Player bound" but does not equal player's
				// lore string, this item is owned by another player.
				if ( s.startsWith(Plugin.LORE_PLAYER_BOUND) && !s.equals(loreString))  
				{
					// cancel event as item is owned by another player
					event.setCancelled(true);
				}
			}
		}
	}
}