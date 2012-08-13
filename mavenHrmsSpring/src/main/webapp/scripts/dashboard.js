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
var widgetcount = 0;

function openDownloadWindow(fileName,isDownlodable,folderName ){
    if (typeof folderName == "undefined") {
        folderName = "libraryfiles";
    }

    if(isDownlodable != false){
        fileName=escape(fileName);
        var url = encodeURI("FileOperations?filename=" + fileName + "&mode=18&folder=" + folderName);
       document.getElementById("downloadframe").src = url;
    } else {
        Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.error"),WtfGlobal.getLocaleText({key:"hrms.Dashboard.Youdontnothavepermissiontoaccess",params:["<b>"+fileName+"</b>"]}));
    }
}
function openDownloadWindow_postfix(fileName,isDownlodable,folderName,postfix ){
    //Opens the window to download a file
    if (typeof folderName == "undefined") {
        folderName = "libraryfiles";
    }
    fileName=unescape(fileName);
    if(isDownlodable != false){
        fileName=escape(fileName);
        var url = encodeURI("FileOperations?filename=" + fileName + "&mode=19&folder=" + folderName + "&postfix=" + postfix);
       document.getElementById("downloadframe").src = url;
    } else {
        Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.error"),WtfGlobal.getLocaleText({key:"hrms.Dashboard.Youdontnothavepermissiontoaccess",params:["<b>"+fileName+"</b>"]}));
    }
}
function openDownloadWindowForMyDocs(fileName,path){
    fileName=escape(fileName);
    path=escape(path);
    document.getElementById("downloadframe").src = "jspfiles/s3docs.jsp?mode=2&fileName="+
    "&pwd="+path;
}

function openWorkspace(recordIndex){
    var workspaceid = Wtf.getCmp("DSBtags").tagstore.getAt(recordIndex).get("workspaceid");
    var workspacename = Wtf.getCmp("DSBtags").tagstore.getAt(recordIndex).get("book");
    mainPanel.loadTab("communityHome.html","   " + workspaceid,workspacename,"navareadashboard",1,true);
}

function pagingRedirect(panelid, pager, subPan, searchstr, panelcount){
	var myPanel = Wtf.getCmp(panelid);
	myPanel.doPaging(myPanel.config1[subPan].url, (panelcount * pager), searchstr, pager, subPan);
}

function pagingRedirect1(panelid, pager, subPan, searchstr, panelcount){
	var myPanel = Wtf.getCmp(panelid);
	myPanel.doPaging(myPanel.config0[subPan].url, (panelcount * pager), searchstr, pager, subPan);
}

function quoteReplace(psString){
	var lsRegExp = /'|%/g;
	return String(psString).replace(lsRegExp, "");
}

function btnpressed(panelid){
	var searchid = "search" + panelid;
	var searchstr = document.getElementById(searchid).value;
	searchstr = quoteReplace(searchstr);
	var myPanel = Wtf.getCmp(panelid);
	myPanel.doSearch(myPanel.url, searchstr);
}

function createtooltip1(target, tpl_tool_tip, autoHide, closable, height){
	usertooltip = tpl_tool_tip, new Wtf.ToolTip({
		id: "1KChampsToolTip" + target,
		autoHide: autoHide,
		closable: closable,
		html: usertooltip,
		height: height,
		target: target
	});
}

function getToolsArrayForModules(code,managePerm) {
    var ta = [];
    var tip=listViewTips(code);
    ta.push({
        id:'updatewizardlink',
        qtip : tip.update,
        handler: function(e, target, panel) {
            openUpdate(panel.id,panel.id+'_updatelink');
        }
    });
    if(managePerm) {
        ta.push({
            id:'quickwizardlink',
            qtip : tip.addlink,
            handler: function(e, target, panel) {
                openQuickAdd(panel.id,panel.id+'_quickaddlink',code);
            }
        });
    }
    ta.push({
        id:'paichartwizard',
        qtip : tip.chart,
        handler: function(e, target, panel) {
            openGraph(panel.id,panel.id+'_graphlink');
        }
    });
//TODO
//    ta.push({
//        id:'barchartwizard',
//        qtip : tip.chart,
//        handler: function(e, target, panel) {
//            openBarGraph(panel.id,panel.id+'_graphlink');
//        }
//    });
    if(code != 6) {
        ta.push({
            id:'detailwizardlink',
            qtip : tip.detail,
            handler: function(e, target, panel) {
                switch(panel.code) {
                    case 0 : addCampaignTab();break;
                    case 1 : addLeadTab();break;
                    case 2 : addAccountTab();break;
                    case 3 : addContactTab();break;
                    case 4 : addOpportunityTab();break;
                    case 5 : addCaseTab();break;
                    case 6 : addActivityMasterTab();break;
                    case 7 : addProductMasterTab();break;
                }
            }
        });
    }
    ta.push({
        id: 'close',
        handler: function(e, target, panel){
            var tt = panel.title;
            panel.ownerCt.remove(panel, true);
            panel.destroy();
            removeWidget(tt);
        }
    });
    return ta;
}

