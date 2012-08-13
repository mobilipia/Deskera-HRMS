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
package com.krawler.spring.hrms.rec.job;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.Docmap;
import com.krawler.common.admin.Docs;
import com.krawler.common.admin.Role;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.update.Updates;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.common.docs.HrmsDocmap;
import com.krawler.hrms.common.docs.HrmsDocs;
import com.krawler.hrms.ess.Emphistory;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.recruitment.Allapplications;
import com.krawler.hrms.recruitment.ConfigRecruitment;
import com.krawler.hrms.recruitment.ConfigRecruitmentData;
import com.krawler.hrms.recruitment.ConfigRecruitmentMaster;
import com.krawler.hrms.recruitment.Jobapplicant;
import com.krawler.hrms.recruitment.Jobprofile;
import com.krawler.hrms.recruitment.Positionmain;
import com.krawler.hrms.recruitment.Recruiter;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.hrms.template.db.HtmlTemplate;
import com.krawler.spring.hrms.template.db.LetterHistory;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.fileupload.FileUploadException;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.krawler.utils.json.base.JSONObject;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;


/**
 *
 * @author shs
 */
public class hrmsRecJobDAOImpl implements hrmsRecJobDAO,MessageSourceAware {

    private HibernateTemplate hibernateTemplate;
    private MessageSource messageSource;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public KwlReturnObject getPositionmain(HashMap<String,Object> requestParams) {
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
                hql = "from Positionmain where positionid=?";
                String rid = requestParams.get("positionid").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{rid});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from Positionmain ";
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
            ex.printStackTrace();
        } finally {
            return result;
        }
    }

    public KwlReturnObject getJobProfile(ArrayList name, ArrayList value){
        boolean success = false;
        List lst = null;
        try {
//            String positionid = requestParams.get("positionid").toString();
//            Integer type = Integer.valueOf(requestParams.get("type").toString());
//            String hql = "from Jobprofile where position.positionid=? and type=?";
            String hql = "from Jobprofile ";
            hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, value.toArray());
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", lst, lst.size());
        }
    }


    public KwlReturnObject getPositionstatus(ArrayList name, ArrayList value) {
        List tabledata = null;
        boolean success = false;
        try {
            //String hql = "from Allapplications where employee.userID=? and position.positionid=?";
            String hql = "from Allapplications ";
            hql +=StringUtil.filterQuery(name, "where");
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, value.toArray());
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }
    

    @Override
    public KwlReturnObject cancelAllapplications(ArrayList name, ArrayList value){
        boolean success = false;
        List tabledata = null;
        try {
            String hql = "from Allapplications ";
            hql +=StringUtil.filterQuery(name, "where");
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, value.toArray());
            if (!tabledata.isEmpty()) {
                    Allapplications allapp = (Allapplications) tabledata.get(0);
                    allapp.setDelflag(1);
                    hibernateTemplate.update(allapp);
            }

            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "Application(s) Deleted successfully.", "-1", tabledata, tabledata.size());
        }
    }
     public KwlReturnObject getPositionstatus(HashMap<String,Object> requestParams){
        boolean success = false;
        List lst = null,result1 = null,result2 = null;
        KwlReturnObject result = null;
        int totalcount = 0;
        try {
            ArrayList name = null;
            ArrayList value = null;
            String[] searchCol = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String hql = "",filterString = "",searchString = "",orderString = "";
            if(requestParams.containsKey("primary")&&(Boolean)requestParams.get("primary")){
                hql = "from Allapplications where id=?";
                String id = requestParams.get("id").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }

            hql = "from Allapplications ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = (ArrayList)requestParams.get("filter_names");
                value = (ArrayList)requestParams.get("filter_values");
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }
            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null) {
                searchCol = (String[])requestParams.get("searchcol");
                searchString = StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }
            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = (ArrayList)requestParams.get("order_by");
                ordertype = (ArrayList)requestParams.get("order_type");
                orderString =com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            if(requestParams.get("searchcol1") == null){
                result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql + searchString + orderString, value);
            } else{
                result1 = HibernateUtil.executeQuery(hibernateTemplate, hql + searchString + orderString, value.toArray());
                totalcount = result1.size();
                hql = "from Allapplications ";
                if(requestParams.get("filter_names1")!=null&&requestParams.get("filter_values1")!=null){
                	name = (ArrayList)requestParams.get("filter_names1");
                    value = (ArrayList)requestParams.get("filter_values1");
                    hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
                }
                if(name.contains("status"))
                    value.set(5, 4); //configjobapplicant=4 reset to 0
                else
                    value.set(4, 4); //configjobapplicant=4 reset to 0
                if(requestParams.get("searchcol1")!=null && requestParams.get("ss")!=null) {
                    searchCol = (String[])requestParams.get("searchcol1");
                    searchString = StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
                }
                result2 = HibernateUtil.executeQuery(hibernateTemplate, hql + searchString + orderString, value.toArray());
                totalcount += result2.size();
                result1.addAll(result2);
                String allflag = requestParams.get("allflag").toString();
                if(allflag.equals("false")) {
                    try {
                        int st = Integer.parseInt(requestParams.get("start").toString());
                        int ed = Math.min(result1.size(),st+Integer.parseInt(requestParams.get("limit").toString()));
                        lst = result1.subList(st, ed);
                        result1 = lst;
                    } catch (NumberFormatException ne) {
                        throw ServiceException.FAILURE("CompanyHandler.getGoodsReceipts", ne);
                    }
                }
                result = new KwlReturnObject(success, "success", "", result1, totalcount);
            }
            success = true;
        } catch (Exception ex) {
            success = false;
            ex.printStackTrace();
        } finally {
            return result;
        }
    }
     public KwlReturnObject getRecruiterPositionstatus(HashMap<String,Object> requestParams){
        boolean success = false;
        List lst = null,result1 = null,result2 = null;
        KwlReturnObject result = null;
        int totalcount = 0;
        try {
            ArrayList name = null;
            ArrayList value = null;
            String[] searchCol = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String hql = "",filterString = "",searchString = "",orderString = "";
            if(requestParams.containsKey("primary")&&(Boolean)requestParams.get("primary")){
                hql = "from Recruiter where rid=?";
                String id = requestParams.get("id").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }

            hql = "from Recruiter ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = (ArrayList)requestParams.get("filter_names");
                value = (ArrayList)requestParams.get("filter_values");
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }
            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null) {
                searchCol = (String[])requestParams.get("searchcol");
                searchString = StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }
            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = (ArrayList)requestParams.get("order_by");
                ordertype = (ArrayList)requestParams.get("order_type");
                orderString =com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            String groupstring = " group by allapplication.id";
            if(requestParams.get("searchcol1") == null){
                result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql + searchString + orderString , value);
            } else{
                result1 = HibernateUtil.executeQuery(hibernateTemplate, hql + searchString + orderString, value.toArray());
                totalcount = result1.size();
                value = (ArrayList)requestParams.get("filter_values1");
                name = (ArrayList)requestParams.get("filter_names1");
                if(name.contains("status"))
                    value.set(4, 4); //configjobapplicant=4 reset to 0
                else
                    value.set(3, 4); //configjobapplicant=4 reset to 0
                if(requestParams.get("searchcol1")!=null && requestParams.get("ss")!=null) {
                    searchCol = (String[])requestParams.get("searchcol1");
                    searchString = StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
                }
                result2 = HibernateUtil.executeQuery(hibernateTemplate, hql + searchString + orderString, value.toArray());
                totalcount += result2.size();
                result1.addAll(result2);
                String allflag = requestParams.get("allflag").toString();
                if(allflag.equals("false")) {
                    try {
                        int st = Integer.parseInt(requestParams.get("start").toString());
                        int ed = Math.min(result1.size(),st+Integer.parseInt(requestParams.get("limit").toString()));
                        lst = result1.subList(st, ed);
                        result1 = lst;
                    } catch (NumberFormatException ne) {
                        throw ServiceException.FAILURE("hrmsrecjobdaoimpl.getRecruiterPositionstatus", ne);
                    }
                }
                result = new KwlReturnObject(success, "success", "", result1, totalcount);
            }
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }
    public KwlReturnObject getMaxCountJobid(HashMap<String,Object> requestParams) {
        boolean success = false;
        List list = null;
        try {
            String companyid = requestParams.get("companyid").toString();
            String jobque = "select max(jobidwthformat) from Positionmain where company.companyID=?";
            list = HibernateUtil.executeQuery(hibernateTemplate, jobque, new Object[]{companyid});
            Integer maxcount = 0;
            if (!list.isEmpty()) {
                maxcount = list.get(0)==null?1:(Integer)list.get(0)+ 1;
            }
            list.clear();
            list.add(maxcount);
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
        return new KwlReturnObject(success, "success", "-1", list, list.size());
    }

    @Override
    public KwlReturnObject setPositionmain(HashMap<String, Object> requestParams) {
        List <Positionmain> list = new ArrayList<Positionmain>();
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        boolean success = false;
        try {
                Positionmain posmain = new Positionmain();
                if(requestParams.get("startdate")!=null)
                    posmain.setStartdate((Date) fmt.parse(requestParams.get("startdate").toString()));
                if(requestParams.get("enddate")!=null)
                    posmain.setEnddate((Date) fmt.parse(requestParams.get("enddate").toString()));
                if(requestParams.get("position")!=null)
                    posmain.setPosition((MasterData) requestParams.get("position"));
                if(requestParams.get("details")!=null)
                    posmain.setDetails(requestParams.get("details").toString());
                if(requestParams.get("jobtype")!=null)
                    posmain.setJobtype(requestParams.get("jobtype").toString());
                if(requestParams.get("jobidwthformat")!=null)
                    posmain.setJobidwthformat(Integer.parseInt(requestParams.get("jobidwthformat").toString()));
                if(requestParams.get("jobid")!=null)
                    posmain.setJobid(requestParams.get("jobid").toString());
                if(requestParams.get("delflag")!=null)
                    posmain.setDelflag(Integer.parseInt(requestParams.get("delflag").toString()));
                if(requestParams.get("company")!=null)
                    posmain.setCompany((Company)requestParams.get("company"));
                if(requestParams.get("manager")!=null)
                    posmain.setManager((User) requestParams.get("manager"));
                if(requestParams.get("departmentid")!=null)
                    posmain.setDepartmentid((MasterData)requestParams.get("departmentid"));
                if(requestParams.get("noofpos")!=null)
                    posmain.setNoofpos(Integer.parseInt(requestParams.get("noofpos").toString()));
                if(requestParams.get("createdby")!=null)
                    posmain.setCreatedBy((User) requestParams.get("createdby"));
                if(requestParams.get("positionsfilled")!=null)
                    posmain.setPositionsfilled(Integer.parseInt(requestParams.get("positionsfilled").toString()));
                hibernateTemplate.save(posmain);
                list.add(posmain);
                success = true;

        } catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, "Positionmain added successfully.", "-1", list, list.size());
        }
    }

    public KwlReturnObject updatePositionmain(HashMap<String, Object> requestParams) {
        List <Positionmain> list = new ArrayList<Positionmain>();
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        Positionmain posmain = null;
        boolean success = false;
        try {
                posmain = (Positionmain) hibernateTemplate.get(Positionmain.class, requestParams.get("positionid").toString());
                if(requestParams.get("startdate")!=null)
                    posmain.setStartdate((Date) fmt.parse(requestParams.get("startdate").toString()));
                if(requestParams.get("enddate")!=null)
                    posmain.setEnddate((Date) fmt.parse(requestParams.get("enddate").toString()));
                if(requestParams.get("details")!=null)
                    posmain.setDetails(requestParams.get("details").toString());
//                if(requestParams.get("jobtype")!=null)
//                    posmain.setJobtype(requestParams.get("jobtype").toString());
//                if(requestParams.get("jobidwthformat")!=null)
//                    posmain.setJobidwthformat(Integer.parseInt(requestParams.get("jobidwthformat").toString()));
//                if(requestParams.get("jobid")!=null)
//                    posmain.setJobid(requestParams.get("jobid").toString());
                if(requestParams.get("delflag")!=null)
                    posmain.setDelflag(Integer.parseInt(requestParams.get("delflag").toString()));
//                if(requestParams.get("company")!=null)
//                    posmain.setCompany((Company)requestParams.get("company"));
                if(requestParams.get("manager")!=null)
                    posmain.setManager((User) requestParams.get("manager"));
                if(requestParams.get("departmentid")!=null)
                    posmain.setDepartmentid((MasterData)requestParams.get("departmentid"));
                if(requestParams.get("noofpos")!=null)
                    posmain.setNoofpos(Integer.parseInt(requestParams.get("noofpos").toString()));
                if (requestParams.get("jobshift")!=null) {
                    posmain.setJobshift(requestParams.get("jobshift").toString());
                }
//                if(requestParams.get("createdby")!=null)
//                    posmain.setCreatedBy((User) requestParams.get("createdby"));
                if(requestParams.get("positionsfilled")!=null)
                    posmain.setPositionsfilled(Integer.parseInt(requestParams.get("positionsfilled").toString()));
                if (requestParams.get("travel")!=null) {
                    posmain.setTravel(requestParams.get("travel").toString());
                }
                if (requestParams.get("location")!=null) {
                    posmain.setLocation(requestParams.get("location").toString());
                }
                if(requestParams.get("relocation")!=null)
                    posmain.setRelocation(requestParams.get("relocation").toString());
                if(requestParams.get("experiencemonth")!=null)
                    posmain.setExperiencemonth(Integer.parseInt(requestParams.get("experiencemonth").toString()));
                if(requestParams.get("experienceyear")!=null)
                    posmain.setExperienceyear(Integer.parseInt(requestParams.get("experienceyear").toString()));
                hibernateTemplate.update(posmain);
                list.add(posmain);
                success = true;

        } catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, "Positionmain added successfully.", "-1", list, list.size());
        }
    }

    public KwlReturnObject addJobProfile(HashMap<String, Object> requestParams) {
        List <Jobprofile> list = new ArrayList<Jobprofile>();
        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
        boolean success = false;
        try {
                Jobprofile job = new Jobprofile();
                if(requestParams.get("id")!=null){
                    job.setId(requestParams.get("id").toString());
                }
                if(requestParams.get("responsibility")!=null)
                    job.setResponsibility(requestParams.get("responsibility").toString());
                if(requestParams.get("position")!=null)
                    job.setPosition((Positionmain) requestParams.get("position"));
                if(requestParams.get("type")!=null)
                    job.setType(Integer.parseInt(requestParams.get("type").toString()));
                if(requestParams.get("skill")!=null)
                    job.setSkill(requestParams.get("skill").toString());
                if(requestParams.get("skilldesc")!=null)
                    job.setSkilldesc(requestParams.get("skilldesc").toString());
                if(requestParams.get("qualification")!=null)
                    job.setQualification(requestParams.get("qualification").toString());
                if(requestParams.get("qualificationdesc")!=null)
                    job.setQualificationdesc(requestParams.get("qualificationdesc").toString());
                hibernateTemplate.saveOrUpdate(job);
                list.add(job);
                success = true;

        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }finally{
            return new KwlReturnObject(success, "JobProfile added successfully.", "-1", list, list.size());
        }
    }

    public KwlReturnObject getRecruiter(HashMap<String, Object> requestParams) {
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
                hql = "from Recruiter where rid=?";
                String rid = requestParams.get("rid").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{rid});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from Recruiter ";
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

    // @@ to be removed.. Use function in hrmscommon
