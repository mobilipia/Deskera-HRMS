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
import com.krawler.common.admin.Useraccount;
import com.krawler.common.service.ServiceException;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import java.util.List;

/**
 *
 * @author Kuldeep Singh
 */
public interface organizationChartDAO {

    public KwlReturnObject getUnmappedUsers(String companyid) throws ServiceException;

    public boolean insertNode(String parentid, String childid) throws Exception;

    public Empprofile deleteNode(String nodeid) throws Exception;

    public User getParentNode(String nodeid) throws ServiceException;

    public List<Empprofile> getChildNode(String nodeid) throws ServiceException;

    public boolean assignNewParent(String[] empProfileIds, User parentObj) throws ServiceException;

    public boolean updateNode(String parentid, String childid) throws Exception;

    public void getAssignManager(String manID, List appendList, int exceptionAt, String extraQuery) throws ServiceException;

    public Useraccount rootUser(String userid, String companyid) throws ServiceException;

    public boolean isPerfectRole(String parentid, String childid) throws ServiceException;

     public User getUser(String userid) throws ServiceException;
}
