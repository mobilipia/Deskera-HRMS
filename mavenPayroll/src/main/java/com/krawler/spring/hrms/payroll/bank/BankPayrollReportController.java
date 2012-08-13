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

package com.krawler.spring.hrms.payroll.bank;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.hrms.common.HrmsCommonPayroll;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.payroll.bank.tabularreport.BankPayrollReportSection;
import com.krawler.spring.hrms.payroll.bank.reports.cimb.CIMBBankPayrollReport;
import com.krawler.spring.hrms.payroll.bank.tabularreport.Record;
import com.krawler.spring.hrms.payroll.bank.fileformat.TXTFile;
import com.krawler.spring.hrms.payroll.bank.reports.mtd.MTDData;
import com.krawler.spring.hrms.payroll.bank.tabularreport.Column;
import com.krawler.spring.hrms.payroll.hrmsPayrollDAO;
import com.krawler.spring.hrms.payroll.incometax.MalaysianIncomeTaxUtil;
import com.krawler.spring.hrms.payroll.statutoryform.MalaysianStatutoryFormDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import masterDB.MalaysiaCompanyForm;
import masterDB.MalaysiaFormCP39;
import masterDB.PayrollHistory;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author krawler
 */
public class BankPayrollReportController extends MultiActionController{

    private MalaysianStatutoryFormDAO malaysianStatutoryFormDAO;
    private sessionHandlerImpl sessionHandlerImplObj;
    private profileHandlerDAO profileHandlerDAO;
    private hrmsPayrollDAO hrmsPayrollDAO;
    private kwlCommonTablesDAO kwlCommonTablesDAO;
    
    public void setMalaysianStatutoryFormDAO(MalaysianStatutoryFormDAO malaysianStatutoryFormDAO) {
        this.malaysianStatutoryFormDAO = malaysianStatutoryFormDAO;
    }

