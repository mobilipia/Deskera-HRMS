/*
 * Copyright (C) 2012  Krawler Information Systems Pvt Ltd
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.krawler.common.util;

/**
 * A place to keep commonly-used constants.
 */
public class Constants {
    //Image Path
	public static final String ImgBasePath = "images/store/";
	private static final String defaultImgPath = "images/defaultuser.png";
    private static final String defaultCompanyImgPath = "images/logo.gif";

	public static final long MILLIS_PER_SECOND = 1000;
	public static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60;
	public static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
	public static final long MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;
	public static final long MILLIS_PER_WEEK = MILLIS_PER_DAY * 7;
	public static final long MILLIS_PER_MONTH = MILLIS_PER_DAY * 31;
    // Regex for email and phone
    public static final String emailRegex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String contactRegex = "^(\\(?\\+?[0-9]*\\)?)?[0-9_\\- \\(\\)]*$";
    
	public static final int SECONDS_PER_MINUTE = 60;
	public static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * 60;
	public static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;
	public static final int SECONDS_PER_WEEK = SECONDS_PER_DAY * 7;
	public static final int SECONDS_PER_MONTH = SECONDS_PER_DAY * 31;
    // IDS from Table crm_combodata
    public static final String LEADSTATUSID_QUALIFIED ="f01e5a6f-7011-4e2d-b93e-58b5c6270239";
    public static final String CAMPAIGN_COMPLETE = "0fca425b-458f-4ceb-ba3e-5b7aab469d7e";
    public static final String CASESTATUS_PENDING = "443dd38f-1c39-43c3-8f41-6d490dcf8302";
    public static final String CASESTATUS_NEWCASE = "00962c0b-42c3-4640-b3c7-8be8ea922ed3";
    public static final String CASESTATUS_ESCALATED = "98e4ed03-259b-4d62-8c06-e0fb630170f8";
    public static final String LEADSTATUSID_CONTACTED ="c5c96e60-16a2-4222-99a1-125d00fe80f1";
    public static final String LEADSTATUSID_OPEN ="5c74d621-304c-491a-9217-74acb18549b6";
    public static final String CASEPRIORITY_HIGH ="3a047d1c-9945-4361-bd91-26cac9be1d97";
    public static final String CASESTATUS_NOTSTARTED ="1434ca1a-c7f1-470b-951c-a9e804ab2f30";
	public static final String OPPSTAGEID_CLOSEDWON ="667946c6-f7b0-49ee-8040-26573b820d2e";
    //Full MS-OUTLOOK CSV Header
    //public static final String[] CSV_HEADER_MSOUTLOOK = {"Title","First Name","Middle Name","Last Name","Suffix","Company","Department","Job Title","Business Street","Business Street 2","Business Street 3","Business City","Business State","Business Postal Code","Business Country/Region","Home Street","Home Street 2","Home Street 3","Home City","Home State","Home Postal Code","Home Country/Region","Other Street","Other Street 2","Other Street 3","Other City","Other State","Other Postal Code","Other Country/Region","Assistant's Phone","Business Fax","Business Phone","Business Phone 2","Callback","Car Phone","Company Main Phone","Home Fax","Home Phone","Home Phone 2","ISDN","Mobile Phone","Other Fax","Other Phone","Pager","Primary Phone","Radio Phone","TTY/TDD Phone","Telex","Account","Anniversary","Assistant's Name","Billing Information","Birthday","Business Address PO Box","Categories","Children","Directory Server","E-mail Address","E-mail Type","E-mail Display Name","E-mail 2 Address","E-mail 2 Type","E-mail 2 Display Name","E-mail 3 Address","E-mail 3 Type","E-mail 3 Display Name","Gender","Government ID Number","Hobby","Home Address PO Box","Initials","Internet Free Busy","Keywords","Language","Location","Manager's Name","Mileage","Notes","Office Location","Organizational ID Number","Other Address PO Box","Priority","Private","Profession","Referred By","Sensitivity","Spouse","User 1","User 2","User 3","User 4","Web Page"};
    //Header used for our contact export
    public static final String[] CSV_HEADER_MSOUTLOOK = {"\"First Name\"","\"E-mail Address\"","\"Business Phone\"","\"Business Street\""};
    //Default ids
    public static final String CURRENCY_DEFAULT ="1";
    public static final String TIMEZONE_DEFAULT ="1";
    public static final String NEWYORK_TIMEZONE_ID ="23";
    
    public static final String callBack_CONFIGID ="9";
    public static final String payComponent_CONFIGID ="21";
    public static final String timesheetjob_CONFIGID ="25";
    
    public static final String MALAYSIAN_COUNTRY_CODE ="137";
    
    
    public static final int MASTER_DATA_COMPONENTSUBTYPE_COMPONENT_TYPE_TAX =3;
    public static final int MASTER_DATA_COMPONENTSUBTYPE_COMPONENT_TYPE_INCOME_TAX =5;
    
    public static final int DESKERA_APPLICATION_ID_HRMS =4;
    
    
}
