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
package com.krawler.spring.hrms.employee;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.APICallHandler;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.hrms.payroll.deduction.hrmsPayrollDeductionDAO;
import com.krawler.spring.hrms.payroll.tax.hrmsPayrollTaxDAO;
import com.krawler.spring.hrms.payroll.template.hrmsPayrollTemplateDAO;
import com.krawler.common.admin.userSalaryTemplateMap;
import com.krawler.spring.hrms.payroll.employercontribution.hrmsPayrollEmployerContributionDAO;
import com.krawler.spring.hrms.payroll.wages.hrmsPayrollWageDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import masterDB.EmployerContribution;
import masterDB.Historydetail;
import masterDB.Payhistory;
import masterDB.TempTemplateMagDeduction;
import masterDB.TempTemplatemaptax;
import masterDB.TempTemplatemapwage;
import masterDB.Template;
import masterDB.TemplateMapEmployerContribution;
import masterDB.Templatemapdeduction;
import masterDB.Templatemaptax;
import masterDB.Templatemapwage;
import masterDB.Wagemaster;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author shs
 */
public class hrmsEmpController extends MultiActionController implements MessageSourceAware {

    private String successView;
    private hrmsEmpDAO hrmsEmpDAOObj;
    private hrmsPayrollTemplateDAO hrmsPayrollTemplateDAOObj;
    private hrmsPayrollTaxDAO hrmsPayrollTaxDAOObj;
    private hrmsPayrollWageDAO hrmsPayrollWageDAOObj;
    private hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj;
    private hrmsPayrollEmployerContributionDAO hrmsPayrollEmployerContributionDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private MessageSource messageSource;

