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
package com.krawler.spring.hrms.payroll.template;

import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.userSalaryTemplateMap;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;

import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.hrms.employee.hrmsEmpDAO;
import com.krawler.spring.hrms.payroll.deduction.hrmsPayrollDeductionDAO;
import com.krawler.spring.hrms.payroll.employercontribution.hrmsPayrollEmployerContributionDAO;
import com.krawler.spring.hrms.payroll.tax.hrmsPayrollTaxDAO;
import com.krawler.spring.hrms.payroll.wages.hrmsPayrollWageDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import masterDB.Payhistory;
import masterDB.PayrollHistory;
import masterDB.Template;

import org.apache.poi.hssf.record.formula.functions.Count;
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
public class hrmsPayrollTemplateController extends MultiActionController implements MessageSourceAware {

    private String successView;
    private hrmsPayrollTemplateDAO hrmsPayrollTemplateDAOObj;
    private hrmsPayrollWageDAO hrmsPayrollWageDAOObj;
    private hrmsPayrollTaxDAO hrmsPayrollTaxDAOObj;
    private hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj;
    private hrmsPayrollEmployerContributionDAO hrmsPayrollEmployerContributionDAOObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private hrmsEmpDAO hrmsEmpDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private MessageSource messageSource;

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

    public void sethrmsEmpDAO(hrmsEmpDAO hrmsEmpDAOObj) {
        this.hrmsEmpDAOObj = hrmsEmpDAOObj;
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

    public void setHrmsPayrollTemplateDAO(hrmsPayrollTemplateDAO hrmsPayrollTemplateDAOObj1) {
        this.hrmsPayrollTemplateDAOObj = hrmsPayrollTemplateDAOObj1;
    }

    public void setHrmsPayrollWageDAO(hrmsPayrollWageDAO hrmsPayrollWageDAOObj1) {
        this.hrmsPayrollWageDAOObj = hrmsPayrollWageDAOObj1;
    }

    public void setHrmsPayrollTaxDAO(hrmsPayrollTaxDAO hrmsPayrollTaxDAOObj1) {
        this.hrmsPayrollTaxDAOObj = hrmsPayrollTaxDAOObj1;
    }

    public void setHrmsPayrollEmployerContributionDAO(hrmsPayrollEmployerContributionDAO hrmsPayrollEmployerContributionDAOObj) {
        this.hrmsPayrollEmployerContributionDAOObj = hrmsPayrollEmployerContributionDAOObj;
    }

    public void setHrmsPayrollDeductionDAO(hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj1) {
        this.hrmsPayrollDeductionDAOObj = hrmsPayrollDeductionDAOObj1;
    }

    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }


