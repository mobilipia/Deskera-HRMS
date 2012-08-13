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

package com.krawler.spring.hrms.payroll.statutoryform;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.hrms.common.HrmsCommonPayroll;
import com.krawler.hrms.common.HrmsPayrollConstants;
import com.krawler.hrms.common.MalaysianIncomeTaxConstants;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.hrms.payroll.hrmsPayrollDAO;
import com.krawler.spring.hrms.payroll.incometax.MalaysianIncomeTax;
import com.krawler.spring.hrms.payroll.incometax.MalaysianIncomeTaxDAO;
import com.krawler.spring.hrms.payroll.incometax.MalaysianIncomeTaxUtil;
import com.krawler.spring.hrms.payroll.salaryslip.ExportSalarySlipService;
import com.krawler.spring.hrms.payroll.statutoryform.amanahsahamnasional.AmanahSahamNasional;
import com.krawler.spring.hrms.payroll.statutoryform.amanahsahamnasional.AmanahSahamNasionalEmployee;
import com.krawler.spring.hrms.payroll.statutoryform.cp21.CP21;
import com.krawler.spring.hrms.payroll.statutoryform.cp39.CP39;
import com.krawler.spring.hrms.payroll.statutoryform.cp39.CP39Employee;
import com.krawler.spring.hrms.payroll.statutoryform.cp39A.CP39A;
import com.krawler.spring.hrms.payroll.statutoryform.cp39A.CP39AEmployee;
import com.krawler.spring.hrms.payroll.statutoryform.ea.EA;
import com.krawler.spring.hrms.payroll.statutoryform.hrdlevy.HRDLevy;
import com.krawler.spring.hrms.payroll.statutoryform.hrdlevy.HRDLevyEmployee;
import com.krawler.spring.hrms.payroll.statutoryform.pcb2.PCB2;
import com.krawler.spring.hrms.payroll.statutoryform.pcb2.PCB2Employee;
import com.krawler.spring.hrms.payroll.statutoryform.tabunghaji.TabungHaji;
import com.krawler.spring.hrms.payroll.statutoryform.tabunghaji.TabungHajiEmployee;
import com.krawler.spring.hrms.payroll.statutoryform.tp2.TP2;
import com.krawler.spring.hrms.payroll.statutoryform.tp3.TP3;
import com.krawler.spring.hrms.payroll.statutoryform.tp1.TP1;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import masterDB.ComponentResourceMappingHistory;
import masterDB.MalaysiaCompanyForm;
import masterDB.MalaysiaFormAmanahSahamNasional;
import masterDB.MalaysiaFormCP21;
import masterDB.MalaysiaFormCP39;
import masterDB.MalaysiaFormCP39A;
import masterDB.MalaysiaFormEA;
import masterDB.MalaysiaFormHRDLevy;
import masterDB.MalaysiaFormTP1;
import masterDB.MalaysiaFormPCB2;
import masterDB.MalaysiaFormTP2;
import masterDB.MalaysiaFormTP3;
import masterDB.MalaysiaFormTabungHaji;
import masterDB.MalaysianUserIncomeTaxInfo;
import masterDB.MalaysianUserTaxBenefits;
import masterDB.MalaysianUserTaxComponentHistory;
import masterDB.PayrollHistory;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author krawler
 */
public class MalaysianStatutoryFormController extends MultiActionController implements MessageSourceAware{

    private sessionHandlerImpl sessionHandlerImplObj;
    private hrmsPayrollDAO hrmsPayrollDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private HibernateTransactionManager txnManager;
    private MalaysianStatutoryFormDAO malaysianStatutoryFormDAO;
    private profileHandlerDAO profileHandlerDAO;
    private MalaysianIncomeTaxDAO malaysianIncomeTaxDAO;
    private ExportSalarySlipService exportSalarySlipService;
    private MessageSource messageSource;
    
    public void setMalaysianIncomeTaxDAO(MalaysianIncomeTaxDAO malaysianIncomeTaxDAO) {
		this.malaysianIncomeTaxDAO = malaysianIncomeTaxDAO;
	}
    
    public void setHrmsPayrollDAO(hrmsPayrollDAO hrmsPayrollDAOObj1) {
        this.hrmsPayrollDAOObj = hrmsPayrollDAOObj1;
    }

