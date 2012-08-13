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
package com.krawler.spring.hrms.template;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.hrms.template.db.HtmlTemplate;
import com.krawler.spring.hrms.template.db.LetterHistory;
import com.krawler.spring.hrms.template.db.PlaceHolder;
import com.krawler.spring.hrms.template.db.PlaceHolderMapping;
import com.krawler.spring.hrms.template.db.TargetList;
import com.krawler.spring.hrms.template.db.TargetListTargets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author Abhishek Dubey <abhishek.dubey@krawlernetworks.com>
 */
public class hrmsTemplateDAOImpl implements hrmsTemplateDAO{
    private HibernateTemplate hibernateTemplate;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public KwlReturnObject getParameterType(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try {
            String hql = "select distinct type from PlaceHolder";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getParameterTypeValuePair(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try {
            String hql = "from PlaceHolder";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject saveTemplate(HashMap<String, Object> requestParams){
        HtmlTemplate ht = null;
        try {
            if (requestParams.containsKey("tid")) {
                ht = (HtmlTemplate) hibernateTemplate.get(HtmlTemplate.class, requestParams.get("tid").toString());
            } else {
                ht = new HtmlTemplate();
            }
            ht.setText((String)requestParams.get("tbody"));
            ht.setDescr((String)requestParams.get("tdesc"));
            ht.setPlaintext((String)requestParams.get("tplaintext"));
            ht.setSubject((String)requestParams.get("tsub"));
            ht.setName((String)requestParams.get("tname"));
            ht.setCompany((Company)requestParams.get("company"));
            ///mode????????????????
            //tbody,mode,tdesc,tname,tsub

            if (requestParams.get("Action").equals("Add")) {
                ht.setCreatedon((Date)requestParams.get("date"));
                hibernateTemplate.save(ht);
            } else {
                ht.setModifiedon((Date)requestParams.get("date"));
                hibernateTemplate.update(ht);
            }
            List a = new ArrayList();
            a.add(ht.getId());
            return new KwlReturnObject(true, "{\"valid\":\"true\",\"success\":\"true\",data:{msg:\"Template saved successfully.\",value:\"success\",action:\"" + requestParams.get("Action") + "ed\"}}", "", a, 0);
        } catch (Exception se) {
            se.printStackTrace();
        }
        return new KwlReturnObject(false, "{\"valid\":\"true\",data:{value:\"failed\"}}", "", null, 0);
    }

    public KwlReturnObject getTemplates(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        ArrayList<Object> value = new ArrayList<Object>();
        String[] searchCol = null;
        try {
            String Cid = (String) requestParams.get("Cid");
            String hql = "from HtmlTemplate where company.companyID = ? ";
            value.add(Cid);
            
            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }
            
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public KwlReturnObject delTemplate(HashMap<String, Object> requestParams) {
        try {
            HtmlTemplate ht = (HtmlTemplate) hibernateTemplate.get(HtmlTemplate.class, (String)requestParams.get("tempid"));
            hibernateTemplate.delete(ht);
            return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"success\"}}", "", null, 0);
        } catch (Exception se) {
            se.printStackTrace();
            return new KwlReturnObject(false, "{\"valid\":\"true\",data:{value:\"failed\"}}", "", null, 0);
        }
    }

    public KwlReturnObject getTemplateContent(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try {
            String id = (String) requestParams.get("id");
            String hql = "from HtmlTemplate where id = ? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getTemplateTargetList(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try {
            String Cid = (String) requestParams.get("Cid");
            String hql = "from TargetList where creator.company.companyID = ? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{Cid});
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

   public KwlReturnObject saveTemplateTargetList(HashMap<String, Object> requestParams){
        TargetList tl = null;
        try {
            if (requestParams.containsKey("tid")) {
                tl = (TargetList) hibernateTemplate.get(TargetList.class, requestParams.get("tid").toString());
            } else {
                tl = new TargetList();
            }
//            tl.setDeleted(deleted);
            tl.setDescription((String)requestParams.get("descr"));
            tl.setName((String)requestParams.get("name"));
//            tl.setSaveflag(saveflag);
            if (requestParams.get("Action").equals("Add")) {
                tl.setCreator((User)requestParams.get("creator"));
                tl.setCreatedon((Date)requestParams.get("date"));
                hibernateTemplate.save(tl);
            } else {
                tl.setModifiedon((Date)requestParams.get("date"));
                hibernateTemplate.update(tl);
            }
            return new KwlReturnObject(true, "{\"valid\":\"true\",\"success\":\"true\",data:{msg:\"Target List saved successfully.\",value:\"success\",action:\"" + requestParams.get("Action") + "ed\"}}", "", null, 0);
        } catch (Exception se) {
            se.printStackTrace();
        }
        return new KwlReturnObject(false, "{\"valid\":\"true\",data:{value:\"failed\"}}", "", null, 0);
    }

   public KwlReturnObject delTemplateTargetList(HashMap<String, Object> requestParams) {
        try {
            TargetList tl = (TargetList) hibernateTemplate.get(TargetList.class, (String)requestParams.get("listid"));
            hibernateTemplate.delete(tl);
            return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"success\"}}", "", null, 0);
        } catch (Exception se) {
            se.printStackTrace();
            return new KwlReturnObject(false, "{\"valid\":\"true\",data:{value:\"failed\"}}", "", null, 0);
        }
    }

   public KwlReturnObject getTemplateTargetListDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try {
            String listid = (String) requestParams.get("listid");
            String hql = "from TargetListTargets where targetlistid.id = ? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{listid});
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject saveTemplateTargetListDetails(HashMap<String, Object> requestParams){
        TargetListTargets tlt = null;
        try {
                tlt = new TargetListTargets();
                tlt.setUsrid((String)requestParams.get("uid"));
                tlt.setFname((String)requestParams.get("fname"));
                tlt.setLname((String)requestParams.get("lname"));
                tlt.setEmailid((String)requestParams.get("emailid"));
                tlt.setTargetlistid((TargetList)requestParams.get("list"));               
                hibernateTemplate.save(tlt);
            return new KwlReturnObject(true, "{\"valid\":\"true\",\"success\":\"true\",data:{msg:\"Target Details saved successfully.\",value:\"success\",action:\"" + requestParams.get("Action") + "ed\"}}", "", null, 0);
        } catch (Exception se) {
            se.printStackTrace();
    //        throw ServiceException.FAILURE("hrmsInvestmentDAOImpl.setInvestmentStructure : " + se.getMessage(), se);
        }
        return new KwlReturnObject(false, "{\"valid\":\"true\",data:{value:\"failed\"}}", "", null, 0);
    }



   public KwlReturnObject delTemplateTargetListDetails(HashMap<String, Object> requestParams) {
        try {
            String hql = "delete from TargetListTargets where targetlistid.id = ? ";
            HibernateUtil.executeUpdate(hibernateTemplate, hql, new Object[]{(String)requestParams.get("listid")});
            return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"success\"}}", "", null, 0);
        } catch (Exception se) {
            se.printStackTrace();
            return new KwlReturnObject(false, "{\"valid\":\"true\",data:{value:\"failed\"}}", "", null, 0);
        }
    }


   public KwlReturnObject getNewTemplateTargetListDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try {
            String Cid = (String) requestParams.get("Cid");
            String hql = "from User where company.companyID = ? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{Cid});
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

   public KwlReturnObject delTemplatePlaceHolderMapping(HashMap<String, Object> requestParams) {
        try {
            String hql = "delete from PlaceHolderMapping where template.id = ? ";
            HibernateUtil.executeUpdate(hibernateTemplate, hql, new Object[]{(String)requestParams.get("htid")});
            return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"success\"}}", "", null, 0);
        } catch (Exception se) {
            se.printStackTrace();
            return new KwlReturnObject(false, "{\"valid\":\"true\",data:{value:\"failed\"}}", "", null, 0);
        }
    }


    public KwlReturnObject saveTemplatePlaceHolderMapping(HashMap<String, Object> requestParams){
        PlaceHolderMapping phm = null;
        try {
                phm = new PlaceHolderMapping();
                phm.setPlaceholder((PlaceHolder)hibernateTemplate.get(PlaceHolder.class, (String)requestParams.get("phid")));
                phm.setTemplate((HtmlTemplate)requestParams.get("ht"));
                hibernateTemplate.save(phm);
            return new KwlReturnObject(true, "{\"valid\":\"true\",\"success\":\"true\",data:{msg:\"Template Placeholder mapping saved successfully.\",value:\"success\",action:\"" + requestParams.get("Action") + "ed\"}}", "", null, 0);
        } catch (Exception se) {
            se.printStackTrace();
    //        throw ServiceException.FAILURE("hrmsInvestmentDAOImpl.setInvestmentStructure : " + se.getMessage(), se);
        }
        return new KwlReturnObject(false, "{\"valid\":\"true\",data:{value:\"failed\"}}", "", null, 0);
    }


    public KwlReturnObject getIdFromPlaceholders(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try {
            String hql = "select id from PlaceHolder where (concat(type,'_',placeholder)) in ("+(String)requestParams.get("phstrings")+")";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }
}
