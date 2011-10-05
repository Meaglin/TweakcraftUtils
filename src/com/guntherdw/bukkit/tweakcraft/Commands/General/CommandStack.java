package com.guntherdw.bukkit.tweakcraft.Commands.General;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;

import com.sk89q.worldedit.blocks.ItemType;

/**
 * 
 * @author Sk89q
 *
 */
public class CommandStack implements iCommand {

	@Override
	public boolean executeCommand(CommandSender sender, String command,
			String[] args, TweakcraftUtils plugin) throws PermissionsException,
			CommandSenderException, CommandUsageException, CommandException {
		
		if(!(sender instanceof Player))
			 throw new CommandSenderException("You're the console, where do you think you're going?");
		
		Player player = (Player) sender;
		if (!plugin.check(player, "stack"))
			throw new PermissionsException(command);
		
        boolean ignoreMax = plugin.check(player, "stack.ignoremaxstack");
        
        ItemStack[] items = player.getInventory().getContents();
        int len = items.length;

        int affected = 0;
        
        for (int i = 0; i < len; i++) {
            ItemStack item = items[i];

            // Avoid infinite stacks and stacks with durability
            if (item == null || item.getAmount() <= 0
                    || (!ignoreMax && item.getMaxStackSize() == 1)) {
                continue;
            }

            int max = ignoreMax ? 64 : item.getMaxStackSize();

            if (item.getAmount() < max) {
                int needed = max - item.getAmount(); // Number of needed items until max

                // Find another stack of the same type
                for (int j = i + 1; j < len; j++) {
                    ItemStack item2 = items[j];

                    // Avoid infinite stacks and stacks with durability
                    if (item2 == null || item2.getAmount() <= 0
                            || (!ignoreMax && item.getMaxStackSize() == 1)) {
                        continue;
                    }

                    // Same type?
                    // Blocks store their color in the damage value
                    if (item2.getTypeId() == item.getTypeId() &&
                            (!ItemType.usesDamageValue(item.getTypeId())
                                    || item.getDurability() == item2.getDurability())) {
                        // This stack won't fit in the parent stack
                        if (item2.getAmount() > needed) {
                            item.setAmount(64);
                            item2.setAmount(item2.getAmount() - needed);
                            break;
                        // This stack will
                        } else {
                            items[j] = null;
                            item.setAmount(item.getAmount() + item2.getAmount());
                            needed = 64 - item.getAmount();
                        }

                        affected++;
                    }
                }
            }
        }

        if (affected > 0) {
            player.getInventory().setContents(items);
        }

        player.sendMessage(ChatColor.YELLOW + "Items compacted into stacks!");
	    return true;
	}

	@Override
	public String getPermissionSuffix() {
		return "stack";
	}

}