function getToolsArray(ru,onlyRss){
	var ta = [];
	/*if (ru) {
		ta.push({
			id: onlyRss?'rss-white':'rss',
			handler: function(e, target, panel){
				window.open(ru, '_blank');
			}
		});
	}*/
    if(!onlyRss){
        ta.push({
            id: 'close',
            handler: function(e, target, panel){
                var tt = panel.title;
                panel.ownerCt.remove(panel, true);
                panel.destroy();
                removeWidget(tt);
            }
        });
    }
	return ta;
}

function listViewTips(code) {
    var toolTip=new Object();
    toolTip.detail="View";
    toolTip.update="View";
    toolTip.addlink="Quickly add";
    toolTip.chart="View";
    switch(code){
        case 0: toolTip.detail+=" Campaign";
                toolTip.update+=" Campaign";
                toolTip.addlink+=" campaigns";
                toolTip.chart+=" Campaign";
                break;
        case 1: toolTip.detail+=" Lead";
                toolTip.update+=" Leads";
                toolTip.addlink+=" leads";
                toolTip.chart+=" Lead";
                break;
        case 2: toolTip.detail+=" Account";
                toolTip.update+=" Account";
                toolTip.addlink+=" accounts";
                toolTip.chart+=" Account";
                break;
        case 3: toolTip.detail+=" Contact";
                toolTip.update+=" Contact";
                toolTip.addlink+=" contacts";
                toolTip.chart+=" Contact";
                break;
        case 4: toolTip.detail+=" Opportunity";
                toolTip.update+=" Opportunities";
                toolTip.addlink+=" opportunities";
                toolTip.chart+=" Opportunity";
                break;
        case 5: toolTip.detail+=" Cases";
                toolTip.update+=" Case";
                toolTip.addlink+=" cases";
                toolTip.chart+=" Case";
                break;
        case 6: toolTip.detail+=" Activity";
                toolTip.update+=" Activity";
                toolTip.addlink+=" activities";
                toolTip.chart+=" Activity";
                break;
        case 7: toolTip.detail+=" Product";
                toolTip.update+=" Product";
                toolTip.addlink+=" products";
                toolTip.chart+=" Product";
                break;
    }
    toolTip.detail+=" details";
    toolTip.update+=" updates";
    toolTip.chart+=" report";
    return toolTip;
}

function createNewPanel(setting){
    if(setting !== undefined) {
        if (setting.config1 != null) {
            return (new Wtf.WtfCustomPanel(setting));
        } else if (setting.config0 != null) {
            return (new Wtf.WtfCustomCrmPanel(setting));
        }
        else {
            if (setting.url != null) {
                return (new Wtf.WtfIframeWidgetComponent(setting));
            }
            else {
                return (new Wtf.WtfWidgetComponent(setting));
            }
        }
    }
}

function createWidget(ix){
    widgetcount--;
    if(widgetcount>1){
        titleSpan.innerHTML = '<span><span style="float:left;">'+WtfGlobal.getLocaleText("hrms.Dashboard.AddDashboardWidgets")+'</span><span>'+WtfGlobal.getLocaleText({key:"hrms.Dashboard.xWidget",params:[widgetcount]})+"</span></span>";
    }else{
        titleSpan.innerHTML = '<span><span style="float:left;">'+WtfGlobal.getLocaleText("hrms.Dashboard.AddDashboardWidgets")+'</span><span>'+WtfGlobal.getLocaleText({key:"hrms.Dashboard.xWidget",params:[widgetcount]})+"</span></span>";
    }
	var count = 0
	var lowCountCol = 1;
	var lowCount = 1;
	if (Wtf.getCmp("portal_container_box3").items != null) {
		lowCount = Wtf.getCmp("portal_container_box3").items.length;
	}
	else
		if (Wtf.getCmp("portal_container_box2").items != null) {
			lowCount = Wtf.getCmp("portal_container_box2").items.length;
		}
		else
			if (Wtf.getCmp("portal_container_box1").items != null) {
				lowCount = Wtf.getCmp("portal_container_box1").items.length;
			}

	for (var i = 3; i > 0; i--) {
        count=0;
        if (Wtf.getCmp("portal_container_box"+i).items != null){
            count = Wtf.getCmp("portal_container_box" + i).items.length;
        }
		if (count <= lowCount) {
			lowCount = count;
			lowCountCol = i;
		}
	}

	var pl = Wtf.getCmp("portal_container_box" + lowCountCol);
	if (pl != null) {
		var pn = createNewPanel(panelArr[ix]);
		pl.add(pn);
		pl.doLayout();
		var t = Wtf.get("lix_" + ix);
		t.remove();
	}
    insertIntoWidgetState(lowCountCol,widgetIdArray[ix]);
}
function insertIntoWidgetState(colno,wid){
    Wtf.Ajax.requestEx({
        url: Wtf.req.base + 'widget.jsp',
        params:{
            flag:3,
            wid:wid,
            colno:colno
        }
    }, this, function(){
    }, function(){})

}
function removeWidget(tt){
	var ix = widgetArr.indexOf(WtfGlobal.HTMLStripper(tt));
    requestForWidgetRemove(widgetIdArray[ix]);
	appendWidget(ix);
}
function widgetTooltip(name){
    var tip="";
    switch(name){
        case 'Campaigns': tip="Maintain comprehensive details of marketing initiatives such as an advertisement, direct mail, or conference that you conduct in order to generate prospects and build brand awareness.";
            break;
        case 'Accounts': tip="Maintain comprehensive details of the organization or company you want to track such as customers, partners, or competitors. Easily track your existing customers as well as prospective clients.";
            break;
        case 'Opportunities': tip="Maintain complete information related to specific sales and pending deals that needs to be cracked. Add to that, you can record, all the related contacts and activities information for each opportunity.";
            break; 
        case 'Cases': tip="Capture detailed description of a customer\'s feedback, problem or questions. Effectively manage cases through regular tracking of customer queries.";
            break;
        case 'Activities': tip="Maintain complete details of all activities including tasks and events associated with existing and prospective customers.";
            break;
        case 'Products': tip="Maintain comprehensive details of items or services that you sell to your customers. You can also record associated vendor details here.";
            break;
        case 'Leads': tip="Capture all relevant information on potential sales opportunities or prospects i.e. individuals who have expressed some interest in your product or company.";
            break;
        case 'Contacts': tip="Maintain complete information about the individuals you know in an account and interact with.";
            break;
    }
    return tip;
}

