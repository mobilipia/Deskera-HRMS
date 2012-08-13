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
package com.krawler.esp.servlets;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.CompanyPreferences;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.User;
import com.krawler.common.admin.Useraccount;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.KrawlerLog;
import com.krawler.common.util.StringUtil;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.hrmsManager;
import com.krawler.hrms.ess.Empprofile;
import com.krawler.utils.json.base.JSONException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.TimeZone;
import javax.servlet.ServletContext;
import masterDB.Payhistory;

public class ExportLeavePdfServlet extends HttpServlet implements MessageSourceAware{
		
	private MessageSource messageSource;
	
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
                PdfPCell footerPageNocell = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.computer.generated.payslip.not.require.signature", null, RequestContextUtils.getLocale(request)), fontMediumRegular));
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
    private static final long serialVersionUID = -763555229410947890L;
    private static Font fontSmallRegular = FontFactory.getFont("Times New Roman", 8, Font.NORMAL, Color.BLACK);    
    private static Font fontSmallBold = FontFactory.getFont("Times New Roman", 8, Font.BOLD, Color.BLACK);
    private static Font fontMediumBold = FontFactory.getFont("Times New Roman", 12, Font.BOLD, Color.BLACK);    
    private static Font fontMediumRegular = FontFactory.getFont("Times New Roman", 10, Font.NORMAL, Color.BLACK);
    private static Font fontTblMediumBold = FontFactory.getFont("Times New Roman", 8, Font.BOLD, Color.BLACK);  
    private static Font fontTblLargeBold = FontFactory.getFont("Times New Roman", 14, Font.BOLD, Color.BLACK);
    private static String imgPath = "";
    private DateFormat dFmt = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat tFmt = new SimpleDateFormat("HH:mm");
    private static String errorMsg = "";
    private String tdiff;
    
  
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        Session session = HibernateUtil.getCurrentSession();
        try {
            String filename = "Employee_Payslip.pdf";
            String cname = AuthHandler.getCompanyName(request);
            if(!StringUtil.isNullOrEmpty(request.getParameter("reportname"))){
                String temp=request.getParameter("reportname");
                filename=temp.lastIndexOf(".pdf")>=0?temp:temp+".pdf";
            }
            ByteArrayOutputStream baos = null;
            if(request.getParameter("exporttype") != null && request.getParameter("exporttype").equals("print")) {
                List list = checkForPayHistory(request, session);
                if(list.size()>0){
                    Payhistory wage = (Payhistory) list.get(0);
                    createPrinPriviewFile(request, response,session,wage);
                }
                
                
            } else {
                int flag = 1;

                switch (flag) {
                    case 1:
                        baos = createLeaveForm(cname, request, session, true);
                        if (baos != null) {
                            writeDataToFile(filename, baos, response);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    public static List checkForPayHistory(HttpServletRequest request, Session session) throws ParseException, Exception  {
        Query query = null;
        Date d2 = null;
        Date d3 = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

        String stDate = request.getParameter("stdate").replace("/", "-");
        String endDate = request.getParameter("enddate").replace("/", "-");
        d2 = sdf.parse(stDate);
        d3 = sdf.parse(endDate);
        query = session.createQuery("from masterDB.Payhistory t where t.userID.userID=:empid and t.paycyclestart=:paycyclestart and t.paycycleend=:paycycleend");
        query.setString("empid", request.getParameter("empid"));
        query.setDate("paycyclestart", d2);
        query.setDate("paycycleend", d3);

        List lst = (List) query.list();
        if (lst.size() == 0) {
            throw new Exception("Exception occured");
        }

        return lst;
    }
    
    public static User getUserObject(Session session, String userid) {
        Query query = null;
        query = session.createQuery("from com.krawler.common.admin.User as t where t.userID=:empid ");
        query.setString("empid", userid);
        List lst = (List) query.list();
        
        return (com.krawler.common.admin.User) lst.get(0);
    }
    public static Useraccount getUserAccount(Session session,String userid) {
        return (Useraccount) session.get(Useraccount.class, userid);
    }

    public static String getUserName(User user) {
        String username = user.getFirstName()!=null ? user.getFirstName():"";
        username+=user.getLastName() != null ? " "+user.getLastName() : "";
        return username;
    }

    public static String getEmployeeID(HttpServletRequest request, Session session,Useraccount ua) throws ServiceException {
        String empid = "";
        if(ua.getEmployeeIdFormat()==null){
            empid =  ua.getEmployeeid() == null ? "" : hrmsManager.getEmpidFormatEdit(session, request, ua.getEmployeeid());
        }else{
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("standardEmpId", hrmsManager.getEmpidFormatEdit(session, request, ua.getEmployeeid()));
            requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
            empid = hrmsManager.getNewEmployeeIdFormat(requestParams);
        }
        return empid;
    }

    public static String getUserDespartment(Useraccount ua) {
        String userdeptt =ua.getDepartment() != null ? ua.getDepartment().getValue() : "";
        return userdeptt;
    }

    public static String getUserDesignation(Useraccount ua) {
        String userdesig =ua.getDesignationid()!= null ? ua.getDesignationid().getValue() : "";
        return userdesig;
    }

    public static String getUserBankAccount(Useraccount ua) {
        String userbankAc =ua.getAccno() != null ? ua.getAccno() : "";
        return userbankAc;
    }

    public static int getWorkingDays(HttpServletRequest request, Session session, String companyid) throws ParseException {
       int days=0;

       Date d2 = null;
       Date d3 = null;
       Calendar cal1 = Calendar.getInstance();
       Calendar cal2 = Calendar.getInstance();
       String month = "";

       SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
       String stDate = request.getParameter("stdate").replace("/", "-");
       String endDate = request.getParameter("enddate").replace("/", "-");
       d2 = sdf.parse(stDate);
       d3 = sdf.parse(endDate);
       DateFormat fo = new SimpleDateFormat("MMMM");
       month = fo.format(d3);
       cal1.setTime(d3);
       cal2.setTime(d2);
       days=(int)((cal1.getTimeInMillis()-cal2.getTimeInMillis())/(24.0 * 60 * 60 * 1000));
       days=days+1;
       List holidayList = getWeeklyOffDays(session, companyid);
       
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

    public static String getUserEPF(Session session, String userid) {
        String epf="";
        Empprofile empprof=null;
        try{
           empprof=(Empprofile) session.get(Empprofile.class,userid);
           epf=StringUtil.isNullOrEmpty(empprof.getPfno())?"":empprof.getPfno();
        }catch(Exception ex){
           epf="";
        }   
        return epf;
    }

    public static String getUserDateOFJoining(Session session, String userid) {
        String dateofjoin="";
        Empprofile empprof=null;
        try{
           empprof=(Empprofile) session.get(Empprofile.class,userid);
           dateofjoin=empprof.getJoindate()!=null?empprof.getJoindate().toString():"";
        }catch(Exception ex){
           dateofjoin="";
        }
        return dateofjoin;
    }

    public static Date getUserDateOFJoiningInMilliSeconds(Session session, String userid) {
        
        Empprofile empprof=(Empprofile) session.get(Empprofile.class,userid);
                
        Date DOJ = new java.util.Date(empprof.getJoindate().getTime());
        
        return DOJ;
    }

    public static String getCurrencySymbol(HttpServletRequest request, Session session) throws SessionExpiredException {
        String currSymbol="";
        String curid=AuthHandler.getCurrencyID(request);
        Query queryx = session.createQuery("from KWLCurrency where currencyID=:curid");
        queryx.setString("curid",curid);
        KWLCurrency curncy = (KWLCurrency) queryx.uniqueResult();
        String currsym=curncy.getHtmlcode();
        String currname=curncy.getName();
        try{
            char a1= (char) Integer.parseInt(currsym,16);
            currSymbol = Character.toString(a1);
        } catch (Exception e){
            currSymbol = currsym;
        }
        return currSymbol;
    }

    public static String getCurrencyName(HttpServletRequest request, Session session) throws SessionExpiredException {

        String curid=AuthHandler.getCurrencyID(request);
        Query queryx = session.createQuery("from KWLCurrency where currencyID=:curid");
        queryx.setString("curid",curid);
        KWLCurrency curncy = (KWLCurrency) queryx.uniqueResult();
        
        String currname=curncy.getName();

        return currname;
    }

    public static String getCompanyLogo(HttpServletRequest request, String companyid) throws SessionExpiredException {
        String logo="";
        try {
            String src = StorageHandler.GetProfileImgStorePath()+companyid+".png";
            logo="<img src="+src+">";
            
        } catch (Exception e) {
            logo=AuthHandler.getCompanyName(request);
            
        }

        return logo;
    }

    public static List getEmployerContribution(Session session, String historyid) {

        Query query4 = session.createQuery("from masterDB.Historydetail t where t.payhistory.historyid=:historyid and t.name=:name order by t.amount");
        query4.setString("historyid", historyid);
        query4.setString("name", "Employer Contribution");
        List lst4 = (List) query4.list();

        return lst4;
    }

    public static List getWeeklyOffDays(Session session, String companyid) {

        Query query = session.createQuery("select holidaycode from CompanyPreferences where  company.companyID=:companyid");
        query.setString("companyid", companyid);
        List lst = (List) query.list();
        if(lst != null){
            int code = Integer.parseInt(lst.get(0).toString());
            lst.clear();
            for(int i=1; i<=7; i++){
                if(code%2==1){
                   lst.add(i);
                }
                code=code/2;
            }
        }

        return lst;
    }

    public static double getDoubleRoundUpValue(String amount) {

        BigDecimal bd = new BigDecimal(amount);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        double amnt = bd.doubleValue();

        return amnt;
        
    }

    public static String getCompanyAddressHtml(HttpServletRequest request, Session session, String companyName) throws SessionExpiredException {
            Company com = (Company) session.get(Company.class, AuthHandler.getCompanyid(request));
           
            String companyAddressHtml ="<font size='4'  >"+companyName+"</font>";
            if(!StringUtil.isNullOrEmpty(com.getCity())){
                companyAddressHtml +="<font size='2'  ><br>"+com.getCity()+"</font>";

            }
            if(!StringUtil.isNullOrEmpty(com.getAddress())){
                companyAddressHtml +="<font size='2'  ><br>"+com.getAddress()+"</font>";

            }
            if(!StringUtil.isNullOrEmpty(com.getState())){
                companyAddressHtml +="<font size='2'  ><br>"+com.getState()+"</font>";

            }
            if(!StringUtil.isNullOrEmpty(com.getZipCode())){
                companyAddressHtml +="<font size='2'  ><br>"+com.getZipCode()+"</font>";

            }
            return companyAddressHtml;
    }

    public void createPrinPriviewFile(HttpServletRequest request, HttpServletResponse response, Session session, Payhistory wage) throws IOException  {

        try {
            
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
            String cmpid=AuthHandler.getCompanyid(request);
            
            String startdate=formatDate(System.currentTimeMillis(),true,false);
 
            String userid = request.getParameter("empid");
            
            com.krawler.common.admin.User userinfo = getUserObject(session,userid);

            Useraccount ua = getUserAccount(session, userid);

            String uname = getUserName(userinfo);
            
            String empid = getEmployeeID(request, session, ua);
                
            String department = wage.getDepartment();

            String designation = getUserDesignation(ua);

            int days = getWorkingDays(request, session, cmpid);
            
            String bankacc = getUserBankAccount(ua);

            String epf = getUserEPF(session, userid);

            String dateofjoin = getUserDateOFJoining(session, userid);

            String currSymbol=getCurrencySymbol(request, session);
                
            String domainName = com.krawler.common.util.URLUtil.getDomainName(request);
            
            String imageSourceURL="http://apps.deskera.com";
            
            if(StringUtil.isStandAlone()){
                imageSourceURL= com.krawler.common.util.URLUtil.getPageURL(request, "");
            }
                
            String title=AuthHandler.getCompanyName(request);
            
            String companyAddressHtml =getCompanyAddressHtml(request, session, title);

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
                historyid = wage.getHistoryid();
                double unpaidleaves = wage.getUnpaidleaves();

                Query queryw = session.createQuery("from masterDB.Historydetail t where t.payhistory.userID=:userid and createdfor >=:stdate and createdfor <=:enddate and t.type =:type");
                Query basicquery1 = session.createQuery("from masterDB.Historydetail t where t.payhistory.historyid=:historyid and t.name=:name and t.type=:type ");
                basicquery1.setString("historyid", historyid);
                basicquery1.setString("name", "Wages");
                basicquery1.setString("type", "Basic");
                List lst1 = (List) basicquery1.list();
                Query query1 = session.createQuery("from masterDB.Historydetail t where t.payhistory.historyid=:historyid and t.name=:name and t.type<>:type order by t.amount");
                query1.setString("historyid", historyid);
                query1.setString("name", "Wages");
                query1.setString("type", "Basic");
                lst1.addAll((List) query1.list());

                Query query2 = session.createQuery("from masterDB.Historydetail t where t.payhistory.historyid=:historyid and t.name=:name order by t.amount");
                query2.setString("historyid", historyid);
                query2.setString("name", "Taxes");
                List lst2 = (List) query2.list();
 
                CompanyPreferences comp = (CompanyPreferences) session.get(CompanyPreferences.class, cmpid);
                int financialMonth = comp.getFinancialmonth();

                Date d2 = null;
                Date d3 = null;
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
                String stDate = request.getParameter("stdate").replace("/", "-");
                String endDate = request.getParameter("enddate").replace("/", "-");
                d2 = sdf.parse(stDate);
                d3 = sdf.parse(endDate);
                Calendar c1 = Calendar.getInstance();
                c1.setTime(d3);
                c1.set(Calendar.MONTH, financialMonth);
                c1.set(Calendar.DATE, 1);
                Date d = c1.getTime();

                Date DOJ = getUserDateOFJoiningInMilliSeconds(session, userid);

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
                
                Query query3 = session.createQuery("from masterDB.Historydetail t where t.payhistory.historyid=:historyid and t.name=:name order by t.amount");
                query3.setString("historyid", historyid);
                query3.setString("name", "Deduction");
                List lst3 = (List) query3.list();
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
                        

                        queryw.setString("userid", request.getParameter("empid"));
                        queryw.setTimestamp("stdate", d);
                        queryw.setTimestamp("enddate", d3);
                        queryw.setString("type", hd.getType());
                        List lstw = (List) queryw.list();
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
                     
                        queryw.setString("userid", request.getParameter("empid"));
                        queryw.setTimestamp("stdate", d);
                        queryw.setTimestamp("enddate", d3);
                        queryw.setString("type", hd.getType());
                        List lstw1 = (List) queryw.list();
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
                     

                        queryw.setString("userid", request.getParameter("empid"));
                        queryw.setTimestamp("stdate", d);
                        queryw.setTimestamp("enddate", d3);
                        queryw.setString("type", hd.getType());
                        List lstw2 = (List) queryw.list();
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

            List lst4 = getEmployerContribution(session, historyid);

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

                int netpayi = (int) netpay;
                String netinword = new EnglishNumberToWords(request).convert(netpay);
                
                String currname = getCurrencyName(request, session);


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
        } catch (SessionExpiredException ex) {
            errorMsg = ex.getMessage();
            System.out.print(ex.getMessage());
        } catch (IOException ex) {
            errorMsg = ex.getMessage();
            System.out.print(ex.getMessage());
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
            
        }finally{
              if (response.getOutputStream() != null) {
                    response.getOutputStream().flush();
                    response.getOutputStream().close();
              }
        }
    }
    
    private ByteArrayOutputStream createLeaveForm(String cname,
            HttpServletRequest request, Session session, boolean isEmm)
            throws JSONException, SessionExpiredException, ParseException {

        String companyid = AuthHandler.getCompanyid(request);
        Company com = (Company) session.get(Company.class, companyid);
        String from = "";
        String to = "";
        String formTitle = "Absence Leave Form";
        int i = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat decfm=new DecimalFormat("#,##0.00");
        Date d2 = null;
        Date d3 = null;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        String month = "";
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

        days = getWorkingDays(request, session, companyid);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer=null;
        try {

            String empname = "";
            String design = "";
            String historyid = "";
            String net = "";
            String name = "";
            String wtot = "";
            String dtot = "";
            String ttot = "";
            Date generatedOn = null;
            DateFormat monthformat = new SimpleDateFormat("MMMM");
            DateFormat monthformat2 = new SimpleDateFormat("MMMM dd yyyy");
            Query query = null;

//            if (request.getParameter("flagpdf").equals("datewise")) {
//                query = session.createQuery("from masterDB.Payhistory t where t.userID.userID=:empid and t.paymonth=:month");
//                query.setString("empid", request.getParameter("empid"));
//                query.setString("month", month);
//            }
//            if (request.getParameter("flagpdf").equals("monthwise")) {
//                month = request.getParameter("month");
//                query = session.createQuery("from masterDB.Payhistory t where t.userID.userID=:empid and t.paymonth=:month");
//                query.setString("empid", request.getParameter("empid"));
//                query.setString("month", request.getParameter("month"));
//            }
            query = session.createQuery("from masterDB.Payhistory t where t.userID.userID=:empid and t.paycyclestart=:paycyclestart and t.paycycleend=:paycycleend");
            query.setString("empid", request.getParameter("empid"));
            query.setDate("paycyclestart", d2);
            query.setDate("paycycleend", d3);

                String currSymbol="";
                String curid=AuthHandler.getCurrencyID(request);
                Query queryx = session.createQuery("from KWLCurrency where currencyID=:curid");
                queryx.setString("curid",curid);
                KWLCurrency curncy = (KWLCurrency) queryx.uniqueResult();
                String currsym=curncy.getHtmlcode();
                String currname=curncy.getName();
                try{
                    char a1= (char) Integer.parseInt(currsym,16);
                    currSymbol = Character.toString(a1);
                } catch (Exception e){
                    currSymbol = currsym;
                }

            List lst = (List) query.list();
            if (lst.size() == 0) {
                throw new Exception("Exception occured");
            }
            if (lst.size() > 0) {
                masterDB.Payhistory wage = (masterDB.Payhistory) lst.get(0);
                empname = wage.getName();
                design = wage.getDesign();
                historyid = wage.getHistoryid();
                net = String.format("%.2f", Float.valueOf(wage.getNet().trim()).floatValue());
                name = wage.getName();
                wtot = wage.getWagetot();
                ttot = wage.getTaxtot();
                dtot = wage.getDeductot();
                double unpaidleaves = wage.getUnpaidleaves();
                generatedOn = wage.getGeneratedon();
                String gen = monthformat2.format(generatedOn);
                String startdate = monthformat2.format(wage.getCreatedon());
                String enddate = monthformat2.format(wage.getCreatedfor());

                Document document = new Document(PageSize.A4, 15, 15, 15, 15);
                writer = PdfWriter.getInstance(document, baos);
                writer.setPageEvent(new EndPage(request));
                document.open();

                String userid = request.getParameter("empid");

                query = session.createQuery("from com.krawler.common.admin.User as t where t.userID=:empid ");
                query.setString("empid", userid);
                lst = (List) query.list();
                com.krawler.common.admin.User userinfo = (com.krawler.common.admin.User) lst.get(0);
                Useraccount ua = (Useraccount) session.get(Useraccount.class, userinfo.getUserID());
                String uname = userinfo.getLastName() != null ? userinfo.getLastName() : "";
                String empid = "";
                if(ua.getEmployeeIdFormat()==null){
                	empid =  ua.getEmployeeid() == null ? "" : hrmsManager.getEmpidFormatEdit(session, request, ua.getEmployeeid());
                }else{
                	HashMap<String, Object> requestParams = new HashMap<String, Object>();
                	requestParams.put("standardEmpId", hrmsManager.getEmpidFormatEdit(session, request, ua.getEmployeeid()));
                	requestParams.put("employeeIdFormat", ua.getEmployeeIdFormat());
                    empid = hrmsManager.getNewEmployeeIdFormat(requestParams);
                }
                
                String department = wage.getDepartment();
                String designation = getUserDesignation(ua);
                String bankacc = ua.getAccno() != null ? ua.getAccno() : "";
                Empprofile empprof=null;
                String epf="";
                String dateofjoin="";
                try{
                   empprof=(Empprofile) session.get(Empprofile.class,userid);
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
                imgPath = "images/logo1.gif";
                String cmpid=AuthHandler.getCompanyid(request);
                ServletContext sc = getServletContext();
                userTable1.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                userTable1.setWidthPercentage(10);
                PdfPCell cell11 = null;

                PdfPCell cell1x = new PdfPCell(new Paragraph("  ", fontMediumBold));
                cell1x.setBorder(0);
                userTable1.addCell(cell1x);
                try {
                    Image img = Image.getInstance(StorageHandler.GetProfileImgStorePath()+cmpid+".png");
                    cell11 = new PdfPCell(img);
                    cell11.setPaddingLeft(5);
                } catch (Exception e) {
                    cell11 = new PdfPCell(new Paragraph(AuthHandler.getCompanyName(request),fontTblLargeBold));
                    cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                if(cell11!=null){
                cell11.setBorder(0);                
                userTable1.addCell(cell11);
                }

                PdfPCell cell1 = new PdfPCell(new Paragraph("", fontMediumBold));
                cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell1.setBorder(0);
                userTable1.addCell(cell1);

                PdfPTable userTable2 = new PdfPTable(1);
                userTable2.setWidthPercentage(100);
                PdfPCell cell = new PdfPCell(new Paragraph(com.getCompanyName(), fontMediumBold));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                cell.setPaddingTop(15);
                userTable2.addCell(cell);
                cell = new PdfPCell(new Paragraph(com.getCity(), fontSmallRegular));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                userTable2.addCell(cell);
                cell = new PdfPCell(new Paragraph(com.getAddress(), fontSmallRegular));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                userTable2.addCell(cell);
                cell = new PdfPCell(new Paragraph(com.getState(), fontSmallRegular));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                userTable2.addCell(cell);
                cell = new PdfPCell(new Paragraph(com.getZipCode(), fontSmallRegular));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                userTable2.addCell(cell);
                cell = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.payslip.period.params", new Object[]{request.getParameter("stdate"), request.getParameter("enddate")}, RequestContextUtils.getLocale(request)), fontSmallBold));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(0);
                userTable2.addCell(cell);

                PdfPTable userTable3 = new PdfPTable(1);
                userTable3.setWidthPercentage(100);
                PdfPCell cell2 = new PdfPCell(new Paragraph("", fontSmallBold));
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

                PdfPCell h11 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.common.employee.code", null, RequestContextUtils.getLocale(request)), fontSmallBold));
                h11.setHorizontalAlignment(Element.ALIGN_LEFT);
                h11.setPadding(5);
                if(!showborder){
                    h11.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h11);
                PdfPCell h12 = new PdfPCell(new Paragraph("" + empid, fontSmallRegular));
                h12.setHorizontalAlignment(Element.ALIGN_LEFT);
                h12.setPadding(5);
                if(!showborder){
                    h12.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h12);
                PdfPCell h13 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.common.employee.name", null, RequestContextUtils.getLocale(request)), fontSmallBold));
                h13.setHorizontalAlignment(Element.ALIGN_LEFT);
                h13.setPadding(5);
                if(!showborder){
                    h13.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h13);
                PdfPCell h14 = new PdfPCell(new Paragraph(userinfo.getFirstName()+" "+ uname, fontSmallRegular));
                h14.setHorizontalAlignment(Element.ALIGN_LEFT);
                h14.setPadding(5);
                if(!showborder){
                    h14.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h14);

                PdfPCell h21 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.common.department", null, RequestContextUtils.getLocale(request)), fontTblMediumBold));
                h21.setHorizontalAlignment(Element.ALIGN_LEFT);
                h21.setPadding(5);
                if(!showborder){
                    h21.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h21);
                PdfPCell h22 = new PdfPCell(new Paragraph(department, fontSmallRegular));
                h22.setHorizontalAlignment(Element.ALIGN_LEFT);
                h22.setPadding(5);
                if(!showborder){
                    h22.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h22);
                PdfPCell h33 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.common.designation", null, RequestContextUtils.getLocale(request)), fontTblMediumBold));
                h33.setHorizontalAlignment(Element.ALIGN_LEFT);
                h33.setPadding(5);
                if(!showborder){
                    h33.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h33);
                PdfPCell h34 = new PdfPCell(new Paragraph(designation, fontSmallRegular));
                h34.setHorizontalAlignment(Element.ALIGN_LEFT);
                h34.setPadding(5);
                if(!showborder){
                    h34.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h34);
                PdfPCell h43 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.common.bank.ac.no", null, RequestContextUtils.getLocale(request)), fontTblMediumBold));
                h43.setHorizontalAlignment(Element.ALIGN_LEFT);
                h43.setPadding(5);
                if(!showborder){
                    h43.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h43);
                PdfPCell h44 = new PdfPCell(new Paragraph(bankacc, fontSmallRegular));
                h44.setHorizontalAlignment(Element.ALIGN_LEFT);
                h44.setPadding(5);
                if(!showborder){
                    h44.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h44);             
                PdfPCell h61 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.common.working.days", null, RequestContextUtils.getLocale(request)), fontTblMediumBold));
                h61.setHorizontalAlignment(Element.ALIGN_LEFT);
                h61.setPadding(5);
                if(!showborder){
                    h61.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h61);
                PdfPCell h62 = new PdfPCell(new Paragraph(String.valueOf(days), fontSmallRegular));
                h62.setHorizontalAlignment(Element.ALIGN_LEFT);
                h62.setPadding(5);
                if(!showborder){
                    h62.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h62);
                PdfPCell h63 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.common.e.p.f", null, RequestContextUtils.getLocale(request)), fontTblMediumBold));
                h63.setHorizontalAlignment(Element.ALIGN_LEFT);
                h63.setPadding(5);
                if(!showborder){
                    h63.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h63);
                PdfPCell h64 = new PdfPCell(new Paragraph(epf, fontSmallRegular));
                h64.setHorizontalAlignment(Element.ALIGN_LEFT);
                h64.setPadding(5);
                if(!showborder){
                    h64.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h64);
                PdfPCell h65 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.common.DateofJoining", null, RequestContextUtils.getLocale(request)), fontTblMediumBold));
                h65.setHorizontalAlignment(Element.ALIGN_LEFT);
                h65.setPadding(5);
                if(!showborder){
                    h65.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h65);
                PdfPCell h66 = new PdfPCell(new Paragraph(dateofjoin, fontSmallRegular));
                h66.setHorizontalAlignment(Element.ALIGN_LEFT);
                h66.setPadding(5);
                if(!showborder){
                    h66.setBorder(0);
                }
//                else{
//                    mainCell1.setBorder(1);
//                }
                table2.addCell(h66);
                
                for (int y = 0; y < 2; y++) {
                    PdfPCell h71 = new PdfPCell(new Paragraph("  ", fontTblMediumBold));
                    h71.setHorizontalAlignment(Element.ALIGN_LEFT);
                    if(!showborder){
                        h71.setBorder(0);
                    } else{
                         h71.setBorder(Rectangle.LEFT);
                    }
                    table2.addCell(h71);
                    for (i = 0; i < 2; i++) {
                        h71 = new PdfPCell(new Paragraph("  ", fontTblMediumBold));
                        h71.setHorizontalAlignment(Element.ALIGN_LEFT);
                        h71.setBorder(0);
                        table2.addCell(h71);
                    }
                    h71 = new PdfPCell(new Paragraph("  ", fontTblMediumBold));
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
                //table31.setWidths(new float[]{20,15,15,20,15,15});
                table31.setWidths(new float[]{30,20,30,20});
                PdfPTable table41 = new PdfPTable(2);
                table41.setWidthPercentage(100);
                //table31.setWidths(new float[]{20,15,15,20,15,15});
                table41.setWidths(new float[]{50,50});

              //***************************************************************************************************************
                PdfPCell s11 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.Earnings", null, RequestContextUtils.getLocale(request)), fontTblMediumBold));
                s11.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(!showborder){
                        s11.setBorder(0);
                } else{
                     s11.setBorder(Rectangle.LEFT + Rectangle.BOTTOM);
                }
                s11.setPadding(5);
                table31.addCell(s11);
                PdfPCell s12 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.amount.params", new Object[]{currSymbol}, RequestContextUtils.getLocale(request)), fontTblMediumBold));
                s12.setHorizontalAlignment(Element.ALIGN_RIGHT);
                s12.setPadding(5);
                if(!showborder){
                        s12.setBorder(0);
                } else{
                     s12.setBorder(Rectangle.LEFT + Rectangle.BOTTOM);
                }
                table31.addCell(s12);
//                PdfPCell s13 = new PdfPCell(new Paragraph("Year to Date", fontTblMediumBold));
//                s13.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                s13.setBorder(Rectangle.LEFT + Rectangle.BOTTOM);
//                s13.setPadding(5);
//                table31.addCell(s13);
                PdfPCell s14 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.Deduction", null, RequestContextUtils.getLocale(request)), fontTblMediumBold));
                s14.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(!showborder){
                        s14.setBorder(0);
                } else{
                     s14.setBorder(Rectangle.LEFT + Rectangle.BOTTOM);
                }
                s14.setPadding(5);
                table31.addCell(s14);
                PdfPCell s15 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.amount.params", new Object[]{currSymbol}, RequestContextUtils.getLocale(request)), fontTblMediumBold));
                s15.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(!showborder){
                     s15.setBorder(0);
                } else{
                     s15.setBorder(Rectangle.LEFT + Rectangle.RIGHT + Rectangle.BOTTOM);
                }
                s15.setPadding(5);
                table31.addCell(s15);
//                PdfPCell s16 = new PdfPCell(new Paragraph("Year to Date", fontTblMediumBold));
//                s16.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                s16.setBorder(Rectangle.RIGHT + Rectangle.LEFT + Rectangle.BOTTOM);
//                s16.setPadding(5);
//                table31.addCell(s16);
                int Wcount = 0;
                float taxtotal = 0;

                Query queryw = session.createQuery("from masterDB.Historydetail t where t.payhistory.userID=:userid and createdfor >=:stdate and createdfor <=:enddate and t.type =:type");
                Query basicquery1 = session.createQuery("from masterDB.Historydetail t where t.payhistory.historyid=:historyid and t.name=:name and t.type=:type ");
                basicquery1.setString("historyid", historyid);
                basicquery1.setString("name", "Wages");
                basicquery1.setString("type", "Basic");
                List lst1 = (List) basicquery1.list();
                Query query1 = session.createQuery("from masterDB.Historydetail t where t.payhistory.historyid=:historyid and t.name=:name and t.type<>:type order by t.amount");
                query1.setString("historyid", historyid);
                query1.setString("name", "Wages");
                query1.setString("type", "Basic");
                lst1.addAll((List) query1.list());

                Query query2 = session.createQuery("from masterDB.Historydetail t where t.payhistory.historyid=:historyid and t.name=:name order by t.amount");
                query2.setString("historyid", historyid);
                query2.setString("name", "Taxes");
                List lst2 = (List) query2.list();

                CompanyPreferences comp = (CompanyPreferences) session.get(CompanyPreferences.class, cmpid);
                int financialMonth = comp.getFinancialmonth();
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
                Query query3 = session.createQuery("from masterDB.Historydetail t where t.payhistory.historyid=:historyid and t.name=:name order by t.amount");
                query3.setString("historyid", historyid);
                query3.setString("name", "Deduction");
                List lst3 = (List) query3.list();
                int size= lst1.size()>(lst2.size()+lst3.size())?lst1.size():lst2.size()+lst3.size();
                float wagetotal = 0;
                double totalYTDEarning = 0, totalYTDDeduction = 0;
                for (i = 0; i < size; i++) {
                   masterDB.Historydetail hd =null;
                   if(i<lst1.size()){
                        hd = (masterDB.Historydetail) lst1.get(i);
                        PdfPCell s21 = new PdfPCell(new Paragraph(hd.getType(), fontSmallRegular));
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

                        PdfPCell s22 = new PdfPCell(new Paragraph(String.valueOf(decfm.format(newamount)), fontSmallRegular));
                        s22.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        if(!showborder){
                             s22.setBorder(0);
                        } else{
                             s22.setBorder(Rectangle.LEFT);
                        }
                        s22.setPadding(5);
                        table31.addCell(s22);

                        queryw.setString("userid", request.getParameter("empid"));
                        queryw.setTimestamp("stdate", d);
                        queryw.setTimestamp("enddate", d3);
                        queryw.setString("type", hd.getType());
                        List lstw = (List) queryw.list();
                        double wageAmt = 0;
                        masterDB.Historydetail hd1 =null;
                        for (int y = 0; y < lstw.size(); y++) {
                            hd1 = (masterDB.Historydetail) lstw.get(y);
                            wageAmt += Double.parseDouble(hd1.getAmount());
                        }
//                        PdfPCell s23 = new PdfPCell(new Paragraph(String.valueOf(decfm.format(wageAmt)), fontSmallRegular));
//                        s23.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                        s23.setBorder(Rectangle.LEFT);
//                        s23.setPadding(5);
//                        table31.addCell(s23);
                        wagetotal = wagetotal + Float.parseFloat(hd.getAmount());
                        totalYTDEarning = totalYTDEarning + wageAmt;
                        Wcount++;
                   }else{
                      for(int j=0;j<2;j++){
                        PdfPCell s21 = new PdfPCell(new Paragraph("", fontSmallRegular));
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
                        PdfPCell s24 = new PdfPCell(new Paragraph(hd.getType(), fontSmallRegular));
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

                        PdfPCell s25 = new PdfPCell(new Paragraph(decfm.format(newamount), fontSmallRegular));
                        s25.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        if(!showborder){
                             s25.setBorder(0);
                        } else{
                             s25.setBorder(Rectangle.RIGHT + Rectangle.LEFT);
                        }
                        s25.setPadding(5);
                        table31.addCell(s25);

                        queryw.setString("userid", request.getParameter("empid"));
                        queryw.setTimestamp("stdate", d);
                        queryw.setTimestamp("enddate", d3);
                        queryw.setString("type", hd.getType());
                        List lstw1 = (List) queryw.list();
                        double taxAmt = 0;
                        masterDB.Historydetail hd2 =null;
                        for (int y = 0; y < lstw1.size(); y++) {
                            hd2 = (masterDB.Historydetail) lstw1.get(y);
                            taxAmt += Double.parseDouble(hd2.getAmount());
                        }
//                        PdfPCell s26 = new PdfPCell(new Paragraph(decfm.format(taxAmt), fontSmallRegular));
//                        s26.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                        s26.setBorder(Rectangle.RIGHT + Rectangle.LEFT);
//                        s26.setPadding(5);
//                        table31.addCell(s26);
                        taxtotal = taxtotal + Float.parseFloat(hd.getAmount());
                        totalYTDDeduction = totalYTDDeduction + taxAmt;
                   }else {
                      if(i-lst2.size() < lst3.size()){
                        hd = (masterDB.Historydetail) lst3.get(i-lst2.size());
                        PdfPCell s24 = new PdfPCell(new Paragraph(hd.getType(), fontSmallRegular));
                        if(hd.getType().equals("Unpaid_leaves")){
                            s24 = new PdfPCell(new Paragraph(hd.getType()+"("+unpaidleaves+")", fontSmallRegular));
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

                        PdfPCell s25 = new PdfPCell(new Paragraph(decfm.format(newamount), fontSmallRegular));
                        s25.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        if(!showborder){
                             s25.setBorder(0);
                        } else{
                             s25.setBorder(Rectangle.RIGHT + Rectangle.LEFT);;
                        }
                        s25.setPadding(5);
                        table31.addCell(s25);

                        queryw.setString("userid", request.getParameter("empid"));
                        queryw.setTimestamp("stdate", d);
                        queryw.setTimestamp("enddate", d3);
                        queryw.setString("type", hd.getType());
                        List lstw2 = (List) queryw.list();
                        masterDB.Historydetail hd3 =null;
                        double deductAmt = 0;
                        for (int y = 0; y < lstw2.size(); y++) {
                            hd3 = (masterDB.Historydetail) lstw2.get(y);
                            deductAmt += Double.parseDouble(hd3.getAmount());
                        }

//                        PdfPCell s26 = new PdfPCell(new Paragraph(decfm.format(deductAmt), fontSmallRegular));
//                        s26.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                        s26.setBorder(Rectangle.RIGHT + Rectangle.LEFT);
//                        s26.setPadding(5);
//                        table31.addCell(s26);
                        taxtotal = taxtotal + Float.parseFloat(hd.getAmount());
                        totalYTDDeduction = totalYTDDeduction + deductAmt;
                      }else{
//                          if(i-lst2.size()-lst3.size() < lst4.size()){
//                                hd = (masterDB.Historydetail) lst4.get(i-lst2.size()-lst3.size());
//                                PdfPCell v24 = new PdfPCell(new Paragraph(hd.getType(), fontSmallRegular));
//                                v24.setHorizontalAlignment(Element.ALIGN_LEFT);
//                                v24.setBorder(Rectangle.LEFT);
//                                v24.setPadding(5);
//                                table31.addCell(v24);
//
//                                BigDecimal bd = new BigDecimal(hd.getAmount());
//                                bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
//                                double newamount = bd.doubleValue();
//
//                                PdfPCell s25 = new PdfPCell(new Paragraph(decfm.format(newamount), fontSmallRegular));
//                                s25.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                                s25.setBorder(Rectangle.RIGHT + Rectangle.LEFT);;
//                                s25.setPadding(5);
//                                table31.addCell(s25);
//                          }else{
                              for(int j=0;j<2;j++){
                                PdfPCell s21 = new PdfPCell(new Paragraph("", fontSmallRegular));
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
//                }
                PdfPCell s91 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.total.earnings", null, RequestContextUtils.getLocale(request)), fontTblMediumBold));
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
                PdfPCell s92 = new PdfPCell(new Paragraph(decfm.format(newamount), fontSmallRegular));
                s92.setHorizontalAlignment(Element.ALIGN_BASELINE);
                s92.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(!showborder){
                     s92.setBorder(0);
                } else{
                     s92.setBorder(Rectangle.LEFT + Rectangle.TOP + Rectangle.BOTTOM);
                }
                s92.setPadding(5);
                table31.addCell(s92);
//                PdfPCell s93 = new PdfPCell(new Paragraph(decfm.format(totalYTDEarning), fontSmallRegular));
//                s93.setHorizontalAlignment(Element.ALIGN_BASELINE);
//                s93.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                s93.setBorder(Rectangle.LEFT + Rectangle.TOP);
//                s93.setPadding(5);
//                table31.addCell(s93);
                PdfPCell s94 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.total.deductions", null, RequestContextUtils.getLocale(request)), fontTblMediumBold));
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
                PdfPCell s95 = new PdfPCell(new Paragraph(decfm.format(newamount), fontSmallRegular));
                s95.setHorizontalAlignment(Element.ALIGN_BASELINE);
                s95.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(!showborder){
                     s95.setBorder(0);
                } else{
                     s95.setBorder(Rectangle.RIGHT + Rectangle.LEFT + Rectangle.TOP + Rectangle.BOTTOM);
                }
                s95.setPadding(5);
                table31.addCell(s95);
//                PdfPCell s96 = new PdfPCell(new Paragraph(decfm.format(totalYTDDeduction), fontSmallRegular));
//                s96.setHorizontalAlignment(Element.ALIGN_BASELINE);
//                s96.setHorizontalAlignment(Element.ALIGN_RIGHT);
//                s96.setBorder(Rectangle.RIGHT + Rectangle.LEFT + Rectangle.TOP);
//                s96.setPadding(5);
//                table31.addCell(s96);

                for (int y = 0; y < 2; y++) {
                    PdfPCell he71 = new PdfPCell(new Paragraph("  ", fontTblMediumBold));
                    he71.setHorizontalAlignment(Element.ALIGN_LEFT);
                    if(!showborder){
                         he71.setBorder(0);
                    } else{
                         he71.setBorder(Rectangle.LEFT);
                    }
                    table31.addCell(he71);
                    for (i = 0; i < 2; i++) {
                        he71 = new PdfPCell(new Paragraph("  ", fontTblMediumBold));
                        he71.setHorizontalAlignment(Element.ALIGN_LEFT);
                        he71.setBorder(0);
                        table31.addCell(he71);
                    }
                    he71 = new PdfPCell(new Paragraph("  ", fontTblMediumBold));
                    he71.setHorizontalAlignment(Element.ALIGN_LEFT);
              //      he71.setBorder(Rectangle.RIGHT);
                    if(!showborder){
                         he71.setBorder(0);
                    } else{
                         he71.setBorder(Rectangle.RIGHT);
                    }
                    table31.addCell(he71);
                }

                Query query4 = session.createQuery("from masterDB.Historydetail t where t.payhistory.historyid=:historyid and t.name=:name order by t.amount");
                query4.setString("historyid", historyid);
                query4.setString("name", "Employer Contribution");
                List lst4 = (List) query4.list();
                double ectotal = 0;
                PdfPCell ec11 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.EmployerContribution", null, RequestContextUtils.getLocale(request)), fontTblMediumBold));
                ec11.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(!showborder){
                     ec11.setBorder(0);
                } else{
                     ec11.setBorder(Rectangle.LEFT + Rectangle.BOTTOM);
                }
                ec11.setPadding(5);
                table41.addCell(ec11);
                PdfPCell ec12 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.amount.params", new Object[]{currSymbol}, RequestContextUtils.getLocale(request)), fontTblMediumBold));
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
                    PdfPCell s24 = new PdfPCell(new Paragraph(hd1.getType(), fontSmallRegular));
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

                    PdfPCell s25 = new PdfPCell(new Paragraph(decfm.format(newamount), fontSmallRegular));
                    s25.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    if(!showborder){
                         s25.setBorder(0);
                    } else{
                         s25.setBorder(Rectangle.RIGHT + Rectangle.LEFT);
                    }
                    s25.setPadding(5);
                    table41.addCell(s25);
                }

                PdfPCell se91 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.total.contribution", null, RequestContextUtils.getLocale(request)), fontTblMediumBold));
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
                PdfPCell se92 = new PdfPCell(new Paragraph(decfm.format(newamount), fontSmallRegular));
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
                    PdfPCell i33 = new PdfPCell(new Paragraph("  ", fontTblMediumBold));
                    i33.setHorizontalAlignment(Element.ALIGN_CENTER);
                    if(!showborder){
                        i33.setBorder(0);
                    } else{
                        i33.setBorder(Rectangle.LEFT + Rectangle.RIGHT);
                    }
                    table4.addCell(i33);
                }

                
                PdfPCell i31 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.netpay", null, RequestContextUtils.getLocale(request))+" ", fontTblMediumBold));
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
                //PdfPCell i33 = new PdfPCell(new Paragraph(currSymbol+String.valueOf((int)netpay+".00"), fontSmallRegular));
                PdfPCell i33 = new PdfPCell(new Paragraph(amount, fontTblMediumBold));
//                PdfPCell i33 = new PdfPCell(new Paragraph("Rs 13,350.00", fontTblMediumBold));
                i33.setHorizontalAlignment(Element.ALIGN_RIGHT);
                if(!showborder){
                    i33.setBorder(0);
                } else{
                    i31.setBorder(Rectangle.RIGHT);
                }
                i33.setPadding(5);
                table4.addCell(i33);

                PdfPCell i32 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.in.words", null, RequestContextUtils.getLocale(request)), fontTblMediumBold));
                i32.setHorizontalAlignment(Element.ALIGN_LEFT);
                if(!showborder){
                    i32.setBorder(0);
                } else{
                    i31.setBorder(Rectangle.LEFT);
                }
                i32.setPadding(5);
                table4.addCell(i32);
                int netpayi = (int) netpay;
//                String netinword = EnglishNumberToWords.convert(Long.parseLong(String.valueOf(netpayi)));
                String netinword = "";//new EnglishNumberToWords(request).convert(netpay);
                PdfPCell i34 = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.only", new Object[]{currname, netinword}, RequestContextUtils.getLocale(request)), fontTblMediumBold));
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
                cell = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.employee.signature", null, RequestContextUtils.getLocale(request)), fontSmallBold));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(0);
                table5.addCell(cell);
                cell = new PdfPCell(new Paragraph(messageSource.getMessage("hrms.payroll.manager.signature", null, RequestContextUtils.getLocale(request)), fontSmallBold));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(0);
                table5.addCell(cell);

                document.add(mainTable);
//                document.add(new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n"));
//                document.add(table5);
                document.close();
            }

            return baos;

        } catch (Exception ex) {
        	ex.printStackTrace();
            return null;
        }finally{
              writer.close();
        }

    }
    public static String numberFormatter(double values,String compSymbol) {
        NumberFormat numberFormatter;
        java.util.Locale currentLocale = java.util.Locale.US;
        numberFormatter = NumberFormat.getNumberInstance(currentLocale);
        numberFormatter.setMinimumFractionDigits(2);
        numberFormatter.setMaximumFractionDigits(2);
        String amount = compSymbol + " " + numberFormatter.format(values);
        return amount;
    }
    
    private void writeDataToFile(String filename, ByteArrayOutputStream baos,
            HttpServletResponse response) throws IOException {
        try {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setContentType("application/octet-stream");
            response.setContentLength(baos.size());
            response.getOutputStream().write(baos.toByteArray());            
        } catch (IOException e) {
            KrawlerLog.op.warn("Unable To Download File :" + e.toString());
        }finally{
            if (response.getOutputStream() != null) {
                response.getOutputStream().flush();
                response.getOutputStream().close();
            }
        }
    }
    public class EnglishNumberToWords{
    	
    	HttpServletRequest request;
    	
    	public EnglishNumberToWords(HttpServletRequest request){
    		this.request = request;
    	}
        private final String[] tensNames = {
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
        private final String[] numNames = {
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

        private String convertLessThanOneThousand(int number) {
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
    protected String formatDate(long millisec, boolean showDate, boolean showTime){
    	return formatDate(millisec, showDate, showTime, TimeZone.getTimeZone("GMT"+this.tdiff));
    }

    protected String formatDate(long millisec, boolean showDate, boolean showTime, TimeZone tz){
    	String fmt="";
    	dFmt.setTimeZone(tz);
    	tFmt.setTimeZone(tz);
    	if(showDate)
    		fmt += dFmt.format(millisec);
    	if(showTime)
    		fmt += tFmt.format(millisec);
    	return fmt;
    }
    public String formatValue(String value, String xtype) {
		try {
			if ("datefield".equals(xtype) && value.length() > 0) {
				value = formatDate(Long.parseLong(value), true, false);
			}
			if ("timefield".equals(xtype) && value.length() > 0) {
				value = formatDate(Long.parseLong(value), false, true);
			}
		} catch (Exception e) {
			
		}
		return value;
	}
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

	@Override
	public void setMessageSource(MessageSource ms) {
		messageSource = ms;
	}
}
