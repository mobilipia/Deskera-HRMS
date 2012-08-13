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
package com.krawler.spring.authHandler;

import com.krawler.common.service.ServiceException;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.apache.commons.lang.RandomStringUtils;

/**
 *
 * @author Karthik
 */
public class authHandler {

    public static DateFormat getDateFormatter(String userTimeFormatId, String timeZoneDiff) throws ServiceException {
        SimpleDateFormat sdf = null;
        try {
            String dateformat = "";
            if (userTimeFormatId.equals("1")) {
                dateformat = "MMMM d, yyyy hh:mm:ss aa";
            } else {
                dateformat = "MMMM d, yyyy HH:mm:ss";
            }
            sdf = new SimpleDateFormat(dateformat);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZoneDiff));
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getDateFormatter", e);
        }
        return sdf;
    }

    public static DateFormat getPrefDateFormatter(String userTimeFormatId, String timeZoneDiff, String pref) throws ServiceException {
        SimpleDateFormat sdf = null;
        try {
            String dateformat = "";
            if (userTimeFormatId.equals("1")) {
                dateformat = pref.replace('H', 'h');
                if (!dateformat.equals(pref)) {
                    dateformat += " a";
                }
            } else {
                dateformat = pref;
            }
            sdf = new SimpleDateFormat(dateformat);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZoneDiff));
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getPrefDateFormatter", e);
        }
        return sdf;
    }

    public static DateFormat getTimeFormatter(String userTimeFormatId) throws ServiceException {
        SimpleDateFormat sdf = null;
        try {
            String dateformat = "";
            if (userTimeFormatId.equals("1")) {
                dateformat = " hh:mm:ss aa ";
            } else {
                dateformat = "HH:mm:ss";
            }
            sdf = new SimpleDateFormat(dateformat);
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getTimeFormatter", e);
        }
        return sdf;
    }

    public static String generateNewPassword() throws ServiceException {
        String randomStr = "";
        try {
            randomStr = RandomStringUtils.random(8, true, true);
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.generateNewPassword", e);
        }
        return randomStr;
    }

    public static String getSHA1(String inStr) throws ServiceException {
        String outStr = inStr;
        try {
            byte[] theTextToDigestAsBytes = inStr.getBytes("utf-8");

            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] digest = sha.digest(theTextToDigestAsBytes);

            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                String h = Integer.toHexString(b & 0xff);
                if (h.length() == 1) {
                    sb.append("0" + h);
                } else {
                    sb.append(h);
                }
            }
            outStr = sb.toString();
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getSHA1", e);
        }
        return outStr;
    }
}
