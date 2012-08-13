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
package com.krawler.spring.hrms.exportreport;

import com.krawler.common.admin.AuditAction;
import com.krawler.common.admin.User;
import com.krawler.common.fontsettings.FontContext;
import com.krawler.common.fontsettings.FontFamilySelector;
import com.krawler.common.fontsettings.FontSetting;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.KrawlerLog;
import com.krawler.common.util.StringUtil;
import com.lowagie.text.DocumentException;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.hrms.performance.Appraisalcycle;
import com.krawler.spring.hrms.anonymousappraisal.hrmsAnonymousAppraisalConstants;
import com.krawler.spring.hrms.common.hrmsCommonDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.servlet.support.RequestContextUtils;


public class exportAppraisalReportPDFDAOImpl implements exportAppraisalReportPDFDAO, MessageSourceAware {

    private HibernateTemplate hibernateTemplate;
    private storageHandlerImpl storageHandlerImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private hrmsCommonDAO hrmsCommonDAOObj;
    private MessageSource messageSource;
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }
    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj1) {
        this.storageHandlerImplObj = storageHandlerImplObj1;
    }
    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    public void setHrmsCommonDAO(hrmsCommonDAO hrmsCommonDAOObj) {
        this.hrmsCommonDAOObj = hrmsCommonDAOObj;
    }

    private static final long serialVersionUID = -763555229410947890L;
    private FontFamilySelector fontFamilySelector = null;

    public exportAppraisalReportPDFDAOImpl(){
    	fontFamilySelector = FontSetting.getFontFamilySelector();
    }
    
    public class EndPage extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Rectangle page = document.getPageSize();
                PdfPTable footer = new PdfPTable(1);
                footer.setWidthPercentage(100);
                footer.setSpacingBefore(20);
                String FootPager = String.valueOf(document.getPageNumber());//current page no
                PdfPCell footerPageNocell = new PdfPCell(new Phrase(fontFamilySelector.process(FootPager, FontContext.SMALL_BOLD_HELVETICA)));
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
            HttpServletResponse response, String str, String goalstr, String quesansstring) throws ServletException, IOException, ServiceException {
        response.setContentType("text/html;charset=UTF-8");
        Session session = HibernateUtil.getCurrentSession();
        try {
            String filename=messageSource.getMessage("hrms.performance.appraisal.report.filename", null, RequestContextUtils.getLocale(request));
            ByteArrayOutputStream baos = null;
            if(!StringUtil.isNullOrEmpty(request.getParameter("filename"))){
                 filename=request.getParameter("filename");
            }
            int flag = 1;

            switch (flag) {
                case 1:
                    baos = AppraisalDetail(request, session, true, str, goalstr, quesansstring);
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


    private ByteArrayOutputStream AppraisalDetail(HttpServletRequest request, Session session, boolean isEmm, String str, String goalstr, String quesansstr)
            throws JSONException, SessionExpiredException, DocumentException, ServiceException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String[] colHeader = { messageSource.getMessage("hrms.common.employee.name", null, RequestContextUtils.getLocale(request)) ,
        		               messageSource.getMessage("hrms.performance.appraisal.cycle.name", null, RequestContextUtils.getLocale(request)) ,
        		               messageSource.getMessage("hrms.performance.appraisal.start.date", null, RequestContextUtils.getLocale(request)) ,
        		               messageSource.getMessage("hrms.performance.appraisal.end.date", null, RequestContextUtils.getLocale(request)) ,
        		               messageSource.getMessage("hrms.performance.total.appraisals", null, RequestContextUtils.getLocale(request)) ,
        		               messageSource.getMessage("hrms.common.Updates.NoofAppraisalsSubmitted", null, RequestContextUtils.getLocale(request)) ,
        		               messageSource.getMessage("hrms.performance.overall.self.comments", null, RequestContextUtils.getLocale(request)) ,
        		               messageSource.getMessage("hrms.performance.overall.appraiser.comments", null, RequestContextUtils.getLocale(request)) ,
        		               messageSource.getMessage("hrms.performance.overall.competency.score", null, RequestContextUtils.getLocale(request)) ,
        		               messageSource.getMessage("hrms.performance.competencies", null, RequestContextUtils.getLocale(request))};
        String[] colHeader1 = { messageSource.getMessage("hrms.common.employee.name", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.performance.appraisal.cycle.name", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.performance.appraisal.start.date", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.performance.appraisal.end.date", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.performance.total.appraisals", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.common.Updates.NoofAppraisalsSubmitted", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.performance.overall.appraiser.comments", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.performance.overall.competency.score", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.performance.competencies", null, RequestContextUtils.getLocale(request))};
        String[] colHeader2 = { messageSource.getMessage("hrms.common.employee.name", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.performance.appraisal.cycle.name", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.performance.appraisal.start.date", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.performance.appraisal.end.date", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.performance.total.appraisals", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.common.Updates.NoofAppraisalsSubmitted", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.performance.overall.competency.score", null, RequestContextUtils.getLocale(request)) ,
        		                messageSource.getMessage("hrms.performance.competencies", null, RequestContextUtils.getLocale(request))};
        String[] dataIndex ={"empname","appcylename","appcylestdate","appcylendate","totalappraisal","appraisalsubmitted","empcomment","mancom","manavgwght",""};
        String[] dataIndex1 ={"empname","appcylename","appcylestdate","appcylendate","totalappraisal","appraisalsubmitted","mancom","manavgwght",""};
        String[] dataIndex2 ={"empname","appcylename","appcylestdate","appcylendate","totalappraisal","appraisalsubmitted","manavgwght",""};
        String[] compHeader={ messageSource.getMessage("hrms.common.name", null, RequestContextUtils.getLocale(request)) ,
        		              messageSource.getMessage("hrms.performance.description", null, RequestContextUtils.getLocale(request)) ,
        		              messageSource.getMessage("hrms.performance.self.appraisal.score", null, RequestContextUtils.getLocale(request)) ,
        		              messageSource.getMessage("hrms.performance.self.appraisal.comment", null, RequestContextUtils.getLocale(request)) ,
        		              messageSource.getMessage("hrms.performance.appraiser.competency.score", null, RequestContextUtils.getLocale(request)) ,
        		              messageSource.getMessage("hrms.performance.appraiser.comments", null, RequestContextUtils.getLocale(request))};
        String[] compDataIndex={"comptename","comptdesc","selfcompscore","selfcomment","compmanwght"};
        String[] compGoalHeader={ messageSource.getMessage("hrms.performance.goals", null, RequestContextUtils.getLocale(request)) ,
        		                  messageSource.getMessage("hrms.common.assigned.by", null, RequestContextUtils.getLocale(request)) ,
        		                  messageSource.getMessage("hrms.performance.appraiser.rating", null, RequestContextUtils.getLocale(request)) ,
        		                  messageSource.getMessage("hrms.performance.appraiser.comment", null, RequestContextUtils.getLocale(request)) ,
        		                  messageSource.getMessage("hrms.performance.self.rating", null, RequestContextUtils.getLocale(request)) ,
        		                  messageSource.getMessage("hrms.performance.self.comments", null, RequestContextUtils.getLocale(request))};
        String[] compGoalDataIndex={"gname","assignedby","gmanrat","mangoalcomment","gemprat","empgoalcomment"};
        String[] quesAnsHeader1={ messageSource.getMessage("hrms.performance.Questions", null, RequestContextUtils.getLocale(request)) ,
        		                  messageSource.getMessage("hrms.performance.appraiser.response", null, RequestContextUtils.getLocale(request))};
        String[] quesAnsHeader={ messageSource.getMessage("hrms.performance.Questions", null, RequestContextUtils.getLocale(request)) ,
        		                 messageSource.getMessage("hrms.performance.appraiser.response", null, RequestContextUtils.getLocale(request)), 
        		                 messageSource.getMessage("hrms.performance.self.response", null, RequestContextUtils.getLocale(request))};
        String[] quesAnsDataIndex={hrmsAnonymousAppraisalConstants.question,hrmsAnonymousAppraisalConstants.answer,hrmsAnonymousAppraisalConstants.employeeanswer};
        String[] quesAnsDataIndex1={hrmsAnonymousAppraisalConstants.question,hrmsAnonymousAppraisalConstants.answer};
        String managerComments = "";
        String scoreAvg="";
        String companyid=null;
        PdfWriter writer=null;
        try {
                String usID = request.getParameter("userid");
                String self = request.getParameter("self");
                Boolean removecolumn = false;
                if(!StringUtil.isNullOrEmpty(self) && self.equals("false")) {
                    quesAnsHeader=quesAnsHeader1;
                    quesAnsDataIndex=quesAnsDataIndex1;
                    colHeader=colHeader1;
                    dataIndex=dataIndex1;
                    removecolumn = true;
                }
                
                if (StringUtil.isNullOrEmpty(usID)) {
                    usID = sessionHandlerImplObj.getUserid(request);
                }
                if (StringUtil.isNullOrEmpty(request.getParameter("pdfEmail")))
                    companyid=sessionHandlerImplObj.getCompanyid(request);
                else {
                    User u = (User) hibernateTemplate.get(User.class, usID);
                    companyid = u.getCompany().getCompanyID();
                }

                HashMap<String, Object> requestParams = new HashMap<String, Object>();
//                companyid  = sessionHandlerImplObj.getCompanyid(request);
                
                requestParams.put("companyid", companyid);
                requestParams.put("checklink", "appraisal");
                if (!hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                    quesAnsHeader=quesAnsHeader1;
                    quesAnsDataIndex=quesAnsDataIndex1;
                    colHeader=colHeader1;
                    removecolumn = true;
                }
                requestParams.clear();
                requestParams.put("companyid", companyid);
                requestParams.put("checklink", "overallcomments");
                if (!hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                   colHeader=colHeader2;
                   dataIndex=dataIndex2;
                }
                
                requestParams.clear();
                Document document = new Document(PageSize.A4.rotate(), 25, 25, 25, 25);
                writer=PdfWriter.getInstance(document, baos);
                writer.setPageEvent(new EndPage());
                document.open();
                java.awt.Color tColor = new Color(9, 9, 9);
                //fontSmallBold.setColor(tColor);
                Paragraph p = new Paragraph();

                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{55,75});

                PdfPTable mainTable = new PdfPTable(1);
                mainTable.setTotalWidth(90);
                mainTable.setWidthPercentage(100);
                mainTable.setSpacingBefore(20);

                PdfPCell headcell = null;
                headcell = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.performance.appraisal.details", null, RequestContextUtils.getLocale(request)), FontContext.REGULAR_BOLD_HELVETICA, tColor)));
                headcell.setBackgroundColor(new Color(0xEEEEEE));
                headcell.setPadding(5);
                mainTable.addCell(headcell);
                document.add(mainTable);

//                String str=hrmsManager.getAppraisalReport(session, request);
                JSONObject jobjTemplate = new JSONObject(str);
                com.krawler.utils.json.base.JSONArray jarr = jobjTemplate.getJSONArray("data");
                com.krawler.utils.json.base.JSONArray jarr2=jarr.getJSONObject(0).getJSONArray("data");
                JSONObject jobjAppraisal = new JSONObject(jarr2.getString(0));
                jarr = jobjAppraisal.getJSONArray("competencies");
                int headlen = colHeader.length;
                if (jarr.length()<1) {
                   headlen = headlen-2;
                }
                
                for(int i=0; i<headlen;i++) {
                    PdfPCell pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(colHeader[i], FontContext.MEDIUM_BOLD_HELVETICA)));
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
                        pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(!dataIndex[i].equals("")?!jobjAppraisal.isNull(dataIndex[i])?jobjAppraisal.getString(dataIndex[i]):"":"", FontContext.SMALL_NORMAL_HELVETICA)));
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
                            JSONArray spl = new JSONArray(jobjAppraisal.getString(dataIndex[i]));
                            String strData="";
                            for(int counter=0;counter<spl.length();counter++) {
                                strData+=spl.getString(counter)+"\n";
                            }
                            pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(strData, FontContext.SMALL_NORMAL_HELVETICA)));
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

                PdfPTable quesansTable = new PdfPTable(quesAnsHeader.length);
                quesansTable.setWidthPercentage(100);
                if(removecolumn) {
                    quesansTable.setWidths(new float[]{40,40});
                }else{
                    quesansTable.setWidths(new float[]{40,40,40});
                }
                quesansTable.setSpacingBefore(20);
                quesansTable.setHeaderRows(1);
                for (int i = 0; i < quesAnsHeader.length; i++) {
                    PdfPCell pgcell = new PdfPCell(new Paragraph(fontFamilySelector.process(quesAnsHeader[i], FontContext.MEDIUM_BOLD_HELVETICA)));
                    pgcell.setBorder(0);
                    pgcell.setBorder(PdfPCell.BOX);
                    pgcell.setPadding(4);
                    pgcell.setBorderColor(Color.GRAY);
                    pgcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    quesansTable.addCell(pgcell);
                }

                JSONObject quesansjobj = new JSONObject(quesansstr);
                JSONArray quesansjarr = quesansjobj.getJSONArray("quesans");
                JSONObject qajobj = new JSONObject();
                for (int i = 0; i < quesansjarr.length(); i++) {
                    qajobj = quesansjarr.getJSONObject(i);
                    for (int k = 0; k < quesAnsHeader.length; k++) {
                        String qatext  = !qajobj.isNull(quesAnsDataIndex[k]) ? qajobj.getString(quesAnsDataIndex[k]) : "";
                        qatext = qatext.replaceAll("~", "\n\n");
                        qatext = qatext.replaceAll("\n", "<br/>");
                        StyleSheet st = new StyleSheet();
                        st.loadTagStyle("body", "face", "HELVETICA");
                        st.loadTagStyle("body", "size", "1");
                        st.loadTagStyle("body", "leading", "8,0");
                        StringReader stringReader = new StringReader(qatext);
                        PdfPCell pcell = new PdfPCell();
                        ArrayList listStr = HTMLWorker.parseToList(stringReader, st);
                                pcell.setPadding(4);
                                for (int htmlCount = 0; htmlCount < listStr.size(); ++htmlCount) {
                                    if(!listStr.get(htmlCount).toString().equals("[]"))
                                        pcell.addElement((Element) listStr.get(htmlCount));
                                }
                        
                        pcell.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                        pcell.setBorderColor(Color.GRAY);
                        pcell.setPadding(4);
                        pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        pcell.setVerticalAlignment(Element.ALIGN_CENTER);
                        quesansTable.addCell(pcell);
                    }
                }
                document.add(quesansTable);

                PdfPTable compTable = new PdfPTable(6);
                compTable.setWidthPercentage(100);
                compTable.setWidths(new float[]{50,60,30,50,42,70});
                compTable.setSpacingBefore(20);
                compTable.setHeaderRows(1);

                for(int i=0;i<compHeader.length;i++){
                        PdfPCell pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(compHeader[i], FontContext.MEDIUM_BOLD_HELVETICA)));
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
                            Chunk chunk1 = fontFamilySelector.processChunk(jobjAppraisal.getString(compDataIndex[k])+"\n\n", FontContext.SMALL_NORMAL_HELVETICA);
                            Chunk chunk2 = null;
                            requestParams.clear();
                            if (!StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
//                                User user=(User)session.get(User.class,request.getParameter("userid"));
//                                companyid=user.getCompany().getCompanyID();
                                requestParams.put("companyid", companyid);
                                requestParams.put("checklink", "modaverage");
                                if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                                    chunk2 = fontFamilySelector.processChunk("["+messageSource.getMessage("hrms.performance.mod.avg", null, RequestContextUtils.getLocale(request))+":  " + scoreAvg + " ]", FontContext.SMALL_NORMAL_HELVETICA);
                                } else {
                                    chunk2 = fontFamilySelector.processChunk("["+messageSource.getMessage("hrms.performance.avg", null, RequestContextUtils.getLocale(request))+":  " + scoreAvg + " ]", FontContext.SMALL_NORMAL_HELVETICA);
                                }
                            } else {
                                requestParams.put("companyid", companyid);
                                requestParams.put("checklink", "modaverage");
                                if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                                    chunk2 = fontFamilySelector.processChunk("["+messageSource.getMessage("hrms.performance.mod.avg", null, RequestContextUtils.getLocale(request))+":  " + scoreAvg + " ]", FontContext.SMALL_NORMAL_HELVETICA);
                                } else {
                                    chunk2 = fontFamilySelector.processChunk("["+messageSource.getMessage("hrms.performance.avg", null, RequestContextUtils.getLocale(request))+":  " + scoreAvg + " ]", FontContext.SMALL_NORMAL_HELVETICA);
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
                                ArrayList<Element> listStr = HTMLWorker.parseToList(stringReader, st);
                                pcell.setPadding(4);
                                for (int htmlCount = 0; htmlCount < listStr.size(); ++htmlCount) {
                                    if(!listStr.get(htmlCount).getChunks().isEmpty()){
                                    	pcell.addElement(fontFamilySelector.processElement(listStr.get(htmlCount).getChunks(), FontContext.SMALL_NORMAL_HELVETICA));
                                    }
                                }
                            } else
                                pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(!jobjAppraisal.isNull(compDataIndex[k]) ? StringUtil.serverHTMLStripper(jobjAppraisal.getString(compDataIndex[k])) : "", FontContext.SMALL_NORMAL_HELVETICA)));
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
                        PdfPCell pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(managerComments, FontContext.SMALL_NORMAL_HELVETICA)));
                        pcell.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                        pcell.setPadding(4);
                        pcell.setBorderColor(Color.GRAY);
                        pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        pcell.setVerticalAlignment(Element.ALIGN_LEFT);
                        compTable.addCell(pcell);
                    }
                }
            }
            document.add(compTable);
            PdfPCell pcell;
             if (jarr.length()>0) {
                PdfPTable helpTable = new PdfPTable(1);
                helpTable.setTotalWidth(90);
                helpTable.setWidthPercentage(100);
                helpTable.setSpacingBefore(20);


                pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(messageSource.getMessage("hrms.performance.mod.avg", null, RequestContextUtils.getLocale(request))+". : "+ messageSource.getMessage("hrms.performance.mode.average.ratings", null, RequestContextUtils.getLocale(request)), FontContext.SMALL_BOLD_HELVETICA)));
                pcell.setBorder(0);
                pcell.setPadding(4);
                pcell.setHorizontalAlignment(Element.ALIGN_LEFT);
                helpTable.addCell(pcell);


                requestParams.clear();
                requestParams.put("companyid", companyid);
                requestParams.put("checklink", "modaverage");
                if (!StringUtil.isNullOrEmpty(request.getParameter("pdfEmail"))) {
                    if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                         document.add(helpTable);
                     }
                }
                else{
                    if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                     document.add(helpTable);
                 }
                }
             }
            PdfPTable compgTable = new PdfPTable(6);
                compgTable.setWidthPercentage(100);
                compgTable.setWidths(new float[]{50,60,30,50,42,70});
                compgTable.setSpacingBefore(20);
                compgTable.setHeaderRows(1);
            for(int i=0;i<compGoalHeader.length;i++){
                        PdfPCell pgcell = new PdfPCell(new Paragraph(fontFamilySelector.process(compGoalHeader[i], FontContext.MEDIUM_BOLD_HELVETICA)));
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
                for (int k = 0; k < compHeader.length; k++) {
                        pcell = new PdfPCell(new Paragraph(fontFamilySelector.process(!jobjl.isNull(compGoalDataIndex[k]) ? StringUtil.serverHTMLStripper(jobjl.getString(compGoalDataIndex[k])) : "", FontContext.SMALL_NORMAL_HELVETICA)));
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
        } catch (DocumentException ex) {
        	ex.printStackTrace();
            throw ServiceException.FAILURE("AppraisalDetails.AppraisalDetail", ex);
        } catch (JSONException e){
        	e.printStackTrace();
            throw ServiceException.FAILURE("AppraisalDetails.AppraisalDetail", e);
        }catch (Exception ex) {
        	ex.printStackTrace();
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

    protected void printReport(HttpServletRequest request ,HttpServletResponse response, String desc, String goals, String quesstr){
    	HashMap<String, Object> requestParams = new HashMap<String, Object>();
    	String space = "<span style='padding-left:20px'></span>";
    	String ashtmlString = "<html> " +
    	"<head><style type=\"text/css\">@media print {button#print {display: none;}}</style><title>"+messageSource.getMessage("hrms.Dashboard.AppraisalReport", null, RequestContextUtils.getLocale(request))+"</title></head>";        	
    	ashtmlString +="<body style = \"font-family: Tahoma, Verdana, Arial, Helvetica, sans-sarif;\"><center>";
    	ashtmlString +="<h2 align='center'>"+messageSource.getMessage("hrms.performance.appraisal.details", null, RequestContextUtils.getLocale(request))+"</h2></br></br>";
    	try {
    		String companyid = sessionHandlerImplObj.getCompanyid(request);
    		
			JSONObject jsonDesc = new JSONObject(desc);
	    	JSONObject jsonCompOrQues = new JSONObject(quesstr);
	    	JSONObject jsonGoals = new JSONObject(goals);
	    	
	    	JSONArray descArr = jsonDesc.getJSONArray("data");
	    	JSONObject descArr1 = descArr.getJSONObject(0);
	    	JSONArray data = new JSONArray(descArr1.getString("data"));
	    	JSONObject data1 = data.getJSONObject(0);	
	    	
	    	JSONArray quesArr = jsonCompOrQues.getJSONArray("quesans");	
	    	
	    	JSONArray goalArr = jsonGoals.getJSONArray("data");
	    	
	    	String empname = data1.has("empname")?data1.getString("empname"):space;
	    	String designation = data1.has("designation")?data1.getString("designation"):space;
	    	String reviewercomment = data1.has("reviewercomment")?data1.getString("reviewercomment"):space;
	    	String appcylendate = data1.has("appcylendate")?data1.getString("appcylendate"):space;
	    	String appcylestdate = data1.has("appcylestdate")?data1.getString("appcylestdate"):space;
	    	String mancom = data1.has("mancom")?data1.getString("mancom"):space;
	    	String appcylename = data1.has("appcylename")?data1.getString("appcylename"):space;
	    	String compempavgwght = data1.has("compempavgwght")?data1.getString("compempavgwght"):space;
	    	String dept = data1.has("dept")?data1.getString("dept"):space;
	    	String totalappraisal = data1.has("totalappraisal")?data1.getString("totalappraisal"):space;
	    	String appraisalsubmitted = data1.has("appraisalsubmitted")?data1.getString("appraisalsubmitted"):space;
	    	String empcomment = data1.has("empcomment")?data1.getString("empcomment"):space;
	    	String manavgwght = data1.has("manavgwght")?data1.getString("manavgwght"):space;
	    	
	    	ashtmlString +="<table cellspacing=0 border=0 cellpadding=2 width='100%'>";
        	ashtmlString +="<tr>";
        	ashtmlString +="<td style='white:space:pre-line'>";
	    	ashtmlString +=messageSource.getMessage("hrms.common.employee.name", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
	    	ashtmlString +="</td>";
        	ashtmlString +="<td style='white:space:pre-line'>";
	    	ashtmlString +=(empname!=null?empname:"")+"</br></br>";
	    	ashtmlString +="</td>";
        	ashtmlString +="</tr>";
	    	
        	ashtmlString +="<tr>";
        	ashtmlString +="<td style='white:space:pre-line'>";
	    	ashtmlString +=messageSource.getMessage("hrms.performance.appraisal.cycle.name", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
	    	ashtmlString +="</td>";
        	ashtmlString +="<td style='white:space:pre-line'>";
	    	ashtmlString +=(appcylename!=null?appcylename:"")+"</br></br>";
	    	ashtmlString +="</td>";
        	ashtmlString +="</tr>";
	    	
        	ashtmlString +="<tr>";
        	ashtmlString +="<td style='white:space:pre-line'>";
	    	ashtmlString +=messageSource.getMessage("hrms.performance.appraisal.start.date", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
	    	ashtmlString +="</td>";
        	ashtmlString +="<td style='white:space:pre-line'>";
	    	ashtmlString +=(appcylestdate!=null?appcylestdate:"")+"</br></br>";
	    	ashtmlString +="</td>";
        	ashtmlString +="</tr>";
	    	
        	ashtmlString +="<tr>";
        	ashtmlString +="<td style='white:space:pre-line'>";
	    	ashtmlString +=messageSource.getMessage("hrms.performance.appraisal.end.date", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
	    	ashtmlString +="</td>";
        	ashtmlString +="<td style='white:space:pre-line'>";
	    	ashtmlString +=(appcylendate!=null?appcylendate:"")+"</br></br>";
	    	ashtmlString +="</td>";
        	ashtmlString +="</tr>";
	    	
        	ashtmlString +="<tr>";
        	ashtmlString +="<td style='white:space:pre-line'>";
	    	ashtmlString +=messageSource.getMessage("hrms.performance.total.appraisals", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
	    	ashtmlString +="</td>";
        	ashtmlString +="<td style='white:space:pre-line'>";
	    	ashtmlString +=(totalappraisal!=null?totalappraisal:"")+"</br></br>";
	    	ashtmlString +="</td>";
        	ashtmlString +="</tr>";
	    	
        	ashtmlString +="<tr>";
        	ashtmlString +="<td style='white:space:pre-line'>";
	    	ashtmlString +=messageSource.getMessage("hrms.common.Updates.NoofAppraisalsSubmitted", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
	    	ashtmlString +="</td>";
        	ashtmlString +="<td style='white:space:pre-line'>";
	    	ashtmlString +=(appraisalsubmitted!=null?appraisalsubmitted:"")+"</br></br>";
	    	ashtmlString +="</td>";
        	ashtmlString +="</tr>";
     
        	requestParams.clear();
        	requestParams.put("companyid", companyid);
            requestParams.put("checklink", "overallcomments");
        	if(hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
        		ashtmlString +="<tr>";
            	ashtmlString +="<td style='white:space:pre-line'>";
    	    	ashtmlString +=messageSource.getMessage("hrms.performance.overall.self.comments", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
    	    	ashtmlString +="</td>";
            	ashtmlString +="<td style='white:space:pre-line'>";
    	    	ashtmlString +=(empcomment!=null?empcomment:"")+"</br></br>";
    	    	ashtmlString +="</td>";
            	ashtmlString +="</tr>";
    	    	
            	ashtmlString +="<tr>";
            	ashtmlString +="<td style='white:space:pre-line'>";
    	    	ashtmlString +=messageSource.getMessage("hrms.performance.overall.appraiser.comments", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
    	    	ashtmlString +="</td>";
            	ashtmlString +="<td style='white:space:pre-line'>";
            	JSONArray mancomJsonArr = new JSONArray(mancom);
            	for(int i=0;i<mancomJsonArr.length();i++)
            		ashtmlString +=mancomJsonArr.getString(i)+"</br>";
    	    	ashtmlString +="</br>";
    	    	ashtmlString +="</td>";
            	ashtmlString +="</tr>";
        	}
	    	
        	requestParams.clear();
        	requestParams.put("companyid", companyid);
            requestParams.put("checklink", "competency");
        	if(quesArr.length()==0){
            	ashtmlString +="<tr>";
            	ashtmlString +="<td style='white:space:pre-line'>";
    	    	ashtmlString +=messageSource.getMessage("hrms.performance.overall.competency.score", null, RequestContextUtils.getLocale(request))+":  "+"</br></br>";
    	    	ashtmlString +="</td>";
            	ashtmlString +="<td style='white:space:pre-line'>";
            	ashtmlString +=manavgwght+"</br>";
    	    	ashtmlString +="</br>";
    	    	ashtmlString +="</td>";
            	ashtmlString +="</tr>";
            }
            ashtmlString +="</table>";
	    	ashtmlString+="</br></br></br></br>";
            
	    	requestParams.clear();
        	requestParams.put("companyid", companyid);
            requestParams.put("checklink", "competency");
        	if(hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
        		String avg = "";
        		requestParams.clear();
            	requestParams.put("companyid", companyid);
                requestParams.put("checklink", "modaverage");
        		if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
                    avg = messageSource.getMessage("hrms.performance.mod.avg", null, RequestContextUtils.getLocale(request))+": ";
                } else {
                    avg = messageSource.getMessage("hrms.performance.avg", null, RequestContextUtils.getLocale(request))+": ";
                }
        		JSONArray jsonComp = data1.getJSONArray("competencies");
    	    	if(jsonComp.length()!=0){
    	        	ashtmlString+="<table style='page-break-after:auto' cellspacing=0 border=1 cellpadding=2 width='100%'>";
    	        	ashtmlString+="<tr><th>"+messageSource.getMessage("hrms.EmailTemplateCmb.Name", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.description", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.self.appraisal.score", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.self.appraisal.comment", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.appraiser.competency.score", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.appraiser.comment", null, RequestContextUtils.getLocale(request))+"</th></tr>";
    	        	for(int i=0; i<jsonComp.length();i++){
    	        		ashtmlString+="<tr>";
    					ashtmlString+="<td style='white:space:pre-line'>"+(jsonComp.getJSONObject(i).has("comptename")?(jsonComp.getJSONObject(i).getString("comptename").equals("")?space:jsonComp.getJSONObject(i).getString("comptename")):space)+"</td>";
    					ashtmlString+="<td style='white:space:pre-line'>"+(jsonComp.getJSONObject(i).has("comptdesc")?(jsonComp.getJSONObject(i).getString("comptdesc").equals("")?space:jsonComp.getJSONObject(i).getString("comptdesc")):space)+"</td>";
    					ashtmlString+="<td style='white:space:pre-line'><center>"+(jsonComp.getJSONObject(i).has("selfcompscore")?(jsonComp.getJSONObject(i).getString("selfcompscore").equals("")?space:jsonComp.getJSONObject(i).getString("selfcompscore")):space)+"</center></td>";
    					ashtmlString+="<td style='white:space:pre-line'>"+(jsonComp.getJSONObject(i).has("selfcomment")?(jsonComp.getJSONObject(i).getString("selfcomment").equals("")?space:jsonComp.getJSONObject(i).getString("selfcomment")):space)+"</td>";
    					ashtmlString+="<td style='white:space:pre-line'><center>"+(jsonComp.getJSONObject(i).has("compmanwght")?(jsonComp.getJSONObject(i).getString("compmanwght").equals("")?space:jsonComp.getJSONObject(i).getString("compmanwght")):space);
    					ashtmlString+="<style='white:space:pre-line'></br><b>["+avg+(jsonComp.getJSONObject(i).has("nominalRat")?(jsonComp.getJSONObject(i).getString("nominalRat").equals("")?space:jsonComp.getJSONObject(i).getString("nominalRat")):space)+"]</b></center></td>";
    					try{
    						String commentStr="";
    						if(jsonComp.getJSONObject(i).has("comments")){
    							JSONArray comment = jsonComp.getJSONObject(i).getJSONArray("comments");
    							for(int j=0;j<comment.length()&&comment.getJSONObject(j).has("managercomment");j++){	
    								commentStr += (("<b>"+(j+1)+") </b>")+comment.getJSONObject(j).getString("managercomment")+"</br>");
    							}
    						}
    						ashtmlString+="<td style='white:space:pre-line'>"+(commentStr.equals("")?space:commentStr)+"</td>";
    					}catch(Exception e){
    						ashtmlString+="<td>"+space+"</td>";
    						e.printStackTrace();
    					}
    					ashtmlString+="</tr>";
    	        	}
    	        	ashtmlString+="</table>";
    	        	
    	        	requestParams.clear();
                	requestParams.put("companyid", companyid);
                    requestParams.put("checklink", "modaverage");
            		if (hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()) {
            			ashtmlString+="<h4>"+messageSource.getMessage("hrms.performance.mod.avg", null, RequestContextUtils.getLocale(request))+". : "+messageSource.getMessage("hrms.performance.mode.average.ratings", null, RequestContextUtils.getLocale(request))+"</h4>";
            		}
    	        	ashtmlString+="</br></br></br></br>";
    	    	}
    	    	
    	    	if(jsonComp.length()==0 && quesArr.length()!=0){
    	    		ashtmlString+="<table style='page-break-after:auto' cellspacing=0 border=1 cellpadding=2 width='100%'>";
    	    		ashtmlString+="<tr><th>"+messageSource.getMessage("hrms.performance.Questions", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.appraiser.response", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.self.response", null, RequestContextUtils.getLocale(request))+"</th></tr>";
    	    		for(int i=0; i<quesArr.length();i++){
    	    			ashtmlString+="<tr>";
    	    			ashtmlString+="<td style='white:space:pre-line'>"+(quesArr.getJSONObject(i).has("question")?(quesArr.getJSONObject(i).getString("question").equals("")?space:quesArr.getJSONObject(i).getString("question")):space)+"</td>";
    	    			ashtmlString+="<td style='white:space:pre-line'>"+(quesArr.getJSONObject(i).has("answer")?(quesArr.getJSONObject(i).getString("answer").equals("")?space:quesArr.getJSONObject(i).getString("answer")):space)+"</td>";
    	    			ashtmlString+="<td style='white:space:pre-line'>"+(quesArr.getJSONObject(i).has("employeeanswer")?(quesArr.getJSONObject(i).getString("employeeanswer").equals("")?space:quesArr.getJSONObject(i).getString("employeeanswer")):space)+"</td>";
    	    			ashtmlString+="</tr>";
    	    		}
    	    		ashtmlString+="</table>";
    	    		ashtmlString+="</br></br></br></br>";
    	    	}
        	}
	    	
        	requestParams.clear();
        	requestParams.put("companyid", companyid);
            requestParams.put("checklink", "goal");
        	if(hrmsCommonDAOObj.checkModule(requestParams).isSuccessFlag()){
        		if(goalArr.length()>0){
    	    		ashtmlString+="<table style='page-break-after:auto' cellspacing=0 border=1 cellpadding=2 width='100%'>";
    	    		ashtmlString+="<tr><th>"+messageSource.getMessage("hrms.performance.goals", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.common.assigned.by", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.appraiser.rating", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.appraiser.comment", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.self.rating", null, RequestContextUtils.getLocale(request))+"</th><th>"+messageSource.getMessage("hrms.performance.self.comments", null, RequestContextUtils.getLocale(request))+"</th></tr>";
    	    		for(int i=0; i<goalArr.length();i++){
    	    			ashtmlString+="<tr>";
    	    			ashtmlString+="<td style='white:space:pre-line' align='center'>"+(goalArr.getJSONObject(i).has("gname")?(goalArr.getJSONObject(i).getString("gname").equals("")?space:goalArr.getJSONObject(i).getString("gname")):space)+"</td>";
    	    			ashtmlString+="<td style='white:space:pre-line' align='center'>"+(goalArr.getJSONObject(i).has("assignedby")?(goalArr.getJSONObject(i).getString("assignedby").equals("")?space:goalArr.getJSONObject(i).getString("assignedby")):space)+"</td>";
    	    			ashtmlString+="<td style='white:space:pre-line' align='center'>"+(goalArr.getJSONObject(i).has("gmanrat")?(goalArr.getJSONObject(i).getString("gmanrat").equals("")?space:goalArr.getJSONObject(i).getString("gmanrat")):space)+"</td>";
    	    			ashtmlString+="<td style='white:space:pre-line' align='center'>"+(goalArr.getJSONObject(i).has("mangoalcomment")?(goalArr.getJSONObject(i).getString("mangoalcomment").equals("")?space:goalArr.getJSONObject(i).getString("mangoalcomment")):space)+"</td>";
    	    			ashtmlString+="<td style='white:space:pre-line' align='center'>"+(goalArr.getJSONObject(i).has("gemprat")?((goalArr.getJSONObject(i).getString("gemprat").equals("")||goalArr.getJSONObject(i).getString("gemprat").equals("0"))?space:goalArr.getJSONObject(i).getString("gemprat")):space)+"</td>";
    	    			ashtmlString+="<td style='white:space:pre-line' align='center'>"+(goalArr.getJSONObject(i).has("empgoalcomment")?(goalArr.getJSONObject(i).getString("empgoalcomment").equals("")?space:goalArr.getJSONObject(i).getString("empgoalcomment")):space)+"</td>";
    	    			ashtmlString+="</tr>";
    	    		}
    	    		ashtmlString+="</table>";
    	    	}
        	}
        	ashtmlString +="</center>";
        	ashtmlString +="<div style='float: left; padding-top: 3px; padding-right: 5px;'>" + "<button id = 'print' title='Print' onclick='window.print();' style='color: rgb(8, 55, 114);' href='#'>"+messageSource.getMessage("hrms.common.print", null, RequestContextUtils.getLocale(request))+"</button>" +"</div>" ;
			ashtmlString +="</body>" +
        	"</html>";
	    	response.setContentType("text/html");
	    	response.setContentLength(ashtmlString.length());
	    	response.getOutputStream().write(ashtmlString.getBytes());
	    	response.getOutputStream().flush();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SessionExpiredException e) {
			e.printStackTrace();	
		}
    }
    
    @Override
    public User getUser(String userid){
    	User user = null;
    	try{
    		List<User> list = HibernateUtil.executeQuery("from User where userid=?", userid);
    		for(User u: list){
    			user = u;
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return user;
    }
    
    @Override
    public Appraisalcycle getAppraisalCycle(String id){
    	Appraisalcycle appraisalcycle = null;
    	try{
    		List<Appraisalcycle> list = HibernateUtil.executeQuery("from Appraisalcycle where id=?", id);
    		for(Appraisalcycle cycle: list){
    			appraisalcycle = cycle;
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return appraisalcycle;
    }
    
    @Override
    public AuditAction getAuditAction(String id){
    	AuditAction auditAction = null;
    	try{
    		List<AuditAction> list = HibernateUtil.executeQuery("from AuditAction where ID=?", id);
    		for(AuditAction action: list){
    			auditAction = action;
        	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return auditAction;
    }
    
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
}
