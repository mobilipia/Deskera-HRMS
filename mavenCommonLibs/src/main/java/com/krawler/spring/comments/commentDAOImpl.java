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
package com.krawler.spring.comments;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.admin.Comment;
import com.krawler.common.admin.NewComment;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.spring.common.KwlReturnMsg;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author Karthik
 */
public class commentDAOImpl implements commentDAO {

    private HibernateTemplate hibernateTemplate;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public KwlReturnObject getComments(String parentid) throws ServiceException {
        List ll = null;
        int dl = 0;
        String recid = StringUtil.checkForNull(parentid);
        try {
            String Hql = " FROM Comment c  where c.leadid=? order by c.postedon desc";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hql, new Object[]{recid});
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.getComments : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject deleteComments(String userid, String id) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String Hqldelcom = "from NewComment c where c.userId.userID= ? and c.commentid.Id=?";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hqldelcom, new Object[]{userid, id});
            dl = ll.size();
            if (dl > 0) {
                NewComment cnw = (NewComment) ll.get(0);
                hibernateTemplate.delete(cnw);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.deleteComments : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public KwlReturnObject addComments(JSONObject jobj) throws ServiceException {

        List ll = null;
        int dl = 0;
        try {
            Comment crmcomment = new Comment();
            if(jobj.has("userid")) {
                crmcomment.setuserId((User) hibernateTemplate.get(User.class, jobj.getString("userid")));
            }
            if(jobj.has("refid")) {
                crmcomment.setleadid(jobj.getString("refid"));
            }
            if(jobj.has("id")) {
                crmcomment.setId(jobj.getString("id"));
            }
            if(jobj.has("comment")) {
                crmcomment.setComment(jobj.getString("comment"));
            }
            if(jobj.has("mapid")) {
                crmcomment.setRelatedto(jobj.getString("mapid"));
            }
            crmcomment.setPostedon(new Date());

            hibernateTemplate.save(crmcomment);

            String Hqlc = " FROM User c  where   c.company.companyID= ? ";
            ll = HibernateUtil.executeQuery(hibernateTemplate, Hqlc, new Object[]{jobj.getString("companyid")});
            dl = ll.size();
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                User obj = (User) ite.next();
                if (!jobj.getString("userid").equals(obj.getUserID())) {
                    NewComment crmnewcomment = new NewComment();
                    crmnewcomment.setuserId((User) hibernateTemplate.get(User.class, obj.getUserID()));
                    crmnewcomment.setCid(jobj.getString("cid"));
                    crmnewcomment.setCommentid(crmcomment);
                    hibernateTemplate.save(crmnewcomment);
                }
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmCommentDAOImpl.addComments : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, dl);
    }

    public int getNewCommentList(String userid, String parentid) throws ServiceException {
        int count = 0;
        int newCommentdl = 0;
        try {
            String Hql = " FROM Comment c  where c.leadid=? ";
            List lst = HibernateUtil.executeQuery(hibernateTemplate, Hql, new Object[]{parentid});
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Comment t = (Comment) ite.next();
                String newCommentHql = "from NewComment  where  commentid.Id=? and userId.userID=?";
                List ncomlst = HibernateUtil.executeQuery(hibernateTemplate, newCommentHql, new Object[]{t.getId(), userid});
                newCommentdl = ncomlst.size();
                if (newCommentdl > 0) {
                    count++;
                }
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return count;
    }
}
