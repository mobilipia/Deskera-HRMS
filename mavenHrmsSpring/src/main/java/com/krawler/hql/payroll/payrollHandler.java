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
package com.krawler.hql.payroll;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.utils.json.base.JSONObject;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import masterDB.Wagemaster;
import org.hibernate.Query;
import org.hibernate.Session;
import com.krawler.esp.hibernate.impl.HibernateUtil.*;
import com.krawler.hrms.master.MasterData;
import com.krawler.utils.json.base.JSONArray;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import masterDB.AudittrailPayroll;
import masterDB.Deductionmaster;
import masterDB.Historydetail;
import masterDB.Payhistory;

import masterDB.Taxmaster;
import masterDB.TempTemplate;
import masterDB.TempTemplateMagDeduction;
import masterDB.TempTemplatemaptax;
import masterDB.TempTemplatemapwage;
import masterDB.Template;
import masterDB.Templatemapdeduction;
import masterDB.Templatemaptax;
import masterDB.Templatemapwage;

import com.krawler.esp.handlers.ProfileHandler;
import com.krawler.common.admin.AuditAction;
import com.krawler.common.admin.Useraccount;
import com.krawler.hrms.ess.Empprofile;
import java.util.ArrayList;
import com.krawler.hrms.ess.Empprofile;
/**
 *
 * @author trainee
 */
public class payrollHandler {

    private static int w = 0;
    private static int t = 0;
    private static int m = 0;

    public String setWagesData(HttpServletRequest request, String name, String rate, String code, String Cid, Session session) throws SessionExpiredException, ServiceException {
        try {
            String option = request.getParameter("option");
            boolean isChecked = Boolean.parseBoolean(request.getParameter("isChecked"));
            String Cmpid = AuthHandler.getCompanyid(request);
            if (request.getParameter("Action").equals("Add")) {
                String hql = "from Wagemaster where wcode=? and companydetails.companyID=?";
                Object[] obj2 = {code, Cmpid};
                List tabledata = HibernateUtil.executeQuery(session, hql, obj2);
                int count = tabledata.size();
                Company cd = (Company) session.get(Company.class, Cid);
                if (count > 0) {
                    return "{\"success\":\"false\",data:{value:\"exist\"}}";
                } else {
                    String uids = java.util.UUID.randomUUID().toString();
                    Wagemaster insertWage = new Wagemaster();
                    insertWage.setWageid(uids);
                    insertWage.setCompanydetails(cd);
                    insertWage.setWagetype(name);
                    if (option.equals("Percent")) {
                        insertWage.setRate(1);
                        insertWage.setCash(Float.parseFloat(rate));
                    }
                    if (option.equals("Amount")) {
                        insertWage.setRate(0);
                        insertWage.setCash(Float.parseFloat(rate));
                    }
                    insertWage.setWcode(code);
                    insertWage.setIsdefault(isChecked);
                    session.save(insertWage);
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new payroll component " +insertWage.getWagetype() + " of type Wage"  ,request);
                }
                return ("{\"success\":\"true\",data:{value:\"success\",action:\"Added\"}}");
            } else {
                String typeid = request.getParameter("typeid");
                String hql = "from Wagemaster where wcode=? and companydetails.companyID=? and wageid !=?";
                Object[] obj2 = {code, Cmpid, typeid};
                List tabledata = HibernateUtil.executeQuery(session, hql, obj2);
                int count = tabledata.size();
                Company cd = (Company) session.get(Company.class, Cid);
                if (count > 0) {
                    return "{\"success\":\"false\",data:{value:\"exist\"}}";
                } else {
                    hql = "from Templatemapwage t where t.wagemaster.wageid=?";
                    List lst = HibernateUtil.executeQuery(session, hql, new Object[]{typeid});
                    Wagemaster insertWage;
                    if (lst.size() > 0) {
                        insertWage = (Wagemaster) session.get(Wagemaster.class, typeid);
                        int ratetype;
                        if (option.equals("Percent")) {
                            ratetype = 1;
                        } else {
                            ratetype = 0;
                        }
                        if (insertWage.getRate() != ratetype || insertWage.getCash() != Double.parseDouble(rate)|| !insertWage.getWcode().equals(code)) {
                            return ("{\"success\":\"false\",data:{value:\"assign\"}}");
                        } else {
                            insertWage.setWagetype(name);
                            session.save(insertWage);
                        }
                    } else {
                        Query query = session.createQuery("from Wagemaster where wageid=:typeid");
                        query.setString("typeid", typeid);
                        insertWage = (Wagemaster) query.uniqueResult();
                        insertWage.setWagetype(name);
                        if (option.equals("Percent")) {
                            insertWage.setRate(1);
                            insertWage.setCash(Double.parseDouble(rate));
                        }
                        if (option.equals("Amount")) {
                            insertWage.setRate(0);
                            insertWage.setCash(Double.parseDouble(rate));
                        }
                        insertWage.setWcode(code);
                        insertWage.setIsdefault(isChecked);
                        session.update(insertWage);
                    }
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited payroll component " +insertWage.getWagetype()  ,request);
                }
                return ("{\"success\":\"true\",data:{value:\"success\",action:\"Edited\"}}");
            }
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("setWagesData", se.COMPANYID_NULL);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("{\"success\":\"true\",data:{value:\"failed\"}}", ex);
        }
    }

