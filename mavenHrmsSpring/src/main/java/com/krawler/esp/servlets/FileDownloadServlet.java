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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.krawler.common.util.KrawlerLog;
import com.krawler.esp.database.DBCon;
import com.krawler.esp.handlers.StorageHandler;

public class FileDownloadServlet extends HttpServlet {

    private static final long serialVersionUID = -7262043406413106392L;

    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException, ServiceException {
        try {
            Hashtable ht = DBCon.getfileinfo(request.getParameter("url"));
            String src = StorageHandler.GetDocStorePath1();
            if (request.getParameter("mailattch") != null) {
                src = src + ht.get("svnname");
            } else {
                src = src + ht.get("userid").toString() + "/" + ht.get("svnname");
            }

            File fp = new File(src);
            byte[] buff = new byte[(int) fp.length()];
            FileInputStream fis = new FileInputStream(fp);
            int read = fis.read(buff);
            javax.activation.FileTypeMap mmap = new javax.activation.MimetypesFileTypeMap();
            response.setContentType(mmap.getContentType(src));
            response.setContentLength((int) fp.length());
            response.setHeader("Content-Disposition", request.getParameter("dtype") + "; filename=\"" + ht.get("Name") + "\";");
            response.getOutputStream().write(buff);            
        } catch (IOException ex) {
            KrawlerLog.op.warn("Unable To Download File :" + ex.toString());
        }finally{
            if (response.getOutputStream() != null) {
                response.getOutputStream().flush();
                response.getOutputStream().close();
            }
        }

    }

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ServiceException ex) {
            Logger.getLogger(FileDownloadServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ServiceException ex) {
            Logger.getLogger(FileDownloadServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getServletInfo() {
        return "Short description";
    }
}

