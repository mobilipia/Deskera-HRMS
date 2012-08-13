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
package com.krawler.spring.hrms.appraisalcycle;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.update.Updates;
import com.krawler.common.util.BuildCriteria;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
//import com.krawler.hrms.performance.Finalgoalmanagement;
import com.krawler.hrms.performance.Appraisalcycle;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.AppraisalmanagementQuestionAnswers;
import com.krawler.hrms.performance.Assignreviewer;
import com.krawler.hrms.performance.QuestionGroup;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.hrms.anonymousappraisal.hrmsAnonymousAppraisalConstants;
import com.krawler.utils.json.base.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

public class hrmsAppraisalcycleDAOImpl implements hrmsAppraisalcycleDAO,MessageSourceAware {

    private HibernateTemplate hibernateTemplate;
     private MessageSource messageSource;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    /**
     * Returns a resultset after executing a query on the Appraisalcycle
     * table. 
     * <p>"Where" clause parameters passed as "filter_names" and "filter_values"
     * arraylists in the hashmap.
     *
     * @param  requestParams  contains one or more of the following keys
     *                <ul><code>filter_names</code> - an ArrayList of column names to be used in
     *                              where clause
     *                <br/><code>filter_values</code> - an ArrayList of values to be used while executing
     *                              queries
     *                <br/><code>searchcol</code> - an array of strings containing column names on
     *                              which search operation is to be performed
     *                <br/><code>ss</code> - the search string
     *                <br/><code>order_by</code> - an ArrayList of column names to be used in
     *                              order by clause
     *                <br/><code>order_type</code> - an ArrayList of order by types
     *                </ul>
     *
     * @return      KwlReturnObject
     */
    public KwlReturnObject getAppraisalCycle(HashMap<String,Object> requestParams) {
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
                hql = "from Appraisalcycle where id=? order by cyclename,submitenddate";
                String id = requestParams.get("id").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from Appraisalcycle ac ";
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
                orderby = (ArrayList)requestParams.get("order_by");
                ordertype = (ArrayList)requestParams.get("order_type");
                hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            hql+=" order by cyclename,submitenddate";
            result = StringUtil.getPagingquery(requestParams, searchCol, hibernateTemplate, hql, value);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }

