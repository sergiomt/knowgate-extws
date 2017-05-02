package com.knowgate.sms.impl;

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
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.sql.SQLException;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Base64;

import com.knowgate.sms.SMSPush;
import com.knowgate.sms.SMSMessage;
import com.knowgate.sms.SMSResponse;

/**
 * <p>Implementation for sending SMS messages with Sybase 365 platform</p>
 * @author Sergio Montoro Ten
 * @version 5.0
 */

public final class SMSPushSybase365 extends SMSPush {
  
  private static final String DEFAULT_URL = "http://messaging.mobileway.com/";
  
  private static SimpleDateFormat DTF = new SimpleDateFormat("yyyyMMddHHmmssSSS");

  private HttpURLConnection oCon;
  
  /**
   * <p>Open HTTP connection for sending messages</p>
   * @param sUrl Base URL. Typically it should be "http://messaging.mobileway.com/",
   * if null then the default value http://messaging.mobileway.com/ is used
   * The target URL will be sUrl+sUser+"/"+sUser+".sms"
   * For example, if sUser is kwgate_pus01120 then the target URL will be
   * http://messaging.mobileway.com/kwgate_pus01120/kwgate_pus01120.sms
   * @param sUser Sybase 365 Customer Account Identifier
   * @param sPassword Sybase 365 Customer Account Password
   * @param oConnectionProps Not used, must be null
   * @throws IOException
   * @throws MalformedURLException
   */

  public void connect(String sUrl, String sUser, String sPassword, Properties oConnectionProps)
    throws IOException,SQLException,MalformedURLException {

	// Sanitize base URL
	if (sUrl==null) sUrl = DEFAULT_URL;
	if (sUrl.length()==0) sUrl = DEFAULT_URL;
	if (!sUrl.endsWith("/")) sUrl += "/";

	String sUsrPwd = sUser+":"+sPassword;
    String sEncAuth = Base64.getEncoder().encodeToString(sUsrPwd.getBytes());
	URL oUrl = new URL(sUrl+sUser+"/"+sUser+".sms");
    oCon = (HttpURLConnection) oUrl.openConnection();
    oCon.setRequestProperty("Authorization", "Basic " + sEncAuth);
    oCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    oCon.setDoOutput(true);
    oCon.setDoInput (true);
    oCon.setUseCaches(false);
	oCon.setRequestMethod("POST");
  } // connect

  /**
   * Send SMS
   * @param SMSMessage
   * @return SMSResponse
   * @throws IOException
   * @throws SQLException
   * @throws IllegalArgumentException
   * @throws UnsupportedEncodingException
   */

