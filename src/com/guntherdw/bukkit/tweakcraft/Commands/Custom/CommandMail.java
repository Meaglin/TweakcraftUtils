package com.guntherdw.bukkit.tweakcraft.Commands.Custom;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avaje.ebean.ExpressionList;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;
import com.guntherdw.bukkit.tweakcraft.Commands.iCommand;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.Mail;
import com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass.PlayerData;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandSenderException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.CommandUsageException;
import com.guntherdw.bukkit.tweakcraft.Exceptions.PermissionsException;

public class CommandMail implements iCommand {

	private Map<Integer, Mail> buffer = new HashMap<Integer, Mail>();
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean executeCommand(CommandSender sender, String command,
			String[] args, TweakcraftUtils plugin) throws PermissionsException,
			CommandSenderException, CommandUsageException, CommandException {
		
		if(!(sender instanceof Player))
			throw new CommandSenderException("Sod off!");
		
		if(args.length == 0) 
			throw new CommandUsageException("Missing command action, /mail [inbox|read|append|remove|send|outbox] <args>");
		
		PlayerData data = plugin.getPlayerData((Player)sender);
		
		String action = args[0].toLowerCase();
		if(action.equals("inbox")) {
			int count = 5;
			boolean unreadonly = false;
			if(args.length > 0) {
				for(int i = 1; i < args.length;i++) {
					try {
						count = Integer.parseInt(args[i]);
					} catch(NumberFormatException e) {
						if(args[i].toLowerCase().startsWith("unread"))
							unreadonly = true;
					}
				}
			}
			ExpressionList<Mail> ex = plugin.getDatabase().find(Mail.class).where();
			ex.setMaxRows(count);
			ex.eq("receiverid",data.getId());
			ex.orderBy().desc("sentdate");
			if(unreadonly) ex.gt("readdate", 0);
			List<Mail> mail = ex.findList();
			if(mail.size() == 0){
				sender.sendMessage(ChatColor.GOLD + "No mail found.");
				return true;
			}
			sender.sendMessage(ChatColor.GOLD + "Your inbox:");
			for(Mail m : mail) {
				Date date = new Date(m.getSentdate());
				sender.sendMessage(ChatColor.BLUE + "[" + (m.getReaddate() != 0 ? ChatColor.GREEN + "R" : ChatColor.RED + "UR") +
						ChatColor.BLUE + "]" + "(" + m.getId() + ")" + 
						ChatColor.YELLOW + date.getDate() + "-" + date.getMonth() + "-'" + (date.getYear()-100) + "@" + date.getHours() + ":" + date.getMinutes() +
						"[" + ChatColor.WHITE + m.getSendername()  + ChatColor.YELLOW + "->" +  ChatColor.WHITE + m.getReceivername() +  ChatColor.YELLOW + "]"+
						ChatColor.WHITE + ":" + m.getSubject()
						);
			}
			
		} else if(action.equals("outbox")) {
			int count = 5;
			boolean unreadonly = false;
			if(args.length > 0) {
				for(int i = 1; i < args.length;i++) {
					try {
						count = Integer.parseInt(args[i]);
					} catch(NumberFormatException e) {
						if(args[i].toLowerCase().startsWith("unread"))
							unreadonly = true;
					}
				}
			}
			ExpressionList<Mail> ex = plugin.getDatabase().find(Mail.class).where();
			ex.setMaxRows(count);
			ex.eq("senderid",data.getId());
			ex.orderBy().desc("sentdate");
			if(unreadonly) ex.gt("readdate", 0);
			List<Mail> mail = ex.findList();
			if(mail.size() == 0){
				sender.sendMessage(ChatColor.GOLD + "No mail found.");
				return true;
			}
			sender.sendMessage(ChatColor.GOLD + "Your outbox:");
			for(Mail m : mail) {
				Date date = new Date(m.getSentdate());
				sender.sendMessage(ChatColor.BLUE + "[" + (m.getReaddate() != 0 ? ChatColor.GREEN + "R" : ChatColor.RED + "UR") +
						ChatColor.BLUE + "]" + "(" + m.getId() + ")" + 
						ChatColor.YELLOW + date.getDate() + "-" + date.getMonth() + "-'" + (date.getYear()-100) + "@" + date.getHours() + ":" + date.getMinutes() +
						"[" + ChatColor.WHITE + m.getSendername()  + ChatColor.YELLOW + "->" +  ChatColor.WHITE + m.getReceivername() +  ChatColor.YELLOW + "]"+
						ChatColor.WHITE + ":" + m.getSubject()
						);
			}
		} else if(action.equals("read")) {
			if(args.length < 2)
				throw new CommandUsageException("Missing mailid, /mail read [id]");
				
			int mailid = -1;
			try {
				mailid = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) { mailid = -1; }
			if(mailid == -1) 
				throw new CommandUsageException("Invalid mailid, /mail read [id]");
			
			Mail mail = plugin.getDatabase().find(Mail.class).where().eq("id", mailid).findUnique();
			if(mail == null) 
				throw new CommandException("No mail found with id " + mailid + "!");
			
			if(mail.getReceiverid() != data.getId() && mail.getSenderid() != data.getId())
				throw new CommandException("You're not allowed to read this mail.");
			
			if(mail.getReaddate() == 0 && mail.getReceiverid() == data.getId()) {
				mail.setReaddate(System.currentTimeMillis());
				plugin.getDatabase().update(mail);
			}
			Date date = new Date(mail.getSentdate());
			Date receive = new Date(mail.getReaddate());
			
			sender.sendMessage(ChatColor.GOLD + "-------------------------");
			sender.sendMessage(ChatColor.GOLD + "Mail:");
			sender.sendMessage(ChatColor.GOLD + "-------------------------");
			sender.sendMessage("From " + ChatColor.WHITE + mail.getSendername() + ChatColor.GOLD + " To " + ChatColor.WHITE + mail.getReceivername());
			sender.sendMessage(ChatColor.GOLD + "Sent: " + 
					ChatColor.YELLOW + date.getDate() + "-" + date.getMonth() + "-'" + (date.getYear()-100) + "@" + date.getHours() + ":" + date.getMinutes() +
					(mail.getReaddate() != 0 ? ChatColor.GOLD + "Read: " + 
					ChatColor.YELLOW + receive.getDate() + "-" + receive.getMonth() + "-'" + (receive.getYear()-100) + "@" + receive.getHours() + ":" + receive.getMinutes() : "")
			);
			sender.sendMessage(ChatColor.GOLD + "Subject: " + ChatColor.WHITE + mail.getSubject());
			for(String p : mail.getMessage().split("\n"))
				sender.sendMessage(p);
			
		} else if(action.equals("make")) {
			if(args.length < 3)
				throw new CommandUsageException("Missing arguments, /mail make [playername] [subject]");
			
			String name = args[1];
			String subject = "";
			for(int i = 2; i < args.length; i++)
				subject += " " + args[i];
			if(subject.length() > 1) subject = subject.substring(1);
			if(subject.trim().equals(""))
				throw new CommandUsageException("Too short subject, /mail make [playername] [subject]");
			
			PlayerData to = plugin.getPlayerData(name);
			if(to == null)
				throw new CommandUsageException("No player found with name " + name + ", /mail make [playername] [subject]");
			
			buffer.put(((Player)sender).getEntityId(), new Mail(data, to, subject, ""));
			sender.sendMessage(ChatColor.GOLD + "New mail, To:" + ChatColor.WHITE + to.getName() + ChatColor.GOLD + " Subject:" + ChatColor.WHITE + subject);
			
		} else if(action.equals("send")) {
			Mail mail = buffer.get(((Player)sender).getEntityId());
			if(mail == null)
				throw new CommandUsageException("Please setup a mail first, /mail make [playername] [subject]");
			
			if(mail.getMessage().trim().equals(""))
				throw new CommandUsageException("You cannot send an empty message, /mail append [text]");
			
			mail.setSentdate(System.currentTimeMillis());
			plugin.getDatabase().save(mail);
			buffer.remove(((Player)sender).getEntityId());
		} else if(action.equals("append")) {
			Mail mail = buffer.get(((Player)sender).getEntityId());
			if(mail == null)
				throw new CommandUsageException("Please setup a mail first, /mail make [playername] [subject]");
			
			if(args.length < 2)
				throw new CommandUsageException("Missing text, /mail append [text]");
			
			String text = "";
			for(int i = 1; i < args.length; i++)
				text += " " + args[i];
			if(text.length() > 1) text = text.substring(1);
			
			mail.setMessage(mail.getMessage() + text + "\n");
			sender.sendMessage(ChatColor.GOLD + "Added line:" + ChatColor.WHITE + text);
		} else if(action.equals("remove")) {
			Mail mail = buffer.get(((Player)sender).getEntityId());
			if(mail == null)
				throw new CommandUsageException("Please setup a mail first, /mail make [playername] [subject]");
			
			if(args.length < 2)
				throw new CommandUsageException("Missing argument, /mail remove [last|linenumber]");
			
			if(mail.getMessage().trim().equals(""))
				throw new CommandUsageException("Nothing to remove.");
			
			String[] lines = mail.getMessage().split("\n");
			int line = lines.length-1;
			try {
				line = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				if(!args[1].equalsIgnoreCase("last")) 
					throw new CommandUsageException("Invalid argument, /mail remove [last|linenumber]"); 
			}
			if(line < 1 || line > (lines.length - 1))
				throw new CommandUsageException("Invalid argument, /mail remove [last|linenumber]"); 
			String newmessage = "";
			String removed = "";
			for(int i = 0; i < (lines.length-1); i++) {
				if(i == (line - 1))  
					removed = lines[i];
				else
					newmessage += lines[i] + "\n";
					
			}
			mail.setMessage(newmessage);
			sender.sendMessage(ChatColor.GOLD + "Line removed:" + ChatColor.WHITE + removed);
		}
		return true;
	}

	@Override
	public String getPermissionSuffix() {
		return "mail";
	}

}
