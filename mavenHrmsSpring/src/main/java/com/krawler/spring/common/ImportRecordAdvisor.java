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

package com.krawler.spring.common;

import com.krawler.common.util.DataInvalidateException;
import com.krawler.esp.hibernate.impl.HibernateUtil;
//import com.krawler.hql.accounting.Account;
//import com.krawler.hql.accounting.Group;
//import com.krawler.hql.accounting.MasterItem;
import com.krawler.spring.importFunctionality.ImportHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author krawler
 */
public class ImportRecordAdvisor implements MethodInterceptor {
    private HibernateTemplate hibernateTemplate;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Object result = null;
        boolean proceed = true;
        String methodName = mi.getMethod().getName();

        if(methodName.equals("saveRecord")) {
            proceed = BeforeSaveRecord(mi);
            // Throw DataInvalidateException : To stop execution and to log Invalidate data message or any other error
            // Return false : To stop execution without any error log
            // Return true  : To continue the execution
            if(proceed) {
                result = mi.proceed(); //Execute main method
                AfterSaveRecord(mi, result);
            }
        } else if(methodName.equals("getRefModuleData")) {
            BeforeGetRefModuleData(mi);
            result = mi.proceed(); //Execute main method
            result = AfterGetRefModuleData(mi, result);
        } else {
            result = mi.proceed();
        }

