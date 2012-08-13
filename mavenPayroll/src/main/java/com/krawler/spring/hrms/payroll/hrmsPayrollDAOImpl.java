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

//import com.krawler.common.admin.Company;
//import com.krawler.common.admin.User;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CostCenter;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
//import com.krawler.utils.json.base.JSONException;
//import com.krawler.utils.json.base.JSONObject;
//import java.util.Date;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.hrms.master.MasterData;
import com.krawler.spring.common.KwlReturnObject;
import java.io.Serializable;
import java.util.*;
import masterDB.*;
import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;


/**
 *
 * @author shs
 */

public class hrmsPayrollDAOImpl implements hrmsPayrollDAO{
    private HibernateTemplate hibernateTemplate;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

    public KwlReturnObject getPaycomponent_date(HashMap<String,Object> requestParams) {
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            if(requestParams.containsKey("primary")&&(Boolean)requestParams.get("primary")){
                hql = "from Componentmaster where compid=?";
                String cmptid = requestParams.get("compid").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{cmptid});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from Componentmaster ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = (ArrayList)requestParams.get("filter_names");
                value = (ArrayList)requestParams.get("filter_values");
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }
            if(requestParams.get("available")!=null){
                hql += "and isdefault=false and isblock=false and compid not in (select component from ComponentResourceMapping where user.userID ='"+requestParams.get("userid").toString()+"') ";
            }
            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = (ArrayList)requestParams.get("order_by");
                ordertype = (ArrayList)requestParams.get("order_type");
                hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
            success = false;
            ex.printStackTrace();
        } finally {
            return result;
        }
    }
    public KwlReturnObject addPaycomponent_date(HashMap<String, Object> requestParams) {
        String goalid = "";
        boolean success=false;
        List lst = new ArrayList();
        int method = -1;
        int count=0;
        try {
        	String companyid = requestParams.get("companyid").toString();
            Date sdate=null,edate=null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if(requestParams.get("sdate")!=null && !StringUtil.isNullOrEmpty(requestParams.get("sdate").toString())){
            	sdate =sdf.parse(requestParams.get("sdate").toString());
            }
            if(requestParams.get("edate")!=null && !StringUtil.isNullOrEmpty(requestParams.get("edate").toString())){
            	edate =sdf.parse(requestParams.get("edate").toString());
            }
            Componentmaster componentmaster = null;
            if(requestParams.get("compid").toString().equals("0")){
                 componentmaster = new Componentmaster();
            } else {
                componentmaster = (Componentmaster) hibernateTemplate.load(Componentmaster.class, requestParams.get("compid").toString());
            }
             
            if (requestParams.get("action").toString().equals("Add")) {
                componentmaster.setCode(requestParams.get("code").toString());
                componentmaster.setCompany((Company) hibernateTemplate.get(Company.class, companyid));
                componentmaster.setSdate(sdate);
                componentmaster.setEdate(edate);
                componentmaster.setDescription(requestParams.get("description").toString());
                componentmaster.setSubtype((MasterData) hibernateTemplate.get(MasterData.class, requestParams.get("subtype").toString()));
                componentmaster.setFrequency(Integer.parseInt(requestParams.get("frequency").toString()));
                componentmaster.setCostcenter((CostCenter) hibernateTemplate.get(CostCenter.class, requestParams.get("costcenter").toString()));
                componentmaster.setPaymentterm((MasterData) hibernateTemplate.get(MasterData.class, requestParams.get("paymentterm").toString()));
                if(requestParams.containsKey("method")) {
                	method = Integer.parseInt(requestParams.get("method").toString());
                    componentmaster.setMethod(method);
                }
                if(!StringUtil.isNullOrEmpty(requestParams.get("computeon").toString())) {
                    componentmaster.setComputeon((Componentmaster) hibernateTemplate.get(Componentmaster.class, requestParams.get("computeon").toString()));
                }else{
                	componentmaster.setComputeon(null);
                }
                componentmaster.setAmount(Double.parseDouble(requestParams.get("amount").toString()));
                componentmaster.setIsadjust(Boolean.parseBoolean(requestParams.get("isadjust").toString()));
                componentmaster.setIsblock(Boolean.parseBoolean(requestParams.get("isblock").toString()));
                componentmaster.setIstaxablecomponent(Boolean.parseBoolean(requestParams.get("istaxablecomponent").toString()));
                componentmaster.setIsdefault(Boolean.parseBoolean(requestParams.get("isdefault").toString()));
                hibernateTemplate.save(componentmaster);
                
                HibernateUtil.executeUpdate(hibernateTemplate, "delete from SpecifiedComponents where masterComponent.compid = ? ", new Object[]{goalid});
                if(!StringUtil.isNullOrEmpty(requestParams.get("expression").toString())){
                	JSONArray expression = new JSONArray(requestParams.get("expression").toString());
                	if(method==2){
                		for(int i=0; i<expression.length(); i++){
                			JSONObject object = new JSONObject(expression.get(i).toString());
                			SpecifiedComponents specifiedComponents = new SpecifiedComponents();
                			specifiedComponents.setCoefficient(Integer.parseInt(object.get("coefficient").toString()));
                			specifiedComponents.setOperator(object.get("operator").toString());
                			specifiedComponents.setComponent((Componentmaster) hibernateTemplate.get(Componentmaster.class, object.get("component").toString()));
                			specifiedComponents.setMasterComponent(componentmaster);
                			hibernateTemplate.save(specifiedComponents);
                		}
                	}
                }
                if(Boolean.parseBoolean(requestParams.get("isdefault").toString()) && !Boolean.parseBoolean(requestParams.get("isblock").toString())){
            		List<User> list = HibernateUtil.executeQuery(hibernateTemplate, "from User where company.companyID = ? and frequency = ? ", new Object[]{companyid, Integer.parseInt(requestParams.get("frequency").toString())});
            		ComponentResourceMapping mapping = null;
            		for(User user: list){
            			mapping = new ComponentResourceMapping();
            			mapping.setUser(user);
            			mapping.setComponent(componentmaster);
            			hibernateTemplate.save(mapping);
            		}
            	}
            } else {
                goalid = requestParams.get("compid").toString();
                componentmaster = (Componentmaster) hibernateTemplate.load(Componentmaster.class, goalid);
                componentmaster.setCode(requestParams.get("code").toString());
                componentmaster.setCompany((Company) hibernateTemplate.get(Company.class,requestParams.get("companyid").toString()));
                componentmaster.setSdate(sdate);
                componentmaster.setEdate(edate);
                componentmaster.setDescription(requestParams.get("description").toString());
                if(requestParams.containsKey("subtype") && requestParams.get("subtype")!=null){
                    componentmaster.setSubtype((MasterData) hibernateTemplate.get(MasterData.class, requestParams.get("subtype").toString()));
                }
                componentmaster.setFrequency(Integer.parseInt(requestParams.get("frequency").toString()));
                componentmaster.setCostcenter((CostCenter) hibernateTemplate.get(CostCenter.class, requestParams.get("costcenter").toString()));
                componentmaster.setPaymentterm((MasterData) hibernateTemplate.get(MasterData.class, requestParams.get("paymentterm").toString()));
                if(requestParams.containsKey("method") && requestParams.get("method")!=null) {
                    method = Integer.parseInt(requestParams.get("method").toString());
                	componentmaster.setMethod(method);
                }
                if(!StringUtil.isNullOrEmpty(requestParams.get("computeon").toString())) {
                    componentmaster.setComputeon((Componentmaster) hibernateTemplate.get(Componentmaster.class, requestParams.get("computeon").toString()));
                }else{
                	componentmaster.setComputeon(null);
                }
                componentmaster.setAmount(Double.parseDouble(requestParams.get("amount").toString()));
                componentmaster.setIsadjust(Boolean.parseBoolean(requestParams.get("isadjust").toString()));
                componentmaster.setIsblock(Boolean.parseBoolean(requestParams.get("isblock").toString()));
                componentmaster.setIstaxablecomponent(Boolean.parseBoolean(requestParams.get("istaxablecomponent").toString()));
                componentmaster.setIsdefault(Boolean.parseBoolean(requestParams.get("isdefault").toString()));
                hibernateTemplate.save(componentmaster);
                HibernateUtil.executeUpdate(hibernateTemplate, "delete from SpecifiedComponents where masterComponent.compid = ? ", new Object[]{goalid});
                if(!StringUtil.isNullOrEmpty(requestParams.get("expression").toString())){
                	JSONArray expression = new JSONArray(requestParams.get("expression").toString());
                	if(method==2){
                		for(int i=0; i<expression.length(); i++){
                			JSONObject object = new JSONObject(expression.get(i).toString());
                			SpecifiedComponents specifiedComponents = new SpecifiedComponents();
                			specifiedComponents.setCoefficient(Integer.parseInt(object.get("coefficient").toString()));
                			specifiedComponents.setOperator(object.get("operator").toString());
                			specifiedComponents.setComponent((Componentmaster) hibernateTemplate.get(Componentmaster.class, object.get("component").toString()));
                			specifiedComponents.setMasterComponent(componentmaster);
                			hibernateTemplate.save(specifiedComponents);
                		}
                	}
                }
                
                if(Boolean.parseBoolean(requestParams.get("isdefault").toString()) && !Boolean.parseBoolean(requestParams.get("isblock").toString())){
                	HibernateUtil.executeUpdate(hibernateTemplate, "delete from ComponentResourceMapping where component.compid = ? and user.userID in (select userID from User where company.companyID = ?)", new Object[]{componentmaster.getCompid(), companyid});
            		List<User> list = HibernateUtil.executeQuery(hibernateTemplate, "from User where company.companyID = ? and frequency = ? ", new Object[]{companyid, Integer.parseInt(requestParams.get("frequency").toString())});
            		ComponentResourceMapping mapping = null;
            		for(User user: list){
            			mapping = new ComponentResourceMapping();
            			mapping.setUser(user);
            			mapping.setComponent(componentmaster);
            			hibernateTemplate.save(mapping);
            		}
            	}else{
            		HibernateUtil.executeUpdate(hibernateTemplate, "delete from ComponentResourceMapping where component.compid = ? and user.userID in (select userID from User where company.companyID = ?)", new Object[]{componentmaster.getCompid(), companyid});
                }
                
                if(Boolean.parseBoolean(requestParams.get("isblock").toString())){
            		HibernateUtil.executeUpdate(hibernateTemplate, "delete from ComponentResourceMapping where component.compid = ? and user.userID in (select userID from User where company.companyID = ?)", new Object[]{componentmaster.getCompid(), companyid});
                }
            }
            lst.add(componentmaster);
            if(!lst.isEmpty()){
                count = lst.size();
            }
            
            success=true;
            //@@ProfileHandler.insertAuditLog(session, auditID, "User <b>"+fullName+"</b> "+auditMsg+"master item <b>"+gmst.getValue()+"</b> of master group <b>"+master.getName()+".</b>", request);
        } catch (Exception e) {
            success=false;
            e.printStackTrace();
        }
    return new KwlReturnObject(success, "", "-1", lst, count);
    }

    
    @Override
    public KwlReturnObject deletePaycomponent_date(String componentid) {
        boolean success=false;
        List lst= new ArrayList();
        int count=0;
        try {
            
            Componentmaster mdata=(Componentmaster) hibernateTemplate.load(Componentmaster.class,componentid );
            String groupName=mdata.getCode();
            hibernateTemplate.delete(mdata);
            
            lst.add(groupName);
            success=true;
        } catch (Exception e) {
            e.printStackTrace();
            success=false;
        }
        return new KwlReturnObject(success, "", "-1", lst, count);
    }

    public List<ComponentResourceMapping> getAssignedComponent(String userid) {
        List<ComponentResourceMapping> list = null;
        try {
            list = HibernateUtil.executeQuery(hibernateTemplate, "from ComponentResourceMapping where user.userID = ?", new Object[]{userid});
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return list;
    }
    
    @SuppressWarnings("finally")
	@Override
    public List<ComponentResourceMappingHistory> getAssignedComponentsToResource(String userid, Date enddate, int frequency){
    	List<ComponentResourceMappingHistory> componentResourceMappingHistories = null;
    	try{
    		componentResourceMappingHistories = hibernateTemplate.find("from ComponentResourceMappingHistory where user.userID = ? and periodenddate = ? and frequency = ?", new Object[]{userid, enddate, frequency});
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		return componentResourceMappingHistories;
    	}
    }

    @SuppressWarnings("finally")
	@Override
    public List<ComponentResourceMappingHistory> getSalaryComponentForEmployee(String userid, Date startdate, Date enddate, boolean showAll){
    	List<ComponentResourceMappingHistory> componentResourceMappingHistories = null;
    	try{
    		if(showAll){
                componentResourceMappingHistories = hibernateTemplate.find("select cr from ComponentResourceMappingHistory as cr , PayrollHistory as ph     where cr.user.userID = ? and ph.user.userID=? and cr.periodstartdate=ph.paycyclestartdate  ", new Object[]{userid,userid});
            } else {
                componentResourceMappingHistories = hibernateTemplate.find("from ComponentResourceMappingHistory where user.userID = ? and periodstartdate = ? and periodenddate = ? ", new Object[]{userid, startdate, enddate});
            }
            
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		return componentResourceMappingHistories;
    	}
    }

    @SuppressWarnings({ "unchecked", "finally" })
	@Override
    public boolean deleteAssignedComponents(String userid) {
    	boolean success = false;
        try{
        	HibernateUtil.executeUpdate(hibernateTemplate, "delete from ComponentResourceMapping where user.userID in ("+userid+")", null);
        	success = true;
        }catch(Exception e){
        	success = false;
        	e.printStackTrace();
        }finally{
        	return success;
        }
    }
    
    @Override
    public boolean addAssignedComponent(String userid, String componentid) {
    	boolean success = false;
        try{
        	ComponentResourceMapping componentResourceMapping = new ComponentResourceMapping();
        	componentResourceMapping.setUser((User) hibernateTemplate.get(User.class, userid));
        	componentResourceMapping.setComponent((Componentmaster) hibernateTemplate.get(Componentmaster.class, componentid));
        	hibernateTemplate.save(componentResourceMapping);
        }catch(Exception e){
        	success = true;
        	e.printStackTrace();
        }finally{
        	return success;
        }
    }
    
    @Override
    public boolean setComponentRuleObject(ComponentRule componentRuleObj) {
    	boolean success = false;
        try{
        	hibernateTemplate.save(componentRuleObj);
                success = true;
        }catch(Exception e){
        	success = false;
        	e.printStackTrace();
        }finally{
        	return success;
        }
    }
    
    
    @Override
    public List<ComponentResourceMapping> getUserIncomeTaxComponent(String userid, Date year) {
        List<ComponentResourceMapping> list = null;
        try {
                list = HibernateUtil.executeQuery(hibernateTemplate, "from ComponentResourceMapping where user.userID = ? and component.istaxablecomponent='T' ", new Object[]{userid});
        } catch (Exception e) {
                e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<UserTaxDeclaration> getUserIncomeTaxComponent(String userid, StringBuffer componentList){
    	List<UserTaxDeclaration> list = null;
    	
    	try{
            String hql = "from UserTaxDeclaration where user.userID = ?  ";
            if(componentList.length()>0){
                hql+=" and component.compid in ("+componentList+") ";
            }
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userid});
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    
    @Override
    public boolean saveUserIncomeTaxDeclaration(HashMap<Componentmaster,String> map, String userid, Double savings){
    	boolean success = false;
        List<UserTaxDeclaration> list = null;
        try{
            User user = (User) hibernateTemplate.get(User.class, userid);
            
            Empprofile empprofile = (Empprofile) hibernateTemplate.get(Empprofile.class, userid);
            
            empprofile.setSavings(savings);
            
            hibernateTemplate.save(empprofile);
            
            for(Map.Entry<Componentmaster, String> entry : map.entrySet()){
                list = HibernateUtil.executeQuery(hibernateTemplate, "from UserTaxDeclaration where user.userID=? and component.id = ? ", new Object[]{userid, entry.getKey().getCompid()});
                if(list!=null && list.size()>0){
                    for(UserTaxDeclaration utd: list){

                        if(StringUtil.isNullOrEmpty(entry.getValue())){
                                utd.setValue(0.0);
                        }else{
                                utd.setValue(Double.parseDouble(entry.getValue()));
                        }

                        hibernateTemplate.save(utd);
                    }
                }else {
                
                    UserTaxDeclaration utd = new UserTaxDeclaration();
                    
                    utd.setComponent(entry.getKey());
                    utd.setUser(user);
                    
                    if(StringUtil.isNullOrEmpty(entry.getValue())){
                            utd.setValue(0.0);
                    }else{
                            utd.setValue(Double.parseDouble(entry.getValue()));
                    }
                    
                    hibernateTemplate.save(utd);
                    
                }
            }
            success = true;
        }catch(Exception e){
                e.printStackTrace();
        }
        return success;
    }
    
    @Override
    public ComponentResourceMappingHistory editAssignedComponentsToResource(String id, double amount){
    	ComponentResourceMappingHistory componentResourceMappingHistory = null;
        try{
        	componentResourceMappingHistory = (ComponentResourceMappingHistory) hibernateTemplate.get(ComponentResourceMappingHistory.class, id);
        	componentResourceMappingHistory.setAmount(amount);
        	hibernateTemplate.save(componentResourceMappingHistory);
        }catch(Exception e){
        	e.printStackTrace();
        }
        return componentResourceMappingHistory;
    }

     @Override
    public KwlReturnObject deleteComponentOfResource(StringBuffer userList, Date startdate, Date enddate, int frequency) {

        List tmp = new ArrayList();
        boolean success=false;
        try{

            String hql = "delete from ComponentResourceMappingHistory where periodenddate = ? and frequency = ? and user.userID in ("+userList+")";
            HibernateUtil.executeUpdate(hibernateTemplate, hql, new Object[]{enddate, frequency});

            success = true;
        }catch(Exception e){
            e.printStackTrace();
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tmp,0);
        }

    }

    @Override
    public KwlReturnObject deleteGeneratePayrollData(StringBuffer userList, Date startdate, Date enddate, Integer frequency) {

        List tmp = new ArrayList();
        boolean success=false;
        try{

            String hql = "delete from PayrollHistory where paycycleenddate = ? and frequency = ? and user.userID in ("+userList+")";
            HibernateUtil.executeUpdate(hibernateTemplate, hql, new Object[]{enddate, frequency.toString()});

            success = true;
        }catch(Exception e){
            e.printStackTrace();
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tmp,0);
        }

    }

    @SuppressWarnings("finally")
	@Override
    public boolean assignComponentOfResource(String userid, Componentmaster component, double amount, Date startdate, Date enddate, int frequency) {
    	boolean success = false;
        try{
        	ComponentResourceMappingHistory componentResourceMappingHistory = new ComponentResourceMappingHistory();
        	
        	componentResourceMappingHistory.setUser((User) hibernateTemplate.get(User.class, userid));
        	componentResourceMappingHistory.setComponent(component);
        	componentResourceMappingHistory.setAmount(amount);
        	componentResourceMappingHistory.setComponentstartdate(component.getSdate());
        	componentResourceMappingHistory.setComponentenddate(component.getEdate());
        	componentResourceMappingHistory.setPeriodstartdate(startdate);
        	componentResourceMappingHistory.setPeriodenddate(enddate);
        	componentResourceMappingHistory.setFrequency(frequency);
        	hibernateTemplate.save(componentResourceMappingHistory);
        }catch(Exception e){
        	success = true;
        	e.printStackTrace();
        }finally{
        	return success;
        }
    }

    @Override
    public KwlReturnObject getPayrollHistory(HashMap<String,Object> requestParams) {
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            if(requestParams.containsKey("primary")&&(Boolean)requestParams.get("primary")){
                hql = "from PayrollHistory where historyid=?";
                String cmptid = requestParams.get("historyid").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{cmptid});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from PayrollHistory ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = (ArrayList)requestParams.get("filter_names");
                value = (ArrayList)requestParams.get("filter_values");
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }
            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }
            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = (ArrayList)requestParams.get("order_by");
                ordertype = (ArrayList)requestParams.get("order_type");
                hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            if(requestParams.containsKey("ss")){
                hql+=" and name like '" + requestParams.get("ss").toString() + "%' ";
            }
