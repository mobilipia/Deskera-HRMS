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

import com.krawler.common.admin.hrms_Modules;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.esp.handlers.*;
import com.krawler.hql.payroll.payrollHandler;
import com.krawler.hrms.*;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.*;
import com.krawler.utils.json.base.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krawler
 */
public class hrmsDbcon {

    public static String getDemo(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getDummyFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getWidgetStatus(HttpServletRequest request) throws ServiceException {
        String result = "";
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            result = hrmsManager.getWidgetStatus(session,request);
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {tx.rollback();}
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return result;
    }

    public static String removeWidgetFromState(HttpServletRequest request) {
        String result = "";
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            result = hrmsManager.removeWidgetFromState(session,request);
            tx.commit();
        } catch (JSONException ex) {
            if (tx != null) {tx.rollback();}
            Logger.getLogger(hrmsDbcon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SessionExpiredException ex) {
            if (tx != null) {tx.rollback();}
            Logger.getLogger(hrmsDbcon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            if (tx != null) {tx.rollback();}
            Logger.getLogger(hrmsDbcon.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return result;
    }

    public static String insertWidgetIntoState(HttpServletRequest request) {
        String result = "";
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            result = hrmsManager.insertWidgetIntoState(session,request);
            tx.commit();
        } catch (JSONException ex) {
            if (tx != null) {tx.rollback();}
            Logger.getLogger(hrmsDbcon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SessionExpiredException ex) {
            if (tx != null) {tx.rollback();}
            Logger.getLogger(hrmsDbcon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServiceException ex) {
            if (tx != null) {tx.rollback();}
            Logger.getLogger(hrmsDbcon.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return result;
    }

    public static String changeWidgetStateOnDrop(HttpServletRequest request) throws SessionExpiredException{
        Transaction tx = null;
        Session session = null;
        String result="";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            result = hrmsManager.changeWidgetStateOnDrop(session, request);
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {tx.rollback();}
            Logger.getLogger(hrmsDbcon.class.getName()).log(Level.SEVERE, null, ex);
        }catch (JSONException ex) {
            if (tx != null) {tx.rollback();}
            Logger.getLogger(hrmsDbcon.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            HibernateUtil.closeSession(session);
        }
        return result;
    }

    public static String getUsersData(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getAllUserDetails(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String addDemo(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.Insertdummyfunction(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getInternalJobs(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getInternalJobs(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String insertInternalJobs(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj=hrmsHandler.InternalJobpositions(session, request);           
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }

    public static String deleteinternaljobs(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj=hrmsHandler.DeleteInternalJobs(session, request);            
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }

    public static String applyforinernalJobs(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.ApplyforInternalJobs(session, request);
            //hrmsHandler.ApplyforInternalJobs(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String addMasterField(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.addMasterField(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String addMasterDataField(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.addMasterDataField(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String insertTimeSheet(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsHandler.insertTimeSheet(session, request);                      
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String insertGoal(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.insertGoal(session, request);            
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getAllEmployeeGoals(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.GoalandEmployees(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getMasterField(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getMasterField(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getManager(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getManager(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getSomeUserData(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getSomeUserData(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getMasterDataField(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getMasterDataField(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getCompetency(HttpServletRequest request, int start, int limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getCompetencyFunction(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String addCompetency(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj=hrmsHandler.addCompetencyFunction(session, request);           
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }

    public static String getCompetency2(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getCompetencyFunction2(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String assignCompetency(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.assignCompetencyFunction(session, request);           
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getCompAndDesig(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getCompAndDesigFunction(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String deleteComp(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj=hrmsHandler.deleteCompFunction(session, request);            
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }

    public static String editComp(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.editCompFunction(session, request);          
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String showComp(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.showCompFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String addempappraisal(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj = hrmsHandler.AppraisalAssign(session, request);                     
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();

    }

    public static com.krawler.utils.json.base.JSONObject getAppraisalforemployee(HttpServletRequest request) throws ServiceException, JSONException {
        com.krawler.utils.json.base.JSONObject jobj = new com.krawler.utils.json.base.JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj = hrmsManager.Employeesappraisal(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj;
    }

    public static String getallappraisals(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.AllEmployeesappraisal(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getTimesheet(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.EmployeesTimesheet(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getAllTimesheets(HttpServletRequest request, Integer start, Integer limit) throws JSONException, SessionExpiredException, ParseException, ServiceException, SQLException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            str = hrmsManager.AllTimesheets(session, request, start, limit);
        } catch (JSONException ex) {
            throw ServiceException.FAILURE("hrmsDbcon.getAllTimesheets", ex);
        }catch (ParseException ex) {
            throw ServiceException.FAILURE("hrmsDbcon.getAllTimesheets", ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String ApproveTimesheets(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.AlltimesheetsApproval(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;

    }

    public static String addCompensation(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.addcompensationFunction(session, request);         
             str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getCompensation(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getCompensationFunction(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String finalemployeegoals(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.Employeesgoalfinal(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getAppraisalCycles(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getAppraisalCycles(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getCompetency3(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getCompetencyFunction3(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String showCompGrid(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.showCompGridFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String delDesigComp(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.delDesigCompFunction(session, request);         
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String showInternalApplicants(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.showInternalApplicantsFunction(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String addAgency(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.addAgencyFunction(session, request);          
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String showAgency(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.showAgencyFunction(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String delAgency(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.delAgencyFunction(session, request);           
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getsearchjobs(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.jobsearch(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String addRecruiters(HttpServletRequest request) throws ServiceException,HibernateException  {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.addRecruitersFunction(session, request);           
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (HibernateException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String applyforexternaljob(HttpServletRequest request) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.applyforjobexternal(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getAppliedJobsExt(HttpServletRequest request, Integer start, Integer limit) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.GetExtapplyjobs(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String scheduleinterview(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.scheduleinterview(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String adminallappssave(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.adminallapps(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String deleteallappsadmin(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.Allappsdelete(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String viewRecruiters(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.viewRecruitersFunction(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
   
    public static String allappsformsave(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.allappsformsave(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String deleteassignedgoals(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.assignedgoalsdelete(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String deletejobstimesheet(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj = hrmsHandler.deletetimesheetjobs(session, request);

            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }

    public static String applyAgency(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.applyAgencyFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

  public static String getfinalReport(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getfinalReportFunction(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
  public static String getfinalReportWithColumns(HttpServletRequest request, Integer start, Integer limit) throws ServiceException {
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getfinalReportWithColumns(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String assignManager(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.assignManagerFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getassignManager(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getassignManagerFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String updateinernalJobs(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.updateinernalJobsFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String extusersetpass(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.extusrchngpass(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getAlerts(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getAlertsFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getEmpForManager(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getEmpForManagerFunction(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getappraisaltype(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getappraisaltypeFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

  public static String viewagencyJobs(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.viewagencyJobsFunction(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getChart(String name, HttpServletRequest request) throws ServiceException, JSONException {
        Session session = null;
        Transaction tx = null;
        // JSONObject jObj = new JSONObject();
        String ret = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            ret = hrmsManager.getChart(name, session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return ret;
    }

    public static String deletemasterdata(HttpServletRequest request) throws ServiceException, JSONException, Exception {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj = hrmsHandler.deletemasterdata(session, request);
            tx.commit();
            str = "{\"success\":\"true\"}";
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw new Exception("{\"data\":\"Can not delete Master data due to dependency\"}");
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }

    public static String archivedgoals(HttpServletRequest request, int start, int limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.archivedgoalsfunction(session, request, start, limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getEmpSchedule(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getEmpSchedule(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static JSONObject getPerformanceData(int id) throws ServiceException {
        JSONObject jobj = new JSONObject();
        //JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj = hrmsManager.getPerformanceDataFunction(session, id);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return (jobj);
    //return jobj;
    }

    public static String addComments(HttpServletRequest request) throws ServiceException, SessionExpiredException {
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.addCommentsfunction(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (HibernateException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (SessionExpiredException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getComments(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            str = hrmsManager.getComments(session, request);
        } catch (JSONException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }catch (SessionException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }


    public static String getDashboard(HttpServletRequest request) throws ServiceException {
        String ret = " ";
        try {
                ret = getCompanyDashboard(request);
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);

        } finally {
        }
        return ret;
    }

    public static String getDashboardLinks(HttpServletRequest request,int flag) throws ServiceException {
        String ret = " ";
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            JSONObject perms = PermissionHandler.getPermissions(session, AuthHandler.getUserid(request));
            switch(flag) {
                case 6 :
                    ret = hrmsDashboard.getHRMSadministrationLinks(request, perms);
                    break;
                case 7 :
                    ret = hrmsDashboard.getHRMSrecruitmentLinks(request, perms);
                    break;
                case 8 :
                    ret = hrmsDashboard.getHRMSpayrollLinks(request, perms);
                    break;
                case 9 :
                    ret = hrmsDashboard.getHRMStimesheetLinks(request, perms);
                    break;
                case 10 :
                    ret = hrmsDashboard.getHRMSperformanceLinks(request, perms);
                    break;
            }

        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);

        } finally {
        }
        return ret;
    }

    public static String getSuperAdminDashboard(HttpServletRequest request) throws ServiceException {
        Session session = null;
        String ret = " ";
        StringBuilder sbTemp = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"DashboardContent\" class=\"dashboardcontent\">");

        try {
            session = HibernateUtil.getCurrentSession();

            sb.append("<div class=\"statuspanelouter\"><div class=\"statuspanelinner\">");
            StringBuilder sbTasks = new StringBuilder();

            sbTasks = hrmsDashboard.getSuperAdminDashboardUpdateList(session, request, sb);
            hrmsDashboard.getSectionHeader(sb, "<span style='float: left;'>Updates</span><span style='float: right;font-weight:normal'></span>");
            sb.append(sbTasks);
            sb.append("</div></div>");

            sb.append("<div class=\"linkspanel\">");

            hrmsDashboard.getSectionHeader(sb, "SuperAdmin");
            sb.append("<ul id=\"superadminlinksUL\">");
//            sbTemp = hrmsDashboard.getSuperAdminDashboardList(session, request);
//            sb.append(sbTemp);
            sb.append("</ul>");
            sb.append("<div>&nbsp;</div>");

            ret = sb.toString();

        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);

        } finally {
            HibernateUtil.closeSession(session);
        }
        return ret;
    }

    public static String getCompanyDashboard(HttpServletRequest request) throws ServiceException {
        Session session = null;
        String ret = " ";
        StringBuilder sbTemp = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"DashboardContent\" class=\"dashboardcontent\">");

        try {

            session = HibernateUtil.getCurrentSession();
            JSONObject perms = PermissionHandler.getPermissions(session, AuthHandler.getUserid(request));
            String cmpsub = AuthHandler.getCmpSubscription(request);
            /* div 1*/ sb.append("<div class=\"statuspanelouter\"><div class=\"statuspanelinner\">");
            StringBuilder sbTasks = new StringBuilder();

            sbTasks = hrmsDashboard.getDashboardUpdateList(session, request, sb);
            hrmsDashboard.getSectionHeader(sb, "<span style='float: left;'>Updates</span><span style='float: right;font-weight:normal'></span>");
            sb.append(sbTasks);
            if (!PermissionHandler.isEmployee(session, request)) {
                hrmsDashboard.getGraphs(session, request, sb);
            }
            sb.append("</div></div>");



            /* div 2*/ sb.append("<div class=\"linkspanel\">");

            // if ((!PermissionHandler.isEmployee(session, request))) {
            sbTemp = hrmsDashboard.getHRMSadministrationLinks(session, request, perms);
            if (sbTemp.length() > 0) {
                hrmsDashboard.getSectionHeader(sb, "Administration");
                sb.append("<ul id=\"administrationlinks\">");
                sb.append(sbTemp);
                sb.append("</ul>");
                sb.append("<div>&nbsp;</div>");
            }
            //  }
            if (PermissionHandler.isSubscribed(hrms_Modules.recruitment, cmpsub)) {
                sbTemp = hrmsDashboard.getHRMSrecruitmentLinks(session, request, perms);
                if (sbTemp.length() > 0) {
                    hrmsDashboard.getSectionHeader(sb, "Recruitment Management");
                    sb.append("<ul id=\"recruitmentlinks\">");
                    sb.append(sbTemp);
                    sb.append("</ul>");
                    sb.append("<div>&nbsp;</div>");
                }
            }

            if (PermissionHandler.isSubscribed(hrms_Modules.payroll, cmpsub)) {
                if (PermissionHandler.isPermitted(perms, "payroll", "view")) {
                    if (sbTemp.length() > 0) {
                        sbTemp = hrmsDashboard.getHRMSpayrollLinks(session, request, perms);
                        hrmsDashboard.getSectionHeader(sb, "Payroll");
                        sb.append("<ul id=\"payrolllinks\">");
                        sb.append(sbTemp);
                        sb.append("</ul>");
                        sb.append("<div>&nbsp;</div>");
                    }
                }
            }

            if (PermissionHandler.isSubscribed(hrms_Modules.timesheet, cmpsub)) {
                sbTemp = hrmsDashboard.getHRMStimesheetLinks(session, request, perms);
                if (sbTemp.length() > 0) {
                    hrmsDashboard.getSectionHeader(sb, "Timesheet Management");
                    sb.append("<ul id=\"hrmstmshtlinksUL\">");
                    sb.append(sbTemp);
                    sb.append("</ul>");
                    sb.append("<div>&nbsp;</div>");
                }
            }

            if (PermissionHandler.isSubscribed(hrms_Modules.appraisal, cmpsub)) {
                sbTemp = hrmsDashboard.getHRMSperformanceLinks(session, request, perms);
                if (sbTemp.length() > 0) {
                    hrmsDashboard.getSectionHeader(sb, "Performance Appraisal");
                    sb.append("<ul id=\"PerformanceAppraisallinks\">");
                    sb.append(sbTemp);
                    sb.append("</ul>");
                    sb.append("<div>&nbsp;</div>");
                }
            }

//            sbTemp = hrmsDashboard.getHRMSessLinks(session, request, perms);
//            if (sbTemp.length() > 0) {
//                hrmsDashboard.getSectionHeader(sb, "Employee Self Service");
//                sb.append("<ul id=\"ess\">");
//                sb.append(sbTemp);
//                sb.append("</ul>");
//                sb.append("<div>&nbsp;</div>");
//            }
            sb.append("</div>");
            sb.append("</div>");
            ret = sb.toString();


        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);

        } finally {
            HibernateUtil.closeSession(session);
        }
        return ret;
    }
public static String GetApplicant(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.GetApplicant(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
public static String getRecruiters(HttpServletRequest request,Integer start, Integer limit) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getRecruitersFunction(session, request,start,limit);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }


       public static String saveempprofile(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj=hrmsHandler.saveempprofile(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }


    public static String getEmpidFormat(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getEmpidFormat(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

        public static String getempexperience(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.GetEmpExperience(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getEmpData(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.empProfile(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String deleteEmpexperience(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.EmpExperiencedelete(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }


      public static String getEmpDocuments(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, JSONException {
        Session session = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            str = hrmsManager.GetEmpDocuments(session, request, start, limit);
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }


    public static String createapplicant(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj=hrmsHandler.createapplicantFunction(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }

    public static String getapplicantdata(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getapplicantdataFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

        public static String update_profile_status(HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            ProfileHandler.update_profile_status(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (SessionExpiredException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

        public static String getGroupheader(HttpServletRequest request) throws ServiceException, JSONException {
        Session session = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            str = hrmsManager.getGroupheader(session, request);
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getjobProfile(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getjobprofileFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String addjobProfile(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.addjobprofileFunction(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String viewjobProfile(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.viewjobprofileFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
    public static String getJobidFormat(HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        Session session = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            str = hrmsManager.getJobidFormat(session, request);
        } catch (JSONException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (SessionExpiredException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
     public static JSONObject getTimesheetReport(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj = hrmsManager.EmployeesTimesheetReport(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj;
    }
    public static JSONObject saveReportTemplate(HttpServletRequest request) throws ServiceException, JSONException {
        Session session = null;
        Transaction tx = null;
        JSONObject result = new JSONObject();
        try {
            result.put("success", false);
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            result = hrmsHandler.saveReportTemplate(session, request);
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {
                tx.rollback();
            }
        } catch (JSONException ex) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            HibernateUtil.closeSession(session);
        }
        return result;
    }

    public static JSONObject getAllReportTemplate(HttpServletRequest request) throws ServiceException {
        Session session = null;
        JSONObject result = new JSONObject();
        try {
            session = HibernateUtil.getCurrentSession();
            result = hrmsHandler.getAllReportTemplate(session, request);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return result;
    }

    public static JSONObject deleteReportTemplate(HttpServletRequest request) throws ServiceException {
        Session session = null;
        JSONObject result = new JSONObject();
        try {
            session = HibernateUtil.getCurrentSession();
            result = hrmsHandler.deleteReportTemplate(session, request);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return result;
    }

    public static String getappraisalList(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getappraisallistFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getUserList(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getUserList(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String appraisal(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj=hrmsHandler.appraisalFunction(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }

    public static String getappraisalCompetency(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getappraisalCompetencyFunction(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getappraisalGoals(String crmURL,HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            if(!StringUtil.isStandAlone()){
                session = HibernateUtil.getCurrentSession();
                tx = session.beginTransaction();
                HashMap GoalRate  =hrmsManager.updateappraisalGoalsFromCrm(crmURL,session, request);
                str = hrmsManager.getappraisalGoalsFunction(session, request,GoalRate);
                tx.commit();
            }    
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getTimesheetChart(HttpServletRequest request) throws ServiceException, SessionExpiredException, ParseException {
        Session session = null;
        String result =" ";
        try {
            session = HibernateUtil.getCurrentSession();
            result = hrmsManager.getAllReportChart(session, request);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return result;
    }
    public static String getTimesheetChart1(HttpServletRequest request) throws ServiceException, SessionExpiredException, ParseException {
        Session session = null;
        String result =" ";
        try {
            session = HibernateUtil.getCurrentSession();
            result = hrmsManager.getAllReportChart1(session, request);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return result;
    }

    public static String deleteApplicants(HttpServletRequest request) throws ServiceException, SessionExpiredException, HibernateException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.deleteApplicants(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (HibernateException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getappraisalFunction(HttpServletRequest request, Integer start, Integer limit) throws ServiceException, SessionExpiredException, ParseException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            str = hrmsManager.getappraisalFunction(session, request, start, limit);
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
    public static JSONObject setInterviewerConfirmation(String cmp,String user,HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj = hrmsHandler.setConfirmation(session, request,cmp,user);
            tx.commit();
        } catch (JSONException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }catch (SessionException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj;
    }

    public static String reviewAppraisal(HttpServletRequest request) throws ServiceException, HibernateException,JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
           jobj= hrmsHandler.reviewappraisalFunction(session, request);
            //str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (HibernateException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }

    public static String transferappData(HttpServletRequest request) throws ServiceException, HibernateException, SessionExpiredException, UnsupportedEncodingException, NoSuchAlgorithmException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj=hrmsHandler.transferappdataFunction(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (HibernateException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }
      public static String terminateEmp(HttpServletRequest request) throws ServiceException,HibernateException,SessionExpiredException,ParseException  {
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.terminateEmp(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
      public static String rehireEmp(HttpServletRequest request) throws ServiceException,HibernateException,ParseException, SessionExpiredException  {
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.rehireEmp(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
      public static String Emppaysliphst(HttpServletRequest request) throws ServiceException,HibernateException,ParseException, SessionExpiredException  {
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str=payrollHandler.empPayhist(session, request);
            //str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

      public static String setappraisalCycle(HttpServletRequest request) throws ServiceException, HibernateException, SessionExpiredException, UnsupportedEncodingException, NoSuchAlgorithmException, JSONException, ParseException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj=hrmsHandler.setappraisalcycleFunction(session, request);
            tx.commit();
        } catch (HibernateException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }

    public static String getappraisalCycle(HttpServletRequest request) throws ServiceException, SessionExpiredException, ParseException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            str = hrmsManager.getappraisalcycleFunction(session, request);
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
        public static String deleteDocuments(HttpServletRequest request) throws ServiceException, HibernateException {
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.deleteDocuments(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (HibernateException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getratingData(HttpServletRequest request) throws ServiceException, SessionExpiredException, ParseException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            str = hrmsManager.getratingdataFunction(session, request);
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getallEmployee(HttpServletRequest request) throws ServiceException, SessionExpiredException, ParseException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            str = hrmsManager.getallemployeeFunction(session, request);
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String reviewanonymousAppraisal(HttpServletRequest request) throws ServiceException,HibernateException,SessionExpiredException,ParseException  {
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str=hrmsHandler.reviewanonymousAppraisal(session, request);
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getAssignedManagers(HttpServletRequest request) throws ServiceException,JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            str = hrmsManager.getAssignedManager(session, request);
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
    public static String getAppraisalReport(HttpServletRequest request) throws ServiceException,JSONException {
        Session session = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            str = hrmsManager.getAppraisalReport(session, request);
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
    public static String getAppraisalReportforGrid(HttpServletRequest request) throws ServiceException,JSONException {
        Session session = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            str = hrmsManager.getAppraisalReportforGrid(session, request);
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
     public static String reviewAppraisalReport(HttpServletRequest request) throws ServiceException,HibernateException,SessionExpiredException,ParseException  {
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str=hrmsHandler.reviewanonymousAppraisalReport(session, request);
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static String getUpdatesForWidgets(HttpServletRequest request) {
        String result = "";
        Transaction tx = null;
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            result = hrmsManager.getUpdatesForWidgets(session,request);
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {tx.rollback();}
            //Logger.getLogger(crmDbcon.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return result;
    }

    public static String approveAppraisalCycle(HttpServletRequest request) throws ServiceException,HibernateException,SessionExpiredException,ParseException  {
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.approveAppraisalCycle(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }

    public static JSONObject getDummyStatus(String cmp,String desg,HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;        
        try {
            session = HibernateUtil.getCurrentSession();            
            jobj = hrmsHandler.getDummyStatus(session, request,cmp, desg);            
        } catch (JSONException ex) {            
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }catch (SessionException ex) {            
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj;
    }
     public static JSONObject setDummyStatus(HttpServletRequest request) throws ServiceException,HibernateException,SessionExpiredException,ParseException, JSONException  {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj=hrmsHandler.setDummyStatus(session, request);
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj;
    }

    public static String sendappraisalEmail(HttpServletRequest request) throws ServiceException,HibernateException,SessionExpiredException,ParseException, JSONException  {
        Session session = null;
        Transaction tx = null;
        String str = "";
        JSONObject jobj = new JSONObject();
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj =hrmsHandler.sendappraisalemailFunction(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }
    public static String sendReviewerEmail(HttpServletRequest request) throws ServiceException,HibernateException,SessionExpiredException,ParseException, JSONException  {
        Session session = null;
        Transaction tx = null;
        String str = "";
        JSONObject jobj = new JSONObject();
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj =hrmsHandler.sendRevieweremailFunction(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }
    public static String sendappraisalreportEmail(HttpServletRequest request) throws ServiceException,HibernateException,SessionExpiredException,ParseException, JSONException  {
        Session session = null;
        Transaction tx = null;
        String str = "";
        JSONObject jobj = new JSONObject();
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            jobj =hrmsHandler.sendappraisalreportEmail(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (ServiceException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj.toString();
    }
    public static JSONObject getJobsforJsp(String cmp,String desg,HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        try {
            session = HibernateUtil.getCurrentSession();
            jobj = hrmsHandler.getjobsForjsp(session, request,cmp, desg);
        } catch (JSONException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }catch (SessionException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return jobj;
    }
    public static JSONObject saveJobsapplication(Session session,String desg,HttpServletRequest request,String cmp) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Transaction tx=null;
        try {
            tx = session.beginTransaction();
            jobj = hrmsHandler.saveJobs(session, request,cmp, desg);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return jobj;
    }
    public static JSONObject saveJobsapplication(Session session,String desg,HttpServletRequest request,String cmp,String colnumbers) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Transaction tx=null;
        try {
            tx = session.beginTransaction();
            jobj = hrmsHandler.saveJobs(session, request,cmp, desg,colnumbers);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return jobj;
    }
    public static String deletejobprofileData(HttpServletRequest request) throws ServiceException, HibernateException, JSONException {
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            hrmsHandler.deletejobprofileData(session, request);
            str = "{\"success\":\"true\"}";
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
    public static String getConfigData(HttpServletRequest request) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        Session session = null;
        Transaction tx = null;
        String str = "";
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            str = hrmsManager.getConfigData(session, request);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
        return str;
    }
    public static String getLocalTextforJsp(int caseid,String companyid){
        String Ltext="";
        try {
            Ltext= hrmsHandler.getLocalTextforJsp(caseid, companyid);
        } catch (ServiceException ex) {
            Logger.getLogger(hrmsDbcon.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Ltext;
    }
}
