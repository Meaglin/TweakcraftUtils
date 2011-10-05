package com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotNull;

@Entity
@Table(name="tcutils_punishlog")
public class PunishEntry {

	@Id
	private int id;
	
	@NotNull
	private String type;
	@NotNull
	private String punnish_sender;
	@NotNull
	private String punnish_receiver;

	private long time;
	
	private long duration;
	
	@NotNull
	private String reason;
	
	public PunishEntry() {}
	
	public void set(String type, String sender, String receiver, long duration, String reason){
		this.type = type;
		punnish_sender = sender;
		punnish_receiver = receiver;
		this.duration = duration;
		setTime(System.currentTimeMillis());
		this.reason = reason;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the punnish_sender
	 */
	public String getPunnish_sender() {
		return punnish_sender;
	}
	/**
	 * @param punnish_sender the punnish_sender to set
	 */
	public void setPunnish_sender(String punnish_sender) {
		this.punnish_sender = punnish_sender;
	}
	/**
	 * @return the punnish_receiver
	 */
	public String getPunnish_receiver() {
		return punnish_receiver;
	}
	/**
	 * @param punnish_receiver the punnish_receiver to set
	 */
	public void setPunnish_receiver(String punnish_receiver) {
		this.punnish_receiver = punnish_receiver;
	}
	/**
	 * @param duration the duration to set
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}
	/**
	 * @return the duration
	 */
	public long getDuration() {
		return duration;
	}
	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
}
