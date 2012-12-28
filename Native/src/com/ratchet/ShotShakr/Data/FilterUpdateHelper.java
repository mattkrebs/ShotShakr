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

public class FilterUpdateHelper {

	private static final String GETFILTER_SOAP_ACTION = "http://tempuri.org/GetAllFilters";
	
	private static final String GETFILTER_METHOD_NAME = "GetAllFilters";
	private static final String NAMESPACE = "http://tempuri.org";
	private static final String URL = "http://www.shotshakr.com/Service/ShotsService.asmx";
	
	private int _updateStatus;
	
	TextView tv;
	JSONArray json;

	ShotRepository repo;
	
	
	public void setUpdateStatus(int value){
		_updateStatus = value;
	}
	public int getUpdateStatus(){
		return _updateStatus;
	}
	
	public void GetFilterUpdate(Context context) {
		Log.w("Get Filter Helper", "Updating Filter from Service");
		SoapObject Request = new SoapObject(NAMESPACE, GETFILTER_METHOD_NAME);

		SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		soapEnvelope.dotNet = true;
		soapEnvelope.setOutputSoapObject(Request);

		AndroidHttpTransport aht = new AndroidHttpTransport(URL);

		SoapPrimitive resultString;
		try {
			aht.call(GETFILTER_SOAP_ACTION, soapEnvelope);
			resultString = (SoapPrimitive) soapEnvelope.getResponse();

			json = new JSONArray(resultString.toString());

			this.repo = new ShotRepository(context); 
			this.repo.deleteAllFilters();
			
			//perform insert
			for (int i = 0; i < json.length(); i++) {				 
				JSONObject filter = new JSONObject();
				filter = json.getJSONObject(i);
				Log.w("Update Filter","Updating Filter.... " + filter.getString("filter_type"));
				this.repo.filterInsert(filter.getInt("filter_id"), filter.getInt("shot_id"), filter.getString("filter_type"));				
				setUpdateStatus(i + 1);
			}
			
			

		} catch (Exception e) {
			Log.w("ServiceCall", e.getMessage());
		}
		this.repo.close();
	}
	
	
	

	

}
