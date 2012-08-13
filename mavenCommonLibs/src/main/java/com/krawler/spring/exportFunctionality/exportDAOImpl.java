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
package com.krawler.spring.exportFunctionality;

import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.fontsettings.FontContext;
import com.krawler.common.fontsettings.FontFamilySelector;
import com.krawler.common.fontsettings.FontSetting;
import java.net.URLDecoder;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.text.DecimalFormat;
import java.util.Date;
import org.apache.commons.lang.StringEscapeUtils;
import org.hibernate.SessionFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.servlet.support.RequestContextUtils;

public class exportDAOImpl implements MessageSourceAware{

    private HibernateTemplate hibernateTemplate;
    private storageHandlerImpl storageHandlerImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
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
    
    @Override
    public void setMessageSource(MessageSource ms) {
        this.messageSource = ms;
    }
    
    private FontFamilySelector fontFamilySelector = null;
    
    private static String imgPath = "";
    private static String companyName = "";
    private static com.krawler.utils.json.base.JSONObject config = null;
    private PdfPTable header = null;
    private PdfPTable footer = null;
    private static final long serialVersionUID = -8401651817881523209L;
    static SimpleDateFormat df = new SimpleDateFormat("yyyy-M-dd");
    
    private static final String defaultCompanyImgPath = "images/logo.gif";

    
    public exportDAOImpl(){
    	fontFamilySelector = FontSetting.getFontFamilySelector();
    }
    
