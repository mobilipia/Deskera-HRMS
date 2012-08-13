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

Wtf.assignComponentWin=function(config){
    Wtf.apply(this,config);
    Wtf.assignComponentWin.superclass.constructor.call(this,{
        buttonAlign : 'right',
        buttons :[this.savebtn=new Wtf.Button({
            text : WtfGlobal.getLocaleText("hrms.common.Save"),
            scope: this,
            minWidth:75,
            disabled:true,
            handler:function() {
                if(this.generatePayrollLink){
                    this.createPayrollMapping();
                }else {
                    this.createMemberList();
                }
                
            }
        }),{
            text : WtfGlobal.getLocaleText("hrms.common.cancel"),
            scope : this,
            handler : function(){
                this.close();
            }
        }]
    });
    this.addEvents({
        "savedata": true
    });
}

Wtf.extend(Wtf.assignComponentWin, Wtf.Window, {
    group_id:"",
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    onRender:function(config){
        Wtf.assignComponentWin.superclass.onRender.call(this,config);
        this.availablesm = new Wtf.grid.CheckboxSelectionModel();
        this.availablecm = new Wtf.grid.ColumnModel([this.availablesm,
        {
            dataIndex: 'compid',
            hidden: true,
            fixed:true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.code"),
            dataIndex: 'code',
            sortable: true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.common.start.date"),
            dataIndex: 'sdate',
            sortable: true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.common.end.date"),
            dataIndex: 'edate',
            sortable: true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.performance.description"),
            dataIndex: 'desc',
            sortable: true,
            renderer:function(val){
                if(Wtf.isIE6 || Wtf.isIE7)
                    return "<pre style='word-wrap:break-word;font:11px arial, tahoma, helvetica, sans-serif;' wtf:qtip=\""+val+"\">"+val+"</pre>";
                return "<span style='white-space:pre-wrap;'>"+val+"</span>";
            }
        },
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.is.blocked"),
            dataIndex: 'isblock',
            align:'center',
            renderer:function(val){
                if(val){
                    return '<span style=\'color:red !important;\'>'+WtfGlobal.getLocaleText("hrms.common.yes")+'</span>';
                }else{
                    return '<span style=\'color:green !important;\'>'+WtfGlobal.getLocaleText("hrms.common.no")+'</span>';
                }
            }
        }
        ]);
        this.quickSearchEmp = new Wtf.wtfQuickSearch({
            width: 150,
            field:"code",
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.search.code")
        });
        this.availableds = new Wtf.data.Store({
            //            url:  Wtf.req.base + 'hrms.jsp',
            url: "Payroll/Date/getAvailableComponent.py",
            baseParams: {
                userid:this.userid,
                available:true,
                frequency:this.frequency
            },
            reader: new Wtf.data.KwlJsonReader1({
                root: 'data',
                totalProperty:'count'
            },new Wtf.data.Record.create([{
                "name":"compid"
            },
            {
                "name":"code"
            },
            {
                "name":"sdate"
            },
            {
                "name":"edate"
            },
            {
                "name":"desc"
            },
            {
                "name":"type"
            },
            {
                "name":"isblock"
            }])
            ),
            autoLoad : false
        });

        if(this.previousSalaryFlag){
            this.availableds.on('beforeload',function(store,option){
                option.params= option.params||{};
                option.params.startdate=this.startdate;
                option.params.enddate=this.enddate;
                option.params.generatePayrollLink=this.generatePayrollLink;
                option.params.previousSalaryFlag=this.previousSalaryFlag;
                option.params.frequency=this.frequency;
            },this);
        }

        this.availablegrid = new Wtf.grid.EditorGridPanel({
            height:100,
            store:this.availableds,
            cm: this.availablecm,
            border:false,
            id:this.id+'compavailablegrid',
            sm : this.availablesm,
            autoScroll:true,
            searchField:"code",
            serverSideSearch:true,
            searchEmptyText:WtfGlobal.getLocaleText("hrms.payroll.search.code"),
            viewConfig: {
                forceFit: true,
                autoFill:true
            },
            tbar : [WtfGlobal.getLocaleText("hrms.common.QuickSearch")+': ',this.quickSearchEmp]
        });

        this.availableds.on("load",this.empSearch,this);

        this.selectedRec = new Wtf.data.Record.create([
        {
            "name":"compid"
        },
        {
            "name":"code"
        },
        {
            "name":"sdate"
        },
        {
            "name":"edate"
        },
        {
            "name":"desc"
        },
        {
            "name":"type"
        },
        {
            "name":"amount"
        },
        {
            "name":"isblock"
        }
        ]);
        
        var URL="Payroll/Date/getAssignedComponent.py";
        var readerJson=new Wtf.data.KwlJsonReader1({
            root:'data',
            totalProperty: 'count'
        },this.selectedRec);

        if(this.previousSalaryFlag){
            URL="Payroll/Date/getSalaryComponentsForEmployee.py";

            readerJson=new Wtf.data.KwlJsonReader({
                root:'data',
                totalProperty: 'count'
            },this.selectedRec);
        }
        
        this.selectedds = new Wtf.data.Store({
            //            url: Wtf.req.base + "hrms.jsp",
            url: URL,
            baseParams: {
                userid:this.userid,
                assigned:true,
                frequency:this.frequency
            },
            reader: readerJson,
            autoLoad : false
        });
        if(this.generatePayrollLink){
            this.selectedds.on('beforeload',function(store,option){
                option.params= option.params||{};
                option.params.startdate=this.startdate;
                option.params.enddate=this.enddate;
                option.params.generatePayrollLink=this.generatePayrollLink;
                option.params.frequency=this.frequency;
            },this);
        }
        
        
        this.selectedsm = new Wtf.grid.CheckboxSelectionModel();
        this.selectedcm = new Wtf.grid.ColumnModel([this.selectedsm,
        {
            dataIndex: 'compid',
            hidden: true,
            fixed:true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.code"),
            dataIndex: 'code',
            sortable: true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.common.start.date"),
            dataIndex: 'sdate',
            sortable: true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.common.end.date"),
            dataIndex: 'edate',
            sortable: true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.performance.description"),
            dataIndex: 'desc',
            sortable: true,
            renderer:function(val){
                if(Wtf.isIE6 || Wtf.isIE7)
                    return "<pre style='word-wrap:break-word;font:11px arial, tahoma, helvetica, sans-serif;' wtf:qtip=\""+val+"\">"+val+"</pre>";
                return "<span style='white-space:pre-wrap;'>"+val+"</span>";
            }
        },
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.is.blocked"),
            dataIndex: 'isblock',
            align:'center',
            renderer:function(val){
                if(val){
                    return '<span style=\'color:red !important;\'>'+WtfGlobal.getLocaleText("hrms.common.yes")+'</span>';
                }else{
                    return '<span style=\'color:green !important;\'>'+WtfGlobal.getLocaleText("hrms.common.no")+'</span>';
                }
            }
        }
        ]);
        this.quickSearchAssgEmp = new Wtf.wtfQuickSearch({
            width: 150,
            field:"code",
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.search.code")
        });
        this.selectedgrid = new Wtf.grid.EditorGridPanel({
            height:100,
            store: this.selectedds,
            cm: this.selectedcm,
            sm : this.selectedsm,
            autoScroll:true,
            border:false,
            viewConfig: {
                forceFit: true
            },
            tbar : [WtfGlobal.getLocaleText("hrms.common.QuickSearch")+': ',this.quickSearchAssgEmp]
        });
        this.selectedds.load();
        this.selectedds.on("load",this.empAssgSearch,this);

        this.movetoright = document.createElement('img');
        this.movetoright.src = "../../images/arrowright.gif";
        this.movetoright.style.width = "24px";
        this.movetoright.style.height = "24px";
        this.movetoright.style.margin = "5px 0px 5px 0px";
        this.movetoright.onclick = this.movetorightclicked.createDelegate(this,[]);

        this.movetoleft = document.createElement('img');
        this.movetoleft.src = "../../images/arrowleft.gif";
        this.movetoleft.style.width = "24px";
        this.movetoleft.style.height = "24px";
        this.movetoleft.style.margin = "5px 0px 5px 0px";
        this.movetoleft.onclick = this.movetoleftclicked.createDelegate(this,[]);

        this.centerdiv = document.createElement("div");
        this.centerdiv.appendChild(this.movetoright);
        this.centerdiv.appendChild(this.movetoleft);
        this.centerdiv.style.padding = "135px 10px 135px 10px";
        //        var id="1";
        //        var msg1="";
        //        var head="";
        //        if(this.text1=='edit')
        //        {
        //            this.bttntext='Update';
        //            var flag=9;
        //            id=this.group_id;
        //            msg1="Group Updated successfully.";
        //            head="Edit Group";
        //            this.imgsrc = 'edit-group.gif';
        //        }
        //        else
        //        {
        //            this.bttntext='Create';
        //            var flag=6;
        //            id=this.group_id;
        //            msg1="Group Added successfully.";
        //            head="Create New Group";
        //            this.imgsrc = 'add-group.gif';
        //        }

        var title=WtfGlobal.getLocaleText("hrms.payroll.component");
        this.assignTeamPanel = new Wtf.Panel({
            layout : 'border',
            items :[{
                region : 'north',
                height : 80,
                border : false,
                cls : 'formstyleClass',
                html :getTopHtml(WtfGlobal.getLocaleText({key:"hrms.payroll.assign.params",params:[title]}), WtfGlobal.getLocaleText({key:"hrms.payroll.assign.params",params:[title]}), "../../images/assign-manager.gif")

            },{
                region : 'center',
                border : false,
                layout : 'fit',
                items : [{
                    border : false,
                    bodyStyle : 'background:transparent;',
                    layout : 'border',
                    items : [
                    {
                        region : 'west',
                        border : false,
                        width : 400,
                        layout : 'fit',
                        items :[{
                            xtype : 'KWLListPanel',
                            title : WtfGlobal.getLocaleText("hrms.payroll.components.header"),
                            border : false,
                            paging : false,
                            layout : 'fit',
                            autoLoad : false,
                            items : this.availablegrid
                        }]
                    },{
                        region : 'center',
                        border : false,
                        contentEl : this.centerdiv
                    },{
                        region : 'east',
                        border : false,
                        width : 400,
                        layout : 'fit',
                        items :[{
                            xtype : 'KWLListPanel',
                            title : WtfGlobal.getLocaleText("hrms.payroll.assigned.component"),
                            border : false,
                            paging : false,
                            layout : 'fit',
                            autoLoad : false,
                            items : this.selectedgrid
                        }]
                    }]
                }]
            }]
        });


        this.add(this.assignTeamPanel);

    },
    empSearch: function(store, rec, opt) {
        this.quickSearchEmp.StorageChanged(store);
    },
    empAssgSearch: function(store, rec, opt) {
        this.quickSearchAssgEmp.StorageChanged(store);
        if(this.selectedds.getCount()>0)
            this.savebtn.enable();
        else
            this.savebtn.disable();
        this.refreshAssignedparams();
        this.availableds.load();

    },
    refreshAssignedparams : function() {
        var managerids = [];
        for(var i=0;i<this.selectedds.getCount();i++){
            managerids.push(this.selectedds.getAt(i).get('compid'));
        }
        this.availableds.baseParams.managerids=managerids;
    },
    movetorightclicked : function() {
        var selected = this.availablesm.getSelections();
        if(selected.length>0){
            this.selectedds.add(selected);
            this.refreshAssignedparams();
        }
        for(var ctr=0;ctr<selected.length;ctr++){
            this.availableds.remove(selected[ctr]);
        }
        this.quickSearchEmp.StorageChanged(this.availableds);
        this.quickSearchAssgEmp.StorageChanged(this.selectedds);
        if(this.selectedds.getCount()>0)
            this.savebtn.enable();
        else
            this.savebtn.disable();
    },

    movetoleftclicked : function() {

        var selected = this.selectedsm.getSelections();
        if(selected.length>0){
            this.availableds.add(selected);
            this.refreshAssignedparams();
        }
        for(var ctr=0;ctr<selected.length;ctr++){
            this.selectedds.remove(selected[ctr]);
        }
        this.quickSearchEmp.StorageChanged(this.availableds);
        this.quickSearchAssgEmp.StorageChanged(this.selectedds);
        if(this.selectedds.getCount()>0)
            this.savebtn.enable();
        else
            this.savebtn.disable();
    },
    createMemberList:function(){
        var componentid=[];
        var availcomponentid=[];
        var blockComponent=[];
        var userid=[];
        this.assignflag=true;
        for(var i=0;i<this.selectedds.getCount();i++){
            if(!this.selectedds.getAt(i).get('isblock')){
                componentid.push(this.selectedds.getAt(i).get('compid'));
            }else {
                blockComponent.push(this.selectedds.getAt(i).get('code'));
            }
        }

        if(blockComponent.length>0){
            var strMsg = WtfGlobal.getLocaleText("hrms.payroll.list.blocked.component")+"<br><br> <b>"+WtfGlobal.getLocaleText("hrms.payroll.remove.from.assigned.component.list")+"<br></b>"
            var count=1;
            for(var k=0; k < blockComponent.length; k++){
                strMsg += "<br>"+count+") "+blockComponent[k];
                count++;
            }

            Wtf.MessageBox.show({
                title: WtfGlobal.getLocaleText("hrms.common.Alert"),
                msg: strMsg,
                buttons: Wtf.MessageBox.OK,
                animEl: 'mb9',
                icon: Wtf.MessageBox.INFO
            });
           
            return;
        }

        for(var i=0;i<this.availableds.getCount();i++){
            availcomponentid.push(this.availableds.getAt(i).get('compid'));
        }
        
        var rec=this.allempGrid.getSelectionModel().getSelected();
        this.allempGrid.getSelectionModel().clearSelections();

        if(rec!=undefined){
            var row=this.empGDS.indexOf(rec);
            WtfGlobal.highLightRow(this.allempGrid ,"33CC33",5,row);
        }

        calMsgBoxShow(200,4,true);
        userid.push(this.userid);
        Wtf.Ajax.requestEx({
            url: "Payroll/Date/assignComponent.py",
            params:  {
                componentid:componentid,
                availcomponentid:availcomponentid,
                userid:userid
            }
        },
        this,
        function(){
            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.components.assigned.successfully")],0,0);
            this.close();
        },
        function(){
            Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.admin.Errorinassigningmanager"));
            this.close();
        });
    },

    createPayrollMapping:function(){
    	var componentid=[];
        var blockComponent=[];
    	var componentidamount=[];
        var availcomponentid=[];
        var userid=[];
        this.assignflag=true;
        var payrollRecData = this.rec.data;
        for(var i=0;i<this.selectedds.getCount();i++){

            if(!this.selectedds.getAt(i).get('isblock')){
                componentid.push(this.selectedds.getAt(i).get('compid'));
                componentidamount.push(this.selectedds.getAt(i).get('amount'));
            }else {
                blockComponent.push(this.selectedds.getAt(i).get('code'));
            }
        }
        if(blockComponent.length>0){
            var strMsg = WtfGlobal.getLocaleText("hrms.payroll.components.blocked.administrator")+"<br><br> <b>"+WtfGlobal.getLocaleText("hrms.payroll.remove.components.assigned.component.list")+"<br></b>"
            var count=1;
            for(var k=0; k < blockComponent.length; k++){
                strMsg += "<br>"+count+") "+blockComponent[k];
                count++;
            }

            Wtf.MessageBox.show({
                title: WtfGlobal.getLocaleText("hrms.common.Alert"),
                msg: strMsg,
                buttons: Wtf.MessageBox.OK,
                animEl: 'mb9',
                icon: Wtf.MessageBox.INFO
            });
            return;
        }
        for(var i=0;i<this.availableds.getCount();i++){
            availcomponentid.push(this.availableds.getAt(i).get('compid'));
        }
        
        var rec=this.allempGrid.getSelectionModel().getSelected();
        this.allempGrid.getSelectionModel().clearSelections();

        if(rec!=undefined){
            var row=this.empGDS.indexOf(rec);
            WtfGlobal.highLightRow(this.allempGrid ,"33CC33",5,row);
        }

        userid.push(this.userid);
        calMsgBoxShow(200,4,true);
        Wtf.Ajax.requestEx({
        	url: "Payroll/Date/assignComponentToResource.py",
            params:  {
                componentid:componentid,
                componentidamount:componentidamount,
                availcomponentid:availcomponentid,
                userid:userid,
                startdate:this.startdate,
                enddate:this.enddate,
                frequency:this.frequency,
              //  id:payrollRecData.id,
                fullname:payrollRecData.fullname,
                costcenter: payrollRecData.costcenter,
                jobtitle:payrollRecData.jobtitle,
                contract:payrollRecData.contract,
                absence:payrollRecData.absence,
                paycyclestartdate:this.startdate,
                paycycleenddate:this.enddate,
                difference:payrollRecData.difference,
                status:payrollRecData.status,
                historyid:payrollRecData.id,
                overwrite:false,
                isGeneratable:false
            }
        },
        this,
        function(response){
        	var res=eval('('+response+')');
        	if(res.success){
        		calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.components.assigned.successfully")],0,0);
        		var delayTask = new Wtf.util.DelayedTask(function(){
                    if(this.generatePayrollLink){
                        this.empGDS.load({
                            scope:this,
                            params:{
                                start:this.allempGrid.pag.cursor,
                                limit:this.allempGrid.pag.pageSize,
                                sdate :this.startdate,
                                edate :this.enddate,
                                frequency:this.frequency
                            }
                        });
                    }else{
                        this.empGDS.load({
                            params:{
                                start:this.allempGrid.pag.cursor,
                                limit:this.allempGrid.pag.pageSize,
                                ss: Wtf.getCmp("Quick"+this.allempGrid.id).getValue()
                            }
                        });
                    }
                    
                    this.close();
                },this);
                delayTask.delay(1000);
        	
            }else if(this.generatePayrollLink){
            	if(res.sameFrequency){
        		Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.payroll.components.already.assigned.want.overwrite"), function(btn){
                    if(btn!="yes"){
                      return;
                    }

                    calMsgBoxShow(200,4,true);
                    Wtf.Ajax.requestEx({
                        url: "Payroll/Date/assignComponentToResource.py",
                        params:  {
                            componentid:componentid,
                            availcomponentid:availcomponentid,
                            userid:userid,
                            startdate:res.startDate,
                            enddate:res.endDate,
                            frequency:this.frequency,
                            fullname:payrollRecData.fullname,
                            costcenter: payrollRecData.costcenter,
                            jobtitle:payrollRecData.jobtitle,
                            contract:payrollRecData.contract,
                            absence:payrollRecData.absence,
                            paycyclestartdate:res.startDate,
                            paycycleenddate:res.endDate,
                            difference:payrollRecData.difference,
                            status:payrollRecData.status,
                            historyid:payrollRecData.id,
                            overwrite:true,
                            isGeneratable:false
                        }
                    }, this,
                    function(response){
                        var res=eval('('+response+')');
                        if(res.success){
                            calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.components.assigned.successfully")],0,0);
                            var delayTask = new Wtf.util.DelayedTask(function(){

                                this.empGDS.load({
                                    scope:this,
                                    params:{
                                        start:this.allempGrid.pag.cursor,
                                        limit:this.allempGrid.pag.pageSize,
                                        sdate :this.startdate,
                                        edate :this.enddate,
                                        frequency:this.frequency
                                    }
                                });

                                this.close();
                            },this);
                            delayTask.delay(1000);

                        }
                    },
                    function(response)
                    {
                        Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.payroll.error.overwrite.component"));
                    })
                },this);
            	}else{
            		if(res.isGeneratable){
            			Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText({key:"hrms.payroll.components.already.mapped.salary.already.exist",params:[res.startdate, res.enddate, res.startDateDisplay, res.endDateDisplay]}),function(btn){
            			if(btn!="yes"){
            				this.close();
            				return;
                        }
                        calMsgBoxShow(200,4,true);
                        Wtf.Ajax.requestEx({
                            url: "Payroll/Date/assignComponentToResource.py",
                            params:  {
                                componentid:componentid,
                                availcomponentid:availcomponentid,
                                userid:userid,
                                startdate:res.startDate,
                                enddate:res.endDate,
                                frequency:this.frequency,
                                fullname:payrollRecData.fullname,
                                costcenter: payrollRecData.costcenter,
                                jobtitle:payrollRecData.jobtitle,
                                contract:payrollRecData.contract,
                                absence:payrollRecData.absence,
                                paycyclestartdate:res.startDate,
                                paycycleenddate:res.endDate,
                                difference:payrollRecData.difference,
                                status:payrollRecData.status,
                                historyid:payrollRecData.id,
                                overwrite:true,
                                isGeneratable:true
                            }
                        }, this,
                        function(response){
                            var res=eval('('+response+')');
                            if(res.success){
                                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.components.assigned.successfully")],0,0);
                                var delayTask = new Wtf.util.DelayedTask(function(){

                                    this.empGDS.load({
                                        scope:this,
                                        params:{
                                            start:this.allempGrid.pag.cursor,
                                            limit:this.allempGrid.pag.pageSize,
                                            sdate :this.startdate,
                                            edate :this.enddate,
                                            frequency:this.frequency
                                        }
                                    });
                                    this.close();
                                },this);
                                delayTask.delay(1000);
                            }
                        },
                        function(response)
                        {
                            Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.payroll.error.overwrite.component"));
                        });
            		},this);
            		}else{
            			this.close();
            			calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText({key:"hrms.payroll.component.mapping.salary.already.exist",params:[res.startdate, res.enddate]})],0,0);
            		}
            	}
        	}
        },
        function(){
            Wtf.Msg.alert(WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.admin.Errorinassigningmanager"));
        });
       
    }
});


