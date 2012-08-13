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

package com.krawler.spring.hrms.common;


import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.ConfigData;
import com.krawler.common.admin.ConfigType;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.Role;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.admin.UserSearchState;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.admin.hrms_Modules;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.handlers.FileUploadHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.ess.Emphistory;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.esp.servlets.ProfileImageServlet;
import com.krawler.hrms.ess.Empexperience;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.performance.Assignmanager;
import com.krawler.hrms.performance.Assignreviewer;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.customcol.customcolDAO;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;

import java.io.Serializable;
import java.lang.String;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.hibernate.SessionFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author shs
 */
public class hrmsCommonDAOImpl implements hrmsCommonDAO, MessageSourceAware{

    private HibernateTemplate hibernateTemplate;
    private storageHandlerImpl storageHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private customcolDAO customcolDAOObj;
    private MessageSource messageSource;
    
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

    public void setCustomcolDAO(customcolDAO customcolDAOObj) {
        this.customcolDAOObj = customcolDAOObj;
    }

    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj1) {
        this.storageHandlerImplObj = storageHandlerImplObj1;
    }

     public KwlReturnObject getMasterDataField(HashMap<String,Object> requestParams) {
            boolean success = false;
            List lst = null;
        try {
            String hql = "from MasterData where masterid.id=? and ( company is null or company.companyID=? )order by weightage,value ";
            Integer masterid = (Integer)requestParams.get("masterid");
            //Master master = (Master) hibernateTemplate.get(Master.class, masterid);
            String companyid = String.valueOf(requestParams.get("companyid"));
            //Company company = (Company) hibernateTemplate.get(Company.class, companyid);
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{masterid,companyid});
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }finally{
            return new KwlReturnObject(success, "", "-1", lst, lst.size());
        }

    }

     public KwlReturnObject getCostCenter(HashMap<String,Object> requestParams) {
            boolean success = false;
            List lst = null;
        try {
            String hql = "from CostCenter where company.companyID=?";
            String companyid = String.valueOf(requestParams.get("companyid"));
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{companyid});
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }finally{
            return new KwlReturnObject(success, "", "-1", lst, lst.size());
        }

    }

     public KwlReturnObject getMasterData(HashMap<String,Object> requestParams) {
            boolean success = false;
            List<MasterData> lst = new ArrayList<MasterData>();
        try {
            String masterid = requestParams.get("masterid").toString();
            MasterData md = (MasterData) hibernateTemplate.load(MasterData.class, masterid);
            lst.add(md);
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }finally{
            return new KwlReturnObject(success, "", "-1", lst, lst.size());
        }

    }

     public KwlReturnObject getManagers(HashMap<String,Object> requestParams){
        boolean success = false;
        List list = null;
        try {
            String SELECT_USER_INFO = "from Useraccount where (role.ID=? or role.ID=?) and user.company.companyID=? and user.deleteflag=? order by user.firstName";
            String role1 = requestParams.get("role1").toString();
            String role2 = requestParams.get("role2").toString();
            String companyid = requestParams.get("companyid").toString();
            boolean deleted = Boolean.valueOf(requestParams.get("deleted").toString());
            int delete = 0;
            if(deleted)
                delete=1;
            else
                delete=0;

            list = HibernateUtil.executeQuery(hibernateTemplate, SELECT_USER_INFO, new Object[]{role1,role2,companyid,delete});
            success = true;
        } catch (Exception e) {
            success = true;
        }

        return new KwlReturnObject(success, "success", "-1", list, list.size());
    }

     public KwlReturnObject getUserDetailsbyUserid(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try{
            String empid =(String) requestParams.get("empid");
            String hql = "from User where userID=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{empid});
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getEmpDetailsbyEmpid(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try{
            String empid =(String) requestParams.get("empid");
            String hql = "from Empprofile where userID=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{empid});
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getUserDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        int count = 0;
        try{
            String templateid =(String) requestParams.get("templateid");
            boolean deleted =(Boolean) requestParams.get("deleted");
            String companyid =(String) requestParams.get("companyid");
            String allflag = "true";
            if(requestParams.containsKey("allflag"))
                allflag = requestParams.get("allflag").toString();
            int start = 0;
            int limit = 0;

            if(allflag.equals("false")){
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }

            String[] searchcol;
            searchcol=new String[]{"firstName","lastName"};

            ArrayList params = new ArrayList();
            params.add(templateid);
            params.add(deleted);
            params.add(companyid);

            String hql = "from User where templateid=? and deleted=? and company.companyID=?";
            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                        StringUtil.insertParamSearchString(params, ss, 1);
                        String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                        hql +=searchQuery;
                }
            }
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, params.toArray());
            count = tabledata.size();
            if(allflag.equals("false"))
                tabledata = HibernateUtil.executeQueryPaging(hibernateTemplate, hql, params.toArray(), new Integer[]{start, limit});
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }


    public KwlReturnObject getCompanyid(HashMap<String, Object> requestParams) {
        String result;
        List tabledata = null;
        boolean success = false;
        try {
            String companyid = requestParams.get("companyid").toString();
            String hql = "from Company  where companyID=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{companyid});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject gethrms_EmailTemplates(HashMap<String, Object> requestParams) {
        String result;
        List tabledata = null;
        boolean success = false;
        try {
            String temptype = requestParams.get("temptype").toString();
            String hql = "select body_plain,body_html,subject from hrms_EmailTemplates where templatetype=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{temptype});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }


    public KwlReturnObject getEmployeeList(HashMap<String,Object> requestParams){
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {
            String hql = "";
            ArrayList name = null;
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "select ua from Useraccount ua";
            if(requestParams.get("searchcol")!=null&&requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += " where ua."+searchCol[0]+" = ?";
            }
           List list  = HibernateUtil.executeQuery(hibernateTemplate,hql, new Object[]{requestParams.get("ss")});
           
            success = true;
            result =  new KwlReturnObject(success, "", "-1", list, list.size());
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }
     // @@ change reference everywhere

     public  KwlReturnObject getEmpprofileuser(HashMap<String,Object> requestParams){
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {
            String hql = "";
            ArrayList name = null;
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "select emp,ua from Empprofile emp right outer join emp.Useraccount ua ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = (ArrayList)requestParams.get("filter_names");
                value = (ArrayList)requestParams.get("filter_values");
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }
            if(requestParams.get("searchcol")!=null&&requestParams.get("ss")!=null){
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
        } finally {
            return result;
        }
    }

    public KwlReturnObject getEmpidFormatEdit(HashMap<String, Object> requestParameters) {
        String mainstr = "";
        String empids = "";
        boolean success = false;
        List<String> lst = new ArrayList<String>();
        try {
            String cmpnyid = requestParameters.get("companyid").toString();
            Integer empid = Integer.parseInt(requestParameters.get("empid").toString());
            KwlReturnObject result = getCompanyid(requestParameters);
            List list = result.getEntityList();
            for (int ctr = 0; ctr < list.size(); ctr++) {
                Company cmp = (Company) list.get(ctr);
                CompanyPreferences cmppref = (CompanyPreferences) hibernateTemplate.get(CompanyPreferences.class, cmpnyid);
                String empidformat = cmppref.getEmpidformat() == null ? "" : cmppref.getEmpidformat();
                String[] row = (empidformat).split("-");
                if (row.length == 1) {
                    if ((row[0]).startsWith("0")) {
                        int len = row[0].length();
                        String regex = "%0" + len + "d";
                        empids = String.format(regex, empid);
                        mainstr = empids;
                    } else {
                        mainstr = empid.toString();
                    }

                } else {
                    for (int i = 0; i < row.length; i++) {

                        if ((row[i]).startsWith("0")) {
                            int len = row[i].length();
                            String regex = "%0" + len + "d";
                            empids = String.format(regex, empid); // $$$Throws exception if not parsing

                        }
                        if (!row[i].equals("")) {
                            if (!(row[i]).startsWith("0")) {
                                mainstr = mainstr + row[i] + "-";
                            } else {
                                mainstr = mainstr + empids + "-";
                            }
                        } else {
                            mainstr = mainstr + empids + "-";

                        }
                    }
                    mainstr = mainstr.substring(0, mainstr.length() - 1);
                }
            }
            lst.add(mainstr);
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        return new KwlReturnObject(success, "success", "-1", lst, lst.size());
    }

    public KwlReturnObject getUserDateFormatter(HashMap<String, Object> requestParams) {
        SimpleDateFormat sdf = null;
        boolean success = false;
        List<DateFormat> list = new ArrayList<DateFormat>();
        try {
            HttpServletRequest request = (HttpServletRequest) requestParams.get("request");
            KWLDateFormat df = (KWLDateFormat) hibernateTemplate.load(KWLDateFormat.class, AuthHandler.getDateFormatID(request));
            String dateformat = "";
            String timeformat = AuthHandler.getUserTimeFormat(request);
            if (timeformat.equals("1")) {
                dateformat = df.getJavaForm().replace('H', 'h');
                if (!dateformat.equals(df.getJavaForm())) {
                    dateformat += " a";
                }
            } else {
                dateformat = df.getJavaForm();
            }
            sdf = new SimpleDateFormat(dateformat);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + AuthHandler.getTimeZoneDifference(request)));
            list.add(sdf);
            success = true;
        } catch (Exception e) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "", list, list.size());
        }
    }

    public KwlReturnObject getEmpForManagerFunction(HashMap<String, Object> requestParams) {
        boolean success = true;
        ArrayList params = new ArrayList();
        String ss = null;
        String paging = null;
        List lst = null;
        int count = 0;
        Integer start, limit;
        try {
            if (requestParams.get("ss") != null) {
                ss = requestParams.get("ss").toString();
            }
            if (requestParams.get("paging") != null) {
                paging = requestParams.get("paging").toString();
            }
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
            String hql = "from Empprofile where reportto.userID=? and userLogin.user.company.companyID=?";
            params.add(requestParams.get("userid"));
            params.add(requestParams.get("companyid"));
            if (!StringUtil.isNullOrEmpty(ss)) {
                StringUtil.insertParamSearchString(params, ss, 2);
                String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"userLogin.user.firstName", "userLogin.user.lastName"});
                hql += searchQuery;
            }
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, params.toArray());
            count = lst.size();
            if (!StringUtil.isNullOrEmpty(paging)) {
                lst = HibernateUtil.executeQueryPaging(hibernateTemplate, hql, params.toArray(), new Integer[]{start, limit});
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", lst, count);
        }
    }

    public KwlReturnObject getUserList(HashMap<String, Object> requestParams) {
        boolean success = true;
        ArrayList params = new ArrayList();
        String ss = null;
        String paging = null;
        String dept = null;
        List lst = null;
        int count = 0;
        Integer start, limit;
        try {
            if (requestParams.get("ss") != null) {
                ss = requestParams.get("ss").toString();
            }
            if (requestParams.get("paging") != null) {
                paging = requestParams.get("paging").toString();
            }
            if (requestParams.get("dept") != null) {
                dept = requestParams.get("dept").toString();
            }
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
            String hql = "from Useraccount where user.deleteflag=? and user.company.companyID=?";
            params.add(0);
            params.add(requestParams.get("companyid").toString());
            if (!StringUtil.isNullOrEmpty(dept)) {
                if (!StringUtil.equal(dept, "0")) {
                    hql = hql + " and department.id=?";
                    params.add(dept);
                }
            }
            if (!StringUtil.isNullOrEmpty(ss)) {
                StringUtil.insertParamSearchString(params, ss, 2);
                String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"user.firstName", "user.lastName"});
                hql += searchQuery;
            }
            hql += " order by user.firstName,user.lastName";
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, params.toArray());
            count = lst.size();
            if (!StringUtil.isNullOrEmpty(paging)) {
                lst = HibernateUtil.executeQueryPaging(hibernateTemplate, hql, params.toArray(), new Integer[]{start, limit});
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", lst, count);
        }
    }

    public KwlReturnObject isEmployee(HashMap<String, Object> requestParams) {
        boolean success = false;
        try {
            String userid = requestParams.get("userid").toString();
            Useraccount u = (Useraccount) hibernateTemplate.load(Useraccount.class, userid);
            if (StringUtil.equal(u.getRole().getID(), Role.COMPANY_USER)) {
                success = true;
            }

        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", null, 0);
        }
    }

    public KwlReturnObject isManager(HashMap<String, Object> requestParams) {
        boolean success = false;
        try {
            String userid = requestParams.get("userid").toString();
            Useraccount u = (Useraccount) hibernateTemplate.load(Useraccount.class, userid);
            if (StringUtil.equal(u.getRole().getID(), Role.COMPANY_MANAGER)) {
                success = true;
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", null, 0);
        }
    }

    public boolean isAdmin(String userid) {
        boolean success = false;
        try {
            Useraccount u = (Useraccount) hibernateTemplate.load(Useraccount.class, userid);
            if (StringUtil.equal(u.getRole().getID(), Role.COMPANY_ADMIN)) {
                success = true;
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return success;
        }
    }

    public boolean isSalaryManager(String userid) {
        boolean success = false;
        try {
            Useraccount u = (Useraccount) hibernateTemplate.load(Useraccount.class, userid);
            if(u.isSalarymanager()){
                success = true;
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return success;
        }
    }

    public boolean isPermitted(JSONObject perms, String featureName, String activityName){
        int perm = 0;
        int uperm = 0;
        try{
            perm=perms.getJSONObject("Perm").getJSONObject(featureName).optInt(activityName);
            uperm=perms.getJSONObject("UPerm").optInt(featureName);
        }catch(Exception e){
            e.printStackTrace();
        }
        if((perm & uperm)==perm)
            return true;

        return false;
    }

      public KwlReturnObject isSubscribed(HashMap<String, Object> requestParams) {

        boolean result = false;
        try {
            String cmpsubscription = requestParams.get("cmpsubscription").toString();
            int module = (Integer) requestParams.get("module");
            int subscrpt = Integer.parseInt(cmpsubscription);
            if (((int) Math.pow(2, module) & subscrpt) == (int) Math.pow(2, module)) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new KwlReturnObject(result, "", "", null, 0);
    }
    public KwlReturnObject getAssignmanager(HashMap<String, Object> requestParams) {
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList select = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            if(requestParams.containsKey("primary")&&(Boolean)requestParams.get("primary")){
                hql = "from Assignmanager where id=?";
                String id = requestParams.get("id").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
                hql = "from Assignmanager ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }

            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = new ArrayList((List<String>)requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>)requestParams.get("order_type"));
                hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            if (requestParams.get("select") != null) {
                select = new ArrayList((List<String>) requestParams.get("select"));
                String selectstr = "select ";
                for (int ctr = 0; ctr < select.size(); ctr++) {
                    selectstr += select.get(ctr) + ",";
                }
                selectstr = selectstr.substring(0, selectstr.length() - 1);
                hql = selectstr + " " + hql;
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
        	ex.printStackTrace();
            success = false;
        } finally {
            return result;
        }
    }

    public KwlReturnObject getAssignSalaryManager(HashMap<String, Object> requestParams) {
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            String[] searchCol = null;
            hql = "from Useraccount  ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }
            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
        	ex.printStackTrace();
            success = false;
        } finally {
            return result;
        }
    }

     public KwlReturnObject addAssignSalaryManager(final String[] managerIDs, final String[] availmanagerid) {
        boolean success = true;
        int numRow = 0;
        try{
            final String hql1 = "Update Useraccount set salarymanager= false where userid in (:managerids) ";
            numRow = (Integer) hibernateTemplate.execute(new HibernateCallback() {

                public Object doInHibernate(Session session) {
                    int numRows = 0;
                    Query query = session.createQuery(hql1);
                    if (managerIDs != null) {

                        query.setParameterList("managerids", availmanagerid);

                    }
                    numRows = query.executeUpdate();
                    return numRows;
                }
            });

            final String hql = "Update Useraccount set salarymanager= true where userid in (:managerids) ";
            numRow = (Integer) hibernateTemplate.execute(new HibernateCallback() {

                public Object doInHibernate(Session session) {
                    int numRows = 0;
                    Query query = session.createQuery(hql);
                    if (managerIDs != null) {

                        query.setParameterList("managerids", managerIDs);

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
           return new KwlReturnObject(success, "", "-1", null, numRow);
        }
    }
     public KwlReturnObject addAssignmanager(HashMap<String, Object> requestParams) {
        List <Assignmanager> list = new ArrayList<Assignmanager>();
        boolean success = false;
        try {
            Assignmanager assignman = (Assignmanager) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.hrms.performance.Assignmanager", "Id");
            list.add(assignman);

        }catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, "Manager Assigned successfully.", "-1", list, list.size());
        }
     }

// @@ change references

    public KwlReturnObject getAssignreviewer(HashMap<String, Object> requestParams) {
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList select = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            if (requestParams.containsKey("primary") && (Boolean) requestParams.get("primary")) {
                hql = "from Assignreviewer where id=?";
                String id = requestParams.get("id").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            if(requestParams.get("distinct") != null) {
                hql = "select distinct " + requestParams.get("distinct") + " from Assignreviewer ";
            }
            else {
                hql = "from Assignreviewer ";
            }
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null) {
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }

            if (requestParams.get("searchcol") != null && requestParams.get("ss") != null) {
                searchCol = (String[]) requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null) {
                orderby = new ArrayList((List<String>)requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>)requestParams.get("order_type"));
                hql += com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            if (requestParams.get("select") != null) {
                select = new ArrayList((List<String>) requestParams.get("select"));
                String selectstr = "select ";
                for (int ctr = 0; ctr < select.size(); ctr++) {
                    selectstr += select.get(ctr) + ",";
                }
                selectstr = selectstr.substring(0, selectstr.length() - 1);
                hql = selectstr + " " + hql;
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public KwlReturnObject addAssignreviewer(HashMap<String, Object> requestParams) {
        List <Assignreviewer> list = new ArrayList<Assignreviewer>();
        boolean success = false;
        try {
            Assignreviewer assignrev = (Assignreviewer) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.hrms.performance.Assignreviewer", "Id");
            list.add(assignrev);

        }catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, "Reviewer Assigned successfully.", "-1", list, list.size());
        }
     }

    public KwlReturnObject getManagerCountforUserid(HashMap<String, Object> requestParams) {
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {

            String hql = "";
            hql = "select count(assignman.userID) from Assignmanager  where assignemp.userID=? and managerstatus=1";
            String id = requestParams.get("userid").toString();
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
            result = new KwlReturnObject(success, "success", "", lst, lst.size());
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public KwlReturnObject getReviewerCountforUserid(HashMap<String, Object> requestParams) {
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {

            String hql = "";
            hql = "select count(reviewer.userID) from Assignreviewer  where employee.userID=? and reviewerstatus=1";
            String id = requestParams.get("userid").toString();
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
            result = new KwlReturnObject(success, "success", "", lst, lst.size());
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return result;
        }
    }
    public KwlReturnObject getEmpProfile(HashMap<String,Object> requestParams) {
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
                hql = "from Empprofile where userID=?";
                String userid = requestParams.get("userid").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userid});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from Empprofile ";
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
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }

    public KwlReturnObject getUseraccount(HashMap<String,Object> requestParams) {
        boolean success = false;
        List lst = null;
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            ArrayList select = null;
            String[] searchCol = null;
            hql = "from Useraccount ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }

            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = new ArrayList((List<String>)requestParams.get("order_by"));
                ordertype = new ArrayList((List<String>)requestParams.get("order_type"));
                hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            if(requestParams.get("select")!=null){
                select = (ArrayList)requestParams.get("select");
                String selectstr = "select ";
                for(int ctr=0;ctr<select.size();ctr++){
                    selectstr += select.get(ctr)+",";
                }
                selectstr = selectstr.substring(0, selectstr.length()-1);
                hql = selectstr +" " + hql;
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
        	ex.printStackTrace();
            success = false;
        } finally {
            return result;
        }
    }
    
    public CompanyPreferences getCompanyPreferences(String companyid) {
    	CompanyPreferences cp = null;
        try {
        	cp = (CompanyPreferences) hibernateTemplate.get(CompanyPreferences.class, companyid);
        } catch (Exception ex) {
        	ex.printStackTrace();
        } finally {
            return cp;
        }
    }
    
    public KwlReturnObject checkForAdminTermination(HashMap<String,Object> requestParams) {
        boolean success = false;
        List<Useraccount> lst = null;
        KwlReturnObject result = null;
        try{
        	String[] userids = (String[]) requestParams.get("userids");
        	String in_query="";
        	for(int i=0;i<userids.length;i++){
        		if(i<userids.length-1){
        			in_query+="?,";
        		}
        		else{
        			in_query+="?";
        		}
        	}	
        	
        	Object[] params_value=new Object[userids.length+3];
        	for(int i=0;i<userids.length;i++){
        		params_value[i]=userids[i];	
        	}
        	
        	params_value[userids.length] = (String) requestParams.get("companyid");
        	params_value[userids.length+1] = (String) requestParams.get("roleid");
        	params_value[userids.length+2] = (Integer) requestParams.get("deleteflag");
        	
        	String query = "from Useraccount where user.userID IN ("+in_query+") and user.company.companyID = ? and role.ID = ? and user.deleteflag = ? order by user.firstName";
            lst = HibernateUtil.executeQuery(hibernateTemplate, query, params_value);
            success = true;
        }catch(Exception e){
        	e.printStackTrace();
        	success = false;
        }finally{
        	return new KwlReturnObject(success, "", "", lst, lst.size());
        }
    }
    
    public KwlReturnObject getAdmins(HashMap<String,Object> requestParams){
    	boolean success = false;
        List<Useraccount> lst = null;
        KwlReturnObject result = null;
        try{
        	String companyid = (String) requestParams.get("companyid");
        	String roleid = requestParams.get("roleid").toString();
        	Integer deleteflag = (Integer) requestParams.get("deleted");
        	String query = "from Useraccount where user.company.companyID = ? and role.ID = ? and user.deleteflag = ? order by user.firstName";
            lst = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{companyid, roleid, deleteflag});
            success = true;
        }catch(Exception e){
        	e.printStackTrace();
        	success = false;
        }finally{
        	return new KwlReturnObject(success, "", "", lst, lst.size());
        }
    }

    public KwlReturnObject getReportingTo(HashMap<String,Object> requestParams){
    	boolean success = false;
        List<Useraccount> lst = null;
        KwlReturnObject result = null;
        try{
        	String companyid = (String) requestParams.get("companyid");
        	String roleid = requestParams.get("roleid").toString();
        	Integer deleteflag = (Integer) requestParams.get("deleted");
        	String query = "from Useraccount where user.company.companyID = ? and role.ID != ? and user.deleteflag = ? order by user.firstName";
            lst = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{companyid, roleid, deleteflag});
            success = true;
        }catch(Exception e){
        	e.printStackTrace();
        	success = false;
        }finally{
        	return new KwlReturnObject(success, "", "", lst, lst.size());
        }
    }

     public KwlReturnObject addEmpprofile(HashMap<String, Object> requestParams) {
        List <Empprofile> list = new ArrayList<Empprofile>();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        boolean success = false;
        try {
                Empprofile empprofile = null;
                if(requestParams.get("userid")!=null){
                    empprofile = (Empprofile) hibernateTemplate.get(Empprofile.class, requestParams.get("userid").toString());
                    if(empprofile==null){
                        empprofile = new Empprofile();
                        empprofile.setUserLogin((UserLogin)hibernateTemplate.get(UserLogin.class,  requestParams.get("userid").toString()));
                    }
                }else{
                    empprofile = new Empprofile();
                    String id = UUID.randomUUID().toString();
                    empprofile.setUserID(id);
                }

                if(requestParams.get("userlogin")!=null)
                    empprofile.setUserLogin((UserLogin)hibernateTemplate.get(UserLogin.class,  requestParams.get("userlogin").toString()));
                if(requestParams.get("dob")!=null)
                    empprofile.setDoB(fmt.parse(requestParams.get("dob").toString()));
                if(requestParams.get("middlename")!=null)
                    empprofile.setMiddlename(requestParams.get("middlename").toString());
                if(requestParams.get("gender")!=null)
                    empprofile.setGender(requestParams.get("gender").toString());
                if(requestParams.get("marriage")!=null)
                    empprofile.setMarriage(requestParams.get("marriage").toString());
                if(requestParams.get("bloodgrp")!=null)
                    empprofile.setBloodgrp(requestParams.get("bloodgrp").toString());
                if(requestParams.get("fathername")!=null)
                    empprofile.setFathername(requestParams.get("fathername").toString());
                if(requestParams.get("fatherdob")!=null)
                    empprofile.setFatherDoB(fmt.parse(requestParams.get("fatherdob").toString()));
                if(requestParams.get("mothername")!=null)
                    empprofile.setMothername(requestParams.get("mothername").toString());
                if(requestParams.get("motherdob")!=null)
                    empprofile.setMotherDoB(fmt.parse(requestParams.get("motherdob").toString()));
                if(requestParams.get("spousename")!=null)
                    empprofile.setSpousename(requestParams.get("spousename").toString());
                if(requestParams.get("spousedob")!=null)
                    empprofile.setSpouseDoB(fmt.parse(requestParams.get("spousedob").toString()));
                if(requestParams.get("child1name")!=null)
                    empprofile.setChild1name(requestParams.get("child1name").toString());
                if(requestParams.get("child1dob")!=null)
                    empprofile.setChild1DoB(fmt.parse(requestParams.get("child1dob").toString()));
                if(requestParams.get("child2name")!=null)
                    empprofile.setChild2name(requestParams.get("child2name").toString());
                if(requestParams.get("child2dob")!=null)
                    empprofile.setChild2DoB(fmt.parse(requestParams.get("child2dob").toString()));
                if(requestParams.get("bankacc")!=null)
                    empprofile.setBankacc(requestParams.get("bankacc").toString());
                if(requestParams.get("bankname")!=null)
                    empprofile.setBankname(requestParams.get("bankname").toString());
                if(requestParams.get("bankbranch")!=null)
                    empprofile.setBankbranch(requestParams.get("bankbranch").toString());
                if(requestParams.get("panno")!=null)
                    empprofile.setPanno(requestParams.get("panno").toString());
                if(requestParams.get("pfno")!=null)
                    empprofile.setPfno(requestParams.get("pfno").toString());
                if(requestParams.get("drvlicense")!=null)
                    empprofile.setDrvlicense(requestParams.get("drvlicense").toString());
                if(requestParams.get("passportno")!=null)
                    empprofile.setPassportno(requestParams.get("passportno").toString());
                if(requestParams.get("exppassport")!=null)
                    empprofile.setExppassport(fmt.parse(requestParams.get("exppassport").toString()));
                if(requestParams.get("mobno")!=null)
                    empprofile.setMobno(requestParams.get("mobno").toString());
                if(requestParams.get("workno")!=null)
                    empprofile.setWorkno(requestParams.get("workno").toString());
                if(requestParams.get("landno")!=null)
                    empprofile.setLandno(requestParams.get("landno").toString());
                if(requestParams.get("presentaddr")!=null)
                    empprofile.setPresentaddr(requestParams.get("presentaddr").toString());
                if(requestParams.get("presentcity")!=null)
                    empprofile.setPresentcity(requestParams.get("presentcity").toString());
                if(requestParams.get("presentstate")!=null)
                    empprofile.setPresentstate(requestParams.get("presentstate").toString());
                if(requestParams.get("presentcountry")!=null)
                    empprofile.setPresentcountry((MasterData)hibernateTemplate.get(MasterData.class,  requestParams.get("presentcountry").toString()));
                if(requestParams.get("permaddr")!=null)
                    empprofile.setPermaddr(requestParams.get("permaddr").toString());
                if(requestParams.get("permcity")!=null)
                    empprofile.setPermcity(requestParams.get("permcity").toString());
                if(requestParams.get("permstate")!=null)
                    empprofile.setPermstate(requestParams.get("permstate").toString());
                if(requestParams.get("permcountry")!=null)
                    empprofile.setPermcountry((MasterData)hibernateTemplate.get(MasterData.class,  requestParams.get("permcountry").toString()));
                if(requestParams.get("mailaddr")!=null)
                    empprofile.setMailaddr(requestParams.get("mailaddr").toString());
                if(requestParams.get("emgname")!=null)
                    empprofile.setEmgname(requestParams.get("emgname").toString());
                if(requestParams.get("emgreln")!=null)
                    empprofile.setEmgreln(requestParams.get("emgreln").toString());
                if(requestParams.get("emghome")!=null)
                    empprofile.setEmghome(requestParams.get("emghome").toString());
                if(requestParams.get("emgwork")!=null)
                    empprofile.setEmgwork(requestParams.get("emgwork").toString());
                if(requestParams.get("emgmob")!=null)
                    empprofile.setEmgmob(requestParams.get("emgmob").toString());
                if(requestParams.get("emgaddr")!=null)
                    empprofile.setEmgaddr(requestParams.get("emgaddr").toString());
                if(requestParams.get("reportto")!=null)
                    empprofile.setReportto((User)hibernateTemplate.get(User.class,  requestParams.get("reportto").toString()));
                if(requestParams.get("joindate")!=null)
                    empprofile.setJoindate(fmt.parse(requestParams.get("joindate").toString()));
                if(requestParams.get("confirmdate")!=null)
                    empprofile.setConfirmdate(fmt.parse(requestParams.get("confirmdate").toString()));
                if(requestParams.get("relievedate")!=null)
                    empprofile.setRelievedate(fmt.parse(requestParams.get("relievedate").toString()));
                if(requestParams.get("trainperiod")!=null)
                    empprofile.setTrainperiod(requestParams.get("trainperiod").toString());
                if(requestParams.get("probperiod")!=null)
                    empprofile.setProbperiod(requestParams.get("probperiod").toString());
                if(requestParams.get("noticeperiod")!=null)
                    empprofile.setNoticeperiod(requestParams.get("noticeperiod").toString());
                if(requestParams.get("emptype")!=null)
                    empprofile.setEmptype(requestParams.get("emptype").toString());
                if(requestParams.get("workmail")!=null)
                    empprofile.setWorkmail(requestParams.get("workmail").toString());
                if(requestParams.get("othermail")!=null)
                    empprofile.setOthermail(requestParams.get("othermail").toString());
                if(requestParams.get("commid")!=null)
                    empprofile.setCommid(requestParams.get("commid").toString());
                if(requestParams.get("branchcode")!=null)
                    empprofile.setBranchcode(requestParams.get("branchcode").toString());
                if(requestParams.get("branchaddr")!=null)
                    empprofile.setBranchaddr(requestParams.get("branchaddr").toString());
                if(requestParams.get("branchcity")!=null)
                    empprofile.setBranchcity(requestParams.get("branchcity").toString());
                if(requestParams.get("branchcountry")!=null)
                    empprofile.setBranchcountry((MasterData)hibernateTemplate.get(MasterData.class,  requestParams.get("branchcountry").toString()));
                if(requestParams.get("updated_by")!=null)
                    empprofile.setUpdated_by(requestParams.get("updated_by").toString());
                if(requestParams.get("status")!=null)
                    empprofile.setStatus(requestParams.get("status").toString());
                if(requestParams.get("keyskills")!=null)
                    empprofile.setKeyskills(requestParams.get("keyskills").toString());
                if(requestParams.get("wkstarttime")!=null)
                    empprofile.setWkstarttime(requestParams.get("wkstarttime").toString());
                if(requestParams.get("wkendtime")!=null)
                    empprofile.setWkendtime(requestParams.get("wkendtime").toString());
                if(requestParams.get("weekoff")!=null)
                    empprofile.setWeekoff(requestParams.get("weekoff").toString());
                if(requestParams.get("updated_on")!=null)
                    empprofile.setUpdated_on(fmt.parse(requestParams.get("updated_on").toString()));
                if(requestParams.get("tercause")!=null)
                    empprofile.setTercause((MasterData)hibernateTemplate.get(MasterData.class,  requestParams.get("tercause").toString()));
                if(requestParams.get("terReason")!=null)
                    empprofile.setTerReason(requestParams.get("terReason").toString());
                if(requestParams.get("terminatedby")!=null)
                    empprofile.setTerminatedby((User)hibernateTemplate.get(User.class,  requestParams.get("terminatedby").toString()));
                if(requestParams.get("termnd")!=null)
                {
                    empprofile.setTermnd(Boolean.parseBoolean(requestParams.get("termnd").toString()));
                    if(!(Boolean)requestParams.get("termnd"))
                    {
                    	empprofile.setConfirmdate(null);
                    	empprofile.setRelievedate(null);
                    }
                }


                hibernateTemplate.save(empprofile);
                list.add(empprofile);
                success = true;
        } catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, "EmpProfile added successfully.", "-1", list, list.size());
        }
    }

     public KwlReturnObject getEmpHistory(HashMap<String,Object> requestParams) {
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
                hql = "from Emphistory where hid=?";
                String hid = requestParams.get("hid").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{hid});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from Emphistory ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
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
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }
     public KwlReturnObject getLastUpdatedHistory(HashMap<String,Object> requestParams) {
        boolean success = false;
        List list = null;
        try {
            String hql = "";
            hql = "select max(updatedon) from Emphistory  where userid.userID=? and category=? ";
            String id = requestParams.get("id").toString();
            String cat = requestParams.get("cat").toString();
            list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id, cat});
            String latestDate = "";
            if (!list.isEmpty()) {
                latestDate = list.get(0)==null?"":list.get(0).toString();
            }
            list.clear();
            list.add(latestDate);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", list, list.size());
        }
    }

     public KwlReturnObject addEmphistory(HashMap<String, Object> requestParams) {
        List <Emphistory> list = new ArrayList<Emphistory>();
        boolean success = true;
        try {
            Emphistory emphist = (Emphistory) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.hrms.ess.Emphistory", "Hid");
            list.add(emphist);

        }catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, "Employee history added successfully.", "-1", list, list.size());
        }
     }

       public KwlReturnObject adduser(HashMap<String, Object> requestParams) {
            List <User> list = new ArrayList<User>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
            boolean success = true;
            try {
                User user = (User) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.common.admin.User", "UserID");
                list.add(user);
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "Employee history added successfully.", "-1", list, list.size());
            }
        }

        public KwlReturnObject addUseraccount(HashMap<String, Object> requestParams) {
            List <Useraccount> list = new ArrayList<Useraccount>();
            boolean success = true;
            try {
                Useraccount ua = (Useraccount) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.common.admin.Useraccount", "UserID");
                list.add(ua);
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "Useraccount added successfully.", "-1", list, list.size());
            }
        }

     public KwlReturnObject getHrmsmodule(Locale localObj) {
        JSONObject jobj = new JSONObject();
        List tabledata = null;
        boolean success = false;
        try {
            String hql = "from hrms_Modules";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql);
            Iterator itr = tabledata.iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                hrms_Modules u = (hrms_Modules) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("moduleid", u.getModuleID());
                obj.put("modulename",u.getModuleName() );
                obj.put("moduledispname",messageSource.getMessage("hrms.hrmsModules."+u.getModuleName(), null, u.getDisplayModuleName() ,localObj));
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            tabledata.clear();
            tabledata.add(jobj);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

     // @@ Change reference everywhere
    public KwlReturnObject getUserDetailsHrms(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        try{
            ArrayList name = new ArrayList();
            ArrayList value = new ArrayList();
            String[] searchCol = null;
            String combo = requestParams.get("combo").toString();
            String hql = "";
            hql = "select distinct ua.userID,emp.userID from Useraccount ua inner join ua.user u " +
                    "left join ua.empProfile emp left join ua.assignmanagers mgr left join " +
                    "ua.assignreviewers rev left join ua.empexperience exp  left join ua.configdata cfd ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<Object>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }
            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if(!StringUtil.isNullOrEmpty(combo)){
                hql =hql +" order by ua.user.firstName asc";
            }else{
                hql =hql +" order by ua.employeeid asc";
            }
            result = StringUtil.getPagingquery(requestParams, null, hibernateTemplate, hql, value);
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            return result;
        }
    }

    public KwlReturnObject checkModule(HashMap<String, Object> requestParams) {
        boolean result = false;
        try {
            String companyid = requestParams.get("companyid").toString();
            String checklink = requestParams.get("checklink").toString();
            CompanyPreferences cmpPref = null;
            String query = "from CompanyPreferences where company.companyID=?";
            List lst = HibernateUtil.executeQuery(hibernateTemplate, query, companyid);
            if (!lst.isEmpty()) {
                cmpPref = (CompanyPreferences) hibernateTemplate.get(CompanyPreferences.class, companyid);
                if (checklink.equalsIgnoreCase("appraisal")) {
                    if (cmpPref.isSelfappraisal()) {
                        result = true;
                    }
                }
                if (checklink.equalsIgnoreCase("competency")) {
                    if (cmpPref.isCompetency()) {
                        result = true;
                    }
                }
                if (checklink.equalsIgnoreCase("goal")) {
                    if (cmpPref.isGoal()) {
                        result = true;
                    }
                }
                if (checklink.equalsIgnoreCase("anonymous")) {
                    if (cmpPref.isAnnmanager()) {
                        result = true;
                    }
                }
                if (checklink.equalsIgnoreCase("approveAppWin")) {
                    if (cmpPref.isApproveappraisal()) {
                        result = true;
                    }
                }
                if (checklink.equalsIgnoreCase("weightage")) {
                    if (cmpPref.isWeightage()) {
                        result = true;
                    }
                }
                if (checklink.equalsIgnoreCase("reviewappraisal")) {
                    if (cmpPref.isReviewappraisal()) {
                        result = true;
                    }
                }
                if (checklink.equalsIgnoreCase("fullupdates")) {
                    if (cmpPref.isFullupdates()) {
                        result = true;
                    }
                }
                if (checklink.equalsIgnoreCase("modaverage")) {
                    if (cmpPref.isModaverage()) {
                        result = true;
                    }
                }
                if (checklink.equalsIgnoreCase("promotion")) {
                    if (cmpPref.isPromotion()) {
                        result=true;
                    }
                }
                if (checklink.equalsIgnoreCase("overallcomments")) {
                    if (cmpPref.isOverallcomments()) {
                        result=true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new KwlReturnObject(result, "", "", null, 0);
    }
public KwlReturnObject getUsers(HashMap<String, Object> requestParams) {
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
            if (requestParams.containsKey("primary") && (Boolean) requestParams.get("primary")) {
                hql = "from User where userID=?";
                String id = requestParams.get("id").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from User ";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null) {
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }
            
            if(requestParams.get("append")!=null){
                hql += requestParams.get("append").toString();
            }
            if (requestParams.get("searchcol") != null && requestParams.get("ss") != null) {
                searchCol = (String[]) requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null) {
                orderby = new ArrayList((List<String>)requestParams.get("order_by"));
                ordertype = new ArrayList((List<String>)requestParams.get("order_type"));
                hql += com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }

    public KwlReturnObject saveUser(HashMap<String, Object> requestParams, Locale localObj) {
        String msg = "";
        boolean success = false;
        String employeeIdFormat = null;
        List <User> list = new ArrayList<User>();
        try {


            UserLogin userLogin;
            User user;
            Useraccount useraccount;
            String id = "";
            String companyid = requestParams.get("companyid").toString();
            if (requestParams.containsKey("id")) {
                id = requestParams.get("id").toString();
            }

            if (StringUtil.isNullOrEmpty(id) == false) {
                user = (User) hibernateTemplate.load(User.class, id);
                useraccount = (Useraccount) hibernateTemplate.load(Useraccount.class, id);
                userLogin = user.getUserLogin();
                if (requestParams.containsKey("templateid")) {
                    useraccount.setTemplateid(requestParams.get("templateid").toString());
                }
                msg = messageSource.getMessage("hrms.common.Userhasbeeneditedsuccessfully", null,"User has been edited successfully.", localObj);//"User has been edited successfully.";
            } else {
                String uuid = UUID.randomUUID().toString();
                user = new User();
                userLogin = new UserLogin();
                useraccount = new Useraccount();
                userLogin.setUserID(uuid);
                useraccount.setUserID(uuid);
                user.setUserLogin(userLogin);
                userLogin.setUser(user);

                userLogin.setUserName(requestParams.get("username").toString());
                userLogin.setPassword(requestParams.get("pwd").toString());
                user.setCompany((Company) hibernateTemplate.load(Company.class, requestParams.get("companyid").toString()));
                msg = "User has been saved successfully.";
            }
            if(requestParams.get("employeeIdFormat")!=null && !StringUtil.isNullOrEmpty(requestParams.get("employeeIdFormat").toString()))
            	employeeIdFormat = requestParams.get("employeeIdFormat").toString();
            user.setFirstName(requestParams.get("fname").toString());
            user.setLastName(requestParams.get("lname").toString());
            user.setEmailID(requestParams.get("emailid").toString());
            user.setAddress(requestParams.get("address").toString());
            user.setContactNumber(requestParams.get("contactno").toString());
            useraccount.setEmployeeid((Integer) requestParams.get("empid"));
            useraccount.setEmployeeIdFormat(employeeIdFormat);
            if (requestParams.containsKey("roleid")) {
                useraccount.setRole((Role) hibernateTemplate.load(Role.class, requestParams.get("roleid").toString()));
            }
            if (requestParams.containsKey("designationid")) {
                user.setDesignation("none");
                useraccount.setDesignationid((MasterData) hibernateTemplate.load(MasterData.class, requestParams.get("designationid").toString()));
            }
            if (requestParams.containsKey("department")) {
                useraccount.setDepartment((MasterData) hibernateTemplate.load(MasterData.class, requestParams.get("department").toString()));
            }
            if (requestParams.containsKey("salary")) {
                useraccount.setSalary(requestParams.get("salary").toString());
            }
            if (requestParams.containsKey("accno")) {
                useraccount.setAccno(requestParams.get("accno").toString());
            }
            if (requestParams.containsKey("formatid")) {
                user.setDateFormat((KWLDateFormat) hibernateTemplate.load(KWLDateFormat.class, requestParams.get("formatid").toString()));
            }
            if (requestParams.containsKey("tzid")) {
                KWLTimeZone timeZone = (KWLTimeZone) hibernateTemplate.load(KWLTimeZone.class, requestParams.get("tzid").toString());
                user.setTimeZone(timeZone);
            }
            if (requestParams.containsKey("aboutuser")) {
                user.setAboutUser(requestParams.get("aboutuser").toString());
            }
            if (requestParams.containsKey("userimage")) {
                hibernateTemplate.saveOrUpdate(user);
                String fileName = user.getUserID() +".png";
                user.setImage(ProfileImageServlet.ImgBasePath + fileName);
//                new FileUploadHandler().uploadImage((FileItem) requestParams.get("userimage"),
//                        fileName,
//                        storageHandlerImplObj.GetProfileImgStorePath(), 100, 100, false, false);
                
            }

            hibernateTemplate.saveOrUpdate(userLogin);
            hibernateTemplate.saveOrUpdate(user);
            list.add(user);
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
        return new KwlReturnObject(success, msg, "-1", list, list.size());
    }

    public boolean addEmpExperience(Empexperience empexp) {
        boolean success = true;
        try {
            hibernateTemplate.saveOrUpdate(empexp);
        } catch (Exception e) {
        	success = false;
        	e.printStackTrace();
        } finally {
            return success;
        }
    }

    public KwlReturnObject getPayHistory(HashMap<String, Object> requestParams) {
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
                hql = "from Payhistory where hid=?";
                String hid = requestParams.get("hid").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{hid});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from Payhistory ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<Object>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
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
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }

    public int insertConfigData(HttpServletRequest request, String formtype, String referenceid, String companyid) {
        int successflag = 0;
        KwlReturnObject result;

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            requestParams.put("filter_names", Arrays.asList("formtype","company.companyID"));
            requestParams.put("filter_values", Arrays.asList(formtype,companyid));
            result = customcolDAOObj.getConfigType(requestParams);
            List lst = result.getEntityList();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                ConfigType contyp = (ConfigType) ite.next();
                requestParams.clear();
                requestParams.put("filter_names", Arrays.asList("referenceid"));
                requestParams.put("filter_values", Arrays.asList(referenceid));
                result = customcolDAOObj.getConfigData(requestParams);
                List lst1= result.getEntityList();
                Iterator ite1 = lst1.iterator();
                ConfigData condata = null;
                requestParams.clear();
                if (ite1.hasNext()) {
                    condata = (ConfigData) ite1.next();
                    requestParams.put("Id", condata.getId());
                    requestParams.put("Col"+contyp.getColnum(), request.getParameter(contyp.getName()));

                } else {
                    requestParams.put("Referenceid",referenceid);
                    requestParams.put("Col"+contyp.getColnum(), request.getParameter(contyp.getName()));
                }
                result = customcolDAOObj.addConfigData(requestParams);
           }
        } catch (Exception e) {
            return 0;
        }
        return successflag;
    }
    @Override
    public KwlReturnObject saveSearch(String searchName,int searchFlag, String searchState, String userid) {
        List <UserSearchState> list = new ArrayList<UserSearchState>();
        boolean success = true;
        try {
            UserSearchState userSearchState = new UserSearchState();
            userSearchState.setModifiedon(new Date());
            userSearchState.setSearchFlag(searchFlag);
            userSearchState.setSearchState(searchState);
            userSearchState.setUser((User) hibernateTemplate.get(User.class, (Serializable) userid));
            userSearchState.setSearchName(searchName);
            userSearchState.setDeleteflag(false);
            hibernateTemplate.save(userSearchState);
            list.add(userSearchState);
        }catch (Exception e) {
            success = false;
            e.printStackTrace();
        }finally{
            return new KwlReturnObject(success, "User search saved successfully.", "-1", list, list.size());
        }
     }
    @Override
    public int insertConfigData(HttpServletRequest request, String formtype, String referenceid, String companyid,HashMap<String, Object> requestParams_extra) {
        int successflag = 0;
        KwlReturnObject result;

        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            Useraccount Useraccountobj = (Useraccount) requestParams_extra.get("Useraccount");
            requestParams.put("filter_names", Arrays.asList("formtype","company.companyID"));
            requestParams.put("filter_values", Arrays.asList(formtype,companyid));
            result = customcolDAOObj.getConfigType(requestParams);
            List lst = result.getEntityList();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                ConfigType contyp = (ConfigType) ite.next();
                requestParams.clear();
                requestParams.put("filter_names", Arrays.asList("referenceid"));
                requestParams.put("filter_values", Arrays.asList(referenceid));
                result = customcolDAOObj.getConfigData(requestParams);
                List lst1= result.getEntityList();
                Iterator ite1 = lst1.iterator();
                ConfigData condata = null;
                requestParams.clear();
                if (ite1.hasNext()) {
                    condata = (ConfigData) ite1.next();
                    requestParams.put("Id", condata.getId());
                    requestParams.put("Col"+contyp.getColnum(), request.getParameter(contyp.getName()));

                } else {
                    requestParams.put("Referenceid",referenceid);
                    requestParams.put("Col"+contyp.getColnum(), request.getParameter(contyp.getName()));
                }
                result = customcolDAOObj.addConfigData(requestParams);
                if(result.getRecordTotalCount() > 0)
                Useraccountobj.setConfigdata((ConfigData) result.getEntityList().get(0));
           }
        } catch (Exception e) {
            return 0;
        }
        return successflag;
    }
    @Override
    public List<UserSearchState> getSavedSearchesForUser(String userid) {
        boolean success = true;
        List<UserSearchState> tabledata = null;
        try{
            String hql = "from UserSearchState where user.userID=? and deleteflag=? order by searchName ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userid, false});
            success = true;
        }catch(Exception e){
            success = false;
            e.printStackTrace();
        }finally{
           return tabledata;
        }
    }

    @Override
    public boolean checkForSearchName(String searchName, String userid) {
        boolean duplicate = false;
        List tabledata = null;
        try{
            String hql = "from UserSearchState where user.userID=? and searchName=? and deleteflag=? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userid, searchName, false});
            if(tabledata.size()>0){
                duplicate = true;
            }
            
        }catch(Exception e){
            e.printStackTrace();
            duplicate = true;
        }finally{
           return duplicate;
        }
    }

    @Override
    public boolean deleteSavedSearch(String searchid) {
        boolean success = false;
        try{
                UserSearchState userSaveSearch = (UserSearchState) hibernateTemplate.load(UserSearchState.class, searchid);

                userSaveSearch.setDeleteflag(true);

                hibernateTemplate.save(userSaveSearch);

                success = true;

        }catch(Exception e){
            e.printStackTrace();
            success = false;
        }finally{
           return success;
        }
    }
    @Override
    public KwlReturnObject getSavedSearch(String searchid, int searchFlag) {
        boolean success = true;
        List tabledata = null;
        try{
            String hql = "from UserSearchState where id = ?  and searchFlag=? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{searchid, searchFlag});
            success = true;
        }catch(Exception e){
            success = false;
            e.printStackTrace();
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

	@Override
	public boolean editEmpProfileForTerOfFutureDates(){
		boolean success = false;
		try{
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Date today = calendar.getTime();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date tmr = calendar.getTime();
			String hql = "update Empprofile set termnd = ? where relievedate >= ? and relievedate < ?";
			int a = HibernateUtil.executeUpdate(hibernateTemplate, hql, new Object[]{true, today, tmr});
			success = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			return success;
		}
	}
	
	@Override
	public boolean editUserForTerOfFutureDates(){
		boolean success = false;
		try{
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Date today = calendar.getTime();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date tmr = calendar.getTime();
			String hql = "update User set deleteflag = ? where userID IN (select userID from Empprofile where relievedate >= ? and relievedate < ?)";
			int a = HibernateUtil.executeUpdate(hibernateTemplate, hql, new Object[]{1, today, tmr});
			success = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			return success;
		}
	}
	
	@Override
	public boolean editEmpProfileForRehOfFutureDates(){
		boolean success = false;
		try{
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Date today = calendar.getTime();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date tmr = calendar.getTime();
			String hql = "update Empprofile set termnd = ?, confirmdate = ?, relievedate = ? where joindate >= ? and joindate < ?";
			int a = HibernateUtil.executeUpdate(hibernateTemplate, hql, new Object[]{false, null, null, today, tmr});
			success = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			return success;
		}
	}
	
	@Override
	public boolean editUserForRehOfFutureDates(){
		boolean success = false;
		try{
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Date today = calendar.getTime();
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			Date tmr = calendar.getTime();
			String hql = "update User set deleteflag = ? where userID IN (select userID from Empprofile where joindate >= ? and joindate < ?)";
			int a = HibernateUtil.executeUpdate(hibernateTemplate, hql, new Object[]{0, today, tmr});
			success = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			return success;
		}
	}
	
	@Override
	public Useraccount getUseraccountByUserId(String userId){
		Useraccount useraccount = null;
		try{
            useraccount = (Useraccount) hibernateTemplate.get(Useraccount.class, userId);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
           return useraccount;
        }
	}
        
    @Override
    public  String getSysEmailIdByCompanyID(String companyid){
    	String emailId = "admin@deskera.com";
    	try {
    		Company company = (Company)hibernateTemplate.get(Company.class, companyid);
    		if(company!=null){
    			emailId = company.getEmailID();
    			if(StringUtil.isNullOrEmpty(emailId)){
    				emailId = company.getCreator().getEmailID();                    
                }
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
        }
    	finally{    
    		return emailId;
        }    
    }
    
    @Override
    public boolean isCompanySuperAdmin(String userid, String companyid){
    	boolean isSuperAdmin = false;
    	try{
    		Company company = (Company)hibernateTemplate.get(Company.class, companyid);
    		if(company!=null && company.getCreator()!=null && userid!=null){
    			isSuperAdmin = userid.equals(company.getCreator().getUserID());
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		return isSuperAdmin;
    	}
    }
    
    @Override
    public String getUserDateFormat(String userid){
    	String dateFormat = "MM-dd-yyyy";
    	try{
    		User user = (User) hibernateTemplate.get(User.class, userid);
    		if(user!=null && user.getDateFormat()!=null){
    			dateFormat = user.getDateFormat().getJavaForm();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally{
    		return dateFormat;
    	}
    }
}
