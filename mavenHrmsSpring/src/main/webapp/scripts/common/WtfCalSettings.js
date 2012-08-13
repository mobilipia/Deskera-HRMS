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
function getInstrMsg(msg) {
    return "<span style='font-size:10px !important;color:gray !important;'>"+msg+"</span>";
}

function deleteMsgBox(msg) {
    return WtfGlobal.getLocaleText({key:"hrms.Messages.Deleteselected",params:[msg]})+"<br><br><b>"+WtfGlobal.getLocaleText("hrms.Messages.DateCannotbeRetrive")+"</b>";
}

function calMsgBoxShow(choice, type,wait, width) {
    if(wait===null || wait===undefined)
        wait=false;
    if(width===null || width===undefined)
        width=300;
    var title="";
    var strobj = [];
    switch (choice) {
        case 1:
        	strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 2:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 3:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 4:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 5:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 6:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 7:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 8:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 9:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 10:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 11:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 12:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 13:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 14:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 15:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 16:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 17:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 18:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 19:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 20:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 21:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 22:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 23:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 24:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 25:
            strobj = [WtfGlobal.getLocaleText("hrms.common.status"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 26:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 27:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 28:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 29:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 30:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 31:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 32:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 33:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 34:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 35:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 36:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 37:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 38:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 39:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 40:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 41:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 42:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 43:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 44:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 45:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 46:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 47:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 48:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 49:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 50:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 51:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 52:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 53:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 54:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 55:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 56:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 57:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 58:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 59:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 60:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 61:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 62:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 63:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 64:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 65:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 66:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 67:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 68:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 69:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 70:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 71:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 72:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 73:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 74:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 75:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 76:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 77:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 78:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 79:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 80:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 81:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 82:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 83:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 84:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 85:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 86:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 87:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 88:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 89:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 90:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 91:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 92:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 93:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 94:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 95:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 96:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 97:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 98:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 99:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 100:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 101:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 102:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 103:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 104:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 105:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 106:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 107:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 108:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 109:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 110:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 111:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 112:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 113:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 114:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 115:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 116:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 117:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 118:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 119:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 120:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 121:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 122:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 123:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 124:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 125:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 126:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 127:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 128:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 129:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 130:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 131:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 132:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 133:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 134:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 135:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 136:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 137:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 138:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 139:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 140:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 141:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 142:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 143:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 144:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 145:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 146:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 147:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 148:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 149:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 150:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 151:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 152:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 153:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 154:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 155:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 156:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 157:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 158:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 159:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;

        case 160:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 161:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 162:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 163:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 164:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 165:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 166:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 168:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 169:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 170:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 171:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 172:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 173:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 174:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 175:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 176:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 177:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 178:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 179:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 180:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 181:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 182:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 183:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 184:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 185:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 186:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 187:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 188:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 189:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 190:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 191:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 192:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 193:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 194:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 200:
            strobj = [WtfGlobal.getLocaleText("hrms.common.Savingdata")];
            title=WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice);
            break;
        case 201:
            strobj = [WtfGlobal.getLocaleText("hrms.common.Deletingdata")];
            title=WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice);
            break;
        case 202:
            strobj = [WtfGlobal.getLocaleText("hrms.common.Loadingdata")];
            title=WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice);
            break;
        case 203:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 204:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 205:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 206:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 207:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 208:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 209:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 210:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 211:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 212:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 213:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 214:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 215:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 216:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 217:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 218:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 219:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 220:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 221:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 222:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 223:
//            strobj = ['Warning', 'Please select a Designation,Pay Interval and Date/Day first.'];
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 224:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 225:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 226:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 227:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 228:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 229:
            strobj = [WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        case 230:
            strobj = [WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.timesheet.selected.timesheets.rejected.successfully")];
            break;
        case 231:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.timesheet.job.cannot.deleted.already.assigned")];
            break;
        case 232:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.timesheet.component.subtype.cannot.deleted")];
            break;
        case 233:
            strobj = [WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow"+choice)];
            break;
        default:
            strobj= [choice[0],choice[1]];
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
    else if(type == 4)
        iconType = 'wtf-mb-download';
if(wait)
    {
    Wtf.MessageBox.show({
       msg: strobj,
       width:width,
       wait:true,
       title:title,
       //icon:iconType,
       waitConfig: {interval:200}
    });
    msgFlag=1;
    }
else{
    Wtf.MessageBox.show({
        title: strobj[0],
        msg: strobj[1],
        width:width,
        buttons: Wtf.MessageBox.OK,
        animEl: 'mb9',
        icon: iconType
    });
    msgFlag=0;
}
}
