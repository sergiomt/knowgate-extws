package com.knowgate.yahoo;

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
import java.net.URISyntaxException;
import java.util.Date;

import net.oauth.OAuthException;

import org.json.JSONObject;
import org.json.JSONException;

public class SocialProfile {

	private JSONObject oProfile,oEmails,oImage;
		
	public SocialProfile(YahooSession oState) throws IOException, OAuthException, URISyntaxException, JSONException {
		String[] aParams = new String[] {"q", "select * from social.profile where guid=me", "format", "json", "callback", ""};
		String sProfile = oState.invokeString("http://query.yahooapis.com/v1/yql", "POST", aParams);
		JSONObject oJSONObj = new JSONObject (sProfile);
		JSONObject oQuery = oJSONObj.getJSONObject("query");
		JSONObject oResults = oQuery.getJSONObject("results");
		oProfile = oResults.getJSONObject("profile");
		oEmails = oProfile.getJSONObject("emails");
		oImage = oProfile.getJSONObject("image");
	}

  public Date getBirthDate() throws NumberFormatException, JSONException {
  	String[] aMonthDate = oProfile.getString("birthdate").split("/");
  	int iYear = Integer.parseInt(oProfile.getString("birthYear"))-1900;
  	return new Date(iYear, Integer.parseInt(aMonthDate[0])-1, Integer.parseInt(aMonthDate[1]));
  }
  
  public String getGivenName() throws JSONException {
  	return oProfile.getString("givenName");
  }
  
  public String getFamilyName() throws JSONException {
  	return oProfile.getString("familyName");
  }

  public String getGender() throws JSONException {
  	return oProfile.getString("gender");
  }

  public String getEmail() throws JSONException {
  	return oEmails.getString("handle");
  }

  public String getImageUrl() throws JSONException {
  	return oImage.getString("imageUrl");
  }
  
}
