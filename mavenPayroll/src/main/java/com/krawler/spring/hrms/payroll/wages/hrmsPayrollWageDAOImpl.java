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
package com.krawler.spring.hrms.payroll.wages;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.common.admin.userSalaryTemplateMap;
import com.krawler.utils.json.base.JSONException;
import java.util.HashMap;
import java.util.List;
import masterDB.Wagemaster;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import masterDB.Deductionmaster;
import masterDB.EmployerContribution;
import masterDB.TempTemplatemapwage;
import masterDB.Templatemapwage;
import masterDB.Template;
import masterDB.PayrollSummary;
import java.util.Map;
/**
 *
 * @author shs
 */
public class hrmsPayrollWageDAOImpl implements hrmsPayrollWageDAO {

    private HibernateTemplate hibernateTemplate;
    private static double wagetotalval;
    private static double wagedeductot;

    public static double getWagedeductot() {
        return wagedeductot;
    }

    public static void setWagedeductot(double wagedeductot) {
        hrmsPayrollWageDAOImpl.wagedeductot = wagedeductot;
    }

    public static double getWagetotalval() {
        return wagetotalval;
    }

    public static void setWagetotalval(double wagetotalval) {
        hrmsPayrollWageDAOImpl.wagetotalval = wagetotalval;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public KwlReturnObject setWagesData(HashMap<String, Object> requestParams) throws ServiceException {
        try {
            String code = requestParams.get("code").toString();
            String Cid = requestParams.get("Cid").toString();
            String compute = "";
            String name = requestParams.get("name").toString();
            String option = requestParams.get("option").toString();
            String rate = requestParams.get("rate").toString();
            String expr = requestParams.get("expr").toString();
            boolean isChecked = Boolean.parseBoolean(requestParams.get("isChecked").toString());
            if (requestParams.get("Action").equals("Add")) {
                String hql = "from Wagemaster where wcode=? and companydetails.companyID=?";
                List<Wagemaster> wagemasterList = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{code, Cid});
                Company cd = (Company) hibernateTemplate.get(Company.class, Cid);
                if (wagemasterList.size() > 0) {
                    return new KwlReturnObject(false, "{\"success\":\"false\",data:{value:\"exist\"}}", "", null, 0);
                } else {
                    String uids = java.util.UUID.randomUUID().toString();
                    Wagemaster insertWage = new Wagemaster();
                    insertWage.setWageid(uids);
                    insertWage.setCompanydetails(cd);
                    insertWage.setWagetype(name);
                    if (option.equals("Percent")) {
                        insertWage.setRate(1);
                        insertWage.setCash(Double.parseDouble(rate));
                    }
                    if (option.equals("Amount")) {
                        insertWage.setRate(0);
                        insertWage.setCash(Double.parseDouble(rate));
                    }
                    Wagemaster depwageid = (Wagemaster) hibernateTemplate.get(Wagemaster.class, requestParams.get("depwageid").toString());
                    if(requestParams.containsKey("computeon")){
                        compute = requestParams.get("computeon").toString();
                        insertWage.setComputeon(Integer.parseInt(compute));
                    }
                    insertWage.setDepwageid(depwageid);
                    insertWage.setWcode(code);
                    insertWage.setIsdefault(isChecked);
                    
                    insertWage.setExpr(expr);
                    hibernateTemplate.save(insertWage);
                }
                return new KwlReturnObject(true, "{\"success\":\"true\",data:{value:\"success\",action:\"Added\"}}", "", null, 0);
            } else {//edit
                String typeid = requestParams.get("typeid").toString();
                Wagemaster depwageid = (Wagemaster) hibernateTemplate.get(Wagemaster.class, requestParams.get("depwageid").toString());
                String hql = "from Wagemaster where wcode=? and companydetails.companyID=? and wageid !=?";
                List<Wagemaster> wagemasterList = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{code, Cid, typeid});
                if (wagemasterList.size() > 0) {
                    return new KwlReturnObject(false, "{\"success\":\"false\",data:{value:\"exist\"}}", "", null, 0);
                } else {
                    hql = "from Templatemapwage t where t.wagemaster.wageid=?";
                    List<Templatemapwage> templatemapwageList = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{typeid});
                    Wagemaster insertWage = null;
                    if (templatemapwageList.size() > 0) {
                        insertWage = (Wagemaster) hibernateTemplate.get(Wagemaster.class, typeid);
                        int ratetype,flag = 0;
                        if(insertWage.getDepwageid() != null){
                            if(depwageid == null){
                                flag = 1;
                            } else {
                                if(!insertWage.getDepwageid().getWageid().equals(depwageid.getWageid())){
                                    flag = 1;
                                }
                            }
                        } else {
                            if(depwageid != null){
                                flag = 1;
                            }
                        }
                        if (option.equals("Percent")) {
                            ratetype = 1;
                        } else {
                            ratetype = 0;
                        }
                        if (insertWage.getRate() != ratetype || insertWage.getCash() != Double.parseDouble(rate) || !insertWage.getWcode().equals(code) || flag == 1) {
                            return new KwlReturnObject(false, "{\"success\":\"false\",data:{value:\"assign\"}}", "", null, 0);
                        } else {
                            insertWage.setWagetype(name);
                            insertWage.setIsdefault(isChecked);
                            if(requestParams.containsKey("computeon")){
                                insertWage.setComputeon(Integer.parseInt(requestParams.get("computeon").toString()));
                            }
                            insertWage.setExpr(expr);
                            hibernateTemplate.save(insertWage);
                        }
                    } else {
                    	insertWage = (Wagemaster) hibernateTemplate.get(Wagemaster.class, typeid);
                        if (insertWage != null) {
                            insertWage = (Wagemaster) hibernateTemplate.get(Wagemaster.class, typeid);
                            insertWage.setWagetype(name);
                            if (option.equals("Percent")) {
                                insertWage.setRate(1);
                                insertWage.setCash(Double.parseDouble(rate));
                            }
                            if (option.equals("Amount")) {
                                insertWage.setRate(0);
                                insertWage.setCash(Double.parseDouble(rate));
                            }
                            insertWage.setWcode(code);
                            insertWage.setIsdefault(isChecked);
                            insertWage.setDepwageid(depwageid);
                            if(requestParams.containsKey("computeon")){
                                insertWage.setComputeon(Integer.parseInt(requestParams.get("computeon").toString()));
                            }
                            insertWage.setExpr(expr);
                            hibernateTemplate.update(insertWage);
                        }
                    }
                }
                return new KwlReturnObject(true, "{\"success\":\"true\",data:{value:\"success\",action:\"Edited\"}}", "", null, 0);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsPayrollWageDAOImpl.setWagesData : " + e.getMessage(), e);
        }
    }

    public KwlReturnObject deleteMasterWage(HashMap<String, Object> requestParams) {
        try {

            String id = requestParams.get("id").toString();
            ArrayList name = null;
            String hql = "from Templatemapwage t where t.wagemaster.wageid=?";
            String hql1 = "select expr from Wagemaster t ";
            if (requestParams.get("filter_names") != null) {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                hql1 += com.krawler.common.util.StringUtil.filterQuery(name, "where");
            }
            List lst1 = HibernateUtil.executeQuery(hibernateTemplate, hql1, new Object[]{"%"+id+"%"});
            if (lst1.size() > 0) {
                return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"depend\"}}", "", null, 0);
            }
            List lst = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{id});
            List <String>rsltlist= new ArrayList<String>();
            if (lst.size() > 0) {
                return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"assign\"}}", "", null, 0);
            } else {

                Wagemaster wm = (Wagemaster) hibernateTemplate.get(Wagemaster.class, id);
//                ProfileHandler.insertAuditLog(session, AuditAction.PAYROLL_COMPONENT_DELETED, "User " + AuthHandler.getFullName(session, AuthHandler.getUserid(request)) + " has deleted payroll template  " + wm.getWagetype() + " of type Wage" ,request);
                rsltlist.add(wm.getWagetype());
                hibernateTemplate.delete(wm);
            }
            return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"success\"}}", "", rsltlist, 0);
        } catch (Exception se) {
            return new KwlReturnObject(true, "{\"valid\":\"true\",data:{value:\"failed\"}}", "", null, 0);
        }
    }

    public KwlReturnObject getWageTemplateCount(String id) throws ServiceException {
        try {
            String queryTax = "select count(*) from Templatemapwage as t where t.template.templateid=?";
            List lst = HibernateUtil.executeQuery(hibernateTemplate, queryTax, new Object[]{id});
            return new KwlReturnObject(true, "", "", null, Integer.parseInt(lst.get(0).toString()));
        } catch (Exception e) {
            throw ServiceException.FAILURE("hrmsPayrollTemplateDAOImpl.getWageTemplateCount : " + e.getMessage(), e);
        }
    }

     public KwlReturnObject setWageTemplateData(HashMap<String, Object> requestParams, Template template) {
        boolean success = false;
        try {
            JSONObject jobjwage = new JSONObject(requestParams.get("wagedata").toString());
            com.krawler.utils.json.base.JSONArray jarraywage = jobjwage.getJSONArray("WageDataADD");
            for (int i = 0; i < jarraywage.length(); i++) {
                String wageuid = java.util.UUID.randomUUID().toString();
                Templatemapwage templatemapwage = new Templatemapwage();
                Wagemaster wm = (Wagemaster) hibernateTemplate.get(Wagemaster.class, jarraywage.getJSONObject(i).getString("WageId"));
                templatemapwage.setTemplate(template);
                templatemapwage.setWagemaster(wm);
                templatemapwage.setRate(jarraywage.getJSONObject(i).getString("WageRate"));
                templatemapwage.setId(wageuid);
                hibernateTemplate.save(templatemapwage);
            }
            success = true;
        } catch (Exception e) {
            System.out.println("Error is "+e);
            success = false;
        }

        return new KwlReturnObject(success, "Template added successfully.", "-1", null, 0);
    }

        public KwlReturnObject getDefualtWages(HashMap<String, Object> requestParams) {
        JSONObject jobj = new JSONObject();
        boolean success = false;
        List lst2 = null;
        try {
            String query1 = "from Wagemaster where isdefault=? and companydetails.companyID=? and (rate = 0 or (rate = 1 and computeon is not null)) ";
            String cid = (String)requestParams.get("cid");
            lst2 = (List) HibernateUtil.executeQuery(hibernateTemplate, query1, new Object[]{true, cid});
            success = true;
        } catch (ServiceException ex) {
            success = true;
        } finally {
            return new KwlReturnObject(success,"","",lst2,lst2.size());
        }
    }

    public double getWageRateInTemplate(HashMap<String, Object> requestParams) {
        double result = 0;
        List tabledata = null;
        try {
            String row = null;
            String templateid = (String)requestParams.get("templateid");
            String wageid = (String)requestParams.get("wageid");
            String query1 = "select rate from Templatemapwage where template.templateid=? and wagemaster.wageid=?";
            tabledata = (List) HibernateUtil.executeQuery(hibernateTemplate, query1, new Object[]{templateid, wageid});
            row = (String)tabledata.get(0);
            result = Double.parseDouble(row);
        } catch (ServiceException ex) {
            System.out.println("Error is "+ex);
        } finally {
            return result;
        }
    }

    public KwlReturnObject getWageMaster(HashMap<String, Object> requestParams) {
    	KwlReturnObject result = null;
        try {
            ArrayList<String> name = null;
            ArrayList<Object> value = null;
            String hql = "";
            hql = "from Wagemaster ";
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
            String[] searchcol;
            searchcol=new String[]{"wagetype"};
            if(requestParams.get("append")!=null){
                hql += requestParams.get("append").toString();
            }
            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                        StringUtil.insertParamSearchString(value, ss, 1);
                        String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                        hql +=searchQuery;
                }
            }
            result = StringUtil.getPagingquery(requestParams, null, hibernateTemplate, hql, value);
        } catch(Exception e){
            e.printStackTrace();
        }finally{
        	return result;
        }
    }

    public KwlReturnObject getWageMasterForComponent(HashMap<String, Object> requestParams) {

        List lst = null;
        int count = 0;
        try {
            String tablename = "Wagemaster";
            String allflag = "true";
            if(requestParams.containsKey("allflag"))
                requestParams.get("allflag").toString();
            ArrayList params = new ArrayList();
            String hql = "";
            hql = "from " + tablename + " where companydetails.companyID=? and wageid not in "+requestParams.get("childwage").toString();
            params.add(requestParams.get("Cid").toString());
//            params.add(requestParams.get("childwage").toString());
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, params.toArray());
            count = lst.size();
        } catch(Exception e){

        }
        return new KwlReturnObject(true, "success", " ", lst, count);
    }

    public Double calculatewagessalary(String exprstr,String mappingid) {
        Double amount = 0.0;
        try {
        String[] expr = exprstr.split("\\(add\\)");
        for(int ctr=0;ctr<expr.length;ctr++){
            String[] subexpr = expr[ctr].split("\\(sub\\)");
            for(int cnt=0;cnt<subexpr.length;cnt++){
                    String[] coeffwage = subexpr[cnt].split("\\*");
                    double coeff = 1;
                    String wageid = "";
                    if(coeffwage.length>1) {
                        coeff = Double.parseDouble(coeffwage[0]);
                        wageid = coeffwage[1];
                    } else {
                        wageid = coeffwage[0];
                    }
                    if(wageid.equals("-1")){
                        userSalaryTemplateMap temp = (userSalaryTemplateMap) hibernateTemplate.get(com.krawler.common.admin.userSalaryTemplateMap.class,mappingid);
                        if(temp!=null){
                            if(cnt==0)
                                amount += coeff * temp.getBasic();
                            else
                                amount -= coeff * temp.getBasic();
                        }
                    }
                    Wagemaster subwm = (Wagemaster) hibernateTemplate.get(masterDB.Wagemaster.class, wageid);
                    if(subwm!=null){
                        if(subwm.getComputeon()!=null&&subwm.getComputeon()==3){
                            if(cnt==0)
                                amount += coeff * (calculatewagessalary(subwm.getExpr(),mappingid)*subwm.getCash()/100);
                            else
                                amount -= coeff * (calculatewagessalary(subwm.getExpr(),mappingid)*subwm.getCash()/100);
                        }else if(subwm.getRate()!=null&&subwm.getRate()==0){
                            if(cnt==0)
                                amount += coeff * subwm.getCash();
                            else
                                amount -= coeff * subwm.getCash();
                        }
                    }else{
                        Deductionmaster subdm = (Deductionmaster) hibernateTemplate.get(masterDB.Deductionmaster.class, wageid);
                        if(subdm!=null){
                            if(subdm.getComputeon()!=null&&subdm.getComputeon()==3){
                                if(cnt==0)
                                    amount += coeff * calculatewagessalary(subdm.getExpr(),mappingid)*subdm.getCash()/100;
                                else
                                    amount -= coeff * calculatewagessalary(subdm.getExpr(),mappingid)*subdm.getCash()/100;
                            }else if(subdm.getRate()!=null&&subdm.getRate()==0){
                                if(cnt==0)
                                    amount += coeff * subdm.getCash();
                                else
                                    amount -= coeff * subdm.getCash();
                            }
                        } else {
                            EmployerContribution subecm = (EmployerContribution) hibernateTemplate.get(masterDB.EmployerContribution.class, wageid);
                            if(subecm!=null){
                                if(subecm.getComputeon()!=null&&subecm.getComputeon()==3){
                                    if(cnt==0)
                                        amount += coeff * calculatewagessalary(subecm.getExpr(),mappingid)*subecm.getCash()/100;
                                    else
                                        amount -= coeff * calculatewagessalary(subecm.getExpr(),mappingid)*subecm.getCash()/100;
                                }else if(subecm.getRate()!=null&&subecm.getRate()==0){
                                    if(cnt==0)
                                        amount += coeff *  subecm.getCash();
                                    else
                                        amount -= coeff *  subecm.getCash();
                                }
                            }
                        }
                    }
            }
        }
        } catch (Exception ex) {
            System.out.print(ex);
        }finally{
            return amount;
        }
    }

    public KwlReturnObject getWageMasterChild(HashMap<String, Object> requestParams) {

        List lst = null;
        int count = 0;
        try {
            String tablename = "Wagemaster";
            ArrayList params = new ArrayList();
            String hql = "";
            hql = "from " + tablename + " where companydetails.companyID=? and depwageid.wageid =?";
            params.add(requestParams.get("Cid").toString());
            params.add(requestParams.get("wageid").toString());
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, params.toArray());
            for (int i = 0; i < lst.size(); i++) {
                params.clear();
                masterDB.Wagemaster wage = (masterDB.Wagemaster) lst.get(i);
                params.add(requestParams.get("Cid").toString());
                params.add(wage.getWageid());
                lst.addAll(getRecursiveWage(params, requestParams.get("Cid").toString()));
            }
            count = lst.size();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return new KwlReturnObject(true, "success", " ", lst, count);
    }

    public List getRecursiveWage(ArrayList params, String companyid) {
        List lst = null;
        int count = 0;
        try {
            String tablename = "Wagemaster";
            String hql = "";
            hql = "from " + tablename + " where companydetails.companyID=? and depwageid.wageid =?";
            lst = HibernateUtil.executeQuery(hibernateTemplate, hql, params.toArray());
            for (int i = 0; i < lst.size(); i++) {
                params.clear();
                masterDB.Wagemaster wage = (masterDB.Wagemaster) lst.get(i);
                params.add(companyid);
                params.add(wage.getWageid());
                lst.addAll(getRecursiveWage(params, companyid));
            }
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return lst;
    }

    public KwlReturnObject getWageTemplateDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try{
            String templateid =(String) requestParams.get("templateid");
            ArrayList orderby = null;
            ArrayList ordertype = null;
            ArrayList name = null;
            ArrayList value = null;
            String hql = "from Templatemapwage ";
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
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject getTempWageTemplateDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        List tabledata = null;
        try{
            String templateid =(String) requestParams.get("templateid");
            String hql = "from TempTemplatemapwage as t where t.template.templateid=?";
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{templateid});
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

    public KwlReturnObject deleteWageTemplateData(HashMap<String, Object> requestParams)  {
        boolean success = true;

        try {
            String templateid =(String) requestParams.get("templateid");
            String hql = "from Templatemapwage where templateid=?";
            List lst=HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{templateid});
            int x = 0;
            for (x = 0; x < lst.size(); x++) {
                Templatemapwage tx = (Templatemapwage) lst.get(x);
                hibernateTemplate.delete(tx);
            }
        } catch(Exception e) {
            success = false;
        } finally{
           return new KwlReturnObject(success, "", "-1", null,0);
        }
    }

    public KwlReturnObject deleteTempWageTemplateData(HashMap<String, Object> requestParams)  {
        boolean success = true;

        try {
            String templateid =(String) requestParams.get("templateid");
            String hql = "from TempTemplatemapwage where templateid=?";
            List lst=HibernateUtil.executeQuery(hibernateTemplate, hql, new Object[]{templateid});
            int x = 0;
            for (x = 0; x < lst.size(); x++) {
                TempTemplatemapwage tx = (TempTemplatemapwage) lst.get(x);
                hibernateTemplate.delete(tx);
            }
        } catch(Exception e) {
            success = false;
        } finally{
           return new KwlReturnObject(success, "", "-1", null,0);
        }
    }
     public KwlReturnObject getPayRollSummary(HashMap<String, Object> requestParams)  {
        boolean success = true;
        List tabledata = null;
        try{
            String hql = "from masterDB.PayrollSummary c where c.deleteflag='F' ";
            String ss ="";
            if(requestParams.get("ss")!=null && !StringUtil.isNullOrEmpty(requestParams.get("ss").toString())){
                ss= requestParams.get("ss").toString();
                hql+="  and c.empname like '"+ss+"%'";
            }
            
            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql);
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }

      public KwlReturnObject deletePayrollSummary(String []ids)  {
			boolean successFlag = false;
			String resultmsg="";
			String id="";

	        try {
                
	            for(int i=0; i< ids.length; i++){
                    masterDB.PayrollSummary payrollSummary =(masterDB.PayrollSummary) hibernateTemplate.load(masterDB.PayrollSummary.class, ids[i]);
                    if(payrollSummary!=null)
                        payrollSummary.setDeleteflag(true);
                }
 
	            resultmsg = "Selected record deleted successfully.";

	        } catch (Exception e) {
	            successFlag = false;
	        }

	        successFlag = true;
	        return new KwlReturnObject(successFlag, resultmsg, "", null, 0);
	}

     public KwlReturnObject payrollSummarySave(JSONObject jobj) {
        JSONObject myjobj = new JSONObject();
        boolean success = true;
        List ll = new ArrayList();
        int dl = 0;
        try {
            masterDB.PayrollSummary payrollSummary = null;
            String userid = null;
            boolean isNew = false;
            if(jobj.has("id")) {
                String ID = jobj.getString("id");
                if(ID.equals("0")) {
                     payrollSummary = new PayrollSummary();
                    isNew = true;
                } else {
                     payrollSummary = (masterDB.PayrollSummary) hibernateTemplate.get(masterDB.PayrollSummary.class, ID);
                }
            }
            if(jobj.has("empname")) {
                payrollSummary.setEmpname(jobj.getString("empname"));
            }
            if(jobj.has("empid")) {
                payrollSummary.setEmpid(jobj.getString("empid"));
            }
            if(jobj.has("nric")) {
                payrollSummary.setNric(jobj.getString("nric"));
            }
            if(jobj.has("epfnumber")) {
                payrollSummary.setEpfnumber(jobj.getString("epfnumber"));
            }
            if(jobj.has("grosssalary")) {
                payrollSummary.setGrosssalary(jobj.getString("grosssalary"));
            }
            if(jobj.has("epfemployer")) {
                payrollSummary.setEpfemployer(jobj.getString("epfemployer"));
            }
            if(jobj.has("epfemployee")) {
                payrollSummary.setEpfemployee(jobj.getString("epfemployee"));
            }
            if(jobj.has("month")) {
                payrollSummary.setMonth(jobj.getString("month"));
            }
            
            
            hibernateTemplate.save(payrollSummary);
            ll.add(payrollSummary);
        } catch(Exception ex) {
            success = false;
        }
        return new KwlReturnObject(true, "", "", ll, dl);
    }

      public KwlReturnObject setTempWageTemplateDetails(HashMap<String, Object> requestParams) {
        boolean success = true;
        try{
            String templateid =(String) requestParams.get("templateid");
            JSONObject jobjwage = new JSONObject(requestParams.get("wagedata").toString());
            com.krawler.utils.json.base.JSONArray jarraywage = jobjwage.getJSONArray("WageDataADD");
            for (int i = 0; i < jarraywage.length(); i++) {
                String wageuid = java.util.UUID.randomUUID().toString();
                TempTemplatemapwage templatemapwage = new TempTemplatemapwage();
                Template templ = (Template) hibernateTemplate.get(Template.class, templateid);
                templatemapwage.setTemplate(templ);
                Wagemaster wmas = (Wagemaster) hibernateTemplate.get(Wagemaster.class, jarraywage.getJSONObject(i).getString("WageId"));
                templatemapwage.setWagemaster(wmas);
                templatemapwage.setRate(jarraywage.getJSONObject(i).getString("WageRate"));
                templatemapwage.setId(wageuid);
                hibernateTemplate.save(templatemapwage);
            }
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", null, 0);
        }
    }
    public KwlReturnObject getCompanyDetail(HashMap<String, Object> requestParams,String companyid)  {
        boolean success = true;
        List tabledata = null;
        try{
            String hql = "from masterDB.CompanyEPFInfo c where c.companyid=?";

            tabledata = HibernateUtil.executeQuery(hibernateTemplate, hql,new Object[]{companyid});
            success = true;
        }catch(Exception e){
            success = false;
        }finally{
           return new KwlReturnObject(success, "", "-1", tabledata, tabledata.size());
        }
    }
public KwlReturnObject SaveCompanyEPFDetail(String companyid,String cname,String cno,String addr,String cepfno,JSONObject jobj) {
        JSONObject myjobj = new JSONObject();
        boolean success = true;
        List ll = new ArrayList();
        int dl = 0;
        try {
           masterDB.CompanyEPFInfo CompanyEPFInfo = null;
            boolean isNew = false;
            CompanyEPFInfo = new masterDB.CompanyEPFInfo();
            isNew = true;
            CompanyEPFInfo.setCompanyid(companyid);
            CompanyEPFInfo.setCompanyname(cname);
            CompanyEPFInfo.setCompanyaddress(addr);
            CompanyEPFInfo.setCompanyno(cno);
            CompanyEPFInfo.setCompanyepfno(cepfno);
            hibernateTemplate.saveOrUpdate(CompanyEPFInfo);
            ll.add(CompanyEPFInfo);
        } catch(Exception ex) {
            success = false;
        }
        return new KwlReturnObject(true, "", "", ll, dl);
    }



}
