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
Wtf.viewApplicants=function(config){
    Wtf.viewApplicants.superclass.constructor.call(this,config);
    Wtf.form.Field.prototype.msgTarget='side';
};
Wtf.extend(Wtf.viewApplicants,Wtf.Panel,{
    initComponent:function(config){
        Wtf.viewApplicants.superclass.initComponent.call(this,config);

        this.sm = new Wtf.grid.CheckboxSelectionModel({
                singleSelect:false
            });

            this.record = Wtf.data.Record.create([
            {
                name:"empid"
            },
            {
                name:"jobid"
            },
            {
                name:"appname"
            },
            {
                name:"designation"
            },
            ,
            {
                name:"department"
            },
            {
                name:"email"
            },
            {
                name:"contactno"
            }
            ]);
            calMsgBoxShow(202,4,true);
            this.ds = new Wtf.data.Store({
                baseParams: {
                    flag: 120,
                    jobid:this.jid
                },
                url: Wtf.req.base + "hrms.jsp",
                reader: new Wtf.data.KwlJsonReader1({
                    root: 'data',
                    totalProperty:'count'
                },
                this.record
                )
            });
            this.ds.load();
            this.ds.on("load",function(){WtfGlobal.closeProgressbar()},this);

            this.cm = new Wtf.grid.ColumnModel([
                new Wtf.grid.RowNumberer(),
                {
                    header: WtfGlobal.getLocaleText("hrms.recruitment.applicant.name"),
                    dataIndex: 'appname'
                },
                {
                    header: WtfGlobal.getLocaleText("hrms.common.designation"),
                    dataIndex: 'designation'
                },
                {
                    header: WtfGlobal.getLocaleText("hrms.common.department"),
                    dataIndex: 'department'
                },
                {
                    header: WtfGlobal.getLocaleText("hrms.common.email.id"),
                    dataIndex: 'email',
                    renderer: WtfGlobal.renderEmailTo
                },
                {
                    header: WtfGlobal.getLocaleText("hrms.common.contact.no"),
                    dataIndex: 'contactno'
                }
                ]);


            this.viewApplicantsGrid=new Wtf.KwlGridPanel({
                cm:this.cm,
                store:this.ds,
                loadMask:true,
                viewConfig: {
                    forceFit: true,
                    emptyText:'<center><font size="4">'+WtfGlobal.getLocaleText("hrms.recruitment.job.grid.msg")+'</font></center>'
                },
                searchLabel:" ",
                searchLabelSeparator:" ",
                searchEmptyText:WtfGlobal.getLocaleText("hrms.recruitment.external.grid.search.msg"),
                searchField:"appname",
                displayInfo:true
            });
            this.add(this.viewApplicantsGrid);

            
        },//end of init component
        changestatus:function(){

        if(this.viewApplicantsGrid.getSelectionModel().getCount()==0){
           calMsgBoxShow(42,0);
        }
        else{
            this.statrec=this.viewApplicantsGrid.getSelectionModel().getSelections();
            this.apparr=[];
            for(var i=0;i<this.statrec.length;i++){
                this.apparr.push(this.statrec[i].get('appid'));
            }
        Wtf.Ajax.requestEx({
            url: Wtf.req.base + 'hrms.jsp',
            params:  {
                flag:10,           
                appid:this.apparr
        }
        },
        this,
        function(){
           calMsgBoxShow(70,0);
            this.ds.load();
        },
        function(){
           calMsgBoxShow(71,1);
        })
        }
    },
    onRender:function(config){
        Wtf.viewApplicants.superclass.onRender.call(this,config);
    }

});//end of extend
