package com.ratchet.ShotShakr.Data;

import com.google.gson.annotations.SerializedName;

public class Shot {
	 
	   
	   ////_shotInstructions.replace("&reg;", "®");
	   
	   
	   
	   @SerializedName("shot_name")
	   public String shotName;
	   @SerializedName("shot_id")
	   public int shotId;
	   @SerializedName("ingredients")
	   public String ingredients;
	   @SerializedName("instructions")
	   public String instructions;
	   @SerializedName("amount")
	   public String amount;
		
		
		
	   public Shot(int shotid, String name, String i, String instruct, String amount) {
			this.shotId = shotid;
			this.shotName = name.replace("&reg;", "®");;
			this.ingredients = i.replace("&reg;", "®");;
			this.instructions = instruct.replace("&reg;", "®");;
			this.amount = amount.replace("&reg;", "®");;
	   }
		


}