//            if(requestParams.containsKey("type")){
//                if(StringUtil.equal(requestParams.get("type").toString(), "authorization")){
//                    hql+=" and salarystatus in ("+2+","+3+") ";
//                } else if(StringUtil.equal(requestParams.get("type").toString(), "process")){
//                    hql+=" and salarystatus in ("+3+","+5+","+6+") ";
//                }
//
//            }

            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }
    
    @SuppressWarnings("finally")
	public boolean isResourceMappingExit(String userid, Date startdate, Date enddate){
    	boolean flag = true;
    	try{
    		List<ComponentResourceMappingHistory> componentResourceMappingHistories = HibernateUtil.executeQuery(hibernateTemplate, "from ComponentResourceMappingHistory where user.userID = ? and periodstartdate = ? and periodenddate = ?", new Object[]{userid, startdate, enddate});
    		if(componentResourceMappingHistories.size()>0){
    			flag = false;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		return flag;
    	}
    }
    
    @Override
    public KwlReturnObject calculatePayroll(JSONObject jobj) {
        JSONObject myjobj = new JSONObject();
        boolean success = true;
        List ll = new ArrayList();
        int dl = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            PayrollHistory ph = null;
            String historyid = jobj.get("historyid").toString();
            if(StringUtil.equal(historyid, "0")){
                ph = new PayrollHistory();
                String id = UUID.randomUUID().toString();
                ph.setHistoryid(id);
                ph.setFrequency(PayrollHistory.DEFAULT_FREQUENCY);
            }
            else{
                ph = (PayrollHistory) hibernateTemplate.get(PayrollHistory.class, historyid);
                if(jobj.has("frequency")){
                    ph.setFrequency(jobj.getString("frequency"));
                }

            }
           
            if(jobj.has("absence")) {
                double absence = Double.parseDouble(jobj.get("absence").toString());
                ph.setUnpaidleaves(absence);
            }
            if(jobj.has("actualhours")) {
                double actual = Double.parseDouble(jobj.get("actualhours").toString());
                ph.setActualhours(actual);
            }
            if(jobj.has("contracthours")) {
                double contracthours = Double.parseDouble(jobj.get("contracthours").toString());
                ph.setContracthours(contracthours);
            }
            if(jobj.has("contractdate")) {
                ph.setContractdate(new Date());
            }
            if(jobj.has("createdon")) {
                ph.setCreatedon(new Date());
            }
            if(jobj.has("deduction")) {
                double deduction = Double.parseDouble(jobj.get("deduction").toString());
                ph.setDeduction(deduction);
            }
            if(jobj.has("tax")) {
                ph.setTax(Double.parseDouble(jobj.get("tax").toString()));
            }
            if(jobj.has("otherRemuneration")) {
                ph.setOtherRemuneration(Double.parseDouble(jobj.get("otherRemuneration").toString()));
            }
            if(jobj.has("jobtitle")) {
                MasterData m = (MasterData) hibernateTemplate.get(MasterData.class, jobj.get("jobtitle").toString());
                ph.setJobTitle(m);
            }
            if(jobj.has("costcenter")) {
                CostCenter c = (CostCenter) hibernateTemplate.get(CostCenter.class, jobj.get("costcenter").toString());
                ph.setCostCenter(c);
            }
            if(jobj.has("earning")) {
                double earning = Double.parseDouble(jobj.get("earning").toString());
                ph.setEarning(earning);
            }
            
            if(jobj.has("generatedon")) {
                ph.setGeneratedon(new Date());
            }
            if(jobj.has("fullname")) {
                ph.setName(jobj.get("fullname").toString());
            }
            if(jobj.has("netsalary")) {
                double netsalary = Double.parseDouble(jobj.get("netsalary").toString());
                ph.setNet(netsalary);
            }
            if(jobj.has("paycyclestartdate")) {
                Date startdate = sdf.parse(jobj.get("paycyclestartdate").toString());
                ph.setPaycyclestartdate(startdate);
            }
            if(jobj.has("paycycleenddate")) {
                Date enddate = sdf.parse(jobj.get("paycycleenddate").toString());
                ph.setPaycycleenddate(enddate);
            }
            if(jobj.has("paymentmonth")) {
                ph.setPaymonth(jobj.get("paymentmonth").toString());
            }
            if(jobj.has("salarystatus")) {
                int status = Integer.parseInt(jobj.get("salarystatus").toString());
                ph.setSalarystatus(status);
            }
            if(jobj.has("leaves")) {
                double leaves = Double.parseDouble(jobj.get("leaves").toString());
                ph.setUnpaidleaves(leaves);
            }
            if(jobj.has("userid")) {
                User user = (User) hibernateTemplate.get(User.class, jobj.get("userid").toString());
                ph.setUser(user);

                Empprofile ep = (Empprofile) hibernateTemplate.get(Empprofile.class, jobj.get("userid").toString());
                ph.setEmployementdate(ep.getJoindate());
            }
            if(jobj.has("incometax")) {
                double incometax = Double.parseDouble(jobj.get("incometax").toString());
                ph.setIncometaxAmount(incometax);
            }
            hibernateTemplate.save(ph);
            ll.add(ph);
        } catch(Exception ex) {
            success = false;
        }
        return new KwlReturnObject(success, "", "", ll, dl);
    }
    
    public KwlReturnObject updatePayrollHistory(HashMap<String, Object> requestParams) {
        boolean success=false;
        List lst= new ArrayList();
        int count=0;
        try {
            PayrollHistory payrollHistory = null;
            if(requestParams.containsKey("historyid")) {
                payrollHistory = (PayrollHistory) hibernateTemplate.get(PayrollHistory.class, (Serializable) requestParams.get("historyid"));
            }
            if(requestParams.containsKey("salarystatus")) {
            	int salarystatus = Integer.parseInt(requestParams.get("salarystatus").toString());
            	if(salarystatus<2){
            		payrollHistory.setIncometaxAmount(0);
            	}
                payrollHistory.setSalarystatus(salarystatus);
            }
            if(requestParams.containsKey("paydate")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date paydate=null;
                if(requestParams.get("paydate")!=null){
                   paydate =sdf.parse(requestParams.get("paydate").toString());
                }
                payrollHistory.setPaymentdate(paydate);
            }
            if(requestParams.containsKey("payspecification")) {
                payrollHistory.setPayspecification(requestParams.get("payspecification").toString());
            }
            if(requestParams.containsKey("sliptxt1")) {
                payrollHistory.setPaysliptxt1(requestParams.get("sliptxt1").toString());
            }
            if(requestParams.containsKey("sliptxt2")) {
                payrollHistory.setPaysliptxt2(requestParams.get("sliptxt2").toString());
            }
            if(requestParams.containsKey("sliptxt3")) {
                payrollHistory.setPaysliptxt3(requestParams.get("sliptxt3").toString());
            }
            
            if(requestParams.containsKey("leaveDeductionAmount")){
            	if(Double.parseDouble(requestParams.get("leaveDeductionAmount").toString())>=0){
            		payrollHistory.setUnpaidleavesAmount(Double.parseDouble(requestParams.get("leaveDeductionAmount").toString()));
            	}
            }
            if(requestParams.containsKey("netsalary")){
            	if(Double.parseDouble(requestParams.get("netsalary").toString())>=0){
            		payrollHistory.setNet(Double.parseDouble(requestParams.get("netsalary").toString()));
            	}
            }
            if(requestParams.containsKey("deduction")){
            	if(Double.parseDouble(requestParams.get("deduction").toString())>=0){
            		payrollHistory.setDeduction(Double.parseDouble(requestParams.get("deduction").toString()));
            	}
            }
            if(requestParams.containsKey("tax")){
            	if(Double.parseDouble(requestParams.get("tax").toString())>=0){
            		payrollHistory.setTax(Double.parseDouble(requestParams.get("tax").toString()));
            	}
            }
            if(requestParams.containsKey("incometax")){
            	if(Double.parseDouble(requestParams.get("incometax").toString())>=0){
            		payrollHistory.setIncometaxAmount(Double.parseDouble(requestParams.get("incometax").toString()));
            	}
            }
            if(requestParams.containsKey("otherRemuneration")){
            	if(Double.parseDouble(requestParams.get("otherRemuneration").toString())>=0){
            		payrollHistory.setOtherRemuneration(Double.parseDouble(requestParams.get("otherRemuneration").toString()));
            	}
            }
            if(requestParams.containsKey("earning")){
            	if(Double.parseDouble(requestParams.get("earning").toString())>=0){
            		payrollHistory.setEarning(Double.parseDouble(requestParams.get("earning").toString()));
            	}
            }
            
//            payrollHistory.setName(requestParams.get("fullname").toString());
//            payrollHistory.setJobtitle(requestParams.get("costcenter").toString());
//            payrollHistory.setCostCenter((CostCenter) hibernateTemplate.get(CostCenter.class, requestParams.get("costcenter").toString()));
//            payrollHistory.setJobtitle(requestParams.get("jobtitle").toString());
//            payrollHistory.setJobTitle((MasterData) hibernateTemplate.get(MasterData.class,requestParams.get("jobtitle").toString()));
//            //payrollHistory.setContracthours(Double.parseDouble(requestParams.get("contract").toString()));
//            payrollHistory.setAbsencehours(Double.parseDouble(requestParams.get("absence").toString()));
//            //payrollHistory.setActualhours(Double.parseDouble(requestParams.get("actual").toString()));
//            //payrollHistory.setEmployementdate(new Date(requestParams.get("employmentdate").toString()));
//            //payrollHistory.setContractdate(new Date(requestParams.get("contractenddate").toString()));
//            //payrollHistory.setD(requestParams.get("difference").toString());
//            //payrollHistory.setSalarystatus(Integer.parseInt(requestParams.get("status").toString()));
            hibernateTemplate.save(payrollHistory);
            lst.add(payrollHistory);
            success=true;
        } catch (Exception e) {
            e.printStackTrace();
            success=false;
        }
        return new KwlReturnObject(success, "", "-1", lst, count);
    }

    public Componentmaster getComponentObj( String componentid){
        
        Componentmaster  componentmaster = (Componentmaster) hibernateTemplate.get(Componentmaster.class, componentid);
        return componentmaster;
    }
    
    @Override
    public List<ComponentResourceMappingHistory> getSalaryDetails(String userid, Date enddate, int frequency, int type, Integer otherRemuneration){
    	List<ComponentResourceMappingHistory> list = null;
    	try{
    		String hql=null;
    		if(otherRemuneration!=null){
    			hql = "from ComponentResourceMappingHistory where user.userID = ? and periodenddate = ? and (component.subtype.componenttype = ? or component.subtype.componenttype = ?)";
    			list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userid, enddate, type, otherRemuneration});
    		}else{
    			hql = "from ComponentResourceMappingHistory where user.userID = ? and periodenddate = ? and component.subtype.componenttype = ?";
    			list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userid, enddate, type});
    		}
        }catch(Exception e){
        	e.printStackTrace();
        }
        return list;
    }

    @SuppressWarnings("finally")
	@Override
    public boolean editResorcePayrollData(HashMap<String, Object> requestParams) {
        boolean success=false;
        try{
        	String historyid = requestParams.get("historyid").toString();
        	String frequency = requestParams.get("frequency").toString();
            double absence =0;
            PayrollHistory payrollHistory = null;
            if(StringUtil.equal(historyid, "0")){

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                User user = (User) hibernateTemplate.get(User.class, requestParams.get("userid").toString());
                Useraccount ua = (Useraccount) hibernateTemplate.get(Useraccount.class, requestParams.get("userid").toString());
                Empprofile empprofile = (Empprofile) hibernateTemplate.get(Empprofile.class, requestParams.get("userid").toString());
                Date empDate = null;
                if(empprofile!=null){
                	empDate = empprofile.getJoindate();
                }
                String periodStartDate = requestParams.get("paycyclestartdate").toString();
                Date startdate = sdf.parse(periodStartDate.toString());
                String periodEndDate = requestParams.get("paycycleenddate").toString();
                Date enddate = sdf.parse(periodEndDate.toString());


                historyid = UUID.randomUUID().toString();

                payrollHistory = new PayrollHistory();

                payrollHistory.setHistoryid(historyid);
                payrollHistory.setUser(user);
                payrollHistory.setSalarystatus(1);
                payrollHistory.setEmployementdate(empDate);
                payrollHistory.setCreatedon(new Date());
                payrollHistory.setGeneratedon(new Date());
                payrollHistory.setPaycyclestartdate(startdate);
                payrollHistory.setPaycycleenddate(enddate);
                payrollHistory.setFrequency(frequency);

                if(requestParams.containsKey("incometax")){
                	if(Double.parseDouble(requestParams.get("incometax").toString())>0){
                		payrollHistory.setIncometaxAmount(Double.parseDouble(requestParams.get("incometax").toString()));
                	}
                }

            } else {
                payrollHistory = (PayrollHistory) hibernateTemplate.get(PayrollHistory.class, (Serializable) requestParams.get("historyid"));
            }

        	payrollHistory.setName(requestParams.get("fullname").toString());
        	payrollHistory.setCostcenter(requestParams.get("costcenter").toString());
        	payrollHistory.setCostCenter((CostCenter) hibernateTemplate.get(CostCenter.class, requestParams.get("costcenter").toString()));
        	payrollHistory.setJobtitle(requestParams.get("jobtitle").toString());
        	payrollHistory.setJobTitle((MasterData) hibernateTemplate.get(MasterData.class,requestParams.get("jobtitle").toString()));
            if(!StringUtil.isNullOrEmpty(requestParams.get("absence").toString())){
                absence = Double.parseDouble(requestParams.get("absence").toString());
            }
        	payrollHistory.setUnpaidleaves(absence);
        	if(!StringUtil.isNullOrEmpty(requestParams.get("leaveDeductionAmount").toString())){
        		payrollHistory.setUnpaidleavesAmount(Double.parseDouble(requestParams.get("leaveDeductionAmount").toString()));
        	}
        	if(requestParams.containsKey("netsalary")){
            	if(Double.parseDouble(requestParams.get("netsalary").toString())>0){
            		payrollHistory.setNet(Double.parseDouble(requestParams.get("netsalary").toString()));
            	}
            }
            if(requestParams.containsKey("deduction")){
            	if(Double.parseDouble(requestParams.get("deduction").toString())>0){
            		payrollHistory.setDeduction(Double.parseDouble(requestParams.get("deduction").toString()));
            	}
            }
            if(requestParams.containsKey("tax")){
            	if(Double.parseDouble(requestParams.get("tax").toString())>0){
            		payrollHistory.setTax(Double.parseDouble(requestParams.get("tax").toString()));
            	}
            }
            if(requestParams.containsKey("otherRemuneration")){
            	if(Double.parseDouble(requestParams.get("otherRemuneration").toString())>0){
            		payrollHistory.setOtherRemuneration(Double.parseDouble(requestParams.get("otherRemuneration").toString()));
            	}
            }
            if(requestParams.containsKey("earning")){
            	if(Double.parseDouble(requestParams.get("earning").toString())>0){
            		payrollHistory.setEarning(Double.parseDouble(requestParams.get("earning").toString()));
            	}
            }
            if(requestParams.containsKey("incometax")){
            	if(Double.parseDouble(requestParams.get("incometax").toString())>0){
            		payrollHistory.setIncometaxAmount(Double.parseDouble(requestParams.get("incometax").toString()));
            	}
            }

            hibernateTemplate.save(payrollHistory);
            success = true;
        }catch(Exception e){
            e.printStackTrace();
            success = false;
        }finally{
           return success;
        }
    }

    @Override
    public KwlReturnObject getGeneratedPayrollList(HashMap<String, Object> requestParams) {
        List<PayrollHistory> historydetails = null;
        boolean success=false;
        int count=0;
        try{
            ArrayList alist = new ArrayList();
            alist.add(requestParams.get("startdate"));
            alist.add(requestParams.get("enddate"));
            alist.add(requestParams.get("companyid"));
            alist.add(requestParams.get("frequency").toString());
            
            String hql = "from PayrollHistory where paycyclestartdate >= ? and paycycleenddate <= ? and user.company.companyID=? and frequency = ?";
            
            if(requestParams.containsKey("userid")){
                hql+=" and user.userID=? ";
                alist.add(requestParams.get("userid").toString());
            }

            if(requestParams.containsKey("status")){
                    hql+=" and salarystatus =? ";
                    alist.add(requestParams.get("status"));
            }
            if(requestParams.containsKey("ss")){
                hql+=" and name like '" + requestParams.get("ss").toString() + "%'";
            }
            
            historydetails = HibernateUtil.executeQuery(hibernateTemplate, hql,alist.toArray());
            if(historydetails!=null){
                count = historydetails.size();
            }

            if(requestParams.containsKey("start") && requestParams.containsKey("limit")){
                int start = Integer.parseInt(requestParams.get("start").toString());
                int limit = Integer.parseInt(requestParams.get("limit").toString());
                historydetails = HibernateUtil.executeQueryPaging(hibernateTemplate, hql, alist.toArray(), new Integer[]{start, limit});
            }
            success = true;
        }catch(Exception e){
            e.printStackTrace();
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", historydetails,count);
        }
    }

    @Override
    public KwlReturnObject getPayrollUserList(StringBuffer userList,String companyid, String searchText, int frequency, String start, String limit) {
        List<User> historydetails = null;
        boolean success=false;
        int count =0;
        try{

            String hql = "from User where  company.companyID=? and deleteflag=0 and frequency = ?";
            if(!StringUtil.isNullOrEmpty(userList.toString())){
                hql+=" and userID not in ("+userList+") ";
            }
            
            if(!StringUtil.isNullOrEmpty(searchText)){
                hql+=" and firstName like '" + searchText + "%'";
            }
			historydetails = HibernateUtil.executeQuery(hibernateTemplate, hql,new Object[]{companyid, frequency});
            if(historydetails!=null){
                count = historydetails.size();
            }
            if(!StringUtil.isNullOrEmpty(start) && !StringUtil.isNullOrEmpty(limit)){
                historydetails = HibernateUtil.executeQueryPaging(hibernateTemplate, hql,new Object[]{companyid, frequency}, new Integer[]{Integer.parseInt(start), Integer.parseInt(limit)});
            }

            success = true;
        }catch(Exception e){
            e.printStackTrace();
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", historydetails,count);
        }
    }

    
    public List<PayrollHistory> getGeneratedSalaries(String userid ,Date startdate, Date enddate){
    	List<PayrollHistory> histories = null;
    	try{
    		histories = HibernateUtil.executeQuery(hibernateTemplate, "from PayrollHistory where user.userID = ? and ((paycyclestartdate <= ? and paycycleenddate >= ?) or (paycyclestartdate <= ? and paycycleenddate >= ?))", new Object[]{userid, startdate, startdate, enddate, enddate});
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return histories;
    }
    
    public List<Componentmaster> getDefaultComponents(String companyid, int frequency){
    	List<Componentmaster> list = null;
    	try{
    		list = HibernateUtil.executeQuery(hibernateTemplate, "from Componentmaster where company.companyID = ? and frequency = ? and isdefault = ? and isblock = ?", new Object[]{companyid, frequency, true, false});
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    
    @Override
    public List<Componentmaster> getComputeOnComponents(String companyid, String componentid, int frequency){
    	List<Componentmaster> list = null;
    	try{
    		list = HibernateUtil.executeQuery(hibernateTemplate, "from Componentmaster where company.companyID = ? and frequency = ? and isblock = ? and computeon is null and method in (0,1) and compid not in (?)", new Object[]{companyid, frequency, false, componentid});
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    
    public List<Componentmaster> getAvailableComponent(StringBuffer assignedComponentList, String companyid, int frequency){
    	List<Componentmaster> list = null;
    	
    	try{
            String hql = "from Componentmaster where company.companyID = ? and frequency = ? ";
            if(assignedComponentList.length()>0){
                hql+=" and compid not in ("+assignedComponentList+") ";
            }
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{companyid, frequency});
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    
    
    @Override
    public KwlReturnObject getDependentComponent(String parentComponentid, Date startdate, Date enddate, String userid){

            KwlReturnObject result = null;
            List lst =null;
            
            try{
                String hql = " select  cm, cr from Componentmaster  as cm , ComponentResourceMappingHistory as cr     where cr.component.compid=? and cr.periodstartdate=? and cr.periodenddate=? and cr.user.userID = ?  and cm.computeon.compid=cr.component.compid ";
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{parentComponentid,startdate,enddate,userid});
                result = new KwlReturnObject(true, "success", "", lst, lst.size());
            }catch(Exception e){
                    e.printStackTrace();
            }
            return result;
     }

    @SuppressWarnings("finally")
	@Override
    public List<ComponentResourceMappingHistory> getComponentsToResourceHistoryForComponent(String userid, int frequency, Date enddate, String componentid){
    	List<ComponentResourceMappingHistory> componentResourceMappingHistories = null;
    	try{
    		componentResourceMappingHistories = hibernateTemplate.find("from ComponentResourceMappingHistory where user.userID = ? and frequency = ? and periodenddate = ? and component.compid = ?", new Object[]{userid, frequency, enddate, componentid});
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		return componentResourceMappingHistories;
    	}
    }

    @Override
    public boolean editAssignFrequency(final String[] empids, final int frequency) {
        boolean success = false;
        int numRow = 0;
        try{
            final String hql1 = "Update User set frequency= "+frequency+" where userID in (:userids) ";
            numRow = (Integer) hibernateTemplate.execute(new HibernateCallback() {

                @Override
                public Object doInHibernate(Session session) {
                    int numRows = 0;
                    Query query = session.createQuery(hql1);
                    if (empids != null) {

                        query.setParameterList("userids", empids);

                    }
                    numRows = query.executeUpdate();
                    return numRows;
                }

            });

            success = true;
        }catch(Exception ex){
            success = false;
            ex.printStackTrace();
        }finally{
           return success;
        }
    }
    
    @Override
    public boolean editSalaryStatus(final String[] historyids, final int status, final String comment) {
        boolean success = false;
        int numRow = 0;
        try{
            final String hql1 = "Update PayrollHistory set salarystatus= "+status+", comment='"+comment+"' where historyid in (:ids) ";
            numRow = (Integer) hibernateTemplate.execute(new HibernateCallback() {

                @Override
                public Object doInHibernate(Session session) {
                    int numRows = 0;
                    Query query = session.createQuery(hql1);
                    if (historyids != null) {

                        query.setParameterList("ids", historyids);

                    }
                    numRows = query.executeUpdate();
                    return numRows;
                }

            });

            success = true;
        }catch(Exception ex){
            success = false;
            ex.printStackTrace();
        }finally{
           return success;
        }
    }
    
    @Override
    public List<Componentmaster> getComponentmaster(HashMap<String,Object> requestParams) {
        List<Componentmaster> list = null;
        try {
            ArrayList<String> name = null;
            String hql = "";
            ArrayList<Object> value = null;
            if(requestParams.containsKey("primary")&&(Boolean)requestParams.get("primary")){
                hql = "from Componentmaster where compid=?";
                String cmptid = requestParams.get("compid").toString();
                list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{cmptid});
            }else{
            	hql = "from Componentmaster ";
            	if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
            		name = (ArrayList)requestParams.get("filter_names");
            		value = (ArrayList)requestParams.get("filter_values");
            		hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            	}
            	list = HibernateUtil.executeQuery(hibernateTemplate, hql, value.toArray());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<ComponentResourceMapping> getComponentResourceMapping(HashMap<String,Object> requestParams) {
        List<ComponentResourceMapping> list = null;
        try {
            ArrayList<String> name = null;
            String hql = "";
            ArrayList<Object> value = null;
            if(requestParams.containsKey("primary")&&(Boolean)requestParams.get("primary")){
                hql = "from ComponentResourceMapping where id=?";
                String cmptid = requestParams.get("compid").toString();
                list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{cmptid});
            }else{
            	hql = "from ComponentResourceMapping ";
            	if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
            		name = (ArrayList)requestParams.get("filter_names");
            		value = (ArrayList)requestParams.get("filter_values");
            		hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            	}
            	list = HibernateUtil.executeQuery(hibernateTemplate, hql, value.toArray());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<ComponentResourceMappingHistory> getComponentResourceMappingHistory(HashMap<String,Object> requestParams) {
        List<ComponentResourceMappingHistory> list = null;
        try {
            ArrayList<String> name = null;
            String hql = "";
            ArrayList<Object> value = null;
            if(requestParams.containsKey("primary")&&(Boolean)requestParams.get("primary")){
                hql = "from ComponentResourceMappingHistory where id=?";
                String cmptid = requestParams.get("compid").toString();
                list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{cmptid});
            }else{
            	hql = "from ComponentResourceMappingHistory ";
            	if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
            		name = (ArrayList)requestParams.get("filter_names");
            		value = (ArrayList)requestParams.get("filter_values");
            		hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            	}
            	list = HibernateUtil.executeQuery(hibernateTemplate, hql, value.toArray());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ComponentResourceMappingHistory> getYearlySalaryComponentsForEmployee(String userid, String componentid, Date startdate, Date enddate) {
        List<ComponentResourceMappingHistory> list = null;
        try {
            
            String hql = "from ComponentResourceMappingHistory where user.userID=? and component.compid=? and periodstartdate between ? and ? order by periodstartdate ";
            
            list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userid,componentid,startdate,enddate});
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    @SuppressWarnings("unchecked")
	public List<PayrollHistory> getPayslips(String userid, int status){
    	List<PayrollHistory> list = null;
    	try{
    		list = HibernateUtil.executeQuery(hibernateTemplate, "from PayrollHistory where user.userID = ? and salarystatus = ? order by paycyclestartdate desc", new Object[]{userid, status});
    	}catch(Exception e){	
    		e.printStackTrace();
    	}
    	return list;
    }
    
    @Override
    public List<SpecifiedComponents> getSpecifiedComponents(String masterComponent){
    	List<SpecifiedComponents> list = null;
    	try{
    		list = HibernateUtil.executeQuery(hibernateTemplate, "from SpecifiedComponents where masterComponent.compid = ?", new Object[]{masterComponent});
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    
    
    @Override
    public List<SpecifiedComponents> getDependentSpecifiedComponents(String component){
    	List<SpecifiedComponents> list = null;
    	try{
    		list = HibernateUtil.executeQuery(hibernateTemplate, "from SpecifiedComponents where component.compid = ?", new Object[]{component});
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    
    
    @Override
    public boolean deleteSpecifiedComponents(String masterComponent){
    	boolean success = true;
    	try{
    		HibernateUtil.executeUpdate(hibernateTemplate, "delete from SpecifiedComponents where masterComponent.compid = ?", new Object[]{masterComponent});
    	}catch(Exception e){
    		success = false;
    		e.printStackTrace();
    	}
    	return success;
    }

    @Override
    public List<PayrollHistory> getGeneratedSalariesForMonth(String companyid, Date enddate, String frequecy) {
        List<PayrollHistory> histories = null;
    	try{
    		histories = HibernateUtil.executeQuery(hibernateTemplate, "from PayrollHistory where user.company.companyID = ? and paycycleenddate=? and frequency=?  ", new Object[]{companyid, enddate, frequecy});
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return histories;
    }

	@Override
	public PayrollHistory getPayrollHistoryForUser(String userid, Date enddate, int frequency, int status) {
		PayrollHistory history = null;
		try{
			List<PayrollHistory> histories = HibernateUtil.executeQuery(hibernateTemplate, "from PayrollHistory where user.userID = ? and paycycleenddate=? and frequency=?  and salarystatus = ?", new Object[]{userid, enddate, String.valueOf(frequency), status});
			for(PayrollHistory obj: histories){
				history = obj;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return history;
	}
	
	@Override
	public PayrollHistory getPayrollHistoryForUser(String userid, Date enddate, int frequency) {
		PayrollHistory history = null;
		try{
			List<PayrollHistory> histories = HibernateUtil.executeQuery(hibernateTemplate, "from PayrollHistory where user.userID = ? and paycycleenddate=? and frequency=?", new Object[]{userid, enddate, String.valueOf(frequency)});
			for(PayrollHistory obj: histories){
				history = obj;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return history;
	}

     public List<PayrollHistory> getGeneratedSalariesForUser(String userid ,Date startdate, Date enddate, int status, String frequency){
    	List<PayrollHistory> histories = null;
    	try{
    		histories = HibernateUtil.executeQuery(hibernateTemplate, "from PayrollHistory where user.userID = ? and paycycleenddate >=? and paycycleenddate<=? and frequency=?  and salarystatus = ?", new Object[]{userid, startdate, enddate,frequency, status});
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return histories;
    }

	@Override
	public List<Componentmaster> getDepenentCompoment(String compid) {
		List<Componentmaster> list = null;
		try {
			list = HibernateUtil.executeQuery(hibernateTemplate, "from Componentmaster where computeon.compid = ? ", new Object[]{compid});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
        
    @Override
	public List<ComponentRule> getComponentsRules(String compid) {
		List<ComponentRule> list = null;
		try {
			list = HibernateUtil.executeQuery(hibernateTemplate, "from ComponentRule where component.compid = ? order by lowerlimit", new Object[]{compid});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
        
        @Override
	public boolean deleteRule(String ruleid) {
		boolean success = false;
		try {
			HibernateUtil.executeUpdate(hibernateTemplate, "delete from ComponentRule where id= ? ", new Object[]{ruleid});
                        success = true;
		} catch (Exception e) {
                        success = false;
			e.printStackTrace();
		}
		return success;
	}
        
        @Override
	public boolean deleteComponentRule(String componentid) {
		boolean success = false;
		try {
			HibernateUtil.executeUpdate(hibernateTemplate, "delete from ComponentRule where component.compid= ? ", new Object[]{componentid});
                        success = true;
		} catch (Exception e) {
                        success = false;
			e.printStackTrace();
		}
		return success;
	}
        
        @Override
	public boolean deleteComponentUserTaxDeclaration(String componentid) {
		boolean success = false;
		try {
			HibernateUtil.executeUpdate(hibernateTemplate, "delete from UserTaxDeclaration where component.compid= ? ", new Object[]{componentid});
                        success = true;
		} catch (Exception e) {
                        success = false;
			e.printStackTrace();
		}
		return success;
	}
        
        @Override
	public List<ComponentResourceMappingHistory> getIncomeTaxComponentCalculationForUser(String userid, Date enddate, int frequency) {
            List<ComponentResourceMappingHistory> componentResourceMappingHistoryList = null;
            try {
            	componentResourceMappingHistoryList= HibernateUtil.executeQuery(hibernateTemplate, "from ComponentResourceMappingHistory where user.userID= ? and component.subtype.componenttype=5 and component.method=3 and periodenddate=? and frequency=? ", new Object[]{userid,enddate, frequency});

            } catch (Exception e) {
                e.printStackTrace();     
            }
            return componentResourceMappingHistoryList;
	}
        
         @Override
	public List<ComponentRule> getIncomeTaxComponentRule(String componentid) {
            List<ComponentRule> componentRuleList = null;
            try {
                    componentRuleList= HibernateUtil.executeQuery(hibernateTemplate, "from ComponentRule where component.compid=? order by upperLimit desc", new Object[]{componentid});

            } catch (Exception e) {
                e.printStackTrace();     
            }
            return componentRuleList;
	}
        
        
}