function appendWidget(ix){
    widgetcount++;
    if(widgetcount>1){
        titleSpan.innerHTML = '<span><span style="float:left;">'+WtfGlobal.getLocaleText("hrms.Dashboard.AddDashboardWidgets")+'</span><span>'+WtfGlobal.getLocaleText({key:"hrms.Dashboard.xWidget",params:[widgetcount]})+"</span></span>";
    }else{
        titleSpan.innerHTML = '<span><span style="float:left;">'+WtfGlobal.getLocaleText("hrms.Dashboard.AddDashboardWidgets")+'</span><span>'+WtfGlobal.getLocaleText({key:"hrms.Dashboard.xWidget",params:[widgetcount]})+"</span></span>";
    }
    var _widgetname=widgetArr[ix].replace(" ","<br/>");
    var tip=widgetTooltip(_widgetname);
    var name_markup="<div class='widget_name'>"+_widgetname+"</div>";
    Wtf.DomHelper.append("widgetUl", "<li id='lix_" + ix +"' style='padding-left:15px !important;'><div wtf:qtip=\""+tip+"\" onclick='javascript:createWidget(" + ix +")' class='dashpwnd "+widgetIdArray[ix]+"'  ></div>"+name_markup+"</li>");
}

var widgetArr = [WtfGlobal.getLocaleText("hrms.Dashboard.Updates"),WtfGlobal.getLocaleText("hrms.Dashboard.Alerts"),WtfGlobal.getLocaleText("hrms.Dashboard.Links"), WtfGlobal.getLocaleText("hrms.common.saved.searches")];
//if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.appraisal))
if(!WtfGlobal.EnableDisable(Wtf.UPerm.useradmin, Wtf.Perm.useradmin.view)||
    !WtfGlobal.EnableDisable(Wtf.UPerm.masterconf, Wtf.Perm.masterconf.view)||
!WtfGlobal.EnableDisable(Wtf.UPerm.setappcycle, Wtf.Perm.setappcycle.view)||
!WtfGlobal.EnableDisable(Wtf.UPerm.audittrail, Wtf.Perm.audittrail.view)) {
  widgetArr.push(WtfGlobal.getLocaleText("hrms.Dashboard.Administration"));
}    
if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.recruitment))
    widgetArr.push(WtfGlobal.getLocaleText("hrms.Dashboard.RecruitmentManagement"));
if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.payroll))
    widgetArr.push(WtfGlobal.getLocaleText("hrms.Dashboard.Payroll"));
if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.timesheet))
    widgetArr.push(WtfGlobal.getLocaleText("hrms.Dashboard.TimesheetManagement"));
if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.appraisal))
    widgetArr.push(WtfGlobal.getLocaleText("hrms.Dashboard.PerformanceAppraisal"));


var panelArr = [];

var _workspacelinks="<div id ='workspaceButtons' style='float:right;padding-top:3px;padding-right:5px;color:#AAA;'>";
_workspacelinks+="<a href='javascript:myworkspace();'>"+WtfGlobal.getLocaleText("hrms.common.Details")+"</a>";
//if(EnableDisable(Wtf.UPerm.Workspace, Wtf.Perm.Workspace.WorkspaceRequests)){
    _workspacelinks+="<a href='javascript:createworkspace(1);'>| "+WtfGlobal.getLocaleText("hrms.common.CreateNew")+"</a>";
//}
_workspacelinks+="</div>";

