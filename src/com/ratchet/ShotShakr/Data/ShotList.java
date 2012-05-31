package com.ratchet.ShotShakr.Data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ShotList {

	@SerializedName("Shots")
	public List<Shot> shotList;
	
	
	public void setShotList(List <Shot> shotList) {
	    this.shotList = shotList;
	}

	public List<Shot> getShotList() {
	    return shotList;
	}

}
