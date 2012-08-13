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

import com.krawler.common.admin.CostCenter;
import com.krawler.common.admin.User;
import com.krawler.common.admin.userSalaryTemplateMap;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.hrms.master.MasterData;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import masterDB.Historydetail;
import masterDB.Payhistory;
import masterDB.PayrollHistory;
import masterDB.Template;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.krawler.utils.json.base.JSONArray;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import masterDB.ComponentResourceMappingHistory;
import masterDB.Componentmaster;
import masterDB.TempTemplate;


/**
 *
 * @author krawler
 */
public class hrmsPayrollTemplateDAOImpl implements hrmsPayrollTemplateDAO {

    private HibernateTemplate hibernateTemplate;
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public KwlReturnObject getTemplateDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try{
            //String cid =(String) requestParams.get("cid");
            String templateid =(String) requestParams.get("templateid");

            String hql = "from Template where templateid=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{templateid});
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getPayProcessData(HashMap<String, Object> requestParams) {
        try {
            String Cid = requestParams.get("Cid").toString();
            int start = Integer.parseInt(requestParams.get("start").toString());
            int limit = Integer.parseInt(requestParams.get("limit").toString());
            String ss = null;
            if (requestParams.get("ss") != null) {
                ss = requestParams.get("ss").toString();
            }
            ArrayList params = new ArrayList();
            String hql = "";
            hql = "from Template where companyid=?";
            params.add(Cid);
            if (!StringUtil.isNullOrEmpty(ss)) {
                StringUtil.insertParamSearchString(params, ss, 1);
                //String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"designationid.value", "templatename"});
                String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"templatename"});
                hql += searchQuery;
            }
            hql += " order by designationid.value";
            List tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, params.toArray());
            int count = tabledata.size();
            List lst = HibernateUtil.executeQueryPaging(hibernateTemplate, hql, params.toArray(), new Integer[]{start, limit});