    public void setHrmsPayrollEmployerContributionDAO(hrmsPayrollEmployerContributionDAO hrmsPayrollEmployerContributionDAOObj) {
        this.hrmsPayrollEmployerContributionDAOObj = hrmsPayrollEmployerContributionDAOObj;
    }
         
    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }
    public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    public kwlCommonTablesDAO getKwlCommonTablesDAOObj() {
        return kwlCommonTablesDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setHrmsEmpDAO(hrmsEmpDAO hrmsEmpDAOObj1) {
        this.hrmsEmpDAOObj = hrmsEmpDAOObj1;
    }
    
    public void sethrmsPayrollTemplateDAO(hrmsPayrollTemplateDAO hrmsPayrollTemplateDAOObj1) {
        this.hrmsPayrollTemplateDAOObj = hrmsPayrollTemplateDAOObj1;
    }

    public void sethrmsPayrollTaxDAO(hrmsPayrollTaxDAO hrmsPayrollTaxDAOObj1) {
        this.hrmsPayrollTaxDAOObj = hrmsPayrollTaxDAOObj1;
    }

    public void sethrmsPayrollWageDAO(hrmsPayrollWageDAO hrmsPayrollWageDAOObj1) {
        this.hrmsPayrollWageDAOObj = hrmsPayrollWageDAOObj1;
    }

    public void sethrmsPayrollDeductionDAO(hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj1) {
        this.hrmsPayrollDeductionDAOObj = hrmsPayrollDeductionDAOObj1;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }



    public ModelAndView getEmpListPerGroupid(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        boolean success = false;
        JSONObject jobj1 = new JSONObject();
        try {
            String paycyclest = request.getParameter("paycyclestart");
            String paycycleed = request.getParameter("paycycleend");
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date paycyclestart = sdf.parse(paycyclest);
            Date paycycleend = sdf.parse(paycycleed);
            String s = String.valueOf(request.getSession().getAttribute("currencyid"));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            if(!StringUtil.isNullOrEmpty(request.getParameter("ss"))){
                requestParams.put("ss", request.getParameter("ss"));
                String[] searchcol = new String[] {"userAccount.user.firstName", "userAccount.user.lastName"};
                requestParams.put("searchcol", searchcol);
            }
            String cid = sessionHandlerImplObj.getCompanyid(request);
            requestParams.put("filter_names", Arrays.asList("salaryTemplate.templateid"));
            requestParams.put("filter_values", Arrays.asList(request.getParameter("groupid")));
            requestParams.put("order_by", Arrays.asList("userAccount.user.firstName"));
            requestParams.put("order_type", Arrays.asList("asc"));
            result = hrmsPayrollTemplateDAOObj.getUserSalaryTemplateMap(requestParams);

            JSONObject data = new JSONObject();
            String data2 = "";
            String finalString = "{ \"data\" : []}";//
            JSONObject jobj = new JSONObject();
            JSONObject jobjtemp = new JSONObject();
            JSONArray JA = new JSONArray();
            ArrayList arr = new ArrayList();
            int i = 0;
            List lst = (List)result.getEntityList();
            Iterator itr = lst.iterator();
            String userid = "";
            while(itr.hasNext()){
                requestParams.remove("ss");
                requestParams.remove("searchcol");
                userSalaryTemplateMap tempmap = (userSalaryTemplateMap) itr.next();
                if(tempmap.getUserAccount().getUserID().equals(userid))
                    continue;
                userid = tempmap.getUserAccount().getUserID();

                Date dt = new Date();
                requestParams.put("filter_names", Arrays.asList("userAccount.userID","<=effectiveDate"));
                requestParams.put("filter_values", Arrays.asList(tempmap.getUserAccount().getUserID(),paycyclestart));
                requestParams.put("append"," and (empProfile.relievedate is NULL or empProfile.relievedate > ?) ");
                requestParams.put("append_values", Arrays.asList(paycyclestart));
                requestParams.put("order_by", Arrays.asList("effectiveDate"));
                requestParams.put("order_type", Arrays.asList("desc"));
                result = hrmsPayrollTemplateDAOObj.getUserSalaryTemplateMap(requestParams);
                if(StringUtil.checkResultobjList(result)){
                    userSalaryTemplateMap tempmap1 = (userSalaryTemplateMap) result.getEntityList().get(0);
                    if(tempmap1.getSalaryTemplate().getTemplateid().equals(tempmap.getSalaryTemplate().getTemplateid())){
                        masterDB.Template group = tempmap1.getSalaryTemplate();
                        jobjtemp.put("templateid", group.getTemplateid());
                        String tempid = group.getTemplateid();
                        requestParams.put("empid", userid);
                        requestParams.put("templateid", group.getTemplateid());
                        requestParams.put("paycyclestart", paycyclestart);
                        requestParams.put("paycycleend", paycycleend);
                        List<Payhistory> payhistories = getPayhistory(requestParams);
                        if(payhistories.isEmpty()){
                        	request.setAttribute("salaryGenerated", false);
                        }else{
                        	request.setAttribute("salaryGenerated", true);
                        }
                        requestParams.put("payhistories", payhistories);
                        String tempdata = getPayEmployeePerTemp(requestParams, s, tempid,cid);
                        jobj1 = new JSONObject(tempdata);
                        JSONObject jobj2 = new JSONObject(jobj1.getString("data"));
                        JSONObject jobj3 = new JSONObject(jobj2.getString("Wages"));
                        String wtot = jobj3.getString("WageTotal");
                        double wagecash = Double.parseDouble(jobj3.getString("cashtotal"));
                        jobj3 = new JSONObject(jobj2.getString("Tax"));
                        String ttot = jobj3.getString("TaxTotal");
                        double taxcash = Double.parseDouble(jobj3.getString("cashtotal"));
                        jobj3 = new JSONObject(jobj2.getString("Deduction"));
                        String dtot = jobj3.getString("DeducTotal");
                        double deduccash = Double.parseDouble(jobj3.getString("cashtotal"));
                        
                        request.setAttribute("empid", userid);
                        data = getEmpPerTempidData1(request, tempid, wtot, ttot, dtot, wagecash, deduccash, taxcash,tempmap1.getUserAccount(),group,tempmap1);
                        if(!StringUtil.isNullOrEmpty(data.getString("data"))) {
                            JSONArray jarr = data.getJSONArray("data");
                            int k;
                            for(k=0;k<jarr.length();k++){
                                jobj.append("data",jarr.get(k));
                            }
                        }
                    }
                }
            }
            if(!jobj.has("data")) {
                jobj.put("data","");
            }
            jobj1.put("valid", true);
            jobj1.put("totalcount", 1);// lst.size());
            jobj1.put("data", jobj);//data
            success = true;
            jobj1.put("success", success);
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            jobj1.put("success", success);
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    public ModelAndView getEmpCycle(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        boolean success = false;
        JSONObject jobj1 = new JSONObject();
        JSONObject jsAray1 = new JSONObject();
        try {
            String paycyclest = request.getParameter("cyclestartdate");
            String paycycleed = request.getParameter("GetEmpListPerGroupid");
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date paycyclestart = sdf.parse(paycyclest);
//            Date paycycleend = sdf.parse(paycycleed);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            if(!StringUtil.isNullOrEmpty(request.getParameter("ss"))){
                requestParams.put("ss", request.getParameter("ss"));
                String[] searchcol = new String[] {"userAccount.user.firstName", "userAccount.user.lastName"};
                requestParams.put("searchcol", searchcol);
            }
            String cid = sessionHandlerImplObj.getCompanyid(request);
            requestParams.put("filter_names", Arrays.asList("salaryTemplate.templateid","<=effectiveDate"));
            requestParams.put("filter_values", Arrays.asList(request.getParameter("groupid"), paycyclestart));
            requestParams.put("order_by", Arrays.asList("effectiveDate"));
            requestParams.put("order_type", Arrays.asList("asc"));
            result = hrmsPayrollTemplateDAOObj.getUserSalaryTemplateMap(requestParams);

            List lst = (List)result.getEntityList();
            Iterator itr = lst.iterator();
            
            while(itr.hasNext()){
                userSalaryTemplateMap tempmap = (userSalaryTemplateMap) itr.next();
                jsAray1.put("effdate", sdf.format(tempmap.getEffectiveDate()));
            }
            if(!jsAray1.has("effdate")) {
                jsAray1.put("effdate","");
            }
            success = true;
            jobj1.put("success", success);
            jobj1.put("valid", true);
            jobj1.put("data", jsAray1);
            
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            jobj1.put("success", success);
            jobj1.put("valid", true);
            jobj1.put("data", jsAray1);
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    @SuppressWarnings("finally")
	public List<Payhistory> getPayhistory(HashMap<String, Object> params){
    	List<Payhistory> payhistories = null;
    	try{
    		HashMap<String, Object> requestParams = new HashMap<String, Object>();
        	requestParams.put("userId", params.get("empid"));
        	requestParams.put("templateid", params.get("templateid"));
        	requestParams.put("paycyclestart", params.get("paycyclestart"));
        	requestParams.put("paycycleend", params.get("paycycleend"));
        	payhistories = hrmsPayrollTemplateDAOObj.getPayhistory(requestParams);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		return payhistories;
    	}
    }
    
    @SuppressWarnings("unchecked")
	public String getPayEmployeePerTemp(HashMap<String, Object> params, String currencyid, String rowTempGroup,String companyid) {
        KwlReturnObject result = null;
        String s = "";
        try {
        	HashMap<String, Object> requestParams = new HashMap<String, Object>();
        	List<Payhistory> payhistories = (List<Payhistory>) params.get("payhistories");
            if(!payhistories.isEmpty()){//Check for salary gerenated
            	List<Historydetail> historydetails = null;
            	Iterator<Historydetail> iterator = null;
            	Historydetail historydetail = null;
            	JSONObject jobj = new JSONObject();
            	JSONObject json = null;
             	Payhistory payhistory = payhistories.get(0);
            	requestParams.clear();
            	requestParams.put("payHistoryId", payhistory.getHistoryid());
            	requestParams.put("name", "Taxes");
                int len = 0;
                double total = 0;
                double cashtotal = 0;
                JSONObject jsonTax = new JSONObject();
                historydetails = hrmsPayrollTemplateDAOObj.getPayHistorydetail(requestParams);
                iterator = historydetails.iterator();
                while (iterator.hasNext()) {
                	historydetail = iterator.next();
                	JSONObject jsonObject = new JSONObject();
                	jsonObject.put("TaxId", "");
                	jsonObject.put("TaxRate", historydetail.getRate());
                	jsonObject.put("cash", historydetail.getAmount());
                	jsonObject.put("TaxType", historydetail.getType());
                	total += Double.parseDouble(historydetail.getAmount());
                	if(historydetail.getRate().equals("-1"))//for Income tax check
                		cashtotal += Double.parseDouble(historydetail.getAmount());
                	jsonTax.append("TAX", jsonObject);
                	len++;
                }
                json = new JSONObject();   
                json.put("TaxArray", jsonTax);
                json.put("NosTax", len);
                json.put("TaxTotal", total);
                json.put("cashtotal", 0);
                jobj.put("Tax", json);
                
                
                requestParams.clear();
            	requestParams.put("payHistoryId", payhistory.getHistoryid());
            	requestParams.put("name", "Wages");
                len = 0;
                total = 0;
                JSONObject jsonWage = new JSONObject();
                historydetails = hrmsPayrollTemplateDAOObj.getPayHistorydetail(requestParams);
                iterator = historydetails.iterator();
                while (iterator.hasNext()) {
                	historydetail = iterator.next();
                	JSONObject jsonObject = new JSONObject();
                	jsonObject.put("WageId", "");
                	jsonObject.put("WageRate", historydetail.getRate());
                	jsonObject.put("cash", historydetail.getAmount());
                	jsonObject.put("WageType", historydetail.getType());
                	total += Double.parseDouble(historydetail.getAmount());
                	jsonWage.append("Wage", jsonObject);
                	len++;
                }
                json = new JSONObject();  
                json.put("WagesArray", jsonWage);
                json.put("NosWage", len);
                json.put("WageTotal", total);
                json.put("cashtotal", 0);
                jobj.put("Wages", json);

                
                requestParams.clear();
            	requestParams.put("payHistoryId", payhistory.getHistoryid());
            	requestParams.put("name", "Deduction");
                len = 0;
                total = 0;
                JSONObject jsonDeduction = new JSONObject();
                historydetails = hrmsPayrollTemplateDAOObj.getPayHistorydetail(requestParams);
                iterator = historydetails.iterator();
                while (iterator.hasNext()) {
                	historydetail = iterator.next();
                	JSONObject jsonObject = new JSONObject();
                	jsonObject.put("DeducId", "");
                	jsonObject.put("DeducRate", historydetail.getRate());
                	jsonObject.put("cash", historydetail.getAmount());
                	jsonObject.put("DeducType", historydetail.getType());
                	total += Double.parseDouble(historydetail.getAmount());
                	jsonDeduction.append("Deduction", jsonObject);
                	len++;
                }
                json = new JSONObject();   
                json.put("DeducArray", jsonDeduction);
                json.put("NosDeduc", len);
                json.put("DeducTotal", total);
                json.put("cashtotal", 0);
                jobj.put("Deduction", json);
                
                requestParams.put("currencyid", currencyid);
                result = hrmsEmpDAOObj.getCurrencyDetails(requestParams);
                List<KWLCurrency> currencyList = result.getEntityList();
                KWLCurrency curncy = null;
                if(currencyList.size()>0)
                    curncy = (KWLCurrency) currencyList.get(0);
                
                requestParams.put("templateid", rowTempGroup);
                result =  hrmsPayrollTaxDAOObj.getTempTaxTemplateDetails(requestParams);
                List iteratorTax = result.getEntityList();
                String tco = String.valueOf(result.getRecordTotalCount());
                
                jobj.put("curval", curncy.getSymbol());
                jobj.put("success", true);
                JSONObject jobj1 = new JSONObject();
                jobj1.put("valid", true);
                jobj1.put("totalcount", tco);
                jobj1.put("data", jobj);
                s = jobj1.toString();
            }else{
        	requestParams.put("cid", companyid);
            requestParams.put("templateid", rowTempGroup);
            result = hrmsPayrollTemplateDAOObj.getTemplateDetails(requestParams);
            List templateList = result.getEntityList();
            Template templ = null;
            if(templateList.size()>0)
                templ = (Template) templateList.get(0);
            JSONObject jobj = new JSONObject();
            JSONObject jobjinloop = new JSONObject();
            if (Integer.parseInt(templ.getStatus().toString()) == 0 || Integer.parseInt(templ.getStatus().toString()) == 1) {
                requestParams = new HashMap<String, Object>();
                requestParams.put("filter_names",Arrays.asList("template.templateid"));
                requestParams.put("filter_values",Arrays.asList(rowTempGroup));
                result = hrmsPayrollTaxDAOObj.getTaxTemplateDetails(requestParams);
                List iteratorTax = result.getEntityList();
                String tco = String.valueOf(result.getRecordTotalCount());
                requestParams = new HashMap<String, Object>();
                requestParams.put("currencyid", currencyid);
                result = hrmsEmpDAOObj.getCurrencyDetails(requestParams);
                List currencyList = result.getEntityList();
                KWLCurrency curncy = null;
                if(currencyList.size()>0)
                    curncy = (KWLCurrency) currencyList.get(0);

                JSONObject jsAray = new JSONObject();
                int len = 0;
                int i = 0;
                double total = 0;
                double cashtotal = 0;
                while (i < iteratorTax.size()) {
                    Templatemaptax row = (Templatemaptax) iteratorTax.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("TaxId", row.getTaxmaster().getTaxid());
                    jsAray1.put("TaxRate", row.getTaxmaster().getRate() == 1 ? row.getRate() : "");
                    jsAray1.put("cash", row.getRate());
                    jsAray1.put("TaxType", row.getTaxmaster().getTaxtype());
                    total += row.getTaxmaster().getRate() == 1 ? Double.parseDouble(row.getRate()) : 0;
                    cashtotal += row.getTaxmaster().getRate() == 0 ? Double.parseDouble(row.getRate()) : 0;
                    jsAray.append("TAX", jsAray1);
                    len++;
                    i++;
                }
                jobjinloop.put("TaxArray", jsAray);
                jobjinloop.put("NosTax", len);
                jobjinloop.put("TaxTotal", total);
                jobjinloop.put("cashtotal", cashtotal);
                jobj.put("Tax", jobjinloop);
                requestParams = new HashMap<String, Object>();
                requestParams.put("filter_names",Arrays.asList("template.templateid"));
                requestParams.put("filter_values",Arrays.asList(rowTempGroup));
                result = hrmsPayrollWageDAOObj.getWageTemplateDetails(requestParams);
                List iteratorWage = result.getEntityList();
                len = 0;
                i = 0;
                total = 0;
                cashtotal = 0;
                jobjinloop = new JSONObject();
                jsAray = new JSONObject();
                while (i < iteratorWage.size()) {
                    Templatemapwage row = (Templatemapwage) iteratorWage.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("WageId", row.getWagemaster().getWageid());
                    total += row.getWagemaster().getRate() == 1 ? Double.parseDouble(row.getRate()) : 0;
                    cashtotal += row.getWagemaster().getRate() == 0 ? Double.parseDouble(row.getRate()) : 0;
                    jsAray1.put("WageRate", row.getWagemaster().getRate() == 1 ? row.getRate() : "");
                    jsAray1.put("cash", row.getRate());
                    jsAray1.put("WageType", row.getWagemaster().getWagetype());

                    jsAray.append("Wage", jsAray1);
                    len++;
                    i++;
                }
                jobjinloop.put("WagesArray", jsAray);
                jobjinloop.put("NosWage", len);
                jobjinloop.put("WageTotal", total);
                jobjinloop.put("cashtotal", cashtotal);
                jobj.put("Wages", jobjinloop);
                requestParams = new HashMap<String, Object>();
                requestParams.put("templateid", rowTempGroup);
                requestParams.put("filter_names",Arrays.asList("template.templateid"));
                requestParams.put("filter_values",Arrays.asList(rowTempGroup));
                result = hrmsPayrollDeductionDAOObj.getDeductionTemplateDetails(requestParams);
                List iteratorDeduc = result.getEntityList();
                len = 0;
                i = 0;
                total = 0;
                cashtotal = 0;
                jobjinloop = new JSONObject();
                jsAray = new JSONObject();
                while (i < iteratorDeduc.size()) {
                    Templatemapdeduction row = (Templatemapdeduction) iteratorDeduc.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("DeducId", row.getDeductionmaster().getDeductionid());
                    jsAray1.put("DeducRate", row.getDeductionmaster().getRate() == 1 ? row.getRate() : "");
                    jsAray1.put("cash", row.getRate());
                    jsAray1.put("DeducType", row.getDeductionmaster().getDeductiontype());
                    jsAray.append("Deduction", jsAray1);
                    total += row.getDeductionmaster().getRate() == 1 ? Double.parseDouble(row.getRate()) : 0;
                    cashtotal += row.getDeductionmaster().getRate() == 0 ? Double.parseDouble(row.getRate()) : 0;

                    len++;
                    i++;
                }
                
                jobjinloop.put("DeducArray", jsAray);
                jobjinloop.put("NosDeduc", len);
                jobjinloop.put("DeducTotal", total);
                jobjinloop.put("cashtotal", cashtotal);
                jobj.put("Deduction", jobjinloop);
                jobj.put("curval", curncy.getSymbol());
                jobj.put("success", true);
                JSONObject jobj1 = new JSONObject();
                jobj1.put("valid", true);
                jobj1.put("totalcount", tco);
                jobj1.put("data", jobj);
                s = jobj1.toString();
            } else if (Integer.parseInt(templ.getStatus().toString()) == 10) {
                requestParams = new HashMap<String, Object>();
                requestParams.put("templateid", rowTempGroup);
                result =  hrmsPayrollTaxDAOObj.getTempTaxTemplateDetails(requestParams);
                List iteratorTax = result.getEntityList();
                String tco = String.valueOf(result.getRecordTotalCount());
                requestParams = new HashMap<String, Object>();
                requestParams.put("currencyid", currencyid);
                result = hrmsEmpDAOObj.getCurrencyDetails(requestParams);
                List currencyList = result.getEntityList();
                KWLCurrency curncy = null;
                if(currencyList.size()>0)
                    curncy = (KWLCurrency) currencyList.get(0);
                JSONObject jsAray = new JSONObject();
                int len = 0;
                int i = 0;
                float total = 0;
                while (i < iteratorTax.size()) {
                    TempTemplatemaptax row = (TempTemplatemaptax) iteratorTax.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("TaxId", row.getTaxmaster().getTaxid());
                    jsAray1.put("TaxRate", row.getRate());
                    jsAray1.put("TaxType", row.getTaxmaster().getTaxtype());
                    total += Float.parseFloat(row.getRate());
                    jsAray.append("TAX", jsAray1);
                    len++;
                    i++;
                }
                jobjinloop.put("TaxArray", jsAray);
                jobjinloop.put("NosTax", len);
                jobjinloop.put("TaxTotal", total);
                jobj.put("Tax", jobjinloop);
                requestParams = new HashMap<String, Object>();
                requestParams.put("templateid", rowTempGroup);
                result = hrmsPayrollWageDAOObj.getTempWageTemplateDetails(requestParams);
                List iteratorWage = result.getEntityList();
                len = 0;
                i = 0;
                total = 0;
                jobjinloop = new JSONObject();
                jsAray = new JSONObject();
                while (i < iteratorWage.size()) {
                    TempTemplatemapwage row = (TempTemplatemapwage) iteratorWage.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("WageId", row.getWagemaster().getWageid());
                    total += Float.parseFloat(row.getRate());
                    jsAray1.put("WageRate", row.getRate());
                    jsAray1.put("WageType", row.getWagemaster().getWagetype());
                    jsAray.append("Wage", jsAray1);
                    len++;
                    i++;
                }
                jobjinloop.put("WagesArray", jsAray);
                jobjinloop.put("NosWage", len);
                jobjinloop.put("WageTotal", total);
                jobj.put("Wages", jobjinloop);
                requestParams = new HashMap<String, Object>();
                requestParams.put("templateid", rowTempGroup);
                result = hrmsPayrollDeductionDAOObj.getTempDeductionTemplateDetails(requestParams);
                List iteratorDeduc = result.getEntityList();
                len = 0;
                i = 0;
                total = 0;
                jobjinloop = new JSONObject();
                jsAray = new JSONObject();
                while (i < iteratorDeduc.size()) {
                    TempTemplateMagDeduction row = (TempTemplateMagDeduction) iteratorDeduc.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("DeducId", row.getDeductionmaster().getDeductionid());
                    jsAray1.put("DeducRate", row.getRate());
                    jsAray1.put("DeducType", row.getDeductionmaster().getDeductiontype());
                    jsAray.append("Deduction", jsAray1);
                    total += Float.parseFloat(row.getRate());
                    len++;
                    i++;
                }
                jobjinloop.put("DeducArray", jsAray);
                jobjinloop.put("NosDeduc", len);
                jobjinloop.put("DeducTotal", total);
                jobj.put("Deduction", jobjinloop);
                jobj.put("curval", curncy.getSymbol());
                jobj.put("success", true);
                JSONObject jobj1 = new JSONObject();
                jobj1.put("valid", true);
                jobj1.put("totalcount", tco);
                jobj1.put("data", jobj);
                s = jobj1.toString();
            }
            }
            return (s);
        } catch (Exception se) {
        	se.printStackTrace();
            return ("failed");
        } finally {
        }
    }

    public double getRecursiveAmountForPaySlip(Wagemaster wm, ArrayList arr, String salary) {
        double amt = 0;
        double sal = 0;
            sal = Double.parseDouble(salary);
            for(int i=arr.size()-1 ; i>=0 ; i--){
                amt = Double.parseDouble(arr.get(i).toString()) * sal / 100;
                sal = amt;
            }
        return sal;
    }

        @SuppressWarnings("deprecation")
		public JSONObject getEmpPerTempidData1(HttpServletRequest request, String tempID, String wtotal, String ttotal, String dtotal, Double wagecash, Double deduccash, Double taxcash,Useraccount ua,Template temp,userSalaryTemplateMap tempmap) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        boolean salaryGenerated = Boolean.parseBoolean(request.getAttribute("salaryGenerated").toString());
        
        try {
        	TimeZone timeZone = TimeZone.getTimeZone(kwlCommonTablesDAOObj.getTimeZone(request, sessionHandlerImplObj.getUserid(request)));
            String ss = request.getParameter("ss");
            String cmpid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
                User usr = ua.getUser();
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("empid", usr.getUserID());
                String lname = usr.getLastName() != null ? usr.getLastName() : "";
                jobjtemp.put("EName", usr.getFirstName() + " " + lname);
                jobjtemp.put("AccNo", ua.getAccno());
                jobjtemp.put("design", ua.getDesignationid() != null ? ua.getDesignationid().getValue() : "");
                jobjtemp.put("tempid", temp.getTemplateid());
                jobjtemp.put("showborder", temp.isShowborder());
                jobjtemp.put("tempinterval", temp.getPayinterval());
                jobjtemp.put("mappingid", tempmap.getMappingid());
                Double gross=0.0;
                Double deduction=0.0;
                Double taxablegross=0.0;
                Double ttax=0.0;
                Double netsal=0.0;
                if(!salaryGenerated){
                    //for wages
                    requestParams = new HashMap<String, Object>();
                    requestParams.put("filter_names",Arrays.asList("template.templateid"));
                    requestParams.put("filter_values",Arrays.asList(temp.getTemplateid()));
                    result = hrmsPayrollWageDAOObj.getWageTemplateDetails(requestParams);
                    List iteratorWage = result.getEntityList();
                    int cn = 0;
                    double total = tempmap.getBasic();
                    HashMap wagearr = new HashMap();
                    HashMap deducarr = new HashMap();
                    HashMap taxarr = new HashMap();

                    while (cn < iteratorWage.size()) {
                        Templatemapwage row = (Templatemapwage) iteratorWage.get(cn);
                        
                        if(row.getWagemaster().getRate() == 1){
                            masterDB.Wagemaster checkwage = row.getWagemaster();
                            if(checkwage.getComputeon()!=null&&checkwage.getComputeon()==0){
                                wagearr.put("deduc", checkwage.getCash());
                            }else if(checkwage.getComputeon()!=null&&checkwage.getComputeon()==1){
                                wagearr.put("earn", checkwage.getCash());
                            }else if(checkwage.getComputeon()!=null&&checkwage.getComputeon()==2){
                                wagearr.put("net", checkwage.getCash());
                            }
                            if(checkwage.getComputeon()!=null&&checkwage.getComputeon()==3){
                                Double amt = 0.0;
                                amt =hrmsPayrollWageDAOObj.calculatewagessalary(checkwage.getExpr(),tempmap.getMappingid());
                                Double perc = Double.parseDouble(row.getRate());
                                requestParams.put("templateid", temp.getTemplateid());
                                requestParams.put("wageid", checkwage.getWageid());
                                total += amt*perc/100;
                            }
                        } else {
                            total += 0;
                        }
                        cn++;
                    }
                    
                    //for deduction
                    requestParams.clear();
                    requestParams.put("templateid", temp.getTemplateid());
                    requestParams.put("filter_names",Arrays.asList("template.templateid"));
                    requestParams.put("filter_values",Arrays.asList(temp.getTemplateid()));
                    result = hrmsPayrollDeductionDAOObj.getDeductionTemplateDetails(requestParams);
                    iteratorWage = result.getEntityList();
                    cn = 0;
                    double amountdeduction = 0,totaldeduction = 0;
                    while (cn < iteratorWage.size()) {
                        Templatemapdeduction wm = (Templatemapdeduction) iteratorWage.get(cn);
                        if (wm.getDeductionmaster().getRate() == 1) {
                            masterDB.Deductionmaster checkdeduc = wm.getDeductionmaster();
                            if(checkdeduc.getComputeon()!=null&&checkdeduc.getComputeon()==0){
                                deducarr.put("deduc", checkdeduc.getCash());
                            }else if(checkdeduc.getComputeon()!=null&&checkdeduc.getComputeon()==1){
                                deducarr.put("earn", checkdeduc.getCash());
                            }else if(checkdeduc.getComputeon()!=null&&checkdeduc.getComputeon()==2){
                                deducarr.put("net", checkdeduc.getCash());
                            }
                            if(checkdeduc.getComputeon()!=null&&checkdeduc.getComputeon()==3){
                                Double amt = 0.0;
                                amt =hrmsPayrollWageDAOObj.calculatewagessalary(checkdeduc.getExpr(),tempmap.getMappingid());
                                Double perc = Double.parseDouble(wm.getRate());
                                requestParams.put("templateid", temp.getTemplateid());
                                requestParams.put("wageid", checkdeduc.getDeductionid());
                                totaldeduction += amt*perc/100;
                            }
                        } else {
                            totaldeduction += 0;
                        }
                        cn++;
                    }

                    //for tax
                    requestParams.clear();
                    requestParams.put("filter_names",Arrays.asList("template.templateid"));
                    requestParams.put("filter_values",Arrays.asList(temp.getTemplateid()));
                    result = hrmsPayrollTaxDAOObj.getTaxTemplateDetails(requestParams);
                    iteratorWage = result.getEntityList();
                    cn = 0;
                    double amounttax = 0,totaltax = 0;
                    while (cn < iteratorWage.size()) {
                        Templatemaptax wm = (Templatemaptax) iteratorWage.get(cn);
                        if (wm.getTaxmaster().getRate() == 1) {
                            masterDB.Taxmaster checktax = wm.getTaxmaster();
                            if(checktax.getComputeon()!=null&&checktax.getComputeon()==0){
                                taxarr.put("deduc", checktax.getCash());
                            }else if(checktax.getComputeon()!=null&&checktax.getComputeon()==1){
                                taxarr.put("earn", checktax.getCash());
                            }else if(checktax.getComputeon()!=null&&checktax.getComputeon()==2){
                                taxarr.put("net", checktax.getCash());
                            }
                            if(checktax.getComputeon()!=null&&checktax.getComputeon()==3){
                                Double amt = 0.0;
                                amt =hrmsPayrollWageDAOObj.calculatewagessalary(checktax.getExpr(),tempmap.getMappingid());
                                Double perc = Double.parseDouble(wm.getRate());
                                requestParams.put("templateid", temp.getTemplateid());
                                requestParams.put("wageid", checktax.getTaxid());
                                totaltax += amt*perc/100;
                            }
                        } else {
                            totaltax += 0;
                        }
                        cn++;
                    }

                    gross = wagecash + total;
                    deduction = deduccash + totaldeduction;
                    ttax = taxcash + totaltax;

                    JSONObject jsAray = new JSONObject();
                    deduction +=setUnpaidleaves(request,jsAray,ua.getSalary(),ua.getEmpProfile());

                    if(wagearr.get("deduc")!=null){
                       gross += deduction * (Double)wagearr.get("deduc") /100;
                    }
                    if(wagearr.get("earn")!=null){
                       gross += gross * (Double)wagearr.get("earn") /100;
                    }
                    if(wagearr.get("net")!=null){
                       gross += (gross - deduction) * (Double)wagearr.get("net") /100;
                    }

                    if(deducarr.get("deduc")!=null){
                       deduction += deduction * (Double)deducarr.get("deduc") /100;
                    }
                    if(deducarr.get("earn")!=null){
                       deduction += gross * (Double)deducarr.get("earn") /100;
                    }
                    if(deducarr.get("net")!=null){
                       deduction += (gross - deduction) * (Double)deducarr.get("net") /100;
                    }

                    if(taxarr.get("deduc")!=null){
                       ttax += deduction * (Double)taxarr.get("deduc") /100;
                    }
                    if(taxarr.get("earn")!=null){
                       ttax += gross * (Double)taxarr.get("earn") /100;
                    }
                    if(taxarr.get("net")!=null){
                       ttax += (gross - deduction) * (Double)taxarr.get("net") /100;
                    }
                    
                    netsal = gross - deduction - ttax;
                    
                    // TODO : set deduction of leaves before joining date here
                }
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                Date d2 = sdf.parse(request.getParameter("paycyclestart"));
                Date d3 = sdf.parse(request.getParameter("paycycleend"));
                String startDate = sdf1.format(d2);
                String endDate = sdf1.format(d3);
                requestParams.put("filter_names", Arrays.asList("userAccount.userID"));
                requestParams.put("filter_values", Arrays.asList(ua.getUserID()));
                requestParams.put("append","  and (effectiveDate > ? or (empProfile.relievedate < ? and empProfile.relievedate > ?)) ");
                requestParams.put("append_values", Arrays.asList(d2,d3,d2));
                requestParams.put("order_by", Arrays.asList("effectiveDate"));
                requestParams.put("order_type", Arrays.asList("asc"));
                result = hrmsPayrollTemplateDAOObj.getUserSalaryTemplateMap(requestParams);
                if(StringUtil.checkResultobjList(result)){
                    userSalaryTemplateMap tempmap1 = (userSalaryTemplateMap) result.getEntityList().get(0);
                    Date compareDate = tempmap1.getEffectiveDate();
                    Empprofile emp = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", tempmap1.getUserAccount().getUserID());
                    if(emp!=null){
                        if(emp.getRelievedate() != null){
                            if(emp.getRelievedate().after(d2) && emp.getRelievedate().before(d3)){
                                compareDate = emp.getRelievedate();
                            }
                        }
                    }
                    if(compareDate.compareTo(d3)<0){
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(compareDate);
                        cal.set(cal.DAY_OF_MONTH, (cal.get(cal.DAY_OF_MONTH)-1));
                        d3 = cal.getTime();
                        jobjtemp.put("paycycleend", sdf.format(d3));
                        
                        cal.setTime(d2);
                        int dayinmonth = 1;
                        if(temp.getPayinterval()==1)
                            dayinmonth = cal.getMaximum(cal.DAY_OF_MONTH);
                        else if(temp.getPayinterval()==3)
                            dayinmonth = 7;           ///
                        else if(temp.getPayinterval()==2){
                        	Calendar calendar = Calendar.getInstance();
                        	//calendar.setTimeZone(timeZone);
                        	calendar.setTime(d2);
                        	if(calendar.get(Calendar.DATE)<=15){
                        		dayinmonth = 15;
                        	}else{
                        		dayinmonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)-15;
                        	}
                        	d3 = sdf.parse(request.getParameter("paycycleend"));
                        }
                        long chargesec = d3.getTime()- d2.getTime()+(1000 * 60 * 60 * 24);
                        int chargedays = (int) (chargesec / (1000 * 60 * 60 * 24));
                        netsal += ttax;
                        netsal = netsal*chargedays/dayinmonth;
                        netsal -= ttax;
                        //ttax = ttax*chargedays/dayinmonth;
                        gross = gross*chargedays/dayinmonth;
                        deduction = deduction*chargedays/dayinmonth;
                    }
                }
                if(!salaryGenerated){
                	requestParams.clear();
                	requestParams.put("companyId", cmpid);
                	Company company = hrmsEmpDAOObj.getCompany(requestParams);
                	requestParams.put("company", company);
                	requestParams.put("incometax", true);
                	requestParams.put("netsal", netsal);
                	String empid = request.getAttribute("empid").toString();
                	Empprofile empprofile = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", empid);
                	requestParams.put("empprofile", empprofile);
                	requestParams.put("userSalaryTemplateMap", tempmap);
                	requestParams.put("payCycleStartDate", sdf.parse(request.getParameter("paycyclestart")));
                	requestParams.put("payCycleEndDate", sdf.parse(request.getParameter("paycycleend")));
                }else{
                	ttax = Double.parseDouble(ttotal);
                	deduction = Double.parseDouble(dtotal);
                	gross = Double.parseDouble(wtotal);
                	netsal = gross -deduction - ttax;
                }
                jobjtemp.put("salaryGenerated", String.valueOf(salaryGenerated));
                jobjtemp.put("Salary", String.valueOf(netsal));
                jobjtemp.put("Tax", String.valueOf(ttax));
                jobjtemp.put("Wage", String.valueOf(gross));
                jobjtemp.put("Deduc", String.valueOf(deduction));
                if(!StringUtil.isNullOrEmpty(request.getParameter("paycycleend"))&&!StringUtil.isNullOrEmpty(request.getParameter("paycyclestart"))){
                  
                requestParams = new HashMap<String, Object>();
                requestParams.put("userid", usr.getUserID());
                requestParams.put("createdfor", d3);
                requestParams.put("filter_names", Arrays.asList("userID.userID","paycyclestart","paycycleend"));
                requestParams.put("filter_values", Arrays.asList(usr.getUserID(),d2,d3));
                result = hrmsEmpDAOObj.getPayHistory(requestParams);
                
                  if(result.getRecordTotalCount()>0){
                      jobjtemp.put("generated","1");
                  }else{
                      jobjtemp.put("generated","0");
                  }
                }
                jobj.append("data", jobjtemp);
                jobj.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
        }
        return (jobj);
    }



    public ModelAndView setPayrollforTemp(HttpServletRequest request, HttpServletResponse response) {
        String msg = "",employeelist="";
        int count = 0,flag=0,employeecount = 1;
        KwlReturnObject result = null;
        boolean success = false, salaryflag = true;
        JSONObject jobj1 = new JSONObject();
        JSONObject jsAray = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            int x = 0;
            String cmpid = sessionHandlerImplObj.getCompanyid(request);
            String paycyclestartdate = request.getParameter("paycyclestart");
            String paycycleenddate = request.getParameter("paycycleend");
            CompanyPreferences cp = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", cmpid);
            String jsondata2 = request.getParameter("jsondata");
            JSONArray jarr2 = new JSONArray("[" + jsondata2 + "]");
            for (x = 0; x < jarr2.length(); x++) {
                int i = 0;
                JSONArray Wagejsondata = new JSONArray();
                JSONArray Taxjsondata = new JSONArray();
                JSONArray Deducjsondata = new JSONArray();
                JSONArray ECjsondata = new JSONArray();
                double wagetotalvalf = 0;
                double deductotalvalf = 0;
                double taxtotalvalf = 0;
                double ectotalvalf = 0;
                double wagedeductotf = 0;
                double amtot = 0;
                int trmLen1 = 0;
                JSONObject jobj2 = jarr2.getJSONObject(x);
                String tempid = jobj2.getString("template");
                String paycyclest = jobj2.getString("paycyclestart");
                String paycycleed = jobj2.getString("paycycleend");
                String ischanged = jobj2.getString("ischanged");
                String empid = jobj2.getString("empid");
                double unpaidleaves = Double.parseDouble(jobj2.getString("unpaidleaves"));
                String mappingid = jobj2.getString("mappingid");
                String salarystatus = jobj2.getString("salarystatus");
                int dayinmonth = 1;
                int chargedays = 1;
                Template temp = (Template)kwlCommonTablesDAOObj.getObject("masterDB.Template", tempid);
                userSalaryTemplateMap tempmap = (userSalaryTemplateMap)kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.userSalaryTemplateMap", mappingid);
                if(ischanged.equals("1") || unpaidleaves > 0){
                    int holiday = cp.getWeeklyholidays();
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                    Date d2 = sdf.parse(paycyclest);
                    Date d3 = sdf.parse(paycycleed);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(d2);
                    Date cyclestartdate = sdf.parse(paycyclestartdate);
                    Date cycleenddate = sdf.parse(paycycleenddate);
                    HashMap<Integer,Integer> p=new HashMap<Integer,Integer>();
                    p = StringUtil.getHolidayArr(holiday);
                    if(temp.getPayinterval()==1){//monthly
                        dayinmonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                        dayinmonth -= StringUtil.nonWorkingDays(cyclestartdate,cycleenddate,p);
                    }else if(temp.getPayinterval()==2){//Bi-monthly
                        Calendar c1 = Calendar.getInstance();
                        Calendar c2 = Calendar.getInstance();
                        c1.setTime(cyclestartdate);
                        c2.setTime(cycleenddate);
                        long diff = c2.getTimeInMillis() - c1.getTimeInMillis();
                        dayinmonth =(int)( diff / (24 * 60 * 60 * 1000));
                        dayinmonth += 1;
                        dayinmonth -= StringUtil.nonWorkingDays(cyclestartdate,cycleenddate,p);
                    }else if(temp.getPayinterval()==3){//weekly
                        dayinmonth = 7;
                        dayinmonth -= StringUtil.nonWorkingDays(cyclestartdate,cycleenddate,p);
                    }
                    long chargesec = d3.getTime()- d2.getTime()+(1000 * 60 * 60 * 24);
                    chargedays = (int) (chargesec / (1000 * 60 * 60 * 24));
                    chargedays -= StringUtil.nonWorkingDays(d2,d3,p);
                }
                
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("filter_names",Arrays.asList("template.templateid"));
                requestParams.put("filter_values",Arrays.asList(tempid));
                result = hrmsPayrollWageDAOObj.getWageTemplateDetails(requestParams);
                List iteratorWage = result.getEntityList();
                HashMap wagearr = new HashMap();
                HashMap deducarr = new HashMap();
                HashMap taxarr = new HashMap();
                HashMap ecarr = new HashMap();
                JSONObject depwageobj = null;
                JSONObject depobj = null;
                if(temp!=null){
                    depwageobj = new JSONObject();
                    depwageobj.put("type", "Basic");
                    depwageobj.put("Rate", tempmap.getBasic()*chargedays/dayinmonth);
                    depwageobj.put("amount", tempmap.getBasic()*chargedays/dayinmonth);
                    depwageobj.put("amtot", tempmap.getBasic()*chargedays/dayinmonth);
                    Wagejsondata.put(depwageobj);
                    amtot = tempmap.getBasic()*chargedays/dayinmonth;
                }
                while (i < iteratorWage.size()) {
                    Templatemapwage wm = (Templatemapwage) iteratorWage.get(i);
                    JSONObject jobj = new JSONObject();
                    jobj.put("type", wm.getWagemaster().getWagetype());
                    jobj.put("Id", wm.getWagemaster().getWageid());
                    if (wm.getWagemaster().getRate() == 0) {
                        double amttemp = Double.parseDouble(wm.getRate());
                        amttemp = amttemp*chargedays/dayinmonth;
                        jobj.put("Rate", wm.getWagemaster().getRate());
                        jobj.put("amount", amttemp);
                        amtot += amttemp;
                    } else {
                        jobj.put("Rate", wm.getRate());
                        masterDB.Wagemaster checkwage = wm.getWagemaster();
                        if(checkwage.getComputeon()!=null&&checkwage.getComputeon()==0){
                            wagearr.put("deduc", jobj);
                        }else if(checkwage.getComputeon()!=null&&checkwage.getComputeon()==1){
                            wagearr.put("earn", jobj);
                        }else if(checkwage.getComputeon()!=null&&checkwage.getComputeon()==2){
                            wagearr.put("net", jobj);
                        }
                        if(checkwage.getComputeon()!=null&&checkwage.getComputeon()==3){
                           // isrecord = true;
                            Double amount = 0.0;
                            amount =hrmsPayrollWageDAOObj.calculatewagessalary(checkwage.getExpr(),mappingid);
                            Double perc = Double.parseDouble(wm.getRate());
                            jobj.put("amount", (amount*perc/100)*chargedays/dayinmonth);
                            jobj.put("computeon", messageSource.getMessage("hrms.payroll.specifiedformula", null, RequestContextUtils.getLocale(request)));
                            amtot += (amount*perc/100)*chargedays/dayinmonth;
                        }
                    }
                    wagetotalvalf = amtot;
                    jobj.put("amtot", amtot);
                    Wagejsondata.put(jobj);
                    i++;
                }

                double wagetotalfordeduction = wagetotalvalf;
                requestParams = new HashMap<String, Object>();
                requestParams.put("templateid", tempid);
                requestParams.put("filter_names",Arrays.asList("template.templateid"));
                requestParams.put("filter_values",Arrays.asList(tempid));
                result = hrmsPayrollDeductionDAOObj.getDeductionTemplateDetails(requestParams);
                iteratorWage = result.getEntityList();

                i = 0;
                amtot = 0;
                if (iteratorWage.size() > 0) {
                    while (i < iteratorWage.size()) {
                        Templatemapdeduction wm = (Templatemapdeduction) iteratorWage.get(i);
                        JSONObject jobj = new JSONObject();
                        jobj.put("type", wm.getDeductionmaster().getDeductiontype());
                        jobj.put("Id", wm.getDeductionmaster().getDeductionid());
                        if (wm.getDeductionmaster().getRate() == 0) {// check if amount
                            jobj.put("Rate", wm.getDeductionmaster().getRate());
                            jobj.put("amount", Double.parseDouble(wm.getRate())*chargedays/dayinmonth);
                            amtot += Double.parseDouble(wm.getRate())*chargedays/dayinmonth;
                        } else { // check if percent
                            jobj.put("Rate", wm.getRate());
                            masterDB.Deductionmaster checkdeduc = wm.getDeductionmaster();
                                if(checkdeduc.getComputeon()!=null&&checkdeduc.getComputeon()==0){
                                    deducarr.put("deduc", jobj);
                                }else if(checkdeduc.getComputeon()!=null&&checkdeduc.getComputeon()==1){
                                    deducarr.put("earn", jobj);
                                }else if(checkdeduc.getComputeon()!=null&&checkdeduc.getComputeon()==2){
                                    deducarr.put("net", jobj);
                                }
                                if(checkdeduc.getComputeon()!=null&&checkdeduc.getComputeon()==3){
                                    Double amount = 0.0;
                                    amount =hrmsPayrollWageDAOObj.calculatewagessalary(checkdeduc.getExpr(),mappingid);
                                    Double perc = Double.parseDouble(wm.getRate());
                                    jobj.put("amount", (amount*perc/100)*chargedays/dayinmonth);
                                    jobj.put("computeon", messageSource.getMessage("hrms.payroll.specifiedformula", null, RequestContextUtils.getLocale(request)));
                                    amtot += (amount*perc/100)*chargedays/dayinmonth;
                                }
                        }
                        deductotalvalf = amtot;
                        wagedeductotf = wagetotalvalf - deductotalvalf;
                        jobj.put("amtot", amtot);
                        Deducjsondata.put(jobj);
                        i++;
                    }
                }

                requestParams = new HashMap<String, Object>();
                requestParams.put("templateid", tempid);
                requestParams.put("filter_names",Arrays.asList("template.templateid"));
                requestParams.put("filter_values",Arrays.asList(tempid));
                result = hrmsPayrollTaxDAOObj.getTaxTemplateDetails(requestParams);
                iteratorWage = result.getEntityList();

                i = 0;
                amtot = 0;
                if (iteratorWage.size() > 0) {
                    while (i < iteratorWage.size()) {
                        Templatemaptax wm = (Templatemaptax) iteratorWage.get(i);
                        JSONObject jobj = new JSONObject();
                        jobj.put("type", wm.getTaxmaster().getTaxtype());
                        jobj.put("Id", wm.getTaxmaster().getTaxid());
                        if (wm.getTaxmaster().getRate() == 0) {// check if amount
                            jobj.put("Rate", wm.getTaxmaster().getRate());
                            jobj.put("amount", Double.parseDouble(wm.getRate())*chargedays/dayinmonth);
                            amtot += Double.parseDouble(wm.getRate())*chargedays/dayinmonth;
                        } else { // check if percent
                            jobj.put("Rate", wm.getRate());
                            masterDB.Taxmaster checktax = wm.getTaxmaster();
                                if(checktax.getComputeon()!=null&&checktax.getComputeon()==0){
                                    taxarr.put("deduc", jobj);
                                }else if(checktax.getComputeon()!=null&&checktax.getComputeon()==1){
                                    taxarr.put("earn", jobj);
                                }else if(checktax.getComputeon()!=null&&checktax.getComputeon()==2){
                                    taxarr.put("net", jobj);
                                }
                                if(checktax.getComputeon()!=null&&checktax.getComputeon()==3){
                                    Double amount = 0.0;
                                    amount =hrmsPayrollWageDAOObj.calculatewagessalary(checktax.getExpr(),mappingid);
                                    Double perc = Double.parseDouble(wm.getRate());
                                    jobj.put("amount", (amount*perc/100)*chargedays/dayinmonth);
                                    jobj.put("computeon", messageSource.getMessage("hrms.payroll.specifiedformula", null, RequestContextUtils.getLocale(request)));
                                    amtot += (amount*perc/100)*chargedays/dayinmonth;
                                }
                        }
                        taxtotalvalf = amtot;
                        jobj.put("amtot", amtot);
                        Taxjsondata.put(jobj);
                        i++;
                    }
                }

                requestParams.clear();
                requestParams.put("filter_names",Arrays.asList("template.templateid"));
                requestParams.put("filter_values",Arrays.asList(tempid));
                result = hrmsPayrollEmployerContributionDAOObj.getEmployerContribTemplateDetails(requestParams);
                iteratorWage = result.getEntityList();

                i = 0;
                amtot = 0;
                if (iteratorWage.size() > 0) {
                    while (i < iteratorWage.size()) {
                        TemplateMapEmployerContribution wm = (TemplateMapEmployerContribution) iteratorWage.get(i);
                        JSONObject jobj = new JSONObject();
                        jobj.put("type", wm.getEmpcontrimaster().getEmpcontritype());
                        jobj.put("Id", wm.getEmpcontrimaster().getId());
                        if (wm.getEmpcontrimaster().getRate() == 0) {// check if amount
                            jobj.put("Rate", wm.getEmpcontrimaster().getRate());
                            jobj.put("amount", Double.parseDouble(wm.getRate())*chargedays/dayinmonth);
                            amtot += Double.parseDouble(wm.getRate())*chargedays/dayinmonth;
                        } else { // check if percent
                            jobj.put("Rate", wm.getRate());
                            EmployerContribution checkec = wm.getEmpcontrimaster();
                                if(checkec.getComputeon()!=null&&checkec.getComputeon()==0){
                                    ecarr.put("deduc", jobj);
                                }else if(checkec.getComputeon()!=null&&checkec.getComputeon()==1){
                                    ecarr.put("earn", jobj);
                                }else if(checkec.getComputeon()!=null&&checkec.getComputeon()==2){
                                    ecarr.put("net", jobj);
                                }
                                if(checkec.getComputeon()!=null&&checkec.getComputeon()==3){
                                    Double amount = 0.0;
                                    amount =hrmsPayrollWageDAOObj.calculatewagessalary(checkec.getExpr(),mappingid);
                                    Double perc = Double.parseDouble(wm.getRate());
                                    jobj.put("amount", (amount*perc/100)*chargedays/dayinmonth);
                                    jobj.put("computeon", messageSource.getMessage("hrms.payroll.specifiedformula", null, RequestContextUtils.getLocale(request)));
                                    amtot += (amount*perc/100)*chargedays/dayinmonth;
                                }
                        }
                        ectotalvalf = amtot;
                        jobj.put("amtot", amtot);
                        ECjsondata.put(jobj);
                        i++;
                    }
                }

                double storeamt = amtot;
                    Double depwage = 0.0;
                    depwageobj = null;
                    if(wagearr.get("deduc")!=null){//Is there any component which depends on current deduction in earning
                        depwageobj = (JSONObject) wagearr.get("deduc");
                        depwage = deductotalvalf * Double.parseDouble(depwageobj.get("Rate").toString()) /100;
                        depwageobj.put("computeon",messageSource.getMessage("hrms.payroll.currentdeductions", null, RequestContextUtils.getLocale(request)));
                        depwage = depwage*chargedays/dayinmonth;
                        wagetotalvalf += depwage;
                        depwageobj.put("amtot",wagetotalvalf);
                        depwageobj.put("amount",depwage);
                    }
                    if(wagearr.get("earn")!=null){//Is there any component which depends on current earning in earning
                        depwageobj = (JSONObject) wagearr.get("earn");
                        depwage = wagetotalvalf * Double.parseDouble(depwageobj.get("Rate").toString()) /100;
                        depwageobj.put("computeon", messageSource.getMessage("hrms.payroll.currentearnings", null, RequestContextUtils.getLocale(request)));
                        depwage = depwage*chargedays/dayinmonth;
                        wagetotalvalf += depwage;
                        depwageobj.put("amtot",wagetotalvalf);
                        depwageobj.put("amount",depwage);

                    }
                    if(wagearr.get("net")!=null){//Is there any component which depends on net salary in earning
                        depwageobj = (JSONObject) wagearr.get("net");
                        depwage = (wagetotalvalf-deductotalvalf) * Double.parseDouble(depwageobj.get("Rate").toString())/100;
                        depwageobj.put("computeon", messageSource.getMessage("hrms.payroll.netsalary", null, RequestContextUtils.getLocale(request)));
                        depwage = depwage*chargedays/dayinmonth;
                        wagetotalvalf += depwage;
                        depwageobj.put("amtot",wagetotalvalf);
                        depwageobj.put("amount",depwage);
                    }

                    /*For Leave Manager Integrate
                     *
                     */
                    JSONObject jobjw = new JSONObject();
                    jobjw.put("type", "Unpaid_leaves");
                    jobjw.put("Rate", 0);
                    double leaveDeductionAmount = wagetotalvalf*unpaidleaves/dayinmonth;
                    jobjw.put("amount", leaveDeductionAmount);
                    storeamt += wagetotalvalf*chargedays/dayinmonth;
                    jobjw.put("amtot", storeamt);
                    Deducjsondata.put(jobjw);

                    depwage =0.0;
                    depwageobj = null;
                    if(deducarr.get("deduc")!=null){//Is there any component which depends on current deduction in deduction
                        depwageobj = (JSONObject) deducarr.get("deduc");
                        depwage = deductotalvalf * Double.parseDouble(depwageobj.get("Rate").toString())/100;
                        depwageobj.put("computeon", messageSource.getMessage("hrms.payroll.currentdeductions", null, RequestContextUtils.getLocale(request)));
                         depwage = depwage*chargedays/dayinmonth;
                        deductotalvalf += depwage;
                        depwageobj.put("amtot",deductotalvalf);
                        depwageobj.put("amount",depwage);
                    }
                    if(deducarr.get("earn")!=null){//Is there any component which depends on current earning in deduction
                        depwageobj = (JSONObject) deducarr.get("earn");
                        depwage = wagetotalvalf * Double.parseDouble(depwageobj.get("Rate").toString())/100;
                        depwageobj.put("computeon", messageSource.getMessage("hrms.payroll.currentearnings", null, RequestContextUtils.getLocale(request)));
                         depwage = depwage*chargedays/dayinmonth;
                        deductotalvalf += depwage;
                        depwageobj.put("amtot",deductotalvalf);
                        depwageobj.put("amount",depwage);
                    }
                    if(deducarr.get("net")!=null){////Is there any component which depends on net salary in deduction
                        depwageobj = (JSONObject) deducarr.get("net");
                        depwage = (wagetotalvalf-deductotalvalf) * Double.parseDouble(depwageobj.get("Rate").toString())/100;
                        depwageobj.put("computeon", messageSource.getMessage("hrms.payroll.netsalary", null, RequestContextUtils.getLocale(request)));
                         depwage = depwage*chargedays/dayinmonth;
                        deductotalvalf += depwage;
                        depwageobj.put("amtot",deductotalvalf);
                        depwageobj.put("amount",depwage);
                    }

                    depwage =0.0;
                    depwageobj = null;
                    if(taxarr.get("deduc")!=null){//Is there any component which depends on current deduction in deduction
                        depwageobj = (JSONObject) taxarr.get("deduc");
                        depwage = deductotalvalf * Double.parseDouble(depwageobj.get("Rate").toString())/100;
                        depwageobj.put("computeon", messageSource.getMessage("hrms.payroll.currentdeductions", null, RequestContextUtils.getLocale(request)));
                         depwage = depwage*chargedays/dayinmonth;
                        taxtotalvalf += depwage;
                        depwageobj.put("amtot",taxtotalvalf);
                        depwageobj.put("amount",depwage);
                    }
                    if(taxarr.get("earn")!=null){//Is there any component which depends on current earning in deduction
                        depwageobj = (JSONObject) taxarr.get("earn");
                        depwage = wagetotalvalf * Double.parseDouble(depwageobj.get("Rate").toString())/100;
                        depwageobj.put("computeon", messageSource.getMessage("hrms.payroll.currentearnings", null, RequestContextUtils.getLocale(request)));
                         depwage = depwage*chargedays/dayinmonth;
                        taxtotalvalf += depwage;
                        depwageobj.put("amtot",taxtotalvalf);
                        depwageobj.put("amount",depwage);
                    }
                    if(taxarr.get("net")!=null){////Is there any component which depends on net salary in deduction
                        depwageobj = (JSONObject) taxarr.get("net");
                        depwage = (wagetotalvalf-deductotalvalf) * Double.parseDouble(depwageobj.get("Rate").toString())/100;
                        depwageobj.put("computeon", messageSource.getMessage("hrms.payroll.netsalary", null, RequestContextUtils.getLocale(request)));
                         depwage = depwage*chargedays/dayinmonth;
                        taxtotalvalf += depwage;
                        depwageobj.put("amtot",taxtotalvalf);
                        depwageobj.put("amount",depwage);
                    }

                    depwage =0.0;
                    depwageobj = null;
                    if(ecarr.get("deduc")!=null){//Is there any component which depends on current deduction in deduction
                        depwageobj = (JSONObject) ecarr.get("deduc");
                        depwage = deductotalvalf * Double.parseDouble(depwageobj.get("Rate").toString())/100;
                        depwageobj.put("computeon", messageSource.getMessage("hrms.payroll.currentdeductions", null, RequestContextUtils.getLocale(request)));
                         depwage = depwage*chargedays/dayinmonth;
                        ectotalvalf += depwage;
                        depwageobj.put("amtot",ectotalvalf);
                        depwageobj.put("amount",depwage);
                    }
                    if(ecarr.get("earn")!=null){//Is there any component which depends on current earning in deduction
                        depwageobj = (JSONObject) ecarr.get("earn");
                        depwage = wagetotalvalf * Double.parseDouble(depwageobj.get("Rate").toString())/100;
                        depwageobj.put("computeon", messageSource.getMessage("hrms.payroll.currentearnings", null, RequestContextUtils.getLocale(request)));
                         depwage = depwage*chargedays/dayinmonth;
                        ectotalvalf += depwage;
                        depwageobj.put("amtot",ectotalvalf);
                        depwageobj.put("amount",depwage);
                    }
                    if(ecarr.get("net")!=null){////Is there any component which depends on net salary in deduction
                        depwageobj = (JSONObject) ecarr.get("net");
                        depwage = (wagetotalvalf-deductotalvalf) * Double.parseDouble(depwageobj.get("Rate").toString())/100;
                        depwageobj.put("computeon", messageSource.getMessage("hrms.payroll.netsalary", null, RequestContextUtils.getLocale(request)));
                         depwage = depwage*chargedays/dayinmonth;
                        ectotalvalf += depwage;
                        depwageobj.put("amtot",ectotalvalf);
                        depwageobj.put("amount",depwage);
                    }


                double net = 0;
                deductotalvalf +=leaveDeductionAmount;
                net = wagetotalvalf - deductotalvalf - taxtotalvalf;
                
                String netval = String.valueOf(net);
                String wagetotal = String.valueOf(wagetotalvalf);
                String taxtotal = String.valueOf(taxtotalvalf);
                String deductotal = String.valueOf(deductotalvalf);
                String ectotal = String.valueOf(ectotalvalf);
                String stdate = request.getParameter("stdate");
                String enddate = request.getParameter("enddate");
                boolean directsal = true;
                result = setPayHistory(request, paycyclest, paycycleed, tempid, jobj2.getString("empid"), jobj2.getString("EName"), jobj2.getString("design"), jobj2.getString("FixedSal"), netval, wagetotal, taxtotal, deductotal, ectotal, Wagejsondata, Deducjsondata, Taxjsondata, ECjsondata, directsal, mappingid, jobj2.getString("unpaidleaves"), salarystatus);
                jobj1.put("valid", true);
                if(result.isSuccessFlag())
                    jsAray.put("value", "success");
                else{
                    salaryflag = false;
                    employeelist += "<b>"+employeecount+". "+jobj2.getString("EName")+"</b><br/>";
                    employeecount++;
                    jsAray.put("value", "failure");
                }
                if(!salaryflag){		
                	jsAray.put("msg", messageSource.getMessage("hrms.payroll.Fortheemployeesalaryisnotgeneratedcheckjoiningdate",null,"For the following employee salary is not generated because either salary was already generated or check the joining date.", RequestContextUtils.getLocale(request))+"<br/><br/> "+employeelist);
                } else {
                    jsAray.put("msg", result.getMsg());
                }
                jobj1.put("data", jsAray);
                if(result.getMsg().equals("Salary generated successfully.") || result.getMsg().equals(messageSource.getMessage("hrms.Messages.calMsgBoxShow16", null,"Salary generated successfully.", RequestContextUtils.getLocale(request)))){
                    auditTrailDAOObj.insertAuditLog(AuditAction.SALARY_GENERATED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has generated salary for  " +jobj2.getString("EName"), request, "0");
                }
            }
            jobj1.put("success", true);
            txnManager.commit(status);
        } catch (Exception se) {
            se.printStackTrace();
            success = false;
            jsAray.put("value", "failure");
            jobj1.put("data", jsAray);
            jobj1.put("success", false);
            txnManager.rollback(status);
        }finally{
            
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public ModelAndView setPayrollforTemp1(HttpServletRequest request, HttpServletResponse response) {

        String msg = "";
        int count = 0,flag=0;
        KwlReturnObject result = null;
        boolean success = false;
        JSONObject jobj1 = new JSONObject();
        JSONObject jsAray = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            int x = 0;
            Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", request.getParameter("empid"));
            User newuser = ua.getUser();
            JSONArray WageJson = new JSONArray("["+request.getParameter("WageJson")+"]");
            JSONArray DeducJson = new JSONArray("["+request.getParameter("DeducJson")+"]");
            JSONArray taxesJson = new JSONArray("["+request.getParameter("taxesJson")+"]");
            JSONArray ECJson = new JSONArray("["+request.getParameter("ECJson")+"]");
            
            result = setPayHistory(request,request.getParameter("paycyclestart"), request.getParameter("paycycleend"),
                    request.getParameter("tempid"), request.getParameter("empid"),
                    /*AuthHandler.getFullName(newuser)*/StringUtil.getFullName(newuser),
                    ua.getDepartment().getValue(),
                    request.getParameter("gross"),
                    request.getParameter("net"),
                    request.getParameter("wagetot"),
                    request.getParameter("taxtot"),
                    request.getParameter("deductot"),
                    request.getParameter("ectot"),
                    WageJson, DeducJson,
                    taxesJson, ECJson,
                    false,request.getParameter("mappingid"), "0",request.getParameter("salaryStatus"));
            jobj1.put("valid", true);
            if(result.isSuccessFlag()){
                jsAray.put("value", "success");
            }else{
                jsAray.put("value", "failure");
            }
            jsAray.put("msg", result.getMsg());
            jobj1.put("data", jsAray);
            jobj1.put("success", true);
            if(result.getMsg().equals("Salary generated successfully.")){
            //@@ProfileHandler.insertAuditLog(session, AuditAction.SALARY_GENERATED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has generated salary for  " +AuthHandler.getFullName(newuser)  ,request);
                auditTrailDAOObj.insertAuditLog(AuditAction.SALARY_GENERATED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has generated salary for  " +profileHandlerDAOObj.getUserFullName(newuser.getUserID()), request, "0");
            }
            txnManager.commit(status);

        } catch (Exception se) {
            success = false;
            jsAray.put("value", "failure");
            jobj1.put("success", false);
            jobj1.put("data", jsAray);
            txnManager.rollback(status);
            se.printStackTrace();
        }finally{

            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public KwlReturnObject setPayHistory(HttpServletRequest request, String stdate, String enddate, String tempid, String empid, String empname, String design, String gross, String net, String wagetot, String taxtot, String deductot, String ectot, JSONArray WageJson, JSONArray DeducJson, JSONArray TaxJson, JSONArray ECJson,boolean directsal, String mappingid, String unpaidleaves, String salarystatus) {
        KwlReturnObject result = null;
        KwlReturnObject returnobj = null;
        JSONObject jobj1 = new JSONObject();
        JSONObject jsAray = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            int i = 0;
            Boolean isvalid = true;
            String histid = java.util.UUID.randomUUID().toString();
            SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
            Date d2 = df.parse(stdate);
            Date d3 = df.parse(enddate);
            DateFormat fo = new SimpleDateFormat("MMMM");
            String months = fo.format(d2);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", Arrays.asList("userID.userID"));
            requestParams.put("filter_values", Arrays.asList(empid));
            result = hrmsEmpDAOObj.getPayHistory(requestParams);
            List list = result.getEntityList();
            Iterator itr = list.iterator();
            while(itr.hasNext()){
                Payhistory ph =(Payhistory) itr.next();
                if(ph.getPaycyclestart()!=null&&ph.getPaycycleend()!=null){
                    if ((d2.compareTo(ph.getPaycyclestart())<0 && d3.compareTo(ph.getPaycyclestart())<0)||(d2.compareTo(ph.getPaycycleend())>0 && d3.compareTo(ph.getPaycycleend())>0)) {
                        isvalid = true;
                      //  break;
                    }else{
                        isvalid = false;
                        returnobj = new KwlReturnObject(false, messageSource.getMessage("hrms.payroll.SelectedemployeessalaryalreadygeneratedPaycyclePleasedeletetogenerateagain",null,"Selected employees salary already generated for this Pay cycle. Please delete salary to generate again.", RequestContextUtils.getLocale(request)), "-1", null, 0);
                        break;
                    }
                }
            }


            if(isvalid){
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", empid);
                User newuser = ua.getUser();
                Empprofile emp = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", empid);
                java.sql.Date payStartDate = new java.sql.Date(d2.getTime());
                if(emp==null || emp.getJoindate()==null||payStartDate.before(emp.getJoindate())){
                    returnobj = new KwlReturnObject(false, messageSource.getMessage("hrms.payroll.noteligiblesalarygenerationPleasecheckjoiningdate",null,"Employee not eligible for salary generation. Please check joining date.", RequestContextUtils.getLocale(request)), "-1", null, 0);
                }else{
                Template temlate = (Template) kwlCommonTablesDAOObj.getObject("masterDB.Template", tempid);
                userSalaryTemplateMap mapid = (userSalaryTemplateMap) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.userSalaryTemplateMap", mappingid);

                Date empjoiningdate = emp.getJoindate();
                Calendar cal = new GregorianCalendar(d2.getYear(), d2.getMonth(), d2.getDate());
                int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                Double deduction_unpaidleaves =new Double(0);
                if(empjoiningdate.compareTo(d2) > 0){
                        int joiningday = empjoiningdate.getDate();
                        int salaryday = 1;//start of month
                        deduction_unpaidleaves = (Double.parseDouble(gross)/days)*(joiningday-salaryday);
                }
                Double deducta=new Double(deductot) + deduction_unpaidleaves;
                deductot = deducta.toString();
                Double neta=new Double(net) - deduction_unpaidleaves;
                net = neta.toString();
                Payhistory payhist = null;//histid,empid,empname,design,gross,net,wagetot,taxtot,deductot,month,null,tempid,gross,null,null,null);
                requestParams = new HashMap<String, Object>();
                requestParams.put("historyid", histid);
                requestParams.put("userid", newuser);
                requestParams.put("template", temlate);
                requestParams.put("name", empname);
                requestParams.put("design", design);
                requestParams.put("department", ua.getDepartment().getValue());
                requestParams.put("gross", gross);
                requestParams.put("net", net);
                requestParams.put("wagetot", wagetot);
                requestParams.put("deductot", deductot);
                requestParams.put("taxtot", taxtot);
                requestParams.put("ectot", ectot);
                requestParams.put("mappingid", mapid);
                requestParams.put("status", 1);
                requestParams.put("createdon", d2);
                requestParams.put("createdfor", d3);
                requestParams.put("paycyclestart", d2);
                requestParams.put("paycycleend", d3);
                requestParams.put("unpaidleaves", Double.parseDouble(unpaidleaves));
                requestParams.put("paymonth", months);
                requestParams.put("salarystatus", salarystatus);
                requestParams.put("generatedon", new Date());
                result = hrmsEmpDAOObj.setPayHistory(requestParams);
                if(result.getEntityList()!=null&&result.getEntityList().size()>0){
                    payhist = (Payhistory)result.getEntityList().get(0);
                }



                JSONArray jarr2 = WageJson;

                for (i = 0; i < jarr2.length(); i++) {
                    JSONObject jobj2 = jarr2.getJSONObject(i);
                    String primid = java.util.UUID.randomUUID().toString();
                    requestParams = new HashMap<String, Object>();
                    requestParams.put("primid", primid);
                    requestParams.put("payhistory", payhist);
                    requestParams.put("name", "Wages");
                    requestParams.put("Id", jobj2.has("Id")?jobj2.getString("Id"):null);
                    requestParams.put("type", jobj2.getString("type"));
                    requestParams.put("amount", jobj2.getString("amount"));
                    requestParams.put("rate", jobj2.getString("Rate"));
                    result = hrmsEmpDAOObj.setHistorydetail(requestParams);
                }
                JSONArray jarr3 = DeducJson;
                
                for (i = 0; i < jarr3.length(); i++) {
                    JSONObject jobj3 = jarr3.getJSONObject(i);
                    String primid = java.util.UUID.randomUUID().toString();
                    requestParams = new HashMap<String, Object>();
                    requestParams.put("primid", primid);
                    requestParams.put("payhistory", payhist);
                    requestParams.put("name", "Deduction");
                    requestParams.put("Id", jobj3.has("Id")?jobj3.getString("Id"):null);
                    requestParams.put("type", jobj3.getString("type"));
                    requestParams.put("amount", jobj3.getString("amount"));
                    requestParams.put("rate", jobj3.getString("Rate"));
                    result = hrmsEmpDAOObj.setHistorydetail(requestParams);
                }

                if(deduction_unpaidleaves > 0 && directsal){
                        String primid = java.util.UUID.randomUUID().toString();
                        requestParams = new HashMap<String, Object>();
                        requestParams.put("primid", primid);
                        requestParams.put("payhistory", payhist);
                        requestParams.put("name", "Deduction");
                        requestParams.put("Id", null);
                        requestParams.put("type", messageSource.getMessage("hrms.payroll.Unpaidleaves", null,"Unpaid Leaves", RequestContextUtils.getLocale(request)));
                        requestParams.put("amount", deduction_unpaidleaves.toString());
                        requestParams.put("rate", "0");
                        result = hrmsEmpDAOObj.setHistorydetail(requestParams);
                }

                JSONArray jarr4 = TaxJson;

                for (i = 0; i < jarr4.length(); i++) {
                    JSONObject jobj4 = jarr4.getJSONObject(i);
                    String primid = java.util.UUID.randomUUID().toString();
                    requestParams = new HashMap<String, Object>();
                    requestParams.put("primid", primid);
                    requestParams.put("payhistory", payhist);
                    requestParams.put("name", "Taxes");
                    requestParams.put("Id", jobj4.has("Id")?jobj4.getString("Id"):null);
                    requestParams.put("type", jobj4.getString("type"));
                    requestParams.put("amount", jobj4.getString("amount"));
                    requestParams.put("rate", jobj4.getString("Rate"));
                    result = hrmsEmpDAOObj.setHistorydetail(requestParams);
                }

                JSONArray jarr5 = ECJson;

                for (i = 0; i < jarr5.length(); i++) {
                    JSONObject jobj5 = jarr5.getJSONObject(i);
                    String primid = java.util.UUID.randomUUID().toString();
                    requestParams = new HashMap<String, Object>();
                    requestParams.put("primid", primid);
                    requestParams.put("payhistory", payhist);
                    requestParams.put("name", "Employer Contribution");
                    requestParams.put("Id", null);
                    requestParams.put("type", jobj5.getString("type"));
                    requestParams.put("amount", jobj5.getString("amount"));
                    requestParams.put("rate", jobj5.getString("Rate"));
                    result = hrmsEmpDAOObj.setHistorydetail(requestParams);
                }
                returnobj = new KwlReturnObject(true, messageSource.getMessage("hrms.Messages.calMsgBoxShow16", null,"Salary generated successfully.", RequestContextUtils.getLocale(request)), "-1", null, 0);
                }
            }
            txnManager.commit(status);
        } catch (Exception se) {
            returnobj = new KwlReturnObject(false, messageSource.getMessage("hrms.common.UnexpectederrorOccuredPleasecontactadmin", null,"Unexpected error Occurred. Please contact admin.", RequestContextUtils.getLocale(request)), "-1", null, 0);
            logger.warn("hrmsEmpController.setPayHistory", se);
            txnManager.rollback(status);
            se.printStackTrace();
        } finally {
            return returnobj;
        }
    }
    public  Double setUnpaidleaves(HttpServletRequest request, JSONObject jsAray,String salary,Empprofile emp) throws ServiceException, com.krawler.utils.json.base.JSONException, ParseException{
        String empid = request.getParameter("empid");
        if (emp != null) {
            Date empjoiningdate = emp.getJoindate();
            if (empjoiningdate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                Date d2 = sdf.parse(request.getParameter("paycyclestart"));

                Calendar cal = new GregorianCalendar(d2.getYear(), d2.getMonth(), 1);
                d2 = new Date(d2.getYear(), d2.getMonth(), 1);
                int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (empjoiningdate.compareTo(d2) > 0) {
                    int joiningday = empjoiningdate.getDate();
                    int salaryday = 1; //start of month
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("Rate", "0");
                    jsAray1.put("Type", messageSource.getMessage("hrms.payroll.Unpaidleaves", null,"Unpaid Leaves", RequestContextUtils.getLocale(request)));
                    jsAray1.put("Unpaid Leaves", empjoiningdate.compareTo(d2));
                    jsAray1.put("amount", (Double.parseDouble(salary) / days) * (joiningday - salaryday));
                    jsAray.append("Deduc", jsAray1);
                    return (Double.parseDouble(salary) / days) * (joiningday - salaryday);
                }
            }
        }
        return new Double(0);
    }

    public ModelAndView deletePayslipDetails(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj1 =  new JSONObject();
        JSONObject jobj =  new JSONObject();
        KwlReturnObject result = null;
        String msg = "";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String endDate = request.getParameter("enddate").replace("/", "-");
            String startDate = request.getParameter("startdate").replace("/", "-");
            Date d3 = sdf.parse(endDate);
            Date d4 = sdf.parse(startDate);
            JSONArray empids = new JSONArray(request.getParameter("empid"));
            
            String userid = "";
            for(int i=0; i<empids.length(); i++){
            	userid+=("'"+empids.get(i).toString()+"',");
            }
            if(empids.length()>0){
            	userid = userid.substring(0, userid.length()-1);
            }

            List<Payhistory> list = hrmsEmpDAOObj.getPayHistorys(userid, d4, d3);

            if (list!=null && !list.isEmpty()) {
                for(Payhistory PayhistoryDeleteObj: list) {
                    HashMap<String, Object> requestParams = new HashMap<String, Object>();
                    requestParams.put("payhistoryid",PayhistoryDeleteObj.getHistoryid());
                    result = hrmsEmpDAOObj.deleteEmpPayHistory(requestParams);
                    if(result.isSuccessFlag()) {
                        msg = messageSource.getMessage("hrms.payroll.SalaryDetailsdeletedsuccessfully",null,"Salary Details deleted successfully.", RequestContextUtils.getLocale(request));
                        User user = PayhistoryDeleteObj.getUserID();
                        auditTrailDAOObj.insertAuditLog(AuditAction.SALARY_DELETED, "User " +
                                profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) +
                                " has deleted salary for  " +StringUtil.getFullName(user), request, "0");
                    } else {
                        msg =  messageSource.getMessage("hrms.payroll.Errorindeletingsalarydetails",null,"Error in deleting salary details.", RequestContextUtils.getLocale(request));
                    }
                 }
            } else {
                 msg =  messageSource.getMessage("hrms.payroll.Nosalarydetailsfoundforthisduration",null,"No salary details found for this duration.", RequestContextUtils.getLocale(request));
            }
            jobj.put("msg",msg);
            jobj.put("success",true);
            jobj1.put("data",jobj.toString());
            jobj1.put("valid",true);
            txnManager.commit(status);
        } catch(Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView generateApprovedSalary(HttpServletRequest request, HttpServletResponse response) {
        boolean success = false;
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject kmsg=null;
        JSONObject jsAray1 = new JSONObject();
        try {
            String historyid = request.getParameter("historyids");
            String mode = request.getParameter("mode");
            if(!StringUtil.isNullOrEmpty(historyid) && !StringUtil.isNullOrEmpty(mode)){
                int status = Integer.parseInt(mode);
                String [] historyIDs = historyid.split(",");
                kmsg= hrmsEmpDAOObj.generateApprovedSalary(historyIDs,status);
                if(kmsg.isSuccessFlag()){
                    if(StringUtil.equal(mode,"3")){
                        for(int i=0; i<historyIDs.length; i++){
                            User user = hrmsEmpDAOObj.getPayHistoryEmployeeName(historyIDs[i]);
                            if(user!=null){
                                auditTrailDAOObj.insertAuditLog(AuditAction.SALARY_AUTHORIZED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has authorized salary for  " +StringUtil.getFullName(user), request, "0");
                            }
                        
                        }
                    }else if(StringUtil.equal(mode,"1")){

                        for(int i=0; i<historyIDs.length; i++){
                            User user = hrmsEmpDAOObj.getPayHistoryEmployeeName(historyIDs[i]);
                            if(user!=null){
                                auditTrailDAOObj.insertAuditLog(AuditAction.SALARY_UNAUTHORIZED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has unauthorized salary for  " +StringUtil.getFullName(user), request, "0");
                            }

                        }
                    }
                    
                }
                

            }
            success = true;
            jobj1.put("success", success);
            jobj1.put("valid", true);
            jobj1.put("data", jsAray1);

        } catch (Exception e) {
            e.printStackTrace();
            success = false;
            jobj1.put("success", success);
            jobj1.put("valid", true);
            jobj1.put("data", jsAray1);
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    
    public ModelAndView getLeavesFromEleaves(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject jResult = new JSONObject();
        boolean success = false;
        try {
        	final String action = "101";
        	if(!StringUtil.isStandAlone()){
                String companyid = sessionHandlerImplObj.getCompanyid(request);
                String eleaveURL = this.getServletContext().getInitParameter("eleaveURL");
                JSONArray jobj = new JSONArray(request.getParameter("jsondata"));
                JSONObject userData = new JSONObject();
                userData.put("data", jobj);
                userData.put("companyid", companyid);
                JSONObject appdata = APICallHandler.callApp(eleaveURL, userData, companyid, action);
                jResult.put("valid", true);
                jResult.put("success", true);
                jResult.put("data", appdata.toString());
             }   
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jResult.toString());
    }
    
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
