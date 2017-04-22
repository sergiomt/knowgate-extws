package facebook4j.test;

import java.util.Iterator;

import org.junit.Test;

import com.knowgate.gis.GeocodingException;
import com.knowgate.gis.StreetAddress;
import com.knowgate.google.GGeocoder;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.IdNameEntity;
import facebook4j.Like;
import facebook4j.ResponseList;
import facebook4j.User;
import facebook4j.auth.AccessToken;

import facebook4j.internal.org.json.JSONArray;
import facebook4j.internal.org.json.JSONException;
import facebook4j.internal.org.json.JSONObject;

public class TestFacebook {

  private final String SERGIOM = "";
  private final String PAULNTS = "";

  @Test
	public void test01ReadUser() throws FacebookException, JSONException, NumberFormatException, GeocodingException {
		// Get Access Token with
  	// https://developers.facebook.com/tools/explorer
  	// View token with
		// https://developers.facebook.com/tools/debug/access_token
		
		Facebook fb = new FacebookFactory().getInstance();
		fb.setOAuthAppId("515248035228467", "661de86d310fafadff8b162947a484b1");
		fb.setOAuthPermissions("email,user_about_me,user_birthday,user_events,user_likes,user_friends,user_education_history,user_location,user_relationships,user_subscriptions");
		fb.setOAuthAccessToken(new AccessToken(SERGIOM, null));
	
	  User usr = fb.getMe();
	  System.out.println("id="+usr.getId());
	  System.out.println("name="+usr.getName());
	  System.out.println("firstName="+usr.getFirstName());
	  System.out.println("lastName="+usr.getLastName());
	  System.out.println("birthday="+usr.getBirthday());
	  System.out.println("bio="+usr.getBio());
	  if (usr.getCover()!=null)
	    System.out.println("coverId="+usr.getCover().getId());
	  else
	    System.out.println("coverId=");
	  System.out.println("email="+usr.getEmail());
	  System.out.println("gender="+usr.getGender());
	  if (usr.getLanguages()!=null) {
	  	System.out.println("languages");
	    for (IdNameEntity e : usr.getLanguages()) {
	    	System.out.println("  language="+e.getName()+" ("+e.getId()+")");
	    }	
	  } else {
	  	System.out.println("no languages");
	  }
	  ResponseList<Like> likes = fb.getUserLikes();
	  Iterator<Like> iter = likes.iterator();
	  while (iter.hasNext()) {
	  	Like l = iter.next();
	  	System.out.println("like "+l.getCategory()+" "+l.getName());
	  }
	  // usr.getFavoriteAthletes()
	  if (usr.getHometown()!=null)
		  System.out.println("hometown="+usr.getHometown().getName()+" ("+usr.getHometown().getId()+")");
	  else
	  	System.out.println("hometown=");
	  if (usr.getLocation()!=null) {
	  	System.out.println("location="+usr.getLocation().getName()+" ("+usr.getLocation().getId()+")");
	  	JSONArray arr = fb.executeFQL("SELECT type,name,latitude,longitude FROM place WHERE page_id='"+usr.getLocation().getId()+"' AND is_city='true'");
	  	for (int i = 0; i < arr.length(); i++) {
	      JSONObject obj = arr.getJSONObject(i);
	      System.out.println(obj.get("type")+","+obj.get("name")+", ["+obj.get("latitude")+","+obj.get("longitude")+"]");
	      GGeocoder gcd = new GGeocoder();
	      StreetAddress adr = gcd.reverseGeoCode(Float.parseFloat(obj.getString("latitude")), Float.parseFloat(obj.getString("longitude")));
	      System.out.println("reverse geocode "+adr.getPostalCode()+" "+adr.getCity()+" "+adr.getCountryName());	      
	    }
	  } else {
	  	System.out.println("location=");
	  }

	  if (usr.getPicture()!=null)
	    System.out.println("main picture="+usr.getPicture().getURL());
	  else
	    System.out.println("main picture=");
	  System.out.println("political="+usr.getPolitical());
	  System.out.println("quotes="+usr.getQuotes());
	  System.out.println("relationShipStatus="+usr.getRelationshipStatus());
	  System.out.println("religion="+usr.getReligion());
	  System.out.println("userName="+usr.getUsername());
	  if (usr.getInterestedIn()!=null)
	  	for (String i : usr.getInterestedIn())
	  	  System.out.println("interested in="+i);
	  if (usr.getWebsite()!=null)
	    System.out.println("website="+usr.getWebsite());
	  else
	    System.out.println("website=");
	}
  
  public static void main(String[] args) throws FacebookException, JSONException, NumberFormatException, GeocodingException {
  	new TestFacebook().test01ReadUser();
  }
}
