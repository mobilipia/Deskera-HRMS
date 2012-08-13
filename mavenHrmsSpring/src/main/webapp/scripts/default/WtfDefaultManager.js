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
function showIcon(searchId){
    var icon = Wtf.get(searchId);
    if(icon){
        icon.dom.style.display = "block";
    }
}

function hideIcon(searchId){
    var icon = Wtf.get(searchId);
    if(icon){
        icon.dom.style.display = "none";
    }
}

function openAdvanceSearchTab(moduleId,searchId){

        switch(moduleId){
            case 1:
                employeemnt(searchId);
                break;
            case 2:
                AddJobs2(searchId);
                break;
            case 3:
                allapps(undefined,searchId, moduleId);
                break;
            case 4:
                allapps(undefined,searchId, moduleId);
                break;
            case 5:
                allapps(undefined,searchId, moduleId);
                break;
            case 6:
                allapps(undefined,searchId, moduleId);
                break;
            
        }
}
function deleteAdvanceSearch(searchId){
   
    Wtf.MessageBox.show({
        title: WtfGlobal.getLocaleText("hrms.common.confirm"),
        msg: WtfGlobal.getLocaleText("hrms.manager.delete.selected.search")+"<br><br><b>"+WtfGlobal.getLocaleText("hrms.Messages.DateCannotbeRetrive"),
        buttons: Wtf.MessageBox.OKCANCEL,
        animEl: 'upbtn',
        icon: Wtf.MessageBox.QUESTION,
        scope:this,
        fn:function(bt){
            if(bt=="ok"){
                Wtf.Ajax.requestEx({
                    url:"Common/deleteSavedSearch.common",
                    params: {
                        searchid: searchId
                    }
                }, this, function(response){
                    var res=eval('('+response+')');
                    if(res.success && res.success == true){
                        reloadSavedSeaches();
                        calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.manager.search.deleted.successfully")],0);
                        
                    }else{
                        calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.manager.search.not.deleted")],0);
                    }
                },function(response){

                });
            }
        }
    });
}
function callLink1(){
    var panel = Wtf.getCmp("link1");
    if(panel==null){
         panel = new Wtf.Panel({
            id : 'link1',
            border : false,
            layout: 'fit',
            title:'Link 1',
            closable: true
//            iconCls:'pwnd managericon'
//            activeItem : 0
        });
        Wtf.getCmp('as').add(panel);
    }
     Wtf.getCmp('as').setActiveTab(panel);
     panel.doLayout();
}

function callLink2(){
    var panel = Wtf.getCmp("link2");
    if(panel==null){
         panel = new Wtf.Panel({
            id : 'link2',
            border : false,
            layout: 'fit',
            title:'Link 2',
            closable: true
//            iconCls:'pwnd managericon'
//            activeItem : 0
        });
        Wtf.getCmp('as').add(panel);
    }
     Wtf.getCmp('as').setActiveTab(panel);
     panel.doLayout();
}

//function callSystemAdmin(){
//    var panel = Wtf.getCmp("systemadmin");
//    if(panel==null){
//        panel = new Wtf.common.SystemAdmin({
//            title : "Company Administration",
//            layout : 'fit',
//            id:'systemadmin',
//            iconCls:'systemadmin',
//            border:false,
//            closable:true
//        });
// Wtf.getCmp('as').add(panel);
//    }
//    Wtf.getCmp('as').setActiveTab(panel);
//    Wtf.getCmp('as').doLayout();
//}
//
//function callCreateCompany(){
//
//
//    var p = Wtf.getCmp("createcompany");
//    if(!p){
//        new Wtf.common.CreateCompany({
//            title:'Create Company',
//            id:'createcompany',
//            closable: true,
//            modal: true,
//            iconCls:'systemadmin',
//            width: 410,
//            height: 370,
//            resizable: false,
//            layout: 'fit',
//            buttonAlign: 'right'
//        }).show();
//    }
//
//}
