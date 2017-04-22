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

import java.util.Date;

public class SMSResponse {

  // --------------------------------------------------------------------------
	
	private String sId;
	private Date dtStamp;
	private ErrorCode eErrorCode;
	private StatusCode eNotificationStatusCode;
	private String sErrorMessage;

  // --------------------------------------------------------------------------
	
	/**
	 * Constructor for SMS response
	 * @param sMsgId Unique Message Identifier, as returned from SMS carrier
	 * @param dtResponse Date of acknowledgement
	 * @param eErrCode Error Code from enumeration { NONE(0), AUTHENTICATION_FAILURE(1), SERVER_UNAVAILABLE(2), INVALID_MSISDN(4), INVALID_CHARACTER(8), TEXT_TOO_LONG(16), UNKNOWN_ERROR(128) }
	 * @param eNotifyStatusCode from enumeration { POSITIVE_ACK(0),TEMPORARY_ACK(1),TEMPORARY_ACK_WAITING_FOR_HANDSET(2),NEGATIVE_RETRYING_DELIVERY(-1),NEGATIVE_MSISDN_IS_BLACKLISTED(-2),NEGATIVE_CALL_BARRED_BY_OPERATOR(-4),NEGATIVE_FAILED_DELIVERY(-8),NEGATIVE_OUT_OF_CREDIT(-16) }
	 * @param sErrorMsg Additional Error Information
	 */
	public SMSResponse(String sMsgId, Date dtResponse, ErrorCode eErrCode, StatusCode eNotifyStatusCode, String sErrorMsg) {
	  sId = sMsgId;
	  dtStamp = dtResponse;
	  eErrorCode = eErrCode;
	  eNotificationStatusCode = eNotifyStatusCode;
	  sErrorMessage = sErrorMsg;
	}

  // --------------------------------------------------------------------------
	
	public String errorMessage() {
	  return sErrorMessage;
	}

  // --------------------------------------------------------------------------
	
	public String messageId() {
	  return sId;
	}

  // --------------------------------------------------------------------------

	public ErrorCode errorCode() {
	  return eErrorCode;
	}

  // --------------------------------------------------------------------------

	public StatusCode notificationStatusCode() {
	  return eNotificationStatusCode;	  
	}

  // --------------------------------------------------------------------------

    public Date dateStamp() {
      return dtStamp;
    }

  // --------------------------------------------------------------------------

    public String toString() {
      return sId+";"+dtStamp+";"+eErrorCode+";"+eNotificationStatusCode+";"+sErrorMessage;
    }

  // --------------------------------------------------------------------------

    public enum ErrorCode {
	  NONE(0),
	  AUTHENTICATION_FAILURE(1),
	  SERVER_UNAVAILABLE(2),
	  INVALID_MSISDN(4),
	  INVALID_CHARACTER(8),
	  TEXT_TOO_LONG(16),
	  UNKNOWN_ERROR(128);

	  private final int iOrd;

	  ErrorCode(int iOrdinal) { iOrd= iOrdinal; }
	  
 	  public int intValue() {
        return iOrd;
      }

 	  public String toString(){
        return name();
      }

    } // ErrorCode

  // --------------------------------------------------------------------------

    public enum StatusCode {
	  POSITIVE_ACK(0),
	  TEMPORARY_ACK(1),
	  TEMPORARY_ACK_WAITING_FOR_HANDSET(2),
	  NEGATIVE_RETRYING_DELIVERY(-1),
	  NEGATIVE_MSISDN_IS_BLACKLISTED(-2),
	  NEGATIVE_CALL_BARRED_BY_OPERATOR(-4),
	  NEGATIVE_FAILED_DELIVERY(-8),
	  NEGATIVE_OUT_OF_CREDIT(-16);

	  private final int iOrd;

	  StatusCode(int iOrdinal) { iOrd= iOrdinal; }
	  
 	  public int intValue(){
        return iOrd;
      }

 	  public String toString(){
        return name();
      }

    } // StatusCode

} // SMSResponse
