package com.Plugin.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.Plugin.Items.ItemAPI;
import com.Plugin.Main.Economy;
import com.Plugin.Main.Plugin;

public class ApocRPGCommand implements CommandExecutor {

	int gearCost = Plugin.Settings.getInt("Command-Settings.cost-for-gear");
	int gemCost = Plugin.Settings.getInt("Command-Settings.cost-for-gem");
	
	@Override
	public boolean onCommand(CommandSender Sender, Command Command, String label, String[] args) {
		Player Player = (Player) Sender;
		if (Command.getLabel().equals("generateitem")) {
			ItemStack item = ItemAPI.createItem();
			if (args.length==0) {
				Player.getInventory().addItem(item);
			} else {
				Player TargetPlayer = Plugin.Plugin.getServer().getPlayer(args[0]);
				TargetPlayer.getInventory().addItem(item);
			}
			return true;
		} else if (Command.getLabel().equals("generateuseful")) {
			ItemStack item;
			if(args.length==0) {
				item = ItemAPI.generateUsefulItem(0);
			} else {
				item = ItemAPI.generateUsefulItem(Integer.parseInt(args[0]));
			}
			Player.getInventory().addItem(item);
			return true;
		} else if (Command.getLabel().equals("generatesocket")) {
			Player.getInventory().addItem(ItemAPI.createSocket());
			return true;
		} else if (Command.getLabel().equals("apocrpg")) {
			if (args.length == 0) {
				ChatColor CommandColor = ChatColor.GOLD;
				ChatColor DescColor = ChatColor.BLUE;
				Player.sendMessage(CommandColor + "/apocrpg buy " + DescColor + "- Buy an enchanted item");
				Player.sendMessage(CommandColor + "/apocrpg buy enchant " + DescColor + "- Buy an enchanted item with specific enchant");
				Player.sendMessage(CommandColor + "/apocrpg buy item " + DescColor + "- Buy a specific enchanted item");
				Player.sendMessage(CommandColor + "/apocrpg buy name " + DescColor + "- Buy an enchanted item with specific name");
				Player.sendMessage(CommandColor + "/apocrpg buy book " + DescColor + "- Buy an identity tome");
				Player.sendMessage(CommandColor + "/apocrpg buy gem" + DescColor + "- Buy a socket gem");
				Player.sendMessage(CommandColor + "/apocrpg sell" + DescColor + "- sell an APOC-RPG Item");
				Player.sendMessage(CommandColor + "/apocrpg sell all" + DescColor + "- sell all APOC-RPG Items in inventory");
				Player.sendMessage(CommandColor + "/apocrpg repair" + DescColor + "- repair an APOC-RPG Item");
				Player.sendMessage(CommandColor + "/apocrpg repair" + DescColor + "- repair all APOC-RPG Items in inventory");
			} else {
				String arg1 = args[0];
				if (arg1.equals("buy")) {
					Inventory Inventory = Player.getInventory();
					if (args.length > 1) {
						String arg2 = args[1];
						
						if (arg2.equals("enchant")) {
							
						} else if (arg2.equals("item")) {
							
						} else if (arg2.equals("name")) {
							if (Economy.hasMoney(Player, gearCost)) {
								Economy.removeMoney(Player, gearCost);
								ItemStack Item = ItemAPI.createItem();
								ItemMeta meta = Item.getItemMeta();
								meta.setDisplayName(args[2]);
								Item.setItemMeta(meta);
								Inventory.addItem(Item);
							} else {
								Player.sendMessage(ChatColor.RED + "Not enough money!");
							}
						} else if (arg2.equals("book")) {
							
						} else if (arg2.equals("gem")) {
							if (Economy.hasMoney(Player, gemCost)) {
								Economy.removeMoney(Player, gemCost);
								Inventory.addItem(ItemAPI.createSocket());
							} else {
								Player.sendMessage(ChatColor.RED + "Not enough money!");
							}
						}
					} else if (Economy.hasMoney(Player, gearCost)) {
						Economy.removeMoney(Player, gearCost);
						Inventory.addItem(ItemAPI.createItem());
					} else {
						Player.sendMessage(ChatColor.RED + "Not enough money!");
					}
				} else if (arg1.equals("repair")) {
					
				}
			}
			return true;
		} else {
			System.out.println("Unkown command sent.");
			return false;
		}
	}
	
}
