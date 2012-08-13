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

package com.krawler.spring.hrms.masterconfig;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.CostCenter;
import com.krawler.common.util.Constants;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.master.Master;
import com.krawler.hrms.master.MasterData;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class hrmsMasterConfigDAOImpl implements hrmsMasterConfigDAO{

    private HibernateTemplate hibernateTemplate;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}


    public KwlReturnObject getMasterField() {
        boolean success=false;
        List lst=null;
        try {
            String hql = "from Master where deleted=? ";
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql,false);
            success=true;
        } catch (Exception e) {
            success=false;
            e.printStackTrace();
        }
        return new KwlReturnObject(success, "", "-1", lst, lst.size());
    }

    public KwlReturnObject getCompanyInformation(HashMap<String, Object> requestParams) {
        boolean success = false;
        List lst= null;
        try{
            String hql = "select c,cpr from CompanyPreferences c right outer join c.company cpr where cpr.companyID=?";
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, requestParams.get("companyid").toString());
            success=true;
        } catch (Exception e) {
            success=false;
            e.printStackTrace();
        }
        return new KwlReturnObject(success, "", "-1", lst, lst.size());

    }

    public KwlReturnObject addMasterDataField(HashMap<String, Object> requestParams) {
        String goalid = "";
        boolean success=false;
        List lst = new ArrayList();
        try {
            String configid = requestParams.get("configid").toString();
            Master master = (Master) hibernateTemplate.load(Master.class, Integer.parseInt(configid));
            MasterData gmst;

            if (requestParams.get("action").toString().equals("Add")) {
                gmst = new MasterData();
                gmst.setValue(requestParams.get("name").toString());
                gmst.setMasterid(master);
                gmst.setCompany((Company) hibernateTemplate.get(Company.class,requestParams.get("companyid").toString()));
                if(configid.equals(Constants.payComponent_CONFIGID)) {//Component sub type
                    gmst.setComponenttype(Integer.parseInt(requestParams.get("weightage").toString()));
                }else if(configid.equals(Constants.timesheetjob_CONFIGID)) {//Timesheet Job
                    gmst.setWorktime(requestParams.get("weightage").toString());
                } else {
                    gmst.setWeightage(Integer.parseInt(requestParams.get("weightage").toString()));
                }
                hibernateTemplate.save(gmst);
                lst.add(gmst.getValue());
                lst.add(master.getName());
            } else {
                goalid = requestParams.get("id").toString();
                gmst = (MasterData) hibernateTemplate.load(MasterData.class, goalid);
                gmst.setValue(requestParams.get("name").toString());
                gmst.setMasterid(master);
                if(configid.equals(Constants.payComponent_CONFIGID)) {//Component sub type
                    gmst.setComponenttype(Integer.parseInt(requestParams.get("weightage").toString()));
                }else if(configid.equals(Constants.timesheetjob_CONFIGID)) {//Timesheet Job
                    gmst.setWorktime(requestParams.get("weightage").toString());
                } else {
                    gmst.setWeightage(Integer.parseInt(requestParams.get("weightage").toString()));
                }
                hibernateTemplate.save(gmst);
                lst.add(gmst.getValue());
                lst.add(master.getName());
            }
            success=true;
            //@@ProfileHandler.insertAuditLog(session, auditID, "User <b>"+fullName+"</b> "+auditMsg+"master item <b>"+gmst.getValue()+"</b> of master group <b>"+master.getName()+".</b>", request);
        } catch (Exception e) {
            success=false;
            e.printStackTrace();
        }
        return new KwlReturnObject(success, "", "-1", lst, 0);
    }

    public KwlReturnObject addCostCenterField(HashMap<String, Object> requestParams) {
        String goalid = "";
        boolean success=false;
        List lst = new ArrayList();
        try {
            CostCenter costcenter = (CostCenter) hibernateTemplate.load(CostCenter.class, requestParams.get("id").toString().equals("0")?"":requestParams.get("id").toString());
            CostCenter gmst;
            if (requestParams.get("action").toString().equals("Add")) {
                gmst = new CostCenter();
                gmst.setName(requestParams.get("name").toString());
                gmst.setCode(requestParams.get("code").toString());
                gmst.setCompany((Company) hibernateTemplate.get(Company.class,requestParams.get("companyid").toString()));
                gmst.setCreationDate(new java.util.Date());
                hibernateTemplate.save(gmst);
                lst.add(costcenter.getName());
            } else {
                goalid = requestParams.get("id").toString();
                gmst = (CostCenter) hibernateTemplate.load(CostCenter.class, goalid);
                gmst.setName(requestParams.get("name").toString());
                gmst.setCode(requestParams.get("code").toString());
                gmst.setCompany((Company) hibernateTemplate.get(Company.class,requestParams.get("companyid").toString()));
                hibernateTemplate.save(gmst);
                lst.add(costcenter.getName());
            }
            success=true;
            //@@ProfileHandler.insertAuditLog(session, auditID, "User <b>"+fullName+"</b> "+auditMsg+"master item <b>"+gmst.getValue()+"</b> of master group <b>"+master.getName()+".</b>", request);
        } catch (Exception e) {
            success=false;
            e.printStackTrace();
        }
        return new KwlReturnObject(success, "", "-1", lst, 0);
    }



     public KwlReturnObject deleteCostcenter(HashMap<String, Object> requestParams) {
        boolean success=false;
        List lst= new ArrayList();
        int count=0;
        try {
            String ids[] = (String[]) requestParams.get("ids");
            String groupName="";
            count = ids.length;
            for (int i = 0; i < ids.length; i++) {
//            	List recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, "from  where revdesid.id=?", new Object[]{ids[i]});
//            	if(recordTotalCount.size()==0){
            		CostCenter mdata=(CostCenter) hibernateTemplate.load(CostCenter.class,ids[i] );
                    groupName=mdata.getName();
                    hibernateTemplate.delete(mdata);
                    success=true;
//            	}
            }
            lst.add(groupName);
        } catch (Exception e) {
            e.printStackTrace();
            success=false;
        }
        return new KwlReturnObject(success, "", "-1", lst, count);
    }

    public KwlReturnObject deletemasterdata(HashMap<String, Object> requestParams) {
        boolean success=false;
        List lst= new ArrayList();
        int count=0;
        int masterid =0;
        try {
            String ids[] = (String[]) requestParams.get("ids");
            String groupName="";
            count = ids.length;
            List recordTotalCount = new ArrayList();
            for (int i = 0; i < ids.length; i++) {
            	MasterData mdata=(MasterData) hibernateTemplate.load(MasterData.class,ids[i] );
            	masterid= mdata.getMasterid().getId();
            	if(masterid==1){
            		recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, "from ReviewerQuestionMap where revdesid.id=?", new Object[]{ids[i]});	
            	}else if(masterid==25){
            		recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, "from Timesheet where jobtype=?", new Object[]{ids[i]});
        		}
            	if(recordTotalCount.size()==0){
        			groupName=mdata.getMasterid().getName();
        			hibernateTemplate.delete(mdata);
        			success=true;
        		}
            }
            lst.add(groupName);
        } catch (Exception e) {
            e.printStackTrace();
            success=false;
        }
        return new KwlReturnObject(success, masterid+"", "-1", lst, count);
    }

    public KwlReturnObject setEmpIdFormat(HashMap<String, Object> requestParams) {

        boolean success=false;
        KwlReturnObject result=null;
        try {
//            String cmpid=AuthHandler.getCompanyid(request);
//            CompanyPreferences cmpPref=null;
            Company company = (Company) hibernateTemplate.load(Company.class,requestParams.get("companyid").toString());
            CompanyPreferences cmpPref = (CompanyPreferences) hibernateTemplate.load(CompanyPreferences.class,requestParams.get("companyid").toString());
            cmpPref.setEmpidformat(requestParams.get("employeeidformat").toString());
            cmpPref.setJobidformat(requestParams.get("jobidformat").toString());
            cmpPref.setFinancialmonth(Integer.parseInt(requestParams.get("financialmonth").toString()));
            cmpPref.setWeeklyholidays(Integer.parseInt(requestParams.get("weeklyholiday").toString()));
            cmpPref.setSubscriptionCode(Long.parseLong(requestParams.get("subcription").toString()));
            cmpPref.setEmailNotification(requestParams.get("emailNotification").toString());
            cmpPref.setApprovesalary(Boolean.parseBoolean(requestParams.get("approvesalary").toString()));
            cmpPref.setHolidaycode(Integer.parseInt(requestParams.get("holidaycode").toString()));
            hibernateTemplate.saveOrUpdate(company);

            requestParams.put("company",company);
            result=setCompanyModules(requestParams);
            if(result.isSuccessFlag()){
                success=true;
            } else {
                success=false;
            }
        } catch (Exception e) {
            success=false;
            e.printStackTrace();
        }

        return new KwlReturnObject(success, "", "-1", null, 0);

    }

    public KwlReturnObject setCompanyModules(HashMap<String, Object> requestParams) {

        boolean success = false;
        List lst = new ArrayList();
        try {
            String cmpid=requestParams.get("companyid").toString();
            String query="from CompanyPreferences where company.companyID=?";
            List   tabledata = HibernateUtil.executeQuery(hibernateTemplate, query,cmpid);
            CompanyPreferences cmpPref=null;
            if(tabledata.size()==0){
               cmpPref=new CompanyPreferences();
               cmpPref.setCompany((Company) requestParams.get("company"));
            }else{
                cmpPref=(CompanyPreferences) hibernateTemplate.get(CompanyPreferences.class,cmpid);
            }
              if(requestParams.containsKey("selfappraisal")){
                  cmpPref.setSelfappraisal(true);
              }else{
                     cmpPref.setSelfappraisal(false);
              }
              if(requestParams.containsKey("competencies")){
                  cmpPref.setCompetency(true);
              }else{
                     cmpPref.setCompetency(false);
              }
              if(requestParams.containsKey("timesheetjob")){
                 cmpPref.setTimesheetjob(true);
              }else{
                 cmpPref.setTimesheetjob(false);
              }
              
	          if(requestParams.containsKey("blockemployees")){
	              cmpPref.setBlockemployees(true);
	          }else{
	             cmpPref.setBlockemployees(false);
	          }
              
              if(requestParams.containsKey("goals")){
                  cmpPref.setGoal(true);
              }else{
                     cmpPref.setGoal(false);
              }
              if(requestParams.containsKey("annmng")){
                  cmpPref.setAnnmanager(true);
              }else{
                     cmpPref.setAnnmanager(false);
              }
              if(requestParams.containsKey("approveappr")){
                  cmpPref.setApproveappraisal(true);
              }else{
                     cmpPref.setApproveappraisal(false);
              }
             if(requestParams.containsKey("promotionrec")){
                  cmpPref.setPromotion(true);
              }else{
                     cmpPref.setPromotion(false);
              }
              if(requestParams.containsKey("weightage")){
                  cmpPref.setWeightage(true);
              }else{
                     cmpPref.setWeightage(false);
              }
              if(requestParams.containsKey("reviewappraisal")){
                  cmpPref.setReviewappraisal(true);
              }else{
                     cmpPref.setReviewappraisal(false);
              }
              if(requestParams.containsKey("partial")){
                  cmpPref.setPartial(true);
              }else{
                  cmpPref.setPartial(false);
              }
              if(requestParams.containsKey("fullupdates")){
                  cmpPref.setFullupdates(true);
              }else{
                  cmpPref.setFullupdates(false);
              }
              if(requestParams.containsKey("modaverage")){
                  cmpPref.setModaverage(true);
              }else{
                  cmpPref.setModaverage(false);
              }
              if(requestParams.containsKey("overallcomments")){
                  cmpPref.setOverallcomments(true);
              }else{
                  cmpPref.setOverallcomments(false);
              }
              if(requestParams.containsKey("defaultapps")){
                  cmpPref.setDefaultapps(requestParams.get("defaultapps").toString());
              }else{
                  cmpPref.setDefaultapps("Internal");
              }
              if(requestParams.containsKey("payrollbase")){
                  cmpPref.setPayrollbase(requestParams.get("payrollbase").toString());
              }else{
                  cmpPref.setPayrollbase("Template");
              }
              if(requestParams.containsKey("approvesalary")){
                  cmpPref.setApprovesalary(Boolean.parseBoolean(requestParams.get("approvesalary").toString()));
              }
            hibernateTemplate.saveOrUpdate(cmpPref);
            success=true;
        } catch (Exception e) {
            success=false;
            e.printStackTrace();
        }
        return new KwlReturnObject(success, "", "-1", lst, lst.size());

    }
}