    public String setTaxData(HttpServletRequest request, Session session) throws SessionExpiredException, ServiceException {
        try {
            String Cid = AuthHandler.getCompanyid(request);
            String option = request.getParameter("option");
            String code = request.getParameter("code");
            String name = request.getParameter("name");
            String rate = request.getParameter("rate");
            double rangefrom = 0;
            double rangeto = 0;
            boolean isChecked = Boolean.parseBoolean(request.getParameter("isChecked"));
            String Cmpid = AuthHandler.getCompanyid(request);
            if (request.getParameter("Action").equals("Add")) {
                String hql = "from Taxmaster where tcode=? and companydetails.companyID=?";
                Object[] obj2 = {code, Cmpid};
                List tabledata = HibernateUtil.executeQuery(session, hql, obj2);
                int count = tabledata.size();
                Company cd = (Company) session.get(Company.class, Cid);
                if (count > 0) {
                    return "{\"success\":\"false\",data:{value:\"exist\"}}";
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
                    insertWage.setTcode(code);
//                    insertWage.setRangefrom(rangefrom);
//                    insertWage.setRangeto(rangeto);
                    insertWage.setIsdefault(isChecked);
                    insertWage.setCategoryid("0");
                    session.save(insertWage);
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new payroll component " +insertWage.getTaxtype() + " of type Tax"  ,request);

                }
                return ("{\"success\":\"true\",data:{value:\"success\",action:\"Added\"}}");
            } else {
                String typeid = request.getParameter("typeid");
                String hql = "from Taxmaster where tcode=? and companydetails.companyID=? and taxid !=?";
                Object[] obj2 = {code, Cmpid, typeid};
                List tabledata = HibernateUtil.executeQuery(session, hql, obj2);
                int count = tabledata.size();
                Company cd = (Company) session.get(Company.class, Cid);
                if (count > 0) {
                    return "{\"success\":\"false\",data:{value:\"exist\"}}";
                } else {
                    hql = "from Templatemaptax t where t.taxmaster.taxid=?";
                    List lst = HibernateUtil.executeQuery(session, hql, new Object[]{typeid});
                    Taxmaster insertWage;
                    if (lst.size() > 0) {
                        insertWage = (Taxmaster) session.get(Taxmaster.class, typeid);
                        int ratetype;
                        if (option.equals("Percent")) {
                            ratetype = 1;
                        } else {
                            ratetype = 0;
                        }
                        if (insertWage.getRate() != ratetype || insertWage.getCash() != Double.parseDouble(rate)||!insertWage.getTcode().equals(code)) {
                            return ("{\"success\":\"false\",data:{value:\"assign\"}}");
                        } else {
                            insertWage.setTaxtype(name);
                            session.save(insertWage);
                        }
                    } else {
                        Query query = session.createQuery("from Taxmaster where taxid=:typeid");
                        query.setString("typeid", typeid);
                        insertWage = (Taxmaster) query.uniqueResult();
                        insertWage.setTaxtype(name);
                        if (option.equals("Percent")) {
                            insertWage.setRate(1);
                            insertWage.setCash(Double.parseDouble(rate));
                        }
                        if (option.equals("Amount")) {
                            insertWage.setRate(0);
                            insertWage.setCash(Double.parseDouble(rate));
                        }
                        insertWage.setTcode(code);
                       /* insertWage.setRangefrom(rangefrom);
                        insertWage.setRangeto(rangeto);*/
                        insertWage.setIsdefault(isChecked);
                        insertWage.setCategoryid(request.getParameter("categoryid"));
                        session.update(insertWage);
                    }
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited payroll component " +insertWage.getTaxtype() ,request);
                }
                return ("{\"success\":\"true\",data:{value:\"success\",action:\"Edited\"}}");
            }
        }catch (SessionExpiredException se) {
            throw new SessionExpiredException("setTaxData", se.COMPANYID_NULL);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("{\"success\":\"true\",data:{value:\"failed\"}}", ex);
        }
    }

    public String setDeductionData(HttpServletRequest request, String name, String rate, String code, String Cid, Session session) throws SessionExpiredException, ServiceException {
        try {
            String option = request.getParameter("option");
            boolean isChecked = Boolean.parseBoolean(request.getParameter("isChecked"));
            String Cmpid = AuthHandler.getCompanyid(request);
            if (request.getParameter("Action").equals("Add")) {
                String hql = "from Deductionmaster where Dcode=? and companydetails.companyID=?";
                Object[] obj2 = {code, Cmpid};
                List tabledata = HibernateUtil.executeQuery(session, hql, obj2);
                int count = tabledata.size();
                Company cd = (Company) session.get(Company.class, Cid);
                if (count > 0) {
                    return "{\"success\":\"false\",data:{value:\"exist\"}}";
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
                    insertWage.setIsdefault(isChecked);
                    session.save(insertWage);
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has added new payroll component " +insertWage.getDeductiontype() + " of type Deduction"  ,request);
                }
                return ("{\"success\":\"true\",data:{value:\"success\",action:\"Added\"}}");
            } else {
                String typeid = request.getParameter("typeid");
                String hql = "from Deductionmaster where Dcode=? and companydetails.companyID=? and deductionid !=?";
                Object[] obj2 = {code, Cmpid, typeid};
                List tabledata = HibernateUtil.executeQuery(session, hql, obj2);
                int count = tabledata.size();
                Company cd = (Company) session.get(Company.class, Cid);
                if (count > 0) {
                    return "{\"success\":\"false\",data:{value:\"exist\"}}";
                } else {
                    hql = "from Templatemapdeduction t where t.deductionmaster.deductionid=?";
                    List lst = HibernateUtil.executeQuery(session, hql, new Object[]{typeid});
                    Deductionmaster insertWage;
                    if (lst.size() > 0) {
                        insertWage = (Deductionmaster) session.get(Deductionmaster.class, typeid);
                        int ratetype;
                        if (option.equals("Percent")) {
                            ratetype = 1;
                        } else {
                            ratetype = 0;
                        }
                        if (insertWage.getRate() != ratetype || insertWage.getCash() != Double.parseDouble(rate)||!insertWage.getDcode().equals(code)) {
                            return ("{\"success\":\"false\",data:{value:\"assign\"}}");
                        } else {
                            insertWage.setDeductiontype(name);
                            session.save(insertWage);
                        }
                    } else {
                        Query query = session.createQuery("from Deductionmaster where deductionid=:typeid");
                        query.setString("typeid", typeid);
                        insertWage = (Deductionmaster) query.uniqueResult();
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
                        insertWage.setIsdefault(isChecked);
                        session.update(insertWage);
                    }
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has edited payroll component " +insertWage.getDeductiontype() ,request);
                }
                return ("{\"success\":\"true\",data:{value:\"success\",action:\"Edited\"}}");
            }

        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("setDeductionData", se.COMPANYID_NULL);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("{\"success\":\"true\",data:{value:\"failed\"}}", ex);
        }
    }

    public String setCompanyData(String name, String website, String address, String currencyid, Session session) {
        try {
            Query query = session.createQuery("from Company where companyName=:name ");
            query.setString("name", name);
            List<Company> lst = query.list();
            if (lst.size() > 0) {
                return "{\"valid\":\"true\",data:{value:\"exist\"}}";
            } else {
                //JSONObject jobj=new JSONObject(value);
                String uids = java.util.UUID.randomUUID().toString();
                Company insertCompany = new Company();
                KWLCurrency cur = (KWLCurrency) session.get(KWLCurrency.class, currencyid);
                insertCompany.setCompanyID(uids);
                insertCompany.setCompanyName(name);
                insertCompany.setWebsite(website);
                insertCompany.setAddress(address);
                KWLCurrency curn = (KWLCurrency) session.get(KWLCurrency.class, cur.getCurrencyID());
                insertCompany.setCurrency(curn);
                session.save(insertCompany);
                return ("{\"valid\":\"true\",data:{value:\"success\"}}");
            }

        } catch (Exception se) {
            return ("{\"valid\":\"true\",data:{value:\"failed\"}}");
        }
    }