//if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.myworkspaces_widget)){
panelArr.push({
            config1:[{
//                url:'jspfiles/knowledgeUni/workspace.jsp',
//                url : Wtf.req.base + 'widget.jsp',
                url:"Dashboard/getUpdatesForWidgets.dsh",
                numRecs:5,
                template:new Wtf.XTemplate(
                            "<tpl><div class='workspace'>"+
                            "<div style='overflow:hidden;'>" +
                                "<div style='padding-left:15px; background:transparent no-repeat scroll 0 0;'>{desc}</div>" +
                                "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
                            "</div>" +
                          "</div></tpl>"
                ),
                isPaging: true,
                emptyText:WtfGlobal.getLocaleText("hrms.Dashboard.NoUpdates"),
                isSearch: false,
                headerHtml : '',
//                WorkspaceLinks :_workspacelinks,
                paramsObj: {flag:5,searchField:'name'}

            }],
            id : "DSBMyWorkspaces",
            draggable:true,
            border:true,
            title: "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.Dashboard.Clickdragandplacewidgetanywhereonthedashboard")+"\">"+WtfGlobal.getLocaleText("hrms.Dashboard.Updates")+"<a href='#' id='refreshiconupdates' title="+WtfGlobal.getLocaleText("hrms.Dashboard.Loading")+" onclick='reloadUpdates();' class='refreshWidgets'></a></div>",
            tools: getToolsArray('jspfiles/rss.jsp?i=my_workspaces&id=' + loginid)
});
panelArr.push({
    config1:[{
//        url:'jspfiles/knowledgeUni/workspace.jsp',
//        url : Wtf.req.base + 'widget.jsp',
        url:"Dashboard/getAlertsForWidgets.dsh",
        numRecs:5,
        template:new Wtf.XTemplate(
                    "<tpl><div class='workspace'>"+
                    "<div style='overflow:hidden;'>" +
                        "<div style='padding-left:15px; background:transparent no-repeat scroll 0 0;'>{desc}</div>" +
                        "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{alerts}</div>" +
                    "</div>" +
                  "</div></tpl>"
        ),
        isPaging: true,
        emptyText:WtfGlobal.getLocaleText("hrms.Dashboard.NoAlerts"),
        isSearch: false,
        headerHtml : '',
//        WorkspaceLinks :_workspacelinks,
        paramsObj: {flag:5,searchField:'name'}

    }],
    id : "DSBAlerts",
    draggable:true,
    border:true,
    title: "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.Dashboard.Clickdragandplacewidgetanywhereonthedashboard")+"\">"+WtfGlobal.getLocaleText("hrms.Dashboard.Alerts")+" <a href='#' id='refreshiconalerts' title="+WtfGlobal.getLocaleText("hrms.Dashboard.Loading")+" onclick='reloadAlerts();' class='refreshWidgets'></a></div>",
    tools: getToolsArray('jspfiles/rss.jsp?i=my_workspaces&id=' + loginid)
});
panelArr.push({
            config1:[{
                url:"Dashboard/getBookmarksForWidgets.dsh",
                numRecs:5,
                template:new Wtf.XTemplate(
                            "<tpl><div class='workspace'>"+
                            "<div style='overflow:hidden;'>" +
                                "<div style='padding-left:15px; background:transparent no-repeat scroll 0 0;'><div class='pwndExport exportpdf' ><a style='margin-left:25px;' href='{linkurl}' title='{linkname}' target='_blank' > {linkname} </a></div> </div>" +
                            "</div>" +
                          "</div></tpl>"
                ),
                isPaging: false,
                emptyText:WtfGlobal.getLocaleText("hrms.Dashboard.NoLinks"),
                isSearch: false,
                headerHtml : '',
                paramsObj: {flag:5,searchField:'linkname'}
            }],
            id : "DSBBookmarks",
            draggable:true,
            border:true,
            title: "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.Dashboard.Clickdragandplacewidgetanywhereonthedashboard")+"\">"+WtfGlobal.getLocaleText("hrms.Dashboard.Links")+"</div>",
            tools: getToolsArray('jspfiles/rss.jsp?i=my_workspaces&id=' + loginid)
});


panelArr.push({
            config1:[{
                url:"Dashboard/getSavedSearchesForWidgets.dsh",
                numRecs:5,
                template:new Wtf.XTemplate(
                            "<tpl><div class='workspace'>"+
                             "<div>" +
                             "<div onmouseover='javascript:showIcon(\"{searchid}\")' onmouseout='javascript:hideIcon(\"{searchid}\")' style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'><a class='DSBSavedSearches' onclick='openAdvanceSearchTab({module},\"{searchid}\")' href=#>{searchname}</a>" +
                                "<img class='stop' onclick='javascript:deleteAdvanceSearch(\"{searchid}\");'  alt='Delete' src='../../images/deleteLink.gif' style=' cursor:pointer; display:none; float: right; padding-left: 5px; padding-top: 3px;' id=\"{searchid}\" />"+
                            "</div></div>" +
                          "</div></tpl>"
                ),
                isPaging: true,
                emptyText:WtfGlobal.getLocaleText("hrms.Dashboard.NoSavedSearchs"),
                isSearch: false,
                headerHtml : '',
                paramsObj: {flag:5,searchField:'name'}

            }],
            id : "DSBSavedSearches",
            draggable:true,
            border:true,
            title: "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.Dashboard.Clickdragandplacewidgetanywhereonthedashboard")+"\">"+WtfGlobal.getLocaleText("hrms.common.saved.searches")+"<a href='#' id='refreshiconsavesearch' title='Loading...' onclick='reloadSavedSeaches();' class='refreshWidgets'></a></div>",
            tools: getToolsArray('jspfiles/rss.jsp?i=my_workspaces&id=' + loginid)
});
//}

