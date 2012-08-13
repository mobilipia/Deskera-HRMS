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

package com.krawler.common.filters;

import com.krawler.common.locale.LocaleResolver;
import com.krawler.common.locale.LocaleUtils;
import com.krawler.common.locale.MessageSource;
import com.krawler.common.locale.MessageSourceProxy;
import com.krawler.common.locale.ResourceBundleMessageSource;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * Web application lifecycle listener.
 * @author krawler-user
 */
public class LocaleResolverBuilder implements ServletContextListener {
//    private static final Log log = LogFactory.getLog(LocaleResolverBuilder.class);
    public static final String LOCALE_RESOLVER_CLASS_NAME_ATTR = "localeResolver";
    public static final String MESSAGE_SOURCE_BASENAME_ATTR = "messageSourceBaseName";
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String clName=context.getInitParameter(LOCALE_RESOLVER_CLASS_NAME_ATTR);
        String baseName=context.getInitParameter(MESSAGE_SOURCE_BASENAME_ATTR);
        try{
            LocaleResolver obj=(LocaleResolver)Class.forName(clName).newInstance();
            context.setAttribute(LocaleUtils.LOCALE_RESOLVER_NAME, obj);
            if(MessageSourceProxy.getMessageSource()==null&&baseName!=null)
                MessageSourceProxy.setMessageSource(createMessageSource(baseName.split(",")));
        }catch(Exception ex){
//            log.debug(ex.getMessage());
        }
    }

    protected MessageSource createMessageSource(String[] basenames){
        ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
        ms.setBasenames(basenames);
        return ms;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.removeAttribute(LOCALE_RESOLVER_CLASS_NAME_ATTR);
    }
}
