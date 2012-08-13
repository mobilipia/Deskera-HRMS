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

//import com.krawler.common.admin.AuditAction;
import com.krawler.common.util.AuditAction;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.KrawlerLog;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.handlers.ProfileHandler;
import com.lowagie.text.DocumentException;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.hrmsManager;
import com.krawler.hrms.performance.Appraisalcycle;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Transaction;

public class AppraisalDetails extends HttpServlet {

    private static final long serialVersionUID = -763555229410947890L;
    private static Font fontSmallRegular = FontFactory.getFont("HELVETICA", 8, Font.NORMAL, Color.BLACK);
    private static Font fontSmallBold = FontFactory.getFont("HELVETICA", 8, Font.BOLD, Color.BLACK);
    private static Font fontMediumBold = FontFactory.getFont("HELVETICA", 10, Font.BOLD, Color.BLACK);
    private static Font helpFont = FontFactory.getFont("HELVETICA", 8, Font.BOLD, Color.BLACK);
    private static Font fontHeadingMediumBold = FontFactory.getFont("HELVETICA", 12, Font.BOLD, Color.BLACK);


    public class EndPage extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Rectangle page = document.getPageSize();
                PdfPTable footer = new PdfPTable(1);
                footer.setWidthPercentage(100);
                footer.setSpacingBefore(20);
                String FootPager = String.valueOf(document.getPageNumber());//current page no
                PdfPCell footerPageNocell = new PdfPCell(new Phrase(FootPager, fontSmallBold));
                footerPageNocell.setBorder(0);
                footerPageNocell.setPaddingBottom(5);
                footerPageNocell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                footer.addCell(footerPageNocell);
                footer.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin() - 5, writer.getDirectContent());

            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
    }
    
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException, ServiceException {
        response.setContentType("text/html;charset=UTF-8");
        Session session = HibernateUtil.getCurrentSession();
        try {
            String filename="AppraisalReport";
            ByteArrayOutputStream baos = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("filename"))){
                 filename=request.getParameter("filename");
            }
            int flag = 1;

            switch (flag) {
                case 1:
                    baos = AppraisalDetail(request, session, true);
                    if (baos != null) {
                        writeDataToFile(filename, baos, response);
                    }
                    break;
            }
        } catch (Exception e) {
            PrintWriter out = response.getWriter();
            out.println("<script type='text/javascript'>alert('Failed to Download Document.');</script>");
             throw ServiceException.FAILURE(e.getMessage(), e);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

    private ByteArrayOutputStream AppraisalDetail(HttpServletRequest request, Session session, boolean isEmm)
            throws JSONException, SessionExpiredException, DocumentException, ServiceException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String[] colHeader = {"Employee Name", "Appraisal Cycle Name", "Appraisal Cycle Start Date",
            "Appraisal Cycle End Date", "Total No. of Appraisals", "No. of Appraisals submitted","Overall Self Comment","Overall Appraiser Comments","Overall Competency Score","Competencies"};
        String[] dataIndex ={"empname","appcylename","appcylestdate","appcylendate","totalappraisal","appraisalsubmitted","empcomment","mancom","manavgwght",""};
        String[] compHeader={"Name","Description","Self Appraisal Score","Self Appraisal Comment","Appraiser Competency Score","Appraiser Comments"};
        String[] compDataIndex={"comptename","comptdesc","selfcompscore","selfcomment","compmanwght"};
        String[] compGoalHeader={"Goals","Assigned By","Appraiser Rating","Appraiser Comment","Self Rating","Self Comments"};
        String[] compGoalDataIndex={"gname","assignedby","gmanrat","mangoalcomment","gemprat","empgoalcomment"};
        String managerComments = "";
        String scoreAvg="";
        String companyid=null;
        Transaction tx = null;
        PdfWriter writer=null;
        try {
                Document document = new Document(PageSize.A4.rotate(), 25, 25, 25, 25);
                writer=PdfWriter.getInstance(document, baos);
                writer.setPageEvent(new EndPage());
                document.open();
                java.awt.Color tColor = new Color(9, 9, 9);
                fontSmallBold.setColor(tColor);
                Paragraph p = new Paragraph();
                
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{55,75});
                
                PdfPTable mainTable = new PdfPTable(1);
                mainTable.setTotalWidth(90);
                mainTable.setWidthPercentage(100);
                mainTable.setSpacingBefore(20);
            
                PdfPCell headcell = null;
                headcell = new PdfPCell(new Paragraph("Appraisal Details", fontHeadingMediumBold));
                headcell.setBackgroundColor(new Color(0xEEEEEE));
                headcell.setPadding(5);
                mainTable.addCell(headcell);
                document.add(mainTable);

                String str=hrmsManager.getAppraisalReport(session, request);
                JSONObject jobjTemplate = new JSONObject(str);
                com.krawler.utils.json.base.JSONArray jarr = jobjTemplate.getJSONArray("data");
                String goalstr = "";
                User user=(User)session.get(User.class,request.getParameter("userid"));
                companyid=user.getCompany().getCompanyID();
                if(hrmsManager.checkModule("goal", session, request, companyid))
                    goalstr = hrmsManager.getAppraisalReportGoalsforGrid(session, request);
                com.krawler.utils.json.base.JSONArray jarr2=jarr.getJSONObject(0).getJSONArray("data");
                JSONObject jobjAppraisal = new JSONObject(jarr2.getString(0));
                jarr = jobjAppraisal.getJSONArray("competencies");
                int headlen = colHeader.length;
                if (jarr.length()<1) {
                   headlen = headlen-1;
                }
                for(int i=0; i<headlen;i++) {
                    PdfPCell pcell = new PdfPCell(new Paragraph(colHeader[i], fontMediumBold));
                    pcell.setBorder(0);
                    if(i==0)
                        pcell.setPaddingTop(10);
                    pcell.setPaddingLeft(15);
                    pcell.setPaddingBottom(4);
                    pcell.setBorderColor(new Color(0xF2F2F2));
                    pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    pcell.setVerticalAlignment(Element.ALIGN_LEFT);
                    table.addCell(pcell);

                    if(!dataIndex[i].equals("mancom")) {
                        pcell = new PdfPCell(new Paragraph(!dataIndex[i].equals("")?!jobjAppraisal.isNull(dataIndex[i])?jobjAppraisal.getString(dataIndex[i]):"":"", fontSmallRegular));
                        if(i==0)
                            pcell.setPaddingTop(10);
                        pcell.setBorder(0);
                        pcell.setPaddingLeft(10);
                        pcell.setPaddingBottom(4);
                        pcell.setBorderColor(new Color(0xF2F2F2));
                        pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        pcell.setVerticalAlignment(Element.ALIGN_LEFT);
                        table.addCell(pcell);
                    } else {
                        if(!jobjAppraisal.isNull(dataIndex[i])) {
                            String[] spl = jobjAppraisal.getString(dataIndex[i]).split(",");
                            String strData="";
                            for(int counter=0;counter<spl.length;counter++) {
                                strData+=spl[counter]+" \n";
                            }
                            pcell = new PdfPCell(new Paragraph(strData, fontSmallRegular));
                            if(i==0)
                                pcell.setPaddingTop(10);
                            pcell.setBorder(0);
                            pcell.setPaddingLeft(10);
                            pcell.setPaddingBottom(4);
                            pcell.setBorderColor(new Color(0xF2F2F2));
                            pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            pcell.setVerticalAlignment(Element.ALIGN_LEFT);
                            table.addCell(pcell);
                        }
                    }
                }


                document.add(table);
                
                PdfPTable compTable = new PdfPTable(6);
                compTable.setWidthPercentage(100);
                compTable.setWidths(new float[]{50,60,30,50,42,70});
                compTable.setSpacingBefore(20);
                compTable.setHeaderRows(1);

                for(int i=0;i<compHeader.length;i++){
                        PdfPCell pcell = new PdfPCell(new Paragraph(compHeader[i], fontMediumBold));
                        pcell.setBorder(0);
                        pcell.setBorder(PdfPCell.BOX);
                        pcell.setPadding(4);
                        pcell.setBorderColor(Color.GRAY);
                        pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        compTable.addCell(pcell);
                }

                for (int i = 0; i < jarr.length(); i++) {
                jobjAppraisal = jarr.getJSONObject(i);
                for (int k = 0; k < compHeader.length; k++) {
                    if (k != compHeader.length - 1) {
                        scoreAvg="";
                        if(!jobjAppraisal.isNull(compDataIndex[k]) && compDataIndex[k].equals("compmanwght")) {
                            scoreAvg = jobjAppraisal.getString("nominalRat");
                            Font font = new Font(Font.HELVETICA, 8, Font.BOLD, Color.BLACK);
                            Chunk chunk1 = new Chunk(jobjAppraisal.getString(compDataIndex[k])+"\n\n",fontSmallRegular);
                            Chunk chunk2 = null;
                            if (!StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
//                                user=(User)session.get(User.class,request.getParameter("userid"));
//                                companyid=user.getCompany().getCompanyID();
                                if (hrmsManager.checkModule("modaverage", session, request,companyid)) {
                                    chunk2 = new Chunk("[Mod Avg:  " + scoreAvg + " ]", font);
                                } else {
                                    chunk2 = new Chunk("[Avg:  " + scoreAvg + " ]", font);
                                }
                            } else {
                                if (hrmsManager.checkModule("modaverage", session, request)) {
                                    chunk2 = new Chunk("[Mod Avg:  " + scoreAvg + " ]", font);
                                } else {
                                    chunk2 = new Chunk("[Avg:  " + scoreAvg + " ]", font);
                                }
                            }
                            Phrase phrase1 = new Phrase();
                            phrase1.add(chunk1);
                            phrase1.add(chunk2);
                            
                            p = new Paragraph();
                            p.add(phrase1);
                        }
                        PdfPCell pcell=new PdfPCell();
                        if(!scoreAvg.equals("")) {
                            pcell = new PdfPCell(new Paragraph(p));
                            pcell.setPadding(0);
                            pcell.setPaddingTop(2);
                            pcell.setPaddingBottom(4);
                        } else {
                            if (!jobjAppraisal.isNull(compDataIndex[k])) {
                                String htmlStr = jobjAppraisal.getString(compDataIndex[k]);
                                htmlStr=htmlStr.replaceAll("\n","<br>");
                                StyleSheet st = new StyleSheet();
                                st.loadTagStyle("body", "face", "HELVETICA");
                                st.loadTagStyle("body", "size", "1");
                                st.loadTagStyle("body", "leading", "8,0");
                                HTMLWorker worker = new HTMLWorker(document);
                                StringReader stringReader = new StringReader(htmlStr);
                                ArrayList listStr = HTMLWorker.parseToList(stringReader, st);
                                pcell.setPadding(4);
                                for (int htmlCount = 0; htmlCount < listStr.size(); ++htmlCount) {
                                    if(!listStr.get(htmlCount).toString().equals("[]"))
                                        pcell.addElement((Element) listStr.get(htmlCount));
                                }
                            } else
                                pcell = new PdfPCell(new Paragraph(!jobjAppraisal.isNull(compDataIndex[k]) ? hrmsManager.serverHTMLStripper(jobjAppraisal.getString(compDataIndex[k])) : "", fontSmallRegular));
                        }
                        pcell.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                        pcell.setBorderColor(Color.GRAY);
                        pcell.setHorizontalAlignment(compDataIndex[k].equals("comptename") || compDataIndex[k].equals("comptdesc") || compDataIndex[k].equals("selfcomment") ? Element.ALIGN_LEFT : Element.ALIGN_CENTER);
                        pcell.setVerticalAlignment(compDataIndex[k].equals("comptdesc") || compDataIndex[k].equals("comptdesc") || compDataIndex[k].equals("selfcomment") ? Element.ALIGN_LEFT : Element.ALIGN_CENTER);
                        compTable.addCell(pcell);
                        
                    } else {
                        jarr2 = jobjAppraisal.getJSONArray("comments");
                        managerComments="";
                        int commentCount=1;
                        for (int j = jarr2.length()-1; j >= 0 ; j--) {
                            jobjTemplate = jarr2.getJSONObject(j);
                            managerComments += commentCount+")  "+jobjTemplate.getString("managercomment") + "\n\n";
                            commentCount++;
                        }
                        PdfPCell pcell = new PdfPCell(new Paragraph(managerComments, fontSmallRegular));
                        pcell.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                        pcell.setPadding(4);
                        pcell.setBorderColor(Color.GRAY);
                        pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        pcell.setVerticalAlignment(Element.ALIGN_LEFT);
                        compTable.addCell(pcell);
                    }
                }
            }

            PdfPTable helpTable = new PdfPTable(1);
            helpTable.setTotalWidth(90);
            helpTable.setWidthPercentage(100);
            helpTable.setSpacingBefore(20);
            

            PdfPCell pcell = new PdfPCell(new Paragraph("Mod Avg. : Average of ratings after excluding a minimum and a maximum rating. For e.g, mod average of 2, 3, 2, 4, 5, 3 is (2+3+4+3)/4", helpFont));
            pcell.setBorder(0);
            pcell.setPadding(4);
            pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
            helpTable.addCell(pcell);

            document.add(compTable);
            if (!StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
                if (hrmsManager.checkModule("modaverage", session, request,companyid)){
                 document.add(helpTable);
             }
            }
            else{
                if (hrmsManager.checkModule("modaverage", session, request)){
                 document.add(helpTable);
             }
            }

            if(!StringUtil.isNullOrEmpty(goalstr)) {
                PdfPTable compgTable = new PdfPTable(6);
                compgTable.setWidthPercentage(100);
                compgTable.setWidths(new float[]{50,60,30,50,42,70});
                compgTable.setSpacingBefore(20);
                compgTable.setHeaderRows(1);
                for(int i=0;i<compGoalHeader.length;i++){
                      PdfPCell pgcell = new PdfPCell(new Paragraph(compGoalHeader[i], fontMediumBold));
                      pgcell.setBorder(0);
                      pgcell.setBorder(PdfPCell.BOX);
                      pgcell.setPadding(4);
                      pgcell.setBorderColor(Color.GRAY);
                      pgcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                      compgTable.addCell(pgcell);
                }
                JSONObject jobjTemplates = new JSONObject(goalstr);
                com.krawler.utils.json.base.JSONArray jarr11 = jobjTemplates.getJSONArray("data");
                JSONObject jobjl = new JSONObject();
                for (int i = 0; i < jarr11.length(); i++) {
                    jobjl = jarr11.getJSONObject(i);
                    for (int k = 0; k < compGoalHeader.length; k++) {
                            pcell = new PdfPCell(new Paragraph(!jobjl.isNull(compGoalDataIndex[k]) ? StringUtil.serverHTMLStripper(jobjl.getString(compGoalDataIndex[k])) : "", fontSmallRegular));
                            pcell.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                            pcell.setBorderColor(Color.GRAY);
                            pcell.setPadding(4);
                            pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            pcell.setVerticalAlignment(Element.ALIGN_CENTER);
                            compgTable.addCell(pcell);
                    }
                }
                document.add(compgTable);

                document.newPage();
                document.close();
            }
            if (!StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
                 String ipaddr = "";
                 if (StringUtil.isNullOrEmpty(request.getHeader("x-real-ip"))) {
                      ipaddr = request.getRemoteAddr();
                  } else {
                      ipaddr = request.getHeader("x-real-ip");
                  }
                  User u = (User) session.get(User.class, request.getParameter("d"));
                  User u1 = (User) session.get(User.class, request.getParameter("userid"));
                  Appraisalcycle appcy = (Appraisalcycle) session.get(Appraisalcycle.class, request.getParameter("appraisalcycid"));
                  //AuditAction action = (AuditAction) session.load(AuditAction.class, AuditAction.Appraisal_Report_Download);
                  String details = "";
                  if (StringUtil.equal(request.getParameter("d"), request.getParameter("userid"))) {
                          details = "User " + u.getFirstName() + " " + u.getLastName() + " has downloaded self" +
                            " appraisal report for appraisal cycle " + appcy.getCyclename() + " through email link";
                  } else {
                         details = "Reviewer " + u.getFirstName() + " " + u.getLastName() + " has downloaded " + u1.getFirstName() + " " + u1.getLastName() + "'s" +
                            " appraisal report for appraisal cycle " + appcy.getCyclename() + " through email link";
                 }
                    tx = session.beginTransaction();
                    //@@ProfileHandler.insertAuditLog(session, action, details, ipaddr, u);
                    tx.commit();
            } else {
                        String details="";
                        User u=null;
                        String appCycleID = request.getParameter("appraisalcycid");
                        Appraisalcycle appcy=(Appraisalcycle) session.get(Appraisalcycle.class,appCycleID);
                        String userID = request.getParameter("userid");
                        if (StringUtil.isNullOrEmpty(userID)) {
                            userID = AuthHandler.getUserid(request);
                            u=(User)session.get(User.class,userID);
                            details="User " + AuthHandler.getFullName(u) + " has downloaded self" +
                            " appraisal report for appraisal cycle " + appcy.getCyclename() + " through Deskera HRMS";
                        }else{
                            u=(User)session.get(User.class,userID);

                           details = "Reviewer " + AuthHandler.getFullName((User)session.get(User.class,AuthHandler.getUserid(request))) + " has downloaded " + AuthHandler.getFullName(u)+"'s" +
                            " appraisal report for appraisal cycle " + appcy.getCyclename() + " through Deskera HRMS";
                        }
                  tx = session.beginTransaction();
                  //@@ProfileHandler.insertAuditLog(session, AuditAction.Appraisal_Report_Download,details,request);
                  tx.commit();
            }
        } catch (DocumentException ex) {
            throw ServiceException.FAILURE("AppraisalDetails.AppraisalDetail", ex);
        } catch (JSONException e){
            throw ServiceException.FAILURE("AppraisalDetails.AppraisalDetail", e);
        }catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }finally{
               writer.close();
        }
        return baos;

    }
    
    private void writeDataToFile(String filename, ByteArrayOutputStream baos,
            HttpServletResponse response) throws IOException {
        try {
            response.setHeader("Content-Disposition", "attachment; filename=\""+ filename +".pdf\"");
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
    
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(AppraisalDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
                processRequest(request, response);
        } catch (ServiceException ex) {
            Logger.getLogger(AppraisalDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