    public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setMalaysianStatutoryFormDAO(MalaysianStatutoryFormDAO malaysianStatutoryFormDAO) {
        this.malaysianStatutoryFormDAO = malaysianStatutoryFormDAO;
    }
    
    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAO) {
        this.profileHandlerDAO = profileHandlerDAO;
    }
    
    public void setExportSalarySlipService(ExportSalarySlipService exportSalarySlipService) {
		this.exportSalarySlipService = exportSalarySlipService;
	}
    
    public ModelAndView getAmanahSahamNasional(HttpServletRequest request, HttpServletResponse response) throws SessionExpiredException, ServiceException {
        ModelAndView modelAndView = null;

        String mnth = request.getParameter("month");
        String year = request.getParameter("year");
        String title = messageSource.getMessage("hrms.payroll.contribution.parmodalan.nasional.berhad", null, RequestContextUtils.getLocale(request))+" ";

        int monthInt = 0;
        if(!StringUtil.isNullOrEmpty(mnth)){
            monthInt = Integer.parseInt(mnth);
        }
        int yearInt = 0;
        if(!StringUtil.isNullOrEmpty(year)){
            yearInt = Integer.parseInt(year);
        }
        String month = getMonthName(monthInt, request);
        
        title = title+month+", "+year;
        String companyid = sessionHandlerImplObj.getCompanyid(request);
        Company company = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", companyid);

        MalaysiaCompanyForm asnCompany = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(companyid, monthInt, yearInt);

        List<AmanahSahamNasional> sampleList = new ArrayList<AmanahSahamNasional>(1);
        List<AmanahSahamNasionalEmployee> amanahSahamNasionalEmployeeLists = new ArrayList<AmanahSahamNasionalEmployee>(1);
        AmanahSahamNasional report = new AmanahSahamNasional();
        AmanahSahamNasionalEmployee amanahSahamNasionalEmployee1 = new AmanahSahamNasionalEmployee();
        if(asnCompany!=null){
                
                report.setAddress(company.getAddress());
                report.setChequeNo(asnCompany.getAmanahSahamNasionalChequeno());
                report.setCompanyName(company.getCompanyName());
                report.setPaymentBy(asnCompany.getAmanahSahamNasionalPaymentType()==1?messageSource.getMessage("hrms.payroll.cash", null, RequestContextUtils.getLocale(request)):messageSource.getMessage("hrms.payroll.cheque", null, RequestContextUtils.getLocale(request)));
                report.setPreparedBy(StringUtil.getFullName(asnCompany.getAmanahSahamNasionalUser()));
                report.setTelephone(asnCompany.getAmanahSahamNasionalUser().getContactNumber());
                report.setTitle(title.toString());
                


                List lst = getAllUsers(request);

                BigDecimal amnt = BigDecimal.ZERO;
                int no =1;
                for(int i=0; i< lst.size(); i++){
                    User usr = (User) lst.get(i);

                    MalaysiaFormAmanahSahamNasional asn = malaysianStatutoryFormDAO.getUserAmanahSahamNasional(usr.getUserID(),monthInt, yearInt);

                    if(asn!=null){
                        AmanahSahamNasionalEmployee amanahSahamNasionalEmployee = new AmanahSahamNasionalEmployee();

                        amanahSahamNasionalEmployee.setAccountNo(String.valueOf(asn.getAccountno()));
                        amanahSahamNasionalEmployee.setAmount(BigDecimal.valueOf(asn.getAmount()).setScale(2).toString());
                        amanahSahamNasionalEmployee.setIcno(String.valueOf(asn.getIcno()));
                        amanahSahamNasionalEmployee.setName(StringUtil.getFullName(usr));
                        amanahSahamNasionalEmployee.setNo(String.valueOf(no));

                        amanahSahamNasionalEmployeeLists.add(amanahSahamNasionalEmployee);

                        no++;

                        BigDecimal amt = BigDecimal.valueOf(asn.getAmount());

                        amnt = amnt.add(amt);

                    }

                }

                
                amanahSahamNasionalEmployee1.setAccountNo("");
                amanahSahamNasionalEmployee1.setAmount(String.valueOf(amnt.setScale(2)));
                amanahSahamNasionalEmployee1.setIcno("");
                amanahSahamNasionalEmployee1.setName(messageSource.getMessage("hrms.timesheet.total", null, RequestContextUtils.getLocale(request)));
                amanahSahamNasionalEmployee1.setNo("");

                
        }
        sampleList.add(report);
        amanahSahamNasionalEmployeeLists.add(amanahSahamNasionalEmployee1);
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("datasource", new JRBeanCollectionDataSource(sampleList));
        parameterMap.put("SubReportData",  new JRBeanCollectionDataSource(amanahSahamNasionalEmployeeLists));
        
        modelAndView = new ModelAndView("pdfAmanahSahamNasional", parameterMap);
        return modelAndView;
    }

    public ModelAndView getTabungHaji(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = null;
        try{
            String mnth = request.getParameter("month");
            String year = request.getParameter("year");
            String title = messageSource.getMessage("hrms.payroll.contribution.tabung.haji", null, RequestContextUtils.getLocale(request))+" ";

            int monthInt = 0;
            if(!StringUtil.isNullOrEmpty(mnth)){
                monthInt = Integer.parseInt(mnth);
            }
            int yearInt = 0;
            if(!StringUtil.isNullOrEmpty(year)){
                yearInt = Integer.parseInt(year);
            }
            String month = getMonthName(monthInt, request);

            title = title+month+", "+year;
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            Company company = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", companyid);

            MalaysiaCompanyForm companyForm = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(companyid, monthInt, yearInt);

            List<TabungHaji> sampleList = new ArrayList<TabungHaji>(1);
            List<TabungHajiEmployee> tabunghajiEmployeeLists = new ArrayList<TabungHajiEmployee>(1);
            TabungHajiEmployee tabunghajiEmployee1 = new TabungHajiEmployee();
            
            TabungHaji report = new TabungHaji();
            if(companyForm!=null){
                report.setAddress(company.getAddress());
                report.setChequeNo(companyForm.getTabungHajiChequeno());
                report.setCompanyName(company.getCompanyName());
                report.setPaymentBy(companyForm.getTabungHajiPaymentType()==1?messageSource.getMessage("hrms.payroll.cash", null, RequestContextUtils.getLocale(request)):messageSource.getMessage("hrms.payroll.cheque", null, RequestContextUtils.getLocale(request)));
                report.setPreparedBy(StringUtil.getFullName(companyForm.getTabungHajiUser()));
                report.setTelephone(companyForm.getTabungHajiUser().getContactNumber());
                report.setTitle(title.toString());
                


                List lst = getAllUsers(request);

                
                BigDecimal amnt = BigDecimal.ZERO;
                int no =1;
                for(int i=0; i< lst.size(); i++){
                    User usr = (User) lst.get(i);

                    MalaysiaFormTabungHaji th = malaysianStatutoryFormDAO.getUserTabungHaji(usr.getUserID(),monthInt,yearInt);

                    if(th!=null){
                        TabungHajiEmployee tabunghajiEmployee = new TabungHajiEmployee();

                        tabunghajiEmployee.setAccountNo(String.valueOf(th.getAccountno()));
                        tabunghajiEmployee.setAmount(BigDecimal.valueOf(th.getAmount()).setScale(2).toString());
                        tabunghajiEmployee.setIcno(String.valueOf(th.getIcno()));
                        tabunghajiEmployee.setName(StringUtil.getFullName(usr));
                        tabunghajiEmployee.setNo(String.valueOf(no));

                        tabunghajiEmployeeLists.add(tabunghajiEmployee);

                        no++;

                        BigDecimal amt = BigDecimal.valueOf(th.getAmount());

                        amnt = amnt.add(amt);

                    }

                }

                
                tabunghajiEmployee1.setAccountNo("");
                tabunghajiEmployee1.setAmount(String.valueOf(amnt.setScale(2)));
                tabunghajiEmployee1.setIcno("");
                tabunghajiEmployee1.setName(messageSource.getMessage("hrms.timesheet.total", null, RequestContextUtils.getLocale(request)));
                tabunghajiEmployee1.setNo("");

                
            }

            sampleList.add(report);
            tabunghajiEmployeeLists.add(tabunghajiEmployee1);
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("datasource", new JRBeanCollectionDataSource(sampleList));
            parameterMap.put("SubReportData",  new JRBeanCollectionDataSource(tabunghajiEmployeeLists));

            modelAndView = new ModelAndView("pdfTabungHaji", parameterMap);
        } catch (Exception e){
            e.printStackTrace();
        }
        return modelAndView;
    }

    public ModelAndView getCP21(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = null;
        try{

            String mnth = request.getParameter("month");
            String year = request.getParameter("year");
            int monthInt = Integer.parseInt(mnth);
            int yearInt = Integer.parseInt(year);

            String companyid = sessionHandlerImplObj.getCompanyid(request);
            Company company = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", companyid);
            User user = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", request.getParameter("userid"));
            Useraccount useraccount = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
            
            MalaysiaCompanyForm companyForm = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(companyid, monthInt, yearInt);
            MalaysiaFormCP21 cp = malaysianStatutoryFormDAO.getUserCP21(user.getUserID(), monthInt, yearInt);
            if(cp!=null){

                List<CP21> sampleList = new ArrayList<CP21>(1);
                CP21 report = new CP21();
                report.setEmployeeFileRefNo(cp.getEmpfilerefno());
                report.setEmployerFileRefNo(companyForm.getCp21EmployerFileRefno());
                report.setEmployerPhoneNo(company.getPhoneNumber());
                report.setFromAddress(company.getCompanyName());
                report.setToAddress(" ");// To address
                report.setExpectedDateToLeaveMalaysia(getDateForForms(cp.getExpDateToLeave()));
                report.setEmployeeName(StringUtil.getFullName(user));
                report.setEmployeePassportNo(cp.getPassportno());
                report.setEmployeeAddress(user.getAddress());
                report.setEmploymentNature(cp.getNatureofemployment()==1?messageSource.getMessage("hrms.common.Permanent", null, RequestContextUtils.getLocale(request)):messageSource.getMessage("hrms.common.ad.hoc", null, RequestContextUtils.getLocale(request)));
                report.setReasonForDeparture(cp.getReasonForDeparture());
                report.setCorrespondenceAddress(cp.getCorresspondenceAddress());
                report.setProbableDateOfReturn(getDateForForms(cp.getDateOfReturn()));
                report.setSalaryPeriod(getPeriodFromDateRange(cp.getSalaryFrom(),cp.getSalaryTo()));
                report.setSalaryRM(getRMFromAmount(getAmount(cp.getSalaryAmount())));
                report.setSalarySen(getSenFromAmount(getAmount(cp.getSalaryAmount())));
                report.setLeavePeriod(getPeriodFromDateRange(cp.getLeavePayFrom(), cp.getLeavePayTo()));
                report.setLeaveRM(getRMFromAmount(getAmount(cp.getLeavePayAmount())));
                report.setLeaveSen(getSenFromAmount(getAmount(cp.getLeavePayAmount())));
                report.setBonusPeriod(getPeriodFromDateRange(cp.getBonusFrom(), cp.getBonusTo()));
                report.setBonusRM(getRMFromAmount(getAmount(cp.getBonusAmount())));
                report.setBonusSen(getSenFromAmount(getAmount(cp.getBonusAmount())));
                report.setGratuityPeriod(getPeriodFromDateRange(cp.getGratuityFrom(), cp.getGratuityTo()));
                report.setGratuityRM(getRMFromAmount(getAmount(cp.getGratuityAmount())));
                report.setGratuitySen(getSenFromAmount(getAmount(cp.getGratuityAmount())));
                report.setStateAllowancesPeriod(getPeriodFromDateRange(cp.getAllowanceFrom(), cp.getAllowanceTo()));
                report.setStateAllowancesRM(getRMFromAmount(getAmount(cp.getAllowanceAmount())));
                report.setStateAllowancesSen(getSenFromAmount(getAmount(cp.getAllowanceAmount())));
                report.setPensionPeriod(getPeriodFromDateRange(cp.getPensionFrom(), cp.getPensionTo()));
                report.setPensionRM(getRMFromAmount(getAmount(cp.getPensionAmount())));
                report.setPensionSen(getSenFromAmount(getAmount(cp.getPensionAmount())));
                report.setResidenceValuebyEmployerPeriod(getPeriodFromDateRange(cp.getResidenceFrom(), cp.getResidenceTo()));
                report.setResidenceValuebyEmployerRM(getRMFromAmount(getAmount(cp.getResidenceAmount())));
                report.setResidenceValuebyEmployerSn(getSenFromAmount(getAmount(cp.getResidenceAmount())));
                report.setAllowanceInKindPeriod(getPeriodFromDateRange(cp.getAllowanceinkindFrom(), cp.getAllowanceinkindTo()));
                report.setAllowanceInKindRM(getRMFromAmount(getAmount(cp.getAllowanceinkindAmount())));
                report.setAllowanceInKindSen(getSenFromAmount(getAmount(cp.getAllowanceinkindAmount())));
                report.setEmployeePFPeriod(getPeriodFromDateRange(cp.getProvidentFundFrom(), cp.getProvidentFundTo()));
                report.setEmployeePFRM(getRMFromAmount(getAmount(cp.getProvidentFundAmount())));
                report.setEmployeePFSen(getSenFromAmount(getAmount(cp.getProvidentFundAmount())));
                report.setDueAmount(getAmount(cp.getAmountdue()));
                String paymentType =messageSource.getMessage("hrms.common.none", null, RequestContextUtils.getLocale(request));
                if(cp.getNatureofpayment()==1){
                    paymentType=messageSource.getMessage("hrms.payroll.cash", null, RequestContextUtils.getLocale(request));
                }else if(cp.getNatureofpayment()==2){
                    paymentType=messageSource.getMessage("hrms.payroll.cheque", null, RequestContextUtils.getLocale(request));
                }
                report.setPaymentNature(paymentType);
                report.setPaymentDate(getDateForForms(cp.getPaymentDate()));
                report.setPaidAmount(getAmount(cp.getAmountToBePaid()));
                report.setCurrentDate(getDateForForms(cp.getDateOfSubmission()));
                report.setDesignation(useraccount.getDesignationid()!=null?useraccount.getDesignationid().getValue():"");

                sampleList.add(report);

                JRBeanCollectionDataSource jRBeanCollectionDataSource = new JRBeanCollectionDataSource(sampleList);

                Map<String, Object> parameterMap = new HashMap<String, Object>();
                parameterMap.put("datasource", jRBeanCollectionDataSource);
                modelAndView = new ModelAndView("pdfCPTwnetyOne", parameterMap);
            }

        }catch(Exception ex) {
          ex.printStackTrace();
        }
        
        
        return modelAndView;
    }

    public String getPeriodFromDateRange(Date d1, Date d2){
        String period ="";
        String from="";
        String to="";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(d1!=null){

            from= sdf.format(d1);
            period=from;
        }
        if(d2!=null){
            to = sdf.format(d2);
            period=to;
        }
        if(!StringUtil.isNullOrEmpty(to) && !StringUtil.isNullOrEmpty(from)){
            period = from+" to "+to;
        }

        return period;
    }

    public String getAmount (double amount){

        return BigDecimal.valueOf(amount).setScale(2).toString();

    }
    public String getRMFromAmount(String amount){
        
        String [] amnt = amount.split("\\.");
        return amnt[0];
    }
    public String getSenFromAmount(String amount){

        String [] amnt = amount.split("\\.");
        return amnt[1];
    }
    @SuppressWarnings("empty-statement")
     public ModelAndView getHRDLevy(HttpServletRequest request, HttpServletResponse response) throws SessionExpiredException, ServiceException {
        ModelAndView modelAndView = null;


        String mnth = request.getParameter("month");
        String year = request.getParameter("year");
        int monthInt = Integer.parseInt(mnth);
        int yearInt = Integer.parseInt(year);
        
        String companyid = sessionHandlerImplObj.getCompanyid(request);
        Company company = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", companyid);

        MalaysiaCompanyForm asnCompany = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(companyid, monthInt, yearInt);

        List<HRDLevy> sampleList = new ArrayList<HRDLevy>(1);

        HRDLevy report = new HRDLevy();
        report.setChequeNo(asnCompany.getHrdLevyChequeno());
        report.setCompanyName(company.getCompanyName());
        report.setPaymentBy(asnCompany.getHrdLevyPaymentType()==1?messageSource.getMessage("hrms.payroll.cash", null, RequestContextUtils.getLocale(request)):messageSource.getMessage("hrms.payroll.cheque", null, RequestContextUtils.getLocale(request)));
        report.setAddress(company.getAddress());
        sampleList.add(report);


        List lst = getAllUsers(request);

        List<HRDLevyEmployee> hRDLevyEmployeeLists = new ArrayList<HRDLevyEmployee>(1);

        BigDecimal baseAmnt = BigDecimal.ZERO;
        BigDecimal netAmnt = BigDecimal.ZERO;
        BigDecimal otherAmnt = BigDecimal.ZERO;
        BigDecimal hrdamnt = BigDecimal.ZERO;
        
        double amnt =0;
        int no =1;
        for(int i=0; i< lst.size(); i++){
            User usr = (User) lst.get(i);
            MalaysiaFormHRDLevy hl = malaysianStatutoryFormDAO.getUserHRDLevy(usr.getUserID(),monthInt,yearInt);

            if(hl!=null) {

                HRDLevyEmployee hRDLevyEmployee = new HRDLevyEmployee();
                hRDLevyEmployee.setBaseSalary(BigDecimal.valueOf(hl.getBaseSalary()).setScale(2).toString());
                hRDLevyEmployee.setEmployeeName(StringUtil.getFullName(usr));
                hRDLevyEmployee.setHrd(BigDecimal.valueOf(hl.getHrdLevy()).setScale(2).toString());
                hRDLevyEmployee.setNetSalary(BigDecimal.valueOf(hl.getNetSalary()).setScale(2).toString());
                hRDLevyEmployee.setNo(String.valueOf(no));
                hRDLevyEmployee.setOthers(BigDecimal.valueOf(hl.getOthers()).setScale(2).toString());


                hRDLevyEmployeeLists.add(hRDLevyEmployee);

                no++;
                BigDecimal bamt = BigDecimal.valueOf(hl.getBaseSalary());
                baseAmnt = baseAmnt.add(bamt);

                BigDecimal namt = BigDecimal.valueOf(hl.getNetSalary());
                netAmnt = netAmnt.add(namt);

                BigDecimal oamt = BigDecimal.valueOf(hl.getOthers());
                otherAmnt = otherAmnt.add(oamt);

                BigDecimal hamt = BigDecimal.valueOf(hl.getHrdLevy());
                hrdamnt = hrdamnt.add(hamt);
            }

        }

        HRDLevyEmployee hRDLevyEmployee = new HRDLevyEmployee();
        hRDLevyEmployee.setNo("");
        hRDLevyEmployee.setEmployeeName(messageSource.getMessage("hrms.timesheet.total", null, RequestContextUtils.getLocale(request)));
        hRDLevyEmployee.setBaseSalary(String.valueOf(baseAmnt.setScale(2)));;
        hRDLevyEmployee.setOthers(String.valueOf(netAmnt.setScale(2)));
        hRDLevyEmployee.setNetSalary(String.valueOf(otherAmnt.setScale(2)));
        hRDLevyEmployee.setHrd(String.valueOf(hrdamnt.setScale(2)));
        hRDLevyEmployeeLists.add(hRDLevyEmployee);        

        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("datasource", new JRBeanCollectionDataSource(sampleList));
        parameterMap.put("SubReportData",  new JRBeanCollectionDataSource(hRDLevyEmployeeLists));

        modelAndView = new ModelAndView("pdfHRDLevy", parameterMap);
        return modelAndView;
    }
    
    public List<User> getAllUsers(HttpServletRequest request) {

        List lst = null;
        try {
            StringBuffer userList = new StringBuffer();
            String companyid =sessionHandlerImplObj.getCompanyid(request);
            String searchText ="";
            Integer frequency = Integer.parseInt(request.getParameter("frequency"));
            String start="";
            String limit="";

            KwlReturnObject users = hrmsPayrollDAOObj.getPayrollUserList(userList,companyid,searchText,frequency,start,limit);
            lst = users.getEntityList();
        }catch(Exception ex) {
          ex.printStackTrace();
        }

        return lst;
    }

    public ModelAndView saveUserFormInformation(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            
            User user = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", request.getParameter("userid"));
            
            String mnth = request.getParameter("declarationMonth");
            String yr = request.getParameter("declarationYear");

            int month = 0;
        	int year = 0;
        	if(!StringUtil.isNullOrEmpty(mnth)){
        		month = Integer.parseInt(mnth);
        	}
        	if(!StringUtil.isNullOrEmpty(yr)){
        		year = Integer.parseInt(yr);
        	}
            
            saveAmanahSahamNasionalUserData(request, user, month, year);

            saveTabungHajiUserData(request, user, month, year);

            saveCP21UserData(request, user, month, year);

            saveHRDLevyUserData(request, user, month, year);

            saveTP1(request, user, month, year);
            saveTP2(request, user, month, year);
            saveTP3(request, user, month, year);
            saveCP39(request, user, month, year);
            saveCP39A(request, user, month, year);
            savePCB2(request, user, month, year);
            saveEA(request, user, month, year);
            jsonObject.put("success", true);
            jsonObject.put("valid", true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jsonObject.toString());
    }

    public void saveTP1(HttpServletRequest request, User user, int month, int year){
        try {
        	
        	Useraccount useraccount = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
        	
        	MalaysiaFormTP1 tp1 = malaysianStatutoryFormDAO.getEmployeeTP1(user.getUserID(), month, year);
        	if(tp1==null){
        		tp1 = new MalaysiaFormTP1();
        	}
        	tp1.setOldIdentificationNumber(request.getParameter("oldIdentificationNumberForTP1"));
        	tp1.setNewIdentificationNumber(request.getParameter("newIdentificationNumberForTP1"));
        	tp1.setArmyOrPoliceNumber(request.getParameter("passportNumberForTP1"));
        	tp1.setPassportNumber(request.getParameter("armyOrPoliceNumberForTP1"));
        	tp1.setIncomeTaxNumber(request.getParameter("incomeTaxNumberForTP1"));
        	tp1.setMonth(month);
        	tp1.setYear(year);
        	tp1.setUseraccount(useraccount);

            String statusString = request.getParameter("tp1authorize");

            int status = getStatusForStatutoryForm(statusString);
        	tp1.setAuthorizeStatus(status);
            
	        malaysianStatutoryFormDAO.saveMalaysiaFormTP1(tp1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveTP2(HttpServletRequest request, User user, int month, int year){
        try {
        	
        	Useraccount useraccount = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
        	String statusString = request.getParameter("tp2authorize");
            int status = getStatusForStatutoryForm(statusString);
        	MalaysiaFormTP2 tp2 = malaysianStatutoryFormDAO.getEmployeeTP2(user.getUserID(), month, year);
        	if(tp2==null){
        		tp2 = new MalaysiaFormTP2();
        	}
        	tp2.setOldIdentificationNumber(request.getParameter("oldIdentificationNumberForTP2"));
        	tp2.setNewIdentificationNumber(request.getParameter("newIdentificationNumberForTP2"));
        	tp2.setArmyOrPoliceNumber(request.getParameter("armyOrPoliceNumberForTP2"));
        	tp2.setPassportNumber(request.getParameter("passportNumberForTP2"));
        	tp2.setIncomeTaxLHDNNumber(request.getParameter("incomeTaxLHDNNumberForTP2"));
        	if(!StringUtil.isNullOrEmpty(request.getParameter("carForTP2"))){
        		tp2.setCar(Double.parseDouble(request.getParameter("carForTP2")));	
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("driverForTP2"))){
        		tp2.setDriver(Double.parseDouble(request.getParameter("driverForTP2")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("householdItemsForTP2"))){
        		tp2.setHouseholdItems(Double.parseDouble(request.getParameter("householdItemsForTP2")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("entertainmentForTP2"))){
        		tp2.setEntertainment(Double.parseDouble(request.getParameter("entertainmentForTP2")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("gardenerForTP2"))){
        		tp2.setGardener(Double.parseDouble(request.getParameter("gardenerForTP2")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("maidForTP2"))){
        		tp2.setMaid(Double.parseDouble(request.getParameter("maidForTP2")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("holidayAllowanceForTP2"))){
        		tp2.setHolidayAllowance(Double.parseDouble(request.getParameter("holidayAllowanceForTP2")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("membershipForTP2"))){
        		tp2.setMembership(Double.parseDouble(request.getParameter("membershipForTP2")));
        	}
        	tp2.setMonth(month);
        	tp2.setYear(year);
        	tp2.setUseraccount(useraccount);
            tp2.setAuthorizeStatus(status);
        	
	        malaysianStatutoryFormDAO.saveMalaysiaFormTP2(tp2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void saveTP3(HttpServletRequest request, User user, int month, int year){
        try {
        	
        	Useraccount useraccount = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
        	String statusString = request.getParameter("tp3authorize");

            int status = getStatusForStatutoryForm(statusString);

            MalaysiaFormTP3 tp3 = malaysianStatutoryFormDAO.getEmployeeTP3(user.getUserID(), month, year);
        	if(tp3==null){
        		tp3 = new MalaysiaFormTP3();
        	}
        	tp3.setPreviousEmployer1(request.getParameter("previousEmployer1ForTP3"));
        	tp3.setEmployerReferenceNo1(request.getParameter("employerReferenceNo1ForTP3"));
        	tp3.setPreviousEmployer2(request.getParameter("previousEmployer2ForTP3"));
        	tp3.setEmployerReferenceNo2(request.getParameter("employerReferenceNo2ForTP3"));
        	tp3.setOldIdentificationNumber(request.getParameter("oldIdentificationNumberForTP3"));
        	tp3.setNewIdentificationNumber(request.getParameter("newIdentificationNumberForTP3"));
        	tp3.setArmyOrPoliceNumber(request.getParameter("armyOrPoliceNumberForTP3"));
        	tp3.setPassportNumber(request.getParameter("passportNumberForTP3"));
        	tp3.setIncomeTaxFileNumber(request.getParameter("incomeTaxFileNumberForTP3"));
        	if(!StringUtil.isNullOrEmpty(request.getParameter("freeSampleProductOnDiscountForTP3"))){
        		tp3.setFreeSampleProductOnDiscount(Double.parseDouble(request.getParameter("freeSampleProductOnDiscountForTP3")));	
        	}
        	tp3.setEmployeeLongServiceAward(request.getParameter("employeeLongServiceAwardForTP3"));
        	if(!StringUtil.isNullOrEmpty(request.getParameter("totalContributionToKWSPForTP3"))){
        		tp3.setTotalContributionToKWSP(Double.parseDouble(request.getParameter("totalContributionToKWSPForTP3")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("tuitionfeesForTP3"))){
        		tp3.setTuitionfees(Double.parseDouble(request.getParameter("tuitionfeesForTP3")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("contributionToPrivatePensionForTP3"))){
        		tp3.setContributionToPrivatePension(Double.parseDouble(request.getParameter("contributionToPrivatePensionForTP3")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("totalAllowanceForTP3"))){
        		tp3.setTotalAllowance(Double.parseDouble(request.getParameter("totalAllowanceForTP3")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("otherAllowanceForTP3"))){
        		tp3.setOtherAllowance(Double.parseDouble(request.getParameter("otherAllowanceForTP3")));
        	}
        	tp3.setMonth(month);
        	tp3.setYear(year);
        	tp3.setUseraccount(useraccount);
            tp3.setAuthorizeStatus(status);
        	
	        malaysianStatutoryFormDAO.saveMalaysiaFormTP3(tp3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void saveCP39(HttpServletRequest request, User user, int month, int year){
        try {
        	
        	Useraccount useraccount = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
        	String statusString = request.getParameter("cp39authorize");
            
            int status = getStatusForStatutoryForm(statusString);

            MalaysiaFormCP39 cp39 = malaysianStatutoryFormDAO.getEmployeeCP39(user.getUserID(), month, year);
        	if(cp39==null){
        		cp39 = new MalaysiaFormCP39();
        	}
        	cp39.setIncomeTaxFileNumber(request.getParameter("incomeTaxFileNumberForCP39"));
        	cp39.setOldIdentificationNumber(request.getParameter("oldIdentificationNumberForCP39"));
        	cp39.setNewIdentificationNumber(request.getParameter("newIdentificationNumberForCP39"));
        	cp39.setPassportNumber(request.getParameter("passportNumberForCP39"));
        	cp39.setCountryCode(request.getParameter("countryCodeForCP39"));
        	if(!StringUtil.isNullOrEmpty(request.getParameter("deductionAmountForCP38ForCP39"))){
        		cp39.setDeductionAmountForCP38(Double.parseDouble(request.getParameter("deductionAmountForCP38ForCP39")));	
        	}
        	cp39.setMonth(month);
        	cp39.setYear(year);
        	cp39.setUseraccount(useraccount);
            cp39.setAuthorizeStatus(status);
        	
	        malaysianStatutoryFormDAO.saveMalaysiaFormCP39(cp39);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveCP39A(HttpServletRequest request, User user, int month, int year){
        try {
        	
        	Useraccount useraccount = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
        	String statusString = request.getParameter("cp39Aauthorize");
            
            int status = getStatusForStatutoryForm(statusString);

            MalaysiaFormCP39A cp39A = malaysianStatutoryFormDAO.getEmployeeCP39A(user.getUserID(), month, year);
        	if(cp39A==null){
        		cp39A = new MalaysiaFormCP39A();
        	}
        	cp39A.setIncomeTaxFileNumber(request.getParameter("incomeTaxFileNumberForCP39A"));
        	cp39A.setOldIdentificationNumber(request.getParameter("oldIdentificationNumberForCP39A"));
        	cp39A.setNewIdentificationNumber(request.getParameter("newIdentificationNumberForCP39A"));
        	cp39A.setPassportNumber(request.getParameter("passportNumberForCP39A"));
        	cp39A.setCountryCode(request.getParameter("countryCodeForCP39A"));
        	if(!StringUtil.isNullOrEmpty(request.getParameter("deductionAmountForCP38ForCP39A"))){
        		cp39A.setDeductionAmountForCP38(Double.parseDouble(request.getParameter("deductionAmountForCP38ForCP39A")));	
        	}
        	cp39A.setMonth(month);
        	cp39A.setYear(year);
        	cp39A.setUseraccount(useraccount);
            cp39A.setAuthorizeStatus(status);
        	
	        malaysianStatutoryFormDAO.saveMalaysiaFormCP39A(cp39A);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void savePCB2(HttpServletRequest request, User user, int month, int year){
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	String statusString = request.getParameter("pcb2authorize");
            
            int status = getStatusForStatutoryForm(statusString);

            Useraccount useraccount = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
        	
        	MalaysiaFormPCB2 pcb2 = malaysianStatutoryFormDAO.getEmployeePCB2(user.getUserID(), month, year);
        	if(pcb2==null){
        		pcb2 = new MalaysiaFormPCB2();
        	}
        	
        	if(!StringUtil.isNullOrEmpty(request.getParameter("deductionAmountCP38ForPCB2"))){
        		pcb2.setDeductionAmountForCP38(Double.parseDouble(request.getParameter("deductionAmountCP38ForPCB2")));	
        	}
        	pcb2.setTaxResitForPCB(request.getParameter("taxResitPCBForPCB2"));
        	if(!StringUtil.isNullOrEmpty(request.getParameter("taxResitPCBDateForPCB2"))){
        		pcb2.setTaxResitForPCBDate(sdf.parse(request.getParameter("taxResitPCBDateForPCB2")));
        	}
        	pcb2.setTaxResitForCP38(request.getParameter("taxResitCP38ForPCB2"));
        	if(!StringUtil.isNullOrEmpty(request.getParameter("taxResitCP38DateForPCB2"))){
        		pcb2.setTaxResitForCP38Date(sdf.parse(request.getParameter("taxResitCP38DateForPCB2")));
        	}
        	pcb2.setNewIdentificationNumber(request.getParameter("newIdentificationNumberForPCB2"));
        	pcb2.setIncomeTaxFileNumber(request.getParameter("incomeTaxFileNumberForPCB2"));
        	pcb2.setMonth(month);
        	pcb2.setYear(year);
        	pcb2.setUseraccount(useraccount);
            pcb2.setAuthorizeStatus(status);
        	
	        malaysianStatutoryFormDAO.saveMalaysiaFormPCB2(pcb2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void saveEA(HttpServletRequest request, User user, int month, int year){
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	String statusString = request.getParameter("eaAauthorize");
            
            int status = getStatusForStatutoryForm(statusString);

            Useraccount useraccount = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", user.getUserID());
        	
        	MalaysiaFormEA ea = malaysianStatutoryFormDAO.getEmployeeEA(user.getUserID(), month, year);
        	if(ea==null){
        		ea = new MalaysiaFormEA();
        	}
        	
        	ea.setSerialNumber(request.getParameter("serialNumberForEA"));
        	ea.setEmployerERefNumber(request.getParameter("employerERefNumberForEA"));
        	ea.setIncomeTaxFileNumber(request.getParameter("incomeTaxFileNumberForEA"));
        	ea.setIncomeTaxBranch(request.getParameter("incomeTaxBranchForEA"));
        	ea.setOldIdentificationNumber(request.getParameter("oldIdentificationNumberForEA"));
        	ea.setNewIdentificationNumber(request.getParameter("newIdentificationNumberForEA"));
        	ea.setAccNumberKWSP(request.getParameter("accNumberKWSPForEA"));
        	if(!StringUtil.isNullOrEmpty(request.getParameter("incomeTaxPaidByEmployerForEA"))){
        		ea.setIncomeTaxPaidByEmployer(Double.parseDouble(request.getParameter("incomeTaxPaidByEmployerForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("carAndPetrolForEA"))){
        		ea.setCarAndPetrol(Double.parseDouble(request.getParameter("carAndPetrolForEA")));
        	}
        	ea.setCarType(request.getParameter("carTypeForEA"));
        	if(!StringUtil.isNullOrEmpty(request.getParameter("carYearMakeForEA"))){
        		ea.setCarYearMake(sdf.parse(request.getParameter("carYearMakeForEA")));
        	}
        	ea.setCarModel(request.getParameter("carModelForEA"));
        	if(!StringUtil.isNullOrEmpty(request.getParameter("driverWagesForEA"))){
        		ea.setDriverWages(Double.parseDouble(request.getParameter("driverWagesForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("entertainmentForEA"))){
        		ea.setEntertainment(Double.parseDouble(request.getParameter("entertainmentForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("handphoneForEA"))){
        		ea.setHandphone(Double.parseDouble(request.getParameter("handphoneForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("maidAndGardenerForEA"))){
        		ea.setMaidAndGardener(Double.parseDouble(request.getParameter("maidAndGardenerForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("airTicketsForHolidaysForEA"))){
        		ea.setAirTicketsForHolidays(Double.parseDouble(request.getParameter("airTicketsForHolidaysForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("otherBenefitsForClothingAndFoodsForEA"))){
        		ea.setOtherBenefitsForClothingAndFoods(Double.parseDouble(request.getParameter("otherBenefitsForClothingAndFoodsForEA")));
        	}
        	ea.setHousingAddress(request.getParameter("housingAddressForEA"));
        	if(!StringUtil.isNullOrEmpty(request.getParameter("refundsFromKWSPOtherForEA"))){
        		ea.setRefundsFromKWSPOther(Double.parseDouble(request.getParameter("refundsFromKWSPOtherForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("compensationLossWorkForEA"))){
        		ea.setCompensationLossWork(Double.parseDouble(request.getParameter("compensationLossWorkForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("retirementPaymentForEA"))){
        		ea.setRetirementPayment(Double.parseDouble(request.getParameter("retirementPaymentForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("periodicalPaymentForEA"))){
        		ea.setPeriodicalPayment(Double.parseDouble(request.getParameter("periodicalPaymentForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("cp38DeductionForEA"))){
        		ea.setCp38Deduction(Double.parseDouble(request.getParameter("cp38DeductionForEA")));
        	}
        	ea.setName(request.getParameter("nameForEA"));
        	if(!StringUtil.isNullOrEmpty(request.getParameter("portionOfKWSPForEA"))){
        		ea.setPortionOfKWSP(Double.parseDouble(request.getParameter("portionOfKWSPForEA")));
        	}
        	ea.setTypeOfIncome(request.getParameter("typeOfIncomeForEA"));
        	if(!StringUtil.isNullOrEmpty(request.getParameter("contributionKWSPForEA"))){
        		ea.setContributionKWSP(Double.parseDouble(request.getParameter("contributionKWSPForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("amountForEA"))){
        		ea.setAmount(Double.parseDouble(request.getParameter("amountForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("nonTaxableAmountForEA"))){
        		ea.setNonTaxableAmount(Double.parseDouble(request.getParameter("nonTaxableAmountForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("otherBenefitsForEA"))){
        		ea.setOtherBenefits(Double.parseDouble(request.getParameter("otherBenefitsForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("housingBenefitsWithFurnitureForEA"))){
        		ea.setHousingBenefitsWithFurniture(Double.parseDouble(request.getParameter("housingBenefitsWithFurnitureForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("housingBenefitsWithKitchenForEA"))){
        		ea.setHousingBenefitsWithKitchen(Double.parseDouble(request.getParameter("housingBenefitsWithKitchenForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("furnitureAndFittingForEA"))){
        		ea.setFurnitureAndFitting(Double.parseDouble(request.getParameter("furnitureAndFittingForEA")));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("kitchenAndUtensilsForEA"))){
        		ea.setKitchenAndUtensils(Double.parseDouble(request.getParameter("kitchenAndUtensilsForEA")));
        	}
        	        	
        	ea.setMonth(month);
        	ea.setYear(year);
        	ea.setUseraccount(useraccount);
            ea.setAuthorizeStatus(status);
        	
	        malaysianStatutoryFormDAO.saveMalaysiaFormEA(ea);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    public void saveHRDLevyUserData (HttpServletRequest request, User user, int month, int year){

        try {
            String id= request.getParameter("HRDLevyid");
            String baseSalary = request.getParameter("HRDLevybasesalary");
            String others = request.getParameter("HRDLevyothers");
            String netSalary = request.getParameter("HRDLevynetsalary");
            String levy = request.getParameter("HRDLevyhrdlevy");
            String statusString = request.getParameter("HRDLevyauthorize");
            int status = getStatusForStatutoryForm(statusString);
            
            malaysianStatutoryFormDAO.saveHRDLevy(id, baseSalary, others, netSalary, levy, user, month, year, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public int getStatusForStatutoryForm (String statusString){
        int status = MalaysianIncomeTaxConstants.STATUTORY_FORM_STATUS_PENDING;
        
        if(!StringUtil.isNullOrEmpty(statusString)){
            status = Integer.parseInt(statusString);
            if(status== MalaysianIncomeTaxConstants.STATUTORY_FORM_STATUS_UNAUTHORIZE){
                status = MalaysianIncomeTaxConstants.STATUTORY_FORM_STATUS_PENDING;
            }
        }
        return status;
    }
    public void saveAmanahSahamNasionalUserData (HttpServletRequest request, User user, int month, int year){

        try {
            String id= request.getParameter("amnid");
            String icno = request.getParameter("asnicno");
            String accno = request.getParameter("asnaccno");
            String amount = request.getParameter("asnamount");
            String statusString = request.getParameter("asnauthorize");

            int status = getStatusForStatutoryForm(statusString);
            
            malaysianStatutoryFormDAO.saveAmanahSahamNasional(id, icno, accno, amount, user, month, year, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveTabungHajiUserData (HttpServletRequest request, User user, int month, int year){

        try{
            String thid= request.getParameter("tabunghajiid");
            String thicno = request.getParameter("tabunghajiicno");
            String thaccno = request.getParameter("tabunghajiaccno");
            String thamount = request.getParameter("tabunghajiamount");
            String statusString = request.getParameter("tabunghajiauthorize");
            int status = getStatusForStatutoryForm(statusString);
            
            malaysianStatutoryFormDAO.saveTabungHaji(thid, thicno, thaccno, thamount, user, month, year, status);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveCP21UserData (HttpServletRequest request, User user, int month, int year){

        try{

            String cp21id= request.getParameter("cp21id");
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date datetoleave = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("datetoleave"))){
                datetoleave = sdf.parse(request.getParameter("datetoleave"));
            }
            String passportno = request.getParameter("passportno");
            String empfilerefno = request.getParameter("empfilerefno");
            String natureofemployment = "0";
            if(!StringUtil.isNullOrEmpty(request.getParameter("natureofemployment"))){
                if(!StringUtil.equal(request.getParameter("natureofemployment"), "undefined")){
                    natureofemployment = request.getParameter("natureofemployment");
                }
            }
            String departurereason = request.getParameter("departurereason");
            String correspondenceaddress= request.getParameter("correspondenceaddress");
            
            Date dateofreturn = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("dateofreturn"))){
                dateofreturn = sdf.parse(request.getParameter("dateofreturn"));
            }
            String dueamount = request.getParameter("dueamount");
            Date dateofform = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("dateofform"))){
                dateofform = sdf.parse(request.getParameter("dateofform"));
            }

            Date salaryfrom = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("salaryfrom"))){
                salaryfrom = sdf.parse(request.getParameter("salaryfrom"));
            }
            Date salaryto = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("salaryto"))){
                salaryto = sdf.parse(request.getParameter("salaryto"));
            }
            String salaryamount = request.getParameter("salaryamount");
            
            Date leavepayfrom = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("leavepayfrom"))){
                leavepayfrom = sdf.parse(request.getParameter("leavepayfrom"));
            }
            Date leavepayto = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("leavepayto"))){
                leavepayto = sdf.parse(request.getParameter("leavepayto"));
            }
            String leavepayamount = request.getParameter("leavepayamount");

            Date bonusfrom = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("bonusfrom"))){
                bonusfrom = sdf.parse(request.getParameter("bonusfrom"));
            }
            Date bonusto = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("bonusto"))){
                bonusto = sdf.parse(request.getParameter("bonusto"));
            }
            String bonusamount= request.getParameter("bonusamount");

            Date gratuityfrom = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("gratuityfrom"))){
                gratuityfrom = sdf.parse(request.getParameter("gratuityfrom"));
            }
            Date gratuityto = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("gratuityto"))){
                gratuityto = sdf.parse(request.getParameter("gratuityto"));
            }
            String gratuityamount = request.getParameter("gratuityamount");

            Date allowancefrom = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("allowancefrom"))){
                allowancefrom = sdf.parse(request.getParameter("allowancefrom"));
            }
            Date allowanceto = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("allowanceto"))){
                allowanceto = sdf.parse(request.getParameter("allowanceto"));
            }
            String allowanceamount = request.getParameter("allowanceamount");
            
            Date pensionfrom = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("pensionfrom"))){
                pensionfrom = sdf.parse(request.getParameter("pensionfrom"));
            }
            Date pensionto = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("pensionto"))){
                pensionto = sdf.parse(request.getParameter("pensionto"));
            }
            String pensionamount = request.getParameter("pensionamount");

            Date residencefrom = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("residencefrom"))){
                residencefrom = sdf.parse(request.getParameter("residencefrom"));
            }
            Date residenceto = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("residenceto"))){
                residenceto = sdf.parse(request.getParameter("residenceto"));
            }
            String residenceamount= request.getParameter("residenceamount");

            Date allowanceinkindfrom = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("allowanceinkindfrom"))){
                allowanceinkindfrom = sdf.parse(request.getParameter("allowanceinkindfrom"));
            }
            Date allowanceinkindto = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("allowanceinkindto"))){
                allowanceinkindto = sdf.parse(request.getParameter("allowanceinkindto"));
            }
            String allowanceinkindamount = request.getParameter("allowanceinkindamount");

            Date pffrom = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("pffrom"))){
                pffrom = sdf.parse(request.getParameter("pffrom"));
            }
            Date pfto = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("pfto"))){
                pfto = sdf.parse(request.getParameter("pfto"));
            }
            String pfamount = request.getParameter("pfamount");

            String natureofpayment = "0";
            if(!StringUtil.isNullOrEmpty(request.getParameter("natureofpayment"))){
                if(!StringUtil.equal(request.getParameter("natureofpayment"), "undefined")){
                    natureofpayment = request.getParameter("natureofpayment");
                }
            }

            Date dateofpayment = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("dateofpayment"))){
                dateofpayment = sdf.parse(request.getParameter("dateofpayment"));
            }
            String amounttobepaid = request.getParameter("amounttobepaid");

            String statusString = request.getParameter("cp21authorize");
            int status = getStatusForStatutoryForm(statusString);

            malaysianStatutoryFormDAO.saveCP21(cp21id, empfilerefno, user, datetoleave, passportno, natureofemployment, departurereason, correspondenceaddress,
                    dateofreturn,dueamount, dateofform, salaryfrom, salaryto, salaryamount, leavepayfrom, leavepayto, leavepayamount,
                    bonusfrom, bonusto, bonusamount, gratuityfrom, gratuityto, gratuityamount, allowancefrom, allowanceto, allowanceamount,
                    pensionfrom, pensionto, pensionamount, residencefrom, residenceto, residenceamount, allowanceinkindfrom, allowanceinkindto,
                    allowanceinkindamount, pffrom, pfto, pfamount, natureofpayment, dateofpayment, amounttobepaid, month, year, status);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public ModelAndView getUserStatutoryFormInformation(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonType = new JSONObject();
        try {

            int month = 0;
        	int year = 0;
        	if(!StringUtil.isNullOrEmpty(request.getParameter("declarationMonth"))){
        		month = Integer.parseInt(request.getParameter("declarationMonth"));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("declarationYear"))){
        		year = Integer.parseInt(request.getParameter("declarationYear"));
        	}

            String userid = request.getParameter("userID");
        	MalaysiaFormAmanahSahamNasional asn = malaysianStatutoryFormDAO.getUserAmanahSahamNasional(userid, month, year);
            MalaysiaFormTabungHaji th = malaysianStatutoryFormDAO.getUserTabungHaji(userid, month, year);
            MalaysiaFormCP21 cp = malaysianStatutoryFormDAO.getUserCP21(userid, month, year);
            MalaysiaFormHRDLevy hl = malaysianStatutoryFormDAO.getUserHRDLevy(userid, month, year);
            
            MalaysiaFormTP1 tp1 = malaysianStatutoryFormDAO.getEmployeeTP1(userid, month, year);
            MalaysiaFormTP2 tp2 = malaysianStatutoryFormDAO.getEmployeeTP2(userid, month, year);
            MalaysiaFormTP3 tp3 = malaysianStatutoryFormDAO.getEmployeeTP3(userid, month, year);
            MalaysiaFormCP39 cp39 = malaysianStatutoryFormDAO.getEmployeeCP39(userid, month, year);
            MalaysiaFormCP39A cp39A = malaysianStatutoryFormDAO.getEmployeeCP39A(userid, month, year);
            MalaysiaFormPCB2 pcb2 = malaysianStatutoryFormDAO.getEmployeePCB2(userid, month, year);
            MalaysiaFormEA ea = malaysianStatutoryFormDAO.getEmployeeEA(userid, month, year);
            
            if(asn!=null){
                jsonObj.put("asnid", asn.getId());
                jsonObj.put("asnaccno", asn.getAccountno());
                jsonObj.put("asnamount", asn.getAmount());
                jsonObj.put("asnicno", asn.getIcno());
                jsonObj.put("asnemployeename", StringUtil.getFullName(asn.getUser()));
                jsonObj.put("asnauthorize", asn.getAuthorizeStatus());
            }

            if(th!=null){
                jsonObj.put("tabunghajiid", th.getId());
                jsonObj.put("tabunghajiaccno", th.getAccountno());
                jsonObj.put("tabunghajiamount", th.getAmount());
                jsonObj.put("tabunghajiicno", th.getIcno());
                jsonObj.put("tabunghajiemployeename", StringUtil.getFullName(asn.getUser()));
                jsonObj.put("tabunghajiauthorize", th.getAuthorizeStatus());
            }

            if(cp!=null){

                
                jsonObj.put("cp21id", cp.getId());
                jsonObj.put("datetoleave", getDateForForms(cp.getExpDateToLeave()));
                jsonObj.put("passportno", cp.getPassportno());
                jsonObj.put("empfilerefno", cp.getEmpfilerefno());
                jsonObj.put("natureofemployment", cp.getNatureofemployment());
                jsonObj.put("departurereason", cp.getReasonForDeparture());
                jsonObj.put("correspondenceaddress", cp.getCorresspondenceAddress());
                jsonObj.put("dateofreturn", getDateForForms(cp.getDateOfReturn()));
                jsonObj.put("dueamount", cp.getAmountdue());
                jsonObj.put("dateofform", getDateForForms(cp.getDateOfSubmission()));
                jsonObj.put("salaryfrom", getDateForForms(cp.getSalaryFrom()));
                jsonObj.put("salaryto", getDateForForms(cp.getSalaryTo()));
                jsonObj.put("salaryamount", cp.getSalaryAmount());
                jsonObj.put("leavepayfrom", getDateForForms(cp.getLeavePayFrom()));
                jsonObj.put("leavepayto", getDateForForms(cp.getLeavePayTo()));
                jsonObj.put("leavepayamount", cp.getLeavePayAmount());
                jsonObj.put("bonusfrom", getDateForForms(cp.getBonusFrom()));
                jsonObj.put("bonusto", getDateForForms(cp.getBonusTo()));
                jsonObj.put("bonusamount", cp.getBonusAmount());
                jsonObj.put("gratuityfrom", getDateForForms(cp.getGratuityFrom()));
                jsonObj.put("gratuityto", getDateForForms(cp.getGratuityTo()));
                jsonObj.put("gratuityamount", cp.getGratuityAmount());
                jsonObj.put("allowancefrom", getDateForForms(cp.getAllowanceFrom()));
                jsonObj.put("allowanceto", getDateForForms(cp.getAllowanceTo()));
                jsonObj.put("allowanceamount", cp.getAllowanceAmount());
                jsonObj.put("pensionfrom", getDateForForms(cp.getPensionFrom()));
                jsonObj.put("pensionto", getDateForForms(cp.getPensionTo()));
                jsonObj.put("pensionamount", cp.getPensionAmount());
                jsonObj.put("residencefrom", getDateForForms(cp.getResidenceFrom()));
                jsonObj.put("residenceto", getDateForForms(cp.getResidenceTo()));
                jsonObj.put("residenceamount", cp.getResidenceAmount());
                jsonObj.put("allowanceinkindfrom", getDateForForms(cp.getAllowanceinkindFrom()));
                jsonObj.put("allowanceinkindto", getDateForForms(cp.getAllowanceinkindTo()));
                jsonObj.put("allowanceinkindamount", cp.getAllowanceinkindAmount());
                jsonObj.put("pffrom", getDateForForms(cp.getProvidentFundFrom()));
                jsonObj.put("pfto", getDateForForms(cp.getProvidentFundTo()));
                jsonObj.put("pfamount", cp.getProvidentFundAmount());
                jsonObj.put("natureofpayment", cp.getNatureofpayment());
                jsonObj.put("dateofpayment", getDateForForms(cp.getPaymentDate()));
                jsonObj.put("amounttobepaid", cp.getAmountToBePaid());
                jsonObj.put("cp21authorize", cp.getAuthorizeStatus());


            }

            if(hl!=null){

                jsonObj.put("HRDLevyid", hl.getId());
                jsonObj.put("HRDLevybasesalary", hl.getBaseSalary());
                jsonObj.put("HRDLevyothers", hl.getOthers());
                jsonObj.put("HRDLevynetsalary", hl.getNetSalary());
                jsonObj.put("HRDLevyhrdlevy", hl.getHrdLevy());
                jsonObj.put("HRDLevyauthorize", hl.getAuthorizeStatus());

            }
            
            
            if(tp1!=null){
            	jsonObj.put("idForTP1", tp1.getId());
            	jsonObj.put("oldIdentificationNumberForTP1", tp1.getOldIdentificationNumber());
            	jsonObj.put("newIdentificationNumberForTP1", tp1.getNewIdentificationNumber());
            	jsonObj.put("passportNumberForTP1", tp1.getPassportNumber());
            	jsonObj.put("armyOrPoliceNumberForTP1", tp1.getArmyOrPoliceNumber());
            	jsonObj.put("incomeTaxNumberForTP1", tp1.getIncomeTaxNumber());
                jsonObj.put("tp1authorize", tp1.getAuthorizeStatus());
            }
            
            if(tp2!=null){
            	jsonObj.put("idForTP2", tp2.getId());
            	jsonObj.put("oldIdentificationNumberForTP2", tp2.getOldIdentificationNumber());
            	jsonObj.put("newIdentificationNumberForTP2", tp2.getNewIdentificationNumber());
            	jsonObj.put("passportNumberForTP2", tp2.getPassportNumber());
            	jsonObj.put("armyOrPoliceNumberForTP2", tp2.getArmyOrPoliceNumber());
            	jsonObj.put("incomeTaxLHDNNumberForTP2", tp2.getIncomeTaxLHDNNumber());
            	jsonObj.put("carForTP2", tp2.getCar());
            	jsonObj.put("driverForTP2", tp2.getDriver());
            	jsonObj.put("householdItemsForTP2", tp2.getHouseholdItems());
            	jsonObj.put("entertainmentForTP2", tp2.getEntertainment());
            	jsonObj.put("gardenerForTP2", tp2.getGardener());
            	jsonObj.put("maidForTP2", tp2.getMaid());
            	jsonObj.put("holidayAllowanceForTP2", tp2.getHolidayAllowance());
            	jsonObj.put("membershipForTP2", tp2.getMembership());
                jsonObj.put("tp2authorize", tp2.getAuthorizeStatus());
            }
            
            
            if(tp3!=null){
            	jsonObj.put("idForTP3", tp3.getId());
            	jsonObj.put("previousEmployer1ForTP3", tp3.getPreviousEmployer1());
            	jsonObj.put("employerReferenceNo1ForTP3", tp3.getEmployerReferenceNo1());
            	jsonObj.put("previousEmployer2ForTP3", tp3.getPreviousEmployer2());
            	jsonObj.put("employerReferenceNo2ForTP3", tp3.getEmployerReferenceNo2());
            	jsonObj.put("oldIdentificationNumberForTP3", tp3.getOldIdentificationNumber());
            	jsonObj.put("newIdentificationNumberForTP3", tp3.getNewIdentificationNumber());
            	jsonObj.put("armyOrPoliceNumberForTP3", tp3.getArmyOrPoliceNumber());
            	jsonObj.put("passportNumberForTP3", tp3.getPassportNumber());
            	jsonObj.put("incomeTaxFileNumberForTP3", tp3.getIncomeTaxFileNumber());
            	jsonObj.put("freeSampleProductOnDiscountForTP3", tp3.getFreeSampleProductOnDiscount());
            	jsonObj.put("employeeLongServiceAwardForTP3", tp3.getEmployeeLongServiceAward());
            	jsonObj.put("totalContributionToKWSPForTP3", tp3.getTotalContributionToKWSP());
            	jsonObj.put("tuitionfeesForTP3", tp3.getTuitionfees());
            	jsonObj.put("contributionToPrivatePensionForTP3", tp3.getContributionToPrivatePension());
            	jsonObj.put("totalAllowanceForTP3", tp3.getTotalAllowance());
            	jsonObj.put("otherAllowanceForTP3", tp3.getOtherAllowance());
                jsonObj.put("tp3authorize", tp3.getAuthorizeStatus());
            }
            
            if(cp39!=null){
            	jsonObj.put("idForCP39", cp39.getId());
            	jsonObj.put("incomeTaxFileNumberForCP39", cp39.getIncomeTaxFileNumber());
            	jsonObj.put("oldIdentificationNumberForCP39", cp39.getOldIdentificationNumber());
            	jsonObj.put("newIdentificationNumberForCP39", cp39.getNewIdentificationNumber());
            	jsonObj.put("passportNumberForCP39", cp39.getPassportNumber());
            	jsonObj.put("countryCodeForCP39", cp39.getCountryCode());
            	jsonObj.put("deductionAmountForCP38ForCP39", cp39.getDeductionAmountForCP38());
                jsonObj.put("cp39authorize", cp39.getAuthorizeStatus());
            }
            
            if(cp39A!=null){
            	jsonObj.put("idForCP39A", cp39A.getId());
            	jsonObj.put("incomeTaxFileNumberForCP39A", cp39A.getIncomeTaxFileNumber());
            	jsonObj.put("oldIdentificationNumberForCP39A", cp39A.getOldIdentificationNumber());
            	jsonObj.put("newIdentificationNumberForCP39A", cp39A.getNewIdentificationNumber());
            	jsonObj.put("passportNumberForCP39A", cp39A.getPassportNumber());
            	jsonObj.put("countryCodeForCP39A", cp39A.getCountryCode());
            	jsonObj.put("deductionAmountForCP38ForCP39A", cp39A.getDeductionAmountForCP38());
                jsonObj.put("cp39Aauthorize", cp39A.getAuthorizeStatus());
            }
            
            
            if(pcb2!=null){
            	jsonObj.put("idForPCB2", pcb2.getId());
            	jsonObj.put("deductionAmountCP38ForPCB2", pcb2.getDeductionAmountForCP38());
            	jsonObj.put("taxResitPCBForPCB2", pcb2.getTaxResitForPCB());
            	jsonObj.put("taxResitPCBDateForPCB2", pcb2.getTaxResitForPCBDate());
            	jsonObj.put("taxResitCP38ForPCB2", pcb2.getTaxResitForCP38());
            	jsonObj.put("taxResitCP38DateForPCB2", pcb2.getTaxResitForCP38Date());
            	jsonObj.put("newIdentificationNumberForPCB2", pcb2.getNewIdentificationNumber());
            	jsonObj.put("incomeTaxFileNumberForPCB2", pcb2.getIncomeTaxFileNumber());
                jsonObj.put("pcb2authorize", pcb2.getAuthorizeStatus());
            }
            
            if(ea!=null){
            	jsonObj.put("idForEA", ea.getId());
            	jsonObj.put("serialNumberForEA", ea.getSerialNumber());
            	jsonObj.put("employerERefNumberForEA", ea.getEmployerERefNumber());
            	jsonObj.put("incomeTaxFileNumberForEA", ea.getIncomeTaxFileNumber());
            	jsonObj.put("incomeTaxBranchForEA", ea.getIncomeTaxBranch());
            	jsonObj.put("oldIdentificationNumberForEA", ea.getOldIdentificationNumber());
            	jsonObj.put("newIdentificationNumberForEA", ea.getNewIdentificationNumber());
            	jsonObj.put("accNumberKWSPForEA", ea.getAccNumberKWSP());
            	jsonObj.put("incomeTaxPaidByEmployerForEA", ea.getIncomeTaxPaidByEmployer());
            	jsonObj.put("carAndPetrolForEA", ea.getCarAndPetrol());
            	jsonObj.put("carTypeForEA", ea.getCarType());
            	jsonObj.put("carYearMakeForEA", ea.getCarYearMake());
            	jsonObj.put("carModelForEA", ea.getCarModel());
            	jsonObj.put("driverWagesForEA", ea.getDriverWages());
            	jsonObj.put("entertainmentForEA", ea.getEntertainment());
            	jsonObj.put("handphoneForEA", ea.getHandphone());
            	jsonObj.put("maidAndGardenerForEA", ea.getMaidAndGardener());
            	jsonObj.put("airTicketsForHolidaysForEA", ea.getAirTicketsForHolidays());
            	jsonObj.put("otherBenefitsForClothingAndFoodsForEA", ea.getOtherBenefitsForClothingAndFoods());
            	jsonObj.put("housingAddressForEA", ea.getHousingAddress());
            	jsonObj.put("refundsFromKWSPOtherForEA", ea.getRefundsFromKWSPOther());
            	jsonObj.put("compensationLossWorkForEA", ea.getCompensationLossWork());
            	jsonObj.put("retirementPaymentForEA", ea.getRetirementPayment());
            	jsonObj.put("periodicalPaymentForEA", ea.getPeriodicalPayment());
            	jsonObj.put("cp38DeductionForEA", ea.getCp38Deduction());
            	jsonObj.put("nameForEA", ea.getName());
            	jsonObj.put("portionOfKWSPForEA", ea.getPortionOfKWSP());
            	jsonObj.put("typeOfIncomeForEA", ea.getTypeOfIncome());
            	jsonObj.put("contributionKWSPForEA", ea.getContributionKWSP());
            	jsonObj.put("amountForEA", ea.getAmount());
            	jsonObj.put("nonTaxableAmountForEA", ea.getNonTaxableAmount());
            	jsonObj.put("otherBenefitsForEA", ea.getOtherBenefits());
            	jsonObj.put("housingBenefitsWithFurnitureForEA", ea.getHousingBenefitsWithFurniture());
            	jsonObj.put("housingBenefitsWithKitchenForEA", ea.getHousingBenefitsWithKitchen());
            	jsonObj.put("furnitureAndFittingForEA", ea.getFurnitureAndFitting());
            	jsonObj.put("kitchenAndUtensilsForEA", ea.getKitchenAndUtensils());
            	jsonObj.put("eaAauthorize", ea.getAuthorizeStatus());
            }
        	
    		jsonType.put("userdata", jsonObj);
        	json.put("success", true);
        	json.put("data", jsonType);
        	jsonObject.put("data", json.toString());
            jsonObject.put("valid", true);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return new ModelAndView("jsonView","model",jsonObject.toString());
    }

    public String getDateForForms(Date dt){
        String dat="";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(dt!=null){
            dat = sdf.format(dt);
        }

        return dat;
    }
    public ModelAndView saveCompanyFormInformation(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
        	int month = 0;
        	int year = 0;
        	if(!StringUtil.isNullOrEmpty(request.getParameter("month"))){
        		month = Integer.parseInt(request.getParameter("month"));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("year"))){
        		year = Integer.parseInt(request.getParameter("year"));
        	}
        	
        	MalaysiaCompanyForm companyForm = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(sessionHandlerImplObj.getCompanyid(request), month, year);;
        	if(companyForm==null){
        		companyForm = new MalaysiaCompanyForm();
        	}
        	
        	if(!StringUtil.isNullOrEmpty(request.getParameter("asnpaymenttype"))){
        		companyForm.setAmanahSahamNasionalPaymentType(Integer.parseInt(request.getParameter("asnpaymenttype")));		
        	}
        	companyForm.setAmanahSahamNasionalChequeno(request.getParameter("asnchqno"));		
        	companyForm.setAmanahSahamNasionalUser((User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", request.getParameter("asnpreparedby")));
        	
        	if(!StringUtil.isNullOrEmpty(request.getParameter("tabunghajipaymenttype"))){
        		companyForm.setTabungHajiPaymentType(Integer.parseInt(request.getParameter("tabunghajipaymenttype")));		
        	}
        	companyForm.setTabungHajiChequeno(request.getParameter("tabunghajichqno"));		
        	companyForm.setTabungHajiUser((User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", request.getParameter("asnpreparedby")));
        	
        	if(!StringUtil.isNullOrEmpty(request.getParameter("hrdpaymenttype"))){
        		companyForm.setHrdLevyPaymentType(Integer.parseInt(request.getParameter("hrdpaymenttype")));		
        	}
        	companyForm.setHrdLevyChequeno(request.getParameter("hrdchqno"));
        	companyForm.setBranch(request.getParameter("branch"));
        	companyForm.setEmployerno(request.getParameter("employerno"));
            companyForm.setEmployernoHQ(request.getParameter("companyEmployerNumbeHeadQuarter"));
        	companyForm.setCp21EmployerFileRefno(request.getParameter("cp21employerfilerefno"));
        	companyForm.setPcb2User((User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", request.getParameter("personinchargepcb2")));
        	companyForm.setTp1User((User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", request.getParameter("personinchargetp1")));
        	companyForm.setTp2User((User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", request.getParameter("personinchargetp2")));
        	companyForm.setTp3User((User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", request.getParameter("personinchargetp3")));
        	companyForm.setCp39User((User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", request.getParameter("personinchargecp39")));
        	companyForm.setCp39AUser((User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", request.getParameter("personinchargecp39a")));
        	companyForm.setEaUser((User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", request.getParameter("adminCmbEA")));
        	companyForm.setMonth(month);
        	companyForm.setYear(year);
        	companyForm.setCompany((Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", sessionHandlerImplObj.getCompanyid(request)));
        	malaysianStatutoryFormDAO.saveCompanyFormInformation(companyForm);
        	jsonObject.put("success", true);
            jsonObject.put("valid", true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jsonObject.toString());
    }
    
    
    public ModelAndView getCompanyFormInformation(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObj = new JSONObject();
        JSONObject jsonType = new JSONObject();
        try {
        	int month = 0;
        	int year = 0;
        	if(!StringUtil.isNullOrEmpty(request.getParameter("month"))){
        		month = Integer.parseInt(request.getParameter("month"));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("year"))){
        		year = Integer.parseInt(request.getParameter("year"));
        	}
            String companyid =sessionHandlerImplObj.getCompanyid(request);
        	MalaysiaCompanyForm asn = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(companyid, month, year);
            if(asn!=null){
                jsonObj.put("asnid", asn.getId());
                jsonObj.put("asnpaymenttype", asn.getAmanahSahamNasionalPaymentType());
                jsonObj.put("asnchqno", asn.getAmanahSahamNasionalChequeno());
                jsonObj.put("asnpreparedby", asn.getAmanahSahamNasionalUser().getUserID());
                jsonObj.put("tabunghajipaymenttype", asn.getTabungHajiPaymentType());
                jsonObj.put("tabunghajichqno", asn.getTabungHajiChequeno());
                jsonObj.put("tabunghajipreparedby", asn.getTabungHajiUser().getUserID());
                jsonObj.put("cp21employerfilerefno", asn.getCp21EmployerFileRefno());
                jsonObj.put("hrdpaymenttype", asn.getHrdLevyPaymentType());
                jsonObj.put("hrdchqno", asn.getHrdLevyChequeno());
                jsonObj.put("personinchargepcb2", asn.getPcb2User().getUserID());
                jsonObj.put("personinchargetp1", asn.getTp1User().getUserID());
                jsonObj.put("personinchargetp2", asn.getTp2User().getUserID());
                jsonObj.put("personinchargetp3", asn.getTp3User().getUserID());
                jsonObj.put("branch", asn.getBranch());
                jsonObj.put("employerno", asn.getEmployerno());
                jsonObj.put("companyEmployerNumbeHeadQuarter", asn.getEmployernoHQ());
                jsonObj.put("personinchargecp39", asn.getCp39User().getUserID());
                jsonObj.put("personinchargecp39a", asn.getCp39AUser().getUserID());
                if(asn.getEaUser()!=null){
                	jsonObj.put("adminCmbEA", asn.getEaUser().getUserID());
                }
            }
            

    		jsonType.put("userdata", jsonObj);
        	json.put("success", true);
        	json.put("data", jsonType);
        	jsonObject.put("data", json.toString());
            jsonObject.put("valid", true);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return new ModelAndView("jsonView","model",jsonObject.toString());
    }
    
    
    
    public ModelAndView getCP39(HttpServletRequest request, HttpServletResponse response) throws SessionExpiredException, ServiceException {
    	Map<String, Object> map = new HashMap<String, Object>();
    	try{
    		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            int month = 0;
        	int year = 0;
        	int frequency = 0;
        	if(!StringUtil.isNullOrEmpty(request.getParameter("month"))){
        		month = Integer.parseInt(request.getParameter("month"));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("year"))){
        		year = Integer.parseInt(request.getParameter("year"));
        	}
            if(!StringUtil.isNullOrEmpty(request.getParameter("frequency"))){
            	frequency = Integer.parseInt(request.getParameter("frequency"));
            }
            
            Calendar calendar = Calendar.getInstance();
            Date startDate = null;
            Date endDate = null;
            calendar.set(year, month, 1);
            startDate = calendar.getTime();
            calendar.set(Calendar.DATE, calendar.getActualMaximum(calendar.DATE));
            endDate = calendar.getTime();
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            
            
        	HrmsCommonPayroll hrmsCommonPayroll = new HrmsCommonPayroll();
        	List<CP39> listCP39 = new ArrayList<CP39>();
        	CP39 cp39 = new CP39();
            MalaysiaCompanyForm companyInfo = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(companyid, month, year);
            Company company = companyInfo.getCompany();
            User personInCharge = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", companyInfo.getCp39AUser().getUserID());
            
            Useraccount useraccountEmployer = null;
            if(personInCharge!=null){
            	useraccountEmployer = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", personInCharge.getUserID());
            }
            String employerReferenceNumber = null;
            if(useraccountEmployer!=null){
            	employerReferenceNumber = hrmsCommonPayroll.getEmployeeIdFormat(useraccountEmployer, profileHandlerDAO);
            	cp39.setDesignation(useraccountEmployer.getDesignationid().getValue());
            }
            
            cp39.setDate(dateFormat.format(new Date()));
            cp39.setAmount(String.valueOf(""));
            cp39.setFullName(StringUtil.getFullName(company.getCreator()));
            cp39.setCompanyName(company.getCompanyName());
            cp39.setCompanyAddress(company.getAddress());
            cp39.setEmployerReferenceNumber(employerReferenceNumber);
            listCP39.add(cp39);
            map.put("datasource", new JRBeanCollectionDataSource(listCP39));
            
            List<MalaysiaFormCP39> list = malaysianStatutoryFormDAO.getEmployeeCP39List(companyid, month, year);
            List<CP39Employee> listEmployee = new ArrayList<CP39Employee>();
            
            int count=0;
            double grandTotal = 0, totalForCP38 = 0, totalForPCB = 0;
            for(MalaysiaFormCP39 malaysiaFormCP39: list){
            	CP39Employee employee = new CP39Employee();
            	List<PayrollHistory> histories = hrmsPayrollDAOObj.getGeneratedSalaries(malaysiaFormCP39.getUseraccount().getUserID(), startDate, endDate);
            	if(histories!=null&&!histories.isEmpty()){
            		Double pcb = histories.get(0).getIncometaxAmount();
            		if(pcb!=null){
            			employee.setDeductionAmountForPCB(BigDecimal.valueOf(pcb).setScale(2).toString());
            			totalForPCB+=pcb;
            		}
            	}else{
            		employee.setDeductionAmountForPCB(BigDecimal.valueOf(0).setScale(2).toString());
            	}
            	employee.setNumber(String.valueOf(count++));
            	employee.setFullName(StringUtil.getFullName(malaysiaFormCP39.getUseraccount().getUser()));
            	employee.setEmployeeNumber(hrmsCommonPayroll.getEmployeeIdFormat(malaysiaFormCP39.getUseraccount(), profileHandlerDAO));
            	employee.setNewIdentificationNumber(malaysiaFormCP39.getNewIdentificationNumber());
            	employee.setOldIdentificationNumber(malaysiaFormCP39.getOldIdentificationNumber());
            	employee.setPassportNumber(malaysiaFormCP39.getPassportNumber());
            	employee.setCountryCode(malaysiaFormCP39.getCountryCode());
            	employee.setIncomeTaxFileNumber(malaysiaFormCP39.getIncomeTaxFileNumber());
                if(malaysiaFormCP39.getDeductionAmountForCP38()!=null){
                	employee.setDeductionAmountForCP38(BigDecimal.valueOf(malaysiaFormCP39.getDeductionAmountForCP38()).setScale(2).toString());
                	totalForCP38+=malaysiaFormCP39.getDeductionAmountForCP38();
                }else{
                	employee.setDeductionAmountForCP38(BigDecimal.valueOf(0).setScale(2).toString());
                }
                grandTotal = totalForPCB+ totalForCP38;
                employee.setTotalForPCB(BigDecimal.valueOf(totalForPCB).setScale(2).toString());
                employee.setTotalForCP38(BigDecimal.valueOf(totalForCP38).setScale(2).toString());
                employee.setGrandTotal(BigDecimal.valueOf(grandTotal).setScale(2).toString());
                listEmployee.add(employee);
            }
            
            if(list.isEmpty()){
            	listEmployee.add(new CP39Employee());
            }
            map.put("SubReportData",  new JRBeanCollectionDataSource(listEmployee));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return new ModelAndView("pdfCP39", map);
    }
    
    
    public ModelAndView getCP39A(HttpServletRequest request, HttpServletResponse response) throws SessionExpiredException, ServiceException {
    	Map<String, Object> map = new HashMap<String, Object>();
    	try{
    		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            int month = 0;
        	int year = 0;
        	int frequency = 0;
        	if(!StringUtil.isNullOrEmpty(request.getParameter("month"))){
        		month = Integer.parseInt(request.getParameter("month"));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("year"))){
        		year = Integer.parseInt(request.getParameter("year"));
        	}
            if(!StringUtil.isNullOrEmpty(request.getParameter("frequency"))){
            	frequency = Integer.parseInt(request.getParameter("frequency"));
            }
            
            Calendar calendar = Calendar.getInstance();
            Date startDate = null;
            Date endDate = null;
            calendar.set(year, month, 1);
            startDate = calendar.getTime();
            calendar.set(Calendar.DATE, calendar.getActualMaximum(calendar.DATE));
            endDate = calendar.getTime();
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            
            
        	HrmsCommonPayroll hrmsCommonPayroll = new HrmsCommonPayroll();
        	List<CP39A> listCP39A = new ArrayList<CP39A>();
        	CP39A cp39A = new CP39A();
            MalaysiaCompanyForm companyInfo = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(companyid, month, year);
            Company company = companyInfo.getCompany();
            User personInCharge = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", companyInfo.getCp39AUser().getUserID());
            
            Useraccount useraccountEmployer = null;
            if(personInCharge!=null){
            	useraccountEmployer = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", personInCharge.getUserID());
            }
            String employerReferenceNumber = null;
            if(useraccountEmployer!=null){
            	employerReferenceNumber = hrmsCommonPayroll.getEmployeeIdFormat(useraccountEmployer, profileHandlerDAO);
            	cp39A.setDesignation(useraccountEmployer.getDesignationid().getValue());
            }
            
            cp39A.setDate(dateFormat.format(new Date()));
            cp39A.setAmount(String.valueOf(""));
            cp39A.setFullName(StringUtil.getFullName(company.getCreator()));
            cp39A.setCompanyName(company.getCompanyName());
            cp39A.setCompanyAddress(company.getAddress());
            cp39A.setEmployerReferenceNumber(employerReferenceNumber);
            listCP39A.add(cp39A);
            map.put("datasource", new JRBeanCollectionDataSource(listCP39A));
            
            List<MalaysiaFormCP39A> list = malaysianStatutoryFormDAO.getEmployeeCP39AList(companyid, month, year);
            List<CP39AEmployee> listEmployee = new ArrayList<CP39AEmployee>();
            
            int count=0;
            double grandTotal = 0, totalForCP38 = 0, totalForPCB = 0;
            for(MalaysiaFormCP39A malaysiaFormCP39A: list){
            	CP39AEmployee employee = new CP39AEmployee();
            	List<PayrollHistory> histories = hrmsPayrollDAOObj.getGeneratedSalaries(malaysiaFormCP39A.getUseraccount().getUserID(), startDate, endDate);
            	if(histories!=null&&!histories.isEmpty()){
            		Double pcb = histories.get(0).getIncometaxAmount();
            		if(pcb!=null){
            			employee.setDeductionAmountForPCB(BigDecimal.valueOf(pcb).setScale(2).toString());
            			totalForPCB+=pcb;
            		}
            	}else{
            		employee.setDeductionAmountForPCB(BigDecimal.valueOf(0).setScale(2).toString());
            	}
            	employee.setNumber(String.valueOf(count++));
            	employee.setFullName(StringUtil.getFullName(malaysiaFormCP39A.getUseraccount().getUser()));
            	employee.setEmployeeNumber(hrmsCommonPayroll.getEmployeeIdFormat(malaysiaFormCP39A.getUseraccount(), profileHandlerDAO));
            	employee.setNewIdentificationNumber(malaysiaFormCP39A.getNewIdentificationNumber());
            	employee.setOldIdentificationNumber(malaysiaFormCP39A.getOldIdentificationNumber());
            	employee.setPassportNumber(malaysiaFormCP39A.getPassportNumber());
            	employee.setCountryCode(malaysiaFormCP39A.getCountryCode());
            	employee.setIncomeTaxFileNumber(malaysiaFormCP39A.getIncomeTaxFileNumber());            
                if(malaysiaFormCP39A.getDeductionAmountForCP38()!=null){
                	employee.setDeductionAmountForCP38(BigDecimal.valueOf(malaysiaFormCP39A.getDeductionAmountForCP38()).setScale(2).toString());
                	totalForCP38+=malaysiaFormCP39A.getDeductionAmountForCP38();
                }else{
                	employee.setDeductionAmountForCP38(BigDecimal.valueOf(0).setScale(2).toString());
                }
                grandTotal = totalForPCB+ totalForCP38;
                employee.setTotalForPCB(BigDecimal.valueOf(totalForPCB).setScale(2).toString());
                employee.setTotalForCP38(BigDecimal.valueOf(totalForCP38).setScale(2).toString());
                employee.setGrandTotal(BigDecimal.valueOf(grandTotal).setScale(2).toString());
                listEmployee.add(employee);
            }
            
            if(list.isEmpty()){
            	listEmployee.add(new CP39AEmployee());
            }
            map.put("SubReportData",  new JRBeanCollectionDataSource(listEmployee));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return new ModelAndView("pdfCP39A", map);
    }

    public ModelAndView getTP1(HttpServletRequest request, HttpServletResponse response) throws SessionExpiredException, ServiceException {

        Map<String, Object> map = new HashMap<String, Object>();
        try{    
        	String userid = request.getParameter("userid");
            String frequency = request.getParameter("frequency");
            int month = 0;
        	int year = 0;
        	if(!StringUtil.isNullOrEmpty(request.getParameter("month"))){
        		month = Integer.parseInt(request.getParameter("month"));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("year"))){
        		year = Integer.parseInt(request.getParameter("year"));
        	}
            TP1 report = new TP1();
            List<TP1> sampleList = new ArrayList<TP1>(1);
            if(!StringUtil.isNullOrEmpty(userid)){
                MalaysiaFormTP1 malaysiaFormTP1 = malaysianStatutoryFormDAO.getEmployeeTP1(userid, month, year);
                if(malaysiaFormTP1!=null){
                	MalaysiaCompanyForm companyInfo = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(malaysiaFormTP1.getUseraccount().getUser().getCompany().getCompanyID(), month, year);
                    if(companyInfo!=null){
                    	HrmsCommonPayroll hrmsCommonPayroll = new HrmsCommonPayroll();
                        String empid = hrmsCommonPayroll.getEmployeeIdFormat(malaysiaFormTP1.getUseraccount(), profileHandlerDAO);
                        
                        
                        report.setMonth(getMonthName(month, request));
                        report.setYear(String.valueOf(year));
                        report.setCompanyName(companyInfo.getCompany().getCompanyName());
                        report.setCompanyReferenceNumber(companyInfo.getEmployerno());
                        report.setFullName(StringUtil.getFullName(malaysiaFormTP1.getUseraccount().getUser()));
                        report.setNewIdentificationNumber(malaysiaFormTP1.getNewIdentificationNumber());
                        report.setOldIdentificationNumber(malaysiaFormTP1.getOldIdentificationNumber());
                        report.setArmyOrPoliceNumber(malaysiaFormTP1.getArmyOrPoliceNumber());
                        report.setPassportNumber(malaysiaFormTP1.getPassportNumber());
                        report.setIncomeTaxNumber(malaysiaFormTP1.getIncomeTaxNumber());
                        report.setEmployeeNumber(empid);
                        
                        String accumulatedMedicalExpensesForParents = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_MEDICAL_EXPENSES_PARENTS);
                        report.setAccumulatedMedicalExpensesForParents(accumulatedMedicalExpensesForParents);
                        report.setCurrentMedicalExpensesForParents(getCurrentAmountForComponentWithLimit(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_MEDICAL_EXPENSES_PARENTS, accumulatedMedicalExpensesForParents));
                        
                        String accumulatedMedicalEquipment = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_MEDICAL_EQUIPMENT);
                        report.setCurrentMedicalEquipment(getCurrentAmountForComponentWithLimit(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_MEDICAL_EQUIPMENT, accumulatedMedicalEquipment));
                        report.setAccumulatedMedicalEquipment(accumulatedMedicalEquipment);

                        String accumulatedMedicalCheckup = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_MEDICAL_EXAMINATION);
                        String accumulatedMedicalExpensesForSeriousDisease = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_MEDICAL_SERIOUS_DISEASE);

                        accumulatedMedicalCheckup = getMedicalCheckUpAmount(accumulatedMedicalCheckup, accumulatedMedicalExpensesForSeriousDisease);
                        accumulatedMedicalExpensesForSeriousDisease = getMedicalExpenseforSeriousDiseases(accumulatedMedicalCheckup, accumulatedMedicalExpensesForSeriousDisease);

                        String currentMedicalCheckup = getCurrentAmountForComponentWithLimit(year,month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_MEDICAL_EXAMINATION, accumulatedMedicalCheckup);
                        currentMedicalCheckup = getCurrentMedicalCheckUpAmount(currentMedicalCheckup, accumulatedMedicalCheckup, accumulatedMedicalExpensesForSeriousDisease);

                        report.setCurrentMedicalCheckup(currentMedicalCheckup);
                        report.setAccumulatedMedicalCheckup(accumulatedMedicalCheckup);

                        String currentMedicalExpensesForSeriousDisease = getCurrentAmountForComponentWithLimit(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_MEDICAL_SERIOUS_DISEASE, accumulatedMedicalExpensesForSeriousDisease);
                        currentMedicalExpensesForSeriousDisease = getCurrentMedicalSeriousDiseaseAmount(currentMedicalCheckup, currentMedicalExpensesForSeriousDisease,  accumulatedMedicalCheckup, accumulatedMedicalExpensesForSeriousDisease);
                        
                        report.setCurrentMedicalExpensesForSeriousDisease(currentMedicalExpensesForSeriousDisease);
                        report.setAccumulatedMedicalExpensesForSeriousDisease(accumulatedMedicalExpensesForSeriousDisease);

                        String accumulatedPurchaseOfBooks = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_PURCHASE_BOOKS);
                        report.setCurrentPurchaseOfBooks(getCurrentAmountForComponentWithLimit(year,month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_PURCHASE_BOOKS, accumulatedPurchaseOfBooks));
                        report.setAccumulatedPurchaseOfBooks(accumulatedPurchaseOfBooks);

                        String accumulatedPurchaseOfComputer = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_PURCHASE_COMPUTER);
                        report.setCurrentPurchaseOfComputer(getCurrentAmountForComponentWithLimit(year,month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_PURCHASE_COMPUTER, accumulatedPurchaseOfComputer));
                        report.setAccumulatedPurchaseOfComputer(accumulatedPurchaseOfComputer);

                        String accumulatedEducationFund = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_EDUCATION_FUND_SSPN);
                        report.setCurrentEducationFund(getCurrentAmountForComponentWithLimit(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_EDUCATION_FUND_SSPN, accumulatedEducationFund));
                        report.setAccumulatedEducationFund(accumulatedEducationFund);

                        String accumulatedPurchaseOfSportsEquipment = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_PURCHASE_SPORTS_EUIPMENT);
                        report.setCurrentPurchaseOfSportsEquipment(getCurrentAmountForComponentWithLimit(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_PURCHASE_SPORTS_EUIPMENT, accumulatedPurchaseOfSportsEquipment));
                        report.setAccumulatedPurchaseOfSportsEquipment(accumulatedPurchaseOfSportsEquipment);

                        String accumulatedTutionFees = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_TUTION_FEES);
                        report.setCurrentTutionFees(getCurrentAmountForComponentWithLimit(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_TUTION_FEES, accumulatedTutionFees));
                        report.setAccumulatedTutionFees(accumulatedTutionFees);

                        String accumulatedAlimony = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_ALIMONY_PAYMENT);
                        report.setCurrentAlimony(getCurrentAmountForComponentWithLimit(year,month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_ALIMONY_PAYMENT,accumulatedAlimony));
                        report.setAccumulatedAlimony(accumulatedAlimony);

                        String accumulatedEducationAndMedicalInsurance = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_EDUCATION_MEDICAL_PREMIUM);
                        report.setCurrentEducationAndMedicalInsurance(getCurrentAmountForComponentWithLimit(year,month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_EDUCATION_MEDICAL_PREMIUM, accumulatedEducationAndMedicalInsurance));
                        report.setAccumulatedEducationAndMedicalInsurance(accumulatedEducationAndMedicalInsurance);

                        String accumulatedInsurance = getAccumulatedLifeInsurance(year, month, userid);
                        report.setCurrentInsurance(getCurrentLifeInsuranceWithLimit(year, userid, accumulatedInsurance));
                        report.setAccumulatedInsurance(accumulatedInsurance);

                        String accumulatedPensionAndAnnuity = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_PRIVATE_RETIREMENT_AND_ANNUITY);
                        report.setCurrentPensionAndAnnuity(getCurrentAmountForComponentWithLimit(year,month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_PRIVATE_RETIREMENT_AND_ANNUITY,accumulatedPensionAndAnnuity));
                        report.setAccumulatedPensionAndAnnuity(accumulatedPensionAndAnnuity);

                        String accumulatedInternet = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_INTERNET_BROADBAND);
                        report.setCurrentInternet(getCurrentAmountForComponentWithLimit(year, month,userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_INTERNET_BROADBAND, accumulatedInternet));
                        report.setAccumulatedInternet(accumulatedInternet);

                        String accumulatedHousingLoan = getAccumulatedAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_HOUSING_LOAN);
                        report.setCurrentHousingLoan(getCurrentAmountForComponentWithLimit(year, month,userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_HOUSING_LOAN, accumulatedHousingLoan));
                        report.setAccumulatedHousingLoan(accumulatedHousingLoan);

                        report.setZakatPaid(getCurrentZakat(year, userid));
                        report.setLevyPaid(getCurrentZakat(year, userid));

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date());
                        int yr = cal.get(Calendar.YEAR);

                        report.setTarikhD(String.valueOf(cal.get(Calendar.DATE)));
                        report.setTarikhM(String.valueOf(cal.get(Calendar.MONTH)+1));
                        report.setTarikhY(String.valueOf(yr));

                        User pInCharge = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", companyInfo.getTp1User().getUserID());

                        if(pInCharge!=null){
                            report.setPersonInCharge(StringUtil.getFullName(pInCharge));
                        }
                        Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", companyInfo.getTp1User().getUserID());
                        report.setUserDesignation(ua.getDesignationid()!=null?ua.getDesignationid().getValue():"");

                        report.setCompanyAddress(companyInfo.getCompany().getAddress());
                    }
                }
            }
            sampleList.add(report);
            map.put("datasource", new JRBeanCollectionDataSource(sampleList));
        }catch(Exception ex) {
          ex.printStackTrace();
        }


        return new ModelAndView("pdfTPOne", map);
    }
    
    
    public ModelAndView getTP2(HttpServletRequest request, HttpServletResponse response) throws SessionExpiredException, ServiceException {
    	Map<String, Object> map = new HashMap<String, Object>();
    	try{
        	String userid = request.getParameter("userid");
        	int month = 0;
        	int year = 0;
        	if(!StringUtil.isNullOrEmpty(request.getParameter("month"))){
        		month = Integer.parseInt(request.getParameter("month"));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("year"))){
        		year = Integer.parseInt(request.getParameter("year"));
        	}
            List<TP2> list = new ArrayList<TP2>();
            TP2 tp2 = new TP2();
            if(!StringUtil.isNullOrEmpty(userid)){
            	MalaysiaFormTP2 malaysiaFormTP2= malaysianStatutoryFormDAO.getEmployeeTP2(userid, month, year);
            	if(malaysiaFormTP2!=null){
            		HrmsCommonPayroll hrmsCommonPayroll = new HrmsCommonPayroll();
                    String employeeReferenceNumber = hrmsCommonPayroll.getEmployeeIdFormat(malaysiaFormTP2.getUseraccount(), profileHandlerDAO);
                    MalaysiaCompanyForm companyInfo = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(malaysiaFormTP2.getUseraccount().getUser().getCompany().getCompanyID(), month, year);
            		
            		if(companyInfo!=null){
            			User personInCharge = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", companyInfo.getTp2User().getUserID());
            			if(personInCharge!=null){
                    		tp2.setEmployerFullName(StringUtil.getFullName(personInCharge));
                    	}
            			Useraccount useraccountEmployer= (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", personInCharge.getUserID());
                        String employerReferenceNumber = hrmsCommonPayroll.getEmployeeIdFormat(useraccountEmployer, profileHandlerDAO);
                    	tp2.setEmployerReferenceNumber(employerReferenceNumber);
                    	tp2.setFullName(StringUtil.getFullName(malaysiaFormTP2.getUseraccount().getUser()));
                    	tp2.setOldIdentificationNumber(malaysiaFormTP2.getOldIdentificationNumber());
                    	tp2.setNewIdentificationNumber(malaysiaFormTP2.getNewIdentificationNumber());
                    	tp2.setArmyOrPoliceNumber(malaysiaFormTP2.getArmyOrPoliceNumber());
                    	tp2.setPassportNumber(malaysiaFormTP2.getPassportNumber());
                    	tp2.setEmployeeNumber(employeeReferenceNumber);
                    	tp2.setYear(String.valueOf(year));
                    	tp2.setMonth(String.valueOf(month+1));
                    	tp2.setIncomeTaxLHDNNumber(malaysiaFormTP2.getIncomeTaxLHDNNumber());
                    	if(malaysiaFormTP2.getCar()!=null){
                    		tp2.setCar(BigDecimal.valueOf(malaysiaFormTP2.getCar()).setScale(2).toString());
                    	}
                    	if(malaysiaFormTP2.getDriver()!=null){
                    		tp2.setDriver(BigDecimal.valueOf(malaysiaFormTP2.getDriver()).setScale(2).toString());
                    	}
                    	if(malaysiaFormTP2.getEntertainment()!=null){
                    		tp2.setEntertainment(BigDecimal.valueOf(malaysiaFormTP2.getEntertainment()).setScale(2).toString());
                    	}
                    	if(malaysiaFormTP2.getGardener()!=null){
                    		tp2.setGardener(BigDecimal.valueOf(malaysiaFormTP2.getGardener()).setScale(2).toString());
                    	}
                    	if(malaysiaFormTP2.getMaid()!=null){
                    		tp2.setMaid(BigDecimal.valueOf(malaysiaFormTP2.getMaid()).setScale(2).toString());
                    	}
                    	if(malaysiaFormTP2.getHolidayAllowance()!=null){
                    		tp2.setHolidayAllowance(BigDecimal.valueOf(malaysiaFormTP2.getHolidayAllowance()).setScale(2).toString());
                    	}
                    	if(malaysiaFormTP2.getMembership()!=null){
                    		tp2.setMembership(BigDecimal.valueOf(malaysiaFormTP2.getMembership()).setScale(2).toString());
                    	}
                    	if(malaysiaFormTP2.getHouseholdItems()!=null){
                    		tp2.setHouseholdItems(BigDecimal.valueOf(malaysiaFormTP2.getHouseholdItems()).setScale(2).toString());
                    	}
                    	tp2.setHousingBenefitProvidedByEmployer(getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_EMPLOYER_CONTRIBUTION_SUBSIDISED_INTEREST_HOUSING));
                    	Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date());
                        int yr = cal.get(Calendar.YEAR);
                        tp2.setTarikhD(String.valueOf(cal.get(Calendar.DATE)));
                        tp2.setTarikhM(String.valueOf(cal.get(Calendar.MONTH)+1));
                        tp2.setTarikhY(String.valueOf(yr));
                        tp2.setPosition(useraccountEmployer.getDesignationid().getValue());
                        tp2.setCompanyAddress(companyInfo.getCompany().getAddress());
            		}
            	}
            }
        	list.add(tp2);
        	map.put("datasource",  new JRBeanCollectionDataSource(list));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return new ModelAndView("pdfTP2", map);
    }
    
    
    public ModelAndView getTP3(HttpServletRequest request, HttpServletResponse response) throws SessionExpiredException, ServiceException {
    	Map<String, Object> map = new HashMap<String, Object>();
    	try{
    		String userid = request.getParameter("userid");
            String mnth = request.getParameter("month");
    		int month = 0;
        	int year = 0;
        	int frequency = 0;
        	if(!StringUtil.isNullOrEmpty(mnth)){
        		month = Integer.parseInt(mnth);
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("year"))){
        		year = Integer.parseInt(request.getParameter("year"));
        	}
            if(!StringUtil.isNullOrEmpty(request.getParameter("frequency"))){
            	frequency = Integer.parseInt(request.getParameter("frequency"));
            }
            List<TP3> list = new ArrayList<TP3>();
            TP3 tp3 = new TP3();
            if(!StringUtil.isNullOrEmpty(userid)){
            	MalaysiaFormTP3 malaysiaFormTP3 = malaysianStatutoryFormDAO.getEmployeeTP3(userid, month, year);
            	if(malaysiaFormTP3!=null){
            		MalaysiaCompanyForm companyInfo = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(malaysiaFormTP3.getUseraccount().getUser().getCompany().getCompanyID(), month, year);
                    if(companyInfo!=null){
                    	Calendar calendar = Calendar.getInstance();
                    	calendar.set(year, month, 1);
                    	Date date = calendar.getTime();
                    	
                    	tp3.setPreviousEmployer1(malaysiaFormTP3.getPreviousEmployer1());
                        tp3.setEmployerReferenceNo1(malaysiaFormTP3.getEmployerReferenceNo1());
                        tp3.setPreviousEmployer2(malaysiaFormTP3.getPreviousEmployer2());
                        tp3.setEmployerReferenceNo2(malaysiaFormTP3.getEmployerReferenceNo2());
                    	tp3.setFullName(StringUtil.getFullName(malaysiaFormTP3.getUseraccount().getUser()));
                    	tp3.setOldIdentificationNumber(malaysiaFormTP3.getOldIdentificationNumber());
                    	tp3.setNewIdentificationNumber(malaysiaFormTP3.getNewIdentificationNumber());
                    	tp3.setArmyOrPoliceNumber(malaysiaFormTP3.getArmyOrPoliceNumber());
                    	tp3.setPassportNumber(malaysiaFormTP3.getPassportNumber());
                    	tp3.setIncomeTaxFileNumber(malaysiaFormTP3.getIncomeTaxFileNumber());
                    	if(malaysiaFormTP3.getFreeSampleProductOnDiscount()!=null){
                    		tp3.setFreeSampleProductOnDiscount(BigDecimal.valueOf(malaysiaFormTP3.getFreeSampleProductOnDiscount()).setScale(2).toString());
                    	}
                    	tp3.setEmployeeLongServiceAward(malaysiaFormTP3.getEmployeeLongServiceAward());
                    	if(malaysiaFormTP3.getTotalContributionToKWSP()!=null){
                    		tp3.setTotalContributionToKWSP(BigDecimal.valueOf(malaysiaFormTP3.getTotalContributionToKWSP()).setScale(2).toString());
                    	}
                    	if(malaysiaFormTP3.getTuitionfees()!=null){
                    		tp3.setTuitionfees(BigDecimal.valueOf(malaysiaFormTP3.getTuitionfees()).setScale(2).toString());
                    	}
                    	tp3.setTotalZakatContribution(getPaidZakatForUser(year, month, userid, String.valueOf(frequency)));
                    	tp3.setTotalForPCB(getAccumulatedIncomeTax(year, month, userid, frequency));
                    	MalaysianIncomeTax incomeTax = getMalaysianIncomeTax(request, userid, date, frequency);
                    	if(incomeTax!=null){
                    		tp3.setTotalTaxableAmount(BigDecimal.valueOf(incomeTax.getTaxableIncome()).setScale(2).toString());
                    	}
                    	tp3.setChildCareAllowance(getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_CHILD_CARE_ALLOWANCE));
                		tp3.setLifeSupportingEquipment(getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_MEDICAL_EQUIPMENT));
                		tp3.setMedicalExpensesForParents(getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_MEDICAL_EXPENSES_PARENTS));
                		String seriousIll = getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_MEDICAL_SERIOUS_DISEASE);
                        tp3.setMedicalFeesForSeriousIll(seriousIll);
                        String medicalCheckUp = getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_MEDICAL_EXAMINATION);
                		tp3.setFullMedicalChekup(medicalCheckUp);
                        double seriousIllDbl = Double.parseDouble(seriousIll);
                        double medicalCheckUpDbl = Double.parseDouble(medicalCheckUp);
                        String sum = BigDecimal.valueOf(seriousIllDbl).setScale(2).add(BigDecimal.valueOf(medicalCheckUpDbl).setScale(2)).toString();
                        tp3.setSumOfMedicalFeesAndCheckup(sum);
                		tp3.setBookMagazinePurchase(getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_PURCHASE_BOOKS));
                		tp3.setComputerPurchase(getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_PURCHASE_COMPUTER));
                		tp3.setEducationalFund(getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_EDUCATION_FUND_SSPN));
                		tp3.setSportEquipmentPurchase(getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_PURCHASE_SPORTS_EUIPMENT));
                		tp3.setAlimonyPayment(getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_ALIMONY_PAYMENT));
                		tp3.setLifeInsurance(getCurrentLifeInsurance(year, userid));
                		tp3.setMedicalAndEducationalInsurance(getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_EDUCATION_MEDICAL_PREMIUM));
                		tp3.setInternetBroadbandSubscription(getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_INTERNET_BROADBAND));
                		tp3.setHousingLoanInterest(getCurrentAmountForComponent(year, month, userid, MalaysianIncomeTaxConstants.UNIQUE_CODE_HOUSING_LOAN));
                		if(malaysiaFormTP3.getContributionToPrivatePension()!=null){
                			tp3.setContributionToPrivatePension(BigDecimal.valueOf(malaysiaFormTP3.getContributionToPrivatePension()).setScale(2).toString());
                		}
                		if(malaysiaFormTP3.getTotalAllowance()!=null){
                			tp3.setTotalAllowance(BigDecimal.valueOf(malaysiaFormTP3.getTotalAllowance()).setScale(2).toString());
                		}
                		if(malaysiaFormTP3.getOtherAllowance()!=null){
                			tp3.setOtherAllowances(BigDecimal.valueOf(malaysiaFormTP3.getOtherAllowance()).setScale(2).toString());
                		}
                    	Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date());
                        int yr = cal.get(Calendar.YEAR);
                        tp3.setTarikhD(String.valueOf(cal.get(Calendar.DATE)));
                        tp3.setTarikhM(String.valueOf(cal.get(Calendar.MONTH)+1));
                        tp3.setTarikhY(String.valueOf(yr));
                        
                    }
                    
                }	
            }
        	list.add(tp3);
        	map.put("datasource",  new JRBeanCollectionDataSource(list));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return new ModelAndView("pdfTP3", map);
    }
    

    public ModelAndView getEA(HttpServletRequest request, HttpServletResponse response) throws SessionExpiredException, ServiceException {
    	Map<String, Object> map = new HashMap<String, Object>();
    	try{
    		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    		String userid = request.getParameter("userid");
            String mnth = request.getParameter("month");
    		int month = 0;
        	int year = 0;
        	int frequency = 0;
        	if(!StringUtil.isNullOrEmpty(mnth)){
        		month = Integer.parseInt(mnth);
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("year"))){
        		year = Integer.parseInt(request.getParameter("year"));
        	}
            if(!StringUtil.isNullOrEmpty(request.getParameter("frequency"))){
            	frequency = Integer.parseInt(request.getParameter("frequency"));
            }
            List<EA> list = new ArrayList<EA>();
            EA ea = new EA();
            if(!StringUtil.isNullOrEmpty(userid)){
            	MalaysiaFormEA malaysiaFormEA = malaysianStatutoryFormDAO.getEmployeeEA(userid, month, year);
            	if(malaysiaFormEA!=null){
            		
            		ea.setSerialNumber(malaysiaFormEA.getSerialNumber());
            		ea.setEmployerERefNumber(malaysiaFormEA.getEmployerERefNumber());
            		ea.setIncomeTaxFileNumber(malaysiaFormEA.getIncomeTaxFileNumber());
            		ea.setIncomeTaxBranch(malaysiaFormEA.getIncomeTaxBranch());
            		ea.setFullName(StringUtil.getFullName(malaysiaFormEA.getUseraccount().getUser()));
            		ea.setPosition(malaysiaFormEA.getUseraccount().getDesignationid().getValue());
            		ea.setEmployeeNumber(new HrmsCommonPayroll().getEmployeeIdFormat(malaysiaFormEA.getUseraccount(), profileHandlerDAO));
            		ea.setOldIdentificationNumber(malaysiaFormEA.getOldIdentificationNumber());
            		ea.setNewIdentificationNumber(malaysiaFormEA.getNewIdentificationNumber());
            		ea.setAccNumberKWSP(malaysiaFormEA.getAccNumberKWSP());
            		
            		Empprofile empprofile = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", malaysiaFormEA.getUseraccount().getUserID());
            		if(empprofile!=null){
            			if(empprofile.getJoindate()!=null){
            				ea.setJoiningDate(sdf.format(empprofile.getJoindate()));
            			}
            			if(empprofile.getRelievedate()!=null){
            				ea.setTerminationDate(sdf.format(empprofile.getRelievedate()));
            			}
            		}
            		if(malaysiaFormEA.getIncomeTaxPaidByEmployer()!=null){
            			ea.setIncomeTaxPaidByEmployer(BigDecimal.valueOf(malaysiaFormEA.getIncomeTaxPaidByEmployer()).setScale(2).toString());
            		}
            		if(malaysiaFormEA.getCarAndPetrol()!=null){
            			ea.setCarAndPetrol(BigDecimal.valueOf(malaysiaFormEA.getCarAndPetrol()).setScale(2).toString());
            		}
            		ea.setCarType(malaysiaFormEA.getCarType());
            		if(malaysiaFormEA.getCarYearMake()!=null){
            			ea.setCarYearMake(sdf.format(malaysiaFormEA.getCarYearMake()));
            		}
            		ea.setCarModel(malaysiaFormEA.getCarModel());
            		if(malaysiaFormEA.getDriverWages()!=null){
            			ea.setDriverWages(BigDecimal.valueOf(malaysiaFormEA.getDriverWages()).setScale(2).toString());
            		}
            		if(malaysiaFormEA.getEntertainment()!=null){
            			ea.setEntertainment(BigDecimal.valueOf(malaysiaFormEA.getEntertainment()).setScale(2).toString());
            		}
            		if(malaysiaFormEA.getHandphone()!=null){
            			ea.setHandphone(BigDecimal.valueOf(malaysiaFormEA.getHandphone()).setScale(2).toString());
            		}
            		if(malaysiaFormEA.getMaidAndGardener()!=null){
            			ea.setMaidAndGardener(BigDecimal.valueOf(malaysiaFormEA.getMaidAndGardener()).setScale(2).toString());
            		}
            		if(malaysiaFormEA.getAirTicketsForHolidays()!=null){
            			ea.setAirTicketsForHolidays(BigDecimal.valueOf(malaysiaFormEA.getAirTicketsForHolidays()).setScale(2).toString());
            		}
            		if(malaysiaFormEA.getOtherBenefitsForClothingAndFoods()!=null){
            			ea.setOtherBenefitsForClothingAndFoods(BigDecimal.valueOf(malaysiaFormEA.getOtherBenefitsForClothingAndFoods()).setScale(2).toString());
            		}
            		ea.setHousingAddress(malaysiaFormEA.getHousingAddress());
            		if(malaysiaFormEA.getRefundsFromKWSPOther()!=null){
            			ea.setRefundsFromKWSPOther(BigDecimal.valueOf(malaysiaFormEA.getRefundsFromKWSPOther()).setScale(2).toString());
            		}
            		if(malaysiaFormEA.getCompensationLossWork()!=null){
            			ea.setCompensationLossWork(BigDecimal.valueOf(malaysiaFormEA.getCompensationLossWork()).setScale(2).toString());
            		}
            		double total = 0;
            		if(malaysiaFormEA.getRetirementPayment()!=null){
            			total =+ malaysiaFormEA.getRetirementPayment();
            			ea.setRetirementPayment(BigDecimal.valueOf(malaysiaFormEA.getRetirementPayment()).setScale(2).toString());
            		}
            		if(malaysiaFormEA.getPeriodicalPayment()!=null){
            			total =+ malaysiaFormEA.getPeriodicalPayment();
            			ea.setPeriodicalPayment(BigDecimal.valueOf(malaysiaFormEA.getPeriodicalPayment()).setScale(2).toString());
            		}
            		ea.setTotal(BigDecimal.valueOf(total).setScale(2).toString());
            		if(malaysiaFormEA.getCp38Deduction()!=null){
            			ea.setCp38Deduction(BigDecimal.valueOf(malaysiaFormEA.getCp38Deduction()).setScale(2).toString());
            		}
            		ea.setZakatDeduction(getPaidZakatForUser(year, month, userid, String.valueOf(frequency)));
            		
            		ea.setName(malaysiaFormEA.getName());
            		if(malaysiaFormEA.getPortionOfKWSP()!=null){
            			ea.setPortionOfKWSP(BigDecimal.valueOf(malaysiaFormEA.getPortionOfKWSP()).setScale(2).toString());
            		}
            		ea.setForYear(String.valueOf(year));
            		ea.setTypeOfIncome(malaysiaFormEA.getTypeOfIncome());
            		if(malaysiaFormEA.getContributionKWSP()!=null){
            			ea.setContributionKWSP(BigDecimal.valueOf(malaysiaFormEA.getContributionKWSP()).setScale(2).toString());
            		}
            		
            		PayrollHistory history = hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, MalaysianIncomeTaxUtil.getEndDateOfMonth(year, month), frequency, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);
            		if(history!=null){
            			ea.setDeductionPCB(BigDecimal.valueOf(history.getIncometaxAmount()).setScale(2).toString());
            			ea.setSalary(BigDecimal.valueOf(history.getNet()).setScale(2).toString());
            			ea.setBonus(BigDecimal.valueOf(history.getOtherRemuneration()).setScale(2).toString());
            		}
            		ea.setDate(sdf.format(MalaysianIncomeTaxUtil.getEndDateOfMonth(year, month)));
            		if(malaysiaFormEA.getNonTaxableAmount()!=null){
            			ea.setNonTaxableIncome(BigDecimal.valueOf(malaysiaFormEA.getNonTaxableAmount()).setScale(2).toString());
            		}
            		MalaysiaCompanyForm companyInfo = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(malaysiaFormEA.getUseraccount().getUser().getCompany().getCompanyID(), 0, 2012);
            		if(companyInfo!=null){
            			if(companyInfo.getEaUser()!=null){
            				ea.setRepresentativeName(StringUtil.getFullName(companyInfo.getEaUser()));
            				Useraccount useraccount = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", companyInfo.getEaUser().getUserID());
                			if(useraccount!=null){
                				ea.setRepresentativePosition(useraccount.getDesignationid().getValue());
                				ea.setEmployerNameAddress(StringUtil.getFullName(useraccount.getUser())+" "+useraccount.getUser().getAddress());
                			}
            			}           	
                    }
                }	
            }
        	list.add(ea);
        	map.put("datasource",  new JRBeanCollectionDataSource(list));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return new ModelAndView("pdfEA", map);
    }
    
    
    public String getCurrentAmountForComponent(int year, int month, String userid, int uniquecode){
        double amount = 0;

        try {
            
            Date startdate = MalaysianIncomeTaxUtil.getStartDateOfMonth(year, month);
            Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, month);

            List<MalaysianUserTaxComponentHistory> userComponent = malaysianIncomeTaxDAO.getUserIncomeTaxComponentHistoryForParticularComponentByPassingComponentsUniqueCode(userid, startdate, enddate, uniquecode);

            for (MalaysianUserTaxComponentHistory comp : userComponent) {

                amount = amount + comp.getAmount();
            }

        } catch (ParseException ex) {
            Logger.getLogger(MalaysianStatutoryFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(amount).setScale(2).toString();
    }

    public String getCurrentAmountForComponentWithLimit(int year, int month, String userid, int uniquecode, String accumulatedAmountforCurrentComponent){
        double amount = 0;

        try {

            Date startdate = MalaysianIncomeTaxUtil.getStartDateOfMonth(year, month);
            Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, month);
            double accumulatedAmount = Double.parseDouble(accumulatedAmountforCurrentComponent);

            List<MalaysianUserTaxComponentHistory> userComponent = malaysianIncomeTaxDAO.getUserIncomeTaxComponentHistoryForParticularComponentByPassingComponentsUniqueCode(userid, startdate, enddate, uniquecode);

            for (MalaysianUserTaxComponentHistory comp : userComponent) {

                double compLimit = Double.parseDouble(comp.getDeduction().getAmount());
                if(compLimit>0){
                    if(accumulatedAmount > compLimit){ // If already exceed from its max limit

                        amount = 0;

                    } else {

                        if ( (comp.getAmount()+accumulatedAmount) > compLimit) {
                            amount = compLimit-accumulatedAmount;
                        }else {
                            amount = comp.getAmount();
                        }
                        
                    }
                } else {
                    amount = amount + comp.getAmount();
                }
                   
            }

        } catch (ParseException ex) {
            Logger.getLogger(MalaysianStatutoryFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(amount).setScale(2).toString();
    }

    public String getMedicalCheckUpAmount(String accumulatedMedicalCheckup , String accumulatedMedicalExpensesForSeriousDisease){
        double medicalChkUp = 0;
        double medicalDisease = 0;

        try {
            medicalChkUp = Double.parseDouble(accumulatedMedicalCheckup);
            medicalDisease = Double.parseDouble(accumulatedMedicalExpensesForSeriousDisease);

            if(medicalChkUp > MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP){
                medicalChkUp = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP;
            }
            

        } catch (Exception ex) {
            Logger.getLogger(MalaysianStatutoryFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(medicalChkUp).setScale(2).toString();
    }

    public String getCurrentMedicalCheckUpAmount(String currentMedicalCheckup, String accumulatedMedicalCheckup , String accumulatedMedicalExpensesForSeriousDisease){
        double medicalChkUp = 0;
        double medicalDisease = 0;
        double currentmedicalChkUp = 0;

        try {

            currentmedicalChkUp = Double.parseDouble(currentMedicalCheckup);
            medicalChkUp = Double.parseDouble(accumulatedMedicalCheckup);
            medicalDisease = Double.parseDouble(accumulatedMedicalExpensesForSeriousDisease);

            if(medicalChkUp >= MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP){
                currentmedicalChkUp =0;
            } else {

                if((medicalChkUp+currentmedicalChkUp) > MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP){
                    currentmedicalChkUp = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP - medicalChkUp;
                }
            }
            


        } catch (Exception ex) {
            Logger.getLogger(MalaysianStatutoryFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(currentmedicalChkUp).setScale(2).toString();
    }

    public String getCurrentMedicalSeriousDiseaseAmount(String currentMedicalCheckup, String currentMedicalExpensesForSeriousDisease, String accumulatedMedicalCheckup , String accumulatedMedicalExpensesForSeriousDisease){
        double medicalChkUp = 0;
        double medicalDisease = 0;
        double currentmedicalChkUp = 0;
        double currentSeriousDisease = 0;

        try {

            currentmedicalChkUp = Double.parseDouble(currentMedicalCheckup);
            currentSeriousDisease = Double.parseDouble(currentMedicalExpensesForSeriousDisease);
            medicalChkUp = Double.parseDouble(accumulatedMedicalCheckup);
            medicalDisease = Double.parseDouble(accumulatedMedicalExpensesForSeriousDisease);

            if((medicalDisease +medicalChkUp ) >= MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP_AND_SERIOUS_DISEASE){
                currentSeriousDisease =0;
            } else {

                if((medicalChkUp+medicalDisease+currentmedicalChkUp) > MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP_AND_SERIOUS_DISEASE){
                    currentSeriousDisease = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP_AND_SERIOUS_DISEASE- (medicalChkUp+medicalDisease);
                } 
            }



        } catch (Exception ex) {
            Logger.getLogger(MalaysianStatutoryFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(currentSeriousDisease).setScale(2).toString();
    }

    public String getMedicalExpenseforSeriousDiseases(String accumulatedMedicalCheckup , String accumulatedMedicalExpensesForSeriousDisease){
        double medicalChkUp = 0;
        double medicalDisease = 0;

        try {
            medicalChkUp = Double.parseDouble(accumulatedMedicalCheckup);
            medicalDisease = Double.parseDouble(accumulatedMedicalExpensesForSeriousDisease);

            if(medicalChkUp > MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP){
                medicalChkUp =  MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP;
            }
            if((medicalDisease+medicalChkUp) > MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP_AND_SERIOUS_DISEASE){
                medicalDisease = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_MEDICAL_CHECKUP_AND_SERIOUS_DISEASE - medicalChkUp;
            }
        } catch (Exception ex) {
            Logger.getLogger(MalaysianStatutoryFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(medicalDisease).setScale(2).toString();
    }

    public String getAccumulatedAmountForComponent(int year, int month, String userid, int uniquecode){
        double amount = 0;

        try {

            Date taxdate = getTaxDate(year);
            boolean limitFlag =false;
            Date startdate = MalaysianIncomeTaxUtil.getFinanacialDate(taxdate);
            Date prevMonthEndDate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, (month-1));
            if(month==0){
                prevMonthEndDate = null;
            }
            
            List<MalaysianUserTaxComponentHistory> componentHistory = malaysianIncomeTaxDAO.getUserIncomeTaxComponentHistoryForParticularComponentByPassingComponentsUniqueCode(userid, startdate, prevMonthEndDate, uniquecode);
            double componentLimit = 0;
            for (MalaysianUserTaxComponentHistory compHistory : componentHistory) {

                amount = amount + compHistory.getAmount();
                componentLimit = Double.parseDouble(compHistory.getDeduction().getAmount());
                if(componentLimit > 0){
                    limitFlag = true;
                } 
            }
            if(limitFlag){


                if(amount > componentLimit){ // If already exceed from its max limit

                    amount = componentLimit;

                } 

            }
            
        } catch (ParseException ex) {
            Logger.getLogger(MalaysianStatutoryFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(amount).setScale(2).toString();
    }

    public MalaysianUserIncomeTaxInfo getUserInfo(Date taxdate, String userid){

        List<MalaysianUserIncomeTaxInfo> userinfo = null;
        MalaysianUserIncomeTaxInfo user = null;

        Date taxdat = MalaysianIncomeTaxUtil.getFinanacialDate(taxdate);

        userinfo = malaysianIncomeTaxDAO.getUserIncomeTaxInfo(userid, taxdat);
        if(userinfo!=null && !userinfo.isEmpty()){
            user = userinfo.get(0);
        }

        return user;
    }

     public List<MalaysianUserTaxBenefits> getUserBenefits(Date taxdate, String userid){

        List<MalaysianUserTaxBenefits> userBenefits = null;

        Date startdate = MalaysianIncomeTaxUtil.getFinanacialDate(taxdate);
	    Date enddate = MalaysianIncomeTaxUtil.getEndFinanacialDate(taxdate);

	    userBenefits =  malaysianIncomeTaxDAO.getUserBenefits(startdate, enddate, userid);

        return userBenefits;
    }

    public String getPaidZakatForUser(int year, int month, String userid, String frequency){
        BigDecimal paidzakat = BigDecimal.ZERO;

        try {

            Date taxdate = getTaxDate(year);

            Date startdate = MalaysianIncomeTaxUtil.getFinanacialDate(taxdate);
            Date prevMonthEndDate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, (month-1));
            if(month==0){
                prevMonthEndDate = null;
            }
            List<PayrollHistory> listPH = hrmsPayrollDAOObj.getGeneratedSalariesForUser(userid, startdate, prevMonthEndDate, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL, frequency);
            StringBuffer buff = new StringBuffer();
            for (PayrollHistory ph : listPH){
                buff.append("'");
                buff.append(ph.getHistoryid());
                buff.append("',");
            }
            String payrollHistoryIds="";
            if(buff.length()>0){
                payrollHistoryIds=buff.substring(0, (buff.length()-1));
            }
            List<MalaysianUserTaxBenefits> taxBenefitsList = malaysianIncomeTaxDAO.getMalaysianUserTaxBenefits(payrollHistoryIds);

            for(MalaysianUserTaxBenefits taxben : taxBenefitsList) {
                paidzakat= paidzakat.add(BigDecimal.valueOf(taxben.getPaidZakat()));
            }

        } catch (Exception ex) {
            Logger.getLogger(MalaysianStatutoryFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return paidzakat.setScale(2).toString();
    }

    public String getAccumulatedIncomeTax(int year, int month, String userid, int frequency){
    	BigDecimal paid = BigDecimal.ZERO;
    	try{
    		Date taxdate = getTaxDate(year);

            Date startdate = MalaysianIncomeTaxUtil.getFinanacialDate(taxdate);
            Date prevMonthEndDate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, (month-1));
            if(month==0){
                prevMonthEndDate = null;
            }
            List<PayrollHistory> listPH = hrmsPayrollDAOObj.getGeneratedSalariesForUser(userid, startdate, prevMonthEndDate, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL, String.valueOf(frequency));
            for(PayrollHistory ph : listPH) {
            	paid= paid.add(BigDecimal.valueOf(ph.getIncometaxAmount()));
            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return paid.setScale(2).toString();
    }
    
    public Date getTaxDate(int year) throws ParseException{

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String txdate = year + "-01-01";
        Date taxdate = sdf.parse(txdate);

        return taxdate;

    }

    public String getCurrentLifeInsurance(int year, String userid){
        double currentLIC = 0;

        try {
            MalaysianUserIncomeTaxInfo userinfo = null;
            Date taxdate = getTaxDate(year);
            
            userinfo = malaysianIncomeTaxDAO.getUserInformation(userid, taxdate);

            if(userinfo!=null){
                currentLIC = userinfo.getCurrentLICAndOther();
            }

        } catch (Exception ex) {
            Logger.getLogger(MalaysianStatutoryFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(currentLIC).setScale(2).toString();
    }

    public String getCurrentZakat(int year, String userid){
        double zakat = 0;

        try {
            MalaysianUserIncomeTaxInfo userinfo = null;
            Date taxdate = getTaxDate(year);

            userinfo = malaysianIncomeTaxDAO.getUserInformation(userid, taxdate);

            if(userinfo!=null){
                zakat = userinfo.getCurrentZakat();
            }

        } catch (Exception ex) {
            Logger.getLogger(MalaysianStatutoryFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(zakat).setScale(2).toString();
    }

    public String getCurrentLifeInsuranceWithLimit(int year, String userid, String accumulatedLifeInsurance){
        double currentLIC = 0;

        try {
            MalaysianUserIncomeTaxInfo userinfo = null;
            Date taxdate = getTaxDate(year);
            double accumulatedLifeInsuranceDbl = Double.parseDouble(accumulatedLifeInsurance);
            userinfo = malaysianIncomeTaxDAO.getUserInformation(userid, taxdate);

            if(userinfo!=null){
                currentLIC = userinfo.getCurrentLICAndOther();

                if(accumulatedLifeInsuranceDbl > MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC){ // If already exceed from its max limit

                    currentLIC = 0;

                } else {

                    if ( (currentLIC+accumulatedLifeInsuranceDbl) > MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC) {
                        currentLIC = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC-accumulatedLifeInsuranceDbl;
                    }

                }
                
            }

        } catch (Exception ex) {
            Logger.getLogger(MalaysianStatutoryFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(currentLIC).setScale(2).toString();
    }

    public String getAccumulatedLifeInsurance(int year, int month, String userid){
        double accumulatedLIC = 0;

        try {
            Date taxdate = getTaxDate(year);
            Date endDate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, (month-1)); // Excluding current month
            if(month==0){
                endDate = null;
            }
            String ids = "";
            List<PayrollHistory> histories = malaysianIncomeTaxDAO.getPayrollHistories(userid, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL, MalaysianIncomeTaxUtil.getFinanacialDate(taxdate), endDate);

            for(PayrollHistory history: histories){
    			ids+=("'"+history.getHistoryid()+"',");
    		}
    		if(ids.length()>0){
    			List<MalaysianUserTaxBenefits> taxBenefits = malaysianIncomeTaxDAO.getMalaysianUserTaxBenefits(ids.substring(0, ids.length()-1));
    			for(MalaysianUserTaxBenefits obj: taxBenefits){

    				accumulatedLIC+=obj.getPaidLICAndOther();
    			}
    		}

            MalaysianUserIncomeTaxInfo user = getUserInfo(taxdate, userid);

            if(user!=null){
                
                accumulatedLIC+= user.getPreviousEmployerLIC();
            }

            if(accumulatedLIC > MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC){ // If already exceed from its max limit

                accumulatedLIC = MalaysianIncomeTaxConstants.MAX_LIMIT_FOR_EPF_AND_LIC;

            }

        } catch (Exception ex) {
            Logger.getLogger(MalaysianStatutoryFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return BigDecimal.valueOf(accumulatedLIC).setScale(2).toString();
    }

    public String getMonthName(int month, HttpServletRequest request){
      
        String[] monthName = { messageSource.getMessage("hrms.January", null, RequestContextUtils.getLocale(request)), 
        		               messageSource.getMessage("hrms.February", null, RequestContextUtils.getLocale(request)),
        		               messageSource.getMessage("hrms.March", null, RequestContextUtils.getLocale(request)),
        		               messageSource.getMessage("hrms.April", null, RequestContextUtils.getLocale(request)),
        		               messageSource.getMessage("hrms.May", null, RequestContextUtils.getLocale(request)),
        		               messageSource.getMessage("hrms.June", null, RequestContextUtils.getLocale(request)),
        		               messageSource.getMessage("hrms.July", null, RequestContextUtils.getLocale(request)),
        		               messageSource.getMessage("hrms.August", null, RequestContextUtils.getLocale(request)),
        		               messageSource.getMessage("hrms.September", null, RequestContextUtils.getLocale(request)),
        		               messageSource.getMessage("hrms.October", null, RequestContextUtils.getLocale(request)),
        		               messageSource.getMessage("hrms.November", null, RequestContextUtils.getLocale(request)),
        		               messageSource.getMessage("hrms.December", null, RequestContextUtils.getLocale(request)) };
        
        return monthName[month].toString();
    }
    
    public MalaysianIncomeTax getMalaysianIncomeTax(HttpServletRequest request, String userid, Date financialDate, int frequency){
    	MalaysianIncomeTax itax = null;
    	try{
    		itax  = (MalaysianIncomeTax) exportSalarySlipService.getIncomTaxObj(request);
            if(itax!=null){
                itax.setUserid(userid);
                itax.setFinancialDate(financialDate);
                itax.setFrequency(frequency);
            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return itax;
    }

     public ModelAndView getPCB2PDF(HttpServletRequest request, HttpServletResponse response) throws SessionExpiredException, ServiceException {

        ModelAndView modelAndView = null;
        try{
            
        	String userid = request.getParameter("userid");
    		int month = 0;
        	int year = 0;
        	int frequency = 0;
        	if(!StringUtil.isNullOrEmpty(request.getParameter("month"))){
        		month = Integer.parseInt(request.getParameter("month"));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("year"))){
        		year = Integer.parseInt(request.getParameter("year"));
        	}
            if(!StringUtil.isNullOrEmpty(request.getParameter("frequency"))){
            	frequency = Integer.parseInt(request.getParameter("frequency"));
            }
            List<PCB2> sampleList = new ArrayList<PCB2>(1);
            List<PCB2Employee> listEmployee = new ArrayList<PCB2Employee>(1);
            if(!StringUtil.isNullOrEmpty(userid)){
                String companyid = sessionHandlerImplObj.getCompanyid(request);
                MalaysiaCompanyForm companyInfo = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(companyid, month, year);
                List<MalaysiaFormPCB2> list = malaysianStatutoryFormDAO.getEmployeePCB2List(userid, year);
                BigDecimal amntPCB = BigDecimal.ZERO;
                BigDecimal amntCP38 = BigDecimal.ZERO;
                
                if(companyInfo!=null ){
                	HrmsCommonPayroll hrmsCommonPayroll = new HrmsCommonPayroll();
                	String empid = null;
                	
                    PCB2 report = new PCB2();
                	if(list!=null && !list.isEmpty()){
                		MalaysiaFormPCB2 pcb2 = list.get(0);
                		empid = hrmsCommonPayroll.getEmployeeIdFormat(pcb2.getUseraccount(), profileHandlerDAO);
                		report.setFullName(StringUtil.getFullName(pcb2.getUseraccount().getUser()));
                		report.setNewIdentificationNumber(pcb2.getNewIdentificationNumber());
                        report.setIncomeTaxFileNumber(pcb2.getIncomeTaxFileNumber());
                        report.setDesignation(pcb2.getUseraccount().getDesignationid().getValue());
                        report.setTelephoneNumber(pcb2.getUseraccount().getUser().getContactNumber());
                	} else {
                        
                        Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", userid);
                        empid = hrmsCommonPayroll.getEmployeeIdFormat(ua, profileHandlerDAO);
                        report.setFullName(StringUtil.getFullName(ua.getUser()));
                    }
                    

                    report.setTarikh(getDateForForms(new Date()));
                    report.setBranchIncomeTax(companyInfo.getBranch());
                    
                    
                    report.setEmployeeNumber(empid);
                    report.setIncomeTaxDeductionYear(String.valueOf(year));
                    
                    report.setYear(String.valueOf(year));

                    MalaysiaFormPCB2 objJan = getEmployeeInfoForMonth(list, 0);

                    if(objJan!=null){
                        Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, 0);
                        PayrollHistory ph =  hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);

                        report.setJanRMPCB(getPCBAmount(ph));
                        report.setJanRMCP38(getCP38Amount(objJan));
                        report.setJanNoResitPCB(getPCBReciept(objJan));
                        report.setJanNoResitCP38(getCP38Reciept(objJan));
                        report.setJanTarikhPCB(getPCBRecieptDate(objJan));
                        report.setJanTarikhCP38(getCP38RecieptDate(objJan));
                        
                        if(ph!=null){
                            BigDecimal amt = BigDecimal.valueOf(ph.getIncometaxAmount());
                            amntPCB = amntPCB.add(amt);
                        }
                        if(objJan.getDeductionAmountForCP38()!=null){
                            BigDecimal amt1 = BigDecimal.valueOf(objJan.getDeductionAmountForCP38());
                            amntCP38 = amntCP38.add(amt1);
                        }
                        
                    }



                    MalaysiaFormPCB2 objFeb = getEmployeeInfoForMonth(list, 1);
                    if(objFeb!=null){
                        Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, 1);
                        PayrollHistory ph =  hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);

                        report.setFebRMPCB(getPCBAmount(ph));
                        report.setFebRMCP38(getCP38Amount(objFeb));
                        report.setFebNoResitPCB(getPCBReciept(objFeb));
                        report.setFebNoResitCP38(getCP38Reciept(objFeb));
                        report.setFebTarikhPCB(getPCBRecieptDate(objFeb));
                        report.setFebTarikhCP38(getCP38RecieptDate(objFeb));

                       
                        if(ph!=null){
                            BigDecimal amt = BigDecimal.valueOf(ph.getIncometaxAmount());
                            amntPCB = amntPCB.add(amt);
                        }
                        if(objFeb.getDeductionAmountForCP38()!=null){
                            BigDecimal amt1 = BigDecimal.valueOf(objFeb.getDeductionAmountForCP38());
                            amntCP38 = amntCP38.add(amt1);
                        }
                        
                    }
                    
                    MalaysiaFormPCB2 objMar = getEmployeeInfoForMonth(list, 2);
                    if(objMar!=null){
                        
                        Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, 2);
                        PayrollHistory ph =  hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);

                        report.setMacRMPCB(getPCBAmount(ph));
                        report.setMacRMCP38(getCP38Amount(objMar));
                        report.setMacNoResitPCB(getPCBReciept(objMar));
                        report.setMacNoResitCP38(getCP38Reciept(objMar));
                        report.setMacTarikhPCB(getPCBRecieptDate(objMar));
                        report.setMacTarikhCP38(getCP38RecieptDate(objMar));

                        
                        if(ph!=null){
                            BigDecimal amt = BigDecimal.valueOf(ph.getIncometaxAmount());
                            amntPCB = amntPCB.add(amt);
                        }
                        
                        if(objMar.getDeductionAmountForCP38()!=null){
                        	BigDecimal amt1 = BigDecimal.valueOf(objMar.getDeductionAmountForCP38());
                        	amntCP38 = amntCP38.add(amt1);
                        }
                    }
                        

                    MalaysiaFormPCB2 objApr = getEmployeeInfoForMonth(list, 3);
                    if(objApr!=null){

                        Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, 3);
                        PayrollHistory ph =  hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);

                        report.setAprilRMPCB(getPCBAmount(ph));
                        report.setAprilRMCP38(getCP38Amount(objApr));
                        report.setAprilNoResitPCB(getPCBReciept(objApr));
                        report.setAprilNoResitCP38(getCP38Reciept(objApr));
                        report.setAprilTarikhPCB(getPCBRecieptDate(objApr));
                        report.setAprilTarikhCP38(getCP38RecieptDate(objApr));

                        
                        if(ph!=null){
                            BigDecimal amt = BigDecimal.valueOf(ph.getIncometaxAmount());
                            amntPCB = amntPCB.add(amt);
                        }
                        if(objApr.getDeductionAmountForCP38()!=null){
                            BigDecimal amt1 = BigDecimal.valueOf(objApr.getDeductionAmountForCP38());
                            amntCP38 = amntCP38.add(amt1);
                        }
                        
                    }
                    

                    MalaysiaFormPCB2 objMay = getEmployeeInfoForMonth(list, 4);
                    if(objMay!=null){
                        Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, 4);
                        PayrollHistory ph =  hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);

                        report.setMeiRMPCB(getPCBAmount(ph));
                        report.setMeiRMCP38(getCP38Amount(objMay));
                        report.setMeiNoResitPCB(getPCBReciept(objMay));
                        report.setMeiNoResitCP38(getCP38Reciept(objMay));
                        report.setMeiTarikhPCB(getPCBRecieptDate(objMay));
                        report.setMeiTarikhCP38(getCP38RecieptDate(objMay));

                        
                        if(ph!=null){
                            BigDecimal amt = BigDecimal.valueOf(ph.getIncometaxAmount());
                            amntPCB = amntPCB.add(amt);
                        }
                        if(objMay.getDeductionAmountForCP38()!=null){
                            BigDecimal amt1 = BigDecimal.valueOf(objMay.getDeductionAmountForCP38());
                            amntCP38 = amntCP38.add(amt1);
                        }
                    }
                    MalaysiaFormPCB2 objJun = getEmployeeInfoForMonth(list, 5);
                    if(objJun!=null){
                        Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, 5);
                        PayrollHistory ph =  hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);

                        report.setJunRMPCB(getPCBAmount(ph));
                        report.setJunRMCP38(getCP38Amount(objJun));
                        report.setJunNoResitPCB(getPCBReciept(objJun));
                        report.setJunNoResitCP38(getCP38Reciept(objJun));
                        report.setJunTarikhPCB(getPCBRecieptDate(objJun));
                        report.setJunTarikhCP38(getCP38RecieptDate(objJun));

                        
                        if(ph!=null){
                            BigDecimal amt = BigDecimal.valueOf(ph.getIncometaxAmount());
                            amntPCB = amntPCB.add(amt);
                        }
                        if(objJun.getDeductionAmountForCP38()!=null){
                            BigDecimal amt1 = BigDecimal.valueOf(objJun.getDeductionAmountForCP38());
                            amntCP38 = amntCP38.add(amt1);
                        }
                    }
                    

                    MalaysiaFormPCB2 objJul = getEmployeeInfoForMonth(list, 6);
                    if(objJul!=null){
                        Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, 6);
                        PayrollHistory ph =  hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);

                        report.setJulaiRMPCB(getPCBAmount(ph));
                        report.setJulaiRMCP38(getCP38Amount(objJul));
                        report.setJulaiNoResitPCB(getPCBReciept(objJul));
                        report.setJulaiNoResitCP38(getCP38Reciept(objJul));
                        report.setJulaiTarikhPCB(getPCBRecieptDate(objJul));
                        report.setJulaiTarikhCP38(getCP38RecieptDate(objJul));
                        if(ph!=null){
                            BigDecimal amt = BigDecimal.valueOf(ph.getIncometaxAmount());
                            amntPCB = amntPCB.add(amt);
                        }

                        
                        if(objJul.getDeductionAmountForCP38()!=null){
                            BigDecimal amt1 = BigDecimal.valueOf(objJul.getDeductionAmountForCP38());
                            amntCP38 = amntCP38.add(amt1);
                        }
                    }
                    

                    MalaysiaFormPCB2 objAug = getEmployeeInfoForMonth(list, 7);
                    if(objAug!=null){
                        Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, 7);
                        PayrollHistory ph =  hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);

                        report.setOgosRMPCB(getPCBAmount(ph));
                        report.setOgosRMCP38(getCP38Amount(objAug));
                        report.setOgosNoResitPCB(getPCBReciept(objAug));
                        report.setOgosNoResitCP38(getCP38Reciept(objAug));
                        report.setOgosTarikhPCB(getPCBRecieptDate(objAug));
                        report.setOgosTarikhCP38(getCP38RecieptDate(objAug));

                        if(ph!=null){
                            BigDecimal amt = BigDecimal.valueOf(ph.getIncometaxAmount());
                            amntPCB = amntPCB.add(amt);
                        }
                        if(objAug.getDeductionAmountForCP38()!=null){
                            BigDecimal amt1 = BigDecimal.valueOf(objAug.getDeductionAmountForCP38());
                            amntCP38 = amntCP38.add(amt1);
                        }
                    }


                    MalaysiaFormPCB2 objSep = getEmployeeInfoForMonth(list, 8);
                    if(objSep!=null){
                        Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, 8);
                        PayrollHistory ph =  hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);

                        report.setSeptRMPCB(getPCBAmount(ph));
                        report.setSeptRMCP38(getCP38Amount(objSep));
                        report.setSeptNoResitPCB(getPCBReciept(objSep));
                        report.setSeptNoResitCP38(getCP38Reciept(objSep));
                        report.setSeptTarikhPCB(getPCBRecieptDate(objSep));
                        report.setSeptTarikhCP38(getCP38RecieptDate(objSep));

                        if(ph!=null){
                            BigDecimal amt = BigDecimal.valueOf(ph.getIncometaxAmount());
                            amntPCB = amntPCB.add(amt);
                        }
                        if(objSep.getDeductionAmountForCP38()!=null){
                            BigDecimal amt1 = BigDecimal.valueOf(objSep.getDeductionAmountForCP38());
                            amntCP38 = amntCP38.add(amt1);
                        }

                    }
                    

                    MalaysiaFormPCB2 objOct = getEmployeeInfoForMonth(list, 9);
                    if(objOct!=null){
                        Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, 9);
                        PayrollHistory ph =  hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);

                        report.setOktoRMPCB(getPCBAmount(ph));
                        report.setOktoRMCP38(getCP38Amount(objOct));
                        report.setOktoNoResitPCB(getPCBReciept(objOct));
                        report.setOktoNoResitCP38(getCP38Reciept(objOct));
                        report.setOktoTarikhPCB(getPCBRecieptDate(objOct));
                        report.setOktoTarikhCP38(getCP38RecieptDate(objOct));

                        if(ph!=null){
                            BigDecimal amt = BigDecimal.valueOf(ph.getIncometaxAmount());
                            amntPCB = amntPCB.add(amt);
                        }
                        if(objOct.getDeductionAmountForCP38()!=null){
                            BigDecimal amt1 = BigDecimal.valueOf(objOct.getDeductionAmountForCP38());
                            amntCP38 = amntCP38.add(amt1);
                        }
                    }
                    
                    MalaysiaFormPCB2 objNov = getEmployeeInfoForMonth(list, 10);
                    if(objNov!=null){
                        Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, 10);
                        PayrollHistory ph =  hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);

                        report.setNovRMPCB(getPCBAmount(ph));
                        report.setNovRMCP38(getCP38Amount(objNov));
                        report.setNovNoResitPCB(getPCBReciept(objNov));
                        report.setNovNoResitCP38(getCP38Reciept(objNov));
                        report.setNovTarikhPCB(getPCBRecieptDate(objNov));
                        report.setNovTarikhCP38(getCP38RecieptDate(objNov));

                        if(ph!=null){
                            BigDecimal amt = BigDecimal.valueOf(ph.getIncometaxAmount());
                            amntPCB = amntPCB.add(amt);
                        }
                        if(objNov.getDeductionAmountForCP38()!=null){
                            BigDecimal amt1 = BigDecimal.valueOf(objNov.getDeductionAmountForCP38());
                            amntCP38 = amntCP38.add(amt1);
                        }
                    }
                    

                    MalaysiaFormPCB2 objDec = getEmployeeInfoForMonth(list, 11);

                    if(objDec!=null){
                        Date enddate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, 11);
                        PayrollHistory ph =  hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL);

                        report.setDesRMPCB(getPCBAmount(ph));
                        report.setDesRMCP38(getCP38Amount(objDec));
                        report.setDesNoResitPCB(getPCBReciept(objDec));
                        report.setDesNoResitCP38(getCP38Reciept(objDec));
                        report.setDesTarikhPCB(getPCBRecieptDate(objDec));
                        report.setDesTarikhCP38(getCP38RecieptDate(objDec));

                        if(ph!=null){
                            BigDecimal amt = BigDecimal.valueOf(ph.getIncometaxAmount());
                            amntPCB = amntPCB.add(amt);
                        }
                        if(objDec.getDeductionAmountForCP38()!=null){
                            BigDecimal amt1 = BigDecimal.valueOf(objDec.getDeductionAmountForCP38());
                            amntCP38 = amntCP38.add(amt1);
                        }
                    }
                    

                    report.setCompanyReferenceNumber(companyInfo.getEmployerno());

                    report.setTotalForPCB(String.valueOf(amntPCB.setScale(2)));
                    report.setTotalForCP38(String.valueOf(amntCP38.setScale(2)));

                    
                    report.setPersonInCharge(StringUtil.getFullName(companyInfo.getPcb2User()));
                    

                    sampleList.add(report);

                    Date taxdate = MalaysianIncomeTaxUtil.getEndDateOfMonth(year, month);

                    List<ComponentResourceMappingHistory> components = hrmsPayrollDAOObj.getSalaryDetails(userid, taxdate, frequency, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION);

                    for(ComponentResourceMappingHistory comp :components){

                        if(comp.getComponent().getSubtype().getComponenttype()==HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION){

                            PCB2Employee reportEmployee = new PCB2Employee();

                            reportEmployee.setAmountPCB(BigDecimal.valueOf(comp.getAmount()*MalaysianIncomeTaxConstants.EPF_PERCENT).setScale(2).toString());
                            reportEmployee.setIncomeMonth(getMonthName(month, request));
                            reportEmployee.setIncomeResit("");
                            reportEmployee.setIncomeType(comp.getComponent().getDescription());
                            reportEmployee.setIncomeYear(String.valueOf(year));
                            reportEmployee.setResitDate("");

                            listEmployee.add(reportEmployee);

                        }

                    }
               
                }
            }
            
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("datasource", new JRBeanCollectionDataSource(sampleList));
            parameterMap.put("SubReportData",  new JRBeanCollectionDataSource(listEmployee));

            modelAndView = new ModelAndView("pdfPCBTwo", parameterMap);
          
        }catch(Exception ex) {
          ex.printStackTrace();
        }


        return modelAndView;
    }

    public MalaysiaFormPCB2 getEmployeeInfoForMonth(List<MalaysiaFormPCB2> empInfoList, int month){

        MalaysiaFormPCB2 employeeInfo = null;
        
        for(MalaysiaFormPCB2 obj :empInfoList ){

            if(month==obj.getMonth()){

                employeeInfo= obj;
                break;
            }

        }

        return employeeInfo;

    }
    public String getPCBAmount (PayrollHistory obj){
    	Double value = 0.0;
    	if(obj!=null){
    		value = obj.getIncometaxAmount();
    	}
        return BigDecimal.valueOf(value).setScale(2).toString();

    }

    public String getCP38Amount (MalaysiaFormPCB2 obj){
    	Double value = 0.0;
    	if(obj!=null && obj.getDeductionAmountForCP38()!=null){
    		value = obj.getDeductionAmountForCP38();
    	}
        return BigDecimal.valueOf(value).setScale(2).toString();

    }

    public String getPCBReciept (MalaysiaFormPCB2 obj){
    	String value = "";
    	if(obj!=null){
    		value = obj.getTaxResitForPCB();
    	}
        return value;

    }

    public String getCP38Reciept (MalaysiaFormPCB2 obj){
    	String value = "";
    	if(obj!=null){
    		value = obj.getTaxResitForCP38();
    	}
        return value;
    }
    public String getPCBRecieptDate (MalaysiaFormPCB2 obj){

        return getDateForForms(obj.getTaxResitForCP38Date());

    }
    public String getCP38RecieptDate (MalaysiaFormPCB2 obj){

        return getDateForForms(obj.getTaxResitForCP38Date());

    }

    public ModelAndView authorizeStatutoryFormsData(HttpServletRequest request, HttpServletResponse response) {

        JSONObject jsonObject = new JSONObject();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
        	
            String[] empids = request.getParameterValues("empids");
            String[] formarr = request.getParameterValues("formarr");

            int month = Integer.parseInt(request.getParameter("month"));
            int year = Integer.parseInt(request.getParameter("year"));

            int action = Integer.parseInt(request.getParameter("action"));

            for(String formID : formarr){
                
                formID = formID.toString();

                malaysianStatutoryFormDAO.authorizeStatutoryFormsData(empids,action, formID, month,  year);
            }
            
            
            jsonObject.put("success", true);
            jsonObject.put("valid", true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jsonObject.toString());
    }

    public ModelAndView getStatusForStatutoryForms(HttpServletRequest request,HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            int month = 0;
        	int year = 0;
        	if(!StringUtil.isNullOrEmpty(request.getParameter("month"))){
        		month = Integer.parseInt(request.getParameter("month"));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("year"))){
        		year = Integer.parseInt(request.getParameter("year"));
        	}

            String userid = request.getParameter("userid");
        	MalaysiaFormAmanahSahamNasional asn = malaysianStatutoryFormDAO.getUserAmanahSahamNasional(userid, month, year);
            MalaysiaFormTabungHaji th = malaysianStatutoryFormDAO.getUserTabungHaji(userid, month, year);
            MalaysiaFormCP21 cp = malaysianStatutoryFormDAO.getUserCP21(userid, month, year);
            MalaysiaFormHRDLevy hl = malaysianStatutoryFormDAO.getUserHRDLevy(userid, month, year);

            MalaysiaFormTP1 tp1 = malaysianStatutoryFormDAO.getEmployeeTP1(userid, month, year);
            MalaysiaFormTP2 tp2 = malaysianStatutoryFormDAO.getEmployeeTP2(userid, month, year);
            MalaysiaFormTP3 tp3 = malaysianStatutoryFormDAO.getEmployeeTP3(userid, month, year);
            MalaysiaFormCP39 cp39 = malaysianStatutoryFormDAO.getEmployeeCP39(userid, month, year);
            MalaysiaFormCP39A cp39A = malaysianStatutoryFormDAO.getEmployeeCP39A(userid, month, year);
            MalaysiaFormPCB2 pcb2 = malaysianStatutoryFormDAO.getEmployeePCB2(userid, month, year);
            MalaysiaFormEA ea = malaysianStatutoryFormDAO.getEmployeeEA(userid, month, year);
            
            JSONObject tmpObj = new JSONObject();
            
            if(asn!=null){
                tmpObj = new JSONObject();
                tmpObj.put("formName", MalaysianIncomeTaxConstants.STATUTORY_FORM_NAME_AMANAH_SAHAM_NASIONAL);
                tmpObj.put("status", asn.getAuthorizeStatus());
                jarr.put(tmpObj);
            }

            if(th!=null){
                tmpObj = new JSONObject();
                tmpObj.put("formName", MalaysianIncomeTaxConstants.STATUTORY_FORM_NAME_TABUNG_HAJI);
                tmpObj.put("status", th.getAuthorizeStatus());
                jarr.put(tmpObj);
            }

            if(cp!=null){
                tmpObj = new JSONObject();
                tmpObj.put("formName", MalaysianIncomeTaxConstants.STATUTORY_FORM_NAME_CP21);
                tmpObj.put("status", cp.getAuthorizeStatus());
                jarr.put(tmpObj);

            }

            if(hl!=null){
                tmpObj = new JSONObject();
                tmpObj.put("formName", MalaysianIncomeTaxConstants.STATUTORY_FORM_NAME_HRD_LEVY);
                tmpObj.put("status", hl.getAuthorizeStatus());
                jarr.put(tmpObj);
            }


            if(tp1!=null){
                tmpObj = new JSONObject();
            	tmpObj.put("formName", MalaysianIncomeTaxConstants.STATUTORY_FORM_NAME_TP1);
                tmpObj.put("status", tp1.getAuthorizeStatus());
                jarr.put(tmpObj);
            }

            if(tp2!=null){
                tmpObj = new JSONObject();
            	tmpObj.put("formName", MalaysianIncomeTaxConstants.STATUTORY_FORM_NAME_TP2);
                tmpObj.put("status", tp2.getAuthorizeStatus());
                jarr.put(tmpObj);
            }


            if(tp3!=null){
                tmpObj = new JSONObject();
            	tmpObj.put("formName", MalaysianIncomeTaxConstants.STATUTORY_FORM_NAME_TP3);
                tmpObj.put("status", tp3.getAuthorizeStatus());
                jarr.put(tmpObj);
            }

            if(cp39!=null){
                tmpObj = new JSONObject();
            	tmpObj.put("formName", MalaysianIncomeTaxConstants.STATUTORY_FORM_NAME_CP39);
                tmpObj.put("status", cp39.getAuthorizeStatus());
                jarr.put(tmpObj);
            }

            if(cp39A!=null){
                tmpObj = new JSONObject();
            	tmpObj.put("formName", MalaysianIncomeTaxConstants.STATUTORY_FORM_NAME_CP39A);
                tmpObj.put("status", cp39A.getAuthorizeStatus());
                jarr.put(tmpObj);
            }


            if(pcb2!=null){
                tmpObj = new JSONObject();
            	tmpObj.put("formName", MalaysianIncomeTaxConstants.STATUTORY_FORM_NAME_PCB2);
                tmpObj.put("status", pcb2.getAuthorizeStatus());
                jarr.put(tmpObj);
            }
            
            if(ea!=null){
                tmpObj = new JSONObject();
            	tmpObj.put("formName", MalaysianIncomeTaxConstants.STATUTORY_FORM_NAME_EA);
                tmpObj.put("status", ea.getAuthorizeStatus());
                jarr.put(tmpObj);
            }
            

            jobj.put("data", jarr);
            jobj.put("count", jarr.length());
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    
    
    public ModelAndView getUserListForMalaysia(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject countobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
        	int month = 0;
        	int year = 0;
        	if(!StringUtil.isNullOrEmpty(request.getParameter("month"))){
        		month = Integer.parseInt(request.getParameter("month"));
        	}
        	if(!StringUtil.isNullOrEmpty(request.getParameter("year"))){
        		year = Integer.parseInt(request.getParameter("year"));
        	}
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList<String> filter_names =  new ArrayList<String>(Arrays.asList("ua.user.company.companyID","ua.user.deleteflag"));
            ArrayList<Object> filter_values = new ArrayList<Object>(Arrays.asList(companyid,0));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("allflag", false);
            requestParams.put("searchcol", new String[]{"u.firstName","u.lastName","ua.role.name","u.emailID"});
            if(request.getParameter("combo")!=null) {
                requestParams.put("combo",request.getParameter("combo"));
                requestParams.put("allflag", true);
            } else {
                requestParams.put("combo","");
            }
            StringUtil.checkpaging(requestParams, request);
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            if(!StringUtil.isNullOrEmpty(request.getParameter("stdate"))){
                filter_names.add(">=emp.joindate");
                filter_values.add(new Date(df.format(new Date(request.getParameter("stdate")))));
                filter_names.add("<=emp.joindate");
                filter_values.add(new Date(df.format(new Date(request.getParameter("enddate")))));
            }
            
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);

            kmsg = hrmsCommonDAOObj.getUserDetailsHrms(requestParams);
            List<Object> lst = kmsg.getEntityList();
            jarr = kwlCommonTablesDAOObj.getDetailsJson(lst,0,"com.krawler.common.admin.User");
            
            for(int ctr=0;ctr<jarr.length();ctr++){
                jobj = jarr.getJSONObject(ctr);
                Object[] row = (Object[]) lst.get(ctr);
                User u = (User)jobj.get("instance");
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", row[0].toString());
                if (row[1] != null) {
                    Empprofile e = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile",row[1].toString());
                    if (!StringUtil.isNullOrEmpty(e.getStatus())) {
                        jobj.put("status", e.getStatus());
                    } else {
                        jobj.put("status", messageSource.getMessage("hrms.recruitment.pending", null, RequestContextUtils.getLocale(request)));
                    }
                    jobj.put("joindate", (e.getJoindate() == null ? "" : sessionHandlerImplObj.getDateFormatter(request).format(e.getJoindate())));
                } else {
                    jobj.put("status", messageSource.getMessage("hrms.recruitment.InComplete", null, RequestContextUtils.getLocale(request)));
                }
                jobj.put("department", (ua.getDepartment() == null ? "" : ua.getDepartment().getId()));
                jobj.put("departmentname", (ua.getDepartment() == null ? "" : ua.getDepartment().getValue()));
                jobj.put("role", (ua.getRole() == null ? "" : ua.getRole().getID()));
                String name="";
                if(ua.getRole()!=null&&ua.getRole().getCompany()!=null){
                	name = ua.getRole().getName();
                }else{
                	name = messageSource.getMessage("hrms.common.role."+ua.getRole().getID(),null, ua.getRole().getName(), RequestContextUtils.getLocale(request));
                }
                jobj.put("rolename", (ua.getRole() == null ? "" : name));
                jobj.put("username", u.getUserLogin().getUserName());
                jobj.put("fullname",u.getFirstName()+" "+ (u.getLastName()==null?"":u.getLastName()));
                jobj.put("lastlogin", (u.getUserLogin().getLastActivityDate() == null ? "" : sessionHandlerImplObj.getDateFormatter(request).format(u.getUserLogin().getLastActivityDate())));
                jobj.put("designation", ua.getDesignationid() == null ? "" : ua.getDesignationid().getValue());
                jobj.put("designationid", ua.getDesignationid() == null ? "" : ua.getDesignationid().getId());
                jobj.put("templateid", ua.getTemplateid()!=null?ua.getTemplateid():"");
                jobj.put("salary", ua.getSalary());
                jobj.put("accno", ua.getAccno());
                jobj.put("frequency", u.getFrequency());
                requestParams.clear();
                requestParams.put("companyid",sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("empid",ua.getEmployeeid());
                if(ua.getEmployeeIdFormat()==null){
                	jobj.put("employeeid", ua.getEmployeeid() == null ? "" : profileHandlerDAO.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                }else{
                	requestParams.put("standardEmpId", profileHandlerDAO.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
                	requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                    jobj.put("employeeid", profileHandlerDAO.getNewEmployeeIdFormat(requestParams));
                }
                MalaysiaCompanyForm mc = malaysianStatutoryFormDAO.getMalaysiaCompanyForm(ua.getUser().getCompany().getCompanyID(), month, year);
                if(mc!=null){
                	jobj.put("companyFormStatus", true);
                }else{
                	jobj.put("companyFormStatus", false);
                }
                MalaysiaFormCP21 cp21 = malaysianStatutoryFormDAO.getUserCP21(ua.getUserID(), month, year);
                if(cp21!=null){
                	jobj.put("cp21Status", true);
                }else{
                	jobj.put("cp21Status", false);
                }
                MalaysiaFormTP1 tp1 = malaysianStatutoryFormDAO.getEmployeeTP1(ua.getUserID(), month, year);
                if(tp1!=null){
                	jobj.put("tp1Status", true);
                }else{
                	jobj.put("tp1Status", false);
                }
                MalaysiaFormTP2 tp2 = malaysianStatutoryFormDAO.getEmployeeTP2(ua.getUserID(), month, year);
                if(tp2!=null){
                	jobj.put("tp2Status", true);
                }else{
                	jobj.put("tp2Status", false);
                }
                MalaysiaFormTP3 tp3 = malaysianStatutoryFormDAO.getEmployeeTP3(ua.getUserID(), month, year);
                if(tp3!=null){
                	jobj.put("tp3Status", true);
                }else{
                	jobj.put("tp3Status", false);
                }
                MalaysiaFormPCB2 pcb2 = malaysianStatutoryFormDAO.getEmployeePCB2(ua.getUserID(), month, year);
                if(pcb2!=null){
                	jobj.put("pcb2Status", true);
                }else{
                	jobj.put("pcb2Status", false);
                }
                MalaysiaFormEA ea = malaysianStatutoryFormDAO.getEmployeeEA(ua.getUserID(), month, year);
                if(ea!=null){
                	jobj.put("eaStatus", true);
                }else{
                	jobj.put("eaStatus", false);
                }
                jarr.put(ctr,jobj);
            }           
            countobj.put("data", jarr);
            countobj.put("count", kmsg!=null?kmsg.getRecordTotalCount():0);
            jobj1.put("data", countobj);
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
