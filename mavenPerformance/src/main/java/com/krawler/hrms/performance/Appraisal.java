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
package com.krawler.hrms.performance;

/**
 *
 * @author krawler
 */
public class Appraisal {

    private String appid;
    private Finalgoalmanagement goal;
    private Managecmpt competency;
    private Appraisalmanagement appraisal;
    private double compemprating;
    private double compmanrating;
    private double compempgap;
    private double compmangap;
    private double goalemprating;
    private double goalmanrating;
    private String compempcomment;
    private String compmancomment;
    private String goalempcomment;
    private String goalmancomment;

    public Appraisal() {
    }

    public Appraisal(String appid, Finalgoalmanagement goal, Managecmpt competency, Appraisalmanagement appraisal, double compemprating, double compmanrating, double compempgap, double compmangap, double goalemprating, double goalmanrating, String compempcomment, String compmancomment, String goalempcomment, String goalmancomment) {
        this.appid = appid;
        this.goal = goal;
        this.competency = competency;
        this.appraisal = appraisal;
        this.compemprating = compemprating;
        this.compmanrating = compmanrating;
        this.compempgap = compempgap;
        this.compmangap = compmangap;
        this.goalemprating = goalemprating;
        this.goalmanrating = goalmanrating;
        this.compempcomment = compempcomment;
        this.compmancomment = compmancomment;
        this.goalempcomment = goalempcomment;
        this.goalmancomment = goalmancomment;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public Appraisalmanagement getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(Appraisalmanagement appraisal) {
        this.appraisal = appraisal;
    }

    public double getCompempgap() {
        return compempgap;
    }

    public void setCompempgap(double compempgap) {
        this.compempgap = compempgap;
    }

    public double getCompemprating() {
        return compemprating;
    }

    public void setCompemprating(double compemprating) {
        this.compemprating = compemprating;
    }

    public Managecmpt getCompetency() {
        return competency;
    }

    public void setCompetency(Managecmpt competency) {
        this.competency = competency;
    }

    public double getCompmangap() {
        return compmangap;
    }

    public void setCompmangap(double compmangap) {
        this.compmangap = compmangap;
    }

    public double getCompmanrating() {
        return compmanrating;
    }

    public void setCompmanrating(double compmanrating) {
        this.compmanrating = compmanrating;
    }

    public Finalgoalmanagement getGoal() {
        return goal;
    }

    public void setGoal(Finalgoalmanagement goal) {
        this.goal = goal;
    }

    public double getGoalemprating() {
        return goalemprating;
    }

    public void setGoalemprating(double goalemprating) {
        this.goalemprating = goalemprating;
    }

    public double getGoalmanrating() {
        return goalmanrating;
    }

    public void setGoalmanrating(double goalmanrating) {
        this.goalmanrating = goalmanrating;
    }

    public String getCompempcomment() {
        return compempcomment;
    }

    public void setCompempcomment(String compempcomment) {
        this.compempcomment = compempcomment;
    }

    public String getCompmancomment() {
        return compmancomment;
    }

    public void setCompmancomment(String compmancomment) {
        this.compmancomment = compmancomment;
    }

    public String getGoalempcomment() {
        return goalempcomment;
    }

    public void setGoalempcomment(String goalempcomment) {
        this.goalempcomment = goalempcomment;
    }

    public String getGoalmancomment() {
        return goalmancomment;
    }

    public void setGoalmancomment(String goalmancomment) {
        this.goalmancomment = goalmancomment;
    }
}
