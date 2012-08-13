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
package com.krawler.common.locale;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionLocaleResolver extends AbstractLocaleResolver {

    public static final String LOCALE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".LOCALE";

    public Locale resolveLocale(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Locale locale = (Locale) (session != null ? session.getAttribute(LOCALE_SESSION_ATTRIBUTE_NAME) : null);
        if (locale == null) {
            locale = determineDefaultLocale(request);
        }
        return locale;
    }

    protected Locale determineDefaultLocale(HttpServletRequest request) {
        Locale defaultLocale = getDefaultLocale();
        if (defaultLocale == null) {
            defaultLocale = request.getLocale();
        }
        return defaultLocale;
    }

    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        if (locale != null) {
            request.getSession().setAttribute(LOCALE_SESSION_ATTRIBUTE_NAME, locale);
        } else {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(LOCALE_SESSION_ATTRIBUTE_NAME);
            }
        }
    }
}
