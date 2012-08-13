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

package com.krawler.spring.hrms.payroll;

import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.util.StringUtil;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.hrms.payroll.deduction.hrmsPayrollDeductionDAO;
import com.krawler.spring.hrms.payroll.salaryslip.ExportSalarySlipService;
import com.krawler.spring.hrms.payroll.tax.hrmsPayrollTaxDAO;
import com.krawler.spring.hrms.payroll.wages.hrmsPayrollWageDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.locale.MessageSourceProxy;
import com.krawler.common.util.Constants;
import com.krawler.common.util.LocaleUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.common.HrmsCommonPayroll;
import com.krawler.hrms.common.HrmsPayrollConstants;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.spring.hrms.payroll.incometax.IncomeTax;
import com.krawler.utils.json.base.JSONException;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.krawler.utils.json.base.JSONObject;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import masterDB.*;

import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


/**
 *
 * @author shs
 */

public class hrmsPayrollController extends MultiActionController{

    private hrmsPayrollWageDAO hrmsPayrollWageDAOObj;
    private hrmsPayrollTaxDAO hrmsPayrollTaxDAOObj;
    private hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj;
    private String successView;
    private sessionHandlerImpl sessionHandlerImplObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private HibernateTransactionManager txnManager;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private hrmsPayrollDAO hrmsPayrollDAOObj;
    private ExportSalarySlipService exportSalarySlipService;
    

    public void setHrmsPayrollDAO(hrmsPayrollDAO hrmsPayrollDAOObj1) {
        this.hrmsPayrollDAOObj = hrmsPayrollDAOObj1;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
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

    public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
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
    public void setHrmsPayrollTaxDAO(hrmsPayrollTaxDAO hrmsPayrollTaxDAOObj1) {
        this.hrmsPayrollTaxDAOObj = hrmsPayrollTaxDAOObj1;
    }
    public void setHrmsPayrollDeductionDAO(hrmsPayrollDeductionDAO hrmsPayrollDeductionDAOObj1) {
        this.hrmsPayrollDeductionDAOObj = hrmsPayrollDeductionDAOObj1;
    }
    
    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }
    
    public void setExportSalarySlipService(ExportSalarySlipService exportSalarySlipService) {
		this.exportSalarySlipService = exportSalarySlipService;
	}

