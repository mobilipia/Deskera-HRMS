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
Wtf.recruitmentJobstatus= function(config){
    Wtf.recruitmentJobstatus.superclass.constructor.call(this, config);
};
Wtf.extend(Wtf.recruitmentJobstatus, Wtf.Panel, {
    initComponent: function() {
        Wtf.recruitmentJobstatus.superclass.initComponent.call(this);
    },
    onRender: function(config) {
        Wtf.recruitmentJobstatus.superclass.onRender.call(this, config);

        this.record = Wtf.data.Record.create([{
            name:'id'
        },{
            name:'jobid'
        },{
            name:'jname'
        },{
            name:'applydt',
            type:'date'
        },{
            name:'interviewdt',
            type:'date'
        },{
            name:'status'
        },{
            name:'jobpositionid'
        },{
            name:'jobDetails'
        }]);
        this.reader= new Wtf.data.KwlJsonReader1({
            root: 'data',
            totalProperty:"count"
        },
        this.record
        );

        this.ds = new Wtf.data.Store({
//            url: Wtf.req.base + 'hrms.jsp',
            url: "Rec/Job/getJobApplications.rec",
            baseParams: {
                flag:38 ,
                userid:this.profId
            },
            reader:this.reader
        });
        calMsgBoxShow(202,4,true);
        this.ds.load();
        this.ds.on("load",function(){Wtf.MessageBox.hide();},this);

        this.sm= new Wtf.grid.RowSelectionModel({
            singleSelect:false
        });
        this.cm = new Wtf.grid.ColumnModel([
            new Wtf.grid.RowNumberer(),{
                header: WtfGlobal.getLocaleText("hrms.recruitment.job.id"),
                dataIndex: 'jobpositionid'
            },{
                header: WtfGlobal.getLocaleText("hrms.common.designation"),
                dataIndex: 'jname'
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.job.details"),
                dataIndex: 'jobDetails'
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.applied.date"),
                renderer:this.dateRenderer,
                dataIndex: 'applydt'
            },{
                header:  WtfGlobal.getLocaleText("hrms.common.status"),
                dataIndex: 'status',
                renderer : function(val) {
                    if(val=='Pending')
                        return '<FONT COLOR="blue">'+WtfGlobal.getLocaleText("hrms.recruitment.pending")+'</FONT>'
                    else if(val=='Shortlisted')
                        return '<FONT COLOR="green">'+WtfGlobal.getLocaleText("hrms.recruitment.shortlisted")+'</FONT>'
                    else if(val=='In Process')
                        return '<FONT COLOR="red">'+WtfGlobal.getLocaleText("hrms.recruitment.in.process")+'</FONT>'
                    else if(val=='On Hold')
                        return '<FONT COLOR="DarkGoldenRod">'+WtfGlobal.getLocaleText("hrms.recruitment.on.hold")+'</FONT>'
                    else if(val=='Rejected')
                        return '<FONT COLOR="Indigo">'+WtfGlobal.getLocaleText("hrms.recruitment.rejected")+'</FONT>'
                    else if(val=='Selected')
                        return '<FONT COLOR="Fuchsia">'+WtfGlobal.getLocaleText("hrms.recruitment.Selected")+'</FONT>'
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.recruitment.interview.date"),
                renderer:this.dateRenderer,
                dataIndex: 'interviewdt'
            }]);

        this.myjobstatusgrid=new Wtf.KwlGridPanel({
            id:'recjobstatusgrid'+this.profId,
            cm:this.cm,
            store:this.ds,
            sm:this.sm,
            border:false,
            layout:'fit',
            loadMask:true,
            displayInfo:true,
            searchLabel:" ",
            searchLabelSeparator:" ",
            searchEmptyText:WtfGlobal.getLocaleText("hrms.recruitment.SearchbyJobIdJobDetails"),
            searchField:"jobpositionid",
            serverSideSearch: true,
            viewConfig:{
                forceFit:true,
                emptyText:'<center><font size="4">'+WtfGlobal.getLocaleText("hrms.recruitment.job.grid.msg")+'</font></center>'
            }
        });

        this.add(this.myjobstatusgrid);
    },
    dateRenderer: function(v) {
        if(!v) return v;
        return v.format("l, F d, Y");
    }
});





