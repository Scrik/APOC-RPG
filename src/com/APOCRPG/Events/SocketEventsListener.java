package com.APOCRPG.Events;

import com.APOCRPG.API.EffectAPI;
import com.APOCRPG.Items.Gems;
import com.APOCRPG.Items.Items;
import com.APOCRPG.Main.Plugin;
import com.APOCRPG.Main.Settings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SocketEventsListener implements Listener {
	// Why is this a hashmap?
	// This doesn't seem to need to ever hold more than one item and player
	// ever.
	// Weird.
	// Considering that the handler runs once per event call.
	private HashMap<Player, ItemStack> SelectedSocket = new HashMap<Player, ItemStack>();
	private HashMap<Player, Integer> SelectedSlot = new HashMap<Player, Integer>();

	public SocketEventsListener() {
		Plugin.instance.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.instance, new Runnable() {
			public void run() {
				for (Player player : Plugin.instance.getServer().getOnlinePlayers()) {
					if (SelectedSocket.containsKey(player) && SelectedSocket.get(player) != null) {
						SelectedSocket.put(player, null);
						player.sendMessage("Socket unselected!");
					}
				}
			}
		}, 1200l, 1200l);
	}

	private ItemStack mergeGems(ItemStack gem1, ItemStack gem2) {
		ItemStack retval = null;
		ItemMeta gem1Meta = gem1.getItemMeta();
		ArrayList<String> gem1Lore = (ArrayList<String>) gem1Meta.getLore();
		String gem1Effect = null;
		String gem1Type = null;
		String gem1Level = null;
		int gem1IntLvl = -1;

		// parse gem1 data
		for (String s1 : gem1Lore) {
			if (s1.startsWith(Settings.Cfg.LORE_GEM_OF.getString())) {
				try {
					gem1Level = s1.substring(s1.lastIndexOf(" ") + 1);
					// remove gem lore prefix and level
					gem1Effect = s1.replace(Settings.Cfg.LORE_GEM_OF.getString(), "").replace(" " + gem1Level, "");
					try {
						gem1IntLvl = Integer.parseInt(gem1Level);
					} catch (Exception e) {
						gem1IntLvl = Plugin.romanToInt(gem1Level);
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} else if (s1.endsWith("type")) {
				gem1Type = s1;
			}
		}

		ItemMeta gem2Meta = gem2.getItemMeta();
		ArrayList<String> gem2Lore = (ArrayList<String>) gem2Meta.getLore();
		String gem2Effect = null;
		String gem2Type = null;
		String gem2Level = null;
		int gem2IntLvl = -1;

		// parse gem2 data
		for (String s2 : gem2Lore) {
			if (s2.startsWith(Settings.Cfg.LORE_GEM_OF.getString())) {
				try {
					gem2Level = s2.substring(s2.lastIndexOf(" ") + 1);
					// remove gem lore prefix and level
					gem2Effect = s2.replace(Settings.Cfg.LORE_GEM_OF.getString(), "").replace(" " + gem2Level, "");
					try {
						gem2IntLvl = Integer.parseInt(gem2Level);
					} catch (Exception e) {
						gem2IntLvl = Plugin.romanToInt(gem2Level);
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} else if (s2.endsWith("type")) {
				gem2Type = s2;
			}
		}

		// compare gem effects
		if (!gem1Effect.equals(gem2Effect) || !gem1Type.equals(gem2Type)) {
			// these are not the same types of gems
			return null;
		}
		// compare gem levels. if both levels are not greater than 0 or the
		// combined levels are greater than 10, then fail
		if (gem1IntLvl < 1 || gem2IntLvl < 1 || (gem1IntLvl + gem2IntLvl) > 10) {
			return null;
		}

		return Gems.createGem(gem1Effect, gem1Type, Plugin.intToRoman((gem1IntLvl + gem2IntLvl)));
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerRightClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();
		ItemMeta itemMeta = (item != null && item.hasItemMeta()) ? item.getItemMeta()
				: Plugin.instance.getServer().getItemFactory().getItemMeta(item.getType());
		List<String> itemLore = (itemMeta != null && itemMeta.hasLore()) ? item.getItemMeta().getLore()
				: new ArrayList<String>();
		Inventory inv = player.getInventory();

		if (item != null && !item.getData().equals(Material.AIR)
				&& (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			if (item.getType().equals(Material.WRITTEN_BOOK) && Plugin.containsLoreText(itemLore, Settings.Cfg.LORE_TOME.getString())) {
				BookMeta bm = (BookMeta) item.getItemMeta();
				if (bm == null || !bm.hasAuthor() || !bm.hasTitle() && bm.getTitle().contains("Identify Tome")) {
					return;
				}

				ItemStack invItem = null;
				ItemStack unidItem = null;
				for (int i = 0; i < inv.getSize() && unidItem == null; i++) {
					invItem = inv.getItem(i);
					if (invItem != null && !invItem.getType().equals(Material.AIR)) {
						ItemMeta invMeta = invItem.getItemMeta();
						if (item != null && invMeta.getDisplayName() != null
								&& invMeta.getDisplayName().equals(Settings.Cfg.LORE_UNKNOWN_ITEM.getString())
								&& invMeta.getLore() == null) {
							unidItem = invItem;
						}
					}
				}
				if (unidItem != null && !unidItem.getType().equals(Material.AIR)) {
					ItemMeta unidMeta = unidItem.getItemMeta();
					if ((unidMeta != null && unidMeta.getDisplayName() != null)
							&& (unidMeta.getDisplayName().equals(Settings.Cfg.LORE_UNKNOWN_ITEM.getString())
									&& unidMeta.getLore() == null)) {
						inv.remove(unidItem);
						ItemStack clone = Items.diablofy(unidItem.clone(), Plugin.Random.nextInt(5));
						clone.setDurability(invItem.getDurability());
						Plugin.addLoreText(clone, Settings.Cfg.LORE_PLAYER_BOUND.getString(), player.getName());
						player.setItemInHand(clone);
						event.setUseItemInHand(Result.DENY);
						event.setCancelled(true);
						player.updateInventory();
						player.closeInventory();
						return;
					}
				} else {
					player.sendMessage(Settings.Cfg.APOCRPG_ERROR.getString() + "You have nothing to identify!");
					event.setUseItemInHand(Result.DENY);
					event.setCancelled(true);
					int slot = player.getInventory().getHeldItemSlot();
					inv.remove(item);
					player.setItemInHand(item);
					player.updateInventory();
					player.closeInventory();
				}
				// if we are still here, we haven't found any unidentified items
				// check worn armor

			} else {
				ItemStack gem = null;
				if (SelectedSocket.containsKey(player)) {
					gem = (ItemStack) SelectedSocket.get(player);
					String gemName = null;
					if (gem != null && gem.hasItemMeta() && gem.getItemMeta().hasDisplayName()) {
						gemName = gem.getItemMeta().getDisplayName();
					}
					if (gem != null && (gemName == null || !gemName.equals(Settings.Cfg.DISPLAY_NAME_GEM.getString()))) {
						gem = null;
					}
				}

				if (item.getType().equals(Material.EMERALD) && Plugin.containsLoreText(itemLore, Settings.Cfg.LORE_GEM_OF.getString())) {
					if (gem != null) {
						// create new gem
						ItemStack newGem = null;

						// get slot numbers for initial gem and gem to be merged
						int gemSlot = SelectedSlot.get(player);
						int itemSlot = player.getInventory().getHeldItemSlot();
						int itemAmt = item.getAmount();
						// check to see if the player double clicked on the same
						// stack
						if (itemSlot == gemSlot && itemAmt < 2) {
							player.sendMessage(Settings.Cfg.APOCRPG_ERROR.getString() + "You can not attach a gem to itself!");
						} else if (itemSlot == gemSlot && itemAmt >= 2) {
							newGem = mergeGems(gem, item);
							if (newGem != null) {
								if (item.getAmount() > 2) {
									item.setAmount(item.getAmount() - 2);
									inv.setItem(itemSlot, item);
									inv.addItem(newGem);
								} else {
									inv.setItem(itemSlot, newGem);
								}
							} else {
								player.sendMessage(Settings.Cfg.APOCRPG_ERROR.getString() + "You can not combine those gems.");
							}
						} else {
							newGem = mergeGems(gem, item);
							if (newGem != null) {
								inv.setItem(itemSlot, newGem);
								inv.setItem(gemSlot, null);
							} else {
								player.sendMessage(Settings.Cfg.APOCRPG_ERROR.getString() + "You can not combine those gems.");
							}
						}
						player.updateInventory();
						SelectedSocket.remove(player);
						SelectedSlot.remove(player);
						event.setCancelled(true);
					} else {
						SelectedSocket.put(player, player.getItemInHand());
						SelectedSlot.put(player, player.getInventory().getHeldItemSlot());
						player.sendMessage("Select the item with an empty Socket and right click.");
					}
				} else if (gem != null && itemLore != null
						&& (Plugin.containsLoreText(itemLore, Settings.Cfg.LORE_ITEM_SOCKET.getString())
								|| Plugin.containsLoreText(itemLore, Settings.Cfg.LORE_GEM_OF.getString()))) {
					boolean socketFound = false;

					if (gem != null && gem.getType().equals(Material.EMERALD)
							&& gem.getItemMeta().getDisplayName().equals(Settings.Cfg.DISPLAY_NAME_GEM.getString())) {
						// get gem lore information for effect, type, and level
						String gemEffect = null;
						String gemType = null;
						String gemStrLvl = null;
						int gemIntLvl = 0;
						String itemStrLvl = null;
						int itemIntLvl = 0;
						int newLevel = 0;
						// There are no universal Gems
						ItemMeta gemMeta = (SelectedSocket.get(player)).getItemMeta();
						List<String> gemLore = (gemMeta != null && gemMeta.hasLore()) ? gemMeta.getLore()
								: new ArrayList<String>();
						for (String s : gemLore) {
							if (s.startsWith(Settings.Cfg.LORE_GEM_OF.getString())) {
								gemEffect = s.substring(0, s.lastIndexOf(" ")).replaceAll(Settings.Cfg.LORE_GEM_OF.getString(), "");
								gemStrLvl = s.substring(s.lastIndexOf(" ") + 1);
								gemIntLvl = Plugin.romanToInt(gemStrLvl);
								gemType = EffectAPI.getEffectTypeFromName(gemEffect);
							} else if (s.endsWith("Type")) {
								gemType = s;
							}
						}

						// List<String> newLore = new ArrayList<String>();
						// Type Checking
						// Item Type Hierarchy= Bow<-Weapon<-Tool // Armor
						boolean typeFailure = false;
						switch (Items.GemLookupTable.get(item.getType())) {
						case EffectAPI.TYPE_ARMOR:
							if (!gemType.equals(EffectAPI.TYPE_ARMOR))
								typeFailure = true;
							else
								typeFailure = false;
							break;
						case EffectAPI.TYPE_BOW: // Case Fallthrough is
													// deliberate
							if (gemType.equals(EffectAPI.TYPE_BOW)) {
								typeFailure = false;
								break;
							}
						case EffectAPI.TYPE_WEAPON:
							if (gemType.equals(EffectAPI.TYPE_WEAPON)
									|| gemType.equals(EffectAPI.TYPE_WEAPON_PASSIVE)) {
								typeFailure = false;
								break;
							}
						case EffectAPI.TYPE_TOOL:
							if (gemType.equals(EffectAPI.TYPE_TOOL)) {
								typeFailure = false;
								break;
							}
						default:
							typeFailure = true;
							break;
						}
						if (typeFailure) {
							// Type Failure -- Break Event
							player.sendMessage(Settings.Cfg.APOCRPG_ERROR_SOCKET.getString());
							event.setCancelled(true);
							return;
						}
						for (int i = 0; i < itemLore.size() && !socketFound; i++) {
							String s = itemLore.get(i);
							// check to see if the item already has the same gem
							// socketed
							if (s.startsWith(Settings.Cfg.LORE_GEM_OF.getString() + gemEffect)) {
								itemStrLvl = s.substring(s.lastIndexOf(" ") + 1);
								itemIntLvl = Plugin.romanToInt(itemStrLvl);

								if (gemIntLvl > 0 && itemIntLvl > 0) {
									newLevel = gemIntLvl + itemIntLvl;
								}
								// if there is a match, make sure the gem
								// wouldn't push the item over the max level
								if (newLevel > 0 && newLevel <= 10) {
									itemLore.remove(i);
									itemLore.add(i, Settings.Cfg.LORE_GEM_OF.getString() + gemEffect + " " + Plugin.intToRoman(newLevel));
									socketFound = true;
								} else {
									player.sendMessage(Settings.Cfg.APOCRPG_ERROR_SOCKET.getString());
									event.setCancelled(true);
									player.updateInventory();
									return;
								}
							} else if (s.equals(Settings.Cfg.LORE_ITEM_SOCKET.getString())) {
								itemLore.remove(i);
								itemLore.add(i, Settings.Cfg.LORE_GEM_OF.getString() + gemEffect + " " + gemStrLvl);
								socketFound = true;
							}
						}
						if (socketFound) {
							itemMeta.setLore(itemLore);
							item.setItemMeta(itemMeta);
							if (gem.getAmount() > 1) {
								gem.setAmount(gem.getAmount() - 1);
								inv.setItem(SelectedSlot.get(player), gem);
							} else {
								inv.setItem(SelectedSlot.get(player), null);
							}
							// TODO: Handle Gem Effects Here --
							/**
							 * All Other Effects - Event Handlers must address
							 * Warding- Direct modification Ravaging- Direct
							 * modification
							 * 
							 */
							if (gemEffect.equals("Ravaging"))
								// Add some damage
								System.out.println();
							else if (gemEffect.equals("Warding"))
								// Add some defense
								System.out.println();
						} else {
							player.sendMessage(Settings.Cfg.APOCRPG_ERROR_SOCKET.getString());
							event.setCancelled(true);
						}
						SelectedSocket.remove(player);
						SelectedSlot.remove(player);
						player.updateInventory();

						/*
						 * ItemStack coal = new ItemStack(Material.COAL);
						 * ItemMeta coalMeta = coal.getItemMeta(); List<String>
						 * coalLore = new ArrayList<String>(); coalLore.add(
						 * "Used socket"); coalMeta.setLore(coalLore);
						 * coal.setItemMeta(coalMeta);
						 * player.getInventory().setItem(SelectedSlot.get(player
						 * ), coal);
						 */
					} else {
						player.sendMessage(Settings.Cfg.APOCRPG_ERROR.getString() + "No socket selected!");
					}
					SelectedSocket.remove(player);
					SelectedSlot.remove(player);
				} else if (gem != null) {
					player.sendMessage(Settings.Cfg.APOCRPG_ERROR.getString() + "Selected Item has no sockets available!");
					SelectedSocket.remove(player);
					SelectedSlot.remove(player);
				}
			}
		} else {
			SelectedSocket.remove(player);
			SelectedSlot.remove(player);
		}
	}

}
