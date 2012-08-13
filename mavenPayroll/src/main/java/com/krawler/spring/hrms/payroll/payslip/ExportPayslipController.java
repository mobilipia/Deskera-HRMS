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
package com.krawler.spring.hrms.payroll.payslip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import masterDB.Historydetail;
import masterDB.Payhistory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;
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
import com.krawler.common.util.URLUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.esp.servlets.ProfileImageServlet;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONObject;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class ExportPayslipController extends MultiActionController implements MessageSourceAware{
	
	private String successView;
    private sessionHandlerImpl sessionHandlerImplObj;
	private MessageSource messageSource;
	private kwlCommonTablesDAO kwlCommonTablesDAOObj;
	private hrmsCommonDAO hrmsCommonDAOObj;
	private ExportPayslipDAO exportPayslipDAO;
	private profileHandlerDAO profileHandlerDAOObj;
	
	public void setExportPayslipDAO(ExportPayslipDAO exportPayslipDAO) {
		this.exportPayslipDAO = exportPayslipDAO;
	}

	public sessionHandlerImpl getSessionHandlerImplObj() {
        return sessionHandlerImplObj;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public String getSuccessView() {
		return successView;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj;
    }
    
    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }
    
    public void setProfileHandlerDAO(profileHandlerDAO profileHandlerDAOObj) {
        this.profileHandlerDAOObj = profileHandlerDAOObj;
    }
    
    
	@Override
	public void setMessageSource(MessageSource ms) {
		messageSource = ms;
	}
	
	private static final long serialVersionUID = -763555229410947890L;
	private FontFamilySelector fontFamilySelector = null;
	
	public ExportPayslipController(){
    	fontFamilySelector = FontSetting.getFontFamilySelector();
    }
	
	public class EndPage extends PdfPageEventHelper {
    	HttpServletRequest request;
    	
    	public EndPage(HttpServletRequest request){
    		this.request = request;
    	}
    	
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Rectangle page = document.getPageSize();
                PdfPTable footer = new PdfPTable(1);
                footer.setWidthPercentage(100);
                footer.setSpacingBefore(20);
                PdfPCell footerPageNocell = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.computer.generated.payslip.not.require.signature", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
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
	
	public ModelAndView exportPDF(HttpServletRequest request,HttpServletResponse response) {
		JSONObject jsonResp = new JSONObject();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer=null;
        try {

        	Company company = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", sessionHandlerImplObj.getCompanyid(request));
            int i = 0;
            DecimalFormat decfm=new DecimalFormat("#,##0.00");
            Date d2 = null;
            Date d3 = null;
            int days=0;
            boolean showborder = true;
            if(!StringUtil.isNullOrEmpty(request.getParameter("showborder"))){
                showborder = Boolean.parseBoolean(request.getParameter("showborder"));
            }
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String stDate = request.getParameter("stdate").replace("/", "-");
            String endDate = request.getParameter("enddate").replace("/", "-");
            d2 = sdf.parse(stDate);
            d3 = sdf.parse(endDate);

            days = getWorkingDays(request.getParameter("stdate").replace("/", "-"), request.getParameter("enddate").replace("/", "-"), company.getCompanyID());
            
            String historyid = "";
            
            List<Payhistory> lst = exportPayslipDAO.getPayhistory(request.getParameter("empid"), d2, d3);; 
            if (lst !=null && lst.size() == 0) {
                throw new Exception("Exception occured");
            } else if (lst !=null && lst.size() > 0) {
            	String currSymbol= getCurrencySymbol(request, false);
                masterDB.Payhistory payhistory = (masterDB.Payhistory) lst.get(0);
                historyid = payhistory.getHistoryid();
                double unpaidleaves = payhistory.getUnpaidleaves();
                
                Document document = new Document(PageSize.A4, 15, 15, 15, 15);
                writer = PdfWriter.getInstance(document, baos);
                writer.setPageEvent(new EndPage(request));
                document.open();

                String userid = request.getParameter("empid");

                User userinfo = payhistory.getUserID();
                Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", userinfo.getUserID());
                String uname = userinfo.getLastName() != null ? userinfo.getLastName() : "";
                String empid = getUserCode(ua, company.getCompanyID());
                
                String department = payhistory.getDepartment();
                String designation = getUserDesignation(ua);
                String bankacc = ua.getAccno() != null ? ua.getAccno() : "";
                Empprofile empprof=null;
                String epf="";
                String dateofjoin="";
                try{
                   empprof=(Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", userid);
                   epf=StringUtil.isNullOrEmpty(empprof.getPfno())?"":empprof.getPfno();
                   dateofjoin=empprof.getJoindate()!=null?empprof.getJoindate().toString():"";
                }catch(Exception ex){
                   epf="";
                }                
                PdfPTable mainTable = new PdfPTable(1);
                mainTable.setWidthPercentage(100);
                PdfPTable table1 = new PdfPTable(3);
                table1.setWidthPercentage(100);
                table1.setWidths(new float[]{40, 40, 20});
                PdfPTable userTable1 = new PdfPTable(1);
                String cmpid=AuthHandler.getCompanyid(request);
                userTable1.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                userTable1.setWidthPercentage(10);
                PdfPCell cell11 = null;

                PdfPCell cell1x = new PdfPCell(new Paragraph(fontFamilySelector.process("  ", FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN)));
                cell1x.setBorder(0);
                userTable1.addCell(cell1x);
                try {
                    String imgPath = StorageHandler.GetProfileImgStorePath()+cmpid+".png";
                    
                    if(StringUtil.isStandAlone()){
                        imgPath = URLUtil.getPageURL(request, "").concat(ProfileImageServlet.defaultCompanyImgPath);
                    }
                    
                    Image img = Image.getInstance(imgPath);
                    cell11 = new PdfPCell(img);
                    cell11.setPaddingLeft(5);
                } catch (Exception e) {
                    cell11 = new PdfPCell(new Paragraph(fontFamilySelector.process(AuthHandler.getCompanyName(request),FontContext.TABLE_BOLD_TIMES_NEW_ROMAN)));
                    cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                if(cell11!=null){
                cell11.setBorder(0);                
                userTable1.addCell(cell11);
                }

                PdfPCell cell1 = new PdfPCell(new Paragraph(fontFamilySelector.process("", FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN)));
                cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell1.setBorder(0);
                userTable1.addCell(cell1);

                PdfPTable userTable2 = new PdfPTable(1);
                userTable2.setWidthPercentage(100);
                PdfPCell cell = new PdfPCell(new Paragraph(fontFamilySelector.process(company.getCompanyName(), FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN)));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setPaddingTop(15);
                userTable2.addCell(cell);
                cell = new PdfPCell(new Paragraph(fontFamilySelector.process(company.getCity(), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                userTable2.addCell(cell);
                cell = new PdfPCell(new Paragraph(fontFamilySelector.process(company.getAddress(), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                userTable2.addCell(cell);
                cell = new PdfPCell(new Paragraph(fontFamilySelector.process(company.getState(), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                userTable2.addCell(cell);
                cell = new PdfPCell(new Paragraph(fontFamilySelector.process(company.getZipCode(), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                userTable2.addCell(cell);
                cell = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.payslip.period.params", new Object[]{request.getParameter("stdate"), request.getParameter("enddate")}, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                userTable2.addCell(cell);

                PdfPTable userTable3 = new PdfPTable(1);
                userTable3.setWidthPercentage(100);
                PdfPCell cell2 = new PdfPCell(new Paragraph(fontFamilySelector.process("", FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell2.setBorder(0);
                userTable1.addCell(cell2);

                PdfPCell mainCell11 = new PdfPCell(userTable1);
                if(!showborder){
                    mainCell11.setBorder(0);
                }else{
                    mainCell11.setBorder(Rectangle.LEFT);
                }

                table1.addCell(mainCell11);
                PdfPCell mainCell13 = new PdfPCell(userTable2);
                mainCell13.setBorder(0);
                table1.addCell(mainCell13);
                PdfPCell mainCell15 = new PdfPCell(userTable3);
                if(!showborder){
                    mainCell15.setBorder(0);
                }else{
                    mainCell15.setBorder(Rectangle.RIGHT);
                }
                table1.addCell(mainCell15);

                PdfPCell mainCell12 = new PdfPCell(new Paragraph(""));
                if(!showborder){
                    mainCell12.setBorder(0);
                }else{
                    mainCell12.setBorder(Rectangle.LEFT);
                }
                table1.addCell(mainCell12);
                PdfPCell mainCell14 = new PdfPCell(new Paragraph(""));
                mainCell14.setBorder(0);
                table1.addCell(mainCell14);
                PdfPCell mainCell16 = new PdfPCell(new Paragraph(""));
                if(!showborder){
                    mainCell16.setBorder(0);
                }else{
                    mainCell16.setBorder(Rectangle.RIGHT);
                }
                table1.addCell(mainCell16);

                PdfPCell mainCell1 = new PdfPCell(table1);
                if(!showborder){
                    mainCell1.setBorder(0);
                }else{
                    mainCell1.setBorder(1);
                }
                mainTable.addCell(mainCell1);
                // __________________________________________________________________________
                PdfPTable table2 = new PdfPTable(4);
                table2.setWidthPercentage(100);
                table2.setWidths(new float[]{20, 30, 20, 30});

                PdfPCell h11 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.common.employee.code", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                h11.setHorizontalAlignment(Element.ALIGN_LEFT);
                h11.setPadding(5);
                if(!showborder){
                    h11.setBorder(0);
                }
                table2.addCell(h11);
                PdfPCell h12 = new PdfPCell(new Paragraph(fontFamilySelector.process("" + empid, FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                h12.setHorizontalAlignment(Element.ALIGN_LEFT);
                h12.setPadding(5);
                if(!showborder){
                    h12.setBorder(0);
                }
                table2.addCell(h12);
                PdfPCell h13 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.common.employee.name", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                h13.setHorizontalAlignment(Element.ALIGN_LEFT);
                h13.setPadding(5);
                if(!showborder){
                    h13.setBorder(0);
                }
                table2.addCell(h13);
                PdfPCell h14 = new PdfPCell(new Paragraph(fontFamilySelector.process(userinfo.getFirstName()+" "+ uname, FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                h14.setHorizontalAlignment(Element.ALIGN_LEFT);
                h14.setPadding(5);
                if(!showborder){
                    h14.setBorder(0);
                }
                table2.addCell(h14);

                PdfPCell h21 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.common.department", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                h21.setHorizontalAlignment(Element.ALIGN_LEFT);
                h21.setPadding(5);
                if(!showborder){
                    h21.setBorder(0);
                }
                table2.addCell(h21);
                PdfPCell h22 = new PdfPCell(new Paragraph(fontFamilySelector.process(department, FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                h22.setHorizontalAlignment(Element.ALIGN_LEFT);
                h22.setPadding(5);
                if(!showborder){
                    h22.setBorder(0);
                }
                table2.addCell(h22);
                PdfPCell h33 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.common.designation", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                h33.setHorizontalAlignment(Element.ALIGN_LEFT);
                h33.setPadding(5);
                if(!showborder){
                    h33.setBorder(0);
                }
                table2.addCell(h33);
                PdfPCell h34 = new PdfPCell(new Paragraph(fontFamilySelector.process(designation, FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                h34.setHorizontalAlignment(Element.ALIGN_LEFT);
                h34.setPadding(5);
                if(!showborder){
                    h34.setBorder(0);
                }
                table2.addCell(h34);
                PdfPCell h43 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.common.bank.ac.no", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                h43.setHorizontalAlignment(Element.ALIGN_LEFT);
                h43.setPadding(5);
                if(!showborder){
                    h43.setBorder(0);
                }
                table2.addCell(h43);
                PdfPCell h44 = new PdfPCell(new Paragraph(fontFamilySelector.process(bankacc, FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                h44.setHorizontalAlignment(Element.ALIGN_LEFT);
                h44.setPadding(5);
                if(!showborder){
                    h44.setBorder(0);
                }
                table2.addCell(h44);             
                PdfPCell h61 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.common.working.days", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                h61.setHorizontalAlignment(Element.ALIGN_LEFT);
                h61.setPadding(5);
                if(!showborder){
                    h61.setBorder(0);
                }
                table2.addCell(h61);
                PdfPCell h62 = new PdfPCell(new Paragraph(fontFamilySelector.process(String.valueOf(days), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                h62.setHorizontalAlignment(Element.ALIGN_LEFT);
                h62.setPadding(5);
                if(!showborder){
                    h62.setBorder(0);
                }
                table2.addCell(h62);
                PdfPCell h63 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.common.e.p.f", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                h63.setHorizontalAlignment(Element.ALIGN_LEFT);
                h63.setPadding(5);
                if(!showborder){
                    h63.setBorder(0);
                }
                table2.addCell(h63);
                PdfPCell h64 = new PdfPCell(new Paragraph(fontFamilySelector.process(epf, FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                h64.setHorizontalAlignment(Element.ALIGN_LEFT);
                h64.setPadding(5);
                if(!showborder){
                    h64.setBorder(0);
                }
                table2.addCell(h64);
                PdfPCell h65 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.common.DateofJoining", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                h65.setHorizontalAlignment(Element.ALIGN_LEFT);
                h65.setPadding(5);
                if(!showborder){
                    h65.setBorder(0);
                }
                table2.addCell(h65);
                PdfPCell h66 = new PdfPCell(new Paragraph(fontFamilySelector.process(dateofjoin, FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                h66.setHorizontalAlignment(Element.ALIGN_LEFT);
                h66.setPadding(5);
                if(!showborder){
                    h66.setBorder(0);
                }
                table2.addCell(h66);
                
                for (int y = 0; y < 2; y++) {
                    PdfPCell h71 = new PdfPCell(new Paragraph(fontFamilySelector.process("  ", FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                    h71.setHorizontalAlignment(Element.ALIGN_LEFT);
                    if(!showborder){
                        h71.setBorder(0);
                    } else{
                         h71.setBorder(Rectangle.LEFT);
                    }
                    table2.addCell(h71);
                    for (i = 0; i < 2; i++) {
                        h71 = new PdfPCell(new Paragraph(fontFamilySelector.process("  ", FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                        h71.setHorizontalAlignment(Element.ALIGN_LEFT);
                        h71.setBorder(0);
                        table2.addCell(h71);
                    }
                    h71 = new PdfPCell(new Paragraph(fontFamilySelector.process("  ", FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                    h71.setHorizontalAlignment(Element.ALIGN_LEFT);
                    if(!showborder){
                        h71.setBorder(0);
                    } else{
                         h71.setBorder(Rectangle.RIGHT);
                    }
                    table2.addCell(h71);
                }
                PdfPCell mainCell2 = new PdfPCell(table2);
                if(!showborder){
                        mainCell2.setBorder(0);
                } else{
                     mainCell2.setBorder(1);
                }
                mainTable.addCell(mainCell2);

                PdfPTable table3main = new PdfPTable(1);
                table3main.setWidthPercentage(100);
                PdfPTable table7main = new PdfPTable(1);
                table7main.setWidthPercentage(100);

                PdfPTable table31 = new PdfPTable(4);
                table31.setWidthPercentage(100);
                table31.setWidths(new float[]{30,20,30,20});
                PdfPTable table41 = new PdfPTable(2);
                table41.setWidthPercentage(100);
                table41.setWidths(new float[]{50,50});

              //***************************************************************************************************************
                PdfPCell s11 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.Earnings", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                s11.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(!showborder){
                        s11.setBorder(0);
                } else{
                     s11.setBorder(Rectangle.LEFT + Rectangle.BOTTOM);
                }
                s11.setPadding(5);
                table31.addCell(s11);
                PdfPCell s12 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.amount.params", new Object[]{currSymbol}, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                s12.setHorizontalAlignment(Element.ALIGN_RIGHT);
                s12.setPadding(5);
                if(!showborder){
                        s12.setBorder(0);
                } else{
                     s12.setBorder(Rectangle.LEFT + Rectangle.BOTTOM);
                }
                table31.addCell(s12);
                PdfPCell s14 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.Deduction", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                s14.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(!showborder){
                        s14.setBorder(0);
                } else{
                     s14.setBorder(Rectangle.LEFT + Rectangle.BOTTOM);
                }
                s14.setPadding(5);
                table31.addCell(s14);
                PdfPCell s15 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.amount.params", new Object[]{currSymbol}, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                s15.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(!showborder){
                     s15.setBorder(0);
                } else{
                     s15.setBorder(Rectangle.LEFT + Rectangle.RIGHT + Rectangle.BOTTOM);
                }
                s15.setPadding(5);
                table31.addCell(s15);
                int Wcount = 0;
                float taxtotal = 0;

                List<Historydetail> lst1 = exportPayslipDAO.getHistorydetail(historyid, "Wages", "Basic");
                List<Historydetail> list = exportPayslipDAO.getHistorydetailNotType(historyid, "Wages", "Basic");
                if(list!=null){
                	lst1.addAll(list);
                }

                List<Historydetail> lst2 = exportPayslipDAO.getHistorydetail(historyid, "Taxes");

                CompanyPreferences cp = hrmsCommonDAOObj.getCompanyPreferences(company.getCompanyID());
                int financialMonth = cp.getFinancialmonth();
                Calendar c1 = Calendar.getInstance();
                c1.setTime(d3);
                c1.set(Calendar.MONTH, financialMonth);
                c1.set(Calendar.DATE, 1);
                Date d = c1.getTime();
                Date DOJ = new java.util.Date(empprof.getJoindate().getTime());
                if(d3.before(d)){
                    int currentYear = c1.get(Calendar.YEAR) - 1;
                    c1.set(Calendar.YEAR, currentYear);
                    d = c1.getTime();
                }
                if(DOJ.after(d)){
                    c1.setTime(DOJ);
                    c1.set(Calendar.DATE, 1);
                    d = c1.getTime();
                }
                List<Historydetail> lst3 = exportPayslipDAO.getHistorydetail(historyid, "Deduction");
                int size= lst1.size()>(lst2.size()+lst3.size())?lst1.size():lst2.size()+lst3.size();
                float wagetotal = 0;
                double totalYTDEarning = 0, totalYTDDeduction = 0;
                for (i = 0; i < size; i++) {
                   masterDB.Historydetail hd =null;
                   if(i<lst1.size()){
                        hd = (masterDB.Historydetail) lst1.get(i);
                        PdfPCell s21 = new PdfPCell(new Paragraph(fontFamilySelector.process(hd.getType(), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                        s21.setHorizontalAlignment(Element.ALIGN_LEFT);
                        if(!showborder){
                             s21.setBorder(0);
                        } else{
                             s21.setBorder(Rectangle.LEFT);
                        }
                        s21.setPadding(5);
                        table31.addCell(s21);

                        BigDecimal bd = new BigDecimal(hd.getAmount());
                        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        double newamount = bd.doubleValue();

                        PdfPCell s22 = new PdfPCell(new Paragraph(fontFamilySelector.process(String.valueOf(decfm.format(newamount)), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                        s22.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        if(!showborder){
                             s22.setBorder(0);
                        } else{
                             s22.setBorder(Rectangle.LEFT);
                        }
                        s22.setPadding(5);
                        table31.addCell(s22);

                        List<Historydetail> lstw = exportPayslipDAO.getHistorydetail(request.getParameter("empid"), d, d3, hd.getType());
                        double wageAmt = 0;
                        masterDB.Historydetail hd1 =null;
                        for (int y = 0; y < lstw.size(); y++) {
                            hd1 = (masterDB.Historydetail) lstw.get(y);
                            wageAmt += Double.parseDouble(hd1.getAmount());
                        }
                        wagetotal = wagetotal + Float.parseFloat(hd.getAmount());
                        totalYTDEarning = totalYTDEarning + wageAmt;
                        Wcount++;
                   }else{
                      for(int j=0;j<2;j++){
                        PdfPCell s21 = new PdfPCell(new Paragraph(fontFamilySelector.process("", FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                        s21.setHorizontalAlignment(Element.ALIGN_LEFT);
                        if(!showborder){
                             s21.setBorder(0);
                        } else{
                             s21.setBorder(Rectangle.LEFT);
                        }
                        s21.setPadding(5);
                        table31.addCell(s21);
                      }
                   }
                   if(i<lst2.size()){
                        hd = (masterDB.Historydetail) lst2.get(i);
                        PdfPCell s24 = new PdfPCell(new Paragraph(fontFamilySelector.process(hd.getType(), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                        s24.setHorizontalAlignment(Element.ALIGN_LEFT);
                        if(!showborder){
                             s24.setBorder(0);
                        } else{
                             s24.setBorder(Rectangle.LEFT);
                        }
                        s24.setPadding(5);
                        table31.addCell(s24);

                        BigDecimal bd = new BigDecimal(hd.getAmount());
                        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        double newamount = bd.doubleValue();

                        PdfPCell s25 = new PdfPCell(new Paragraph(fontFamilySelector.process(decfm.format(newamount), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                        s25.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        if(!showborder){
                             s25.setBorder(0);
                        } else{
                             s25.setBorder(Rectangle.RIGHT + Rectangle.LEFT);
                        }
                        s25.setPadding(5);
                        table31.addCell(s25);

                        List<Historydetail> lstw1 = exportPayslipDAO.getHistorydetail(request.getParameter("empid"), d, d3, hd.getType());
                        double taxAmt = 0;
                        masterDB.Historydetail hd2 =null;
                        for (int y = 0; y < lstw1.size(); y++) {
                            hd2 = (masterDB.Historydetail) lstw1.get(y);
                            taxAmt += Double.parseDouble(hd2.getAmount());
                        }
                        taxtotal = taxtotal + Float.parseFloat(hd.getAmount());
                        totalYTDDeduction = totalYTDDeduction + taxAmt;
                   }else {
                      if(i-lst2.size() < lst3.size()){
                        hd = (masterDB.Historydetail) lst3.get(i-lst2.size());
                        PdfPCell s24 = new PdfPCell(new Paragraph(fontFamilySelector.process(hd.getType(), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                        if(hd.getType().equals("Unpaid_leaves")){
                            s24 = new PdfPCell(new Paragraph(fontFamilySelector.process(hd.getType()+"("+unpaidleaves+")", FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                        }
                        s24.setHorizontalAlignment(Element.ALIGN_LEFT);
                        if(!showborder){
                             s24.setBorder(0);
                        } else{
                             s24.setBorder(Rectangle.LEFT);
                        }
                        s24.setPadding(5);
                        table31.addCell(s24);

                        BigDecimal bd = new BigDecimal(hd.getAmount());
                        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        double newamount = bd.doubleValue();

                        PdfPCell s25 = new PdfPCell(new Paragraph(fontFamilySelector.process(decfm.format(newamount), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                        s25.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        if(!showborder){
                             s25.setBorder(0);
                        } else{
                             s25.setBorder(Rectangle.RIGHT + Rectangle.LEFT);;
                        }
                        s25.setPadding(5);
                        table31.addCell(s25);

                        List<Historydetail> lstw2 = exportPayslipDAO.getHistorydetail(request.getParameter("empid"), d, d3, hd.getType());
                        masterDB.Historydetail hd3 =null;
                        double deductAmt = 0;
                        for (int y = 0; y < lstw2.size(); y++) {
                            hd3 = (masterDB.Historydetail) lstw2.get(y);
                            deductAmt += Double.parseDouble(hd3.getAmount());
                        }

                        taxtotal = taxtotal + Float.parseFloat(hd.getAmount());
                        totalYTDDeduction = totalYTDDeduction + deductAmt;
                      }else{
                          for(int j=0;j<2;j++){
                            PdfPCell s21 = new PdfPCell(new Paragraph(fontFamilySelector.process("", FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                            s21.setHorizontalAlignment(Element.ALIGN_LEFT);
                            if(!showborder){
                                 s21.setBorder(0);
                            } else{
                                 s21.setBorder(Rectangle.LEFT+Rectangle.RIGHT);
                            }
                            s21.setPadding(5);
                            table31.addCell(s21);
                         }
                     }
                   }
                 }
                PdfPCell s91 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.total.earnings", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                s91.setHorizontalAlignment(Element.ALIGN_BASELINE);
                s91.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(!showborder){
                     s91.setBorder(0);
                } else{
                     s91.setBorder(Rectangle.LEFT + Rectangle.TOP + Rectangle.BOTTOM);
                }
                s91.setPadding(5);
                table31.addCell(s91);
                BigDecimal bd = new BigDecimal(String.valueOf(wagetotal));
                bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                double newamount = bd.doubleValue();
                PdfPCell s92 = new PdfPCell(new Paragraph(fontFamilySelector.process(decfm.format(newamount), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                s92.setHorizontalAlignment(Element.ALIGN_BASELINE);
                s92.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(!showborder){
                     s92.setBorder(0);
                } else{
                     s92.setBorder(Rectangle.LEFT + Rectangle.TOP + Rectangle.BOTTOM);
                }
                s92.setPadding(5);
                table31.addCell(s92);
                PdfPCell s94 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.total.deductions", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                s94.setHorizontalAlignment(Element.ALIGN_BASELINE);
                s94.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(!showborder){
                     s94.setBorder(0);
                } else{
                     s94.setBorder(Rectangle.LEFT + Rectangle.TOP + Rectangle.BOTTOM);
                }
                s94.setPadding(5);
                table31.addCell(s94);
                bd = new BigDecimal(String.valueOf(taxtotal));
                bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                newamount = bd.doubleValue();
                PdfPCell s95 = new PdfPCell(new Paragraph(fontFamilySelector.process(decfm.format(newamount), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                s95.setHorizontalAlignment(Element.ALIGN_BASELINE);
                s95.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(!showborder){
                     s95.setBorder(0);
                } else{
                     s95.setBorder(Rectangle.RIGHT + Rectangle.LEFT + Rectangle.TOP + Rectangle.BOTTOM);
                }
                s95.setPadding(5);
                table31.addCell(s95);
                for (int y = 0; y < 2; y++) {
                    PdfPCell he71 = new PdfPCell(new Paragraph(fontFamilySelector.process("  ", FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                    he71.setHorizontalAlignment(Element.ALIGN_LEFT);
                    if(!showborder){
                         he71.setBorder(0);
                    } else{
                         he71.setBorder(Rectangle.LEFT);
                    }
                    table31.addCell(he71);
                    for (i = 0; i < 2; i++) {
                        he71 = new PdfPCell(new Paragraph(fontFamilySelector.process("  ", FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                        he71.setHorizontalAlignment(Element.ALIGN_LEFT);
                        he71.setBorder(0);
                        table31.addCell(he71);
                    }
                    he71 = new PdfPCell(new Paragraph(fontFamilySelector.process("  ", FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                    he71.setHorizontalAlignment(Element.ALIGN_LEFT);
                    if(!showborder){
                         he71.setBorder(0);
                    } else{
                         he71.setBorder(Rectangle.RIGHT);
                    }
                    table31.addCell(he71);
                }

                List<Historydetail> lst4 = exportPayslipDAO.getHistorydetail(historyid, "Employer Contribution");
                double ectotal = 0;
                PdfPCell ec11 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.EmployerContribution", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                ec11.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(!showborder){
                     ec11.setBorder(0);
                } else{
                     ec11.setBorder(Rectangle.LEFT + Rectangle.BOTTOM);
                }
                ec11.setPadding(5);
                table41.addCell(ec11);
                PdfPCell ec12 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.amount.params", new Object[]{currSymbol}, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                ec12.setHorizontalAlignment(Element.ALIGN_RIGHT);
                ec12.setPadding(5);
                if(!showborder){
                     ec12.setBorder(0);
                } else{
                     ec12.setBorder(Rectangle.LEFT + Rectangle.BOTTOM + Rectangle.RIGHT) ;
                }
                table41.addCell(ec12);
                masterDB.Historydetail hd1 =null;
                for (i = 0; i < lst4.size(); i++) {
                    hd1 = (masterDB.Historydetail) lst4.get(i);
                    PdfPCell s24 = new PdfPCell(new Paragraph(fontFamilySelector.process(hd1.getType(), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                    s24.setHorizontalAlignment(Element.ALIGN_LEFT);
                    if(!showborder){
                         s24.setBorder(0);
                    } else{
                         s24.setBorder(Rectangle.LEFT);
                    }
                    s24.setPadding(5);
                    table41.addCell(s24);
                    bd = new BigDecimal(hd1.getAmount());
                    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                    newamount = bd.doubleValue();
                    ectotal = ectotal + newamount;

                    PdfPCell s25 = new PdfPCell(new Paragraph(fontFamilySelector.process(decfm.format(newamount), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                    s25.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    if(!showborder){
                         s25.setBorder(0);
                    } else{
                         s25.setBorder(Rectangle.RIGHT + Rectangle.LEFT);
                    }
                    s25.setPadding(5);
                    table41.addCell(s25);
                }

                PdfPCell se91 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.total.contribution", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                se91.setHorizontalAlignment(Element.ALIGN_BASELINE);
                se91.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(!showborder){
                     se91.setBorder(0);
                } else{
                     se91.setBorder(Rectangle.LEFT + Rectangle.TOP + Rectangle.BOTTOM);
                }
                se91.setPadding(5);
                table41.addCell(se91);
                bd = new BigDecimal(String.valueOf(ectotal));
                bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                newamount = bd.doubleValue();
                PdfPCell se92 = new PdfPCell(new Paragraph(fontFamilySelector.process(decfm.format(newamount), FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN)));
                se92.setHorizontalAlignment(Element.ALIGN_BASELINE);
                se92.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(!showborder){
                     se92.setBorder(0);
                } else{
                     se92.setBorder(Rectangle.LEFT + Rectangle.TOP + Rectangle.BOTTOM + Rectangle.RIGHT);
                }
                se92.setPadding(5);
                table41.addCell(se92);

                PdfPCell Cell31 = new PdfPCell(table31);
                if(!showborder){
                     Cell31.setBorder(0);
                } else{
                     Cell31.setBorder(1);
                }
                table3main.addCell(Cell31);

                PdfPCell mainCell4 = new PdfPCell(table3main);
                if(!showborder){
                     mainCell4.setBorder(0);
                } else{
                     mainCell4.setBorder(1);
                }
                mainTable.addCell(mainCell4);


                PdfPCell Cell41 = new PdfPCell(table41);
                if(!showborder){
                     Cell41.setBorder(0);
                } else{
                     Cell41.setBorder(1);
                }
                table7main.addCell(Cell41);

                PdfPCell mainCell44 = new PdfPCell(table7main);
                if(!showborder){
                     mainCell44.setBorder(0);
                } else{
                     mainCell44.setBorder(1);
                }
                mainTable.addCell(mainCell44);


//***************************************************************************************************************

                PdfPTable table4 = new PdfPTable(2);
                table4.setWidthPercentage(100);
                for (i = 0; i < 4; i++) {
                    PdfPCell i33 = new PdfPCell(new Paragraph(fontFamilySelector.process("  ", FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                    i33.setHorizontalAlignment(Element.ALIGN_CENTER);
                    if(!showborder){
                        i33.setBorder(0);
                    } else{
                        i33.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
                    }
                    table4.addCell(i33);
                }

                
                PdfPCell i31 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.netpay", null, RequestContextUtils.getLocale(request))+" ", FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                i31.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(!showborder){
                    i31.setBorder(0);
                } else{
                    i31.setBorder(Rectangle.LEFT + Rectangle.TOP);
                }
                i31.setPadding(5);
                table4.addCell(i31);
                double netpay = 0;
                netpay = wagetotal - taxtotal;
                netpay = Math.round(netpay);
                String amount = numberFormatter((double)netpay,currSymbol);
                PdfPCell i33 = new PdfPCell(new Paragraph(fontFamilySelector.process(amount, FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                i33.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(!showborder){
                    i33.setBorder(0);
                } else{
                    i31.setBorder(Rectangle.RIGHT);
                }
                i33.setPadding(5);
                table4.addCell(i33);

                PdfPCell i32 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.in.words", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                i32.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(!showborder){
                    i32.setBorder(0);
                } else{
                    i31.setBorder(Rectangle.LEFT);
                }
                i32.setPadding(5);
                table4.addCell(i32);
                EnglishNumberToWords enw = new EnglishNumberToWords(request, messageSource);
                String netinword = enw.convert(netpay);
                PdfPCell i34 = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.only", new Object[]{getCurrencyName(request), netinword}, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                i34.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(!showborder){
                    i34.setBorder(0);
                } else{
                    i31.setBorder(Rectangle.RIGHT);
                }
                i34.setPadding(5);
                table4.addCell(i34);


                PdfPCell mainCell5 = new PdfPCell(table4);
                if(!showborder){
                    mainCell5.setBorder(0);
                } else{
                    mainCell5.setBorder(1);
                }
                mainTable.addCell(mainCell5);

                document.add(new Paragraph("\n\n"));

                PdfPTable table5 = new PdfPTable(2);
                table5.setWidthPercentage(80);
                cell = new PdfPCell(new Paragraph("___________________________"));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(0);
                table5.addCell(cell);
                cell = new PdfPCell(new Paragraph("___________________________"));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(0);
                table5.addCell(cell);
                cell = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.employee.signature", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(0);
                table5.addCell(cell);
                cell = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.payroll.manager.signature", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_TIMES_NEW_ROMAN)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(0);
                table5.addCell(cell);

                document.add(mainTable);
                document.close();
            }

            String filename = "Employee_Payslip.pdf";
            if(!StringUtil.isNullOrEmpty(request.getParameter("reportname"))){
                String temp=request.getParameter("reportname");
                filename=temp.lastIndexOf(".pdf")>=0?temp:temp+".pdf";
            }
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setContentType("application/octet-stream");
            response.setContentLength(baos.size());
            response.getOutputStream().write(baos.toByteArray());
            
            jsonResp.put("valid", true);
            jsonResp.put("data", "");
        } catch (Exception ex) {
        	ex.printStackTrace();
        }finally{
        	try {
				if (response.getOutputStream() != null) {
				    response.getOutputStream().flush();
				    response.getOutputStream().close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
            writer.close();
        }
		return new ModelAndView("jsonView", "model", jsonResp.toString());
	}
	
	
	public ModelAndView printHTML(HttpServletRequest request,HttpServletResponse response) {
		JSONObject jsonResp = new JSONObject();
		try {
			Company company = (Company) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Company", sessionHandlerImplObj.getCompanyid(request));
            
            String[] str=null;
            StringBuilder newtitle=new StringBuilder();
            DecimalFormat decfm=new DecimalFormat("#,##0.00");

            int showborder = 1;
            if(!StringUtil.isNullOrEmpty(request.getParameter("showborder"))){
                boolean border = Boolean.parseBoolean(request.getParameter("showborder"));
                if(!border){
                    showborder = 0;
                }
            }
            
            String userid = request.getParameter("empid");
            
            User userinfo = (User) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User", userid);

            Useraccount ua = (Useraccount) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.Useraccount", userid);

            String uname = StringUtil.getFullName(userinfo);
            
            String empid = getUserCode(ua, company.getCompanyID());
               
            String startDate = request.getParameter("stdate").replace("/", "-");
            String endDate = request.getParameter("enddate").replace("/", "-");
            
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            
            List<Payhistory> list = exportPayslipDAO.getPayhistory(userid, sdf.parse(startDate), sdf.parse(endDate));
            Payhistory payhistory = null;
            if(list!=null && list.size()>0){
            	payhistory = list.get(0);
            }
            
            String department = payhistory.getDepartment();

            String designation = getUserDesignation(ua);

            int days = getWorkingDays(startDate, endDate, company.getCompanyID());
            
            String bankacc = getUserBankAccount(ua);
            Empprofile empprofile=(Empprofile) kwlCommonTablesDAOObj.getObject("com.krawler.hrms.ess.Empprofile", userid);
            
            String epf = getUserEPF(empprofile);

            String dateofjoin = getUserDateOFJoining(empprofile);

            String currSymbol=getCurrencySymbol(request, true);
                
            String domainName = com.krawler.common.util.URLUtil.getDomainName(request);
            
            String imageSourceURL="http://apps.deskera.com";
            
            if(StringUtil.isStandAlone()){
                imageSourceURL= com.krawler.common.util.URLUtil.getPageURL(request, "");
            }
                
            String title=company.getCompanyName();
            
            String companyAddressHtml =getCompanyAddressHtml(company);

            //To modify the Heading of document to be printed ..String 'newtitle' is manipulated.
            String name = request.getParameter("reportname");
            String maintitle=name;
            
            str=maintitle.split("(?=\\p{Upper})");
            if(!StringUtil.isNullOrEmpty(title)){
            	newtitle.append(title);
            }else{
            	for(int i=0;i<str.length;i++){
                	newtitle.append(str[i]+" ");
            	}
            }
            newtitle.trimToSize();
            
            String ashtmlString = "<html> " +
                    "<head>" +
                    "<title></title>" +
                    "<style type=\"text/css\">@media print {button#print {display: none;} .payslipFooter { position:fixed !important; bottom:5px !important;}}</style>" +
                    "<style type=\"text/css\">@media screen {.payslipFooter { display:none;}}</style>" +
                    "</head>" +
                    "<body style = \"font-family: Tahoma, Verdana, Arial, Helvetica, sans-sarif;\">" +
                    "<center><div style='padding-bottom: 5px; padding-right: 5px;'>" +
                    "<h3>  </h3>" +
                    "</div></center>";

          
            ashtmlString+="<center>";
            
            ashtmlString += "<table cellspacing=0 border=1 width='100%' style='margin-bottom:30px'>";
            ashtmlString +="<tr><td>";
            
                ashtmlString +="<table cellspacing=0 border=0 cellpadding=2 width='100%' >";
                ashtmlString +="<tr>";
                ashtmlString +="<th width='200' height='50'><font size='4'  ><img src="+imageSourceURL+"/b/"+domainName+"/images/store/?company=true></font></th>";
                ashtmlString +="<th style='text-align:left;' height='50' >"+companyAddressHtml+"</th>";
                ashtmlString +="</tr><br>";
                ashtmlString +="<tr>";
                ashtmlString +="<th height='50'></th>";
                ashtmlString +="<th style='text-align:left;' height='50'><font size='1'  >"+messageSource.getMessage("hrms.payroll.payslip.period.params", new Object[]{request.getParameter("stdate"), request.getParameter("enddate")}, RequestContextUtils.getLocale(request)) +"</font></th>";
                ashtmlString +="</tr>";
                ashtmlString +="</table>";
            
            ashtmlString +="</td></tr>";

            ashtmlString +="<tr><td>";

                ashtmlString +="<table cellspacing=0 border="+showborder+" cellpadding=5 width='100%' >";
                ashtmlString +="<tr>";
                ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.common.employee.code", null, RequestContextUtils.getLocale(request))+"</b></font></td><td><font size='2'  >"+empid+"</font></td>";
                ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.common.employee.name", null, RequestContextUtils.getLocale(request))+"</b></font></td><td><font size='2'  >"+ uname+"</font></td>";
                ashtmlString +="</tr>";

                ashtmlString +="<tr>";
                ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.common.department", null, RequestContextUtils.getLocale(request))+"</b></font></td><td><font size='2'  >"+department+"</font></td>";
                ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.common.designation", null, RequestContextUtils.getLocale(request))+"</b></font></td><td><font size='2'  >"+designation+"</font></td>";
                ashtmlString +="</tr>";
                
                ashtmlString +="<tr>";
                ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.common.bank.ac.no", null, RequestContextUtils.getLocale(request))+"</b></font></td><td><font size='2'  >"+bankacc+"</font></td>";
                ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.common.working.days", null, RequestContextUtils.getLocale(request))+"</b></font></td><td><font size='2'  >"+days+"</font></td>";
                ashtmlString +="</tr>";

                ashtmlString +="<tr>";
                ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.common.e.p.f", null, RequestContextUtils.getLocale(request))+"</b></font></td><td><font size='2'  >"+epf+"</font></td>";
                ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.common.DateofJoining", null, RequestContextUtils.getLocale(request))+"</b></font></td><td><font size='2'  >"+dateofjoin+"</font></td>";
                ashtmlString +="</tr>";

                ashtmlString += "</table>";
                ashtmlString += "<br>";

            ashtmlString +="</td></tr>";

            ashtmlString +="<tr><td>";
            
            ashtmlString +="<table cellspacing=0 border="+showborder+" cellpadding=5 width='100%' >";

                ashtmlString +="<tr>";
                ashtmlString +="<th align='left'><font size='2'  >"+messageSource.getMessage("hrms.common.Earnings", null, RequestContextUtils.getLocale(request))+"</font></th>";
                ashtmlString +="<th align='right'><font size='2'  >"+messageSource.getMessage("hrms.payroll.amount.params", new Object[]{currSymbol}, RequestContextUtils.getLocale(request))+"</font></th>";
                ashtmlString +="<th align='left'><font size='2'  >"+messageSource.getMessage("hrms.payroll.Deduction", null, RequestContextUtils.getLocale(request))+"</font></th>";
                ashtmlString +="<th align='right'><font size='2'  >"+messageSource.getMessage("hrms.payroll.amount.params", new Object[]{currSymbol}, RequestContextUtils.getLocale(request))+"</font></th>";
                ashtmlString +="</tr>";
                
// Earnings and Deduction
                int Wcount = 0;
                float taxtotal = 0;
                String historyid = "";
                historyid = payhistory.getHistoryid();
                double unpaidleaves = payhistory.getUnpaidleaves();

                List<Historydetail> lst1 = exportPayslipDAO.getHistorydetail(historyid, "Wages", "Basic");
                List<Historydetail> list1 = exportPayslipDAO.getHistorydetailNotType(historyid, "Wages", "Basic");
                if(list!=null){
                	lst1.addAll(list1);
                }

                List<Historydetail> lst2 = exportPayslipDAO.getHistorydetail(historyid, "Taxes");
 
                CompanyPreferences cp = hrmsCommonDAOObj.getCompanyPreferences(company.getCompanyID());
                int financialMonth = cp.getFinancialmonth();

                Date d3 = null;
                d3 = sdf.parse(endDate);
                Calendar c1 = Calendar.getInstance();
                c1.setTime(d3);
                c1.set(Calendar.MONTH, financialMonth);
                c1.set(Calendar.DATE, 1);
                Date d = c1.getTime();

                Date DOJ = getUserDateOFJoiningInMilliSeconds(empprofile);

                if(d3.before(d)){
                    int currentYear = c1.get(Calendar.YEAR) - 1;
                    c1.set(Calendar.YEAR, currentYear);
                    d = c1.getTime();
                }
                if(DOJ.after(d)){
                    c1.setTime(DOJ);
                    c1.set(Calendar.DATE, 1);
                    d = c1.getTime();
                }
                
                List<Historydetail> lst3 = exportPayslipDAO.getHistorydetail(historyid, "Deduction");
                int size= lst1.size()>(lst2.size()+lst3.size())?lst1.size():lst2.size()+lst3.size();
                float wagetotal = 0;
                double totalYTDEarning = 0, totalYTDDeduction = 0;
                int i = 0;
                String earningHtml="";
                String earningHtmlAmount="";
                String deductionHtml="";
                String deductionHtmlAmount="";


                   for (i = 0; i < size; i++) {
                   masterDB.Historydetail hd =null;
                   if(i<lst1.size()){
                        hd = (masterDB.Historydetail) lst1.get(i);
                        
                        double newamount = getDoubleRoundUpValue(hd.getAmount());

                        earningHtml +="<tr><td><font size='2'  >"+hd.getType()+"</font></td></tr>";
                        earningHtmlAmount +="<tr><td style='text-align:right;'><font size='2'  >"+String.valueOf(decfm.format(newamount))+"</font></td></tr>";
                        

                        List<Historydetail> lstw = exportPayslipDAO.getHistorydetail(request.getParameter("empid"), d, d3, hd.getType());
                        double wageAmt = 0;
                        masterDB.Historydetail hd1 =null;
                        for (int y = 0; y < lstw.size(); y++) {
                            hd1 = (masterDB.Historydetail) lstw.get(y);
                            wageAmt += Double.parseDouble(hd1.getAmount());
                        }

                        wagetotal = wagetotal + Float.parseFloat(hd.getAmount());
                        totalYTDEarning = totalYTDEarning + wageAmt;
                        Wcount++;
                   }else{
                      for(int j=0;j<2;j++){
                        earningHtml +="<tr><td><font size='2'  ></font></td></tr>";
                        earningHtmlAmount +="<tr><td style='text-align:right;'><font size='2'  ></font></td></tr>";
                        
                      }
                   }
                   if(i<lst2.size()){
                        hd = (masterDB.Historydetail) lst2.get(i);

                        double newamount = getDoubleRoundUpValue(hd.getAmount());

                        deductionHtml +="<tr><td><font size='2'  >"+hd.getType()+"</font></td></tr>";
                        deductionHtmlAmount +="<tr><td style='text-align:right;'><font size='2'  >"+String.valueOf(decfm.format(newamount))+"</font></td></tr>";
                     
                        List<Historydetail> lstw1 = exportPayslipDAO.getHistorydetail(request.getParameter("empid"), d, d3, hd.getType());
                        double taxAmt = 0;
                        masterDB.Historydetail hd2 =null;
                        for (int y = 0; y < lstw1.size(); y++) {
                            hd2 = (masterDB.Historydetail) lstw1.get(y);
                            taxAmt += Double.parseDouble(hd2.getAmount());
                        }

                        taxtotal = taxtotal + Float.parseFloat(hd.getAmount());
                        totalYTDDeduction = totalYTDDeduction + taxAmt;
                   }

                    else {
                      if(i-lst2.size() < lst3.size()){
                        hd = (masterDB.Historydetail) lst3.get(i-lst2.size());

                        if(hd.getType().equals("Unpaid_leaves")){

                            deductionHtml +="<tr><td><font size='2'  >"+hd.getType()+"("+unpaidleaves+")"+"</font></td></tr>";
                        }else {
                            deductionHtml +="<tr><td><font size='2'  >"+hd.getType()+"</font></td></tr>";
                        }
                        double newamount = getDoubleRoundUpValue(hd.getAmount());
                        deductionHtmlAmount +="<tr><td style='text-align:right;'><font size='2'  >"+String.valueOf(decfm.format(newamount))+"</font></td></tr>";
                     

                        List<Historydetail> lstw2 = exportPayslipDAO.getHistorydetail(request.getParameter("empid"), d, d3, hd.getType());
                        masterDB.Historydetail hd3 =null;
                        double deductAmt = 0;
                        for (int y = 0; y < lstw2.size(); y++) {
                            hd3 = (masterDB.Historydetail) lstw2.get(y);
                            deductAmt += Double.parseDouble(hd3.getAmount());
                        }

                        taxtotal = taxtotal + Float.parseFloat(hd.getAmount());
                        totalYTDDeduction = totalYTDDeduction + deductAmt;
                      }else{
                            for(int j=0;j<2;j++){
                                deductionHtml +="<tr><td><font size='2'  ></font></td></tr>";
                                deductionHtmlAmount +="<tr><td style='text-align:right;'><font size='2'  ></font></td></tr>";

                             }
                      }
                   }
                 }

/////////////////////////


                ashtmlString +="<td>";
                ashtmlString +="<table cellspacing=0 border=0 cellpadding=5 width='100%' >";
                ashtmlString +=earningHtml;
                ashtmlString +="</table>";
                ashtmlString +="</td>";

                ashtmlString +="<td>";
                ashtmlString +="<table cellspacing=0 border=0 cellpadding=5 width='100%' >";
                ashtmlString +=earningHtmlAmount;
                ashtmlString +="</table>";
                ashtmlString +="</td>";

                ashtmlString +="<td>";
                ashtmlString +="<table cellspacing=0 border=0 cellpadding=5 width='100%' >";
                ashtmlString +=deductionHtml;
                ashtmlString +="</table>";
                ashtmlString +="</td>";

                ashtmlString +="<td>";
                ashtmlString +="<table cellspacing=0 border=0 cellpadding=5 width='100%' >";
                ashtmlString +=deductionHtmlAmount;
                ashtmlString +="</table>";
                ashtmlString +="</td>";
                
              
                double totalEarnings = getDoubleRoundUpValue(String.valueOf(wagetotal));

                double totalDesductions = getDoubleRoundUpValue(String.valueOf(taxtotal));
                
                ashtmlString +="<tr>";
                ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.payroll.total.earnings", null, RequestContextUtils.getLocale(request))+"</b></font></td><td style='text-align:right;'><font size='2'  >"+decfm.format(totalEarnings)+"</font></td>";
                ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.payroll.total.deductions", null, RequestContextUtils.getLocale(request))+"</b></font></td><td style='text-align:right;'><font size='2'  >"+decfm.format(totalDesductions)+"</font></td>";
                ashtmlString +="</tr>";

                ashtmlString += "</table>";
                ashtmlString += "<br>";

            ashtmlString +="</td></tr>";

// Employer Contribution
            double ectotal = 0;
            String employerHtml="";

            List<Historydetail> lst4 = exportPayslipDAO.getHistorydetail(historyid, "Employer Contribution");

            masterDB.Historydetail hd1 =null;
            for (i = 0; i < lst4.size(); i++) {
                    hd1 = (masterDB.Historydetail) lst4.get(i);

                    double emprContri = getDoubleRoundUpValue(hd1.getAmount());

                    ectotal = ectotal + emprContri;
                    
                    employerHtml+="<tr><td><font size='2'  >"+hd1.getType()+"</font></td><td style='text-align:right;'><font size='2'  >"+decfm.format(emprContri)+"</font></td></tr>";
                    
            }

           ashtmlString +="<tr><td>";

                ashtmlString +="<table cellspacing=0 border="+showborder+" cellpadding=5 width='100%' >";

                ashtmlString +="<tr>";
                ashtmlString +="<th align='left'><font size='2'  >"+messageSource.getMessage("hrms.payroll.EmployerContribution", null, RequestContextUtils.getLocale(request))+"</font></th>";
                ashtmlString +="<th align='right'><font size='2'  >"+messageSource.getMessage("hrms.payroll.amount.params", new Object[]{currSymbol}, RequestContextUtils.getLocale(request))+"</font></th>";
                ashtmlString +="</tr>";


                ashtmlString +=employerHtml;

                ectotal = getDoubleRoundUpValue(String.valueOf(ectotal));

                ashtmlString +="<tr>";
                ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.payroll.total.contribution", null, RequestContextUtils.getLocale(request))+"</b></font></td>";
                ashtmlString +="<td style='text-align:right;'><font size='2'  >"+ectotal+"</font></td>";
                ashtmlString +="</tr>";

                ashtmlString += "</table>";
                
            ashtmlString +="</td></tr>";
//// Net Pay
            ashtmlString +="<tr><td>";

                ashtmlString +="<table cellspacing=0 border="+showborder+" cellpadding=5 width='100%' >";

                double netpay = 0;
                netpay = wagetotal - taxtotal;
                netpay = Math.round(netpay);
                String amount = numberFormatter((double)netpay,currSymbol);

                ashtmlString +="<br>";
                ashtmlString +="<tr>";
                ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.payroll.netpay", null, RequestContextUtils.getLocale(request))+"</b></font></td>";
                ashtmlString +="<td style='text-align:right;'><font size='2'  ><b>"+amount+"</b></font></td>";
                ashtmlString +="</tr>";

                EnglishNumberToWords enw = new EnglishNumberToWords(request, messageSource);
                String netinword = enw.convert(netpay);
                
                String currname = getCurrencyName(request);


                ashtmlString +="<tr>";
                ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.payroll.in.words", null, RequestContextUtils.getLocale(request))+"</b></font></td>";
                if(showborder==0){
                    ashtmlString +="<td align='right'><font size='2'  ><b>"+messageSource.getMessage("hrms.payroll.only", new Object[]{currname, netinword}, RequestContextUtils.getLocale(request))+"</b></font></td>";
                }else{
                    ashtmlString +="<td><font size='2'  ><b>"+messageSource.getMessage("hrms.payroll.only", new Object[]{currname, netinword}, RequestContextUtils.getLocale(request))+"</b></font></td>";
                }
                
                ashtmlString +="</tr>";

                ashtmlString += "</table>";
            ashtmlString +="</td></tr>";

            ashtmlString += "</table>";
            ashtmlString += "<br>";
                
            
            ashtmlString += "</center>";
            
            ashtmlString +="<div style='float: left; padding-top: 3px; padding-right: 5px;'>" +
                                    "<button id = 'print' title='Print Payslip' onclick='window.print();' style='color: rgb(8, 55, 114);' href='#'>"+messageSource.getMessage("hrms.common.print", null, RequestContextUtils.getLocale(request))+"</button>" +
                                "</div>" ;
            ashtmlString +="<div class='payslipFooter'><font size='2'  >"+messageSource.getMessage("hrms.payroll.computer.generated.payslip.not.require.signature", null, RequestContextUtils.getLocale(request))+"</font></div>";
            ashtmlString +="</body>" +
            "</html>";
            
            response.getOutputStream().write(ashtmlString.getBytes());
            response.getOutputStream().flush();
        
			jsonResp.put("valid", true);
            jsonResp.put("data", "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
            try {
				if (response.getOutputStream() != null) {
				    response.getOutputStream().flush();
				    response.getOutputStream().close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
       }
		return new ModelAndView("jsonView", "model", jsonResp.toString());
	}
	
	public Date getUserDateOFJoiningInMilliSeconds(Empprofile empprofile) {
        
        Date DOJ = new java.util.Date(empprofile.getJoindate().getTime());
        
        return DOJ;
    }
	
	public double getDoubleRoundUpValue(String amount) {

        BigDecimal bd = new BigDecimal(amount);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        double amnt = bd.doubleValue();

        return amnt;
        
    }

	public String getCompanyAddressHtml(Company company){
        
        String companyAddressHtml ="<font size='4'  >"+company.getCompanyName()+"</font>";
        if(!StringUtil.isNullOrEmpty(company.getCity())){
            companyAddressHtml +="<font size='2'  ><br>"+company.getCity()+"</font>";

        }
        if(!StringUtil.isNullOrEmpty(company.getAddress())){
            companyAddressHtml +="<font size='2'  ><br>"+company.getAddress()+"</font>";

        }
        if(!StringUtil.isNullOrEmpty(company.getState())){
            companyAddressHtml +="<font size='2'  ><br>"+company.getState()+"</font>";

        }
        if(!StringUtil.isNullOrEmpty(company.getZipCode())){
            companyAddressHtml +="<font size='2'  ><br>"+company.getZipCode()+"</font>";

        }
        return companyAddressHtml;
	}
	
	public String getUserBankAccount(Useraccount ua) {
        String userbankAc =ua.getAccno() != null ? ua.getAccno() : "";
        return userbankAc;
    }
	
	public int getWorkingDays(String startDate, String endDate, String companyid) throws ParseException {
       int days=0;
       Date d2 = null;
       Date d3 = null;
       Calendar cal1 = Calendar.getInstance();
       Calendar cal2 = Calendar.getInstance();
       
       SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
       d2 = sdf.parse(startDate);
       d3 = sdf.parse(endDate);
       cal1.setTime(d3);
       cal2.setTime(d2);
       days=(int)((cal1.getTimeInMillis()-cal2.getTimeInMillis())/(24.0 * 60 * 60 * 1000));
       days=days+1;
       List<Integer> holidayList = getWeeklyOffDays(companyid);
       
       int count=0;
       for(int i=0; i<days; i++){
          int day_of_week = cal2.get(Calendar.DAY_OF_WEEK);
          if(holidayList.contains(day_of_week)){
            count++;
          }
          cal2.add(Calendar.DATE, 1);
       }
       days=days-count;
       return days;
	}
	
	public List<Integer> getWeeklyOffDays(String companyid) {
		CompanyPreferences cp = hrmsCommonDAOObj.getCompanyPreferences(companyid);
        List<Integer> list = new ArrayList<Integer>();
        if(cp!=null){
        	int code = cp.getHolidaycode();
            for(int i=1; i<=7; i++){
                if(code%2==1){
                   list.add(i);
                }
                code=code/2;
            }
        }
        return list;
    }
	
	
	public String getCurrencySymbol(HttpServletRequest request, boolean print) throws SessionExpiredException {
        String symbol="";
        KWLCurrency currency = null;
        try{
        	String currencyID = sessionHandlerImplObj.getCurrencyID(request);
        	currency = (KWLCurrency) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.KWLCurrency", currencyID);
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
	
	public String getCurrencyName(HttpServletRequest request){
		String currencyID;
		String currname="";
		try {
			currencyID = sessionHandlerImplObj.getCurrencyID(request);
			KWLCurrency currency = (KWLCurrency) kwlCommonTablesDAOObj.getObject("com.krawler.common.admin.KWLCurrency", currencyID);
	        if(currency!=null){
	        	currname = currency.getName();
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return currname;
    }
	
	public String getUserCode(Useraccount useraccount, String companyid){
		String code = null;
		try{
			HashMap<String, Object> requestParams = new HashMap<String, Object>();
			requestParams.put("empid", useraccount.getEmployeeid());
			requestParams.put("companyid", companyid);
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
	
	public String getUserDesignation(Useraccount ua) {
        String userdesig =ua.getDesignationid()!= null ? ua.getDesignationid().getValue() : "";
        return userdesig;
    }
	
	public String numberFormatter(double values,String compSymbol) {
        NumberFormat numberFormatter;
        java.util.Locale currentLocale = java.util.Locale.US;
        numberFormatter = NumberFormat.getNumberInstance(currentLocale);
        numberFormatter.setMinimumFractionDigits(2);
        numberFormatter.setMaximumFractionDigits(2);
        String amount = compSymbol + " " + numberFormatter.format(values);
        return amount;
    }
	
	public String getUserEPF(Empprofile empprof) {
        String epf="";
        try{
        	epf=StringUtil.isNullOrEmpty(empprof.getPfno())?"":empprof.getPfno();
        }catch(Exception ex){
           epf="";
        }   
        return epf;
    }
	
	public String getUserDateOFJoining(Empprofile empprof) {
        String dateofjoin="";
        try{
           dateofjoin=empprof.getJoindate()!=null?empprof.getJoindate().toString():"";
        }catch(Exception ex){
           dateofjoin="";
        }
        return dateofjoin;
    }
}


class EnglishNumberToWords{
	
	HttpServletRequest request;
	MessageSource messageSource;
	
	public EnglishNumberToWords(HttpServletRequest request, MessageSource messageSource){
		this.request = request;
		this.messageSource = messageSource;
	}
	
    private String convertLessThanOneThousand(int number) {
    	final String[] tensNames = {
    	        "", " "+messageSource.getMessage("hrms.payroll.ten", null, RequestContextUtils.getLocale(request)), 
    	            " "+messageSource.getMessage("hrms.payroll.twenty", null, RequestContextUtils.getLocale(request)),
    	            " "+messageSource.getMessage("hrms.payroll.thirty", null, RequestContextUtils.getLocale(request)),
    	            " "+messageSource.getMessage("hrms.payroll.forty", null, RequestContextUtils.getLocale(request)),
    	            " "+messageSource.getMessage("hrms.payroll.fifty", null, RequestContextUtils.getLocale(request)),
    	            " "+messageSource.getMessage("hrms.payroll.sixty", null, RequestContextUtils.getLocale(request)),
    	            " "+messageSource.getMessage("hrms.payroll.seventy", null, RequestContextUtils.getLocale(request)),
    	            " "+messageSource.getMessage("hrms.payroll.eighty", null, RequestContextUtils.getLocale(request)),
    	            " "+messageSource.getMessage("hrms.payroll.ninety", null, RequestContextUtils.getLocale(request))
    	    };
    	final String[] numNames = {
    	        "",
    	        " "+messageSource.getMessage("hrms.payroll.one", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.two", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.three", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.four", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.five", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.six", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.seven", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.eight", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.nine", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.ten", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.eleven", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.twelve", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.thirteen", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.fourteen", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.fifteen", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.sixteen", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.seventeen", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.eighteen", null, RequestContextUtils.getLocale(request)),
    	        " "+messageSource.getMessage("hrms.payroll.nineteen", null, RequestContextUtils.getLocale(request))
    	    };
    	
        String soFar;
        if (number % 100 < 20) {
            soFar = numNames[number % 100];
            number /= 100;
        } else {
            soFar = numNames[number % 10];
            number /= 10;
            soFar = tensNames[number % 10] + soFar;
            number /= 10;
        }
        if (number == 0) {
            return soFar;
        }
        return messageSource.getMessage("hrms.payroll.hundred", new Object[]{numNames[number], soFar}, RequestContextUtils.getLocale(request));
    }

    public String convert(double number) {
        if (number == 0) {
            return "zero";
        }
        String snumber = Double.toString(number);
        String mask = "000000000000";
        DecimalFormat df = new DecimalFormat(mask);
        snumber = df.format(number);
        int billions = Integer.parseInt(snumber.substring(0, 3));
        int millions = Integer.parseInt(snumber.substring(3, 6));
        int hundredThousands = Integer.parseInt(snumber.substring(6, 9));
        int thousands = Integer.parseInt(snumber.substring(9, 12));
        String tradBillions;
        switch (billions) {
            case 0:
                tradBillions = "";
                break;
            case 1:
                tradBillions = convertLessThanOneThousand(billions) + " "+messageSource.getMessage("hrms.payroll.billion", null, RequestContextUtils.getLocale(request))+" ";
                break;
            default:
                tradBillions = convertLessThanOneThousand(billions) + " "+messageSource.getMessage("hrms.payroll.billion", null, RequestContextUtils.getLocale(request))+" ";
        }
        String result = tradBillions;

        String tradMillions;
        switch (millions) {
            case 0:
                tradMillions = "";
                break;
            case 1:
                tradMillions = convertLessThanOneThousand(millions) + " "+messageSource.getMessage("hrms.payroll.million", null, RequestContextUtils.getLocale(request))+" ";
                break;
            default:
                tradMillions = convertLessThanOneThousand(millions) + " "+messageSource.getMessage("hrms.payroll.million", null, RequestContextUtils.getLocale(request))+" ";
        }
        result = result + tradMillions;

        String tradHundredThousands;
        switch (hundredThousands) {
            case 0:
                tradHundredThousands = "";
                break;
            case 1:
                tradHundredThousands = messageSource.getMessage("hrms.payroll.one.thousand", null, RequestContextUtils.getLocale(request))+" ";
                break;
            default:
                tradHundredThousands = convertLessThanOneThousand(hundredThousands) + " "+messageSource.getMessage("hrms.payroll.thousand", null, RequestContextUtils.getLocale(request))+" ";
        }
        result = result + tradHundredThousands;
        String tradThousand;
        tradThousand = convertLessThanOneThousand(thousands);
        result = result + tradThousand;
        return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
    }
}
