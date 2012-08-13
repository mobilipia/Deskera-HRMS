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
Wtf.WageTaxDeducWin=function(config){
    config.border=false;
    config.layout="fit";
    config.ren=0;
    Wtf.WageTaxDeducWin.superclass.constructor.call(this,config);
}
Wtf.extend(Wtf.WageTaxDeducWin,Wtf.Panel,{
    initComponent:function(config){
        Wtf.WageTaxDeducWin.superclass.initComponent.call(this,config);
        this.text=new Wtf.form.NumberField({});
        this.netcomp = 0;
        this.amtcomp = 0;
        this.deduccomp = 0;
        this.text1=new Wtf.form.NumberField({});
        this.sm=new Wtf.grid.CheckboxSelectionModel({
            scope:this
        });
        this.fieldstax=[
        {
            name:'type'
        },{
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
            name: 'depwage'
        },
        {
            name: 'depwageid'
        },
        {
            name: 'expr'
        },
        {
            name: 'computeon'
        },
        {
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
                obj.storetax.clearFilter(true);
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
                        var ind = obj.storetax.find('id',wgid);
                        if(ind>-1){
                            if(cnt==0){
                                returnstr += "+";
                            }else{
                                returnstr += "-";
                            }
                            returnstr +=  ceff + " * " + obj.storetax.getAt(ind).get("type");
                        }
                    }
                }
                obj.storetax.filter("comp",'tax');
                return returnstr;
            }else{
                return "N/A";
            }
            }},this)
        });
        this.fieldstax=Wtf.data.Record.create(this.fieldstax);
        this.readertax=new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.fieldstax);
        this.storetax= new Wtf.data.Store({
            url: "Payroll/Tax/getTaxMaster.py",
            method:'GET',
            reader: this.readertax
        });
        this.storetaxforgrid= new Wtf.data.Store({
            url: "Payroll/Tax/getDefualtTax.py",
            method:'GET',
            reader: this.readertax
        });
        this.storetaxforgrid.load({
            params:{
                type:'getDefualtTaxes',
                start:0,
                grouper:'addpayroll',
                limit:15
            }
        });
        this.storetaxforgrid.on("load",function(){
            this.addemptyrec();
        },this)


        this.storetax1= new Wtf.data.Store({
            url: Wtf.req.base + "PayrollHandler.jsp",
            method:'GET'
        });
        this.Ttypecombo= new Wtf.form.ComboBox({
            store:this.storetax,
            fieldLabel:WtfGlobal.getLocaleText("hrms.payroll.TaxTypeName"),
            displayField:'type',
            valueField:'type',
            mode: 'local',
            forceSelection:true,
            scope:this,
            width:180,
            height:200,
            triggerAction :'all',
            listeners:{
                scope:this,
                select:function(combo,rec,index){
                    //this.insertrate=this.grid.getSelectionModel().getSelected();
                    this.insertrateRate=rec.get('cash');
                    this.insertrateCode=rec.get('code');
                    this.insertrateId=rec.get('id');
                    this.insertrateratetype=rec.get('rate');
                    this.insertratedepwage=rec.get('depwage');
                    this.insertratedepwageid=rec.get('depwageid');
                    this.insertratetype=rec.get('type');
                    this.insertratecomputeon=rec.get('computeon');
                    this.insertrateexpr=rec.get('expr');
                }
            }
        });
        this.Ttypecombo.on("expand", function(obj, rec){
//            this.storetax.filterBy(function(record,id){
//                if(record.get('rate')==0){
//                    return true;
//                }else{
//                    return false;
//                }
//            },this);
            this.storetax.filter("comp",'tax');
        },this);
        this.Tratenumberfield=new Wtf.form.NumberField({
            allowNegative:false
        });
        this.checkselmodel=new Wtf.grid.CheckboxSelectionModel({
            scope:this
        });
        this.cmtax=new Wtf.grid.ColumnModel([
//            new Wtf.grid.CheckboxSelectionModel({
//                scope:this
//            }),
            this.expander,
            {
                header: WtfGlobal.getLocaleText("hrms.payroll.TaxType"),
                scope:this,
                width:200,
                dataIndex: 'type',
                editor:this.Ttypecombo
            },{
                header:WtfGlobal.getLocaleText("hrms.common.code"),
                dataIndex: 'code'
            },{
                header:  WtfGlobal.getLocaleText("hrms.common.value"),
                dataIndex: 'cash',
                renderer:function(val, meta, record, rowIndex, colIndex, store){
                    if(record.data.rate=='1'){
                        return('<div align=\"right\">'+parseFloat(val).toFixed(2)+' %</div>');
                    }
                    else{
                        return('<div align=\"right\" style=\"font-family:Lucida Sans Unicode;\">'+WtfGlobal.payrollcurrencyRenderer(val)+'</div>');
                    }
                }
            },{
                header: WtfGlobal.getLocaleText("hrms.payroll.percent.of"),
                dataIndex: 'computeon',
                align: 'center',
                sortable: true,
//                renderer:function(val,a,b){
//                    if(val!=""){
//                        if(b.get('rate')!=0)
//                            return val;
//                        else
//                            return ('<div>-</div>');
//                    }else{
//                        if(b.get('rate')==0)
//                            return ('<div>-</div>');
//                        else
//                            return ('<div>Salary/month</div>');
//                    }
//                }
                renderer:this.rendererfun.createDelegate(this)
            },{
            	header:  WtfGlobal.getLocaleText("hrms.common.delete"),
                dataIndex:'',
                width:35,
                renderer:function(val, meta, record){
                    if(record.data.id!='-1')
                        return "<div><div class='pwndCommon gridCancel' style='cursor:pointer' wtf:qtip="+WtfGlobal.getLocaleText("hrms.common.DeleteRecord")+"></div></div>";
                }
            }
            ]);
        this.storetax.load({
            params:{
                allflag:'true',
                type:'Tax',
                wage:'true',
                deduc:'true',
                grouper:'addpayroll',
                cname:'aa'
            }
        });
        this.grid = new Wtf.grid.EditorGridPanel({
            scope:this,
            stripeRows: true,
            clicksToEdit:1,
            id:this.id+'addtaxgrid',
            store:this.storetaxforgrid,
            cm:this.cmtax,
            sm:this.checkselmodel,
            listeners:
            {
                scope:this,
                cellclick:function(grid,rowind,colind,e){
                }
            },
            plugins:this.expander,
            autoScroll :true,
            viewConfig: {
                forceFit: true,
                emptyText:WtfGlobal.emptyGridRenderer(WtfGlobal.getLocaleText("hrms.payroll.Nowageassignedforcurrentsalarytemplate"))
            },
            title: WtfGlobal.getLocaleText("hrms.common.Taxes")
        });
        this.grPanel=new Wtf.Panel({
            layout:'fit',
            border:false,
            items:[this.grid]
        });
