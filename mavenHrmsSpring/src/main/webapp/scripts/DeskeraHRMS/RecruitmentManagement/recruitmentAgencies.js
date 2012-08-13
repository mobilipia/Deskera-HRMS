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
Wtf.recruitAgencies = function(config){
    Wtf.apply(this, config);
    Wtf.recruitAgencies.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.recruitAgencies, Wtf.Panel, {
    initComponent: function(){
        Wtf.recruitAgencies.superclass.initComponent.call(this);
    },
    onRender:function(config) {
        Wtf.recruitAgencies.superclass.onRender.call(this,config);
        this.agencyReportGrid();
        this.add(this.agencyGrid);
    },
    agencyReportGrid:function(){
        this.sm2 = new Wtf.grid.CheckboxSelectionModel({
            singleSelect:false
        });
        this.agencyRecord=Wtf.data.Record.create([{
            name:'agid'
        },{
            name:'agname'
        },{
            name:'url'
        },{
            name:'cost'
        },{
            name:'manager'
        },{
            name:'managerid'
        },{
            name:'contactperson'
        },{
            name:'address'
        },{
            name:"phoneno"
        }]);

        this.agencyGDS = new Wtf.data.Store({
            baseParams: {
                flag: 122
            },
//            url: Wtf.req.base + "hrms.jsp",
            url: "Rec/Agency/showAgency.rec",
            reader: new Wtf.data.KwlJsonReader1({
                root: 'data',
                totalProperty:'count'
            },
            this.agencyRecord
            )});
        calMsgBoxShow(202,4,true);
        this.agencyGDS.load();
        this.agencyGDS.on("load",function(){
            if(msgFlag==1)
            WtfGlobal.closeProgressbar()
        },this);

        this.cm = new Wtf.grid.ColumnModel(
            [this.sm2,
            {
                header: WtfGlobal.getLocaleText("hrms.recruitment.agency.id"),//"Agency Id",
                dataIndex: 'agid',
                hidden:true,
                sortable: true

            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.agency.name"),//"Agency Name",
                dataIndex: 'agname',
                sortable: true

            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.website"),//"Website",
                dataIndex: 'url',
                sortable: true,
                renderer:function(value){
                        return "<div class='mailTo'><a href="+value+" target=_blank>"+value+"</a></div>";
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.recruitment.cost"),//"Recruitment Cost",
                dataIndex: 'cost',
                sortable: true,
                renderer:function(val) {
                    if(val!=null){
                        return('<div align=\"right\" style="font-family:Lucida Sans Unicode;">'+WtfGlobal.currencyRenderer(parseFloat(val).toFixed(2))+'</div>');
                    }
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.common.approving.manager"),//"Approving Manager",
                dataIndex: 'manager',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.contact.person"),//"Contact Person",
                dataIndex: 'contactperson',
                sortable: true

            },{
                header: WtfGlobal.getLocaleText("hrms.common.contact.address"),//"Contact Address",
                dataIndex: 'address',
                sortable: true
            },{
                header: WtfGlobal.getLocaleText("hrms.common.contact.phone.no"),//"Contact Phone No",
                dataIndex: 'phoneno',
                sortable: true
            }]);

        var agencybtns=new Array();
        agencybtns.push('-',new Wtf.Toolbar.Button({
     		text:WtfGlobal.getLocaleText("hrms.common.reset"),//'Reset',
     		scope: this,
     		iconCls:'pwndRefresh',
     		handler:function(){
        		this.agencyGDS.load({params:{start:0,limit:this.agencyGrid.pag.pageSize}});
        		Wtf.getCmp("Quick"+this.agencyGrid.id).setValue("");
        	}
     	}));
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.agencies, Wtf.Perm.agencies.create)){
              agencybtns.push('-',{
                text:WtfGlobal.getLocaleText("hrms.recruitment.add.agency"),//'Add Agency',
                tooltip:WtfGlobal.getLocaleText("hrms.recruitment.add.agency.tooltip"),//"Enter the information of a new recruitment agency to the database of existing agencies.",
                iconCls:getButtonIconCls(Wtf.btype.addbutton),
                scope:this,
                handler:function(){
                    this.addAgencies();
                }
            });
        }
        this.delagency=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.recruitment.delete.agencies"),//'Delete Agencies',
                tooltip:WtfGlobal.getLocaleText("hrms.recruitment.delete.agencies.tooltip"),//"Delete the agency which does not comply to the requirements anymore.",
                iconCls:getButtonIconCls(Wtf.btype.deletebutton),
                scope:this,
                disabled:true,
                handler:function(){
                    this.delAgencies();
                }
            });

        this.editagency=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.recruitment.edit.agency"),//'Edit Agency',
                tooltip:WtfGlobal.getLocaleText("hrms.recruitment.edit.agency.tooltip"),//"Edit the details entered by you for the recruitment agecies.",
                iconCls:getButtonIconCls(Wtf.btype.editbutton),
                scope:this,
                disabled:true,
                handler:function(){
                    this.editAgencies();
                }
            });

         this.assignjob=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.recruitment.assign.jobs"),//'Assign Jobs',
                tooltip:WtfGlobal.getLocaleText("hrms.recruitment.assign.jobs.tooltip"),//"Assign jobs to the recruitment agency.",
                disabled:true,
                iconCls:getButtonIconCls(Wtf.btype.assignbutton),
                scope:this,
                handler:function(){
                    if(this.sm2.getCount()==0 || this.sm2.getCount()>1)
                    {
                        calMsgBoxShow(42,2);
                    }
                    else{
                        this.assignJobs();
                    }
                }
            });
        this.viewjobs=new Wtf.Button({
                text:WtfGlobal.getLocaleText("hrms.recruitment.view.jobs"),//'View Jobs',
                tooltip:WtfGlobal.getLocaleText("hrms.recruitment.view.jobs.tooltip"),//"View the list of assigned jobs for each agency.",
                disabled:true,
                iconCls:getButtonIconCls(Wtf.btype.viewbutton),
                scope:this,
                handler:function(){
                    if(this.sm2.getCount()==0 || this.sm2.getCount()>1)
                    {
                        calMsgBoxShow(42,2);
                    }
                    else{
                        this.viewJobs();
                    }
                }
            });

        if(!WtfGlobal.EnableDisable(Wtf.UPerm.agencies, Wtf.Perm.agencies.deleteagn)){
             agencybtns.push('-',this.editagency
            );
        }
        if(!WtfGlobal.EnableDisable(Wtf.UPerm.agencies, Wtf.Perm.agencies.deleteagn)){
             agencybtns.push('-',this.delagency
            );
        }
          if(!WtfGlobal.EnableDisable(Wtf.UPerm.agencies, Wtf.Perm.agencies.assignjobs)){
             agencybtns.push('-',this.assignjob);
          }
          if(!WtfGlobal.EnableDisable(Wtf.UPerm.agencies, Wtf.Perm.agencies.create)){
             agencybtns.push('-',this.viewjobs);
         }
        this.agencyGrid = new Wtf.KwlGridPanel({
            border: false,
            store: this.agencyGDS,
            cm: this.cm,
            sm: this.sm2,
            loadMask:true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:addagency(\""+this.id+"\")'>"+WtfGlobal.getLocaleText("hrms.recruitment.add.agency.grid.msg")+"</a>")//WtfGlobal.emptyGridRenderer("<a class='grid-link-text' href='#' onClick='javascript:addagency(\""+this.id+"\")'>Get started by adding a recruitment agency now...</a>")
            },
            displayInfo:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.recruitment.search.by.agency.name"),//"Search by Agency Name",
            searchField:"agname",
            enableColumnHide: false,
            trackMouseOver: true,
            serverSideSearch:true,
            stripeRows: true,
            listeners:{
                scope:this,
                rowclick:function(agencyGrid,rowIndex,evObj)
                {
                    this.rowindex=rowIndex;
                }
            },
            tbar:agencybtns
        });
         this.sm2.on("selectionchange",function(){
            WtfGlobal.enableDisableBtnArr(agencybtns, this.agencyGrid, [5,9,11], [7]);
        },this);
    },
    addAgencies:function(){
        this.addagency= new Wtf.agencyWindow({
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            modal:true,
            closable:true,
            title: WtfGlobal.getLocaleText("hrms.recruitment.recruitment.agency"),//'Recruitment Agency',
            isEdit:false,
            wintitle:WtfGlobal.getLocaleText("hrms.recruitment.add.agency"),//"Add Agency",
            resizable:false,
            layout:'fit',        
            ds:this.agencyGDS
        })
        this.addagency.on('show',function(){
            this.addagency.agname.focus(true,100);
        },this);
        this.addagency.show();
   },
   editAgencies:function(){
        var rec = this.agencyGrid.getSelectionModel().getSelected();
        this.addagency= new Wtf.agencyWindow({
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            modal:true,
            closable:true,
            title: WtfGlobal.getLocaleText("hrms.recruitment.agency"),//'Recruitment Agency',
            isEdit:true,
            wintitle:WtfGlobal.getLocaleText("hrms.recruitment.edit.agency"),
            resizable:false,
            layout:'fit',
            record:rec,
            ds:this.agencyGDS
        })
        this.addagency.on('show',function(){
            this.addagency.agname.focus(true,100);
        },this);
        this.addagency.show();
   },

    delAgencies:function(){
        if(this.sm2.getCount()==0)
        {
           calMsgBoxShow(42,2);
        }
        else{
            Wtf.MessageBox.show({
                title:WtfGlobal.getLocaleText("hrms.common.confirm"),//'Confirm',
                msg:deleteMsgBox('agency'),
                buttons:Wtf.MessageBox.YESNO,
                icon:Wtf.MessageBox.QUESTION,
                scope:this,
                fn:function(button){
                    if(button=='yes')
                    {
                        this.statrec=this.agencyGrid.getSelectionModel().getSelections();
                        this.apparr=[];
                        this.sm2.clearSelections();
                        for(var i=0;i<this.statrec.length;i++){
                            this.apparr.push(this.statrec[i].get('agid'));
                             var rec=this.agencyGDS.indexOf(this.statrec[i]);
                             WtfGlobal.highLightRow(this.agencyGrid,"FF0000",5, rec)
                        }
                        calMsgBoxShow(201,4,true);
                        Wtf.Ajax.requestEx({
//                            url: Wtf.req.base + "hrms.jsp?flag=123",
                            url: "Rec/Agency/deleteAgency.rec",
                            scope:this,
                            params:{
                                agencyid:this.apparr
                            }
                        },
                        this,
                        function(response)
                        {                            
                            var params={
                                start:0,
                                limit:this.agencyGrid.pag.pageSize
                            }
                            calMsgBoxShow(69,0);
                            WtfGlobal.delaytasks(this.agencyGDS,params);
                         },
                        function()
                        {
                           calMsgBoxShow(27,1);
                        }
                        );
                    }
                }
            });
        }
    },
    assignJobs:function(){

        var main=Wtf.getCmp("agencyTab");
        var ageid=this.agencyGrid.getSelectionModel().getSelected().get('agid');
        var edjobTab=Wtf.getCmp(ageid+'Jobs');
        if(edjobTab==null)
        {
            edjobTab=new Wtf.jobmaster2({
                id:ageid+'Jobs',
                title:WtfGlobal.getLocaleText("hrms.recruitment.assign.jobs.to")+" "+this.agencyGrid.getSelectionModel().getSelected().get('agname'),//"Assign Jobs To "+this.agencyGrid.getSelectionModel().getSelected().get('agname'),
                layout:'fit',
                closable:true,
                jobbuttons:false,
                agency:false,
                disableBut:false,
                iconCls:getTabIconCls(Wtf.etype.hrmsexternaljob),
                agencybuttons:true,
                agencyid:ageid
            });
            main.add(edjobTab);
        }
        main.setActiveTab(edjobTab);
        main.doLayout();
        Wtf.getCmp("as").doLayout();

    },
    viewJobs:function(){

            var agency=this.agencyGrid.getSelectionModel().getSelected().get('agname');
            var main =Wtf.getCmp('agencyTab');
            var mainSuccessionTab=Wtf.getCmp(agency+'jobs');
            if(mainSuccessionTab==null)
            {
                mainSuccessionTab=new Wtf.jobmaster2({
                    title:this.agencyGrid.getSelectionModel().getSelected().get('agname')+WtfGlobal.getLocaleText("hrms.recruitment.s.jobs"),//"'s Jobs",
                    id:this.agencyGrid.getSelectionModel().getSelected().get('agid')+'viewjobs',
                    border:false,
                    layout:'fit',
                    closable:true,
                    disableBut:true,
                    jobbuttons:false,
                    agency:true,
                    agencyid:this.agencyGrid.getSelectionModel().getSelected().get('agid'),
                    agencybuttons:false,
                    iconCls:getTabIconCls(Wtf.etype.hrmsexternaljob)
                });
                main.add(mainSuccessionTab);
            }
            main.setActiveTab(mainSuccessionTab);
            main.doLayout();
    }
});

function addagency(Id){
 Wtf.getCmp(Id).addAgencies();
}
 
