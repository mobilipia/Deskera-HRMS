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
package com.krawler.spring.companyDetails;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyHoliday;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.Country;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.FileUploadHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnMsg;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author Karthik
 */
public class companyDetailsDAOImpl implements companyDetailsDAO{

    private HibernateTemplate hibernateTemplate;
    private storageHandlerImpl storageHandlerImplObj;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj1) {
        this.storageHandlerImplObj = storageHandlerImplObj1;
    }

    public KwlReturnObject getCompanyPreferences(String cmpid){
		List tabledata = null;
        List <JSONArray> list = new ArrayList<JSONArray>();
        JSONArray preferences = new JSONArray();
		try {
			JSONObject obj = new JSONObject();
            CompanyPreferences cmpPref=null;
			String query="from CompanyPreferences where company.companyID=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, query,cmpid );
            if(tabledata.size()>0){
                cmpPref=(CompanyPreferences)hibernateTemplate.get(CompanyPreferences.class,cmpid);
                obj.put("selfapp",cmpPref.isSelfappraisal());
                obj.put("competency",cmpPref.isCompetency());
                obj.put("timesheetjob",cmpPref.isTimesheetjob());
                obj.put("goal",cmpPref.isGoal());
                obj.put("annmng",cmpPref.isAnnmanager());
                obj.put("approveappraisal",cmpPref.isApproveappraisal());
                obj.put("promotionrec",cmpPref.isPromotion());
                obj.put("weightage",cmpPref.isWeightage());
                obj.put("reviewappraisal",cmpPref.isReviewappraisal());
                obj.put("partial",cmpPref.isPartial());
                obj.put("fullupdates",cmpPref.isFullupdates());
                obj.put("approvesalary",cmpPref.isApprovesalary());
                obj.put("modaverage",cmpPref.isModaverage());
                obj.put("overallcomments",cmpPref.isOverallcomments());
                obj.put("blockemployees",cmpPref.isBlockemployees());
                if(StringUtil.isNullOrEmpty(cmpPref.getDefaultapps())){
                    obj.put("defaultapps","Internal");
                } else{
                    obj.put("defaultapps",cmpPref.getDefaultapps());
                }
                if(StringUtil.isNullOrEmpty(cmpPref.getPayrollbase())){
                    obj.put("payrollbase","Template");
                } else{
                    obj.put("payrollbase",cmpPref.getPayrollbase());
                }
            }else{
                obj.put("selfapp",false);
                obj.put("competency",false);
                obj.put("timesheetjob",true);
                obj.put("goal",false);
                obj.put("annmng",false);
                obj.put("approveappraisal",false);
                obj.put("promotionrec",false);
                obj.put("weightage",false);
                obj.put("reviewappraisal",false);
                obj.put("partial",false);
                obj.put("fullupdates",false);
                obj.put("modaverage",false);
                obj.put("approvesalary",false);
                obj.put("blockemployees",false);
                obj.put("defaultapps","Internal");
                obj.put("payrollbase","Template");
            }
            preferences.put(obj);
			list.add(preferences);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new KwlReturnObject(true, KwlReturnMsg.S01, "", list, 0);
	}

//    public KwlReturnObject getCompanyPreferences(String cmpid)
//			throws ServiceException {
//        List ll = new ArrayList();
//		JSONObject obj = new JSONObject();
//		try {
//		    CompanyPreferences cmpPref=null;
//            cmpPref = (CompanyPreferences) hibernateTemplate.load(CompanyPreferences.class, cmpid);
//            if(cmpPref != null){
//                obj.put("campaign",cmpPref.isCampaign());
//                obj.put("lead",cmpPref.isLead());
//                obj.put("account",cmpPref.isAccount());
//                obj.put("contact",cmpPref.isContact());
//                obj.put("opportunity",cmpPref.isOpportunity());
//                obj.put("cases",cmpPref.isCases());
//                obj.put("product",cmpPref.isProduct());
//                obj.put("activity",cmpPref.isActivity());
//            }else{
//                obj.put("campaign",false);
//                obj.put("lead",false);
//                obj.put("account",false);
//                obj.put("contact",false);
//                obj.put("opportunity",false);
//                obj.put("cases",false);
//                obj.put("product",true);
//                obj.put("activity",false);
//            }
//            ll.add(obj);
//		} catch (com.krawler.utils.json.base.JSONException e) {
//			throw ServiceException.FAILURE("companyDetailsDAOImpl.getCompanyPreferences", e);
//		}
//		return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, 0);
//	}

//    public KwlReturnObject setCompanyPref(HashMap hm) throws ServiceException {
//        List ll = new ArrayList();
//        int dl = 0;
//        try {
//            CompanyPreferences cmpPref = null;
//            Company company = null;
//            String cmpid = null;
//            if (hm.containsKey("companyid") && hm.get("companyid") != null) {
//                cmpid = hm.get("companyid").toString();
//                company = (Company) hibernateTemplate.load(Company.class, cmpid);
//
//                cmpPref = (CompanyPreferences) hibernateTemplate.load(CompanyPreferences.class, cmpid);
//                if(cmpPref == null) {
//                    cmpPref = new CompanyPreferences();
//                    cmpPref.setCompany(company);
//                }
//            }
//
//            if (hm.containsKey("heirarchypermisssioncampaign") && StringUtil.isNullOrEmpty(hm.get("heirarchypermisssioncampaign").toString())) {
//                cmpPref.setCampaign(false);
//            } else {
//                cmpPref.setCampaign(true);
//            }
//            if (hm.containsKey("heirarchypermisssionleads") && StringUtil.isNullOrEmpty(hm.get("heirarchypermisssionleads").toString())) {
//                cmpPref.setLead(false);
//            } else {
//                cmpPref.setLead(true);
//            }
//            if (hm.containsKey("heirarchypermisssionaccounts") && StringUtil.isNullOrEmpty(hm.get("heirarchypermisssionaccounts").toString())) {
//                cmpPref.setAccount(false);
//            } else {
//                cmpPref.setAccount(true);
//            }
//            if (hm.containsKey("heirarchypermisssioncontacts") && StringUtil.isNullOrEmpty(hm.get("heirarchypermisssioncontacts").toString())) {
//                cmpPref.setContact(false);
//            } else {
//                cmpPref.setContact(true);
//            }
//            if (hm.containsKey("heirarchypermisssionopportunity") && StringUtil.isNullOrEmpty(hm.get("heirarchypermisssionopportunity").toString())) {
//                cmpPref.setOpportunity(false);
//            } else {
//                cmpPref.setOpportunity(true);
//            }
//            if (hm.containsKey("heirarchypermisssioncases") && StringUtil.isNullOrEmpty(hm.get("heirarchypermisssioncases").toString())) {
//                cmpPref.setCases(false);
//            } else {
//                cmpPref.setCases(true);
//            }
//            if (hm.containsKey("heirarchypermisssionproduct") && StringUtil.isNullOrEmpty(hm.get("heirarchypermisssionproduct").toString())) {
//                cmpPref.setProduct(false);
//            } else {
//                cmpPref.setProduct(true);
//            }
//            if (hm.containsKey("heirarchypermisssionactivity") && StringUtil.isNullOrEmpty(hm.get("heirarchypermisssionactivity").toString())) {
//                cmpPref.setActivity(false);
//            } else {
//                cmpPref.setActivity(true);
//            }
//
//            hibernateTemplate.saveOrUpdate(cmpPref);
//
//            ll.add(cmpPref);
//        } catch (Exception e) {
//            throw ServiceException.FAILURE("companyDetailsDAOImpl.setCompanyPref", e);
//        }
//        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
//    }

    public KwlReturnObject getCompanyInformation(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
        String companyid = "";
        try {
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
//            String query = "from Company c ";
            String query = "select c,cpr from CompanyPreferences c right outer join c.company cpr ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            query += filterQuery;
            
            ll = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{companyid});
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsDAOImpl.getCompanyInformation", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getCompanyHolidays(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
        String companyid = "";
        try {
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            String query = "from CompanyHoliday c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            query += filterQuery;
            
            ll = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{companyid});
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsDAOImpl.getCompanyInformation", e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public void updateCompany(HashMap hm) throws ServiceException {
        String companyid = "";
        DateFormat dateformat = null;
        try {
            if (hm.containsKey("companyid") && hm.get("companyid") != null) {
                companyid = hm.get("companyid").toString();
            }
            if (hm.containsKey("dateformat") && hm.get("dateformat") != null) {
                dateformat = (DateFormat) hm.get("dateformat");
            }
            Company company = (Company) hibernateTemplate.load(Company.class, companyid);
            if (hm.containsKey("companyname") && hm.get("companyname") != null) {
                company.setCompanyName((String) hm.get("companyname"));
            }
            if (hm.containsKey("address") && hm.get("address") != null) {
                company.setAddress((String) hm.get("address"));
            }
            if (hm.containsKey("city") && hm.get("city") != null) {
                company.setCity((String) hm.get("city"));
            }
            if (hm.containsKey("state") && hm.get("state") != null) {
                company.setState((String) hm.get("state"));
            }
            if (hm.containsKey("zip") && hm.get("zip") != null) {
                company.setZipCode((String) hm.get("zip"));
            }
            if (hm.containsKey("phone") && hm.get("phone") != null) {
                company.setPhoneNumber((String) hm.get("phone"));
            }
            if (hm.containsKey("fax") && hm.get("fax") != null) {
                company.setFaxNumber((String) hm.get("fax"));
            }
            if (hm.containsKey("website") && hm.get("website") != null) {
                company.setWebsite((String) hm.get("website"));
            }
            if (hm.containsKey("mail") && hm.get("mail") != null) {
                company.setEmailID((String) hm.get("mail"));
            }
            if (hm.containsKey("domainname") && hm.get("domainname") != null) {
                company.setSubDomain((String) hm.get("domainname"));
            }
            if (hm.containsKey("country") && hm.get("country") != null) {
                company.setCountry((Country) hibernateTemplate.load(Country.class, (String) hm.get("country")));
            }
            if (hm.containsKey("currency") && hm.get("currency") != null) {
                company.setCurrency((KWLCurrency) hibernateTemplate.load(KWLCurrency.class, (String) hm.get("currency")));
            }
            if (hm.containsKey("timezone") && hm.get("timezone") != null) {
                KWLTimeZone timeZone = (KWLTimeZone) hibernateTemplate.load(KWLTimeZone.class, (String) hm.get("timezone"));
                company.setTimeZone(timeZone);
            }
            company.setModifiedOn(new Date());
            if (hm.containsKey("holidays") && hm.get("holidays") != null) {
                JSONArray jArr = new JSONArray((String) hm.get("holidays"));
                Set<CompanyHoliday> holidays = company.getHolidays();
                holidays.clear();
                DateFormat formatter = dateformat;
                for (int i = 0; i < jArr.length(); i++) {
                    CompanyHoliday day = new CompanyHoliday();
                    JSONObject obj = jArr.getJSONObject(i);
                    day.setDescription(obj.getString("description"));
                    day.setHolidayDate(formatter.parse(obj.getString("day")));
                    day.setCompany(company);
                    holidays.add(day);
                }
            }
            if (hm.containsKey("logo") && hm.get("logo") != null) {
                String imageName = ((FileItem) (hm.get("logo"))).getName();
                if (imageName != null && imageName.length() > 0) {
                    String fileName = companyid + FileUploadHandler.getCompanyImageExt();
                    company.setCompanyLogo(Constants.ImgBasePath + fileName);
                    new FileUploadHandler().uploadImage((FileItem) hm.get("logo"),
                            fileName,
                            storageHandlerImplObj.GetProfileImgStorePath(), 130, 25, true, false);
                }
            }
            hibernateTemplate.update(company);
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsDAOImpl.updateCompany", e);
        }
    }

    public void deleteCompany(HashMap<String, Object> requestParams) throws ServiceException {
        String companyid = "";
        try {
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            
            Company company = (Company) hibernateTemplate.load(Company.class, companyid);
            company.setDeleted(1);
            
            hibernateTemplate.update(company);
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsDAOImpl.deleteCompany", e);
        }
    }

    public String getSubDomain(String companyid) throws ServiceException {
        String subdomain = "";
        try {
            Company company = (Company) hibernateTemplate.get(Company.class, companyid);
            subdomain = company.getSubDomain();
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsDAOImpl.getSubDomain", e);
        }
        return subdomain;
    }

    public String getCompanyid(String domain) throws ServiceException {
        String companyId = "";
        List ll = new ArrayList();
        try {
            String Hql = "select companyID from Company where subDomain = ?";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql, new Object[]{domain});
            companyId = companyDetailsHandler.getCompanyid(ll);
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsDAOImpl.getCompanyid", e);
        }
        return companyId;
    }
}
