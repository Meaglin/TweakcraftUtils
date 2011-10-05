package com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotNull;
import com.guntherdw.bukkit.tweakcraft.TweakcraftUtils;

/**
 * @author Meaglin
 */
@Entity()
@Table(name="tcutils_playerdata")
public class PlayerData {

	@Id
	private int id;
	
	private int forumid;
	
	@NotNull
	@Length(max=100)
	private String name;
	@NotNull
	@Length(max=100)
	private String displayname;
	@NotNull
	@Length(max=100)
	private String lastrank;
	
	private long bantime;
	private long mutetime;
	
	private long demotetime;
	@Length(max=100)
	private String oldrank;
	
	private long firstlogin;
	private long lastlogin;
	private boolean online;
	
	private long onlinetime;
	private int blockplaced;
	private int blockdestroyed;
	
	
	public void init(String name, String displayname, String rank) {
		this.name = name;
		this.displayname = displayname;
		this.lastrank = rank;
		this.firstlogin = System.currentTimeMillis();
		this.onlinetime = 0;
		this.blockplaced = 0;
		this.blockdestroyed = 0;
	}
	
	public boolean isBanned() {
		if(System.currentTimeMillis() < getBantime())
            return true;
        
        return (getBantime() == -1);
	}
	
	public String banToString() {
		int time = (int) Math.floor((getBantime() - System.currentTimeMillis())/1000);
		return "You are banned from this server" + (time > 0 ? " for another " + formatRemaining(time) : "") + "!";
	}
	
	public boolean isMuted() {
		if(System.currentTimeMillis() < getMutetime())
            return true;
        
        return getMutetime() == -1;
	}
	
	public String muteToString() {
		int time = (int) Math.floor((getMutetime() - System.currentTimeMillis())/1000);
		return "You are muted" + (time > 0 ? " for another " + formatRemaining(time) : "") + "!";
	}
	


	public boolean isDemoted() {
		if(System.currentTimeMillis() < getDemotetime())
            return true;
        
        return getDemotetime() == -1;
	}
	
	public static void onLogin(TweakcraftUtils plugin, Player player, PlayerData data) {
		if (data.getDemotetime() != 0 && data.getDemotetime() > 0 && data.getDemotetime() <= System.currentTimeMillis()) {
			plugin.getPermissionHandler().getUserObject("world", player.getName()).removeParent(plugin.getPermissionHandler().getGroupObject("world", "outcast"));
			plugin.getPermissionHandler().getUserObject("world", player.getName()).addParent(plugin.getPermissionHandler().getGroupObject("world", data.getOldrank()));
			data.setDemotetime(0);
			data.setLastrank(data.getOldrank());
			data.setOldrank("");
			player.sendMessage(ChatColor.GREEN + "You are not outcast more!");
		}
		
		if(data.getDemotetime() > System.currentTimeMillis() || data.getDemotetime() == -1) {
			int time = (int) Math.floor((data.getDemotetime() - System.currentTimeMillis())/1000);
			player.sendMessage(ChatColor.GOLD + "You are outcast from this server" + (time > 0 ? " for another " + formatRemaining(time) : "") + "!");
		}
		player.setDisplayName(plugin.getNickWithColors("world", player.getName()));
		data.setOnline(true);
		data.setLastlogin(System.currentTimeMillis());
		data.setDisplayname(player.getDisplayName());
		data.setLastrank(plugin.getPermissionHandler().getPrimaryGroup("world", data.getName()));
		if(data.getMutetime() != -1 && data.getMutetime() < System.currentTimeMillis()) data.setMutetime(0);
		if(data.getBantime() != -1 && data.getBantime() < System.currentTimeMillis()) data.setBantime(0);
		plugin.getDatabase().update(data); 
	}
	
