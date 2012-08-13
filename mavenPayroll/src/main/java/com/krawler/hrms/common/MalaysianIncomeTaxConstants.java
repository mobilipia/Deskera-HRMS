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
package com.krawler.hrms.common;

public class MalaysianIncomeTaxConstants {
	public static final double EPF_PERCENT = .11;
	public static final double MAX_LIMIT_FOR_EPF_AND_LIC = 6000;

    public static final double MAX_LIMIT_FOR_MEDICAL_CHECKUP = 500;
    public static final double MAX_LIMIT_FOR_MEDICAL_CHECKUP_AND_SERIOUS_DISEASE = 5000;

    public static final double NON_RESIDENT_EMPLOYEE_TAX_COEFFICIENT = 0.26;

    public static final int STATUSID_RESIDENT = 1;
    public static final int STATUSID_NON_RESIDENT = 2;
    public static final int STATUSID_RETURNING_EXPERT_PROGRAM = 3;
    public static final int STATUSID_KNOWLEDGE_WORKER = 4;

    public static final int CATEGORYID_SINGLE =1;
    public static final int CATEGORYID_MARRIED_WIFE_NOT_WORKING =2;
    public static final int CATEGORYID_MARRIED_WIFE_WORKING =3;

    public static final double RETURNING_EXPERT_PROGRAM_INTEREST_RATE =0.15;
    public static final int RETURNING_EXPERT_PROGRAM_REBATE_SINGLE_OR_WIFE_WORKING =400;
    public static final int RETURNING_EXPERT_PROGRAM_REBATE_WIFE_NOT_WORKING =800;
    public static final int RETURNING_EXPERT_PROGRAM_SLAB_VALUE =35000;

    public static final double KNOWLEDGE_WORKER_INTEREST_RATE =0.15;

    public static final int UNIQUE_CODE_HOUSING_LOAN =11;
    public static final int UNIQUE_CODE_TUTION_FEES =12;
    public static final int UNIQUE_CODE_EDUCATION_MEDICAL_PREMIUM =13;
    public static final int UNIQUE_CODE_PRIVATE_RETIREMENT_AND_ANNUITY =14;
    public static final int UNIQUE_CODE_MEDICAL_EXAMINATION =15;
    public static final int UNIQUE_CODE_MEDICAL_SERIOUS_DISEASE =16;
    public static final int UNIQUE_CODE_MEDICAL_EXPENSES_PARENTS =17;
    public static final int UNIQUE_CODE_ALIMONY_PAYMENT =18;
    public static final int UNIQUE_CODE_PURCHASE_BOOKS =19;
    public static final int UNIQUE_CODE_PURCHASE_COMPUTER =20;
    public static final int UNIQUE_CODE_PURCHASE_SPORTS_EUIPMENT =21;
    public static final int UNIQUE_CODE_INTERNET_BROADBAND =22;
    public static final int UNIQUE_CODE_MEDICAL_EQUIPMENT =23;
    public static final int UNIQUE_CODE_EDUCATION_FUND_SSPN =24;
    public static final int UNIQUE_CODE_CHILD_CARE_ALLOWANCE =38;
    public static final int UNIQUE_CODE_EMPLOYER_CONTRIBUTION_SUBSIDISED_INTEREST_HOUSING =44;
    
    public static final String STATUTORY_FORM_TABLE_POJO_NAME_AMANAH_SAHAM_NASIONAL ="MalaysiaFormAmanahSahamNasional";
    public static final String STATUTORY_FORM_TABLE_POJO_NAME_CP39 ="MalaysiaFormCP39";
    public static final String STATUTORY_FORM_TABLE_POJO_NAME_CP39A ="MalaysiaFormCP39A";
    public static final String STATUTORY_FORM_TABLE_POJO_NAME_CP21 ="MalaysiaFormCP21";
    public static final String STATUTORY_FORM_TABLE_POJO_NAME_HRD_LEVY="MalaysiaFormHRDLevy";
    public static final String STATUTORY_FORM_TABLE_POJO_NAME_PCB2 ="MalaysiaFormPCB2";
    public static final String STATUTORY_FORM_TABLE_POJO_NAME_TABUNG_HAJI ="MalaysiaFormTabungHaji";
    public static final String STATUTORY_FORM_TABLE_POJO_NAME_TP1 ="MalaysiaFormTP1";
    public static final String STATUTORY_FORM_TABLE_POJO_NAME_TP2 ="MalaysiaFormTP2";
    public static final String STATUTORY_FORM_TABLE_POJO_NAME_TP3 ="MalaysiaFormTP3";
    public static final String STATUTORY_FORM_TABLE_POJO_NAME_EA ="MalaysiaFormEA";

    public static final String STATUTORY_FORM_NAME_AMANAH_SAHAM_NASIONAL ="Amanah saham Nasional";
    public static final String STATUTORY_FORM_NAME_CP39 ="CP 39";
    public static final String STATUTORY_FORM_NAME_CP39A ="CP 39A";
    public static final String STATUTORY_FORM_NAME_CP21 ="Cukai Pendapatan 21";
    public static final String STATUTORY_FORM_NAME_HRD_LEVY="HRD Levy";
    public static final String STATUTORY_FORM_NAME_PCB2 ="PCB 2";
    public static final String STATUTORY_FORM_NAME_TABUNG_HAJI ="Tabung Haji";
    public static final String STATUTORY_FORM_NAME_TP1 ="TP 1";
    public static final String STATUTORY_FORM_NAME_TP2 ="TP 2";
    public static final String STATUTORY_FORM_NAME_TP3 ="TP 3";
    public static final String STATUTORY_FORM_NAME_EA ="EA";

    public static final int STATUTORY_FORM_STATUS_PENDING =0;
    public static final int STATUTORY_FORM_STATUS_AUTHORIZE =1;
    public static final int STATUTORY_FORM_STATUS_UNAUTHORIZE =2;

}
