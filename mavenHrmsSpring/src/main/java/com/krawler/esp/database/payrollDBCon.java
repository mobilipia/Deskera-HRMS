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

package com.krawler.esp.database;

import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.handlers.ProfileHandler;
import java.io.IOException;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.krawler.hql.payroll.payrollManager;
import com.krawler.hql.payroll.payrollHandler;
public class payrollDBCon {

    private String typeWork = null;
    private String send = "";

    public String getter(int flag, HttpServletRequest request) throws ServletException {
        Session session = HibernateUtil.getCurrentSession();
        payrollManager rh = new payrollManager();
        try {
            String st = "0";
            String limit = "20";
            Integer start2 = 0;
            Integer limit2 = 0;
            if (request.getParameter("start") != null) {
                st = request.getParameter("start");
            }
            if (request.getParameter("limit") != null) {
                limit = request.getParameter("limit");
            }
            if (flag == 1) {
                send = rh.getWagesData(session);
            } else if (flag == 2) {
                send = rh.getWagesData(AuthHandler.getCompanyid(request), st, limit, session);
            } else if (flag == 3) {
                send = rh.getTaxData(session);
            } else if (flag == 4) {
                send = rh.getTaxData(AuthHandler.getCompanyid(request), st, limit, session);
            } else if (flag == 5) {
                send = rh.getDeductionData(session);
            } else if (flag == 6) {
                send = rh.getDeductionData(AuthHandler.getCompanyid(request), st, limit, session);
            } else if (flag == 7) {
                send = rh.getCompanyData(session);
            } else if (flag == 9) {
               send = rh.getPayProcessData(request,AuthHandler.getCompanyid(request), st, "10", session);
            } else if (flag == 10) {
                String s = String.valueOf(AuthHandler.getCurrencyID(request));
                send = rh.getPayEmployeePerTemp(s, request.getParameter("TempId"), session);
            } else if (flag == 11) {
                send = rh.getEmpPerTempidData(request, request.getParameter("TempId"), request.getParameter("wtotal"), request.getParameter("ttotal"), request.getParameter("dtotal"), session);
            } else if (flag == 12) {
                send = rh.getwagesPerTempid(request.getParameter("TempId"), request.getParameter("salary"), session);
            } else if (flag == 13) {
                send = rh.getTaxsPerTempid(request.getParameter("TempId"), request.getParameter("salary"), session);
            } else if (flag == 14) {
                send = rh.getDeducPerTempid(request.getParameter("TempId"), request.getParameter("salary"), session);
            } else if (flag == 15) {
                send = rh.getAllUserlist(AuthHandler.getCompanyid(request), request.getParameter("Gname"), request.getParameter("tid"), session);
            } else if (flag == 17) {
                send = rh.getAuditData(request.getParameter("stdate"), request.getParameter("enddate"), request.getSession().getAttribute("companyid").toString(), st, limit, session);
            } else if (flag == 18) {
                send = rh.getWagesData(AuthHandler.getCompanyid(request), request.getParameter("assign"), session);
            } else if (flag == 19) {
                send = rh.getTaxData(AuthHandler.getCompanyid(request), request.getParameter("assign"), session);
            } else if (flag == 20) {
                send = rh.getDeductionData(AuthHandler.getCompanyid(request), request.getParameter("assign"), session);
            } else if (flag == 21) {
                send = rh.getAllCurrency(session);
            } else if (flag == 22) {
                send = rh.GenerateCode(request.getParameter("codetype"), request.getParameter("minus"), request.getSession().getAttribute("companyid").toString(), session);
            } else if (flag == 23) {
                send = rh.getAssignedUserlist(AuthHandler.getCompanyid(request), request.getParameter("Tid"), session);
            } else if (flag == 28) {
                String s = String.valueOf(request.getSession().getAttribute("currencyid"));
                send = rh.getEmpListPerGroupid(request, s, request.getParameter("groupid"), session);
            } else if (flag == 34) {
                send = rh.getReportPerMonth(session, request);
            } else if (flag == 35) {
                send = rh.getviewmypayslip(session, request);
            }
            else if (flag == 36) {
                send = rh.getHistWages(session, request);
            }
            else if (flag == 37) {
                send = rh.getHistTaxes(session, request);
            }
            else if (flag == 38) {
                send = rh.getHistDeduces(session, request);
            }
             else if (flag == 39) {
                send = rh.getPayComponent(session, request);
            }
            else if (flag == 42) {
                send = rh.GetTaxperCatgry(session, request);
            }
            else if (flag == 43) {
                send = rh.getTemplistperDesign(session, request);
            }
            else if (flag == 44) {
                send = rh.getDefualtWages(session, request);
            }
            else if (flag == 45) {
                send = rh.getDefualtDeduction(session, request);
            }
            else if (flag == 46) {
                send = rh.getDefualtTaxes(session, request);
            }
            else if (flag == 48) {
                send = rh.getGenerateSalaryList(session, request);
            }
            return (send);
        } catch (Exception e) {
        	e.printStackTrace();
            return (send = e.getMessage());
        } finally {
            session.close();
        }
    }

