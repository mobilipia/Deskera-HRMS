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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title id="Deskerahrmstitle">HRMS</title>
		<script type="text/javascript">
		/*<![CDATA[*/
			function _r(url){ window.top.location.href = url;}
		/*]]>*/
		</script>
<!-- css -->
		<link rel="stylesheet" type="text/css" href="http://apps.deskera.com/lib/resources/css/wtf-all.css?v=3.4.0"/>
		<link rel="stylesheet" type="text/css" href="../../style/HQLHrms.css?v=3.4.0"/>
	<!--[if lte IE 6]>
		<link rel="stylesheet" type="text/css" href="../../style/ielte6hax.css" />
	<![endif]-->
	<!--[if IE 7]>
		<link rel="stylesheet" type="text/css" href="../../style/ie7hax.css" />
	<![endif]-->
	<!--[if IE 8]>
		<link rel="stylesheet" type="text/css" href="../../style/ie8hax.css" />
	<![endif]-->
<!-- /css -->
		<link rel="shortcut icon" href="../../images/deskera.png"/>
	</head>
	<body>
		<div id="loading-mask" style="width:100%;height:100%;background:#c3daf9;position:absolute;z-index:20000;left:0;top:0;">&#160;</div>
		<div id="loading">
			<div class="loading-indicator"><img src="../../images/loading.gif" style="width:16px;height:16px; vertical-align:middle" alt="Loading" />&#160;Loading...</div>
		</div>
<!-- js -->
		<script type="text/javascript" src="http://apps.deskera.com/lib/adapter/wtf/wtf-base.js?v=3.4.0"></script>
		<script type="text/javascript" src="http://apps.deskera.com/lib/wtf-all.js?v=3.4.0"></script>
		<script type="text/javascript" src="../../props/wtf-lang-locale.js?v=3.4.0"></script>
        <script type="text/javascript" src="../../props/msgs/messages.js?v=3.4.0"></script>
		<script type="text/javascript" src="../../scripts/HQLHrms.js?v=3.4.0"></script>
		<script type="text/javascript" src="../../scripts/HQLPayroll.js?v=3.4.0"></script>
		<!--script type="text/javascript" src="../../scripts/dashboard-ex.js?v=9"></script-->
		<script type="text/javascript">
		/*<![CDATA[*/
			PostProcessLoad = function(){
				setTimeout(function(){Wtf.get('loading').remove(); Wtf.get('loading-mask').fadeOut({remove: true});}, 250);
				Wtf.EventManager.un(window, "load", PostProcessLoad);
			}
			Wtf.EventManager.on(window, "load", PostProcessLoad);
		/*]]>*/
		</script>
<!-- /js -->
<!-- html -->
		<div id="header" style="position: relative;">
			  <img id="companyLogo" src="http://apps.deskera.com/b/<%=com.krawler.common.util.URLUtil.getDomainName(request)%>/images/store/?company=true" alt="logo"/>
                        <img src="../../images/hrms-right-logo.gif" alt="hrms" style="float:left;margin-left:4px;margin-top:1px;"/>

			<div id="userinfo" class="userinfo">
				<span id="whoami"></span><br /><a id="whoami_sign_out" href="#" onclick="signOut('signout');"></a>&nbsp;&nbsp;<a id="whoami_show_person_profile" href="#" onclick="showPersnProfile();"></a>&nbsp;&nbsp;<a  id ="whoami_change_password" href="#" onclick="changepass();"></a>&nbsp;&nbsp;<a id="myProfile" href=# onclick='myProfile()'></a>&nbsp;&nbsp;<a href="#"  id="organisationlink" onclick="loadOrganizationPage();"></a>
			</div>
			<div id="serchForIco"></div>
			<div id="searchBar"></div>
			<div id="shortcuts" class="shortcuts">
                            <div id="shortcutmenu1"style="float:left !important;position: relative;"></div>
			</div>
		</div>
		<div id='centerdiv'></div>
		<div style="display:none;">
			<iframe id="downloadframe"></iframe>
		</div>
        <input id="cursor_bin" type="text" style="display:none;"/>
<!-- /html -->
	</body>
</html>
