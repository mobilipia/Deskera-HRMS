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

package com.krawler.common.util;

/**
 *
 * @author krawler-user
 */
public class AuditAction {
    private String ID;
    private String actionName;
    private AuditGroup auditGroup;

    public final static String LOG_IN_SUCCESS="1";
    public final static String LOG_IN_FAILED="2";
    public final static String LOGOUT_SUCCESS="3";
    public final static String LOGOUT_FAILED="4";
    public final static String COMPANY_CREATION="5";
    public final static String COMPANY_UPDATION="6";
    public final static String COMPANY_DELETION="8";
    public final static String INTERNAL_JOB_ADDED="11";
    public final static String EXTERNAL_JOB_ADDED="12";
    public final static String USER_SELECTED="13";
    public final static String APPERAISAL_DONE="14";
    public final static String TIME_SHEET_APPROVED="15";
    public final static String INITIATE_APPRAISAL="16";
    public final static String EMPLOYEE_TERMINATED="17";
    public final static String APPRAISAL_SUBMITTED="18";
    public final static String TIMESHEET_SUBMITTED="19";
    public final static String SET_AS_INTERVIEWER="20";
    public final static String UNASSIGN_INTERVIEWER="21";
    public final static String ACCEPT_AS_INTERVIEWER="22";
    public final static String REJECT_AS_INTERVIEWER="23";
    public final static String ADD_AGENCY="24";
    public final static String DELETE_AGENCY="25";
    public final static String PROFILE_EDITED="26";
    public final static String INTERVIEW_SCHEDULED="27";
    public final static String PROSPECT_EDITED="28";
    public final static String DELETE_APPLICATION="29";
    public final static String CREATE_APPLICANT="30";
    public final static String DELETE_APPLICANT="31";
    public final static String CYCLE_ADDED="32";
    public final static String CYCLE_EDITED="33";
    public final static String APPRAISER_ASSIGNED="34";
    public final static String REVIEWER_ASSIGNED="35";
    public final static String PROFILE_APPROVED="36";
    public final static String USER_CREATED="37";
    public final static String USER_MODIFIED="38";
    public final static String USER_DELETED="39";
    public final static String PERMISSIONS_MODIFIED="40";
    public final static String PASSWORD_RESET="41";
    public final static String PASSWORD_CHANGED="42";
    public final static String PROFILE_CHANGED="43";
    public final static String REHIRE_EMPLOYEE="44";
    public final static String EDIT_AGENCY="45";
    public final static String JOB_TO_AGENCY="46";
    public final static String APPLY_FOR_JOB="47";
    public final static String COMPETENCY_ADDED="48";
    public final static String COMPETENCY_EDITED="49";
    public final static String COMPETENCY_DELETED="50";
    public final static String ASSIGN_COMPETENCY="51";
    public final static String GOAL_ADDED="52";
    public final static String GOAL_EDITED="53";
    public final static String GOAL_DELETED="54";
    public final static String GOAL_ARCHIVED="55";
    public final static String GOAL_ASSIGNED="56";
    public final static String GOAL_UNARCHIVED="57";
    public final static String TRANSFER_APPLICANT="58";
    public final static String SEND_LETTERS="59";
    public final static String SALARY_AUTHORIZED="60";
    public final static String SALARY_UNAUTHORIZED="61";
    public final static String SALARY_DELETED="62";

    public final static String ORGANIZATION_CHART_NODE_ASSIGNED="71";
    public final static String ORGANIZATION_CHART_NODE_DELETED="72";

    public final static String Appraisal_Report_Download="80";
    public final static String PAYROLL_TEMPLATE_EDITED="81";
    public final static String PAYROLL_COMPONENT_DELETED="82";
    public final static String PAYROLL_TEMPLATE_DELETED="83";
    public final static String PAYROLL_TEMPLATE_ADDED="84";
    public final static String SALARY_GENERATED="85";
    public final static String PAYROLL_COMPONENT_EDITED="86";
    public final static String PAYROLL_COMPONENT_ADDED="87";
    public final static String payroll="88";
    public final static String MASTER_ADDED="89";
    public final static String MASTER_EDITED="90";
    public final static String MASTER_DELETED="91";

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public AuditGroup getAuditGroup() {
        return auditGroup;
    }

    public void setAuditGroup(AuditGroup auditGroup) {
        this.auditGroup = auditGroup;
    }
}
