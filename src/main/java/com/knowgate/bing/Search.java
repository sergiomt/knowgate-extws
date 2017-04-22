package com.knowgate.bing;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;

import java.net.URLEncoder;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;

/**
 * <p>Search using bing.com</p>
 * @author Sergio Montoro Ten
 *
 */
public class Search {

  private String appid;
  
  public Search(String sAppId) {
	  appid = URLEncoder.encode(sAppId);
  }

  /**
   * Perform an Internet wide search using bing.com
   * @param sText String searched
   * @return Array of Item or <b>null</b> if no results were found
   * @throws IllegalArgumentException
   * @throws IOException
   */
  public Item[] query (String sText)
    throws IllegalArgumentException, IOException {
	SyndFeed oFeed = null;
	Item[] aItems = null;
	HttpURLFeedFetcher oFtchr = new HttpURLFeedFetcher();	
	try {
	  oFeed = oFtchr.retrieveFeed(new URL("http://www.bing.com/search?appid="+appid+"&format=rss&q="+sText.replace(' ','+')));
	} catch (MalformedURLException neverthrown) {		
	} catch (FeedException e) {
		throw new IOException(e.getMessage(),e);
	} catch (FetcherException e) {
		throw new IOException(e.getMessage(),e);
	}
	List<SyndEntry> oEntries = oFeed.getEntries();
	if (oEntries!=null) {
	  if (oEntries.size()>0) {
		aItems = new Item[oEntries.size()];
		ListIterator<SyndEntry> oIter = oEntries.listIterator();
		int iItem = 0;
		while (oIter.hasNext()) {
			SyndEntry oEntry = oIter.next();
		  aItems[iItem++] = new Item(oEntry.getTitle(), oEntry.getLink(), oEntry.getDescription().getValue(), oEntry.getPublishedDate());		  
		}
	  }
	}
	return aItems;
  } // query

  /**
   * Search text at an specific site
   * @param sText String searched
   * @param sSite String domain name
   * @return Array of Item or <b>null</b> if no results were found
   * @throws IllegalArgumentException
   * @throws IOException
   */
  public Item[] query (String sText, String sSite) throws IllegalArgumentException, IOException {
	  return query(sText+"+site%3A"+sSite);
  }
  
}
