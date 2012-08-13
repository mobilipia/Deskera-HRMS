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
package com.krawler.spring.hrms.appraisal;

import com.krawler.common.admin.User;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.master.MasterData;
import com.krawler.hrms.performance.Appraisal;
import com.krawler.hrms.performance.AppraisalQuestionAnswers;
import com.krawler.hrms.performance.Appraisalcycle;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.AppraisalmanagementQuestionAnswers;
import com.krawler.hrms.performance.CompetencyQuestion;
import com.krawler.hrms.performance.Finalgoalmanagement;
import com.krawler.hrms.performance.Managecmpt;
import com.krawler.hrms.performance.competencyAvg;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.krawler.spring.hrms.anonymousappraisal.hrmsAnonymousAppraisalConstants;
import com.krawler.spring.hrms.appraisalcycle.hrmsAppraisalcycleDAO;
import java.util.Iterator;

public class hrmsAppraisalDAOImpl implements hrmsAppraisalDAO {

    private HibernateTemplate hibernateTemplate;
    private hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOobj;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public hrmsAppraisalcycleDAO getHrmsAppraisalcycleDAOobj() {
        return hrmsAppraisalcycleDAOobj;
    }

    public void setHrmsAppraisalcycleDAO(hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOobj) {
        this.hrmsAppraisalcycleDAOobj = hrmsAppraisalcycleDAOobj;
    }
    