/**Component - Link Component
 *
 */

Wtf.linkComponentWin=function(config){
    Wtf.apply(this,config);
    Wtf.linkComponentWin.superclass.constructor.call(this,{
        buttonAlign : 'right',
        buttons :[this.savebtn=new Wtf.Button({
            text : WtfGlobal.getLocaleText("hrms.common.Save"),
            scope: this,
            minWidth:75,
            disabled:this.status,
            handler:function() {
        		var ids = [];
        		var amounts = [];
                var components = [];
        		for(var i=0;i<this.selectedds.data.items.length;i++){
        			ids[i] = this.selectedds.data.items[i].data.id;
        			amounts[i] = this.selectedds.data.items[i].data.amount;
                    components[i] = this.selectedds.data.items[i].data.compid;
        		}

        		if(this.selectedds.data.items.length==0){
        			calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.warning"), WtfGlobal.getLocaleText("hrms.payroll.no.componets.save")],2);
        			return 0;
        		}
        		
                var htmlString=WtfGlobal.getLocaleText("hrms.payroll.update.salary.selected.employee")+"<br><br><form><input type='checkbox' name='update_dependent_component_amount' value='update' /> "+WtfGlobal.getLocaleText("hrms.payroll.check.update.all.dependent.components")+"<br /></form> ";

                Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), htmlString,function(btn){
                    if(btn!="yes") return;

                    var updateAllDependents = document.getElementsByName('update_dependent_component_amount')[0].checked;

                    calMsgBoxShow(200,4,true);
                    Wtf.Ajax.requestEx({
                        url:"Payroll/Date/editAssignedComponentsToResource.py",
                        scope:this,
                        params: {
                            ids:ids,
                            amounts:amounts,
                            components:components,
                            startdate:this.startdate,
                            enddate:this.enddate,
                            userid:this.userid,
                            updateAllDependents:updateAllDependents,
                            frequency:this.frequency,
                            absence:this.absence,
                            historyid:this.historyid
                        }
                    }, this,
                    function(response){
                        calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.salary.amount.updated.successfully")],0);

                        if(this.reviewPayrollFlag){
                            this.fireEvent("reloadComponents", this)
                        }
                        
                        this.close();
                    },
                    function(){
                        this.close();
                    });
                },this);
        		
            }
        }),{
            text : WtfGlobal.getLocaleText("hrms.common.cancel"),
            scope : this,
            handler : function(){
                this.close();
            }
        }]
    });
    
}

