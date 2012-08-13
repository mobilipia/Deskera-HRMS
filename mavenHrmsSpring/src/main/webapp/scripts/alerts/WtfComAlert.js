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
 function WtfComMsgBox(choice, type) {
    var strobj = [];
    switch (choice) {
        case 152:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"),WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
       case 453:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
       case 457:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.alert"), WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
       case 458:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
     
        case 470:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;

        case 605:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.alert"), WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
        case 606:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.alert"), WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
        case 607:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.alert"), WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;

        case 950:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.alert"),WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
            break;
        case 953:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
  
        case 955:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.alert"),WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
        case 956:
            strobj = [WtfGlobal.getLocaleText("hrms.administration.email.template.tooltip"),WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
        case 1050:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.alert"),WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
        case 1051:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.alert"),WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
        case 1052:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.alert"),WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
        case 1053:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.alert"),WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
        case 1054:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.alert"),WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
        case 1055:
            strobj = [ WtfGlobal.getLocaleText("hrms.common.alert"),WtfGlobal.getLocaleText("hrms.Messages.WtfComMsgBox"+choice)];
            break;
        default:
            strobj = [choice[0], choice[1]];
            break;
    }


	var iconType = Wtf.MessageBox.INFO;

    if(type == 0)
        iconType = Wtf.MessageBox.INFO;

	if(type == 1)
	    iconType = Wtf.MessageBox.ERROR;

    else if(type == 2)
        iconType = Wtf.MessageBox.WARNING;

    else if(type == 3)
        iconType = Wtf.MessageBox.INFO;


    Wtf.MessageBox.show({
        title: strobj[0],
        msg: strobj[1],
        buttons: Wtf.MessageBox.OK,
        animEl: 'mb9',
        icon: iconType
    });
}