    public KwlReturnObject getEmployeeAppraisals(HashMap<String, Object> requestParams) {
        boolean success = false;
        List lst = null;
        try {
            String employeeid = requestParams.get("employeeid").toString();
            String hql = "from Appraisalmanagement where employee.userID=?";
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{employeeid});
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", lst, lst.size());
        }
    }

    public Appraisalmanagement AppraisalAssign(HashMap<String, Object> requestParams) {
        Appraisalmanagement appm = new Appraisalmanagement();
        try {
            appm.setAppcycle((Appraisalcycle) requestParams.get("appcycle"));
            appm.setEmployee((User) requestParams.get("user"));
            appm.setManager((User) requestParams.get("manager"));
            appm.setEmployeestatus(Integer.parseInt(requestParams.get("empstatus").toString()));
            appm.setManagerstatus(Integer.parseInt(requestParams.get("mgrstatus").toString()));
            appm.setEmployeedraft(Integer.parseInt(requestParams.get("empdraft").toString()));
            appm.setManagerdraft(Integer.parseInt(requestParams.get("mgrdraft").toString()));
            appm.setAppraisalstatus(requestParams.get("apprstat").toString());
            appm.setReviewstatus(Integer.parseInt(requestParams.get("revstat").toString()));
            appm.setEmpdesid((MasterData) requestParams.get("empdesid"));
            appm.setManagerdesid((MasterData) requestParams.get("managerdesid"));
            Set<CompetencyQuestion> competencyQuestions = new HashSet<CompetencyQuestion>((List<CompetencyQuestion>) requestParams.get("competencyQuestions")); 
            appm.setCompetencyQuestions(competencyQuestions);
            hibernateTemplate.saveOrUpdate(appm);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return appm;
        }
    }

    public KwlReturnObject getAppraisal(HashMap<String, Object> requestParams) {
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
            ArrayList select = null;
            if (requestParams.containsKey("primary") && (Boolean) requestParams.get("primary")) {
                hql = "from Appraisal where appid=?";
                String id = requestParams.get("id").toString();
                lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
                result = new KwlReturnObject(success, "success", "", lst, lst.size());
                return result;
            }
            hql = "from Appraisal";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null) {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
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
                orderby = new ArrayList((List<String>) requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>) requestParams.get("order_type"));
                hql += com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            if(requestParams.get("select")!=null){
                select = new ArrayList((List<String>) requestParams.get("select"));
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

    public KwlReturnObject getcompetencyAvg(HashMap<String, Object> requestParams) {
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
            hql = "from competencyAvg";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null) {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }

            if (requestParams.get("searchcol") != null && requestParams.get("ss") != null) {
                searchCol = (String[]) requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null) {
                orderby = new ArrayList((List<String>) requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>) requestParams.get("order_type"));
                hql += com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            if(requestParams.get("select")!=null){
                select = new ArrayList((List<String>) requestParams.get("select"));
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

            public KwlReturnObject addcompetencyAvg(HashMap<String, Object> requestParams) {
            List <competencyAvg> list = new ArrayList<competencyAvg>();
            boolean success = false;
            try {
                competencyAvg comptavg = (competencyAvg) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "com.krawler.hrms.performance.competencyAvg", "Id");
                list.add(comptavg);
            }catch (Exception e) {
                success = false;
                System.out.println("Error is "+e);
            }finally{
                return new KwlReturnObject(success, "", "-1", list, list.size());
            }
        }

    public KwlReturnObject getAppraisalforAppCyc(HashMap<String, Object> requestParams) {
        boolean success = false;
        List lst = null;
        int count = 0;
        try {
            String id = requestParams.get("appid").toString();
            String hql = "from Appraisal where appraisal.appraisalid = ? and competency is not null";
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
            count = lst.size();

        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", lst, count);
        }
    }

    public KwlReturnObject getCompAvgScore(HashMap<String, Object> requestParams) {
        boolean success = false;
        List lst = null;
        try {
            if (requestParams.containsKey("globalavg") && (Boolean) requestParams.get("globalavg")) {
                String id = requestParams.get("appid").toString();
                String userid = requestParams.get("userid").toString();
                String qry = "select avg(manavg) from competencyAvg where appcycle.id=? and employee.userID=? ";
                lst = HibernateUtil.executeQuery(hibernateTemplate, qry, new Object[]{id, userid});

            } else if (requestParams.containsKey("compavg") && (Boolean) requestParams.get("compavg")) {
                String id = requestParams.get("appid").toString();
                String compid = requestParams.get("compid").toString();
                String qry = "select avg(manavg) from competencyAvg where appcycle.id=? and competency.mastercmpt.cmptid=? ";
                lst = HibernateUtil.executeQuery(hibernateTemplate, qry, new Object[]{id, compid});

            } else if (requestParams.containsKey("avg") && (Boolean) requestParams.get("avg")) {
                Appraisalcycle appcyc = (Appraisalcycle) requestParams.get("appcyc");
                String compid = requestParams.get("compid").toString();
                User emp = (User) requestParams.get("emp");
                String qry = "from competencyAvg where appcycle = ? and competency.mid = ? and employee = ?";
                lst = HibernateUtil.executeQuery(hibernateTemplate, qry, new Object[]{appcyc, compid, emp});
            }
        } catch (Exception ex) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", lst, 0);
        }
    }

    public KwlReturnObject appraisalFunction(HashMap<String, Object> requestParams) {
        boolean success = false;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            Appraisalmanagement appmanage = null;
            Appraisal app = null;
            String appid;
            if (requestParams.get("rateperformance") == null) {
                String jsondata1 = requestParams.get("jsoncompetency").toString();
                String jsondata2 = requestParams.get("jsongoal").toString();

                appmanage = (Appraisalmanagement) requestParams.get("appmanage");
                appid = requestParams.get("appid").toString();
                if (Boolean.parseBoolean(requestParams.get("employee").toString())) {
                    appmanage.setEmployeecomment(requestParams.get("empcomment").toString());
                    if (!Boolean.parseBoolean(requestParams.get("saveasDraft").toString())) {
                        appmanage.setEmployeestatus(1);
                        appmanage.setEmployeedraft(0);
                    } else {
                        appmanage.setEmployeedraft(1);
                    }
                    appmanage.setEmployeecompscore(Double.parseDouble(requestParams.get("competencyscore").toString()));
                    appmanage.setEmployeegoalscore(Double.parseDouble(requestParams.get("goalscore").toString()));
                    appmanage.setEmployeegapscore(Double.parseDouble(requestParams.get("compgapscore").toString()));
                    appmanage.setEmployeesubmitdate((Date) fmt.parse(requestParams.get("submitdate").toString()));
                } else {
                    appmanage.setManagercomment(requestParams.get("mancomment").toString());
                    if (!Boolean.parseBoolean(requestParams.get("saveasDraft").toString())) {
                        appmanage.setManagerstatus(1);
                        appmanage.setManagerdraft(0);
                        appmanage.setAppraisalstatus("submitted");

                    } else {
                        appmanage.setAppraisalstatus("pending");
                        appmanage.setManagerdraft(1);
                    }
                    appmanage.setReviewstatus(0);
                    if (!StringUtil.isNullOrEmpty(requestParams.get("performance").toString())) {
                        appmanage.setPerformance((MasterData) hibernateTemplate.load(MasterData.class, requestParams.get("performance").toString()));
                    }
                    appmanage.setManagercompscore(Double.parseDouble(requestParams.get("competencyscore").toString()));
                    appmanage.setManagergoalscore(Double.parseDouble(requestParams.get("goalscore").toString()));
                    appmanage.setManagergapscore(Double.parseDouble(requestParams.get("compgapscore").toString()));
                    appmanage.setManagersubmitdate((Date) fmt.parse(requestParams.get("submitdate").toString()));
                    if (Boolean.parseBoolean(requestParams.get("salarychange").toString())) {
                        appmanage.setSalaryrecommend(1);
                        if (requestParams.get("newdesignation") != null && !StringUtil.isNullOrEmpty(requestParams.get("newdesignation").toString())) {
                            appmanage.setNewdesignation((MasterData) hibernateTemplate.load(MasterData.class, requestParams.get("newdesignation").toString()));
                        } else {
                            appmanage.setNewdesignation(null);
                        }
                        if (requestParams.get("newdepartment") != null && !StringUtil.isNullOrEmpty(requestParams.get("newdepartment").toString())) {
                            appmanage.setNewdepartment((MasterData) hibernateTemplate.load(MasterData.class, requestParams.get("newdepartment").toString()));
                        } else {
                            appmanage.setNewdepartment(null);
                        }
                        if (requestParams.get("salaryincrement") != null && !StringUtil.isNullOrEmpty(requestParams.get("salaryincrement").toString())) {
                            appmanage.setSalaryincrement(Float.parseFloat(requestParams.get("salaryincrement").toString()));
                        } else {
                            appmanage.setSalaryincrement(0);
                        }
                    } else {
                        appmanage.setSalaryrecommend(0);
                    }
                }
                if (appmanage.getEmployeestatus() == 1 && appmanage.getManagerstatus() == 1) {
                    appmanage.setAppraisalstatus("submitted");
                    appmanage.setReviewstatus(0);
                    appmanage.setTotalscore((appmanage.getEmployeegoalscore() + appmanage.getEmployeecompscore() + appmanage.getManagergoalscore() + appmanage.getManagercompscore()) / 4);
                }
                hibernateTemplate.save(appmanage);
                List cmplst = (List) requestParams.get("complist");
                JSONArray jarr = new JSONArray(jsondata1);
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobj = jarr.getJSONObject(i);
                    if (cmplst.size() > 0) {
                        app = (Appraisal) cmplst.get(i);
                    } else {
                        app = new Appraisal();
                    }
                    if (Boolean.parseBoolean(requestParams.get("employee").toString())) {
                        if (!StringUtil.isNullOrEmpty(jobj.getString("compemprate"))) {
                            app.setCompemprating(Double.parseDouble(jobj.getString("compemprate")));
                        } else {
                            app.setCompemprating(0);
                        }
                        app.setCompempgap(Double.parseDouble(jobj.getString("compempgap")));
                        app.setCompempcomment(jobj.getString("compempcomment"));
                    } else {
                        if (!StringUtil.isNullOrEmpty(jobj.getString("compmanrate"))) {
                            app.setCompmanrating(Double.parseDouble(jobj.getString("compmanrate")));
                        } else {
                            app.setCompmanrating(0);
                        }
                        app.setCompmangap(Double.parseDouble(jobj.getString("compmangap")));
                        app.setCompmancomment(jobj.getString("compmancomment"));
                    }
                    app.setAppraisal((Appraisalmanagement) hibernateTemplate.load(Appraisalmanagement.class, appid));
                    app.setCompetency((Managecmpt) hibernateTemplate.load(Managecmpt.class, jobj.getString("mid")));
                    hibernateTemplate.save(app);
                }

                jarr = new JSONArray(jsondata2);
                for (int j = 0; j < jarr.length(); j++) {
                    JSONObject jobj = jarr.getJSONObject(j);
                    if (StringUtil.isNullOrEmpty(jobj.getString("id"))) {
                        app = new Appraisal();
                    } else {
                        app = (Appraisal) hibernateTemplate.load(Appraisal.class, jobj.getString("id"));
                    }
                    if (Boolean.parseBoolean(requestParams.get("employee").toString())) {
                        if (StringUtil.equal(appid, jobj.getString("goalapprid"))) {
                            if (!StringUtil.isNullOrEmpty(jobj.getString("goalemprate"))) {
                                app.setGoalemprating(Double.parseDouble(jobj.getString("goalemprate")));
                            } else {
                                app.setGoalemprating(0);
                            }
                            app.setGoalempcomment(jobj.getString("goalempcomment"));
                            app.setAppraisal((Appraisalmanagement) hibernateTemplate.load(Appraisalmanagement.class, appid));
                            app.setGoal((Finalgoalmanagement) hibernateTemplate.load(Finalgoalmanagement.class, jobj.getString("goalid")));
                            hibernateTemplate.save(app);
                        }
                    } else {
                        if (!StringUtil.isNullOrEmpty(jobj.getString("goalmanrate"))) {
                            app.setGoalmanrating(Double.parseDouble(jobj.getString("goalmanrate")));
                        } else {
                            app.setGoalmanrating(0);
                        }
                        app.setGoalmancomment(jobj.getString("goalmancomment"));
                        app.setAppraisal((Appraisalmanagement) hibernateTemplate.load(Appraisalmanagement.class, appid));
                        app.setGoal((Finalgoalmanagement) hibernateTemplate.load(Finalgoalmanagement.class, jobj.getString("goalid")));
                        hibernateTemplate.save(app);
                    }
                }
            } else {

                MasterData perfo = (MasterData) hibernateTemplate.load(MasterData.class, requestParams.get("perf").toString());
                appmanage = (Appraisalmanagement) requestParams.get("appmanage");
                appmanage.setPerformance(perfo);
                appmanage.setAppraisalstatus("submitted");
                appmanage.setReviewstatus(0);

                hibernateTemplate.update(appmanage);
            }
        } catch (Exception e) {
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", null, 0);
        }
    }

    public String checkEntry(String empid, String manid, String quesid, String appid) {
        String returnObj = "";
        List lst = null;
        int count = 0;
        try {
            String hql = "from AppraisalmanagementQuestionAnswers where employee.userID and ";
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{empid, manid, quesid, appid});
            if (lst.size() > 0) {

            }
        } catch(Exception e){

        }
        return returnObj;
    }

    public KwlReturnObject saveAnswers(HashMap<String, Object> requestParams) {
        boolean success=false;
        KwlReturnObject result = null;
        try {
            String apprmanager = "";
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            AppraisalmanagementQuestionAnswers fgmt = null;
            String jsondata = requestParams.get("jsondata").toString();
            boolean isEmployee = Boolean.parseBoolean(requestParams.get("isemployee").toString());
            JSONArray jarr = new JSONArray(jsondata);
            String id = UUID.randomUUID().toString();
            Appraisalmanagement appmanage = null;
            User employeeuser = (User) hibernateTemplate.load(User.class, requestParams.get("empid").toString());
            User manageruser = (User) hibernateTemplate.load(User.class, requestParams.get("manid").toString());
            String checkObj="";
            if(!isEmployee){
                appmanage = (Appraisalmanagement) hibernateTemplate.load(Appraisalmanagement.class, requestParams.get("appraisalid").toString());
            }
            List<AppraisalmanagementQuestionAnswers> list = null;
            HashMap<String, Integer> questionmap = new HashMap<String, Integer>();
            HashMap<String, Object> questionrequestParams = new HashMap<String, Object>();
            ArrayList filter_params = new ArrayList();
            ArrayList filter_names = new ArrayList();

            filter_names.add(hrmsAnonymousAppraisalConstants.appcycleId);
            filter_params.add(requestParams.get("appcycle").toString());

            filter_names.add(hrmsAnonymousAppraisalConstants.employeeUserID);
            filter_params.add(requestParams.get("empid").toString());

            filter_names.add(hrmsAnonymousAppraisalConstants.managerUserID);
            filter_params.add(requestParams.get("manid").toString());

            questionrequestParams.put(hrmsAnonymousAppraisalConstants.filter_names, filter_names);
            questionrequestParams.put(hrmsAnonymousAppraisalConstants.filter_params, filter_params);
            result = hrmsAppraisalcycleDAOobj.getQuestionAnswerGrid(questionrequestParams);
            list = result.getEntityList();
            int k=0;
            for (AppraisalmanagementQuestionAnswers AppraisalmanagementQuestionAnswersobj : list) {
                    questionmap.put(AppraisalmanagementQuestionAnswersobj.getCmptquestion().getQuesid(),k++);
            }
            
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj = jarr.getJSONObject(i);
              //  checkObj = checkEntry(requestParams.get("empid").toString(),requestParams.get("manid").toString(),jobj.getString("quesid"),requestParams.get("appcycle").toString());
                Set<AppraisalQuestionAnswers> appraisalquestionanswers =new HashSet<AppraisalQuestionAnswers>(0);
                if(questionmap.containsKey(jobj.getString("quesid"))){
                    fgmt = list.get(questionmap.get(jobj.getString("quesid")));
                    appraisalquestionanswers = fgmt.getAppraisalquestionanswers();
                    hibernateTemplate.deleteAll(appraisalquestionanswers);
                }else{
                    fgmt = new AppraisalmanagementQuestionAnswers();
                }
                fgmt.setEmployee(employeeuser);
                fgmt.setManager(manageruser);
                fgmt.setCmptquestion((CompetencyQuestion) hibernateTemplate.load(CompetencyQuestion.class, jobj.getString("quesid")));
                fgmt.setAppcycle((Appraisalcycle) hibernateTemplate.load(Appraisalcycle.class, requestParams.get("appcycle").toString()));
                JSONArray ansjarr = jobj.getJSONArray("quesans");
                appraisalquestionanswers=new HashSet<AppraisalQuestionAnswers>(0);
                for (int j = 0; j < ansjarr.length(); j++) {
                    AppraisalQuestionAnswers appraisalquestionanswersobj = new AppraisalQuestionAnswers();
                    appraisalquestionanswersobj.setAnswer(ansjarr.getJSONObject(j).getString("ans"+j));
                    appraisalquestionanswersobj.setOrderofans(j);
                    appraisalquestionanswersobj.setAppraisalmanagementquestionanswers(fgmt);
                    if(!isEmployee){
                        appraisalquestionanswersobj.setAppraisal(appmanage);
                    }
                    appraisalquestionanswers.add(appraisalquestionanswersobj);
                }
                fgmt.setAppraisalquestionanswers(appraisalquestionanswers);
                hibernateTemplate.saveOrUpdate(fgmt);
            }
            if(!isEmployee){
                //appmanage.setSalaryrecommend(0);
                appmanage.setManagercomment(requestParams.get("mancomment").toString());
                if (!Boolean.parseBoolean(requestParams.get("saveasDraft").toString())) {
                    appmanage.setManagerstatus(1);
                    appmanage.setManagerdraft(0);
                    appmanage.setAppraisalstatus("submitted");

                } else {
                    appmanage.setAppraisalstatus("pending");
                    appmanage.setManagerdraft(1);
                }
                appmanage.setReviewstatus(0);
                if (!StringUtil.isNullOrEmpty(requestParams.get("performance").toString())) {
                    appmanage.setPerformance((MasterData) hibernateTemplate.load(MasterData.class, requestParams.get("performance").toString()));
                }
                appmanage.setManagersubmitdate((Date) fmt.parse(requestParams.get("submitdate").toString()));
            } else {
//                ArrayList al = (ArrayList) requestParams.get("commentArray");
//                for(int g=0;g<al.size();g++){
//                    Appraisalmanagement amanage = (Appraisalmanagement) hibernateTemplate.load(Appraisalmanagement.class, (Serializable) al.get(g));
//                    amanage.setEmployeecomment(checkObj);
                    Iterator ite = (Iterator) requestParams.get("ite");
                    hibernateTemplate.save(ite);
//                }
            }
            success=true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            return new KwlReturnObject(success, "", "-1", null, 0);
        }
    }
    
    @Override
    public List<Appraisalmanagement> getAppraisalmanagements(HashMap<String, Object> requestParams) {
        List<Appraisalmanagement>  appraisalmanagements= null;
        ArrayList<String> name = null;	
        ArrayList<Object> value = null;
        try {
            String hql = "from Appraisalmanagement";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null) {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }
            appraisalmanagements = HibernateUtil.executeQuery(hibernateTemplate, hql, value.toArray());
        } catch(Exception e){
        	e.printStackTrace();
        }
        return appraisalmanagements;
    }

    public List<CompetencyQuestion> getCompetencyQuestions(HashMap<String, Object> requestParams){
    	List<CompetencyQuestion> competencyQuestions = null;
        try {
        	MasterData empdes = (MasterData) requestParams.get("empdesid");
        	String hql = null;
        	hql = "from CompetencyQuestion cq where cq.quesid in (select rqm.cmptquestion from ReviewerQuestionMap rqm where rqm.empdesid.id = ? ) and cq.visible = ?";
        	competencyQuestions = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{empdes.getId(), true});
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            return competencyQuestions;
        }
    }
}
