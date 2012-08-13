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
package com.krawler.spring.hrms.payroll.salaryslip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;
import masterDB.ComponentResourceMappingHistory;
import masterDB.PayrollHistory;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.fontsettings.FontContext;
import com.krawler.common.fontsettings.FontFamilySelector;
import com.krawler.common.fontsettings.FontSetting;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.hrms.common.HrmsCommonPayroll;
import com.krawler.hrms.common.HrmsPayrollConstants;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.payroll.hrmsPayrollDAO;
import com.krawler.spring.hrms.payroll.incometax.IncomeTax;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class ExportSalarySlipService implements MessageSourceAware{
	private ExportSalarySlipDAO exportSalarySlipDAO;
	private profileHandlerDAO profileHandlerDAOObj;
	private hrmsPayrollDAO hrmsPayrollDAOObj;
	private kwlCommonTablesDAO kwlCommonTablesDAO;
	private sessionHandlerImpl sessionHandlerImplObj;
	private Map<String, IncomeTax> calculatorMap;
	private MessageSource messageSource;
	
	private static DecimalFormat currencyFormat = new DecimalFormat(",###.00");
    
    public void setExportSalarySlipDAO(ExportSalarySlipDAO exportSalarySlipDAO) {
		this.exportSalarySlipDAO = exportSalarySlipDAO;
	}
	
	public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }
	
	public void setHrmsPayrollDAO(hrmsPayrollDAO hrmsPayrollDAOObj1) {
        this.hrmsPayrollDAOObj = hrmsPayrollDAOObj1;
    }
	
	public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAO) {
        this.kwlCommonTablesDAO = kwlCommonTablesDAO;
    }
	
	public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }
	
	public void setCalculatorMap(Map<String, IncomeTax> calculatorMapObj) {
        this.calculatorMap = calculatorMapObj;
    }
	
	private FontFamilySelector fontFamilySelector = null;
	//private static FontFamilySelector fontFamilySelector = FontSetting.getFontFamilySelector();
	
	public ExportSalarySlipService(){
    	fontFamilySelector = FontSetting.getFontFamilySelector();
    }
	
	public class EndPage extends PdfPageEventHelper {
		private Locale locale;
		
		public EndPage(Locale locale){
			this.locale = locale;
		}

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Rectangle page = document.getPageSize();
                PdfPTable footer = new PdfPTable(1);
                footer.setWidthPercentage(100);
                footer.setSpacingBefore(20);
                PdfPCell footerPageNocell = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.computer.generated.payslip.not.require.signature", null, this.locale), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN)));
                footerPageNocell.setBorder(0);
                footerPageNocell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                footer.addCell(footerPageNocell);
                footer.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin()+2 , writer.getDirectContent());

            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }
	
	public void exportSalarySlip(String userid, Date startdate, Date enddate, HttpServletRequest request, HttpServletResponse response, String historyid){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4);
		PdfWriter writer=null;
		try {
				HrmsCommonPayroll hrmsCommonPayroll = new HrmsCommonPayroll();
				DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				User user = exportSalarySlipDAO.getUser(userid);
				Empprofile empprofile = exportSalarySlipDAO.getEmpprofile(userid);
				Useraccount useraccount = exportSalarySlipDAO.getUserAccount(userid);
				Company company = user.getCompany();
				PayrollHistory payrollHistory = exportSalarySlipDAO.getPayrollHistory(historyid);
				CompanyPreferences preferences = (CompanyPreferences) kwlCommonTablesDAO.getObject("com.krawler.common.admin.CompanyPreferences", company.getCompanyID());
				Date financialStartDate = hrmsCommonPayroll.getFinanacialYearStartDate(enddate, preferences.getFinancialmonth());
				Date financialEndDate = hrmsCommonPayroll.getFinanacialYearEndDate(enddate, preferences.getFinancialmonth()+11);
				IncomeTax incomeTax = getIncomTaxObj(request);
				List<PayrollHistory> list = null;
				Map<String, Double> mapEPF = null;
				if(incomeTax!=null){
					list = incomeTax.getPayrollHistories(userid, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL, financialStartDate, financialEndDate);
					mapEPF = incomeTax.getEPF(list, payrollHistory);
				}
				writer=PdfWriter.getInstance(document, baos);
				writer.setPageEvent(new EndPage(RequestContextUtils.getLocale(request)));
				document.open();
				
				PdfPTable mainTable = new PdfPTable(1);
                mainTable.setWidthPercentage(100);
                
                mainTable.addCell(getEmployeeDetails(user, empprofile, useraccount, company, payrollHistory, sdf, Rectangle.NO_BORDER, request));
                mainTable.addCell(getSalaryDetails(user, empprofile, useraccount, company, payrollHistory, sdf, Rectangle.NO_BORDER, request));
                mainTable.addCell(getEmployeeAndEmployerDetails(user, empprofile, useraccount, company, payrollHistory, sdf, Rectangle.CELL,list, mapEPF, request));
                
                document.newPage();
                document.add(mainTable);
                document.close();
                response.setHeader("Content-Disposition", "attachment; filename=\""+ user.getFirstName()+"_"+sdf.format(startdate)+".pdf\"");
                response.setContentType("application/octet-stream");
                response.setContentLength(baos.size());
                response.getOutputStream().write(baos.toByteArray());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			writer.close();
		}
	}
	
	public PdfPCell getEmployeeDetails(User user, Empprofile empprofile, Useraccount useraccount, Company company, PayrollHistory payrollHistory, DateFormat sdf, int border, HttpServletRequest request){
		PdfPTable mainTable = new PdfPTable(3);
		PdfPCell mainCell = null;
		try{
			int paddingTop = 4;
			int paddingBottom = 4;
			mainTable.setWidths(new float[]{48, 4, 48});
            String empIdName = getUserCode(useraccount.getEmployeeid(), useraccount.getUserID(), company.getCompanyID())+" "+profileHandlerDAOObj.getUserFullName(user.getUserID());
            String costCenter = payrollHistory.getCostCenter()!=null?payrollHistory.getCostCenter().getName():" ";
            String passport = " ";
            if(empprofile!=null && empprofile.getPassportno()!=null){
                passport = empprofile.getPassportno();
            }
            String period = sdf.format(payrollHistory.getPaycyclestartdate())+" to "+sdf.format(payrollHistory.getPaycycleenddate());
            
            PdfPTable leftTable = new PdfPTable(2);
            leftTable.setWidths(new float[]{40, 60});
    		PdfPCell leftCell = null;
    		leftTable.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.common.company.name", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		leftTable.addCell(getPdfPCellInstance(company.getCompanyName(), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		leftTable.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.common.employee.id", null, RequestContextUtils.getLocale(request))+"/"+messageSource.getMessage("hrms.common.name", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		leftTable.addCell(getPdfPCellInstance(empIdName, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		leftTable.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.common.nric", null, RequestContextUtils.getLocale(request))+"/"+messageSource.getMessage("hrms.common.passport", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		leftTable.addCell(getPdfPCellInstance(passport, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		leftCell = new PdfPCell(leftTable);
    		leftCell.setBorder(border);
            mainTable.addCell(leftCell);
            

            PdfPCell centerCell = new PdfPCell();
            centerCell.setBorder(border);
            mainTable.addCell(centerCell);

            
            PdfPTable rigthTable = new PdfPTable(2);
            rigthTable.setWidths(new float[]{40, 60});
    		PdfPCell rigthCell = null;
    		rigthTable.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.common.payment.period", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		rigthTable.addCell(getPdfPCellInstance(period, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		rigthTable.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		rigthTable.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		rigthTable.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.common.costcenter", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		rigthTable.addCell(getPdfPCellInstance(costCenter, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            rigthCell = new PdfPCell(rigthTable);
    		rigthCell.setBorder(border);
            mainTable.addCell(rigthCell);
            
            mainCell = new PdfPCell(mainTable);
            mainCell.setPaddingTop(15);
            mainCell.setBorder(border);
            mainCell.setPaddingBottom(20);
		}catch(Exception e){
			e.printStackTrace();
		}
		return mainCell;
	}
	
	
	public PdfPCell getSalaryDetails(User user, Empprofile empprofile, Useraccount useraccount, Company company, PayrollHistory payrollHistory, DateFormat sdf, int border, HttpServletRequest request){
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell mainCell = null;
		try{
			String currencySymbol = getCurrencySymbol(request, false);
			int paddingTop = 2;
			int paddingBottom = 2;
			double net=0;
			double earningAmount=0;
			double deductionAmount=0;
			String val = null;
			String accno = useraccount!=null?useraccount.getAccno():" ";
			accno = StringUtil.isNullOrEmpty(accno)?" ":accno;
			mainTable.setWidths(new float[]{100});
			
			PdfPTable mainTable1 = new PdfPTable(3);
			PdfPCell mainCell1 = null;
			mainTable1.setWidths(new float[]{45, 10, 45});
			
			PdfPTable mainTable2 = new PdfPTable(3);
			PdfPCell mainCell2 = null;
			mainTable2.setWidths(new float[]{45, 10, 45});
			            
			//####################Earnings##########################
			PdfPTable leftTable1 = new PdfPTable(2);
            leftTable1.setWidths(new float[]{60, 40});
    		PdfPCell leftCell1 = null;
    		leftTable1.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.common.payments", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, 7, 7, border));
    		leftTable1.addCell(getPdfPCellInstance("( "+currencySymbol+" )", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_RIGHT, Element.ALIGN_CENTER, 7, 7, border));
    		
    		List<ComponentResourceMappingHistory> list = hrmsPayrollDAOObj.getSalaryDetails(user.getUserID(), payrollHistory.getPaycycleenddate(), Integer.parseInt(payrollHistory.getFrequency()), HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION);
            for(ComponentResourceMappingHistory crm: list){
    			val = currencyFormat.format(crm.getAmount());
    			earningAmount+=currencyFormat.parse(val).doubleValue();
    			leftTable1.addCell(getPdfPCellInstance(crm.getComponent().getDescription(), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    			leftTable1.addCell(getPdfPCellInstance(val, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_RIGHT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		}
            leftCell1 = new PdfPCell(leftTable1);
    		leftCell1.setBorder(border);
            mainTable1.addCell(leftCell1);
            
            PdfPTable leftTable2 = new PdfPTable(2);
            leftTable2.setWidths(new float[]{60, 40});
    		PdfPCell leftCell2 = null;
            leftTable2.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
			leftTable2.addCell(getPdfPCellInstance("------------", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_RIGHT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		leftTable2.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.common.subtotal", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
			leftTable2.addCell(getPdfPCellInstance(currencyFormat.format(earningAmount), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_RIGHT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		leftCell2 = new PdfPCell(leftTable2);
    		leftCell2.setBorder(border);
            mainTable2.addCell(leftCell2);
            //####################Earnings##########################
            
            PdfPCell centerCell1 = new PdfPCell();
            centerCell1.setBorder(border);
            mainTable1.addCell(centerCell1);
            
            PdfPCell centerCell2 = new PdfPCell();
            centerCell2.setBorder(border);
            mainTable2.addCell(centerCell2);
            
            //####################Deductions And Tax##########################
			PdfPTable rigthTable1 = new PdfPTable(2);
			rigthTable1.setWidths(new float[]{60, 40});
    		PdfPCell rigthCell1 = null;
    		rigthTable1.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.payroll.Deduction", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, 7, 7, border));
    		rigthTable1.addCell(getPdfPCellInstance("( "+currencySymbol+" )", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_RIGHT, Element.ALIGN_CENTER, 7, 7, border));
    		
    		list = hrmsPayrollDAOObj.getSalaryDetails(user.getUserID(), payrollHistory.getPaycycleenddate(), Integer.parseInt(payrollHistory.getFrequency()), HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_DEDUCTION, null);
            for(ComponentResourceMappingHistory crm: list){
    			val = currencyFormat.format(crm.getAmount());
    			deductionAmount+=currencyFormat.parse(val).doubleValue();
    			rigthTable1.addCell(getPdfPCellInstance(crm.getComponent().getDescription(), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    			rigthTable1.addCell(getPdfPCellInstance(val, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_RIGHT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		}
            
            list = hrmsPayrollDAOObj.getSalaryDetails(user.getUserID(), payrollHistory.getPaycycleenddate(), Integer.parseInt(payrollHistory.getFrequency()), HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_TAX, null);
            for(ComponentResourceMappingHistory crm: list){
    			val = currencyFormat.format(crm.getAmount());
    			deductionAmount+=currencyFormat.parse(val).doubleValue();
    			rigthTable1.addCell(getPdfPCellInstance(crm.getComponent().getDescription(), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    			rigthTable1.addCell(getPdfPCellInstance(val, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_RIGHT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		}
                
            
            //###############Unpaid Leaves START##########
            if(payrollHistory.getUnpaidleavesAmount()>0){
	            val = currencyFormat.format(payrollHistory.getUnpaidleavesAmount());
				deductionAmount+=currencyFormat.parse(val).doubleValue();
				rigthTable1.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.payroll.Unpaidleaves", null, RequestContextUtils.getLocale(request))+" ("+payrollHistory.getUnpaidleaves()+")", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
				rigthTable1.addCell(getPdfPCellInstance(val, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_RIGHT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            }
            //###############Unpaid Leaves START##########
            
            //###############Income Tax START##########
            if(payrollHistory.getIncometaxAmount()>0){
            	val = currencyFormat.format(payrollHistory.getIncometaxAmount());
            	rigthTable1.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.payroll.income.tax", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            	rigthTable1.addCell(getPdfPCellInstance(val, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_RIGHT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            	deductionAmount+=currencyFormat.parse(val).doubleValue();
            }
            //###############Income Tax END##########
            
            //###############EPF START################
			Map<String, Double> map = getIncomTaxBenefits(request, payrollHistory.getUser().getUserID(), payrollHistory.getPaycycleenddate(), Integer.parseInt(payrollHistory.getFrequency()), payrollHistory);
            if(map!=null && map.get("epf")!=null && map.get("epf")>0){
				val = currencyFormat.format(map.get("epf"));
				rigthTable1.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.common.epf", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            	rigthTable1.addCell(getPdfPCellInstance(val, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_RIGHT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
				deductionAmount+=currencyFormat.parse(val).doubleValue();
			}
			//###############EPF END################
            
            
            rigthCell1 = new PdfPCell(rigthTable1);
            rigthCell1.setBorder(border);
            mainTable1.addCell(rigthCell1);
            	
            PdfPTable rigthTable2 = new PdfPTable(2);
			rigthTable2.setWidths(new float[]{60, 40});
    		PdfPCell rigthCell2 = null;
            rigthTable2.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		rigthTable2.addCell(getPdfPCellInstance("------------", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_RIGHT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		rigthTable2.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.common.subtotal", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		rigthTable2.addCell(getPdfPCellInstance(currencyFormat.format(deductionAmount), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_RIGHT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		
    		net=earningAmount-deductionAmount;
    		rigthTable2.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.payroll.net", null, RequestContextUtils.getLocale(request))+"(a/c:"+accno+")", FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		rigthTable2.addCell(getPdfPCellInstance(currencyFormat.format(net), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_RIGHT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
    		rigthCell2 = new PdfPCell(rigthTable2);
    		rigthCell2.setBorder(border);
            mainTable2.addCell(rigthCell2);
            //####################Deductions And Tax##########################
            
            
            mainCell1 = new PdfPCell(mainTable1);
            mainCell1.setBorder(border);
            mainCell2 = new PdfPCell(mainTable2);
            mainCell2.setBorder(border);
            
            mainTable.addCell(mainCell1);
            mainTable.addCell(mainCell2);
            mainCell = new PdfPCell(mainTable);
            mainCell.setBorder(border);
            mainCell.setPaddingBottom(20);
		}catch(Exception e){
			e.printStackTrace();
		}
		return mainCell;
	}
	
	
	public PdfPCell getEmployeeAndEmployerDetails(User user, Empprofile empprofile, Useraccount useraccount, Company company, PayrollHistory payrollHistory, DateFormat sdf, int border, List<PayrollHistory> list, Map<String, Double> mapEPF, HttpServletRequest request){
		PdfPTable mainTable = new PdfPTable(1);
		PdfPCell mainCell = null;
		try{
			String currencySymbol = getCurrencySymbol(request, false);
			int paddingTop = 5;
			int paddingBottom = 5;
			mainTable.setWidths(new float[]{100});
            
			PdfPCell cell1 = null;
			PdfPTable table1 = new PdfPTable(4);
            table1.setWidths(new float[]{14, 36, 25, 25});
            table1.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_CENTER, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table1.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.common.reference.no", null, RequestContextUtils.getLocale(request))+".", FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_CENTER, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table1.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.common.this.period", null, RequestContextUtils.getLocale(request))+" ( "+currencySymbol+" )", FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_CENTER, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table1.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.common.year.to.date", null, RequestContextUtils.getLocale(request))+" ( "+currencySymbol+" )", FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_CENTER, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            cell1 = new PdfPCell(table1);
            mainTable.addCell(cell1);
            cell1.setBorder(border);
            
            PdfPCell cell2 = null;
			PdfPTable table2 = new PdfPTable(7);
            table2.setWidths(new float[]{14, 18, 18, 12, 13, 12, 13});
            table2.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_CENTER, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table2.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.payroll.Employee", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_CENTER, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table2.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.payroll.employer", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_CENTER, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table2.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.payroll.Employee", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_CENTER, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table2.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.payroll.employer", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_CENTER, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table2.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.payroll.Employee", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_CENTER, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table2.addCell(getPdfPCellInstance(messageSource.getMessage("hrms.payroll.employer", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, Element.ALIGN_CENTER, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            cell2 = new PdfPCell(table2);
            mainTable.addCell(cell2);
            cell2.setBorder(border);
            
            String paidEPF=" ";
            String currentEPF=" ";
            if(mapEPF!=null){
            	if(mapEPF.get("paidEPF")>0){
            		paidEPF = currencyFormat.format(mapEPF.get("paidEPF"));
            	}
            	if(mapEPF.get("currentEPF")>0){
            		currentEPF = currencyFormat.format(mapEPF.get("currentEPF"));
            	}
            }
            PdfPCell cell3 = null;
			PdfPTable table3 = new PdfPTable(7);
            table3.setWidths(new float[]{14, 18, 18, 12, 13, 12, 13});
            table3.addCell(getPdfPCellInstance("  "+messageSource.getMessage("hrms.common.epf", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table3.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table3.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table3.addCell(getPdfPCellInstance(currentEPF, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table3.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table3.addCell(getPdfPCellInstance(paidEPF, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table3.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            cell3 = new PdfPCell(table3);
            mainTable.addCell(cell3);
            cell3.setBorder(border);
            
            
            PdfPCell cell4 = null;
			PdfPTable table4 = new PdfPTable(7);
            table4.setWidths(new float[]{14, 18, 18, 12, 13, 12, 13});
            table4.addCell(getPdfPCellInstance("  "+messageSource.getMessage("hrms.common.socso", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table4.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table4.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table4.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table4.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table4.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table4.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            cell4 = new PdfPCell(table4);
            mainTable.addCell(cell4);
            cell4.setBorder(border);
            
            double currenttax = payrollHistory.getIncometaxAmount();
            String currentTax=" ";
            if(currenttax>0){
            	currentTax=currencyFormat.format(currenttax);
            }
            
            HrmsCommonPayroll hrmsCommonPayroll = new HrmsCommonPayroll();
            double paidtax = hrmsCommonPayroll.getPaidTax(list, payrollHistory);
            String paidTax=" ";
            if(paidtax>0){
            	paidTax=currencyFormat.format(paidtax);
            }
            
            PdfPCell cell5 = null;
			PdfPTable table5 = new PdfPTable(7);
            table5.setWidths(new float[]{14, 18, 18, 12, 13, 12, 13});
            table5.addCell(getPdfPCellInstance("  "+messageSource.getMessage("hrms.payroll.tax", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table5.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table5.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table5.addCell(getPdfPCellInstance(currentTax, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table5.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table5.addCell(getPdfPCellInstance(paidTax, FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table5.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            cell5 = new PdfPCell(table5);
            mainTable.addCell(cell5);
            cell5.setBorder(border);
            
            
            
            PdfPCell cell6 = null;
			PdfPTable table6 = new PdfPTable(7);
            table6.setWidths(new float[]{14, 18, 18, 12, 13, 12, 13});
            table6.addCell(getPdfPCellInstance("  ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table6.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table6.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table6.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table6.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table6.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            table6.addCell(getPdfPCellInstance(" ", FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, Element.ALIGN_LEFT, Element.ALIGN_CENTER, paddingTop, paddingBottom, border));
            cell6 = new PdfPCell(table6);
            mainTable.addCell(cell6);
            cell6.setBorder(border);
			
            mainCell = new PdfPCell(mainTable);
            mainCell.setBorder(Rectangle.NO_BORDER);
		}catch(Exception e){
			e.printStackTrace();
		}
		return mainCell;
	}
	
	
		
	public PdfPCell getPdfPCellInstance(String name, FontContext font, int horizontalALIGN, int verticalALIGN, int paddingTop, int paddingBottom, int border){
		PdfPCell cell = new PdfPCell(new Paragraph(fontFamilySelector.process(name, font)));
		try{
			cell.setHorizontalAlignment(horizontalALIGN);
			cell.setVerticalAlignment(verticalALIGN);
			cell.setBorder(border);
			cell.setPaddingTop(paddingTop);
			cell.setPaddingBottom(paddingBottom);
		}catch(Exception e){
			e.printStackTrace();
		}
		return cell;
	}
	
	
	public void printSalarySlip(String userid, Date startdate, Date enddate, HttpServletRequest request, HttpServletResponse response, String historyid){
		try {
			DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			int border =0;
			User user = exportSalarySlipDAO.getUser(userid);
			Empprofile empprofile = exportSalarySlipDAO.getEmpprofile(userid);
			Useraccount useraccount = exportSalarySlipDAO.getUserAccount(userid);
			Company company = user.getCompany();
			PayrollHistory payrollHistory = exportSalarySlipDAO.getPayrollHistory(historyid);
			HrmsCommonPayroll hrmsCommonPayroll = new HrmsCommonPayroll();
			CompanyPreferences preferences = (CompanyPreferences) kwlCommonTablesDAO.getObject("com.krawler.common.admin.CompanyPreferences", company.getCompanyID());
			Date financialStartDate = hrmsCommonPayroll.getFinanacialYearStartDate(enddate, preferences.getFinancialmonth());
			Date financialEndDate = hrmsCommonPayroll.getFinanacialYearEndDate(enddate, preferences.getFinancialmonth()+11);
			IncomeTax incomeTax = getIncomTaxObj(request);
			List<PayrollHistory> list = null;
			Map<String, Double> mapEPF = null;
			if(incomeTax!=null){
				list = incomeTax.getPayrollHistories(userid, HrmsPayrollConstants.PAYROLL_HISTORY_STATUS_PROCESSED_FINAL, financialStartDate, financialEndDate);
				mapEPF = incomeTax.getEPF(list, payrollHistory);
			}
			 String ashtmlString = 
				 "<html> " +
             		"<head>" +
             			"<title>Salary Slip</title>" +
             			"<style type=\"text/css\">@media print {button#print {display: none;} .payslipFooter { position:fixed !important; bottom:5px !important;}}</style>" +
             			"<style type=\"text/css\">@media screen {.payslipFooter { display:none;}}</style>" +
             		"</head>" +
             		"<body style = \"font-family: Tahoma, Verdana, Arial, Helvetica, sans-sarif;\">" +
             			"<center>" +
             				"<div style='padding-bottom: 5px; padding-right: 5px;'>" +
             					"<h3>"+messageSource.getMessage("hrms.payroll.salary.slip", null, RequestContextUtils.getLocale(request))+"</h3>" +
             				"</div>" +
             			"</center>";
			 
			 	ashtmlString +=getEmployeeDetailsOfPrint(user, empprofile, useraccount, company, payrollHistory, sdf, border, request);
			 	ashtmlString +=getSalaryDetailsOfPrint(user, empprofile, useraccount, company, payrollHistory, sdf, border, request);
			 	ashtmlString +=getEmployeeAndEmployerDetailsOfPrint(user, empprofile, useraccount, company, payrollHistory, sdf, 1, list, mapEPF, request);
			 	
			 
			 	ashtmlString +=
			 		"<div style='float: left; padding-top: 3px; padding-right: 5px;'>" +
			 			"<button id = 'print' title='Print Payslip' onclick='window.print();' style='color: rgb(8, 55, 114);' href='#'>Print</button>" +
			 		"</div>" ;
	            ashtmlString +=
	            	"<div class='payslipFooter'><font size='2'  >"+messageSource.getMessage("hrms.payroll.computer.generated.payslip.not.require.signature", null, RequestContextUtils.getLocale(request))+"</font></div>";
	            ashtmlString +=
	            	"</body>" +
	            	"</html>";
	            response.getOutputStream().write(ashtmlString.getBytes());
	            response.getOutputStream().flush();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(response.getOutputStream() != null){
				    response.getOutputStream().flush();
				    response.getOutputStream().close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	
	public String getEmployeeDetailsOfPrint(User user, Empprofile empprofile, Useraccount useraccount, Company company, PayrollHistory payrollHistory, DateFormat sdf, int border, HttpServletRequest request){
		String str = "";
		try{
			String empIdName = getUserCode(useraccount.getEmployeeid(), useraccount.getUserID(), company.getCompanyID())+" "+profileHandlerDAOObj.getUserFullName(user.getUserID());
            String costCenter = payrollHistory.getCostCenter()!=null?payrollHistory.getCostCenter().getName():" ";
            String passport = " ";
            if(empprofile!=null && empprofile.getPassportno()!=null){
                passport = empprofile.getPassportno();
            }
            String period = sdf.format(payrollHistory.getPaycyclestartdate())+" to "+sdf.format(payrollHistory.getPaycycleenddate());
			str +="<p><table cellspacing=0 border="+border+" cellpadding=5 width='100%' >";
				str +="<tr>";
					str +="<td VALIGN='top'>";
						str +="<table cellspacing=0 border="+border+" width='100%' >";
							str +="<tr>";
								str +="<td WIDTH='25%' HEIGHT='100%' align='left'><b>"+messageSource.getMessage("hrms.common.company.name", null, RequestContextUtils.getLocale(request))+"</b></td>";
								str +="<td WIDTH='25%' HEIGHT='100%' align='left'>"+company.getCompanyName()+"</td>";
							str +="</tr>";
						str +="</table>";
					str +="</td>";
		
					str +="<td VALIGN='top'>";
						str +="<table cellspacing=0 border="+border+" width='100%' >";
							str +="<tr>";
								str +="<td WIDTH='25%' HEIGHT='100%' align='left'><b>"+messageSource.getMessage("hrms.common.payment.period", null, RequestContextUtils.getLocale(request))+"</b></td>";
								str +="<td WIDTH='25%' HEIGHT='100%' align='left'>"+period+"</td>";
							str +="</tr>";
						str +="</table>";
					str +="</td>";
				str +="</tr>";
				
				str +="<tr>";
					str +="<td VALIGN='top'>";
						str +="<table cellspacing=0 border="+border+" width='100%' >";
							str +="<tr>";
							str +="<td WIDTH='25%' HEIGHT='100%' align='left'><b>"+messageSource.getMessage("hrms.common.employee.id", null, RequestContextUtils.getLocale(request))+"/"+messageSource.getMessage("hrms.common.name", null, RequestContextUtils.getLocale(request))+"</b></td>";
							str +="<td WIDTH='25%' HEIGHT='100%' align='left'>"+empIdName+"</td>";
							str +="</tr>";
						str +="</table>";
					str +="</td>";
	
					str +="<td VALIGN='top'>";
						str +="<table cellspacing=0 border="+border+" width='100%' >";
							str +="<tr>";
								str +="<td WIDTH='25%' HEIGHT='100%' align='left'>&nbsp;</td>";
								str +="<td WIDTH='25%' HEIGHT='100%' align='left'>&nbsp;</td>";
							str +="</tr>";
						str +="</table>";
					str +="</td>";
				str +="</tr>";
			
			
				str +="<tr>";
					str +="<td VALIGN='top'>";
						str +="<table cellspacing=0 border="+border+" width='100%' >";
							str +="<tr>";
								str +="<td WIDTH='25%' HEIGHT='100%' align='left'><b>"+messageSource.getMessage("hrms.common.nric", null, RequestContextUtils.getLocale(request))+"/"+messageSource.getMessage("hrms.common.passport", null, RequestContextUtils.getLocale(request))+"</b></td>";
								str +="<td WIDTH='25%' HEIGHT='100%' align='left'>"+passport+"</td>";
							str +="</tr>";
						str +="</table>";
					str +="</td>";

					str +="<td VALIGN='top'>";
						str +="<table cellspacing=0 border="+border+" width='100%' >";
							str +="<tr>";
								str +="<td WIDTH='25%' HEIGHT='100%' align='left'><b>"+messageSource.getMessage("hrms.common.costcenter", null, RequestContextUtils.getLocale(request))+"</b></td>";
								str +="<td WIDTH='25%' HEIGHT='100%' align='left'>"+costCenter+"</td>";
							str +="</tr>";
						str +="</table>";
					str +="</td>";
				str +="</tr>";
			str +="</table></p>";	
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	
	public String getSalaryDetailsOfPrint(User user, Empprofile empprofile, Useraccount useraccount, Company company, PayrollHistory payrollHistory, DateFormat sdf, int border, HttpServletRequest request){
		String str = "";
		try{
			String currencySymbol = getCurrencySymbol(request, true);
			double net=0;
			double earningAmount=0;
			double deductionAmount=0;
			String val = null;
			String accno = useraccount!=null?useraccount.getAccno():" ";
			accno = StringUtil.isNullOrEmpty(accno)?" ":accno;
			str +="<table cellspacing=0 border="+border+" cellpadding=5 width='100%' >";
				str +="<tr>";
					str +="<td VALIGN='top' width='50%'>";
						str +="<table cellspacing=0 border="+border+" width='100%' >";
							str +="<tr>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='left'><b>"+messageSource.getMessage("hrms.common.payments", null, RequestContextUtils.getLocale(request))+"</b></th>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='right'><b>( "+currencySymbol+" )</b></th>";
							str +="</tr>";
							List<ComponentResourceMappingHistory> list = hrmsPayrollDAOObj.getSalaryDetails(user.getUserID(), payrollHistory.getPaycycleenddate(), Integer.parseInt(payrollHistory.getFrequency()), HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING, HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION);
							for(ComponentResourceMappingHistory crm: list){
								val = currencyFormat.format(crm.getAmount());
								earningAmount+=currencyFormat.parse(val).doubleValue();
								str +="<tr>";
									str +="<td WIDTH='25%' HEIGHT='100%' align='left'>"+crm.getComponent().getDescription()+"</td>";
									str +="<td WIDTH='25%' HEIGHT='100%' align='right'>"+val+"</td>";
								str +="</tr>";
							}	
						str +="</table>";
					str +="</td>";
					
					str +="<td VALIGN='top' width='50%'>";
						str +="<table cellspacing=0 border="+border+" width='100%' >";
							str +="<tr>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='left'><b>"+messageSource.getMessage("hrms.common.Deductions", null, RequestContextUtils.getLocale(request))+"</b></th>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='right'><b>( "+currencySymbol+" )</b></th>";
							str +="</tr>";
							list = hrmsPayrollDAOObj.getSalaryDetails(user.getUserID(), payrollHistory.getPaycycleenddate(), Integer.parseInt(payrollHistory.getFrequency()), HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_DEDUCTION, null);
							for(ComponentResourceMappingHistory crm: list){
								val = currencyFormat.format(crm.getAmount());
								deductionAmount+=currencyFormat.parse(val).doubleValue();
								str +="<tr>";
									str +="<td WIDTH='25%' HEIGHT='100%' align='left'>"+crm.getComponent().getDescription()+"</td>";
									str +="<td WIDTH='25%' HEIGHT='100%' align='right'>"+val+"</td>";
								str +="</tr>";
							}
							
							list = hrmsPayrollDAOObj.getSalaryDetails(user.getUserID(), payrollHistory.getPaycycleenddate(), Integer.parseInt(payrollHistory.getFrequency()), HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_TAX, null);
							for(ComponentResourceMappingHistory crm: list){
								val = currencyFormat.format(crm.getAmount());
								deductionAmount+=currencyFormat.parse(val).doubleValue();
								str +="<tr>";
									str +="<td WIDTH='25%' HEIGHT='100%' align='left'>"+crm.getComponent().getDescription()+"</td>";
									str +="<td WIDTH='25%' HEIGHT='100%' align='right'>"+val+"</td>";
								str +="</tr>";
							}
							
							//###############Unpaid Leaves START##########
				            if(payrollHistory.getUnpaidleavesAmount()>0){
					            val = currencyFormat.format(payrollHistory.getUnpaidleavesAmount());
								deductionAmount+=currencyFormat.parse(val).doubleValue();
								str +="<tr>";
									str +="<td WIDTH='25%' HEIGHT='100%' align='left'>"+messageSource.getMessage("hrms.payroll.Unpaidleaves", null, RequestContextUtils.getLocale(request))+" ("+payrollHistory.getUnpaidleaves()+")</td>";
									str +="<td WIDTH='25%' HEIGHT='100%' align='right'>"+val+"</td>";
								str +="</tr>";
				            }
				            //###############Unpaid Leaves START##########
							
							//###############IncomeTax START################
							if(payrollHistory.getIncometaxAmount()>0){
								val = currencyFormat.format(payrollHistory.getIncometaxAmount());
								deductionAmount+=currencyFormat.parse(val).doubleValue();
								str +="<tr>";
									str +="<td WIDTH='25%' HEIGHT='100%' align='left'>"+messageSource.getMessage("hrms.payroll.income.tax", null, RequestContextUtils.getLocale(request))+"</td>";
									str +="<td WIDTH='25%' HEIGHT='100%' align='right'>"+val+"</td>";
								str +="</tr>";
							}
							//###############IncomeTax END################
							
							//###############EPF START################
							Map<String, Double> map = getIncomTaxBenefits(request, payrollHistory.getUser().getUserID(), payrollHistory.getPaycycleenddate(), Integer.parseInt(payrollHistory.getFrequency()), payrollHistory);
				            if(map!=null && map.get("epf")!=null && map.get("epf")>0){
								val = currencyFormat.format(map.get("epf"));
								deductionAmount+=currencyFormat.parse(val).doubleValue();
								str +="<tr>";
									str +="<td WIDTH='25%' HEIGHT='100%' align='left'>"+messageSource.getMessage("hrms.common.epf", null, RequestContextUtils.getLocale(request))+"</td>";
									str +="<td WIDTH='25%' HEIGHT='100%' align='right'>"+val+"</td>";
								str +="</tr>";
							}
							//###############EPF END################
						str +="</table>";
					str +="</td>";
				str +="</tr>";
				
				str +="<tr>";
					str +="<td VALIGN='top' width='50%'>";
						str +="<table cellspacing=0 border="+border+" width='100%' >";
							str +="<tr>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='left'>&nbsp;</th>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='right'>------------</th>";
							str +="</tr>";
							str +="<tr>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='left'>"+messageSource.getMessage("hrms.common.subtotal", null, RequestContextUtils.getLocale(request))+"</th>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='right'>"+currencyFormat.format(earningAmount)+"</th>";
							str +="</tr>";
						str +="</table>";
					str +="</td>";
					
					str +="<td VALIGN='top' width='50%'>";
						str +="<table cellspacing=0 border="+border+" width='100%' >";
							str +="<tr>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='left'>&nbsp;</th>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='right'>------------</th>";
							str +="</tr>";
							str +="<tr>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='left'>"+messageSource.getMessage("hrms.common.subtotal", null, RequestContextUtils.getLocale(request))+"</th>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='right'>"+currencyFormat.format(deductionAmount)+"</th>";
							str +="</tr>";
						str +="</table>";
					str +="</td>";
				str +="</tr>";
				
				str +="<tr>";
					str +="<td VALIGN='top' width='50%'>";
						str +="<table cellspacing=0 border="+border+" width='100%' >";
							str +="<tr>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='left'>&nbsp;</th>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='right'>&nbsp;</th>";
							str +="</tr>";
						str +="</table>";
					str +="</td>";
				
					net = earningAmount-deductionAmount;
					str +="<td VALIGN='top' width='50%'>";
						str +="<table cellspacing=0 border="+border+" width='100%' >";
							str +="<tr>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='left'>"+messageSource.getMessage("hrms.payroll.net", null, RequestContextUtils.getLocale(request))+"(a/c:"+accno+")</th>";
								str +="<th WIDTH='25%' HEIGHT='100%' align='right'>"+currencyFormat.format(net)+"</th>";
							str +="</tr>";
						str +="</table>";
					str +="</td>";
				str +="</tr>";
			str +="</table></p>";
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	
	
	public String getEmployeeAndEmployerDetailsOfPrint(User user, Empprofile empprofile, Useraccount useraccount, Company company, PayrollHistory payrollHistory, DateFormat sdf, int border, List<PayrollHistory> list, Map<String, Double> mapEPF, HttpServletRequest request){
		String str = "";
		try{
			String currencySymbol = getCurrencySymbol(request, true);
			str +="<p><table cellspacing=0 border="+border+" cellpadding=5 width='100%' >";
				str +="<tr>";
					str +="<td WIDTH='10%' HEIGHT='100%' align='center'>&nbsp;</td>";
					str +="<td WIDTH='40%' HEIGHT='100%' colspan='2' align='center'><b>"+messageSource.getMessage("hrms.common.reference.no", null, RequestContextUtils.getLocale(request))+".</b></td>";
					str +="<td WIDTH='25%' HEIGHT='100%' colspan='2' align='center'><b>"+messageSource.getMessage("hrms.common.this.period", null, RequestContextUtils.getLocale(request))+" ( "+currencySymbol+" )</b></td>";
					str +="<td WIDTH='25%' HEIGHT='100%' colspan='2' align='center'><b>"+messageSource.getMessage("hrms.common.year.to.date", null, RequestContextUtils.getLocale(request))+" ( "+currencySymbol+" )</b></td>";
				str +="</tr>";
				
				str +="<tr>";
					str +="<td WIDTH='10%' HEIGHT='100%' align='center'>&nbsp;</td>";
					str +="<td WIDTH='20%' HEIGHT='100%' align='center'><b>"+messageSource.getMessage("hrms.payroll.Employee", null, RequestContextUtils.getLocale(request))+"</b></td>";
					str +="<td WIDTH='20%' HEIGHT='100%' align='center'><b>"+messageSource.getMessage("hrms.payroll.employer", null, RequestContextUtils.getLocale(request))+"</b></td>";
					str +="<td WIDTH='12%' HEIGHT='100%' align='center'><b>"+messageSource.getMessage("hrms.payroll.Employee", null, RequestContextUtils.getLocale(request))+"</b></td>";
					str +="<td WIDTH='13%' HEIGHT='100%' align='center'><b>"+messageSource.getMessage("hrms.payroll.employer", null, RequestContextUtils.getLocale(request))+"</b></td>";
					str +="<td WIDTH='12%' HEIGHT='100%' align='center'><b>"+messageSource.getMessage("hrms.payroll.Employee", null, RequestContextUtils.getLocale(request))+"</b></td>";
					str +="<td WIDTH='13%' HEIGHT='100%' align='center'><b>"+messageSource.getMessage("hrms.payroll.employer", null, RequestContextUtils.getLocale(request))+"</b></td>";
				str +="</tr>";
				
				String paidEPF="&nbsp;";
	            String currentEPF="&nbsp;";
	            if(mapEPF!=null){
	            	if(mapEPF.get("paidEPF")>0){
	            		paidEPF = currencyFormat.format(mapEPF.get("paidEPF"));
	            	}
	            	if(mapEPF.get("currentEPF")>0){
	            		currentEPF = currencyFormat.format(mapEPF.get("currentEPF"));
	            	}
	            }
				str +="<tr>";
					str +="<td WIDTH='10%' HEIGHT='100%' > "+messageSource.getMessage("hrms.common.epf", null, RequestContextUtils.getLocale(request))+"</td>";
					str +="<td WIDTH='20%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='20%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='12%' HEIGHT='100%' >"+currentEPF+"</td>";
					str +="<td WIDTH='13%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='12%' HEIGHT='100%' >"+paidEPF+"</td>";
					str +="<td WIDTH='13%' HEIGHT='100%' >&nbsp;</td>";
				str +="</tr>";
		
		
				str +="<tr>";
					str +="<td WIDTH='10%' HEIGHT='100%' > "+messageSource.getMessage("hrms.common.socso", null, RequestContextUtils.getLocale(request))+"</td>";
					str +="<td WIDTH='20%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='20%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='12%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='13%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='12%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='13%' HEIGHT='100%' >&nbsp;</td>";
				str +="</tr>";
                
				double currenttax = payrollHistory.getIncometaxAmount();
	            String currentTax="&nbsp;";
	            if(currenttax>0){
	            	currentTax=currencyFormat.format(currenttax);
	            }
	            
	            HrmsCommonPayroll hrmsCommonPayroll = new HrmsCommonPayroll();
	            double paidtax = hrmsCommonPayroll.getPaidTax(list, payrollHistory);
	            String paidTax="&nbsp;";
	            if(paidtax>0){
	            	paidTax=currencyFormat.format(paidtax);
	            }
				str +="<tr>";
					str +="<td WIDTH='10%' HEIGHT='100%' > "+messageSource.getMessage("hrms.payroll.tax", null, RequestContextUtils.getLocale(request))+"</td>";
					str +="<td WIDTH='20%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='20%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='12%' HEIGHT='100%' >"+currentTax+"</td>";
					str +="<td WIDTH='13%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='12%' HEIGHT='100%' >"+paidTax+"</td>";
					str +="<td WIDTH='13%' HEIGHT='100%' >&nbsp;</td>";
				str +="</tr>";
			
				str +="<tr>";
					str +="<td WIDTH='10%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='20%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='20%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='12%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='13%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='12%' HEIGHT='100%' >&nbsp;</td>";
					str +="<td WIDTH='13%' HEIGHT='100%' >&nbsp;</td>";
				str +="</tr>";
			str +="</table></p>";	
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	
	public void exportPayDetails(Date enddate, int frequency, HttpServletRequest request, HttpServletResponse response, String companyId, String module, Integer status){
		try {
			StringBuilder reportSB = new StringBuilder();
			List<String> headers = getHeaders(enddate, companyId, request);
			int count =0;
			int size = headers.size();
			for(String header: headers) {
				count++;
				if(count<size){
					reportSB.append("\"" + header + "\",");
				}else{
					reportSB.append("\"" + header + "\"\n");
				}
			}
			
			String [][] employeeDetails = getSalaryDetals(enddate, frequency, companyId, module, status, request);
			for(int i=0; i<employeeDetails.length; i++){
				size = employeeDetails[i].length;
				for(int j=0; j<employeeDetails[i].length; j++){
					if((j+1)<size){
						reportSB.append("\"" + employeeDetails[i][j] + "\",");
					}else{
						reportSB.append("\"" + employeeDetails[i][j] + "\"\n");
					}
				}
			}
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(reportSB.toString().getBytes());
			os.close();
			response.setHeader("Content-Disposition", "attachment; filename=\"" + "PayDetails" + ".csv\"");
			response.setContentType("application/octet-stream");
			response.setContentLength(os.size());
			response.getOutputStream().write(os.toByteArray());
			response.getOutputStream().flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("finally")
	public List<String> getHeaders(Date enddate, String companyId, HttpServletRequest request){
		List<String> headers = new ArrayList<String>();
		try{
			headers.add(messageSource.getMessage("hrms.payroll.resource", null, RequestContextUtils.getLocale(request)));
			headers.add(messageSource.getMessage("hrms.common.FullName", null, RequestContextUtils.getLocale(request)));
			headers.add(messageSource.getMessage("hrms.common.costcenter", null, RequestContextUtils.getLocale(request)));
			headers.add(messageSource.getMessage("hrms.payroll.bank.account", null, RequestContextUtils.getLocale(request)));
			String amountText = messageSource.getMessage("hrms.payroll.Amount", null, RequestContextUtils.getLocale(request));
            headers.addAll(exportSalarySlipDAO.getHeaders(enddate, companyId, amountText));
			headers.add(messageSource.getMessage("hrms.payroll.Unpaidleaves", null, RequestContextUtils.getLocale(request)));
			headers.add(messageSource.getMessage("hrms.payroll.income.tax", null, RequestContextUtils.getLocale(request)));
			headers.add(messageSource.getMessage("hrms.payroll.gross", null, RequestContextUtils.getLocale(request)));
			headers.add(messageSource.getMessage("hrms.timesheet.total", null, RequestContextUtils.getLocale(request)));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return headers;
		}
	}
	
	@SuppressWarnings("finally")
	public String[][] getSalaryDetals(Date enddate, int frequency, String companyId, String module, Integer status, HttpServletRequest request){
		String employeeDetails [][] = null;
		List<User> users = null;
		try{
			users = exportSalarySlipDAO.getUsersList(String.valueOf(frequency), enddate, companyId, module, status);
			List<String> ids = exportSalarySlipDAO.getComponentIds(enddate, frequency, companyId);
			Map<String, ComponentResourceMappingHistory> map = null;
			employeeDetails = new String[users.size()+2][ids.size()+8];
			double amount[] = new double[ids.size()+4];
			int i = 0;
			int j = 0;
			for(User user: users){
				double net = 0;
				double gross = 0;
				double deduction = 0;
				j = 4;
				PayrollHistory payrollHistory = getPayrollHistory(user.getUserID(), enddate, frequency, module, status);
                if(payrollHistory!=null){
                    Useraccount useraccount = exportSalarySlipDAO.getUserAccount(user.getUserID());
                    User userTemp = exportSalarySlipDAO.getUser(user.getUserID());
                    String uname = userTemp.getFirstName();
                    if(userTemp.getLastName()!=null){
                        uname +=" "+userTemp.getLastName();
                    }
                    employeeDetails[i][0]=getUserCode(useraccount.getEmployeeid(), user.getUserID(), companyId);
                    employeeDetails[i][1]=uname;
                    employeeDetails[i][2]=(payrollHistory.getCostCenter()!=null?payrollHistory.getCostCenter().getName():"");
                    employeeDetails[i][3]=(useraccount.getAccno()!=null?useraccount.getAccno():"");
                    map = exportSalarySlipDAO.getSalaryHistoryForUser(enddate, frequency, user.getUserID());
                    for(String id: ids){
                        if(map.get(id)!=null){
                            amount[j-4] += map.get(id).getAmount();
                            employeeDetails[i][j] = String.valueOf(map.get(id).getAmount());
                            if(map.get(id).getComponent().getSubtype().getComponenttype()==HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_EARNING || map.get(id).getComponent().getSubtype().getComponenttype()==HrmsPayrollConstants.PAYROLL_COMPONENT_TYPE_OTHER_REMUNERATION){
                            	employeeDetails[i][j] = String.valueOf(map.get(id).getAmount());
                            	gross += map.get(id).getAmount();
                            }else{
                            	employeeDetails[i][j] = String.valueOf("-"+map.get(id).getAmount());
                            	deduction += map.get(id).getAmount();
                            }
                        }else{
                            employeeDetails[i][j] = "";
                        }
                        j++;
                    }
                    
                    amount[j-4] += payrollHistory.getUnpaidleavesAmount();
                    employeeDetails[i][j] = String.valueOf("-"+payrollHistory.getUnpaidleavesAmount());
                    deduction += payrollHistory.getUnpaidleavesAmount();
                    
                    j++;
                    amount[j-4] += payrollHistory.getIncometaxAmount();
                    employeeDetails[i][j] = String.valueOf("-"+payrollHistory.getIncometaxAmount());
                    deduction += payrollHistory.getIncometaxAmount();
                    
                    j++;
                    if(payrollHistory.getEarning()!=null){
                        amount[j-4] += payrollHistory.getEarning();
                        employeeDetails[i][j] = String.valueOf(payrollHistory.getEarning());
                    }else{
                    	amount[j-4] += gross;
                        employeeDetails[i][j] = String.valueOf(gross);
                    }
                    
                    j++;
                    if(payrollHistory.getNet()!=null){
                        amount[j-4] += payrollHistory.getNet();
                        employeeDetails[i][j] = String.valueOf(payrollHistory.getNet());
                    }else{
                    	net = gross - deduction;
                        amount[j-4] += net;
                        employeeDetails[i][j] = String.valueOf(net);
                    }
                    i++;
                }
				
			}
			
			for(int a=0; a<employeeDetails[0].length; a++){
				employeeDetails[employeeDetails.length-2][a] = "";
			}
			employeeDetails[employeeDetails.length-1][0] = messageSource.getMessage("hrms.timesheet.total", null, RequestContextUtils.getLocale(request));
			employeeDetails[employeeDetails.length-1][1] = "";
			employeeDetails[employeeDetails.length-1][2] = "";
			employeeDetails[employeeDetails.length-1][3] = "";
			for(int a=0; a<amount.length; a++){
				employeeDetails[employeeDetails.length-1][a+4] = String.valueOf(amount[a]);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return employeeDetails;
		}
	}
	
	public String getUserCode(int empid, String userid, String companyid){
		String code = null;
		try{
			HashMap<String, Object> requestParams = new HashMap<String, Object>();
			requestParams.put("empid", empid);
			requestParams.put("companyid", companyid);
			Useraccount useraccount = exportSalarySlipDAO.getUserAccount(userid);
			if(useraccount.getEmployeeIdFormat()==null){
            	code = useraccount.getEmployeeid() == null ? "" : profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString();
            }else{
            	requestParams.put("standardEmpId", profileHandlerDAOObj.getEmpidFormatEdit(requestParams).getEntityList().get(0).toString());
            	requestParams.put("employeeIdFormat", useraccount.getEmployeeIdFormat());
            	code = profileHandlerDAOObj.getNewEmployeeIdFormat(requestParams);
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		return code;
	}
	
	@SuppressWarnings({ "unchecked"})
	public PayrollHistory getPayrollHistory(String userid, Date enddate, int frequency, String module, Integer status){
		PayrollHistory payrollHistory = null;
		try{
			HashMap<String, Object> requestParams = new HashMap<String, Object>();
			ArrayList filter_names = new ArrayList(), filter_values = new ArrayList();
			filter_names.add("user.userID");
	        filter_values.add(userid);
			
	        filter_names.add("paycycleenddate");
	        filter_values.add(enddate);

	        filter_names.add("frequency");
	        filter_values.add(String.valueOf(frequency));
	        
	        filter_names.add("salarystatus");
	        filter_values.add(status);

	        requestParams.put("filter_names", filter_names);
	        requestParams.put("filter_values", filter_values);
            requestParams.put("type", module);

            List<PayrollHistory> list = hrmsPayrollDAOObj.getPayrollHistory(requestParams).getEntityList();
	        for(PayrollHistory history: list){
	        	payrollHistory = history;
	        }
		}catch(Exception e){
			e.printStackTrace();
		}
		return payrollHistory;
	}
	
	
	public IncomeTax getIncomTaxObj(HttpServletRequest request){
        IncomeTax itax = null;
        try{
            Company company = (Company) kwlCommonTablesDAO.getObject("com.krawler.common.admin.Company", sessionHandlerImplObj.getCompanyid(request));
            String country= company.getCountry().getID();

            if(this.calculatorMap.containsKey(country)){
                itax = this.calculatorMap.get(country);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return itax;
    }
	
	
	public Map<String, Double> getIncomTaxBenefits(HttpServletRequest request, String userid, Date financialDate, int frequency, PayrollHistory history){

    	Map<String, Double> map = null;
        IncomeTax itax = getIncomTaxObj(request);
        if(itax!=null){
            itax.setUserid(userid);
            itax.setFinancialDate(financialDate);
            itax.setFrequency(frequency);
            map = itax.getUserTaxBenefitsData(history);
        }
        return map;
    }
	
	
	public String getCurrencySymbol(HttpServletRequest request, boolean print) throws SessionExpiredException {
        String symbol="";
        KWLCurrency currency = null;
        try{
        	String currencyID = sessionHandlerImplObj.getCurrencyID(request);
        	currency = (KWLCurrency) kwlCommonTablesDAO.getObject("com.krawler.common.admin.KWLCurrency", currencyID);
        	if(currency!=null){
        		if(print){
        			symbol = currency.getSymbol();
        		}else{
        			symbol = String.valueOf((char) Integer.parseInt(currency.getHtmlcode(),16));
        		}
        	}
        }catch(Exception e){
        	if(currency!=null){
        		symbol = currency.getHtmlcode();
        	}
        }
        return symbol;
    }
	
	@Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