    public JSONObject getRecursivePayCycle(HashMap<String, Object> requestParams, String userid, int count, userSalaryTemplateMap tempmap1, JSONObject jobj) {
        KwlReturnObject result = null;
        JSONObject transferjobj = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        try {
        	DateFormat companyDateFormat = requestParams.get("companyDateFormat")!=null?((DateFormat) requestParams.get("companyDateFormat")):sdf;
        	Calendar financialDate = (Calendar) requestParams.get("financialDate");
            Calendar cal = Calendar.getInstance();
            Calendar tempcal = Calendar.getInstance();
            Calendar tempcal2 = Calendar.getInstance();
            result = hrmsPayrollTemplateDAOObj.getUserSalaryTemplateMap(requestParams);
            if(StringUtil.checkResultobjList(result)){
                List lst = result.getEntityList();
                userSalaryTemplateMap tempmap = (userSalaryTemplateMap)lst.get(0);
                int day = 1;
                ArrayList tempArray = new ArrayList((List<Object>)requestParams.get("filter_values"));
                if(tempmap.getSalaryTemplate().getPayinterval()==1){//if paycycle is monthly
                    
                    cal.setTime(sdf.parse(sdf.format(tempArray.get(1))));
                    cal.add(Calendar.DATE, -cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    Date startdate = cal.getTime();
                    if(tempmap.getEffectiveDate()!=null)
                        day = tempmap.getEffectiveDate().getDate();
                    Date paysdate = null;
                    if(startdate.compareTo(tempmap.getEffectiveDate())>0){
                    	if(day>28){
                    		if(cal.getActualMaximum(Calendar.DAY_OF_MONTH)>day)
                        		cal.set(Calendar.DATE, day);
                        	else
                        		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    	}else
                    		cal.set(Calendar.DATE, day);
                        paysdate = cal.getTime();
                    }else{
                        paysdate = tempmap.getEffectiveDate();
                    }

                    tempcal2.setTime(sdf.parse(sdf.format(tempArray.get(1))));
                    tempcal2.set(Calendar.DATE, tempcal2.get(Calendar.DATE)+1);

                    tempcal.setTime(paysdate);
                    tempcal.set(Calendar.DATE, tempcal.get(Calendar.DATE)-1);
                    Date storeDate = tempcal.getTime();
                    while(paysdate.compareTo(sdf.parse(sdf.format(tempArray.get(1))))<0 && financialDate.getTime().compareTo(paysdate)<0){
                        JSONObject jtemp = new JSONObject();
                        jtemp.put("paycyclestart", sdf.format(paysdate));
                        jtemp.put("paycycleshowstart", companyDateFormat.format(paysdate));
                        cal.setTime(paysdate);
                        
                        if(tempmap.getSalaryTemplate().getEffdate()>28){
                        	Calendar calTemp = (Calendar) cal.clone();
                        	calTemp.add(calTemp.DATE, 5);
                        	if(calTemp.getActualMaximum(Calendar.DAY_OF_MONTH)>day){
                        		cal.add(Calendar.DATE, day);
                        	}else{
                        		if(day!=31)
                        			cal.add(Calendar.DATE, calTemp.getActualMaximum(Calendar.DAY_OF_MONTH)+1);
                        		else
                        			cal.add(Calendar.DATE, calTemp.getActualMaximum(Calendar.DAY_OF_MONTH));
                        	}
                        }
                        else{
                        	cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+1);
                        }
                        paysdate = cal.getTime();
                        cal.set(Calendar.DATE, cal.get(Calendar.DATE)-1);
                        if(cal.after(tempcal2) || cal.equals(tempcal2)){
                            tempcal2.set(Calendar.DATE, tempcal2.get(Calendar.DATE)-1);
                            jtemp.put("paycycleactualend", sdf.format(tempcal2.getTime()));
                            jtemp.put("paycycleshowend", companyDateFormat.format(tempcal2.getTime()));
                        } else {
                            jtemp.put("paycycleactualend", sdf.format(cal.getTime()));
                            jtemp.put("paycycleshowend", companyDateFormat.format(cal.getTime()));
                        }
                        jtemp.put("paycycleend", sdf.format(cal.getTime()));
                        jtemp.put("TempID", tempmap.getSalaryTemplate().getTemplateid());
                        jtemp.put("paycycletemplate", tempmap.getSalaryTemplate().getTemplatename());
                        transferjobj.append("data",jtemp);
                        
                    }
                    if(transferjobj.has("data")){
                        com.krawler.utils.json.base.JSONArray tmpArr = transferjobj.getJSONArray("data");
                        for(int h = tmpArr.length()-1 ; h >= 0 ; h--){
                            jobj.append("data",tmpArr.get(h));
                            count--;
                            if(count <= 0){
                                break;
                            }
                        }
                    }
                    if(count > 0 && financialDate.getTime().compareTo(paysdate)<0){
                        requestParams.clear();
                        requestParams.put("filter_names", Arrays.asList("userAccount.userID","<=effectiveDate"));
                        requestParams.put("filter_values", Arrays.asList(userid, storeDate));
                        requestParams.put("order_by", Arrays.asList("effectiveDate"));
                        requestParams.put("order_type", Arrays.asList("desc"));
                        requestParams.put("companyDateFormat", companyDateFormat);
                        requestParams.put("financialDate", financialDate);
                        jobj = getRecursivePayCycle(requestParams, userid, count, tempmap, jobj);
                    }

                }else if(tempmap.getSalaryTemplate().getPayinterval()==3){//if paycycle is weekly
                    cal.setTime(sdf.parse(sdf.format(tempArray.get(1))));
                    cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
                    Date startdate = cal.getTime();
                    if(tempmap.getSalaryTemplate().getEffdate()!=null)
                        day = tempmap.getSalaryTemplate().getEffdate();
                    Date paysdate = null;
                    if(startdate.compareTo(tempmap.getEffectiveDate())>0){
                        int mdate = cal.get(cal.DATE);
                        int mday = cal.get(cal.DAY_OF_WEEK);
                        int datediff = day-mdate;
                        cal.set(Calendar.DATE, mdate+datediff);
                        paysdate = cal.getTime();
                    }else{
                        paysdate = tempmap.getEffectiveDate();
                    }

                    tempcal2.setTime(sdf.parse(sdf.format(tempArray.get(1))));
                    tempcal2.set(Calendar.DATE, tempcal2.get(Calendar.DATE)+1);

                    tempcal.setTime(paysdate);
                    tempcal.set(Calendar.DATE, tempcal.get(Calendar.DATE)-1);
                    Date storeDate = tempcal.getTime();
                    
                    while(paysdate.compareTo(sdf.parse(sdf.format(tempArray.get(1))))<=0 && financialDate.getTime().compareTo(paysdate)<0){

                        JSONObject jtemp = new JSONObject();
                        jtemp.put("paycyclestart", sdf.format(paysdate));
                        jtemp.put("paycycleshowstart", companyDateFormat.format(paysdate));
                        cal.setTime(paysdate);
                        cal.set(Calendar.DATE, cal.get(Calendar.DATE)+7);
                        paysdate = cal.getTime();
                        cal.set(Calendar.DATE, cal.get(Calendar.DATE)-1);
                        if(cal.after(tempcal2) || cal.equals(tempcal2)){
                            tempcal2.set(Calendar.DATE, tempcal2.get(Calendar.DATE)-1);
                            jtemp.put("paycycleactualend", sdf.format(tempcal2.getTime()));
                            jtemp.put("paycycleshowend", companyDateFormat.format(tempcal2.getTime()));
                        } else {
                            jtemp.put("paycycleactualend", sdf.format(cal.getTime()));
                            jtemp.put("paycycleshowend", companyDateFormat.format(cal.getTime()));
                        }
                        jtemp.put("paycycleend", sdf.format(cal.getTime()));
                        jtemp.put("TempID", tempmap.getSalaryTemplate().getTemplateid());
                        jtemp.put("paycycletemplate", tempmap.getSalaryTemplate().getTemplatename());
                        transferjobj.append("data",jtemp);
                    //    count--;
                        
                    }
                    if(transferjobj.has("data")){
                        com.krawler.utils.json.base.JSONArray tmpArr = transferjobj.getJSONArray("data");
                        for(int h = tmpArr.length()-1 ; h >= 0 ; h--){
                            jobj.append("data",tmpArr.get(h));
                            count--;
                            if(count <= 0){
                                break;
                            }
                        }
                    }
                    if(count > 0 && financialDate.getTime().compareTo(paysdate)<0){
                        requestParams.clear();
                        requestParams.put("filter_names", Arrays.asList("userAccount.userID","<=effectiveDate"));
                        requestParams.put("filter_values", Arrays.asList(userid, storeDate));
                        requestParams.put("order_by", Arrays.asList("effectiveDate"));
                        requestParams.put("order_type", Arrays.asList("desc"));
                        requestParams.put("companyDateFormat", companyDateFormat);
                        requestParams.put("financialDate", financialDate);
                        jobj = getRecursivePayCycle(requestParams, userid, count, tempmap, jobj);
                    }
                }else if(tempmap.getSalaryTemplate().getPayinterval()==2){//if paycycle is Bi-monthly
                    cal.setTime(sdf.parse(sdf.format(tempArray.get(1))));
                    cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)-1);
                    Date startdate = cal.getTime();
                    if(tempmap.getSalaryTemplate().getEffdate()!=null)
                        day = tempmap.getSalaryTemplate().getEffdate();
                    Date paysdate = null;
                    if(startdate.compareTo(tempmap.getEffectiveDate())>0){
                        cal.set(Calendar.DATE, day);
                        paysdate = cal.getTime();
                    }else{
                        if(tempmap.getEffectiveDate().getDate() > 16){
                            Calendar tempcalendar = Calendar.getInstance();
                            tempcalendar.setTime(tempmap.getEffectiveDate());
                            tempcalendar.set(Calendar.DATE, 1);
                            tempcalendar.set(Calendar.MONTH, tempcalendar.get(Calendar.MONTH)+1);
                            paysdate = tempcalendar.getTime();
                        } else {
                            Calendar tempcalendar = Calendar.getInstance();
                            tempcalendar.setTime(tempmap.getEffectiveDate());
                            tempcalendar.set(Calendar.DATE, 16);
                            paysdate = tempcalendar.getTime();
                        }
                    }

                    tempcal2.setTime(sdf.parse(sdf.format(tempArray.get(1))));
                    tempcal2.set(Calendar.DATE, tempcal2.get(Calendar.DATE)+1);

                    tempcal.setTime(paysdate);
                    tempcal.set(Calendar.DATE, tempcal.get(Calendar.DATE)-1);
                    Date storeDate = tempcal.getTime();

                    while(paysdate.compareTo(sdf.parse(sdf.format(tempArray.get(1))))<=0 && financialDate.getTime().compareTo(paysdate)<0){

                        JSONObject jtemp = new JSONObject();
                        jtemp.put("paycyclestart", sdf.format(paysdate));
                        jtemp.put("paycycleshowstart", companyDateFormat.format(paysdate));
                        cal.setTime(paysdate);
                        if(cal.get(Calendar.DATE) == 16){
                            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH)+1);
                        } else {
                            cal.set(Calendar.DATE, cal.get(Calendar.DATE)+15);
                        }
                        //cal.set(Calendar.DATE, cal.get(Calendar.DATE)+7);
                        paysdate = cal.getTime();
                        cal.set(Calendar.DATE, cal.get(Calendar.DATE)-1);
                        if(cal.after(tempcal2) || cal.equals(tempcal2)){
                            tempcal2.set(Calendar.DATE, tempcal2.get(Calendar.DATE)-1);
                            jtemp.put("paycycleactualend", sdf.format(tempcal2.getTime()));
                            jtemp.put("paycycleshowend", companyDateFormat.format(tempcal2.getTime()));
                        } else {
                            jtemp.put("paycycleactualend", sdf.format(cal.getTime()));
                            jtemp.put("paycycleshowend", companyDateFormat.format(cal.getTime()));
                        }
                        jtemp.put("paycycleend", sdf.format(cal.getTime()));
                        jtemp.put("TempID", tempmap.getSalaryTemplate().getTemplateid());
                        jtemp.put("paycycletemplate", tempmap.getSalaryTemplate().getTemplatename());
                        transferjobj.append("data",jtemp);
                    //    count--;

                    }
                    if(transferjobj.has("data")){
                        com.krawler.utils.json.base.JSONArray tmpArr = transferjobj.getJSONArray("data");
                        for(int h = tmpArr.length()-1 ; h >= 0 ; h--){
                            jobj.append("data",tmpArr.get(h));
                            count--;
                            if(count <= 0){
                                break;
                            }
                        }
                    }
                    if(count > 0 && financialDate.getTime().compareTo(paysdate)<0){
                        requestParams.clear();
                        requestParams.put("filter_names", Arrays.asList("userAccount.userID","<=effectiveDate"));
                        requestParams.put("filter_values", Arrays.asList(userid, storeDate));
                        requestParams.put("order_by", Arrays.asList("effectiveDate"));
                        requestParams.put("order_type", Arrays.asList("desc"));
                        requestParams.put("companyDateFormat", companyDateFormat);
                        requestParams.put("financialDate", financialDate);
                        jobj = getRecursivePayCycle(requestParams, userid, count, tempmap, jobj);
                    }
                }

            }
            return jobj;
        } catch (Exception e) {
            return (null);
        }
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
	public ModelAndView getuserpaycycle(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject transferjobj = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        JSONArray tmpArr = null;
        try {
        	DateFormat df = new SimpleDateFormat(hrmsCommonDAOObj.getUserDateFormat(sessionHandlerImplObj.getUserid(request)));
        	//sdf.setTimeZone(TimeZone.getTimeZone("GMT" + sessionHandlerImplObj.getTimeZoneDifference(request)));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            CompanyPreferences companyPreferences = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", sessionHandlerImplObj.getCompanyid(request));
            Calendar financialDate = Calendar.getInstance();
            //TimeZone timeZone = TimeZone.getTimeZone(kwlCommonTablesDAOObj.getTimeZone(request, sessionHandlerImplObj.getUserid(request)));
            //df.setTimeZone(timeZone);
            //sdf.setTimeZone(timeZone);
            //financialDate.setTimeZone(timeZone);
//            financialDate.set(Calendar.HOUR_OF_DAY, 0);
//            financialDate.set(Calendar.MINUTE, 0);
//            financialDate.set(Calendar.SECOND, 0);
//            financialDate.set(Calendar.MILLISECOND, 0);
            int currentMonth = financialDate.get(Calendar.MONTH);
            financialDate.set(Calendar.DATE, 1);
            financialDate.set(Calendar.MONTH, companyPreferences.getFinancialmonth());
            if(financialDate.get(Calendar.MONTH)>currentMonth){
            	financialDate.add(Calendar.YEAR, -1);
            }
            int effectiveDate = 0;
            boolean isTemplateDriven = request.getParameter("isTemplateDriven")!=null?Boolean.parseBoolean(request.getParameter("isTemplateDriven")):false;
            if(isTemplateDriven){
            	Calendar cal = Calendar.getInstance();
            	//cal.setTimeZone(timeZone);
            	requestParams.put("filter_names", Arrays.asList("salaryTemplate.templateid","<=effectiveDate"));
                requestParams.put("filter_values", Arrays.asList(request.getParameter("templateid"),new Date()));
                requestParams.put("order_by", Arrays.asList("effectiveDate"));
                requestParams.put("order_type", Arrays.asList("desc"));
                result = hrmsPayrollTemplateDAOObj.getUserSalaryTemplateMap(requestParams);
                if(StringUtil.checkResultobjList(result)){
                    List<userSalaryTemplateMap> lst = result.getEntityList();
                    userSalaryTemplateMap tempmap = (userSalaryTemplateMap)lst.get(0);
                    if(tempmap.getSalaryTemplate().getPayinterval()==1){//if paycycle is monthly
                        Date paysdate = null;
                        if(tempmap.getSalaryTemplate()!=null){
            				effectiveDate = tempmap.getSalaryTemplate().getEffdate();
            			}
                        if(effectiveDate>financialDate.getActualMaximum(Calendar.DAY_OF_MONTH)){
            				financialDate.set(Calendar.DATE, financialDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            			}else{
            				financialDate.set(Calendar.DATE, effectiveDate);
            			}
            			paysdate = financialDate.getTime();
                		int x=0;
                		financialDate.setTime(paysdate);
                		while(financialDate.getTime().before(cal.getTime())){
            				JSONObject jtemp = new JSONObject();
            				financialDate.setTime(paysdate);
            				financialDate.roll(Calendar.MONTH, x);
            				if(financialDate.get(Calendar.MONTH)==11){
                            	paysdate.setYear(paysdate.getYear()+1);
            				}
            				if(effectiveDate>28){
            					if(effectiveDate>=financialDate.getActualMaximum(Calendar.DAY_OF_MONTH))
            						financialDate.set(Calendar.DATE, financialDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            					else
            						financialDate.set(Calendar.DATE, effectiveDate);
            				}
                            jtemp.put("paycyclestart", sdf.format(financialDate.getTime()));
                            jtemp.put("paycycleshowstart", df.format(financialDate.getTime()));
                            financialDate.setTime(paysdate);
                            financialDate.roll(Calendar.MONTH, ++x);
                            if(effectiveDate>28){
            					if(effectiveDate>=financialDate.getActualMaximum(Calendar.DAY_OF_MONTH))
            						financialDate.set(Calendar.DATE, financialDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            					else
            						financialDate.set(Calendar.DATE, effectiveDate);
            				}
                            financialDate.add(Calendar.DATE, -1);
                            jtemp.put("paycycleend", sdf.format(financialDate.getTime()));
                            jtemp.put("paycycleactualend", sdf.format(financialDate.getTime()));
                            jtemp.put("paycycleshowend", df.format(financialDate.getTime()));
                            jtemp.put("TempID", tempmap.getSalaryTemplate().getTemplateid());
                            jtemp.put("paycycletemplate", tempmap.getSalaryTemplate().getTemplatename());
                            transferjobj.append("data",jtemp);
            			}
                        if(transferjobj.has("data")){
                            tmpArr = transferjobj.getJSONArray("data");
                            for(int h = tmpArr.length()-1 ; h >= 0 ; h--){
                                jobj.append("data",tmpArr.get(h));
                            }
                        }
                    }else if(tempmap.getSalaryTemplate().getPayinterval()==3){//if paycycle is weekly
                        if(tempmap.getSalaryTemplate().getEffdate()!=null){
                        	financialDate.set(financialDate.DAY_OF_WEEK, tempmap.getSalaryTemplate().getEffdate());
                        }
                        Date paysdate = financialDate.getTime();
                        while(paysdate.compareTo(cal.getTime())<0){
                            JSONObject jtemp = new JSONObject();
                            jtemp.put("paycyclestart", sdf.format(paysdate));
                            jtemp.put("paycycleshowstart", df.format(paysdate));
                            financialDate.setTime(paysdate);
                            financialDate.set(Calendar.DATE, financialDate.get(Calendar.DATE)+7);
                            paysdate = financialDate.getTime();
                            financialDate.set(Calendar.DATE, financialDate.get(Calendar.DATE)-1);
                            jtemp.put("paycycleend", sdf.format(financialDate.getTime()));
                            jtemp.put("paycycleactualend", sdf.format(financialDate.getTime()));
                            jtemp.put("paycycleshowend", df.format(financialDate.getTime()));
                            jtemp.put("TempID", tempmap.getSalaryTemplate().getTemplateid());
                            jtemp.put("paycycletemplate", tempmap.getSalaryTemplate().getTemplatename());
                            transferjobj.append("data",jtemp);
                        }
                        if(transferjobj.has("data")){
                            tmpArr = transferjobj.getJSONArray("data");
                            for(int h = tmpArr.length()-1 ; h >= 0 ; h--){
                                jobj.append("data",tmpArr.get(h));
                            }
                        }
                    }else if(tempmap.getSalaryTemplate().getPayinterval()==2){//if paycycle is bi-monthly
                        if(tempmap.getSalaryTemplate().getEffdate()!=null)
                        	financialDate.set(Calendar.DATE, tempmap.getSalaryTemplate().getEffdate());
                        Date paysdate = financialDate.getTime();;
                        while(paysdate.compareTo(cal.getTime())<0){
                            JSONObject jtemp = new JSONObject();
                            jtemp.put("paycyclestart", sdf.format(paysdate));
                            jtemp.put("paycycleshowstart", df.format(paysdate));
                            financialDate.setTime(paysdate);
                            if(financialDate.get(Calendar.DATE) == 16){
                            	financialDate.set(Calendar.DATE, financialDate.getActualMaximum(Calendar.DAY_OF_MONTH)+1);
                            } else {
                            	financialDate.set(Calendar.DATE, financialDate.get(Calendar.DATE)+15);
                            }
                            paysdate = financialDate.getTime();
                            financialDate.set(Calendar.DATE, financialDate.get(Calendar.DATE)-1);
                            jtemp.put("paycycleend", sdf.format(financialDate.getTime()));
                            jtemp.put("paycycleactualend", sdf.format(financialDate.getTime()));
                            jtemp.put("paycycleshowend", df.format(financialDate.getTime()));
                            jtemp.put("TempID", tempmap.getSalaryTemplate().getTemplateid());
                            jtemp.put("paycycletemplate", tempmap.getSalaryTemplate().getTemplatename());
                            transferjobj.append("data",jtemp);
                        }
                        if(transferjobj.has("data")){
                            tmpArr = transferjobj.getJSONArray("data");
                            for(int h = tmpArr.length()-1 ; h >= 0 ; h--){
                                jobj.append("data",tmpArr.get(h));
                            }
                        }
                    }
                }else {
                    jobj.put("data", "");
                }
            }else{
            	Calendar cal = Calendar.getInstance();
            	Calendar cals = Calendar.getInstance();
            	//cal.setTimeZone(timeZone);
                Date startdate = null;
            	requestParams.put("filter_names", Arrays.asList("userAccount.userID","<=effectiveDate"));
            	requestParams.put("filter_values", Arrays.asList(request.getParameter("userid"),new Date()));
            	requestParams.put("order_by", Arrays.asList("effectiveDate"));
            	requestParams.put("order_type", Arrays.asList("asc"));
            	result = hrmsPayrollTemplateDAOObj.getUserSalaryTemplateMap(requestParams);
            	if(StringUtil.checkResultobjList(result)){
            	List<userSalaryTemplateMap> list = result.getEntityList();
            	for(int i=0; i<list.size(); i++){
            		Calendar upto = Calendar.getInstance();
            		userSalaryTemplateMap tempmap = list.get(i);
            		if(tempmap.getSalaryTemplate().getPayinterval()==1){//if paycycle is monthly
            			if(tempmap.getEffectiveDate()!=null){
            				effectiveDate = tempmap.getEffectiveDate().getDate();
            			}
                		if(tempmap.getEffectiveDate().before(financialDate.getTime())){
                			if(effectiveDate>financialDate.getActualMaximum(Calendar.DAY_OF_MONTH)){
                				financialDate.set(Calendar.DATE, financialDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                			}else{
                				financialDate.set(Calendar.DATE, effectiveDate);
                			}
                			startdate = financialDate.getTime();
                		}else{
                			startdate = tempmap.getEffectiveDate();
                		}
                		
                		if((i+1)<list.size()){
                			upto.setTime(list.get(i+1).getEffectiveDate());
                			Map<String, Object> requesParams = new HashMap<String, Object>();
                			requesParams.put("tempmap", list.get(i+1));
                			requesParams.put("request", request);
                			upto.set(Calendar.DATE, getRemainigDaysBetweenTwoTemplates(requesParams));
                		}else{
                			upto.setTime(new Date());
                		}
            			int x=0;
            			cals.setTime(startdate);
//            			cals.set(cals.HOUR_OF_DAY, cal.HOUR_OF_DAY);
//            			cals.set(cals.MINUTE, cal.MINUTE);
//            			cals.set(cals.SECOND, cal.SECOND);
            			cal.setTime(cals.getTime());
            			while(cal.getTime().before(upto.getTime())){
            				JSONObject jtemp = new JSONObject();
            				cal.setTime(cals.getTime());
            				cal.roll(Calendar.MONTH, x);
            				if(cal.get(Calendar.MONTH)==11)
            					cals.add(cals.YEAR, 1);
            				if(effectiveDate>28){
            					if(effectiveDate>=cal.getActualMaximum(Calendar.DAY_OF_MONTH))
            						cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            					else
            						cal.set(Calendar.DATE, effectiveDate);
            				}
                            jtemp.put("paycyclestart", sdf.format(cal.getTime()));
                            jtemp.put("paycycleshowstart", df.format(cal.getTime()));
                           	cal.setTime(cals.getTime());
                           	cal.roll(Calendar.MONTH, ++x);
                           	cal.add(Calendar.DATE, -1);
                           	jtemp.put("paycycleend", sdf.format(cal.getTime()));
                           	cal.add(Calendar.DATE, 1);
                           	if(cal.getTime().compareTo(upto.getTime())>=0 && ((i+1)!=list.size())){
                           		cal = upto;
//                           	cal.set(cal.HOUR_OF_DAY, cals.HOUR_OF_DAY);
//                    			cal.set(cal.MINUTE, cals.MINUTE);
//                    			cal.set(cal.SECOND, cals.SECOND);
                           	}
                           	if(effectiveDate>28){
            					if(effectiveDate>=cal.getActualMaximum(Calendar.DAY_OF_MONTH))
            						cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            					else
            						cal.set(Calendar.DATE, effectiveDate);
            				}
                           	cal.add(Calendar.DATE, -1);
                            jtemp.put("paycycleactualend", sdf.format(cal.getTime()));
                            jtemp.put("paycycleshowend", df.format(cal.getTime()));
                            jtemp.put("TempID", tempmap.getSalaryTemplate().getTemplateid());
                            jtemp.put("paycycletemplate", tempmap.getSalaryTemplate().getTemplatename());
                            transferjobj.append("data",jtemp);
            			}
            		}else{
            			if(tempmap.getSalaryTemplate().getPayinterval()==3){//if paycycle is weekly
            				if(tempmap.getEffectiveDate()!=null){
                				effectiveDate = tempmap.getEffectiveDate().getDay();
                			}
                    		if(tempmap.getEffectiveDate().before(financialDate.getTime())){
                    			if(effectiveDate!=financialDate.get(Calendar.DAY_OF_WEEK)){
                    				financialDate.set(Calendar.DAY_OF_WEEK, effectiveDate+1);
                    			}
                    			startdate = financialDate.getTime();
                    		}else{
                    			startdate = tempmap.getEffectiveDate();
                    		}
                    		
                    		if((i+1)<list.size()){
                    			upto.setTime(list.get(i+1).getEffectiveDate());
                    			Map<String, Object> requesParams = new HashMap<String, Object>();
                    			requesParams.put("tempmap", list.get(i+1));
                    			requesParams.put("request", request);
                    			upto.set(Calendar.DATE, getRemainigDaysBetweenTwoTemplates(requesParams));
                    		}else{
                    			upto.setTime(new Date());
                    		}
                    		cals.setTime(startdate);
//                			cals.set(cals.HOUR_OF_DAY, cal.HOUR_OF_DAY);
//                			cals.set(cals.MINUTE, cal.MINUTE);
//                			cals.set(cals.SECOND, cal.SECOND);
                    		cal.setTime(cals.getTime());
                    		while(cal.getTime().before(upto.getTime())){
                				JSONObject jtemp = new JSONObject();
                				cal.setTime(cals.getTime());
                				jtemp.put("paycyclestart", sdf.format(cal.getTime()));
                                jtemp.put("paycycleshowstart", df.format(cal.getTime()));
                                cal.add(Calendar.DATE, 7);
                                cal.add(Calendar.DATE, -1);
                                jtemp.put("paycycleend", sdf.format(cal.getTime()));
                                cal.add(Calendar.DATE, 1);
                                if(cal.getTime().compareTo(upto.getTime())>=0 && ((i+1)!=list.size())){
                               		cal = upto;
//                               	cal.set(cal.HOUR_OF_DAY, cals.HOUR_OF_DAY);
//                        			cal.set(cal.MINUTE, cals.MINUTE);
//                        			cal.set(cal.SECOND, cals.SECOND);
                                }
                                cals.setTime(cal.getTime());
                                cal.add(Calendar.DATE, -1);
                                jtemp.put("paycycleactualend", sdf.format(cal.getTime()));
                                jtemp.put("paycycleshowend", df.format(cal.getTime()));
                                jtemp.put("TempID", tempmap.getSalaryTemplate().getTemplateid());
                                jtemp.put("paycycletemplate", tempmap.getSalaryTemplate().getTemplatename());
                                transferjobj.append("data",jtemp);
                			}
            			}else{
            				if(tempmap.getSalaryTemplate().getPayinterval()==2){//if paycycle is bi-monthly
            					if(tempmap.getEffectiveDate()!=null){
                    				effectiveDate = tempmap.getEffectiveDate().getDate();
                    			}
                        		if(tempmap.getEffectiveDate().before(financialDate.getTime())){
                        			if(effectiveDate!=1 || effectiveDate!=16){
                    					if(effectiveDate<16){
                    						financialDate.set(Calendar.DATE, 1);
                    					}else{
                    						if(tempmap.getEffectiveDate().before(financialDate.getTime()))
                    							financialDate.set(Calendar.DATE, 1);
                    						else
                    							financialDate.set(Calendar.DATE, 16);
                    					}
                    				}
                        			startdate = financialDate.getTime();
                        		}else{
                        			startdate = tempmap.getEffectiveDate();
                        		}
                        		
                        		if((i+1)<list.size()){
                        			upto.setTime(list.get(i+1).getEffectiveDate());
                        			Map<String, Object> requesParams = new HashMap<String, Object>();
                        			requesParams.put("tempmap", list.get(i+1));
                        			requesParams.put("request", request);
                        			upto.set(Calendar.DATE, getRemainigDaysBetweenTwoTemplates(requesParams));
                        		}else{
                        			upto.setTime(new Date());
                        		}
                        		cals.setTime(startdate);
//                    			cals.set(cals.HOUR_OF_DAY, cal.HOUR_OF_DAY);
//                    			cals.set(cals.MINUTE, cal.MINUTE);
//                    			cals.set(cals.SECOND, cal.SECOND);
                        		cal.setTime(cals.getTime());
                    			while(cal.getTime().before(upto.getTime())){
                    				JSONObject jtemp = new JSONObject();
                    				cal.setTime(cals.getTime());
                    				jtemp.put("paycyclestart", sdf.format(cal.getTime()));
                                    jtemp.put("paycycleshowstart", df.format(cal.getTime()));
                                    if(cal.get(Calendar.DATE) >= 16){
                                    	cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH)+1);
                                    } else {
                                    	if(cal.get(Calendar.DATE)!=1){
                                    		cal.set(Calendar.DATE, 16);
                        				}else{                                    	
                        					cal.set(Calendar.DATE, cal.get(Calendar.DATE)+15);
                        				}
                                    }
                                    cal.add(Calendar.DATE, -1);
                                    jtemp.put("paycycleend", sdf.format(cal.getTime()));
                                    cal.add(Calendar.DATE, 1);
                                    if(cal.getTime().compareTo(upto.getTime())>=0 && ((i+1)!=list.size())){
                                   		cal = upto;
//                                   	cal.set(cal.HOUR_OF_DAY, cals.HOUR_OF_DAY);
//                            			cal.set(cal.MINUTE, cals.MINUTE);
//                            			cal.set(cal.SECOND, cals.SECOND);
                                    }
                                    cals.setTime(cal.getTime());
                                    cal.add(Calendar.DATE, -1);
                                    jtemp.put("paycycleactualend", sdf.format(cal.getTime()));
                                    jtemp.put("paycycleshowend", df.format(cal.getTime()));
                                    jtemp.put("TempID", tempmap.getSalaryTemplate().getTemplateid());
                                    jtemp.put("paycycletemplate", tempmap.getSalaryTemplate().getTemplatename());
                                    transferjobj.append("data",jtemp);
                    			}
                			}
            			}
            		}
            	}
            	if(transferjobj.has("data")){
                    tmpArr = transferjobj.getJSONArray("data");
                    for(int h = tmpArr.length()-1 ; h >= 0 ; h--){
                        jobj.append("data",tmpArr.get(h));
                    }
                }
            }
        	else {
                jobj.put("data", "");
            }
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", jobj.length());
            jobj1.put("data", jobj);
            return new ModelAndView("jsonView", "model", jobj1.toString());
        } catch (Exception se) {
        	se.printStackTrace();
            return new ModelAndView("jsonView", "model", "failed");
        } finally {
        }
    }
    
    @SuppressWarnings({ "finally"})
	public int getRemainigDaysBetweenTwoTemplates(Map<String, Object> requesParams){
    	int endDate = 0;
    	try{
    		userSalaryTemplateMap tempmap = (userSalaryTemplateMap) requesParams.get("tempmap");
    		//HttpServletRequest request = (HttpServletRequest) requesParams.get("request");
    		//TimeZone timeZone = TimeZone.getTimeZone(kwlCommonTablesDAOObj.getTimeZone(request, sessionHandlerImplObj.getUserid(request)));
    		Calendar calendar = Calendar.getInstance();
    		//calendar.setTimeZone(timeZone);
    		calendar.setTime(tempmap.getEffectiveDate());
    		endDate = calendar.get(Calendar.DATE);
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		return endDate;
    	}
    }

    @SuppressWarnings("unchecked")
    public ModelAndView getuserlist(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            StringUtil.checkpaging(requestParams, request);
            requestParams.put("order_by", Arrays.asList("user.firstName"));
            requestParams.put("order_type", Arrays.asList("asc"));
            requestParams.put("filter_names", Arrays.asList("user.company.companyID", "user.deleteflag"));
            requestParams.put("filter_values", Arrays.asList(sessionHandlerImplObj.getCompanyid(request), new Integer(0)));
            requestParams.put("searchcol", new String[]{"user.firstName","user.lastName"});
            requestParams.put("ss", request.getParameter("ss"));
            result = hrmsCommonDAOObj.getUseraccount(requestParams);
            List lst = result.getEntityList();
            for (int i = 0; i < lst.size(); i++) {
                try{
                    if(lst.get(i)!=null){
                        Useraccount ua = (Useraccount) lst.get(i);
                        if(ua.getUser()!=null){
                            User user = ua.getUser();
                            JSONObject jtemp = new JSONObject();
                            jtemp.put("id", user.getUserID());
                            jtemp.put("GroupName", (ua.getDesignationid()==null)?"":ua.getDesignationid().getValue());
                            jtemp.put("TempName", ((user.getFirstName()==null)?"":user.getFirstName())+" "+((user.getLastName()==null)?"":user.getLastName()));
                            jobj.append("data", jtemp);
                        }
                    }
                }catch(Exception e){
                	logger.warn("Exception occurred in hrmsPayrollTemplateController.getuserlist", e);
                }
            }
            if (lst.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
            return new ModelAndView("jsonView", "model", jobj1.toString());
        } catch (Exception se) {
            logger.warn("Exception occurred in hrmsPayrollTemplateController.getuserlist", se);
            return new ModelAndView("jsonView", "model", "failed");
        } finally {
        }
    }


    public ModelAndView getPayProcessData(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            requestParams.put("Cid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("start", request.getParameter("start"));
            requestParams.put("limit", request.getParameter("limit"));
            requestParams.put("ss", request.getParameter("ss"));

            result = hrmsPayrollTemplateDAOObj.getPayProcessData(requestParams);
            List lst = result.getEntityList();
            for (int i = 0; i < lst.size(); i++) {
                masterDB.Template wage = (masterDB.Template) lst.get(i);
                JSONObject jobjinloop = new JSONObject();
                jobjinloop.put("TempID", wage.getTemplateid());
                jobjinloop.put("payinterval", wage.getPayinterval());
                jobjinloop.put("effdate", wage.getEffdate());
                jobjinloop.put("basic", 0);
                jobjinloop.put("TempName", wage.getTemplatename());
                jobjinloop.put("showborder", wage.isShowborder());
                jobjinloop.put("TempRange", wage.getStartrange() + " - " + wage.getEndrange());
                jobjinloop.put("GroupId", wage.getDesignationid() != null ? wage.getDesignationid().getId() : "");
                jobjinloop.put("GroupName", wage.getDesignationid() != null ? wage.getDesignationid().getValue() : "");
                int taxcount = hrmsPayrollTaxDAOObj.getTaxTemplateCount(wage.getTemplateid()).getRecordTotalCount();
                jobjinloop.put("NosTax", Integer.toString(taxcount));
                taxcount = hrmsPayrollWageDAOObj.getWageTemplateCount(wage.getTemplateid()).getRecordTotalCount();
                jobjinloop.put("NosWage", Integer.toString(taxcount));
                taxcount = hrmsPayrollDeductionDAOObj.getDeducTemplateCount(wage.getTemplateid()).getRecordTotalCount();
                jobjinloop.put("NosDeduc", Integer.toString(taxcount));
                jobj.append("data", jobjinloop);

            }
            if (lst.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
            return new ModelAndView("jsonView", "model", jobj1.toString());
        } catch (Exception se) {
            return new ModelAndView("jsonView", "model", "failed");
        } finally {
        }
    }
    public ModelAndView assignTemplateToUser(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jtemp = new JSONObject();
        boolean flag = true;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        try {
            if(request.getParameter("mode").equals("delete")){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date d1 = sdf.parse(request.getParameter("effectivedate").toString());
                requestParams.put("filter_names", Arrays.asList("userID.userID","<=paycycleend"));
                requestParams.put("filter_values", Arrays.asList(request.getParameter("userid"),d1));
                requestParams.put("order_by", Arrays.asList("paycycleend"));
                requestParams.put("order_type", Arrays.asList("desc"));
                result = hrmsEmpDAOObj.getPayHistory(requestParams);
//                requestParams.put("filter_names", Arrays.asList("userAccount.userID","<=effectiveDate"));
//                requestParams.put("filter_values", Arrays.asList(request.getParameter("userid"),d1));
//                requestParams.put("order_by", Arrays.asList("effectiveDate"));
//                requestParams.put("order_type", Arrays.asList("desc"));
//                result = hrmsPayrollTemplateDAOObj.getUserSalaryTemplateMap(requestParams);
                if(StringUtil.checkResultobjList(result)){
                    Calendar cal = Calendar.getInstance();
                    Calendar calstart = Calendar.getInstance();
                    Calendar calend = Calendar.getInstance();
                    List lst = result.getEntityList();
                    Payhistory tempmap = (Payhistory)lst.get(0);
                    Template te = tempmap.getTemplate();
                    cal.setTime(d1);
                    cal.add(Calendar.DATE, -1);
                    d1 = cal.getTime();
                    Date d2 = tempmap.getPaycyclestart();
                    calstart.setTime(d2);
                    if(tempmap.getTemplate().getPayinterval() == 1){
                        int dayinmonth = calstart.getActualMaximum(Calendar.DAY_OF_MONTH);
                        calstart.add(Calendar.DATE, dayinmonth);
                        calstart.add(Calendar.DATE, -1);
                        if(!calstart.getTime().equals(tempmap.getPaycycleend())){
                            flag = false;
                        }
                    } else if(tempmap.getTemplate().getPayinterval() == 2){
                        calstart.add(Calendar.DATE, 7);
                        calstart.add(Calendar.DATE, -1);
                        if(!calstart.getTime().equals(tempmap.getPaycycleend())){
                            flag = false;
                        }
                    } else if(tempmap.getTemplate().getPayinterval() == 3){
                        int dayinmonth = calstart.getActualMaximum(Calendar.DAY_OF_MONTH);
                        if(calstart.get(Calendar.DATE) == 16 && calstart.getActualMaximum(Calendar.DAY_OF_MONTH) == 31){
                            calstart.set(Calendar.DATE, calstart.get(Calendar.DATE)+16);
                        } else {
                            calstart.set(Calendar.DATE, calstart.get(Calendar.DATE)+15);
                        }
                        calstart.add(Calendar.DATE, -1);
                        calstart.set(Calendar.DATE, calstart.get(Calendar.DATE)-1);
                        if(!calstart.getTime().equals(tempmap.getPaycycleend())){
                            flag = false;
                        }
                    }
                }
            }
            if(flag){
                requestParams.put("userid", request.getParameter("userid"));
                requestParams.put("templateid", request.getParameter("templateid"));
                requestParams.put("basic", request.getParameter("basicsal"));
                requestParams.put("effectivedate", request.getParameter("effectivedate"));
                requestParams.put("mode", request.getParameter("mode"));
                result = hrmsPayrollTemplateDAOObj.assignTemplateToUser(requestParams);

                if (result.isSuccessFlag()) {
                    jobj.put("msg", "valid");
                    jobj.put("success", true);
                    jtemp.put("valid", true);
                    jtemp.put("data", jobj);
                    txnManager.commit(status);
                    return new ModelAndView("jsonView", "model", jtemp.toString());
                } else if(!StringUtil.isNullOrEmpty(result.getMsg())){
                    jobj.put("msg", result.getMsg());
                    jobj.put("success", false);
                    jtemp.put("valid", true);
                    jtemp.put("data", jobj);
                    txnManager.rollback(status);
                    return new ModelAndView("jsonView", "model", jtemp.toString());
                } else {
                    txnManager.rollback(status);
                    return new ModelAndView("jsonView", "model", "failed");
                }
            } else {
                txnManager.rollback(status);
                jobj.put("msg", "invalid");
                jobj.put("success", true);
                jtemp.put("valid", true);
                jtemp.put("data", jobj);
                return new ModelAndView("jsonView", "model", jtemp.toString());
            }
        } catch (ParseException ex) {
            txnManager.rollback(status);
            return new ModelAndView("jsonView", "model", "failed");
        } catch (ServiceException ex) {
           
            txnManager.rollback(status);
            
            return new ModelAndView("jsonView", "model", "failed");
        } catch (JSONException ex) {
             txnManager.rollback(status);
            Logger.getLogger(hrmsPayrollTemplateController.class.getName()).log(Level.SEVERE, null, ex);
            return new ModelAndView("jsonView", "model", "failed");
        }
    }
     public ModelAndView getAssignedTemplateForEmponDate(HttpServletRequest request, HttpServletResponse response){
        try {
            KwlReturnObject result = null;
            JSONObject jobj = new JSONObject();
            JSONObject jtemp = new JSONObject();
                 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            
                requestParams.put("records", request.getParameter("records").toString());
            result = hrmsPayrollTemplateDAOObj.getTemplateForEmponDate(requestParams);
            List lst = result.getEntityList();
            for (int i = 0; i < lst.size(); i++) {
                try {
                    userSalaryTemplateMap usersalObj = (userSalaryTemplateMap) lst.get(i);
                    Template template = usersalObj.getSalaryTemplate();
                    JSONObject tempObj = new JSONObject();
                    tempObj.put("effectivedate",sdf.format(usersalObj.getEffectiveDate()));
                    tempObj.put("templatename",template.getTemplatename());
                    tempObj.put("templateid",template.getTemplateid());
                    tempObj.put("userid",usersalObj.getUserAccount().getUserID());
                    jobj.append("data", tempObj);
                } catch (JSONException ex) {
                    Logger.getLogger(hrmsPayrollTemplateController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            jobj.put("success", true);
            jtemp.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jtemp.put("data", jobj);
            return new ModelAndView("jsonView", "model", jtemp.toString());
        } catch (JSONException ex) {
            Logger.getLogger(hrmsPayrollTemplateController.class.getName()).log(Level.SEVERE, null, ex);
            return new ModelAndView("jsonView", "model", "failed");
        }

    }
    public ModelAndView getAssignedTemplateForEmp(HttpServletRequest request, HttpServletResponse response){
        try {
            KwlReturnObject result = null;
            JSONObject jobj = new JSONObject();
            JSONObject jtemp = new JSONObject();
                 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", request.getParameter("userid"));
            result = hrmsPayrollTemplateDAOObj.getTemplateForEmp(requestParams);
            List lst = result.getEntityList();
            for (int i = 0; i < lst.size(); i++) {
                try {
                    userSalaryTemplateMap usersalObj = (userSalaryTemplateMap) lst.get(i);
                    Template template = usersalObj.getSalaryTemplate();
                    JSONObject tempObj = new JSONObject();
                    requestParams.clear();
//                    requestParams.put("filter_names", Arrays.asList("userID.userID","template.templateid"));
//                    requestParams.put("filter_values", Arrays.asList(request.getParameter("userid"), template.getTemplateid()));
                    requestParams.put("filter_names", Arrays.asList("userID.userID","mappingid.mappingid"));
                    requestParams.put("filter_values", Arrays.asList(request.getParameter("userid"), usersalObj.getMappingid()));
                    result = hrmsEmpDAOObj.getPayHistory(requestParams);

                    List lst1 = result.getEntityList();
                    if (lst1.size() != 0) {
                        tempObj.put("salaryflag", "1");
                    } else {
                        tempObj.put("salaryflag", "0");
                    }
                    tempObj.put("effectivedate",sdf.format(usersalObj.getEffectiveDate()));
                    tempObj.put("templatename",template.getTemplatename());
                    tempObj.put("templateid",template.getTemplateid());
                    tempObj.put("payinterval",template.getPayinterval());
                    tempObj.put("effdate", template.getEffdate());
                    tempObj.put("basic", usersalObj.getBasic());
                    jobj.append("data", tempObj);
                } catch (JSONException ex) {
                    Logger.getLogger(hrmsPayrollTemplateController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            jobj.put("success", true);
            jtemp.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jtemp.put("data", jobj);
            return new ModelAndView("jsonView", "model", jtemp.toString());
        } catch (JSONException ex) {
            Logger.getLogger(hrmsPayrollTemplateController.class.getName()).log(Level.SEVERE, null, ex);
            return new ModelAndView("jsonView", "model", "failed");
        }

    }
    public ModelAndView getAssignedEmpForTemplate(HttpServletRequest request, HttpServletResponse response){
        try {
            KwlReturnObject result = null;
            JSONObject jobj = new JSONObject();
            JSONObject jtemp = new JSONObject();
 

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("templateid", request.getParameter("templateid"));
            requestParams.put("filter_names", Arrays.asList("salaryTemplate.templateid"));
            requestParams.put("filter_values", Arrays.asList(request.getParameter("templateid")));
            result = hrmsPayrollTemplateDAOObj.getUserSalaryTemplateMap(requestParams);
            List lst = result.getEntityList();
            for (int i = 0; i < lst.size(); i++) {
                try {
                    userSalaryTemplateMap usersalObj = (userSalaryTemplateMap) lst.get(i);
                    Useraccount useraccount = usersalObj.getUserAccount();
                    Template template = usersalObj.getSalaryTemplate();
                    JSONObject tempObj = new JSONObject();
                    requestParams.clear();
                    requestParams.put("filter_names", Arrays.asList("userID.userID","mappingid.mappingid"));
                    requestParams.put("filter_values", Arrays.asList(useraccount.getUserID(), usersalObj.getMappingid()));
                    result = hrmsEmpDAOObj.getPayHistory(requestParams);

                    List lst1 = result.getEntityList();
                    if (lst1.size() != 0) {
                        tempObj.put("salaryflag", "1");
                    } else {
                        tempObj.put("salaryflag", "0");
                    }
                    tempObj.put("usertemplateid", usersalObj.getMappingid());
                    tempObj.put("fullname", useraccount.getUser().getFirstName() + " " + useraccount.getUser().getLastName());
                    tempObj.put("userid", useraccount.getUserID());
                    tempObj.put("effectivedate",sdf.format(usersalObj.getEffectiveDate()));
                    tempObj.put("templatename",template.getTemplatename());
                    tempObj.put("templateid",template.getTemplateid());
                    tempObj.put("payinterval",template.getPayinterval());
                    tempObj.put("effdate", template.getEffdate());
                    tempObj.put("basic", usersalObj.getBasic());
                    jobj.append("data", tempObj);
                } catch (JSONException ex) {
                    Logger.getLogger(hrmsPayrollTemplateController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            jobj.put("success", true);
            jtemp.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jtemp.put("data", jobj);
            return new ModelAndView("jsonView", "model", jtemp.toString());
        } catch (JSONException ex) {
            
            Logger.getLogger(hrmsPayrollTemplateController.class.getName()).log(Level.SEVERE, null, ex);
            return new ModelAndView("jsonView", "model", "failed");
        }

    }
    public ModelAndView setTemplateData(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jtemp = new JSONObject();
        KwlReturnObject result = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            String successmsg="success";
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            requestParams.put("formdata", request.getParameter("formdata"));
            requestParams.put("cmpic", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("deducdata", request.getParameter("deducdata"));
            requestParams.put("taxdata", request.getParameter("taxdata"));
            requestParams.put("wagedata", request.getParameter("wagedata"));
            requestParams.put("ecdata", request.getParameter("ecdata"));
            requestParams.put("assignedEmployees", request.getParameter("assignedEmployees"));
            result=hrmsPayrollTemplateDAOObj.setTemplateData(requestParams);
            Template tmpdate = null;
            if(result.isSuccessFlag()){
                if(result.getEntityList()!=null){
                    if(result.getEntityList().size()>0){
                        tmpdate = (Template)result.getEntityList().get(0);
                        result=hrmsPayrollWageDAOObj.setWageTemplateData(requestParams, tmpdate);
                        if(result.isSuccessFlag())
                            result=hrmsPayrollTaxDAOObj.setTaxTemplateData(requestParams,tmpdate);
                        if(result.isSuccessFlag())
                            result=hrmsPayrollDeductionDAOObj.setDeducTemplateData(requestParams,tmpdate);
                        if(result.isSuccessFlag())
                            result=hrmsPayrollEmployerContributionDAOObj.setEmployerContribTemplateData(requestParams,tmpdate);
                        if(result.isSuccessFlag())
                            auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_TEMPLATE_ADDED, "User " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has added new payroll template   " +tmpdate.getTemplatename()  , request, "0");
                    }
                }else{
                    result.setSuccessFlag(false);
                    successmsg="Exist";
                }
            }
            //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_TEMPLATE_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(req)) + " has added new payroll template   " +template.getTemplatename()  ,req);
            jobj.put("valid", true);
            jtemp.put("value", successmsg);
            jobj.put("data", jtemp);
            if(result.isSuccessFlag())
            	txnManager.commit(status);
            else
            	txnManager.rollback(status);
        } catch (Exception e) {
        //            return ("{\"valid\":\"true\",data:{value:\"failed\"}}");
            System.out.println("Error is "+e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model",jobj.toString());
    }
    public ModelAndView updateTemplateData(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj=new JSONObject();
        JSONObject jobj1=new JSONObject();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);

        try {
            int torp = Integer.parseInt(request.getParameter("torp"));
            if (torp == 1) {

                String tempid = request.getParameter("tempid");
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("templateid",tempid);
                result=hrmsPayrollDeductionDAOObj.deleteDeductionTemplateData(requestParams);
                if(result.isSuccessFlag())
                    result=hrmsPayrollTaxDAOObj.deleteTaxTemplateData(requestParams);
                if(result.isSuccessFlag())
                    result=hrmsPayrollWageDAOObj.deleteWageTemplateData(requestParams);
                if(result.isSuccessFlag())
                    result=hrmsPayrollEmployerContributionDAOObj.deleteEmployerContribTemplateData(requestParams);
                if(result.isSuccessFlag())
                    result=hrmsPayrollDeductionDAOObj.deleteTempDeductionTemplateData(requestParams);
                if(result.isSuccessFlag())
                    result=hrmsPayrollTaxDAOObj.deleteTempTaxTemplateData(requestParams);
                if(result.isSuccessFlag())
                    result=hrmsPayrollWageDAOObj.deleteTempWageTemplateData(requestParams);
                if(result.isSuccessFlag())
                    result=hrmsPayrollTemplateDAOObj.deleteTempTemplate(requestParams);

                //delete End
                    String tempuid = tempid;
                    requestParams.put("formdata", request.getParameter("formdata"));
                    requestParams.put("assignedEmployees", request.getParameter("assignedEmployees"));
                    //if(result.isSuccessFlag())
                        result = hrmsPayrollTemplateDAOObj.editTemplateData(requestParams);
                    if(result.isSuccessFlag()) {
                        List lst=result.getEntityList();
                    Template template = (Template) lst.get(0);
                    requestParams.put("deducdata", request.getParameter("deducdata"));
                    result=hrmsPayrollDeductionDAOObj.setDeducTemplateData(requestParams, template);
                    requestParams.put("taxdata", request.getParameter("taxdata"));
                    if(result.isSuccessFlag())
                        result=hrmsPayrollTaxDAOObj.setTaxTemplateData(requestParams, template);
                    requestParams.put("wagedata", request.getParameter("wagedata"));
                    if(result.isSuccessFlag())
                        result=hrmsPayrollWageDAOObj.setWageTemplateData(requestParams, template);
                    requestParams.put("ecdata", request.getParameter("ecdata"));
                    if(result.isSuccessFlag())
                        result=hrmsPayrollEmployerContributionDAOObj.setEmployerContribTemplateData(requestParams, template);
                    if(result.isSuccessFlag())
                        auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_TEMPLATE_EDITED, "User " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has edited template  " +template.getTemplatename() +" permanently" , request, "0");
                    }
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_TEMPLATE_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(req)) + " has edited template  " +template.getTemplatename() +" permanently" ,req);
                jobj.put("valid", true);
                jobj1.put("value", "success");
                jobj.put("data", jobj1);

            } else if (torp == 2) {
                String tempid = request.getParameter("tempid");
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("templateid",tempid);
                result=hrmsPayrollTemplateDAOObj.checkTemplateUserMap(requestParams);
                List lst=result.getEntityList();
                if (lst.size() > 0) {
                      jobj.put("valid", true);
                    jobj1.put("value", "either template doesn't exist or user assigned to this template");
                    jobj.put("data", jobj1);
                } else {
                    result=hrmsPayrollDeductionDAOObj.deleteTempDeductionTemplateData(requestParams);
                    if(result.isSuccessFlag())
                        result=hrmsPayrollTaxDAOObj.deleteTempTaxTemplateData(requestParams);
                    if(result.isSuccessFlag())
                        result=hrmsPayrollWageDAOObj.deleteTempWageTemplateData(requestParams);
                    requestParams.put("status", "1");
                    if(result.isSuccessFlag())
                        result=hrmsPayrollTemplateDAOObj.setTemplateStatus(requestParams);
                    Template template=(Template) result.getEntityList().get(0);
                    requestParams.put("formdata",request.getParameter("formdata"));
                    if(result.isSuccessFlag())
                        result=hrmsPayrollTemplateDAOObj.setTempTemplateData(requestParams);
                    requestParams.put("deducdata", request.getParameter("deducdata"));
                    if(result.isSuccessFlag())
                        result=hrmsPayrollDeductionDAOObj.setTempDeductionTemplateDetails(requestParams);
                    requestParams.put("taxdata", request.getParameter("taxdata"));
                    if(result.isSuccessFlag())
                        result=hrmsPayrollTaxDAOObj.setTempTaxTemplateDetails(requestParams);
                    requestParams.put("wagedata", request.getParameter("wagedata"));
                    if(result.isSuccessFlag())
                        result=hrmsPayrollWageDAOObj.setTempWageTemplateDetails(requestParams);
                    if(result.isSuccessFlag())
                        auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_TEMPLATE_EDITED, "User " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has edited template  " +template.getTemplatename() +" temporarily" , request, "0");
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_TEMPLATE_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(req)) + " has edited template  " +template.getTemplatename() +" temporarily" ,req);
                    jobj.put("valid", true);
                    jobj1.put("value", "success");
                    jobj.put("data", jobj1);

                }
            } else {
                  jobj.put("ret", "unsaved");

            }
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model",jobj.toString());
    }

    public ModelAndView deleteTemplateData(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();

        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String template = "";
            List lst = null;

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("tempid", request.getParameterValues("tempid"));
            String[] tempid = request.getParameterValues("tempid");
            for (int j = 0; j < tempid.length; j++) {
                ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
                filter_names.add("salaryTemplate.templateid");
                filter_values.add(tempid[j]);
                filter_names.add("userAccount.user.deleteflag");
                filter_values.add(0);
                filter_names.add("userAccount.user.company.companyID");
                filter_values.add(sessionHandlerImplObj.getCompanyid(request));
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                result = hrmsPayrollTemplateDAOObj.getUserSalaryTemplateMap(requestParams);
                lst = result.getEntityList();
                if (lst.size() > 0) {
                    Template tmp = (Template) kwlCommonTablesDAOObj.getObject("masterDB.Template", tempid[j]);
                    template += "" + tmp.getTemplatename() + ",";
                }
            }
            if (!StringUtil.isNullOrEmpty(template)) {
                template = template.substring(0, template.length() - 1);
                jobj.put("value", "assign");
                jobj.put("templates", template);
            } else {
                for (int j1 = 0; j1 < tempid.length; j1++) {
                    Template tmp = (Template) kwlCommonTablesDAOObj.getObject("masterDB.Template", tempid[j1]);
                    requestParams.clear();
                    requestParams.put("templateid", tempid[j1]);
                    result = hrmsPayrollDeductionDAOObj.deleteDeductionTemplateData(requestParams);
                    result = hrmsPayrollTaxDAOObj.deleteTaxTemplateData(requestParams);
                    result = hrmsPayrollWageDAOObj.deleteWageTemplateData(requestParams);
                    result = hrmsEmpDAOObj.deleteHistorydetail(requestParams);
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_TEMPLATE_DELETED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted payroll template  " + tmp.getTemplatename() ,request);
                    String auditmsg = "User " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has deleted payroll template  " + tmp.getTemplatename();
                    result = hrmsPayrollTemplateDAOObj.deleteTemplate(requestParams);
                    auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_TEMPLATE_DELETED, auditmsg, request, "0");
                }
                jobj.put("value", "success");
            }
            jobj1.put("data", jobj);
            jobj1.put("success", true);
            jobj1.put("valid", true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            jobj1.put("success", false);
            jobj1.put("valid", true);
            txnManager.rollback(status);
        } finally{
            return new ModelAndView("jsonView", "model", jobj1.toString());
        }
        
    }
//    public ModelAndView deleteTemplateData(HttpServletRequest request, HttpServletResponse response) {
//        KwlReturnObject result = null;
//        JSONObject jobj = new JSONObject();
//        JSONObject jobj1 = new JSONObject();
//
//        //Create transaction
//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        def.setName("JE_Tx");
//        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
//        TransactionStatus status = txnManager.getTransaction(def);
//        try {
//            String template = "";
//            List lst = null;
//
//            HashMap<String, Object> requestParams = new HashMap<String, Object>();
//            requestParams.put("tempid", request.getParameterValues("tempid"));
//            String[] tempid = request.getParameterValues("tempid");
//            for (int j = 0; j < tempid.length; j++) {
//                ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
//                filter_names.add("templateid");
//                filter_values.add(tempid[j]);
//                filter_names.add("user.deleteflag");
//                filter_values.add(0);
//                filter_names.add("user.company.companyID");
//                filter_values.add(sessionHandlerImplObj.getCompanyid(request));
//                requestParams.put("filter_names", filter_names);
//                requestParams.put("filter_values", filter_values);
//                result = hrmsCommonDAOObj.getUseraccount(requestParams);
//                lst = result.getEntityList();
//                if (lst.size() > 0) {
//                    Template tmp = (Template) kwlCommonTablesDAOObj.getObject("masterDB.Template", tempid[j]);
//                    template += "" + tmp.getTemplatename() + ",";
//                }
//            }
//            if (!StringUtil.isNullOrEmpty(template)) {
//                template = template.substring(0, template.length() - 1);
//                jobj.put("value", "assign");
//                jobj.put("templates", template);
//            } else {
//                for (int j1 = 0; j1 < tempid.length; j1++) {
//                    Template tmp = (Template) kwlCommonTablesDAOObj.getObject("masterDB.Template", tempid[j1]);
//                    requestParams.clear();
//                    requestParams.put("templateid", tempid[j1]);
//                    result = hrmsPayrollDeductionDAOObj.deleteDeductionTemplateData(requestParams);
//                    result = hrmsPayrollTaxDAOObj.deleteTaxTemplateData(requestParams);
//                    result = hrmsPayrollWageDAOObj.deleteWageTemplateData(requestParams);
//                    result = hrmsEmpDAOObj.deleteHistorydetail(requestParams);
//                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_TEMPLATE_DELETED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted payroll template  " + tmp.getTemplatename() ,request);
//                    String auditmsg = "User " + profileHandlerDAOObj.getUserFullName(sessionHandlerImplObj.getUserid(request)) + " has deleted payroll template  " + tmp.getTemplatename();
//                    result = hrmsPayrollTemplateDAOObj.deleteTemplate(requestParams);
//                    auditTrailDAOObj.insertAuditLog(AuditAction.PAYROLL_TEMPLATE_DELETED, auditmsg, request, "0");
//                }
//                jobj.put("value", "success");
//            }
//            jobj1.put("data", jobj);
//            jobj1.put("valid", true);
//            txnManager.commit(status);
//        } catch (Exception e) {
//            e.printStackTrace();
//            txnManager.rollback(status);
//        }
//        return new ModelAndView("jsonView", "model", jobj1.toString());
//    }

    public ModelAndView getTemplistperDesign(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {
//            String query1 = "from Template where designationid.id=? and companyid=?";
            String cid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", Arrays.asList("designationid.id","companyid"));
            requestParams.put("filter_values", Arrays.asList(request.getParameter("desigid"),cid));
            result = hrmsPayrollTemplateDAOObj.getTemplate(requestParams);
            List lst2 = result.getEntityList();

//            lst2 = (List) HibernateUtil.executeQuery(session, query1, new Object[]{request.getParameter("desigid"), cid});

            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst2.size(); i++) {
                masterDB.Template taxobj = (masterDB.Template) lst2.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("templateid", taxobj.getTemplateid());
                jobjtemp.put("name", taxobj.getTemplatename());
                jobj.append("data", jobjtemp);
            }
            if (lst2.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ModelAndView(successView,"model",jobj1.toString());
        }
    }

    public ModelAndView getTemplatePaycycle(HttpServletRequest request, HttpServletResponse response){
        KwlReturnObject result = null;
        JSONObject jobj1 = new JSONObject();
        try {
        
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return new ModelAndView(successView,"model",jobj1.toString());
        }
    }

    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