    /**
     * Returns a resultset after executing a query on the Appraisalmanagement
     * table.
     * <p>"Where" clause parameters passed as "filter_names" and "filter_values"
     * arraylists in the hashmap.
     *
     * @param  requestParams  contains one or more of the following keys
     *                <ul><code>distinct</code> - a String containing the distinct column name which
     *                              is to be fetched
     *                <br/><code>filter_names</code> - an ArrayList of column names to be used in
     *                              where clause
     *                <br/><code>filter_values</code> - an ArrayList of values to be used while executing
     *                              queries
     *                <br/><code>append</code> - a String to be appended at the end of the where clause.
     *                              At least one value is to be sent in filter_values and filter_names
     *                              before string can be appended
     *                <br/><code>searchcol</code> - an array of strings containing column names on
     *                              which search operation is to be performed
     *                <br/><code>ss</code> - the search string
     *                <br/><code>order_by</code> - an ArrayList of column names to be used in
     *                              order by clause
     *                <br/><code>order_type</code> - an ArrayList of order by types
     *                <br/><code>select</code> - an ArrayList of column names to be fetched
     *                </ul>
     *
     * @return      KwlReturnObject
     */
    public KwlReturnObject getAppraisalmanagement(HashMap<String,Object> requestParams) {
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
            if(requestParams.containsKey("primary")&&(Boolean)requestParams.get("primary")){
                hql = "from Appraisalmanagement where appraisalid=?";
                String id = requestParams.get("id").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            if(requestParams.get("distinct")!=null) {
                hql = "select distinct " + requestParams.get("distinct") + " from Appraisalmanagement ";
            }
            else {
                hql = "from Appraisalmanagement";
            }
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
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
                orderby = new ArrayList((List<String>) requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>) requestParams.get("order_type"));
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
            success = false;
        } finally {
            return result;
        }
    }

    /**
     * Adds or edits an Appraisalmanagement object. If the primary key id is passed then the object
     * is loaded else a new object is created.
     * <p>Returns a KwlReturnObject with the object added into the entity list
     *
     * @param  requestParams  contains the columns names (ie the Setter Method name excluding 'set') and
     *                        the corresponding values in the appropriate format

     *
     * @return      KwlReturnObject
     */
    public KwlReturnObject addAppraisalmanagement(HashMap<String, Object> requestParams) {
            List <Appraisalmanagement> list = new ArrayList<Appraisalmanagement>();
            boolean success = false;
            try {
                Appraisalmanagement appmanagement = (Appraisalmanagement) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.hrms.performance.Appraisalmanagement", "Appraisalid");
                list.add(appmanagement);
                success = true;
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "Appraisal Management added successfully.", "-1", list, list.size());
            }
        }


    /**
     * Checks if Appraisal Cycles with start date or end date between the specified dates exist.
     * <p>Returns a KwlReturnObject with the resultset added into the entity list
     *
     * @param  requestParams  contains the following keys
     *              <ul><code>companyid</code> - a String specifying the company id
     *              <br/><code>date1</code> - a Date specifying the start of the date range 
     *              <br/><code>date2</code> - a Date specifying the end of the date range 
     *              </ul>   
     *
     * @return      KwlReturnObject
     */
    // @@ to be generalised
    public KwlReturnObject getAppraisalcycle1(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String companyid = requestParams.get("companyid").toString();
            Date date1 = (Date)requestParams.get("date1");
            Date date2 = (Date)requestParams.get("date2");
            String hql = "from Appraisalcycle where company.companyID=? and ((? between startdate and enddate) or (? between startdate and enddate))";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{companyid,date1,date2});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    /**
     * Checks if Appraisal Cycles with start date or end date between the specified dates exist that are
     * not the Appraisal Cycle with the passed id. (Used mainly while editing the appraisal cycle)
     * <p>Returns a KwlReturnObject with the resultset added into the entity list
     *
     * @param  requestParams  contains the following keys
     *              <ul><code>companyid</code> - a String specifying the company id
     *              <br/><code>id</code> - a String specifying the id of the appraisal cycle
     *              <br/><code>date1</code> - a Date specifying the start of the date range 
     *              <br/><code>date2</code> - a Date specifying the end of the date range 
     *              </ul>   
     *
     * @return      KwlReturnObject
     */
    // @@ to be generalised
    public KwlReturnObject getAppraisalcycle2(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String companyid = requestParams.get("companyid").toString();
            String id = requestParams.get("id").toString();
            Date date1 = (Date)requestParams.get("date1");
            Date date2 = (Date)requestParams.get("date2");
            String hql="from Appraisalcycle where company.companyID=? and id != ? and ((? between startdate and enddate) or (? between startdate and enddate)) ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{companyid,id,date1,date2});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    /**
     * Returns a resultset of Appraisal Cycles with specified date between start date and end date or
     * the specified date is greater than end date.
     * <p>Returns a KwlReturnObject with the resultset added into the entity list
     *
     * @param  requestParams  contains the following keys
     *              <ul><code>companyid</code> - a String specifying the company id
     *              <br/><code>date</code> - a Date 
     *              </ul>   
     *
     * @return      KwlReturnObject
     */
    public KwlReturnObject getAppraisalcycleform(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String companyid = requestParams.get("companyid").toString();
            Date date = (Date)requestParams.get("date");
            String hql = "from Appraisalcycle where company.companyID=? and ((startdate <= ? and enddate >= ?) " +
                        " or enddate < ?) order by enddate desc ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{companyid,date,date,date});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    /**
     * Returns a resultset of initiated Appraisal Cycles for a specific employee.
     * <p>Returns a KwlReturnObject with the resultset added into the entity list
     *
     * @param  requestParams  contains the following keys
     *              <ul><code>companyid</code> - a String specifying the company id
     *              <br/><code>userid</code> - a String specifying the userid
     *              </ul>
     *
     * @return      KwlReturnObject
     */

    public KwlReturnObject getAppraisalcycleemployee(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String companyid = requestParams.get("companyid").toString();
            String userid = requestParams.get("userid").toString();
            String hql = "select apm,appcyle from Appraisalmanagement apm right outer join apm.appcycle appcyle where apm.employee.userID=? "+
                            "and appcyle.company.companyID=? group by apm.appcycle.id";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userid,companyid});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    /**
     * Returns a resultset of Appraisals from Appraisalmanagement table for a particular appraiser
     * for a particular cycle.
     * <p>Returns a KwlReturnObject with the resultset added into the entity list
     *
     * @param  requestParams  contains the following keys
     *              <ul><code>users</code> - a String of userids of users that come under the appraiser
     *              <br/><code>managerid</code> - a String specifying the id of the manager
     *              <br/><code>appcycle</code> - a String specifying the id of the appraisal cycle
     *              <br/><code>date</code> - a Date which lies between the appraisal cycle submission dates
     *              </ul>
     *
     * @return      KwlReturnObject
     */
    public KwlReturnObject getAppraisallistbyManager(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String users = requestParams.get("users").toString();
            String managerid = requestParams.get("managerid").toString();
            String appcycle = requestParams.get("appcycle").toString();
            Date date = (Date) requestParams.get("date");
            String hql = "from Appraisalmanagement where employee.userID in (" + users + ") and manager.userID=? and appcycle.id=? and ? between appcycle.submitstartdate and appcycle.submitenddate) order by concat(employee.firstName, employee.lastName) asc";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{managerid,appcycle,date});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    /**
     * Returns a resultset of distinct employees that are within a list of users and are appraised for a
     * a specified appraisal cycle.
     * <p>Returns a KwlReturnObject with the resultset added into the entity list
     *
     * @param  requestParams  contains the following keys
     *              <ul><code>users</code> - a String of user ids
     *              <br/><code>appcycle</code> - an Appraisalcycle object
     *              </ul>
     *
     * @return      KwlReturnObject
     */
    public KwlReturnObject getUsersforAppCyc(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String users = requestParams.get("users").toString();
            Appraisalcycle appcycle = (Appraisalcycle) requestParams.get("appcycle");
            String hql = "select distinct employee from Appraisalmanagement a where a.employee.userID in (" + users + ") and a.employee.deleteflag = ? and appcycle=? order by a.employee.firstName";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{0,appcycle});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    @Override
    public KwlReturnObject getUsersforAppCycleMYSQL(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String reviewerId = requestParams.get("reviewerId").toString();
            String appCycleId = requestParams.get("appCycleId").toString();
            String hql = "select ar.employee, ua.designationid, am.appcycle, us.fname, us.lname, am.empdesid from assignreviewer as ar inner join useraccount as ua on ar.employee=ua.userid inner join appraisalmanagement as am on ar.employee=am.employee inner join users as us on ar.employee=us.userid and us.deleteflag = ? where ar.reviewer=? and ar.reviewerstatus=1 and am.appcycle=? group by am.employee order by us.fname";
            tabledata = HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession(), hql, new Object[]{0,reviewerId, appCycleId});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    

    public KwlReturnObject addAppraisalcycle(HashMap<String, Object> requestParams) {
        List <Appraisalcycle> list = new ArrayList<Appraisalcycle>();
        boolean success = false;
        boolean sameRecFound = false;
        try {
        		if(requestParams.get("cyclename")!=null && requestParams.get("company")!=null){
        			String cyclename = requestParams.get("cyclename").toString();
        			String companyid=((Company)hibernateTemplate.get(Company.class, requestParams.get("company").toString())).getCompanyID().toString();
        			List <Appraisalcycle> list1 = new ArrayList<Appraisalcycle>();
        			String hql = "from Appraisalcycle where cyclename=? and company.companyID=?";
        			list1 = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{cyclename,companyid});
            		if(list1.size()>0){
            			Iterator<Appraisalcycle> itr = list1.iterator();
            			while(itr.hasNext()){
            				Appraisalcycle appraisalcycle = itr.next();
            				if(appraisalcycle.getCyclename().equals(cyclename) && !(requestParams.get("id")!=null && appraisalcycle.getId().equals(requestParams.get("id").toString()))){
            					sameRecFound = true;	
            					break;
            				}            						
            			}	
            		}
        		}
        	
                if(!sameRecFound){
                	Appraisalcycle appcycle = null;
                    if(requestParams.get("id")!=null)
                        appcycle = (Appraisalcycle) hibernateTemplate.get(Appraisalcycle.class, requestParams.get("id").toString());
                    else{
                        appcycle = new Appraisalcycle();
                        String id = UUID.randomUUID().toString();
                        appcycle.setId(id);
                    }
                    if(requestParams.get("cyclename")!=null)
                        appcycle.setCyclename(requestParams.get("cyclename").toString());
                    if(requestParams.get("startdate")!=null)
                        appcycle.setStartdate((Date)requestParams.get("startdate"));
                    if(requestParams.get("enddate")!=null)
                        appcycle.setEnddate((Date)requestParams.get("enddate"));
                    if(requestParams.get("createdby")!=null)
                        appcycle.setCreatedby((User)hibernateTemplate.get(User.class, requestParams.get("createdby").toString()));
                    if(requestParams.get("company")!=null)
                        appcycle.setCompany((Company)hibernateTemplate.get(Company.class, requestParams.get("company").toString()));
                    if(requestParams.get("submitstartdate")!=null)
                        appcycle.setSubmitstartdate((Date)requestParams.get("submitstartdate"));
                    if(requestParams.get("submitenddate")!=null)
                        appcycle.setSubmitenddate((Date)requestParams.get("submitenddate"));
                    if(requestParams.get("cycleapproval")!=null)
                        appcycle.setCycleapproval((Boolean)requestParams.get("cycleapproval"));
                    if(requestParams.get("reviewed")!=null)
                        appcycle.setReviewed((Boolean)requestParams.get("reviewed"));
                    hibernateTemplate.save(appcycle);
                    list.add(appcycle);
                    success = true;
                }

        } catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, "Appraisal Cycle added successfully.", "-1", list, list.size());
        }
    }

    public KwlReturnObject deleteAppraisalcycle(HashMap<String, Object> requestParams) {
        boolean success = false;
            try {
                String id = requestParams.get("id").toString();
                Appraisalcycle appcyc = (Appraisalcycle) hibernateTemplate.load(Appraisalcycle.class, id);
                hibernateTemplate.delete(appcyc);
                success = true;
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "Appraisal Cycle deleted successfully.", "-1", null,0);
            }
    }

    public KwlReturnObject getAppraisalManagers(HashMap<String, Object> requestParams){
        List lst1= new ArrayList();
        boolean success = false;
        try {
            String empid=requestParams.get("empid").toString();
            String cycleid=requestParams.get("cycleid").toString();
            String hql="select count(*) from Appraisalmanagement where employee.userID=? and appcycle.id=? ";
             List lst=HibernateUtil.executeQuery(hibernateTemplate,hql,new Object[]{empid,cycleid});
//             jobj.put("totalappraisal", lst.get(0));
             lst1.add(lst.get(0));
             hql="select count(*) from Appraisalmanagement where employee.userID=? and appcycle.id=? and managerstatus!=?";
             lst=HibernateUtil.executeQuery(hibernateTemplate,hql,new Object[]{empid,cycleid,0});
             lst1.add(lst.get(0));
             success=true;

             //             jobj.put("appraisalsubmitted", lst.get(0));
        } catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);

        }
        return new KwlReturnObject(success, "", "", lst1, lst1.size());
    }

    public KwlReturnObject submitAppraisalCheck(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {

            String employeeid = requestParams.get("employeeid").toString();
            String appcycleid = requestParams.get("appcycleid").toString();

            String hql = "from Appraisalmanagement where appcycle.id=? and employee.userID=? and reviewstatus=2 and (not date(now()) between appcycle.submitstartdate and appcycle.submitenddate)";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{appcycleid, employeeid});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getappraisalcycleUpdates(HashMap<String, Object> requestParams){
        Boolean success = false;
        int diff;
        List<Updates> list = new ArrayList<Updates>();
        try{
        	Locale locale = null;
            if(requestParams.get("locale")!=null){
            	locale = (Locale) requestParams.get("locale");
            }
            String userid = requestParams.get("userid").toString();
            Date userdate = (Date)(requestParams.get("userdate"));
            String query = "select distinct appm.appcycle from Appraisalmanagement appm where appm.manager.userID=? and appm.managerstatus=0 and ( appm.appcycle.submitstartdate<= ? and appm.appcycle.submitenddate>=? ) ";
            List recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{userid, userdate, userdate});

//            HashMap<String,Object> requestParams = new HashMap<String, Object>();
//            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
//
//            filter_names.add("manager.userID");
//            filter_values.add(AuthHandler.getUserid(request));
//
//            filter_names.add("managerstatus");
//            filter_values.add(0);
//
//            filter_names.add("<=appcycle.submitstartdate");
//            filter_values.add(userdate);
//
//            filter_names.add(">=appcycle.submitenddate");
//            filter_values.add(userdate);
//
//            requestParams.put("filter_names", filter_names);
//            requestParams.put("filter_values", filter_values);
//            result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
//            List recordTotalCount = result.getEntityList();


                Iterator itr = recordTotalCount.iterator();
                while (itr.hasNext()) {
                    String updateDiv = "";
//                    JSONObject obj = new JSONObject();
                    Appraisalcycle app = (Appraisalcycle) itr.next();
                    diff = (int) ((app.getSubmitenddate().getTime() - userdate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
                    updateDiv += messageSource.getMessage("hrms.common.Alert.SumitedTheApprisalForm",new Object[]{"<a href='#' onclick='competencyedit(\""+app.getId()+"\")'> "+messageSource.getMessage("hrms.common.AppraisalForm", null, locale)+" </a>",app.getCyclename(),"<font color='green'> " + diff + "</font>"}, "Submit the <a href='#' onclick='competencyedit(\""+app.getId()+"\")'> Appraisal Form </a> for appraisal cycle " + app.getCyclename() + " in <font color='green'> " + diff + "</font> day(s)", locale);
//                    obj.put("update", getContentSpan(updateDiv));
//                    jArr.put(obj);
                    list.add(new Updates(StringUtil.getContentSpan(updateDiv), app.getSubmitenddate()));
                }
         success = true;
        }catch(Exception ex){
            success = false;
            ex.printStackTrace();
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
    }


    public KwlReturnObject updateappraisalstatus(HashMap<String,Object> requestParams){
        Boolean success = false;
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        KwlReturnObject result = null;
        try{
            Date userdate = (Date)(requestParams.get("userdate"));
                    String query = "from Appraisalmanagement appm where appm.managerstatus=1 and appm.reviewstatus!=2 and appm.appcycle.submitenddate<?";
                    List recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{userdate});
//                    filter_names.add("managerstatus");
//                    filter_values.add(1);
//
//                    filter_names.add("!reviewstatus");
//                    filter_values.add(2);
//
//                    filter_names.add("<appcycle.submitenddate");
//                    filter_values.add(userdate);
//
//                    result = hrmsAppraisalcycleDAOObj.getAppraisalmanagement(requestParams);
//                    List recordTotalCount = result.getEntityList();

                    Iterator itr = recordTotalCount.iterator();
                    while (itr.hasNext()) {
                        Appraisalmanagement app = (Appraisalmanagement) itr.next();
                        app.setReviewstatus(2);
                        hibernateTemplate.update(app);
//                        requestParams.put("Appraisalid", app.getAppraisalid());
//                        requestParams.put("Reviewstatus", 2);
//                        hrmsAppraisalcycleDAOObj.addAppraisalmanagement(requestParams);
                    }
            success = true;
        }catch(Exception ex){
            ex.printStackTrace();
            success = false;
        }finally{
            return new KwlReturnObject(success, "", "", null, 0);
        }
    }


    public KwlReturnObject getAppraisalapprovalupdate(HashMap<String,Object> requestParams){
        Boolean success = false;
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int diff;
        List <Updates> list = new ArrayList<Updates>();
        try{
            String userid = requestParams.get("userid").toString();
            Date userdate = (Date)(requestParams.get("userdate"));
                String query = "from Appraisalmanagement appm where appm.employee.userID=? and appm.reviewstatus=2 and appm.appcycle.cycleapproval=? group by appm.appcycle";
                List recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{userid, true});
                
                Iterator itr = recordTotalCount.iterator();
                while (itr.hasNext()) {
                    Appraisalmanagement app = (Appraisalmanagement) itr.next();
                    if (app.getReviewersubmitdate() != null) {
                        diff = (int) ((userdate.getTime() - app.getReviewersubmitdate().getTime()) / (1000 * 60 * 60 * 24)) + 1;
                        if (diff < 10) {
                            String updateDiv = "";
                            JSONObject obj = new JSONObject();
                            Locale locale = null;
                            if(requestParams.get("locale")!=null){
                            	locale = (Locale) requestParams.get("locale");
                            }
                            updateDiv +=messageSource.getMessage("hrms.common.Updates.getAppraisalapprovalupdate",new Object[]{"<a href='#' onclick='myfinalReport(\""+app.getAppcycle().getId()+"\")'>"+messageSource.getMessage("hrms.common.appraisal1",null, locale)+"</a>",app.getAppcycle().getCyclename()},"Your <a href='#' onclick='myfinalReport(\""+app.getAppcycle().getId()+"\")'>appraisal</a> for appraisal cycle " + app.getAppcycle().getCyclename() + " has been approved", locale);
                            list.add(new Updates(StringUtil.getContentSpan(updateDiv), app.getEnddate()));	
                        }
                    }
                }
                success = true;
        }catch(Exception ex){
            ex.printStackTrace();
            success = false;
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
    }

     public KwlReturnObject getFillappraisalupdate(HashMap<String,Object> requestParams){
        Boolean success = false;
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int diff;
        List <Updates> list = new ArrayList<Updates>();
        try{
            String userid = requestParams.get("userid").toString();
            Date userdate = (Date)(requestParams.get("userdate"));

            Locale locale = null;
            if(requestParams.get("locale")!=null){
            	locale = (Locale) requestParams.get("locale");
            }
            
                    User user = (User) hibernateTemplate.get(User.class, userid);
                    String query = "select distinct appm.appcycle from Appraisalmanagement appm where appm.employee.userID=? and appm.employeestatus=0 and ( appm.appcycle.submitstartdate<=? and appm.appcycle.submitenddate>=? )";
                    Object[] object = {userid, userdate, userdate};
                    List recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, object);

                    Iterator itr = recordTotalCount.iterator();

                    while (itr.hasNext()) {
                        String updateDiv = "";
                        JSONObject obj = new JSONObject();
                        Appraisalcycle app = (Appraisalcycle) itr.next();
                        diff = (int) ((app.getSubmitenddate().getTime() - userdate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
                        updateDiv += messageSource.getMessage("hrms.common.Alert.FillTheAppriasalUpdate",new Object[]{"<a href='#' onclick='myAppraisal(\""+app.getId()+"\")'> "+messageSource.getMessage("hrms.common.AppraisalForm", null, locale)+"</a>","<font color='green'> " + diff + "</font>",app.getCyclename(),user.getCompany().getCreator().getFirstName() + " " + user.getCompany().getCreator().getLastName()}, "Fill the <a href='#' onclick='myAppraisal(\""+app.getId()+"\")'> Appraisal Form </a>in <font color='green'> " + diff + "</font> day(s) for appraisal cycle " + app.getCyclename() + " initiated by " + user.getCompany().getCreator().getFirstName() + " " + user.getCompany().getCreator().getLastName(), locale);
                        list.add(new Updates(StringUtil.getContentSpan(updateDiv), app.getSubmitenddate()));
                    }
                    success = true;
        }catch(Exception ex){
            ex.printStackTrace();
            success = false;
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
    }

     public KwlReturnObject getAppraisalcycleupdatesDetail(HashMap<String,Object> requestParams){
         KwlReturnObject result = null;
        Boolean success = false;
        List <Updates> list = new ArrayList<Updates>();
        try{
	        	Locale locale = null;
	            if(requestParams.get("locale")!=null){
	            	locale = (Locale) requestParams.get("locale");
	            }
                String updateDiv = "";
                Date userdate = (Date) requestParams.get("userdate");
//                DateFormat df = (DateFormat) requestParams.get("df");
                SimpleDateFormat df = new SimpleDateFormat("MMMM d, yyyy");
                String userid = requestParams.get("userid").toString();
                boolean selfappraisal = false;
                if(requestParams.get("selfappraisal")!=null) {
                    selfappraisal = Boolean.parseBoolean(requestParams.get("selfappraisal").toString());
                }
                String query = "select distinct appm.appcycle from Appraisalmanagement appm where (appm.manager.userID=? or appm.employee.userID=?) and appm.appcycle.submitenddate>=?";
                List recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{userid,userid,userdate});
                Iterator itr = recordTotalCount.iterator();
                while (itr.hasNext()) {
                    JSONObject obj = new JSONObject();
                    Appraisalcycle app = (Appraisalcycle) itr.next();
                        Object[] row;
                        String selfapp = "";
                        String appoth = "";
                        updateDiv = messageSource.getMessage("hrms.common.Updates.getAppraisalcycleupdatesDetail1",new Object[]{"<font color='red'>" + app.getCyclename() + "</font><hr><li style='margin-left:10px;'>","<font color='blue'>" + df.format(app.getSubmitenddate()) + "</font></li>"},"Appraisal Cycle: <font color='red'>" + app.getCyclename() + "</font><hr><li style='margin-left:10px;'>Deadline for submission: <font color='blue'>" + df.format(app.getSubmitenddate()) + "</font></li>", locale);
                        String hql = "select appm.employee.userID,appm.employeestatus,appm.reviewstatus from Appraisalmanagement appm where appm.manager.userID=? and " +
                                "appm.appcycle.id=? group by appm.appcycle.id";
                        Object[] object = {userid, app.getId()};
                        recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, hql, object);
                        if (recordTotalCount.size() > 0) {
                            row = (Object[]) recordTotalCount.get(0);
                            // @@ need to chck if there is any need
                            //if (hrmsManager.isAppraiser(AuthHandler.getUserid(request), session, request)) {
                            requestParams.clear();
                            requestParams.put("empid", userid);
                            requestParams.put("cycleid", app.getId());
//                                result = getAppraisalManagers(requestParams);
                                  result = getAppraisalManagercount(requestParams)  ;
                                List applist = result.getEntityList();
                                //totalno = getAppraisalManagercount(hibernateTemplate, userid, app.getId(), totalno);
                                int submitted = Integer.valueOf(applist.get(1).toString());
                                int remaIning = Integer.valueOf(applist.get(0).toString()) - submitted;
                                updateDiv += "<li style='margin-left:10px;'>"+messageSource.getMessage("hrms.common.Updates.NoofAppraisalsSubmitted",null,"No. of Appraisals Submitted", locale)+": <font color='blue'>" + submitted + "</font></li>";
                                if(remaIning > 0){
                                    appoth = "<a href='#' onclick='competencyedit(\""+app.getId()+"\")'>" + remaIning + "</a>";
                                } else {
                                    appoth = ""+remaIning;
                                }
                                updateDiv += "<li style='margin-left:10px;'>"+messageSource.getMessage("hrms.common.Updates.NoofAppraisalsRemaining",null,"No. of Appraisals Remaining", locale)+": <font color='blue'>" + appoth + "</font></li>";
                            //}
                        }
                        if(selfappraisal) {
                            hql = "from Appraisalmanagement appm where appm.employee.userID=? and appm.appcycle.id=? group by appm.appcycle.id";
                            recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userid, app.getId()});
                            if (!recordTotalCount.isEmpty()) {
                                Appraisalmanagement log = (Appraisalmanagement) recordTotalCount.get(0);
                                if ((Integer) log.getEmployeestatus() == 0) {
                                    selfapp = "<a href='#' onclick='myAppraisal(\""+app.getId()+"\")'> "+messageSource.getMessage("hrms.common.Updates.NotSubmitted",null," Not Submitted", locale)+"</a>";
                                } else {
                                    selfapp = messageSource.getMessage("hrms.common.Updates.Submitted",null,"Submitted", locale);
                                }
                                updateDiv += "<li style='margin-left:10px;'>"+messageSource.getMessage("hrms.common.Updates.SelfAppraisal",null,"Self Appraisal", locale)+": <font color='blue'>" + selfapp + "</font></li>";
                            }
                        }
                        list.add(new Updates(StringUtil.getContentSpan(updateDiv), app.getSubmitenddate()));
                        
                }
        }catch(Exception ex){
            ex.printStackTrace();
            success = false;
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
    }

    public KwlReturnObject getAppraisalManagercount(HashMap<String, Object> requestParams){
        List lst1= new ArrayList();
        boolean success = false;
        try {
            String empid=requestParams.get("empid").toString();
            String cycleid=requestParams.get("cycleid").toString();
            String hql="select count(*) from Appraisalmanagement where manager.userID=? and appcycle.id=? ";
             List lst=HibernateUtil.executeQuery(hibernateTemplate,hql,new Object[]{empid,cycleid});
//             jobj.put("totalappraisal", lst.get(0));
             lst1.add(lst.get(0));
             hql="select count(*) from Appraisalmanagement where manager.userID=? and appcycle.id=? and managerstatus!=?";
             lst=HibernateUtil.executeQuery(hibernateTemplate,hql,new Object[]{empid,cycleid,0});
             lst1.add(lst.get(0));
             success=true;

             //             jobj.put("appraisalsubmitted", lst.get(0));
        } catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);

        }
        return new KwlReturnObject(success, "", "", lst1, lst1.size());
    }

     public KwlReturnObject showappraisalForm(HashMap<String,Object>requestParams){
        KwlReturnObject result = null;
        List tabledata = null;
        Boolean success = false;
        try {
            Date userdate = (Date) requestParams.get("userdate");
            Boolean employee = (Boolean) requestParams.get("employee");
            String userid = requestParams.get("userid").toString();
            HashMap<String,Object> requestParams1 = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
            if (employee) {
                  filter_names.add("employee.userID");
                  filter_values.add(userid);
                  
                  filter_names.add(">=appcycle.submitenddate");
                  filter_values.add(userdate);

//                hql = "from Appraisalmanagement where employee.userID=? and appcycle.submitenddate>=?";
            } else{
//                hql = "from Appraisalmanagement where manager.userID=? and appcycle.submitenddate>=?";
                  filter_names.add("manager.userID");
                  filter_values.add(userid);

                  filter_names.add(">=appcycle.submitenddate");
                  filter_values.add(userdate);
            }
            requestParams1.put("filter_names", filter_names);
            requestParams1.put("filter_values", filter_values);
//            tabledata = HibernateUtil.executeQuery(session, hql, new Object[]{AuthHandler.getUserid(request),userdate});
            result = getAppraisalmanagement(requestParams1);
            tabledata = result.getEntityList();
            if (!tabledata.isEmpty()) {
                success = true;
            }
        } catch (Exception ex) {
            success = false;
        }
        return new KwlReturnObject(success, "", "", tabledata, tabledata.size());
    }

     public KwlReturnObject isReviewer(HashMap<String,Object>requestParams){
        List tabledata = null;
        String hql;
        Boolean success = false;
        try {
            String userid = requestParams.get("userid").toString();
            Date userdate = (Date) requestParams.get("userdate");
            hql = "from Assignreviewer where reviewer.userID=? and reviewerstatus=1";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, userid);
            Iterator ite = tabledata.iterator();
            String users = "";
            while (ite.hasNext()) {
                Assignreviewer log = (Assignreviewer) ite.next();
                users += "'" + log.getEmployee().getUserID() + "',";
            }
            if (users.length() > 0) {//review link will be shown only after end date of submission
                users = users.substring(0, users.length() - 1);
                hql = "from Appraisalmanagement appm where appm.employee.userID in (" + users + ") and appm.appcycle.submitenddate<?";
                tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{userdate});
                if (!tabledata.isEmpty()) {
                    success = true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            success = false;
        }
        return new KwlReturnObject(success, "", "", tabledata, tabledata.size());
    }

    @Override
    public KwlReturnObject getQuestionAnswerGrid(HashMap<String, Object> requestParams) {
        List<AppraisalmanagementQuestionAnswers> ll = null;
        boolean success = false;
        try{
            DetachedCriteria crit = DetachedCriteria.forClass(AppraisalmanagementQuestionAnswers.class,hrmsAnonymousAppraisalConstants.alias_apqa);

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();

            if(requestParams.containsKey("filter_names")){
                filter_names = (ArrayList) requestParams.get("filter_names");
            }
            if(requestParams.containsKey("filter_params")){
                filter_params = (ArrayList) requestParams.get("filter_params");
            }
            BuildCriteria.filterQuery(crit, filter_names, filter_params, "and");
            
            ll = hibernateTemplate.findByCriteria(crit);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, hrmsAnonymousAppraisalConstants.question_success, " ", ll, ll.size());
        }
    }
    public KwlReturnObject getQuestions(HashMap<String, Object> requestParams) {
        List<QuestionGroup> ll = null;
        boolean success = false;
        try{
            DetachedCriteria crit = DetachedCriteria.forClass(QuestionGroup.class,hrmsAnonymousAppraisalConstants.alias_apqa);

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();

            if(requestParams.containsKey("filter_names")){
                filter_names = (ArrayList) requestParams.get("filter_names");
            }
            if(requestParams.containsKey("filter_params")){
                filter_params = (ArrayList) requestParams.get("filter_params");
            }
            BuildCriteria.filterQuery(crit, filter_names, filter_params, "and");

            ll = hibernateTemplate.findByCriteria(crit);
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, hrmsAnonymousAppraisalConstants.question_success, " ", ll, ll.size());
        }
    }
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
