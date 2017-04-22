package com.knowgate.sms;

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

import java.net.MalformedURLException;

import java.sql.SQLException;

import java.util.Date;
import java.util.Properties;

import com.oreilly.servlet.MailMessage;

import com.knowgate.sms.SMSPush;

public class SMSPushEMail extends SMSPush {

  private Properties oMailProps;
  
  public void connect(String sHost, String sUser, String sPass, Properties oConnectionProps)
    throws IOException,SQLException,MalformedURLException {
  	oMailProps=oConnectionProps;
  	if (sHost!=null) oMailProps.setProperty("mail.smtp.host", sHost);
  	if (sUser!=null) oMailProps.setProperty("mail.user", sUser);
  	if (sPass!=null) oMailProps.setProperty("mail.password", sPass);
  }

  public SMSResponse push (SMSMessage oSms)
    throws IOException,SQLException,IllegalArgumentException,UnsupportedEncodingException {

	  MailMessage oMail = new MailMessage();
	  oMail.to(oSms.msisdnNumber());
	  oMail.from("noreply@hipergate.org");
	  oMail.setSubject("Hipergate SMS Test");
	  oMail.getPrintStream().print(oSms.textBody());
	  oMail.sendAndClose();
      return new SMSResponse(oSms.subject(), new Date(), SMSResponse.ErrorCode.NONE, SMSResponse.StatusCode.POSITIVE_ACK, null);
  }	
	
  public void close() throws IOException,SQLException { }

}
