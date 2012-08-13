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
package com.krawler.spring.hrms.rec.agency;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.recruitment.Allapplications;
import com.krawler.hrms.recruitment.Agency;
import com.krawler.hrms.recruitment.Applyagency;
import com.krawler.hrms.recruitment.Positionmain;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.JSONArray;
import com.krawler.utils.json.base.JSONException;
import java.util.HashMap;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.krawler.utils.json.base.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Hibernate;


/**
 *
 * @author shs
 */
public class hrmsRecAgencyDAOImpl implements hrmsRecAgencyDAO {

    private HibernateTemplate hibernateTemplate;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }


     public KwlReturnObject getAgency(HashMap<String,Object> requestParams){
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {
            String hql = "";
            ArrayList name = null;
            ArrayList value = null;
            String[] searchCol = null;
            hql = "from Agency ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = (ArrayList)requestParams.get("filter_names");
                value = (ArrayList)requestParams.get("filter_values");
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }
            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                name.clear();
                value.clear();
                name = (ArrayList)requestParams.get("order_by");
                value = (ArrayList)requestParams.get("order_type");
                hql +=com.krawler.common.util.StringUtil.orderQuery(name, value);
            }
            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null) {
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }

     public KwlReturnObject addagency(HashMap<String, Object> requestParams) throws ServiceException {
        List <Agency> list = new ArrayList<Agency>();
        boolean success = false;
        boolean isDetetedRecFound = true;
        String msg = "Agency added successfully.";
        try {
                Agency agency = null;
                List<Agency> tabledata = null;
                String hql = "from Agency where agencyname=? and agencyweb=? and company.companyID=?";
                if(requestParams.get("agencyid")!=null){
                    hql = "from Agency where agencyname=? and agencyweb=? and company.companyID=? and agencyid !=? ";
                    tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{requestParams.get("agencyname"), requestParams.get("agencyweb"), requestParams.get("company"), requestParams.get("agencyid")});
                } else {
                	hql = "from Agency where agencyname=? and agencyweb=? and company.companyID=? and delflag=1";
                    tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{requestParams.get("agencyname"), requestParams.get("agencyweb"), requestParams.get("company")});
                    if(tabledata.size()<=0){
                    	isDetetedRecFound = false;
                    	hql = "from Agency where agencyname=? and agencyweb=? and company.companyID=?";
                        tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{requestParams.get("agencyname"), requestParams.get("agencyweb"), requestParams.get("company")});
                    }
                }
                Iterator<Agency> itr = tabledata.iterator();
                int count = tabledata.size();
                if (count > 0 && !isDetetedRecFound) {
                    msg = "exist";
                    return new KwlReturnObject(success, msg, "", null, 0);
                } else {
                    if(requestParams.get("agencyid")!=null)
                        agency = (Agency) hibernateTemplate.get(Agency.class, requestParams.get("agencyid").toString());
                    else{
                    	agency = new Agency();
                		String id = UUID.randomUUID().toString();
                		agency.setAgencyid(id);
                    }
                if(requestParams.get("company")!=null)
                    agency.setCompany((Company)hibernateTemplate.get(Company.class,  requestParams.get("company").toString()));
                if(requestParams.get("delflag")!=null)
                    agency.setDelflag(Integer.valueOf(requestParams.get("delflag").toString()));
                if(requestParams.get("agencyname")!=null)
                    agency.setAgencyname(requestParams.get("agencyname").toString());
                if(requestParams.get("agencyweb")!=null)
                    agency.setAgencyweb(requestParams.get("agencyweb").toString());
                if(requestParams.get("reccost")!=null)
                    agency.setReccost(requestParams.get("reccost").toString());
                if(requestParams.get("conperson")!=null)
                    agency.setConperson(requestParams.get("conperson").toString());
                if(requestParams.get("apprman")!=null)
                    agency.setApprman((User)hibernateTemplate.get(User.class,  requestParams.get("apprman").toString()));
                if(requestParams.get("agencyno")!=null)
                    agency.setAgencyno(requestParams.get("agencyno").toString());
                if(requestParams.get("agencyadd")!=null)
                    agency.setAgencyadd(requestParams.get("agencyadd").toString());
                hibernateTemplate.save(agency);
                list.add(agency);
                success = true;
                }
        } catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }
        finally{
            return new KwlReturnObject(success, msg, "-1", list, list.size());
        }
    }

     public KwlReturnObject deleteAgency(HashMap<String,Object> requestParams){
        boolean success = false;
        List tabledata = null;
        KwlReturnObject result = null;
        try {
            String agencyid = requestParams.get("agencyid").toString();
            String hql = "from Agency where agencyid=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, agencyid);
            if (!tabledata.isEmpty()) {
                    Agency agency = (Agency) tabledata.get(0);
                    agency.setDelflag(1);
                    hibernateTemplate.update(agency);
            }
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "Agency Deleted successfully.", "-1", tabledata, tabledata.size());
        }
    }

     public KwlReturnObject deleteApplyAgency(HashMap<String,Object> requestParams){
        boolean success = false;
        List tabledata = null;
        KwlReturnObject result = null;
        try {
            String agencyid = requestParams.get("agencyid").toString();
            String posid = requestParams.get("posid").toString();
            String hql = "Delete from Applyagency where agencyid=? and posid=?";
            HibernateUtil.executeUpdate(hibernateTemplate, hql, new Object[]{agencyid,posid});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "ApplyAgency Deleted successfully.", "-1", tabledata, 0);
        }
    }

     public KwlReturnObject addApplyAgency(HashMap<String, Object> requestParams) {
        List <Applyagency> list = new ArrayList<Applyagency>();
        boolean success = false;
        try {
                Applyagency applyagency = null;
                if(requestParams.get("applyid")!=null)
                    applyagency = (Applyagency) hibernateTemplate.get(Applyagency.class, requestParams.get("applyid").toString());
                else{
                    applyagency = new Applyagency();
                    String id = UUID.randomUUID().toString();
                    applyagency.setApplyid(id);
                }
                if(requestParams.get("applyagency")!=null)
                    applyagency.setApplyagency((Agency)hibernateTemplate.get(Agency.class,  requestParams.get("applyagency").toString()));
                if(requestParams.get("applypos")!=null)
                    applyagency.setApplypos((Positionmain)hibernateTemplate.get(Positionmain.class,  requestParams.get("applypos").toString()));
                hibernateTemplate.save(applyagency);
                list.add(applyagency);
                success = true;
        } catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, "Applyagency added successfully.", "-1", list, list.size());
        }
    }

    public KwlReturnObject getApplyagency(HashMap<String,Object> requestParams){
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {
            String hql = "";
            ArrayList name = null;
            ArrayList value = null;
            String[] searchCol = null;
            hql = "from Applyagency ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }
            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                name.clear();
                value.clear();
                name = (ArrayList)requestParams.get("order_by");
                value = (ArrayList)requestParams.get("order_type");
                hql +=com.krawler.common.util.StringUtil.orderQuery(name, value);
            }
            if(requestParams.get("searchcol")!=null)
                searchCol = (String[])requestParams.get("searchcol");
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }

}
