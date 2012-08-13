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
function setextusr(){
    var _uElem = document.getElementById('whoamiext');
    _uElem.innerText = _gC('username'); //mofo IE
    _uElem.textContent = _gC('username');
    addTopCuts("#", "Change Password", "changepass()");
}

function enableDisableButton(buttonid,GridStore,GridSm){
    //Ankush  Kale
    GridSm.on("selectionchange",function(){
        changeEnable(buttonid,GridSm);
    },this);


    GridStore.on("load",function(){
        changeEnable(buttonid,GridSm);
    },this)

}

function changeEnable(buttonid,GridSm){
    //Ankush  Kale
    var selRec=GridSm.getSelections();
    var flag=0;
    if(selRec.length==1)
    {
        for (i=0;i<buttonid.length;i++)
        {
            if(Wtf.getCmp(buttonid[i]))
            {
                Wtf.getCmp(buttonid[i]).enable();
                flag=1;
            }
        }
        if(flag==0){
            if(Wtf.getCmp(buttonid))
                Wtf.getCmp(buttonid).enable();
        }
    }
    if(selRec.length<1||selRec.length>1)
    {
        for (i=0;i<buttonid.length;i++)
        {
            if(Wtf.getCmp(buttonid[i]))
            {
                Wtf.getCmp(buttonid[i]).disable();
                flag=1;
            }
        }
        if(flag==0)
            if(Wtf.getCmp(buttonid))
                Wtf.getCmp(buttonid).disable();
    }

}

function getTopHtml(text, body){
    return("<div class = 'adminWinCont'>"
        +"<div class = 'adminWinImgDiv'>"
        +"</div>"
        +"<div class = 'adminWinDesc'>"
        +"<div class = 'adminWinTitle'><strong>" + text + "</strong></div>"
        +"<div class = 'adminWinAbout'>"+ body +"</div>"
        +"</div></div>");
}

function addTopCuts(u, t, eh){
    Wtf.DomHelper.append('shortcuts','<a ' + (eh ? ('onclick="' + eh) : ('target="_blank')) + '" href="' + u + '" title="'+ t + '">' + t + '</a> ');
}

function changepass(){
    this.changepasswin=new Wtf.passwin({
        title:'Change Password',
        width:390,
        modal:true,
        height:260,
        resizable:false,
        layout:'fit',
        profId:_gC("lid")
    }).show();
}

Wtf.ApplicantPanel = function (config){
    Wtf.apply(this, config);
    Wtf.ApplicantPanel.superclass.constructor.call(this);
}

Wtf.extend(Wtf.ApplicantPanel,Wtf.TabPanel,{
    id:"mainTabPanel",
    resizeTabs:true,
    tabWidth:150,
    activeTab:0
});

Wtf.onReady(function (){
    this.appform= new Wtf.createapplicantForm({
        autoScroll:true,
        title:WtfGlobal.getLocaleText("hrms.Featurelist.myprofile"),
        iconCls:'pwndCommon profiletabIcon',
        border:false,
        profId:_gC("lid"),
        id:_gC("lid")+'profile'
    });
    this.jobs= new Wtf.recruitmentJobs({
        title:WtfGlobal.getLocaleText("hrms.recruitment.job.search"),
        layout:'fit',
        iconCls:'pwndHRMS jobsearchtabIcon',
        border:false,
        profId:_gC("lid"),
        id:_gC("lid")+'jobsearch'
    });
    this.jobstatus= new Wtf.recruitmentJobstatus({
        title:WtfGlobal.getLocaleText("hrms.recruitment.job.status"),
        layout:'fit',
        iconCls:'pwndHRMS jobstatustabIcon',
        border:false,
        profId:_gC("lid"),
        id:_gC("lid")+'jobstatus'
    });
    new Wtf.Viewport({
        frame:true,
        layout:"border",
        border:false,
        items:[
        new Wtf.Panel({
            frame:true,
            region:"north",
            height:50
        }),
        new Wtf.ApplicantPanel({
            //                title:"north",
            border:false,
            id:'maintab',
            region:"center",
            activeTab:0,
            items:[this.appform,this.jobs,this.jobstatus]
        })
        ]
    })
    setextusr();
});
 


