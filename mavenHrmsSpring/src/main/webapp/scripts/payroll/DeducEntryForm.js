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
Wtf.DeducEntryForm=function(config){
    config.border=false;
    config.layout="fit";
    Wtf.DeducEntryForm.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.DeducEntryForm,Wtf.Panel,{
    initComponent:function(config){
        Wtf.DeducEntryForm.superclass.initComponent.call(this,config);
        this.earncomp = 0;
        this.deduccomp = 0;
        this.netcomp = 0;
        this.amtcomp = 0;
        this.fieldsdeduc=[
        {
            name:'type'
        },
        {
            name:'code'
        },
        {
            name:'cash'
        },
        {
            name:'id'
        },
        {
            name:'rate'
        },
        {
            name:'assigned'
        },
        {
            name:'amount'
        },
        {
            name: 'depwage'
        },
        {
            name: 'depwageid'
        },{
            name: 'computeon'
        },{
            name: 'expr'
        },{
            name: 'comp'
        }
        ];
         this.expander = new Wtf.grid.RowExpander({
           tpl : new Wtf.XTemplate('<p>&nbsp;&nbsp;&nbsp;&nbsp;<b>'+WtfGlobal.getLocaleText("hrms.payroll.compute.on")+':</b> {[this.f(values)]}</p>',{f:function(record){
            var val = record.computeon;
            var obj = Wtf.getCmp(this.id);
            if(val=="0"){
                return WtfGlobal.getLocaleText("hrms.payroll.currentdeductions");
            }else if(val=="1"){
                return WtfGlobal.getLocaleText("hrms.payroll.currentearnings");
            }else if(val=="2"){
                return WtfGlobal.getLocaleText("hrms.payroll.netsalary");
            }else if(val=="3"){
                var returnstr = "";
                var expr = record.expr.split("(add)");
                obj.storededuc.clearFilter(true);
                for(var ctr=0;ctr<expr.length;ctr++){
                    var subexpr = expr[ctr].split("(sub)");
                    for(var cnt=0;cnt<subexpr.length;cnt++){
                        var exprwthcoeff = subexpr[cnt].split("\*");
                        var ceff =1;
                        var wgid;
                        if(exprwthcoeff.length > 1) {
                            ceff = exprwthcoeff[0];
                            wgid = exprwthcoeff[1]
                        } else {
                            wgid = exprwthcoeff[0]
                        }
                        if(wgid==-1){
                            if(cnt==0){
                                returnstr += "+";
                            }else{
                                returnstr += "-";
                            }
                            returnstr += ceff + " * " + "<b>"+WtfGlobal.getLocaleText("hrms.payroll.template.basic")+"</b>"
                        }
                        var ind = obj.storededuc.find('id',wgid);
                        if(ind>-1){
                            if(cnt==0){
                                returnstr += "+";
                            }else{
                                returnstr += "-";
                            }
                            returnstr += ceff + " * " + obj.storededuc.getAt(ind).get("type");
                        }
                    }
                }
//                obj.storededuc.filter("comp",'deduc');
                return returnstr;
            }else{
                return WtfGlobal.getLocaleText("hrms.common.n.a");
            }
            }},this)
        });
        this.fieldsdeduc=Wtf.data.Record.create(this.fieldsdeduc);
        this.readerdeduc=new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.fieldsdeduc);
        this.storededuc= new Wtf.data.Store({
            url: "Payroll/Deduction/getDeductionMaster.py",
            method:'GET',
            reader: this.readerdeduc
        });
        this.storededucforgrid= new Wtf.data.Store({
            url: "Payroll/Deduction/getDefualtDeduction.py",
            method:'GET',
            reader: this.readerdeduc
        });
        this.storededucforgrid.load({
            params:{
                type:'getDefualtDeduction',
                start:0,
                grouper:'addpayroll',
                limit:15
            }
        });
        this.storededucforgrid.on("load",function(){
            var totalcount = this.storededucforgrid.query("computeon", "2");
            this.deduccomp = totalcount.length;
            this.addemptyrec();
        },this)
        this.Dtypecombo= new Wtf.form.ComboBox({
            store:this.storededuc,
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.DeductionTypeName"),
            displayField:'type',
            mode: 'local',
            forceSelection:true,
            valueField:'type',
            scope:this,
            width:180,
            height:200,
            triggerAction :'all',
            listeners:{
                scope:this,
                select:function(combo,rec,index){
                    this.insertrate=this.DeductionGridPanel.getSelectionModel().getSelected();
                    this.insertrateRate=rec.get('cash');
                    this.insertrateCode=rec.get('code');
                    this.insertrateId=rec.get('id');
                    this.insertratetype=rec.get('type');
                    this.insertrateratetype=rec.get('rate');
                    this.insertratedepwage=rec.get('depwage');
                    this.insertratedepwageid=rec.get('depwageid');
                    this.insertratecomputeon=rec.get('computeon');
                    this.insertrateexpr=rec.get('expr');
                }
            }
        });
        this.Dtypecombo.on("expand", function(obj, rec){
            this.storededuc.filter("comp",'deduc');
        },this);
        this.text=new Wtf.form.NumberField({
            allowNegative:false
        });
        this.checkboxselmodel=new Wtf.grid.CheckboxSelectionModel({
            scope:this
        });
        this.cmdeduc=new Wtf.grid.ColumnModel([
//            new Wtf.grid.CheckboxSelectionModel({
//                scope:this
//            }),
            this.expander,{
                header: WtfGlobal.getLocaleText("hrms.payroll.DeductionType"),
                dataIndex: 'type',
                width:200,
                editor:this.Dtypecombo
            },{
                header:WtfGlobal.getLocaleText("hrms.common.code"),
                dataIndex: 'code'
            },
            {
                header:  WtfGlobal.getLocaleText("hrms.common.value"),
                dataIndex: 'cash',
                renderer:function(val, meta, record, rowIndex, colIndex, store){
                    if(record.data.rate=='1'){
                        return('<div align=\"right\">'+parseFloat(val).toFixed(2)+' %</div>');
                    } else{
                        return('<div align=\"right\" style=\"font-family:Lucida Sans Unicode;\">'+WtfGlobal.payrollcurrencyRenderer(val)+'</div>');
                    }
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.payroll.percent.of"),
                dataIndex: 'computeon',
                align: 'center',
                sortable: true,
                renderer:this.rendererfun.createDelegate(this)
            },{
            	header:  WtfGlobal.getLocaleText("hrms.common.delete"),
                dataIndex:'',
                width:35,
                renderer:function(val, meta, record){
                    if(record.data.id !='-1')
                         return "<div><div class='pwndCommon gridCancel' style='cursor:pointer' wtf:qtip="+WtfGlobal.getLocaleText("hrms.common.DeleteRecord")+"></div></div>";
                }
            }
            ]);

        this.storededuc1= new Wtf.data.Store({
            url: Wtf.req.base + "PayrollHandler.jsp",
            method:'GET'
        });
        this.storededuc.load({
            params:{
                allflag:'true',
                type:'Deduction',
                cname:'aa',
                empcont:'true',
                grouper:'addpayroll',
                wage:'true'
            }
        });
        this.sm=new Wtf.grid.CheckboxSelectionModel({
            scope:this
        });
		
        this.DeductionGridPanel=new Wtf.grid.EditorGridPanel({
            scope:this,
            clicksToEdit:1,
            displayField:'type',
            store:this.storededucforgrid,
            cm:this.cmdeduc,
            sm:this.checkboxselmodel,
            stripeRows: true,
            id:this.id+'adddeducgrid',
            listeners:
            {
                scope:this,
                cellclick:function(grid,rowind,colind,e){
                    if(rowind==grid.getStore().getCount()-1){
                }
                }
            },
            autoScroll :true,
            title:WtfGlobal.getLocaleText("hrms.common.Deductions"),
            plugins:this.expander,
            height:440,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.Nowageassignedforcurrentsalarytemplate"))
            }
        });
        this.add(this.DeductionGridPanel);
        this.DeductionGridPanel.on("validateedit",function(e)
        {
            this.currentedit = e.record;
           // if(this.DeductionGridPanel.getStore().find('type',e.value)==-1){
            var row1 = this.DeductionGridPanel.getStore().findBy(function(rec){
                if(rec.data.type == e.value){
                    return true;
                }
            },this);
            if(row1 == -1){
                if(this.insertrateratetype=="0")
                    this.amtcomp++;
                if(this.insertratecomputeon=="0"){
                    if(this.amtcomp==0){
                        Wtf.MessageBox.show({
                                title: WtfGlobal.getLocaleText("hrms.payroll.Operationnotpermitted"),
                                msg: WtfGlobal.getLocaleText("hrms.payroll.Pleaseselectatleastonefixeddeductioncomponentfirst"),
                                buttons: Wtf.MessageBox.OK,
                                animEl: 'mb9',
                                scope:this,
                                icon: Wtf.MessageBox.INFO
                        });
                        this.cancel=true;
                        return false;
                    }else if(this.deduccomp>0||this.netcomp>0){
                        Wtf.MessageBox.show({
                                title: WtfGlobal.getLocaleText("hrms.payroll.Operationnotpermitted"),
                                msg: WtfGlobal.getLocaleText("hrms.payroll.YoucannotincludetwocomponentsontypeCurrentEarning"),
                                buttons: Wtf.MessageBox.OK,
                                animEl: 'mb9',
                                scope:this,
                                icon: Wtf.MessageBox.INFO
                        });
                        this.cancel=true;
                        return false;
                    }else{
                        this.deduccomp++;
                        this.insertcomp(e.column);
                    }
                }else if(this.insertratecomputeon=="1"){
                    var idsplit = this.id.split("adddeduc");
                        if(idsplit.length>0){
                            var deducobj = Wtf.getCmp(idsplit[0]+"addwage");
                            if(deducobj!=null){
                                if(deducobj.deduccomp>0||deducobj.netcomp>0){
                                    Wtf.MessageBox.show({
                                            title: WtfGlobal.getLocaleText("hrms.payroll.Operationnotpermitted"),
                                            msg: WtfGlobal.getLocaleText("hrms.payroll.AsyouhaveincludedcomponentinWageDeductionyoucannotincludeWagedependentcomponentinDeduction"),
                                            buttons: Wtf.MessageBox.OK,
                                            animEl: 'mb9',
                                            scope:this,
                                            icon: Wtf.MessageBox.INFO
                                    });
                                    this.cancel=true;
                                    return false;
                                }else{
                    this.earncomp++;
                    this.insertcomp(e.column);
                                    this.amtcomp++;
                                }
                            }
                        }
                }else if(this.insertratecomputeon=="2"){
                    if(this.deduccomp>0||this.netcomp>0){
                         Wtf.MessageBox.show({
                                title:WtfGlobal.getLocaleText("hrms.payroll.Operationnotpermitted"),
                                msg: WtfGlobal.getLocaleText("hrms.payroll.Youcannotincludetwocomponentswhichdependsoneachother"),
                                buttons: Wtf.MessageBox.OK,
                                animEl: 'mb9',
                                scope:this,
                                icon: Wtf.MessageBox.INFO
                        });
                        this.cancel=true;
                        return false;
                    }else{
                        var idsplit = this.id.split("adddeduc");
                        if(idsplit.length>0){
                            var deducobj = Wtf.getCmp(idsplit[0]+"addwage");
                            if(deducobj!=null){
                                if(deducobj.deduccomp>0||deducobj.netcomp>0){
                                    Wtf.MessageBox.show({
                                            title: WtfGlobal.getLocaleText("hrms.payroll.Operationnotpermitted"),
                                            msg: WtfGlobal.getLocaleText("hrms.payroll.AsyouhaveincludedcomponentinWageDeductionyoucannotincludeWagedependentcomponentinDeduction"),
                                            buttons: Wtf.MessageBox.OK,
                                            animEl: 'mb9',
                                            scope:this,
                                            icon: Wtf.MessageBox.INFO
                                    });
                                    this.cancel=true;
                                    return false;
                                }else{
                        this.netcomp++;
                        this.insertcomp(e.column);
                    }
                            }
                        }
                    }
                    this.amtcomp++;
                }else if(this.insertratecomputeon=="3"){
                    Wtf.MessageBox.show({
                            title:  WtfGlobal.getLocaleText("hrms.common.warning"),
                            msg: WtfGlobal.getLocaleText("hrms.payroll.SomeDependenceSureYouWanttoContinue"),
                            buttons: Wtf.MessageBox.YESNO,
                            animEl: 'mb9',
                            scope:this,
                            icon: Wtf.MessageBox.INFO,
                            fn:function(btn,text){
                                if(btn=="yes"){
                                    this.insertcomp(e.column);
                                    this.storededuc.clearFilter(true);
                                    this.getdep(this.insertrateexpr);
                                    this.storededuc.filter("comp",'deduc');
                                } else {
                                    this.DeductionGridPanel.getStore().remove(e.record);
//                                    this.insertrate=e.record;
//                                    if(e.column==1){
//                                        this.insertrate.set('type','');
//                                    }
                                    this.cancel=true;
                                    return false;
                                }
                            }
                    });
                    this.amtcomp++;
                }else{
                    this.insertcomp(e.column);
                }

            } else{
                this.cancel=true;
                return false;
            }
            if(e.record.data.rate=='1'){
                if(e.record.data.cash>100){
                    return false;
                }
            }
        },this);
        this.DeductionGridPanel.on("afteredit",function(e){
            if(e.row==this.DeductionGridPanel.getStore().getCount()-1){
                this.addemptyrec();
                this.DeductionGridPanel.getSelectionModel().selectAll();
                this.DeductionGridPanel.getSelectionModel().deselectRow(this.DeductionGridPanel.getStore().getCount()-1);
            }
        },this);
        this.on('activate', function(tp, tab){
            this.doLayout();
        });
        this.storededuc.on('load',function(){
            if(this.edittemp=="yes"){
                for(this.i=0;this.i<this.DeductionGridPanel.getStore().getCount()-1;this.i++){
                    this.rec=this.DeductionGridPanel.getStore().getAt(this.i);
                    if(this.rec.get('assigned')=='1'){
                        this.checkboxselmodel.selectRow(this.i,true);
                    }
                }
            }
        },this);

        if(this.edittemp=="yes"){
            this.storededucforgrid.on('load',function(){
                var totalcount = this.storewageforgrid.query("computeon", "1");
                var totalcount1 = this.storewageforgrid.query("computeon", "2");
                var totalcount2 = this.storewageforgrid.query("computeon", "0");
                this.earncomp = totalcount.length;
                this.netcomp = totalcount1.length;
                this.deduccomp = totalcount2.length;
                this.DeductionGridPanel.getSelectionModel().selectAll();
                this.DeductionGridPanel.getSelectionModel().deselectRow(this.DeductionGridPanel.getStore().getCount()-1);
            },this);
        }

        this.DeductionGridPanel.on("click",function(e){
            if(e.target.className=='pwndCommon gridCancel'){
                var selected = this.DeductionGridPanel.getSelectionModel().getSelected();
                if(this.DeductionGridPanel.getSelectionModel().getSelections().length>0){
                    if(this.DeductionGridPanel.getStore().indexOf(selected)!=this.DeductionGridPanel.getStore().getCount()-1){
                        for(var n = 0 ; n < this.DeductionGridPanel.getStore().data.length ; n++){
                            var mainstring = this.DeductionGridPanel.getStore().getAt(n).data.expr;
                            if(mainstring != undefined){
                                if(mainstring.indexOf(selected.data.id) != -1){
                                    calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.payroll.CannotdeletedSomeothercomponentsaredependsonthiswage")],2);
                                    return;
                                }
                            }
                        }
                        var taxstore = (this.edittemp=='yes')?Wtf.getCmp(this.parentId).taxentryform1.storetaxforgrid:Wtf.getCmp(this.parentId).taxentryform.storetaxforgrid;
                        var deducstore = (this.edittemp=='yes')?Wtf.getCmp(this.parentId).deductionentryform1.storededuc:Wtf.getCmp(this.parentId).deductionentryform.storededuc;
//                        for(n = 0 ; n < taxstore.data.length ; n++){
////                            if(taxstore.getAt(n).data.depwageid == selected.data.id){
//                            var mainstring = taxstore.getAt(n).data.expr;
//                            if(mainstring.indexOf(selected.data.id) != -1){
//                                calMsgBoxShow(["Warning","Can not deleted.Some other components are depends on this wage."],2);
//                                return;
//                            }
//                        }
                        for(n = 0 ; n < deducstore.data.length ; n++){
                            var mainstring = deducstore.getAt(n).data.expr;
                            if(mainstring.indexOf(selected.data.id) != -1){
                                calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),WtfGlobal.getLocaleText("hrms.payroll.CannotdeletedSomeothercomponentsaredependsonthiswage")],2);
                                return;
                            }
                        }
                        this.DeductionGridPanel.getStore().remove(this.DeductionGridPanel.getSelectionModel().getSelected());
                        if(selected.get("rate")==0){
                            this.amtcomp--;
                        }
                        if(selected.get("computeon")==0){
                            this.deduccomp--;
                        }else if(selected.get("computeon")==1){
                            this.earncomp--;
                            this.amtcomp--;
                        }else if(selected.get("computeon")==2){
                            this.netcomp--;
                            this.amtcomp--;
                        }else if(selected.get("computeon")==3){
                            this.amtcomp--;
                        }else {
                        Wtf.getCmp("editTemp").enable();
                    }
                }
            }
            }
        },this);
        this.record=new Wtf.data.Record.create([
        {
            name:'type'

        },
        {
            name:'cash'

        },
        {
            name:'id'

        },
        {
            name:'code'

        }
        ]);