if(!WtfGlobal.EnableDisable(Wtf.UPerm.useradmin, Wtf.Perm.useradmin.view)||
    !WtfGlobal.EnableDisable(Wtf.UPerm.masterconf, Wtf.Perm.masterconf.view)||
!WtfGlobal.EnableDisable(Wtf.UPerm.setappcycle, Wtf.Perm.setappcycle.view)||
!WtfGlobal.EnableDisable(Wtf.UPerm.audittrail, Wtf.Perm.audittrail.view)){
panelArr.push({
			config1:[{
//						url:Wtf.req.base + 'widget.jsp',
                        url:"Dashboard/getHRMSadministrationLinks.dsh",
						numRecs:10,
						isPaging: false,
						isSearch: false,
						template:new Wtf.XTemplate(
							'<tpl><div class="ctitle listpanelcontent" style="padding:0px !important;">',
//                                "<div style='padding-left:15px; background:transparent no-repeat scroll 0 0;'>{desc}</div>" +
                                                "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
							'</div></tpl>'
						),
						emptyText:WtfGlobal.getLocaleText("hrms.Dashboard.NoModules"),
                                                headerHtml:"<div colspan=\"3\" style='padding:5px 0 5px 0;border-bottom:1px solid #e4e4e4;font-size:13px;font-weight:bold;color:#10559a;' wtf:qtip="+WtfGlobal.getLocaleText("hrms.Dashboard.Accesstoolseffective")+">"+WtfGlobal.getLocaleText("hrms.Dashboard.CRMWorkspaces")+"</div>",
						paramsObj: {flag:6,searchField:'announceval'}
            }],
			draggable:true,
			border:true,
			title: "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.Dashboard.Clickdragandplacewidgetanywhereonthedashboard")+"\">"+WtfGlobal.getLocaleText("hrms.Dashboard.Administration")+"</div>",
			id : "dash_adminwidget",
			tools: getToolsArray('jspfiles/rss.jsp?i=announcements&u=' + loginid)
});
}
//}

if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.recruitment)) {
panelArr.push({
			config1:[{
//						url:Wtf.req.base + 'widget.jsp',
                        url:"Dashboard/getHRMSrecruitmentLinks.dsh",
						numRecs:10,
						isPaging: false,
						isSearch: false,
						template:new Wtf.XTemplate(
							'<tpl><div class="ctitle listpanelcontent" style="padding:0px !important;">',
//                                "<div style='padding-left:15px; background:transparent no-repeat scroll 0 0;'>{desc}</div>" +
                                "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
							'</div></tpl>'
						),
						emptyText:WtfGlobal.getLocaleText("hrms.Dashboard.NoModules"),
                        headerHtml:"<div colspan=\"3\" style='padding:5px 0 5px 0;border-bottom:1px solid #e4e4e4;font-size:13px;font-weight:bold;color:#10559a;' wtf:qtip="+WtfGlobal.getLocaleText("hrms.Dashboard.Accesstoolseffective")+">"+WtfGlobal.getLocaleText("hrms.Dashboard.CRMWorkspaces")+"</div>",
						paramsObj: {flag:7,searchField:'announceval'}
            }],
			draggable:true,
			border:true,
			title: "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.Dashboard.Clickdragandplacewidgetanywhereonthedashboard")+"\">"+WtfGlobal.getLocaleText("hrms.Dashboard.RecruitmentManagement")+"</div>",
			id : "dash_recruit",
			tools: getToolsArray('jspfiles/rss.jsp?i=announcements&u=' + loginid)
});
}

if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.payroll)) {
panelArr.push({
			config1:[{
//						url:Wtf.req.base + 'widget.jsp',
                        url:"Dashboard/getHRMSpayrollLinks.dsh",
						numRecs:10,
						isPaging: false,
						isSearch: false,
						template:new Wtf.XTemplate(
							'<tpl><div class="ctitle listpanelcontent" style="padding:0px !important;">',
//                                "<div style='padding-left:15px; background:transparent no-repeat scroll 0 0;'>{desc}</div>" +
                                "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
							'</div></tpl>'
						),
						emptyText:WtfGlobal.getLocaleText("hrms.Dashboard.NoModules"),
                        headerHtml:"<div colspan=\"3\" style='padding:5px 0 5px 0;border-bottom:1px solid #e4e4e4;font-size:13px;font-weight:bold;color:#10559a;' wtf:qtip="+WtfGlobal.getLocaleText("hrms.Dashboard.Accesstoolseffective")+">"+WtfGlobal.getLocaleText("hrms.Dashboard.CRMWorkspaces")+"</div>",
						paramsObj: {flag:8,searchField:'announceval'}
            }],
			draggable:true,
			border:true,
			title: "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.Dashboard.Clickdragandplacewidgetanywhereonthedashboard")+"\">"+WtfGlobal.getLocaleText("hrms.Dashboard.Payroll")+"</div>",
			id : "dash_payroll",
			tools: getToolsArray('jspfiles/rss.jsp?i=announcements&u=' + loginid)
});
}

