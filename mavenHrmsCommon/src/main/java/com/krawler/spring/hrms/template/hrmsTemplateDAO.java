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
package com.krawler.spring.hrms.template;

import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;

/**
 *
 * @author Abhishek Dubey <abhishek.dubey@krawlernetworks.com>
 */
public interface hrmsTemplateDAO {

    public KwlReturnObject getParameterType(HashMap<String, Object> requestParams);

    public KwlReturnObject getParameterTypeValuePair(HashMap<String, Object> requestParams);

    public KwlReturnObject saveTemplate(HashMap<String, Object> requestParams);

    public KwlReturnObject getTemplates(HashMap<String, Object> requestParams);

    public KwlReturnObject delTemplate(HashMap<String, Object> requestParams);

    public KwlReturnObject getTemplateContent(HashMap<String, Object> requestParams);

    public KwlReturnObject getTemplateTargetList(HashMap<String, Object> requestParams);

    public KwlReturnObject saveTemplateTargetList(HashMap<String, Object> requestParams);

    public KwlReturnObject delTemplateTargetList(HashMap<String, Object> requestParams);

    public KwlReturnObject getTemplateTargetListDetails(HashMap<String, Object> requestParams);

    public KwlReturnObject saveTemplateTargetListDetails(HashMap<String, Object> requestParams);

    public KwlReturnObject delTemplateTargetListDetails(HashMap<String, Object> requestParams);

    public KwlReturnObject getNewTemplateTargetListDetails(HashMap<String, Object> requestParams);

    public KwlReturnObject delTemplatePlaceHolderMapping(HashMap<String, Object> requestParams);

    public KwlReturnObject saveTemplatePlaceHolderMapping(HashMap<String, Object> requestParams);
    
    public KwlReturnObject getIdFromPlaceholders(HashMap<String, Object> requestParams);
    
}