        return result;
    }

    private boolean BeforeSaveRecord(MethodInvocation mi) throws DataInvalidateException {
        boolean proceed = true;
        //public Object saveRecord(HttpServletRequest request, HashMap<String, Object> dataMap, Object csvReader, String modeName, String classPath, String primaryKey, Object extraObj, JSONArray customfield) throws DataInvalidateException;
        Object arguments[] = mi.getArguments();
        String mode = (String) arguments[3];
        try {
            if(mode.equalsIgnoreCase("wage") || mode.equalsIgnoreCase("deduction") || mode.equalsIgnoreCase("tax") || mode.equalsIgnoreCase("Employee Contribution")) {                
//                HttpServletRequest request = (HttpServletRequest) arguments[0];
                HashMap<String, Object> dataMap = (HashMap<String, Object>) arguments[1];
//                Object csvReader = arguments[2];
//                String classPath = (String) arguments[4];
//                String primaryKey = (String) arguments[5];
                String code = "";
                if(mode.equalsIgnoreCase("wage")) {
                    code = (String) dataMap.get("Wcode");
                } else if(mode.equalsIgnoreCase("deduction")) {
                    code = (String) dataMap.get("Dcode");
                } else if(mode.equalsIgnoreCase("tax")) {
                    code = (String) dataMap.get("Tcode");
                } else if(mode.equalsIgnoreCase("Employee Contribution")) {
                    code = (String) dataMap.get("Empcontricode");
                }
                
                int computeon = -1;                
                if(dataMap.containsKey("Computeon")) {
                    String computeonStr = dataMap.get("Computeon") != null ? (String) dataMap.get("Computeon"):"";
                    if(computeonStr.equalsIgnoreCase("Current Deductions")) {
                        computeon = 0;
                    } else if(computeonStr.equalsIgnoreCase("Current Earnings")) {
                        computeon = 1;
                    } else if(computeonStr.equalsIgnoreCase("Net Salary")) {
                        computeon = 2;
                    }
                }
                dataMap.remove("Computeon");
                
                if(dataMap.containsKey("Rate")) {
                    String rate = (String) dataMap.get("Rate");
                    double cash = 0;
                    if(rate.equalsIgnoreCase("P") || rate.equalsIgnoreCase("Percent")) {
                        dataMap.put("Rate", 1);
                        if(computeon == -1) {
                            throw new DataInvalidateException("Invalid value for Compute On for code - "+code+".");
                        }
                        dataMap.put("Computeon", computeon);
                    } else if(rate.equalsIgnoreCase("A") || rate.equalsIgnoreCase("Amount")) {
                        dataMap.put("Rate", 0);
                    } else {
                        dataMap.put("Rate", 0);
                        if(dataMap.containsKey("Cash")) {
                            cash = (Double) dataMap.get("Cash");
                            if(cash <= 100) {
                                dataMap.put("Rate", 1);
                                if(computeon == -1) {
                                    throw new DataInvalidateException("Invalid value for Compute On for code - "+code+".");
                                }
                                dataMap.put("Computeon", computeon);
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Logger.getLogger(ImportRecordAdvisor.class.getName()).log(Level.SEVERE, null, ex);
            throw new DataInvalidateException("Failed to create component for "+mode+": "+ex.getMessage());
        }

        return proceed;
    }

    private void AfterSaveRecord(MethodInvocation mi, Object result) throws DataInvalidateException {
    }



    private void BeforeGetRefModuleData(MethodInvocation mi) throws DataInvalidateException {
//        //public List getRefModuleData(HttpServletRequest request, String module, String fetchColumn, String comboConfigid, ArrayList<String> filterNames, ArrayList<Object> filterValues) throws ServiceException, DataInvalidateException;
//        Object arguments[] = mi.getArguments();
//        String module = (String) arguments[1];
//        try {
//            HashMap<String, Object> requestParams = (HashMap<String, Object>) arguments[0];
//            String companyid = requestParams.get("companyid").toString();
//            ArrayList<String> filterNames = (ArrayList<String>) arguments[4];
//            ArrayList<Object> filterValues = (ArrayList<Object>) arguments[5];
//            if(module.equalsIgnoreCase("Term")) {
//                filterValues.add(companyid);
//                filterNames.add("company.companyID");
//            } else if(module.equalsIgnoreCase("Vendor") || module.equalsIgnoreCase("com.krawler.hql.accounting.Vendor")
//                    || module.equalsIgnoreCase("Customer") || module.equalsIgnoreCase("com.krawler.hql.accounting.Customer") ) {
//                filterValues.add(companyid);
//                filterNames.add("company.companyID");
//                filterValues.add(false);
//                filterNames.add("account.deleted");
//            } else if(module.equalsIgnoreCase("Account") || module.equalsIgnoreCase("com.krawler.hql.accounting.Account")
//                    || module.equalsIgnoreCase("Group") || module.equalsIgnoreCase("com.krawler.hql.accounting.Group")) {
//                filterValues.add(companyid);
//                filterNames.add("company.companyID");
//                filterValues.add(false);
//                filterNames.add("deleted");
//            }
//        }catch(Exception ex){
//
//        }
    }

    private Object AfterGetRefModuleData(MethodInvocation mi, Object result) throws DataInvalidateException {
//        if(result!=null) {
//            List masterList = (List) result;
//            if(masterList.size()==0) {
//                Object arguments[] = mi.getArguments();
//                String module = (String) arguments[1];
//                if(ImportHandler.isMasterTable(module)) { //Check for referencing to master
//                    try {
//                        HashMap<String, Object> requestParams = (HashMap<String, Object>) arguments[0];
//                        String companyid = requestParams.get("companyid").toString();
//                        String doAction = requestParams.get("doAction").toString();
//                        String pref = (String) requestParams.get("masterPreference"); //0:Skip Record, 1:Skip Column, 2:Add new
//                        if(doAction.compareToIgnoreCase("import")==0 && pref!=null && pref.compareToIgnoreCase("2")==0){
//                            String comboConfigid = (String) arguments[3];
//                            ArrayList<Object> filterValues = (ArrayList<Object>) arguments[5];
//                            if(module.equalsIgnoreCase("MasterItem") || module.equalsIgnoreCase("com.krawler.hql.accounting.MasterItem")) {
//                                HashMap<String, Object> addParams = new HashMap<String, Object>();
//                                String masterName = filterValues.get(0)!=null?filterValues.get(0).toString():"";
//                                masterName = masterName.length() > 50 ? masterName.substring(0, 50) : masterName; //Maxlength for value is 50 so truncate extra string
//                                addParams.put("Company", companyid);
//                                addParams.put("Value", masterName);
//                                addParams.put("MasterGroup", comboConfigid);
//
//                                MasterItem mItem = (MasterItem) HibernateUtil.objectSetterMethod(hibernateTemplate, addParams, "com.krawler.hql.accounting.MasterItem", "ID");
//                                masterList.add(mItem.getID());
//                            }
//                        }
//                    }catch(Exception e){
//                        System.out.println("A(( AfterGetRefModuleData.InsertMasterModuleEntry: "+e.getMessage());
//                    }
//                } else if(module.equalsIgnoreCase("Group")) { //Accounting specific code
//                    try {
//                        List resultList = (List) result;
//                        if(resultList.size()==0) { //If not found then search group in Global list i.e. companyid=NULL
//                            String fetchColumn = (String) arguments[2];
//                            ArrayList<Object> filterNames = (ArrayList<Object>) arguments[4];
//                            ArrayList<Object> filterValues = (ArrayList<Object>) arguments[5];
//                            String masterValue = filterValues.get(0)!=null?filterValues.get(0).toString():"";
//                            String masterColumnName = filterNames.get(0)!=null?filterNames.get(0).toString():"";
//
//                            String query = "select "+fetchColumn+" from Group where deleted=false and company.companyID is null and "+masterColumnName+"=?";
//                            result = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{masterValue});
//                        }
//                    } catch(Exception ex) {
//                        System.out.println("A(( AfterGetRefModuleData.FetchGroup: "+ex.getMessage());
//                    }
//                }
//            }
//        }
        return result;
    }
}
