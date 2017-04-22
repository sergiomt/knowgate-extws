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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import com.knowgate.debug.DebugFile;

public class SMSPull {

  // --------------------------------------------------------------------------

  private Connection oCon;
  private PreparedStatement oRcv;
  private PreparedStatement oAck;

  // --------------------------------------------------------------------------

  public SMSPull() {
  	oCon = null;
  	oAck = oRcv = null;
  }

  // --------------------------------------------------------------------------

  /**
   * Connect to JDBC DataSource for storing incoming messages and notifications
   * @param sDriver String JDBC driver class name
   * @param sUrl String JDBC connection string
   * @param sUser String GUID of user from k_users table
   * @param sPassword String User's passwords from k_users table
   * @throws SQLException
   */
  public void connect(String sDriver, String sUrl, String sUser, String sPassword)
    throws SQLException {
    
    if (oCon!=null) {
      if (!oCon.isClosed()) throw new SQLException("SMSPull.connect() Already connected to data source");
    }
    
    if (DebugFile.trace) {
      DebugFile.writeln("Begin SMSPull.connect("+sUrl+","+sUser+", ...)");
      DebugFile.incIdent();
    }

    try {
      @SuppressWarnings("unused")
	  Class cDriver = Class.forName(sDriver);
    } catch (ClassNotFoundException cnfe) {
      if (DebugFile.trace)DebugFile.decIdent();
      throw new SQLException("SMSPull.connect() Could not find class for driver "+sDriver);
    }
    
	oCon = DriverManager.getConnection(sUrl,sUser,sPassword);
	oCon.setAutoCommit(true);

	oRcv = oCon.prepareStatement("INSERT INTO k_sms_received (id_msg,id_customer,id_chain,dt_received,bo_readed,dt_readed,nu_msisdn,tx_body) VALUES (?,?,?,?,0,NULL,?,?)");
	oAck = oCon.prepareStatement("INSERT INTO k_sms_notifications (id_msg,dt_received,bo_readed,dt_readed,id_error,id_status) VALUES (?,?,0,NULL,?,?)");

    if (DebugFile.trace) {
      DebugFile.decIdent();
      DebugFile.writeln("End SMSPull.connect()");
    }

  } // connect

  // --------------------------------------------------------------------------

  /**
   * Close JDBC connection to database
   */
  public void close()
    throws SQLException {
	if (null==oCon) throw new SQLException("SMSPull.close() Connection does not exist");
	if (oCon.isClosed()) throw new SQLException("SMSPull.close() Not connected to data source");

	if (null!=oAck) oAck.close();
	if (null!=oRcv) oRcv.close();
	
	oCon.close();
	oCon=null;
  } // close

  // --------------------------------------------------------------------------

  /**
   * Store an incoming message at the database
   * @param oMsg SMSMessage
   * @throws SQLException
   */

  public void receive(SMSMessage oMsg)
  	throws SQLException {

    if (DebugFile.trace) {
      DebugFile.writeln("Begin SMSPull.receive("+oMsg.customerAccount()+","+oMsg.messageId()+","+oMsg.msisdnNumber()+")");
      DebugFile.incIdent();
    }

	oRcv.setString(1, oMsg.customerAccount());
	oRcv.setString(2, oMsg.messageId());
	oRcv.setNull(3, Types.VARCHAR);
	oRcv.setTimestamp(4, new Timestamp(oMsg.dateStamp().getTime()));
	oRcv.setString(5, oMsg.msisdnNumber());
	oRcv.setString(6, oMsg.textBody());
	oRcv.executeUpdate();

    if (DebugFile.trace) {
      DebugFile.decIdent();
      DebugFile.writeln("End SMSPull.receive()");
    }

  } // receive

  // --------------------------------------------------------------------------
  
  public void acknowledge(SMSResponse oRsp)
  	throws SQLException {

    if (DebugFile.trace) {
      DebugFile.writeln("Begin SMSPull.acknowledge("+oRsp.messageId()+","+oRsp.errorCode().toString()+","+oRsp.notificationStatusCode().toString()+")");
      DebugFile.incIdent();
    }
  		
	oAck.setString(1, oRsp.messageId());
	oRcv.setTimestamp(2, new Timestamp(oRsp.dateStamp().getTime()));
	oAck.setInt(3, oRsp.errorCode().intValue());
	oAck.setInt(4, oRsp.notificationStatusCode().intValue());
	oAck.executeUpdate();

    if (DebugFile.trace) {
      DebugFile.decIdent();
      DebugFile.writeln("End SMSPull.acknowledge()");
    }

  } // acknowledge

  // --------------------------------------------------------------------------

} // SMSPull
