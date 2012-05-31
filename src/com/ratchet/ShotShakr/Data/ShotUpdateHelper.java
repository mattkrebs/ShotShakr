package com.ratchet.ShotShakr.Data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

public class ShotUpdateHelper {

	private static final String GETSHOTS_SOAP_ACTION = "http://tempuri.org/GetPremiumShots";
	private static final String GETCOUNT_SOAP_ACTION = "http://tempuri.org/PremiumShotCount";
	private static final String GETSHOTS_METHOD_NAME = "GetPremiumShots";
	private static final String GETCOUNT_METHOD_NAME = "PremiumShotCount";
	private static final String NAMESPACE = "http://tempuri.org";
	private static final String URL = "http://www.shotshakr.com/Service/ShotsService.asmx";
	
	private int _updateStatus;
	
	TextView tv;
	JSONArray json;
	private int dbShotItems;
	ShotRepository repo;
	
	public ShotUpdateHelper(Context context){
		this.repo = new ShotRepository(context);
		this.dbShotItems = this.repo.getAllShots().size();
	}
	
	public void setUpdateStatus(int value){
		_updateStatus = value;
	}
	public int getUpdateStatus(){
		return _updateStatus;
	}
	
	
	
	public Boolean hasNoShots(){
		
		if (dbShotItems == 0){
			return true;
			}
		return false;
	}
	
	public Boolean hasUpdate(){
		
			
			int myCount = GetShotCount();
			Log.w("hasUpdate", "DBItems = " + dbShotItems + "myCount = " + myCount);
			if (dbShotItems == 0){
				return true;
			}
			if ((myCount > 0) && (myCount > dbShotItems)){
				Log.w("hasUpdate", "DBItems = " + dbShotItems + "myCount = " + myCount);	
				return true;
			}
			Log.w("hasUpdate", "returning false");
		
		return false; 
			
	}
	public int GetShotCount() {
		int items = 0;
		
		SoapObject Request = new SoapObject(NAMESPACE, GETCOUNT_METHOD_NAME);
		Log.w("Service Request", Request.toString());
		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		soapEnvelope.dotNet = true;
		soapEnvelope.setOutputSoapObject(Request);

		AndroidHttpTransport aht = new AndroidHttpTransport(URL);

		SoapPrimitive resultString;
		try {
			aht.call(GETCOUNT_SOAP_ACTION, soapEnvelope);
			resultString = (SoapPrimitive) soapEnvelope.getResponse();
			Log.w("GetShotCount","Number of shots in the Service = " +  resultString.toString());
			items = Integer.parseInt(resultString.toString());
			

			
				
			
		

		} catch (Exception e) {
			Log.w("ServiceCall", "Error getting shot count" + e.getMessage());
		}
		
		return items;
	}
	

	

}
