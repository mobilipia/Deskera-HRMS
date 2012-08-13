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
package com.krawler.spring.auditTrailModule;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import java.util.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import com.krawler.common.admin.AuditAction;
import com.krawler.common.admin.AuditTrail;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.Search.SearchBean;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnMsg;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;

public class auditTrailDAOImpl implements auditTrailDAO {
    private HibernateTemplate hibernateTemplate;
    private storageHandlerImpl storageHandlerImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}
    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj1) {
        this.storageHandlerImplObj = storageHandlerImplObj1;
    }
    
    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    public void insertAuditLog(String actionid, String details, HttpServletRequest request, String recid, String extraid)  throws ServiceException{
        try {
            AuditAction action=(AuditAction)hibernateTemplate.load(AuditAction.class, actionid);
            insertAuditLog(action, details, request, recid, extraid);
        } catch (Exception e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public void insertAuditLog(String actionid, String details, HttpServletRequest request, String recid)  throws ServiceException{
        try {
            AuditAction action=(AuditAction)hibernateTemplate.load(AuditAction.class, actionid);
            insertAuditLog(action, details, request, recid, "0");
        } catch (Exception e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public void insertAuditLog(String actionid, String details, String ipAddress, String userid, String recid)  throws ServiceException{
        try {
            AuditAction action=(AuditAction)hibernateTemplate.load(AuditAction.class, actionid);
            User user=(User)hibernateTemplate.load(User.class, userid);
            insertAuditLog(action, details, ipAddress, user, recid, "0");
        } catch (Exception e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public void insertAuditLog(AuditAction action, String details, HttpServletRequest request, String recid, String extraid)  throws ServiceException{
        try {
            User user=(User)hibernateTemplate.load(User.class, sessionHandlerImplObj.getUserid(request));
            String ipaddr = null;
            if(StringUtil.isNullOrEmpty(request.getHeader("x-real-ip"))){
                ipaddr = request.getRemoteAddr();
            }else{
                ipaddr = request.getHeader("x-real-ip");
            }

            insertAuditLog(action, details, ipaddr, user, recid, extraid);
        } catch (Exception e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public void insertAuditLog(AuditAction action, String details, String ipAddress, User user, String recid, String extraid)  throws ServiceException{
        try {
            String aid = UUID.randomUUID().toString();
            AuditTrail auditTrail=new AuditTrail();
            auditTrail.setID(aid);
            auditTrail.setAction(action);
            auditTrail.setAuditTime(new Date());
            auditTrail.setDetails(details);
            auditTrail.setIPAddress(ipAddress);
            auditTrail.setRecid(recid);
            auditTrail.setUser(user);
            auditTrail.setExtraid(extraid);
            hibernateTemplate.save(auditTrail);

            ArrayList<Object> indexFieldDetails = new ArrayList<Object>();
              ArrayList<String> indexFieldName = new ArrayList<String>();
              indexFieldDetails.add(details);
               indexFieldName.add("details");
               indexFieldDetails.add(aid);
               indexFieldName.add("transactionid");
               indexFieldDetails.add(action.getID());
               indexFieldName.add("actionid");
               indexFieldDetails.add(ipAddress);
               indexFieldName.add("ipaddr");
               String userName = user.getUserLogin().getUserName()+" "+user.getFirstName()+" "+user.getLastName();
               indexFieldDetails.add(userName);
               indexFieldName.add("username");
               indexFieldDetails.add(auditTrail.getAuditTime());
               indexFieldName.add("timestamp");
                String indexPath = storageHandlerImplObj.GetAuditTrailIndexPath();
                com.krawler.esp.indexer.KrawlerIndexCreator kwlIndex = new com.krawler.esp.indexer.KrawlerIndexCreator();
                kwlIndex.setIndexPath(indexPath);
                com.krawler.esp.indexer.CreateIndex  cIndex = new com.krawler.esp.indexer.CreateIndex();
                cIndex.indexAlert(kwlIndex, indexFieldDetails, indexFieldName);

        } catch (Exception e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public KwlReturnObject getRecentActivityDetails(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        String recid = "";
        String companyid = "";
        try {
            if (requestParams.containsKey("recid") && requestParams.get("recid") != null) {
                recid = requestParams.get("recid").toString();
            }
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            String query = "from AuditTrail where user.company.companyID=? and recid=? order by auditTime desc";
            ll = HibernateUtil.executeQueryPaging(hibernateTemplate, query, new Object[]{companyid, recid}, new Integer[]{0, 15});
            dl = ll.size();

        } catch (Exception e) {
            throw ServiceException.FAILURE("detailPanelDAOImpl.getRecentActivityDetails : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getAuditData(HashMap<String, Object> requestParams) throws ServiceException {
        int start = 0;
        int limit = 30;
        String groupid = "";
        String searchtext = "";
        String companyid = "";
        List ll = null;
        int dl = 0;
        try {
            if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            if (requestParams.containsKey("groupid") && requestParams.get("groupid") != null) {
                groupid = requestParams.get("groupid").toString();
            }
            if (requestParams.containsKey("search") && requestParams.get("search") != null) {
                searchtext = requestParams.get("search").toString();
            }
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }

            String auditID = "";
            if (searchtext.compareTo("") != 0) {
                String query2 = searchtext + "*";
                SearchBean bean = new SearchBean();
                String indexPath = storageHandlerImplObj.GetAuditTrailIndexPath();
                String[] searchWithIndex = {"details", "ipaddr", "username"};
                Hits hitResult = bean.skynetsearchMulti(query2, searchWithIndex, indexPath);
                if (hitResult != null) {
                    Iterator itrH = hitResult.iterator();
                    while (itrH.hasNext()) {
                        Hit hit1 = (Hit) itrH.next();
                        org.apache.lucene.document.Document doc = hit1.getDocument();
                        auditID += "'" + doc.get("transactionid") + "',";
                    }
                    if (auditID.length() > 0) {
                        auditID = auditID.substring(0, auditID.length() - 1);
                    }
                }
            }

            if (groupid.compareTo("") != 0 && searchtext.compareTo("") != 0) {  /* query for both gid and search  */
                if (auditID.length() > 0) {
                    String query = "from AuditTrail where user.company.companyID=? and ID in (" + auditID + ") and action.auditGroup.ID = ? order by auditTime desc";
                    ll = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{companyid, groupid});
                    dl = ll.size();
                    ll = HibernateUtil.executeQueryPaging(hibernateTemplate, query, new Object[]{companyid, groupid}, new Integer[]{start, limit});
                } else {
                    dl = 0;
                    ll = new ArrayList();
                }
            } else if (groupid.compareTo("") != 0 && searchtext.compareTo("") == 0) { /* query only for gid  */
                String query = "from AuditTrail where user.company.companyID=? and action.auditGroup.ID = ? order by auditTime desc";
                ll = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{companyid, groupid});
                dl = ll.size();
                ll = HibernateUtil.executeQueryPaging(hibernateTemplate, query, new Object[]{companyid, groupid}, new Integer[]{start, limit});
            } else if (groupid.compareTo("") == 0 && searchtext.compareTo("") != 0) {  /* query only for search  */
                if (auditID.length() > 0) {
                    String query = "from AuditTrail where user.company.companyID=? and ID in (" + auditID + ")  order by auditTime desc";
                    ll = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{companyid});
                    dl = ll.size();
                    ll = HibernateUtil.executeQueryPaging(hibernateTemplate, query, new Object[]{companyid}, new Integer[]{start, limit});
                } else {
                    dl = 0;
                    ll = new ArrayList();
                }
            } else {        /* query for all  */
                String query = "from AuditTrail where user.company.companyID=?  order by auditTime desc";
                ll = HibernateUtil.executeQuery(hibernateTemplate, query, new Object[]{companyid});
                dl = ll!=null?ll.size():0;
                ll = HibernateUtil.executeQueryPaging(hibernateTemplate, query, new Object[]{companyid}, new Integer[]{start, limit});
            }
        } catch (IOException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getAuditGroupData() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String query = "from AuditGroup where groupName not in ('User', 'Company', 'Log in', 'Log out')";
            ll = HibernateUtil.executeQuery(hibernateTemplate, query);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject getAuditDetails(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            StringBuffer usersList = null;
            if(requestParams.containsKey("userslist"))
                usersList = (StringBuffer) requestParams.get("userslist");
            String groups = requestParams.get("groups").toString();
            int start = Integer.parseInt(requestParams.get("start").toString());
            int limit = Integer.parseInt(requestParams.get("limit").toString());
            int interval = Integer.parseInt(requestParams.get("interval").toString());
            String query = "from AuditTrail at where";
            if(StringUtil.isNullOrEmpty(usersList.toString())) {
                query += " at.user.userID in (" + usersList + ")  and";
            }
            query += " DATEDIFF(date(now()),date(at.auditTime)) <= ? and " +
                    "at.action.auditGroup.groupName in (" + groups + ") order by at.auditTime desc";
            ll = HibernateUtil.executeQuery(hibernateTemplate, query, interval);
            dl = ll.size();
            ll = HibernateUtil.executeQueryPaging(hibernateTemplate, query, new Object[]{interval}, new Integer[]{start, limit});
        } catch (Exception e) {
            throw ServiceException.FAILURE("auditTrailDAOImpl.getRecentActivityDetails : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, "002", "", ll, dl);
    }
}
