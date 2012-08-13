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

/**
 *
 * @author krawler-user
 */
public class MessageSourceProxy {
    private static MessageSource messageSource;

    private MessageSourceProxy(){

    }

    public static void setMessageSource(MessageSource _messageSource){
        if(messageSource==null&&_messageSource!=null){
            messageSource = _messageSource;
        }
    }

    public static MessageSource getMessageSource(){
        return messageSource;
    }
    public static String getMessage(String code, Object[] args, String defaultMessage, HttpServletRequest request) {
        return messageSource.getMessage(code, args, defaultMessage, LocaleUtils.getLocale(request));
    }

    public static String getMessage(String code, Object[] args, HttpServletRequest request) throws NoSuchMessageException {
        return messageSource.getMessage(code, args, LocaleUtils.getLocale(request));
    }

    public static String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

    public static String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return messageSource.getMessage(code, args, locale);
    }
}
