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
package com.krawler.common.fontsettings;

import java.awt.Color;
import java.io.File;
import java.io.FilenameFilter;

import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;

public class FontSetting {
	private static FontFamilySelector fontFamilySelector=new FontFamilySelector();
    private static final Log log = LogFactory.getLog(FontSetting.class);
    
    static{
    	FontFamily fontFamily=new FontFamily();
    	fontFamily.addFont(FontContext.SMALL_BOLD_HELVETICA, FontFactory.getFont("Helvetica", 8, Font.BOLD, Color.BLACK));
    	fontFamily.addFont(FontContext.SMALL_NORMAL_HELVETICA, FontFactory.getFont("Helvetica", 8, Font.NORMAL, Color.BLACK));
    	fontFamily.addFont(FontContext.MEDIUM_BOLD_HELVETICA, FontFactory.getFont("Helvetica", 10, Font.BOLD, Color.BLACK));
    	fontFamily.addFont(FontContext.REGULAR_BOLD_HELVETICA, FontFactory.getFont("Helvetica", 12, Font.BOLD, Color.BLACK));
    	fontFamily.addFont(FontContext.REGULAR_NORMAL_HELVETICA, FontFactory.getFont("Helvetica", 12, Font.NORMAL, Color.BLACK));
    	fontFamily.addFont(FontContext.REGULAR_BOLD_HELVETICA, FontFactory.getFont("Helvetica", 12, Font.BOLD, Color.BLACK));
    	fontFamily.addFont(FontContext.BIG_NORMAL_HELVETICA, FontFactory.getFont("Helvetica", 24, Font.NORMAL, Color.BLACK));
    	fontFamily.addFont(FontContext.FOOTER_NORMAL_HELVETICA, FontFactory.getFont("Helvetica", 14, Font.NORMAL, Color.BLACK));
    	fontFamily.addFont(FontContext.FOOTER_BOLD_HELVETICA, FontFactory.getFont("Helvetica", 14, Font.BOLD, Color.BLACK));
    	fontFamily.addFont(FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN, FontFactory.getFont("Times New Roman", 8, Font.NORMAL, Color.BLACK));
    	fontFamily.addFont(FontContext.SMALL_BOLD_TIMES_NEW_ROMAN, FontFactory.getFont("Times New Roman", 8, Font.BOLD, Color.BLACK));
    	fontFamily.addFont(FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, FontFactory.getFont("Times New Roman", 12, Font.NORMAL, Color.BLACK));
    	fontFamily.addFont(FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, FontFactory.getFont("Times New Roman", 12, Font.BOLD, Color.BLACK));
    	fontFamily.addFont(FontContext.MEDIUM_NORMAL_TIMES_NEW_ROMAN, FontFactory.getFont("Times New Roman", 10, Font.NORMAL, Color.BLACK));
    	fontFamily.addFont(FontContext.TABLE_BOLD_TIMES_NEW_ROMAN, FontFactory.getFont("Times New Roman", 14, Font.BOLD, Color.BLACK));
    	fontFamilySelector.addFontFamily(fontFamily);
    	
    	File[] files;
		try {
			File f = new File(exportDAOImpl.class.getClassLoader().getResource("fonts").toURI());
			files = f.listFiles(new FilenameFilter() {				
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".ttf");
					}
				});
		} catch (Exception e1) {
			log.warn("error: "+e1.getMessage());
			files = new File[]{};
		}
		for(File file:files){
			try{
				BaseFont bfnt = BaseFont.createFont(file.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
				fontFamily=new FontFamily();
				fontFamily.addFont(FontContext.SMALL_BOLD_HELVETICA, new Font(bfnt, 8, Font.BOLD, Color.BLACK));
				fontFamily.addFont(FontContext.SMALL_NORMAL_HELVETICA, new Font(bfnt, 8, Font.NORMAL, Color.BLACK));
				fontFamily.addFont(FontContext.MEDIUM_BOLD_HELVETICA, new Font(bfnt, 10, Font.BOLD, Color.BLACK));
				fontFamily.addFont(FontContext.REGULAR_BOLD_HELVETICA, new Font(bfnt, 12, Font.BOLD, Color.BLACK));
				fontFamily.addFont(FontContext.REGULAR_NORMAL_HELVETICA, new Font(bfnt, 12, Font.NORMAL, Color.BLACK));
		    	fontFamily.addFont(FontContext.REGULAR_BOLD_HELVETICA, new Font(bfnt, 12, Font.BOLD, Color.BLACK));
		    	fontFamily.addFont(FontContext.BIG_NORMAL_HELVETICA, new Font(bfnt, 24, Font.NORMAL, Color.BLACK));
		    	fontFamily.addFont(FontContext.FOOTER_NORMAL_HELVETICA, new Font(bfnt, 14, Font.NORMAL, Color.BLACK));
		    	fontFamily.addFont(FontContext.FOOTER_BOLD_HELVETICA, new Font(bfnt, 14, Font.BOLD, Color.BLACK));
		    	fontFamily.addFont(FontContext.SMALL_NORMAL_TIMES_NEW_ROMAN, new Font(bfnt, 8, Font.NORMAL, Color.BLACK));
		    	fontFamily.addFont(FontContext.SMALL_BOLD_TIMES_NEW_ROMAN, new Font(bfnt, 8, Font.BOLD, Color.BLACK));
		    	fontFamily.addFont(FontContext.REGULAR_NORMAL_TIMES_NEW_ROMAN, new Font(bfnt, 12, Font.NORMAL, Color.BLACK));
		    	fontFamily.addFont(FontContext.REGULAR_BOLD_TIMES_NEW_ROMAN, new Font(bfnt, 12, Font.BOLD, Color.BLACK));
		    	fontFamily.addFont(FontContext.MEDIUM_NORMAL_TIMES_NEW_ROMAN, new Font(bfnt, 10, Font.NORMAL, Color.BLACK));
		    	fontFamily.addFont(FontContext.TABLE_BOLD_TIMES_NEW_ROMAN, new Font(bfnt, 14, Font.BOLD, Color.BLACK));
		    	fontFamilySelector.addFontFamily(fontFamily);
			}catch(Exception e) {
				log.warn("Font ("+file.getName()+") not available : "+e.getMessage());
			}
		}
    }
    
    public static FontFamilySelector getFontFamilySelector(){
    	return fontFamilySelector;
    }
}
