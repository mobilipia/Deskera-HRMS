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

import com.krawler.common.admin.Company;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;
import java.util.List;
import masterDB.Taxmaster;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import masterDB.TempTemplatemaptax;
import masterDB.Templatemaptax;
import masterDB.Template;
import masterDB.Wagemaster;

/**
 *
 * @author shs
 */
public class hrmsPayrollTaxDAOImpl implements hrmsPayrollTaxDAO {

    private HibernateTemplate hibernateTemplate;
    private static double deductotalval;

     public static double getDeductotalval() {
        return deductotalval;
    }

    public static void setDeductotalval(double deductotalval) {
        hrmsPayrollTaxDAOImpl.deductotalval = deductotalval;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public KwlReturnObject setTaxData(HashMap<String, Object> requestParams) throws ServiceException {

        try {
            String Cid = requestParams.get("Cid").toString();
            String option = requestParams.get("option").toString();
            String code = requestParams.get("code").toString();
            String name = requestParams.get("name").toString();
            String rate = requestParams.get("rate").toString();
//            String compute = requestParams.get("computeon").toString();
            String compute = "";
            String expr = requestParams.get("expr").toString();
            double rangefrom = 0;
            double rangeto = 0;
            boolean isChecked = Boolean.parseBoolean(requestParams.get("isChecked").toString());
            if (requestParams.get("Action").equals("Add")) {
//                String hql = "from Taxmaster where tcode=? and companydetails.companyID=?";
                String hql = "from Taxmaster where tcode=? and companydetails.companyID=? and (rate = 0 or (rate = 1 and computeon is not null)) ";
                Object[] obj2 = {code, Cid};
                List tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, obj2);
                int count = tabledata.size();
                Company cd = (Company) hibernateTemplate.get(Company.class, Cid);
                if (count > 0) {
                    return new KwlReturnObject(true, "{\"success\":\"false\",data:{value:\"exist\"}}", "", null, 0);
                } else {
                    String uids = java.util.UUID.randomUUID().toString();
                    Taxmaster insertWage = new Taxmaster();
                    insertWage.setTaxid(uids);
                    insertWage.setCompanydetails(cd);
                    insertWage.setTaxtype(name);
                    if (option.equals("Percent")) {
                        insertWage.setRate(1);
                        insertWage.setCash(Double.parseDouble(rate));
                    }
                    if (option.equals("Amount")) {
                        insertWage.setRate(0);
                        insertWage.setCash(Double.parseDouble(rate));
                    }
                    Wagemaster depwageid = (Wagemaster) hibernateTemplate.get(Wagemaster.class, requestParams.get("depwageid").toString());
                    if(requestParams.containsKey("computeon")){
                        compute = requestParams.get("computeon").toString();
                        insertWage.setComputeon(Integer.parseInt(compute));
                    }
                    insertWage.setDepwageid(depwageid);
                    insertWage.setTcode(code);
                    insertWage.setRangefrom(rangefrom);
                    insertWage.setRangeto(rangeto);
                    insertWage.setIsdefault(isChecked);
                    insertWage.setCategoryid("0");
                    //insertWage.setComputeon(Integer.parseInt(compute));
                    insertWage.setExpr(expr);
                    hibernateTemplate.save(insertWage);
//                    ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new payroll component " +insertWage.getTaxtype() + " of type Tax"  ,request);

                }
                return new KwlReturnObject(true, "{\"success\":\"true\",data:{value:\"success\",action:\"Added\"}}", "", null, 0);
            } else {
                String typeid = requestParams.get("typeid").toString();
                String hql = "from Taxmaster where tcode=? and companydetails.companyID=? and taxid !=?";
                Object[] obj2 = {code, Cid, typeid};
                List tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, obj2);
                int count = tabledata.size();
                Company cd = (Company) hibernateTemplate.get(Company.class, Cid);
                if (count > 0) {
                    return new KwlReturnObject(true, "{\"success\":\"false\",data:{value:\"exist\"}}", "", null, 0);
                } else {
                    hql = "from Templatemaptax t where t.taxmaster.taxid=?";
                    List lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{typeid});
                    Taxmaster insertWage;
                    if (lst.size() > 0) {
                        insertWage = (Taxmaster) hibernateTemplate.get(Taxmaster.class, typeid);
                        int ratetype;
                        if (option.equals("Percent")) {
                            ratetype = 1;
                        } else {
                            ratetype = 0;
                        }
                        if (insertWage.getRate() != ratetype || insertWage.getCash() != Double.parseDouble(rate) || !insertWage.getTcode().equals(code)) {
                            return new KwlReturnObject(true, "{\"success\":\"false\",data:{value:\"assign\"}}", "", null, 0);
                        } else {
                            insertWage.setTaxtype(name);
                            insertWage.setIsdefault(isChecked);
                            if(requestParams.containsKey("computeon")){
                                insertWage.setComputeon(Integer.parseInt(requestParams.get("computeon").toString()));
                            }
                           // insertWage.setComputeon(Integer.parseInt(compute));
                            insertWage.setExpr(expr);
                            hibernateTemplate.save(insertWage);
                        }
                    } else {
                        String query = "from Taxmaster where taxid=?";
                        List lst1 = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{typeid});
                        if (lst1.size() == 1) {
                            insertWage = (Taxmaster) hibernateTemplate.get(Taxmaster.class, typeid);
                            Wagemaster depwageid = (Wagemaster) hibernateTemplate.get(Wagemaster.class, requestParams.get("depwageid").toString());
                            insertWage.setTaxtype(name);
                            if (option.equals("Percent")) {
                                insertWage.setRate(1);
                                insertWage.setCash(Double.parseDouble(rate));
                            }
                            if (option.equals("Amount")) {
                                insertWage.setRate(0);
                                insertWage.setCash(Double.parseDouble(rate));
                            }
                            if(requestParams.containsKey("computeon")){
                                compute = requestParams.get("computeon").toString();
                                insertWage.setComputeon(Integer.parseInt(compute));
                            }
                            insertWage.setTcode(code);
                            insertWage.setRangefrom(rangefrom);
                            insertWage.setRangeto(rangeto);
                            insertWage.setIsdefault(isChecked);
                            insertWage.setDepwageid(depwageid);
                            insertWage.setCategoryid(requestParams.get("categoryid").toString());
                          //  insertWage.setComputeon(Integer.parseInt(compute));
                            insertWage.setExpr(expr);
                            hibernateTemplate.update(insertWage);
                        }
                    }
//                    ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited payroll component " +insertWage.getTaxtype() ,request);
                }
                return new KwlReturnObject(true, "{\"success\":\"true\",data:{value:\"success\",action:\"Edited\"}}", "", null, 0);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsPayrollWageDAOImpl.setWagesData : " + e.getMessage(), e);
        }
    }

    public KwlReturnObject deleteMasterTax(HashMap<String, Object> requestParams) {
        try {

            String id = requestParams.get("id").toString();
            String hql = "from Templatemaptax t where t.taxmaster.taxid=?";
            List lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
            List <String>rsltlist= new ArrayList<String>();
            if (lst.size() > 0) {
                return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"assign\"}}", "", null, 0);
            } else {

                Taxmaster tm = (Taxmaster) hibernateTemplate.get(Taxmaster.class, id);
//                ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_DELETED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted payroll template  " + tm.getTaxtype() + " of type Tax" ,request);
                rsltlist.add(tm.getTaxtype());
                hibernateTemplate.delete(tm);
            }
            return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"success\"}}", "", rsltlist, 0);
        } catch (Exception se) {
            return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"failed\"}}", "", null, 0);
        }
    }

    public double getTaxRateInTemplate(HashMap<String, Object> requestParams) {
        double result = 0;
        List tabledata = null;
        try {
            String row = null;
            String templateid = (String)requestParams.get("templateid");
            String wageid = (String)requestParams.get("taxid");
            String query1 = "select rate from Templatemaptax where template.templateid=? and taxmaster.taxid=?";
            tabledata = (List) HibernateUtil.executeQuery(hibernateTemplate, query1, new Object[]{templateid, wageid});
            row = (String)tabledata.get(0);
            result = Double.parseDouble(row);
        } catch (ServiceException ex) {
            System.out.println("Error is "+ex);
        } finally {
            return result;
        }
    }

    public KwlReturnObject GetTaxperCatgry(HashMap<String, Object> requestParams) throws ServiceException {

        List lst2 = null;
        int cnt = 0;
        try {
            String query1 = "";
            List lst = null;
            String cid = requestParams.get("cid").toString();
            String categoryid = requestParams.get("categoryid").toString();
            int start = 0;
            int limit = 15;

            if (!StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            if (StringUtil.equal(categoryid, "0")) {
                query1 = "from Taxmaster where  companydetails.companyID=? and tcode=?";
                lst = HibernateUtil.executeQuery(hibernateTemplate, query1, new Object[]{cid, "incometax"});
                lst2 = (List) HibernateUtil.executeQueryPaging(hibernateTemplate, query1, new Object[]{cid, "incometax"}, new Integer[]{start, limit});
            } else {
                query1 = "from Taxmaster where categoryid=? and companydetails.companyID=? and tcode=?";
                lst = HibernateUtil.executeQuery(hibernateTemplate, query1, new Object[]{categoryid, cid, "incometax"});
                lst2 = (List) HibernateUtil.executeQueryPaging(hibernateTemplate, query1, new Object[]{categoryid, cid, "incometax"}, new Integer[]{start, limit});
            }
            cnt = lst.size();

        } catch (Exception e) {
            e.printStackTrace();
            throw ServiceException.FAILURE("hrmsPayrollTaxDAOImpl.getTaxperCatgry : " + e.getMessage(), e);
        }

        return new KwlReturnObject(true, "success", "", lst2, cnt);
    }

    public KwlReturnObject setNewIncometax(HashMap<String, Object> requestParams) throws ServiceException {

        try {
            String cid = requestParams.get("cid").toString();
            double rangefrom = Double.parseDouble(requestParams.get("rangefrom").toString());
            double rangeto = Double.parseDouble(requestParams.get("rangeto").toString());
            String catg = requestParams.get("catgryid").toString();
            Company cd = (Company) hibernateTemplate.get(Company.class, cid);

            String uids = java.util.UUID.randomUUID().toString();
            Taxmaster insertTax = new Taxmaster();
            insertTax.setTaxid(uids);
            insertTax.setCompanydetails(cd);
            insertTax.setTaxtype(requestParams.get("categoryname").toString());
            insertTax.setRate(1);
            insertTax.setCash(Double.parseDouble(requestParams.get("rate").toString()));
            insertTax.setTcode("incometax");
            insertTax.setRangefrom(rangefrom);
            insertTax.setRangeto(rangeto);
            insertTax.setCategoryid(catg);
            hibernateTemplate.save(insertTax);

            return new KwlReturnObject(true, "{\"success\":\"true\",data:{value:\"success\"}}", "", null, 0);
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsPayrollTaxDAOImpl.setNewIncomeTax : " + e.getMessage(), e);
        }
    }

    public KwlReturnObject deleteincomeTax(HashMap<String, Object> requestParams) {
        try {

            Taxmaster tm = (Taxmaster) hibernateTemplate.get(Taxmaster.class, requestParams.get("typeid").toString());
            hibernateTemplate.delete(tm);

            return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"success\"}}", "", null, 0);
        } catch (Exception se) {
            return new KwlReturnObject(false, "{\"valid\":\"true\",data:{value:\"failed\"}}", "", null, 0);
        }
    }

    public KwlReturnObject getTaxTemplateCount(String id) throws ServiceException {
        try {
            String queryTax = "select count(*) from Templatemaptax as t where t.template.templateid=?";
            List lst = HibernateUtil.executeQuery(hibernateTemplate, queryTax, new Object[]{id});
            return new KwlReturnObject(true, "", "", null, Integer.parseInt(lst.get(0).toString()));
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsPayrollTemplateDAOImpl.getTaxTemplateCount : " + e.getMessage(), e);
        }
    }

    public KwlReturnObject setTaxTemplateData(HashMap<String, Object> requestParams, Template template) {
        boolean success = false;
        try {
            JSONObject jobjtax = new JSONObject(requestParams.get("taxdata").toString());
            com.krawler.utils.json.base.JSONArray jarraytax = jobjtax.getJSONArray("TaxDataADD");
            if (jarraytax.length() > 0) {
                for (int i = 0; i < jarraytax.length(); i++) {
                    String taxuid = java.util.UUID.randomUUID().toString();
                    Templatemaptax templatemaptax = new Templatemaptax();
                    templatemaptax.setId(taxuid);
                    Taxmaster tm = (Taxmaster) hibernateTemplate.get(Taxmaster.class, jarraytax.getJSONObject(i).getString("TaxId"));
                    templatemaptax.setTaxmaster(tm);
                    templatemaptax.setRate(jarraytax.getJSONObject(i).getString("TaxRate"));
                    templatemaptax.setTemplate(template);
                    hibernateTemplate.save(templatemaptax);
                }
            }
            success = true;
        } catch (Exception e) {
            System.out.println("Error is "+e);
            success = false;
        }
        finally{
            return new KwlReturnObject(success, "Template added successfully.", "-1", null, 0);
        }
    }

    public KwlReturnObject getTaxMaster(HashMap<String, Object> requestParams) {

        List lst = null;
        int count = 0;
        try {

            String tablename = "Taxmaster";
            String allflag = "true";
            if(requestParams.containsKey("allflag"))
                requestParams.get("allflag").toString();
            int start = 0;
            int limit = 0;
            if(allflag.equals("false")){
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            ArrayList params = new ArrayList();
            String hql = "";
            hql = "from Taxmaster where companydetails.companyID=? and tcode!='incometax'and (rate = 0 or (rate = 1 and computeon is not null)) ";
            params.add(requestParams.get("Cid").toString());
            String[] searchcol;
            searchcol=new String[]{"taxtype"};
            
            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                        StringUtil.insertParamSearchString(params, ss, 1);
                        String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                        hql +=searchQuery;
                }
            }
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, params.toArray());
            count = lst.size();
            if(allflag.equals("false"))
                lst = HibernateUtil.executeQueryPaging(hibernateTemplate, hql, params.toArray(), new Integer[]{start, limit});
        } catch(Exception e){
            e.printStackTrace();
            return new KwlReturnObject(false, "success", " ", lst, count);
        }
        return new KwlReturnObject(true, "success", " ", lst, count);

    }

    public KwlReturnObject getDefualtTax(HashMap<String, Object> requestParams) {
        JSONObject jobj = new JSONObject();
        boolean success = false;
        List lst2 = null;
        try {
            String query1 = "from Taxmaster where isdefault=? and companydetails.companyID=?";
            String cid = (String)requestParams.get("cid");
            lst2 = (List) HibernateUtil.executeQuery(hibernateTemplate, query1, new Object[]{true, cid});
            success = true;
        } catch (ServiceException ex) {
            success = true;
        } finally {
            return new KwlReturnObject(success,"","",lst2,lst2.size());
        }
    }

    public KwlReturnObject getTaxTemplateDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try{
            String templateid =(String) requestParams.get("templateid");
            ArrayList orderby = null;
            ArrayList ordertype = null;
            ArrayList name = null;
            ArrayList value = null;
            String hql = "from Templatemaptax ";
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

    public KwlReturnObject getTempTaxTemplateDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try{
            String templateid =(String) requestParams.get("templateid");
            String hql = "from TempTemplatemaptax as t where t.template.templateid=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{templateid});
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject deleteTaxTemplateData(HashMap<String, Object> requestParams)  {
        boolean success = true;

        try {
            String templateid =(String) requestParams.get("templateid");
            String hql = "from Templatemaptax where templateid=?";
            List lst=HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{templateid});
            int x = 0;
            for (x = 0; x < lst.size(); x++) {
                Templatemaptax tx = (Templatemaptax) lst.get(x);
                hibernateTemplate.delete(tx);
            }
        } catch(Exception e) {
            success = false;
        } finally{
           return new KwlReturnObject(success, "", "-1", null,0);
        }
    }

    public KwlReturnObject deleteTempTaxTemplateData(HashMap<String, Object> requestParams)  {
        boolean success = true;

        try {
            String templateid =(String) requestParams.get("templateid");
            String hql = "from TempTemplatemaptax where templateid=?";
            List lst=HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{templateid});
            int x = 0;
            for (x = 0; x < lst.size(); x++) {
                TempTemplatemaptax tx = (TempTemplatemaptax) lst.get(x);
                hibernateTemplate.delete(tx);
            }
        } catch(Exception e) {
            success = false;
        } finally{
           return new KwlReturnObject(success, "", "-1", null,0);
        }
    }

    public KwlReturnObject setTempTaxTemplateDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        try{
            String templateid =(String) requestParams.get("templateid");
            JSONObject jobjtax = new JSONObject(requestParams.get("taxdata").toString());
            com.krawler.utils.json.base.JSONArray jarraytax = jobjtax.getJSONArray("TaxDataADD");
            for (int i = 0; i < jarraytax.length(); i++) {
                String taxuid = java.util.UUID.randomUUID().toString();
                TempTemplatemaptax templatemaptax = new TempTemplatemaptax();
                templatemaptax.setId(taxuid);
                Taxmaster tmas = (Taxmaster) hibernateTemplate.get(Taxmaster.class, jarraytax.getJSONObject(i).getString("TaxId"));
                templatemaptax.setTaxmaster(tmas);
                templatemaptax.setRate(jarraytax.getJSONObject(i).getString("TaxRate"));
                Template templ = (Template) hibernateTemplate.get(Template.class, templateid);
                templatemaptax.setTemplate(templ);
                hibernateTemplate.save(templatemaptax);
            }
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", null, 0);
        }
    }

}
