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

package com.krawler.spring.hrms.payroll.deduction;

import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.payroll.template.hrmsPayrollTemplateDAO;
import com.krawler.common.admin.userSalaryTemplateMap;
import com.krawler.common.locale.MessageSourceProxy;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.LocaleUtil;
import com.krawler.spring.hrms.payroll.employercontribution.hrmsPayrollEmployerContributionDAO;
import com.krawler.spring.hrms.payroll.wages.hrmsPayrollWageDAO;
import com.krawler.spring.hrms.payroll.wages.hrmsPayrollWageDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import masterDB.Deductionmaster;
import masterDB.EmployerContribution;
import masterDB.Historydetail;
import masterDB.Payhistory;
import masterDB.TempTemplateMagDeduction;
import masterDB.Template;
import masterDB.Templatemapdeduction;
import masterDB.Wagemaster;

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
 * @author shs
 */
public class hrmsPayrollDeductionController extends MultiActionController implements MessageSourceAware{

    private String successView;
    private hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj;
    private hrmsPayrollWageDAO hrmsPayrollWageDAOObj;
    private hrmsPayrollTemplateDAO hrmsPayrollTemplateDAOObj;
    private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private hrmsPayrollEmployerContributionDAO hrmsPayrollEmployerContributionDAOObj;
    private MessageSource messageSource;

    public void setHrmsPayrollEmployerContributionDAO(hrmsPayrollEmployerContributionDAO hrmsPayrollEmployerContributionDAOObj) {
        this.hrmsPayrollEmployerContributionDAOObj = hrmsPayrollEmployerContributionDAOObj;
    }

