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
package com.krawler.spring.hrms.payroll.tax;

import com.krawler.common.util.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.hrms.master.MasterData;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.employee.hrmsEmpDAO;
import com.krawler.spring.hrms.payroll.template.hrmsPayrollTemplateDAO;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.userSalaryTemplateMap;
import com.krawler.spring.hrms.payroll.deduction.hrmsPayrollDeductionDAO;
import com.krawler.spring.hrms.payroll.employercontribution.hrmsPayrollEmployerContributionDAO;
import com.krawler.spring.hrms.payroll.wages.hrmsPayrollWageDAO;
import com.krawler.spring.hrms.payroll.wages.hrmsPayrollWageDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.utils.json.base.JSONObject;
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
import masterDB.EmployerContribution;
import masterDB.Historydetail;
import masterDB.Payhistory;
import masterDB.Taxmaster;
import masterDB.TempTemplatemaptax;
import masterDB.Template;
import masterDB.Templatemaptax;
import masterDB.Wagemaster;
import org.hibernate.SessionFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTemplate;
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
public class hrmsPayrollTaxController extends MultiActionController implements MessageSourceAware {

    private String successView;
    private hrmsPayrollTaxDAO hrmsPayrollTaxDAOObj;
    private hrmsPayrollWageDAO hrmsPayrollWageDAOObj;
    private hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj;
    private HibernateTemplate hibernateTemplate;
    private hrmsPayrollTemplateDAO hrmsPayrollTemplateDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private hrmsPayrollEmployerContributionDAO hrmsPayrollEmployerContributionDAOObj;
    private hrmsEmpDAO hrmsEmpDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private MessageSource messageSource;
    

