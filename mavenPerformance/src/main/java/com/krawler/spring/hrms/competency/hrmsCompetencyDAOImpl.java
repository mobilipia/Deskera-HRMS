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
package com.krawler.spring.hrms.competency;

import com.krawler.common.admin.Company;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.AppraisalmanagementQuestionAnswers;
import com.krawler.hrms.performance.CompetencyDesMap;
import com.krawler.hrms.performance.CompetencyQuestion;
import com.krawler.hrms.performance.Managecmpt;
import com.krawler.hrms.performance.Mastercmpt;
import com.krawler.hrms.performance.QuestionGroup;
import com.krawler.hrms.performance.ReviewerQuestionMap;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class hrmsCompetencyDAOImpl implements hrmsCompetencyDAO {

    private HibernateTemplate hibernateTemplate;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }
    
    public KwlReturnObject getManagecmpt(HashMap<String,Object> requestParams) {
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
                hql = "from Managecmpt where mid=?";
                String mid = requestParams.get("mid").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{mid});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from Managecmpt ";
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

    public KwlReturnObject addManagecmpt(HashMap<String, Object> requestParams) {
        List <Managecmpt> list = new ArrayList<Managecmpt>();
        boolean success = false;
        try {
                Managecmpt managecmpt = null;
                if(requestParams.get("mid")!=null)
                    managecmpt = (Managecmpt) hibernateTemplate.get(Managecmpt.class, requestParams.get("mid").toString());
                else{
                    managecmpt = new Managecmpt();
                    String id = UUID.randomUUID().toString();
                    managecmpt.setMid(id);
                }
                if(requestParams.get("mastercmpt")!=null)
                    managecmpt.setMastercmpt((Mastercmpt)hibernateTemplate.get(Mastercmpt.class, requestParams.get("mastercmpt").toString()));
                if(requestParams.get("desig")!=null)
                    managecmpt.setDesig((MasterData)hibernateTemplate.get(MasterData.class, requestParams.get("desig").toString()));
                if(requestParams.get("delflag")!=null)
                    managecmpt.setDelflag(Integer.valueOf(requestParams.get("delflag").toString()));
                if(requestParams.get("weightage")!=null)
                    managecmpt.setWeightage(Integer.valueOf(requestParams.get("weightage").toString()));
                hibernateTemplate.save(managecmpt);
                list.add(managecmpt);
                success = true;

        } catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, "Competency assigned successfully.", "-1", list, list.size());
        }
    }

    public KwlReturnObject getMastercmpt(HashMap<String,Object> requestParams) {
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
                hql = "from Mastercmpt where cmptid=?";
                String cmptid = requestParams.get("cmptid").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{cmptid});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from Mastercmpt ";
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

    public KwlReturnObject addMastercmpt(HashMap<String, Object> requestParams) {
        List <Mastercmpt> list = new ArrayList<Mastercmpt>();
        boolean success = false;
        try {
                Mastercmpt mastercmpt = null;
                if(requestParams.get("cmptid")!=null)
                    mastercmpt = (Mastercmpt) hibernateTemplate.get(Mastercmpt.class, requestParams.get("cmptid").toString());
                else{
                    mastercmpt = new Mastercmpt();
                    String id = UUID.randomUUID().toString();
                    mastercmpt.setCmptid(id);
                }
                if(requestParams.get("cmptname")!=null)
                    mastercmpt.setCmptname(requestParams.get("cmptname").toString());
                if(requestParams.get("cmptwt")!=null)
                    mastercmpt.setCmptwt(Integer.valueOf(requestParams.get("cmptwt").toString()));
                if(requestParams.get("cmptdesc")!=null)
                    mastercmpt.setCmptdesc(requestParams.get("cmptdesc").toString());
                if(requestParams.get("company")!=null)
                    mastercmpt.setCompany((Company)hibernateTemplate.get(Company.class, requestParams.get("company").toString()));
                hibernateTemplate.save(mastercmpt);
                list.add(mastercmpt);
                success = true;

        } catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, "Competency added successfully.", "-1", list, list.size());
        }
    }

     public KwlReturnObject deleteMastercmpt(HashMap<String, Object> requestParams) {
         boolean success = false;
         String cmpname="";
         try {
            String cmptid = requestParams.get("cmptid").toString();
            Mastercmpt item = (Mastercmpt) hibernateTemplate.get(Mastercmpt.class, cmptid);
            cmpname=item.getCmptname();
            hibernateTemplate.delete(item);
            success = true;
        } catch (Exception e) {
            success = false;
            System.out.println("Error is "+e);
        }finally{
            return new KwlReturnObject(success, cmpname, "-1", null, 0);
        }
    }


    public KwlReturnObject getCompetencyAvailable(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String companyid = requestParams.get("companyid").toString();
            String desigid = requestParams.get("desigid").toString();
            Integer delflag = Integer.valueOf(requestParams.get("delflag").toString());
            String hql = "from Mastercmpt where company.companyID=? and cmptid not in (select mastercmpt from Managecmpt where desig.id=? and delflag=?)";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{companyid,desigid,delflag});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getCompetencyAssigned(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String companyid = requestParams.get("companyid").toString();
            String desigid = requestParams.get("desigid").toString();
            Integer delflag = Integer.valueOf(requestParams.get("delflag").toString());
            String hql = "from Managecmpt where desig.id=? and mastercmpt.company.companyID=? and delflag=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{desigid,companyid,delflag});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    // ## shift to appraisal component latter
    public KwlReturnObject getAppraisal(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String cmptid = requestParams.get("cmptid").toString();
            String hql = "from Appraisal where competency.mastercmpt.cmptid=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{cmptid});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject insertQuestion(HashMap<String, Object> requestParams) {
        boolean success=false;
        try {
            String apprmanager = "";
            CompetencyQuestion fgmt = null;
            boolean flg = true;
//            User usr = (User) hibernateTemplate.load(User.class, requestParams.get("empid").toString());
            String jsondata = requestParams.get("jsondata").toString();
            String groupID = requestParams.get("groupID").toString();
            JSONArray jarr = new JSONArray(jsondata);
            String id = UUID.randomUUID().toString();
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.getJSONObject(i);
                if (jobj.getString("qid").equals("undefined") || jobj.getString("qid").equals("")) {
                    fgmt = new CompetencyQuestion();
                    String qid = UUID.randomUUID().toString();
                    fgmt.setQuesid(qid);

                    fgmt.setQuesdesc(jobj.getString("qdescription"));
                    fgmt.setNoofans(Integer.parseInt(jobj.getString("qans")));
                    fgmt.setQuesorder(Integer.parseInt(jobj.getString("qorder")));
                    fgmt.setQuestype(jobj.getString("qtype"));
                    fgmt.setVisible(Boolean.parseBoolean(jobj.getString("isVisible")));
                    hibernateTemplate.save(fgmt);

                    QuestionGroup qg = new QuestionGroup();
                    String mid = UUID.randomUUID().toString();
                    qg.setMid(mid);
                    if(StringUtil.isNullOrEmpty(groupID)){
                        qg.setGroupid(id);
                    } else {
                        qg.setGroupid(groupID);
                    }
                    
                    qg.setCmptquestion(fgmt);
                    hibernateTemplate.save(qg);
    //
                    ReviewerQuestionMap rqm = new ReviewerQuestionMap();
                    String rmid = UUID.randomUUID().toString();
                    rqm.setMid(rmid);
                    if(!StringUtil.isNullOrEmpty(jobj.getString("qdes"))){
                        rqm.setRevdesid((MasterData)hibernateTemplate.get(MasterData.class, jobj.getString("qdes").toString()));
                    }
                    rqm.setEmpdesid((MasterData)hibernateTemplate.get(MasterData.class, requestParams.get("desigid").toString()));
                    rqm.setCmptquestion(fgmt);
                    hibernateTemplate.save(rqm);
                } else {
                    flg = false;
                    fgmt = (CompetencyQuestion) hibernateTemplate.load(CompetencyQuestion.class, jobj.getString("qid"));
                    fgmt.setQuesdesc(jobj.getString("qdescription"));
                    fgmt.setNoofans(Integer.parseInt(jobj.getString("qans")));
                    fgmt.setQuesorder(Integer.parseInt(jobj.getString("qorder")));
                    fgmt.setQuestype(jobj.getString("qtype"));
                    fgmt.setVisible(Boolean.parseBoolean(jobj.getString("isVisible")));
                    hibernateTemplate.save(fgmt);
                    
                    try{
                    	List<ReviewerQuestionMap> rqm = (List<ReviewerQuestionMap>) HibernateUtil.executeQuery(hibernateTemplate, "from ReviewerQuestionMap where quesid=?", fgmt);
             		    Iterator<ReviewerQuestionMap> irqm = rqm.iterator();	
             		    while(irqm.hasNext()){
             		    	ReviewerQuestionMap temp = irqm.next();
             		    	temp.setRevdesid((MasterData)hibernateTemplate.get(MasterData.class, jobj.getString("qdes").toString()));
             		    	hibernateTemplate.saveOrUpdate(temp);
             		    }
                    }catch(ObjectNotFoundException e){
                    	e.printStackTrace();
                    }
                }
                
            }
            if(flg){
                CompetencyDesMap cdm = new CompetencyDesMap();
                String cdmid = UUID.randomUUID().toString();
                cdm.setMid(cdmid);
                cdm.setGroupid(id);
                cdm.setDesig((MasterData)hibernateTemplate.get(MasterData.class, requestParams.get("desigid").toString()));
                hibernateTemplate.save(cdm);
            }
            success=true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", null, 0);
        }
    }

    public KwlReturnObject getCompetencyDesMap(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String desigid = requestParams.get("desigid").toString();
            String hql = " from CompetencyDesMap where desig.id=? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{desigid});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getQuestionGroup(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String groupid = requestParams.get("grid").toString();
            String hql = " from QuestionGroup where groupid=? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{groupid});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getQuestionGroupMysql1(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String groupid = requestParams.get("grid").toString();
            String isEmployee = requestParams.get("isEmployee").toString();
            String designationid = requestParams.get("revdesid").toString();
            String empdesignationid = requestParams.get("empdesid").toString();
//            String hql = " from QuestionGroup where groupid=? ";
            String sql = "";
            if(isEmployee.equals("false")){
//                 sql = "select cq.quesid, cq.quesdesc, noofans, questype, quesorder from competencyquestion as cq inner join questiongroup as qg on qg.quesid = cq.quesid where groupid = ? ";
                 sql = "select cq.quesid, cq.quesdesc, noofans, questype, quesorder, rqm.revdesid from competencyquestion as cq inner join questiongroup as qg on qg.quesid = cq.quesid inner join (select * from reviewerquestionmap where empdesid = ?) as rqm on rqm.quesid = qg.quesid where groupid = ? ";
                 tabledata = HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession(), sql, new Object[]{empdesignationid,groupid});
            } else {
                 sql = "select cq.quesid, cq.quesdesc, noofans, questype, quesorder, rqm.revdesid from competencyquestion as cq inner join questiongroup as qg on qg.quesid = cq.quesid inner join (select * from reviewerquestionmap where empdesid = ?) as rqm on rqm.quesid = qg.quesid where groupid = ? and (rqm.revdesid = ? OR rqm.revdesid is null) ";
                 tabledata = HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession(), sql, new Object[]{empdesignationid, groupid, designationid});
