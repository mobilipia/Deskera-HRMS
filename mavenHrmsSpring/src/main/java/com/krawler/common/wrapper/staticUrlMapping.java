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

package com.krawler.common.wrapper;

import java.util.Arrays;
import java.util.HashMap;

public class staticUrlMapping {

    public final static HashMap<String,Object> staticurlmap = new HashMap<String, Object>();
    
    public staticUrlMapping() {
        
        /*-----------Commonlibs-servlet.xml-----------*/
        
        staticurlmap.put("/OrganizationChart/*.common","organizationChartController");
        staticurlmap.put("/ProfileHandler/*.do","profileHandlerController");
        staticurlmap.put("/CompanyDetails/*.do","companyDetailsController");
        staticurlmap.put("/KwlCommonTables/*.do","kwlCommonTablesController");
        staticurlmap.put("/ExportPdfTemplate/*.do","exportPdfTemplateController");
        staticurlmap.put("/FirstRunHelp/*.do","firstRunHelpController");
        staticurlmap.put("/CustomCol/*.do","customcolController");
        
        /*-----------Common-servlet.xml-----------*/
        
        staticurlmap.put("/Common/*.common","hrmsCommonController");
        staticurlmap.put("/AuthHandler/*.common","authHandlercontroller");
        staticurlmap.put("/Dashboard/*.common","hrmsDashboardController");
        staticurlmap.put("/Common/ExportPdfTemplate/*.common","exportPdfTemplateController");
        staticurlmap.put("/Common/Export/*.common","exportdao");
        staticurlmap.put("/Common/Document/*.common","documentController");
        staticurlmap.put("/Common/AuditTrail/*.common","auditTrailController");
        staticurlmap.put("/Common/Master/*.common","hrmsMasterConfigController");
        staticurlmap.put("/Common/Permission/*.common","permissionHandlerHrmsController");
        staticurlmap.put("/Common/Template/*.common","hrmsTemplateController");
        
        /*-----------dispatcher-servlet.xml-----------*/
        
        staticurlmap.put("/ImportRecords/*.dsh","importcontroller");
        staticurlmap.put("/Dashboard/*.dsh","dashboardController");
        staticurlmap.put("/General/*.dsh","generalController");
        
        /*-----------Payroll-servlet.xml-----------*/
        staticurlmap.put("/Payroll/Date/*.py","hrmsPayrollController");
        staticurlmap.put("/Payroll/Wage/*.py","hrmsPayrollWageController");
        staticurlmap.put("/Payroll/Tax/*.py","hrmsPayrollTaxController");
        staticurlmap.put("/Payroll/Deduction/*.py","hrmsPayrollDeductionController");
        staticurlmap.put("/Payroll/Template/*.py","hrmsPayrollTemplateController");
        staticurlmap.put("/Payroll/EmpContrib/*.py","hrmsPayrollEmployerContributionController");
        staticurlmap.put("/Emp/*.py","hrmsEmpController");
        
        /*-----------Performance-servlet.xml-----------*/
        
        staticurlmap.put("/Performance/Goal/*.pf","hrmsGoalController");
        staticurlmap.put("/Performance/Competency/*.pf","hrmsCompetencyController");
        staticurlmap.put("/Performance/Appraisalcycle/*.pf","hrmsAppraisalcycleController");
        staticurlmap.put("/Performance/Appraisal/*.pf","hrmsAppraisalController");
        staticurlmap.put("/Performance/AnonymousAppraisal/*.pf","hrmsAnonymousAppraisalController");
        staticurlmap.put("/Performance/NonAnonymousAppraisal/*.pf","hrmsNonAnonymousAppraisalController");

        /*-----------Rec-servlet.xml-----------*/
        
        staticurlmap.put("/Rec/Job/*.rec","hrmsRecJobController");
        staticurlmap.put("/Rec/Agency/*.rec","hrmsRecAgencyController");
        staticurlmap.put("/Rec/Job/ExternalJOb/*.rec","externalJobcontroller");

        /*-----------Timesheet-servlet.xml-----------*/

        staticurlmap.put("/Timesheet/*.ts","hrmsTimesheetController");
        
    }
}