    public String setter(int flag, HttpServletRequest request) throws ServletException {
        Session session = HibernateUtil.getCurrentSession();
        payrollHandler rh = new payrollHandler();
        Transaction trans = session.beginTransaction();

        try {
            trans.begin();
            if (flag == 50) {
                send = rh.setWagesData(request,request.getParameter("name"), request.getParameter("rate"), request.getParameter("code"), AuthHandler.getCompanyid(request), session);
                typeWork = "";
            } else if (flag == 51) {
                send = rh.setTaxData(request,session);
                typeWork = "";
            } else if (flag == 52) {
                send = rh.setDeductionData(request,request.getParameter("name"), request.getParameter("rate"), request.getParameter("code"), AuthHandler.getCompanyid(request), session);
                typeWork = "";
            } else if (flag == 53) {
                send = rh.setCompanyData(request.getParameter("name"), request.getParameter("website"), request.getParameter("address"), request.getParameter("currency"), session);
                typeWork = "Added New Company";
            } else if (flag == 56) {
                send = rh.setTemplateData(request, session);
                typeWork = "";
            } else if (flag == 57) {
                if (request.getParameterValues("empidarr") == null) {
                    send = rh.AssignEmptoTemp(null, request.getParameter("tid"), session);
                    typeWork = "Employee unassignment to template";
                }
                String[] ti = request.getParameterValues("empidarr");
                send = rh.AssignEmptoTemp(ti, request.getParameter("tid"), session);
                if (send.equals("1")) {
                    trans.rollback();
                    send = "Salary not in template range";
                } else {
                    typeWork = "Assigned employee to template -" + request.getParameter("TempName");
                }

            } else if (flag == 58) {
                send = rh.setPayHistory(request, request.getParameter("stdate"), request.getParameter("enddate"), request.getParameter("tempid"), request.getParameter("empid"), request.getParameter("empname"), request.getParameter("design"), request.getParameter("gross"), request.getParameter("net"), request.getParameter("wagetot"), request.getParameter("taxtot"), request.getParameter("deductot"), request.getParameter("paymonth"), request.getParameter("WageJson"), request.getParameter("DeducJson"), request.getParameter("taxesJson"), session);
                typeWork = "Generate employee payslip";
            } else if (flag == 59) {
                send = rh.updateTemplateData(request, session);
                typeWork = "";

            } else if (flag == 64) {
                send = rh.setPayrollforTemp(request, session);
                typeWork = "";
            }
            else if (flag == 65) {
                send = rh.setNewIncometax(request, session);
                typeWork = "Added new income tax";
            }
            
//            trans.commit();
//            session.close();
//            session=HibernateUtil.getCurrentSession();
//            trans=session.beginTransaction();
//            trans.begin();
            //rh.addToTrail(typeWork, request.getRemoteAddr(), AuthHandler.getCompanyid(request), request.getSession().getAttribute("username").toString(), session);
            if(!typeWork.equals("")){
                ProfileHandler ph = new ProfileHandler();
                //@@ph.insertAuditLog(session, "88", request.getSession().getAttribute("username").toString() + typeWork, request);
            }
            trans.commit();
            return (send);

        } catch (Exception e) {
            if (trans != null) {
                trans.rollback();
            }

            return (send = e.getMessage());

        } finally {
            HibernateUtil.closeSession(session);

        }

    }

    public String remover(int flag, HttpServletRequest request) throws ServletException {
        Session session = HibernateUtil.getCurrentSession();
        payrollHandler rh = new payrollHandler();
        Transaction trans = session.beginTransaction();
        try {
            if (flag == 1) {
                send = rh.deleteTemplateData(request.getParameterValues("tempid"), session,request);
                typeWork = "Template Deleted(" + request.getParameter("tempid") + ")";
            } else if (flag == 3) {
                send = rh.deleteMasterTax(request.getParameter("typeid"), session,request);
                typeWork = "MasterTax Deleted(" + request.getParameter("typeid") + ")";
            }
            else if (flag == 4) {
                send = rh.deleteincomeTax(request.getParameter("typeid"), session);
                typeWork = "Income Tax Deleted( " + request.getParameter("typeid") + ")";
            }else if (flag == 5) {
                send = rh.deleteMasterWage(request.getParameter("typeid"), session,request);
                typeWork = "MasterWage Deleted(" + request.getParameter("typeid") + ")";
            } else if (flag == 7) {
                send = rh.deleteMasterDeduc(request.getParameter("typeid"), session,request);
                typeWork = "MasterDeduction Deleted(" + request.getParameter("typeid") + ")";
            }
            rh.addToTrail(typeWork, request.getRemoteAddr(), AuthHandler.getCompanyid(request), AuthHandler.getUserName(request), session);
            trans.commit();
            return (send);
        } catch (Exception e) {
            if (trans != null) {
                trans.rollback();
            }

            return (send = e.getMessage());

        } finally {
            HibernateUtil.closeSession(session);

        }


    }
}
