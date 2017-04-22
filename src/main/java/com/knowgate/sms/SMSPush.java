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

import java.util.Properties;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.sql.SQLException;

import java.net.MalformedURLException;

public abstract class SMSPush {

  /**
   * Conect to SMS HTTP Push Platform
   * @param sUrl String Connection URL
   * @param sUser String User
   * @param sPassword String Password
   * @throws IOException
   * @throws MalformedURLException
   */
  public abstract void connect(String sUrl, String sUser, String sPassword, Properties oConnectionProps)
    throws IOException,SQLException,MalformedURLException;

  /**
   * Close SMS HTTP Push Connection
   */
  public abstract void close() throws IOException,SQLException;
	
  /**
   * Send Plain Text SMS
   * @param SMSMessage oMsg
   * @throws IOException
   * @throws IllegalArgumentException If MSISDN or text are malformed or dtWhenMustBeSend is before now
   * @throws UnsupportedCharacterEncoding If given sCharacterEncoding is not supported
   * @return SMSResponse
   */
  public abstract SMSResponse push (SMSMessage oMsg)
    throws IOException,SQLException,IllegalArgumentException,UnsupportedEncodingException;	

} // SMSPush