  public SMSResponse push (SMSMessage oMsg)
    throws IOException,SQLException,IllegalArgumentException,UnsupportedEncodingException {    

    String sData = "Version=2.0\nSubject="+oMsg.subject()+"\n[MSISDN]\nList="+oMsg.msisdnNumber()+"\n[MESSAGE]\nText="+oMsg.textBody();

	OutputStreamWriter oWrt = new OutputStreamWriter(oCon.getOutputStream());
    oWrt.write(sData);
    oWrt.flush();
	oWrt.close();
	
    int iStatusCode = oCon.getResponseCode();
    SMSResponse oRsp;
	BufferedReader oRdr;
    String sLin, uLin, sId, sErr;
    SMSResponse.ErrorCode iErrCode;
    boolean bBody;
		
	switch (iStatusCode) {
	  case 200:
	  	sId = "";
	    oRdr = new BufferedReader(new InputStreamReader(oCon.getInputStream()));
	
	    sLin = oRdr.readLine();
        bBody = false;
        while( null != sLin ) {
	      uLin = sLin.toUpperCase().trim();	  
	      if (!bBody && uLin.startsWith("<BODY>")) bBody = true;
	      if ( bBody && uLin.startsWith("</BODY>")) bBody = false;
	      if (bBody) {
	  	    try {
	  	      sLin = sLin.replaceAll("<br>" , "\n");
	  	      sLin = sLin.replaceAll("<br/>", "\n");
	  	      sLin = sLin.replaceAll("<BR>" , "\n");
	  	      sLin = sLin.replaceAll("<BR/>", "\n");
	  	    } catch (Exception neverthrown) { }
	        if (uLin.startsWith("ORDERID")) {
	          sId = split2(sLin,'=')[1];
	          sId = sId.replace('\n', ' ').trim();
	        }
	      } // fi
	      sLin = oRdr.readLine();
        }
        oRdr.close();
	    oRsp = new SMSResponse(sId, new Date(), SMSResponse.ErrorCode.NONE, SMSResponse.StatusCode.POSITIVE_ACK, "");
	    break;

	  case 400:
	    oRdr = new BufferedReader(new InputStreamReader(oCon.getErrorStream()));
	
		iErrCode = SMSResponse.ErrorCode.UNKNOWN_ERROR;
		sErr = "";
	    sLin = oRdr.readLine();
        bBody = false;
        while( null != sLin ) {
	      uLin = sLin.toUpperCase().trim();	  
	      if (!bBody && uLin.startsWith("<BODY>")) bBody = true;
	      if ( bBody && uLin.startsWith("</BODY>")) bBody = false;
	      if (bBody) {
	  	    try {
	  	      sLin = sLin.replaceAll("<br>" , "\n");
	  	      sLin = sLin.replaceAll("<br/>", "\n");
	  	      sLin = sLin.replaceAll("<BR>" , "\n");
	  	      sLin = sLin.replaceAll("<BR/>", "\n");
	  	    } catch (Exception neverthrown) { }
	        if (uLin.startsWith("5001") || uLin.startsWith("5002") ||
	        	uLin.startsWith("5003") || uLin.startsWith("5004") ||
	        	uLin.startsWith("5005") || uLin.startsWith("5006") ||
	            uLin.startsWith("8000")) {
	          iErrCode = SMSResponse.ErrorCode.SERVER_UNAVAILABLE;
	          sErr = split2(sLin, ' ')[1].replace('\n', ' ').trim();
	        } else if (uLin.startsWith("600F")) {
	          iErrCode = SMSResponse.ErrorCode.AUTHENTICATION_FAILURE;
	          sErr = split2(sLin, ' ')[1].replace('\n', ' ').trim();
	        } else if (uLin.startsWith("6017") ||
	        	       uLin.startsWith("602A") || uLin.startsWith("602B")) {
	          iErrCode = SMSResponse.ErrorCode.INVALID_CHARACTER;
	          sErr = split2(sLin, ' ')[1].replace('\n', ' ').trim();
	        } else if (uLin.startsWith("6018") || uLin.startsWith("6019")) {
	          iErrCode = SMSResponse.ErrorCode.INVALID_MSISDN;
	          sErr = split2(sLin, ' ')[1].replace('\n', ' ').trim();
	        }
	      } // fi
	      sLin = oRdr.readLine();
        }
        oRdr.close();
	    oRsp = new SMSResponse(null, new Date(), iErrCode, SMSResponse.StatusCode.NEGATIVE_FAILED_DELIVERY, sErr);
	    break;

	    default:
	      throw new IOException("Invalid HTTP response Code "+String.valueOf(iStatusCode));
	} // end switch

	return oRsp;
  } // push

  /**
   * Open a new connection and send and SMS
   * @param sAccount Sybase 365 Customer Account Identifier
   * @param sAuthStr Sybase 365 Customer Account Password
   * @param sMsisdn MSISDN Number with country preffix like +34609090603
   * @param sSubject Message subject. Will not be sent to recipient but returned in status reports by Sybase 365.
   * @param sText Message Text. If it is over 160 character it will be splitted over several messages.
   * @return SMSResponse
   * @throws IOException
   * @throws IllegalArgumentException
   */

  public static SMSResponse push (String sAccount, String sAuthStr,
  						          String sMsisdn, String sSubject, String sText)
    throws IOException,IllegalArgumentException {      
  	Date dtNow = new Date();
  	String sId = DTF.format(dtNow);
  	SMSResponse oRsp = null;
    SMSPushSybase365 oSms = new SMSPushSybase365();
    try {
  	  oSms.connect(null, sAccount, sAuthStr, null);
      oRsp = oSms.push(new SMSMessage(SMSMessage.MType.PLAIN_TEXT,
      			       sId, sAccount,
      			       sMsisdn, sSubject, sText,
      			       "ISO8859_1", dtNow));
  	  oSms.close();
  	} catch (SQLException neverthrown) {}
  	  catch (UnsupportedEncodingException neverthrown) {}
  	return oRsp;
  } // push
  
  public void close() throws IOException,SQLException {
  	oCon.disconnect();
	oCon=null;
  }

  private String[] split2(String sInputStr, char cDelimiter)
    throws NullPointerException {

    int iDelim = sInputStr.indexOf(cDelimiter);

    if (iDelim<0)
      return new String[]{sInputStr};
    else if (iDelim==0)
      return new String[]{"", sInputStr.substring(iDelim+1)};
    else if (iDelim==sInputStr.length()-1)
      return new String[]{sInputStr.substring(0, iDelim), ""};
    else
      return new String[]{sInputStr.substring(0, iDelim), sInputStr.substring(iDelim+1)};
  } // split2
  
} // SMSPushSybase365