    public void setHrmsPayrollEmployerContributionDAO(hrmsPayrollEmployerContributionDAO hrmsPayrollEmployerContributionDAOObj) {
        this.hrmsPayrollEmployerContributionDAOObj = hrmsPayrollEmployerContributionDAOObj;
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
     public void setHrmsPayrollDeductionDAO(hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj) {
        this.hrmsPayrollDeductionDAOObj = hrmsPayrollDeductionDAOObj;
    }
    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public hrmsPayrollTemplateDAO getHrmsPayrollTemplateDAOObj() {
        return hrmsPayrollTemplateDAOObj;
    }

    public void setHrmsPayrollTemplateDAO(hrmsPayrollTemplateDAO hrmsPayrollTemplateDAOObj) {
        this.hrmsPayrollTemplateDAOObj = hrmsPayrollTemplateDAOObj;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setHrmsPayrollTaxDAO(hrmsPayrollTaxDAO hrmsPayrollTaxDAOObj1) {
        this.hrmsPayrollTaxDAOObj = hrmsPayrollTaxDAOObj1;
    }

    public void setHrmsPayrollWageDAO(hrmsPayrollWageDAO hrmsPayrollWageDAOObj1) {
        this.hrmsPayrollWageDAOObj = hrmsPayrollWageDAOObj1;
    }
    
    public void setHrmsEmpDAO(hrmsEmpDAO hrmsEmpDAOObj1) {
        this.hrmsEmpDAOObj = hrmsEmpDAOObj1;
    }
    
    public kwlCommonTablesDAO getKwlCommonTablesDAOObj() {
        return kwlCommonTablesDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public ModelAndView setTaxData(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("name", request.getParameter("name"));
            requestParams.put("rate", request.getParameter("rate"));
            requestParams.put("code", request.getParameter("code"));
            requestParams.put("Cid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("option", request.getParameter("option"));
            requestParams.put("isChecked", request.getParameter("isChecked"));
            requestParams.put("Action", request.getParameter("Action"));
            requestParams.put("typeid", request.getParameter("typeid"));
            requestParams.put("categoryid", request.getParameter("categoryid"));
            requestParams.put("depwageid",request.getParameter("depwageid"));
            requestParams.put("expr",request.getParameter("expr"));
//            requestParams.put("computeon",request.getParameter("computeon"));
            if(!StringUtil.isNullOrEmpty(request.getParameter("computeon")) && !(request.getParameter("option").equals("Amount")))
                    requestParams.put("computeon",request.getParameter("computeon"));
            result = hrmsPayrollTaxDAOObj.setTaxData(requestParams);
            if (request.getParameter("Action").equals("Add")&&result.isSuccessFlag()) {
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new payroll component " +insertWage.getTaxtype() + " of type Tax"  ,request);
                auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_COMPONENT_ADDED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has added new payroll component " +request.getParameter("name") + " of Tax type", request, "0");
            } else if(result.isSuccessFlag()){
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited payroll component " +insertWage.getTaxtype() ,request);
                auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_COMPONENT_EDITED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has edited payroll component " +request.getParameter("name"), request, "0");
            }
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }

    public ModelAndView deleteMasterTax(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("id", request.getParameter("typeid"));

            result = hrmsPayrollTaxDAOObj.deleteMasterTax(requestParams);
            //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_DELETED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted payroll template  " + tm.getTaxtype() + " of type Tax" ,request);
            if(result!=null && result.getEntityList()!=null && !result.getEntityList().isEmpty()){
                String logtemp=(String) result.getEntityList().get(0);
                auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_COMPONENT_DELETED, "User " + profileHandlerDAOObj.getUserFullName(AuthHandler.getUserid(request)) + " has deleted payroll template  " + logtemp + " of Tax type", request, "0");
            }
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }

    public ModelAndView GetTaxperCatgry(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            MasterData m = null;
            List lst2 = null;
            int cnt = 0;

            requestParams.put("cid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("start", request.getParameter("start"));
            requestParams.put("limit", request.getParameter("limit"));
            requestParams.put("categoryid", request.getParameter("categoryid"));
            result = hrmsPayrollTaxDAOObj.GetTaxperCatgry(requestParams);
            lst2 = result.getEntityList();
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst2.size(); i++) {
                masterDB.Taxmaster taxobj = (masterDB.Taxmaster) lst2.get(i);
                JSONObject jobjtemp = new JSONObject();
                if (!StringUtil.isNullOrEmpty(taxobj.getCategoryid())) {
                    m = (MasterData) hibernateTemplate.get(MasterData.class, taxobj.getCategoryid());
                    jobjtemp.put("category", m!=null?m.getValue():"");
                }
                jobjtemp.put("rate", taxobj.getRate() == 1 ? taxobj.getCash() : "");
                jobjtemp.put("rangefrom", taxobj.getRangefrom());
                jobjtemp.put("rangeto", taxobj.getRangeto());
                jobjtemp.put("id", taxobj.getTaxid());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", cnt);
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView setNewIncometax(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            requestParams.put("cid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("rangefrom", request.getParameter("rangefrom"));
            requestParams.put("rangeto", request.getParameter("rangeto"));
            requestParams.put("catgryid", request.getParameter("catgryid"));
            requestParams.put("categoryname", request.getParameter("categoryname"));
            requestParams.put("rate", request.getParameter("rate"));

            result = hrmsPayrollTaxDAOObj.setNewIncometax(requestParams);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }

    public ModelAndView deleteincomeTax(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("typeid", request.getParameter("typeid"));
            result = hrmsPayrollTaxDAOObj.deleteincomeTax(requestParams);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }

     public ModelAndView getTaxMaster(HttpServletRequest request, HttpServletResponse response)  {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null;
        try {
           String cid = sessionHandlerImplObj.getCompanyid(request);
           HashMap<String, Object> requestParams = new HashMap<String, Object>();
           requestParams.put("Cid", cid);
           requestParams.put("start", request.getParameter("start"));
           requestParams.put("limit", request.getParameter("limit"));
           requestParams.put("allflag", request.getParameter("allflag"));
           requestParams.put("ss", request.getParameter("ss"));
           result=hrmsPayrollTaxDAOObj.getTaxMaster(requestParams);
                List lst=result.getEntityList();
                for (int i = 0; i < lst.size(); i++) {
                    masterDB.Taxmaster wage = (masterDB.Taxmaster) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("comp", "tax");
                    jobjtemp.put("type", wage.getTaxtype());
                    jobjtemp.put("rate", wage.getRate());
                    jobjtemp.put("cash", wage.getCash());
                    jobjtemp.put("id", wage.getTaxid());
                    jobjtemp.put("code", wage.getTcode());
                    jobjtemp.put("isdefault", wage.isIsdefault());
                    jobjtemp.put("depwage", (wage.getDepwageid()==null)?"":wage.getDepwageid().getWagetype());
                    jobjtemp.put("depwageid", (wage.getDepwageid()==null)?"":wage.getDepwageid().getWageid());
                    jobjtemp.put("rangefrom", !Double.isNaN(wage.getRangefrom()) ? wage.getRangefrom() : 0);
                    jobjtemp.put("rangeto", !Double.isNaN(wage.getRangeto()) ? wage.getRangeto() : 0);
                    jobjtemp.put("category", wage.getCategoryid() != null ? wage.getCategoryid() : "");
                    jobjtemp.put("computeon", wage.getComputeon());
                    jobjtemp.put("expr", wage.getExpr());
                    jobj.append("data", jobjtemp);
                }
                if(request.getParameter("deduc")!=null&&request.getParameter("deduc").equals("true")){
                   requestParams.put("filter_names",Arrays.asList("companydetails.companyID"));
                   requestParams.put("filter_values",Arrays.asList(cid));
                   requestParams.put("append","  and (rate = 0 or (rate = 1 and computeon is not null)) ");
                   result=hrmsPayrollDeductionDAOObj.getDeductionMaster(requestParams);
                   lst=result.getEntityList();
                        for (int i = 0; i < lst.size(); i++) {
                            masterDB.Deductionmaster deduc = (masterDB.Deductionmaster) lst.get(i);
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
                if(request.getParameter("wage")!=null&&request.getParameter("wage").equals("true")){
                    result=hrmsPayrollWageDAOObj.getWageMaster(requestParams);
                    lst=result.getEntityList();
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
                if(request.getParameter("empcont")!=null && request.getParameter("empcont").equals("true")){
                    result=hrmsPayrollEmployerContributionDAOObj.getMasterEmployerContrib(requestParams);
                    lst=result.getEntityList();
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

     public ModelAndView getDefualtTax(HttpServletRequest request, HttpServletResponse response)  {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String cid = sessionHandlerImplObj.getCompanyid(request);
            requestParams.put("cid", cid);
            result = hrmsPayrollTaxDAOObj.getDefualtTax(requestParams);
            List <masterDB.Taxmaster> lst2 = result.getEntityList();
            for (int i = 0; i < lst2.size(); i++) {
                masterDB.Taxmaster taxobj = (masterDB.Taxmaster) lst2.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("id", taxobj.getTaxid());
                jobjtemp.put("type", taxobj.getTaxtype());
                jobjtemp.put("cash", taxobj.getCash());
                jobjtemp.put("rate", taxobj.getRate());
                jobjtemp.put("computeon", taxobj.getComputeon());
                jobjtemp.put("code", taxobj.getTcode());
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
     
     public ModelAndView getTaxPerTempid(HttpServletRequest request, HttpServletResponse response) {
        boolean success = false;
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {
        	int i = 0;
            String paycyclest = request.getParameter("paycyclestart");
            String paycycleed = request.getParameter("paycycleend");
            String paycyclestartdate = request.getParameter("paycycleactualstart");
            String paycycleenddate = request.getParameter("paycycleactualend");
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date paycyclestart = sdf.parse(paycyclest);
            Date paycycleend = sdf.parse(paycycleed);
            JSONObject jsAray = new JSONObject();
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String TempId = request.getParameter("TempId");
            String empid = request.getParameter("empid");
            requestParams.put("empid", empid);
            requestParams.put("templateid", TempId);
            requestParams.put("paycyclestart", paycyclestart);
            requestParams.put("paycycleend", paycycleend);
            String cid = sessionHandlerImplObj.getCompanyid(request);
            CompanyPreferences cp = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", cid);
            List<Payhistory> payhistories = getPayhistory(requestParams);
            if(payhistories.isEmpty()){
            Empprofile empprofile = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", empid);
            String salary = request.getParameter("salary");
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
            String ischanged = request.getParameter("ischanged");
//            if(ischanged.equals("1")){
//                int holiday = cp.getWeeklyholidays();
//                Date cyclestartdate = sdf.parse(paycyclestartdate);
//                Date cycleenddate = sdf.parse(paycycleenddate);
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(paycyclestart);
//                int[] intArray = new int[]{-1,-1,-1,-1,-1,-1,-1};
//                HashMap<Integer,Integer> p=new HashMap<Integer,Integer>();
//                p = StringUtil.getHolidayArr(holiday);
//                if(temp.getPayinterval()==1){
//                    dayinmonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//                    dayinmonth -= StringUtil.nonWorkingDays(cyclestartdate,cycleenddate,p);
//                }else if(temp.getPayinterval()==3){
//                    dayinmonth = 7;
//                    dayinmonth -= StringUtil.nonWorkingDays(cyclestartdate,cycleenddate,p);
//                }else if(temp.getPayinterval()==2){
//                    Calendar c1 = Calendar.getInstance();
//                    Calendar c2 = Calendar.getInstance();
//                    c1.setTime(cyclestartdate);
//                    c2.setTime(cycleenddate);
//                    long diff = c2.getTimeInMillis() - c1.getTimeInMillis();
//                    dayinmonth =(int)( diff / (24 * 60 * 60 * 1000));
//                    dayinmonth += 1;
//                    dayinmonth -= StringUtil.nonWorkingDays(cyclestartdate,cycleenddate,p);
//                }
//                long chargesec = paycycleend.getTime()- paycyclestart.getTime()+(1000 * 60 * 60 * 24);
//                chargedays = (int) (chargesec / (1000 * 60 * 60 * 24));
//                chargedays -= StringUtil.nonWorkingDays(paycyclestart,paycycleend,p);
//            }
            if (Integer.parseInt(temp.getStatus().toString()) == 0) {
                requestParams = new HashMap<String, Object>();
                requestParams.put("filter_names",Arrays.asList("template.templateid"));
                requestParams.put("filter_values",Arrays.asList(temp.getTemplateid()));
                result = hrmsPayrollTaxDAOObj.getTaxTemplateDetails(requestParams);
                List iteratorWage = result.getEntityList();
                i = 0;
                Boolean isrecord = false;
                double amtot = 0;
                while (i < iteratorWage.size()) {
                    Templatemaptax wm = (Templatemaptax) iteratorWage.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    isrecord = false;
                    if (wm.getTaxmaster().getRate() == 0) {
                        amtot += Float.parseFloat(wm.getRate())*chargedays/dayinmonth;
                        jsAray1.put("Rate", wm.getTaxmaster().getRate());
                        jsAray1.put("amount", Double.parseDouble(wm.getRate())*chargedays/dayinmonth);
                        jsAray1.put("computeon", "-");
                        jsAray1.put("Id", wm.getTaxmaster().getTaxid());
                        jsAray1.put("Type", wm.getTaxmaster().getTaxtype());
                        jsAray1.put("ratetype", wm.getTaxmaster().getRate());
                        jsAray1.put("depwage", (wm.getTaxmaster().getDepwageid()==null)?"":wm.getTaxmaster().getDepwageid().getWagetype());
                        jsAray1.put("depwageid", (wm.getTaxmaster().getDepwageid()==null)?"":wm.getTaxmaster().getDepwageid().getWageid());
                        jsAray1.put("amtot", amtot);
                        jsAray.append("Tax", jsAray1);
                    } else {
                            masterDB.Taxmaster checkdeduc = wm.getTaxmaster();
                            if(checkdeduc.getComputeon()!=null&&checkdeduc.getComputeon()==3){
                                isrecord = true;
                                Double amount = 0.0;
                                amount =hrmsPayrollWageDAOObj.calculatewagessalary(checkdeduc.getExpr(),tempmap.getMappingid());
                                Double perc = Double.parseDouble(wm.getRate());
                                jsAray1.put("Id", wm.getTaxmaster().getTaxid());
                                jsAray1.put("Rate", wm.getRate());
                                jsAray1.put("amount", (amount*perc/100)*chargedays/dayinmonth);
                                jsAray1.put("computeon", messageSource.getMessage("hrms.payroll.specifiedformula", null, RequestContextUtils.getLocale(request)));
                                amtot += (amount*perc/100)*chargedays/dayinmonth;
                            }
                    }
                    if(isrecord){
                        hrmsPayrollTaxDAOImpl.setDeductotalval(amtot);
                        jsAray1.put("depwage", (wm.getTaxmaster().getDepwageid()==null)?"":wm.getTaxmaster().getDepwageid().getWagetype());
                        jsAray1.put("depwageid", (wm.getTaxmaster().getDepwageid()==null)?"":wm.getTaxmaster().getDepwageid().getWageid());
                        jsAray1.put("WageId", wm.getTaxmaster().getTaxid());
                        jsAray1.put("Id", wm.getTaxmaster().getTaxid());
                        jsAray1.put("Type", wm.getTaxmaster().getTaxtype());
                        jsAray1.put("ratetype", wm.getTaxmaster().getRate());
                        jsAray1.put("amtot", amtot);
                        jsAray.append("Tax", jsAray1);
                    }
                   i++;
                }
            } else if (Integer.parseInt(temp.getStatus().toString()) == 1) {
                requestParams = new HashMap<String, Object>();
                requestParams.put("templateid", TempId);
                result = hrmsPayrollTaxDAOObj.getTempTaxTemplateDetails(requestParams);
                List iteratorWage = result.getEntityList();
                i = 0;
                float amtot = 0;
                while (i < iteratorWage.size()) {
                    TempTemplatemaptax wm = (TempTemplatemaptax) iteratorWage.get(i);
                    double amount = (Float.parseFloat(wm.getRate()) * hrmsPayrollWageDAOImpl.getWagedeductot()) / 100;
                    amtot += (Float.parseFloat(wm.getRate()) * hrmsPayrollWageDAOImpl.getWagedeductot()) / 100;
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("Id", wm.getTaxmaster().getTaxid());
                    jsAray1.put("Rate", wm.getRate());
                    jsAray1.put("Type", wm.getTaxmaster().getTaxtype());
                    jsAray1.put("amount", amount);
                    jsAray1.put("amtot", amtot);
                    jsAray.append("Tax", jsAray1);
                    i++;
                }
            }
            }else{
            	Payhistory payhistory = payhistories.get(0);
            	requestParams.clear();
            	requestParams.put("payHistoryId", payhistory.getHistoryid());
            	requestParams.put("name", "Taxes");
            	List<Historydetail> historydetails = hrmsPayrollTemplateDAOObj.getPayHistorydetail(requestParams);
            	Iterator<Historydetail> iterator = historydetails.iterator();
            	while(iterator.hasNext()){
            		Historydetail historydetail = iterator.next();
            		Taxmaster taxmaster = historydetail.getTaxmaster();
            		JSONObject jsonObject = new JSONObject();
            		if(taxmaster!=null){
                        boolean isrecord = false;
                        if(taxmaster.getRate() == 0){
                        	jsonObject.put("computeon", "-");
                        }else{
                        	if(taxmaster.getComputeon()!=null && taxmaster.getComputeon()==0){
                        		jsonObject.put("computeon", messageSource.getMessage("hrms.payroll.currentdeductions", null, RequestContextUtils.getLocale(request)));
                        	}else{
                        		if(taxmaster.getComputeon()!=null && taxmaster.getComputeon()==1){
                        			jsonObject.put("computeon", messageSource.getMessage("hrms.payroll.currentearnings", null, RequestContextUtils.getLocale(request)));
                        		}else{
                        			if(taxmaster.getComputeon()!=null && taxmaster.getComputeon()==2){
                        				jsonObject.put("computeon", messageSource.getMessage("hrms.payroll.netsalary", null, RequestContextUtils.getLocale(request)));
                        			}else{
                        				isrecord = true;
                        				jsonObject.put("computeon", messageSource.getMessage("hrms.payroll.specifiedformula", null, RequestContextUtils.getLocale(request)));
                        				jsonObject.put("ratetype", taxmaster.getRate());
                        			}
                        		}
                        	}
                        }
                        if(isrecord){
                        	jsonObject.put("depwage", (taxmaster.getDepwageid()==null)?"":taxmaster.getDepwageid().getWagetype());
                        	jsonObject.put("depwageid", (taxmaster.getDepwageid()==null)?"":taxmaster.getDepwageid().getWageid());
                        }
                        jsonObject.put("Id", taxmaster.getTaxid());
            			jsonObject.put("Rate", taxmaster.getRate());
            			jsonObject.put("ratetype", taxmaster.getRate());
            			jsonObject.put("Type", taxmaster.getTaxtype());
            			jsonObject.put("amount", historydetail.getAmount());
            			jsonObject.put("amtot", historydetail.getAmount());
            			jsAray.append("Tax", jsonObject);
            		}else{
            			jsonObject.put("Rate", historydetail.getRate());
            			jsonObject.put("Type", historydetail.getType());
            			jsonObject.put("amount", historydetail.getAmount());
            			jsonObject.put("computeon", "-");
            			jsAray.append("Tax", jsonObject);
            		}
                    i++;
            	}
            }
            if(jsAray.isNull("Tax")){
                jsAray.put("Tax", new com.krawler.utils.json.base.JSONObject());
            }
            jsAray.put("success", true);
            jobj1.put("valid", true);
            jobj1.put("totalcount", i);
            jobj1.put("data", jsAray);
            success = true;
        } catch (Exception e) {
        	e.printStackTrace();
            success = false;
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public double getRecursiveAmountForTaxGrid(Wagemaster wm, ArrayList arr, String salary) {
        double amt = 0;
        double sal = 0;
            sal = Double.parseDouble(salary);
            for(int i=arr.size()-1 ; i>=0 ; i--){
                amt = Double.parseDouble(arr.get(i).toString()) * sal / 100;
                sal = amt;
            }
        return sal;
    }

    public ModelAndView getTaxData(HttpServletRequest request, HttpServletResponse response) {
        boolean success = false;
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {
//            Template templ = (Template) session.get(Template.class, tempid);
//            JSONObject jobj = new JSONObject();

            HashMap<String, Object> requestParams= new HashMap<String, Object>();
            String cid = AuthHandler.getCompanyid(request);
            String TempId = request.getParameter("TempId");
            requestParams.put("templateid", TempId);
            requestParams.put("cid", cid);
            int tc = 0;

            result = hrmsPayrollTemplateDAOObj.getTemplateDetails(requestParams);
            //Template temp = (Template) session.get(Template.class, TempId);
            List templateList = result.getEntityList();
            Template temp = null;
            if(templateList.size()>0)
                temp = (Template) templateList.get(0);

//            requestParams.put();
//            Template templ = (Template) session.get(Template.class, temid);
            JSONObject jobj = new JSONObject();

            if (Integer.parseInt(temp.getStatus().toString()) == 0) {

//                String query = "from Templatemaptax w where w.template.templateid=?";
//                List lst = (List) HibernateUtil.executeQuery(session, query, new Object[]{tempid});
                requestParams.put("filter_names",Arrays.asList("template.templateid"));
                requestParams.put("filter_values",Arrays.asList(TempId));
                result = hrmsPayrollTaxDAOObj.getTaxTemplateDetails(requestParams);
                List lst = result.getEntityList();
                tc=lst.size();
                int i;
                for (i = 0; i < lst.size(); i++) {
                    masterDB.Templatemaptax wage = (masterDB.Templatemaptax) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("assigned", "1");
                    jobjtemp.put("type", wage.getTaxmaster().getTaxtype());
                    jobjtemp.put("cash", wage.getRate());
                    jobjtemp.put("rate", wage.getTaxmaster().getRate());
                    jobjtemp.put("id", wage.getTaxmaster().getTaxid());
                    jobjtemp.put("code", wage.getTaxmaster().getTcode());
                    jobjtemp.put("computeon", wage.getTaxmaster().getComputeon());
                    jobjtemp.put("depwage", (wage.getTaxmaster().getDepwageid()==null)?"":wage.getTaxmaster().getDepwageid().getWagetype());
                    jobjtemp.put("depwageid", (wage.getTaxmaster().getDepwageid()==null)?"":wage.getTaxmaster().getDepwageid().getWageid());
                    jobjtemp.put("expr", wage.getTaxmaster().getExpr());
                    jobj.append("data", jobjtemp);
                }
                if(lst.size()==0){
                  jobj.put("data","");
                }
            } else if (Integer.parseInt(temp.getStatus().toString()) == 1) {
//                Query query = session.createQuery("select t.taxmaster.taxid,t.rate,t.taxmaster.taxtype,t.taxmaster.tcode from TempTemplatemaptax t where t.template.templateid=:tempid and t.taxmaster.companydetails.companyID=:cid");
//                query.setString("tempid", tempid);
//                query.setString("cid", Cid);
//                List lst1 = (List) query.list();
                result = hrmsPayrollTaxDAOObj.getTempTaxTemplateDetails(requestParams);
                List lst1 = result.getEntityList();
                tc=lst1.size();
                int i;
                for (i = 0; i < lst1.size(); i++) {
                    masterDB.TempTemplatemaptax wage = (masterDB.TempTemplatemaptax) lst1.get(i);
//                    Object[] object = (Object[]) lst1.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("assigned", "1");
                    jobjtemp.put("type", wage.getTaxmaster().getTaxtype());
                    jobjtemp.put("cash", wage.getRate());
                    jobjtemp.put("rate", wage.getTaxmaster().getRate());
                    jobjtemp.put("id", wage.getTaxmaster().getTaxid());
                    jobjtemp.put("code", wage.getTaxmaster().getTcode());
                    jobj.append("data", jobjtemp);
                }
                if(lst1.size()==0){
                  jobj.put("data","");
                }
            }
            jobj.put("success", true);
            jobj1.put("valid", true);
            jobj.put("totalcount", tc);
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
    
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