//    public KwlReturnObject getAssignmanager(HashMap<String, Object> requestParams) {
//        List tabledata = null;
//        boolean success = false;
//        try {
//            String userid = requestParams.get("userid").toString();
//            Integer managerstatus = Integer.valueOf(requestParams.get("managerstatus").toString());
//            String hql = "from  Assignmanager where assignemp.userID=? and managerstatus=?";
//            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userid,managerstatus});
//            success = true;
//        } catch (Exception ex) {
//            success = false;
//        } finally {
//            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
//        }
//    }

    public KwlReturnObject addAllapplications(HashMap<String, Object> requestParams) {
        List <Allapplications> list = new ArrayList<Allapplications>();
        boolean success = false;
        try {
                Allapplications allapp = null;
                if(requestParams.get("id")!=null)
                    allapp = (Allapplications) hibernateTemplate.get(Allapplications.class, requestParams.get("id").toString());
                else{
                    allapp = new Allapplications();
                    String id = UUID.randomUUID().toString();
                    allapp.setId(id);
                }
                if(requestParams.get("position")!=null)
                    allapp.setPosition((Positionmain)hibernateTemplate.get(Positionmain.class, requestParams.get("position").toString()));
                if(requestParams.get("jobapplicant")!=null)
                    allapp.setConfigjobapplicant((ConfigRecruitmentData) requestParams.get("jobapplicant"));
                if(requestParams.get("employee")!=null)
                    allapp.setEmployee((User) requestParams.get("employee"));
                if(requestParams.get("applydate")!=null)
                    allapp.setApplydate((Date)requestParams.get("applydate"));
                if(requestParams.get("joiningdate")!=null)
                    allapp.setJoiningdate((Date)requestParams.get("joiningdate"));
                if(requestParams.get("interviewtime")!=null)
                    allapp.setInterviewtime(requestParams.get("interviewtime").toString());
                if(requestParams.get("status")!=null)
                    allapp.setStatus(requestParams.get("status").toString());
                if(requestParams.get("interviewdate")!=null)
                    allapp.setInterviewdate((Date)requestParams.get("interviewdate"));
                if(requestParams.get("rank")!=null)
                    allapp.setRank((MasterData)hibernateTemplate.get(MasterData.class, requestParams.get("rank").toString()));
                if(requestParams.get("callback")!=null)
                    allapp.setCallback((MasterData)hibernateTemplate.get(MasterData.class, requestParams.get("callback").toString()));
                if(requestParams.get("recruiter")!=null)
                    allapp.setRecruiter(requestParams.get("recruiter").toString());
                if(requestParams.get("interviewplace")!=null)
                    allapp.setInterviewplace(requestParams.get("interviewplace").toString());
                if(requestParams.get("interviewcomment")!=null)
                    allapp.setInterviewcomment(requestParams.get("interviewcomment").toString());
                if(requestParams.get("company")!=null)
                    allapp.setCompany((Company) requestParams.get("company"));
                if(requestParams.get("delflag")!=null)
                    allapp.setDelflag(Integer.valueOf(requestParams.get("delflag").toString()));
                if(requestParams.get("applicationflag")!=null)
                    allapp.setApplicationflag(Integer.valueOf(requestParams.get("applicationflag").toString()));
                if(requestParams.get("rejectedbefore")!=null)
                    allapp.setRejectedbefore(Integer.valueOf(requestParams.get("rejectedbefore").toString()));
                if(requestParams.get("statuscomment")!=null)
                    allapp.setStatuscomment(requestParams.get("statuscomment").toString());
                if(requestParams.get("employeetype")!=null)
                    allapp.setEmployeetype(Integer.valueOf(requestParams.get("employeetype").toString()));
                if(requestParams.get("contactperson")!=null)
                    allapp.setContactperson((User)hibernateTemplate.get(User.class, requestParams.get("contactperson").toString()));
                list.add(allapp);
                hibernateTemplate.save(allapp);
                
                success = true;

        } catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, "Allapplications added successfully.", "-1", list, list.size());
        }
    }

    public KwlReturnObject deleteAllapplications(HashMap<String,Object> requestParams){
        boolean success = false;
        List tabledata = null;
        KwlReturnObject result = null;
        try {
            String id = requestParams.get("id").toString();
            String hql = "from Allapplications where id=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, id);
            if (!tabledata.isEmpty()) {
                    Allapplications allapp = (Allapplications) tabledata.get(0);
                    allapp.setDelflag(1);
                    allapp.setStatus(Allapplications.Rejected);
                    hibernateTemplate.update(allapp);
            }
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "Application(s) Deleted successfully.", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getJobApplicant(HashMap<String,Object> requestParams){
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
                hql = "from Jobapplicant where applicantid=?";
                String rid = requestParams.get("applicantid").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{rid});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from Jobapplicant ";
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
    public KwlReturnObject getRecruiters(HashMap<String,Object> requestParams){
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
                hql = "from Recruiter where rid=?";
                String rid = requestParams.get("rid").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{rid});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from Recruiter ";
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
    public KwlReturnObject getConfigJobApplicant(HashMap<String,Object> requestParams){
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
                hql = "from ConfigRecruitmentData where id=?";
                String rid = requestParams.get("applicantid").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{rid});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from ConfigRecruitmentData ";
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
            ex.printStackTrace();
        } finally {
            return result;
        }
    }
    public KwlReturnObject deleteConfigJobapplicant(HashMap<String,Object> requestParams){
        boolean success = false;
        KwlReturnObject result = null;
        try {
            String id = requestParams.get("id").toString();
            ConfigRecruitmentData exps = (ConfigRecruitmentData) hibernateTemplate.load(ConfigRecruitmentData.class, id);
            exps.setDeleted(true);
            hibernateTemplate.saveOrUpdate(exps);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "Application(s) Deleted successfully.", "-1", null, 0);
        }
    }
    public KwlReturnObject deleteJobapplicant(HashMap<String,Object> requestParams){
        boolean success = false;
        KwlReturnObject result = null;
        try {
            String id = requestParams.get("id").toString();
            Jobapplicant exps = (Jobapplicant) hibernateTemplate.load(Jobapplicant.class, id);
                exps.setUsername(exps.getUsername()+"_del");
                exps.setDeleted(true);
                hibernateTemplate.saveOrUpdate(exps);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "Application(s) Deleted successfully.", "-1", null, 0);
        }
    }

    public KwlReturnObject getRecruiterupdate(HashMap<String,Object> requestParams){
        Boolean success = false;
        int diff;
        List <Updates> list = new ArrayList<Updates>();
        try{
		        	Locale locale = null;
		            if(requestParams.get("locale")!=null){
		            	locale = (Locale) requestParams.get("locale");
		            }
                    String userid = requestParams.get("userid").toString();
                    String query = "select delflag from Recruiter where recruit.userID=? and delflag=0 and allapplication is null";
                    List recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{userid});
                    Iterator itr = recordTotalCount.iterator();
                    if (itr.hasNext()) {
                        String updateDiv = "";
                        JSONObject obj = new JSONObject();
                        updateDiv += "<p>"+messageSource.getMessage("hrms.common.Alert.getRecruiterupdate",null,"You are selected on a interview panel.<br/>Please,click on following links for confirmation as a interviewer.", locale)+"</p>"
                                +"<p><a href='#' onclick='interviewerPosition(1)'>"+messageSource.getMessage("hrms.common.Accept",null,"Accept", locale)+"</a>&nbsp;&nbsp;<a href='#' onclick='interviewerPosition(0)'>"+messageSource.getMessage("hrms.common.Reject",null,"Reject", locale)+"</a></p>";
                        list.add(new Updates(StringUtil.getContentSpan(updateDiv), null));
                    }
        }catch(Exception ex){
            ex.printStackTrace();
            success = false;
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
    }
    
    public KwlReturnObject getAppliedJobUpdate(HashMap<String,Object> requestParams){
    	Boolean success = false;
        Calendar calendar = Calendar.getInstance();
    	Date startDate;
        List <Updates> list = new ArrayList<Updates>();
        try{
		        	Locale locale = null;
		            if(requestParams.get("locale")!=null){
		            	locale = (Locale) requestParams.get("locale");
		            }
        			DateFormat df = (DateFormat) requestParams.get("df");
        			Date userdate = (Date)(requestParams.get("userdate"));
                    String userid = requestParams.get("userid").toString();
                    String companyid = requestParams.get("companyid").toString();
                    calendar.setTime(userdate);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.add(Calendar.DAY_OF_MONTH, -7);//Back to 7 day's from current date
                	startDate = calendar.getTime();
                    String query;
                    List<Allapplications> recordTotalCount;
                    if(requestParams.get("isAdmin").toString().equals("true")){
                    	query = "from Allapplications  where company.companyID=?  and delflag=0 and employee is not null and applydate is not null and applydate between ? and ?";
                    	recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{companyid, startDate ,userdate});
                    }
                    else{
                    	query = "from Allapplications where company.companyID=? and position.manager.userID=? and delflag=0 and employee is not null and applydate is not null and applydate between ? and ?";
                    	recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{companyid, userid, startDate ,userdate});
                    }
                    Iterator<Allapplications> itr = recordTotalCount.iterator();
                    while(itr.hasNext()) {
                    	Allapplications allapplications = itr.next();
                        String updateDiv = "";
                        String applicant=allapplications.getEmployee().getFirstName()+" "+allapplications.getEmployee().getLastName();
                        updateDiv += messageSource.getMessage("hrms.common.Updates.getAppliedJobUpdate",new Object[]{"<font color='green'>"+applicant+"</font>","<a href='#' onclick='allapps(\"Internal\")'>"+allapplications.getPosition().getJobid()+" </a>","<font color='green'>"+df.format(allapplications.getApplydate())+"</font>"},"<font color='green'>"+applicant+"</font> applied for job <a href='#' onclick='allapps(\"Internal\")'>"+allapplications.getPosition().getJobid()+" </a> on <font color='green'>"+df.format(allapplications.getApplydate())+"</font>.", locale);
                        list.add(new Updates(StringUtil.getContentSpan(updateDiv), allapplications.getApplydate()));
                    }
        }catch(Exception ex){
            ex.printStackTrace();
            success = false;
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
    }

    public KwlReturnObject getRecruitersList(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        KwlReturnObject result = null;
        int count = 0;
        try {
            String ss = "", searchstring = "";
            if (requestParams.get("ss") != null) {
                ss = requestParams.get("ss").toString();
            }
            ArrayList params = new ArrayList();
            params.add((Integer) requestParams.get("deleteflag"));
            params.add(requestParams.get("companyid").toString());
            if (requestParams.get("delflag") != null) {
                if(requestParams.get("delflag").toString().equals("3")){
                    searchstring = " and u.userID not in (select r.recruit.userID from Recruiter r where delflag in(0,1,2)) ";
                } else {
                    searchstring = " and delflag = ? ";
                    params.add(requestParams.get("delflag").toString());
                }
            }
            String[] searchCol = new String[]{"u.firstName", "u.lastName"};

            String hql = "select u,r from Recruiter r right join r.recruit u where u.deleteflag=? and u.company.companyID=? "+searchstring ;
            if (!StringUtil.isNullOrEmpty(ss)) {
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, params);
            }
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, params);

            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }

    public KwlReturnObject setRecruiters(HashMap<String, Object> requestParams) {
        List<Recruiter> list = new ArrayList<Recruiter>();
        boolean success = false;
        try {
            Recruiter recruiter = (Recruiter) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.hrms.recruitment.Recruiter", "Rid");
            list.add(recruiter);
            success = true;
        } catch (Exception e) {
            success = false;
            System.out.println("Error is " + e);
        } finally {
            return new KwlReturnObject(success, "", "-1", list, list.size());
        }
    }
    public boolean deleteRecruiters(HashMap<String, Object> requestParams) {
        boolean success = true;

        try{
            String applicationid =(String) requestParams.get("Allapplication");
            String hql = "delete from Recruiter where allapplication.id=?";
            int executeUpdate = HibernateUtil.executeUpdate(hibernateTemplate, hql, new Object[]{applicationid});
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return success;
        }
    }
    public KwlReturnObject setJobApplicant(HashMap<String, Object> requestParams) {
        List<Jobapplicant> list = new ArrayList<Jobapplicant>();
        boolean success = false;
        try {
            Jobapplicant jobapp = (Jobapplicant) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.hrms.recruitment.Jobapplicant", "Applicantid");
            list.add(jobapp);
            success = true;
        } catch (Exception e) {
            success = false;
            System.out.println("Error is " + e);
        } finally {
            return new KwlReturnObject(success, "", "-1", list, list.size());
        }
    }
    
    public KwlReturnObject getConfigRecruitment(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "from ConfigRecruitment ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
                 int ind = hql.indexOf("(");
                if(ind>-1){
                    int index = Integer.valueOf(hql.substring(ind+1,ind+2));
                    hql = hql.replace("("+index+")", "("+value.get(index).toString()+")");
                    value.remove(index);
                }
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
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);

        } catch (Exception ex) {
            result.setSuccessFlag(false);
            ex.printStackTrace();

        } finally {
            return result;
        }
    }
    @Override
    public boolean updateConfigRecruitment(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        try {
            String sql = "";
            sql = "update ConfigRecruitment set position=position-1 where position > ? and formtype=? ";
            HibernateUtil.executeUpdate(hibernateTemplate,sql,new Object[]{requestParams.get("position"),requestParams.get("formtype")} );

        } catch (Exception ex) {
            result.setSuccessFlag(false);
            ex.printStackTrace();
            return false;
        } finally {
            return true;
        }
    }
    @Override
    public void updateConfigRecruitmentDatatoDefault(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        try {
            String sql = "";
            sql = "update ConfigRecruitmentData set "+ requestParams.get("column")  +" = ? where company.companyID=? ";
            HibernateUtil.executeUpdate(hibernateTemplate,sql,new Object[]{null,requestParams.get("company")} );

        } catch (Exception ex) {
            result.setSuccessFlag(false);
            ex.printStackTrace();
        }
    }

    @Override
       public KwlReturnObject addConfigRecruitmentType(HashMap<String, Object> requestParams) {
            List <ConfigRecruitment> list = new ArrayList<ConfigRecruitment>();
            boolean success = false;
            try {
                ConfigRecruitment user = (ConfigRecruitment) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.hrms.recruitment.ConfigRecruitment", "Configid");
                list.add(user);
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "Configtype added successfully.", "-1", list, list.size());
            }
   }
    
    @Override
   public KwlReturnObject deleteConfigRecruitment(String configid) {
            boolean success = false;
            try {
                ConfigRecruitment configtype = (ConfigRecruitment) hibernateTemplate.get(ConfigRecruitment.class, configid);
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("position", configtype.getPosition());
                requestParams.put("formtype", configtype.getFormtype());
                boolean successupdate=updateConfigRecruitment(requestParams);
                if(successupdate){
                    hibernateTemplate.delete(configtype);
                    success = true;
                }else{
                    success = false;
                }

                
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "ConfigType deleted successfully.", "-1", null,0);
            }
   }
   public KwlReturnObject getConfigRecruitmentData(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "from ConfigRecruitmentData ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
                int ind = hql.indexOf("(");
                if(ind>-1){
                    int index = Integer.valueOf(hql.substring(ind+1,ind+2));
                    hql.replace("("+index+")", value.get(index).toString());
                    value.remove(index);
                }
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
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);

        } catch (Exception ex) {
            result.setSuccessFlag(false);
            ex.printStackTrace();

        } finally {
            return result;
        }
    }


    @Override
   public KwlReturnObject getConfigMaster(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "from ConfigRecruitmentMaster ";
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
                 int ind = hql.indexOf("(");
                if(ind>-1){
                    int index = Integer.valueOf(hql.substring(ind+1,ind+2));
                    hql = hql.replace("("+index+")", "("+value.get(index).toString()+")");
                    value.remove(index);
                }
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
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);

        } catch (Exception ex) {
            result.setSuccessFlag(false);
            ex.printStackTrace();

        } finally {
            return result;
        }
    }

    @Override
       public KwlReturnObject addConfigMaster(HashMap<String, Object> requestParams) {
            List <ConfigRecruitmentMaster> list = new ArrayList<ConfigRecruitmentMaster>();
            boolean success = false;
            try {
                ConfigRecruitmentMaster user = (ConfigRecruitmentMaster) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.hrms.recruitment.ConfigRecruitmentMaster", "Masterid");
                list.add(user);
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "Configmaster added successfully.", "-1", list, list.size());
            }
   }
    @Override
       public KwlReturnObject addHrmsDocmap(HashMap<String, Object> requestParams) {
            List <HrmsDocmap> list = new ArrayList<HrmsDocmap>();
            boolean success = false;
            try {
                HrmsDocmap HrmsDocmapobj = (HrmsDocmap) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.hrms.common.docs.HrmsDocmap", "Id");
                list.add(HrmsDocmapobj);
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "Configmaster added successfully.", "-1", list, list.size());
            }
   }

    @Override
       public KwlReturnObject addConfigRecruitmentData(HashMap<String, Object> requestParams) {
            List <ConfigRecruitmentData> list = new ArrayList<ConfigRecruitmentData>();
            boolean success = false;
            try {
                ConfigRecruitmentData user = (ConfigRecruitmentData) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.hrms.recruitment.ConfigRecruitmentData", "Id");
                list.add(user);
                success=true;
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "Configmaster added successfully.", "-1", list, list.size());
            }
   }

    @Override
    public KwlReturnObject deleteConfigMaster(String masterid) {
            boolean success = false;
            try {
                ConfigRecruitmentMaster ConfigRecruitmentMaster = (ConfigRecruitmentMaster) hibernateTemplate.get(ConfigRecruitmentMaster.class, masterid);
                hibernateTemplate.delete(ConfigRecruitmentMaster);
                success = true;
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "ConfigType deleted successfully.", "-1", null,0);
            }
   }
    
     @Override
    public boolean saveConfigRecruitment(ConfigRecruitment contyp) {
            boolean success = false;
            try {
                hibernateTemplate.save(contyp);
                success = true;
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return success;
            }
   }
   @Override
    public boolean updownConfigRecruitment(String configid,int positioninc) {
            boolean success = false;
            try {
                ConfigRecruitment ConfigRecruitment = (ConfigRecruitment) hibernateTemplate.get(ConfigRecruitment.class, configid);
                ConfigRecruitment.setPosition(ConfigRecruitment.getPosition() + positioninc);
                hibernateTemplate.save(ConfigRecruitment);
                success = true;
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return success;
            }

   }
    @Override
   public HrmsDocs uploadFile(FileItem fi,ConfigRecruitmentData ConfigRecruitmentDataobj,HashMap arrparam, boolean flag)  {
        HrmsDocs docObj = new HrmsDocs();
        User userObj = null;
        Jobapplicant jobapp = null;
        try {
            String fileName = new String(fi.getName().getBytes(), "UTF8");
            String Ext = "";
            if (fileName.contains(".")) {
                Ext = fileName.substring(fileName.lastIndexOf("."));
            }
            docObj.setDocname(fileName);
            docObj.setReferenceid(ConfigRecruitmentDataobj.getId());
            docObj.setStorename("");
            docObj.setDoctype("");
            docObj.setUploadedon(new Date());
            docObj.setStorageindex(1);
            docObj.setDocsize(fi.getSize() + "");
            docObj.setDeleted(false);
            if (StringUtil.isNullOrEmpty((String) arrparam.get("docname")) == false) {
                docObj.setDispdocname((String) arrparam.get("docname"));
            }
            if (StringUtil.isNullOrEmpty((String) arrparam.get("docdesc")) == false) {
                docObj.setDocdesc((String) arrparam.get("docdesc"));
            }
            hibernateTemplate.save(docObj);
            String fileid = docObj.getDocid();
            if (Ext.length() > 0) {
                fileid = fileid + Ext;
            }
            docObj.setStorename(fileid);
            hibernateTemplate.update(docObj);
            String temp = StorageHandler.GetDocStorePath1();
            uploadFile(fi, temp, fileid);

        } catch (Exception e) {
                System.out.println("Error is "+e);
        }
        return docObj;
    }
    public static void uploadFile(FileItem fi, String destinationDirectory, String fileName) throws ServiceException {
        try {
            File destDir = new File(destinationDirectory);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            File uploadFile = new File(destinationDirectory + "/" + fileName);
            fi.write(uploadFile);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("moduleBuilderMethods.uploadFile", ex);
        }

    }

    @Override
    public void parseRequest(HttpServletRequest request, HashMap<String, Object> arrParam, ArrayList<FileItem> fi, boolean fileUpload,HashMap<Integer,String> filemap)  {
        DiskFileUpload fu = new DiskFileUpload();
        FileItem fi1 = null;
        List fileItems = null;
        int i=0;
        try {
        	fu.setHeaderEncoding("UTF-8");
            fileItems = fu.parseRequest(request);
            
        } catch (FileUploadException e) {
//            throw ServiceException.FAILURE("Admin.createUser", e);
        }
        
        for (Iterator k = fileItems.iterator(); k.hasNext();) {
            fi1 = (FileItem) k.next();
            if (fi1.isFormField()) {
                try {
					arrParam.put(fi1.getFieldName(), new String(fi1.getString().getBytes ("iso-8859-1"), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            } else {
                try {
                    String fileName = new String(fi1.getName().getBytes(), "UTF8");
                    if (fi1.getSize() != 0) {
                        fi.add(fi1);
                        filemap.put(i,fi1.getFieldName());
                        i++;
                        fileUpload = true;
                    }
                } catch (UnsupportedEncodingException ex) {
                }
            }
        }
    }
    @Override
    public KwlReturnObject transferappdata(HashMap<String, Object> requestParams){
        String pass="1234";
        JSONObject jobj = new JSONObject();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        String hql = "";
        List tabledata = null;
        Integer codeid2 = null;
        User user = null;
        Useraccount ua;
        Empprofile ep;
        ArrayList<User> arrayListUser = new ArrayList<User>();
        boolean success = false;
        boolean flag1=false;
        boolean flag2=false;
        String msg ="Applicant data trasfered successfully.";
        try {
            if (requestParams.get("employeerectype").toString().equalsIgnoreCase("0")) {
                if (StringUtil.isNullOrEmpty(requestParams.get("employeeid").toString()) == false) {
                    String[] codeid = (requestParams.get("employeeid").toString()).split("-");
                    for (int x = 0; x < codeid.length; x++) {
                        if (codeid[x].matches("[0-9]*") == true) {
                            codeid2 = Integer.parseInt(codeid[x]);
                        }
                    }
                }
                String hql1 = "from Useraccount where employeeid=? and user.company.companyID=?";
                if (HibernateUtil.executeQuery(hibernateTemplate, hql1, new Object[]{codeid2,requestParams.get("companyid")}).isEmpty() == true) {
                    flag1=true;
                }
                String hql2 = "from UserLogin where userName=? and user.company.companyID=?";
                if (HibernateUtil.executeQuery(hibernateTemplate, hql2, new Object[]{requestParams.get("appusername").toString(), requestParams.get("companyid")}).isEmpty() == true) {
                    flag2=true;
                }
                //tabledata = HibernateUtil.executeQuery(session, hql, requestParams.get("appusername"));
                if (flag1&&flag2) {
                    hql = "from ConfigRecruitmentData where id=?";
                    tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, requestParams.get("applicantid").toString());
                    Iterator ite = tabledata.iterator();
                    while (ite.hasNext()) {
                        ConfigRecruitmentData jobapp = (ConfigRecruitmentData) ite.next();
                        user = new User();
                        ua = new Useraccount();
                        ep = new Empprofile();
                        String userid = UUID.randomUUID().toString();
                        UserLogin userLogin = new UserLogin();
                        userLogin.setUserID(userid);
                        userLogin.setUserName(requestParams.get("appusername").toString());
                        userLogin.setPassword(AuthHandler.getSHA1(pass));
                      //  ep.setUserID(userid);
                        if(!StringUtil.isNullOrEmpty(requestParams.get("dob").toString()))
                            ep.setDoB(fmt.parse(requestParams.get("dob").toString()));
                        if(!StringUtil.isNullOrEmpty(requestParams.get("middlename").toString()))
                            ep.setMiddlename(requestParams.get("middlename").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("gender").toString()))
                            ep.setGender(requestParams.get("gender").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("bloodgroup").toString()))
                            ep.setBloodgrp(requestParams.get("bloodgroup").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("fathername").toString()))
                            ep.setFathername(requestParams.get("fathername").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("fatherdob").toString()))
                            ep.setFatherDoB(fmt.parse(requestParams.get("fatherdob").toString()));
                        if(!StringUtil.isNullOrEmpty(requestParams.get("mothername").toString()))
                            ep.setMothername(requestParams.get("mothername").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("motherdob").toString()))
                            ep.setMotherDoB(fmt.parse(requestParams.get("motherdob").toString()));
                        if(!StringUtil.isNullOrEmpty(requestParams.get("keyskill").toString()))
                            ep.setKeyskills(requestParams.get("keyskill").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("panno").toString()))
                            ep.setPanno(requestParams.get("panno").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("epfno").toString()))
                            ep.setPfno(requestParams.get("epfno").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("drivingno").toString()))
                            ep.setDrvlicense(requestParams.get("drivingno").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("passportno").toString()))
                            ep.setPassportno(requestParams.get("passportno").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("exdateofpassport").toString()))
                            ep.setExppassport(fmt.parse(requestParams.get("exdateofpassport").toString()));
                        if(!StringUtil.isNullOrEmpty(requestParams.get("mobileno").toString()))
                            ep.setMobno(requestParams.get("mobileno").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("landlineno").toString()))
                            ep.setLandno(requestParams.get("landlineno").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("otheremail").toString()))
                            ep.setOthermail(requestParams.get("otheremail").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("permanentaddress").toString()))
                            ep.setPermaddr(requestParams.get("permanentaddress").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("presentaddress").toString()))
                            ep.setPresentaddr(requestParams.get("presentaddress").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("presentcity").toString()))
                            ep.setPresentcity(requestParams.get("presentcity").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("presentstate").toString()))
                            ep.setPresentstate(requestParams.get("presentstate").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("presentcountry").toString())){
                            hql = "from MasterData where value=? ";
                            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{requestParams.get("presentcountry").toString()});
                            Iterator ite1 = tabledata.iterator();
                            while (ite1.hasNext()) {
                                MasterData md = (MasterData) ite1.next();
                                ep.setPresentcountry(md);
                            }
                        }