    public kwlCommonTablesDAO getKwlCommonTablesDAOObj() {
        return kwlCommonTablesDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }
    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }
    public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }
    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

        public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setHrmsPayrollDeductionDAO(hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj1) {
        this.hrmsPayrollDeductionDAOObj = hrmsPayrollDeductionDAOObj1;
    }

    public void setHrmsPayrollTemplateDAO(hrmsPayrollTemplateDAO hrmsPayrollTemplateDAOObj1) {
        this.hrmsPayrollTemplateDAOObj = hrmsPayrollTemplateDAOObj1;
    }

    public void setHrmsPayrollWageDAO(hrmsPayrollWageDAO hrmsPayrollWageDAOObj1) {
        this.hrmsPayrollWageDAOObj = hrmsPayrollWageDAOObj1;
    }

    public ModelAndView setDeductionData(HttpServletRequest request, HttpServletResponse response){

        KwlReturnObject result=null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("name", request.getParameter("name"));
            requestParams.put("rate",request.getParameter("rate"));
            requestParams.put("code",request.getParameter("code"));
            requestParams.put("Cid",sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("option",StringUtil.isNullOrEmpty(request.getParameter("optiontype"))?request.getParameter("option"):(request.getParameter("optiontype").equals("%")?"Percent":"Amount"));
            requestParams.put("isChecked", request.getParameter("isChecked"));
            requestParams.put("Action",request.getParameter("Action"));
            requestParams.put("typeid",request.getParameter("typeid"));
            requestParams.put("depwageid",request.getParameter("depwageid"));
            if(!StringUtil.isNullOrEmpty(request.getParameter("computeon")) && !((request.getParameter("option").equals("Amount"))||(request.getParameter("optiontype").equals("$"))))
                    requestParams.put("computeon",request.getParameter("computeon"));
            requestParams.put("expr",request.getParameter("expr"));
            result = hrmsPayrollDeductionDAOObj.setDeductionData(requestParams);

            if (request.getParameter("Action").equals("Add")&&result.isSuccessFlag()) {
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new payroll component " +insertWage.getDeductiontype() + " of type Deduction"  ,request);
                auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_COMPONENT_ADDED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has added new payroll component " +request.getParameter("name") + " of Deduction type", request, "0");
            } else if(result.isSuccessFlag()){
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited payroll component " +insertWage.getDeductiontype() ,request);
                auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_COMPONENT_EDITED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has edited payroll component " +request.getParameter("name"), request, "0");
            }
            txnManager.commit(status);
        } catch (Exception e) {

            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }

    public ModelAndView deleteMasterDeduc(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result=null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("id", request.getParameter("typeid"));

            result = hrmsPayrollDeductionDAOObj.deleteMasterDeduc(requestParams);

            //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_DELETED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted payroll template  " + dm.getDeductiontype() + " of type Deduction" ,request);
            if(result!=null && result.getEntityList()!=null && !result.getEntityList().isEmpty()){
                String logtemp=(String) result.getEntityList().get(0);
                auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_COMPONENT_DELETED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has deleted payroll template  " + logtemp + " of Deduction type", request, "0");
            }
            txnManager.commit(status);
        } catch (Exception e) {

            e.printStackTrace();
            txnManager.rollback(status);
        }

        return new ModelAndView("jsonView", "model", result.getMsg());
    }

     public ModelAndView getDeductionMaster(HttpServletRequest request, HttpServletResponse response)  {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null;
        int count = 0;
        int deducount = 0, wagecount = 0, empcontcount = 0;
        try {
           String cid = sessionHandlerImplObj.getCompanyid(request);
           HashMap<String, Object> requestParams = new HashMap<String, Object>();
           requestParams.put("Cid", cid);
           requestParams.put("start", request.getParameter("start"));
           requestParams.put("limit", request.getParameter("limit"));
           requestParams.put("allflag", request.getParameter("allflag"));
           requestParams.put("ss", request.getParameter("ss"));
           requestParams.put("filter_names",Arrays.asList("companydetails.companyID"));
           requestParams.put("filter_values",Arrays.asList(cid));
           requestParams.put("append","  and (rate = 0 or (rate = 1 and computeon is not null)) ");
           result=hrmsPayrollDeductionDAOObj.getDeductionMaster(requestParams);
                List lst=result.getEntityList();
                deducount = lst.size();
                for (int i = 0; i < lst.size(); i++) {
                    masterDB.Deductionmaster wage = (masterDB.Deductionmaster) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("comp", "deduc");
                    jobjtemp.put("type", wage.getDeductiontype());
                    jobjtemp.put("rate", wage.getRate());
                    jobjtemp.put("cash", wage.getCash());
                    jobjtemp.put("id", wage.getDeductionid());
                    jobjtemp.put("code", wage.getDcode());
                    jobjtemp.put("isdefault", wage.isIsdefault());
                    jobjtemp.put("depwage", (wage.getDepwageid()==null)?"":wage.getDepwageid().getWagetype());
                    jobjtemp.put("depwageid", (wage.getDepwageid()==null)?"":wage.getDepwageid().getWageid());
                    jobjtemp.put("computeon", wage.getComputeon());
                    jobjtemp.put("expr", wage.getExpr());
                    jobj.append("data", jobjtemp);
                    count = result.getRecordTotalCount();
                }
                if(request.getParameter("wage")!=null&&request.getParameter("wage").equals("true")){
                    result=hrmsPayrollWageDAOObj.getWageMaster(requestParams);
                    lst=result.getEntityList();
                    wagecount = lst.size();
                    for (int i = 0; i < lst.size(); i++) {
                        masterDB.Wagemaster wage = (masterDB.Wagemaster) lst.get(i);
                        JSONObject jobjtemp = new JSONObject();
                        jobjtemp.put("comp", "wage");
                        jobjtemp.put("type", wage.getWagetype());
                        jobjtemp.put("rate", wage.getRate());
                        jobjtemp.put("cash", wage.getCash());
                        jobjtemp.put("id", wage.getWageid());
                        jobjtemp.put("code", wage.getWcode());
                        jobjtemp.put("depwage", (wage.getDepwageid()==null)?"":wage.getDepwageid().getWagetype());
                        jobjtemp.put("depwageid", (wage.getDepwageid()==null)?"":wage.getDepwageid().getWageid());
                        jobjtemp.put("isdefault", wage.isIsdefault());
                        jobjtemp.put("computeon", wage.getComputeon());
                        jobjtemp.put("expr", wage.getExpr());
                        jobj.append("data", jobjtemp);
                    }
                }
                if(request.getParameter("empcont")!=null&&request.getParameter("empcont").equals("true")){
                	requestParams.remove("filter_names");
                    if (request.getParameter("isedit") != null && request.getParameter("isedit").equals("Edit")) {
                        requestParams.put("filter_names", Arrays.asList("companyid.companyID", "!id"));
                    } else {
                        requestParams.put("filter_names",Arrays.asList("companyid.companyID"));
                    }
                    result=hrmsPayrollEmployerContributionDAOObj.getMasterEmployerContrib(requestParams);
                    lst=result.getEntityList();
                    empcontcount = lst.size();
                    for (int i = 0; i < lst.size(); i++) {
                        EmployerContribution ec = (EmployerContribution) lst.get(i);
                        JSONObject jobjtemp = new JSONObject();
                        jobjtemp.put("comp", "empcontrib");
                        jobjtemp.put("type", ec.getEmpcontritype());
                        jobjtemp.put("rate", ec.getRate());
                        jobjtemp.put("cash", ec.getCash());
                        jobjtemp.put("id", ec.getId());
                        jobjtemp.put("code", ec.getEmpcontricode());
                        jobjtemp.put("isdefault", ec.getIsdefault());
                        jobjtemp.put("computeon", ec.getComputeon());
                        jobjtemp.put("expr", ec.getExpr());
                        jobj.append("data", jobjtemp);
                    }
                }
                if (deducount == 0 && wagecount == 0 && empcontcount == 0) {
                    jobj.put("data", "");
                }
                jobj.put("success", true);
                jobj1 = new JSONObject();
                jobj1.put("valid", true);
                jobj.put("totalcount", count);
                jobj1.put("data", jobj);
        } catch (Exception e) {
            jobj.put("success", false);
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
            e.printStackTrace();
        }  finally {
            return new ModelAndView("jsonView", "model",jobj1.toString());
        }
    }

     public ModelAndView getDefualtDeduction(HttpServletRequest request, HttpServletResponse response)  {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String cid = sessionHandlerImplObj.getCompanyid(request);
            requestParams.put("cid", cid);
            result = hrmsPayrollDeductionDAOObj.getDefualtDeduction(requestParams);
            List <masterDB.Deductionmaster> lst2 = result.getEntityList();
            for (int i = 0; i < lst2.size(); i++) {
                masterDB.Deductionmaster taxobj = (masterDB.Deductionmaster) lst2.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("id", taxobj.getDeductionid());
                jobjtemp.put("type", taxobj.getDeductiontype());
                jobjtemp.put("cash", taxobj.getCash());
                jobjtemp.put("computeon", taxobj.getComputeon());
                jobjtemp.put("expr", taxobj.getExpr());
                jobjtemp.put("rate", taxobj.getRate());
                jobjtemp.put("code", taxobj.getDcode());
                jobjtemp.put("depwage", (taxobj.getDepwageid()==null)?"":taxobj.getDepwageid().getWagetype());
                jobjtemp.put("depwageid", (taxobj.getDepwageid()==null)?"":taxobj.getDepwageid().getWageid());
                jobj.append("data", jobjtemp);
            }
            if (lst2.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);

            jobj1.put("valid", true);
            jobj1.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            return new ModelAndView("jsonView", "model",jobj1.toString());
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
     
     public ModelAndView getDeducPerTempid(HttpServletRequest request, HttpServletResponse response) {
         boolean success = false;
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {
//            Template temp = (Template) session.get(Template.class, TempId);
            String paycyclest = request.getParameter("paycyclestart");
            String paycycleed = request.getParameter("paycycleend");
            String paycyclestartdate = request.getParameter("paycycleactualstart");
            String paycycleenddate = request.getParameter("paycycleactualend");
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date paycyclestart = sdf.parse(paycyclest);
            Date paycycleend = sdf.parse(paycycleed);
            String ischanged = request.getParameter("ischanged");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String TempId = request.getParameter("TempId");
            String salary = request.getParameter("salary");
            String empid = request.getParameter("empid");
            
            requestParams.put("empid", empid);
            requestParams.put("templateid", TempId);
            requestParams.put("paycyclestart", paycyclestart);
            requestParams.put("paycycleend", paycycleend);
            List<Payhistory> payhistories = getPayhistory(requestParams);
            String cid = sessionHandlerImplObj.getCompanyid(request);
            CompanyPreferences cp = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", cid);
            JSONObject jsAray = new JSONObject();
            int i = 0;
            if(payhistories.isEmpty()){
            requestParams.put("filter_names", Arrays.asList("userAccount.userID","<=effectiveDate"));
            requestParams.put("filter_values", Arrays.asList(empid,paycyclestart));
            requestParams.put("order_by", Arrays.asList("effectiveDate"));
            requestParams.put("order_type", Arrays.asList("desc"));
            result = hrmsPayrollTemplateDAOObj.getUserSalaryTemplateMap(requestParams);
            userSalaryTemplateMap tempmap = null;
            if(StringUtil.checkResultobjList(result))
                tempmap = (userSalaryTemplateMap) result.getEntityList().get(0);
            Template temp = null;
            if(tempmap!=null)
               temp = tempmap.getSalaryTemplate();
            int dayinmonth=1;
            int chargedays=1;
            if(ischanged.equals("1")){
                    int holiday = cp.getWeeklyholidays();
                    Date cyclestartdate = sdf.parse(paycyclestartdate);
                    Date cycleenddate = sdf.parse(paycycleenddate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(paycyclestart);
                    int[] intArray = new int[]{-1,-1,-1,-1,-1,-1,-1};
                    HashMap<Integer,Integer> p=new HashMap<Integer,Integer>();
                    p = StringUtil.getHolidayArr(holiday);
                    if(temp.getPayinterval()==1){
                        dayinmonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                        dayinmonth -= StringUtil.nonWorkingDays(cyclestartdate,cycleenddate,p);
                    }else if(temp.getPayinterval()==3){
                        dayinmonth = 7;
                        dayinmonth -= StringUtil.nonWorkingDays(cyclestartdate,cycleenddate,p);
                    }else if(temp.getPayinterval()==2){
                        Calendar c1 = Calendar.getInstance();
                        c1.setTime(cyclestartdate);
                        if(c1.get(Calendar.DATE)<=15){
                    		dayinmonth = 15;
                    	}else{
                    		dayinmonth = c1.getActualMaximum(Calendar.DAY_OF_MONTH)-15;
                    	}
                        paycycleend = cycleenddate;
                    }
                    long chargesec = paycycleend.getTime()- paycyclestart.getTime()+(1000 * 60 * 60 * 24);
                    chargedays = (int) (chargesec / (1000 * 60 * 60 * 24));
                    chargedays -= StringUtil.nonWorkingDays(paycyclestart,paycycleend,p);
            }
            if (Integer.parseInt(temp.getStatus().toString()) == 0) {
                ArrayList Deducarr = new ArrayList();
                requestParams = new HashMap<String, Object>();
                requestParams.put("filter_names",Arrays.asList("template.templateid"));
                requestParams.put("filter_values",Arrays.asList(temp.getTemplateid()));
                result = hrmsPayrollDeductionDAOObj.getDeductionTemplateDetails(requestParams);
                List iteratorWage = result.getEntityList();
                i = 0;
                float amtot = 0;
                Boolean isrecord = false;
                while (i < iteratorWage.size()) {
                    isrecord = false;
                    Templatemapdeduction wm = (Templatemapdeduction) iteratorWage.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    if (wm.getDeductionmaster().getRate() == 0) {
                        isrecord = true;
                        jsAray1.put("Id", wm.getDeductionmaster().getDeductionid());
                        jsAray1.put("Rate", wm.getDeductionmaster().getRate());
                        jsAray1.put("amount", Double.parseDouble(wm.getRate())*chargedays/dayinmonth);
                        jsAray1.put("computeon", "-");
                        amtot += Float.parseFloat(wm.getRate())*chargedays/dayinmonth;
                    } else {
                            masterDB.Deductionmaster checkdeduc = wm.getDeductionmaster();
                            if(checkdeduc.getComputeon()!=null&&checkdeduc.getComputeon()==3){
                                isrecord = true;
                                Double amount = 0.0;
                                amount =hrmsPayrollWageDAOObj.calculatewagessalary(checkdeduc.getExpr(),tempmap.getMappingid());
                                Double perc = Double.parseDouble(wm.getRate());
                                jsAray1.put("Id", wm.getDeductionmaster().getDeductionid());
                                jsAray1.put("Rate", wm.getRate());
                                jsAray1.put("amount", (amount*perc/100)*chargedays/dayinmonth);
                                jsAray1.put("computeon", "Specified Formula");
                                amtot += (amount*perc/100)*chargedays/dayinmonth;
                            }
                    }
                    if(isrecord){
                        hrmsPayrollDeductionDAOImpl.setDeductotalval(amtot);
                        hrmsPayrollWageDAOImpl.setWagedeductot(hrmsPayrollWageDAOImpl.getWagetotalval()-hrmsPayrollDeductionDAOImpl.getDeductotalval());
                        jsAray1.put("depwage", (wm.getDeductionmaster().getDepwageid()==null)?"":wm.getDeductionmaster().getDepwageid().getWagetype());
                        jsAray1.put("depwageid", (wm.getDeductionmaster().getDepwageid()==null)?"":wm.getDeductionmaster().getDepwageid().getWageid());
                        jsAray1.put("WageId", wm.getDeductionmaster().getDeductionid());
                        jsAray1.put("Id", wm.getDeductionmaster().getDeductionid());
                        jsAray1.put("Type", wm.getDeductionmaster().getDeductiontype());
                        jsAray1.put("ratetype", wm.getDeductionmaster().getRate());
                        jsAray1.put("amtot", amtot);
                        jsAray.append("Deduc", jsAray1);
                    }
                    i++;
                }
            } else if (Integer.parseInt(temp.getStatus().toString()) == 1) {
                requestParams = new HashMap<String, Object>();
                requestParams.put("templateid", TempId);
                result = hrmsPayrollDeductionDAOObj.getTempDeductionTemplateDetails(requestParams);
                List iteratorWage = result.getEntityList();
                i = 0;
                float amtot = 0;
                while (i < iteratorWage.size()) {
                    TempTemplateMagDeduction wm = (TempTemplateMagDeduction) iteratorWage.get(i);
                    double amount = (Float.parseFloat(wm.getRate()) * hrmsPayrollWageDAOImpl.getWagetotalval()) / 100;
                    amtot += Float.parseFloat(wm.getRate()) * hrmsPayrollWageDAOImpl.getWagetotalval() / 100;
                    hrmsPayrollDeductionDAOImpl.setDeductotalval(amtot);
                    hrmsPayrollWageDAOImpl.setWagedeductot(hrmsPayrollWageDAOImpl.getWagetotalval()-hrmsPayrollDeductionDAOImpl.getDeductotalval());

                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("WageId", wm.getDeductionmaster().getDeductionid());
                    jsAray1.put("Id", wm.getDeductionmaster().getDeductionid());
                    jsAray1.put("Rate", wm.getRate());
                    jsAray1.put("Type", wm.getDeductionmaster().getDeductiontype());
                    jsAray1.put("amount", amount);
                    jsAray1.put("amtot", amtot);
                    jsAray.append("Deduc", jsAray1);
                    i++;
                }
            }
            }else{
            	Payhistory payhistory = payhistories.get(0);
            	requestParams.clear();
            	requestParams.put("payHistoryId", payhistory.getHistoryid());
            	requestParams.put("name", "Deduction");
            	List<Historydetail> historydetails = hrmsPayrollTemplateDAOObj.getPayHistorydetail(requestParams);
            	Iterator<Historydetail> iterator = historydetails.iterator();
            	while(iterator.hasNext()){
            		Historydetail historydetail = iterator.next();
            		Deductionmaster deductionmaster = historydetail.getDeductionmaster();
            		JSONObject jsonObject = new JSONObject();
            		if(deductionmaster!=null){
                        boolean isrecord = false;
                        if(deductionmaster.getRate() == 0){
                        	jsonObject.put("computeon", "-");
                        }else{
                        	if(deductionmaster.getComputeon()!=null && deductionmaster.getComputeon()==0){
                        		jsonObject.put("computeon", "Current Deduction");
                        	}else{
                        		if(deductionmaster.getComputeon()!=null && deductionmaster.getComputeon()==1){
                        			jsonObject.put("computeon", "Current Earning");
                        		}else{
                        			if(deductionmaster.getComputeon()!=null && deductionmaster.getComputeon()==2){
                        				jsonObject.put("computeon", "Net Salary");
                        			}else{
                        				isrecord = true;
                        				jsonObject.put("computeon", "Specified Formula");
                        				jsonObject.put("ratetype", deductionmaster.getRate());
                        			}
                        		}
                        	}
                        }
                        if(isrecord){
                        	jsonObject.put("depwage", (deductionmaster.getDepwageid()==null)?"":deductionmaster.getDepwageid().getWagetype());
                        	jsonObject.put("depwageid", (deductionmaster.getDepwageid()==null)?"":deductionmaster.getDepwageid().getWageid());
                        }
                        jsonObject.put("WageId", deductionmaster.getDeductionid());
                        jsonObject.put("Id", deductionmaster.getDeductionid());
            			jsonObject.put("Rate", deductionmaster.getRate());
            			jsonObject.put("Type", deductionmaster.getDeductiontype());
            			jsonObject.put("amount", historydetail.getAmount());
            			jsonObject.put("amtot", historydetail.getAmount());
            			jsAray.append("Deduc", jsonObject);
            		}else{
            			jsonObject.put("Rate", historydetail.getRate());
            			if(StringUtil.equal(historydetail.getType(), "Unpaid_leaves")){
                                    jsonObject.put("Type", MessageSourceProxy.getMessage("hrms.payroll.Unpaidleaves" ,null,"Unpaid Leaves", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                                }else {
                                    jsonObject.put("Type", historydetail.getType());
                                }
                                
            			jsonObject.put("amount", historydetail.getAmount());
            			jsonObject.put("computeon", "-");
            			jsAray.append("Deduc", jsonObject);
            		}
                    i++;
            	}
            }
            if(jsAray.isNull("Deduc")){
                jsAray.put("Deduc", new com.krawler.utils.json.base.JSONObject());
            }
            jsAray.put("success", true);
            jobj1.put("valid", true);
            jobj1.put("totalcount", i);
            jobj1.put("data", jsAray);
            success = true;
        } catch (Exception se) {
            success = false;
            se.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    public  Double setUnpaidleaves(HttpServletRequest request, JSONObject jsAray,String salary) throws ServiceException, JSONException, SessionExpiredException{
                String empid = request.getParameter("empid");
                Empprofile emp = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", empid);
                if(emp!=null){
                    Date empjoiningdate = emp.getJoindate();
                    Date d2 = new Date(request.getParameter("stdate"));

                    Calendar cal = new GregorianCalendar(d2.getYear(), d2.getMonth(), 1);
                    d2 = new Date(d2.getYear(), d2.getMonth(), 1);
                    int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    if(empjoiningdate.compareTo(d2) > 0){
                            int joiningday = empjoiningdate.getDate();
                            int salaryday = 1;//start of month
                            JSONObject jsAray1 = new JSONObject();
                            jsAray1.put("Rate", "0");
                            jsAray1.put("Type", MessageSourceProxy.getMessage("hrms.payroll.Unpaidleaves" ,null,"Unpaid Leaves", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                            jsAray1.put("Unpaid Leaves", empjoiningdate.compareTo(d2));
                            jsAray1.put("amount", (Double.parseDouble(salary)/days)*(joiningday-salaryday));
                            jsAray.append("Deduc", jsAray1);
                            return (Double.parseDouble(salary)/days)*(joiningday-salaryday);
                    }
                }
                return new Double(0);
    }
    public double getRecursiveAmountForDeducGrid(Wagemaster wm, ArrayList arr, String salary) {
        double amt = 0;
        double sal = 0;
            sal = Double.parseDouble(salary);
            for(int i=arr.size()-1 ; i>=0 ; i--){
                amt = Double.parseDouble(arr.get(i).toString()) * sal / 100;
                sal = amt;
            }
        return sal;
    }

    public ModelAndView getDeductionData(HttpServletRequest request, HttpServletResponse response) {
        boolean success = false;
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {
            HashMap<String, Object> requestParams= new HashMap<String, Object>();
            String cid = AuthHandler.getCompanyid(request);
            String TempId = request.getParameter("TempId");
            requestParams.put("templateid", TempId);
            requestParams.put("cid", cid);
            result = hrmsPayrollTemplateDAOObj.getTemplateDetails(requestParams);
            List templateList = result.getEntityList();
            Template temp = null;
            if(templateList.size()>0)
                temp = (Template) templateList.get(0);
            int tc = 0;
            JSONObject jobj = new JSONObject();
            int i = 0;
            if (Integer.parseInt(temp.getStatus().toString()) == 0) {
                requestParams.put("filter_names",Arrays.asList("template.templateid"));
                requestParams.put("filter_values",Arrays.asList(TempId));
                result = hrmsPayrollDeductionDAOObj.getDeductionTemplateDetails(requestParams);
                List lst = result.getEntityList();
                for (i = 0; i < lst.size(); i++) {
                    masterDB.Templatemapdeduction wage = (masterDB.Templatemapdeduction) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("assigned", "1");
                    jobjtemp.put("type", wage.getDeductionmaster().getDeductiontype());
                    jobjtemp.put("cash", wage.getRate());
                    jobjtemp.put("rate", wage.getDeductionmaster().getRate());
                    jobjtemp.put("id", wage.getDeductionmaster().getDeductionid());
                    jobjtemp.put("code", wage.getDeductionmaster().getDcode());
                    jobjtemp.put("depwage", (wage.getDeductionmaster().getDepwageid()==null)?"":wage.getDeductionmaster().getDepwageid().getWagetype());
                    jobjtemp.put("depwageid", (wage.getDeductionmaster().getDepwageid()==null)?"":wage.getDeductionmaster().getDepwageid().getWageid());
                    jobjtemp.put("computeon", wage.getDeductionmaster().getComputeon());
                    jobjtemp.put("expr", wage.getDeductionmaster().getExpr());
                    jobj.append("data", jobjtemp);
                }
                if(lst.size()==0){
                  jobj.put("data","");
                }
            } else if (Integer.parseInt(temp.getStatus().toString()) == 1) {
                result = hrmsPayrollDeductionDAOObj.getTempDeductionTemplateDetails(requestParams);
                List lst1=result.getEntityList();
                for (i = 0; i < lst1.size(); i++) {
//                    Object[] object = (Object[]) lst1.get(i);
                    masterDB.TempTemplateMagDeduction wage = (masterDB.TempTemplateMagDeduction) lst1.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("assigned", "1");
                    jobjtemp.put("type", wage.getDeductionmaster().getDeductiontype());
                    jobjtemp.put("cash", wage.getRate());
                    jobjtemp.put("rate", wage.getDeductionmaster().getRate());
                    jobjtemp.put("id", wage.getDeductionmaster().getDeductionid());
                    jobjtemp.put("code", wage.getDeductionmaster().getDcode());
                    jobj.append("data", jobjtemp);
                }
                if(lst1.size()==0){
                  jobj.put("data","");
                }
            }
            jobj.put("success", true);
            jobj1.put("valid", true);
            jobj.put("totalcount", i);
            jobj1.put("data", jobj);
            success = true;
        } catch (Exception se) {
            success = false;
            se.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
