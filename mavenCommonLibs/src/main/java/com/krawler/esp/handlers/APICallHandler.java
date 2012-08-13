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

import com.krawler.common.admin.Apiresponse;
import com.krawler.common.admin.Company;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class APICallHandler {

    private static String apistr = "remoteapi.jsp";

    public static JSONObject callApp(String appURL, JSONObject jData, String companyid, String action) {
        JSONObject resObj = new JSONObject();
        boolean result = false;
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getCurrentSession();
            tx = session.beginTransaction();
            PreparedStatement pstmt = null;
            String uid = UUID.randomUUID().toString();
            Apiresponse apires = new Apiresponse();
            apires.setApiid(uid);
            apires.setCompanyid((Company) session.get(Company.class, companyid));
            apires.setApirequest("action=" + action + "&data=" + jData.toString());
            apires.setStatus(0);
            session.save(apires);

            String res = "{}";
            InputStream iStream = null;
            try {
                String strSandbox = appURL + apistr;
                URL u = new URL(strSandbox);
                URLConnection uc = u.openConnection();
                uc.setDoOutput(true);
                uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                java.io.PrintWriter pw = new java.io.PrintWriter(uc.getOutputStream());
                pw.println("action=" + action + "&data=" + jData.toString());
                pw.close();
                iStream = uc.getInputStream();
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(iStream));
                res = in.readLine();
                in.close();
                iStream.close();
            } catch (IOException iex) {
                Logger.getLogger(APICallHandler.class.getName()).log(Level.SEVERE, "IO Exception In API Call", iex);
            } finally {
                if (iStream != null) {
                    try {
                        iStream.close();
                    } catch (Exception e) {
                    }
                }
            }
            resObj = new JSONObject(res);

            if (!resObj.isNull("success") && resObj.getBoolean("success")) {
                result = true;

            } else {
                result = false;
            }

            apires = (Apiresponse) session.load(Apiresponse.class, uid);
            apires.setApiresponse(res);
            apires.setStatus(1);
            session.save(apires);
            tx.commit();
        } catch (JSONException ex) {
            if (tx != null) {
                tx.rollback();
            }
            Logger.getLogger(APICallHandler.class.getName()).log(Level.SEVERE, "JSON Exception In API Call", ex);
            result = false;

        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            Logger.getLogger(APICallHandler.class.getName()).log(Level.SEVERE, "Exception In API Call", ex);
            result = false;
        } finally {
            HibernateUtil.closeSession(session);
        }
        return resObj;
    }
}