if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.timesheet)) {
panelArr.push({
			config1:[{
//						url:Wtf.req.base + 'widget.jsp',
                        url:"Dashboard/getHRMStimesheetLinks.dsh",
						numRecs:10,
						isPaging: false,
						isSearch: false,
						template:new Wtf.XTemplate(
							'<tpl><div class="ctitle listpanelcontent" style="padding:0px !important;">',
//                                "<div style='padding-left:15px; background:transparent no-repeat scroll 0 0;'>{desc}</div>" +
                                "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
							'</div></tpl>'
						),
						emptyText:WtfGlobal.getLocaleText("hrms.Dashboard.NoModules"),
                                                headerHtml:"<div colspan=\"3\" style='padding:5px 0 5px 0;border-bottom:1px solid #e4e4e4;font-size:13px;font-weight:bold;color:#10559a;' wtf:qtip="+WtfGlobal.getLocaleText("hrms.Dashboard.Accesstoolseffective")+">"+WtfGlobal.getLocaleText("hrms.Dashboard.CRMWorkspaces")+"</div>",
						paramsObj: {flag:9,searchField:'announceval'}
            }],
			draggable:true,
			border:true,
			title: "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.Dashboard.Clickdragandplacewidgetanywhereonthedashboard")+"\">"+WtfGlobal.getLocaleText("hrms.Dashboard.TimesheetManagement")+"</div>",
			id : "dash_timesheet",
			tools: getToolsArray('jspfiles/rss.jsp?i=announcements&u=' + loginid)
});
}

if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.appraisal)) {
panelArr.push({
			config1:[{
//						url:Wtf.req.base + 'widget.jsp',
                        url:"Dashboard/getHRMSperformanceLinks.dsh",
						numRecs:10,
						isPaging: false,
						isSearch: false,
						template:new Wtf.XTemplate(
							'<tpl><div class="ctitle listpanelcontent" style="padding:0px !important;">',
//                                "<div style='padding-left:15px; background:transparent no-repeat scroll 0 0;'>{desc}</div>" +
                                "<div style='padding-left:15px; background:transparent url(../../images/bullet2.gif) no-repeat scroll 0 0;'>{update}</div>" +
							'</div></tpl>'
						),
						emptyText:WtfGlobal.getLocaleText("hrms.Dashboard.NoModules"),
                        headerHtml:"<div colspan=\"3\" style='padding:5px 0 5px 0;border-bottom:1px solid #e4e4e4;font-size:13px;font-weight:bold;color:#10559a;' wtf:qtip="+WtfGlobal.getLocaleText("hrms.Dashboard.Accesstoolseffective")+">"+WtfGlobal.getLocaleText("hrms.Dashboard.CRMWorkspaces")+"</div>",
						paramsObj: {flag:10,searchField:'announceval'}
            }],
			draggable:true,
			border:true,
			title: "<div wtf:qtip=\""+WtfGlobal.getLocaleText("hrms.Dashboard.Clickdragandplacewidgetanywhereonthedashboard")+"\">"+WtfGlobal.getLocaleText("hrms.Dashboard.PerformanceAppraisal")+"</div>",
			id : "dash_performance",
			tools: getToolsArray('jspfiles/rss.jsp?i=announcements&u=' + loginid)
});
}


var categoryarray  = [],count=0;
function showcategory(val){
    if(Wtf.get(val).dom.style.display=='none'){
    Wtf.get(val).dom.style.display = 'block';
    Wtf.get(val).dom.style.paddingLeft = '15px';
    Wtf.get('x'+val).dom.className = 'x-tool mycategory-expand';
        var  flag=1;
        var i=0;
        for(i=0;i<count;i++){
            if(Wtf.get(categoryarray[i])!=null){
            if(categoryarray[i]==val){
                flag=0;
            }else{
                Wtf.get(categoryarray[i]).dom.style.display = 'none';
                Wtf.get('x'+categoryarray[i]).dom.className = 'x-tool mycategory-collapse';
    }
        }
        if(flag==1){
            categoryarray[count] = val;
            count++;
        }
        }
    }
    else{
    Wtf.get(val).dom.style.display = 'none';
    Wtf.get('x'+val).dom.className = 'x-tool mycategory-collapse';
    }
}

function loadtab_main(id,book){
    id = '   '+id ;
    book.replace(/'/,'\'');

    mainPanel.loadTab('communityHome.html',id,book,'navareadashboard',1,true);
}

function signout_gmail(){
    var gmailpanel = Wtf.getCmp('DSBGmail');
    var user = '&type=1';
    gmailpanel.doSearch(gmailpanel.url,''+user);
}
var signoutLinks='<div id ="workspaceButtons" style="float:right;padding-top:3px;padding-right:5px;color:#AAA;">';

signoutLinks +='<a href="#" style="color:#083772" onclick="javascript:signout_gmail();">'+WtfGlobal.getLocaleText("hrms.Dashboard.Signout")+'</a>';

