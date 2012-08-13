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

Wtf.recruiters = function(config){
    Wtf.apply(this, config);
    Wtf.recruiters.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.recruiters, Wtf.Panel, {
    initComponent: function(){
        Wtf.recruiters.superclass.initComponent.call(this);
    },
    onRender:function(config) {
        Wtf.recruiters.superclass.onRender.call(this,config);
        this.jobForm();
        this.add(this.recruiterGrid);
        this.on("activate",function(){
            this.doLayout();
        });
    },
    jobForm:function(){
        this.sm2 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.recruiterRec=new Wtf.data.Record.create([{
            name:'userid'
        },{
            name:'username'
        },{
            name:'designation'
        },{
            name:'department'
        },{
            name:'emailid'
        },{
            name:'status'
        }]);
        this.recruiterStore =  new Wtf.data.Store({
//            url:Wtf.req.base + "hrms.jsp",
            url:"Rec/Job/getRecruitersFunction.rec",
            baseParams: {
                flag:152
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data',
                totalProperty:'Count'
            },this.recruiterRec),
            autoLoad : false
        });
        calMsgBoxShow(202,4,true);
        this.recruiterStore.load();
        this.recruiterStore.on("load",function(){
            if(msgFlag==1){
             WtfGlobal.closeProgressbar()
            }
        },this);

        this.cm = new Wtf.grid.ColumnModel(
            [
            this.sm2,{
                header: WtfGlobal.getLocaleText("hrms.recruitment.interviewer.name"),//"Interviewer Name",
                dataIndex: 'username',
                sortable: true

            },{
                header: WtfGlobal.getLocaleText("hrms.common.email.id"),//"Email ID",
                dataIndex: 'emailid',
                sortable: true,
                renderer: WtfGlobal.renderEmailTo
            },{
                header: WtfGlobal.getLocaleText("hrms.common.department"),//"Department",
                dataIndex: 'department',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.designation"),//"Designation",
                dataIndex: 'designation',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.confirmation.request"),//"Confirmation Request",
                dataIndex: 'status',
                sortable: true,
                renderer : function(val) {
                    if(val=='0'){
                    	return "<FONT COLOR='blue'>"+WtfGlobal.getLocaleText("hrms.recruitment.pending")+"</FONT>";//return "<FONT COLOR='blue'>Pending</FONT>";
                    }else if(val=='1'){
                    	return "<FONT COLOR='green'>"+WtfGlobal.getLocaleText("hrms.recruitment.accepted")+"</FONT>";//return "<FONT COLOR='green'>Accepted</FONT>";
                    }else if(val=='2'){
                    	return "<FONT COLOR='red'>"+WtfGlobal.getLocaleText("hrms.recruitment.rejected")+"</FONT>";//return "<FONT COLOR='red'>Rejected</FONT>";
                    }else {
                    	return "<FONT COLOR='DarkGoldenRod'>"+WtfGlobal.getLocaleText("hrms.recruitment.not.sent")+"</FONT>";//return "<FONT COLOR='DarkGoldenRod'>Not Sent</FONT>";
                    }
                }
            }]);
        this.intstatusdata=[['0',WtfGlobal.getLocaleText("hrms.recruitment.pending")],['1',WtfGlobal.getLocaleText("hrms.recruitment.accepted")],['2',WtfGlobal.getLocaleText("hrms.recruitment.rejected")],['3',WtfGlobal.getLocaleText("hrms.recruitment.not.sent")]];

        this.intstatusStore=new Wtf.data.SimpleStore({
            fields: ['id','intstatus'],
            data :this.intstatusdata
        });
        this.interstatus = new Wtf.form.ComboBox({
            fieldLabel:WtfGlobal.getLocaleText("hrms.recruitment.interviewer.status"),//"Interviewer Status",
            store:this.intstatusStore,
            mode:'local',
            hiddenName :'jobtype',
            valueField: 'id',
            displayField:'intstatus',
            triggerAction: 'all',
            typeAhead:true,
            loadMask:true,
          //  value:Wtf.cmpPref.defaultapps,
            //allowBlank:false,
            emptyText:WtfGlobal.getLocaleText("hrms.common.select.status"),//"Select status",
            width:120
        });
        this.clearFilterBut= new Wtf.Button({
            text:WtfGlobal.getLocaleText("hrms.recruitment.clear.filter"),//'Clear Filter',
            handler:this.clearFilter,
            iconCls : 'pwndExport addfilter',
            tooltip:'Clear interviewer status filter.',
            scope:this
        });
        var setrecruiter=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.recruitment.set.as.interviewer"),//'Set As Interviewer',
                tooltip:WtfGlobal.getLocaleText("hrms.recruitment.set.as.interviewer.tooltip"),//"Select an interviewer from the list and set him/her as interviewer. The request will be confirmed later.",
                iconCls:getButtonIconCls(Wtf.btype.assignbutton),
                minWidth:112,
                disabled:true,
                scope:this,
                handler:function(){
                    this.rec=this.sm2.getSelections();
                    if(this.rec.length>0)
                    {
                        this.assignRecruiter();
                    }
                    else{
                        Wtf.MessageBox.show({
                            msg: WtfGlobal.getLocaleText("hrms.common.please.select.record"),//'Please select a record.',
                            buttons: Wtf.MessageBox.OK
                        });
                    }
                }
            }) ;
          this.unassgnrecruiter=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.recruitment.unassign.interviewer"),//'Unassign Interviewer',
                tooltip:WtfGlobal.getLocaleText("hrms.recruitment.unassign.interviewer.tooltip"),//"Select the interviewer and unassign him/her at any point of time.",
                iconCls:getButtonIconCls(Wtf.btype.cancelbutton),
                minWidth:100,
                disabled:true,
                scope:this,
                handler:function(){
                    this.deleteRecruiter();
                }

            });
        var rctrbtns=new Array();
        rctrbtns.push('-', new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),//'Reset',
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.recruiterStore.load({params:{start:0,limit:this.recruiterGrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.recruiterGrid.id).setValue("");
        	}
     	}));
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.recruiters, Wtf.Perm.recruiters.create)){
            rctrbtns.push('-',setrecruiter);
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.recruiters, Wtf.Perm.recruiters.deleterctr)){
            rctrbtns.push('-');
            rctrbtns.push(this.unassgnrecruiter);
        }
        rctrbtns.push('-');
        rctrbtns.push(this.clearFilterBut);
        rctrbtns.push('-');
        rctrbtns.push(this.interstatus);
        this.recruiterGrid = new Wtf.KwlGridPanel({
            border: false,
            store: this.recruiterStore,
            cm: this.cm,
            sm: this.sm2,
            enableColumnHide: false,
            trackMouseOver: true,
            stripeRows: true,
            loadMask:true,
            displayInfo:true,
            serverSideSearch:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.recruitment.search.interviewer.name"),//"Search by Interviewer Name",
            searchField:"username",
            viewConfig: {
                forceFit: true,
                emptyText:'<center><font size="4">'+WtfGlobal.getLocaleText("hrms.recruitment.job.grid.msg")+'</font></center>'//'<center><font size="4">No records to show</font></center>'
            },
            tbar:rctrbtns
        }); 
        this.sm2.on("selectionchange",function(){
            var recruite = this.sm2.getSelections();
            var enablerecruit = true, deleterecruit = true;
            for(var i = 0 ; i < recruite.length ; i++){
              if(recruite[i].get('status') == '1' || recruite[i].get('status') == '0')
                  enablerecruit = false;
              else if(recruite[i].get('status') == '3')
                  deleterecruit = false;
            }
            if(enablerecruit)
                WtfGlobal.enableDisableBtnArr(rctrbtns, this.recruiterGrid, [], [3]);
            if(deleterecruit)
                WtfGlobal.enableDisableBtnArr(rctrbtns, this.recruiterGrid, [], [5]);
        },this);
        this.interstatus.on('select',function(a,b,c){
            this.recruiterStore.baseParams={
                status:this.interstatus.getValue()
            }
            this.recruiterStore.load({
                params:{
                    start:0,
                    limit:this.recruiterGrid.pag.pageSize,
                    ss: Wtf.getCmp("Quick"+this.recruiterGrid.id).getValue()
                }
            });
            //    this.recruiterStore.filter("status",b.data.id);
        },this);
    },

    clearFilter:function(){
        this.interstatus.reset();
        this.recruiterStore.baseParams={
                flag: 152
            }
        this.recruiterStore.load();
    },

    assignRecruiter:function(){
        this.assignflag=true;
        this.recruit=this.sm2.getSelections();
        this.sm2.clearSelections();
        for(var i=0;i<this.recruit.length;i++){
          var rec=this.recruiterStore.indexOf(this.recruit[i]);
          WtfGlobal.highLightRow(this.recruiterGrid,"33CC33",5, rec);
            if(this.recruit[i].get('status')=='1')
                this.assignflag=false;
        }
        if(this.assignflag){
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
                icon:Wtf.MessageBox.QUESTION,
                msg:WtfGlobal.getLocaleText("hrms.recruitment.selected.employees.interviewer.msg"),//'Are you sure you want to assign the selected employees as interviewer?',
                buttons:Wtf.MessageBox.YESNO,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        this.recids=[];
                        for(i=0;i<this.recruit.length;i++){
                            this.recids.push(this.recruit[i].get('userid'));
                        }
                        calMsgBoxShow(200,4,true);
                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + 'hrms.jsp',
                            url:"Rec/Job/addRecruitersFunction.rec",
                            params:  {
                                flag:125,
                                jobids:this.recids
                            }
                        },
                        this,
                        function(){
                            calMsgBoxShow(66,0);
                            var params={
                               start:0,
                               limit:this.recruiterGrid.pag.pageSize,
                               ss: Wtf.getCmp("Quick"+this.recruiterGrid.id).getValue()
                            }
                            WtfGlobal.delaytasks(this.recruiterStore,params);
                            var a=Wtf.getCmp("DSBMyWorkspaces");
                            if(a){
                                a.doSearch(a.url,'');
                            }
                        },
                        function(){
                            calMsgBoxShow(67,1);
                        })
                    }
                }
            })
        }
        else{
            this.recruiterGrid.getSelectionModel().clearSelections();
            calMsgBoxShow(129,0);
        }
    },
    deleteRecruiter:function(){
        if(this.recruiterGrid.getSelectionModel().getCount()==0){
            calMsgBoxShow(42,0);
        }
        else{
            this.deleteflag=true;
            this.statrec=this.recruiterGrid.getSelectionModel().getSelections();
            this.apparr=[];
            this.sm2.clearSelections();
            for(var i=0;i<this.statrec.length;i++){
              var rec=this.recruiterStore.indexOf(this.statrec[i]);
              WtfGlobal.highLightRow(this.recruiterGrid,"FF0000",5, rec);
                if(this.statrec[i].get('status')=='2'||this.statrec[i].get('status')=='3')
                    this.deleteflag=false;
            }
            if(this.deleteflag)
            {
                for(i=0;i<this.statrec.length;i++){
                    this.apparr.push(this.statrec[i].get('userid'));
                }
                this.deleteflag=true;
                Wtf.MessageBox.show({
                    title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
                    icon:Wtf.MessageBox.QUESTION,
                    msg:WtfGlobal.getLocaleText("hrms.recruitment.unassign.selected.interviewer.msg"),//'Are you sure you want to unassign the selected interviewer(s)?',
                    buttons:Wtf.MessageBox.YESNO,
                    scope:this,
                    fn:function(button){
                        if(button=='yes')
                        {
                            calMsgBoxShow(201,4,true);
                            Wtf.Ajax.requestEx({
                                url:"Rec/Job/addRecruitersFunction.rec",
//                                url: Wtf.req.base + 'hrms.jsp',
                                params:  {
                                    flag:125,
                                    appid:this.apparr,
                                    delrec:"true"
                                }
                            },
                            this,
                            function(){
                                calMsgBoxShow(149,0);
                                var params={
                                    start:0,
                                    limit:this.recruiterGrid.pag.pageSize,
                                    ss: Wtf.getCmp("Quick"+this.recruiterGrid.id).getValue()
                                }
                                WtfGlobal.delaytasks(this.recruiterStore,params);
                                var a=Wtf.getCmp("DSBMyWorkspaces");
                                if(a){
                                    a.doSearch(a.url,'');
                                }
                            },
                            function(){
                                calMsgBoxShow(27,1);
                            })
                        }
                    }
                })
            }
            else{
                this.recruiterGrid.getSelectionModel().clearSelections();
                calMsgBoxShow(129,0);
            }
        }
    }
});
