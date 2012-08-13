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

import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.session.SessionExpiredException;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import com.krawler.esp.database.hrmsDbcon;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.esp.database.payrollDBCon;
import com.krawler.esp.handlers.AuthHandler;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.esp.hibernate.impl.HibernateUtil;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
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
import javax.naming.ConfigurationException;


public class ExportServlet extends HttpServlet {

    private static Font fontSmallRegular = FontFactory.getFont("Helvetica", 8, Font.BOLD, Color.BLACK);
    private static Font fontMediumRegular = FontFactory.getFont("Helvetica", 12, Font.NORMAL, Color.BLACK);
    private static Font fontMediumBold = FontFactory.getFont("Helvetica", 12, Font.BOLD, Color.BLACK);
    private static Font fontSmallBold = FontFactory.getFont("Helvetica", 8, Font.BOLD, Color.BLACK);
    private static Font fontRegular = FontFactory.getFont("Helvetica", 12, Font.NORMAL, Color.BLACK);
    private static Font fontBold = FontFactory.getFont("Helvetica", 12, Font.BOLD, Color.BLACK);
    private static Font fontBig = FontFactory.getFont("Helvetica", 24, Font.NORMAL, Color.BLACK);
    private static String imgPath = "";
    private static String companyName = "Deskera";
    
    private static final String defaultCompanyImgPath = "images/logo.gif";

    private static com.krawler.utils.json.base.JSONObject config = null;
    private PdfPTable header = null;
    private PdfPTable footer = null;

