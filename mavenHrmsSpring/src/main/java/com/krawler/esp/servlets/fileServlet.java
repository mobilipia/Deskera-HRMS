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
package com.krawler.esp.servlets;

import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.hrms.common.docs.HrmsDocmap;
import com.krawler.hrms.common.docs.HrmsDocs;
import com.krawler.esp.handlers.AuthHandler;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import org.apache.commons.fileupload.FileItem;
import java.util.HashMap;
import com.krawler.esp.handlers.fileUploader;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class fileServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Session session = null;
        Transaction tx = null;
        String userid = null;
        boolean flag = true;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            HashMap<String, String> arrParam = new HashMap<String, String>();
            boolean fileUpload = false;
            ArrayList<FileItem> fi = new ArrayList<FileItem>();
            if (request.getParameter("fileAdd") != null) {
                fileUploader.parseRequest(request, arrParam, fi, fileUpload);
            }
            if (arrParam.get("applicantid").equalsIgnoreCase("applicant")) {
                userid = arrParam.get("refid");
                flag = false;
            } else {
                userid = AuthHandler.getUserid(request);
                flag = true;
            }

            for (int cnt = 0; cnt < fi.size(); cnt++) {
                HrmsDocs doc = fileUploader.uploadFile(session, fi.get(cnt), userid, arrParam, flag);
                HrmsDocmap docMap = new HrmsDocmap();
                docMap.setDocid(doc);
                docMap.setRecid(arrParam.get("refid"));
                session.save(docMap);
            }
        } catch (SessionExpiredException ex) {
            if (tx != null) {
                tx.rollback();
            }
        } catch (ServiceException e) {
            if (tx != null) {
                tx.rollback();
            }
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            tx.commit();
            HibernateUtil.closeSession(session);
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
