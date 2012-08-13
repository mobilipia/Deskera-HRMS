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
 function ResponseAlert(choice) {
    var strobj = [];
    switch (choice) {
 /*       case 0:
            strobj = ["","The template has been deleted successfully."];
            break;
        case 1:
            strobj = ["","The template has been saved."];
            break;
        case 2:
            strobj = ["","The template has been updated successfully."];
            break;

        case 3:
            strobj = ["","Activity has been saved successfully."];
            break;
        case 4:
            strobj = ["","Opportunity has been saved successfully."];
            break;
        case 5:
            strobj = ["","Contact has been saved successfully."];
            break;
        case 6:
            strobj = ["","Case has been saved successfully."];
            break;
        case 7:
            strobj = ["","Account has been saved successfully."];
            break;
        case 8:
            strobj = ["","Activity has been saved successfully."];
            break;
        case 9:
            strobj = ["","Lead has been saved successfully."];
            break;
        case 10:
            strobj = ["","Product has been saved successfully."];
            break;
        case 11:
            strobj = ["","Campaign has been saved successfully."];
            break;

        case 12:
            strobj = ["","Select a column to search"];
            break;
        case 13:
            strobj = ["","Please specify a Search Term"];
            break;
        case 14:
            strobj = ["","Select a Search Term and add it to get search results."];
            break;

        case 15:
            strobj = ["","Please select dates to filter results"];
            break;
        case 16:
            strobj = ["'From Date' cannot be greater than 'To Date'"];
            break;

        case 17:
            strobj = ["", "The selected Campaign(s) has been deleted successfully."];
            break;
        case 18:
            strobj = ["", "Sorry! The selected Campaign(s) could not be deleted. Please try again."];
            break;
        case 19:
            strobj = ["", "Please select a Campaign to delete."];
            break;

        case 20:
            strobj = ["", "The selected Lead(s) has been deleted successfully."];
            break;
        case 21:
            strobj = ["", "Sorry! The selected Lead(s) could not be deleted. Please try again."];
            break;
        case 22:
            strobj = ["", "Please select a Lead to delete."];
            break;

        case 23:
            strobj = ["", "The selected Contact(s) has been deleted successfully."];
            break;
        case 24:
            strobj = ["", "Sorry! The selected Contact(s) could not be deleted. Please try again."];
            break;
        case 25:
            strobj = ["", "Please select a Contact to delete."];
            break;

        case 26:
            strobj = ["", "The selected Product(s) has been deleted successfully."];
            break;
        case 27:
            strobj = ["", "Sorry! The selected Product(s) could not be deleted. Please try again."];
            break;
        case 28:
            strobj = ["", "Please select a Product to delete."];
            break;

        case 29:
            strobj = ["", "The selected Account(s) has been deleted successfully."];
            break;
        case 30:
            strobj = ["", "Sorry! The selected Account(s) could not be deleted. Please try again."];
            break;
        case 31:
            strobj = ["", "Please select an Account to delete."];
            break;

        case 32:
            strobj = ["", "The selected Opportunity has been deleted successfully."];
            break;
        case 33:
            strobj = ["", "Sorry! The selected Opportunity could not be deleted. Please try again."];
            break;
        case 34:
            strobj = ["", "Please select an Opportunity to delete."];
            break;

        case 35:
            strobj = ["", "The selected Case(s) has been deleted successfully."];
            break;
        case 36:
            strobj = ["", "Sorry! The selected Case(s) could not be deleted. Please try again."];
            break;
        case 37:
            strobj = ["", "Please select a Case to delete."];
            break;

        case 38:
            strobj = ["", "The selected Activity has been deleted successfully."];
            break;
        case 39:
            strobj = ["", "Sorry! The selected Activity could not be deleted. Please try again."];
            break;
        case 40:
            strobj = ["", "Please select an Activity to delete."];
            break;

        case 41:
            strobj = ["", "Document has been uploaded successfully."];
            break;
        case 42:
            strobj = ["", "Sorry! Document could not be uploaded successfully. Please try again."];
            break;
        case 43:
            strobj = ["", "Please select a file to upload."];
            break;

        case 44:
            strobj = ["", "Comment has been added successfully."];
            break;
        case 45:
            strobj = ["", "Sorry! Comment could not be saved successfully. Please try again."];
            break;

        case 46:
            strobj = ["", "The selected Lead(s) has been converted successfully."];
            break;
        case 47:
            strobj = ["", "Sorry! Lead could not be converted successfully. Please try again."];
            break;
        case 48:
            strobj = ["", "Please select a Pre-Qualified Lead to convert."];
            break;
        case 49:
            strobj = ["", "Please select a Lead to convert."];
            break;
        case 50:
            strobj = ["", "Please select only one Lead to convert."];
            break;
        case 51:
            strobj = ["", "Email has been sent successfully to the selected Target List"];
            break;
        case 52:
            strobj = ["", "The selected Target has been deleted successfully."];
            break;
        case 53:
            strobj = ["", "Sorry! Target could not be deleted successfully. Please try again."];
            break;
        case 54:
            strobj = ["", "Please select a Target to delete."];
            break;
        case 55:
            strobj = ["", "Email Template has been deleted successfully."];
            break;
        case 56:
            strobj = ["", "Sorry! E-mail Template could not be deleted successfully. Please try again."];
            break;
        case 57:
            strobj = ["", "Please select an Email Template to delete."];
            break;
        case 58:
            strobj = ["","'From Date' cannot be greater than 'To Date'"];
            break;*/
        case 59:
            strobj = ["", WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow214")];
            break;
        case 60:
            strobj = ["", WtfGlobal.getLocaleText("hrms.Messages.calMsgBoxShow215")];
            break;
/*        case 61:
            strobj = ["","Sorry! E-mail Template could not be saved successfully. Please try again."];
            break;
        case 62:
            strobj = ["","Sorry! E-mail Template could not be updated successfully. Please try again."];
            break;
        case 63:
            strobj = ["","Please enter a Name for the Target list."];
            break;
        case 64:
            strobj = ["","Target List has been deleted successfully."];
            break;
        case 65:
            strobj = ["","Sorry! Target List could not be deleted successfully. Please try again."];
            break;
        case 66:
            strobj = ["","Please select a Target List to delete."];
            break;
        case 67:
            strobj = ["","Please select a Target List to edit."];
            break;
        case 70:
            strobj = ["","Select Campaign Type as 'Email Campaign' for adding Email Marketing."];
            break;
        case 71:
            strobj = ["","Please select a Campaign."];
            break;
        case 72:
            strobj = ["","Please select an Email Marketing to edit."];
            break;
        case 73:
            strobj = ["","Target List is empty. Please import/add targets to save the list. "];
            break;
        case 80:
            strobj = ["", "Lead status cannot be changed to Qualified, unless it is converted into Opportunity or Account."];
            break;
        case 81:
            strobj = ["", "Qualified Lead cannot be updated."];
            break;
        case 82:
            strobj = ["", "Please select a Delimiter type for CSV."];
            break;
        case 83:
            strobj = ["", "Please upload a file with valid file type."];
            break;
        case 84:
            strobj = ["","'Start time' cannot be greater than 'End time' for same date."];
            break;
        case 101:
            strobj = ["","Please enter some information to add comment."];
            break;
        case 151:
            strobj = ["", "Please select an Account or Opportunity with Contact to convert the selected Lead."];
            break;*/
        case 152:
            strobj = ["",  WtfGlobal.getLocaleText("hrms.Messages.msgBoxShow152")];
            break;
        /*case 153:
            strobj = ["", "Please select a Parent Account for Opportunity."];
            break;
        case 154:
            strobj = ["", "Please enter an Account name."];
            break;
        case 200:
            strobj = ["", "Processing your request."];
            break;
        case 201:
            strobj = ["", "Template has been already saved."];
            break;
        case 250:
            strobj = ["","The template has been successfully edited and downloaded."];
            break;
        case 300:
            strobj = ["","Help tooltip has been added successfully."];
            break;
        case 301:
            strobj = ["", "Sorry! Help tooltip could not be saved successfully. Please try again."];
            break;
        case 351:
            strobj = ["", "Selected Google Contacts have been imported successfully."];
            break;
        case 352:
            strobj = ["", "Sorry! Google Contacts could not be imported successfully. Please try again."];
            break;
        case 353:
            strobj = ["", "Google Contacts have been imported successfully."];
            break;
        case 400:
            strobj = ["", "Test Email has been sent successfully to you."];
            break;
        case 401:
            strobj = ["", "Sorry! Test E-mail could not be sent successfully. Please try again."];
            break;
        case 500:
            strobj = ["Loading...", ""];
            break;
        case 501:
            strobj = ["Reloading...", ""];
            break;
        case 510:
            strobj = ["", "There are no rules to apply."];
            break;
        case 550:
            strobj = ["", "You do not have required permission to download document."];
            break;
        case 551:
            strobj = ["", "There are no formulae to apply."];
            break;
        case 552:
            strobj = ["", "Invalid records selected to export."];
            break;
        case 600:
            strobj = ["", "Please select a Template."];
            break;
        case 630:
            strobj = ["", "Schedule Email Marketing have been saved successfully."];
            break;
        case 631:
            strobj = ["", "Sorry! Schedule Email Marketing could not be saved successfully. Please try again."];
            break;
        case 650:
            strobj = ["", "Company preferences have been saved successfully."];
            break;
        case 651:
            strobj = ["", "Sorry! Company preferences could not be saved successfully. Please try again."];
            break;
        case 652:
            strobj = ["","Please select either dates or industry to filter results"];
            break;
        case 653:
            strobj = ["","Please select either dates or lead source to filter results"];
            break;
        case 700:
            strobj = ["","Target has been saved successfully."];
            break;
        case 751:
            strobj = ["","Goals added successfully."];
            break;
        case 752:
            strobj = ["","Sorry! Goal could not be saved successfully. Please try again."];
            break;
        case 753:
            strobj = ['', 'Goals deleted successfully'];
            break;
        case 754:
            strobj = ['', "Sorry! Goals could not be deleted successfully. Please try again."];
            break;
       case 800:
            strobj = ["", "Please select an employee to add Target."];
            break;
       case 810:
            strobj = ["", "Please select main owner."];
            break;
       case 811:
            strobj = ["", "Owners have been saved successfully."];
            break;
       case 812:
            strobj = ["", "Sorry! Owners could not be saved successfully. Please try again."];
            break;
       case 813:
            strobj = ["", "Project created successfully."];
            break;
       case 814:
            strobj = ["", "Sorry! Project could not be saved successfully. Please try again."];
            break;
*/
        default:
            strobj = [choice[0], choice[1]];
            break;
    }
    Wtf.notify.msg(strobj[0],strobj[1]);
}



