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

package com.krawler.spring.hrms.payroll.bank.reports.mtd;

import com.krawler.spring.hrms.payroll.bank.fileformat.FileContentGenerator;
import com.krawler.spring.hrms.payroll.bank.reports.BankPayrollReport;
import com.krawler.spring.hrms.payroll.bank.tabularreport.BankPayrollReportSection;
import com.krawler.spring.hrms.payroll.bank.tabularreport.Column;
import com.krawler.spring.hrms.payroll.bank.tabularreport.TabularReportSection;

/**
 *
 * @author krawler
 */
public class MTDData implements BankPayrollReport{

    private BankPayrollReportSection header;
    private BankPayrollReportSection detail;
    
    public static enum MTD_DATA_REPORT_COLS{

        HEADER_RECORD_TYPE(new Column("Record Type",1)),
        HEADER_EMPLOYER_NO_HQ(new Column("Employer No. (HQ)",10)),
        HEADER_EMPLOYER_NO(new Column("Employer No.",10)),
        HEADER_YEAR_OF_DEDUCTION(new Column("Year of Deduction",4)),
        HEADER_MONTH_OF_DEDUCTION(new Column("Month of Deduction",2)),
        HEADER_TOTAL_MTD_AMOUNT(new Column("Total MTD Amount",10)),
        HEADER_TOTAL_MTD_RECORDS(new Column("Total MTD Records",5)),
        HEADER_TOTAL_CP38_AMOUNT(new Column("Total CP38 Amount",10)),
        HEADER_TOTAL_CP38_RECORDS(new Column("Total CP38 Records",5)),

        DETAIL_RECORD_TYPE(new Column("Record Type",1)),
        DETAIL_TAX_REFERENCE_NUMBER(new Column("Tax Reference No.",10)),
        DETAIL_WIFE_CODE(new Column("Wife code",1)),
        DETAIL_EMPLOYEE_NAME(new Column("Employee’s Name",60)),
        DETAIL_OLD_IC_NUMBER(new Column("Old IC No.",12)),
        DETAIL_NEW_IC_NUMBER(new Column("New IC No.",12)),
        DETAIL_PASSPORT_NUMBER(new Column("Passport No.",12)),
        DETAIL_COUNTRY_CODE(new Column("Country Code",2)),
        DETAIL_MTD_AMOUNT(new Column("MTD Amount",8)),
        DETAIL_CP38_AMOUNT(new Column("CP38 Amount",8)),
        DETAIL_EMPLOYEE_OR_SALARY_NUMBER(new Column("Employee No. or Salary No.",10)

        );

        private Column column;

        MTD_DATA_REPORT_COLS(Column column){
            this.column = column;
        }

        public Column getColumn(){
            return this.column;
        }
    };

    public MTDData(FileContentGenerator fcg) {

        TabularReportSection header = new TabularReportSection(fcg,false,false);

        header.addColumns(MTD_DATA_REPORT_COLS.HEADER_RECORD_TYPE.getColumn(),
                MTD_DATA_REPORT_COLS.HEADER_EMPLOYER_NO_HQ.getColumn(),
                MTD_DATA_REPORT_COLS.HEADER_EMPLOYER_NO.getColumn(),
                MTD_DATA_REPORT_COLS.HEADER_YEAR_OF_DEDUCTION.getColumn(),
                MTD_DATA_REPORT_COLS.HEADER_MONTH_OF_DEDUCTION.getColumn(),
                MTD_DATA_REPORT_COLS.HEADER_TOTAL_MTD_AMOUNT.getColumn(),
                MTD_DATA_REPORT_COLS.HEADER_TOTAL_MTD_RECORDS.getColumn(),
                MTD_DATA_REPORT_COLS.HEADER_TOTAL_CP38_AMOUNT.getColumn(),
                MTD_DATA_REPORT_COLS.HEADER_TOTAL_CP38_RECORDS.getColumn());

        this.header=header;


        TabularReportSection detail = new TabularReportSection(fcg,false,false);

        detail.addColumns(MTD_DATA_REPORT_COLS.DETAIL_RECORD_TYPE.getColumn(),
                MTD_DATA_REPORT_COLS.DETAIL_TAX_REFERENCE_NUMBER.getColumn(),
                MTD_DATA_REPORT_COLS.DETAIL_WIFE_CODE.getColumn(),
                MTD_DATA_REPORT_COLS.DETAIL_EMPLOYEE_NAME.getColumn(),
                MTD_DATA_REPORT_COLS.DETAIL_OLD_IC_NUMBER.getColumn(),
                MTD_DATA_REPORT_COLS.DETAIL_NEW_IC_NUMBER.getColumn(),
                MTD_DATA_REPORT_COLS.DETAIL_PASSPORT_NUMBER.getColumn(),
                MTD_DATA_REPORT_COLS.DETAIL_COUNTRY_CODE.getColumn(),
                MTD_DATA_REPORT_COLS.DETAIL_MTD_AMOUNT.getColumn(),
                MTD_DATA_REPORT_COLS.DETAIL_CP38_AMOUNT.getColumn(),
                MTD_DATA_REPORT_COLS.DETAIL_EMPLOYEE_OR_SALARY_NUMBER.getColumn());

        this.detail=detail;

    }

     @Override
    public void generate(){

        this.header.generate();
        this.detail.generate();
        

    };

    public BankPayrollReportSection getHeaderSection(){

        return this.header;
    }

    public BankPayrollReportSection getDetailSection(){

        return this.detail;
    }

}
