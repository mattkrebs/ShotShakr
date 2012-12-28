package com.ratchet.ShotShakr.Data;

public class Filter {

	private int _shot_id;
	private int _filter_id;
	private String _filter_type;
	
	
	public Filter(int filter_id, int shot_id, String filter_type)
	{
		setFilterId(filter_id);
		setShotId(shot_id);
		setFilterType(filter_type);
	}
	
	
	public String getFilterType() {
		return _filter_type;
	}
	public int getFilterId(){
		return _filter_id;
	}
	public int getShotId(){
		return _shot_id;
	}
	
	
	public void setShotId(int shotId){
		_shot_id = shotId;
	}
	public void setFilterId(int filterId){
		_filter_id = filterId;
	}
	public void setFilterType(String filterType){
		_filter_type = filterType;
	}
}
