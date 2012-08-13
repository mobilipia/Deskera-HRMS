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
package com.krawler.spring.hrms.payroll.employercontribution;

import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.userSalaryTemplateMap;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.employee.hrmsEmpDAO;
import com.krawler.spring.hrms.payroll.deduction.hrmsPayrollDeductionDAO;
import com.krawler.spring.hrms.payroll.template.hrmsPayrollTemplateDAO;
import com.krawler.spring.hrms.payroll.wages.hrmsPayrollWageDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONObject;
import java.text.SimpleDateFormat;
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
import masterDB.Template;
import masterDB.TemplateMapEmployerContribution;
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
 * @author krawler
 */
public class hrmsPayrollEmployerContributionController extends MultiActionController implements MessageSourceAware {


    private static String addString = "Add";
    private static String percentString = "Percent";
    private static String amountString = "Amount";

    private String successView;
    private hrmsPayrollEmployerContributionDAO hrmsPayrollEmployerContributionDAOObj;
    private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private hrmsPayrollWageDAO hrmsPayrollWageDAOObj;
    private hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj;
    private hrmsPayrollTemplateDAO hrmsPayrollTemplateDAOObj;
    private hrmsEmpDAO hrmsEmpDAOObj;
    private MessageSource messageSource;

    public void setHrmsEmpDAO(hrmsEmpDAO hrmsEmpDAOObj1) {
        this.hrmsEmpDAOObj = hrmsEmpDAOObj1;
    }

    public void setHrmsPayrollTemplateDAO(hrmsPayrollTemplateDAO hrmsPayrollTemplateDAOObj1) {
        this.hrmsPayrollTemplateDAOObj = hrmsPayrollTemplateDAOObj1;
    }

    public void setHrmsPayrollDeductionDAO(hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj) {
        this.hrmsPayrollDeductionDAOObj = hrmsPayrollDeductionDAOObj;
    }

