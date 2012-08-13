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
package com.krawler.hrms.ess;

import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.admin.Useraccount;
import com.krawler.hrms.master.MasterData;
import java.util.Date;

/**
 *
 * @author krawler
 */
public class Empprofile {

    private String userID;
    private UserLogin userLogin;
    private Useraccount useraccount;
    private Date DoB;
    private String middlename;
    private String gender;
    private String marriage;
    private String bloodgrp;
    private String fathername;
    private Date fatherDoB;
    private String mothername;
    private Date motherDoB;
    private String spousename;
    private Date spouseDoB;
    private String child1name;
    private Date child1DoB;
    private String child2name;
    private Date child2DoB;
    private String bankacc;
    private String bankname;
    private String bankbranch;
    private String panno;
    private String pfno;
    private String drvlicense;
    private String passportno;
    private Date exppassport;
    private String mobno;
    private String workno;
    private String landno;
    private String presentaddr;
    private String presentcity;
    private String presentstate;
    private MasterData presentcountry;
    private String permaddr;
    private String permcity;
    private String permstate;
    private MasterData permcountry;
    private String mailaddr;
    private String emgname;
    private String emgreln;
    private String emghome;
    private String emgwork;
    private String emgmob;
    private String emgaddr;
    private User reportto;
    private Date joindate;
    private Date confirmdate;
    private Date relievedate;
    private String trainperiod;
    private String probperiod;
    private String noticeperiod;
    private String emptype;
    private String workmail;
    private String othermail;
    private String commid;
    private String branchcode;
    private String branchaddr;
    private String branchcity;
    private MasterData branchcountry;
    private String updated_by;
    private String status;
    private String keyskills;
    private String wkstarttime;
    private String wkendtime;
    private String weekoff;
    private Date updated_on;
    private MasterData tercause;
    private String terReason;
    private User terminatedby;
    private boolean termnd;
    private Double savings;
    
    public Empprofile() {
    }

    public Useraccount getUseraccount() {
        return useraccount;
    }

    public void setUseraccount(Useraccount useraccount) {
        this.useraccount = useraccount;
    }




    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getDoB() {
        return DoB;
    }

    public void setDoB(Date DoB) {
        this.DoB = DoB;
    }

    public String getBankacc() {
        return bankacc;
    }

    public void setBankacc(String bankacc) {
        this.bankacc = bankacc;
    }

    public String getBankbranch() {
        return bankbranch;
    }