//        if(this.paramstore!=1){
//            this.addemptyrec();
//
//            this.storededuc.load({
//                params:{
//                    allflag:'true',
//                    type:'Deduction',
//                    cname:'aa',
//                    wage:'true'
//                }
//            });
//        }
    },
    addemptyrec:function(){
        this.DeductionGridPanel.getStore().add(new this.record({
            type:'',
            cash:'',
            id:'-1',
            code:''
        }));
    },

    rendererfun: function(val,met,record,row,col,store){
        if(record.get("rate")==0){
            return "-"
        }
            if(val=="0"){
                return WtfGlobal.getLocaleText("hrms.payroll.currentdeductions");
            }else if(val=="1"){
                return WtfGlobal.getLocaleText("hrms.payroll.currentearnings");
            }else if(val=="2"){
                return WtfGlobal.getLocaleText("hrms.payroll.netsalary");
            }else if(val=="3"){
                var returnstr = "";
                var expr = record.get("expr").split("(add)");
                this.storededuc.clearFilter(true);
                for(var ctr=0;ctr<expr.length;ctr++){
                    var subexpr = expr[ctr].split("(sub)");
                    for(var cnt=0;cnt<subexpr.length;cnt++){
                        var exprwthcoeff = subexpr[cnt].split("\*");
                        var ceff =1;
                        var wgid;
                        if(exprwthcoeff.length > 1) {
                            ceff = exprwthcoeff[0];
                            wgid = exprwthcoeff[1]
                        } else {
                            wgid = exprwthcoeff[0]
                        }
                        if(wgid==-1){
                            if(cnt==0){
                                returnstr += "+";
                            }else{
                                returnstr += "-";
                            }
                             returnstr += ceff + " * " + "<b>"+WtfGlobal.getLocaleText("hrms.payroll.template.basic")+"</b>"
                        }
                        var ind = this.storededuc.find('id',wgid);
                        if(ind>-1){
                            if(cnt==0){
                                returnstr += "+";
                            }else{
                                returnstr += "-";
                            }
                            returnstr +=  ceff + " * " + this.storededuc.getAt(ind).get("type");
                        }
                    }
                }
                this.storededuc.filter("comp",'deduc');
                return returnstr;
            }
     },
    insertcomp: function(col){
        //this.insertrate=this.DeductionGridPanel.getSelectionModel().getSelected();
        this.insertrate=this.currentedit;
        if(col==1){
            this.insertrate.beginEdit();
            this.insertrate.set('cash',this.insertrateRate);
            this.insertrate.set('code',this.insertrateCode);
            this.insertrate.set('id',this.insertrateId);
            this.insertrate.set('rate',this.insertrateratetype);
            this.insertrate.set('depwage',this.insertratedepwage);
            this.insertrate.set('type',this.insertratetype);
            this.insertrate.set('depwageid',this.insertratedepwageid);
            this.insertrate.set('computeon',this.insertratecomputeon);
            this.insertrate.set('expr',this.insertrateexpr);
            this.insertrate.endEdit();
        }
    },
    getdep: function(exprstr){
        var expr = exprstr.split("(add)");
        for(var ctr=0;ctr<expr.length;ctr++){
            var subexpr = expr[ctr].split("(sub)");
            for(var cnt=0;cnt<subexpr.length;cnt++){
                var exprwthcoeff = subexpr[cnt].split("\*");
                var wgid;
                if(exprwthcoeff.length > 1) {
                    wgid = exprwthcoeff[1]
                } else {
                    wgid = exprwthcoeff[0]
                }
                var index = this.DeductionGridPanel.getStore().find('id',wgid);
                if(index==-1 && subexpr[cnt]!=""){
                      var ind = this.storededuc.find('id',wgid);
                      if(ind>-1){
                          var ind1;
                          var idsplit;
                          var comborec = this.storededuc.getAt(ind);
                          if(comborec.get("comp")=="deduc")
                            this.DeductionGridPanel.getStore().insert(0,comborec);
                          else if(comborec.get("comp")=="wage"){
                                idsplit = this.id.split("adddeduc");
                                if(idsplit.length>0){
                                    var wageobj = Wtf.getCmp(idsplit[0]+"addwage");
                                    if(wageobj!=null){
                                        ind1 = wageobj.storewageforgrid.find('id',wgid);
                                        if(ind1==-1){
                                            wageobj.addrecord(comborec);
                                        }
                                    }
                                }
                          } else {
                                idsplit = this.id.split("adddeduc");
                                if(idsplit.length>0){
                                    var empContObj = Wtf.getCmp(idsplit[0]+"addempcontrib");
                                    if(empContObj!=null){
                                        ind1 = empContObj.storewageforgrid.find('id',wgid);
                                        if(ind1==-1){
                                            empContObj.addrecord(comborec);
                                        }
                                    }
                                }
                          }
                          if(comborec.get("computeon")=="3")
                            this.getdep(comborec.get("expr"));
                      }
                }
            }
        }
    },
    addrecord: function(newrec){
        this.DeductionGridPanel.getStore().insert(0,newrec);
    }
    
});
