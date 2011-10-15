package com.guntherdw.bukkit.tweakcraft.DataSources.PersistenceClass;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;

@Entity
@Table( name = "vouches" )
public class Vouch {
	@Id
	private int id;
	
	@NotNull
	private long vouchdate;
	
	@NotNull
	private int voucherid;
	@NotNull
	@NotEmpty
	private String vouchername;
	
	@NotNull
	private int vouchreceiverid;
	@NotNull
	@NotEmpty
	private String vouchreceivername;
	
	public Vouch() {
	}
	
	public Vouch(PlayerData voucher, PlayerData vouchreceiver) {
		vouchdate = System.currentTimeMillis();
		
		voucherid = voucher.getId();
		vouchername = voucher.getName();
		
		vouchreceiverid = vouchreceiver.getId();
		vouchreceivername = vouchreceiver.getName();
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
	 * @return the vouchdate
	 */
	public long getVouchdate() {
		return vouchdate;
	}

	/**
	 * @param vouchdate the vouchdate to set
	 */
	public void setVouchdate(long vouchdate) {
		this.vouchdate = vouchdate;
	}

	/**
	 * @return the voucherid
	 */
	public int getVoucherid() {
		return voucherid;
	}

	/**
	 * @param voucherid the voucherid to set
	 */
	public void setVoucherid(int voucherid) {
		this.voucherid = voucherid;
	}

	/**
	 * @return the vouchername
	 */
	public String getVouchername() {
		return vouchername;
	}

	/**
	 * @param vouchername the vouchername to set
	 */
	public void setVouchername(String vouchername) {
		this.vouchername = vouchername;
	}

	/**
	 * @return the vouchreceiverid
	 */
	public int getVouchreceiverid() {
		return vouchreceiverid;
	}

	/**
	 * @param vouchreceiverid the vouchreceiverid to set
	 */
	public void setVouchreceiverid(int vouchreceiverid) {
		this.vouchreceiverid = vouchreceiverid;
	}

	/**
	 * @return the vouchreceivername
	 */
	public String getVouchreceivername() {
		return vouchreceivername;
	}

	/**
	 * @param vouchreceivername the vouchreceivername to set
	 */
	public void setVouchreceivername(String vouchreceivername) {
		this.vouchreceivername = vouchreceivername;
	}
}
