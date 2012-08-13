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

package com.krawler.common.util;


public interface HrmsMsgs {
     String jobSubject="[%s] Application for %s submitted.";
     String jobPlnmsg="Hi %s,\n\nThank you so much for taking the time to apply for the position of %s.\n"+
            "We will forward your resume to the appropriate hiring manager for further review. If your resume get shortlisted, we will get in touch with you.\n" 
             +"With best wishes,\nRecruitment Team at %s";
     String jobHTMLmsg="<html><head><title>Jobs Application Details</title></head><style type='text/css'>"
                                + "a:link, a:visited, a:active {\n"
                                + " 	color: #03C;"
                                + "}\n"
                                + "body {\n"
                                + "	font-family: Arial, Helvetica, sans-serif;"
                                + "	color: #000;"
                                + "	font-size: 13px;"
                                + "}\n"
                                + "</style><body>"
                                + "	<div>"
                                + "		<p>Dear Candidate,</p>"
                                + "		<p>Thank you so much for taking the time to apply for  the position of  %s at %s." +
                                " We will forward your resume to the appropriate hiring manager for further review." +
                                " If your resume get shortlisted, we will get in touch with you.</p>"
                                + "<p>Thanks again for applying.</p>"
                                +"<p>With best wishes,<br>Recruitment Team at %s</p>"                                
                                + "	</div></body></html>";
     String interviewSubject="Interview Details for [%s] %s at %s";
     String interviewinviteSubject="Appointment as an interviewer for [%s] %s at %s";
     String interviewPlnmsg="Dear Candidate,\n\nYour job application for the following job:\n%s\nhas been shortlisted.and " +
             "Your interview for this application has been scheduled on %s on %s\nat %s ";
     String interviewHTMLmsg="<html><head><title>Interview Schedule</title></head><style type='text/css'>"
                                + "a:link, a:visited, a:active {\n"
                                + " 	color: #03C;"
                                + "}\n"
                                + "body {\n"
                                + "	font-family: Arial, Helvetica, sans-serif;"
                                + "	color: #000;"
                                + "	font-size: 13px;"
                                + "}\n"
                                + "</style><body>"
                                + "	<div>"
                                + "		<p>Dear <strong>%s</strong>,</p>"
                                + "		<p>Thank you for applying for the position <b>%s</b> with %s.<br></p>"
                                + "		<p>This letter is to confirm and schedule your interview for this position.  Your interview has been scheduled on <b>%s %s, %s</b>.<br></p>"
                                + "		<p>Please note that if you do not appear for this interview, we will conclude that you are not available for this position.</p> " +
                                "<p>Please contact us at %s or e-mail us at %s  if you have any questions. </p>"
                                + "		<p>Sincerely,<br>"
                                + "		%s,<br>%s,<br>%s</p>"
                                + "	</div></body></html>";
    String msgMailInviteUsernamePassword = "<html><head><title>Deskera HRMS - Your Deskera HRMS Account</title></head><style type='text/css'>"
                                + "a:link, a:visited, a:active {\n"
                                + " 	color: #03C;"
                                + "}\n"
                                + "body {\n"
                                + "	font-family: Arial, Helvetica, sans-serif;"
                                + "	color: #000;"
                                + "	font-size: 13px;"
                                + "}\n"
                                + "</style><body>"
                                + "	<div>"
                                + "		<p>Hi <strong>%s</strong>,</p>"
                                + "		<p>%s has created an account for you at %s.</p>"                               
                                + "		<p>Username: <strong>%s</strong> </p>"
                                + "               <p>Password: <strong>%s</strong></p>"
                                + "		<p>You can log in to Deskera HRMS at: <a href=%s>%s</a>.</p>"
                                + "		<br><p>See you on Deskera HRMS!</p><p> - The Deskera HRMS Team</p>"
                                + "	</div></body></html>";
     String interviewinvitePlnmsg="Dear %s,\n\nYou have been appointed on the interview panel for the following job:\n%s\nhas been shortlisted.and " +
             "Interview Details: Date: %s at  %s\n Place %s ";
     String interviewinviteHTMLmsg="<html><head><title>Appointment as a Interviewer</title></head><style type='text/css'>"
                                + "a:link, a:visited, a:active {\n"
                                + " 	color: #03C;"
                                + "}\n"
                                + "body {\n"
                                + "	font-family: Arial, Helvetica, sans-serif;"
                                + "	color: #000;"
                                + "	font-size: 13px;"
                                + "}\n"
                                + "</style><body>"
                                + "	<div>"
                                + "		<p>Hi <strong>%s</strong>,</p>"
                                + "		<p>You have been appointed on the interview panel for the following job:<br><b>%s</b>.</p>"
                                +"<p>Interview Details:<br> Date and Time:&nbsp; <b>%s at %s</b>.<br>Place:&nbsp; <b>%s</b>"
                                + "		<p>Sincerely,<br>"
                                + "		%s,<br>%s,<br>%s</p>"
                                + "	</div></body></html>";
      String rejectPlnmsg="Dear Candidate,\n\nYour job application for the following job:\n%s\nhas been rejected. " ;
      String rejectHTMLmsg="<html><head><title>Job Application Status</title></head><style type='text/css'>"
                                + "a:link, a:visited, a:active {\n"
                                + " 	color: #03C;"
                                + "}\n"
                                + "body {\n"
                                + "	font-family: Arial, Helvetica, sans-serif;"
                                + "	color: #000;"
                                + "	font-size: 13px;"
                                + "}\n"
                                + "</style><body>"
                                + "	<div>"                               
                                + "		<p>Dear Candidate,</p>"
                                + "		<p>Thank you so much for taking the time to apply for a position at %s." +
                                "During the course of our recruiting efforts, we come across many fine candidates such as you," +
                                "and we carefully evaluate each candidate's background and interests against our projected workloads and staffing needs." +
                                "Although we are impressed with your background, the hiring committee has decided to move forward with a different candidate.</p>"
                                + "<p>We will keep your information on file for six months in case future opportunities arise.</p>"
                                +"<p>With best wishes,<br>Recruitment Team at %s</p>"
                                + "	</div></body></html>";
      String rejectSubject="Job Application Details";
      String appointmentSubject="Job Offer";
      String appointmentHTMLmsg="<html><head><title>Job Offer</title></head><style type='text/css'>"
                                + "a:link, a:visited, a:active {\n"
                                + " 	color: #03C;"
                                + "}\n"
                                + "body {\n"
                                + "	font-family: Arial, Helvetica, sans-serif;"
                                + "	color: #000;"
                                + "	font-size: 13px;"
                                + "}\n"
                                + "</style><body>"
                                + "	<div>"
                                + "<p align='right'><b>%s</b>,<br>"
                                + "<b>%s</b>.</p>"
                                + "		<p>Dear <strong>%s</strong>,</p>"
                                + "		<p>It is my pleasure to  offer employment to you on behalf of <strong>%s</strong> for position <strong>%s</strong>.</p>"
                                + "		<p>%s.</p>"
                                +"<p>You will need to submit all your original qualification documents, relieving documents and salary slip (if any) of last three months with a copy of each, on the date of joining.</p>"
                                +"<p>Please,send your confirmation for this position.</p>"
                                + "		<p>Sincerely,<br>"
                                + "		%s,<br>%s,<br>%s.</p>"
                                + "	</div></body></html>";
       String appointmentPlnmsg="Dear Candidate,\n\nYou are selected for position %s.%s. " ;
       String mngrPlnmsg="Dear %s,\n\nFollowing candidate has been selected for position %s :<br>%s." ;
       String mangrHTMLmsg="<html><head><title>Job Position Details</title></head><style type='text/css'>"
                                + "a:link, a:visited, a:active {\n"
                                + " 	color: #03C;"
                                + "}\n"
                                + "body {\n"
                                + "	font-family: Arial, Helvetica, sans-serif;"
                                + "	color: #000;"
                                + "	font-size: 13px;"
                                + "}\n"
                                + "</style><body>"
                                + "	<div>"
                               + "<p align='right'><b>%s</b>,<br>"
                                + "<b>%s</b>.</p>"
                                + "		<p>Dear <strong>%s</strong>,</p>"
                                + "		<p>Following candidate has been selected for job position <b>%s</b>.<br><b>%s</b>.</p>"
                                + "		<p>Sincerely,<br>"
                                + "		%s,<br>%s,<br>%s.</p>"
                                + "	</div></body></html>";
       String mngrSubject="Job Position Details";
       String interviewerSelectionpln="Dear %s,\n\nYou are selected as interviewer.";
       String interviewerSelectionHTML="<html><head><title>Interviewer Request</title></head><style type='text/css'>"
                                + "a:link, a:visited, a:active {\n"
                                + " 	color: #03C;"
                                + "}\n"
                                + "body {\n"
                                + "	font-family: Arial, Helvetica, sans-serif;"
                                + "	color: #000;"
                                + "	font-size: 13px;"
                                + "}\n"
                                + "</style><body>"
                                + "	<div>"                                
                                +"<p>Dear <strong>%s</strong>,</p>"
                                +"<p>You have been selected to be on an interview panel.<br>Please click on following links for confirmation as an interviewer.</p>"
                                +"<p><a href=%s>Accept</a>&nbsp;&nbsp;<a href=%s>Reject</a></p>"
                                + "		<p>Sincerely,<br>"
                                + "		%s,<br>%s.</p>"
                                + "	</div></body></html>";
        String interviewerSubject="Interviewer Request";
       String Thankspln="Dear %s,\n\nThank you for your confirmation as an interviewer.";
       String ThanksHTML="<html><head><title>Thank you for Confirmation</title></head><style type='text/css'>"
                                + "a:link, a:visited, a:active {\n"
                                + " 	color: #03C;"
                                + "}\n"
                                + "body {\n"
                                + "	font-family: Arial, Helvetica, sans-serif;"
                                + "	color: #000;"
                                + "	font-size: 13px;"
                                + "}\n"
                                + "</style><body>"
                                + "	<div>"
                                +"<p>Dear <strong>%s</strong>,</p>"
                                +"<p>Thank you for your confirmation as an interviewer.</p>"
                                + "		<p>Sincerely,<br>"
                                + "		%s,<br>%s,<br>%s.</p>"
                                + "	</div></body></html>";
        String ThanksSubject="Thank you for Confirmation ";
        String transferSubject=" Joining /Acceptance Letter";
        String transferHTMLmsg="<html><style type='text/css'>"
                                + "a:link, a:visited, a:active {\n"
                                + " 	color: #03C;"
                                + "}\n"
                                + "body {\n"
                                + "	font-family: Arial, Helvetica, sans-serif;"
                                + "	color: #000;"
                                + "	font-size: 13px;"
                                + "}\n"
                                + "</style><body>"
                                +"Dear Mr./Mrs. %s"
                                +"<br><p>I am pleased to inform you that you will be joining the company from %s as a/an %s."
                                +"As an employee you will be having access to our Personnel administration system that will "
                                +"enable you to access all the information and smoothly carry on your your services. "
                                +"Following are the login details for your reference:</p></br><br><br>"
                                + "	<div>"
                                + "<p>Login name: %s</p>" +
                                "<p>Password: %s</p>"+
                                "<p>The position is ideally suited to your educational background and interests. " +
                                "I confidently feel that you can make a significant contribution to our company.</p> ";
     String transferPLAINmsg="<html><style type='text/css'>"
                                + "a:link, a:visited, a:active {\n"
                                + " 	color: #03C;"
                                + "}\n"
                                + "body {\n"
                                + "	font-family: Arial, Helvetica, sans-serif;"
                                + "	color: #000;"
                                + "	font-size: 13px;"
                                + "}\n"
                                + "</style><body>"
                                +"Dear Mr./Mrs. %s"
                                +"<br><p>I am pleased to inform you that you will be joining the company from %s as a/an %s."
                                +"As an employee you will be having access to our Personnel administration system that will "
                                +"enable you to access all the information and smoothly carry on your your services. "
                                +"Following are the login details for your reference:</p></br><br><br>"
                                + "	<div>"
                                + "<p>Login name: %s</p>" +
                                "<p>Password: %s</p>"+
                                "<p>The position is ideally suited to your educational background and interests. " +
                                "I confidently feel that you can make a significant contribution to our company.</p> ";
     String rescheduleinterviewSubject="Interview Rescheduled Details for [%s] %s at %s";
     String rescheduleinterviewPlnmsg="Dear %s,\n\nThank you for applying for the position %s with the %s."
                                +"\nThis letter is to inform you that your interview for this position has been rescheduled on %s, %s, %s."
                                +"\nPlease note that if you do not appear for this interview, we will conclude that you are not available for this position."
                                +"Please contact us at %s or e-mail us at %s  if you have any questions."
                                + "\nSincerely,\n"
                                + "%s,\n%s,\n%s\n";
     String rescheduleinterviewHTMLmsg="<html><head><title>Interview Rescheduled</title></head><style type='text/css'>"
                                + "a:link, a:visited, a:active {\n"
                                + " 	color: #03C;"
                                + "}\n"
                                + "body {\n"
                                + "	font-family: Arial, Helvetica, sans-serif;"
                                + "	color: #000;"
                                + "	font-size: 13px;"
                                + "}\n"
                                + "</style><body>"
                                + "	<div>"
                                + "		<p>Dear <strong>%s</strong>,</p>"
                                + "		<p>Thank you for applying for the position <b>%s</b> with %s.<br/></p>"
                                + "		<p>This letter is to inform you that your interview for this position has been rescheduled on <b>%s, %s, %s</b>.<br/></p>"
                                + "		<p>Please note that if you do not appear for this interview, we will conclude that you are not available for this position.</p> " +
                                "<p>Please contact us at %s or e-mail us at %s  if you have any questions. </p>"
                                + "		<p>Sincerely,<br>"
                                + "		%s,<br>%s,<br>%s</p>"
                                + "	</div></body></html>";
}
