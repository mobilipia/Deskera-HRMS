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
package com.krawler.hrms.recruitment;

import com.krawler.common.admin.Company;
import com.krawler.hrms.master.MasterData;
import java.util.Date;

/**
 *
 * @author krawler
 */
public class Jobapplicant {

    private String applicantid;
    private String username;
    private String password;
    private String title;
    private String firstname;
    private String lastname;
    private String email;
    private String otheremail;
    private Date birthdate;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String contactno;
    private String mobileno;
    private String graddegree;
    private String gradspecialization;
    private String gradcollege;
    private String graduniversity;
    private String gradpercent;
    private Date gradpassdate;
    private String pgqualification;
    private String pgspecialization;
    private String pgcollege;
    private String pguniversity;
    private String pgpercent;
    private Date pgpassdate;
    private String otherqualification;
    private String othername;
    private String otherdetails;
    private String otherpercent;
    private Date otherpassdate;
    private Integer experiencemonth;
    private Integer experienceyear;
    private String functionalexpertise;
    private String currentindustry;
    private String currentorganization;
    private String currentdesignation;
    private Integer grosssalary;
    private Integer expectedsalary;
    private String category;
    private String companyrelative;
    private String appearedbefore;
    private String interviewlocation;
    private String keyskills;
    private String filepath;
    private Integer status;
    private Company company;
    private MasterData countryid;
    private boolean deleted;
    private MasterData interviewposition;
    private String interviewplace;
    private Date interviewdate;

    public Jobapplicant() {
    }

