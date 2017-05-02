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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Properties;

import java.sql.SQLException;

import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import com.knowgate.sms.SMSPush;
import com.knowgate.sms.SMSMessage;
import com.knowgate.sms.SMSResponse;

public final class SMSPushRealidadFutura extends SMSPush {

  	private static SimpleDateFormat DTF = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	private static final String DEFAULT_URL = "http://railsfax.realidadfutura.net/sms/send_sms?";

	private String sUrl, sUsr, sPwd;
	
	private Properties oPrp;
	
	/**
     * <p>Prepare internal state for sending messages</p>
     * @param sBaseUrl Base URL. Typically it should be "http://railsfax.realidadfutura.net/sms/send_sms?",
     * if null then the default value http://railsfax.realidadfutura.net/sms/send_sms? is used
     * The target URL will be sUrl+"username="+sUser+"&password="+sPassword+"&from="+oConnectionProps.from+"&to="+34XXXXXXXXX+"&text=Message Text"
     * @param sUser Realidad Futura Customer Account Identifier
     * @param sPassword Realidad Futura Customer Account Password
     * @param oConnectionProps Set property from for changing sender's number
     * @throws IOException
     * @throws MalformedURLException
     */
	public void connect(String sBaseUrl, String sUser, String sPassword, Properties oConnectionProps)
		throws IOException, SQLException, MalformedURLException {

		// Sanitize base URL
		if (sBaseUrl==null) sBaseUrl = DEFAULT_URL;
		if (sBaseUrl.length()==0) sBaseUrl = DEFAULT_URL;
		if (!sBaseUrl.endsWith("?")) sBaseUrl += "?";

		sUrl = sBaseUrl;
		sUsr = sUser;
		sPwd = sPassword;
		oPrp = oConnectionProps;

	}

	/**
	 * This method does not do anything for Realidad Futura platform
	 */
	public void close() throws IOException, SQLException {
	}

  /**
   * Open HTTP connection and send SMS
   * @param SMSMessage
   * @return SMSResponse
   * @throws IOException
   * @throws SQLException
   * @throws IllegalArgumentException
   * @throws UnsupportedEncodingException
   */
	public SMSResponse push(SMSMessage oMsg)
		throws IOException, SQLException, IllegalArgumentException, UnsupportedEncodingException {

    SMSResponse oRsp;
		BufferedReader oRdr;

		String sErr, sLin, sFrm = "";
		if (oPrp.getProperty("from")!=null) sFrm = "&from="+oPrp.getProperty("from");

		String sMsisdn = oMsg.msisdnNumber();
		if (sMsisdn.startsWith("+")) sMsisdn = sMsisdn.substring(1);

		String sQry = sUrl+"username="+sUsr+"&password="+sPwd+sFrm+"&to="+sMsisdn+"&text="+URLEncoder.encode(oMsg.textBody(), "ISO8859_1");

		URL oUrl = new URL(sQry);
    	HttpURLConnection oCon = (HttpURLConnection) oUrl.openConnection();
	    int iStatusCode = oCon.getResponseCode();

	    switch (iStatusCode) {
	    	case 200:
	    		oRdr = new BufferedReader(new InputStreamReader(oCon.getInputStream(), "UTF-8"));
				sLin = oRdr.readLine();
        		while( null != sLin ) {
        		  sLin = oRdr.readLine();
        		} // wend
				oRdr.close();
				oRsp = new SMSResponse(oMsg.messageId(), new Date(), SMSResponse.ErrorCode.NONE, SMSResponse.StatusCode.POSITIVE_ACK, "");
				break;
	    	case 422:
	    		SMSResponse.ErrorCode eErr = SMSResponse.ErrorCode.UNKNOWN_ERROR;
	    		oRdr = new BufferedReader(new InputStreamReader(oCon.getErrorStream(), "UTF-8"));
				sErr = "";
				sLin = oRdr.readLine();
        		while( null != sLin ) {
        		  if (sLin.indexOf("número de móvil válido")>0) eErr = SMSResponse.ErrorCode.INVALID_MSISDN;
        		  if (sLin.indexOf("es demasiado largo")>0) eErr = SMSResponse.ErrorCode.TEXT_TOO_LONG;
        		  if (sLin.indexOf("Usuario o contraseña erróneos")>0) eErr = SMSResponse.ErrorCode.AUTHENTICATION_FAILURE;
        		  sErr += sLin + "\n";
        		  sLin = oRdr.readLine();
        		} // wend
				oRdr.close();
				oRsp = new SMSResponse(oMsg.messageId(), new Date(), eErr, SMSResponse.StatusCode.NEGATIVE_FAILED_DELIVERY, sErr);
				break;
	    	default:
	      		throw new IOException("Invalid HTTP response Code "+String.valueOf(iStatusCode));
	    } // end switch	
		
		oCon.disconnect();
		
		return oRsp;
	} // push

	/**
	 * Open a new connection and send and SMS
	 * @param sUser Realidad Futura Customer Account Identifier
     * @param sAuthStr Realidad Futura Customer Account Password
     * @param sFrom Sender's Number
     * @param sTo Recipient's MSISDN Number with country preffix like 34609090603
     * @param sText Message Text.
     * @return SMSResponse
     * @throws IOException
     * @throws IllegalArgumentException
	*/

	public static SMSResponse push (String sAccount, String sAuthStr,
  						            String sFrom, String sTo, String sText)
    	throws IOException,IllegalArgumentException {
  		Date dtNow = new Date();
  		String sId = DTF.format(dtNow);
  		SMSResponse oRsp = null;
    	SMSPushRealidadFutura oSms = new SMSPushRealidadFutura();
    	try {
    		Properties oProps = new Properties();
    		oProps.put("from", sFrom);
  	  		oSms.connect(null, sAccount, sAuthStr, oProps);
      		oRsp = oSms.push(new SMSMessage(SMSMessage.MType.PLAIN_TEXT,
      			       		 sId, sAccount, sTo, "", sText, "ISO8859_1", dtNow));
  		} catch (SQLException neverthrown) {}
  	  	  catch (UnsupportedEncodingException neverthrown) {}
  		return oRsp;
  	} // push

}
