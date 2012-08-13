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
package com.krawler.spring.organizationChart;

import com.krawler.common.admin.User;
import com.krawler.common.util.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.organizationChart.bizservice.OrganizationServiceDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author Kuldeep Singh
 */
public class organizationChartController extends MultiActionController implements MessageSourceAware {

    private OrganizationServiceDAO organizationService;
    private sessionHandlerImpl sessionHandlerImplObj;
    private HibernateTransactionManager txnManager;
    private String successView;
    private auditTrailDAO auditTrailDAOObj;
    private MessageSource messageSource;

    private static final Log logger = LogFactory.getLog(organizationChartController.class);

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setOrganizationServiceDAO(OrganizationServiceDAO organizationService) {
        this.organizationService = organizationService;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }

    public ModelAndView getUnmappedUsers(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        try {

            String companyid = sessionHandlerImplObj.getCompanyid(request);
            Locale locale =  RequestContextUtils.getLocale(request);
            jobj = organizationService.getUnmappedUsers(companyid, locale);

            jobj1.put("valid", true);
            jobj1.put("data", jobj);
            jobj1.put("success", true);

        } catch (Exception e) {
            logger.warn("General exception in getUnmappedUsers()", e);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    // Organisation Chart - insert node
    public ModelAndView insertNode(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        String retMsg = "";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {

            String parentid = StringUtil.checkForNull(request.getParameter("fromId"));
            String childid = StringUtil.checkForNull(request.getParameter("userid"));

            HashMap<String, Object> hm = organizationService.insertNode(parentid, childid);

            boolean success =Boolean.parseBoolean(hm.get("success").toString());
            User parent = (User) hm.get("parent");
            User child = (User) hm.get("child");

            if(parent!=null && child!=null){
                auditTrailDAOObj.insertAuditLog(AuditAction.ORGANIZATION_CHART_NODE_ASSIGNED,
                child.getFirstName() + " " + child.getLastName() + " re-assigned to " + parent.getFirstName() + " " + parent.getLastName(),
                request, "0");
            }

            if(success){
                retMsg = "{success:true}";
            }else {
                retMsg = "{success:false,msg:\""+messageSource.getMessage("hrms.common.not.assign.parent.node.employee.role", null, "Could not assign because, parent node has Employee role.", RequestContextUtils.getLocale(request))+"\"}";
            }
            jobj.put("data", retMsg);

            jobj1.put("valid", true);
            jobj1.put("data", jobj);
            jobj1.put("success", true);
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn("General exception in insertNode()", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView getMappedUsers(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        try {
            String userid = sessionHandlerImplObj.getUserid(request);
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            Locale locale =  RequestContextUtils.getLocale(request);
            jobj = organizationService.getMappedUsers(userid, companyid, locale);

            jobj1.put("valid", true);
            jobj1.put("data", jobj);
            jobj1.put("success", true);
        } catch (Exception e) {
            logger.warn("General exception in getMappedUsers()", e);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView getGridMappedUsers(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();

        try {
            String childid = request.getParameter("nodeid");
            String parentid = request.getParameter("parentid");
            String userid = sessionHandlerImplObj.getUserid(request);
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            Locale locale =  RequestContextUtils.getLocale(request);
            jobj = organizationService.getGridMappedUsers(parentid, childid, userid, companyid, locale);

            jobj1.put("valid", true);
            jobj1.put("data", jobj);
            jobj1.put("success", true);
        } catch (Exception e) {
            logger.warn("General exception in getGridMappedUsers()", e);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView updateNode(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        String retMsg = "";
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {

            String childid = request.getParameter("nodeid");
            String parentid = request.getParameter("fromId");

            HashMap<String, Object> hm = organizationService.updateNode(parentid, childid);

            boolean success =Boolean.parseBoolean(hm.get("success").toString());
            User parent = (User) hm.get("parent");
            User child = (User) hm.get("child");

            if(parent!=null && child!=null){
                auditTrailDAOObj.insertAuditLog(AuditAction.ORGANIZATION_CHART_NODE_ASSIGNED,
                child.getFirstName() + " " + child.getLastName() + " re-assigned to " + parent.getFirstName() + " " + parent.getLastName(),
                request, "0");
            }

            if(success){
                retMsg = "{success:true}";
            }else {
                retMsg = "{success:false,msg:\""+messageSource.getMessage("hrms.common.not.assign.parent.node.employee.role", null, "Could not assign because, parent node has Employee role.", RequestContextUtils.getLocale(request))+"\"}";
            }
            jobj.put("data", retMsg);

            jobj1.put("valid", true);
            jobj1.put("data", jobj);
            jobj1.put("success", true);
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn("General exception in updateNode()", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    public ModelAndView deleteNode(HttpServletRequest request, HttpServletResponse response) {
       JSONObject jobj = new JSONObject();
       JSONObject jobj1 = new JSONObject();
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {

            boolean success = false;
            String nodeid = request.getParameter("nodeId");
            
            if(!StringUtil.isNullOrEmpty(nodeid)){
            	HashMap<String, Object> deleteJobj = organizationService.deleteNode(nodeid);

                success =Boolean.parseBoolean(deleteJobj.get("success").toString());

                List<Empprofile> ll = (List<Empprofile>) deleteJobj.get("childList");
                Empprofile emp = null;

                if(deleteJobj.get("deletedEmployee")!=null){
                	emp = (Empprofile) deleteJobj.get("deletedEmployee");
                }

                User parentEmp = null;

                if(deleteJobj.get("parentEmployee")!=null){
                	parentEmp = (User) deleteJobj.get("parentEmployee");
                }

                if(parentEmp!=null && emp!=null) {

                    if(emp!=null ){

                        String details= emp.getUserLogin().getUserName() + " [ " +emp. getUserLogin().getUser().getFirstName()+" "+emp. getUserLogin().getUser().getLastName() + " ] Un-assigned from "+
                        parentEmp.getFirstName()+" "+parentEmp.getLastName() + " , and removed from Organization.";
                        auditTrailDAOObj.insertAuditLog(AuditAction.ORGANIZATION_CHART_NODE_DELETED,
                            details, request, "0");
                    }

                    for(Empprofile e : ll){
                         String details = e.getUserLogin().getUser().getFirstName()+" "+e.getUserLogin().getUser().getLastName() + "  re-assigned to "+
                                 parentEmp.getFirstName()+" "+parentEmp.getLastName() + "  ";

                         auditTrailDAOObj.insertAuditLog(AuditAction.ORGANIZATION_CHART_NODE_ASSIGNED,
                            details, request, "0");
                    }
                }

            }

            jobj.put("data", "{success:"+success+"}");
            
            jobj1.put("valid", true);
            jobj1.put("data", jobj);
            jobj1.put("success", true);
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn("General exception in deleteNode()", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }
}
