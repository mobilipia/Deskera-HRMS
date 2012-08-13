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

package com.krawler.spring.hrms.nonanonymousappraisal;

import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.AppraisalmanagementQuestionAnswers;
import com.krawler.spring.common.KwlReturnObject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.hibernate.SessionFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class hrmsNonAnonymousAppraisalDAOImpl implements hrmsNonAnonymousAppraisalDAO, MessageSourceAware{
	
    private HibernateTemplate hibernateTemplate;
    private MessageSource messageSource;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }


    public KwlReturnObject reviewNonAnonymousAppraisalReport(HashMap<String, Object> requestParams) {
        boolean success = false;
        String msg = null;
        try {
        	
        	Locale locale = null;
            if(requestParams.get("locale")!=null){
            	locale = (Locale) requestParams.get("locale");
            }
            Appraisalmanagement app = (Appraisalmanagement) requestParams.get("appmgmt");
            if (Boolean.parseBoolean(requestParams.get("approve").toString())) {//To approve request
                if (app.getReviewstatus() != 2) {

                    app.setOriginaldesignation((MasterData) requestParams.get("oridesig"));
                    app.setOriginaldepartment((MasterData) requestParams.get("oridept"));
                    app.setReviewstatus((Integer) requestParams.get("revstat"));
                    app.setAppraisalstatus(requestParams.get("apprstat").toString());
                    if (requestParams.get("revcomment") != null) {
                        app.setReviewercomment(requestParams.get("revcomment").toString());
                    }
                    app.setEmployeestatus((Integer) requestParams.get("empstat"));
                    app.setManagerstatus((Integer) requestParams.get("mgrstat"));
                    app.setReviewersubmitdate((Date) requestParams.get("revsubmitdate"));
                    if (requestParams.get("revdept") != null) {
                        app.setReviewdepartment((MasterData) requestParams.get("revdept"));
                    }
                    if (requestParams.get("revdesig") != null) {
                        app.setReviewdesignation((MasterData) requestParams.get("revdesig"));
                    }
                    app.setReviewsalaryincrement((Float) (requestParams.get("revsalinc")));

                    hibernateTemplate.update(app);
                    success = true;
                } else {
                    msg = messageSource.getMessage("hrms.performance.appraisal.already.approved", null, locale);
                    success = true;
                }
            } else { //To unapprove the request
                if (app.getReviewstatus() != 2) {
                    app.setManagerstatus((Integer) requestParams.get("mgrstat"));
                    app.setReviewstatus((Integer) requestParams.get("revstat"));
                    if (requestParams.get("revcomment") != null) {
                        app.setReviewercomment(requestParams.get("revcomment").toString());
                    }
                    app.setAppraisalstatus(requestParams.get("apprstat").toString());
                    success = true;
                } else {
                    success = true;
                    msg = messageSource.getMessage("hrms.performance.appraisal.been.already.approved", null, locale);
                }
            }

            success = true;
            msg = messageSource.getMessage("hrms.performance.appraisal.status.changed", null, locale);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new KwlReturnObject(success, msg, "", null, 0);

    }

    public KwlReturnObject changeSalaryonReview(HashMap<String, Object> requestParams) {
        boolean success = false;
        String msg = "";
        try {
        	Locale locale = null;
            if(requestParams.get("locale")!=null){
            	locale = (Locale) requestParams.get("locale");
            }
            User user = (User) requestParams.get("user");
            Useraccount ua = (Useraccount) hibernateTemplate.get(Useraccount.class, user.getUserID());
            ua.setSalary(requestParams.get("salary").toString());
            if (requestParams.get("revdept") != null) {
                ua.setDepartment((MasterData) requestParams.get("revdept"));
            }
            if (requestParams.get("revdesig") != null) {
                ua.setDesignationid((MasterData) requestParams.get("revdesig"));
            }
            hibernateTemplate.update(user);
            success = true;
            msg = messageSource.getMessage("hrms.performance.appraisal.status.changed", null, locale);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new KwlReturnObject(success, msg, "", null, 0);
    }
    
    @Override
    public List<AppraisalmanagementQuestionAnswers> getQuestions(String appcycle, String useid){
    	List<AppraisalmanagementQuestionAnswers> list = null;
    	try{
    		String hql = "from AppraisalmanagementQuestionAnswers where appcycle.id=? and employee.userID=? group by cmptquestion.quesid";
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{appcycle, useid});
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    
    @Override
    public List<AppraisalmanagementQuestionAnswers> getQuestionsAnswers(String appcycle, String useid){
    	List<AppraisalmanagementQuestionAnswers> list = null;
    	try{
    		String hql = "from AppraisalmanagementQuestionAnswers where appcycle.id=? and employee.userID=?";
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{appcycle, useid});
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    
    public MasterData getDesignation(String appcycle, String useid){
    	MasterData designation = null;
    	try{
    		List<Appraisalmanagement> list = null;
    		String hql = "from Appraisalmanagement where appcycle.id=? and employee.userID=? group by employee.userID";
    		list = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{appcycle, useid});
    		for(Appraisalmanagement app: list){
    			designation = app.getEmpdesid();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return designation;
    }
    
    
    @Override
    public List<String> getQuestions(String appraisalid){
    	List<String> list = null;
    	try{
    		String hql = "select competencyquestion from appraisalcyclecompetencyquestions where appraisalid = ? group by competencyquestion";
    		list = HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession(), hql, new Object[]{appraisalid});
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
