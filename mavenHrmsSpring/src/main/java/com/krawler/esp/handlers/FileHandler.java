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
package com.krawler.esp.handlers;

import com.krawler.esp.hibernate.impl.HibernateUtil;
import org.hibernate.*;
import java.util.*;
import com.krawler.common.service.ServiceException;

public class FileHandler {


     public static Hashtable<String, String> getfileinfo(Session session,
            String Id) throws ServiceException {
        Hashtable<String, String> ht = new Hashtable<String, String>();
        try{   
            List lst = HibernateUtil.executeQuery(session, "FROM " +
                "com.krawler.hrms.common.docs.HrmsDocs AS hrmsdocs1 where hrmsdocs1.docid =?",new Object[]{Id});
            Iterator ite = lst.iterator();
            while(ite.hasNext()){
                com.krawler.hrms.common.docs.HrmsDocs t = (com.krawler.hrms.common.docs.HrmsDocs) ite.next();
                ht.put("docid", t.getDocid());
                ht.put("Name", t.getDocname());
                ht.put("Size", t.getDocsize());
                ht.put("Type", t.getDoctype());
                ht.put("svnname", t.getStorename());
                ht.put("storeindex", String.valueOf(t.getStorageindex()));
            }
         } catch(HibernateException e){
            throw ServiceException.FAILURE("FileHandler.getfileinfo", e);
         }
        return ht;
    }

}
