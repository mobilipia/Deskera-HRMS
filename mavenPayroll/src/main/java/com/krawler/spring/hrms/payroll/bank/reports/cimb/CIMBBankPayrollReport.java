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

package com.krawler.spring.hrms.payroll.bank.reports.cimb;

import com.krawler.spring.hrms.payroll.bank.reports.BankPayrollReport;
import com.krawler.spring.hrms.payroll.bank.tabularreport.BankPayrollReportSection;
import com.krawler.spring.hrms.payroll.bank.*;
import com.krawler.spring.hrms.payroll.bank.tabularreport.Column;
import com.krawler.spring.hrms.payroll.bank.tabularreport.TabularReportSection;
import com.krawler.spring.hrms.payroll.bank.fileformat.FileContentGenerator;

/**
 *
 * @author krawler
 */
public class CIMBBankPayrollReport implements BankPayrollReport{

    private BankPayrollReportSection header;
    private BankPayrollReportSection detail;
    private BankPayrollReportSection trailer;

    public static enum CIMB_REPORT_COLS{
        
        HEADER_RECORD_TYPE(new Column("Record Type",2)),
        HEADER_ORGANISATION_CODE(new Column("Organization Code",5)),
        HEADER_ORANISATION_NAME(new Column("Organization Name",40)),
        HEADER_CREDIT_DATE(new Column("Credit Date",8)),
        HEADER_SECURITY_CODE(new Column("Security Code",16)),
        HEADER_FILLER(new Column("Filler",78)),
        
        DETAIL_RECORD_TYPE(new Column("Record Type",2)),
        DETAIL_BNM_CODE(new Column("BNM Code",7)),
        DETAIL_ACCOUNT_NUMBER(new Column("Account Number",16)),
        DETAIL_BENFICIARY_NAME(new Column("Beneficiary Name",40)),
        DETAIL_PAYMENT_AMOUNT(new Column("Payment Amount",11)),
        DETAIL_REFERENCE_NUMBER(new Column("Reference Number",30)),
        DETAIL_BEFICIARY_ID(new Column("Beneficiary ID",20)),
        DETAIL_TRANSACTION_TYPE(new Column("Transaction Type",1)),
        DETAIL_FILLER(new Column("Filler",22)),

        TRAILER_RECORD_TYPE(new Column("Record Type",11)),
        TRAILER_TOTAL_NUMBER_OF_RECORDS(new Column("Total Number of Records",6)),
        TRAILER_TOTAL_AMOUNT(new Column("Total Amount",16)),
        TRAILER_FILLER(new Column("Filler",40));

        private Column column;

        CIMB_REPORT_COLS(Column column){
            this.column = column;
        }

        public Column getColumn(){
            return this.column;
        }
    };

    public CIMBBankPayrollReport(FileContentGenerator fcg) {
        
        TabularReportSection header = new TabularReportSection(fcg,true);
        
        header.addColumns(CIMB_REPORT_COLS.HEADER_RECORD_TYPE.getColumn(),
                CIMB_REPORT_COLS.HEADER_ORANISATION_NAME.getColumn(),
                CIMB_REPORT_COLS.HEADER_ORGANISATION_CODE.getColumn(),
                CIMB_REPORT_COLS.HEADER_SECURITY_CODE.getColumn(),
                CIMB_REPORT_COLS.HEADER_CREDIT_DATE.getColumn(),
                CIMB_REPORT_COLS.HEADER_FILLER.getColumn());
  
        this.header=header;


        TabularReportSection detail = new TabularReportSection(fcg,true);

        detail.addColumns(CIMB_REPORT_COLS.DETAIL_ACCOUNT_NUMBER.getColumn(),
                CIMB_REPORT_COLS.DETAIL_BEFICIARY_ID.getColumn(),
                CIMB_REPORT_COLS.DETAIL_BENFICIARY_NAME.getColumn(),
                CIMB_REPORT_COLS.DETAIL_BNM_CODE.getColumn(),
                CIMB_REPORT_COLS.DETAIL_FILLER.getColumn(),
                CIMB_REPORT_COLS.DETAIL_PAYMENT_AMOUNT.getColumn(),
                CIMB_REPORT_COLS.DETAIL_RECORD_TYPE.getColumn(),
                CIMB_REPORT_COLS.DETAIL_REFERENCE_NUMBER.getColumn(),
                CIMB_REPORT_COLS.DETAIL_TRANSACTION_TYPE.getColumn());
        
        this.detail=detail;


        TabularReportSection trailer = new TabularReportSection(fcg,true);
  
        trailer.addColumns(CIMB_REPORT_COLS.TRAILER_RECORD_TYPE.getColumn(),
                CIMB_REPORT_COLS.TRAILER_TOTAL_AMOUNT.getColumn(),
                CIMB_REPORT_COLS.TRAILER_TOTAL_NUMBER_OF_RECORDS.getColumn(),
                CIMB_REPORT_COLS.TRAILER_FILLER.getColumn());


        this.trailer=trailer;
    }

     @Override
    public void generate(){
       
        this.header.generate();
        this.detail.generate();
        this.trailer.generate();
       
    };

    public BankPayrollReportSection getHeaderSection(){

        return this.header;
    }

    public BankPayrollReportSection getDetailSection(){

        return this.detail;
    }

    public BankPayrollReportSection getTrailerSection(){

        return this.trailer;
    }

}
