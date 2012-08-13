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

package com.krawler.spring.hrms.payroll.employercontribution;

import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import masterDB.EmployerContribution;
import masterDB.Template;
import masterDB.TemplateMapEmployerContribution;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author krawler
 */
public class hrmsPayrollEmployerContributionDAOImpl implements hrmsPayrollEmployerContributionDAO{

    private HibernateTemplate hibernateTemplate;
    private static final Log logger = LogFactory.getLog(hrmsPayrollEmployerContributionDAOImpl.class) ;

    public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

    @Override
    public KwlReturnObject setEmployerContribData(HashMap<String, Object> requestParams) {

        List <EmployerContribution> list = new ArrayList<EmployerContribution>();
        boolean success = true;
        try {
            EmployerContribution EmpContribobj = (EmployerContribution) HibernateUtil.setterMethod(hibernateTemplate, requestParams, "masterDB.EmployerContribution", "Id");
            list.add(EmpContribobj);

        }catch (Exception e) {
            success = false;
           logger.warn("Exception occurred in hrmsPayrollEmployerContributionDAOImpl.setEmployerContribData", e);
        }finally{
            return new KwlReturnObject(success, "Employer Contribution saved successfully.", "-1", list, list.size());
        }
    }

    @Override
    public KwlReturnObject deleteMasterEmployerContrib(HashMap<String, Object> requestParams) {
        try {

            String id = requestParams.get("id").toString();
            String hql = "from TemplateMapEmployerContribution where empcontrimaster.id=?";

            String dependencyHql = "select sum(count) from (select count(expr) as count from wagemaster where expr like '%"+id+"%' union " +
                    "select count(expr) as count  from employercontributionmaster where expr like '%"+id+"%' union " +
                    "select count(expr) as count  from taxmaster where expr like '%"+id+"%' union " +
                    "select count(expr) as count  from deductionmaster where expr like '%"+id+"%') as t1";

            List lst1 = HibernateUtil.executeNativeQuery(hibernateTemplate, dependencyHql, null);

            if (lst1.size() > 0 &&  ((BigDecimal)lst1.iterator().next()).ROUND_UP > 0) {
                return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"depend\"}}", "", null, 0);
            }
            List lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
            List <String>rsltList= new ArrayList<String>();
            if (lst.size() > 0) {
                return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"assign\"}}", "", null, 0);
            } else {

                EmployerContribution ec = (EmployerContribution) hibernateTemplate.get(EmployerContribution.class, id);
                rsltList.add(ec.getEmpcontritype());
                hibernateTemplate.delete(ec);
            }
            return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"success\"}}", "", rsltList, 0);
        } catch (Exception e) {
            logger.warn("Exception occurred in hrmsPayrollEmployerContributionDAOImpl.deleteMasterEmployerContrib", e);
            return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"failed\"}}", "", null, 0);
        }
    }

    @Override
    public KwlReturnObject getMasterEmployerContrib(HashMap<String, Object> requestParams) {
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

            hql = "from EmployerContribution ";
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
        } catch (Exception e) {
            logger.warn("Exception occurred in hrmsPayrollEmployerContributionDAOImpl.getMasterEmployerContrib", e);
            success = false;
        } finally {
            return result;
        }
    }

    public KwlReturnObject setEmployerContribTemplateData(HashMap<String, Object> requestParams, Template template) {
        boolean success = false;
        try {
            JSONObject jobjwage = new JSONObject(requestParams.get("ecdata").toString());
            com.krawler.utils.json.base.JSONArray jarraywage = jobjwage.getJSONArray("ECDataADD");
            for (int i = 0; i < jarraywage.length(); i++) {
                String ecuid = java.util.UUID.randomUUID().toString();
                TemplateMapEmployerContribution templatemapec = new TemplateMapEmployerContribution();
                EmployerContribution ec = (EmployerContribution) hibernateTemplate.get(EmployerContribution.class, jarraywage.getJSONObject(i).getString("Id"));
                templatemapec.setTemplate(template);
                templatemapec.setEmpcontrimaster(ec);
                templatemapec.setRate(jarraywage.getJSONObject(i).getString("ECRate"));
                templatemapec.setId(ecuid);
                hibernateTemplate.save(templatemapec);
            }
            success = true;
        } catch (Exception e) {
            logger.warn("Exception occurred in hrmsPayrollEmployerContributionDAOImpl.setEmployerContribTemplateData", e);
            success = false;
        }

        return new KwlReturnObject(success, "Template added successfully.", "-1", null, 0);
    }

    public KwlReturnObject deleteEmployerContribTemplateData(HashMap<String, Object> requestParams)  {
        boolean success = true;

        try {
            String templateid =(String) requestParams.get("templateid");
            String hql = "from TemplateMapEmployerContribution where template.templateid=?";
            List lst=HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{templateid});
            int x = 0;
            for (x = 0; x < lst.size(); x++) {
                TemplateMapEmployerContribution tx = (TemplateMapEmployerContribution) lst.get(x);
                hibernateTemplate.delete(tx);
            }
        } catch(Exception e) {
            logger.warn("Exception occurred in hrmsPayrollEmployerContributionDAOImpl.deleteEmployerContribTemplateData", e);
            success = false;
        } finally{
           return new KwlReturnObject(success, "", "-1", null,0);
        }
    }

    @Override
    public KwlReturnObject getEmployerContribTemplateDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try{
            ArrayList orderby = null;
            ArrayList ordertype = null;
            ArrayList name = null;
            ArrayList value = null;
            String hql = "from TemplateMapEmployerContribution ";
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
            logger.warn("Exception occurred in hrmsPayrollEmployerContributionDAOImpl.getEmployerContribTemplateDetails", e);
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }
}
