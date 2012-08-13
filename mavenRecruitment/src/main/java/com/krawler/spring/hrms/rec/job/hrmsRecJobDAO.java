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

package com.krawler.spring.hrms.rec.job;

import com.krawler.common.admin.Docs;
import com.krawler.common.admin.User;
import com.krawler.hrms.common.docs.HrmsDocmap;
import com.krawler.hrms.common.docs.HrmsDocs;
import com.krawler.hrms.recruitment.ConfigRecruitment;
import com.krawler.hrms.recruitment.ConfigRecruitmentData;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;

/**
 *
 * @author shs
 */
public interface hrmsRecJobDAO {
    public KwlReturnObject getPositionmain(HashMap<String,Object> requestParams);

    public KwlReturnObject transferappdata(HashMap<String, Object> requestParams);
    public KwlReturnObject updatePositionmain(HashMap<String,Object> requestParams);
    public KwlReturnObject getPositionstatus(ArrayList name, ArrayList value);
    public KwlReturnObject getMaxCountJobid(HashMap<String,Object> requestParams);
    public KwlReturnObject setPositionmain(HashMap<String, Object> requestParams);
    public KwlReturnObject getJobProfile(ArrayList name, ArrayList value);
    public KwlReturnObject addJobProfile(HashMap<String, Object> requestParams);
    public KwlReturnObject getPositionstatus(HashMap<String,Object> requestParams);
    public KwlReturnObject getRecruiter(HashMap<String, Object> requestParams);
//    public KwlReturnObject getAssignmanager(HashMap<String, Object> requestParams);
    public KwlReturnObject addAllapplications(HashMap<String, Object> requestParams);
    public KwlReturnObject deleteAllapplications(HashMap<String,Object> requestParams);
    public KwlReturnObject getJobApplicant(HashMap<String,Object> requestParams);
    public KwlReturnObject deleteJobapplicant(HashMap<String,Object> requestParams);
    public KwlReturnObject setJobApplicant(HashMap<String, Object> requestParams);
    public KwlReturnObject getRecruiterupdate(HashMap<String,Object> requestParams);
    public KwlReturnObject getAppliedJobUpdate(HashMap<String,Object> requestParams);
    public KwlReturnObject getRecruitersList(HashMap<String, Object> requestParams);
    public KwlReturnObject setRecruiters(HashMap<String, Object> requestParams);
    public KwlReturnObject getConfigRecruitment(HashMap<String, Object> requestParams);
    public KwlReturnObject addConfigRecruitmentType(HashMap<String, Object> requestParams);
    public KwlReturnObject deleteConfigRecruitment(String configid);
    public KwlReturnObject getConfigRecruitmentData(HashMap<String, Object> requestParams);
    public KwlReturnObject getConfigMaster(HashMap<String, Object> requestParams);
    public boolean updateConfigRecruitment(HashMap<String, Object> requestParams);
    public KwlReturnObject addConfigMaster(HashMap<String, Object> requestParams);
    public KwlReturnObject deleteConfigMaster(String masterid);
    public boolean saveConfigRecruitment(ConfigRecruitment contyp);
    public boolean updownConfigRecruitment(String configid, int positioninc);
    public KwlReturnObject addConfigRecruitmentData(HashMap<String, Object> requestParams);
    public KwlReturnObject addHrmsDocmap(HashMap<String, Object> arrParam);
    public KwlReturnObject deleteConfigJobapplicant(HashMap<String, Object> requestParams);
    public void parseRequest(HttpServletRequest request, HashMap<String, Object> arrParam, ArrayList<FileItem> hm, boolean fileUpload, HashMap<Integer, String> filemap);
    public void updateConfigRecruitmentDatatoDefault(HashMap<String, Object> requestParams);
    public KwlReturnObject getConfigJobApplicant(HashMap<String, Object> requestParams);
    public HrmsDocs uploadFile(FileItem get, ConfigRecruitmentData ConfigRecruitmentDataobj, HashMap<String, Object> arrParam, boolean b);
    public KwlReturnObject saveLetterHistory(HashMap<String, Object> requestParams);
    public KwlReturnObject getHtmlTemplate(HashMap<String,Object> requestParams);
    public KwlReturnObject getUser(HashMap<String,Object> requestParams);
    public String getPlaceHolderLookupString(HashMap<String,Object> requestParams);
    public KwlReturnObject getPlaceHolderData(HashMap<String,Object> requestParams);
    public KwlReturnObject getPlaceHolderUserValue(HashMap<String,Object> requestParams);
    public String fetchField(HashMap<String,String> requestParams, int index);
    public boolean deleteRecruiters(HashMap<String, Object> requestParams);

    public KwlReturnObject getRecruiters(HashMap<String, Object> requestParams);

    public KwlReturnObject getRecruiterPositionstatus(HashMap<String, Object> requestParams);

    public KwlReturnObject cancelAllapplications(ArrayList name, ArrayList value);
    
    public KwlReturnObject transferExternalAppDocs(ArrayList<HrmsDocmap> hrmsDocmaps, User relatedTo);
    
    public KwlReturnObject transferExternalAppDocMaps(ArrayList<HrmsDocmap> hrmsDocmaps, ArrayList<Docs> docs, User relatedTo);

}