//                 tabledata = HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession().connection(), sql, new Object[]{empdesignationid, groupid, designationid});
            }


            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    public List<CompetencyQuestion> getQuestionGroupMysql(String groupid, boolean isEmployee, String designationid, String empdesignationid, Appraisalmanagement appraisalmanagement) {
        List<CompetencyQuestion> tabledata = null;
        try {
            // get list of reviewer questions
            String hql = "";
            List<String> reviewerQuestionIds = null;
            if(!isEmployee){
            	if(appraisalmanagement!=null){
            		hql = "select competencyquestion from appraisalcyclecompetencyquestions where appraisalid = ?";
            		reviewerQuestionIds = HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession(), hql, new Object[]{appraisalmanagement.getAppraisalid()});
            	}else{
            		hql = "select cmptquestion.quesid from ReviewerQuestionMap where empdesid.id = ?";
            		reviewerQuestionIds = HibernateUtil.executeQuery(hibernateTemplate, hql, empdesignationid);
            	}
            } else {
            	if(appraisalmanagement!=null){
            		hql = "select competencyquestion from appraisalcyclecompetencyquestions where appraisalid = ?";
            		reviewerQuestionIds = HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession(), hql, new Object[]{appraisalmanagement.getAppraisalid()});
            	}else{
            		hql = "select cmptquestion.quesid from ReviewerQuestionMap where empdesid.id = ? and (revdesid.id = ? OR revdesid is null) ";
            		reviewerQuestionIds = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{empdesignationid, designationid});
            	}
            }


            StringBuilder sb = new StringBuilder();
            if (reviewerQuestionIds != null && !reviewerQuestionIds.isEmpty())
            {
                int index = 0;
                for (String qId: reviewerQuestionIds)
                {
                    index++;
                    sb.append("'");
                    sb.append(qId);
                    sb.append("'");
                    if (index < reviewerQuestionIds.size())
                    {
                        sb.append(",");
                    }
                }
            }


            if(!isEmployee){
            	if(sb.length()>0){
                 hql = "select qg.cmptquestion from QuestionGroup qg where qg.groupid = ? and qg.cmptquestion.quesid in ("+sb.toString()+")";
                 tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{groupid});
            	}
            } else {
            	if(sb.length()>0){
                 hql = "select qg.cmptquestion from QuestionGroup qg where qg.groupid = ? and qg.cmptquestion.quesid in ("+sb.toString()+")";
                 tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{groupid});
            	}
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tabledata;
    }


    public KwlReturnObject getCmptQuestion(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String groupid = requestParams.get("qid").toString();
            String hql = " from CompetencyQuestion where quesid=? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{groupid});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getReviewerQuestionMap(HashMap<String, Object> requestParams) {
        List tabledata = null;
        boolean success = false;
        try {
            String qid = requestParams.get("qid").toString();
            String desid = requestParams.get("desid").toString();
            String hql = " from ReviewerQuestionMap where cmptquestion.quesid=? and empdesid.id=? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{qid,desid});
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "success", "-1", tabledata, tabledata.size());
        }
    }

    public String getReviewerDesignation(HashMap<String, Object> requestParams) {
        String result = null;
        boolean success = false;
        try {
            String qid = requestParams.get("qid").toString();
            String desid = requestParams.get("desid").toString();
            String hql = " select revdesid.id from ReviewerQuestionMap where cmptquestion.quesid=? and empdesid.id=? ";
            List tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{qid,desid});
            if(tabledata != null){
                result = tabledata.get(0).toString();
            }
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return result;
        }
    }

    public int getCompetencyDesMapCount(HashMap<String, Object> requestParams) {
        int cnt = 0;
        boolean success = false;
        try {
            String desigid = requestParams.get("desigid").toString();
            String sql = "select count(mid) from competencydesmap where desid = ?";
            List tabledata = HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession(), sql, new Object[]{desigid});
            cnt = Integer.parseInt(tabledata.get(0).toString());
            success = true;
        } catch (Exception ex) {
            success = false;
        } finally {
            return cnt;
        }
    }

    public KwlReturnObject deleteCompetencyQuestion(HashMap<String, Object> requestParams) {
        boolean success = true;
        List <String[]> lst = new ArrayList<String[]>();
        String quename="";
        try {
           String[] ids = (String[]) requestParams.get("ids");
           for (int i = 0; i < ids.length; i++) {
        	   List<Object> amqa = (List<Object>) HibernateUtil.executeSQLQuery(hibernateTemplate.getSessionFactory().getCurrentSession(), "select appraisalid from appraisalcyclecompetencyquestions where competencyquestion=?", new Object[]{ids[i]});
        	   if(amqa.isEmpty()){
        		   //if there is no answer for this question
        		   List<QuestionGroup> qg = (List<QuestionGroup>) HibernateUtil.executeQuery(hibernateTemplate, "from QuestionGroup where quesid=?", ids[i]);
        		   Iterator<QuestionGroup> iqg = qg.iterator();
        		   String gid=qg.get(0).getGroupid();	
        		   while(iqg.hasNext()){
        			   hibernateTemplate.delete(iqg.next());
        		   }
        		   
        		   List<QuestionGroup> qglist = (List<QuestionGroup>) HibernateUtil.executeQuery(hibernateTemplate, "from QuestionGroup where groupid=?", gid);
        		   if(qglist.isEmpty()){
        			   List<CompetencyDesMap> cdm = (List<CompetencyDesMap>) HibernateUtil.executeQuery(hibernateTemplate, "from CompetencyDesMap where groupid=?", gid);
        			   Iterator<CompetencyDesMap> icdm = cdm.iterator();
        			   while(icdm.hasNext()){
        				   hibernateTemplate.delete(icdm.next());
        			   }
        		   }
        		   
        		   List<ReviewerQuestionMap> rqm = (List<ReviewerQuestionMap>) HibernateUtil.executeQuery(hibernateTemplate, "from ReviewerQuestionMap where quesid=?", ids[i]);
        		   Iterator<ReviewerQuestionMap> irqm = rqm.iterator();
        		   while(irqm.hasNext()){
        			   hibernateTemplate.delete(irqm.next());
        		   }
        		   
        		   CompetencyQuestion fgmt = (CompetencyQuestion) hibernateTemplate.get(CompetencyQuestion.class, ids[i]);
        		   quename=fgmt.getQuesdesc();
        		   hibernateTemplate.delete(fgmt);
        	   }else{
        		   success = false;
        	   }
           }
       } catch (Exception e) {
           success = false;
           e.printStackTrace();
       }finally{
           return new KwlReturnObject(success, quename, "-1", null, 0);
       }
   }

}
