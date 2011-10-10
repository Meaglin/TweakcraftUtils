package com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

@Entity
@Table( name = "mail")
public class Mail {
	
	@Id
	private int id;
	
	private long sentdate;
	private long readdate;

	@NotNull
	private int senderid;
	@NotNull
	@NotEmpty
	private String sendername;
	
	@NotNull
	private int receiverid;
	@NotNull
	@NotEmpty
	private String receivername;
	
	@NotNull
	@NotEmpty
	@Column(columnDefinition= "TEXT")
	private String subject;

	@NotNull
	@NotEmpty
	@Column(columnDefinition= "TEXT")
	private String message;
	
	public Mail() {
	}
	
	public Mail(PlayerData sender, PlayerData receiver, String subject, String message) {
		receiverid = receiver.getId();
		receivername = receiver.getName();
		senderid = sender.getId();
		sendername = sender.getName();
		this.subject = subject;
		this.message = message;
		this.sentdate = System.currentTimeMillis();
		this.readdate = 0;
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
	 * @return the sentdate
	 */
	public long getSentdate() {
		return sentdate;
	}

	/**
	 * @param sentdate the sentdate to set
	 */
	public void setSentdate(long sentdate) {
		this.sentdate = sentdate;
	}

	/**
	 * @return the readdate
	 */
	public long getReaddate() {
		return readdate;
	}

	/**
	 * @param readdate the readdate to set
	 */
	public void setReaddate(long readdate) {
		this.readdate = readdate;
	}

	/**
	 * @return the senderid
	 */
	public int getSenderid() {
		return senderid;
	}

	/**
	 * @param senderid the senderid to set
	 */
	public void setSenderid(int senderid) {
		this.senderid = senderid;
	}

	/**
	 * @return the sendername
	 */
	public String getSendername() {
		return sendername;
	}

	/**
	 * @param sendername the sendername to set
	 */
	public void setSendername(String sendername) {
		this.sendername = sendername;
	}

	/**
	 * @return the receiverid
	 */
	public int getReceiverid() {
		return receiverid;
	}

	/**
	 * @param receiverid the receiverid to set
	 */
	public void setReceiverid(int receiverid) {
		this.receiverid = receiverid;
	}

	/**
	 * @return the receivername
	 */
	public String getReceivername() {
		return receivername;
	}

	/**
	 * @param receivername the receivername to set
	 */
	public void setReceivername(String receivername) {
		this.receivername = receivername;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
