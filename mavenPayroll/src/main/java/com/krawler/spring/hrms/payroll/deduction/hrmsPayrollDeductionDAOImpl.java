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

import com.krawler.common.admin.Company;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;
import java.util.List;
import masterDB.Deductionmaster;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import masterDB.TempTemplateMagDeduction;
import masterDB.Templatemapdeduction;
import masterDB.Template;
import masterDB.Wagemaster;

/**
 *
 * @author shs
 */
public class hrmsPayrollDeductionDAOImpl implements hrmsPayrollDeductionDAO{

    private HibernateTemplate hibernateTemplate;
    private static double deductotalval;

    public static double getDeductotalval() {
        return deductotalval;
    }

    public static void setDeductotalval(double deductotalval) {
        hrmsPayrollDeductionDAOImpl.deductotalval = deductotalval;
    }

    
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}


    public KwlReturnObject setDeductionData(HashMap<String, Object> requestParams) throws ServiceException {

        try {
            String code = requestParams.get("code").toString();
            String Cid = requestParams.get("Cid").toString();
            String name = requestParams.get("name").toString();
            String option = requestParams.get("option").toString();
            String rate = requestParams.get("rate").toString();
            String compute = "";
            String expr = requestParams.get("expr").toString();
            boolean isChecked = Boolean.parseBoolean(requestParams.get("isChecked").toString());

            if (requestParams.get("Action").equals("Add")) {
                String hql = "from Deductionmaster where Dcode=? and companydetails.companyID=?";
                Object[] obj2 = {code, Cid};
                List tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, obj2);
                int count = tabledata.size();
                Company cd = (Company) hibernateTemplate.get(Company.class, Cid);
                if (count > 0) {
                    return new KwlReturnObject(false,"{\"success\":\"false\",data:{value:\"exist\"}}","",null,0);
                } else {
                    String uids = java.util.UUID.randomUUID().toString();
                    Deductionmaster insertWage = new Deductionmaster();
                    insertWage.setDeductionid(uids);
                    insertWage.setCompanydetails(cd);
                    insertWage.setDeductiontype(name);
                    if (option.equals("Percent")) {
                        insertWage.setRate(1);
                        insertWage.setCash(Double.parseDouble(rate));
                    }
                    if (option.equals("Amount")) {
                        insertWage.setRate(0);
                        insertWage.setCash(Double.parseDouble(rate));
                    }
                    insertWage.setDcode(code);
                    Wagemaster depwageid = (Wagemaster) hibernateTemplate.get(Wagemaster.class, requestParams.get("depwageid").toString());
                    insertWage.setDepwageid(depwageid);
                    insertWage.setIsdefault(isChecked);
                    if(requestParams.containsKey("computeon")){
                        compute = requestParams.get("computeon").toString();
                        insertWage.setComputeon(Integer.parseInt(compute));
                    }
                   // insertWage.setComputeon(Integer.parseInt(compute));
                    insertWage.setExpr(expr);
                    hibernateTemplate.save(insertWage);
//                    ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new payroll component " +insertWage.getDeductiontype() + " of type Deduction"  ,request);
                }
                return new KwlReturnObject(true,"{\"success\":\"true\",data:{value:\"success\",action:\"Added\"}}","",null,0);
            } else {
                String typeid = requestParams.get("typeid").toString();
                String hql = "from Deductionmaster where Dcode=? and companydetails.companyID=? and deductionid !=?";
                Object[] obj2 = {code, Cid, typeid};
                List tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, obj2);
                int count = tabledata.size();
                Wagemaster depwageid = (Wagemaster) hibernateTemplate.get(Wagemaster.class, requestParams.get("depwageid").toString());
                Company cd = (Company) hibernateTemplate.get(Company.class, Cid);
                if (count > 0) {
                    return new KwlReturnObject(false,"{\"success\":\"false\",data:{value:\"exist\"}}","",null,0);
                } else {
                    hql = "from Templatemapdeduction t where t.deductionmaster.deductionid=?";
                    List lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{typeid});
                    Deductionmaster insertWage;
                    if (lst.size() > 0) {
                        insertWage = (Deductionmaster) hibernateTemplate.get(Deductionmaster.class, typeid);
                        int ratetype;
                        if (option.equals("Percent")) {
                            ratetype = 1;
                        } else {
                            ratetype = 0;
                        }
                        if (insertWage.getRate() != ratetype || insertWage.getCash() != Double.parseDouble(rate)||!insertWage.getDcode().equals(code)) {
                            return new KwlReturnObject(false,"{\"success\":\"false\",data:{value:\"assign\"}}","",null,0);
                        } else {
                            insertWage.setDeductiontype(name);
                            if(requestParams.containsKey("computeon")){
                                insertWage.setComputeon(Integer.parseInt(requestParams.get("computeon").toString()));
                            }
                            //insertWage.setComputeon(Integer.parseInt(compute));
                            insertWage.setExpr(expr);
                            hibernateTemplate.save(insertWage);
                        }
                    } else {
                        String query = "from Deductionmaster where deductionid=?";
                        List lst1 = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{typeid});
                        if(lst1.size() == 1){
                            insertWage = (Deductionmaster) hibernateTemplate.get(Deductionmaster.class, typeid);
                            insertWage.setDeductiontype(name);
                            if (option.equals("Percent")) {
                                insertWage.setRate(1);
                                insertWage.setCash(Double.parseDouble(rate));
                            }
                            if (option.equals("Amount")) {
                                insertWage.setRate(0);
                                insertWage.setCash(Double.parseDouble(rate));
                            }
                            insertWage.setDcode(code);
                            insertWage.setDepwageid(depwageid);
                            insertWage.setIsdefault(isChecked);
                            if(requestParams.containsKey("computeon")){
                                insertWage.setComputeon(Integer.parseInt(requestParams.get("computeon").toString()));
                            }
                            //insertWage.setComputeon(Integer.parseInt(compute));
                            insertWage.setExpr(expr);
                            hibernateTemplate.update(insertWage);
                        }
                    }