    public void setHrmsPayrollWageDAO(hrmsPayrollWageDAO hrmsPayrollWageDAOObj1) {
        this.hrmsPayrollWageDAOObj = hrmsPayrollWageDAOObj1;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
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

    public void setHrmsPayrollEmployerContributionDAO(hrmsPayrollEmployerContributionDAO hrmsPayrollEmployerContributionDAOObj) {
        this.hrmsPayrollEmployerContributionDAOObj = hrmsPayrollEmployerContributionDAOObj;
    }

    public ModelAndView setEmployerContributionData(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        String msg = "";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            String option = request.getParameter("optiontype");
            option = StringUtil.isNullOrEmpty(option)?(StringUtil.isNullOrEmpty(request.getParameter("option"))?"":request.getParameter("option")):(request.getParameter("optiontype").equals("%")?"Percent":"Amount");
            String companyId = sessionHandlerImplObj.getCompanyid(request);
            String empContriCode = request.getParameter("code");

            requestParams.put("filter_names", Arrays.asList("companyid.companyID", "empcontricode"));
            requestParams.put("filter_values", Arrays.asList(companyId, empContriCode));

            if (addString.equals(request.getParameter("Action"))) {
                result = hrmsPayrollEmployerContributionDAOObj.getMasterEmployerContrib(requestParams);
                if (result.getRecordTotalCount() > 0) {
                    txnManager.commit(status);
                    return new ModelAndView("jsonView", "model", "{\"success\":\"false\",data:{value:\"exist\"}}");
                }
                requestParams.clear();
            } else {
                requestParams.clear();
                requestParams.put("Id", request.getParameter("typeid"));
            }
            requestParams.put("Companyid", companyId);
            requestParams.put("Empcontricode", empContriCode);
            requestParams.put("Empcontritype", request.getParameter("name"));
            requestParams.put("Cash", Double.parseDouble(request.getParameter("rate")));
            if (percentString.equals(option)) {
                requestParams.put("Rate", 1);
            } else {
                requestParams.put("Rate", 0);
            }
            String isChecked = request.getParameter("isChecked");
            isChecked = StringUtil.isNullOrEmpty(isChecked)?"false":isChecked;

            requestParams.put("Isdefault", Boolean.parseBoolean(isChecked));

            if (!StringUtil.isNullOrEmpty(request.getParameter("computeon")) &&!(amountString.equals(option)) ) {
                requestParams.put("Computeon", Integer.parseInt(request.getParameter("computeon")));
            }
            requestParams.put("Expr", request.getParameter("expr"));
            result = hrmsPayrollEmployerContributionDAOObj.setEmployerContribData(requestParams);

            if (addString.equals(request.getParameter("Action")) && result.isSuccessFlag()) {
                auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_COMPONENT_ADDED, "User " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has added new payroll component " + request.getParameter("name") + " of Employer Contribution type", request, "0");
                msg = "{\"success\":\"true\",data:{value:\"success\",action:\"Added\"}}";
            } else if (result.isSuccessFlag()) {
                auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_COMPONENT_EDITED, "User " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has edited payroll component " + request.getParameter("name"), request, "0");
                msg = "{\"success\":\"true\",data:{value:\"success\",action:\"Edited\"}}";
            }
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn("Exception occurred in hrmsPayrollEmployerContributionController.setEmployerContributionData", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", msg);
    }

    public ModelAndView getEmpContribMaster(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject result = null;
        int count = 0;
        try {
            String cId = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("Cid", cId);
            requestParams.put("start", request.getParameter("start"));
            requestParams.put("limit", request.getParameter("limit"));
            requestParams.put("allflag", request.getParameter("allflag"));
            requestParams.put("ss", request.getParameter("ss"));
            requestParams.put("searchcol", new String[]{"empcontritype"});
            requestParams.put("filter_names", Arrays.asList("companyid.companyID"));
            requestParams.put("filter_values", Arrays.asList(cId));
            requestParams.put("append", "  and (rate = 0 or (rate = 1 and computeon is not null)) ");
            result = hrmsPayrollEmployerContributionDAOObj.getMasterEmployerContrib(requestParams);
            List lst = result.getEntityList();
            for (int i = 0; i < lst.size(); i++) {
                EmployerContribution empContrib = (EmployerContribution) lst.get(i);
                JSONObject jobjTemp = new JSONObject();
                jobjTemp.put("comp", "empcontrib");
                jobjTemp.put("type", empContrib.getEmpcontritype());
                jobjTemp.put("rate", empContrib.getRate());
                jobjTemp.put("cash", empContrib.getCash());
                jobjTemp.put("id", empContrib.getId());
                jobjTemp.put("code", empContrib.getEmpcontricode());
                jobjTemp.put("isdefault", empContrib.getIsdefault());
                jobjTemp.put("computeon", empContrib.getComputeon());
                jobjTemp.put("expr", empContrib.getExpr());
                jobj.append("data", jobjTemp);
                count = result.getRecordTotalCount();
            }
            requestParams.put("filter_names", Arrays.asList("companydetails.companyID"));
            if (request.getParameter("deduction") != null && Boolean.parseBoolean(request.getParameter("deduction"))) {
                result = hrmsPayrollDeductionDAOObj.getDeductionMaster(requestParams);
                lst = result.getEntityList();
                for (int i = 0; i < lst.size(); i++) {
                    Deductionmaster deduc = (Deductionmaster) lst.get(i);
                    JSONObject jobjTemp = new JSONObject();
                    jobjTemp.put("comp", "deduc");
                    jobjTemp.put("type", deduc.getDeductiontype());
                    jobjTemp.put("rate", deduc.getRate());
                    jobjTemp.put("cash", deduc.getCash());
                    jobjTemp.put("id", deduc.getDeductionid());
                    jobjTemp.put("code", deduc.getDcode());
                    jobjTemp.put("isdefault", deduc.isIsdefault());
                    jobjTemp.put("computeon", deduc.getComputeon());
                    jobjTemp.put("expr", deduc.getExpr());
                    jobj.append("data", jobjTemp);
                    count = result.getRecordTotalCount();
                }
            }
            if (request.getParameter("wage") != null && Boolean.parseBoolean(request.getParameter("wage"))) {
                result = hrmsPayrollWageDAOObj.getWageMaster(requestParams);
                lst = result.getEntityList();
                for (int i = 0; i < lst.size(); i++) {
                    Wagemaster wage = (Wagemaster) lst.get(i);
                    JSONObject jobjTemp = new JSONObject();
                    jobjTemp.put("comp", "wage");
                    jobjTemp.put("type", wage.getWagetype());
                    jobjTemp.put("rate", wage.getRate());
                    jobjTemp.put("cash", wage.getCash());
                    jobjTemp.put("id", wage.getWageid());
                    jobjTemp.put("code", wage.getWcode());
                    jobjTemp.put("isdefault", wage.isIsdefault());
                    jobjTemp.put("computeon", wage.getComputeon());
                    jobjTemp.put("expr", wage.getExpr());
                    jobj.append("data", jobjTemp);
                }
            }
            if (lst.size() == 0) {
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
            logger.warn("Exception occurred in hrmsPayrollEmployerContributionController.getEmpContribMaster", e);
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }

    public ModelAndView getDefaultEmpContrib(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        boolean success = false;
        KwlReturnObject result = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String cid = sessionHandlerImplObj.getCompanyid(request);
            requestParams.put("filter_names", Arrays.asList("companyid.companyID", "isdefault"));
            requestParams.put("filter_values", Arrays.asList(cid, true));
            requestParams.put("append", "and (rate = 0 or (rate = 1 and computeon is not null))");
            result = hrmsPayrollEmployerContributionDAOObj.getMasterEmployerContrib(requestParams);
            List<masterDB.EmployerContribution> lst2 = result.getEntityList();
            for (int i = 0; i < lst2.size(); i++) {
                masterDB.EmployerContribution ecobj = (masterDB.EmployerContribution) lst2.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("id", ecobj.getId());
                jobjtemp.put("type", ecobj.getEmpcontritype());
                jobjtemp.put("cash", ecobj.getCash());
                jobjtemp.put("computeon", ecobj.getComputeon());
                jobjtemp.put("rate", ecobj.getRate());
                jobjtemp.put("expr", ecobj.getExpr());
                jobjtemp.put("code", ecobj.getEmpcontricode());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);

            jobj1.put("valid", true);
            jobj1.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
        } catch (Exception e) {
            logger.warn("Exception occurred in hrmsPayrollEmployerContributionController.getDefaultEmpContrib", e);
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }

    public ModelAndView getEmployerContribData(HttpServletRequest request, HttpServletResponse response) {

        boolean success = false;
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String TempId = request.getParameter("TempId");
            JSONObject jobj = new JSONObject();


            requestParams.put("filter_names", Arrays.asList("template.templateid"));
            requestParams.put("filter_values", Arrays.asList(TempId));
            result = hrmsPayrollEmployerContributionDAOObj.getEmployerContribTemplateDetails(requestParams);
            List lst = result.getEntityList();
            int i;
            int tc = lst.size();

            for (i = 0; i < tc; i++) {
                TemplateMapEmployerContribution tmec = (TemplateMapEmployerContribution) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("assigned", "1");
                jobjtemp.put("type", tmec.getEmpcontrimaster().getEmpcontritype());
                jobjtemp.put("cash", tmec.getRate());
                jobjtemp.put("rate", tmec.getEmpcontrimaster().getRate());
                jobjtemp.put("id", tmec.getEmpcontrimaster().getId());
                jobjtemp.put("code", tmec.getEmpcontrimaster().getEmpcontricode());
                jobjtemp.put("expr", (tmec.getEmpcontrimaster().getExpr() == null) ? "" : tmec.getEmpcontrimaster().getExpr());
                jobjtemp.put("computeon", (tmec.getEmpcontrimaster().getComputeon() == null) ? "" : tmec.getEmpcontrimaster().getComputeon());
                jobj.append("data", jobjtemp);
            }
            if (tc == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);

            jobj1.put("valid", true);
            jobj1.put("totalcount", tc);
            jobj1.put("data", jobj);
            success = true;

        } catch (Exception se) {
            logger.warn("Exception occurred in hrmsPayrollEmployerContributionController.getEmployerContribData", se);

            success = false;
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
    }

    public ModelAndView getEmpContribPerTempid(HttpServletRequest request, HttpServletResponse response) {
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
            int tc = 0;
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
            requestParams.clear();
            requestParams.put("filter_names", Arrays.asList("userAccount.userID", "<=effectiveDate", "mappingid"));
            requestParams.put("filter_values", Arrays.asList(empid, paycyclestart, mappingid));
            requestParams.put("order_by", Arrays.asList("effectiveDate"));
            requestParams.put("order_type", Arrays.asList("desc"));
            result = hrmsPayrollTemplateDAOObj.getUserSalaryTemplateMap(requestParams);

            userSalaryTemplateMap tempmap = null;
            if (StringUtil.checkResultobjList(result)) {
                tempmap = (userSalaryTemplateMap) result.getEntityList().get(0);
            }
            Template temp = null;
            if (tempmap != null) {
                temp = tempmap.getSalaryTemplate();
            }
            int dayinmonth = 1;
            int chargedays = 1;
            if (ischanged.equals("1")) {
                int holiday = cp.getWeeklyholidays();
                Date cyclestartdate = sdf.parse(paycyclestartdate);
                Date cycleenddate = sdf.parse(paycycleenddate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(paycyclestart);
                

                HashMap<Integer, Integer> p = new HashMap<Integer, Integer>();
                p = StringUtil.getHolidayArr(holiday);
                if (temp.getPayinterval() == 1) {
                    dayinmonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    dayinmonth -= StringUtil.nonWorkingDays(cyclestartdate, cycleenddate, p);
                } else if (temp.getPayinterval() == 3) {
                    dayinmonth = 7;
                    dayinmonth -= StringUtil.nonWorkingDays(cyclestartdate, cycleenddate, p);
                } else if (temp.getPayinterval() == 2) {//Bi-monthly
                	Calendar c1 = Calendar.getInstance();
                    c1.setTime(cyclestartdate);
                    if(c1.get(Calendar.DATE)<=15){
                		dayinmonth = 15;
                	}else{
                		dayinmonth = c1.getActualMaximum(Calendar.DAY_OF_MONTH)-15;
                	}
                    paycycleend = cycleenddate;
                }
                long chargesec = paycycleend.getTime() - paycyclestart.getTime() + (1000 * 60 * 60 * 24);
                chargedays = (int) (chargesec / (1000 * 60 * 60 * 24));
                chargedays -= StringUtil.nonWorkingDays(paycyclestart, paycycleend, p);
            }


            requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", Arrays.asList("template.templateid"));
            requestParams.put("filter_values", Arrays.asList(temp.getTemplateid()));
            result = hrmsPayrollEmployerContributionDAOObj.getEmployerContribTemplateDetails(requestParams);
            List iteratorEC = result.getEntityList();
            int i = 0;
            Double amtot = 0.0;
            Boolean isrecord = false;
            
            while (i < iteratorEC.size()) {
                isrecord = false;
                TemplateMapEmployerContribution ec = (TemplateMapEmployerContribution) iteratorEC.get(i);

                double casht = ec.getEmpcontrimaster().getRate() == 0 ? ec.getEmpcontrimaster().getCash() : 0;
                JSONObject jsAray1 = new JSONObject();
                if (ec.getEmpcontrimaster().getRate() == 0) {
                    isrecord = true;
                    jsAray1.put("Rate", ec.getEmpcontrimaster().getRate());
                    jsAray1.put("amount", Double.parseDouble(ec.getRate()) * chargedays / dayinmonth);
                    jsAray1.put("computeon", "-");
                    amtot += Float.parseFloat(ec.getRate()) * chargedays / dayinmonth;
                } else {
                    EmployerContribution checkec = ec.getEmpcontrimaster();
                    if (checkec.getComputeon() != null && checkec.getComputeon() == 3) {
                        isrecord = true;
                        Double amount = 0.0;
                        amount = hrmsPayrollWageDAOObj.calculatewagessalary(checkec.getExpr(), tempmap.getMappingid());
                        Double perc = Double.parseDouble(ec.getRate());
                        jsAray1.put("Rate", ec.getRate());
                        jsAray1.put("amount", (amount * perc / 100) * chargedays / dayinmonth);
                        jsAray1.put("computeon", messageSource.getMessage("hrms.payroll.specifiedformula", null, RequestContextUtils.getLocale(request)));
                        amtot += (amount * perc / 100) * chargedays / dayinmonth;
                    }
                }
                if (isrecord) {
                    jsAray1.put("Id", ec.getEmpcontrimaster().getId());
                    jsAray1.put("Type", ec.getEmpcontrimaster().getEmpcontritype());
                    jsAray1.put("ratetype", ec.getEmpcontrimaster().getRate());
                    jsAray1.put("amtot", amtot);
                    jsAray.append("EC", jsAray1);
                }
                i++;
            }
            tc = i - 1;
            }else{
            	Payhistory payhistory = payhistories.get(0);
            	requestParams.clear();
            	requestParams.put("payHistoryId", payhistory.getHistoryid());
            	requestParams.put("name", "Employer Contribution");
            	List<Historydetail> historydetails = hrmsPayrollTemplateDAOObj.getPayHistorydetail(requestParams);
            	Iterator<Historydetail> iterator = historydetails.iterator();
            	while(iterator.hasNext()){
            		Historydetail historydetail = iterator.next();
            		EmployerContribution employerContribution = historydetail.getEmployercontributionmaster();
            		JSONObject jsonObject = new JSONObject();
            		if(employerContribution!=null){
                        if(employerContribution.getRate() == 0){
                        	jsonObject.put("computeon", "-");
                        }else{
                        	if(employerContribution.getComputeon()!=null && employerContribution.getComputeon()==0){
                        		jsonObject.put("computeon", messageSource.getMessage("hrms.payroll.currentdeductions", null, RequestContextUtils.getLocale(request)));
                        	}else{
                        		if(employerContribution.getComputeon()!=null && employerContribution.getComputeon()==1){
                        			jsonObject.put("computeon", messageSource.getMessage("hrms.payroll.currentearnings", null, RequestContextUtils.getLocale(request)));
                        		}else{
                        			if(employerContribution.getComputeon()!=null && employerContribution.getComputeon()==2){
                        				jsonObject.put("computeon", messageSource.getMessage("hrms.payroll.netsalary", null, RequestContextUtils.getLocale(request)));
                        			}else{
                        				jsonObject.put("computeon", messageSource.getMessage("hrms.payroll.specifiedformula", null, RequestContextUtils.getLocale(request)));
                        				jsonObject.put("ratetype", employerContribution.getRate());
                        			}
                        		}
                        	}
                        }
                        jsonObject.put("Id", employerContribution.getId());
            			jsonObject.put("Rate", employerContribution.getRate());
            			jsonObject.put("ratetype", employerContribution.getRate());
            			jsonObject.put("Type", employerContribution.getEmpcontritype());
            			jsonObject.put("amount", historydetail.getAmount());
            			jsonObject.put("amtot", historydetail.getAmount());
            			jsAray.append("EC", jsonObject);
            		}else{
            			jsonObject.put("Rate", historydetail.getRate());
            			jsonObject.put("Type", historydetail.getType());
            			jsonObject.put("amount", historydetail.getAmount());
            			jsonObject.put("computeon", "-");
            			jsAray.append("EC", jsonObject);
            		}
                    tc++;
            	}
            }

            if (jsAray.isNull("EC")) {
                jsAray.put("EC", new JSONObject());
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
            logger.warn("Exception occurred in hrmsPayrollEmployerContributionController.getEmpContribPerTempid", se);
        } finally {
            return new ModelAndView("jsonView", "model", jobj1.toString());
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
       
    public ModelAndView deleteMasterEmpContrib(HttpServletRequest request, HttpServletResponse response) {

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

            result = hrmsPayrollEmployerContributionDAOObj.deleteMasterEmployerContrib(requestParams);
            if (result.getEntityList() != null) {
                String logText = (String) result.getEntityList().get(0);
                auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_COMPONENT_DELETED, "User " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has deleted payroll component " + logText + " of type Employer Contribution.", request, "0");
            }
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn("Exception occurred in hrmsPayrollEmployerContributionController.deleteMasterEmpContrib", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result.getMsg());
    }

     public ModelAndView getHistEmpContrib(HttpServletRequest request , HttpServletResponse response) {

         KwlReturnObject kRetObj = null;
         JSONObject jobj1 = new JSONObject();
         try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            List lst = null;

            requestParams.put("filter_names", Arrays.asList("payhistory.historyid","name"));
            requestParams.put("filter_values", Arrays.asList(request.getParameter("histid"),"Employer Contribution"));

            kRetObj = hrmsEmpDAOObj.getHistoryDetail(requestParams);
            JSONObject jobj = new JSONObject();
            if(!kRetObj.isSuccessFlag()) {
                jobj.append("EC", new JSONObject());
            } else {
                lst = kRetObj.getEntityList();
                for (int i = 0; i < lst.size(); i++) {
                    masterDB.Historydetail wage = (masterDB.Historydetail) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("Rate", wage.getRate());
                    jobjtemp.put("Type", wage.getType());
                    jobjtemp.put("amount", wage.getAmount());
                    jobj.append("EC", jobjtemp);
                }
            }
            jobj.put("success", true);

            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
        } catch (Exception se) {
            logger.warn("Exception in hrmsPayrollEmployerContributionController.getHistEmpContrib", se);
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
     
     @Override
     public void setMessageSource(MessageSource ms) {
         this.messageSource = ms;
     }
}