    public String setTemplateData(HttpServletRequest req, Session session) {
        try {
            JSONObject jobj1 = new JSONObject(req.getParameter("formdata"));
            Query qry = session.createQuery("from Template where templatename=:tname and companyid=:cid");
            String tn = jobj1.getString("TName");
            String cmpic = AuthHandler.getCompanyid(req);
            qry.setString("tname", jobj1.getString("TName"));
            qry.setString("cid", cmpic);
            List ls = qry.list();
            if (ls.size() > 0) {
                return ("{\"valid\":\"true\",data:{value:\"Exist\"}}");
            } else {
                String tempuid = java.util.UUID.randomUUID().toString();
                Template template = new Template();
                MasterData gr = (MasterData) session.get(MasterData.class, jobj1.getString("GId"));
                template.setDesignationid(gr);
                template.setTemplateid(tempuid);
                template.setStartrange(jobj1.getString("RStart"));
                template.setEndrange(jobj1.getString("REnd"));
                template.setTemplatename(jobj1.getString("TName"));
                template.setCompanyid(cmpic);
                template.setStatus("0");
                session.save(template);


                JSONObject jobjdeduc = new JSONObject(req.getParameter("deducdata"));
                com.krawler.utils.json.base.JSONArray jarraydeduc = jobjdeduc.getJSONArray("DeducDataADD");
                if(jarraydeduc.length()>0){
                    for (int i = 0; i < jarraydeduc.length(); i++) {
                        String deducuid = java.util.UUID.randomUUID().toString();
                        Templatemapdeduction templatemapdeduction = new Templatemapdeduction();
                        Deductionmaster dm = (Deductionmaster) session.get(Deductionmaster.class, jarraydeduc.getJSONObject(i).getString("DeducId"));
                        templatemapdeduction.setDeductionmaster(dm);
                        templatemapdeduction.setRate(jarraydeduc.getJSONObject(i).getString("DeducRate"));
                        templatemapdeduction.setId(deducuid);
                        templatemapdeduction.setTemplate(template);
                        session.save(templatemapdeduction);
                    }
                }
                JSONObject jobjtax = new JSONObject(req.getParameter("taxdata"));
                com.krawler.utils.json.base.JSONArray jarraytax = jobjtax.getJSONArray("TaxDataADD");
                if(jarraytax.length()>0){
                    for (int i = 0; i < jarraytax.length(); i++) {
                        String taxuid = java.util.UUID.randomUUID().toString();
                        Templatemaptax templatemaptax = new Templatemaptax();
                        templatemaptax.setId(taxuid);
                        Taxmaster tm = (Taxmaster) session.get(Taxmaster.class, jarraytax.getJSONObject(i).getString("TaxId"));
                        templatemaptax.setTaxmaster(tm);
                        templatemaptax.setRate(jarraytax.getJSONObject(i).getString("TaxRate"));
                        templatemaptax.setTemplate(template);
                        session.save(templatemaptax);
                    }
                }
                JSONObject jobjwage = new JSONObject(req.getParameter("wagedata"));
                com.krawler.utils.json.base.JSONArray jarraywage = jobjwage.getJSONArray("WageDataADD");
                for (int i = 0; i < jarraywage.length(); i++) {
                    String wageuid = java.util.UUID.randomUUID().toString();
                    Templatemapwage templatemapwage = new Templatemapwage();
                    Wagemaster wm = (Wagemaster) session.get(Wagemaster.class, jarraywage.getJSONObject(i).getString("WageId"));
                    templatemapwage.setTemplate(template);
                    templatemapwage.setWagemaster(wm);
                    templatemapwage.setRate(jarraywage.getJSONObject(i).getString("WageRate"));
                    templatemapwage.setId(wageuid);
                    session.save(templatemapwage);
                }
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_TEMPLATE_ADDED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(req)) + " has added new payroll template   " +template.getTemplatename()  ,req);
                return ("{\"valid\":\"true\",data:{value:\"success\"}}");
            }


        } catch (Exception se) {
            return ("{\"valid\":\"true\",data:{value:\"failed\"}}");
        }
    }

    public String updateTemplateData(HttpServletRequest req, Session session) {

        try {
            int torp = Integer.parseInt(req.getParameter("torp"));
            if (torp == 1) {
                JSONObject jobj1 = new JSONObject(req.getParameter("formdata"));

                String tempid = req.getParameter("tempid");

                //@@ code not used. Commented by Ajay
//                Query query = session.createQuery("from Template t,User nu where t.templateid=:templateid and nu.templateid=:templateid");
//                query.setString("templateid", tempid);
//                List lst = query.list();
                    Query query = session.createQuery("from Templatemapdeduction where templateid=:templateid");
                    query.setString("templateid", tempid);
                    List lst = query.list();
                    int x = 0;
                    for (x = 0; x < lst.size(); x++) {
                        Templatemapdeduction tx = (Templatemapdeduction) lst.get(x);
                        session.delete(tx);
                    }
                    query = session.createQuery("from Templatemaptax where templateid=:templateid");
                    query.setString("templateid", tempid);
                    lst = query.list();
                    for (x = 0; x < lst.size(); x++) {
                        Templatemaptax tx = (Templatemaptax) lst.get(x);
                        session.delete(tx);
                    }
                    query = session.createQuery("from Templatemapwage where templateid=:templateid");
                    query.setString("templateid", tempid);
                    lst = query.list();
                    for (x = 0; x < lst.size(); x++) {
                        Templatemapwage tx = (Templatemapwage) lst.get(x);
                        session.delete(tx);
                    }
                    query = session.createQuery("from TempTemplateMagDeduction where templateid=:templateid");
                    query.setString("templateid", tempid);
                    lst = query.list();
                    for (x = 0; x < lst.size(); x++) {
                        TempTemplateMagDeduction tx = (TempTemplateMagDeduction) lst.get(x);
                        session.delete(tx);
                    }
                    query = session.createQuery("from TempTemplatemaptax where templateid=:templateid");
                    query.setString("templateid", tempid);
                    lst = query.list();
                    for (x = 0; x < lst.size(); x++) {
                        TempTemplatemaptax tx = (TempTemplatemaptax) lst.get(x);
                        session.delete(tx);
                    }
                    query = session.createQuery("from TempTemplatemapwage where templateid=:templateid");
                    query.setString("templateid", tempid);
                    lst = query.list();
                    for (x = 0; x < lst.size(); x++) {
                        TempTemplatemapwage tx = (TempTemplatemapwage) lst.get(x);
                        session.delete(tx);
                    }
                    TempTemplate temtemp = (TempTemplate) session.get(TempTemplate.class, tempid);
                    if (temtemp != null) {
                        session.delete(temtemp);
                    }

                    //delete End
                    String tempuid = tempid;
                    query = session.createQuery("from Template where templateid=:templateid");
                    query.setString("templateid", tempid);
                    Template template = (Template) query.uniqueResult();
                    MasterData gr = (MasterData) session.get(MasterData.class, jobj1.getString("GId"));
                    template.setStartrange(jobj1.getString("RStart"));
                    template.setEndrange(jobj1.getString("REnd"));
                    template.setTemplatename(jobj1.getString("TName"));
                    template.setStatus("0");
                    session.update(template);


                    JSONObject jobjdeduc = new JSONObject(req.getParameter("deducdata"));
                    com.krawler.utils.json.base.JSONArray jarraydeduc = jobjdeduc.getJSONArray("DeducDataADD");
                    if (jarraydeduc.length() > 0) {
                        for (int i = 0; i < jarraydeduc.length(); i++) {
                            String deducuid = java.util.UUID.randomUUID().toString();
                            Templatemapdeduction templatemapdeduction = new Templatemapdeduction();
                            Deductionmaster dmas = (Deductionmaster) session.get(Deductionmaster.class, jarraydeduc.getJSONObject(i).getString("DeducId"));
                            templatemapdeduction.setDeductionmaster(dmas);
                            templatemapdeduction.setRate(jarraydeduc.getJSONObject(i).getString("DeducRate"));
                            templatemapdeduction.setId(deducuid);
                            Template templ = (Template) session.get(Template.class, tempuid);
                            templatemapdeduction.setTemplate(templ);
                            session.save(templatemapdeduction);
                        }
                    }

                    JSONObject jobjtax = new JSONObject(req.getParameter("taxdata"));
                    com.krawler.utils.json.base.JSONArray jarraytax = jobjtax.getJSONArray("TaxDataADD");
                    if(jarraytax.length()>0){
                        for (int i = 0; i < jarraytax.length(); i++) {
                            String taxuid = java.util.UUID.randomUUID().toString();
                            Templatemaptax templatemaptax = new Templatemaptax();
                            templatemaptax.setId(taxuid);
                            Taxmaster tmas = (Taxmaster) session.get(Taxmaster.class, jarraytax.getJSONObject(i).getString("TaxId"));
                            templatemaptax.setTaxmaster(tmas);
                            templatemaptax.setRate(jarraytax.getJSONObject(i).getString("TaxRate"));
                            Template templ = (Template) session.get(Template.class, tempuid);
                            templatemaptax.setTemplate(templ);
                            session.save(templatemaptax);
                        }
                    }
                    JSONObject jobjwage = new JSONObject(req.getParameter("wagedata"));
                    com.krawler.utils.json.base.JSONArray jarraywage = jobjwage.getJSONArray("WageDataADD");
                    if (jarraywage.length() > 0) {
                        for (int i = 0; i < jarraywage.length(); i++) {
                            String wageuid = java.util.UUID.randomUUID().toString();
                            Templatemapwage templatemapwage = new Templatemapwage();
                            Template templ = (Template) session.get(Template.class, tempuid);
                            templatemapwage.setTemplate(templ);
                            Wagemaster wmas = (Wagemaster) session.get(Wagemaster.class, jarraywage.getJSONObject(i).getString("WageId"));
                            templatemapwage.setWagemaster(wmas);
                            templatemapwage.setRate(jarraywage.getJSONObject(i).getString("WageRate"));
                            templatemapwage.setId(wageuid);
                            session.save(templatemapwage);
                        }
                    }
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_TEMPLATE_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(req)) + " has edited template  " +template.getTemplatename() +" permanently" ,req);
                    return ("{\"valid\":\"true\",data:{value:\"success\"}}");

                
            } else if (torp == 2) {
                JSONObject jobj1 = new JSONObject(req.getParameter("formdata"));
                String tempid = req.getParameter("tempid");

                
//                Query query = session.createQuery("from Template t,User nu where t.templateid=:templateid and nu.templateid=:templateid");
//                query.setString("templateid", tempid);
//                List lst = query.list();
                
                // @@ Useraccount query changed by Ajay
                Query query = session.createQuery("from Useraccount ua where ua.templateid=:templateid");
                query.setString("templateid", tempid);
                List lst = query.list();

                if (lst.size() > 0) {
                    return ("{\"valid\":\"true\",data:{value:\"either template doesn't exist or user assigned to this template\"}}");
                } else {
                    query = session.createQuery("from TempTemplateMagDeduction where templateid=:templateid");
                    query.setString("templateid", tempid);
                    lst = query.list();
                    int x = 0;
                    for (x = 0; x < lst.size(); x++) {
                        TempTemplateMagDeduction tx = (TempTemplateMagDeduction) lst.get(x);
                        session.delete(tx);
                    }
                    query = session.createQuery("from TempTemplatemaptax where templateid=:templateid");
                    query.setString("templateid", tempid);
                    lst = query.list();
                    for (x = 0; x < lst.size(); x++) {
                        TempTemplatemaptax tx = (TempTemplatemaptax) lst.get(x);
                        session.delete(tx);
                    }
                    query = session.createQuery("from TempTemplatemapwage where templateid=:templateid");
                    query.setString("templateid", tempid);
                    lst = query.list();
                    for (x = 0; x < lst.size(); x++) {
                        TempTemplatemapwage tx = (TempTemplatemapwage) lst.get(x);
                        session.delete(tx);
                    }
                    String tempuid = tempid;
                    query = session.createQuery("from Template where templateid=:templateid");
                    query.setString("templateid", tempid);
                    Template template = (Template) query.uniqueResult();
                    template.setStatus("1");
                    session.update(template);
                    query = session.createQuery("from TempTemplate where templateid=:templateid");
                    query.setString("templateid", tempid);
                    List templst = (List) query.list();
                    if (templst.size() > 0) {
                        TempTemplate template1 = (TempTemplate) templst.get(0);
                        //template1.setGroup((Group1) session.get(Group1.class, jobj1.getString("GId")));
                        template1.setStartrange(jobj1.getString("RStart"));
                        template1.setEndrange(jobj1.getString("REnd"));
                        template1.setTemplatename(jobj1.getString("TName"));
                        session.update(template1);
                    } else {
                        TempTemplate template1 = new TempTemplate();
                        template1.setTemplateid(tempuid);
                        // template1.setGroup((Group1) session.get(Group1.class, jobj1.getString("GId")));
                        template1.setStartrange(jobj1.getString("RStart"));
                        template1.setEndrange(jobj1.getString("REnd"));
                        template1.setTemplatename(jobj1.getString("TName"));
                        session.save(template1);
                    }
                    JSONObject jobjdeduc = new JSONObject(req.getParameter("deducdata"));
                    com.krawler.utils.json.base.JSONArray jarraydeduc = jobjdeduc.getJSONArray("DeducDataADD");
                    for (int i = 0; i < jarraydeduc.length(); i++) {
                        String deducuid = java.util.UUID.randomUUID().toString();
                        TempTemplateMagDeduction templatemapdeduction = new TempTemplateMagDeduction();
                        Deductionmaster dmas = (Deductionmaster) session.get(Deductionmaster.class, jarraydeduc.getJSONObject(i).getString("DeducId"));
                        templatemapdeduction.setDeductionmaster(dmas);
                        templatemapdeduction.setRate(jarraydeduc.getJSONObject(i).getString("DeducRate"));
                        templatemapdeduction.setId(deducuid);
                        Template templ = (Template) session.get(Template.class, tempuid);
                        templatemapdeduction.setTemplate(templ);
                        session.save(templatemapdeduction);
                    }

                    JSONObject jobjtax = new JSONObject(req.getParameter("taxdata"));
                    com.krawler.utils.json.base.JSONArray jarraytax = jobjtax.getJSONArray("TaxDataADD");
                    for (int i = 0; i < jarraytax.length(); i++) {
                        String taxuid = java.util.UUID.randomUUID().toString();
                        TempTemplatemaptax templatemaptax = new TempTemplatemaptax();
                        templatemaptax.setId(taxuid);
                        Taxmaster tmas = (Taxmaster) session.get(Taxmaster.class, jarraytax.getJSONObject(i).getString("TaxId"));
                        templatemaptax.setTaxmaster(tmas);
                        templatemaptax.setRate(jarraytax.getJSONObject(i).getString("TaxRate"));
                        Template templ = (Template) session.get(Template.class, tempuid);
                        templatemaptax.setTemplate(templ);
                        session.save(templatemaptax);
                    }

                    JSONObject jobjwage = new JSONObject(req.getParameter("wagedata"));
                    com.krawler.utils.json.base.JSONArray jarraywage = jobjwage.getJSONArray("WageDataADD");
                    for (int i = 0; i < jarraywage.length(); i++) {
                        String wageuid = java.util.UUID.randomUUID().toString();
                        TempTemplatemapwage templatemapwage = new TempTemplatemapwage();
                        Template templ = (Template) session.get(Template.class, tempuid);
                        templatemapwage.setTemplate(templ);
                        Wagemaster wmas = (Wagemaster) session.get(Wagemaster.class, jarraywage.getJSONObject(i).getString("WageId"));
                        templatemapwage.setWagemaster(wmas);
                        templatemapwage.setRate(jarraywage.getJSONObject(i).getString("WageRate"));
                        templatemapwage.setId(wageuid);
                        session.save(templatemapwage);
                    }
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_TEMPLATE_EDITED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(req)) + " has edited template  " +template.getTemplatename() +" temporarily" ,req);
                    return ("{\"valid\":\"true\",data:{value:\"success\"}}");

                }
            } else {
                return ("ret:unsaved");
            }
        } catch (Exception se) {
            return ("failed");
        }
    }

    public String AssignEmptoTemp(String[] empidarr, String tid, Session session) {
        try {
            int i = 0;
            Query query = session.createQuery("from Useraccount where templateid=:tid");
            query.setString("tid", tid);
            List lst = query.list();
            for (i = 0; i < lst.size(); i++) {
                Useraccount ua = (Useraccount) lst.get(i);
                //Useraccount ua = (Useraccount) session.get(Useraccount.class, nu.getUserID());
                ua.setTemplateid("0");
                session.update(ua);
//                session.update(nu);
            }
            if (empidarr != null) {
                Template tem = (Template) session.get(Template.class, tid);
                for (i = 0; i < empidarr.length; i++) {
                    User insertC = (User) session.createQuery("from User where userID='" + empidarr[i] + "'").uniqueResult();
                    Useraccount ua = (Useraccount) session.get(Useraccount.class, insertC.getUserID());
                    Float a = Float.parseFloat(tem.getStartrange());
                    Float b = Float.parseFloat(ua.getSalary());
                    Float c = Float.parseFloat(tem.getEndrange());
                    if (Float.parseFloat(tem.getStartrange()) <= Float.parseFloat(ua.getSalary()) && Float.parseFloat(tem.getEndrange()) >= Float.parseFloat(ua.getSalary())) {
                        ua.setTemplateid(tid);
                        session.update(insertC);
                    } else {
                        return ("{\"valid\":\"true\",data:{value:\"1\"}}");
                    }
                }
            }
            return ("{\"valid\":\"true\",data:{value:\"success\"}}");
        } catch (Exception se) {
            return ("{\"valid\":\"true\",data:{value:\"failed\"}}");
        }
    }

    public String addToTrail(String typeofwork, String IP, String payrollCMPID, String userid, Session session) {
        try {
            Date dt = new Date();
            String uids = java.util.UUID.randomUUID().toString();
            AudittrailPayroll audit = new AudittrailPayroll();
            audit.setId(uids);
            audit.setTypeofwork(typeofwork);
            audit.setIp(IP);
            audit.setCompanyid(payrollCMPID);
            audit.setUserid(userid);
            audit.setDate(dt);
            session.save(audit);
            return ("{\"valid\":\"true\",data:{value:\"success\"}}");
        } catch (Exception se) {
            return ("{\"valid\":\"true\",data:{value:\"failed\"}}");
        }
    }

    public String setPayHistory(HttpServletRequest request, String stdate, String enddate, String tempid, String empid, String empname, String design, String gross, String net, String wagetot, String taxtot, String deductot, String month, String WageJson, String DeducJson, String TaxJson, Session session) {
        try {
            int i = 0;
            String histid = java.util.UUID.randomUUID().toString();
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            Date d2 = new Date(request.getParameter("stdate"));
            Date d3 = new Date(request.getParameter("enddate"));
            DateFormat fo = new SimpleDateFormat("MMMM");
            String months = fo.format(d2);
            Query query = session.createQuery("from Payhistory  where  userID=:empid and createdfor = :d3");           
            query.setString("d3", df.format(d3));
            query.setString("empid", empid);

            List lst = query.list();
            if (lst.size() != 0) {
                return ("{\"valid\":\"false\",data:{value:\"failed\"}}");
            } else {
                Payhistory payhist = new Payhistory();//histid,empid,empname,design,gross,net,wagetot,taxtot,deductot,month,null,tempid,gross,null,null,null);
                User newuser = (User) session.get(User.class, empid);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, newuser.getUserID());
                Empprofile emp = (Empprofile) session.get(Empprofile.class,empid);
                if(emp==null || emp.getJoindate()==null||d2.before(emp.getJoindate())){
                    return ("{\"valid\":\"false\",data:{value:\"join date failed\"}}");
                }
                Template temlate = (Template) session.get(Template.class, tempid);
                payhist.setHistoryid(histid);
                payhist.setUserID(newuser);
                payhist.setTemplate(temlate);
                payhist.setName(empname);
                payhist.setDesign(design);
                payhist.setDepartment(ua.getDepartment().getValue());
                payhist.setGross(gross);
                payhist.setNet(net);
                payhist.setWagetot(wagetot);
                payhist.setDeductot(deductot);
                payhist.setTaxtot(taxtot);
                payhist.setStatus("1");
                payhist.setCreatedon(d2);
                payhist.setCreatedfor(d3);
                payhist.setPaymonth(months);
                payhist.setGeneratedon(new Date());

                String jsondata2 = WageJson;
                JSONArray jarr2 = new JSONArray("[" + jsondata2 + "]");

                for (i = 0; i < jarr2.length(); i++) {
                    JSONObject jobj2 = jarr2.getJSONObject(i);
                    Historydetail newobj1 = new Historydetail();
                    Payhistory newobj2 = new Payhistory();
                    String primid = java.util.UUID.randomUUID().toString();
                    newobj1.setPrimid(primid);
                    newobj2.setHistoryid(histid);
                    newobj1.setPayhistory(newobj2);
                    newobj1.setName("Wages");
                    newobj1.setType(jobj2.getString("type"));
                    newobj1.setAmount(jobj2.getString("amount"));
                    newobj1.setRate(jobj2.getString("Rate"));
                    session.save(newobj1);

                }
                String jsondata3 = DeducJson;
                JSONArray jarr3 = new JSONArray("[" + jsondata3 + "]");

                for (i = 0; i < jarr3.length(); i++) {
                    JSONObject jobj3 = jarr3.getJSONObject(i);
                    Historydetail newobj1 = new Historydetail();
                    Payhistory newobj2 = new Payhistory();
                    String primid = java.util.UUID.randomUUID().toString();
                    newobj1.setPrimid(primid);
                    newobj2.setHistoryid(histid);
                    newobj1.setPayhistory(newobj2);
                    newobj1.setName("Deduction");
                    newobj1.setType(jobj3.getString("type"));
                    newobj1.setAmount(jobj3.getString("amount"));
                    newobj1.setRate(jobj3.getString("Rate"));
                    session.save(newobj1);

                }
                String jsondata4 = TaxJson;
                JSONArray jarr4 = new JSONArray("[" + jsondata4 + "]");

                for (i = 0; i < jarr4.length(); i++) {
                    JSONObject jobj4 = jarr4.getJSONObject(i);
                    Historydetail newobj1 = new Historydetail();
                    Payhistory newobj2 = new Payhistory();
                    String primid = java.util.UUID.randomUUID().toString();
                    newobj1.setPrimid(primid);
                    newobj2.setHistoryid(histid);
                    newobj1.setPayhistory(newobj2);
                    newobj1.setName("Taxes");
                    newobj1.setType(jobj4.getString("type"));
                    newobj1.setAmount(jobj4.getString("amount"));
                    newobj1.setRate(jobj4.getString("Rate"));
                    session.save(newobj1);
                }
                session.save(payhist);
                return ("{\"valid\":\"true\",data:{value:\"success\"}}");
            }
        } catch (Exception se) {
            return ("{\"valid\":\"false\",data:{value:\"failed\"}}");
        } finally {
        }
    }

    public String setPayrollforTemp(HttpServletRequest request, Session session) {

        String msg = "";
        int count = 0,flag=0;
        try {
            int x = 0;
            String jsondata2 = request.getParameter("jsondata");
            JSONArray jarr2 = new JSONArray("[" + jsondata2 + "]");
            for (x = 0; x < jarr2.length(); x++) {
                int i = 0;
                String Wagejsondata = "";
                String Taxjsondata = "";
                String Deducjsondata = "";
                double wagetotalvalf = 0;
                double deductotalvalf = 0;
                double wagedeductotf = 0;
                double amtot = 0;
                int trmLen1 = 0;
                JSONObject jobj2 = jarr2.getJSONObject(x);
                String salary = jobj2.getString("FixedSal");
                String tempid = request.getParameter("TempId");
                JSONObject jsAray = new JSONObject();                                
                Query queryTemplate = session.createQuery("from Templatemapwage w where w.template.templateid='" + tempid + "'");
                List iteratorWage = queryTemplate.list();

                while (i < iteratorWage.size()) {
                    Templatemapwage wm = (Templatemapwage) iteratorWage.get(i);
                    Wagejsondata += "{'type':'" + wm.getWagemaster().getWagetype() + "',";
                    if (wm.getWagemaster().getRate() == 0) {
                        Wagejsondata += "'Rate':'" + wm.getWagemaster().getRate() + "',";
                        Wagejsondata += "'amount':'" + wm.getRate() + "',";
                        amtot += Double.parseDouble(wm.getRate());
                    } else {
                        double amount = (Double.parseDouble(wm.getRate()) * Double.parseDouble(salary)) / 100;
                        amtot += Double.parseDouble(wm.getRate()) * Double.parseDouble(salary) / 100;
                        Wagejsondata += "'Rate':'" + wm.getRate() + "',";
                        Wagejsondata += "'amount':'" + amount + "',";
                    }
                    wagetotalvalf = amtot;
                    Wagejsondata += "'amtot':'" + amtot + "'},";
                    i++;
                }
                trmLen1 = Wagejsondata.length() - 1;
                Wagejsondata = Wagejsondata.substring(0, trmLen1);
                queryTemplate = session.createQuery("from Templatemapdeduction w where w.template.templateid='" + tempid + "'");
                iteratorWage = queryTemplate.list();
                i = 0;
                amtot = 0;
                if (iteratorWage.size() > 0) {
                    while (i < iteratorWage.size()) {
                        Templatemapdeduction wm = (Templatemapdeduction) iteratorWage.get(i);
                        Deducjsondata += "{'type':'" + wm.getDeductionmaster().getDeductiontype() + "',";
                        if (wm.getDeductionmaster().getRate() == 0) {
                            Deducjsondata += "'Rate':'" + wm.getDeductionmaster().getRate() + "',";
                            Deducjsondata += "'amount':'" + wm.getRate() + "',";
                            amtot += Double.parseDouble(wm.getRate());
                        } else {
                            double amount = (Double.parseDouble(wm.getRate()) * Double.parseDouble(salary)) / 100;
                            amtot += (Double.parseDouble(wm.getRate()) * Double.parseDouble(salary)) / 100;
                            Deducjsondata += "'Rate':'" + wm.getRate() + "',";
                            Deducjsondata += "'amount':'" + amount + "',";
                        }
                        deductotalvalf = amtot;
                        wagedeductotf = wagetotalvalf - deductotalvalf;
                        Deducjsondata += "'amtot':'" + amtot + "'},";
                        i++;
                    }
                    trmLen1 = Deducjsondata.length() - 1;
                    Deducjsondata = Deducjsondata.substring(0, trmLen1);
                }
                queryTemplate = session.createQuery("from Templatemaptax as w where w.template.templateid='" + tempid + "'");
                iteratorWage = queryTemplate.list();
                i = 0;
                amtot = 0;
                if (iteratorWage.size() > 0) {
                    while (i < iteratorWage.size()) {
                        Templatemaptax wm = (Templatemaptax) iteratorWage.get(i);
                        Taxjsondata += "{'type':'" + wm.getTaxmaster().getTaxtype() + "',";
                        if (wm.getTaxmaster().getRate() == 0) {
                            Taxjsondata += "'Rate':'" + wm.getTaxmaster().getRate() + "',";
                            Taxjsondata += "'amount':'" + wm.getRate() + "',";
                            amtot += Double.parseDouble(wm.getRate());
                        } else {
                            double amount = (Double.parseDouble(wm.getRate()) * Double.parseDouble(salary)) / 100;
                            amtot += (Double.parseDouble(wm.getRate()) * Double.parseDouble(salary)) / 100;
                            Taxjsondata += "'Rate':'" + wm.getRate() + "',";
                            Taxjsondata += "'amount':'" + amount + "',";
                        }
                        Taxjsondata += "'amtot':'" + amtot + "'},";
                        i++;
                    }
                    trmLen1 = Taxjsondata.length() - 1;
                    Taxjsondata = Taxjsondata.substring(0, trmLen1);
                }
                double net = 0;
                net = wagetotalvalf - deductotalvalf - amtot;
                String netval = String.valueOf(net);
                String wagetotal = String.valueOf(wagetotalvalf);
                String taxtotal = String.valueOf(amtot);
                String deductotal = String.valueOf(deductotalvalf);
                String stdate = request.getParameter("stdate");
                String enddate = request.getParameter("enddate");
                msg = setPayHistory(request, stdate, enddate, tempid, jobj2.getString("empid"), jobj2.getString("EName"), jobj2.getString("design"), jobj2.getString("FixedSal"), netval, wagetotal, taxtotal, deductotal, Wagejsondata, Wagejsondata, Deducjsondata, Taxjsondata, session);
                if (msg.equals("{\"valid\":\"true\",data:{value:\"success\"}}")) {
                    flag=1;
                    count++;
                    //@@ProfileHandler.insertAuditLog(session, AuditAction.SALARY_GENERATED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has generated salary for  " +jobj2.getString("EName")  ,request);
                } else if(msg.equals("{\"valid\":\"false\",data:{value:\"join date failed\"}}")&&flag!=1) {
                    flag=2;
                }
            }
            if (count == 0 && flag!=2) {
                return ("{\"valid\":\"true\",data:{value:\"assign\",msg:\"Selected employees salary already generated for this month.\"}}");
            } else if (flag==2){
                return ("{\"valid\":\"true\",data:{value:\"assign\",msg:\"Employee not eligible for salary generation. Please check joining date.\"}}");
            }
            return ("{\"valid\":\"true\",data:{value:\"success\",msg:\"Salary generated successfully for " + count + " employee(s).\"}}");
        } catch (Exception se) {
            return ("failed");
        }
    }

    public String deleteTemplateData(String[] tempid, Session session,HttpServletRequest request) {
        try {
            String template="";
            List lst=null;
            for(int j=0;j<tempid.length;j++){
            String hql = "select userID from Useraccount where templateid=? and user.deleteflag=?";
             lst = HibernateUtil.executeQuery(session, hql, new Object[]{tempid[j], 0});
            if (lst.size() > 0) {
                Template tmp = (Template) session.get(Template.class, tempid[j]);
                template+=""+tmp.getTemplatename()+",";
            }
            }
            if(!StringUtil.isNullOrEmpty(template)){
                template=template.substring(0,template.length()-1);
               return ("{\"valid\":\"true\",data:{value:\"assign\",templates:\""+template.toString()+"\"}}");
            }
            else {
                 for(int j1=0;j1<tempid.length;j1++){
                Template tmp = (Template) session.get(Template.class, tempid[j1]);
                lst = HibernateUtil.executeQuery(session, "from Templatemapdeduction t where t.template.templateid=?", tempid[j1]);
                int i = 0;
                while (i < lst.size()) {
                    Templatemapdeduction td = (Templatemapdeduction) lst.get(i);
                    session.delete(td);
                    i++;
                }
                lst = HibernateUtil.executeQuery(session, "from Templatemaptax t where t.template.templateid=?", tempid[j1]);
                i = 0;
                while (i < lst.size()) {
                    Templatemaptax td = (Templatemaptax) lst.get(i);
                    session.delete(td);
                    i++;
                }
                lst = HibernateUtil.executeQuery(session, "from Templatemapwage t where t.template.templateid=?", tempid[j1]);
                i = 0;
                while (i < lst.size()) {
                    Templatemapwage td = (Templatemapwage) lst.get(i);
                    session.delete(td);
                    i++;
                }
                lst = HibernateUtil.executeQuery(session, "from Payhistory t where t.template.templateid=?", tempid[j1]);
                i = 0;
                while (i < lst.size()) {
                    Payhistory td = (Payhistory) lst.get(i);
                    List lst1 = HibernateUtil.executeQuery(session, "from Historydetail t where t.payhistory.historyid=?", td.getHistoryid());
                    int ii = 0;
                    while (ii < lst1.size()) {
                        Historydetail hd = (Historydetail) lst1.get(ii);
                        session.delete(hd);
                        ii++;
                    }
                    session.delete(td);
                    i++;
                }
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_TEMPLATE_DELETED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted payroll template  " + tmp.getTemplatename() ,request);
                session.delete(tmp);
            }
                 return ("{\"valid\":\"true\",data:{value:\"success\"}}");
            }                         
        } catch (Exception se) {
            return ("{\"valid\":\"false\",data:{value:\"failed\"}}");
        }
    }

    public String deleteMasterTax(String id, Session session,HttpServletRequest request) {
        try {
            String hql = "from Templatemaptax t where t.taxmaster.taxid=?";
            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{id});
            if (lst.size() > 0) {
                return ("{\"valid\":\"true\",data:{value:\"assign\"}}");//+lst.size());
            } else {

                Taxmaster tm = (Taxmaster) session.get(Taxmaster.class, id);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_DELETED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted payroll template  " + tm.getTaxtype() + " of type Tax" ,request);
                session.delete(tm);
            }
            return ("{\"valid\":\"true\",data:{value:\"success\"}}");
        } catch (Exception se) {
            return ("{\"valid\":\"true\",data:{value:\"failed\"}}");
        }
    }

    public String deleteincomeTax(String id, Session session) {
        try {

            Taxmaster tm = (Taxmaster) session.get(Taxmaster.class, id);
            session.delete(tm);

            return ("{\"valid\":\"true\",data:{value:\"success\"}}");
        } catch (Exception se) {
            return ("{\"valid\":\"true\",data:{value:\"failed\"}}");
        }
    }

    public String deleteMasterWage(String id, Session session,HttpServletRequest request) {
        try {
            String hql = "from Templatemapwage t where t.wagemaster.wageid=?";
            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{id});
            if (lst.size() > 0) {
                return ("{\"valid\":\"true\",data:{value:\"assign\"}}");//+lst.size());
            } else {

                Wagemaster wm = (Wagemaster) session.get(Wagemaster.class, id);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_DELETED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted payroll template  " + wm.getWagetype() + " of type Wage" ,request);
                session.delete(wm);
            }
            return ("{\"valid\":\"true\",data:{value:\"success\"}}");
        } catch (Exception se) {
            return ("{\"valid\":\"true\",data:{value:\"failed\"}}");
        }
    }

    public String deleteMasterDeduc(String id, Session session,HttpServletRequest request) {
        try {
            String hql = "from Templatemapdeduction t where t.deductionmaster.deductionid=?";
            List lst = HibernateUtil.executeQuery(session, hql, new Object[]{id});
            if (lst.size() > 0) {
                return ("{\"valid\":\"true\",data:{value:\"assign\"}}");//+lst.size());
            } else {

                Deductionmaster dm = (Deductionmaster) session.get(Deductionmaster.class, id);
                //@@ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_DELETED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted payroll template  " + dm.getDeductiontype() + " of type Deduction" ,request);
                session.delete(dm);
            }
            return ("{\"valid\":\"true\",data:{value:\"success\"}}");
        } catch (Exception se) {
            return ("{\"valid\":\"true\",data:{value:\"failed\"}}");
        }
    }

    public String setNewIncometax(HttpServletRequest request, Session session) throws SessionExpiredException {
        try {
            String cid = AuthHandler.getCompanyid(request);
            String option = request.getParameter("option");
            double rangefrom = Double.parseDouble(request.getParameter("rangefrom"));
            double rangeto = Double.parseDouble(request.getParameter("rangeto"));
            String catg = request.getParameter("catgryid");
            Company cd = (Company) session.get(Company.class, cid);

            String uids = java.util.UUID.randomUUID().toString();
            Taxmaster insertTax = new Taxmaster();
            insertTax.setTaxid(uids);
            insertTax.setCompanydetails(cd);
            insertTax.setTaxtype(request.getParameter("categoryname"));
            insertTax.setRate(1);
            insertTax.setCash(Double.parseDouble(request.getParameter("rate")));
            insertTax.setTcode("incometax");
//            insertTax.setRangefrom(rangefrom);
//            insertTax.setRangeto(rangeto);
            insertTax.setCategoryid(request.getParameter("catgryid"));
            session.save(insertTax);
            return ("{\"success\":\"true\",data:{value:\"success\"}}");

        } catch (SessionExpiredException ex) {
            throw new SessionExpiredException("COMPANYID_NULL", ex.COMPANYID_NULL);
        }
    }

    public static String empPayhist(Session session, HttpServletRequest request) throws ServiceException {

        String msg = "";
        int count = 0;
        try {
            String empid = request.getParameter("empid");
            String histid = UUID.randomUUID().toString();
            String gross = request.getParameter("gross");
            String net = request.getParameter("net");
            String tempid = request.getParameter("tempid");
            String wagetot = request.getParameter("wagetot");
            String taxtot = request.getParameter("taxtot");
            String deductot = request.getParameter("deductot");
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            Date d2 = new Date(request.getParameter("stdate"));
            Date d3 = new Date(request.getParameter("enddate"));
            DateFormat fo = new SimpleDateFormat("MMMM");
            String months = fo.format(d2);
            Query query = session.createQuery("from Payhistory  where  userID=:empid and createdon between :d2 and :d3 and createdfor between :d2 and :d3");
            query.setString("d2", df.format(d2));
            query.setString("d3", df.format(d3));
            query.setString("empid", empid);
            List lst = query.list();
            if (lst.size() != 0) {
                return ("{\"valid\":\"true\",data:{value:\"assign\",msg:\"Selected employees salary already generated for this month.\"}}");
            } else {
                Payhistory payhist = new Payhistory();
                User newuser = (User) session.get(User.class, empid);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, empid);
                Empprofile emp = (Empprofile) session.get(Empprofile.class,empid);
                if(emp==null || emp.getJoindate()==null||d2.before(emp.getJoindate())){
                    return ("{\"valid\":\"true\",data:{value:\"assign\",msg:\"Employee not eligible for salary generation. Please check joining date.\"}}");
                }
                Template temlate = (Template) session.get(Template.class, tempid);
                payhist.setHistoryid(histid);
                payhist.setUserID(newuser);
                payhist.setTemplate(temlate);
                payhist.setName(AuthHandler.getFullName(newuser));
                payhist.setDesign(ua.getDesignationid().getValue());
                payhist.setDepartment(ua.getDepartment().getValue());
                payhist.setGross(gross);
                payhist.setNet(net);
                payhist.setWagetot(wagetot);
                payhist.setDeductot(deductot);
                payhist.setTaxtot(taxtot);
                payhist.setStatus("1");
                payhist.setCreatedon(d2);
                payhist.setCreatedfor(d3);
                payhist.setPaymonth(months);
                payhist.setGeneratedon(new Date());
                session.save(payhist);

                String jsondata2 = request.getParameter("WageJson");
                JSONArray jarr2 = new JSONArray("[" + jsondata2 + "]");
                if (jarr2.length() > 0) {
                    for (int i = 0; i < jarr2.length(); i++) {
                        JSONObject jobj2 = jarr2.getJSONObject(i);
                        Historydetail newobj1 = new Historydetail();
                        String primid = java.util.UUID.randomUUID().toString();
                        newobj1.setPrimid(primid);
                        newobj1.setPayhistory(payhist);
                        newobj1.setName("Wages");
                        newobj1.setType(jobj2.getString("type"));
                        newobj1.setAmount(jobj2.getString("amount"));
                        newobj1.setRate(jobj2.getString("Rate"));
                        session.save(newobj1);
                    }
                }

                String jsondata3 = request.getParameter("DeducJson");
                JSONArray jarr3 = new JSONArray("[" + jsondata3 + "]");
                if (jarr3.length() > 0) {
                    for (int i = 0; i < jarr3.length(); i++) {
                        JSONObject jobj2 = jarr3.getJSONObject(i);
                        Historydetail newobj1 = new Historydetail();
                        String primid = java.util.UUID.randomUUID().toString();
                        newobj1.setPrimid(primid);
                        newobj1.setPayhistory(payhist);
                        newobj1.setName("Deduction");
                        newobj1.setType(jobj2.getString("type"));
                        newobj1.setAmount(jobj2.getString("amount"));
                        newobj1.setRate(jobj2.getString("Rate"));
                        session.save(newobj1);
                    }
                }

                String jsondata4 = request.getParameter("taxesJson");
                JSONArray jarr4 = new JSONArray("[" + jsondata4 + "]");
                if (jarr4.length() > 0) {
                    for (int i = 0; i < jarr4.length(); i++) {
                        JSONObject jobj2 = jarr4.getJSONObject(i);
                        Historydetail newobj1 = new Historydetail();
                        String primid = java.util.UUID.randomUUID().toString();
                        newobj1.setPrimid(primid);
                        newobj1.setPayhistory(payhist);
                        newobj1.setName("Taxes");
                        newobj1.setType(jobj2.getString("type"));
                        newobj1.setAmount(jobj2.getString("amount"));
                        newobj1.setRate(jobj2.getString("Rate"));
                        session.save(newobj1);
                    }
                }
                //@@ProfileHandler.insertAuditLog(session, AuditAction.SALARY_GENERATED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has generated salary for  " +AuthHandler.getFullName(newuser)  ,request);
                return ("{\"valid\":\"true\",data:{value:\"success\",msg:\"Salary generated successfully for the employee.\"}}");
            }

        } catch (Exception se) {
            return ("failed");
        }
    }
}
