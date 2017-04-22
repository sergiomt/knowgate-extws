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
 
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URISyntaxException;
import java.net.MalformedURLException;

public final class API {

  public static Tweet getTweet(String sId)
  	throws UnsupportedEncodingException,IOException,URISyntaxException,MalformedURLException {
    Show oShw = new Show(sId);
    return oShw.getTweet();
  }

}