Wtf.extend(Wtf.linkComponentWin, Wtf.Window, {
    group_id:"",
    iconCls:getButtonIconCls(Wtf.btype.winicon),
    initComponent: function(){

        this.addEvents('reloadComponents');
		Wtf.linkComponentWin.superclass.initComponent.call(this);
	},
    onRender:function(config){
        Wtf.linkComponentWin.superclass.onRender.call(this,config);
        
        this.selectedRec = new Wtf.data.Record.create([{
        	"name":"id"
        },
        {
            "name":"compid"
        },
        {
            "name":"code"
        },
        {
            "name":"sdate"
        },
        {
            "name":"edate"
        },
        {
            "name":"desc"
        },
        {
            "name":"amount"
        },
        {
            "name":"type"
        },
        {
            "name" :"isaddruletypecomponent"  
        },
        {
            "name":"isadjust"
        },{
            "name":"istaxablecomponent"
        }]);

        this.selectedds = new Wtf.data.Store({
            url: "Payroll/Date/getAssignedComponentsToResource.py",
            baseParams: {
        		userid:this.userid,
        		startdate:this.startdate,
        		enddate:this.enddate,
        		frequency:this.frequency,
                assigned:true
            },
            reader: new Wtf.data.KwlJsonReader1({
                root:'data',
                totalProperty: 'count'
            },this.selectedRec),
            autoLoad : false
        });
        this.selectedsm = new Wtf.grid.CheckboxSelectionModel({
        	singleSelect:true
        });
        this.selectedcm = new Wtf.grid.ColumnModel([this.selectedsm,
        {
            dataIndex: 'compid',
            hidden: true,
            fixed:true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.code"),
            dataIndex: 'code',
            sortable: true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.performance.description"),
            dataIndex: 'desc',
            sortable: true,
            renderer:function(val){
                if(Wtf.isIE6 || Wtf.isIE7)
                    return "<pre style='word-wrap:break-word;font:11px arial, tahoma, helvetica, sans-serif;' wtf:qtip=\""+val+"\">"+val+"</pre>";
                return "<span style='white-space:pre-wrap;'>"+val+"</span>";
            }
        },
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.component.type"),
            dataIndex: 'type',
            sortable: true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.Amount"),
            dataIndex: 'amount',
            editor: new Wtf.form.NumberField({
            			maxLength:255,
            			allowBlank:false,
            			allowNegative:false,
            			maxValue:9999999999999999
                	}),
            sortable: true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.payroll.is.adjustable"),
            dataIndex: 'isadjust',
            renderer:function(val){
                if(val){
                    return '<span style=\'color:green !important;\'>'+WtfGlobal.getLocaleText("hrms.common.yes")+'</span>';
                }else{
                    return '<span style=\'color:red !important;\'>'+WtfGlobal.getLocaleText("hrms.common.no")+'</span>';
                }
            },
            sortable: true
        },{
            header: WtfGlobal.getLocaleText("hrms.common.start.date"),
            dataIndex: 'sdate',
            sortable: true
        },
        {
            header: WtfGlobal.getLocaleText("hrms.common.end.date"),
            dataIndex: 'edate',
            sortable: true
        }
        ]);
        this.quickSearchAssgEmp = new Wtf.wtfQuickSearch({
            width: 150,
            field:"code",
            emptyText:WtfGlobal.getLocaleText("hrms.payroll.search.code")
        });
        this.selectedgrid = new Wtf.grid.EditorGridPanel({
            height:100,
            store: this.selectedds,
            cm: this.selectedcm,
            sm : this.selectedsm,
            autoScroll:true,
            border:false,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.no.component.linked.this.period"))
            },
            tbar : [WtfGlobal.getLocaleText("hrms.common.QuickSearch")+': ',this.quickSearchAssgEmp]
        });
        this.selectedds.load();
        this.selectedds.on("load",function(){
            this.quickSearchAssgEmp.StorageChanged(this.selectedds);
        },this);
        
       this.selectedgrid.on("beforeedit",function(obj){
            if(!obj.record.data.isadjust || obj.record.data.isaddruletypecomponent){
                obj.cancel=true;
                return false;
            }
       },this);

        var title=WtfGlobal.getLocaleText("hrms.payroll.component");
        this.assignTeamPanel = new Wtf.Panel({
            layout : 'border',
            items :[{
                region : 'north',
                height : 80,
                border : false,
                cls : 'formstyleClass',
                html :getTopHtml(WtfGlobal.getLocaleText("hrms.payroll.manage.amount"), WtfGlobal.getLocaleText("hrms.payroll.manage.amount"), "../../images/assign-manager.gif")

            },{
                region : 'center',
                border : false,
                layout : 'fit',
                items : [this.selectedgrid]
            }]
        });


        this.add(this.assignTeamPanel);

    }
});

