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

package com.krawler.spring.hrms.payroll.wages;

import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.payroll.deduction.hrmsPayrollDeductionDAO;
import com.krawler.spring.hrms.payroll.template.hrmsPayrollTemplateDAO;
import com.krawler.common.admin.userSalaryTemplateMap;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.hrms.payroll.employercontribution.hrmsPayrollEmployerContributionDAO;
import com.krawler.spring.hrms.payroll.tax.hrmsPayrollTaxDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.utils.json.base.JSONObject;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import masterDB.Deductionmaster;
import masterDB.EmployerContribution;
import masterDB.Historydetail;
import masterDB.Payhistory;
import masterDB.Taxmaster;
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

/**
 *
 * @author shs
 */
public class hrmsPayrollWageController extends MultiActionController {
    private String successView;
    private hrmsPayrollWageDAO hrmsPayrollWageDAOObj;
    private hrmsPayrollTemplateDAO hrmsPayrollTemplateDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj;
    private hrmsPayrollTaxDAO hrmsPayrollTaxDAOObj;
    private hrmsPayrollEmployerContributionDAO hrmsPayrollEmployerContributionDAOObj;

    public void setHrmsPayrollEmployerContributionDAO(hrmsPayrollEmployerContributionDAO hrmsPayrollEmployerContributionDAOObj) {
        this.hrmsPayrollEmployerContributionDAOObj = hrmsPayrollEmployerContributionDAOObj;
    }

    public void setHrmsPayrollDeductionDAO(hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj) {
        this.hrmsPayrollDeductionDAOObj = hrmsPayrollDeductionDAOObj;
    }
    
    public void setHrmsPayrollTaxDAO(hrmsPayrollTaxDAO hrmsPayrollTaxDAOObj) {
        this.hrmsPayrollTaxDAOObj = hrmsPayrollTaxDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }
public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
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

    public void setHrmsPayrollWageDAO(hrmsPayrollWageDAO hrmsPayrollWageDAOObj1) {
        this.hrmsPayrollWageDAOObj = hrmsPayrollWageDAOObj1;
    }

    public void setHrmsPayrollTemplateDAO(hrmsPayrollTemplateDAO hrmsPayrollTemplateDAOObj1) {
        this.hrmsPayrollTemplateDAOObj = hrmsPayrollTemplateDAOObj1;
    }