//                    ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited payroll component " +insertWage.getDeductiontype() ,request);
                }
                return new KwlReturnObject(true,"{\"success\":\"true\",data:{value:\"success\",action:\"Edited\"}}","",null,0);
            }

        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsPayrollDeductionDAOImpl.setDeductionData : " + e.getMessage(), e);
        }
        
        
    }

    public double getDeducRateInTemplate(HashMap<String, Object> requestParams) {
        double result = 0;
        List tabledata = null;
        try {
            String row = null;
            String templateid = (String)requestParams.get("templateid");
            String wageid = (String)requestParams.get("deductionid");
            String query1 = "select rate from Templatemapdeduction where template.templateid=? and deductionmaster.deductionid=?";
            tabledata = (List) HibernateUtil.executeQuery(hibernateTemplate, query1, new Object[]{templateid, wageid});
            row = (String)tabledata.get(0);
            result = Double.parseDouble(row);
        } catch (ServiceException ex) {
            System.out.println("Error is "+ex);
        } finally {
            return result;
        }
    }


    public KwlReturnObject deleteMasterDeduc(HashMap<String, Object> requestParams) {
        try {
            String id=requestParams.get("id").toString();
            String hql = "from Templatemapdeduction t where t.deductionmaster.deductionid=?";
            List lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
            List <String>rsltlist= new ArrayList<String>();
            if (lst.size() > 0) {
                return new KwlReturnObject(true,"{\"valid\":\"true\",data:{value:\"assign\"}}","",null,0);
            } else {

                Deductionmaster dm = (Deductionmaster) hibernateTemplate.get(Deductionmaster.class, id);
//                ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_DELETED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted payroll template  " + dm.getDeductiontype() + " of type Deduction" ,request);
                rsltlist.add(dm.getDeductiontype());
                hibernateTemplate.delete(dm);
            }
            return new KwlReturnObject(true,"{\"valid\":\"true\",data:{value:\"success\"}}","",rsltlist,0);
        } catch (Exception se) {
            return new KwlReturnObject(true,"{\"valid\":\"true\",data:{value:\"failed\"}}","",null,0);
        }
    }

    public KwlReturnObject getDeducTemplateCount(String id) throws ServiceException {
        try {
            String queryTax = "select count(*) from Templatemapdeduction as t where t.template.templateid=?";
            List lst = HibernateUtil.executeQuery(hibernateTemplate, queryTax, new Object[]{id});
            return new KwlReturnObject(true, "", "", null, Integer.parseInt(lst.get(0).toString()));
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsPayrollTemplateDAOImpl.getDeducTemplateCount : " + e.getMessage(), e);
        }
    }

    public KwlReturnObject setDeducTemplateData(HashMap<String, Object> requestParams, Template template) {
        boolean success = false;
        try {
            JSONObject jobjdeduc = new JSONObject(requestParams.get("deducdata").toString());
            com.krawler.utils.json.base.JSONArray jarraydeduc = jobjdeduc.getJSONArray("DeducDataADD");
            if (jarraydeduc.length() > 0) {
                for (int i = 0; i < jarraydeduc.length(); i++) {
                    String deducuid = java.util.UUID.randomUUID().toString();
                    Templatemapdeduction templatemapdeduction = new Templatemapdeduction();
                    Deductionmaster dm = (Deductionmaster) hibernateTemplate.get(Deductionmaster.class, jarraydeduc.getJSONObject(i).getString("DeducId"));
                    templatemapdeduction.setDeductionmaster(dm);
                    templatemapdeduction.setRate(jarraydeduc.getJSONObject(i).getString("DeducRate"));
                    templatemapdeduction.setId(deducuid);
                    templatemapdeduction.setTemplate(template);
                    hibernateTemplate.save(templatemapdeduction);
                }
            }
            success = true;
        } catch (Exception e) {
            System.out.println("Error is "+e);
            success = false;
        }
        return new KwlReturnObject(success, "Template added successfully.", "-1", null, 0);
    }

    public KwlReturnObject getDeductionMaster(HashMap<String, Object> requestParams) {
        List lst = null;
        int count = 0;
        try {
            String allflag = "true";
            ArrayList name = null;
            ArrayList value = null;
            if(requestParams.containsKey("allflag"))
                allflag = requestParams.get("allflag").toString();
            int start = 0;
            int limit = 0;
            if(allflag.equals("false")){
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            String hql = "";
            hql = "from Deductionmaster ";
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
            String[] searchcol;
            searchcol=new String[]{"deductiontype"};
            if(requestParams.get("append")!=null){
                hql += requestParams.get("append").toString();
            }
            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                        StringUtil.insertParamSearchString(value, ss, 1);
                        String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                        hql +=searchQuery;
                }
            }
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, value.toArray());
            count = lst.size();
            if(allflag.equals("false"))
                lst = HibernateUtil.executeQueryPaging(hibernateTemplate, hql, value.toArray(), new Integer[]{start, limit});
        } catch(Exception e){
            return new KwlReturnObject(false, "success", " ", lst, count);
        }
        return new KwlReturnObject(true, "success", " ", lst, count);

    }

    public KwlReturnObject getDefualtDeduction(HashMap<String, Object> requestParams) {
        JSONObject jobj = new JSONObject();
        boolean success = false;
        List lst2 = null;
        try {
            String query1 = "from Deductionmaster where isdefault=? and companydetails.companyID=? and (rate = 0 or (rate = 1 and computeon is not null)) ";
            String cid = (String)requestParams.get("cid");
            lst2 = (List) HibernateUtil.executeQuery(hibernateTemplate, query1, new Object[]{true, cid});
            success = true;
        } catch (ServiceException ex) {
            success = true;
        } finally {
            return new KwlReturnObject(success,"","",lst2,lst2.size());
        }
    }

     public KwlReturnObject getDeductionTemplateDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try{
            ArrayList orderby = null;
            ArrayList ordertype = null;
            ArrayList name = null;
            ArrayList value = null;
            String templateid =(String) requestParams.get("templateid");
            String hql = "from Templatemapdeduction ";
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
            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null) {
                orderby = new ArrayList((List<String>) requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>) requestParams.get("order_type"));
                hql +=StringUtil.orderQuery(orderby, ordertype);
            }
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, value.toArray());
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

     public KwlReturnObject getTempDeductionTemplateDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try{
            String templateid =(String) requestParams.get("templateid");
            String hql = "from TempTemplateMagDeduction as t where t.template.templateid=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{templateid});
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject deleteDeductionTemplateData(HashMap<String, Object> requestParams)  {
        boolean success = true;
        try {
            String templateid =(String) requestParams.get("templateid");
            String hql = "from Templatemapdeduction where templateid=?";
            List lst=HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{templateid});
            int x = 0;
            for (x = 0; x < lst.size(); x++) {
                Templatemapdeduction tx = (Templatemapdeduction) lst.get(x);
                hibernateTemplate.delete(tx);
            }
        } catch(Exception e) {
            success = false;
        } finally{
           return new KwlReturnObject(success, "", "-1", null,0);
        }
    }

    public KwlReturnObject deleteTempDeductionTemplateData(HashMap<String, Object> requestParams)  {
        boolean success = true;

        try {
            String templateid =(String) requestParams.get("templateid");
            String hql = "from TempTemplateMagDeduction where templateid=?";
            List lst=HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{templateid});
            int x = 0;
            for (x = 0; x < lst.size(); x++) {
                TempTemplateMagDeduction tx = (TempTemplateMagDeduction) lst.get(x);
                hibernateTemplate.delete(tx);
            }
        } catch(Exception e) {
            success = false;
        } finally{
           return new KwlReturnObject(success, "", "-1", null,0);
        }
    }

    public KwlReturnObject setTempDeductionTemplateDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        try{
            String templateid =(String) requestParams.get("templateid");
            JSONObject jobjdeduc = new JSONObject(requestParams.get("deducdata").toString());
            com.krawler.utils.json.base.JSONArray jarraydeduc = jobjdeduc.getJSONArray("DeducDataADD");
            for (int i = 0; i < jarraydeduc.length(); i++) {
                String deducuid = java.util.UUID.randomUUID().toString();
                TempTemplateMagDeduction templatemapdeduction = new TempTemplateMagDeduction();
                Deductionmaster dmas = (Deductionmaster) hibernateTemplate.get(Deductionmaster.class, jarraydeduc.getJSONObject(i).getString("DeducId"));
                templatemapdeduction.setDeductionmaster(dmas);
                templatemapdeduction.setRate(jarraydeduc.getJSONObject(i).getString("DeducRate"));
                templatemapdeduction.setId(deducuid);
                Template templ = (Template) hibernateTemplate.get(Template.class, templateid);
                templatemapdeduction.setTemplate(templ);
                hibernateTemplate.save(templatemapdeduction);
            }
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", null, 0);
        }
    }
}