function submitlogin(){

    var gmailpanel = Wtf.getCmp('DSBGmail');
    var user = Wtf.get('UserName');
    var pass = Wtf.get('Password');
            if(user && pass){
                if(user.getValue().length > 0 && pass.getValue().length > 0 ){
                    Wtf.get('LoginButton').dom.setAttribute("disabled", true);
                    user = '&type=0&username='+encodeURI(user.getValue())+ "&pass="+encodeURI(pass.getValue());
                    gmailpanel.doSearch(gmailpanel.url,''+user);
                }
                else{
                      msgBoxShow(43,1);
                      Wtf.get('LoginButton').dom.setAttribute("disabled", false);
                      Wtf.get('UserName').focus();
                }
            }

}

Wtf.Panel.prototype.afterRender = Wtf.Panel.prototype.afterRender.createInterceptor(function() {// Fix For IE  Scrollable Player Bug Fix
	if(this.autoScroll) {
		this.body.dom.style.position = 'relative';
	}
});

//if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.videobrowser_widget))
//panelArr.push(vimeoWidget);

//if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.workspacecloud_widget))
//panelArr.push(tagPanel);

//if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.aboutkc_widget))
//panelArr.push(aboutKC);

var widgetIdArray=[];

//if((Wtf.UPerm.WidgetManagement) && EnableDisable(Wtf.UPerm.WidgetManagement,Wtf.Perm.WidgetManagement.library_widget))
widgetIdArray.push("DSBMyWorkspaces");
widgetIdArray.push("DSBAlerts");
widgetIdArray.push("DSBBookmarks");
widgetIdArray.push("DSBSavedSearches");

if(!WtfGlobal.EnableDisable(Wtf.UPerm.useradmin, Wtf.Perm.useradmin.view)||
    !WtfGlobal.EnableDisable(Wtf.UPerm.masterconf, Wtf.Perm.masterconf.view)||
!WtfGlobal.EnableDisable(Wtf.UPerm.setappcycle, Wtf.Perm.setappcycle.view)||
!WtfGlobal.EnableDisable(Wtf.UPerm.audittrail, Wtf.Perm.audittrail.view)) {
    widgetIdArray.push("dash_adminwidget");
}
if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.recruitment))
widgetIdArray.push("dash_recruit");
if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.payroll))
widgetIdArray.push("dash_payroll");
if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.timesheet))
widgetIdArray.push("dash_timesheet");
if(WtfGlobal.CmpEnableDisable(Wtf.subCode,Wtf.appraisal))
widgetIdArray.push("dash_performance");

var isToAppendArray=new Array();
for(var i=0;i<widgetIdArray.length;i++){
    isToAppendArray[i]=1;
}

/************initially 3 widgets added to dashboard************/
var mainLM=new Wtf.LoadMask(document.body,{msg:WtfGlobal.getLocaleText("hrms.Dashboard.LoadingWidgetThumbnails")});
mainLM.show();
Wtf.Ajax.requestEx({
//    url:Wtf.req.base + 'widget.jsp',
    url:"Dashboard/getWidgetStatus.common",
    params:{
       flag:1
    }
}, this, function(res){
    var _col1=[];
    var index=0;
    for(var i=0;i<res.col1.length;i++){
        index=widgetIdArray.indexOf(res.col1[i].id);
        isToAppendArray[index]=0;
        if(panelArr[index]!==undefined){
            panelArr[index].config1[0].paramsObj.grouper = "dashboard";
            if(i==res.col1.length-1){
                if(res.col2.length==0&&res.col3.length==0){
                    panelArr[index].config1[0].paramsObj.firequery = "1";
                }
            }
            Wtf.getCmp('portal_container_box1').add(createNewPanel(panelArr[index]));
        }
        Wtf.getCmp('portal_container').doLayout();
    }
    var _col2=[];
//    if(res.col2) {
        for(i=0;i<res.col2.length;i++){
            index=widgetIdArray.indexOf(res.col2[i].id);
            isToAppendArray[index]=0;
            if(panelArr[index]!==undefined){
                panelArr[index].config1[0].paramsObj.grouper = "dashboard";
                if(i==res.col2.length-1){
                    if(res.col3.length==0){
                        panelArr[index].config1[0].paramsObj.firequery = "1";
                    }
                }
                Wtf.getCmp('portal_container_box2').add(createNewPanel(panelArr[index]));
            }
            Wtf.getCmp('portal_container').doLayout();
        }
//    }
    var _col3=[];
    for(i=0;i<res.col3.length;i++){
        index=widgetIdArray.indexOf(res.col3[i].id);
        isToAppendArray[index]=0;
        if(panelArr[index]!==undefined){
            panelArr[index].config1[0].paramsObj.grouper = "dashboard";
            if(i==res.col3.length-1){
                panelArr[index].config1[0].paramsObj.firequery = "1";
            }
            Wtf.getCmp('portal_container_box3').add(createNewPanel(panelArr[index]));
        }
        Wtf.getCmp('portal_container').doLayout();
    }
//    Wtf.getCmp('portal_container_box1').add(_col1[0]);
//    Wtf.getCmp('portal_container_box2').add(_col2[0]);
//    Wtf.getCmp('portal_container_box3').add(_col3[0]);

    appendRemainingWidgets();
    setTimeout(function(){
        mainLM.hide();
    },1000);
}, function(){
    mainLM.hide();
});