            return new KwlReturnObject(true, "success", "", lst, count);
        } catch (Exception e) {
            e.printStackTrace();
            return new KwlReturnObject(false, "failure", e.toString(), null, 0);
        }
    }

       public KwlReturnObject setTemplateData(HashMap<String, Object> requestParams) {

       List<Template> tmp = new ArrayList<Template>();
       String returnmsg="";
        try {
            
            JSONObject jobj1 = new JSONObject(requestParams.get("formdata").toString());
            JSONArray jArray = new JSONArray(requestParams.get("assignedEmployees").toString());
            String qry = "from Template where templatename=? and companyid=?";
            String tn = jobj1.getString("TName");
            String cmpic = requestParams.get("cmpic").toString();
            List ls = HibernateUtil.executeQuery(hibernateTemplate, qry, new Object[]{tn, cmpic});
            if (ls.size() > 0) {
                returnmsg="Template with this name already exists.";
                tmp=null;
                 //this.assignEmpToTemplate(jArray,(Template)ls.get(0));
            } else {
                Template template = new Template();
                String tempuid = java.util.UUID.randomUUID().toString();
                MasterData gr = (MasterData) hibernateTemplate.get(MasterData.class, jobj1.getString("GId"));
                template.setDesignationid(gr);
                template.setTemplateid(tempuid);
                template.setStartrange(jobj1.getString("RStart"));
                template.setEndrange(jobj1.getString("REnd"));
                template.setTemplatename(jobj1.getString("TName"));
                template.setPayinterval(jobj1.getInt("payInterval"));
                template.setEffdate(jobj1.getInt("effdate"));
                template.setCompanyid(cmpic);
                template.setStatus("0");
                template.setShowborder(jobj1.getBoolean("showborder"));
                hibernateTemplate.save(template);
                tmp.add(template);
                returnmsg="Template added successfully.";
                this.assignEmpToTemplate(jArray,template);
            }


        } catch (Exception e) {
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(true, returnmsg, "-1", tmp, 0);
        }
    }
        public KwlReturnObject assignTemplateToUser(HashMap<String, Object> requestParams) throws ServiceException{
        Boolean success = false;
        String msg="";
        List result = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if(requestParams.get("mode").equals("delete")){
                String qry = " delete from userSalaryTemplateMap where userAccount.userID = ? and salaryTemplate.templateid = ? and effectiveDate = ?";
                String userid = requestParams.get("userid").toString();
                String templateid = requestParams.get("templateid").toString();
                Date effectiveDate = sdf.parse(requestParams.get("effectivedate").toString());
                HibernateUtil.executeUpdate(hibernateTemplate, qry, new Object[]{userid,templateid,effectiveDate});
                success=true;
            }else{
              Template templateObj = (Template)hibernateTemplate.get(Template.class, requestParams.get("templateid").toString());
              Useraccount uaccountObj =(Useraccount)hibernateTemplate.get(Useraccount.class, requestParams.get("userid").toString());
              Empprofile empprofileObj;
              try {
                empprofileObj =(Empprofile)hibernateTemplate.get(Empprofile.class, requestParams.get("userid").toString());
              }catch(Exception e){
                empprofileObj = null;
              }
              if(empprofileObj == null) {
                  success=false;
                  msg = "Please set joining date before assigning template";
                } else {
                    Double basic = 0.0;
                    if (requestParams.containsKey("basic") && !requestParams.get("basic").equals("")) {
                        basic = Double.parseDouble(requestParams.get("basic").toString());
                    }
                    userSalaryTemplateMap userSalMap = new userSalaryTemplateMap();
                    userSalMap.setSalaryTemplate(templateObj);
                    userSalMap.setUserAccount(uaccountObj);
                    userSalMap.setEmpProfile(empprofileObj);
                    userSalMap.setBasic(basic);
                    userSalMap.setEffectiveDate(sdf.parse(requestParams.get("effectivedate").toString()));
                    hibernateTemplate.save(userSalMap);
                    result.add(userSalMap);
                    success = true;
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(hrmsPayrollTemplateDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
             throw ServiceException.FAILURE("hrmsPayrollTemplateDAOImpl.assignTemplateToUser : " + ex.getMessage(), ex);
        }finally{
            return new KwlReturnObject(success, msg, "-1",result,result.size());
        }
    }
       public KwlReturnObject getTemplateForEmponDate(HashMap<String, Object> requestParams){
        Boolean success = false;
        List result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        try {
            JSONArray recordArray  = new JSONArray(requestParams.get("records").toString());
            String usrlist ="";
            for(int i=0;i<recordArray.length();i++){
                JSONObject obj = (JSONObject) recordArray.get(i);
                if(i > 0){
                    usrlist += ",";
                }
                usrlist += "'"+obj.get("userid").toString()+"'";
            }
            String qry = "from userSalaryTemplateMap where userAccount.userID in ("+usrlist+")";
            
        

            result = HibernateUtil.executeQuery(hibernateTemplate, qry);
            for(int cnt=0;cnt<result.size();cnt++){
                userSalaryTemplateMap userSalMapObj = (userSalaryTemplateMap)result.get(cnt);
                for(int j=0;j<recordArray.length();j++){
                        JSONObject jobj = (JSONObject)recordArray.get(j);
                       if(userSalMapObj.getUserAccount().getUserID().equals(jobj.get("userid").toString()) && sdf.format(userSalMapObj.getEffectiveDate()).equals(jobj.get("effectivedate").toString())){
                                    result.add(userSalMapObj);
                       }
                }
            }
        } catch (ServiceException ex) {
            Logger.getLogger(hrmsPayrollTemplateDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            return new KwlReturnObject(success, "", "-1", result, result.size());
        }
        }
        public KwlReturnObject getTemplateForEmp(HashMap<String, Object> requestParams){
        Boolean success = false;
        List result = null;

        try {
            String qry = "from userSalaryTemplateMap where userAccount.userID = ?";
            String userid = requestParams.get("userid").toString();
            result = HibernateUtil.executeQuery(hibernateTemplate, qry, new Object[]{userid});

            if (result.size() > 0) {
                success = true;
            }

        } catch (ServiceException ex) {
            Logger.getLogger(hrmsPayrollTemplateDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            return new KwlReturnObject(success, "", "-1", result, result.size());
        }
    }
    public KwlReturnObject getUserSalaryTemplateMap(HashMap<String, Object> requestParams){
        Boolean success = true;
        List result = null;
        int count=0;
        try {
            ArrayList name = null;
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            String hql = "from userSalaryTemplateMap";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null) {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
                int ind = hql.indexOf("(");
                if(ind>-1){
                    int index = Integer.valueOf(hql.substring(ind+1,ind+2));
                    hql = hql.replaceAll("("+index+")", value.get(index).toString());
                    value.remove(index);
                }
            }
            if(requestParams.get("append")!=null){
                hql += requestParams.get("append").toString();
                if(requestParams.get("append_values")!=null){
                    value.addAll(new ArrayList((List<Object>) requestParams.get("append_values")));
                }
            }
            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null) {
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }
            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null) {
                orderby = new ArrayList((List<String>) requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>) requestParams.get("order_type"));
                hql +=StringUtil.orderQuery(orderby, ordertype);
            }
            result = HibernateUtil.executeQuery(hibernateTemplate, hql, value.toArray());
            
            if (result.size() > 0) {
                success = true;
                count = result.size();
            }
            
        } catch (ServiceException ex) {
            success = false;
            Logger.getLogger(hrmsPayrollTemplateDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            return new KwlReturnObject(success, "", "-1", result, count);
        }
    }
    private void assignEmpToTemplate(JSONArray jarray,Template template){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	/*try {
            
            
            String templateid = template.getTemplateid();
            //String hql = "delete from userSalaryTemplateMap where salaryTemplate.templateid =  ?";
            HibernateUtil.executeUpdate(hibernateTemplate, hql, new Object[]{templateid});*/
            for (int cnt = 0; cnt < jarray.length(); cnt++) {
                try {
                    JSONObject userObject = new JSONObject(jarray.get(cnt).toString());
                    userSalaryTemplateMap userTemplateMap = null;
                    if(userObject.get("usertemplateid")!=null && !StringUtil.isNullOrEmpty(userObject.get("usertemplateid").toString())){
                    	userTemplateMap = hibernateTemplate.get(userSalaryTemplateMap.class, userObject.get("usertemplateid").toString());
                    }else{
                    	userTemplateMap = new userSalaryTemplateMap();
                    }
                    Useraccount uaccount = (Useraccount) hibernateTemplate.get(Useraccount.class, userObject.get("userid").toString());
                    Empprofile empprofileobj = (Empprofile) hibernateTemplate.get(Empprofile.class, userObject.get("userid").toString());
                    Double basic = 0.0;
                    if(!userObject.get("basic").equals(""))
                        basic= Double.parseDouble(userObject.get("basic").toString());
                    userTemplateMap.setEffectiveDate(sdf.parse(jarray.getJSONObject(cnt).get("effectiveDate").toString()));
                    userTemplateMap.setSalaryTemplate(template);
                    userTemplateMap.setUserAccount(uaccount);
                    userTemplateMap.setEmpProfile(empprofileobj);
                    userTemplateMap.setBasic(basic);
                    hibernateTemplate.save(userTemplateMap);

                } catch (ParseException ex) {
                    Logger.getLogger(hrmsPayrollTemplateDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JSONException ex) {
                    Logger.getLogger(hrmsPayrollTemplateDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
         /*} catch (ServiceException ex) {
            Logger.getLogger(hrmsPayrollTemplateDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    public KwlReturnObject deleteTempTemplate(HashMap<String, Object> requestParams) {
        boolean success = true;
        try {
            String tempid=requestParams.get("templateid").toString();
            TempTemplate temtemp = (TempTemplate) hibernateTemplate.get(TempTemplate.class, tempid);
            if (temtemp != null) {
                hibernateTemplate.delete(temtemp);
            }
        } catch(Exception e){
            success = false;
        } finally {
           return new KwlReturnObject(success, "", "-1", null,0);
        }
    }

    public KwlReturnObject editTemplateData(HashMap<String, Object> requestParams){
        boolean success = true;
        List<Template> tmp = new ArrayList<Template>();
        try{
            JSONObject jobj1 = new JSONObject(requestParams.get("formdata").toString());
            JSONArray jArray = new JSONArray(requestParams.get("assignedEmployees").toString());
            String templateid=requestParams.get("templateid").toString();
            Template template = (Template) hibernateTemplate.get(Template.class, templateid);
            template.setStartrange(jobj1.getString("RStart"));
            template.setEndrange(jobj1.getString("REnd"));
            template.setTemplatename(jobj1.getString("TName"));
            //Payinterval code - "Once a month" - 1, "Twice a month" - 2, "Once a week"- 3
            template.setPayinterval(jobj1.getInt("payInterval"));
            template.setEffdate(jobj1.getInt("effdate"));
            template.setShowborder(jobj1.getBoolean("showborder"));
            template.setStatus("0");
            hibernateTemplate.update(template);
            this.assignEmpToTemplate(jArray,template);
            tmp.add(template);
        } catch(Exception e){
            success = false;
        } finally {
           return new KwlReturnObject(success, "", "-1", tmp,0);
        }
    }

    public KwlReturnObject checkTemplateUserMap(HashMap<String, Object> requestParams){
        boolean success = true;
        List lst=null;
        try{
            String templateid=requestParams.get("templateid").toString();
            String qry = "from Useraccount ua where ua.templateid=?";
            lst = HibernateUtil.executeQuery(hibernateTemplate, qry, new Object[]{templateid});
        } catch(Exception e){
            success = false;
        } finally {
           return new KwlReturnObject(success, "", "-1", lst,lst.size());
        }
    }

    public KwlReturnObject setTemplateStatus(HashMap<String, Object> requestParams){
        boolean success = true;
        List <Template> lst= new ArrayList<Template>();
        try{
            String templateid=requestParams.get("templateid").toString();
            Template template = (Template) hibernateTemplate.get(Template.class, templateid);
            template.setStatus(requestParams.get("status").toString());
            lst.add(template);
            hibernateTemplate.update(template);
        } catch(Exception e){
            success = false;
        } finally {
           return new KwlReturnObject(success, "", "-1", lst,0);
        }
    }

    public KwlReturnObject setTempTemplateData(HashMap<String, Object> requestParams) {

       List<TempTemplate> tmp = new ArrayList<TempTemplate>();
        try {

            JSONObject jobj1 = new JSONObject(requestParams.get("formdata").toString());
            String tempid=requestParams.get("templateid").toString();
            String qry = "from TempTemplate where templateid=?";
            String tn = jobj1.getString("TName");
            String cmpic = requestParams.get("cmpic").toString();
            List lst = HibernateUtil.executeQuery(hibernateTemplate, qry, new Object[]{tempid});
            if (lst.size() > 0) {
                TempTemplate template1 = (TempTemplate) lst.get(0);
                template1.setStartrange(jobj1.getString("RStart"));
                template1.setEndrange(jobj1.getString("REnd"));
                template1.setTemplatename(jobj1.getString("TName"));
                hibernateTemplate.update(template1);
                tmp.add(template1);
            } else {
                TempTemplate template1 = new TempTemplate();
                template1.setTemplateid(tempid);
                template1.setStartrange(jobj1.getString("RStart"));
                template1.setEndrange(jobj1.getString("REnd"));
                template1.setTemplatename(jobj1.getString("TName"));
                hibernateTemplate.save(template1);
                tmp.add(template1);
            }


        } catch (Exception e) {
//            return ("{\"valid\":\"true\",data:{value:\"failed\"}}");
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(true, "Template added successfully.", "-1", tmp, 0);
        }
    }

    public KwlReturnObject deleteTemplate(HashMap<String, Object> requestParams) {
        boolean success = true;
        try {
            String tempid=requestParams.get("templateid").toString();
            Template temp = (Template) hibernateTemplate.get(Template.class, tempid);
            if (temp != null) {
                hibernateTemplate.delete(temp);
            }
        } catch(Exception e){
            success = false;
        } finally {
           return new KwlReturnObject(success, "", "-1", null,0);
        }
    }
    public KwlReturnObject getTemplate(HashMap<String,Object> requestParams) {
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
            hql = "from Template ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<Object>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }

            if(requestParams.get("append")!=null){
                hql += requestParams.get("append").toString();
            }

            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = new ArrayList((List<Object>)requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>)requestParams.get("order_type"));
                hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }
    
    
    @Override
    @SuppressWarnings({ "unchecked", "finally" })
	public List<Payhistory> getPayhistory(HashMap<String, Object> requestParams){
        List<Payhistory> payhistories = null;
        try{
            String hql = "from Payhistory where userID.userID = ? and template.templateid = ? and paycyclestart >= ? and paycycleend <= ?";
            payhistories = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{requestParams.get("userId"), requestParams.get("templateid"), requestParams.get("paycyclestart"), requestParams.get("paycycleend")});
        }catch(Exception e){
            e.printStackTrace();
        }finally{
           return payhistories;
        }
    }
    
    
    @Override
    @SuppressWarnings({ "unchecked", "finally" })
	public List<Historydetail> getPayHistorydetail(HashMap<String, Object> requestParams){
        List<Historydetail> historydetails = null;
        try{
            String hql = "from Historydetail where payhistory.historyid=? and name=?";
            historydetails = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{requestParams.get("payHistoryId"), requestParams.get("name")});
        }catch(Exception e){
            e.printStackTrace();
        }finally{
           return historydetails;
        }
    }

}