    public ModelAndView getPayComponent_Date(HttpServletRequest request,HttpServletResponse response) {
        KwlReturnObject result = null;
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        int cnt = 0;
        try {
            String ss = request.getParameter("ss");
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            filter_names.add("company.companyID");
            filter_values.add(sessionHandlerImplObj.getCompanyid(request));
            filter_names.add("frequency");
            filter_values.add(Integer.parseInt(request.getParameter("frequency")));
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("ss", ss);
            requestParams.put("searchcol", new String[]{"code"});
            StringUtil.checkpaging(requestParams, request);
            result = hrmsPayrollDAOObj.getPaycomponent_date(requestParams);
            cnt = result.getRecordTotalCount();
            if(cnt>0){
                tabledata = result.getEntityList();
                
                for (int i = 0; i < tabledata.size(); i++) {
                    Componentmaster log = (Componentmaster) tabledata.get(i);
                    double amount = getAmount(log);
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("code", log.getCode());
                    tmpObj.put("compid", log.getCompid());
                    tmpObj.put("costcenter", log.getCostcenter().getId());
                    tmpObj.put("desc", log.getDescription());
                    tmpObj.put("edate", log.getEdate());
                    tmpObj.put("frequency", log.getFrequency());
                    tmpObj.put("paymentterm", log.getPaymentterm().getId());
                    tmpObj.put("sdate", log.getSdate());
                    tmpObj.put("type", log.getSubtype().getId());
                    tmpObj.put("isadjust", log.isIsadjust());
                    tmpObj.put("isblocked", log.isIsblock());
                    tmpObj.put("istaxablecomponent", log.isIstaxablecomponent());
                    tmpObj.put("amount", amount);
                    tmpObj.put("percent", log.getAmount());
                    tmpObj.put("isdefault", log.isIsdefault());
                    tmpObj.put("method", log.getMethod());
                    tmpObj.put("computeon", (log.getComputeon()!=null)?log.getComputeon().getCompid():"");
                    tmpObj.put("expression", getExpression(log.getCompid()).toString());
                    jarr.put(tmpObj);
                }
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", cnt);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);

        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        return new ModelAndView("jsonView","model",jobj1.toString());
    }
    
    public double getAmount(Componentmaster componentmaster){
    	double amount = 0.0;
    	try{
    		if(componentmaster.getMethod()==1){//Percent
                Componentmaster computedObj = componentmaster.getComputeon();
                amount = computedObj.getAmount()*(componentmaster.getAmount()/100);
            }else if(componentmaster.getMethod()==2){//Specified Formula
            	amount = getSpecifiedFormulaAmount(componentmaster.getCompid(), componentmaster.getAmount());
            }else if(componentmaster.getMethod()==0){//Amount
            	amount = componentmaster.getAmount();
            }	
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return amount;
    }
    
    public double getEditedAmount(Componentmaster componentmaster, double amt){
    	double amount = 0.0;
    	try{
    		if(componentmaster.getMethod()==1){//Percent
                amount = amt*(componentmaster.getAmount()/100);
            }else if(componentmaster.getMethod()==2){//Specified Formula
            	amount = getSpecifiedFormulaAmount(componentmaster.getCompid(), componentmaster.getAmount());
            }else if(componentmaster.getMethod()==0){//Amount
            	amount = componentmaster.getAmount();
            }	
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return amount;
    }
    
    public JSONArray getExpression(String masterComponent){
    	JSONArray array = new JSONArray();
    	try{
    		List<SpecifiedComponents> list = hrmsPayrollDAOObj.getSpecifiedComponents(masterComponent);
    		for(SpecifiedComponents components: list){
    			JSONObject object = new JSONObject();
    			Componentmaster component = components.getComponent();
    			object.put("operator", components.getOperator());
    			object.put("coefficient", components.getCoefficient());
    			object.put("component", component.getCompid());
    			object.put("componentname", component.getDescription());
    			array.put(object);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return array;
    }
    
    public ModelAndView getComputeOnComponents(HttpServletRequest request,HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            List<Componentmaster> list = hrmsPayrollDAOObj.getComputeOnComponents(sessionHandlerImplObj.getCompanyid(request), request.getParameter("componentid"), Integer.parseInt(request.getParameter("frequency")));
            if(list!=null){
                for (Componentmaster log : list) {
                    double amount = getAmount(log);
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("code", log.getCode());
                    tmpObj.put("compid", log.getCompid());
                    tmpObj.put("costcenter", log.getCostcenter().getId());
                    tmpObj.put("desc", log.getDescription());
                    tmpObj.put("edate", log.getEdate());
                    tmpObj.put("frequency", log.getFrequency());
                    tmpObj.put("paymentterm", log.getPaymentterm().getId());
                    tmpObj.put("sdate", log.getSdate());
                    tmpObj.put("type", log.getSubtype().getId());
                    tmpObj.put("typename", log.getSubtype().getValue());
                    tmpObj.put("isadjust", log.isIsadjust());
                    tmpObj.put("isblocked", log.isIsblock());
                    tmpObj.put("istaxablecomponent", log.isIstaxablecomponent());
                    tmpObj.put("amount", amount);
                    tmpObj.put("percent", log.getAmount());
                    tmpObj.put("isdefault", log.isIsdefault());
                    tmpObj.put("method", log.getMethod());
                    tmpObj.put("componentname", log.getDescription());
                    tmpObj.put("computeon", (log.getComputeon()!=null)?log.getComputeon().getCompid():"");
                    jarr.put(tmpObj);
                }
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", (list!=null?list.size():0));
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);

        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        return new ModelAndView("jsonView","model",jobj1.toString());
    }

    public ModelAndView addPayComponent_Date(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        boolean isDuplicateCode = false;
        KwlReturnObject result = null;
        try {
        	String companyid = sessionHandlerImplObj.getCompanyid(request);
        	String code = request.getParameter("code");
        	String frequency = request.getParameter("frequency");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList<String> filter_names = new ArrayList<String>();
            ArrayList<Object> filter_values = new ArrayList<Object>();
            filter_names.add("company.companyID");
            filter_values.add(companyid);
            filter_names.add("frequency");
            filter_values.add(Integer.parseInt(frequency));
            filter_names.add("code");
            filter_values.add(code);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            List<Componentmaster> list = hrmsPayrollDAOObj.getComponentmaster(requestParams);
            if(list!=null){
            	if(request.getParameter("action").equals("Add")){
            		isDuplicateCode = list.size()==0?false:true;
            	}else{
            		if(list.size()>0){
            			if(list.get(0).getCompid().equals(request.getParameter("id"))){
            				isDuplicateCode = list.size()>1?true:false;
            			}else{
            				isDuplicateCode = list.size()>=1?true:false;
            			}
            		}else{
            			isDuplicateCode = list.size()>1?true:false;
            		}
            	}
            }
            boolean isMethodChanged = true;
            if(!isDuplicateCode){
            	Componentmaster componentmaster = hrmsPayrollDAOObj.getComponentObj(request.getParameter("id"));
            	if(componentmaster!=null && !StringUtil.isNullOrEmpty(request.getParameter("method")) && componentmaster.getMethod()!=Integer.parseInt(request.getParameter("method"))){//Can't change the methode of dependent component
            		List<Componentmaster> components = hrmsPayrollDAOObj.getDepenentCompoment(request.getParameter("id"));
            		if(components!=null && !components.isEmpty()){
            			isMethodChanged = false;
            		}else{
            			List<SpecifiedComponents> specComps = hrmsPayrollDAOObj.getDependentSpecifiedComponents(request.getParameter("id"));
            			if(specComps!=null && !specComps.isEmpty()){
            				isMethodChanged = false;
            			}
            		}
            	}
            	
            	if(isMethodChanged){
		            requestParams.clear();
		            requestParams.put("companyid", companyid);
		            requestParams.put("compid", request.getParameter("id"));
		            requestParams.put("code", code);
		            requestParams.put("sdate", request.getParameter("startdate").equals("Select a Date value")?null:request.getParameter("startdate"));
		            requestParams.put("edate", request.getParameter("enddate").equals("Select a Date value")?null:request.getParameter("enddate"));
		            requestParams.put("description", request.getParameter("description"));
		            requestParams.put("subtype", request.getParameter("itemtypeid"));
		            requestParams.put("frequency", frequency);
		            requestParams.put("costcenter", request.getParameter("debit"));
		            requestParams.put("method", request.getParameter("method"));
		            requestParams.put("amount", request.getParameter("amount"));
		            requestParams.put("computeon", request.getParameter("computeon"));
		            requestParams.put("paymentterm", request.getParameter("payment"));
		            requestParams.put("isadjust", request.getParameter("Adjchk") != null ? true : false);
		            requestParams.put("isdefault", request.getParameter("lauto") != null ? true : false);
		            requestParams.put("isblock", request.getParameter("blocked") != null ? true : false);
                            requestParams.put("istaxablecomponent", request.getParameter("taxablecomponent") != null ? request.getParameter("taxablecomponent") : false);
		            requestParams.put("action", request.getParameter("action"));
		            requestParams.put("expression", request.getParameter("expression"));
		            result= hrmsPayrollDAOObj.addPaycomponent_date(requestParams);
                            List lst = result.getEntityList();
                            
                            Componentmaster comp=null;
                            
                            if(result.getRecordTotalCount()>0){
                                for (int i = 0; i < lst.size(); i++) {
                                    comp = (Componentmaster) lst.get(i);
                                }    
                            }
                            
                            if(request.getParameter("action").equals("Add") && !StringUtil.isNullOrEmpty(request.getParameter("rules"))){
                                
                                String dataString = request.getParameter("rules");
                                JSONObject data = new JSONObject(dataString);
                                JSONArray jarr = data.getJSONArray("data");
                                
                                for(int i=0; i<jarr.length() ; i++){
                                    
                                    JSONObject temp = jarr.getJSONObject(i);
                                    
                                    String lowerlimit = temp.getString("lowerlimit");
                                    String upperlimit = temp.getString("upperlimit");
                                    String coefficient = temp.getString("coefficient");
                                    if(!StringUtil.isNullOrEmpty(lowerlimit) && !StringUtil.isNullOrEmpty(upperlimit) && !StringUtil.isNullOrEmpty(coefficient) && comp!=null){
                                        double lLimit = Double.parseDouble(lowerlimit);
                                        double uLimit = Double.parseDouble(upperlimit);
                                        double Coff = Double.parseDouble(coefficient);
                                        
                                        boolean success = setComponentRuleObject(lLimit, uLimit, Coff, comp);
                                    }
                                }
                            
                            }
                            
                            
            	}
            }
            jobj.put("success", true);
            jobj.put("isDuplicateCode", isDuplicateCode);
            jobj.put("isMethodChanged", isMethodChanged);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView addComponentRule_Date(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        boolean success = false;
        JSONObject json = new JSONObject();
        try {
            
            String lowerLimit = request.getParameter("lowerLimit");
            String upperLimit = request.getParameter("upperLimit");
            String coeff = request.getParameter("coeff");
            String componentid = request.getParameter("componentid");
            
            if(!StringUtil.isNullOrEmpty(lowerLimit) && !StringUtil.isNullOrEmpty(upperLimit) && !StringUtil.isNullOrEmpty(coeff) && !StringUtil.isNullOrEmpty(componentid)){
                double lLimit = Double.parseDouble(lowerLimit);
                double uLimit = Double.parseDouble(upperLimit);
                double Coff = Double.parseDouble(coeff);
                Componentmaster component = hrmsPayrollDAOObj.getComponentObj(componentid);
                
                success = setComponentRuleObject(lLimit, uLimit, Coff, component);
            }

            
            jobj.put("success", true);
            json.put("data", jobj);
            json.put("valid", true);
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", json.toString());
    }
    
    public boolean setComponentRuleObject(double lowerlimit, double upperlimit, double coeff, Componentmaster component){
    
          boolean success = false;
          
          ComponentRule crule = new ComponentRule();
          crule.setLowerLimit(lowerlimit);
          crule.setUpperLimit(upperlimit);
          crule.setCoefficient(coeff);
          crule.setComponent(component);
          
          success= hrmsPayrollDAOObj.setComponentRuleObject(crule);
          
          return success;
          
          
    } 
    
     public ModelAndView getComponentRules(HttpServletRequest request,HttpServletResponse response) {
        List tabledata = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        int cnt = 0;
        try {
            List<ComponentRule> crulelist = new ArrayList<ComponentRule>();
            
            String componentid = request.getParameter("componentid");
            crulelist = hrmsPayrollDAOObj.getComponentsRules(componentid);
            cnt = crulelist.size();
            if(cnt>0){
                
                for(ComponentRule compRule : crulelist){
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("id", compRule.getId());
                    tmpObj.put("lowerlimit", compRule.getLowerLimit());
                    tmpObj.put("upperlimit", compRule.getUpperLimit());
                    tmpObj.put("coefficient", compRule.getCoefficient());
                    tmpObj.put("deleteicon","");
                    jarr.put(tmpObj);
                }
                
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", cnt);
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);

        } catch (Exception ex) {
            ex.printStackTrace();
        } 
        return new ModelAndView("jsonView","model",jobj1.toString());
    }
    
    public ModelAndView deleteComponentRule(HttpServletRequest request, HttpServletResponse response) {
        
        JSONObject msg = new JSONObject();
        JSONObject jobj = new JSONObject();
        boolean success = false;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            
            String ruleid = request.getParameter("ruleid");
            if(!StringUtil.isNullOrEmpty(ruleid)){
                success = hrmsPayrollDAOObj.deleteRule(ruleid);
            }
            
            msg.put("success", success);
            jobj.put("valid", true);
            jobj.put("data", msg.toString());
            txnManager.commit(status);
        
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    } 
    
    
    public ModelAndView getTaxableComponents(HttpServletRequest request, HttpServletResponse response) {
        JSONObject json = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArrayTaxableComponents = new JSONArray();
        JSONObject jsonType = new JSONObject();
        int count = 0;
        try {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String userid = request.getParameter("userID");
            Date year = sdf.parse(request.getParameter("year"));
            List<ComponentResourceMapping> userComponents = hrmsPayrollDAOObj.getUserIncomeTaxComponent(userid, year);
            
            StringBuffer componentList = new StringBuffer();
            
            for(ComponentResourceMapping crm : userComponents) {
                Componentmaster componentmaster = crm.getComponent();

                if(componentmaster.getCompid()!=null){
                    componentList.append("'").append(componentmaster.getCompid()).append("'");
                }
                if(count !=(userComponents.size()-1)){
                    componentList.append(",");
                }
                count++;
            }
            
            count=0;
            
            Map<String, Double> userTaxValue = new HashMap<String, Double>();
            
            List<UserTaxDeclaration> userTaxComponents = hrmsPayrollDAOObj.getUserIncomeTaxComponent(userid, componentList);
            
            for(UserTaxDeclaration tcomp: userTaxComponents){
            
                userTaxValue.put(tcomp.getComponent().getCompid(), tcomp.getValue());
            }
            
            if (userComponents != null && userComponents.size() > 0) {
                count = userComponents.size();
                for (ComponentResourceMapping componentResourceMappingObj : userComponents) {
                    
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("name", componentResourceMappingObj.getComponent().getCode());
                        jsonObj.put("compid", componentResourceMappingObj.getComponent().getCompid());
                        
                        if(userTaxValue.containsKey(componentResourceMappingObj.getComponent().getCompid())){
                            
                            jsonObj.put("value", userTaxValue.get(componentResourceMappingObj.getComponent().getCompid()));
                        }
                        jsonArrayTaxableComponents.put(jsonObj);
                    
                }
            }
            
            // For Savings
            Empprofile empprofile = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", userid);
            
            jsonType.put("savings", empprofile.getSavings());
            
            jsonType.put("taxablecomponents", jsonArrayTaxableComponents);
            
            json.put("success", true);
            json.put("data", jsonType);
            json.put("count", count);
            jsonObject.put("data", json.toString());
            jsonObject.put("valid", true);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jsonObject.toString());
    }
    
    public ModelAndView saveUserIncomeTaxDeclaration(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	String userid = request.getParameter("userid");
        	Date year = sdf.parse(request.getParameter("year"));
                Double savings = 0.0;
                if(!StringUtil.isNullOrEmpty(request.getParameter("savings"))){
                    savings = Double.parseDouble(request.getParameter("savings"));
                }
                
        	HashMap<Componentmaster, String> map = new HashMap<Componentmaster, String>();
        	List<ComponentResourceMapping> userComponents = hrmsPayrollDAOObj.getUserIncomeTaxComponent(userid, year);
        	
                for(ComponentResourceMapping comp: userComponents){
        		map.put(comp.getComponent(), request.getParameter(comp.getComponent().getCompid()));
        	}
        	
        	boolean success = hrmsPayrollDAOObj.saveUserIncomeTaxDeclaration(map, userid, savings);
        	
        	jsonObject.put("success", success);
                jsonObject.put("valid", true);
            
                txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jsonObject.toString());
    }
    
    
     
    public ModelAndView deletePayComponent_Date(HttpServletRequest request, HttpServletResponse response) {
        JSONObject msg = new JSONObject();
        JSONObject jobj = new JSONObject();
        boolean success = true;
        boolean somedeleted = false;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String[] componentid = request.getParameterValues("componentid");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList<String> filter_names = new ArrayList<String>();
            ArrayList<Object> filter_values = new ArrayList<Object>();
            for (int j = 0; j < componentid.length; j++) {
            	requestParams.clear();
        		filter_names.clear();
        		filter_values.clear();
            	filter_names.add("computeon.compid");
            	filter_values.add(componentid[j]);
            	requestParams.put("filter_names", filter_names);	
            	requestParams.put("filter_values", filter_values);
            	List<Componentmaster> componentmasters = hrmsPayrollDAOObj.getComponentmaster(requestParams);
            	if(componentmasters!=null && componentmasters.size()==0){
            		requestParams.clear();
            		filter_names.clear();
            		filter_values.clear();
            		filter_names.add("component.compid");
                	filter_values.add(componentid[j]);
            		requestParams.put("filter_names", filter_names);	
                	requestParams.put("filter_values", filter_values);
                	List<ComponentResourceMapping> componentResourceMappings = hrmsPayrollDAOObj.getComponentResourceMapping(requestParams);
            		if(componentResourceMappings!=null && componentResourceMappings.size()==0){
            			requestParams.clear();
            			filter_names.clear();
                		filter_values.clear();
                		filter_names.add("component.compid");
                    	filter_values.add(componentid[j]);
                		requestParams.put("filter_names", filter_names);	
                    	requestParams.put("filter_values", filter_values);
                    	List<ComponentResourceMappingHistory> componentResourceMappingHistories = hrmsPayrollDAOObj.getComponentResourceMappingHistory(requestParams);
            			if(componentResourceMappingHistories!=null && componentResourceMappingHistories.size()==0){
            				List<SpecifiedComponents> specifiedComponents = hrmsPayrollDAOObj.getDependentSpecifiedComponents(componentid[j]);
            				if(specifiedComponents!=null && specifiedComponents.size()==0){
            					
                                                if(hrmsPayrollDAOObj.deleteSpecifiedComponents(componentid[j])){
                                                    
                                                    if(hrmsPayrollDAOObj.deleteComponentRule(componentid[j])){
                                                        
                                                        if(hrmsPayrollDAOObj.deleteComponentUserTaxDeclaration(componentid[j])){
            						
                                                            hrmsPayrollDAOObj.deletePaycomponent_date(componentid[j]);
                                                            somedeleted = true;
                                                        }   
                                                    }  
            					
                                                }else{
            						success = false;
            					}
            				}else{
        						success = false;
        					}
            			}else{
                    		success = false;
                    	}
            		}else{
                		success = false;
                	}
            	}else{
            		success = false;
            	}
            }
            //@@ProfileHandler.insertAuditLog(session, auditID, "User <b>"+fullName+"</b> "+auditMsg+" <b>"+ids.length+"</b> master item(s) of master group <b>"+groupName+"</b>", request);
            msg.put("success", success);
            msg.put("somedeleted", somedeleted);
            jobj.put("valid", true);
            jobj.put("data", msg.toString());
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
// Function Used : Genrate Payroll Process
//    For gettting the list of availiable components while assigning component for  Previous or Default settings.
    public ModelAndView getAvailableComponent(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        List<Componentmaster> list = null;
        try {
            String userid = request.getParameter("userid");
            String comapnyid = sessionHandlerImplObj.getCompanyid(request);
            int frequency = Integer.parseInt(request.getParameter("frequency"));
            StringBuffer assignedComponentList = new StringBuffer();
            String previousSalaryFlag = request.getParameter("previousSalaryFlag");
            Componentmaster componentmasterassigned = null;
            
            if(!StringUtil.isNullOrEmpty(previousSalaryFlag)){ // For previous salary components
                if(Boolean.parseBoolean(previousSalaryFlag)){

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    Date startdate = df.parse(request.getParameter("startdate"));
                    Date enddate = df.parse(request.getParameter("enddate"));
                    boolean showAll = false;

                    int daysCount = getDayDiffrence(request.getParameter("frequency"), startdate);
                    enddate = getPreviosEndDate(startdate);
                    startdate = getPreviosStartDate(startdate, daysCount);

                    int count =0;
                    List<ComponentResourceMappingHistory> list1 = hrmsPayrollDAOObj.getSalaryComponentForEmployee(userid, startdate, enddate,showAll);
                    for(ComponentResourceMappingHistory crm : list1) {
                        componentmasterassigned = crm.getComponent();
                        
                        if(componentmasterassigned.getCompid()!=null){
                            assignedComponentList.append("'").append(componentmasterassigned.getCompid()).append("'");
                        }
                        if(count !=(list1.size()-1)){
                            assignedComponentList.append(",");
                        }
                        count++;
                    }

                }
            } else{ // For default components
                List<ComponentResourceMapping>  listComp = hrmsPayrollDAOObj.getAssignedComponent(userid);
                
                int count =0;
                for(ComponentResourceMapping mapping : listComp) {

                    componentmasterassigned = mapping.getComponent();
                    if(componentmasterassigned.getCompid()!=null){
                        assignedComponentList.append("'").append(componentmasterassigned.getCompid()).append("'");
                    }
                    if(count !=(listComp.size()-1)){
                        assignedComponentList.append(",");
                    }
                    count++;
                }
            }

            

            list = hrmsPayrollDAOObj.getAvailableComponent(assignedComponentList, comapnyid, frequency);
            JSONArray jarr = new JSONArray();
            JSONObject tmpObj = null;
            for(Componentmaster componentmaster : list) {
                tmpObj = new JSONObject();
                tmpObj.put("compid", componentmaster.getCompid());
                tmpObj.put("code", componentmaster.getCode());
                tmpObj.put("sdate", componentmaster.getSdate());
                tmpObj.put("edate", componentmaster.getEdate());
                tmpObj.put("desc", componentmaster.getDescription());
                tmpObj.put("isblock", componentmaster.isIsblock());
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", (list!=null?list.size():0));
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }
     //Function Used : Genrate Payroll Process
    // This function is used for Assign Default Components in Generate Payroll Process
    public ModelAndView getAssignedComponent(HttpServletRequest request,HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
        	String userid = request.getParameter("userid");
        	List<ComponentResourceMapping>  list = hrmsPayrollDAOObj.getAssignedComponent(userid);
        	Componentmaster componentmaster = null;
        	JSONObject tmpObj = null;
        	for(ComponentResourceMapping mapping : list) {
        		componentmaster = mapping.getComponent();
        		tmpObj = new JSONObject();
        		
        		tmpObj.put("compid", componentmaster.getCompid());
        		tmpObj.put("code", componentmaster.getCode());
        		tmpObj.put("sdate", componentmaster.getSdate());
        		tmpObj.put("edate", componentmaster.getEdate());
        		tmpObj.put("desc", componentmaster.getDescription());
                tmpObj.put("isblock", componentmaster.isIsblock());
        		jarr.put(tmpObj);
        	}
            jobj.put("data", jarr);
            jobj.put("count", (list!=null?list.size():0));
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    

    public ModelAndView getAssignedComponentsToResource(HttpServletRequest request,HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	String userid = request.getParameter("userid");
        	Date startdate = sdf.parse(request.getParameter("startdate"));
        	Date enddate = sdf.parse(request.getParameter("enddate"));
        	int frequency = Integer.parseInt(request.getParameter("frequency"));
        	
        	PayrollHistory ph = hrmsPayrollDAOObj.getPayrollHistoryForUser(userid, enddate, frequency);
        	
        	Componentmaster componentmaster = null;
        	ComponentResourceMappingHistory componentResourceMappingHistory = null;
        	List<ComponentResourceMappingHistory> componentResourceMappingHistories = hrmsPayrollDAOObj.getAssignedComponentsToResource(userid, enddate, frequency);
        	Iterator<ComponentResourceMappingHistory> iterator = componentResourceMappingHistories.iterator();
        	while (iterator.hasNext()) {
        		componentResourceMappingHistory = iterator.next();
        		JSONObject tmpObj = new JSONObject();
        		componentmaster = componentResourceMappingHistory.getComponent();
        		tmpObj.put("id", componentResourceMappingHistory.getId());
        		tmpObj.put("compid", componentmaster.getCompid());
        		tmpObj.put("code", componentmaster.getCode());
        		tmpObj.put("sdate", componentResourceMappingHistory.getComponentstartdate());
        		tmpObj.put("edate", componentResourceMappingHistory.getComponentenddate());
        		tmpObj.put("type", componentResourceMappingHistory.getComponent().getSubtype().getValue());
        		tmpObj.put("desc", componentmaster.getDescription());
        		if(componentmaster.getSubtype().getComponenttype()==5 && ph!=null){
        			tmpObj.put("amount", new BigDecimal(ph.getIncometaxAmount()).setScale(2, BigDecimal.ROUND_UP).doubleValue());
        		}else{
        			tmpObj.put("amount", componentResourceMappingHistory.getAmount());
        		}
        		
        		tmpObj.put("isadjust", componentmaster.isIsadjust());
                        boolean isaddruletypecomponent=false;
//                        if(componentmaster.getSubtype().getComponenttype()==Constants.MASTER_DATA_COMPONENTSUBTYPE_COMPONENT_TYPE_INCOME_TAX){
//                            isaddruletypecomponent=true;
//                        }
                        tmpObj.put("isaddruletypecomponent", isaddruletypecomponent);
        		jarr.put(tmpObj);
        	}
            jobj.put("data", jarr);
            jobj.put("count", componentResourceMappingHistories.size());
            jobj1.put("data", jobj.toString());
            jobj1.put("valid", true);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    
    @SuppressWarnings("finally")
	public ModelAndView editAssignedComponentsToResource(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        KwlReturnObject dependentComponents=null;
        List tabledata= null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
        	double incometax = 0;
            String[] ids = request.getParameterValues("ids");
            String[] amounts = request.getParameterValues("amounts");
            String[] components = request.getParameterValues("components");
            for (int i = 0; i < ids.length; i++) {
                double amnt = Double.parseDouble(amounts[i]);
                ComponentResourceMappingHistory crmh = hrmsPayrollDAOObj.editAssignedComponentsToResource(ids[i], amnt);
                if(crmh!=null && crmh.getComponent()!=null && crmh.getComponent().getSubtype().getComponenttype()==HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_INCOMETAX){
                	incometax = amnt;
                }
                
            }
            // Update dependent components
            boolean updateAllDependents = Boolean.parseBoolean(request.getParameter("updateAllDependents"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String userid = request.getParameter("userid");
            Date startdate = sdf.parse(request.getParameter("startdate"));
            Date enddate = sdf.parse(request.getParameter("enddate"));
            int frequency = Integer.parseInt(request.getParameter("frequency"));
            double absence = 0;
            if(!StringUtil.isNullOrEmpty(request.getParameter("absence"))){
            	absence = Double.parseDouble(request.getParameter("absence"));
            }
            
            if(updateAllDependents){
            	List <ComponentResourceMappingHistory> list = hrmsPayrollDAOObj.getAssignedComponentsToResource(userid, enddate, frequency);
                for (ComponentResourceMappingHistory history: list) {
                    hrmsPayrollDAOObj.editAssignedComponentsToResource(history.getId(), getEditedAmount(history, list));
                }
            }
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            HashMap<String, Double> map = new HashMap<String, Double>();
            
            IncomeTax itax = exportSalarySlipService.getIncomTaxObj(request);//For Malaysia IncomeTax object is not null
            if(updateAllDependents || itax!=null){
            	incometax = getIncomTaxAmount(request, userid, enddate, frequency);
            }
            map = getLeaveDeductionAmount(request, userid, startdate, enddate, frequency, absence,incometax);
            double leaveDeductionAmount= map.get("leavededuction");
            double netsalary= map.get("netsalary");
            double deduction= map.get("deduction");
            double tax= map.get("tax");
            double otherRemuneration= map.get("otherRemuneration");
            double earning= map.get("earning");

            requestParams.put("historyid", request.getParameter("historyid"));
            requestParams.put("leaveDeductionAmount", leaveDeductionAmount);
            requestParams.put("netsalary", netsalary);
            requestParams.put("deduction", deduction);
            requestParams.put("tax", tax);
            requestParams.put("otherRemuneration", otherRemuneration);
            requestParams.put("earning", earning);
            requestParams.put("incometax", incometax);
            
            hrmsPayrollDAOObj.updatePayrollHistory(requestParams);
            jobj.put("valid", true);
            jobj.put("data", "");
            txnManager.commit(status);
        }catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj.toString());
        }
    }
    
    public double getEditedAmount(ComponentResourceMappingHistory history, List<ComponentResourceMappingHistory> list){
    	double amount = 0.0;
    	try{
    		Componentmaster componentmaster = history.getComponent();
    		if(componentmaster.getMethod()==1){//Percent
                amount = getComponentHistoryAmount(history, list)*(componentmaster.getAmount()/100);
            }else if(componentmaster.getMethod()==2){//Specified Formula
            	amount = getEditedSpecifiedFormulaAmount(history, list);
            }else if(componentmaster.getMethod()==0){//Amount
            	amount = history.getAmount();
            }	
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return amount;
    }
    
    
    public double getComponentHistoryAmount(ComponentResourceMappingHistory history, List<ComponentResourceMappingHistory> list){
    	String computeon = history.getComponent().getComputeon().getCompid();
    	double amount=0.0;
    	try{
    		for(ComponentResourceMappingHistory mappingHistory: list){
    			if(mappingHistory.getComponent()!=null && mappingHistory.getComponent().getCompid().equals(computeon)){
    				amount = mappingHistory.getAmount();
    				break;
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return amount;
    }
    
    
    public double getEditedSpecifiedFormulaAmount(ComponentResourceMappingHistory history, List<ComponentResourceMappingHistory> list){
    	double amount=0;
    	try{
    		String componentid=history.getComponent().getCompid();
    		double percentof=history.getComponent().getAmount(); 
    		List<SpecifiedComponents> specifiedComponents = hrmsPayrollDAOObj.getSpecifiedComponents(componentid);
    		for(SpecifiedComponents components: specifiedComponents){
    			double tempAmount=components.getComponent().getAmount();
    			for(ComponentResourceMappingHistory mappingHistory: list){
        			if(mappingHistory.getComponent()!=null && mappingHistory.getComponent().getCompid().equals(components.getComponent().getCompid())){
        				tempAmount = mappingHistory.getAmount();
        				break;
        			}
        		}
    			
    			double amt = tempAmount*components.getCoefficient();
    			if(components.getOperator().equals("-")){
    				amt = amt*-1;
    			}
    			amount+=amt;
    		}
    		amount = amount*(percentof/100);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return amount;
    }
    

    @SuppressWarnings("finally")
	public ModelAndView assignFrequencytoResource(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String[] empids = request.getParameterValues("empids");
            int frequency = Integer.parseInt(request.getParameter("frequency"));
            hrmsPayrollDAOObj.editAssignFrequency(empids,frequency);
            String userid = "";
            for(int i=0; i<empids.length; i++){
            	userid+=("'"+empids[i]+"',");
            }
            if(empids.length>0){
            	userid = userid.substring(0, userid.length()-1);
            }
            hrmsPayrollDAOObj.deleteAssignedComponents(userid);
            
            List<Componentmaster> list = hrmsPayrollDAOObj.getDefaultComponents(sessionHandlerImplObj.getCompanyid(request), frequency);
            for(int i=0; i<empids.length; i++){
            	for(Componentmaster componentmaster: list){
            		hrmsPayrollDAOObj.addAssignedComponent(empids[i],componentmaster.getCompid());
            	}
            }
            
            jobj.put("valid", true);
            jobj.put("data", "");
            txnManager.commit(status);
        }catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj.toString());
        }
    }

    public ModelAndView assignComponent(HttpServletRequest request, HttpServletResponse response){
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String[] userids = request.getParameterValues("userid");
            String[] componentid = request.getParameterValues("componentid");
            String userid = "";
            for(int i=0; i<userids.length; i++){
            	userid+=("'"+userids[i]+"',");
            }
            if(userids.length>0){
            	userid = userid.substring(0, userid.length()-1);
            }
            hrmsPayrollDAOObj.deleteAssignedComponents(userid);
            
            for (int i = 0; i < userids.length; i++) {
            	for (int j = 0; j < componentid.length; j++) {
            		hrmsPayrollDAOObj.addAssignedComponent(userids[i],componentid[j]);
            	}
            }
            jobj.put("valid", true);
            jobj.put("data", "");
            txnManager.commit(status);
        }catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView","model",jobj.toString());
    }

    @SuppressWarnings("finally")
	public ModelAndView assignComponentToResource(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject jobj = new JSONObject();
    	JSONObject jsonObject = new JSONObject();
    	boolean success = true;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	DateFormat df = new SimpleDateFormat(hrmsCommonDAOObj.getUserDateFormat(sessionHandlerImplObj.getUserid(request)));
            String[] userids = request.getParameterValues("userid");
            String[] componentid = request.getParameterValues("componentid");
            String[] componentidamount = request.getParameterValues("componentidamount");
            Date startdate = sdf.parse(request.getParameter("startdate"));
            Date enddate = sdf.parse(request.getParameter("enddate"));
            int frequency = Integer.parseInt(request.getParameter("frequency"));
            boolean overwrite = Boolean.parseBoolean(request.getParameter("overwrite"));
            boolean isGeneratable = Boolean.parseBoolean(request.getParameter("isGeneratable"));
            List<PayrollHistory> histories = null; 
            PayrollHistory payrollHistory = null;
            for (int i = 0; i < userids.length; i++) {
            	if(overwrite){
                    StringBuffer userList = new StringBuffer();
                    userList.append("'").append(userids[i]).append("'");
                    deleteComponentOfResource(userList, startdate, enddate, frequency);
                }else{
                	if(isGeneratable){
                		success = true;
                	}else{
                		histories = hrmsPayrollDAOObj.getGeneratedSalaries(userids[i], startdate, enddate);
                		if(histories!=null && histories.size()>0){
                			payrollHistory = histories.get(0);
                			jsonObject.put("startdate",df.format(histories.get(0).getPaycyclestartdate()));
                			jsonObject.put("enddate", df.format(histories.get(0).getPaycycleenddate()));
                			Calendar calendar = Calendar.getInstance();
                			if(payrollHistory.getFrequency().equals(request.getParameter("frequency"))){
                				jsonObject.put("sameFrequency", true);
                				calendar.setTime(histories.get(0).getPaycyclestartdate());
                			}else{
                				jsonObject.put("sameFrequency", false);
                				calendar.setTime(histories.get(0).getPaycycleenddate());
                				calendar.add(Calendar.DATE, 1);
                			}
                			jsonObject.put("startDateDisplay", df.format(calendar.getTime()));
                			jsonObject.put("endDateDisplay", df.format(enddate));
                			jsonObject.put("startDate", sdf.format(calendar.getTime()));
                			jsonObject.put("endDate", sdf.format(enddate));
                			jsonObject.put("isGeneratable", (histories.get(0).getPaycycleenddate().getTime()<enddate.getTime()?true:false));
                			success = false;
                		}
                	}
                }
            	
                if(success){
                	long workingDays = 0;
                	for (int j = 0; j < componentid.length; j++) {
                		workingDays = 0;
                		Componentmaster component = hrmsPayrollDAOObj.getComponentObj(componentid[j]);
                		double amount=getAmount(component);
                		if(componentidamount!=null && componentidamount.length>j && !StringUtil.isNullOrEmpty(componentidamount[j])){
                			amount = Double.parseDouble(componentidamount[j]);
                		}
                		if(isGeneratable){
                			workingDays = ((enddate.getTime() - startdate.getTime()) / (86400000))+1;
                			amount = workingDays*(amount/new HrmsCommonPayroll().getTotalNumberOfWorkingDays(frequency, startdate));
                		}
                		
                		hrmsPayrollDAOObj.assignComponentOfResource(userids[i],component, amount, startdate, enddate, frequency);
                	}
                	addEditResorcePayrollData(request);
                }
            }
            
            jsonObject.put("success", success);
            jobj.put("valid", true);
            jobj.put("data", jsonObject.toString());
            txnManager.commit(status);
        }catch(ArithmeticException e){
			e.printStackTrace();
			Logger.getLogger(hrmsPayrollController.class.getName()).log(Level.WARNING, "Working days should not be zero");
		}catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }finally{
            return new ModelAndView("jsonView","model",jobj.toString());
        }
    }
    
    public double getSpecifiedFormulaAmount(String mastercomponentid, double percentof){
    	double amount=0;
    	try{
    		List<SpecifiedComponents> list = hrmsPayrollDAOObj.getSpecifiedComponents(mastercomponentid);
    		for(SpecifiedComponents components: list){
    			double amt = components.getComponent().getAmount()*components.getCoefficient();
    			if(components.getOperator().equals("-")){
    				amt = amt*-1;
    			}
    			amount+=amt;
    		}
    		amount = amount*(percentof/100);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return amount;
    }
    
    public ModelAndView getPayrollHistory(HttpServletRequest request,HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	Date startdate = sdf.parse(request.getParameter("sdate").toString());
        	Date enddate = sdf.parse(request.getParameter("edate").toString());
        	String frequency = request.getParameter("frequency");
            String status = request.getParameter("status");
            
            String ss = request.getParameter("ss");
            String companyid=sessionHandlerImplObj.getCompanyid(request);
            HashMap<String,Object> requestParams = new HashMap<String, Object>();

            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            filter_names.add(">=paycyclestartdate");
            filter_values.add(startdate);

            filter_names.add("<=paycycleenddate");
            filter_values.add(enddate);

            filter_names.add("user.company.companyID");
            filter_values.add(companyid);
            
            filter_names.add("frequency");
            filter_values.add(frequency);

            if(!StringUtil.isNullOrEmpty(status)){
                filter_names.add("salarystatus");
                filter_values.add(Integer.parseInt(status));
            }

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            if(!StringUtil.isNullOrEmpty(ss)){
                requestParams.put("ss", ss);
            }

            requestParams.put("searchcol", new String[]{"user.firstName","user.lastName"});
            StringUtil.checkpaging(requestParams, request);
            result = hrmsPayrollDAOObj.getPayrollHistory(requestParams);
            List lst = result.getEntityList();
            if(result.getRecordTotalCount()>0){
                for (int i = 0; i < lst.size(); i++) {
                     masterDB.PayrollHistory ph = (masterDB.PayrollHistory) lst.get(i);

                     Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", ph.getUser().getUserID());
                     Empprofile empprofile = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", ph.getUser().getUserID());

                     double currentSalary =ph.getNet()!=null?ph.getNet():0;
                     int dayDiff = getDayDiffrence(ph.getFrequency(),startdate);
                     
                     double prevSalary = getPreviousSalaryForEmployee(ph.getUser().getUserID(), startdate, enddate, dayDiff, companyid,frequency);
                     double difference = currentSalary-prevSalary;

                    JSONObject jobjinloop = new JSONObject();
                    requestParams.clear();
                    requestParams.put("companyid",sessionHandlerImplObj.getCompanyid(request));
                    requestParams.put("empid",ua.getEmployeeid());
                    if(ua.getEmployeeIdFormat()==null){
                        jobjinloop.put("employeeid", ua.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                    }else{
                        requestParams.put("standardEmpId", profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
                        requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                        jobjinloop.put("employeeid", profileHandlerDAOObj.getNewEmployeeIdFormat(requestParams));
                    }
                    jobjinloop.put("id", ph.getUser().getUserID());

                    jobjinloop.put("payhistoryid", ph.getHistoryid());
                    jobjinloop.put("fullname", ph.getName());
                    jobjinloop.put("resource", ph.getUser().getUserID());
                    jobjinloop.put("accountno", ua.getAccno());
                    jobjinloop.put("costcenter", ph.getCostCenter()!=null?ph.getCostCenter().getId():"");
                    jobjinloop.put("costcentername", ph.getCostCenter()!=null?ph.getCostCenter().getName():"");
                    jobjinloop.put("jobtitle", ph.getJobTitle()!=null?ph.getJobTitle().getId():"");
                    jobjinloop.put("jobtitlename", ph.getJobTitle()!=null?ph.getJobTitle().getValue():"");
                    jobjinloop.put("contract", ph.getContracthours());
                    jobjinloop.put("absence", ph.getUnpaidleaves());
                    jobjinloop.put("unpaidleavesAmount", ph.getUnpaidleavesAmount());
                    jobjinloop.put("incomeTax", ph.getIncometaxAmount());
                    jobjinloop.put("actual", ph.getActualhours());
                    jobjinloop.put("employmentdate", empprofile!=null?empprofile.getJoindate():"");
                    jobjinloop.put("contractenddate", ph.getContractdate());
                    jobjinloop.put("difference", difference);
                    jobjinloop.put("netsalary", ph.getNet());
                    jobjinloop.put("status", ph.getSalarystatus());
                    jobjinloop.put("comment", ph.getComment());

                    jobj.append("data", jobjinloop);

                }
            }
            if (result.getRecordTotalCount() == 0) {
                jobj.put("data", new JSONArray());
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", result.getRecordTotalCount());
            jobj1.put("data", jobj);
            return new ModelAndView("jsonView", "model", jobj1.toString());
        } catch (Exception se) {
            return new ModelAndView("jsonView", "model", "failed");
        }
    }
    
    public ModelAndView calculatePayroll(HttpServletRequest request, HttpServletResponse response) {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
        	boolean countryLevelValid = true;
            JSONObject jobj=new JSONObject();
            JSONArray jarr = new JSONArray(request.getParameter("jsonarray"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	JSONArray validationJson = new JSONArray();
            for(int i=0; i<jarr.length();i++){
                
                jobj = jarr.getJSONObject(i);
                Date startdate = sdf.parse(jobj.get("paycyclestartdate").toString());
                Date enddate = sdf.parse(jobj.get("paycycleenddate").toString());
                int frequency = Integer.parseInt(jobj.get("frequency").toString());
                String userid=jobj.get("userid").toString();
                double absence = 0;
                if(jobj.has("absence")){
                	absence = Double.parseDouble(jobj.get("absence").toString());
                }
                
                double incometax =getIncomTaxAmount(request, userid, enddate, frequency);
                HashMap<String, Double> map = getLeaveDeductionAmount(request, userid, startdate, enddate, frequency, absence, incometax);
	            if(map!=null){
	            	jobj.put("netsalary", map.get("netsalary"));
		            jobj.put("earning", map.get("earning"));
		            jobj.put("deduction", map.get("deduction"));
		            jobj.put("tax", map.get("tax"));
		            jobj.put("otherRemuneration", map.get("otherRemuneration"));
	            }
	            jobj.put("incometax", incometax);
	            jobj.put("createdon", new Date());
	            jobj.put("generatedon", new Date());
	
                hrmsPayrollDAOObj.calculatePayroll(jobj);
            }
            
            jobj.put("validationJson", validationJson);
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", 0);
            jobj1.put("data", jobj);
            if(countryLevelValid){
            	txnManager.commit(status);
            }else{
            	txnManager.rollback(status);
            }
            return new ModelAndView("jsonView", "model", jobj1.toString());
        } catch(ArithmeticException e){
			e.printStackTrace();
			txnManager.rollback(status);
			Logger.getLogger(hrmsPayrollController.class.getName()).log(Level.WARNING, "Working days should not be zero");
			return new ModelAndView("jsonView", "model", "failed");
		} catch (Exception se) {
        	se.printStackTrace();
            txnManager.rollback(status);
            return new ModelAndView("jsonView", "model", "failed");
        }
    }
    // Function Used : Generate, Authorize and Process Payroll Process
    // Used for updating salary status by passing historyid
    public ModelAndView updatePayrollHistory(HttpServletRequest request, HttpServletResponse response) {
        JSONObject msg = new JSONObject();
        JSONObject jobj = new JSONObject();
        KwlReturnObject result = null;
        boolean success=false;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            
            String[] historyid = request.getParameterValues("historyid");
            int statusid = Integer.parseInt(request.getParameter("statusid"));
            String comment = request.getParameter("comment");
            if(StringUtil.isNullOrEmpty(comment)){
                comment = " ";
            }
            
            success = hrmsPayrollDAOObj.editSalaryStatus(historyid, statusid, comment);
            
            //@@ProfileHandler.insertAuditLog(session, auditID, "User <b>"+fullName+"</b> "+auditMsg+" <b>"+ids.length+"</b> master item(s) of master group <b>"+groupName+"</b>", request);
            msg.put("success", success);
            jobj.put("valid", true);
            jobj.put("data", msg.toString());
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView processPayrollHistory(HttpServletRequest request, HttpServletResponse response) {
        JSONObject msg = new JSONObject();
        JSONObject jobj = new JSONObject();
        KwlReturnObject result = null;
        boolean success=false;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            String[] historyid = request.getParameterValues("historyid");
            String[] userids = request.getParameterValues("userids");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date enddate = df.parse(request.getParameter("enddate"));
            int frequency = Integer.parseInt(request.getParameter("frequency"));
            
            int processstatus = Integer.parseInt(request.getParameter("processstatus"));
            String paydate = request.getParameter("paydate");
            String payspecification = request.getParameter("payspecification");
            String sliptxt1 = request.getParameter("sliptxt1");
            String sliptxt2 = request.getParameter("sliptxt2");
            String sliptxt3 = request.getParameter("sliptxt3");
            
            for (int j = 0; j < historyid.length; j++) {
                requestParams.clear();
                if(!StringUtil.isNullOrEmpty(historyid[j])){
                    requestParams.put("historyid", historyid[j]);
                    requestParams.put("processstatus", processstatus);
                    requestParams.put("salarystatus", processstatus);
                    requestParams.put("paydate", paydate);
                    requestParams.put("payspecification", payspecification);
                    requestParams.put("sliptxt1", sliptxt1);
                    requestParams.put("sliptxt2", sliptxt2);
                    requestParams.put("sliptxt3", sliptxt3);
                    requestParams.put("sliptxt3", sliptxt3);
                    result = hrmsPayrollDAOObj.updatePayrollHistory(requestParams);
                    if (result.isSuccessFlag()) {
                        msg.put("data", "Changes done successfully.");
                        success=true;
                    } else {
                        success=false;
                    }
                }else {
                    success=true;
                }

                if(processstatus==6){

                    afterSalaryProcess(request,userids[j], enddate, historyid[j],frequency);

                }
                
            }
            
            
            //@@ProfileHandler.insertAuditLog(session, auditID, "User <b>"+fullName+"</b> "+auditMsg+" <b>"+ids.length+"</b> master item(s) of master group <b>"+groupName+"</b>", request);
            msg.put("success", success);
            jobj.put("valid", true);
            jobj.put("success", true);
            jobj.put("data", msg.toString());
            txnManager.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    @SuppressWarnings("finally")
	public ModelAndView getSalaryComponents(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj1 = new JSONObject();
        JSONArray jsAray = new JSONArray();
        JSONObject jsonData = new JSONObject();
        int type =0;
        String dataType[] = {"EC", "Wage", "Deduc", "Tax"};
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date enddate = df.parse(request.getParameter("enddate"));
            String userid = request.getParameter("userid");
            type = Integer.parseInt(request.getParameter("type"));
            int frequency = Integer.parseInt(request.getParameter("frequency"));
            Integer otherRemuneration = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("otherRemuneration"))){
            	otherRemuneration = Integer.parseInt(request.getParameter("otherRemuneration"));
            }
            double amount =0;
            List<ComponentResourceMappingHistory> list = hrmsPayrollDAOObj.getSalaryDetails(userid, enddate, frequency, type, otherRemuneration);
            for(ComponentResourceMappingHistory crm : list) {
                amount += crm.getAmount();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", crm.getId());
                jsonObject.put("type", crm.getComponent().getDescription());
                jsonObject.put("amount", crm.getAmount());
                if(crm.getComponent()!=null && crm.getComponent().getComputeon()!=null)
                	jsonObject.put("computeon", crm.getComponent().getComputeon().getDescription());
                else
                	jsonObject.put("computeon", "-");
                jsAray.put(jsonObject);
            }
            
            String historyid = request.getParameter("historyid");
            if(!StringUtil.isNullOrEmpty(historyid)){
	            PayrollHistory history = (PayrollHistory) kwlCommonTablesDAOObj.getObject("masterDB.PayrollHistory", historyid);
	            if(history!=null){
		            //###########For Income Tax START#############
		            if(type==3){
		            	if(history.getIncometaxAmount()>0){
		            		JSONObject jsonObject = new JSONObject();
		            		jsonObject.put("id", 0);
		            		jsonObject.put("type", "Income Tax");
		            		jsonObject.put("amount", history.getIncometaxAmount());
		            		jsonObject.put("computeon", "-");
		            		jsAray.put(jsonObject);//jsAray.append(dataType[type], 
		            	}
		            }
		            //###########For Income Tax END#############
		            
		            if(type==2 && history.getUnpaidleavesAmount()>0){//Deduction for Unpaid leaves
		            	JSONObject jsonObject = new JSONObject();
		            	jsonObject.put("id", 0);
		                jsonObject.put("type", MessageSourceProxy.getMessage("hrms.payroll.Unpaidleaves" ,null,"Unpaid Leaves", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0))+"("+history.getUnpaidleaves()+")");
		                jsonObject.put("amount", history.getUnpaidleavesAmount());
		                jsonObject.put("computeon", "-");
		                jsAray.put(jsonObject);
		            }
	            }
	            if(type==2){
		            Map<String, Double> map = exportSalarySlipService.getIncomTaxBenefits(request, userid, enddate, frequency, history);
		            if(map!=null && map.get("epf")!=null && map.get("epf")>0){
		            	JSONObject jsonObject = new JSONObject();
	            		jsonObject.put("id", 0);
	            		jsonObject.put("type", "EPF");
	            		jsonObject.put("amount", map.get("epf"));
	            		jsonObject.put("computeon", "-");
	            		jsAray.put(jsonObject);
		            }
	            }
            }
            
            
            jsonData.put(dataType[type], jsAray);
            jsonData.put("success", true);
            jobj1.put("valid", true);
            jobj1.put("totalcount", jsAray.length());
            jobj1.put("data", jsonData);
        } catch (Exception se) {
        	jsonData.put("success", true);
        	jsonData.put(dataType[type], jsAray);
            jobj1.put("valid", true);
            jobj1.put("data", jsonData);
            se.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }
    //  Function Used : Genrate Payroll Process
    //1 ) For Previous Salary Component assigning in Generate Payroll Process
    //2 ) For Payroll History Report in Generate Payroll Process to show generated salary component with amount etc.
    @SuppressWarnings("finally")
	public ModelAndView getSalaryComponentsForEmployee(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj1 = new JSONObject();
        JSONObject jsAray = new JSONObject();
        
        try {
            double amount =0;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date startdate = df.parse(request.getParameter("startdate"));
            Date enddate = df.parse(request.getParameter("enddate"));
            String userid = request.getParameter("userid");
            boolean showAll = Boolean.parseBoolean(request.getParameter("showAll"));
            String generatePayrollLink= request.getParameter("generatePayrollLink");

            if(!StringUtil.isNullOrEmpty(generatePayrollLink)){
                String frequency = request.getParameter("frequency");
                
                int daysCount = getDayDiffrence(frequency, startdate);
                enddate = getPreviosEndDate(startdate);
                startdate = getPreviosStartDate(startdate, daysCount);
                
            }
            
            List<ComponentResourceMappingHistory> list = hrmsPayrollDAOObj.getSalaryComponentForEmployee(userid, startdate, enddate,showAll);
            for(ComponentResourceMappingHistory crm : list) {
                amount += crm.getAmount();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("compid", crm.getComponent().getCompid());
                jsonObject.put("code", crm.getComponent().getCode());
                jsonObject.put("desc", crm.getComponent().getDescription());
                jsonObject.put("amount", crm.getAmount());
                jsonObject.put("sdate", crm.getPeriodstartdate());
                jsonObject.put("edate", crm.getPeriodenddate());
                jsonObject.put("isblock", crm.getComponent().isIsblock());
                
                jsAray.append("data", jsonObject);
            }
            if(list.size()==0){
            	jsAray.put("data", new JSONArray());
            }
            jsAray.put("success", true);
            jsAray.put("totalcount", list.size());
            jobj1.put("valid", true);
            
            jobj1.put("data", jsAray);
        } catch (Exception se) {
            jsAray.put("success", false);
            jobj1.put("valid", true);
            jobj1.put("data", jsAray);
            se.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    @SuppressWarnings("finally")
	public ModelAndView editResorcePayrollData(HttpServletRequest request, HttpServletResponse response) {
        boolean success = false;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
    	try {

            success = addEditResorcePayrollData(request);
            txnManager.commit(status);

        } catch (Exception e) {
        	txnManager.rollback(status);
            e.printStackTrace();
        } finally {
        	return new ModelAndView("jsonView", "model", success+"");
        }
    }

    public boolean addEditResorcePayrollData(HttpServletRequest request) {
        boolean success = false;
        double leaveDeductionAmount = 0.0;
    	try {
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    		double absence = 0;
    		String userid = request.getParameter("userid");
        	Date startdate = sdf.parse(request.getParameter("paycyclestartdate"));
        	Date enddate = sdf.parse(request.getParameter("paycycleenddate"));
        	int frequency = Integer.parseInt(request.getParameter("frequency"));
    		if(!StringUtil.isNullOrEmpty(request.getParameter("absence"))){
    			absence = Double.parseDouble(request.getParameter("absence"));
    		}
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("id", request.getParameter("id"));
            requestParams.put("fullname", request.getParameter("fullname"));
            requestParams.put("costcenter", request.getParameter("costcenter"));
            requestParams.put("costcenterid", request.getParameter("costcenter"));
            requestParams.put("jobtitle", request.getParameter("jobtitle"));
            requestParams.put("contract", request.getParameter("contract"));
            requestParams.put("absence", request.getParameter("absence"));
            requestParams.put("actual", request.getParameter("actual"));
            requestParams.put("paycyclestartdate", request.getParameter("paycyclestartdate"));
            requestParams.put("paycycleenddate", request.getParameter("paycycleenddate"));
            requestParams.put("difference", request.getParameter("difference"));
            requestParams.put("status", request.getParameter("status"));
            requestParams.put("historyid", request.getParameter("historyid"));
            requestParams.put("userid", userid);
            requestParams.put("frequency", frequency);
            
            double incometax = getIncomTaxAmount(request, userid, enddate, frequency);
            if(absence>0){
                HashMap<String,Double> map = new HashMap<String, Double>();
                map = getLeaveDeductionAmount(request, userid, startdate, enddate, frequency, absence, incometax);
                leaveDeductionAmount= map.get("leavededuction");
                double netsalary= map.get("netsalary");
                double deduction= map.get("deduction");
                double tax= map.get("tax");
                double otherRemuneration= map.get("otherRemuneration");
                double earning= map.get("earning");

                requestParams.put("netsalary", netsalary);
                requestParams.put("deduction", deduction);
                requestParams.put("tax", tax);
                requestParams.put("otherRemuneration", otherRemuneration);
                requestParams.put("earning", earning);
            }
            requestParams.put("incometax", incometax);
            requestParams.put("leaveDeductionAmount", leaveDeductionAmount);
            success = hrmsPayrollDAOObj.editResorcePayrollData(requestParams);
           
        } catch (Exception e) {
        	success=false;
            e.printStackTrace();
        } 
        return success;
    }
    
    public HashMap<String,Double> getLeaveDeductionAmount(HttpServletRequest request, String userid, Date startdate, Date enddate, int frequency, double absence, double incometax){
    	double leaveDeductionAmount = 0.0;
        HashMap<String,Double> map = new HashMap<String,Double>();
    	try{
        	double earning =0.0;
            double deduction =0.0;
            double tax =0.0;
            double otherRemuneration =0.0;
            double netsalary =0.0;
        	Componentmaster componentmaster = null;
        	ComponentResourceMappingHistory componentResourceMappingHistory = null;
        	List<ComponentResourceMappingHistory> componentResourceMappingHistories = hrmsPayrollDAOObj.getAssignedComponentsToResource(userid, enddate, frequency);
        	Iterator<ComponentResourceMappingHistory> iterator = componentResourceMappingHistories.iterator();
        	while (iterator.hasNext()) {
                componentResourceMappingHistory = iterator.next();
        		componentmaster = componentResourceMappingHistory.getComponent();
                if(componentmaster.getSubtype().getComponenttype()==1){
                    earning=earning+componentResourceMappingHistory.getAmount();
                }else if(componentmaster.getSubtype().getComponenttype()==2){//Deduction
                    deduction=deduction+componentResourceMappingHistory.getAmount();
                }else if(componentmaster.getSubtype().getComponenttype()==3){//Tax
                    tax=tax+componentResourceMappingHistory.getAmount();
                }else if(componentmaster.getSubtype().getComponenttype()==4){//Other Remuneration
                	otherRemuneration=otherRemuneration+componentResourceMappingHistory.getAmount();
                }
            }
        	leaveDeductionAmount = absence*(earning/new HrmsCommonPayroll().getTotalNumberOfWorkingDays(frequency, enddate));
        	Map<String, Double> taxMap = exportSalarySlipService.getIncomTaxBenefits(request, userid, enddate, frequency, null);
            double currentEPF = 0;
            if(taxMap!=null && taxMap.get("epf")!=null){
            	currentEPF = taxMap.get("epf");
            }
            double totalDeduction= leaveDeductionAmount+deduction+currentEPF;
            netsalary= earning+otherRemuneration-totalDeduction-tax-incometax;

            map.put("netsalary", netsalary);
            map.put("deduction", totalDeduction);
            map.put("tax", tax);
            map.put("otherRemuneration", otherRemuneration);
            map.put("leavededuction", leaveDeductionAmount);
            map.put("earning", earning);
            map.put("epf", currentEPF);
            
    	}catch(ArithmeticException e){
			e.printStackTrace();
			Logger.getLogger(hrmsPayrollController.class.getName()).log(Level.WARNING, "Working days should not be zero");
		}catch(Exception e){
    		e.printStackTrace();
    	}
    	return map;
    }

    public ModelAndView deleteResourcePayrollData(HttpServletRequest request, HttpServletResponse response) {
       JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	Date startdate = sdf.parse(request.getParameter("startdate"));
        	Date enddate = sdf.parse(request.getParameter("enddate"));
        	int frequency = Integer.parseInt(request.getParameter("frequency"));

            String[] empids = request.getParameterValues("empids");
            String deleteComponentResourceMapping = request.getParameter("deleteComponentResourceMapping");
            StringBuffer userList = new StringBuffer();
            for(int i=0; i<empids.length; i++){
                userList.append("'").append(empids[i]).append("'");
                if(i!=(empids.length-1)){
                    userList.append(",");
                }
            }

            hrmsPayrollDAOObj.deleteGeneratePayrollData(userList, startdate, enddate, frequency);

            if(Boolean.parseBoolean(deleteComponentResourceMapping)){
               deleteComponentOfResource(userList, startdate, enddate, frequency);
            }
            
            jobj.put("valid", true);
            jobj.put("data", "");
            txnManager.commit(status);
        }catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView","model",jobj.toString());
    }

    public boolean deleteComponentOfResource(StringBuffer userList, Date startdate, Date enddate, int frequency) {
        boolean success = false;
        try {
            hrmsPayrollDAOObj.deleteComponentOfResource(userList, startdate, enddate, frequency);
            success = true;
        } catch (Exception e) {
        	success=false;
            e.printStackTrace();
        } 
        return success;
    }

  public ModelAndView getGeneratedPayrollList(HttpServletRequest request, HttpServletResponse response) {

        KwlReturnObject result = null;
        KwlReturnObject users = null;
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        int count1=0;
        int count2=0;
        List lst= null;
        try {
            String companyid =sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String periodStartDate = request.getParameter("sdate");
            Date startdate = sdf.parse(periodStartDate.toString());
            String periodEndDate = request.getParameter("edate");
            Date enddate = sdf.parse(periodEndDate.toString());
            Integer frequency = Integer.parseInt(request.getParameter("frequency"));

            int status = Integer.parseInt(request.getParameter("status"));
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            
            requestParams.put("startdate",startdate);
            requestParams.put("enddate",enddate);
            requestParams.put("companyid",companyid);
            requestParams.put("frequency",frequency);

            String searchText = request.getParameter("ss");
            if(!StringUtil.isNullOrEmpty(searchText)){
                requestParams.put("ss",searchText);
            }
            StringBuffer userList = new StringBuffer();
            if(status>=0){
                if(status!=0){
                    requestParams.put("status",status);
                    requestParams.put("start", start);
                    requestParams.put("limit", limit);
                }
                result = hrmsPayrollDAOObj.getGeneratedPayrollList(requestParams);
                lst = result.getEntityList();

                if(result.getRecordTotalCount()>0){
                    jobj = getUserGeneratedPayrollList(jobj, lst, companyid, frequency.toString(), startdate,  enddate,requestParams);
                    count1= result.getRecordTotalCount();

                }

            }
                

            if(status==0){
                
                if(jobj.has("users") && !StringUtil.isNullOrEmpty(jobj.getString("users"))){
                    String userlst = jobj.getString("users").substring(2, (jobj.getString("users").length()-2));
                    userList = new StringBuffer(userlst);
                }
                jobj = new JSONObject();
                users = hrmsPayrollDAOObj.getPayrollUserList(userList,companyid,searchText,frequency,start,limit);
                lst = users.getEntityList();
                count1=0;
                if(users.getRecordTotalCount()>0){

                    jobj = getUserNotGeneratedPayrollList(jobj, lst, companyid, frequency.toString(), startdate,  enddate,requestParams);
                    count2= users.getRecordTotalCount();
               
                }

            }
            
            if(count1==0 && count2==0){
                jobj.put("data", new JSONArray());
            }
            jobj.put("success", true);
            
            jobj1.put("valid", true);
            jobj.put("totalcount", count1+count2);
            jobj1.put("data", jobj);
            
        } catch (Exception se) {
            logger.warn(se.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public double  getPreviousSalaryForEmployee (String employeeid, Date startdate, Date enddate, int daysCount, String companyid, String frequency){
        double prevSalary=0;
        KwlReturnObject result=null;
        try{

            Date prevStartDate= getPreviosStartDate(startdate, daysCount);
            Date prevEndDate = getPreviosEndDate(startdate);

            HashMap requestparams = new HashMap();
            requestparams.put("userid", employeeid);
            requestparams.put("startdate", prevStartDate);
            requestparams.put("enddate", prevEndDate);
            requestparams.put("companyid",companyid);
            requestparams.put("frequency",frequency);

            result = hrmsPayrollDAOObj.getGeneratedPayrollList(requestparams);
            if(result.getRecordTotalCount()>0){
                List lst = result.getEntityList();
                for(int i=0; i<lst.size();i++){
                    PayrollHistory ph = (PayrollHistory) lst.get(i);
                    if(ph.getNet()!=null){
                    	prevSalary = ph.getNet();
                    }
                    
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }


        return prevSalary;
    }

    public Date getPreviosStartDate (Date startdate, int daysCount){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startdate);
        calendar.add(Calendar.DATE, -daysCount);

        Date prevStartDate= calendar.getTime();

        return prevStartDate;

    }

    public Date getPreviosEndDate (Date startdate){

        return new Date(startdate.getTime()-86400000); // One day time in milliseconds

    }

    public int getDayDiffrence(String frequency, Date startdate){
    int daydiff =0;
    if(StringUtil.equal(frequency, "0")){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startdate);
        calendar.add(Calendar.MONTH, -1);

        daydiff= calendar.getActualMaximum(Calendar.DATE);

    }else if(StringUtil.equal(frequency, "1")){
        daydiff =7;
    }else if(StringUtil.equal(frequency, "2")){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startdate);
        calendar.add(Calendar.MONTH, -1);

        daydiff= calendar.getActualMaximum(Calendar.DATE);
        daydiff = daydiff/2;
    }

    return  daydiff;

    }

    @SuppressWarnings("finally")
	public ModelAndView getYearlySalaryComponentsForEmployee(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj1 = new JSONObject();
        JSONObject jsAray = new JSONObject();
        KwlReturnObject result = null;

        try {
            
            String userid=request.getParameter("userid");
            String frequency= request.getParameter("frequency");
            int year= Integer.parseInt(request.getParameter("year"));
            String searchString = request.getParameter("ss");
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            
            String companyid = sessionHandlerImplObj.getCompanyid(request);

            result = getComponentListByFrequency(companyid, frequency, searchString, start, limit);

            int cnt = result.getRecordTotalCount();
            if(cnt>0){
                List tabledata = result.getEntityList();
                for (int i = 0; i < tabledata.size(); i++) {

                    JSONObject jsonObject = new JSONObject();

                    Componentmaster log = (Componentmaster) tabledata.get(i);

                    jsonObject.put("compid", log.getCompid());
                    jsonObject.put("code", log.getCode());

                    String componentid=log.getCompid();

                    if(StringUtil.equal(frequency, "0")){

                        jsonObject = getComponentAmountByUser(jsonObject,userid,componentid,year);
                    
                    } else if(StringUtil.equal(frequency, "1")){

                        jsonObject = getWeeklyComponentAmountByUser(jsonObject,userid,componentid,year);
                    
                    } else if(StringUtil.equal(frequency, "2")){

                        jsonObject = getTwiceInMonthComponentAmountByUser(jsonObject,userid,componentid,year);
                    }

                    jsAray.append("data", jsonObject);
                    
                    
                }
            }
            if(cnt==0){
            	jsAray.put("data", new JSONArray());
            }
            jsAray.put("success", true);
            jsAray.put("totalcount", cnt);
            jobj1.put("valid", true);

            jobj1.put("data", jsAray);
        } catch (Exception se) {
            jsAray.put("success", false);
            jobj1.put("valid", true);
            jobj1.put("data", jsAray);
            se.printStackTrace();
        } finally {
            return new ModelAndView("jsonView","model",jobj1.toString());
        }
    }

    public KwlReturnObject getComponentListByFrequency(String companyid, String frequency, String searchString, String start, String limit) {
        KwlReturnObject result = null;
        try {

            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            filter_names.add("company.companyID");
            filter_values.add(companyid);
            filter_names.add("frequency");
            filter_values.add(Integer.parseInt(frequency));
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            
            if(!StringUtil.isNullOrEmpty(searchString)){
                requestParams.put("ss", searchString);
                requestParams.put("searchcol", new String[]{"code"});
            }
            if(!StringUtil.isNullOrEmpty(start) && !StringUtil.isNullOrEmpty(limit)){
                requestParams.put("start", start);
                requestParams.put("limit", limit);
                requestParams.put("allflag", false);
            }
            result = hrmsPayrollDAOObj.getPaycomponent_date(requestParams);

            
        }catch (Exception ex) {
            ex.printStackTrace();
            
        }
        return result;
    }


    public JSONObject getComponentAmountByUser(JSONObject jsonObject, String userid, String componentid, int year) {
        
        try {

            Calendar yearcal = Calendar.getInstance();
            yearcal.set(year, 00, 01);
            Date startdate = yearcal.getTime();
            yearcal.set(year, 11, 31);
            Date enddate = yearcal.getTime();
            
            List<ComponentResourceMappingHistory> list = hrmsPayrollDAOObj.getYearlySalaryComponentsForEmployee(userid,componentid,startdate, enddate);

                for(ComponentResourceMappingHistory crm : list) {

                    Date date = crm.getPeriodenddate();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int month=cal.get(Calendar.MONTH);
                    String monthName="jan";
                    if(month==0){

                        monthName="jan";
                        jsonObject.put(monthName, crm.getAmount());
                    } else if(month==1){

                        monthName="feb";
                        jsonObject.put(monthName, crm.getAmount());
                    } else if(month==2){

                        monthName="mar";
                        jsonObject.put(monthName, crm.getAmount());
                    } else if(month==3){

                        monthName="apr";
                        jsonObject.put(monthName, crm.getAmount());
                    } else if(month==4){

                        monthName="may";
                        jsonObject.put(monthName, crm.getAmount());
                    } else if(month==5){

                        monthName="jun";
                        jsonObject.put(monthName, crm.getAmount());
                    } else if(month==6){

                        monthName="jul";
                        jsonObject.put(monthName, crm.getAmount());
                    } else if(month==7){

                        monthName="aug";
                        jsonObject.put(monthName, crm.getAmount());
                    } else if(month==8){

                        monthName="sep";
                        jsonObject.put(monthName, crm.getAmount());
                    } else if(month==9){

                        monthName="oct";
                        jsonObject.put(monthName, crm.getAmount());
                    } else if(month==10){

                        monthName="nov";
                        jsonObject.put(monthName, crm.getAmount());
                    } else if(month==11){

                        monthName="dec";
                        jsonObject.put(monthName, crm.getAmount());
                    }

                }


        }catch (Exception ex) {
            ex.printStackTrace();

        }
        return jsonObject;
    }

    public JSONObject getTwiceInMonthComponentAmountByUser(JSONObject jsonObject, String userid, String componentid, int year) {

        try {

            Calendar yearcal = Calendar.getInstance();
            yearcal.set(year, 00, 01);
            Date startdate = yearcal.getTime();
            yearcal.set(year, 11, 31);
            Date enddate = yearcal.getTime();

            List<ComponentResourceMappingHistory> list = hrmsPayrollDAOObj.getYearlySalaryComponentsForEmployee(userid,componentid,startdate, enddate);

                for(ComponentResourceMappingHistory crm : list) {

                    Date date = crm.getPeriodenddate();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int month=cal.get(Calendar.MONTH);
                    int dat = cal.get(Calendar.DAY_OF_MONTH);
                    String monthName="p1";
                    if(month==0){
                        if(dat>15){
                            monthName="p2";
                            jsonObject.put(monthName, crm.getAmount());
                        } else {
                            monthName="p1";
                            jsonObject.put(monthName, crm.getAmount());
                        }
                    } else if(month==1){
                        if(dat>15){
                            monthName="p4";
                            jsonObject.put(monthName, crm.getAmount());
                        } else {
                            monthName="p3";
                            jsonObject.put(monthName, crm.getAmount());
                        }
                    } else if(month==2){
                        if(dat>15){
                            monthName="p6";
                            jsonObject.put(monthName, crm.getAmount());
                        } else {
                            monthName="p5";
                            jsonObject.put(monthName, crm.getAmount());
                        }
                        
                    } else if(month==3){

                        if(dat>15){
                            monthName="p8";
                            jsonObject.put(monthName, crm.getAmount());
                        } else {
                            monthName="p7";
                            jsonObject.put(monthName, crm.getAmount());
                        }
                        
                    } else if(month==4){
                        if(dat>15){
                            monthName="p10";
                            jsonObject.put(monthName, crm.getAmount());
                        } else {
                            monthName="p9";
                            jsonObject.put(monthName, crm.getAmount());
                        }
                    
                    } else if(month==5){
                        if(dat>15){
                            monthName="p12";
                            jsonObject.put(monthName, crm.getAmount());
                        } else {
                            monthName="p11";
                            jsonObject.put(monthName, crm.getAmount());
                        }
                        
                    } else if(month==6){

                        if(dat>15){
                            monthName="p14";
                            jsonObject.put(monthName, crm.getAmount());
                        } else {
                            monthName="p13";
                            jsonObject.put(monthName, crm.getAmount());
                        }
                        
                    } else if(month==7){

                        if(dat>15){
                            monthName="p16";
                            jsonObject.put(monthName, crm.getAmount());
                        } else {
                            monthName="p15";
                            jsonObject.put(monthName, crm.getAmount());
                        }
                        
                    } else if(month==8){

                        if(dat>15){
                            monthName="p18";
                            jsonObject.put(monthName, crm.getAmount());
                        } else {
                            monthName="p17";
                            jsonObject.put(monthName, crm.getAmount());
                        }
                        
                    } else if(month==9){

                        if(dat>15){
                            monthName="p20";
                            jsonObject.put(monthName, crm.getAmount());
                        } else {
                            monthName="p19";
                            jsonObject.put(monthName, crm.getAmount());
                        }
                        
                    } else if(month==10){

                        if(dat>15){
                            monthName="p22";
                            jsonObject.put(monthName, crm.getAmount());
                        } else {
                            monthName="p21";
                            jsonObject.put(monthName, crm.getAmount());
                        }
                        
                    } else if(month==11){

                        if(dat>15){
                            monthName="p24";
                            jsonObject.put(monthName, crm.getAmount());
                        } else {
                            monthName="p23";
                            jsonObject.put(monthName, crm.getAmount());
                        }
                        
                    }

                }


        }catch (Exception ex) {
            ex.printStackTrace();

        }
        return jsonObject;
    }

    public JSONObject getWeeklyComponentAmountByUser(JSONObject jsonObject, String userid, String componentid, int year) {

        try {

            Calendar yearcal = Calendar.getInstance();
            yearcal.set(year, 00, 01);
            Date startdate = yearcal.getTime();
            yearcal.set(year, 11, 31);
            Date enddate = yearcal.getTime();

            List<ComponentResourceMappingHistory> list = hrmsPayrollDAOObj.getYearlySalaryComponentsForEmployee(userid,componentid,startdate, enddate);

            for(ComponentResourceMappingHistory crm : list) {

                Date date = crm.getPeriodenddate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int dat = cal.get(Calendar.DAY_OF_YEAR);
                int i= (dat-1)/7;

                jsonObject.put("p"+(i+1), crm.getAmount());

            }


        }catch (Exception ex) {
            ex.printStackTrace();

        }
        return jsonObject;
    }
    
    public ModelAndView getPayslips(HttpServletRequest request, HttpServletResponse response) {
    	JSONObject jobj = new JSONObject();
    	JSONArray jsonArray = new JSONArray();
        JSONObject jobj1 = new JSONObject();
        try {
            String userid = request.getParameter("userid");
            if(StringUtil.isNullOrEmpty(userid)){
            	userid = sessionHandlerImplObj.getUserid(request);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
            DateFormat df = new SimpleDateFormat("MMMM");
            User u = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", userid);
            Useraccount useraccount = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", userid);
            JSONObject jobjinloop = null;
            List<PayrollHistory>  list = hrmsPayrollDAOObj.getPayslips(userid, 6);
            for (PayrollHistory payrollHistory: list) {
            	jobjinloop = new JSONObject();
            	jobjinloop.put("historyid", payrollHistory.getHistoryid());
            	jobjinloop.put("userid", userid);
            	jobjinloop.put("accno", useraccount!=null?useraccount.getAccno():"");
            	jobjinloop.put("startdate", sdf.format(payrollHistory.getPaycyclestartdate()));
            	jobjinloop.put("enddate", sdf.format(payrollHistory.getPaycycleenddate()));
            	jobjinloop.put("frequency", payrollHistory.getFrequency());
            	jobjinloop.put("gross",(payrollHistory.getEarning()+payrollHistory.getOtherRemuneration()));
            	jobjinloop.put("tax", (payrollHistory.getTax()+payrollHistory.getIncometaxAmount()));
            	jobjinloop.put("deduction", payrollHistory.getDeduction());
            	jobjinloop.put("net", payrollHistory.getNet());
            	jobjinloop.put("design", payrollHistory.getJobTitle()!=null?payrollHistory.getJobTitle().getValue():"");
            	jobjinloop.put("name", u.getFirstName()+" "+(u.getLastName()!=null?u.getLastName():""));
            	jobjinloop.put("unpaidleavesAmount", payrollHistory.getUnpaidleavesAmount());
            	jobjinloop.put("incomeTax", payrollHistory.getIncometaxAmount());
            	jobjinloop.put("absence", payrollHistory.getUnpaidleaves());
                jobjinloop.put("month", MessageSourceProxy.getMessage("hrms."+df.format(payrollHistory.getPaycyclestartdate()) ,null,df.format(payrollHistory.getPaycyclestartdate()), LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
            	jsonArray.put(jobjinloop);
            }
            jobj.put("data", jsonArray);
            if(list!=null){
            	jobj.put("count", list.size());
            }else{
            	jobj.put("count", 0);
            }
            jobj.put("success", true);
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

     public JSONObject getUserGeneratedPayrollList(JSONObject jobj, List lst, String companyid, String frequency,  Date startdate, Date enddate,HashMap<String, Object> requestParams) throws JSONException {
        StringBuffer userList = new StringBuffer();
        try {

            for (int i = 0; i < lst.size(); i++) {
                    masterDB.PayrollHistory ph = (masterDB.PayrollHistory) lst.get(i);

                    Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", ph.getUser().getUserID());
                    Empprofile empprofile = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", ph.getUser().getUserID());

                    double currentSalary =ph.getNet()!=null?ph.getNet():0;
                    int dayDiff = getDayDiffrence(ph.getFrequency(),startdate);

                    double prevSalary = getPreviousSalaryForEmployee(ph.getUser().getUserID(), startdate, enddate, dayDiff, companyid, frequency);
                    double difference = currentSalary-prevSalary;

                    JSONObject jobjinloop = new JSONObject();
                    requestParams.clear();
                    requestParams.put("companyid",companyid);
                    requestParams.put("empid",ua.getEmployeeid());
                    if(ua.getEmployeeIdFormat()==null){
                        jobjinloop.put("employeeid", ua.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                    }else{
                        requestParams.put("standardEmpId", profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
                        requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                        jobjinloop.put("employeeid", profileHandlerDAOObj.getNewEmployeeIdFormat(requestParams));
                    }
                    jobjinloop.put("id", ph.getHistoryid());
                    jobjinloop.put("accountno", ua.getAccno()!=null?ua.getAccno():"");
                    jobjinloop.put("resource", ph.getUser().getUserID());
                    jobjinloop.put("fullname", ph.getName());
                    jobjinloop.put("costcenter", ph.getCostCenter()!=null?ph.getCostCenter().getId():"");
                    jobjinloop.put("costcentername", ph.getCostCenter()!=null?ph.getCostCenter().getName():"");
                    jobjinloop.put("jobtitle",ph.getJobTitle()!=null? ph.getJobTitle().getId():"");
                    jobjinloop.put("jobtitlename", ph.getJobTitle()!=null?ph.getJobTitle().getValue():"");
                    jobjinloop.put("contract", ph.getContracthours());
                    jobjinloop.put("absence", ph.getUnpaidleaves());
                    jobjinloop.put("unpaidleavesAmount", ph.getUnpaidleavesAmount());
                    jobjinloop.put("incomeTax", ph.getIncometaxAmount());
                    jobjinloop.put("actual", ph.getActualhours());
                    jobjinloop.put("employmentdate", empprofile!=null?empprofile.getJoindate():"");
                    jobjinloop.put("contractenddate", ph.getContractdate());
                    jobjinloop.put("difference", difference>0?1:0);
                    jobjinloop.put("status", ph.getSalarystatus());
                    jobjinloop.put("comment", ph.getComment());


                    userList.append("'").append(ph.getUser().getUserID()).append("'");
                    if(i!=(lst.size()-1)){
                        userList.append(",");
                    }

                    jobj.append("data", jobjinloop);
                    

                }


        }catch (Exception ex) {
            ex.printStackTrace();

        }
        jobj.append("users", userList);
        return jobj;
    }

     public JSONObject getUserNotGeneratedPayrollList(JSONObject jobj, List lst, String companyid, String frequency,  Date startdate, Date enddate,HashMap<String, Object> requestParams) {

        try {

            for (int i = 0; i < lst.size(); i++) {
                User usr = (User) lst.get(i);
                JSONObject jobjinloop = new JSONObject();
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", usr.getUserID());
                Empprofile empprofile = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", usr.getUserID());

                requestParams.clear();
                requestParams.put("companyid",companyid);
                requestParams.put("empid",ua.getEmployeeid());
                if(ua.getEmployeeIdFormat()==null){
                    jobjinloop.put("employeeid", ua.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0));
                }else{
                    requestParams.put("standardEmpId", profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
                    requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                    jobjinloop.put("employeeid", profileHandlerDAOObj.getNewEmployeeIdFormat(requestParams));
                }
                jobjinloop.put("id", 0);
                jobjinloop.put("accountno", ua.getAccno()!=null?ua.getAccno():"");
                jobjinloop.put("resource", usr.getUserID());
                jobjinloop.put("fullname", StringUtil.getFullName(usr));
                jobjinloop.put("costcenter", ua.getCostCenter()!=null?ua.getCostCenter().getId():"");
                jobjinloop.put("costcentername", ua.getCostCenter()!=null?ua.getCostCenter().getName():"");
                jobjinloop.put("jobtitle", ua.getDesignationid()!=null?ua.getDesignationid().getId():"");
                jobjinloop.put("jobtitlename", ua.getDesignationid()!=null?ua.getDesignationid().getValue():"");
                jobjinloop.put("contract", "");
                jobjinloop.put("absence", "");
                jobjinloop.put("unpaidleavesAmount", "");
                jobjinloop.put("actual", "");
                jobjinloop.put("employmentdate", empprofile!=null?empprofile.getJoindate():"");
                jobjinloop.put("contractenddate", "");
                jobjinloop.put("difference", 0);
                jobjinloop.put("status", 0);

                jobj.append("data", jobjinloop);
            }

        }catch (Exception ex) {
            ex.printStackTrace();

        }
        return jobj;
    }

   
    public double getIncomTaxAmount(HttpServletRequest request, String userid, Date financialDate, int frequency){

        double incometaxAmount =0;
        IncomeTax itax = exportSalarySlipService.getIncomTaxObj(request);
        if(itax!=null){ // Currently For Malaysia

            itax.setUserid(userid);
            itax.setFinancialDate(financialDate);
            itax.setFrequency(frequency);

            incometaxAmount = itax.getTax();

        } else { // Calculate Income Tax by Rule
                
            incometaxAmount = getIncomeTaxComponentRuleForUser(request, userid, financialDate, frequency);
        
        }

        return incometaxAmount;
    }
    
   
    
    public double  getIncomeTaxComponentRuleForUser (HttpServletRequest request, String userid, Date enddate, int frequency){
        
        double incomeTax=0;
        
        try{

            HrmsCommonPayroll hrmsCommonPayroll = new HrmsCommonPayroll();
            CompanyPreferences preferences = (CompanyPreferences) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.CompanyPreferences", sessionHandlerImplObj.getCompanyid(request));
            Date financialStartDate = hrmsCommonPayroll.getFinanacialYearStartDate(enddate, preferences.getFinancialmonth());
            Date financialEndDate = hrmsCommonPayroll.getFinanacialYearEndDate(financialStartDate, 11);
            
            List<PayrollHistory> histories = hrmsPayrollDAOObj.getGeneratedSalariesForUser(userid, financialStartDate, financialEndDate, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL, String.valueOf(frequency));
            
            int remainingMonth = hrmsCommonPayroll.getRemainingWorkingMonth(financialStartDate, enddate);
            
            // Get Taxable Income
            double incomeTaxAmount = getIncomeTaxAmountValue(histories, userid, enddate, frequency, remainingMonth);
            
            // Get Slab
            String componentid = getRuleComponentForUser(userid,enddate,frequency);
            
            // Apply Slab on Taxable Income
            incomeTax = getIncomeTaxValue(componentid, incomeTaxAmount);
            
            // calculate tax paid yet
            double taxpaid=0;
            for(PayrollHistory ph : histories){
            
                taxpaid += ph.getIncometaxAmount();
                taxpaid += ph.getTax();
            
            }
            
                
            incomeTax = (incomeTax-taxpaid) / (remainingMonth+1) ;
                
                
            // Get sum of amount of component which have tax type in current month salary
            double taxAmountCurrentMonth = getIncomeTaxValueForCurrentMonth(userid, enddate, frequency);
            
            // Deduct tax paying through tax type component for current salary
            incomeTax = incomeTax-taxAmountCurrentMonth;
            
            if(incomeTax < 0){
                incomeTax = 0;
            }
       
        }catch(Exception e){
            e.printStackTrace();
        }


        return incomeTax;
    }
    
    public String  getRuleComponentForUser (String userid, Date enddate, int frequency){
        
        List<ComponentResourceMappingHistory> componentResourceMappingList = null;
        String componentid= "";
        
        try{
            
            componentResourceMappingList= hrmsPayrollDAOObj.getIncomeTaxComponentCalculationForUser(userid,enddate, frequency);
            
            for(ComponentResourceMappingHistory crmh : componentResourceMappingList){
                
                componentid = crmh.getComponent().getCompid();
                
            }
            
            
        }catch(Exception e){
            e.printStackTrace();
        }

        return componentid;
    }
    
    public int  getSalaryCount (List<PayrollHistory> histories, int remainingMonth){
        
        int salaryCount=0;
        try{
            
            if(histories!=null){
                
                salaryCount +=histories.size();
            }
            
            salaryCount ++; // For Current Month
            
            salaryCount += remainingMonth;  // For Remaaining months
            
        }catch(Exception e){
            e.printStackTrace();
        }

        return salaryCount;
    }
    
    public double  getIncomeTaxValue (String componentid, double incomeTaxAmount){
        
        double incomeTax=0;
        List<ComponentRule> componentRuleList = null;
        try{
            
            componentRuleList= hrmsPayrollDAOObj.getIncomeTaxComponentRule(componentid);
            
            for(ComponentRule crule : componentRuleList){
            
                if(incomeTaxAmount >= crule.getLowerLimit() ){
                
                    double taxableamount = incomeTaxAmount - crule.getLowerLimit()+1;
                    taxableamount = (crule.getCoefficient()/100) * taxableamount;
                    incomeTax = incomeTax+taxableamount;
                    incomeTaxAmount = crule.getLowerLimit()-1;
                }
            
            }
            
            
        }catch(Exception e){
            e.printStackTrace();
        }

        return incomeTax;
    }
    
    public double  getIncomeTaxValueForCurrentMonth (String userid, Date enddate, int frequency){
        
        double taxAmountCurrentMonth=0;
        
        try{
            
            List<ComponentResourceMappingHistory> list = hrmsPayrollDAOObj.getSalaryDetails(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_TAX, null);
            
            for(ComponentResourceMappingHistory crmh : list){
            
                taxAmountCurrentMonth +=crmh.getAmount();
            
            }
            
            
        }catch(Exception e){
            e.printStackTrace();
        }

        return taxAmountCurrentMonth;
    }
    
    public double  getIncomeTaxAmountValue (List<PayrollHistory> histories, String userid, Date enddate, int frequency, int remainingMonth){
        double incomeTaxAmount=0;
        
        try{
            
            double earnings = getEarnings(histories, userid, enddate, frequency, remainingMonth);
            
            double deductions = getSavingsAndDeductions(histories, userid, enddate, frequency, remainingMonth);
            
            incomeTaxAmount = earnings - deductions;
            
            
        }catch(Exception e){
            e.printStackTrace();
        }

        return incomeTaxAmount;
    }
    
    public double  getEarnings (List<PayrollHistory> histories, String userid, Date enddate, int frequency, int remainingMonth){
        double previousMonthEarnings=0;
        double currentMonthEarnings=0;
        double remainingMonthEarnings=0;
        double earnings =0;
        try{
            
            for(PayrollHistory ph : histories){
            
                previousMonthEarnings += ph.getEarning();
            
            }
            
            List<ComponentResourceMappingHistory> list = hrmsPayrollDAOObj.getSalaryDetails(userid, enddate, frequency, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING, null);
            
            for(ComponentResourceMappingHistory history : list ) {
            
                currentMonthEarnings+=history.getAmount();
            } 
            
            remainingMonthEarnings = remainingMonth * currentMonthEarnings;
            
            earnings = previousMonthEarnings + currentMonthEarnings + remainingMonthEarnings;
            
       
        }catch(Exception e){
            e.printStackTrace();
        }


        return earnings;
    }
    
    public double  getSavingsAndDeductions (List<PayrollHistory> histories, String userid, Date enddate, int frequency, int remainingMonth){
        
        double deductions=0;
        try{
            
            
            Empprofile empprofile = (Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", userid);
            
            deductions = empprofile.getSavings();
             
            int count=0;
            
            List<ComponentResourceMapping> userComponents = hrmsPayrollDAOObj.getUserIncomeTaxComponent(userid, enddate);
            
            StringBuffer componentList = new StringBuffer();
            
            for(ComponentResourceMapping crm : userComponents) {
                Componentmaster componentmaster = crm.getComponent();

                if(componentmaster.getCompid()!=null){
                    componentList.append("'").append(componentmaster.getCompid()).append("'");
                }
                if(count !=(userComponents.size()-1)){
                    componentList.append(",");
                }
                count++;
            }
            
            
            List<UserTaxDeclaration> userTaxComponents = hrmsPayrollDAOObj.getUserIncomeTaxComponent(userid, componentList);
            
            for(UserTaxDeclaration tcomp: userTaxComponents){
            
                deductions += tcomp.getValue();
                
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }


        return deductions;
    }
    
    public boolean countryLevelValidation(HttpServletRequest request, String userid, Date financialDate, int frequency){
    	boolean valid = true;
    	try{
    		IncomeTax itax = exportSalarySlipService.getIncomTaxObj(request);
            if(itax!=null){
            	itax.setUserid(userid);
                itax.setFinancialDate(financialDate);
                itax.setFrequency(frequency);
                valid = itax.countryLevelValidation();
            }
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return valid;
    }

     

    public void afterSalaryProcess(HttpServletRequest request, String userid, Date financialDate, String historyid, int frequency){

        IncomeTax itax = exportSalarySlipService.getIncomTaxObj(request);
        if(itax!=null){

            itax.setUserid(userid);
            itax.setFinancialDate(financialDate);
            itax.setFrequency(frequency);

            itax.afterProcessSalary(historyid);

        }

    }

    public boolean checkForEmptyDeclarationForm (HttpServletRequest request, String userid, Date financialDate, int frequency){

        boolean form = true;

        IncomeTax itax = exportSalarySlipService.getIncomTaxObj(request);
        if(itax!=null){

            itax.setUserid(userid);
            itax.setFinancialDate(financialDate);
            itax.setFrequency(frequency);

            form = itax.checkForDeclarationFormFilled();

        } 

        return form;

    }

    public ModelAndView getCheckForFilledDeclarationForm(HttpServletRequest request, HttpServletResponse response) {


        try {
            JSONObject jobj=new JSONObject();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String userid = request.getParameter("userid");
            Date enddate = sdf.parse(request.getParameter("enddate"));
            int frequency = Integer.parseInt(request.getParameter("frequency"));

            boolean checkForDeclarationForm = checkForEmptyDeclarationForm(request, userid, enddate, frequency );
            
            jobj.put("checkForDeclarationForm", checkForDeclarationForm );
            
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", 0);
            jobj1.put("data", jobj);

            return new ModelAndView("jsonView", "model", jobj1.toString());
        } catch(ArithmeticException e){
			e.printStackTrace();
			return new ModelAndView("jsonView", "model", "failed");
		} catch (Exception se) {
        	se.printStackTrace();
            return new ModelAndView("jsonView", "model", "failed");
        }
    }
    
    public ModelAndView getIncomeTaxDataForDebug(HttpServletRequest request, HttpServletResponse response) {

        
        try {
            JSONObject jobj=new JSONObject();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String userid = request.getParameter("userid");
            Date enddate = sdf.parse(request.getParameter("enddate"));
            int frequency = Integer.parseInt(request.getParameter("frequency"));


            IncomeTax itax = exportSalarySlipService.getIncomTaxObj(request);
            if(itax!=null){

                itax.setUserid(userid);
                itax.setFinancialDate(enddate);
                itax.setFrequency(frequency);

                 jobj = itax.getALlDataForDebug();

            }
           
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", 0);
            jobj1.put("data", jobj);
            
            return new ModelAndView("jsonView", "model", jobj1.toString());
        } catch(ArithmeticException e){
			e.printStackTrace();
			return new ModelAndView("jsonView", "model", "failed");
		} catch (Exception se) {
        	se.printStackTrace();
            return new ModelAndView("jsonView", "model", "failed");
        }
    }
}
