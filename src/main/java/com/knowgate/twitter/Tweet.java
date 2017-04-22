package com.knowgate.twitter;

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

import java.util.HashMap;
import java.util.Date;

/**
 * @author Sergio Montoro Ten
 * @version 1.0
 */
public class Tweet extends HashMap<String,Object> {
 
  private User oUsr;
  
  public Tweet() {
  	oUsr = new User();
  	// aResolvedURLs = new ArrayList();
  }

  public String getId() {
  	return (String) get("id");
  }

  public void setId(String sTweetId) {
  	put("id", sTweetId);
  }
  
  public User getUser() {
  	return oUsr;
  }

  public Date getDate(final String sKey) {
  	Object oObj = get(sKey);
  	return oObj==null ? null : (Date) oObj;
  }

  public String getString(final String sKey) {
  	Object oObj = get(sKey);
  	return oObj==null ? null : oObj.toString();
  }
  
}
