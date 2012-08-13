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

import com.krawler.common.admin.Company;
import com.krawler.common.admin.Role;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.spring.common.KwlReturnMsg;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import org.hibernate.Query;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;


/**
 *
 * @author Kuldeep Singh
 */
public class organizationChartDAOImpl implements organizationChartDAO {

    private HibernateTemplate hibernateTemplate;
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public KwlReturnObject getUnmappedUsers(String companyid) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {

            Company company = (Company) hibernateTemplate.get(Company.class, companyid);
            User userC = (User) company.getCreator();

            String Hql = "from User where userID IN (select userID from Empprofile where termnd ='F' and reportto.userID is null ) and company.companyID = ? and deleteflag=0 and userID != ?";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql, new Object[]{company.getCompanyID(), userC.getUserID()});
            dl = ll.size();

        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("organizationChartDAOImpl.getUnmappedUsers", ex);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    @Override
    public boolean insertNode(String parentid, String childid) throws Exception {

        boolean success = false;
        try {

            Empprofile empprofile = (Empprofile) hibernateTemplate.load(Empprofile.class, childid);
            User parent = getUser(parentid);

            empprofile.setReportto(parent);
            hibernateTemplate.save(empprofile);
            success = true;
        } catch (Exception ex) {
            success = false;
            throw ServiceException.FAILURE("organizationChartDAOImpl.insertNode", ex);
        }
        return success;
    }

    @Override
    public Empprofile deleteNode(String nodeUserid) throws ServiceException {

        Empprofile nodeUser=null;
        try {

            nodeUser = (Empprofile) hibernateTemplate.get(Empprofile.class, nodeUserid);
            nodeUser.setReportto(null);
            hibernateTemplate.update(nodeUser);

        } catch (Exception ex) {
            throw ServiceException.FAILURE("organizationChartDAOImpl.deleteNode", ex);
        }
        return nodeUser;
    }



    @Override
    public User getParentNode(String nodeUserid) throws ServiceException {

        User superParentObj = null;
        try {
        	
            String getParent = "from Empprofile where userID = ? ";
            
            List<Empprofile> parentList = HibernateUtil.executeQuery(hibernateTemplate, getParent, new Object[]{nodeUserid});
            
            for(Empprofile empParent : parentList){
            	if(empParent !=null){
                	superParentObj = empParent.getReportto();
            	} 
            }

        } catch (ServiceException ex) {
        	throw ServiceException.FAILURE("organizationChartDAOImpl.getParentNode", ex);
        }

        return superParentObj;
    }

    @Override
    public List<Empprofile> getChildNode(String nodeUserid) throws ServiceException {

        List<Empprofile> childList = new ArrayList<Empprofile>();
        try {

            String getChild = "from Empprofile a where a.reportto.userID = ? ";
            childList = HibernateUtil.executeQuery(hibernateTemplate, getChild, new Object[]{nodeUserid});


        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("organizationChartDAOImpl.getParentNode", ex);
        }

        return childList;
    }

    @Override
    public boolean assignNewParent(final String[] empProfileIds, final User parentObj) throws ServiceException{
        boolean success = false;
        int numRow = 0;
        try{
            final String hql1 = "Update Empprofile set reportto= '"+parentObj.getUserID()+"' where userID in (:userids) ";
            numRow = (Integer) hibernateTemplate.execute(new HibernateCallback() {

                @Override
                public Object doInHibernate(Session session) {
                    int numRows = 0;
                    Query query = session.createQuery(hql1);
                    if (empProfileIds != null) {

                        query.setParameterList("userids", empProfileIds);

                    }
                    numRows = query.executeUpdate();
                    return numRows;
                }

            });

            success = true;
        }catch(Exception ex){
            success = false;
            throw ServiceException.FAILURE("organizationChartDAOImpl.assignNewParent", ex);
        }finally{
           return success;
        }
    }

    @Override
    public boolean updateNode(String parentid, String childid) throws Exception {
        List ll = null;
        boolean success = false;

        try {

            User parent = getUser(parentid);

            Empprofile empProfile = null;

            String Hql = "from Empprofile where userID = ? ";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql, new Object[]{childid});
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                empProfile = (Empprofile) ite.next();
                empProfile.setReportto(parent);
                hibernateTemplate.update(empProfile);
            }
            success = true;

        } catch (ServiceException ex) {
            success = false;
            throw ServiceException.FAILURE("organizationChartDAOImpl.updateNode", ex);
        }
        return success;
    }

    @Override
     public void getAssignManager(String manID, List appendList, int exceptionAt, String extraQuery) throws ServiceException {
    	Stack mStack = new Stack();mStack.add(manID);
    	Map map = new HashMap();
    	List list = new ArrayList();
    	visit(mStack, null, map, new ArrayList(), list, extraQuery);
    	Collections.reverse(list);
    	appendList.addAll(list);
    	appendList.addAll(map.values());
    }

    private void visit(Stack mStack, Empprofile empProfile, Map map,  List visited, List appendList, String extraQuery) throws ServiceException{
    	if(!visited.contains(mStack.peek())){
    		visited.add(mStack.peek());
    		String Hql = "from Empprofile where reportto.userID = ? " + extraQuery;
            List<Empprofile> ll = HibernateUtil.executeQuery(hibernateTemplate, Hql, new Object[]{mStack.peek()});
            for(Empprofile ep:ll) {

                if(!ep.isTermnd()){
                    String empID = ep.getUserID();
                    if(mStack.contains(empID))
                        throw ServiceException.FAILURE("Circular Hierarchy found for userid "+empID, null);
                    mStack.push(empID);
                    visit(mStack, ep, map, visited, appendList, extraQuery);
                    mStack.pop();
                }
            	
            }
            if(empProfile!=null)
            	appendList.add(empProfile);
    	}else{
            if(empProfile!=null) map.put(mStack.peek(), empProfile);
    	}
    }

    @Override
    public Useraccount rootUser(String userid, String companyid) throws ServiceException {

        Useraccount useracc = null;
        try {
            Empprofile empProfile = (Empprofile) hibernateTemplate.get(Empprofile.class, userid);
            Company company = (Company) hibernateTemplate.get(Company.class, companyid);
            User userC = (User) company.getCreator();
            if(empProfile.getReportto()!=null || StringUtil.equal(userid, userC.getUserID())){

                useracc = (Useraccount) hibernateTemplate.get(Useraccount.class, userid);

            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("organizationChartDAOImpl.rootUser", ex);
        }

        return useracc;
    }

    @Override
    public boolean isPerfectRole(String parentid, String childid) throws ServiceException {
        boolean perfect = false;

        Useraccount parent = (Useraccount) hibernateTemplate.get(Useraccount.class, parentid);
        Useraccount child = (Useraccount) hibernateTemplate.get(Useraccount.class, childid);
        String parentRole = parent.getRole().getID();


        if(!StringUtil.equal(parentRole, Role.COMPANY_USER)){

            perfect = true;
        }

        return perfect;
    }

    @Override
    public User getUser(String userid) throws ServiceException{

        User user = (User) hibernateTemplate.load(User.class, userid);
        return user;

    }
}
