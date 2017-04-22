package com.knowgate.google;

/**
 * © Copyright 2016 the original author.
 * This file is licensed under the Apache License version 2.0.
 * You may not use this file except in compliance with the license.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.knowgate.debug.DebugFile;
import com.knowgate.http.HttpRequest;
import com.knowgate.gis.AddressComponent;
import com.knowgate.gis.Geocoder;
import com.knowgate.gis.GeocodingException;
import com.knowgate.gis.LatLong;
import com.knowgate.gis.StreetAddress;

/**
 * @author Sergio Montoro Ten
 * @version 1.0
 */
public class GGeocoder implements Geocoder {

	public static final String BASE_URL = "http://maps.googleapis.com/maps/api/geocode/json?sensor=false";
	
	private String sClient;
	private String sSignature;

	public GGeocoder() {
		sClient = sSignature = null;
	}

	public GGeocoder(String sClientCode, String sSignatureCode) {
		sClient = sClientCode;
		sSignature = sSignatureCode;
	}

	private StreetAddress getStreetAddressFromUrl(String sUrl) throws GeocodingException { 
		if (DebugFile.trace) DebugFile.writeln(sUrl);
		StreetAddress sStAddr = null;
		HttpRequest oHttpReq = new HttpRequest(sUrl);
		try {
			String sJsonResponse = new String((byte[]) oHttpReq.get(), "UTF-8");
			if (DebugFile.trace) DebugFile.writeln(sJsonResponse);
			JSONObject oJson = new JSONObject(sJsonResponse);
			String sStatus = oJson.getString("status");
			System.out.println("status="+sStatus);
			if (sStatus.equals("OK")) {
				sStAddr = new StreetAddress();
				JSONArray oResults = oJson.getJSONArray("results");
				JSONObject oResult0 = oResults.getJSONObject(0);
				JSONArray oComponents = oResult0.getJSONArray("address_components");
				System.out.println(String.valueOf(oComponents.length())+" components found");
				for (int c=oComponents.length()-1; c>=0; c--) {
					JSONObject oComponent = oComponents.getJSONObject(c);
					AddressComponent oAddrc = new AddressComponent();
					oAddrc.setLongName(oComponent.getString("long_name"));
					oAddrc.setShortName(oComponent.getString("short_name"));
					JSONArray oTypes = oComponent.getJSONArray("types");
					for (int t=oTypes.length()-1; t>=0; t--) {
						oAddrc.addType(oTypes.getString(t));
					}
					sStAddr.addComponent(oAddrc);
				}
				sStAddr.setDisplayAddress(oResult0.getString("formatted_address"));
				JSONObject oGeometry = oResult0.getJSONObject("geometry");
				JSONObject oLocation = oGeometry.getJSONObject("location");
				sStAddr.setCoordinates(new LatLong(oLocation.getDouble("lat"),oLocation.getDouble("lng")));
			}
		} catch (NumberFormatException e) {
			throw new GeocodingException(e.getMessage()+" for URL "+sUrl, e);
		} catch (JSONException e) {
			throw new GeocodingException(e.getMessage()+" for URL "+sUrl, e);
		} catch (MalformedURLException e) {
			throw new GeocodingException(e.getMessage()+" for URL "+sUrl, e);
		} catch (IOException e) {
			throw new GeocodingException(e.getMessage()+" for URL "+sUrl, e);
		} catch (URISyntaxException e) {
			throw new GeocodingException(e.getMessage()+" for URL "+sUrl, e);
		}
		return sStAddr;
	}
	
	/**
	 * Get an StreetAddress object from an address string using Google Maps API
	 * @param sFullAddress Address to be geocoded
	 * @return StreetAddress instance or null if given address could not be geocoded
	 * @throws GeocodingException
	 * @since 8.0
	 */
	@Override
	public StreetAddress geoCode(String sFullAddress) throws GeocodingException {  	

		StreetAddress sStAddr = null;
		String sUrl = BASE_URL;
		
		if (sClient!=null) 
			sUrl += "&client="+sClient;
		if (sSignature!=null) 
			sUrl += "&signature="+sSignature;

		if (DebugFile.trace) {
			DebugFile.writeln("Begin GGeocoder.geoCode("+sFullAddress+")");
			DebugFile.incIdent();
		}

		try {
			sUrl += "&address="+URLEncoder.encode(sFullAddress, "UTF-8");
		} catch (UnsupportedEncodingException neverthrown) { }

	  sStAddr = getStreetAddressFromUrl(sUrl);
	  
		if (DebugFile.trace) {
			DebugFile.decIdent();
			DebugFile.writeln("End GGeocoder.geoCode() : "+sStAddr);
		}

		return sStAddr;
	}

  /**
   * Get an StreetAddress object from an lattitude and longitude
   * @param fLat float Lattitude
   * @param fLng float Longitude
   * @return StreetAddress instance or null if given address could not be geocoded
   * @throws GeocodingException
   * @since 8.0
   */
	public StreetAddress reverseGeoCode(float fLat, float fLng) throws GeocodingException {

		StreetAddress sStAddr = null;
		String sUrl = BASE_URL;
		if (sClient!=null) 
			sUrl += "&client="+sClient;
		if (sSignature!=null) 
			sUrl += "&signature="+sSignature;

		if (DebugFile.trace) {
			DebugFile.writeln("Begin GGeocoder.reverseGeoCode("+String.valueOf(fLat)+","+String.valueOf(fLng)+")");
			DebugFile.incIdent();
		}

		sUrl += "&latlng="+String.valueOf(fLat)+","+String.valueOf(fLng);

		sStAddr = getStreetAddressFromUrl(sUrl);
		
		if (DebugFile.trace) {
			DebugFile.decIdent();
			DebugFile.writeln("End GGeocoder.reverseGeoCode() : "+sStAddr);
		}

		return sStAddr;
	}
	
}