    public class EndPage extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                Rectangle page = document.getPageSize();
                try {
                    getHeaderFooter(document);
                } catch (ServiceException ex) {
                    Logger.getLogger(exportDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
                // Add page header
                header.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                header.writeSelectedRows(0, -1, document.leftMargin(), page.getHeight() - 10, writer.getDirectContent());

                // Add page footer
                footer.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin() - 5, writer.getDirectContent());

                // Add page border
                if (config.getBoolean("pageBorder")) {
                    int bmargin = 8;  //border margin
                    PdfContentByte cb = writer.getDirectContent();
                    cb.rectangle(bmargin, bmargin, page.getWidth() - bmargin * 2, page.getHeight() - bmargin * 2);
                    cb.setColorStroke(Color.LIGHT_GRAY);
                    cb.stroke();
                }

            } catch (JSONException e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response, JSONObject jobj) throws ServiceException {
        ByteArrayOutputStream baos = null;
        String filename = request.getParameter("name");
        String fileType = null;
        JSONObject grid = null;
        JSONArray gridmap = null;
        String [] status = {messageSource.getMessage("hrms.recruitment.open", null, "Open", RequestContextUtils.getLocale(request)), 
        		            "",
        		            messageSource.getMessage("hrms.recruitment.expired", null, "Expired", RequestContextUtils.getLocale(request)),
        		            messageSource.getMessage("hrms.recruitment.filled", null, "Filled", RequestContextUtils.getLocale(request))};
        try {	
        	try{
        		if(jobj.getJSONArray("data").length()>0 && jobj.getJSONArray("data").getJSONObject(0).has("positionstatus")){
        			for(int i=0;i<jobj.getJSONArray("data").length();i++){	
        				jobj.getJSONArray("data").getJSONObject(i).put("positionstatus", status[Integer.parseInt(jobj.getJSONArray("data").getJSONObject(i).getString("positionstatus"))]);
        			}
        		}
        		
        		if(jobj.getJSONArray("data").length()>0 && jobj.getJSONArray("data").getJSONObject(0).has("status")){
        			for(int i=0;i<jobj.getJSONArray("data").length();i++){	
        				jobj.getJSONArray("data").getJSONObject(i).put("status", messageSource.getMessage("hrms.recruitment.status."+StringUtil.mergeString(jobj.getJSONArray("data").getJSONObject(i).getString("status")), null, jobj.getJSONArray("data").getJSONObject(i).getString("status"), RequestContextUtils.getLocale(request)));
        			}
        		}
        	}catch(JSONException e){
        		e.printStackTrace();
        	}catch (NumberFormatException e) {
        		e.printStackTrace();
			}
            fileType = request.getParameter("filetype");
            if (request.getParameter("gridconfig") != null) {
                grid = new JSONObject(request.getParameter("gridconfig"));
                gridmap = grid.getJSONArray("data");
            }
            if (StringUtil.equal(fileType, "csv")) {
                createCsvFile(request, response, jobj);
            } else if (StringUtil.equal(fileType, "pdf")) {
                baos = getPdfData(gridmap, request, jobj);
                writeDataToFile(filename, fileType, baos, response);
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
    }

    public void writeDataToFile(String filename, String fileType, ByteArrayOutputStream baos, HttpServletResponse response) throws ServiceException {
        try {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "." + fileType + "\"");
            response.setContentType("application/octet-stream");
            response.setContentLength(baos.size());
            response.getOutputStream().write(baos.toByteArray());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e) {
            try {
                response.getOutputStream().println("{\"valid\": false}");
            } catch (IOException ex) {
                Logger.getLogger(exportDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void addComponyLogo(Document d, HttpServletRequest request) throws ServiceException {
        try {
            PdfPTable table = new PdfPTable(1);
                imgPath = getImgPath(request);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.setWidthPercentage(50);
            PdfPCell cell = null;
            try {
                Image img = Image.getInstance(imgPath);
                cell = new PdfPCell(img);
            } catch (Exception e) {
                companyName = sessionHandlerImplObj.getCompanyName(request);
                cell = new PdfPCell(new Paragraph(fontFamilySelector.process(companyName, FontContext.BIG_NORMAL_HELVETICA)));
            }
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            d.add(table);
        } catch (Exception e) {
            throw ServiceException.FAILURE("exportDAOImpl.addComponyLogo", e);
        }
    }

    public String getImgPath(HttpServletRequest req) throws SessionExpiredException {
        String requestedFileName = "";
        String fileName ="";
        String companyId = null;
        try {
            companyId = sessionHandlerImplObj.getCompanyid(req);
        } catch (Exception ee) {
        }
        if(StringUtil.isStandAlone()){
            fileName = URLUtil.getPageURL(req, "").concat(defaultCompanyImgPath);
        } else {
            if (StringUtil.isNullOrEmpty(companyId)) {
                String domain = URLUtil.getDomainName(req);
                if (!StringUtil.isNullOrEmpty(domain)) {
                    companyId = sessionHandlerImplObj.getCompanyid(req);
                    requestedFileName = "/original_" + companyId + ".png";
                } else {
                    requestedFileName = "logo.gif";
                }
            } else {
                requestedFileName = companyId + ".png";
            }
            fileName = storageHandlerImplObj.GetProfileImgStorePath() + requestedFileName;
        }
        
        
        return fileName;
    }

    public void addTitleSubtitle(Document d) throws ServiceException {
        try {
            java.awt.Color tColor = new Color(Integer.parseInt(config.getString("textColor"), 16));
            /*fontBold.setColor(tColor);
            fontRegular.setColor(tColor);
            fontSmallRegular.setColor(tColor);*/
            PdfPTable table = new PdfPTable(1);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.setWidthPercentage(100);
            table.setSpacingBefore(6);

            //Report Title
            PdfPCell cell = new PdfPCell(new Paragraph(fontFamilySelector.process(config.getString("title"), FontContext.REGULAR_BOLD_HELVETICA, tColor)));
            cell.setBorder(0);
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            //Report Subtitle(s)
            String[] SubTitles = config.getString("subtitles").split("~");// '~' as separator
            for (int i = 0; i < SubTitles.length; i++) {
                cell = new PdfPCell(new Paragraph(fontFamilySelector.process(SubTitles[i], FontContext.SMALL_NORMAL_HELVETICA, tColor)));
                cell.setBorder(0);
                cell.setBorderWidth(0);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
            table.setSpacingAfter(6);
            d.add(table);

            //Separator line
            PdfPTable line = new PdfPTable(1);
            line.setWidthPercentage(100);
            PdfPCell cell1 = null;
            cell1 = new PdfPCell(new Paragraph(""));
            cell1.setBorder(PdfPCell.BOTTOM);
            line.addCell(cell1);
            d.add(line);
        } catch (Exception e) {
            throw ServiceException.FAILURE("exportDAOImpl.addTitleSubtitle", e);
        }
    }

    public int addTable(int stcol, int stpcol, int strow, int stprow, JSONArray store, String[] colwidth2, String[] colHeader, String[] widths, String[] align, Document document, HttpServletRequest request) throws ServiceException {
        try {
            java.awt.Color tColor = new Color(Integer.parseInt(config.getString("textColor"), 16));
            //fontSmallBold.setColor(tColor);
            PdfPTable table;
            float[] tcol;
            tcol = new float[colHeader.length + 1];
            tcol[0] = 40;
            for (int i = 1; i < colHeader.length + 1; i++) {
                tcol[i] = Float.parseFloat(widths[i - 1]);
            }
            table = new PdfPTable(colHeader.length + 1);
            table.setWidthPercentage(tcol, document.getPageSize());

            table.setSpacingBefore(15);
            PdfPCell h2 = new PdfPCell(new Paragraph(fontFamilySelector.process("No.", FontContext.SMALL_BOLD_HELVETICA, tColor)));
            if (config.getBoolean("gridBorder")) {
                h2.setBorder(PdfPCell.BOX);
            } else {
                h2.setBorder(0);
            }
            h2.setPadding(4);
            h2.setBorderColor(Color.GRAY);
            h2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(h2);
            PdfPCell h1 = null;
            for (int hcol = stcol; hcol < colwidth2.length; hcol++) {
                h1 = new PdfPCell(new Paragraph(fontFamilySelector.process(colHeader[hcol], FontContext.SMALL_BOLD_HELVETICA, tColor)));
                h1.setHorizontalAlignment(Element.ALIGN_CENTER);
                if (config.getBoolean("gridBorder")) {
                    h1.setBorder(PdfPCell.BOX);
                } else {
                    h1.setBorder(0);
                }
                h1.setBorderColor(Color.GRAY);
                h1.setPadding(4);
                table.addCell(h1);
            }
            table.setHeaderRows(1);

            for (int row = strow; row < stprow; row++) {
                h2 = new PdfPCell(new Paragraph(fontFamilySelector.process(String.valueOf(row + 1), FontContext.SMALL_NORMAL_HELVETICA, tColor)));
                if (config.getBoolean("gridBorder")) {
                    h2.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                } else {
                    h2.setBorder(0);
                }
                h2.setPadding(4);
                h2.setBorderColor(Color.GRAY);
                h2.setHorizontalAlignment(Element.ALIGN_CENTER);
                h2.setVerticalAlignment(Element.ALIGN_CENTER);
                table.addCell(h2);

                JSONObject temp = store.getJSONObject(row);
                for (int col = 0; col < colwidth2.length; col++) {
                    Paragraph para = new Paragraph(fontFamilySelector.process(temp.getString(colwidth2[col]), FontContext.SMALL_NORMAL_HELVETICA, tColor));
                    h1 = new PdfPCell(para);
                    if (config.getBoolean("gridBorder")) {
                        h1.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                    } else {
                        h1.setBorder(0);
                    }
                    h1.setPadding(4);
                    h1.setBorderColor(Color.GRAY);
                    if (!align[col].equals("right") && !align[col].equals("left")) {
                        h1.setHorizontalAlignment(Element.ALIGN_CENTER);
                        h1.setVerticalAlignment(Element.ALIGN_CENTER);
                    } else if (align[col].equals("right")) {
                        h1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        h1.setVerticalAlignment(Element.ALIGN_RIGHT);
                    } else if (align[col].equals("left")) {
                        h1.setHorizontalAlignment(Element.ALIGN_LEFT);
                        h1.setVerticalAlignment(Element.ALIGN_LEFT);
                    }
                    table.addCell(h1);
                }
            }
            document.add(table);
            document.newPage();
        } catch (Exception e) {
            throw ServiceException.FAILURE("exportDAOImpl.addTable", e);
        }
        return stpcol;
    }

    public void getHeaderFooter(Document document) throws ServiceException {
        try {
            java.awt.Color tColor = new Color(Integer.parseInt(config.getString("textColor"), 16));
            //fontSmallRegular.setColor(tColor);
            java.util.Date dt = new java.util.Date();
            String date = "yyyy-MM-dd";
            java.text.SimpleDateFormat dtf = new java.text.SimpleDateFormat(date);
            String DateStr = dtf.format(dt);

            // -------- header ----------------
            header = new PdfPTable(3);
            String HeadDate = "";
            if (config.getBoolean("headDate")) {
                HeadDate = DateStr;
            }
            PdfPCell headerDateCell = new PdfPCell(new Phrase(fontFamilySelector.process(HeadDate, FontContext.SMALL_NORMAL_HELVETICA, tColor)));
            headerDateCell.setBorder(0);
            headerDateCell.setPaddingBottom(4);
            header.addCell(headerDateCell);

            PdfPCell headerNotecell = new PdfPCell(new Phrase(fontFamilySelector.process(config.getString("headNote"), FontContext.SMALL_NORMAL_HELVETICA, tColor)));
            headerNotecell.setBorder(0);
            headerNotecell.setPaddingBottom(4);
            headerNotecell.setHorizontalAlignment(PdfCell.ALIGN_CENTER);
            header.addCell(headerNotecell);

            String HeadPager = "";
            if (config.getBoolean("headPager")) {
                HeadPager = String.valueOf(document.getPageNumber());//current page no
            }
            PdfPCell headerPageNocell = new PdfPCell(new Phrase(fontFamilySelector.process(HeadPager, FontContext.SMALL_NORMAL_HELVETICA, tColor)));
            headerPageNocell.setBorder(0);
            headerPageNocell.setPaddingBottom(4);
            headerPageNocell.setHorizontalAlignment(PdfCell.ALIGN_RIGHT);
            header.addCell(headerPageNocell);

            PdfPCell headerSeparator = new PdfPCell(new Phrase(""));
            headerSeparator.setBorder(PdfPCell.BOX);
            headerSeparator.setPadding(0);
            headerSeparator.setColspan(3);
            header.addCell(headerSeparator);
            // -------- header end ----------------

            // -------- footer  -------------------
            footer = new PdfPTable(3);
            PdfPCell footerSeparator = new PdfPCell(new Phrase(""));
            footerSeparator.setBorder(PdfPCell.BOX);
            footerSeparator.setPadding(0);
            footerSeparator.setColspan(3);
            footer.addCell(footerSeparator);

            String PageDate = "";
            if (config.getBoolean("footDate")) {
                PageDate = DateStr;
            }
            PdfPCell pagerDateCell = new PdfPCell(new Phrase(fontFamilySelector.process(PageDate, FontContext.SMALL_NORMAL_HELVETICA, tColor)));
            pagerDateCell.setBorder(0);
            footer.addCell(pagerDateCell);

            PdfPCell footerNotecell = new PdfPCell(new Phrase(fontFamilySelector.process(config.getString("footNote"), FontContext.SMALL_NORMAL_HELVETICA, tColor)));
            footerNotecell.setBorder(0);
            footerNotecell.setHorizontalAlignment(PdfCell.ALIGN_CENTER);
            footer.addCell(footerNotecell);

            String FootPager = "";
            if (config.getBoolean("footPager")) {
                FootPager = String.valueOf(document.getPageNumber());//current page no
            }
            PdfPCell footerPageNocell = new PdfPCell(new Phrase(fontFamilySelector.process(FootPager, FontContext.SMALL_NORMAL_HELVETICA, tColor)));
            footerPageNocell.setBorder(0);
            footerPageNocell.setHorizontalAlignment(PdfCell.ALIGN_RIGHT);
            footer.addCell(footerPageNocell);
        // -------- footer end   -----------
        } catch (Exception e) {
            throw ServiceException.FAILURE("exportDAOImpl.getHeaderFooter", e);
        }
    }

    public ByteArrayOutputStream getPdfData(JSONArray gridmap, HttpServletRequest request, JSONObject obj) throws ServiceException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = null;
        Document document = null;
        try {
            String colHeader = "";
            String colHeaderFinal = "";
            String fieldListFinal = "";
            String fieldList = "";
            String width = "";
            String align = "";
            String alignFinal = "";
            String widthFinal = "";
            String colHeaderArrStr[] = null;
            String dataIndexArrStr[] = null;
            String widthArrStr[] = null;
            String alignArrStr[] = null;
            String htmlCode = "";
            String advStr = "";
            int strLength = 0;
            float totalWidth = 0;

            config = new com.krawler.utils.json.base.JSONObject(request.getParameter("config"));
            if (request.getParameter("searchJson") != null && !request.getParameter("searchJson").equals("")) {
                JSONObject json = new JSONObject(request.getParameter("searchJson"));
                JSONArray advSearch = json.getJSONArray("root");
                for (int i = 0; i < advSearch.length(); i++) {
                    JSONObject key = advSearch.getJSONObject(i);
                    String value = "";
                    String name = key.getString("columnheader");
                    name = URLDecoder.decode(name);
                    name.trim();
                    if (name.contains("*")) {
                        name = name.substring(0, name.indexOf("*") - 1);
                    }
                    if (name.contains("(") && name.charAt(name.indexOf("(") + 1) == '&') {
                        htmlCode = name.substring(name.indexOf("(") + 3, name.length() - 2);
                        char temp = (char) Integer.parseInt(htmlCode, 10);
                        htmlCode = Character.toString(temp);
                        if (htmlCode.equals("$")) {
                            String currencyid = sessionHandlerImplObj.getCurrencyID(request);
                            String currency = currencyRender(key.getString("combosearch"), currencyid);
                            name = name.substring(0, name.indexOf("(") - 1);
                            name = name + "(" + htmlCode + ")";
                            value = currency;
                        } else {
                            name = name.substring(0, name.indexOf("(") - 1);
                            value = name + " " + htmlCode;
                        }
                    } else {
                        value = key.getString("combosearch");
                    }
                    advStr += name + " : " + value + ",";
                }
                advStr = advStr.substring(0, advStr.length() - 1);
                config.remove("subtitles");
                config.put("subtitles", "Filtered By: " + advStr);
            }
            if (request.getParameter("frm") != null && !request.getParameter("frm").equals("")) {
                KWLDateFormat dateFormat = (KWLDateFormat) hibernateTemplate.load(KWLDateFormat.class, sessionHandlerImplObj.getDateFormatID(request));
                String prefDate = dateFormat.getJavaForm();
                Date from = new Date(request.getParameter("frm"));
                Date to = new Date(request.getParameter("to"));
                config.remove("subtitles");
                String timeFormatId = sessionHandlerImplObj.getUserTimeFormat(request);
                String timeZoneDiff = sessionHandlerImplObj.getTimeZoneDifference(request);
                config.put("subtitles", "Filtered By: From : " + authHandler.getPrefDateFormatter(timeFormatId, timeZoneDiff,prefDate).format(from) + " To : " + authHandler.getPrefDateFormatter(timeFormatId, timeZoneDiff, prefDate).format(to));
            }

            Rectangle rec = null;
            if (config.getBoolean("landscape")) {
                Rectangle recPage = new Rectangle(PageSize.A4.rotate());
                recPage.setBackgroundColor(new java.awt.Color(Integer.parseInt(config.getString("bgColor"), 16)));
                document = new Document(recPage, 15, 15, 30, 30);
                rec = document.getPageSize();
                totalWidth = rec.getWidth();
            } else {
                Rectangle recPage = new Rectangle(PageSize.A4);
                recPage.setBackgroundColor(new java.awt.Color(Integer.parseInt(config.getString("bgColor"), 16)));
                document = new Document(recPage, 15, 15, 30, 30);
                rec = document.getPageSize();
                totalWidth = rec.getWidth();
            }

            writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new EndPage());
            document.open();
            if (config.getBoolean("showLogo")) {
                addComponyLogo(document, request);
            }

            addTitleSubtitle(document);

            if (gridmap != null) {
                for (int i = 0; i < gridmap.length(); i++) {
                    JSONObject temp = gridmap.getJSONObject(i);
                    colHeader += temp.getString("title");
                    if (colHeader.indexOf("*") != -1) {
                        colHeader = colHeader.substring(0, colHeader.indexOf("*") - 1) + ",";
                    } else {
                        colHeader += ",";
                    }
                    fieldList += temp.getString("header") + ",";
                    if (!config.getBoolean("landscape")) {
                        int totalWidth1 = (int) ((totalWidth / gridmap.length()) - 5.00);
                        width += "" + totalWidth1 + ",";  //resize according to page view[potrait]
                    } else {
                        width += temp.getString("width") + ",";
                    }
                    if (temp.getString("align").equals("")) {
                        align += "none" + ",";
                    } else {
                        align += temp.getString("align") + ",";
                    }
                }
                strLength = colHeader.length() - 1;
                colHeaderFinal = colHeader.substring(0, strLength);
                strLength = fieldList.length() - 1;
                fieldListFinal = fieldList.substring(0, strLength);
                strLength = width.length() - 1;
                widthFinal = width.substring(0, strLength);
                strLength = align.length() - 1;
                alignFinal = align.substring(0, strLength);
                colHeaderArrStr = colHeaderFinal.split(",");
                dataIndexArrStr = fieldListFinal.split(",");
                widthArrStr = widthFinal.split(",");
                alignArrStr = alignFinal.split(",");
            } else {
                fieldList = request.getParameter("header");
                colHeader = request.getParameter("title");
                width = request.getParameter("width");
                align = request.getParameter("align");
                colHeaderArrStr = colHeader.split(",");
                dataIndexArrStr = fieldList.split(",");
                widthArrStr = width.split(",");
                alignArrStr = align.split(",");
            }

            JSONArray store = null;
            if (obj.isNull("coldata")) {
                store = obj.getJSONArray("data");
            } else {
                store = obj.getJSONArray("coldata");
            }
            if(request.getParameter("get").equals("3")){
                int i = 0;
                while(i<colHeaderArrStr.length){
                    int len = (colHeaderArrStr.length-i>=8)?8:(colHeaderArrStr.length-i);
                    String tempHeaderArrStr[] = new String[len];
                    String tempdataIndexArrStr[] = new String[len];
                    String tempwidthArrStr[] = new String[len];
                    String tempalignArrStr[] = new String[len];
                    for(int k=0; k<len; k++,i++){
                        tempHeaderArrStr[k] = colHeaderArrStr[i];
                        tempdataIndexArrStr[k] = dataIndexArrStr[i];
                        tempwidthArrStr[k] = widthArrStr[i];
                        tempalignArrStr[k] = alignArrStr[i];
                    }
                    addTable(0, tempHeaderArrStr.length, 0, store.length(), store, tempdataIndexArrStr, tempHeaderArrStr, tempwidthArrStr, tempalignArrStr, document, request);
                }
            }else
                addTable(0, colHeaderArrStr.length, 0, store.length(), store, dataIndexArrStr, colHeaderArrStr, widthArrStr, alignArrStr, document, request);

        } catch (Exception e) {
            throw ServiceException.FAILURE("exportDAOImpl.getPdfData", e);
        } finally {
            if (document != null) {
                document.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
        return baos;
    }

    public void createCsvFile(HttpServletRequest request, HttpServletResponse response, JSONObject obj) throws ServiceException {
        try {
            String headers[] = null;
            String titles[] = null;
            JSONArray repArr = null;

            if (request.getParameter("header") != null) {
                String head = request.getParameter("header");
                String tit = request.getParameter("title");
                tit = URLDecoder.decode(tit);
                headers = (String[]) head.split(",");
                titles = (String[]) tit.split(",");
            } else {
                headers = (String[]) obj.get("header");
                titles = (String[]) obj.get("title");
            }
            StringBuilder reportSB = new StringBuilder();

            if (obj.isNull("coldata")) {
                repArr = obj.getJSONArray("data");
            } else {
                repArr = obj.getJSONArray("coldata");
            }
            for (int h = 0; h < headers.length; h++) {
                if (h < headers.length - 1) {
                    reportSB.append(StringEscapeUtils.escapeCsv(titles[h])).append(',');
                } else {
                    reportSB.append(StringEscapeUtils.escapeCsv(titles[h])).append('\n');
                }
            }
            for (int t = 0; t < repArr.length(); t++) {
                JSONObject temp = repArr.getJSONObject(t);
                for (int h = 0; h < headers.length; h++) {
                    if (h < headers.length - 1) {
                        reportSB.append(StringEscapeUtils.escapeCsv(temp.getString(headers[h]))).append(',');
                    } else {
                        reportSB.append(StringEscapeUtils.escapeCsv(temp.getString(headers[h]))).append('\n');
                    }
                }
            }
            String fname = request.getParameter("name");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(reportSB.toString().getBytes());
            os.close();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fname + ".csv\"");
            response.setContentType("application/octet-stream");
            response.setContentLength(os.size());
            response.getOutputStream().write(os.toByteArray());
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw ServiceException.FAILURE("exportDAOImpl.createCsvFile : " + e.getMessage(), e);
        } catch (JSONException e) {
            throw ServiceException.FAILURE("exportDAOImpl.createCsvFile : " + e.getMessage(), e);
        }
    }

    public String currencyRender(String currency, String currencyid) throws SessionExpiredException {
        KWLCurrency cur = (KWLCurrency) hibernateTemplate.load(KWLCurrency.class, currencyid);
        String symbol = cur.getHtmlcode();
            char temp = (char) Integer.parseInt(symbol, 16);
            symbol = Character.toString(temp);
        float v = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        if (currency.equals("")) {
            return symbol;
        }
        v = Float.parseFloat(currency);
        String fmt = decimalFormat.format(v);
        fmt = symbol + fmt;
        return fmt;
    }

    public void setHeaderFooter(Document doc, String headerText) {
        HeaderFooter footer = new HeaderFooter(new Phrase(fontFamilySelector.process("  ", FontContext.SMALL_NORMAL_HELVETICA)), true);
        footer.setBorderWidth(0);
        footer.setBorderWidthTop(1);
        footer.setAlignment(HeaderFooter.ALIGN_RIGHT);
        doc.setFooter(footer);
        HeaderFooter header = new HeaderFooter(new Phrase(fontFamilySelector.process(headerText, FontContext.FOOTER_BOLD_HELVETICA)), false);
        doc.setHeader(header);
    }
    
    public void exportConfigData(HttpServletRequest request, HttpServletResponse response, JSONObject jobj){
        String fileType = null;
        try {	
            fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "csv")) {
            	String headers[] = null;
            	String titles[] = null;
            	JSONArray repArr = null;
            	
            	if (request.getAttribute("header") != null) {
            		String head = request.getAttribute("header").toString();
            		String tit = request.getAttribute("title").toString();
            		tit = URLDecoder.decode(tit);
            		headers = (String[]) head.split(",");
            		titles = (String[]) tit.split(",");
            	} else {
            		headers = (String[]) jobj.get("header");
            		titles = (String[]) jobj.get("title");
            	}
            	StringBuilder reportSB = new StringBuilder();
            	
            	if (jobj.isNull("coldata")) {
            		repArr = jobj.getJSONArray("data");
                } else {
                	repArr = jobj.getJSONArray("coldata");
                }
            	for (int h = 0; h < headers.length; h++) {
            		if (h < headers.length - 1) {
            			reportSB.append(StringEscapeUtils.escapeCsv(titles[h])).append(',');
            		} else {
            			reportSB.append(StringEscapeUtils.escapeCsv(titles[h])).append('\n');
            		}
            	}
            	for (int t = 0; t < repArr.length(); t++) {
            		JSONObject temp = repArr.getJSONObject(t);
            		for (int h = 0; h < headers.length; h++) {
            			if (h < headers.length - 1) {
            				reportSB.append(StringEscapeUtils.escapeCsv(temp.getString(headers[h]))).append(',');
            			} else {
            				reportSB.append(StringEscapeUtils.escapeCsv(temp.getString(headers[h]))).append('\n');
            			}
            		}
            	}
            	ByteArrayOutputStream os = new ByteArrayOutputStream();
            	os.write(reportSB.toString().getBytes());
            	os.close();
            	response.setHeader("Content-Disposition", "attachment; filename=\"" + request.getParameter("name") + ".csv\"");
            	response.setContentType("application/octet-stream");
            	response.setContentLength(os.size());
            	response.getOutputStream().write(os.toByteArray());
            	response.getOutputStream().flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
