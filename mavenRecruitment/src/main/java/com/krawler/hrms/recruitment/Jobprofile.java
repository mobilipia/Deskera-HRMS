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

import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author krawler
 */
public class Jobprofile {

    private String id;
    private Positionmain position;
    private String responsibility;
    private String skill;
    private String skilldesc;
    private String qualification;
    private String qualificationdesc;
    private Integer type;
    public static String jobresponsibility="responsibility";
    public static String jobqualification="qualification";
    public static String jobskill="skill";
    public static HashMap jobmeta;
    static{
        jobmeta = new HashMap();
        jobmeta.put(1, jobresponsibility);
        jobmeta.put(2, jobskill);
        jobmeta.put(3, jobqualification);
    }
    

    public Jobprofile(){
    	id = UUID.randomUUID().toString();
    }

    public Jobprofile(String id, Positionmain position, String responsibility, String skill, String skilldesc, String qualification, String qualificationdesc, Integer type) {
        this.id = id;
        this.position = position;
        this.responsibility = responsibility;
        this.skill = skill;
        this.skilldesc = skilldesc;
        this.qualification = qualification;
        this.qualificationdesc = qualificationdesc;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Positionmain getPosition() {
        return position;
    }

    public void setPosition(Positionmain position) {
        this.position = position;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getQualificationdesc() {
        return qualificationdesc;
    }

    public void setQualificationdesc(String qualificationdesc) {
        this.qualificationdesc = qualificationdesc;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getSkilldesc() {
        return skilldesc;
    }

    public void setSkilldesc(String skilldesc) {
        this.skilldesc = skilldesc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    
}
