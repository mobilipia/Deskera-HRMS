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
package com.krawler.spring.hrms.appraisalcycle.bizservice;

import com.krawler.common.util.BuildCriteria;
import com.krawler.common.util.StringUtil;
import com.krawler.hrms.performance.AppraisalQuestionAnswers;
import com.krawler.hrms.performance.Appraisalmanagement;
import com.krawler.hrms.performance.AppraisalmanagementQuestionAnswers;
import com.krawler.hrms.performance.CompetencyQuestion;
import com.krawler.hrms.performance.QuestionGroup;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.hrms.anonymousappraisal.hrmsAnonymousAppraisalConstants;
import com.krawler.spring.hrms.appraisalcycle.hrmsAppraisalcycleDAO;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.sun.org.apache.xpath.internal.axes.HasPositionalPredChecker;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class hrmsAppraisalcycleserviceImpl implements hrmsAppraisalcycleservice {

    private hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj;

    public void setHrmsAppraisalcycleDAOObj(hrmsAppraisalcycleDAO hrmsAppraisalcycleDAOObj) {
        this.hrmsAppraisalcycleDAOObj = hrmsAppraisalcycleDAOObj;
    }

    @Override
    public KwlReturnObject getQuestionAnswerGrid(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        List<AppraisalmanagementQuestionAnswers> list = null;
        result = hrmsAppraisalcycleDAOObj.getQuestionAnswerGrid(requestParams);
        list = result.getEntityList();
        boolean export = false;
        if (requestParams.get(hrmsAnonymousAppraisalConstants.export) != null) {
            export = Boolean.parseBoolean(requestParams.get(hrmsAnonymousAppraisalConstants.export).toString());
        }
        List<HashMap> listmap = new ArrayList<HashMap>();

        if (!list.isEmpty()) {

            HashMap<String, List> jsonmap = new HashMap<String, List>();
            HashMap<String, List> employeejsonmap = new HashMap<String, List>();
            LinkedHashMap<String, String> questionjsonmap = new LinkedHashMap<String, String>();    // Used linkedhashmap as needed to keep order of added questions
            LinkedHashMap<String, String> questionorderjsonmap = new LinkedHashMap<String, String>();
            for (AppraisalmanagementQuestionAnswers AppraisalmanagementQuestionAnswersobj : list) {
                Boolean isemployeeanswer = AppraisalmanagementQuestionAnswersobj.getEmployee().getUserID().equals(AppraisalmanagementQuestionAnswersobj.getManager().getUserID());
                CompetencyQuestion Cmptquestionobj = AppraisalmanagementQuestionAnswersobj.getCmptquestion();
                List<String> answer = null;
                List<List> ls = null;
                Set<AppraisalQuestionAnswers> appraisalquestionanswers = AppraisalmanagementQuestionAnswersobj.getAppraisalquestionanswers();
                if (appraisalquestionanswers != null) {
                    String questionid = Cmptquestionobj.getQuesid();
                    
                    if (!isemployeeanswer && jsonmap.containsKey(questionid)) {
                        ls = jsonmap.get(questionid);
                    } else {
                        ls = new ArrayList<List>();
                    }
                    
                    int i = 0;
                    for (AppraisalQuestionAnswers AppraisalQuestionAnswersobj : appraisalquestionanswers) {
                        boolean isalreadyexist = (ls.size() > i);
                        if (isalreadyexist) {
                            answer = ls.get(i);
                        }else{
                            answer = new ArrayList();
                        }
                        ++i;

                        answer.add("" + AppraisalQuestionAnswersobj.getAnswer());
                        
                        if (!isalreadyexist) {
                            ls.add(answer);
                        }
                    }
                    questionjsonmap.put(Cmptquestionobj.getQuesid(), Cmptquestionobj.getQuesdesc());
                    questionorderjsonmap.put(Cmptquestionobj.getQuesid(), String.valueOf(Cmptquestionobj.getQuesorder()));

                    if (isemployeeanswer) {
                        employeejsonmap.put(Cmptquestionobj.getQuesid(), ls);
                    } else {
                        jsonmap.put(Cmptquestionobj.getQuesid(), ls);
                    }
                }
            }
        createJsonMapFromList(listmap, jsonmap, employeejsonmap, questionjsonmap, questionorderjsonmap, export);
        }
        return new KwlReturnObject(true, hrmsAnonymousAppraisalConstants.question_success, " ", listmap, listmap.size());

    }
    
    private void createJsonMapFromList(List<HashMap> listmap,
            HashMap<String, List> jsonmap,HashMap<String, List> employeejsonmap,HashMap<String, String> questionjsonmap,HashMap<String, String> questionorderjsonmap
            ,Boolean export){
            Integer outputtype = export?1:2;
            HashMap<String, String> Managerjsonmap=new HashMap<String, String>();
            HashMap<String, String> Employeejsonmap=new HashMap<String, String>();;
            for (String key : questionjsonmap.keySet()) {
                List<List> ls = jsonmap.get(key);
                processList(ls, Managerjsonmap, key, outputtype);
                ls = employeejsonmap.get(key);
                processList(ls, Employeejsonmap, key, outputtype);
            }
            if (questionjsonmap.size() > 0) {
                listmap.add(Managerjsonmap);
                listmap.add(questionjsonmap);
                listmap.add(Employeejsonmap);
                listmap.add(questionorderjsonmap);
            }

    }

    private void processList(List<List> ls, HashMap<String, String> jsonmap, String key, Integer outputtype) {
        if (ls != null) {
            String break_string = hrmsAnonymousAppraisalConstants.html_break;
            for (List<String> answers : ls) {
                if (answers != null) {
                    StringBuffer s = new StringBuffer();
                       s.append("<ol>");
                    Collections.shuffle(answers);
                    int i=0;
                    for (String s1 : answers) {
                        if(!StringUtil.isNullOrEmpty(s1)) {
                            s.append("<li>");
                            s.append(s1);
                            s.append("</li>");
                        }
                    }

                    if (jsonmap.containsKey(key)) {
                        StringBuffer s1 = new StringBuffer(jsonmap.get(key));
                        s1.append(s);
                        s = s1;
                    }
                    s.append("</ol>");
                    s.append(break_string);

                    jsonmap.put(key, s.toString());
                }
            }
        }
    }
    
    @Override
    public KwlReturnObject getQuestionAnswerForm(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        List<AppraisalmanagementQuestionAnswers> list = null;
        result = hrmsAppraisalcycleDAOObj.getQuestionAnswerGrid(requestParams);
        list = result.getEntityList();
        List<HashMap<String, String>> listmap = new ArrayList<HashMap<String, String>>();

        if (!list.isEmpty()) {

            HashMap<String, String> jsonmap = new HashMap<String, String>();
            HashMap<String, String> questionjsonmap = new HashMap<String, String>();
            for (AppraisalmanagementQuestionAnswers AppraisalmanagementQuestionAnswersobj : list) {

                CompetencyQuestion Cmptquestionobj = AppraisalmanagementQuestionAnswersobj.getCmptquestion();

                Set<AppraisalQuestionAnswers> appraisalquestionanswers = AppraisalmanagementQuestionAnswersobj.getAppraisalquestionanswers();
                if (appraisalquestionanswers != null) {
                    int i = 0;
                    for (AppraisalQuestionAnswers AppraisalQuestionAnswersobj : appraisalquestionanswers) {
                        jsonmap.put(Cmptquestionobj.getQuesid()+(i++), AppraisalQuestionAnswersobj.getAnswer());
                    }
                    questionjsonmap.put(Cmptquestionobj.getQuesid(), String.valueOf(i));
                }
            }
            if (jsonmap.size() > 0) {
                listmap.add(jsonmap);
                listmap.add(questionjsonmap);
            }

        }
        return new KwlReturnObject(true, "Question are fetched successfully", " ", listmap, listmap.size());

    }

    
    @Override
    public JSONObject getQuestionAnswerGridJson(List<HashMap<String, String>> list, String groupID, Appraisalmanagement appraisalmanagement) throws JSONException {
        int count = 0;
        JSONObject jobj = new JSONObject();
        JSONArray cmpObj = new JSONArray();
        StringBuffer s = new StringBuffer();
        JSONArray jArr1 =  new JSONArray();
        if (!list.isEmpty()) {
            HashMap<String, String> jsonmap = list.get(0);
            HashMap<String, String> questionjsonmap = list.get(1);
            HashMap<String, String> employeejsonmap = list.get(2);
            HashMap<String, String> questionorderjsonmap = list.get(3);
            count = questionjsonmap.size();
            if (questionjsonmap.size() > 0) {
                for (String key : questionjsonmap.keySet()) {
                    s.append(key);
                    s.append(",");
                    JSONObject appObj = new JSONObject();
                    appObj.put(hrmsAnonymousAppraisalConstants.question, questionjsonmap.get(key));
                    appObj.put(hrmsAnonymousAppraisalConstants.answer, jsonmap.containsKey(key) ? jsonmap.get(key) : "");
                    appObj.put(hrmsAnonymousAppraisalConstants.employeeanswer, employeejsonmap.containsKey(key) ? employeejsonmap.get(key) : "");
                    cmpObj.put(Integer.parseInt(questionorderjsonmap.get(key))-1, appObj);
                }
                s.deleteCharAt(s.length()-1);
            }
            KwlReturnObject result = null;
            List<QuestionGroup> list1 = null;
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add(BuildCriteria.OPERATORNOTIN+"cmptquestion.quesid");
            filter_names.add("groupid");

            filter_params.add(s);
            filter_params.add(groupID);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            result = hrmsAppraisalcycleDAOObj.getQuestions(requestParams);
            list1 = result.getEntityList();
            
            Set<CompetencyQuestion> competencyQuestions = appraisalmanagement.getCompetencyQuestions();
        	Iterator<CompetencyQuestion> iterator = competencyQuestions.iterator();
        	Set<String> questions = new HashSet<String>();
        	while (iterator.hasNext()) {
				questions.add(iterator.next().getQuesid());	
			}
            if (!list1.isEmpty()) {
                Iterator <QuestionGroup> ite1 = list1.iterator();
                while (ite1.hasNext()) {
                    QuestionGroup app1 =  ite1.next();
                    if(questions.contains(app1.getCmptquestion().getQuesid())){
                    	JSONObject appObj = new JSONObject();
                    	appObj.put(hrmsAnonymousAppraisalConstants.question, app1.getCmptquestion().getQuesdesc());
                    	cmpObj.put(app1.getCmptquestion().getQuesorder()-1,appObj);
                    }
                }
            }
            for(int i =0;i<cmpObj.length();i++) {
                if(!cmpObj.isNull(i)) {
                    jArr1.put(cmpObj.get(i));
                }
            }
        }
        jobj.put(hrmsAnonymousAppraisalConstants.data, jArr1);
        jobj.put(hrmsAnonymousAppraisalConstants.totalCount, count);
        return jobj;
    }
}
