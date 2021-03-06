package com.APOCRPG.Events;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.World;
//import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Material;
import com.APOCRPG.API.EffectAPI;
import com.APOCRPG.Main.Plugin;


public class MiningEventsListener implements Listener {
	Location loc = new Location(null, 0, 0, 0);
	@EventHandler
	public void onBlockBreak(BlockDamageEvent eve) {
		Player plyr = eve.getPlayer();
		if (plyr.getItemInHand() !=null){
			ItemStack itm = plyr.getItemInHand();
			if (itm.hasItemMeta()) {
				HashMap<String, Integer> effects = EffectAPI.getEffectsFromItem(itm);
				for (Entry<String, Integer> e : effects.entrySet()) {
					switch (e.getKey()) {
					case "Breaking":
						//level limiter to scale back the effect of the gem
						int lvl = e.getValue() / 2;
						double chance = 2.5 + (e.getValue() * .75);
						if (lvl >= 13) {
							lvl = 12;
						}
						if (chance >= 26) {
							chance = 25;
						}
						//Effect of the gem
						if (chance >= Plugin.Random.nextInt(100)) {
							((Player)e).addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 200, lvl));
						}
					break;
					case "Demolition":
						if (2.5 + (e.getValue() * .75) >= Plugin.Random.nextInt(100)) {
							World w = loc.getWorld();
					        w.createExplosion(loc.getX(), loc.getY(), loc.getZ(), 2F, false, true);
						}
					break;
					case "Smelting": //only added vanilla ores, looking up a way to add modded ore.
						if (10 + (e.getValue() * 10) >= Plugin.Random.nextInt(100)) {
							if (!(e instanceof BlockBreakEvent)) {
					            return;
					        }
							if (((Location) e).getBlock().getType().equals(new ItemStack(Material.IRON_ORE))) {
					            ((Location) e).getBlock().setType(Material.AIR);
					            ((Location) e).getBlock().getWorld().dropItemNaturally(((Location) e).getBlock().getLocation(), new ItemStack(Material.IRON_INGOT));
					        }
							if (((Location) e).getBlock().getType().equals(new ItemStack(Material.GOLD_ORE))) {
					            ((Location) e).getBlock().setType(Material.AIR);
					            ((Location) e).getBlock().getWorld().dropItemNaturally(((Location) e).getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT));
					        }
						}
					break;
					}					
				}
			}
		}
	}		
}