    public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAO) {
        this.profileHandlerDAO = profileHandlerDAO;
    }
    public void setHrmsPayrollDAO(hrmsPayrollDAO hrmsPayrollDAO) {
        this.hrmsPayrollDAO = hrmsPayrollDAO;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAO) {
        this.kwlCommonTablesDAO = kwlCommonTablesDAO;
    }
    public void getCIMBBankPayrollReport(HttpServletRequest request, HttpServletResponse response) throws IOException{

        TXTFile file = new TXTFile();
        CIMBBankPayrollReport report = new CIMBBankPayrollReport(file);

        BankPayrollReportSection headerSection = report.getHeaderSection();
        List<Record> listData = new ArrayList<Record>();
        Record rec = new Record();
        
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.HEADER_RECORD_TYPE.getColumn(), "01");
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.HEADER_ORANISATION_NAME.getColumn(), "Krawler");
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.HEADER_ORGANISATION_CODE.getColumn(), 1111);
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.HEADER_CREDIT_DATE.getColumn(), 14112011);
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.HEADER_FILLER.getColumn(), 101010);
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.HEADER_SECURITY_CODE.getColumn(), 1);

        listData.add(rec);
        headerSection.setData(listData);


        BankPayrollReportSection detailSection = report.getDetailSection();
        listData = new ArrayList<Record>();
        rec = new Record();

        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.DETAIL_ACCOUNT_NUMBER.getColumn(), "22222");
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.DETAIL_BEFICIARY_ID.getColumn(), "22222");
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.DETAIL_BENFICIARY_NAME.getColumn(), "Kuldeep Singh");
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.DETAIL_BNM_CODE.getColumn(), 22);
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.DETAIL_FILLER.getColumn(), "22222");
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.DETAIL_PAYMENT_AMOUNT.getColumn(), 22222);
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.DETAIL_RECORD_TYPE.getColumn(), 2);
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.DETAIL_REFERENCE_NUMBER.getColumn(), "22222");
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.DETAIL_TRANSACTION_TYPE.getColumn(), 2);

        listData.add(rec);
        detailSection.setData(listData);

        BankPayrollReportSection trailerSection = report.getTrailerSection();
        listData = new ArrayList<Record>();
        rec = new Record();

        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.TRAILER_FILLER.getColumn(), 333333);
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.TRAILER_RECORD_TYPE.getColumn(), 03);
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.TRAILER_TOTAL_AMOUNT.getColumn(), 333);
        rec.setData(CIMBBankPayrollReport.CIMB_REPORT_COLS.TRAILER_TOTAL_NUMBER_OF_RECORDS.getColumn(), 3);

        listData.add(rec);
        trailerSection.setData(listData);
        
        //////////////
        report.generate();
        String cnt= file.getFileContent();

        File f = new File("/home/krawler/Desktop/txtFile.txt");
        if(!f.exists()){
            f.createNewFile();
        }

        FileOutputStream out = new FileOutputStream(f);
        out.write(cnt.getBytes());


    }

    public void getMTDDataPayrollReport(HttpServletRequest request, HttpServletResponse response) {

        try{
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String mnth = request.getParameter("month");
            String yr = request.getParameter("year");
            String frequency = request.getParameter("frequency");
            int month =0;
            int year =2012;

            if(!StringUtil.isNullOrEmpty(mnth)){
                month = Integer.parseInt(mnth);
            }
            if(!StringUtil.isNullOrEmpty(yr)){
                year = Integer.parseInt(yr);
            }

            TXTFile file = new TXTFile("");
            MTDData report = new MTDData(file);

            BankPayrollReportSection detailSection = report.getDetailSection();

            List<Record> listData = new ArrayList<Record>();

            Record rec = new Record();

            Column col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_RECORD_TYPE.getColumn();

            MalaysiaCompanyForm companyInfo = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(companyid, month, year);

            Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, month);

            List<PayrollHistory> phList = hrmsPayrollDAO.getGeneratedSalariesForMonth(companyid, enddate, frequency);

            List<MalaysiaFormCP39> listCP39 = malaysianStatutoryFormDAO.getEmployeeCP39List(companyid, month, year);

            Set<String> userSet = new HashSet<String>();

            for(MalaysiaFormCP39 cp39 : listCP39){
                if(cp39.getDeductionAmountForCP38()!=null){
                    userSet.add(cp39.getUseraccount().getUserID());
                }
            }
            for(PayrollHistory ph : phList){
                userSet.add(ph.getUser().getUserID());
            }

            int totalRecordCountForCP38=0;
            BigDecimal totalAmountForCP38 = BigDecimal.ZERO;
            int totalRecordCountForMTD=0;
            BigDecimal totalAmountForMTD = BigDecimal.ZERO;

            for (String userid : userSet){

                PayrollHistory ph = getPayrollHistory(userid, phList);
                MalaysiaFormCP39 CP39Obj = getCP39Object(userid, listCP39);

                rec = new Record();
                boolean recordCreated = false;
                
                col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_RECORD_TYPE.getColumn();
                rec.setData(col, getFormatData(col,"D",false,""));

                if(CP39Obj!= null){

                    
                    String taxRefFileNumber = getNumericData(CP39Obj.getIncomeTaxFileNumber());

                    col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_TAX_REFERENCE_NUMBER.getColumn();
                    rec.setData(col, getFormatData(col,getTaxRefernceNumber(taxRefFileNumber),false,"0"));

                    col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_WIFE_CODE.getColumn();
                    rec.setData(col, getFormatData(col,getWifeCode(taxRefFileNumber),false,"0"));

                    col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_EMPLOYEE_NAME.getColumn();
                    rec.setData(col, getFormatData(col,StringUtil.getFullName(CP39Obj.getUseraccount().getUser()),true," "));

                    col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_OLD_IC_NUMBER.getColumn();
                    rec.setData(col, getFormatData(col,getAlphaNumericData(CP39Obj.getOldIdentificationNumber()),true," "));

                    col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_NEW_IC_NUMBER.getColumn();
                    rec.setData(col, getFormatData(col,getAlphaNumericData(CP39Obj.getNewIdentificationNumber()),true," "));

                    col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_PASSPORT_NUMBER.getColumn();
                    rec.setData(col, getFormatData(col,getAlphaNumericData(CP39Obj.getPassportNumber()),true," "));

                    col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_COUNTRY_CODE.getColumn();
                    rec.setData(col, getFormatData(col,CP39Obj.getCountryCode(),false," "));

                    col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_MTD_AMOUNT.getColumn();

                    if(ph!=null){
                        rec.setData(col, getFormatData(col,getCurrencyInFormat(BigDecimal.valueOf(ph.getIncometaxAmount()).setScale(2).toString()),false,"0"));
                    }else {
                        rec.setData(col, getFormatData(col,getCurrencyInFormat("00.00"),false,"0"));
                    }
                    
                    col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_CP38_AMOUNT.getColumn();
                    if(CP39Obj.getDeductionAmountForCP38()!=null){

                        String CP38Amount = BigDecimal.valueOf(CP39Obj.getDeductionAmountForCP38()).setScale(2).toString();
                        rec.setData(col, getFormatData(col,getCurrencyInFormat(CP38Amount),false,"0"));

                        totalAmountForCP38 = totalAmountForCP38.add(BigDecimal.valueOf(CP39Obj.getDeductionAmountForCP38()).setScale(2));
                        totalRecordCountForCP38++;
                    }else {
                        rec.setData(col, getFormatData(col,"00",false,"0"));
                    }

                    HrmsCommonPayroll hrmsCommonPayroll = new HrmsCommonPayroll();

                    col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_EMPLOYEE_OR_SALARY_NUMBER.getColumn();
                    rec.setData(col, getFormatData(col,getAlphaNumericData(hrmsCommonPayroll.getEmployeeIdFormat(CP39Obj.getUseraccount(), profileHandlerDAO)),true," "));

                    listData.add(rec);

                    recordCreated = true;
                    
                    
                }

                if(ph!=null){

                    if(!recordCreated){

                            
                            col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_TAX_REFERENCE_NUMBER.getColumn();
                            rec.setData(col, getFormatData(col,"0000",false,"0"));

                            col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_WIFE_CODE.getColumn();
                            rec.setData(col, getFormatData(col,"0",false,""));

                            col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_EMPLOYEE_NAME.getColumn();
                            rec.setData(col, getFormatData(col,StringUtil.getFullName(ph.getUser()),true," "));

                            col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_OLD_IC_NUMBER.getColumn();
                            rec.setData(col, getFormatData(col,"",true," "));

                            col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_NEW_IC_NUMBER.getColumn();
                            rec.setData(col, getFormatData(col,"",true," "));

                            col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_PASSPORT_NUMBER.getColumn();
                            rec.setData(col, getFormatData(col,"",true," "));

                            col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_COUNTRY_CODE.getColumn();
                            rec.setData(col, getFormatData(col,"",false,"0"));

                            col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_MTD_AMOUNT.getColumn();

                            rec.setData(col, getFormatData(col,getCurrencyInFormat(BigDecimal.valueOf(ph.getIncometaxAmount()).setScale(2).toString()),false,"0"));

                            col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_CP38_AMOUNT.getColumn();
                            
                            rec.setData(col, getFormatData(col,"",false,"0"));

                            HrmsCommonPayroll hrmsCommonPayroll = new HrmsCommonPayroll();

                            col = MTDData.MTD_DATA_REPORT_COLS.DETAIL_EMPLOYEE_OR_SALARY_NUMBER.getColumn();
                            Useraccount uacc = (Useraccount) kwlCommonTablesDAO.getObject("com.krawler.common.admin.Useraccount", ph.getUser().getUserID());
                            rec.setData(col, getFormatData(col,getAlphaNumericData(hrmsCommonPayroll.getEmployeeIdFormat(uacc, profileHandlerDAO)),true," "));


                         listData.add(rec);

                    }
                    
                    totalAmountForMTD = totalAmountForMTD.add(BigDecimal.valueOf(ph.getIncometaxAmount()).setScale(2));
                    totalRecordCountForMTD++;
                }


            }

            detailSection.setData(listData);

            BankPayrollReportSection headerSection = getHeaderSectionForMTDData(report, companyInfo, totalAmountForMTD, totalRecordCountForMTD, totalAmountForCP38, totalRecordCountForCP38, yr, month);

            report.generate();
            
            String cnt= file.getFileContent();
            col = MTDData.MTD_DATA_REPORT_COLS.HEADER_MONTH_OF_DEDUCTION.getColumn();
            String fileName = getFormatData(col,String.valueOf(month+1),false,"0")+"_"+yr;
            if(companyInfo==null || companyInfo.getEmployerno()==null){
                fileName = "xxxxxxxxxx"+fileName+".txt";
            }else {
                col = MTDData.MTD_DATA_REPORT_COLS.HEADER_EMPLOYER_NO.getColumn();
                fileName = getFormatData(col,getNumericData(companyInfo.getEmployerno()),false,"0")+fileName+".txt";
            }
            response.setHeader("Content-Disposition", "attachment;filename="+fileName);
            response.getOutputStream().write(cnt.getBytes());
            response.getOutputStream().flush();

        } catch(IOException ex){
            ex.printStackTrace();
        }catch(ParseException ex){
            ex.printStackTrace();
        }catch(SessionExpiredException ex){
            ex.printStackTrace();
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    public BankPayrollReportSection getHeaderSectionForMTDData (MTDData report, MalaysiaCompanyForm companyInfo, BigDecimal totalAmountForMTD, int totalRecordCountForMTD, BigDecimal totalAmountForCP38, int totalRecordCountForCP38, String year, int month) {

        BankPayrollReportSection headerSection = report.getHeaderSection();

            List<Record> listData = new ArrayList<Record>();
            Record rec = new Record();

            Column col = MTDData.MTD_DATA_REPORT_COLS.HEADER_RECORD_TYPE.getColumn();
            rec.setData(col, "H");

            col = MTDData.MTD_DATA_REPORT_COLS.HEADER_EMPLOYER_NO_HQ.getColumn();
            String employerHQ = "";
            if(companyInfo!=null && companyInfo.getEmployernoHQ()!=null){
                employerHQ = companyInfo.getEmployernoHQ();
            }
            rec.setData(col, getFormatData(col,getNumericData(employerHQ),false,"0"));

            col = MTDData.MTD_DATA_REPORT_COLS.HEADER_EMPLOYER_NO.getColumn();
            String employer = "";
            if(companyInfo!=null && companyInfo.getEmployerno()!=null){
                employer = companyInfo.getEmployerno();
            }
            rec.setData(col, getFormatData(col,getNumericData(employer),false,"0"));

            col = MTDData.MTD_DATA_REPORT_COLS.HEADER_YEAR_OF_DEDUCTION.getColumn();
            rec.setData(col, getFormatData(col,year,false,""));

            col = MTDData.MTD_DATA_REPORT_COLS.HEADER_MONTH_OF_DEDUCTION.getColumn();
            rec.setData(col, getFormatData(col,String.valueOf(month+1),false,"0"));

            col = MTDData.MTD_DATA_REPORT_COLS.HEADER_TOTAL_MTD_AMOUNT.getColumn();
            rec.setData(col, getFormatData(col,getCurrencyInFormat(totalAmountForMTD.setScale(2).toString()),false,"0"));

            col = MTDData.MTD_DATA_REPORT_COLS.HEADER_TOTAL_MTD_RECORDS.getColumn();
            rec.setData(col, getFormatData(col,String.valueOf(totalRecordCountForMTD),false,"0"));

            col = MTDData.MTD_DATA_REPORT_COLS.HEADER_TOTAL_CP38_AMOUNT.getColumn();
            rec.setData(col, getFormatData(col,getCurrencyInFormat(totalAmountForCP38.setScale(2).toString()),false,"0"));

            col = MTDData.MTD_DATA_REPORT_COLS.HEADER_TOTAL_CP38_RECORDS.getColumn();
            rec.setData(col, getFormatData(col,String.valueOf(totalRecordCountForCP38),false,"0"));


            listData.add(rec);
            headerSection.setData(listData);


        return headerSection;
    }

    public String getFormatData (Column col, String data, boolean leftJustify, String justifyBy){

        StringBuffer formatData = new StringBuffer(data);

        StringBuffer justifyStringBuff = new StringBuffer();

        int colLength = col.getLength();
        
        if(data!=null){

            int dataLength = data.length();

            if(colLength>=dataLength){

                for(int i=dataLength ; i< colLength; i++){

                    justifyStringBuff.append(justifyBy);
                }
                
                if(leftJustify){
                    formatData.append(justifyStringBuff);
                    data = formatData.toString();
                } else {
                    justifyStringBuff.append(formatData);
                    data = justifyStringBuff.toString();
                }
                
            }else {

                data = data.substring(0, colLength);
            }
        }

        return data;
    }

    public String getCurrencyInFormat (String currency){
        if(!StringUtil.isNullOrEmpty(currency)){
            currency = currency.replaceAll("\\.", "");
        }else {
            currency ="";
        }
        
        return currency;
    }

    public String getAlphaNumericData (String data){
        if(!StringUtil.isNullOrEmpty(data)){
            data = data.replaceAll("[^a-zA-Z0-9]", "");
        } else {
            data ="";
        }
        
        return data;
    }

    public String getNumericData (String data){
        if(!StringUtil.isNullOrEmpty(data)){
            data = data.replaceAll("[^0-9]", "");
        } else {
            data ="";
        }
        
        return data;
    }

    public String getTaxRefernceNumber (String data){
        if(!StringUtil.isNullOrEmpty(data))
            data = data.substring(0,(data.length()-1));
        return data;
    }

    public String getWifeCode (String data){
        if(!StringUtil.isNullOrEmpty(data))
            data = data.substring((data.length()-1),data.length());
        return data;
    }

    public PayrollHistory getPayrollHistory (String userid, List<PayrollHistory> phList){
        PayrollHistory ph =null;

            for(PayrollHistory p : phList){

                if(StringUtil.equal(userid, p.getUser().getUserID())){
                    ph = p;
                    break;
                }
            }

        return ph;
    }

    public MalaysiaFormCP39 getCP39Object (String userid, List<MalaysiaFormCP39> listCP39){
        MalaysiaFormCP39 cp39Obj =null;

            for(MalaysiaFormCP39 cp39 : listCP39){

                if(StringUtil.equal(userid, cp39.getUseraccount().getUserID())){
                    cp39Obj = cp39;
                    break;
                }
            }

        return cp39Obj;
    }
 
}