    public void setBankbranch(String bankbranch) {
        this.bankbranch = bankbranch;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getBloodgrp() {
        return bloodgrp;
    }

    public void setBloodgrp(String bloodgrp) {
        this.bloodgrp = bloodgrp;
    }

    public Date getChild1DoB() {
        return child1DoB;
    }

    public void setChild1DoB(Date child1DoB) {
        this.child1DoB = child1DoB;
    }

    public String getChild1name() {
        return child1name;
    }

    public void setChild1name(String child1name) {
        this.child1name = child1name;
    }

    public Date getChild2DoB() {
        return child2DoB;
    }

    public void setChild2DoB(Date child2DoB) {
        this.child2DoB = child2DoB;
    }

    public String getChild2name() {
        return child2name;
    }

    public void setChild2name(String child2name) {
        this.child2name = child2name;
    }

    public Date getConfirmdate() {
        return confirmdate;
    }

    public void setConfirmdate(Date confirmdate) {
        this.confirmdate = confirmdate;
    }

    public String getDrvlicense() {
        return drvlicense;
    }

    public void setDrvlicense(String drvlicense) {
        this.drvlicense = drvlicense;
    }

    public String getEmgaddr() {
        return emgaddr;
    }

    public void setEmgaddr(String emgaddr) {
        this.emgaddr = emgaddr;
    }

    public String getEmghome() {
        return emghome;
    }

    public void setEmghome(String emghome) {
        this.emghome = emghome;
    }

    public String getEmgmob() {
        return emgmob;
    }

    public void setEmgmob(String emgmob) {
        this.emgmob = emgmob;
    }

    public String getEmgname() {
        return emgname;
    }

    public void setEmgname(String emgname) {
        this.emgname = emgname;
    }

    public String getEmgreln() {
        return emgreln;
    }

    public void setEmgreln(String emgreln) {
        this.emgreln = emgreln;
    }

    public String getEmgwork() {
        return emgwork;
    }

    public void setEmgwork(String emgwork) {
        this.emgwork = emgwork;
    }

    public String getEmptype() {
        return emptype;
    }

    public void setEmptype(String emptype) {
        this.emptype = emptype;
    }

    public Date getExppassport() {
        return exppassport;
    }

    public void setExppassport(Date exppassport) {
        this.exppassport = exppassport;
    }

    public Date getFatherDoB() {
        return fatherDoB;
    }

    public void setFatherDoB(Date fatherDoB) {
        this.fatherDoB = fatherDoB;
    }

    public String getFathername() {
        return fathername;
    }

    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getJoindate() {
        return joindate;
    }

    public void setJoindate(Date joindate) {
        this.joindate = joindate;
    }

    public String getLandno() {
        return landno;
    }

    public void setLandno(String landno) {
        this.landno = landno;
    }

    public String getMailaddr() {
        return mailaddr;
    }

    public void setMailaddr(String mailaddr) {
        this.mailaddr = mailaddr;
    }

    public String getMarriage() {
        return marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    public String getMobno() {
        return mobno;
    }

    public void setMobno(String mobno) {
        this.mobno = mobno;
    }

    public Date getMotherDoB() {
        return motherDoB;
    }

    public void setMotherDoB(Date motherDoB) {
        this.motherDoB = motherDoB;
    }

    public String getMothername() {
        return mothername;
    }

    public void setMothername(String mothername) {
        this.mothername = mothername;
    }

    public String getNoticeperiod() {
        return noticeperiod;
    }

    public void setNoticeperiod(String noticeperiod) {
        this.noticeperiod = noticeperiod;
    }

    public String getPanno() {
        return panno;
    }

    public void setPanno(String panno) {
        this.panno = panno;
    }

    public String getPassportno() {
        return passportno;
    }

    public void setPassportno(String passportno) {
        this.passportno = passportno;
    }

    public String getPermaddr() {
        return permaddr;
    }

    public void setPermaddr(String permaddr) {
        this.permaddr = permaddr;
    }

    public String getPermcity() {
        return permcity;
    }

    public void setPermcity(String permcity) {
        this.permcity = permcity;
    }

    public MasterData getPermcountry() {
        return permcountry;
    }

    public void setPermcountry(MasterData permcountry) {
        this.permcountry = permcountry;
    }

    public String getPermstate() {
        return permstate;
    }

    public void setPermstate(String permstate) {
        this.permstate = permstate;
    }

    public String getPfno() {
        return pfno;
    }

    public void setPfno(String pfno) {
        this.pfno = pfno;
    }

    public String getPresentaddr() {
        return presentaddr;
    }

    public void setPresentaddr(String presentaddr) {
        this.presentaddr = presentaddr;
    }

    public String getPresentcity() {
        return presentcity;
    }

    public void setPresentcity(String presentcity) {
        this.presentcity = presentcity;
    }

    public MasterData getPresentcountry() {
        return presentcountry;
    }

    public void setPresentcountry(MasterData presentcountry) {
        this.presentcountry = presentcountry;
    }

    public String getPresentstate() {
        return presentstate;
    }

    public void setPresentstate(String presentstate) {
        this.presentstate = presentstate;
    }

    public String getProbperiod() {
        return probperiod;
    }

    public void setProbperiod(String probperiod) {
        this.probperiod = probperiod;
    }

    public Date getRelievedate() {
        return relievedate;
    }

    public void setRelievedate(Date relievedate) {
        this.relievedate = relievedate;
    }

    public User getReportto() {
        return reportto;
    }

    public void setReportto(User reportto) {
        this.reportto = reportto;
    }

    public Date getSpouseDoB() {
        return spouseDoB;
    }

    public void setSpouseDoB(Date spouseDoB) {
        this.spouseDoB = spouseDoB;
    }

    public String getSpousename() {
        return spousename;
    }

    public void setSpousename(String spousename) {
        this.spousename = spousename;
    }

    public String getTrainperiod() {
        return trainperiod;
    }

    public void setTrainperiod(String trainperiod) {
        this.trainperiod = trainperiod;
    }

    public UserLogin getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(UserLogin userLogin) {
        this.userLogin = userLogin;
    }

    public String getWorkno() {
        return workno;
    }

    public void setWorkno(String workno) {
        this.workno = workno;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getOthermail() {
        return othermail;
    }

    public void setOthermail(String othermail) {
        this.othermail = othermail;
    }

    public String getWorkmail() {
        return workmail;
    }

    public void setWorkmail(String workmail) {
        this.workmail = workmail;
    }

    public String getBranchaddr() {
        return branchaddr;
    }

    public void setBranchaddr(String branchaddr) {
        this.branchaddr = branchaddr;
    }

    public String getBranchcity() {
        return branchcity;
    }

    public void setBranchcity(String branchcity) {
        this.branchcity = branchcity;
    }

    public String getBranchcode() {
        return branchcode;
    }

    public void setBranchcode(String branchcode) {
        this.branchcode = branchcode;
    }

    public MasterData getBranchcountry() {
        return branchcountry;
    }

    public void setBranchcountry(MasterData branchcountry) {
        this.branchcountry = branchcountry;
    }

    public String getCommid() {
        return commid;
    }

    public void setCommid(String commid) {
        this.commid = commid;
    }
     public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }
    
    public Date getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(Date updated_on) {
        this.updated_on = updated_on;
    }

    public String getKeyskills() {
        return keyskills;
    }

    public void setKeyskills(String keyskills) {
        this.keyskills = keyskills;
    }

    public String getWeekoff() {
        return weekoff;
    }

    public void setWeekoff(String weekoff) {
        this.weekoff = weekoff;
    }

    public String getWkendtime() {
        return wkendtime;
    }

    public void setWkendtime(String wkendtime) {
        this.wkendtime = wkendtime;
    }

    public String getWkstarttime() {
        return wkstarttime;
    }

    public void setWkstarttime(String wkstarttime) {
        this.wkstarttime = wkstarttime;
    }

    public String getTerReason() {
        return terReason;
    }

    public void setTerReason(String terReason) {
        this.terReason = terReason;
    }

    public MasterData getTercause() {
        return tercause;
    }

    public void setTercause(MasterData tercause) {
        this.tercause = tercause;
    }

    public User getTerminatedby() {
        return terminatedby;
    }

    public void setTerminatedby(User terminatedby) {
        this.terminatedby = terminatedby;
    }

    public boolean isTermnd() {
        return termnd;
    }

    public void setTermnd(boolean termnd) {
        this.termnd = termnd;
    }

    public Double getSavings() {
        return savings;
    }

    public void setSavings(Double savings) {
        this.savings = savings;
    }
    
    
}