    public class EndPage extends PdfPageEventHelper {
    public void onEndPage(PdfWriter writer, Document document) {
            try {
                Rectangle page = document.getPageSize();

                getHeaderFooter(document);
                // Add page header
                header.setTotalWidth(page.getWidth()-document.leftMargin()-document.rightMargin());
                header.writeSelectedRows(0, -1, document.leftMargin(), page.getHeight()-10 ,writer.getDirectContent());

                // Add page footer
                footer.setTotalWidth(page.getWidth()-document.leftMargin()-document.rightMargin());
                footer.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin()-5 ,writer.getDirectContent());

                // Add page border
                if (config.getBoolean("pageBorder")) {
                    int bmargin = 8;  //border margin
                    PdfContentByte cb = writer.getDirectContent();
                    cb.rectangle( bmargin, bmargin, page.getWidth() - bmargin*2, page.getHeight() - bmargin*2);
                    cb.setColorStroke(Color.LIGHT_GRAY);
                    cb.stroke();
                }

            } catch (JSONException e) {
                throw new ExceptionConverter(e);
            }
        }
    }
    private static final long serialVersionUID = -8401651817881523209L;
    static SimpleDateFormat df = new SimpleDateFormat("yyyy-M-dd");

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, ServiceException, IOException, SessionExpiredException, JSONException {
        if (com.krawler.esp.handlers.SessionHandler.isValidSession(request, response)) {
            org.hibernate.Session session = null;
            ByteArrayOutputStream baos = null;
            String filename = request.getParameter("name");
            String fileType = null;
            JSONObject grid=null;
            JSONArray gridmap=null;
            try {
                session = HibernateUtil.getCurrentSession();
                fileType = request.getParameter("filetype");
                if(request.getParameter("gridconfig")!=null) {
                    grid = new JSONObject(request.getParameter("gridconfig"));
                    gridmap = grid.getJSONArray("data");
                }
                if (StringUtil.equal(fileType, "csv")) {
                    createCsvFile(session, request, response);
                } else if (StringUtil.equal(fileType, "pdf")) {
                    baos = getPdfData(gridmap, request,session);
                    writeDataToFile(filename, fileType, baos, response);
                }
            } catch (Exception ex) {
                throw ServiceException.FAILURE(ex.getMessage(), ex);
            } finally {
                HibernateUtil.closeSession(session);
            }
        } else {
            response.getOutputStream().println("{\"valid\": false}");
        }
    }

    private void writeDataToFile(String filename, String fileType, ByteArrayOutputStream baos, HttpServletResponse response) throws IOException
	{
        try {
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "." + fileType + "\"");
            response.setContentType("application/octet-stream");
            response.setContentLength(baos.size());
            response.getOutputStream().write(baos.toByteArray());
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch(IOException e){
            response.getOutputStream().println("{\"valid\": false}");
        }
	}

    private static void addComponyLogo(Document d, HttpServletRequest req) throws ConfigurationException, DocumentException {
        PdfPTable table = new PdfPTable(1);
        imgPath = StorageHandler.GetProfileImgStorePath() + "logo.gif";
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setWidthPercentage(50);
        PdfPCell cell = null;
        try {
            if(StringUtil.isStandAlone()){
                imgPath = URLUtil.getPageURL(req, "").concat(defaultCompanyImgPath);
            }
            Image img = Image.getInstance(imgPath);
            cell = new PdfPCell(img);
        } catch (Exception e) {
            cell = new PdfPCell(new Paragraph(companyName, fontBig));
        }
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        d.add(table);
    }

    private static void addTitleSubtitle(Document d) throws DocumentException, JSONException {
        java.awt.Color tColor = new Color(Integer.parseInt(config.getString("textColor"), 16));
        fontBold.setColor(tColor);
        fontRegular.setColor(tColor);
        PdfPTable table = new PdfPTable(1);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.setWidthPercentage(100);
        table.setSpacingBefore(6);

        //Report Title
        PdfPCell cell = new PdfPCell(new Paragraph(config.getString("title"), fontBold));
        cell.setBorder(0);
        cell.setBorderWidth(0);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        //Report Subtitle(s)
        String[] SubTitles = config.getString("subtitles").split("~");// '~' as separator
        for(int i=0; i < SubTitles.length; i++){
            cell = new PdfPCell(new Paragraph(SubTitles[i], fontSmallRegular));
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
    }


    private int addTable(int stcol, int stpcol, int strow, int stprow, JSONArray store, String[] colwidth2, String[] colHeader, String[] widths, String[] align, Document document,HttpServletRequest request,Session session) throws JSONException, DocumentException, SessionExpiredException {

        java.awt.Color tColor = new Color(Integer.parseInt(config.getString("textColor"), 16));
        fontSmallBold.setColor(tColor);
        PdfPTable table;
        float[] tcol;
        tcol = new float[colHeader.length+1];
        tcol[0]=40;
        for(int i=1;i<colHeader.length+1;i++) {
            tcol[i] = Float.parseFloat(widths[i-1]);
        }
        table = new PdfPTable(colHeader.length+1);
        table.setWidthPercentage(tcol,document.getPageSize());
        table.setSpacingBefore(15);
        Font f1 = FontFactory.getFont("Helvetica", 8, Font.NORMAL, tColor);
        PdfPCell h2 = new PdfPCell(new Paragraph("No.", fontSmallBold));
        if (config.getBoolean("gridBorder")) {
                h2.setBorder(PdfPCell.BOX);
            } else {
                h2.setBorder(0);
            }
        h2.setPadding(4);
        h2.setBorderColor(Color.GRAY);
        h2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(h2);
        PdfPCell h1=null;
        for (int hcol = stcol; hcol < colwidth2.length; hcol++) {
        if(align[hcol].equals("right") && !colHeader[hcol].equals("")) {
            String currency = currencyRender("",session,request);
            h1 = new PdfPCell(new Paragraph(colHeader[hcol]+"("+currency+")", fontSmallBold));
        } else
            h1 = new PdfPCell(new Paragraph(colHeader[hcol], fontSmallBold));
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

        for (int row = strow; row <stprow ; row++) {
            h2 = new PdfPCell(new Paragraph(String.valueOf(row + 1), f1));
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
                Paragraph para = null;
                if(align[col].equals("right") && !temp.getString(colwidth2[col]).equals("")) {
                     String currency = currencyRender(temp.getString(colwidth2[col]),session,request);
                     para = new Paragraph(currency, f1);
                } else {
                    if (colwidth2[col].equals("invoiceno")) {
                        para=new Paragraph(temp.getString("no").toString(),f1);
                    } else if (colwidth2[col].equals("invoicedate")) {
                        para=new Paragraph(temp.getString("date").toString(),f1);
                    } else if ((temp.isNull(colwidth2[col])) && !(colwidth2[col].equals("invoiceno")) && !(colwidth2[col].equals("invoicedate"))) {
                        para =new Paragraph("", fontMediumRegular);
                    } else {
                        para=new Paragraph(temp.getString(colwidth2[col]).toString(),f1);
                    }
                }
                 h1 = new PdfPCell(para);
                    if (config.getBoolean("gridBorder")) {
                        h1.setBorder(PdfPCell.BOTTOM | PdfPCell.LEFT | PdfPCell.RIGHT);
                    } else {
                        h1.setBorder(0);
                    }
                    h1.setPadding(4);
                    h1.setBorderColor(Color.GRAY);
                    if(!align[col].equals("right") && !align[col].equals("left")) {
                        h1.setHorizontalAlignment(Element.ALIGN_CENTER);
                        h1.setVerticalAlignment(Element.ALIGN_CENTER);
                    } else if(align[col].equals("right")) {
                        h1.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        h1.setVerticalAlignment(Element.ALIGN_RIGHT);
                    } else if(align[col].equals("left")) {
                        h1.setHorizontalAlignment(Element.ALIGN_LEFT);
                        h1.setVerticalAlignment(Element.ALIGN_LEFT);
                    }
                    table.addCell(h1);
                }
            }
            document.add(table);
            document.newPage();

           return stpcol;
    }

    public void getHeaderFooter(Document document) throws JSONException {
                java.awt.Color tColor = new Color(Integer.parseInt(config.getString("textColor"), 16));
                fontSmallRegular.setColor(tColor);
                java.util.Date dt = new java.util.Date();
                String date="yyyy-MM-dd";
                java.text.SimpleDateFormat dtf = new java.text.SimpleDateFormat(date);
                String DateStr = dtf.format(dt);

                // -------- header ----------------
                header = new PdfPTable(3);
                String HeadDate = "";
                if (config.getBoolean("headDate")) HeadDate = DateStr;
                PdfPCell headerDateCell = new PdfPCell(new Phrase( HeadDate, fontSmallRegular));
                headerDateCell.setBorder(0);
                headerDateCell.setPaddingBottom(4);
                header.addCell(headerDateCell);

                PdfPCell headerNotecell = new PdfPCell(new Phrase(config.getString("headNote"), fontSmallRegular));
                headerNotecell.setBorder(0);
                headerNotecell.setPaddingBottom(4);
                headerNotecell.setHorizontalAlignment(PdfCell.ALIGN_CENTER);
                header.addCell(headerNotecell);

                String HeadPager = "";
                if(config.getBoolean("headPager")) HeadPager = String.valueOf(document.getPageNumber());//current page no
                PdfPCell headerPageNocell = new PdfPCell(new Phrase(HeadPager, fontSmallRegular));
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
                if(config.getBoolean("footDate")) PageDate = DateStr;
                PdfPCell pagerDateCell = new PdfPCell(new Phrase( PageDate, fontSmallRegular));
                pagerDateCell.setBorder(0);
                footer.addCell(pagerDateCell);

                PdfPCell footerNotecell = new PdfPCell(new Phrase(config.getString("footNote"), fontSmallRegular));
                footerNotecell.setBorder(0);
                footerNotecell.setHorizontalAlignment(PdfCell.ALIGN_CENTER);
                footer.addCell(footerNotecell);

                String FootPager = "";
                if(config.getBoolean("footPager")) FootPager = String.valueOf(document.getPageNumber());//current page no
                PdfPCell footerPageNocell = new PdfPCell(new Phrase(FootPager, fontSmallRegular));
                footerPageNocell.setBorder(0);
                footerPageNocell.setHorizontalAlignment(PdfCell.ALIGN_RIGHT);
                footer.addCell(footerPageNocell);
                // -------- footer end   -----------
    }


    private ByteArrayOutputStream getPdfData(JSONArray gridmap, HttpServletRequest request,Session session) throws ServiceException, SessionExpiredException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer=null;
        try{
            String colHeader ="";
            String colHeaderFinal="";
            String fieldListFinal ="";
            String fieldList = "";
            String width="";
            String align="";
            String alignFinal="";
            String widthFinal="";
            String colHeaderArrStr[]=null;
            String dataIndexArrStr[]=null;
            String widthArrStr[]=null;
            String alignArrStr[]=null;
            String htmlCode="";
            String advStr="";
            int strLength=0;
            float totalWidth=0;

            config = new com.krawler.utils.json.base.JSONObject(request.getParameter("config"));
            String tmpTitle = config.getString("title");
            Document document = null;
            Rectangle rec =null;
            if (config.getBoolean("landscape")){
                Rectangle recPage=new Rectangle(PageSize.A4.rotate());
                recPage.setBackgroundColor(new java.awt.Color(Integer.parseInt(config.getString("bgColor"), 16)));
                document = new Document(recPage, 15, 15, 30, 30);
                rec=document.getPageSize();
                totalWidth=rec.getWidth();
            } else {
                Rectangle recPage=new Rectangle(PageSize.A4);
                recPage.setBackgroundColor(new java.awt.Color(Integer.parseInt(config.getString("bgColor"), 16)));
                document = new Document(recPage, 15, 15, 30, 30);
                rec=document.getPageSize();
                totalWidth=rec.getWidth();
            }

            writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new EndPage());
            document.open();
            if (config.getBoolean("showLogo")) {
                addComponyLogo(document, request);
            }

            addTitleSubtitle(document);

            if(gridmap!=null) {
                 for (int i = 0; i < gridmap.length(); i++) {
                    JSONObject temp = gridmap.getJSONObject(i);
                    colHeader+=temp.getString("title");
                    if(colHeader.indexOf("*")!=-1)
                        colHeader = colHeader.substring(0, colHeader.indexOf("*")-1)+",";
                    else
                        colHeader += ",";
                    fieldList += temp.getString("header") + ",";
                    if(!config.getBoolean("landscape")) {
                        int totalWidth1=(int) ((totalWidth / gridmap.length()) - 5.00);
                        width+=""+totalWidth1 +",";  //resize according to page view[potrait]
                    } else
                        width += temp.getString("width") + ",";
                    if (temp.getString("align").equals("")) {
                        align += "none" + ",";
                    } else {
                        align +=temp.getString("align")+",";
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
                fieldList=request.getParameter("header");
                colHeader=request.getParameter("title");
                width=request.getParameter("width");
                align=request.getParameter("align");
                colHeaderArrStr = colHeader.split(",");
                dataIndexArrStr = fieldList.split(",");
                widthArrStr = width.split(",");
                alignArrStr = align.split(",");
            }

            JSONObject obj = null;
            obj = getReport(request, session);
            JSONArray store = obj.getJSONArray("data");

            addTable(0, colHeaderArrStr.length, 0, store.length(), store, dataIndexArrStr, colHeaderArrStr, widthArrStr, alignArrStr, document, request, session);

            document.close();

        } catch (ConfigurationException ex) {
            throw ServiceException.FAILURE("ExportProjectReport.getPdfData", ex);
        } catch (DocumentException ex) {
            throw ServiceException.FAILURE("ExportProjectReport.getPdfData", ex);
        } catch (JSONException e){
            throw ServiceException.FAILURE("ExportProjectReport.getPdfData", e);
        }finally{
         writer.close();
        }
        return baos;
    }

    public String currencyRender(String currency,Session session,HttpServletRequest request) throws SessionExpiredException {
            KWLCurrency cur = (KWLCurrency)session.load(KWLCurrency.class, AuthHandler.getCurrencyID(request));
            String symbol=cur.getHtmlcode();
            try{
                char temp=  (char) Integer.parseInt(symbol,16);
                symbol=Character.toString(temp);
            } catch (Exception e){
            }
            
            float v = 0;
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            if(currency.equals(""))
                return symbol;
            v = Float.parseFloat(currency);
            String fmt=decimalFormat.format(v);
            fmt=symbol+fmt;
            return fmt;
    }

    public static JSONObject getReport(HttpServletRequest request, Session session) throws ServiceException, JSONException {
        JSONObject obj = new JSONObject();
        try {
            int mode = Integer.parseInt(request.getParameter("get"));
            switch (mode) {
                case 2:
                    obj = hrmsDbcon.getTimesheetReport(request);
                    break;
                case 4:
                    String result = "";
                    JSONObject tempobj = new JSONObject();
                    payrollDBCon rh = new payrollDBCon();
                    result = rh.getter(34, request);
                    tempobj = new JSONObject(result);
                    obj = tempobj.getJSONObject("data");
                    break;
               case 5:
                    String result1 = "";
                    JSONObject tempobj1 = new JSONObject();
                    payrollDBCon rh1 = new payrollDBCon();
                    result1 = rh1.getter(48, request);
                    tempobj1 = new JSONObject(result1);
                    obj = tempobj1.getJSONObject("data");
                    break;
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } finally {
        }
        return obj;
    }

    public static void createCsvFile(Session session, HttpServletRequest request, HttpServletResponse response) {
         try {
            String report = request.getParameter("get");
            String headers[] = null;
            String titles[] = null;
            JSONObject obj = null;
            String nm = null;
            obj = getReport(request, session);
            if (request.getParameter("header") != null) {
                String head = request.getParameter("header");
                String tit = request.getParameter("title");
                headers = (String[]) head.split(",");
                titles = (String[]) tit.split(",");
            } else {
                headers = (String[]) obj.getString("header").split(",");
                titles = (String[]) obj.getString("title").split(",");
            }
            StringBuilder reportSB = new StringBuilder();
            JSONArray repArr = obj.getJSONArray("data");
            for (int h = 0; h < headers.length; h++) {
                if (h < headers.length - 1) {
                    reportSB.append("\"" + titles[h] + "\",");
                } else {
                    reportSB.append("\"" + titles[h] + "\"\n");
                }
            }
            for (int t = 0; t < repArr.length(); t++) {
                JSONObject temp = repArr.getJSONObject(t);
                for (int h = 0; h < headers.length; h++) {
                    if (h < headers.length - 1) {
                        if (headers[h].equals("invoiceno")) {
                            reportSB.append("\" " + temp.getString("no") + "\",");
                        } else if (headers[h].equals("invoicedate")) {
                            reportSB.append("\" " + temp.getString("date") + "\",");
                        } else if ((temp.isNull(headers[h])) && !(headers[h].equals("invoiceno")) && !(headers[h].equals("invoicedate"))) {
                            reportSB.append(",");
                        } else {
                            reportSB.append("\" " + temp.getString(headers[h]) + "\",");
                        }
                    } else {
                        if (headers[h].equals("invoiceno")) {
                            reportSB.append("\" " + temp.getString("no") + "\"\n");
                        } else if (headers[h].equals("invoicedate")) {
                            reportSB.append("\" " + temp.getString("date") + "\"\n");
                        } else if ((temp.isNull(headers[h])) && !(headers[h].equals("invoiceno")) && !(headers[h].equals("invoicedate"))) {
                            reportSB.append("\" \"\n");
                        } else {
                            reportSB.append("\"" + temp.getString(headers[h]) + "\"\n");
                        }
                    }
                }
            }
            String fname = request.getParameter("name");
            nm = request.getParameter("name");
            if (nm == null || nm == "") {
                nm = fname;
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(reportSB.toString().getBytes());
            os.close();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fname + ".csv\"");
            response.setContentType("application/octet-stream");
            response.setContentLength(os.size());
            response.getOutputStream().write(os.toByteArray());
            response.getOutputStream().flush();
        } catch (ServiceException ex) {
            Logger.getLogger(ExportServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExportServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException e) {
            Logger.getLogger(ExportServlet.class.getName()).log(Level.SEVERE, null, e);
        }
    }
   public static void setHeaderFooter(Document doc, String headerText) {
        HeaderFooter footer = new HeaderFooter(new Phrase("  ", FontFactory.getFont("Helvetica", 8, Font.NORMAL, Color.BLACK)), true);
        footer.setBorderWidth(0);
        footer.setBorderWidthTop(1);
        footer.setAlignment(HeaderFooter.ALIGN_RIGHT);
        doc.setFooter(footer);
        HeaderFooter header = new HeaderFooter(new Phrase(headerText, FontFactory.getFont("Helvetica", 14, Font.BOLD, Color.BLACK)), false);
        doc.setHeader(header);
    }

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
            doPost(request, response);
        } catch (Exception ex) {
            Logger.getLogger(ExportServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        try {
            try {
                processRequest(request, response);
            } catch (SessionExpiredException ex) {
                Logger.getLogger(ExportServlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(ExportServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ServiceException ex) {
            Logger.getLogger(ExportServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
}