//var panel1 = createNewPanel(panelArr[1]);
//var panel3 = createNewPanel(panelArr[3]);
//var panel2 = createNewPanel(panelArr[2]);

var paneltop = new Wtf.Panel({
	border: false,
	layout: 'border',
	frame: false,
	items: [{
		region: 'center',
		xtype: 'portal',
        id:'portal_container',
		bodyStyle: "background:white;",
		border: false,
//        html:  "<div id='dashhelp' class='outerHelp'>"+
//               "<div style='float:left; padding-left:25%;'><img src='../../images/alerticon.gif'></div><div class='helpHeader'>New to Deskera CRM?</div>"+
//               "<div class='helpContent'><a href='#' class='helplinks' style='color:#445566;' onclick='takeTour()'>Take a quick tour</a>&nbsp;&nbsp;"+
//               "<a class='helplinks' style='color:#445566;' href='#' onclick='noThanks()'>No Thanks</a>"+
//               "</div></div>",
		items: [{
			columnWidth: .33,
			cls: 'portletcls',
			id: 'portal_container_box1',
			border: false
		}, {
			columnWidth: .33,
			border: false,
			cls: 'portletcls',
			id: 'portal_container_box2'
		}, {
			columnWidth: .32,
			cls: 'portletcls',
			id: 'portal_container_box3',
			border: false
		}]
	}, {
		region: "south",
		height: 145,
        id : "dashboard-south",
		title: WtfGlobal.getLocaleText("hrms.Dashboard.AddDashboardWidgets"),
        autoScroll:true,
		collapsible: true,
		collapsed: true,
		split: true,
		frame: true,
		html: '<div class="widgets" id="widgets">' +
            '<ul id="widgetUl">' +
            '</ul>' +
            '</div>'
	}]
});
Wtf.getCmp('portal_container').on('drop',function(e){
    Wtf.Ajax.requestEx({
        url : Wtf.req.base + 'widget.jsp',
        params:{
            flag:4,
            colno:e.columnIndex+1,
            position:e.position,
            wid:e.panel.id
        }
    }, this, function(){
        Wtf.getCmp('portal_container').doLayout();
    }, function(){});
},this);

Wtf.getCmp("tabdashboard").add(paneltop);
Wtf.getCmp("tabdashboard").doLayout();
/************initially 3 widgets added to top widget bar************/
var titleSpan = document.createElement("div");
titleSpan.innerHTML = WtfGlobal.getLocaleText("hrms.Dashboard.AddDashboardWidgets");
titleSpan.id="southdash";
titleSpan.className = "collapsed-header-title";
Wtf.getCmp("dashboard-south").container.dom.lastChild.appendChild(titleSpan);
Wtf.getCmp("tabdashboard").on("activate",function(){
 document.getElementById('southdash').parentNode.style.height = 20;
 Wtf.getCmp("tabdashboard").doLayout();
},this);

Wtf.QuickTips.register({
    target:  Wtf.get('southdash'),
    trackMouse: true,
    text: WtfGlobal.getLocaleText("hrms.Dashboard.ClicktoexpandDashboardWidget")
});
Wtf.QuickTips.enable();

function appendRemainingWidgets(){
    for(var i=0;i<isToAppendArray.length;i++){
        if(isToAppendArray[i]==1){
            appendWidget(i);
        }
    }
}

function takeTour(){
   showHelp(1);
}

function noThanks(){
    Wtf.get('dashhelp').remove();
}

function requestForWidgetRemove(wid) {
    Wtf.Ajax.requestEx({
    url : Wtf.req.base + 'widget.jsp',
        params:{
            flag:2,
            wid:wid
        }
    }, this, function(){
    }, function(){});
}

function reloadUpdates(){//refreshiconspan
    var refreshUpdates = Wtf.getCmp("DSBMyWorkspaces");
    if(document.getElementById("refreshiconupdates")!=null){
    	document.getElementById("refreshiconupdates").className='loadingMask';
        document.getElementById("refreshiconupdates").title=WtfGlobal.getLocaleText("hrms.Dashboard.Loading");
    }

    if(refreshUpdates) {
    	refreshUpdates.doSearch(refreshUpdates.url,'');
    }
}

function reloadSavedSeaches(){//refreshiconspan
    var refreshUpdates = Wtf.getCmp("DSBSavedSearches");
    if(document.getElementById("refreshiconsavesearch")!=null){
	    document.getElementById("refreshiconsavesearch").className='loadingMask';
	    document.getElementById("refreshiconsavesearch").title=WtfGlobal.getLocaleText("hrms.Dashboard.Loading");
    }
    
    if(refreshUpdates) {
    	refreshUpdates.doSearch(refreshUpdates.url,'');
    }
}

function reloadAlerts(){//refreshiconspan
    var refreshAlerts = Wtf.getCmp("DSBAlerts");
    if(document.getElementById("refreshiconalerts")!=null){
    	document.getElementById("refreshiconalerts").className='loadingMask';
        document.getElementById("refreshiconalerts").title=WtfGlobal.getLocaleText("hrms.Dashboard.Loading");
    }
    if(refreshAlerts) {
    	refreshAlerts.doSearch(refreshAlerts.url,'');
    }
}
