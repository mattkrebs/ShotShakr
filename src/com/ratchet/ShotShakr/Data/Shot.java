package com.ratchet.ShotShakr.Data;

public class Shot {
	   private int _shot_id;
	   private String _shotName;
	   private String _shotIngredients;
	   private String _shotInstructions;
	   private String _shotAmount;
	   public String getShotName() {
		   
			return _shotName.replace("&reg;", "®");
		}

	   public int getShotId(){
		   return _shot_id;
	   }
		public String getIngredients() {
			return _shotIngredients.replace("&reg;", "®");
		}
		public String getInstructions() {
			return _shotInstructions.replace("&reg;", "®");
		}
		public String getAmount() {
			return _shotAmount.replace("&reg;", "®");
		}
		
		public void setShotId(int shotId){
			_shot_id = shotId;
		}
		public void setShotName(String value) {
			_shotName = value;
		}

		public void setIngredients(String value) {
			_shotIngredients = value;
		}
		public void setInstructions(String value){
			_shotInstructions = value;
		}
		public void setAmount(String value){
			_shotAmount = value;
		}
		
		public Shot(int shotid, String name, String i, String instruct, String amount) {
			setShotId(shotid);
			setShotName(name);
			setIngredients(i);
			setInstructions(instruct);
			setAmount(amount);
		}
		


}