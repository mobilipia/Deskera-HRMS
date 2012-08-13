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

package com.krawler.spring.hrms.employee;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.update.Updates;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.common.admin.userSalaryTemplateMap;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import java.util.ArrayList;
import java.util.Date;
import masterDB.Deductionmaster;
import masterDB.EmployerContribution;
import masterDB.Historydetail;
import masterDB.Payhistory;
import masterDB.Taxmaster;
import masterDB.Template;
import masterDB.Wagemaster;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 *
 * @author shs
 */
public class hrmsEmpDAOImpl implements hrmsEmpDAO,MessageSourceAware{

    private HibernateTemplate hibernateTemplate;
    private MessageSource messageSource;
    private static final Log logger = LogFactory.getLog(hrmsEmpDAOImpl.class);
    
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

    public KwlReturnObject getCurrencyDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try{
            String currencyid =(String) requestParams.get("currencyid");
            String hql = "from KWLCurrency where currencyID=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{currencyid});
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getPayHistory(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try{
//            String userid =(String) requestParams.get("userid");
//            User userobj = (User) hibernateTemplate.get(User.class, userid);
//            Date createdfor =(Date) requestParams.get("createdfor");
            ArrayList orderby = null;
            ArrayList ordertype = null;
            ArrayList name = null;
            ArrayList value = null;
            String hql = "from Payhistory ";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null) {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
                int ind = hql.indexOf("(");
                if(ind>-1){
                    int index = Integer.valueOf(hql.substring(ind+1,ind+2));
                    hql = hql.replaceAll("("+index+")", value.get(index).toString());
                    value.remove(index);
                }
            }
            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null) {
                orderby = new ArrayList((List<String>) requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>) requestParams.get("order_type"));
                hql +=StringUtil.orderQuery(orderby, ordertype);
            }
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, value.toArray());
            success = true;
        }catch(Exception e){
            success = false;
            e.printStackTrace();
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    @Override
    public List<Payhistory> getPayHistorys(String userids, Date startDate, Date endDate){
    	List<Payhistory> list = null;
    	try{
    		if(!StringUtil.isNullOrEmpty(userids)){
    			list = HibernateUtil.executeQuery(hibernateTemplate, "from Payhistory where userID.userID in ("+userids+") and paycyclestart=? and paycycleend=?", new Object[]{startDate, endDate});
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    
    public KwlReturnObject setPayHistory(HashMap<String, Object> requestParams) {
        List <Payhistory> list = new ArrayList<Payhistory>();
        boolean success = false;
        try {
                Payhistory payhist = new Payhistory();
                payhist.setHistoryid((String)requestParams.get("historyid"));
                payhist.setUserID((User)requestParams.get("userid"));
                payhist.setTemplate((Template)requestParams.get("template"));
                payhist.setName((String)requestParams.get("name"));
                payhist.setDesign((String)requestParams.get("design"));
                payhist.setDepartment((String)requestParams.get("department"));
                payhist.setGross((String)requestParams.get("gross"));
                payhist.setNet((String)requestParams.get("net"));
                payhist.setWagetot(String.valueOf(requestParams.get("wagetot")));
                payhist.setDeductot((String)requestParams.get("deductot"));
                payhist.setTaxtot((String)requestParams.get("taxtot"));
                payhist.setEctot((String)requestParams.get("ectot"));
                payhist.setStatus(String.valueOf(requestParams.get("status")));
                payhist.setCreatedon((Date)requestParams.get("createdon"));
                payhist.setCreatedfor((Date)requestParams.get("createdfor"));
                payhist.setPaycyclestart((Date)requestParams.get("paycyclestart"));
                payhist.setPaycycleend((Date)requestParams.get("paycycleend"));
                payhist.setPaymonth((String)requestParams.get("paymonth"));
                payhist.setGeneratedon((Date)requestParams.get("generatedon"));
                payhist.setMappingid((userSalaryTemplateMap)requestParams.get("mappingid"));
                payhist.setUnpaidleaves((Double)requestParams.get("unpaidleaves"));
                payhist.setSalarystatus(Integer.parseInt(requestParams.get("salarystatus").toString()));
                hibernateTemplate.save(payhist);
                list.add(payhist);
                success = true;

        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }finally{
            return new KwlReturnObject(success, "Payhistory added successfully.", "-1", list, list.size());
        }
    }

    public KwlReturnObject setHistorydetail(HashMap<String, Object> requestParams) {
    List <Historydetail> list = new ArrayList<Historydetail>();
    boolean success = false;
        try {
                    Historydetail newobj1 = new Historydetail();
                    newobj1.setPrimid((String)requestParams.get("primid"));
                    newobj1.setPayhistory((Payhistory)requestParams.get("payhistory"));
                    newobj1.setName((String)requestParams.get("name"));
                    newobj1.setType((String)requestParams.get("type"));
                    newobj1.setAmount((String)requestParams.get("amount"));
                    newobj1.setRate((String)requestParams.get("rate"));
                    if(requestParams.get("Id")!=null && !StringUtil.isNullOrEmpty(requestParams.get("Id").toString())){
                    	if(requestParams.get("name").toString().equals("Wages"))
                        	newobj1.setWagemaster((Wagemaster) hibernateTemplate.get(Wagemaster.class, (Serializable) requestParams.get("Id")));
                        else if(requestParams.get("name").toString().equals("Deduction"))
                        	newobj1.setDeductionmaster((Deductionmaster) hibernateTemplate.get(Deductionmaster.class, (Serializable) requestParams.get("Id")));
                        else if(requestParams.get("name").toString().equals("Taxes"))
                        	newobj1.setTaxmaster((Taxmaster) hibernateTemplate.get(Taxmaster.class, (Serializable) requestParams.get("Id")));
                        else if(requestParams.get("name").toString().equals("Employer Contribution"))
                        	newobj1.setEmployercontributionmaster((EmployerContribution) hibernateTemplate.get(EmployerContribution.class, (Serializable) requestParams.get("Id")));
                    }
                    hibernateTemplate.save(newobj1);
                    list.add(newobj1);
                    success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }finally{
            return new KwlReturnObject(success, "Historydetail added successfully.", "-1", list, list.size());
        }
    }

    public KwlReturnObject deleteHistorydetail(HashMap<String, Object> requestParams) {
        boolean success = false;
        try {
            String templateid =(String) requestParams.get("templateid");
            String hql = "from Payhistory where templateid=?";
            List lst=HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{templateid});
            int x = 0;
            for (x = 0; x < lst.size(); x++) {
                Payhistory td = (Payhistory) lst.get(x);
                    List lst1 = HibernateUtil.executeQuery(hibernateTemplate, "from Historydetail t where t.payhistory.historyid=?", td.getHistoryid());
                    int ii = 0;
                    while (ii < lst1.size()) {
                        Historydetail hd = (Historydetail) lst1.get(ii);
                        hibernateTemplate.delete(hd);
                        ii++;
                    }
                    hibernateTemplate.delete(td);
            }

            success=true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
        return new KwlReturnObject(success, "Historydetail added successfully.", "-1", null, 0);
    }
    public KwlReturnObject deleteEmpPayHistory(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try{
 //           String historyid =(String) requestParams.get("historyid");
            String hql = "from Historydetail hd where hd.payhistory.historyid=? ";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{requestParams.get("payhistoryid")});
            int i = 0;
            while(i<tabledata.size()){
                Historydetail hd  = (Historydetail)tabledata.get(i);
                hibernateTemplate.delete(hd);
                i++;
            }

            hibernateTemplate.delete((Payhistory)hibernateTemplate.get(Payhistory.class, requestParams.get("payhistoryid").toString()));
            success = true;
        }catch(Exception e){
            e.printStackTrace();
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", null, 0);
        }
    }

    @Override
    public KwlReturnObject getHistoryDetail(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tableData = null;

        try{
            ArrayList name = null;
            ArrayList value = null;
            String hql = "from Historydetail ";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null) {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }
            tableData = HibernateUtil.executeQuery(hibernateTemplate, hql, value.toArray());
            success = true;
        }catch(Exception e){
            success = false;
            logger.warn("Exception in hrmsEmpDAOImpl.getHistpryDetail", e);
        }finally{
           return new KwlReturnObject(success, "", "-1", tableData, tableData.size());
        }
    }
    
    public KwlReturnObject salaryGeneratedUpdates(HashMap<String, Object> requestParams){
    	Boolean success = false;
    	Calendar calendar = Calendar.getInstance();
    	Date date1;
    	Date date2;
        List <Updates> list = new ArrayList<Updates>();
        try{
		        	Locale locale = null;
		            if(requestParams.get("locale")!=null){
		            	locale = (Locale) requestParams.get("locale");
		            }
        			DateFormat df = (DateFormat) requestParams.get("df");
					String userid = requestParams.get("userid").toString();
					Date userdate = (Date) requestParams.get("userdate");
                    String companyid = requestParams.get("companyid").toString();
                    calendar.setTime(userdate);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.add(Calendar.MONTH, -(Calendar.MONTH-1));
                    date1 = calendar.getTime();
                    date1.setDate(1);
                    date2 = calendar.getTime();
                    date2.setDate(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    String query = "from Payhistory where userID.company.companyID=? and userID.userID=? and salarystatus=? and generatedon is not null and createdon is not null  and paycyclestart is not null and paycyclestart between ? and ?";
                    List<Payhistory> recordTotalCount = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{companyid, userid, 3, date1, date2});
                    Iterator<Payhistory> itr = recordTotalCount.iterator();
                    while(itr.hasNext()) {
                    	Payhistory payhistory = itr.next();
                        String updateDiv = "";
                        updateDiv +=messageSource.getMessage("hrms.common.Alert.salaryGeneratedUpdates",new Object[]{"<font color='green'>"+df.format(payhistory.getPaycyclestart())+"</font> <a href='#' onclick='viewmypayslip()'>","</a>"}, "Salary is generated for month <font color='green'>"+df.format(payhistory.getPaycyclestart())+"</font> <a href='#' onclick='viewmypayslip()'>download</a> payslip.", locale);
                        list.add(new Updates(StringUtil.getContentSpan(updateDiv), payhistory.getGeneratedon()));
                    }
        }catch(Exception ex){
            ex.printStackTrace();
            success = false;
        }finally{
            return new KwlReturnObject(success, "", "", list, list.size());
        }
    }
    
    @SuppressWarnings("finally")
	public Company getCompany(Map<String, Object> requestParams){
    	Company company = null;
    	try {
    		company = (Company) hibernateTemplate.get(Company.class, (Serializable) requestParams.get("companyId"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			return company;
		}
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "finally" })
	public List<Payhistory> getSalariesBetweenStartAndEndDate(Map<String, Object> requestParams) {
        List<Payhistory> payhistories = null;
        try{
            String hql = "from Payhistory where userID.userID = ? and paycyclestart >= ? and paycycleend <= ? order by paycyclestart";
            payhistories = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{requestParams.get("userId"), requestParams.get("paycyclestart"), requestParams.get("paycycleend")});
        }catch(Exception e){
            e.printStackTrace();
        }finally{
           return payhistories;
        }
    }
    
    @Override
    @SuppressWarnings({ "unchecked", "finally" })
	public List<userSalaryTemplateMap> getUserSalaryTemplateMap(Map<String, Object> requestParams) {
        List<userSalaryTemplateMap> userSalaryTemplateMaps = null;
        try{
            String hql = "from userSalaryTemplateMap where userAccount.userID = ? and effectiveDate >= ? and effectiveDate <= ? and effectiveDate <= ? order by effectiveDate";
            userSalaryTemplateMaps = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{requestParams.get("userId"), requestParams.get("paycyclestart"), requestParams.get("paycycleend"), requestParams.get("payCycleEndDate")});
        }catch(Exception e){
            e.printStackTrace();
        }finally{
           return userSalaryTemplateMaps;
        }
    }

    public KwlReturnObject generateApprovedSalary(final String[] historyIDs, int mode) {
        boolean success = true;
        int numRow = 0;
        try{
            final String hql = "Update Payhistory set salarystatus="+mode+" where historyid in (:historyids) ";
            numRow = (Integer) hibernateTemplate.execute(new HibernateCallback() {

                public Object doInHibernate(Session session) {
                    int numRows = 0;
                    Query query = session.createQuery(hql);
                    if (historyIDs != null) {
                        
                        query.setParameterList("historyids", historyIDs);
                        
                    }
                    numRows = query.executeUpdate();
                    return numRows;
                }
            });
            success = true;
        }catch(Exception e){
            success = false;
            logger.warn("Exception in hrmsEmpDAOImpl.generateApprovedSalary", e);
        }finally{
           return new KwlReturnObject(success, "", "-1", null, numRow);
        }
    }

    public User getPayHistoryEmployeeName(String historyid) {
        User user=null;
        try{
           Payhistory py = (Payhistory) hibernateTemplate.load(Payhistory.class, historyid);
           user= py.getUserID();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
           return user;
        }
    }
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
