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
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Query;
import org.hibernate.Session;
import com.krawler.esp.hibernate.impl.HibernateUtil.*;
import com.krawler.utils.json.base.JSONArray;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import masterDB.AudittrailPayroll;
import masterDB.TempTemplateMagDeduction;
import masterDB.TempTemplatemaptax;
import masterDB.TempTemplatemapwage;
import masterDB.Template;
import masterDB.Templatemapdeduction;
import masterDB.Templatemaptax;
import masterDB.Templatemapwage;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.locale.MessageSourceProxy;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.LocaleUtil;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.hrms.master.MasterData;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.ArrayList;

public class payrollManager {

    private static double wagetotalval;
    private static double deductotalval;
    private static double wagedeductot;
    private static int w;
    private static int t = 11;
    private static int d = 7;
    private int mylstsize = 0;

    public String getPayComponent(Session session, HttpServletRequest request) {
        try {
            String tablename = request.getParameter("tablename");
            String Cid = AuthHandler.getCompanyid(request);
            int start = Integer.parseInt(request.getParameter("start"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            String hql = "";
            if (tablename.equals("Taxmaster")) {
                hql = "from Taxmaster where companydetails.companyID=? and tcode!='incometax'";
            } else {
                hql = "from " + tablename + " where companydetails.companyID=?";
            }
            params.add(Cid);
            String[] searchcol;
            if (tablename.equals("Wagemaster")) {
                searchcol=new String[]{"wagetype"};
            }else if (tablename.equals("Taxmaster")) {
                searchcol=new String[]{"taxtype"};
            }else{
                searchcol=new String[]{"deductiontype"};
            }
            if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    hql +=searchQuery;
            }
            List tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
            int count = tabledata.size();
            List lst = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            int cnt = lst.size();
            JSONObject jobj = new JSONObject();
            if (tablename.equals("Wagemaster")) {
                for (int i = 0; i < lst.size(); i++) {
                    masterDB.Wagemaster wage = (masterDB.Wagemaster) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("type", wage.getWagetype());
                    jobjtemp.put("ratetype", wage.getRate());
                    jobjtemp.put("cash", wage.getCash());
                    jobjtemp.put("id", wage.getWageid());
                    jobjtemp.put("code", wage.getWcode());
                    jobjtemp.put("isdefault", wage.isIsdefault());
                    jobj.append("data", jobjtemp);
                }
                if (lst.size() == 0) {
                    jobj.put("data", "");
                }
            }
            if (tablename.equals("Taxmaster")) {
                for (int i = 0; i < lst.size(); i++) {
                    masterDB.Taxmaster wage = (masterDB.Taxmaster) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("type", wage.getTaxtype());
                    jobjtemp.put("ratetype", wage.getRate());
                    jobjtemp.put("cash", wage.getCash());
                    jobjtemp.put("id", wage.getTaxid());
                    jobjtemp.put("code", wage.getTcode());
                    jobjtemp.put("isdefault", wage.isIsdefault());
                    jobjtemp.put("rangefrom", !Double.isNaN(wage.getRangefrom())  ? wage.getRangefrom() : 0);
                    jobjtemp.put("rangeto", !Double.isNaN(wage.getRangeto()) ? wage.getRangeto() : 0);
                    jobjtemp.put("category", wage.getCategoryid() != null ? wage.getCategoryid() : "");
                    jobj.append("data", jobjtemp);
                }
                if (lst.size() == 0) {
                    jobj.put("data", "");
                }
            }
            if (tablename.equals("Deductionmaster")) {
                for (int i = 0; i < lst.size(); i++) {
                    masterDB.Deductionmaster wage = (masterDB.Deductionmaster) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("type", wage.getDeductiontype());
                    jobjtemp.put("ratetype", wage.getRate());
                    jobjtemp.put("cash", wage.getCash());
                    jobjtemp.put("id", wage.getDeductionid());
                    jobjtemp.put("code", wage.getDcode());
                    jobjtemp.put("isdefault", wage.isIsdefault());
                    jobj.append("data", jobjtemp);
                }
                if (lst.size() == 0) {
                    jobj.put("data", "");
                }
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", count);
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getWagesData(Session session) {
        try {
            Query query = session.createQuery("from masterDB.Wagemaster");
            List lst = (List) query.list();
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst.size(); i++) {
                masterDB.Wagemaster wage = (masterDB.Wagemaster) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("WagesType", wage.getWagetype());
                jobjtemp.put("WagesRate", wage.getRate());
                jobjtemp.put("WagesId", wage.getWageid());
                jobjtemp.put("WagesCode", wage.getWcode());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getWagesData(String Cid, String st, String lim, Session session) {
        try {
            String hql = "from masterDB.Wagemaster where companydetails.companyID=?";
            Object[] obj2 = {Cid};
            List lst = HibernateUtil.executeQuery(session, hql, obj2);
            int count = lst.size();
            JSONObject jobj = new JSONObject();
            int i = 0;
            int x = 0;
            for (i = 0; i < lst.size(); i++) {
                masterDB.Wagemaster wage = (masterDB.Wagemaster) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("WagesType", wage.getWagetype());
                jobjtemp.put("WagesRate", wage.getCash());
                jobjtemp.put("ratetype", wage.getRate());
                jobjtemp.put("WagesId", wage.getWageid());
                jobjtemp.put("WagesCode", wage.getWcode());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());

        } finally {
        }
    }

    public String getWagesData(String Cid, String temid, Session session) {

        try {
            Template templ = (Template) session.get(Template.class, temid);
            JSONObject jobj = new JSONObject();
            int tc = 0;
            if (Integer.parseInt(templ.getStatus().toString()) == 0) {
                String query = "from Templatemapwage w where w.template.templateid=?";
                List lst = (List) HibernateUtil.executeQuery(session, query, new Object[]{temid});
                int i;
                for (i = 0; i < lst.size(); i++) {
                    masterDB.Templatemapwage wage = (masterDB.Templatemapwage) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("assigned", "1");
                    jobjtemp.put("WagesType", wage.getWagemaster().getWagetype());
                    jobjtemp.put("WagesRate", wage.getRate());
                    jobjtemp.put("ratetype", wage.getWagemaster().getRate());
                    jobjtemp.put("WagesId", wage.getWagemaster().getWageid());
                    jobjtemp.put("WagesCode", wage.getWagemaster().getWcode());
                    jobj.append("data", jobjtemp);
                }
                tc = i;
            } else if (Integer.parseInt(templ.getStatus().toString()) == 1) {
                String query = "select t.wagemaster.wageid,t.rate,t.wagemaster.wagetype,t.wagemaster.wcode from TempTemplatemapwage t where t.template.templateid=? and t.wagemaster.companydetails.companyID=?";
                List lst = (List) HibernateUtil.executeQuery(session, query, new Object[]{temid, Cid});
                int i;
                for (i = 0; i < lst.size(); i++) {
                    Object[] obj = (Object[]) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("WagesType", obj[2]);
                    jobjtemp.put("assigned", "1");
                    jobjtemp.put("WagesRate", obj[1]);
                    jobjtemp.put("WagesId", obj[0]);
                    jobjtemp.put("WagesCode", obj[3]);
                    jobj.append("data", jobjtemp);
                }
                tc = i;
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", tc);
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getTaxData(Session session) {
        try {
            Query query = session.createQuery("from masterDB.Taxmaster");
            List lst = (List) query.list();
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst.size(); i++) {
                masterDB.Taxmaster tax = (masterDB.Taxmaster) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("TaxType", tax.getTaxtype());
                jobjtemp.put("TaxRate", tax.getRate());
                jobjtemp.put("TaxId", tax.getTaxid());
                jobjtemp.put("TaxCode", tax.getTcode());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getTaxData(String Cid, String st, String lim, Session session) {
        try {
            Query query = session.createQuery("from masterDB.Taxmaster where companydetails.companyID=:cid");
            query.setString("cid", Cid);
            List lst = (List) query.list();
            JSONObject jobj = new JSONObject();
            int i = 0;
            int x = 0;
            for (i = 0; i < lst.size(); i++) {
                masterDB.Taxmaster tax = (masterDB.Taxmaster) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("TaxType", tax.getTaxtype());
                jobjtemp.put("TaxRate", tax.getCash());
                jobjtemp.put("ratetype", tax.getRate());
                jobjtemp.put("TaxId", tax.getTaxid());
                jobjtemp.put("TaxCode", tax.getTcode());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getTaxData(String Cid, String tempid, Session session) {
        try {
            Template templ = (Template) session.get(Template.class, tempid);
            JSONObject jobj = new JSONObject();
            int tc = 0;
            if (Integer.parseInt(templ.getStatus().toString()) == 0) {

                String query = "from Templatemaptax w where w.template.templateid=?";
                List lst = (List) HibernateUtil.executeQuery(session, query, new Object[]{tempid});
                tc=lst.size();
                int i;
                for (i = 0; i < lst.size(); i++) {
                    masterDB.Templatemaptax wage = (masterDB.Templatemaptax) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("assigned", "1");
                    jobjtemp.put("TaxType", wage.getTaxmaster().getTaxtype());
                    jobjtemp.put("TaxRate", wage.getRate());
                    jobjtemp.put("ratetype", wage.getTaxmaster().getRate());
                    jobjtemp.put("TaxId", wage.getTaxmaster().getTaxid());
                    jobjtemp.put("TaxCode", wage.getTaxmaster().getTcode());
                    jobj.append("data", jobjtemp);
                }
                if(lst.size()==0){
                  jobj.put("data","");
                }
            } else if (Integer.parseInt(templ.getStatus().toString()) == 1) {
                Query query = session.createQuery("select t.taxmaster.taxid,t.rate,t.taxmaster.taxtype,t.taxmaster.tcode from TempTemplatemaptax t where t.template.templateid=:tempid and t.taxmaster.companydetails.companyID=:cid");
                query.setString("tempid", tempid);
                query.setString("cid", Cid);
                List lst1 = (List) query.list();
                tc=lst1.size();
                int i;
                for (i = 0; i < lst1.size(); i++) {
                    Object[] object = (Object[]) lst1.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("assigned", "1");
                    jobjtemp.put("TaxType", object[2]);
                    jobjtemp.put("TaxRate", object[1]);
                    jobjtemp.put("TaxId", object[0]);
                    jobjtemp.put("TaxCode", object[3]);
                    jobj.append("data", jobjtemp);
                }
                if(lst1.size()==0){
                  jobj.put("data","");
                }
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", tc);
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getDeductionData(Session session) {
        try {
            Query query = session.createQuery("from masterDB.Deductionmaster");
            List lst = (List) query.list();
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst.size(); i++) {
                masterDB.Deductionmaster deduc = (masterDB.Deductionmaster) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("DeductionType", deduc.getDeductiontype());
                jobjtemp.put("DeducRate", deduc.getRate());
                jobjtemp.put("DeducId", deduc.getDeductionid());
                jobjtemp.put("DeducCode", deduc.getDcode());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getDeductionData(String Cid, String st, String lim, Session session) {
        try {
            Query query = session.createQuery("from masterDB.Deductionmaster where companydetails.companyID=:cid");
            query.setString("cid", Cid);
            List lst = (List) query.list();
            JSONObject jobj = new JSONObject();
            int i = 0, x = 0;
            for (i = 0; i < lst.size(); i++) {
                masterDB.Deductionmaster deduc = (masterDB.Deductionmaster) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("DeductionType", deduc.getDeductiontype());
                jobjtemp.put("DeducRate", deduc.getCash());
                jobjtemp.put("ratetype", deduc.getRate());
                jobjtemp.put("DeducId", deduc.getDeductionid());
                jobjtemp.put("DeducCode", deduc.getDcode());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", i);
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getDeductionData(String Cid, String tempid, Session session) {
        try {
            Template templ = (Template) session.get(Template.class, tempid);
            JSONObject jobj = new JSONObject();
            int i = 0;
            if (Integer.parseInt(templ.getStatus().toString()) == 0) {
                String query = "from Templatemapdeduction w where w.template.templateid=?";
                List lst = (List) HibernateUtil.executeQuery(session, query, new Object[]{tempid});
                for (i = 0; i < lst.size(); i++) {
                    masterDB.Templatemapdeduction wage = (masterDB.Templatemapdeduction) lst.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("assigned", "1");
                    jobjtemp.put("DeductionType", wage.getDeductionmaster().getDeductiontype());
                    jobjtemp.put("DeducRate", wage.getRate());
                    jobjtemp.put("ratetype", wage.getDeductionmaster().getRate());
                    jobjtemp.put("DeducId", wage.getDeductionmaster().getDeductionid());
                    jobjtemp.put("DeducCode", wage.getDeductionmaster().getDcode());
                    jobj.append("data", jobjtemp);
                }
                if(lst.size()==0){                 
                  jobj.put("data","");
                }
            } else if (Integer.parseInt(templ.getStatus().toString()) == 1) {
                Query query = session.createQuery("select t.deductionmaster.deductionid,t.rate,t.deductionmaster.deductiontype,t.deductionmaster.dcode from TempTemplateMagDeduction t where t.template.templateid=:tempid and t.deductionmaster.companydetails.companyID=:cid");
                query.setString("tempid", tempid);
                query.setString("cid", Cid);
                List lst1 = (List) query.list();
                for (i = 0; i < lst1.size(); i++) {
                    Object[] object = (Object[]) lst1.get(i);
                    JSONObject jobjtemp = new JSONObject();
                    jobjtemp.put("assigned", "1");
                    jobjtemp.put("DeductionType", object[2]);
                    jobjtemp.put("DeducRate", object[1]);
                    jobjtemp.put("DeducId", object[0]);
                    jobjtemp.put("DeducCode", object[3]);
                    jobj.append("data", jobjtemp);
                }
                if(lst1.size()==0){                  
                  jobj.put("data","");
                }
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", i);
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getCompanyData(Session session) {
        try {
            Query query = session.createQuery("from Company");
            List lst = (List) query.list();
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst.size(); i++) {
                Company company = (Company) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("cname", company.getCompanyName());
                jobjtemp.put("cid", company.getCompanyID());
                jobjtemp.put("cmail", company.getWebsite());
                jobjtemp.put("cadd", company.getAddress());
                jobjtemp.put("curr", company.getCurrency().getCurrencyID());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getPayProcessData(HttpServletRequest request, String Cid, String st, String lim, Session session) {
        try {
            String Cids = AuthHandler.getCompanyid(request);
            int start = Integer.parseInt(request.getParameter("start"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            JSONObject jobj = new JSONObject();
            String hql = "";
            hql = "from Template where companyid=?";
            params.add(Cid);
            if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 2);
                    String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"designationid.value","templatename"});
                    hql +=searchQuery;
            }
            hql +=" order by designationid.value";
            List tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
            int count = tabledata.size();
            List lst = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            int cnt = lst.size();

            for (int i = 0; i < lst.size(); i++) {
                masterDB.Template wage = (masterDB.Template) lst.get(i);
                JSONObject jobjinloop = new JSONObject();
                jobjinloop.put("TempID", wage.getTemplateid());
                jobjinloop.put("TempName", wage.getTemplatename());
                jobjinloop.put("TempRange", wage.getStartrange() + " - " + wage.getEndrange());
                jobjinloop.put("GroupId", wage.getDesignationid() != null ? wage.getDesignationid().getId() : "");
                jobjinloop.put("GroupName", wage.getDesignationid() != null ? wage.getDesignationid().getValue() : "");
                Query queryTax = session.createQuery("select count(*) from Templatemaptax as t where t.template.templateid='" + wage.getTemplateid() + "'");
                Iterator iteratorTax = queryTax.iterate();
                Object taxcount = iteratorTax.next();
                jobjinloop.put("NosTax", taxcount.toString());
                queryTax = session.createQuery("select count(*) from Templatemapwage as t where t.template.templateid='" + wage.getTemplateid() + "'");
                iteratorTax = queryTax.iterate();
                taxcount = iteratorTax.next();
                jobjinloop.put("NosWage", taxcount.toString());
                queryTax = session.createQuery("select count(*) from Templatemapdeduction as t where t.template.templateid='" + wage.getTemplateid() + "'");
                iteratorTax = queryTax.iterate();
                taxcount = iteratorTax.next();
                jobjinloop.put("NosDeduc", taxcount.toString());
                jobj.append("data", jobjinloop);
            }
            if (lst.size() == 0) {
                jobj.put("data", "");
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", count);
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return ("failed");
        } finally {
        }
    }

    public String getPayEmployeePerTemp(String currencyid, String rowTempGroup, Session session) {
        try {
            Template templ = (Template) session.get(Template.class, rowTempGroup);
            String s = "";
            JSONObject jobj = new JSONObject();
            JSONObject jobjinloop = new JSONObject();
            if (Integer.parseInt(templ.getStatus().toString()) == 0 || Integer.parseInt(templ.getStatus().toString()) == 1) {
                Query queryTemplate = session.createQuery("from Templatemaptax as t where t.template.templateid='" + rowTempGroup + "'");
                List iteratorTax = queryTemplate.list();
                String tco = String.valueOf(iteratorTax.size());
                Query query;
                query = session.createQuery("from KWLCurrency where currencyID=:curid");
                query.setString("curid", currencyid);
                KWLCurrency curncy = (KWLCurrency) query.uniqueResult();
                JSONObject jsAray = new JSONObject();
                int len = 0;
                int i = 0;
                double total = 0;
                double cashtotal = 0;
                while (i < iteratorTax.size()) {
                    Templatemaptax row = (Templatemaptax) iteratorTax.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("TaxId", row.getTaxmaster().getTaxid());
                    jsAray1.put("TaxRate", row.getTaxmaster().getRate() == 1 ? row.getRate() : "");
                    jsAray1.put("cash", row.getRate());
                    jsAray1.put("TaxType", row.getTaxmaster().getTaxtype());
                    total += row.getTaxmaster().getRate() == 1 ? Double.parseDouble(row.getRate()) : 0;
                    cashtotal += row.getTaxmaster().getRate() == 0 ? Double.parseDouble(row.getRate()) : 0;
                    jsAray.append("TAX", jsAray1);
                    len++;
                    i++;
                }
                jobjinloop.put("TaxArray", jsAray);
                jobjinloop.put("NosTax", len);
                jobjinloop.put("TaxTotal", total);
                jobjinloop.put("cashtotal", cashtotal);
                jobj.put("Tax", jobjinloop);
                queryTemplate = session.createQuery("from Templatemapwage w where w.template.templateid='" + rowTempGroup + "'");
                List iteratorWage = queryTemplate.list();
                len = 0;
                i = 0;
                total = 0;
                cashtotal = 0;
                jobjinloop = new JSONObject();
                jsAray = new JSONObject();
                while (i < iteratorWage.size()) {
                    Templatemapwage row = (Templatemapwage) iteratorWage.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("WageId", row.getWagemaster().getWageid());
                    total += row.getWagemaster().getRate() == 1 ? Double.parseDouble(row.getRate()) : 0;
                    cashtotal += row.getWagemaster().getRate() == 0 ? Double.parseDouble(row.getRate()) : 0;
                    jsAray1.put("WageRate", row.getWagemaster().getRate() == 1 ? row.getRate() : "");
                    jsAray1.put("cash", row.getRate());
                    jsAray1.put("WageType", row.getWagemaster().getWagetype());

                    jsAray.append("Wage", jsAray1);
                    len++;
                    i++;
                }
                jobjinloop.put("WagesArray", jsAray);
                jobjinloop.put("NosWage", len);
                jobjinloop.put("WageTotal", total);
                jobjinloop.put("cashtotal", cashtotal);
                jobj.put("Wages", jobjinloop);
                queryTemplate = session.createQuery("from Templatemapdeduction d where d.template.templateid='" + rowTempGroup + "'");
                List iteratorDeduc = queryTemplate.list();
                len = 0;
                i = 0;
                total = 0;
                cashtotal = 0;
                jobjinloop = new JSONObject();
                jsAray = new JSONObject();
                while (i < iteratorDeduc.size()) {
                    Templatemapdeduction row = (Templatemapdeduction) iteratorDeduc.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("DeducId", row.getDeductionmaster().getDeductionid());
                    jsAray1.put("DeducRate", row.getDeductionmaster().getRate() == 1 ? row.getRate() : "");
                    jsAray1.put("cash", row.getRate());
                    jsAray1.put("DeducType", row.getDeductionmaster().getDeductiontype());
                    jsAray.append("Deduction", jsAray1);
                    total += row.getDeductionmaster().getRate() == 1 ? Double.parseDouble(row.getRate()) : 0;
                    cashtotal += row.getDeductionmaster().getRate() == 0 ? Double.parseDouble(row.getRate()) : 0;

                    len++;
                    i++;
                }
                jobjinloop.put("DeducArray", jsAray);
                jobjinloop.put("NosDeduc", len);
                jobjinloop.put("DeducTotal", total);
                jobjinloop.put("cashtotal", cashtotal);
                jobj.put("Deduction", jobjinloop);
                jobj.put("curval", curncy.getSymbol());
                jobj.put("success", true);
                JSONObject jobj1 = new JSONObject();
                jobj1.put("valid", true);
                jobj1.put("totalcount", tco);
                jobj1.put("data", jobj);
                s = jobj1.toString();
            } else if (Integer.parseInt(templ.getStatus().toString()) == 10) {
                Query queryTemplate = session.createQuery("from TempTemplatemaptax as t where t.template.templateid='" + rowTempGroup + "'");
                List iteratorTax = queryTemplate.list();
                String tco = String.valueOf(iteratorTax.size());
                Query query;
                query = session.createQuery("from KWLCurrency where currencyID=:curid");
                query.setString("curid", currencyid);
                KWLCurrency curncy = (KWLCurrency) query.uniqueResult();
                JSONObject jsAray = new JSONObject();
                int len = 0;
                int i = 0;
                float total = 0;
                while (i < iteratorTax.size()) {
                    TempTemplatemaptax row = (TempTemplatemaptax) iteratorTax.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("TaxId", row.getTaxmaster().getTaxid());
                    jsAray1.put("TaxRate", row.getRate());
                    jsAray1.put("TaxType", row.getTaxmaster().getTaxtype());
                    total += Float.parseFloat(row.getRate());
                    jsAray.append("TAX", jsAray1);
                    len++;
                    i++;
                }
                jobjinloop.put("TaxArray", jsAray);
                jobjinloop.put("NosTax", len);
                jobjinloop.put("TaxTotal", total);
                jobj.put("Tax", jobjinloop);
                queryTemplate = session.createQuery("from TempTemplatemapwage w where w.template.templateid='" + rowTempGroup + "'");
                List iteratorWage = queryTemplate.list();
                len = 0;
                i = 0;
                total = 0;
                jobjinloop = new JSONObject();
                jsAray = new JSONObject();
                while (i < iteratorWage.size()) {
                    TempTemplatemapwage row = (TempTemplatemapwage) iteratorWage.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("WageId", row.getWagemaster().getWageid());
                    total += Float.parseFloat(row.getRate());
                    jsAray1.put("WageRate", row.getRate());
                    jsAray1.put("WageType", row.getWagemaster().getWagetype());
                    jsAray.append("Wage", jsAray1);
                    len++;
                    i++;
                }
                jobjinloop.put("WagesArray", jsAray);
                jobjinloop.put("NosWage", len);
                jobjinloop.put("WageTotal", total);
                jobj.put("Wages", jobjinloop);
                queryTemplate = session.createQuery("from TempTemplateMagDeduction d where d.template.templateid='" + rowTempGroup + "'");
                List iteratorDeduc = queryTemplate.list();
                len = 0;
                i = 0;
                total = 0;
                jobjinloop = new JSONObject();
                jsAray = new JSONObject();
                while (i < iteratorDeduc.size()) {
                    TempTemplateMagDeduction row = (TempTemplateMagDeduction) iteratorDeduc.get(i);
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("DeducId", row.getDeductionmaster().getDeductionid());
                    jsAray1.put("DeducRate", row.getRate());
                    jsAray1.put("DeducType", row.getDeductionmaster().getDeductiontype());
                    jsAray.append("Deduction", jsAray1);
                    total += Float.parseFloat(row.getRate());
                    len++;
                    i++;
                }
                jobjinloop.put("DeducArray", jsAray);
                jobjinloop.put("NosDeduc", len);
                jobjinloop.put("DeducTotal", total);
                jobj.put("Deduction", jobjinloop);
                jobj.put("curval", curncy.getSymbol());
                jobj.put("success", true);
                JSONObject jobj1 = new JSONObject();
                jobj1.put("valid", true);
                jobj1.put("totalcount", tco);
                jobj1.put("data", jobj);
                s = jobj1.toString();
            }
            return (s);
        } catch (Exception se) {
            return ("failed");
        } finally {
        }
    }

    public String getEmpPerTempidData(HttpServletRequest request, String tempID, String wtotal, String ttotal, String dtotal, Session session) {
        try {

            double taxcash = Double.parseDouble(request.getParameter("taxcash"));
            double wagecash = Double.parseDouble(request.getParameter("wagecash"));
            double deduccash = Double.parseDouble(request.getParameter("deduccash"));
            JSONObject jobj = new JSONObject(getEmpPerTempidData1(request, tempID, wtotal, ttotal, dtotal, wagecash, deduccash, taxcash, session));
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", mylstsize);
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());

        } finally {
        }
    }

    public String getEmpPerTempidData1(HttpServletRequest request, String tempID, String wtotal, String ttotal, String dtotal, Double wagecash, Double deduccash, Double taxcash, Session session) {
        Query query;
        try {
            int start = Integer.parseInt(request.getParameter("start"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");            
            String cmpid=AuthHandler.getCompanyid(request);
            String hql = "from User where templateid=? and deleted=? and company.companyID=?";
            params.add(tempID);
            params.add(false);
            params.add(cmpid);
            if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 2);
                    String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"firstName","lastName"});
                    hql +=searchQuery;
            }
            List tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
            int count = tabledata.size();
            List usrList = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            int cnt = usrList.size();
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < usrList.size(); i++) {
                User usr = (User) usrList.get(i);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, usr.getUserID());
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("empid", usr.getUserID());
                String lname = usr.getLastName() != null ? usr.getLastName() : "";
                jobjtemp.put("EName", usr.getFirstName() + " " + lname);
                jobjtemp.put("AccNo", ua.getAccno());
                jobjtemp.put("design", ua.getDesignationid() != null ? ua.getDesignationid().getValue() : "");
                jobjtemp.put("tempid", ua.getTemplateid());
                Float sal = Float.parseFloat(ua.getSalary());
                Double gross=0.0;
                Double deduction=0.0;
                Double taxablegross=0.0;
                Double ttax=0.0;
                Double netsal=0.0;
                if(Double.parseDouble(ua.getSalary())>0){
                gross = (Double.parseDouble(ua.getSalary()) * Double.parseDouble(wtotal) / 100) + wagecash;
                deduction = (Double.parseDouble(dtotal) * (Double.parseDouble(ua.getSalary()) / 100)) + deduccash;
                taxablegross = gross - deduction;
                ttax = (Double.parseDouble(ua.getSalary()) * Double.parseDouble(ttotal) / 100) + taxcash;
                netsal = taxablegross - ttax;
                }
                jobjtemp.put("Salary", String.valueOf(netsal));
                jobjtemp.put("Tax", String.valueOf(ttax));
                jobjtemp.put("FixedSal", ua.getSalary());
                jobjtemp.put("Wage", String.valueOf(gross));
                jobjtemp.put("Deduc", String.valueOf(deduction));
                if(!StringUtil.isNullOrEmpty(request.getParameter("enddate"))){
                  Date d3 = new Date(request.getParameter("enddate"));
                  Query query1 = session.createQuery("from Payhistory  where  userID=:empid and createdfor = :d3");
                  query1.setString("d3", df.format(d3));
                  query1.setString("empid",usr.getUserID());
                  List paydata=query1.list();
                  if(paydata.size()>0){
                      jobjtemp.put("generated","1");
                  }else{
                      jobjtemp.put("generated","0");
                  }
                }
                jobj.append("data", jobjtemp);
            }
            mylstsize = usrList.size();
            return (jobj.toString());
        } catch (Exception se) {
            return (se.getMessage());

        } finally {
        }
    }

    public String getwagesPerTempid(String TempId, String salary, Session session) {


        try {
            JSONObject jsAray = new JSONObject();
            Template temp = (Template) session.get(Template.class, TempId);
            int tc = 0;
            if (Integer.parseInt(temp.getStatus().toString()) == 0) {
                Query queryTemplate = session.createQuery("from Templatemapwage w where w.template.templateid='" + TempId + "'");
                List iteratorWage = queryTemplate.list();
                int i = 0;
                float amtot = 0;
                while (i < iteratorWage.size()) {
                    Templatemapwage wm = (Templatemapwage) iteratorWage.get(i);

                    double casht = wm.getWagemaster().getRate() == 0 ? wm.getWagemaster().getCash() : 0;
                    JSONObject jsAray1 = new JSONObject();
                    if (wm.getWagemaster().getRate() == 0) {
                        jsAray1.put("Rate", wm.getWagemaster().getRate());
                        jsAray1.put("amount", wm.getRate());
                        amtot += Float.parseFloat(wm.getRate());
                    } else {
                        double amount = (Double.parseDouble(wm.getRate())  * Double.parseDouble(salary)) / 100;
                        amtot += Double.parseDouble(wm.getRate()) * Double.parseDouble(salary) / 100;
                        jsAray1.put("Rate", wm.getRate());
                        jsAray1.put("amount", amount);
                    }
                    wagetotalval = amtot;
                    jsAray1.put("Id", wm.getWagemaster().getWageid());
                    jsAray1.put("Type", wm.getWagemaster().getWagetype());
                    jsAray1.put("ratetype", wm.getWagemaster().getRate());

                    jsAray1.put("amtot", amtot);
                    jsAray.append("Wage", jsAray1);
                    i++;
                }
                tc = i - 1;

            } else if (Integer.parseInt(temp.getStatus().toString()) == 1) {
                Query queryTemplate = session.createQuery("from TempTemplatemapwage w where w.template.templateid='" + TempId + "'");
                List iteratorWage = queryTemplate.list();
                int i = 0;
                i = 0;
                float amtot = 0;
                while (i < iteratorWage.size()) {
                    TempTemplatemapwage wm = (TempTemplatemapwage) iteratorWage.get(i);
                    float amount = (Float.parseFloat(wm.getRate()) * Float.parseFloat(salary)) / 100;
                    amtot += Float.parseFloat(wm.getRate()) * Float.parseFloat(salary) / 100;
                    wagetotalval = amtot;
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
            jsAray.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", tc);
            jobj1.put("data", jsAray);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getDeducPerTempid(String TempId, String salary, Session session) {
        try {
            JSONObject jsAray = new JSONObject();
            Template temp = (Template) session.get(Template.class, TempId);
            int i = 0;
            if (Integer.parseInt(temp.getStatus().toString()) == 0) {
                Query queryTemplate = session.createQuery("from Templatemapdeduction w where w.template.templateid='" + TempId + "'");
                List iteratorWage = queryTemplate.list();
                i = 0;
                float amtot = 0;
                double amount = 0;
                while (i < iteratorWage.size()) {
                    Templatemapdeduction wm = (Templatemapdeduction) iteratorWage.get(i);
//                    amount = (wm.getDeductionmaster().getRate()==1?wm.getDeductionmaster().getCash():0* wagetotalval) / 100;
//                    amtot += wm.getDeductionmaster().getCash()*wagetotalval / 100;
//                    deductotalval = amtot;
//                     double casht=wm.getDeductionmaster().getCash()==0?wm.getDeductionmaster().getCash():0;

                    JSONObject jsAray1 = new JSONObject();
                    if (wm.getDeductionmaster().getRate() == 0) {
                        jsAray1.put("Rate", wm.getDeductionmaster().getRate());
                        jsAray1.put("amount", wm.getRate());
                        amtot += Float.parseFloat(wm.getRate());
                    } else {
                        amtot += (Double.parseDouble(wm.getRate())  * Double.parseDouble(salary)) / 100;
                        amount = (Double.parseDouble(wm.getRate())  * Double.parseDouble(salary)) / 100;
                        jsAray1.put("Rate", wm.getRate());
                        jsAray1.put("amount", amount);
                    }
                    deductotalval = amtot;
                    wagedeductot = wagetotalval - deductotalval;


                    jsAray1.put("WageId", wm.getDeductionmaster().getDeductionid());
                    jsAray1.put("Type", wm.getDeductionmaster().getDeductiontype());
                    jsAray1.put("ratetype", wm.getDeductionmaster().getRate());
                    jsAray1.put("amtot", amtot);
                    jsAray.append("Deduc", jsAray1);
                    i++;
                }
            } else if (Integer.parseInt(temp.getStatus().toString()) == 1) {
                Query queryTemplate = session.createQuery("from TempTemplateMagDeduction w where w.template.templateid='" + TempId + "'");
                List iteratorWage = queryTemplate.list();
                i = 0;
                float amtot = 0;
                while (i < iteratorWage.size()) {
                    TempTemplateMagDeduction wm = (TempTemplateMagDeduction) iteratorWage.get(i);
                    double amount = (Float.parseFloat(wm.getRate()) * wagetotalval) / 100;
                    amtot += Float.parseFloat(wm.getRate()) * wagetotalval / 100;
                    deductotalval = amtot;
                    wagedeductot = wagetotalval - deductotalval;
                    JSONObject jsAray1 = new JSONObject();
                    jsAray1.put("WageId", wm.getDeductionmaster().getDeductionid());
                    jsAray1.put("Rate", wm.getRate());
                    jsAray1.put("Type", wm.getDeductionmaster().getDeductiontype());
                    jsAray1.put("amount", amount);
                    jsAray1.put("amtot", amtot);
                    jsAray.append("Deduc", jsAray1);
                    i++;
                }
            }
            jsAray.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", i);
            jobj1.put("data", jsAray);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getTaxsPerTempid(String TempId, String salary, Session session) {
        try {
            JSONObject jsAray = new JSONObject();
            Template temp = (Template) session.get(Template.class, TempId);
            int i = 0;
            if (Integer.parseInt(temp.getStatus().toString()) == 0) {
                Query queryTemplate = session.createQuery("from Templatemaptax as w where w.template.templateid='" + TempId + "'");
                List iteratorWage = queryTemplate.list();
                i = 0;
                double amtot = 0;
                double amount = 0;
                while (i < iteratorWage.size()) {
                    Templatemaptax wm = (Templatemaptax) iteratorWage.get(i);
                    JSONObject jsAray1 = new JSONObject();

                    if (wm.getTaxmaster().getRate() == 0) {
                        amtot += Float.parseFloat(wm.getRate());
                        jsAray1.put("Rate", wm.getTaxmaster().getRate());
                        jsAray1.put("amount", wm.getRate());
                    } else {
                        amount = (Double.parseDouble(wm.getRate())  * Double.parseDouble(salary)) / 100;
                        amtot += (Double.parseDouble(wm.getRate())  * Double.parseDouble(salary)) / 100;
                        jsAray1.put("Rate", wm.getRate());
                        jsAray1.put("amount", amount);
                    }
                    jsAray1.put("Id", wm.getTaxmaster().getTaxid());
                    jsAray1.put("Type", wm.getTaxmaster().getTaxtype());
                    jsAray1.put("ratetype", wm.getTaxmaster().getRate());
                    jsAray1.put("amtot", amtot);
                    jsAray.append("Tax", jsAray1);
                    i++;
                }
            } else if (Integer.parseInt(temp.getStatus().toString()) == 1) {
                Query queryTemplate = session.createQuery("from TempTemplatemaptax as w where w.template.templateid='" + TempId + "'");
                List iteratorWage = queryTemplate.list();
                i = 0;
                float amtot = 0;
                while (i < iteratorWage.size()) {
                    TempTemplatemaptax wm = (TempTemplatemaptax) iteratorWage.get(i);
                    double amount = (Float.parseFloat(wm.getRate()) * wagedeductot) / 100;
                    amtot += (Float.parseFloat(wm.getRate()) * wagedeductot) / 100;
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
            jsAray.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", i);
            jobj1.put("data", jsAray);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getAllUserlist(String Cid, String Gname, String tid, Session session) {
        try {
            Query query = session.createQuery("from User where company.companyID=:cid and deleted=false and templateid!=:tid");
            query.setString("cid", Cid);
            query.setString("tid", tid);
            List lst = (List) query.list();
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst.size(); i++) {
                User group = (User) lst.get(i);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, group.getUserID());
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("empname", group.getFirstName() + " " + group.getLastName());
                jobjtemp.put("lname", group.getLastName());
                jobjtemp.put("design", ua.getDesignationid() != null ? ua.getDesignationid().getValue() : "");
                jobjtemp.put("accno", ua.getAccno());
                jobjtemp.put("salary", ua.getSalary());
                jobjtemp.put("address", group.getAddress());
                jobjtemp.put("empid", group.getUserID());
                if (!ua.getTemplateid().equals("0")) {
                    jobjtemp.put("status", "2");
                } else {
                    jobjtemp.put("status", "0");
                }
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        }
    }

    public String getAssignedUserlist(String Cid, String Tid, Session session) {
        try {
            Query query = session.createQuery("from User where company.companyID=:cid and templateid=:tid");
            query.setString("cid", Cid);
            query.setString("tid", Tid);
            List lst = (List) query.list();
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst.size(); i++) {
                User group = (User) lst.get(i);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, group.getUserID());
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("empname", group.getFirstName() + " " + group.getLastName());
                jobjtemp.put("lname", group.getLastName());
                jobjtemp.put("design", ua.getDesignationid() != null ? ua.getDesignationid().getValue() : "");
                jobjtemp.put("accno", ua.getAccno());
                jobjtemp.put("salary", ua.getSalary());
                jobjtemp.put("address", group.getAddress());
                jobjtemp.put("empid", group.getUserID());
                jobjtemp.put("status", "1");
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());

        }
    }

    public String getAuditData(String stdate, String enddate, String Cid, String start, String limit, Session session) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            Date d2 = new Date(enddate);
            Date d3 = new Date(stdate);
            String ssttrr = df.format(d3);
            Query query = session.createQuery("from AudittrailPayroll where companyid=:cid and date between :st and :end");
            query.setString("st", df.format(d3));
            query.setString("end", df.format(d2));
            query.setString("cid", Cid);
            query.setFirstResult(Integer.parseInt(start));
            query.setMaxResults(Integer.parseInt(limit));
            List lst = (List) query.list();
            JSONObject jobj = new JSONObject();
            int i = 0;
            int x = 0;
            for (i = 0; i < lst.size(); i++) {
                AudittrailPayroll at = (AudittrailPayroll) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("companyid", at.getCompanyid());
                jobjtemp.put("date", at.getDate());
                jobjtemp.put("ip", at.getIp());
                jobjtemp.put("typeofwork", at.getTypeofwork());
                jobjtemp.put("username", at.getUserid());
                jobj.append("data", jobjtemp);
            }
            query = session.createQuery("from AudittrailPayroll where companyid=:cid and date between :st and :end");
            query.setString("st", df.format(d3));
            query.setString("end", df.format(d2));
            query.setString("cid", Cid);
            lst = (List) query.list();
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception e) {
            return (e.getMessage());
        }
    }

    public String getAllCurrency(Session session) {
        try {
            Query query = session.createQuery("from KWLCurrency");
            List lst = (List) query.list();
            JSONObject jobj = new JSONObject();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                KWLCurrency kwlcur = (KWLCurrency) ite.next();
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("currencyid", kwlcur.getCurrencyID());
                jobjtemp.put("currencyname", kwlcur.getName());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception e) {
            return (e.getMessage());
        }
    }

    public String GenerateCode(String codetype, String minus, String cid, Session session) {
        String code = "";
        try {
            Query query = session.createQuery("from Wagemaster as w where w.companydetails.companyID=:cid");
            query.setString("cid", cid);
            List lst = (List) query.list();
            w = lst.size() + 1;
            Query query1 = session.createQuery("from Taxmaster as w where w.companydetails.companyID=:cid");
            query1.setString("cid", cid);
            List lst1 = (List) query1.list();
            t = lst1.size() + 1;
            Query query2 = session.createQuery("from Deductionmaster as w where w.companydetails.companyID=:cid");
            query2.setString("cid", cid);
            List lst2 = (List) query2.list();
            d = lst2.size() + 1;
            if (codetype.equals("Wages") && minus.equals("false")) {
                code = "WA" + w;
            }
            if (codetype.equals("Wages") && minus.equals("true")) {
                code = "z";
                w--;
            }
            if (codetype.equals("Tax") && minus.equals("false")) {
                code = "TX" + t;
            }
            if (codetype.equals("Tax") && minus.equals("true")) {
                code = "z";
                t--;
            }
            if (codetype.equals("Deduction") && minus.equals("false")) {
                code = "DD" + d;
            }
            if (codetype.equals("Deduction") && minus.equals("true")) {
                code = "z";
                d--;
            }
            return "{\"valid\":\"true\",data:{value:\"" + code + "\"}}";
        } catch (Exception e) {
            return ("{\"valid\":\"true\",data:{value:\"" + e.getMessage() + "\"}}");
        }
    }

    public String getEmpListPerGroupid(HttpServletRequest request, String s, String gid, Session session) {
        try {
            String cid = AuthHandler.getCompanyid(request);
            Query query = session.createQuery("from Template where templateid=? and companyid=?");
            query.setParameter(0, gid);
            query.setParameter(1, cid);
            List lst = (List) query.list();
            String data = "";
            String data2 = "";
            String finalString = "{ \"data\" : []}";
            JSONObject jobj = new JSONObject();
            JSONObject jobjtemp = new JSONObject();
            JSONArray JA = new JSONArray();
            int i = 0;
            for (i = 0; i < lst.size(); i++) {

                masterDB.Template group = (masterDB.Template) lst.get(i);
                jobjtemp.put("templateid", group.getTemplateid());
                String tempid = group.getTemplateid();
                String tempdata = getPayEmployeePerTemp(s, tempid, session);
                JSONObject jobj1 = new JSONObject(tempdata);
                JSONObject jobj2 = new JSONObject(jobj1.getString("data"));
                JSONObject jobj3 = new JSONObject(jobj2.getString("Wages"));
                String wtot = jobj3.getString("WageTotal");
                double wagecash = Double.parseDouble(jobj3.getString("cashtotal"));
                jobj3 = new JSONObject(jobj2.getString("Tax"));
                String ttot = jobj3.getString("TaxTotal");
                double taxcash = Double.parseDouble(jobj3.getString("cashtotal"));
                jobj3 = new JSONObject(jobj2.getString("Deduction"));
                String dtot = jobj3.getString("DeducTotal");
                double deduccash = Double.parseDouble(jobj3.getString("cashtotal"));

                data = getEmpPerTempidData1(request, tempid, wtot, ttot, dtot, wagecash, deduccash, taxcash, session);
                if (data.length() > 2) {
                    jobjtemp = new JSONObject(data);
                    data = jobjtemp.toString();
                    //data = data.substring(1, data.length()-1);
                    String dum = "";
                    jobjtemp = new JSONObject(data);
                    if (jobjtemp.has("data")) {
                        dum = jobjtemp.get("data").toString();
                        dum = dum.substring(1, dum.length() - 1);
                        data2 += dum;
                        data2 += ",";
                    }
                }
                if (data2.length() > 0) {
                }
            }
            data2 = data2.substring(0, data2.length());
            finalString = "{ \"data\" : [" + data2 + "]}";
            jobj = new JSONObject(finalString);
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());// lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception e) {
            return (e.getMessage());
        }
    }

    public String getviewmypayslip(Session session, HttpServletRequest request) {
        try {
            String empid = "";
            if (StringUtil.isNullOrEmpty(request.getParameter("empid"))) {
                empid = AuthHandler.getUserid(request);
            } else {
                empid = request.getParameter("empid");
            }
            Query query = session.createQuery("from User where  userID=:empid");
            query.setString("empid", empid);
            List lst1 = (List) query.list();
            User obju = (User) lst1.get(0);
            Useraccount ua = (Useraccount) session.get(Useraccount.class, obju.getUserID());
            SimpleDateFormat df1 = new SimpleDateFormat("MMM-dd-yyyy");
            SimpleDateFormat fmt1 = new SimpleDateFormat("MMMM d, yyyy");
            int start = Integer.parseInt(request.getParameter("start"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            String hql = "from Payhistory  where  userID.userID=? and salarystatus=3 order by paycyclestart DESC";
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            params.add(empid);
            if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"paymonth"});
                    hql +=searchQuery;
            }
            List tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
            int totalcount = tabledata.size();
            List lst = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
            int cnt = lst.size();

            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst.size(); i++) {
                masterDB.Payhistory group = (masterDB.Payhistory) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("EName", group.getName());
                jobjtemp.put("design", group.getDesign());
                jobjtemp.put("FixedSal", group.getGross());
                jobjtemp.put("Wage", group.getWagetot());
                jobjtemp.put("empid", group.getUserID().getUserID());
                jobjtemp.put("Deduc", group.getDeductot());
                jobjtemp.put("tempid", group.getTemplate().getTemplateid());
                jobjtemp.put("showborder", group.getTemplate().isShowborder());
                jobjtemp.put("Tax", group.getTaxtot());
                jobjtemp.put("Salary", group.getNet());
                jobjtemp.put("month", MessageSourceProxy.getMessage("hrms."+group.getPaymonth() ,null,group.getPaymonth(), LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0)));
                jobjtemp.put("stdate", fmt1.format(group.getPaycyclestart()));
                jobjtemp.put("enddate", fmt1.format(group.getPaycycleend()));
                jobjtemp.put("AccNo", ua.getAccno());
                jobjtemp.put("histid", group.getHistoryid());
                jobj.append("data", jobjtemp);
            }
            if (lst.size() == 0) {
                JSONObject jobjtemp = new JSONObject();
                jobj.put("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", totalcount);
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception e) {
            return (e.getMessage());
        }
    }

    public String getReportPerMonth(Session session, HttpServletRequest request) {
        try {
            String name="";
            String stdate = request.getParameter("stdate");
            String enddate = request.getParameter("enddate");
            String month1 = request.getParameter("month");
            String cid = AuthHandler.getCompanyid(request);
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat df1 = new SimpleDateFormat("MMMM d, yyyy");
            Date d2 = new Date(enddate);
            Date d3 = new Date(stdate);
            String ssttrr = df.format(d3);
            String st = df.format(d3);
            String end = df.format(d2);
            String ci = AuthHandler.getCompanyid(request);
            String ss = request.getParameter("ss");
            ArrayList params = new ArrayList();
            List lst = null;
          //  String hql = "from Payhistory  where  userID.company.companyID=? and createdon >=? and createdon <=? and createdfor >=? and createdfor <=?";
            String hql = "from Payhistory  where  userID.company.companyID=? and paycycleend >=? and paycycleend <=? and salarystatus= 3 ";
            params.add(ci);
            params.add(d3);
            params.add(d2);
            if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"name"});
                    hql +=searchQuery;
            }
            List tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
            int count = tabledata.size();
            if(!StringUtil.isNullOrEmpty(request.getParameter("start"))){
                int start = Integer.parseInt(request.getParameter("start"));
                int limit = Integer.parseInt(request.getParameter("limit"));
                lst = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
                int cnt = lst.size();
            } else {
                lst = tabledata;
            }
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst.size(); i++) {
                masterDB.Payhistory group = (masterDB.Payhistory) lst.get(i);
                String ccid = group.getUserID().getCompany().getCompanyID();
                if (ccid.equals(cid)) {
                	name="";
                    JSONObject jobjtemp = new JSONObject();
                    if(!StringUtil.isNullOrEmpty(group.getUserID().getLastName()))
                    	name=group.getUserID().getLastName();
                    jobjtemp.put("EName", group.getUserID().getFirstName()+" "+name);
                    jobjtemp.put("design", group.getDesign());
                    jobjtemp.put("FixedSal", group.getGross());
                    jobjtemp.put("Wage", group.getWagetot());
                    jobjtemp.put("empid", group.getUserID().getUserID());
                    jobjtemp.put("Deduc", group.getDeductot());
                    jobjtemp.put("tempid", group.getTemplate().getTemplateid());
                    jobjtemp.put("tempname", group.getTemplate().getTemplatename());
                    jobjtemp.put("Tax", group.getTaxtot());
                    jobjtemp.put("Salary", group.getNet());
                    jobjtemp.put("month", group.getPaymonth());
                    jobjtemp.put("stdate", df1.format(group.getCreatedon()));
                    jobjtemp.put("enddate", df1.format(group.getCreatedfor()));
                    jobj.append("data", jobjtemp);
                }
            }
            if (lst.size() == 0) {
                JSONObject jobjtemp = new JSONObject();
                jobj.put("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", tabledata.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception e) {
            return (e.getMessage());
        }
    }

    public String getGenerateSalaryList(Session session, HttpServletRequest request) {
        try {
            String name="";
            String stdate = request.getParameter("stdate");
            String enddate = request.getParameter("enddate");
            String month1 = request.getParameter("month");
            String cid = AuthHandler.getCompanyid(request);
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat df1 = new SimpleDateFormat("MMMM d, yyyy");
            
            Date d2 = new Date(enddate);
            Date d3 = new Date(stdate);
            String ssttrr = df.format(d3);
            String st = df.format(d3);
            String end = df.format(d2);
            boolean isExport = false;
            String exportReq= request.getParameter("exportFile");
            if(!StringUtil.isNullOrEmpty(exportReq)){
                isExport = Boolean.parseBoolean(exportReq);
            }
            String ci = AuthHandler.getCompanyid(request);
            String ss = request.getParameter("ss");
            String salaryStatus = request.getParameter("salaryStatus");
            ArrayList params = new ArrayList();
            List lst = null; 
          //  String hql = "from Payhistory  where  userID.company.companyID=? and createdon >=? and createdon <=? and createdfor >=? and createdfor <=?";
            String hql = "from Payhistory  where  userID.company.companyID=? and paycycleend >=? and paycycleend <=? ";
            params.add(ci);
            params.add(d3);
            params.add(d2);
            if(!StringUtil.isNullOrEmpty(salaryStatus)){
                if(StringUtil.equal(salaryStatus, "1")){
                    hql+=" and salarystatus= 1 ";
                } else if(StringUtil.equal(salaryStatus, "2")){
                    hql+=" and salarystatus= 2 ";
                } else if(StringUtil.equal(salaryStatus, "3")){
                    hql+=" and salarystatus= 3 ";
                }

            }
            if(!StringUtil.isNullOrEmpty(ss)){
                    StringUtil.insertParamSearchString(params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"name"});
                    hql +=searchQuery;
            }
            List tabledata = HibernateUtil.executeQuery(session, hql, params.toArray());
            int count = tabledata.size();
            if(!StringUtil.isNullOrEmpty(request.getParameter("start"))){
                int start = Integer.parseInt(request.getParameter("start"));
                int limit = Integer.parseInt(request.getParameter("limit"));
                lst = HibernateUtil.executeQueryPaging(session, hql, params.toArray(), new Integer[]{start, limit});
                int cnt = lst.size();
            } else {
                lst = tabledata;
            }
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst.size(); i++) {
                masterDB.Payhistory group = (masterDB.Payhistory) lst.get(i);
                String ccid = group.getUserID().getCompany().getCompanyID();
                if (ccid.equals(cid)) {
                	name="";
                    Useraccount ua = (Useraccount) session.get(Useraccount.class, group.getUserID().getUserID());
                    JSONObject jobjtemp = new JSONObject();
                    if(!StringUtil.isNullOrEmpty(group.getUserID().getLastName()))
                    	name=group.getUserID().getLastName();
                    jobjtemp.put("EName", group.getUserID().getFirstName()+" "+name);
                    jobjtemp.put("design", group.getDesign());
                    jobjtemp.put("AccNo", ua.getAccno());
                    jobjtemp.put("FixedSal", group.getGross());
                    jobjtemp.put("Wage", group.getWagetot());
                    jobjtemp.put("empid", group.getUserID().getUserID());
                    jobjtemp.put("Deduc", group.getDeductot());
                    jobjtemp.put("tempid", group.getTemplate().getTemplateid());
                    jobjtemp.put("tempname", group.getTemplate().getTemplatename());
                    jobjtemp.put("Tax", group.getTaxtot());
                    jobjtemp.put("Salary", group.getNet());
                    jobjtemp.put("month", group.getPaymonth());
                    jobjtemp.put("stdate", df1.format(group.getCreatedon()));
                    jobjtemp.put("enddate", df1.format(group.getCreatedfor()));
                    if(isExport){
                        int salStatus = group.getSalarystatus();
                        String state="";
                        if(salStatus==1){
                            state="Unauthorized";
                        }else if(salStatus==2){
                            state="Pending";
                        }else if(salStatus==3){
                            state="Authorized";
                        }
                        jobjtemp.put("salarystatus", state);
                    }else{
                        jobjtemp.put("salarystatus", group.getSalarystatus());
                    }
                    
                    jobjtemp.put("historyid", group.getHistoryid());
                    jobj.append("data", jobjtemp);
                }
            }
            if (lst.size() == 0) {
                JSONObject jobjtemp = new JSONObject();
                jobj.put("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", tabledata.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception e) {
            return (e.getMessage());
        }
    }

    public static JSONObject getAnnualExpense(String Year, HttpServletRequest request) {
        try {
            String query;
            List lst = null;
            Calendar cal = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            if (Year == null) {
                query = "select sum(ph.net),ph.paymonth,ph.createdon from Payhistory ph WHERE YEAR(ph.createdon) =? and ph.userID.company.companyID=? group by ph.paymonth order by ph.createdon";
                lst = HibernateUtil.executeQuery(query, new Object[]{cal2.get(Calendar.YEAR), AuthHandler.getCompanyid(request)});
            } else {
                query = "select sum(ph.net),ph.paymonth,ph.createdon from Payhistory ph WHERE YEAR(ph.createdon) = ? and ph.userID.company.companyID=? group by ph.paymonth order by ph.createdon";
                lst = HibernateUtil.executeQuery(query, new Object[]{Year, AuthHandler.getCompanyid(request)});
            }
            JSONObject jobj = new JSONObject();
            int previousMonth, currentMonth = 0, startingMonth = 0, checkMonth;
            DateFormat sdf = new SimpleDateFormat("MMM");
            if (lst.size() > 0) {
                Object obj2[] = (Object[]) lst.get(0);
                JSONObject job1;
                cal.setTime((Date) obj2[2]);
                checkMonth = cal.get(Calendar.MONTH);

                if (startingMonth < checkMonth) {
                    job1 = new JSONObject();
                    job1.put("amount", 0);
                    cal.set(Calendar.MONTH, startingMonth);
                    job1.put("month", sdf.format(cal.getTime()));
                    jobj.append("data", job1);
                }
                for (int i = 0; i < lst.size(); i++) {
                    Object obj[] = (Object[]) lst.get(i);
                    JSONObject job;
                    cal.setTime((Date) obj[2]);
                    previousMonth = currentMonth;
                    currentMonth = cal.get(Calendar.MONTH);
                    while (previousMonth + 1 < currentMonth) {
                        job = new JSONObject();
                        previousMonth++;
                        job.put("amount", 0);
                        cal.set(Calendar.MONTH, previousMonth);
                        job.put("month", sdf.format(cal.getTime()));
                        jobj.append("data", job);
                    }
                    while (previousMonth + 1 < currentMonth) {
                        job = new JSONObject();
                        previousMonth++;
                        job.put("amount", 0);
                        cal.set(Calendar.MONTH, previousMonth);
                        job.put("month", sdf.format(cal.getTime()));
                        jobj.append("data", job);
                    }
                    cal.set(Calendar.MONTH, currentMonth);
                    job = new JSONObject();
                    Double amt = Double.parseDouble(obj[0].toString());
                    NumberFormat formatter = new DecimalFormat("#0.00");
                    job.put("amount", formatter.format(amt));
                    job.put("month", sdf.format(cal.getTime()));
                    jobj.append("data", job);
                }
                while (++currentMonth < 12) {
                    JSONObject job = new JSONObject();
                    job.put("amount", 0);
                    cal.set(Calendar.MONTH, currentMonth);
                    job.put("month", sdf.format(cal.getTime()));
                    jobj.append("data", job);
                }
                jobj.append("data", jobj.get("data"));
                jobj.append("currency", AuthHandler.getCurrencyID(request));
                jobj.append("success", "true");
                return (jobj);
            } else {
                jobj.append("data", "");
                jobj.append("currency", "");
                jobj.append("success", "false");
                return jobj;
            }
        } catch (Exception e) {
            return (null);
        }
    }

    public String getHistWages(Session session, HttpServletRequest request) {

        try {
            String temid = request.getParameter("TempId");
            String histid = request.getParameter("histid");
            String query = "from Historydetail t where t.payhistory.historyid=? and t.name=?";
            List lst = (List) HibernateUtil.executeQuery(session, query, new Object[]{histid, "Wages"});

            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst.size(); i++) {
                masterDB.Historydetail wage = (masterDB.Historydetail) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("Rate", wage.getRate());
                jobjtemp.put("Type", wage.getType());
                jobjtemp.put("amount", wage.getAmount());
                jobj.append("Wage", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getHistTaxes(Session session, HttpServletRequest request) {

        try {
            String temid = request.getParameter("TempId");
            String histid = request.getParameter("histid");
            String query = "from Historydetail t where t.payhistory.historyid=? and t.name=?";
            List lst = (List) HibernateUtil.executeQuery(session, query, new Object[]{histid, "Taxes"});

            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst.size(); i++) {
                masterDB.Historydetail wage = (masterDB.Historydetail) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("Rate", wage.getRate());
                jobjtemp.put("Type", wage.getType());
                jobjtemp.put("amount", wage.getAmount());
                jobj.append("Tax", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String getHistDeduces(Session session, HttpServletRequest request) {

        try {
            String temid = request.getParameter("TempId");
            String histid = request.getParameter("histid");
            String query = "from Historydetail t where t.payhistory.historyid=? and t.name=?";
            List lst = (List) HibernateUtil.executeQuery(session, query, new Object[]{histid, "Deduction"});

            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst.size(); i++) {
                masterDB.Historydetail wage = (masterDB.Historydetail) lst.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("Rate", wage.getRate());
                if(wage.getType().equals("Unpaid_leaves")){
                    jobjtemp.put("Type",MessageSourceProxy.getMessage("hrms.payroll.Unpaidleaves",null,"Unpaid Leaves", LocaleUtil.getCompanyLocale(AuthHandler.getCompanyid(request),0))+"("+wage.getPayhistory().getUnpaidleaves()+")" );
                }else{
                    jobjtemp.put("Type", wage.getType());
                }
                jobjtemp.put("amount", wage.getAmount());
                jobj.append("Deduc", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (Exception se) {
            return (se.getMessage());
        } finally {
        }
    }

    public String GetTaxperCatgry(Session session, HttpServletRequest request) throws SessionExpiredException, ServiceException, JSONException {
        try {
            MasterData m = null;
            String ctg = "";
            String query1 = "";
            List lst2 = null;
            List lst = null;
            int cnt = 0;
            String cid = AuthHandler.getCompanyid(request);
            int start = 0;
            int limit = 15;
            if (!StringUtil.isNullOrEmpty(request.getParameter("start"))) {
                start = Integer.parseInt(request.getParameter("start"));
                limit = Integer.parseInt(request.getParameter("limit"));
            }
            if (StringUtil.equal(request.getParameter("categoryid"), "0")) {
                query1 = "from Taxmaster where  companyid=? and tcode=?";
                lst = HibernateUtil.executeQuery(session, query1, new Object[]{cid, "incometax"});
                lst2 = (List) HibernateUtil.executeQueryPaging(session, query1, new Object[]{cid, "incometax"}, new Integer[]{start, limit});
            } else {
                query1 = "from Taxmaster where categoryid=? and companyid=? and tcode=?";
                lst = HibernateUtil.executeQuery(session, query1, new Object[]{request.getParameter("categoryid"), cid, "incometax"});
                lst2 = (List) HibernateUtil.executeQueryPaging(session, query1, new Object[]{request.getParameter("categoryid"), cid, "incometax"}, new Integer[]{start, limit});
            }
            cnt = lst.size();
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst2.size(); i++) {
                masterDB.Taxmaster taxobj = (masterDB.Taxmaster) lst2.get(i);
                JSONObject jobjtemp = new JSONObject();
                if (!StringUtil.isNullOrEmpty(taxobj.getCategoryid())) {
                    m = (MasterData) session.get(MasterData.class, taxobj.getCategoryid());
                    ctg = m.getValue();
                    jobjtemp.put("category", ctg);
                }
                jobjtemp.put("rate", taxobj.getRate() == 1 ? taxobj.getCash() : "");
                jobjtemp.put("rangefrom", taxobj.getRangefrom());
                jobjtemp.put("rangeto", taxobj.getRangeto());
                jobjtemp.put("id", taxobj.getTaxid());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", cnt);
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("GetTaxperCatgry", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("GetTaxperCatgry", ex);
        } catch (JSONException ex) {
            throw new JSONException("GetTaxperCatgry");
        } finally {
        }
    }

    public String getTemplistperDesign(Session session, HttpServletRequest request) throws SessionExpiredException, ServiceException, JSONException {
        try {
           // String query1 = "from Template where designationid.id=? and startrange <= ? and endrange >= ? and companyid=?";
            String query1 = "from Template where designationid.id=? and companyid=?";
            String cid = AuthHandler.getCompanyid(request);
            List lst2;

            lst2 = (List) HibernateUtil.executeQuery(session, query1, new Object[]{request.getParameter("desigid"),
            cid});
           // request.getParameter("sal"),request.getParameter("sal"),cid});

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
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (SessionExpiredException se) {
        	se.printStackTrace();
            throw new SessionExpiredException("getTemplistperDesign", se.getMessage());
        } catch (ServiceException ex) {
        	ex.printStackTrace();
            throw ServiceException.FAILURE("getTemplistperDesign", ex);
        } catch (JSONException ex) {
        	ex.printStackTrace();
            throw new JSONException(ex.getMessage());
        } finally {
        }
    }

    public String getDefualtWages(Session session, HttpServletRequest request) throws SessionExpiredException, ServiceException, JSONException {
        try {
            String query1 = "from Wagemaster where isdefault=? and companydetails.companyID=?";
            String cid = AuthHandler.getCompanyid(request);
            List lst2;
            lst2 = (List) HibernateUtil.executeQuery(session, query1, new Object[]{true, cid});
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst2.size(); i++) {
                masterDB.Wagemaster taxobj = (masterDB.Wagemaster) lst2.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("WagesId", taxobj.getWageid());
                jobjtemp.put("WagesType", taxobj.getWagetype());
                jobjtemp.put("WagesRate", taxobj.getCash());
                jobjtemp.put("ratetype", taxobj.getRate());
                jobjtemp.put("WagesCode", taxobj.getWcode());
                jobjtemp.put("amount", taxobj.getCash());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst2.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getDefualtWages", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getDefualtWages", ex);
        } catch (JSONException ex) {
            throw new JSONException(ex.getMessage());
        } finally {
        }
    }

    public String getDefualtDeduction(Session session, HttpServletRequest request) throws SessionExpiredException, ServiceException, JSONException {
        try {
            String query1 = "from Deductionmaster where isdefault=? and companydetails.companyID=?";
            String cid = AuthHandler.getCompanyid(request);
            List lst2;
            lst2 = (List) HibernateUtil.executeQuery(session, query1, new Object[]{true, cid});
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst2.size(); i++) {
                masterDB.Deductionmaster taxobj = (masterDB.Deductionmaster) lst2.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("DeducId", taxobj.getDeductionid());
                jobjtemp.put("DeductionType", taxobj.getDeductiontype());
                jobjtemp.put("DeducRate", taxobj.getCash());
                jobjtemp.put("DeducCode", taxobj.getDcode());
                jobjtemp.put("ratetype", taxobj.getRate());
                jobjtemp.put("amount", taxobj.getCash());
                jobj.append("data", jobjtemp);
            }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj1.put("totalcount", lst2.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getDefualtDeduction", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getDefualtDeduction", ex);
        } catch (JSONException ex) {
            throw new JSONException(ex.getMessage());
        } finally {
        }
    }

    public String getDefualtTaxes(Session session, HttpServletRequest request) throws SessionExpiredException, ServiceException, JSONException {
        try {
            String query1 = "from Taxmaster where isdefault=? and companydetails.companyID=?";
            String cid = AuthHandler.getCompanyid(request);
            List lst2;
            lst2 = (List) HibernateUtil.executeQuery(session, query1, new Object[]{true, cid});
            JSONObject jobj = new JSONObject();
            for (int i = 0; i < lst2.size(); i++) {
                masterDB.Taxmaster taxobj = (masterDB.Taxmaster) lst2.get(i);
                JSONObject jobjtemp = new JSONObject();
                jobjtemp.put("TaxId", taxobj.getTaxid());
                jobjtemp.put("TaxType", taxobj.getTaxtype());
                jobjtemp.put("TaxRate", taxobj.getCash());
                jobjtemp.put("ratetype", taxobj.getRate());
                jobjtemp.put("TaxCode", taxobj.getTcode());
                jobjtemp.put("amount", taxobj.getCash());
                jobj.append("data", jobjtemp);
            }
            if(lst2.size()==0){
                  jobj.put("data","");
                }
            jobj.put("success", true);
            JSONObject jobj1 = new JSONObject();
            jobj1.put("valid", true);
            jobj.put("totalcount", lst2.size());
            jobj1.put("data", jobj);
            return (jobj1.toString());
        } catch (SessionExpiredException se) {
            throw new SessionExpiredException("getDefualtTaxes", se.getMessage());
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("getDefualtTaxes", ex);
        } catch (JSONException ex) {
            throw new JSONException(ex.getMessage());
        } finally {
        }
    }
}