////                            ep.setPresentcountry((MasterData)hibernateTemplate.get(MasterData.class, requestParams.get("presentcountry").toString()));
                        if(!StringUtil.isNullOrEmpty(requestParams.get("Permanentcity").toString()))
                            ep.setPermcity(requestParams.get("Permanentcity").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("Permanentstate").toString()))
                            ep.setPermstate(requestParams.get("Permanentstate").toString());
                        if(!StringUtil.isNullOrEmpty(requestParams.get("Permanentcountry").toString())){
                            hql = "from MasterData where value=? ";
                            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{requestParams.get("Permanentcountry").toString()});
                            Iterator ite1 = tabledata.iterator();
                            while (ite1.hasNext()) {
                                MasterData md = (MasterData) ite1.next();
                                ep.setPermcountry(md);
                            }
                        }
//                            ep.setPermcountry((MasterData)hibernateTemplate.get(MasterData.class, requestParams.get("Permanentcountry").toString()));
                        ep.setUserLogin(userLogin);
                        ep.setJoindate(fmt.parse(requestParams.get("empjoindate").toString()));
                        ep.setConfirmdate(fmt.parse(requestParams.get("empjoindate").toString()));
                        ep.setUserLogin(userLogin);
                        user.setFirstName(jobapp.getCol1());
                        user.setLastName(jobapp.getCol2());
                        user.setEmailID(jobapp.getCol3());
                        user.setContactNumber(jobapp.getCol4());
                        ua.setUserID(userid);
                        ua.setUser(user);
                        ua.setEmployeeid(codeid2);
                        ua.setSalary("0");
                        user.setCompany((Company) hibernateTemplate.get(Company.class,requestParams.get("companyid").toString()));
                        ua.setDesignationid((MasterData) hibernateTemplate.get(MasterData.class, requestParams.get("designationid").toString()));
                        ua.setDepartment((MasterData) hibernateTemplate.get(MasterData.class, requestParams.get("departmentid").toString()));
                        ua.setRole((Role) hibernateTemplate.get(Role.class, "3"));
                        User cre = user.getCompany().getCreator();
                        user.setTimeZone(cre.getTimeZone());
                        user.setDateFormat(cre.getDateFormat());
                        user.setUserLogin(userLogin);
                        jobapp.setDeleted(true);
                        deleteuserappFunction(0, false, requestParams.get("applicantid").toString());
                        hibernateTemplate.save(userLogin);
                        hibernateTemplate.save(user);
                        hibernateTemplate.save(ua);
                        hibernateTemplate.save(ep);
                        hibernateTemplate.update(jobapp);
                        success=true;
                    }
                } else {
                    success = false;
                    msg = "Username is already exists.";
                }
            } else{
                user=(User) hibernateTemplate.get(User.class, requestParams.get("applicantid").toString());
                ua = (Useraccount) hibernateTemplate.get(Useraccount.class, user.getUserID().toString());
                if(user!=null){
                    deleteuserappFunction(1, false, requestParams.get("applicantid").toString());
                    int histsave = 0;
                    Date saveDate = new Date();
                    SimpleDateFormat fmt1 = new SimpleDateFormat("yyyy/MM/dd");
                    saveDate = new Date(fmt1.format(saveDate));
                    Emphistory ehst = new Emphistory();
                    User updatedby = (User) hibernateTemplate.get(User.class, requestParams.get("userid").toString());
                    if ((MasterData) hibernateTemplate.get(MasterData.class, requestParams.get("designationid").toString()) != ua.getDesignationid()) {
                        ehst.setDesignation(ua.getDesignationid());
                        histsave = 1;
                    }
                    ua.setDesignationid((MasterData) hibernateTemplate.get(MasterData.class, requestParams.get("designationid").toString()));
                    if ((MasterData) hibernateTemplate.get(MasterData.class, requestParams.get("departmentid").toString()) != ua.getDepartment()) {
                        ehst.setDepartment(ua.getDepartment());
                        if (histsave == 0) {
                            ehst.setDesignation(ua.getDesignationid());
                        }
                        histsave = 2;
                    }
                    ua.setDepartment((MasterData) hibernateTemplate.get(MasterData.class, requestParams.get("departmentid").toString()));
                    if (histsave == 1) {
                        ehst.setDepartment(ua.getDepartment());
                    }
                    if (histsave == 1 || histsave == 2) {
                        ehst.setUserid(user);
                        ehst.setEmpid(ua.getEmployeeid());
                        ehst.setUpdatedon(saveDate);
                        ehst.setUpdatedby(updatedby);
                        ehst.setCategory(Emphistory.Emp_Desg_change);
                        hibernateTemplate.save(ehst);
                    }
                    hibernateTemplate.update(user);
                    success=true;
                }
            }
            arrayListUser.add(user);
        }catch (Exception e) {
                success = false;
                msg = "Error occured while trasfered Applicant data.";
                System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, msg, "-1", arrayListUser, arrayListUser.size());
        }
    }
    public KwlReturnObject deleteuserappFunction(int employeetype, boolean flag,String applicantid){
        String hql="";
        List tabledata=null;
        boolean success = false;
        try {
            if (employeetype == 0) {
                if (flag) {
                    hql = "from Allapplications where configjobapplicant.id=? and id!=?";
                    tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{applicantid, applicantid});
                } else {
                    hql = "from Allapplications where configjobapplicant.id=?";
                    tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{applicantid});
                }
            } else {
                if (flag) {
                    hql = "from Allapplications where employee.userID=? and id!=?";
                    tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{applicantid, applicantid});
                } else{
                    hql = "from Allapplications where employee.userID=?";
                    tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{applicantid});
                }
            }
            Iterator ite = tabledata.iterator();
            while (ite.hasNext()) {
                Allapplications allapp = (Allapplications) ite.next();
                allapp.setDelflag(1);
                hibernateTemplate.update(allapp);
            }
        }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, "All Applicant data trasfered successfully.", "-1", null,0);
        }
    }

     public KwlReturnObject saveLetterHistory(HashMap<String, Object> requestParams){
        LetterHistory lh = null;
        try {
	        	Locale locale = null;
	            if(requestParams.get("locale")!=null){
	            	locale = (Locale) requestParams.get("locale");
	            }
         //   if (requestParams.containsKey("tid")) {
         //       tl = (TargetList) hibernateTemplate.get(TargetList.class, requestParams.get("tid").toString());
         //   } else {
                lh = new LetterHistory();
          //  }
                lh.setEmailId((String)requestParams.get("emailid"));
  //              lh.setSentDate((String)requestParams.get("fname"));
                HtmlTemplate ht = (HtmlTemplate)hibernateTemplate.get(HtmlTemplate.class,(String)requestParams.get("templateid"));
                lh.setTemplate(ht);//(HtmlTemplate)requestParams.get("template"));
                lh.setTemplateName(ht.getName());//String)requestParams.get("templatename"));
                //lh.setUser((User)requestParams.get("user"));
                lh.setApplicantId((String)requestParams.get("appid"));
                lh.setSaveDate((Date)requestParams.get("savedate"));
//                lh.setSent(sent);
                hibernateTemplate.save(lh);
            return new KwlReturnObject(true, "{\"valid\":\"true\",\"success\":\"true\",data:{msg:\""+messageSource.getMessage("hrms.recruitment.LetterHistorysavedsuccessfully",null,"Letter History saved successfully.", locale)+"\",value:\"success\",action:\"" + requestParams.get("Action") + "ed\"}}", "", null, 0);
        } catch (Exception se) {
            se.printStackTrace();
    //        throw ServiceException.FAILURE("hrmsInvestmentDAOImpl.setInvestmentStructure : " + se.getMessage(), se);
        }
        return new KwlReturnObject(false, "{\"valid\":\"true\",data:{value:\"failed\"}}", "", null, 0);
    }


    public KwlReturnObject getHtmlTemplate(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try {
            String hql = "from HtmlTemplate where id = ? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql,(String)requestParams.get("templateid"));
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getUser(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try {
            String hql = "from User where userID = ? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql,(String)requestParams.get("userid"));
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }


    public String getPlaceHolderLookupString(HashMap<String, Object> requestParams) {
        List tabledata = null;
        String orderedLookupStr = "";
        try {
            Object[] row = null;
            String src_field = null;
            String dest_table = (String)requestParams.get("dest_table");
            String hql = "select src_table,src_field from PlaceHolderLookup where dest_table = ? ";
      //      orderedLookupStr += dest_table;
            do{
                tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql,dest_table);
                if(tabledata==null || tabledata.isEmpty())break;
                row = (Object[])tabledata.get(0);
                dest_table = (String)row[0];
                src_field = (String)row[1];
//                orderedLookupStr+="."+src_field+","+dest_table;
                orderedLookupStr=/*dest_table+"."+*/src_field+"."+orderedLookupStr;
            }while(true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return orderedLookupStr;
        }
    }

    public KwlReturnObject getPlaceHolderData(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try {
            String hql = "from PlaceHolder where type = ? AND placeholder = ? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql,new Object[]{requestParams.get("phs1"),requestParams.get("phs2")});
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getPlaceHolderUserValue(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        List temp = null;
        try {
            String hql = "";
            String companyid = (String)requestParams.get("companyid");
            if(requestParams.get("getph").equals("cmail")){
                hql = "select emailID from Company  where companyID=?";
                tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{companyid});
            } else if(requestParams.get("getph").equals("cname")){
                hql = "select companyName from Company  where companyID=?";
                tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{companyid});
            } else if(requestParams.get("getph").equals("currentyear")){
                hql = "select date_format(date(now()),'%Y') as currentyear from Company  where companyID=?";
                tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{companyid});
            } else {
                if(requestParams.get("gettable").equals("Applicant"))
                    hql = "select "+(String)requestParams.get("getph")+" from ConfigRecruitmentData where id = ? ";
                else if(requestParams.get("gettable").equals("User"))
                    hql = "select "+(String)requestParams.get("getph")+" from User where userid = ? ";
                else {
                    hql = "select "+(String)requestParams.get("getph")+" from User where userid = ? ";
                    temp = HibernateUtil.executeQuery(hibernateTemplate, hql,new Object[]{requestParams.get("applicant_id")});
                    if(temp.size()<1){
                        hql = "select "+(String)requestParams.get("getph")+" from ConfigRecruitmentData where id = ? ";
                    }
                }
                tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql,new Object[]{requestParams.get("applicant_id")});
            }
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public String fetchField(HashMap<String,String> requestParams, int index) {
        boolean success = false;
        String result = "";
        List list = null;
        try {
            String id = requestParams.get("id").toString();
            String jobque = "select col"+index+" from ConfigRecruitmentData where id=?";
            list = HibernateUtil.executeQuery(hibernateTemplate, jobque, new Object[]{id});
            if (!list.isEmpty()) {
                result = list.get(0)==null?"":list.get(0).toString();
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
        return result;
    }
    
    public KwlReturnObject transferExternalAppDocs(ArrayList<HrmsDocmap> hrmsDocmaps, User relatedTo){
    	boolean success = true;
    	ArrayList<Docs> arrayListDocs = new ArrayList<Docs>();
    	Iterator<HrmsDocmap> itr = hrmsDocmaps.iterator();
    	try{
    		while(itr.hasNext()){
        		Docs docs = new Docs();
        		HrmsDocs hrmsDocs = itr.next().getDocid();
        		
        		docs.setDocname(hrmsDocs.getDocname());
        		docs.setDocsize(hrmsDocs.getDocsize());
        		docs.setDoctype(hrmsDocs.getDoctype());
        		docs.setDocdesc(hrmsDocs.getDocdesc());
        		docs.setUploadedon(hrmsDocs.getUploadedon());
        		docs.setStorename(hrmsDocs.getStorename());
        		docs.setStorageindex(hrmsDocs.getStorageindex());
        		docs.setUserid(relatedTo);
        		docs.setCompany(relatedTo.getCompany());
        		docs.setDeleteflag(0);
        		arrayListDocs.add(docs);
        	}
        	hibernateTemplate.saveOrUpdateAll(arrayListDocs);
    	}catch(Exception e){
    		success = false;
    		e.printStackTrace();
    	}finally{
    		return new KwlReturnObject(success, "", "", arrayListDocs, arrayListDocs.size());
    	}	
    }
    
    public KwlReturnObject transferExternalAppDocMaps(ArrayList<HrmsDocmap> hrmsDocmaps, ArrayList<Docs> docs, User relatedTo){
    	boolean success = true;
    	ArrayList<Docmap> arrayListDocmap = new ArrayList<Docmap>();
    	Iterator<HrmsDocmap> itr1 = hrmsDocmaps.iterator();
    	Iterator<Docs> itr2 = docs.iterator();
    	try{
    		while(itr1.hasNext()){
        		Docmap docmaps = new Docmap();
        		HrmsDocmap hrmsDocmap = itr1.next();
        		
        		docmaps.setDocid(itr2.next());	
        		docmaps.setRecid(relatedTo.getUserID());
        		arrayListDocmap.add(docmaps);
        	}
        	hibernateTemplate.saveOrUpdateAll(arrayListDocmap);
    	}catch(Exception e){
    		success = false;
    		e.printStackTrace();
    	}finally{
    		return new KwlReturnObject(success, "", "", arrayListDocmap, arrayListDocmap.size());
    	}	
    }
     @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
