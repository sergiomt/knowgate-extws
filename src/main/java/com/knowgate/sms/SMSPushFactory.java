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

public final class SMSPushFactory {

 /**
  * Create an instance of an SMSPush subclass
  * @param sProviderClassName One of { com.knowgate.sms.SMSPushAltiria, com.knowgate.sms.SMSPushRealidadFutura, com.knowgate.sms.SMSPushSybase365 }
  */

 public static SMSPush newInstanceOf(String sProviderClassName)
   throws ClassNotFoundException,ClassCastException,InstantiationException,IllegalAccessException {

   return (SMSPush) Class.forName(sProviderClassName).newInstance(); 

 } // newInstanceOf

} // SMSPushFactory