	public static void onLogout(TweakcraftUtils plugin, Player player, PlayerData data) {
		data.setOnlinetime(data.getOnlinetime() + (System.currentTimeMillis() - data.getLastlogin()));
        data.setOnline(false);
        data.setLastlogin(System.currentTimeMillis());
        plugin.getDatabase().update(data);
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the forumid
	 */
	public int getForumid() {
		return forumid;
	}
	/**
	 * @param forumid the forumid to set
	 */
	public void setForumid(int forumid) {
		this.forumid = forumid;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the displayname
	 */
	public String getDisplayname() {
		return displayname;
	}
	/**
	 * @param displayname the displayname to set
	 */
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	/**
	 * @return the lastrank
	 */
	public String getLastrank() {
		return lastrank;
	}
	/**
	 * @param lastrank the lastrank to set
	 */
	public void setLastrank(String lastrank) {
		this.lastrank = lastrank;
	}
	/**
	 * @return the bantime
	 */
	public long getBantime() {
		return bantime;
	}
	/**
	 * @param bantime the bantime to set
	 */
	public void setBantime(long bantime) {
		this.bantime = bantime;
	}
	/**
	 * @return the mutetime
	 */
	public long getMutetime() {
		return mutetime;
	}
	/**
	 * @param mutetime the mutetime to set
	 */
	public void setMutetime(long mutetime) {
		this.mutetime = mutetime;
	}
	/**
	 * @return the oldrank
	 */
	public String getOldrank() {
		return oldrank;
	}
	/**
	 * @param oldrank the oldrank to set
	 */
	public void setOldrank(String oldrank) {
		this.oldrank = oldrank;
	}
	/**
	 * @return the firstlogin
	 */
	public long getFirstlogin() {
		return firstlogin;
	}
	/**
	 * @param firstlogin the firstlogin to set
	 */
	public void setFirstlogin(long firstlogin) {
		this.firstlogin = firstlogin;
	}
	/**
	 * @return the lastlogin
	 */
	public long getLastlogin() {
		return lastlogin;
	}
	/**
	 * @param lastlogin the lastlogin to set
	 */
	public void setLastlogin(long lastlogin) {
		this.lastlogin = lastlogin;
	}
	/**
	 * @return the onlinetime
	 */
	public long getOnlinetime() {
		return onlinetime;
	}
	/**
	 * @param onlinetime the onlinetime to set
	 */
	public void setOnlinetime(long onlinetime) {
		this.onlinetime = onlinetime;
	}
	/**
	 * @return the blockplaced
	 */
	public int getBlockplaced() {
		return blockplaced;
	}
	/**
	 * @param blockplaced the blockplaced to set
	 */
	public void setBlockplaced(int blockplaced) {
		this.blockplaced = blockplaced;
	}
	/**
	 * @return the blockdestroyed
	 */
	public int getBlockdestroyed() {
		return blockdestroyed;
	}
	/**
	 * @param blockdestroyed the blockdestroyed to set
	 */
	public void setBlockdestroyed(int blockdestroyed) {
		this.blockdestroyed = blockdestroyed;
	}

	/**
	 * @param online the online to set
	 */
	public void setOnline(boolean online) {
		this.online = online;
	}

	/**
	 * @return the online
	 */
	public boolean isOnline() {
		return online;
	}
	
	/**
	 * @param demotetime the demotetime to set
	 */
	public void setDemotetime(long demotetime) {
		this.demotetime = demotetime;
	}

	/**
	 * @return the demotetime
	 */
	public long getDemotetime() {
		return demotetime;
	}

	public static final int WEEK = 60 * 60 * 24 * 7;
    public static final int DAY = 60 * 60 * 24;
    public static final int HOUR = 60 * 60;
    public static final int MINUTE = 60;
    
    public static String formatRemaining(int remainingtime) {
        int weeks = 0,days = 0,hours = 0,minutes = 0,seconds = 0;
        if(remainingtime >= WEEK){
            weeks = (int) Math.floor(remainingtime / WEEK);
            remainingtime -= weeks * WEEK;
        }
        if(remainingtime >= DAY){
            days = (int) Math.floor(remainingtime / DAY);
            remainingtime -= days * DAY;
        }
        if(remainingtime >= HOUR){
            hours = (int) Math.floor(remainingtime / HOUR);
            remainingtime -= hours * HOUR;
        }
        if(remainingtime >= MINUTE){
            minutes = (int) Math.floor(remainingtime / MINUTE);
            remainingtime -= minutes * MINUTE;
        }
        seconds = remainingtime;
        String rt = "";
        
        if(weeks != 0) rt += (weeks == 1 ? "1 day, " : weeks + " days, ");
        if(hours != 0) rt += (hours == 1 ? "1 hour, " : hours + " hours, ");
        if(minutes != 0) rt += (minutes == 1 ? "1 minute, " : minutes + " minutes, ");
        if(seconds != 0){
            if(rt.length() > 0){
                rt = rt.substring(0, rt.length() - 2 );
                rt += " and ";
            }
            rt += (seconds == 1 ? "1 second" : seconds + " seconds");
        } else if(rt.length() > 0){
            rt = rt.substring(0, rt.length() - 2);
        }
        
        return rt;
    }
}