    public Jobapplicant(String applicantid, String username, String password, String title, String firstname, String lastname, String email, String otheremail, Date birthdate, String address1, String address2, String city, String state, String country, String contactno, String mobileno, String graddegree, String gradspecialization, String gradcollege, String graduniversity, String gradpercent, Date gradpassdate, String pgqualification, String pgspecialization, String pgcollege, String pguniversity, String pgpercent, Date pgpassdate, String otherqualification, String othername, String otherdetails, String otherpercent, Date otherpassdate, Integer experiencemonth, Integer experienceyear, String functionalexpertise, String currentindustry, String currentorganization, String currentdesignation, Integer grosssalary, Integer expectedsalary, String category, String companyrelative, String appearedbefore, String interviewlocation, String keyskills, String filepath, Integer status, Company company, MasterData countryid, boolean deleted, MasterData interviewposition, String interviewplace, Date interviewdate) {
        this.applicantid = applicantid;
        this.username = username;
        this.password = password;
        this.title = title;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.otheremail = otheremail;
        this.birthdate = birthdate;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.contactno = contactno;
        this.mobileno = mobileno;
        this.graddegree = graddegree;
        this.gradspecialization = gradspecialization;
        this.gradcollege = gradcollege;
        this.graduniversity = graduniversity;
        this.gradpercent = gradpercent;
        this.gradpassdate = gradpassdate;
        this.pgqualification = pgqualification;
        this.pgspecialization = pgspecialization;
        this.pgcollege = pgcollege;
        this.pguniversity = pguniversity;
        this.pgpercent = pgpercent;
        this.pgpassdate = pgpassdate;
        this.otherqualification = otherqualification;
        this.othername = othername;
        this.otherdetails = otherdetails;
        this.otherpercent = otherpercent;
        this.otherpassdate = otherpassdate;
        this.experiencemonth = experiencemonth;
        this.experienceyear = experienceyear;
        this.functionalexpertise = functionalexpertise;
        this.currentindustry = currentindustry;
        this.currentorganization = currentorganization;
        this.currentdesignation = currentdesignation;
        this.grosssalary = grosssalary;
        this.expectedsalary = expectedsalary;
        this.category = category;
        this.companyrelative = companyrelative;
        this.appearedbefore = appearedbefore;
        this.interviewlocation = interviewlocation;
        this.keyskills = keyskills;
        this.filepath = filepath;
        this.status = status;
        this.company = company;
        this.countryid = countryid;
        this.deleted = deleted;
        this.interviewposition = interviewposition;
        this.interviewplace = interviewplace;
        this.interviewdate = interviewdate;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAppearedbefore() {
        return appearedbefore;
    }

    public void setAppearedbefore(String appearedbefore) {
        this.appearedbefore = appearedbefore;
    }

    public String getApplicantid() {
        return applicantid;
    }

    public void setApplicantid(String applicantid) {
        this.applicantid = applicantid;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getCompanyrelative() {
        return companyrelative;
    }

    public void setCompanyrelative(String companyrelative) {
        this.companyrelative = companyrelative;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getCurrentdesignation() {
        return currentdesignation;
    }

    public void setCurrentdesignation(String currentdesignation) {
        this.currentdesignation = currentdesignation;
    }

    public String getCurrentindustry() {
        return currentindustry;
    }

    public void setCurrentindustry(String currentindustry) {
        this.currentindustry = currentindustry;
    }

    public String getCurrentorganization() {
        return currentorganization;
    }

    public void setCurrentorganization(String currentorganization) {
        this.currentorganization = currentorganization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getExpectedsalary() {
        return expectedsalary;
    }

    public void setExpectedsalary(Integer expectedsalary) {
        this.expectedsalary = expectedsalary;
    }

    public Integer getExperiencemonth() {
        return experiencemonth;
    }

    public void setExperiencemonth(Integer experiencemonth) {
        this.experiencemonth = experiencemonth;
    }

    public Integer getExperienceyear() {
        return experienceyear;
    }

    public void setExperienceyear(Integer experienceyear) {
        this.experienceyear = experienceyear;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getFunctionalexpertise() {
        return functionalexpertise;
    }

    public void setFunctionalexpertise(String functionalexpertise) {
        this.functionalexpertise = functionalexpertise;
    }

    public String getGradcollege() {
        return gradcollege;
    }

    public void setGradcollege(String gradcollege) {
        this.gradcollege = gradcollege;
    }

    public String getGraddegree() {
        return graddegree;
    }

    public void setGraddegree(String graddegree) {
        this.graddegree = graddegree;
    }

    public Date getGradpassdate() {
        return gradpassdate;
    }

    public void setGradpassdate(Date gradpassdate) {
        this.gradpassdate = gradpassdate;
    }

    public String getGradpercent() {
        return gradpercent;
    }

    public void setGradpercent(String gradpercent) {
        this.gradpercent = gradpercent;
    }

    public String getGradspecialization() {
        return gradspecialization;
    }

    public void setGradspecialization(String gradspecialization) {
        this.gradspecialization = gradspecialization;
    }

    public String getGraduniversity() {
        return graduniversity;
    }

    public void setGraduniversity(String graduniversity) {
        this.graduniversity = graduniversity;
    }

    public Integer getGrosssalary() {
        return grosssalary;
    }

    public void setGrosssalary(Integer grosssalary) {
        this.grosssalary = grosssalary;
    }

    public String getInterviewlocation() {
        return interviewlocation;
    }

    public void setInterviewlocation(String interviewlocation) {
        this.interviewlocation = interviewlocation;
    }

    public String getKeyskills() {
        return keyskills;
    }

    public void setKeyskills(String keyskills) {
        this.keyskills = keyskills;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getOtherdetails() {
        return otherdetails;
    }

    public void setOtherdetails(String otherdetails) {
        this.otherdetails = otherdetails;
    }

    public String getOtheremail() {
        return otheremail;
    }

    public void setOtheremail(String otheremail) {
        this.otheremail = otheremail;
    }

    public String getOthername() {
        return othername;
    }

    public void setOthername(String othername) {
        this.othername = othername;
    }

    public Date getOtherpassdate() {
        return otherpassdate;
    }

    public void setOtherpassdate(Date otherpassdate) {
        this.otherpassdate = otherpassdate;
    }

    public String getOtherqualification() {
        return otherqualification;
    }

    public void setOtherqualification(String otherqualification) {
        this.otherqualification = otherqualification;
    }

    public String getOtherpercent() {
        return otherpercent;
    }

    public void setOtherpercent(String otherpercent) {
        this.otherpercent = otherpercent;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPgcollege() {
        return pgcollege;
    }

    public void setPgcollege(String pgcollege) {
        this.pgcollege = pgcollege;
    }

    public Date getPgpassdate() {
        return pgpassdate;
    }

    public void setPgpassdate(Date pgpassdate) {
        this.pgpassdate = pgpassdate;
    }

    public String getPgpercent() {
        return pgpercent;
    }

    public void setPgpercent(String pgpercent) {
        this.pgpercent = pgpercent;
    }

    public String getPgqualification() {
        return pgqualification;
    }

    public void setPgqualification(String pgqualification) {
        this.pgqualification = pgqualification;
    }

    public String getPgspecialization() {
        return pgspecialization;
    }

    public void setPgspecialization(String pgspecialization) {
        this.pgspecialization = pgspecialization;
    }

    public String getPguniversity() {
        return pguniversity;
    }

    public void setPguniversity(String pguniversity) {
        this.pguniversity = pguniversity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public MasterData getCountryid() {
        return countryid;
    }

    public void setCountryid(MasterData countryid) {
        this.countryid = countryid;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getInterviewdate() {
        return interviewdate;
    }

    public void setInterviewdate(Date interviewdate) {
        this.interviewdate = interviewdate;
    }

    public String getInterviewplace() {
        return interviewplace;
    }

    public void setInterviewplace(String interviewplace) {
        this.interviewplace = interviewplace;
    }

    public MasterData getInterviewposition() {
        return interviewposition;
    }

    public void setInterviewposition(MasterData interviewposition) {
        this.interviewposition = interviewposition;
    }
    
}