//        this.grid.on("validateedit",function(e)
//        {
//            var row1 = this.grid.getStore().findBy(function(rec){
//                if(rec.data.type == e.value){
//                    return true;
//                }
//            },this);
//            if(row1 == -1){
//           // if(this.grid.getStore().find('type',e.value)==-1){
//                var wagestore = (this.edittemp=='yes')?Wtf.getCmp(this.parentId).wageentryform1.storewageforgrid:Wtf.getCmp(this.parentId).wageentryform.storewageforgrid;
//                var row = wagestore.findBy(function(rec){
//                    if(rec.data.type == this.insertratedepwage){
//                        return true;
//                    }
//                },this);
//                if(row!=-1 || this.insertratedepwage==""){
//               // if(wagestore.find('type',this.insertratedepwage)!=-1 || this.insertratedepwage==""){
//                    //this.insertrate=this.grid.getSelectionModel().getSelected();
//                    this.insertrate=e.record;
//                    if(e.column==0){
//                        this.insertrate.beginEdit();
//                        this.insertrate.set('cash',this.insertrateRate);
//                        this.insertrate.set('code',this.insertrateCode);
//                        this.insertrate.set('id',this.insertrateId);
//                        this.insertrate.set('rate',this.insertrateratetype);
//                        this.insertrate.set('depwage',this.insertratedepwage);
//                        this.insertrate.set('depwageid',this.insertratedepwageid);
//                        this.insertrate.endEdit();
//                    }
//                } else {
//                    this.cancel=true;
//                    return false;
//                }
//            } else
//            {
//                this.cancel=true;
//                return false;
//            }
//            var rec=e.record;
//            if(rec.data.rate=='1'){
//                if(e.value>100){
//                    return false;
//                }
//            }
//        },this);

        this.grid.on("validateedit",function(e)
        {
            this.currentedit = e.record;
            var row1 = this.grid.getStore().findBy(function(rec){
                if(rec.data.type == e.value){
                    return true;
                }
            },this);
            if(row1 == -1){
                if(this.insertrateratetype=="0")
                    this.amtcomp++;
                if(this.insertratecomputeon=="0"){
                    this.deduccomp++;
                    this.insertcomp(e.column);
                }else if(this.insertratecomputeon=="1"){
//                    this.earncomp++;
                    this.insertcomp(e.column);
                    this.amtcomp++;
                }else if(this.insertratecomputeon=="2"){
                    if(this.netcomp>0){
                         Wtf.MessageBox.show({
                                title: WtfGlobal.getLocaleText("hrms.payroll.Operationnotpermitted"),
                                msg: WtfGlobal.getLocaleText("hrms.payroll.Youcannotincludetwocomponentswhichdependsoneachother"),
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
                                    this.storetax.clearFilter(true);
                                    this.getdep(this.insertrateexpr);
                                    this.storetax.filter("comp",'tax');
                                } else {
                                    this.grid.getStore().remove(e.record);
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

        this.grid.on("afteredit",function(e){
            if(e.row==(this.grid.getStore().getCount()-1)){
                this.addemptyrec();
                this.grid.getSelectionModel().selectAll();
                this.grid.getSelectionModel().deselectRow(this.grid.getStore().getCount()-1);
            }
        },this);
        this.add(this.grPanel);
        this.on('activate', function(tp, tab){
            this.doLayout();
        });
        this.storetax.on('load',function(){
            if(this.edittemp=="yes"){
                for(this.i=0;this.i<this.grid.getStore().getCount()-1;this.i++){
                    this.rec=this.grid.getStore().getAt(this.i);
                    if(this.rec.get('assigned')=='1'){
                        this.checkselmodel.selectRow(this.i,true);
                    }
                }
            }
        },this);

        if(this.edittemp=="yes"){
            this.storetaxforgrid.on('load',function(){
                this.grid.getSelectionModel().selectAll();
                this.grid.getSelectionModel().deselectRow(this.grid.getStore().getCount()-1);
            },this);
        }
        this.grid.on("click",function(e){
            if(e.target.className=='pwndCommon gridCancel'){
                var selected = this.grid.getSelectionModel().getSelected();
                if(this.grid.getSelectionModel().getSelections().length>0){
                    if(this.grid.getStore().indexOf(this.grid.getSelectionModel().getSelected())!=this.grid.getStore().getCount()-1){
                        this.grid.getStore().remove(this.grid.getSelectionModel().getSelected());
                        Wtf.getCmp("editTemp").enable();
                        if(selected.get("computeon")==2){
                            this.netcomp--;
                        }
                    }
                }else{
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
        if(this.paramstore!=1){
            this.addemptyrec();
        }
    },
    getdep: function(exprstr){
        var expr = exprstr.split("(add)");
        for(var ctr=0;ctr<expr.length;ctr++){
            var subexpr = expr[ctr].split("(sub)");
            for(var cnt=0;cnt<subexpr.length;cnt++){
                var index = this.grid.getStore().find('id',subexpr[cnt]);
                if(index==-1 && subexpr[cnt]!=""){
                      var ind = this.storetax.find('id',subexpr[cnt]);
                      if(ind>-1){
                          var comborec = this.storetax.getAt(ind);
                          if(comborec.get("comp")=="tax"){
                            this.grid.getStore().insert(0,comborec);
                          } else if(comborec.get("comp")=="wage"){
                                var idsplit = this.id.split("addtax");
                                if(idsplit.length>0){
                                    var wageobj = Wtf.getCmp(idsplit[0]+"addwage");
                                    if(wageobj!=null){
                                        var ind = wageobj.storewageforgrid.find('id',subexpr[cnt]);
                                        if(ind==-1){
                                            wageobj.addrecord(comborec);
                                        }
                                    }
                                }
                          } else {
                              if(idsplit.length>0){
                                    var wageobj = Wtf.getCmp(idsplit[0]+"adddeduc");
                                    if(wageobj!=null){
                                        var ind = wageobj.storededucforgrid.find('id',subexpr[cnt]);
                                        if(ind==-1){
                                            wageobj.addrecord(comborec);
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
    insertcomp: function(col){
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
                this.storetax.clearFilter(true);
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
                        var ind = this.storetax.find('id',subexpr[cnt]);
                        if(ind>-1){
                            if(cnt==0){
                                returnstr += "+";
                            }else{
                                returnstr += "-";
                            }
                            returnstr +=  ceff + " * " + this.storetax.getAt(ind).get("type");
                        }
                    }
                }
                this.storetax.filter("comp",'tax');
                return returnstr;
            }
     },
    addemptyrec:function(ind){
        this.grid.getStore().add(new this.record({
            type:'',
            cash:'',
            id:'-1',
            code:''
        }));
    }
});