Wtf.AssignFrequency = function (config){
    Wtf.apply(this,config);
    this.save = true;
    Wtf.AssignFrequency.superclass.constructor.call(this,{
        buttons:[
                {
                    text:WtfGlobal.getLocaleText("hrms.common.Save"),
                    id:'btnsave',
                    handler:function (){
                        this.saveAssignFrequency();
                    },
                    scope:this
                },
                {
                    text:WtfGlobal.getLocaleText("hrms.common.cancel"),
                    handler:function (){
                        this.close();
                    },
                    scope:this
                }
            ]
    });
}

Wtf.extend(Wtf.AssignFrequency,Wtf.Window,{
    initComponent:function (){
        Wtf.AssignFrequency.superclass.initComponent.call(this);
        this.GetNorthPanel();
        this.GetCenterPanel();
        this.GetSouthPanel();

        this.mainPanel = new Wtf.Panel({
            layout:"border",
            items:[
                this.northPanel,
                this.allempGrid,
                this.AddEditForm
            ]
        });

        this.add(this.mainPanel);
    },
    GetNorthPanel:function (){

        this.northPanel = new Wtf.Panel({
            region:"north",
            height:75,
            border:false,
            bodyStyle:"background-color:white;padding:8px;border-bottom:1px solid #bfbfbf;",
            html:getTopHtml("Assign Frequency","Assign frequency for selected employees", "../../images/assign-manager.gif")
        });
    },
    GetCenterPanel:function (){
          this.cm = new Wtf.grid.ColumnModel(
            [
            new Wtf.grid.RowNumberer(),
            {
                header: WtfGlobal.getLocaleText("hrms.common.employee.id"),
                dataIndex: 'employeeid'
            },{
                header: WtfGlobal.getLocaleText("hrms.common.UserName"),
                dataIndex: 'username'
            },{
                header: WtfGlobal.getLocaleText("hrms.common.employee.name"),
                dataIndex: 'fullname'
            },{
                header: WtfGlobal.getLocaleText("hrms.payroll.current.frequency"),
                dataIndex: 'frequency',
                renderer: function(val){
                    if(val==0){
                        return WtfGlobal.getLocaleText("hrms.payroll.Monthly");
                    } else if(val==1){
                        return WtfGlobal.getLocaleText("hrms.payroll.Weekly");
                    } else if(val==2){
                        return WtfGlobal.getLocaleText("hrms.payroll.twice.month");
                    }
                }


            }
         ]);
         this.allempGrid = new Wtf.grid.GridPanel({
            region:'center',
            id:this.id+'qualifiedgr',
            store: this.empGDS,
            cm: this.cm,
            loadMask:true,
            displayInfo:true,
            enableColumnHide: false,
            trackMouseOver: true,
            clicksToEdit:1,
            autoScroll:true,
            stripeRows: true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            viewConfig: {
                forceFit: true
            }

        });

    },
    GetSouthPanel:function (){

         this.frequencyStoreCmb = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.assign.frequency"),
            hiddenName: 'frequency',
            mode:"local",
            valueField:'id',
            displayField:'name',
            store:Wtf.frequencyStore,
            width:150,
            typeAhead:true,
            value:0
        });

        this.AddEditForm = new Wtf.form.FormPanel({
            region:"south",
            height:75,
            border:false,
            bodyStyle:"background-color:#f1f1f1;padding:55px 55px 55px 145px",
            url:"Common/Master/addMasterDataField.common",
            items:[
                this.frequencyStoreCmb
            ]
        });
    },
    saveAssignFrequency:function (){
        var empids=[];
        var asd=""
        for(var i=0; i<this.emparr.length;i++){
            empids.push(this.emparr[i].get('userid'));
            asd+=this.emparr[i].get('userid')+","
        }

        Wtf.MessageBox.confirm(WtfGlobal.getLocaleText("hrms.common.confirm"), WtfGlobal.getLocaleText("hrms.payroll.update.frequency.listed.employee"), function(btn){
            if(btn!="yes") return;
            Wtf.commonWaitMsgBox(WtfGlobal.getLocaleText("hrms.common.Savingdata"));

            Wtf.Ajax.requestEx({

            url: "Payroll/Date/assignFrequencytoResource.py",
            params:{
                empids : empids,
                frequency:this.frequencyStoreCmb.getValue()
            }},this,
            function(res,action){
                calMsgBoxShow([WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.frequency.assigned.successfully")],0,0);
                this.close();
                this.grid.getStore().reload();
            },
            function() {
                calMsgBoxShow(27,1);
            }
        );

        },this);
    }
});

