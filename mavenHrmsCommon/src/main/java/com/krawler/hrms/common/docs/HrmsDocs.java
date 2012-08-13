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
package com.krawler.hrms.common.docs;

import com.krawler.common.admin.User;
import com.krawler.hrms.recruitment.Jobapplicant;
import java.io.Serializable;

public class HrmsDocs implements Serializable {

    private String docid;
    private String docname;
    private String docsize;
    private String doctype;
    private String dispdocname;
    private String docdesc;
    private java.util.Date uploadedon;
    private String uploadedby;
    private String storename;
    private int storageindex;
    private User userid;
    private Jobapplicant applicantid;
    private boolean deleted;
    private String referenceid;

    public String getDocid() {
        if (this.docid == null) {
            this.docid = "";
        }
        return this.docid;
    }

    public void setDocid(String docid) {
        if ((docid == null && this.docid != null) ||
                (docid != null && this.docid == null) ||
                (docid != null && this.docid != null && !docid.equals(this.docid))) {
            this.docid = docid;
        }
    }

    public String getDocname() {
        if (this.docname == null) {
            this.docname = "";
        }
        return this.docname;
    }

    public void setDocname(String docname) {
        if ((docname == null && this.docname != null) ||
                (docname != null && this.docname == null) ||
                (docname != null && this.docname != null && !docname.equals(this.docname))) {
            this.docname = docname;
        }
    }

    public String getDocsize() {
        if (this.docsize == null) {
            this.docsize = "";
        }
        return this.docsize;
    }

    public void setDocsize(String docsize) {
        if ((docsize == null && this.docsize != null) ||
                (docsize != null && this.docsize == null) ||
                (docsize != null && this.docsize != null && !docsize.equals(this.docsize))) {
            this.docsize = docsize;
        }
    }

    public String getDoctype() {
        if (this.doctype == null) {
            this.doctype = "";
        }
        return this.doctype;
    }

    public void setDoctype(String doctype) {
        if ((doctype == null && this.doctype != null) ||
                (doctype != null && this.doctype == null) ||
                (doctype != null && this.doctype != null && !doctype.equals(this.doctype))) {
            this.doctype = doctype;
        }
    }

    public java.util.Date getUploadedon() {
        return this.uploadedon;
    }

    public void setUploadedon(java.util.Date uploadedon) {
        if (uploadedon != this.uploadedon) {
            this.uploadedon = uploadedon;
        }
    }

    public String getStorename() {
        if (this.storename == null) {
            this.storename = "";
        }
        return this.storename;
    }

    public void setStorename(String storename) {
        if ((storename == null && this.storename != null) ||
                (storename != null && this.storename == null) ||
                (storename != null && this.storename != null && !storename.equals(this.storename))) {
            this.storename = storename;
        }
    }

    public int getStorageindex() {
        return this.storageindex;
    }

    public void setStorageindex(int storageindex) {
        if (storageindex != this.storageindex) {
            this.storageindex = storageindex;
        }
    }

    public User getUserid() {
        return this.userid;
    }

    public void setUserid(User userid) {
        if (userid != this.userid) {
            this.userid = userid;
        }
    }

    public String getDispdocname() {
        return dispdocname;
    }

    public void setDispdocname(String dispdocname) {
        this.dispdocname = dispdocname;
    }

    public String getDocdesc() {
        return docdesc;
    }

    public void setDocdesc(String docdesc) {
        this.docdesc = docdesc;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Jobapplicant getApplicantid() {
        return this.applicantid;
    }

    public void setApplicantid(Jobapplicant applicantid) {
        if (applicantid != this.applicantid) {
            this.applicantid = applicantid;
        }
    }
    public void setReferenceid(String referenceid) {
        this.referenceid = referenceid;
    }

    public String getReferenceid() {
        return referenceid;
    }

    public String getUploadedby() {
        return uploadedby;
    }

    public void setUploadedby(String uploadedby) {
        this.uploadedby = uploadedby;
    }
}