    public ModelAndView setWagesData(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result=null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try{
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
            requestParams.put("expr",request.getParameter("expr"));
            if(!StringUtil.isNullOrEmpty(request.getParameter("computeon")) && !((request.getParameter("option").equals("Amount"))||(request.getParameter("optiontype").equals("$"))))
                requestParams.put("computeon",request.getParameter("computeon"));

            result = hrmsPayrollWageDAOObj.setWagesData(requestParams);
            if (request.getParameter("Action").equals("Add")&&result.isSuccessFlag()) {
                auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_COMPONENT_ADDED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has added new payroll component " +request.getParameter("name") + " of Wage type", request, "0");
            } else if(result.isSuccessFlag()){
                auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_COMPONENT_EDITED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has edited payroll component " +request.getParameter("name"), request, "0");
            }
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }

    public ModelAndView deleteMasterWage(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try{
            ArrayList filter_names =  new ArrayList();
            ArrayList filter_values =  new ArrayList();
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("id",request.getParameter("typeid"));
            filter_names.add("LIKE"+"t.expr");
            filter_values.add(request.getParameter("typeid"));
            requestParams.put("filter_names", filter_names);
            result = hrmsPayrollWageDAOObj.deleteMasterWage(requestParams);
            //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_DELETED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted payroll template  " + wm.getWagetype() + " of type Wage" ,request);
            if(result!=null && result.getEntityList()!=null && result.getEntityList().size()>0){
            	String logtemp=(String) result.getEntityList().get(0);
            	auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_COMPONENT_DELETED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has deleted payroll template  " + logtemp + " of type Wage", request, "0");
            }
            txnManager.commit(status);
        } catch (Exception e) {

            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }

     public ModelAndView getWageMaster(HttpServletRequest request, HttpServletResponse response)  {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;
        int count = 0;
        int deduccount = 0 , empcontcount = 0;
        try {
           String cid = sessionHandlerImplObj.getCompanyid(request);
           HashMap<String, Object> requestParams = new HashMap<String, Object>();
           requestParams.put("Cid", cid);
           requestParams.put("start", request.getParameter("start"));
           requestParams.put("limit", request.getParameter("limit"));
           requestParams.put("allflag", request.getParameter("allflag"));
           requestParams.put("ss", request.getParameter("ss"));
           if(request.getParameter("computeon")!=null){
               if(request.getParameter("isedit").equals("Edit")){
                   requestParams.put("filter_names",Arrays.asList("companydetails.companyID","!wageid"));
                   requestParams.put("filter_values",Arrays.asList(cid,request.getParameter("wageno")));
               } else {
                   requestParams.put("filter_names",Arrays.asList("companydetails.companyID"));
                   requestParams.put("filter_values",Arrays.asList(cid));
               }
               requestParams.put("append"," and ( computeon in (" +request.getParameter("computeon")+") or rate = 0 ) ");
           }else{
               requestParams.put("filter_names",Arrays.asList("companydetails.companyID"));
               requestParams.put("filter_values",Arrays.asList(cid));
               requestParams.put("append","  and (rate = 0 or (rate = 1 and computeon is not null)) ");
           }
           result=hrmsPayrollWageDAOObj.getWageMaster(requestParams);
                List lst=result.getEntityList();
                List lst1 = null;
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
                count = result.getRecordTotalCount();
           if(request.getParameter("deduc")!=null&&request.getParameter("deduc").equals("true")){
               if(request.getParameter("isedit")!=null&&request.getParameter("isedit").equals("Edit")){
                   requestParams.remove("filter_names");
                   requestParams.put("filter_names",Arrays.asList("companydetails.companyID","!deductionid"));
               }
               result=hrmsPayrollDeductionDAOObj.getDeductionMaster(requestParams);
               lst1=result.getEntityList();
               deduccount = lst1.size();
                    for (int i = 0; i < deduccount; i++) {
                        masterDB.Deductionmaster deduc = (masterDB.Deductionmaster) lst1.get(i);
                        JSONObject jobjtemp = new JSONObject();
                        jobjtemp.put("comp", "deduc");
                        jobjtemp.put("type", deduc.getDeductiontype());
                        jobjtemp.put("rate", deduc.getRate());
                        jobjtemp.put("cash", deduc.getCash());
                        jobjtemp.put("id", deduc.getDeductionid());
                        jobjtemp.put("code", deduc.getDcode());
                        jobjtemp.put("isdefault", deduc.isIsdefault());
                        jobjtemp.put("computeon", deduc.getComputeon());
                        jobjtemp.put("expr", deduc.getExpr());
                        jobj.append("data", jobjtemp);
                    }
           }
            if (request.getParameter("empcont") != null && request.getParameter("empcont").equals("true")) {
                requestParams.remove("filter_names");
                if (request.getParameter("isedit") != null && request.getParameter("isedit").equals("Edit")) {
                    requestParams.put("filter_names", Arrays.asList("companyid.companyID", "!id"));
                } else {
                    requestParams.put("filter_names",Arrays.asList("companyid.companyID"));
                }
                result = hrmsPayrollEmployerContributionDAOObj.getMasterEmployerContrib(requestParams);
                lst1 = result.getEntityList();
                empcontcount = lst1.size();
                for (int i = 0; i < empcontcount; i++) {
                    masterDB.EmployerContribution empcontrib = (masterDB.EmployerContribution) lst1.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("comp", "empcontrib");
                    jobjtemp.put("type", empcontrib.getEmpcontritype());
                    jobjtemp.put("rate", empcontrib.getRate());
                    jobjtemp.put("cash", empcontrib.getCash());
                    jobjtemp.put("id", empcontrib.getId());
                    jobjtemp.put("code", empcontrib.getEmpcontricode());
                    jobjtemp.put("isdefault", empcontrib.getIsdefault());
                    jobjtemp.put("computeon", empcontrib.getComputeon());
                    jobjtemp.put("expr", empcontrib.getExpr());
                    jobj.append("data", jobjtemp);
                }
            }

            if (lst.size() == 0 && deduccount == 0 && empcontcount == 0) {
                jobj.put("data", "");
            }
                jobj.put("success", true);
                jobj1 = new JSONObject();
                jobj1.put("valid", true);
                jobj.put("totalcount", count);
                jobj1.put("data", jobj);
        } catch (Exception e) {
            jobj.put("success", true);
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
            System.out.println("Error is "+e);
        }  finally {
            return new ModelAndView("jsonView", "model",jobj1.toString());
        }
    }

     public ModelAndView getWageMasterForComponent(HttpServletRequest request, HttpServletResponse response)  {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null,result1 = null;
        try {
           String cid = sessionHandlerImplObj.getCompanyid(request);
           HashMap<String, Object> requestParams = new HashMap<String, Object>();
           requestParams.put("Cid", cid);
           requestParams.put("wageid", request.getParameter("wageid"));
           result=hrmsPayrollWageDAOObj.getWageMasterChild(requestParams);
           List lst=result.getEntityList();
           String childWage = "(";
           for (int i = 0; i < lst.size(); i++) {
               masterDB.Wagemaster wage = (masterDB.Wagemaster) lst.get(i);
               childWage += "\'"+wage.getWageid()+"\'"+",";
           }
           childWage += "\'"+request.getParameter("wageid")+"\'";
           childWage += ")";
           
           requestParams.put("start", request.getParameter("start"));
           requestParams.put("limit", request.getParameter("limit"));
           requestParams.put("allflag", request.getParameter("allflag"));
           requestParams.put("ss", request.getParameter("ss"));
           requestParams.put("childwage", childWage);
           result=hrmsPayrollWageDAOObj.getWageMasterForComponent(requestParams);
                lst=result.getEntityList();
                for (int i = 0; i < lst.size(); i++) {
                    masterDB.Wagemaster wage = (masterDB.Wagemaster) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("type", wage.getWagetype());
                    jobjtemp.put("rate", wage.getRate());
                    jobjtemp.put("cash", wage.getCash());
                    jobjtemp.put("id", wage.getWageid());
                    jobjtemp.put("code", wage.getWcode());
                    jobjtemp.put("depwage", (wage.getDepwageid()==null)?"":wage.getDepwageid().getWagetype());
                    jobjtemp.put("depwageid", (wage.getDepwageid()==null)?"":wage.getDepwageid().getWageid());
                    jobjtemp.put("isdefault", wage.isIsdefault());
                    jobj.append("data", jobjtemp);
                }
                if (lst.size() == 0) {
                    jobj.put("data", "");
                }
                jobj.put("success", true);
                jobj1 = new JSONObject();
                jobj1.put("valid", true);
                jobj.put("totalcount", result.getRecordTotalCount());
                jobj1.put("data", jobj);
        } catch (Exception e) {
            System.out.println("Error is "+e);
        }  finally {
            return new ModelAndView("jsonView", "model",jobj1.toString());
        }
    }

    public ModelAndView getDefualtWages(HttpServletRequest request, HttpServletResponse response)  {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String cid = sessionHandlerImplObj.getCompanyid(request);
            requestParams.put("cid", cid);
            result = hrmsPayrollWageDAOObj.getDefualtWages(requestParams);
            List <masterDB.Wagemaster> lst2 = result.getEntityList();
            for (int i = 0; i < lst2.size(); i++) {
                masterDB.Wagemaster taxobj = (masterDB.Wagemaster) lst2.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("id", taxobj.getWageid());
                jobjtemp.put("type", taxobj.getWagetype());
                jobjtemp.put("cash", taxobj.getCash());
                jobjtemp.put("computeon", taxobj.getComputeon());
                jobjtemp.put("rate", taxobj.getRate());
                jobjtemp.put("expr", taxobj.getExpr());
                jobjtemp.put("code", taxobj.getWcode());
                jobjtemp.put("depwage", (taxobj.getDepwageid()==null)?"":taxobj.getDepwageid().getWagetype());
                jobjtemp.put("depwageid", (taxobj.getDepwageid()==null)?"":taxobj.getDepwageid().getWageid());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);

            jobj1.put("valid", true);
            jobj1.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
        } catch (Exception e) {
            System.out.println("Error is "+e);
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

        public ModelAndView getwagesPerTempid(HttpServletRequest request, HttpServletResponse response) {
        boolean success = false;
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        JSONObject jsAray = new JSONObject();
        try {
            String paycyclest = request.getParameter("paycyclestart");
            String paycycleed = request.getParameter("paycycleend");
            String ischanged = request.getParameter("ischanged");
            String mappingid = request.getParameter("mappingid");
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date paycyclestart = sdf.parse(paycyclest);
            Date paycycleend = sdf.parse(paycycleed);
            String paycyclestartdate = request.getParameter("paycycleactualstart");
            String paycycleenddate = request.getParameter("paycycleactualend");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String TempId = request.getParameter("TempId");
            String empid = request.getParameter("empid");
            String salary = request.getParameter("salary");
            int tc = 0;
            
            requestParams.put("empid", empid);
            requestParams.put("templateid", TempId);
            requestParams.put("paycyclestart", paycyclestart);
            requestParams.put("paycycleend", paycycleend);
            List<Payhistory> payhistories = getPayhistory(requestParams);
            if(payhistories.isEmpty()){
            String cid = sessionHandlerImplObj.getCompanyid(request);
            CompanyPreferences cp = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", cid);
            requestParams.put("filter_names", Arrays.asList("userAccount.userID","<=effectiveDate","mappingid"));
            requestParams.put("filter_values", Arrays.asList(empid,paycyclestart,mappingid));
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
                    }else if(temp.getPayinterval()==2){//Bi-monthly
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
                ArrayList Wagearr = new ArrayList();
                requestParams = new HashMap<String, Object>();
                requestParams.put("filter_names",Arrays.asList("template.templateid"));
                requestParams.put("filter_values",Arrays.asList(temp.getTemplateid()));
                result = hrmsPayrollWageDAOObj.getWageTemplateDetails(requestParams);
                List iteratorWage = result.getEntityList();
                int i = 0;
                Double amtot = tempmap.getBasic()*chargedays/dayinmonth;
                Boolean isrecord = false;
                JSONObject jsAraybasic = new JSONObject();
                jsAraybasic.put("Rate", tempmap.getBasic()*chargedays/dayinmonth);
                jsAraybasic.put("amount", tempmap.getBasic()*chargedays/dayinmonth);
                jsAraybasic.put("computeon", "-");
                jsAraybasic.put("Type", "Basic");
                jsAray.append("Wage", jsAraybasic);
                while (i < iteratorWage.size()) {
                    isrecord = false;
                    Templatemapwage wm = (Templatemapwage) iteratorWage.get(i);

                    double casht = wm.getWagemaster().getRate() == 0 ? wm.getWagemaster().getCash() : 0;
                    JSONObject jsAray1 = new JSONObject();
                    if (wm.getWagemaster().getRate() == 0) {
                            isrecord = true;
                            jsAray1.put("Rate", wm.getWagemaster().getRate());
                            jsAray1.put("amount", Double.parseDouble(wm.getRate())*chargedays/dayinmonth);
                            jsAray1.put("computeon", "-");
                            amtot += Float.parseFloat(wm.getRate())*chargedays/dayinmonth;
                    } else {
                        masterDB.Wagemaster checkwage = wm.getWagemaster();
                        if(checkwage.getComputeon()!=null&&checkwage.getComputeon()==3){
                            isrecord = true;
                            Double amount = 0.0;
                            amount =hrmsPayrollWageDAOObj.calculatewagessalary(checkwage.getExpr(),tempmap.getMappingid());
                            Double perc = Double.parseDouble(wm.getRate());
                            jsAray1.put("Rate", wm.getRate());
                            jsAray1.put("amount", (amount*perc/100)*chargedays/dayinmonth);
                            jsAray1.put("computeon", "Specified Formula");
                            amtot += (amount*perc/100)*chargedays/dayinmonth;
                        }
                    }
                    if(isrecord){
                        hrmsPayrollWageDAOImpl.setWagetotalval(amtot);
                        jsAray1.put("Id", wm.getWagemaster().getWageid());
                        jsAray1.put("Type", wm.getWagemaster().getWagetype());
                        jsAray1.put("ratetype", wm.getWagemaster().getRate());
                        jsAray1.put("depwage", (wm.getWagemaster().getDepwageid()==null)?"":wm.getWagemaster().getDepwageid().getWagetype());
                        jsAray1.put("depwageid", (wm.getWagemaster().getDepwageid()==null)?"":wm.getWagemaster().getDepwageid().getWageid());
                        jsAray1.put("amtot", amtot);
                        jsAray.append("Wage", jsAray1);
                    }
                    i++;
                }
                tc = i - 1;

            } else if (Integer.parseInt(temp.getStatus().toString()) == 1) {
                requestParams = new HashMap<String, Object>();
                requestParams.put("templateid", TempId);
                result = hrmsPayrollWageDAOObj.getTempWageTemplateDetails(requestParams);
                List iteratorWage = result.getEntityList();
                int i = 0;
                i = 0;
                float amtot = 0;
                while (i < iteratorWage.size()) {
                    TempTemplatemapwage wm = (TempTemplatemapwage) iteratorWage.get(i);
                    float amount = (Float.parseFloat(wm.getRate()) * Float.parseFloat(salary)) / 100;
                    amtot += Float.parseFloat(wm.getRate()) * Float.parseFloat(salary) / 100;
                    hrmsPayrollWageDAOImpl.setWagetotalval(amtot);
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("Id", wm.getWagemaster().getWageid());
                    jsAray1.put("Rate", wm.getRate());
                    jsAray1.put("Type", wm.getWagemaster().getWagetype());
                    jsAray1.put("amount", amount);
                    jsAray1.put("amtot", amtot);
                    jsAray.append("Wage", jsAray1);
                    i++;
                }
                tc = i - 1;
            }
            }else{
            	Payhistory payhistory = payhistories.get(0);
            	requestParams.clear();
            	requestParams.put("payHistoryId", payhistory.getHistoryid());
            	requestParams.put("name", "Wages");
            	List<Historydetail> historydetails = hrmsPayrollTemplateDAOObj.getPayHistorydetail(requestParams);
            	Iterator<Historydetail> iterator = historydetails.iterator();
            	while(iterator.hasNext()){
            		Historydetail historydetail = iterator.next();
            		Wagemaster wagemaster = historydetail.getWagemaster();
            		JSONObject jsonObject = new JSONObject();
            		if(wagemaster!=null){
                        boolean isrecord = false;
                        if(wagemaster.getRate() == 0){
                        	jsonObject.put("computeon", "-");
                        }else{
                        	if(wagemaster.getComputeon()!=null && wagemaster.getComputeon()==0){
                        		jsonObject.put("computeon", "Current Deduction");
                        	}else{
                        		if(wagemaster.getComputeon()!=null && wagemaster.getComputeon()==1){
                        			jsonObject.put("computeon", "Current Earning");
                        		}else{
                        			if(wagemaster.getComputeon()!=null && wagemaster.getComputeon()==2){
                        				jsonObject.put("computeon", "Net Salary");
                        			}else{
                        				isrecord = true;
                        				jsonObject.put("computeon", "Specified Formula");
                        				jsonObject.put("ratetype", wagemaster.getRate());
                        			}
                        		}
                        	}
                        }
                        if(isrecord){
                        	jsonObject.put("depwage", (wagemaster.getDepwageid()==null)?"":wagemaster.getWagetype());
                        	jsonObject.put("depwageid", (wagemaster.getDepwageid()==null)?"":wagemaster.getWageid());
                        }
                        
                        jsonObject.put("Id", wagemaster.getWageid());
                        jsonObject.put("Rate", wagemaster.getRate());
                        jsonObject.put("Type", wagemaster.getWagetype());
                        jsonObject.put("amount", historydetail.getAmount());
                        jsonObject.put("amtot", historydetail.getAmount());
                        jsAray.append("Wage", jsonObject);
            		}else{
            			jsonObject.put("Rate", historydetail.getRate());
            			jsonObject.put("Type", historydetail.getType());
            			jsonObject.put("amount", historydetail.getAmount());
            			jsonObject.put("computeon", "-");
            			jsAray.append("Wage", jsonObject);
            		}
                    tc++;
            	}
            }
            if(jsAray.isNull("Wage")){
                jsAray.put("Wage", new com.krawler.utils.json.base.JSONObject());
            }
            jsAray.put("success", true);
            jobj1.put("valid", true);
            jobj1.put("totalcount", tc);
            jobj1.put("data", jsAray);
            success = true;
        } catch (Exception se) {
            success = false;
            jsAray.put("success", true);
            jobj1.put("valid", true);
            jobj1.put("data", jsAray);
            se.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }


    public ModelAndView getDepwages(HttpServletRequest request, HttpServletResponse response) {
        boolean success = false;
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            JSONObject jsAray = new JSONObject();
            String TempId = request.getParameter("TempId");
            String ischanged = request.getParameter("ischanged");
            String empid = request.getParameter("empid");
            String paycyclest = request.getParameter("paycyclestart");
            String paycycleed = request.getParameter("paycycleend");
            String paycyclestartdate = request.getParameter("paycycleactualstart");
            String paycycleenddate = request.getParameter("paycycleactualend");
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date paycyclestart = sdf.parse(paycyclest);
            Date paycycleend = sdf.parse(paycycleed);
            
            String cid = sessionHandlerImplObj.getCompanyid(request);
            CompanyPreferences cp = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", cid);
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
            int tc = 0;
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
                    }
                    long chargesec = paycycleend.getTime()- paycyclestart.getTime()+(1000 * 60 * 60 * 24);
                    chargedays = (int) (chargesec / (1000 * 60 * 60 * 24));
                    chargedays -= StringUtil.nonWorkingDays(paycyclestart,paycycleend,p);
            }
            if (Integer.parseInt(temp.getStatus().toString()) == 0) {
                requestParams = new HashMap<String, Object>();
                requestParams.put("filter_names",Arrays.asList("template.templateid","INwagemaster.computeon","wagemaster.rate"));
                requestParams.put("filter_values",Arrays.asList(TempId,"0,1,2",1));
                requestParams.put("order_by",Arrays.asList("wagemaster.computeon"));
                requestParams.put("order_type",Arrays.asList("asc"));
                result = hrmsPayrollWageDAOObj.getWageTemplateDetails(requestParams);
                List iteratorWage = result.getEntityList();
                int i = 0;

                Double amtot = Double.parseDouble(request.getParameter("earnamount"));
                Double deducamtot = Double.parseDouble(request.getParameter("deducamount"));
                while (i < iteratorWage.size()) {
                    Templatemapwage wm = (Templatemapwage) iteratorWage.get(i);
                    double casht = wm.getWagemaster().getRate() == 0 ? wm.getWagemaster().getCash() : 0;
                    JSONObject jsAray1 = new JSONObject();
                    Wagemaster wagemaster = (Wagemaster) wm.getWagemaster();
                    if (wagemaster.getRate() == 1) {
                        if(wagemaster.getComputeon()!=null&&wagemaster.getComputeon()==0){
                            Double perc = Double.parseDouble(wm.getRate());
                            jsAray1.put("Rate", wm.getRate());
                            jsAray1.put("amount", (deducamtot*perc/100)*chargedays/dayinmonth);
                            jsAray1.put("computeon", "Current Deduction");
                            amtot += (deducamtot*perc/100)*chargedays/dayinmonth;
                        }else if(wagemaster.getComputeon()!=null&&wagemaster.getComputeon()==1){
                            Double perc = Double.parseDouble(wm.getRate());
                            jsAray1.put("Rate", wm.getRate());
                            jsAray1.put("amount", (amtot*perc/100)*chargedays/dayinmonth);
                            jsAray1.put("computeon", "Current Earning");
                            amtot += (amtot*perc/100)*chargedays/dayinmonth;
                        }else if(wagemaster.getComputeon()!=null&&wagemaster.getComputeon()==2){
                            Double perc = Double.parseDouble(wm.getRate());
                            jsAray1.put("Rate", wm.getRate());
                            jsAray1.put("amount", ((amtot-deducamtot)*perc/100)*chargedays/dayinmonth);
                            jsAray1.put("computeon", "Net Salary");
                            amtot += ((amtot-deducamtot)*perc/100)*chargedays/dayinmonth;
                        }
                    }
                    hrmsPayrollWageDAOImpl.setWagetotalval(amtot);
                    jsAray1.put("Id", wm.getWagemaster().getWageid());
                    jsAray1.put("Type", wm.getWagemaster().getWagetype());
                    jsAray1.put("ratetype", wm.getWagemaster().getRate());
                    jsAray1.put("depwage", (wm.getWagemaster().getDepwageid()==null)?"":wm.getWagemaster().getDepwageid().getWagetype());
                    jsAray1.put("depwageid", (wm.getWagemaster().getDepwageid()==null)?"":wm.getWagemaster().getDepwageid().getWageid());


                    jsAray1.put("amtot", amtot);
                    jsAray.append("Wage", jsAray1);
                    i++;
                }

                requestParams.clear();
                requestParams.put("filter_names",Arrays.asList("template.templateid","INdeductionmaster.computeon","deductionmaster.rate"));
                requestParams.put("filter_values",Arrays.asList(TempId,"0,1,2",1));
                requestParams.put("order_by",Arrays.asList("deductionmaster.computeon"));
                requestParams.put("order_type",Arrays.asList("asc"));
                result = hrmsPayrollDeductionDAOObj.getDeductionTemplateDetails(requestParams);
                iteratorWage = result.getEntityList();
                i = 0;

                while (i < iteratorWage.size()) {
                    Templatemapdeduction wm = (Templatemapdeduction) iteratorWage.get(i);
                    double casht = wm.getDeductionmaster().getRate() == 0 ? wm.getDeductionmaster().getCash() : 0;
                    JSONObject jsAray1 = new JSONObject();
                        Deductionmaster deducmaster = (Deductionmaster) wm.getDeductionmaster();
                    if (deducmaster.getRate() == 1) {
                        if(deducmaster.getComputeon()!=null&&deducmaster.getComputeon()==0){
                            Double perc = Double.parseDouble(wm.getRate());
                            jsAray1.put("Rate", wm.getRate());
                            jsAray1.put("amount", (deducamtot*perc/100)*chargedays/dayinmonth);
                            jsAray1.put("computeon", "Current Deduction");
                            deducamtot += (deducamtot*perc/100)*chargedays/dayinmonth;
                        }else if(deducmaster.getComputeon()!=null&&deducmaster.getComputeon()==1){
                            Double perc = Double.parseDouble(wm.getRate());
                            jsAray1.put("Rate", wm.getRate());
                            jsAray1.put("amount", (amtot*perc/100)*chargedays/dayinmonth);
                            jsAray1.put("computeon", "Current Earning");
                            deducamtot += (amtot*perc/100)*chargedays/dayinmonth;
                        }else if(deducmaster.getComputeon()!=null&&deducmaster.getComputeon()==2){
                            Double perc = Double.parseDouble(wm.getRate());
                            jsAray1.put("Rate", wm.getRate());
                            jsAray1.put("amount", ((amtot-deducamtot)*perc/100)*chargedays/dayinmonth);
                            jsAray1.put("computeon", "Net Salary");
                            deducamtot += ((amtot-deducamtot)*perc/100)*chargedays/dayinmonth;
                        }
                    }
                    hrmsPayrollWageDAOImpl.setWagetotalval(amtot);
                    jsAray1.put("Id", wm.getDeductionmaster().getDeductionid());
                    jsAray1.put("Type", wm.getDeductionmaster().getDeductiontype());
                    jsAray1.put("ratetype", wm.getDeductionmaster().getRate());
                    jsAray1.put("depwage", "");
                    jsAray1.put("depwageid","");
                    jsAray1.put("amtot", amtot);
                    jsAray.append("Deduc", jsAray1);
                    i++;
                }

                requestParams.clear();
                requestParams.put("filter_names",Arrays.asList("template.templateid","INtaxmaster.computeon","taxmaster.rate"));
                requestParams.put("filter_values",Arrays.asList(TempId,"0,1,2",1));
                requestParams.put("order_by",Arrays.asList("taxmaster.computeon"));
                requestParams.put("order_type",Arrays.asList("asc"));
                result = hrmsPayrollTaxDAOObj.getTaxTemplateDetails(requestParams);
                iteratorWage = result.getEntityList();
                i = 0;

                while (i < iteratorWage.size()) {
                    Templatemaptax wm = (Templatemaptax) iteratorWage.get(i);
                    double casht = wm.getTaxmaster().getRate() == 0 ? wm.getTaxmaster().getCash() : 0;
                    JSONObject jsAray1 = new JSONObject();
                        Taxmaster taxmaster = (Taxmaster) wm.getTaxmaster();
                    if (taxmaster.getRate() == 1) {
                        if(taxmaster.getComputeon()!=null&&taxmaster.getComputeon()==0){
                            Double perc = Double.parseDouble(wm.getRate());
                            jsAray1.put("Rate", wm.getRate());
                            jsAray1.put("amount", (deducamtot*perc/100)*chargedays/dayinmonth);
                            jsAray1.put("computeon", "Current Deduction");
                            deducamtot += (deducamtot*perc/100)*chargedays/dayinmonth;
                        }else if(taxmaster.getComputeon()!=null&&taxmaster.getComputeon()==1){
                            Double perc = Double.parseDouble(wm.getRate());
                            jsAray1.put("Rate", wm.getRate());
                            jsAray1.put("amount", (amtot*perc/100)*chargedays/dayinmonth);
                            jsAray1.put("computeon", "Current Earning");
                            deducamtot += (amtot*perc/100)*chargedays/dayinmonth;
                        }else if(taxmaster.getComputeon()!=null&&taxmaster.getComputeon()==2){
                            Double perc = Double.parseDouble(wm.getRate());
                            jsAray1.put("Rate", wm.getRate());
                            jsAray1.put("amount", ((amtot-deducamtot)*perc/100)*chargedays/dayinmonth);
                            jsAray1.put("computeon", "Net Salary");
                            deducamtot += ((amtot-deducamtot)*perc/100)*chargedays/dayinmonth;
                        }
                    }
                    hrmsPayrollWageDAOImpl.setWagetotalval(amtot);
                    jsAray1.put("Id", wm.getTaxmaster().getTaxid());
                    jsAray1.put("Type", wm.getTaxmaster().getTaxtype());
                    jsAray1.put("ratetype", wm.getTaxmaster().getRate());
                    jsAray1.put("depwage", "");
                    jsAray1.put("depwageid","");
                    jsAray1.put("amtot", amtot);
                    jsAray.append("Tax", jsAray1);
                    i++;
                }

                requestParams.clear();
                requestParams.put("filter_names",Arrays.asList("template.templateid","INempcontrimaster.computeon","empcontrimaster.rate"));
                requestParams.put("filter_values",Arrays.asList(TempId,"0,1,2",1));
                requestParams.put("order_by",Arrays.asList("empcontrimaster.computeon"));
                requestParams.put("order_type",Arrays.asList("asc"));
                result = hrmsPayrollEmployerContributionDAOObj.getEmployerContribTemplateDetails(requestParams);
                iteratorWage = result.getEntityList();
                i = 0;

                while (i < iteratorWage.size()) {
                    TemplateMapEmployerContribution wm = (TemplateMapEmployerContribution) iteratorWage.get(i);
                    double casht = wm.getEmpcontrimaster().getRate() == 0 ? wm.getEmpcontrimaster().getCash() : 0;
                    JSONObject jsAray1 = new JSONObject();
                        EmployerContribution empContrib = (EmployerContribution) wm.getEmpcontrimaster();
                    if (empContrib.getRate() == 1) {
                        if(empContrib.getComputeon()!=null&&empContrib.getComputeon()==0){
                            Double perc = Double.parseDouble(wm.getRate());
                            jsAray1.put("Rate", wm.getRate());
                            jsAray1.put("amount", (deducamtot*perc/100)*chargedays/dayinmonth);
                            jsAray1.put("computeon", "Current Deduction");
                            deducamtot += (deducamtot*perc/100)*chargedays/dayinmonth;
                        }else if(empContrib.getComputeon()!=null&&empContrib.getComputeon()==1){
                            Double perc = Double.parseDouble(wm.getRate());
                            jsAray1.put("Rate", wm.getRate());
                            jsAray1.put("amount", (amtot*perc/100)*chargedays/dayinmonth);
                            jsAray1.put("computeon", "Current Earning");
                            deducamtot += (amtot*perc/100)*chargedays/dayinmonth;
                        }else if(empContrib.getComputeon()!=null&&empContrib.getComputeon()==2){
                            Double perc = Double.parseDouble(wm.getRate());
                            jsAray1.put("Rate", wm.getRate());
                            jsAray1.put("amount", ((amtot-deducamtot)*perc/100)*chargedays/dayinmonth);
                            jsAray1.put("computeon", "Net Salary");
                            deducamtot += ((amtot-deducamtot)*perc/100)*chargedays/dayinmonth;
                        }
                    }
                    hrmsPayrollWageDAOImpl.setWagetotalval(amtot);
                    jsAray1.put("Id", wm.getEmpcontrimaster().getId());
                    jsAray1.put("Type", wm.getEmpcontrimaster().getEmpcontritype());
                    jsAray1.put("ratetype", wm.getEmpcontrimaster().getRate());
                    jsAray1.put("amtot", amtot);
                    jsAray.append("EC", jsAray1);
                    i++;
                }
                
            }
            jsAray.put("success", true);
            jobj1.put("valid", true);
            jobj1.put("totalcount", tc);
            jobj1.put("data", jsAray);
            success = true;
        }catch(Exception ex){
            logger.warn("Exception occurred in hrmsPayrollWageController.getDepwages", ex);
            success = false;
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    
    public double getRecursiveAmountForWageGrid(Wagemaster wm, ArrayList arr, String salary) {
        double amt = 0;
        double sal = 0;
            sal = Double.parseDouble(salary);
            for(int i=arr.size()-1 ; i>=0 ; i--){
                amt = Double.parseDouble(arr.get(i).toString()) * sal / 100;
                sal = amt;
            }
        return sal;
    }
    public ModelAndView getWagesData(HttpServletRequest request, HttpServletResponse response) {

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
            //Template temp = (Template) session.get(Template.class, TempId);
            List templateList = result.getEntityList();
            Template temp = null;
            if(templateList.size()>0)
                temp = (Template) templateList.get(0);
            int tc = 0;
//            requestParams.put();
//            Template templ = (Template) session.get(Template.class, temid);
            JSONObject jobj = new JSONObject();

            if (Integer.parseInt(temp.getStatus().toString()) == 0) {
//                String query = "from Templatemapwage w where w.template.templateid=?";
//                List lst = (List) HibernateUtil.executeQuery(session, query, new Object[]{temid});
                requestParams.put("filter_names",Arrays.asList("template.templateid"));
                requestParams.put("filter_values",Arrays.asList(TempId));
                result = hrmsPayrollWageDAOObj.getWageTemplateDetails(requestParams);
                List lst=result.getEntityList();
                int i;
                for (i = 0; i < lst.size(); i++) {
                    masterDB.Templatemapwage wage = (masterDB.Templatemapwage) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("assigned", "1");
                    jobjtemp.put("type", wage.getWagemaster().getWagetype());
//                    if(wage.getWagemaster().getRate() == 0 && Integer.parseInt(wage.getRate()) < 100){
//                        double amount = (Double.parseDouble(wage.getRate())  * Double.parseDouble(temp.getEndrange())) / 100;
//                        jobjtemp.put("cash", amount);
//                    } else {
                        jobjtemp.put("cash", wage.getRate());
//                    }
//                    //jobjtemp.put("cash", wage.getRate());
                    jobjtemp.put("rate", wage.getWagemaster().getRate());
                    jobjtemp.put("id", wage.getWagemaster().getWageid());
                    jobjtemp.put("code", wage.getWagemaster().getWcode());
                    jobjtemp.put("depwage", (wage.getWagemaster().getDepwageid()==null)?"":wage.getWagemaster().getDepwageid().getWagetype());
                    jobjtemp.put("depwageid", (wage.getWagemaster().getDepwageid()==null)?"":wage.getWagemaster().getDepwageid().getWageid());
                    jobjtemp.put("expr", (wage.getWagemaster().getExpr()==null)?"":wage.getWagemaster().getExpr());
                    jobjtemp.put("computeon", (wage.getWagemaster().getComputeon()==null)?"":wage.getWagemaster().getComputeon());
                    jobj.append("data", jobjtemp);
                }
                tc = i;
                if(lst.size()==0){
                  jobj.put("data","");
                }
            } else if (Integer.parseInt(temp.getStatus().toString()) == 1) {
//                String query = "select t.wagemaster.wageid,t.rate,t.wagemaster.wagetype,t.wagemaster.wcode from TempTemplatemapwage t where t.template.templateid=? and t.wagemaster.companydetails.companyID=?";
//                List lst = (List) HibernateUtil.executeQuery(session, query, new Object[]{temid, Cid});
                result = hrmsPayrollWageDAOObj.getTempWageTemplateDetails(requestParams);
                List lst =result.getEntityList();
                int i;
                for (i = 0; i < lst.size(); i++) {
//                    Object[] obj = (Object[]) lst.get(i);
//                    JSONObject jobjtemp = new JSONObject();
//                    jobjtemp.put("type", obj[2]);
//                    jobjtemp.put("assigned", "1");
//                    jobjtemp.put("cash", obj[1]);
//                    jobjtemp.put("id", obj[0]);
//                    jobjtemp.put("code", obj[3]);
//                    jobj.append("data", jobjtemp);
                    masterDB.TempTemplatemapwage wage = (masterDB.TempTemplatemapwage) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("type", wage.getWagemaster().getWagetype());
                    jobjtemp.put("assigned", "1");
                    jobjtemp.put("cash", wage.getRate());
                    jobjtemp.put("rate", wage.getWagemaster().getRate());
                    jobjtemp.put("id", wage.getWagemaster().getWageid());
                    jobjtemp.put("code", wage.getWagemaster().getWcode());
                    jobj.append("data", jobjtemp);
                }
                tc = i;
                if(lst.size()==0){
                  jobj.put("data","");
                }
            }
            jobj.put("success", true);

            jobj1.put("valid", true);
            jobj1.put("totalcount", tc);
            jobj1.put("data", jobj);
            success = true;
//            return (jobj1.toString());
        } catch (Exception se) {
//            return (se.getMessage());
            success = false;
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

     public ModelAndView getPayRollSummary(HttpServletRequest request, HttpServletResponse response)  {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null,result1 = null;
        try {
           String cid = sessionHandlerImplObj.getCompanyid(request);
           String ss = request.getParameter("ss");
           HashMap<String, Object> requestParams = new HashMap<String, Object>();
           if(!StringUtil.isNullOrEmpty(ss)){
                requestParams.put("ss", ss);
           }
           result=hrmsPayrollWageDAOObj.getPayRollSummary(requestParams);
           List lst=result.getEntityList();
                for (int i = 0; i < lst.size(); i++) {
                    masterDB.PayrollSummary ps = (masterDB.PayrollSummary) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("id", ps.getId());
                    jobjtemp.put("empname", ps.getEmpname());
                    jobjtemp.put("empid", ps.getEmpid());
                    jobjtemp.put("nric", ps.getNric());
                    jobjtemp.put("epfnumber", ps.getEpfnumber());
                    jobjtemp.put("grosssalary", ps.getGrosssalary());
                    jobjtemp.put("epfemployer", ps.getEpfemployer());
                    jobjtemp.put("epfemployee", ps.getEpfemployee());
                    jobjtemp.put("month", ps.getMonth());
                    jobj.append("data", jobjtemp);
                }
                if (lst.size() == 0) {
                    jobj.put("data", "");
                }
                jobj.put("success", true);
                jobj1 = new JSONObject();
                jobj1.put("valid", true);
                jobj.put("totalcount", result.getRecordTotalCount());
                jobj1.put("data", jobj);
        } catch (Exception e) {
            System.out.println("Error is "+e);
        }  finally {
            return new ModelAndView("jsonView", "model",jobj1.toString());
        }
    }
     
     private exportDAOImpl exportDAOImplObj;
     public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
     }
     
     public ModelAndView getPayRollSummaryExport(HttpServletRequest request, HttpServletResponse response)  {        
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null,result1 = null;
        try {
           String cid = sessionHandlerImplObj.getCompanyid(request);
           String ss = request.getParameter("ss");
           HashMap<String, Object> requestParams = new HashMap<String, Object>();
           if(!StringUtil.isNullOrEmpty(ss)){
                requestParams.put("ss", ss);
           }
           result=hrmsPayrollWageDAOObj.getPayRollSummary(requestParams);
           List lst=result.getEntityList();
            for (int i = 0; i < lst.size(); i++) {
                masterDB.PayrollSummary ps = (masterDB.PayrollSummary) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("id", ps.getId());
                jobjtemp.put("empname", ps.getEmpname());
                jobjtemp.put("empid", ps.getEmpid());
                jobjtemp.put("nric", ps.getNric());
                jobjtemp.put("epfnumber", ps.getEpfnumber());
                jobjtemp.put("grosssalary", ps.getGrosssalary());
                jobjtemp.put("epfemployer", ps.getEpfemployer());
                jobjtemp.put("epfemployee", ps.getEpfemployee());
                jobjtemp.put("month", ps.getMonth());
                jobj.append("data", jobjtemp);
            }
            if (lst.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            exportDAOImplObj.processRequest(request, response, jobj);
        } catch (Exception e) {
            System.out.println("Error is "+e);
        }  finally {
            return new ModelAndView("jsonView", "model",jobj.toString());
        }
    }

     public ModelAndView deletePayrollSummary(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        try{
           
            String id[] = request.getParameterValues("ids");
            result = hrmsPayrollWageDAOObj.deletePayrollSummary(id);
            jobj.put("success", true);
            jobj.put("msg", true);
            
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
            txnManager.commit(status);
        } catch (Exception e) {

            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }
    
     public ModelAndView payrollSummarySave(HttpServletRequest request, HttpServletResponse response)
     {
        JSONObject myjobj = new JSONObject();
        JSONObject myjobj1 = new JSONObject();
        KwlReturnObject kmsg = null;
        masterDB.PayrollSummary payrollSummary = null;
//        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String userid = sessionHandlerImplObj.getUserid(request);
            String Id = jobj.getString("id");
            jobj.put("userid ", userid);
            kmsg = hrmsPayrollWageDAOObj.payrollSummarySave(jobj);
            payrollSummary = (masterDB.PayrollSummary) kmsg.getEntityList().get(0);
            
            myjobj.put("success", true);
            myjobj.put("ID", payrollSummary.getId());
            
            myjobj1.put("valid", true);
            myjobj1.put("data", myjobj);
            txnManager.commit(status);
        } catch (Exception e) {
            txnManager.rollback(status);
            logger.warn("Exception occurred in hrmsPayrollWageController.payrollSummarySave", e);
           
        } finally {
            
        }
        return new ModelAndView("jsonView", "model", myjobj1.toString());
    }
     public ModelAndView getCompanyDetail(HttpServletRequest request, HttpServletResponse response)  {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null,result1 = null;
        try {
           String cid = sessionHandlerImplObj.getCompanyid(request);
           String ss = request.getParameter("ss");
           HashMap<String, Object> requestParams = new HashMap<String, Object>();
           if(!StringUtil.isNullOrEmpty(ss)){
                requestParams.put("ss", ss);
           }
           result=hrmsPayrollWageDAOObj.getCompanyDetail(requestParams,cid);
           List lst=result.getEntityList();
                for (int i = 0; i < lst.size(); i++) {
                    masterDB.CompanyEPFInfo ps = (masterDB.CompanyEPFInfo) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("id", ps.getCompanyid());
                    jobjtemp.put("companyname", ps.getCompanyname());
                    jobjtemp.put("address", ps.getCompanyaddress());
                    jobjtemp.put("companyno", ps.getCompanyno());
                    jobjtemp.put("Companyepfno", ps.getCompanyepfno());
                    jobj.append("data", jobjtemp);
                }
                if (lst.size() == 0) {
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("id", "");
                    jobjtemp.put("companyname", "");
                    jobjtemp.put("address", "");
                    jobjtemp.put("companyno", "");
                    jobjtemp.put("Companyepfno","");
                    jobj.append("data", jobjtemp);
                }
                jobj.put("success", true);
                jobj1 = new JSONObject();
                jobj1.put("valid", true);
                jobj.put("totalcount", result.getRecordTotalCount());
                jobj1.put("data", jobj);
        } catch (Exception e) {
            System.out.println("Error is "+e);
        }  finally {
            return new ModelAndView("jsonView", "model",jobj1.toString());
        }
    }

        public ModelAndView CompanyEPFDetailSave(HttpServletRequest request, HttpServletResponse response)
     {
        JSONObject myjobj = new JSONObject();
        JSONObject myjobj1 = new JSONObject();
        KwlReturnObject kmsg = null;
        String cname=request.getParameter("name"),cno=request.getParameter("compno"),addr=request.getParameter("addr"),cepfno=request.getParameter("epfno");
        masterDB.CompanyEPFInfo payrollSummary = null;
//        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            JSONObject jobj = new JSONObject();
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String userid = sessionHandlerImplObj.getUserid(request);
//            String Id = jobj.getString("id");
//            jobj.put("userid ", userid);
            kmsg = hrmsPayrollWageDAOObj.SaveCompanyEPFDetail(companyid,cname,cno,addr,cepfno,jobj);
            payrollSummary = (masterDB.CompanyEPFInfo) kmsg.getEntityList().get(0);
            myjobj.put("success", true);
            myjobj.put("ID", payrollSummary.getCompanyid());
            myjobj1.put("valid", true);
            myjobj1.put("data", myjobj);
            txnManager.commit(status);
        } catch (Exception e) {
            txnManager.rollback(status);
            logger.warn("Exception occurred in hrmsPayrollWageController.CompanyEPFInfo", e);
        } finally {
        }
        return new ModelAndView("jsonView", "model", myjobj1.toString());
    }
}
