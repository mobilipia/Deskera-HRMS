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
Wtf.MalaysianUserIncomeTaxForm = function (config){
    Wtf.apply(this,config);
    Wtf.MalaysianUserIncomeTaxForm.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.MalaysianUserIncomeTaxForm, Wtf.Panel,{
	layout:'fit',
	closable:false,
	
    initComponent:function (){
        Wtf.MalaysianUserIncomeTaxForm.superclass.initComponent.call(this);
    },

    onRender: function(config){
    	var arr = [];
    	var userInfo = this.getUserInformationFieldSet();
        var currentEmployer = this.getCurrentEmployerFieldSet();
        var previousEmployer = this.getPreviousEmployerFieldSet();
        arr.push(userInfo);
        arr.push(currentEmployer);
        arr.push(previousEmployer);
        
    	var compulsory = this.getComponent(this.compulsoryDeductions, "(Compulsory Deductions)", 0);
		var optional = this.getComponent(this.optionalDeductions, "Optional Deductions", 1);
		var allowance= this.getComponent(this.allowanceDeductions, "(Allowance Deductions)", 0);
		for(var i=0; i<compulsory.length; i++){
			arr.push(compulsory[i]);
		}
		for(var i=0; i<optional.length; i++){
			arr.push(optional[i]);
		}
		for(var i=0; i<allowance.length; i++){
			arr.push(allowance[i]);
		}
		
//		for(var i=0; i<arr.length; i++){
//			for(var j=0; j<arr[i].items.items[j].length; j++){
//				arr[i].items.items[j].on("change", function(){
//				}, this);
//			}
//		}
		
		this.deductionFormPanel= new Wtf.form.FormPanel({
			labelWidth:600,
            autoScroll:true,
			baseParams:{
				userid:this.userid,
				year:this.year
			},
			url:"Payroll/MalaysianIncomeTax/saveUserIncomeTaxInformation.py",
			border:true,
			bodyBorder:false,
			style:"padding-left:20px; padding-top:20px",
			id:'deductionFormPanel'+this.usrid,
			items:arr
		});
    	this.mainPanel = new Wtf.Panel({
    		layout:"fit",
    		border:false,
    		scope:this,
    		items:[this.deductionFormPanel],
    		bbar:['->',{
    	     	id: 'Submit'+this.usrid,
    	     	iconCls:getButtonIconCls(Wtf.btype.submitbutton),
    	        text:WtfGlobal.getLocaleText("hrms.common.submit"),
    	        handler: this.submit.createDelegate(this)
    	    }]
    	});
    	
    	this.add(this.mainPanel);
    	Wtf.MalaysianUserIncomeTaxForm.superclass.onRender.call(this, config);
    },
    
    submit:function(){
    	if(this.deductionFormPanel.form.isValid()){
    		this.deductionFormPanel.getForm().submit({
                waitMsg:WtfGlobal.getLocaleText("hrms.payroll.saving.incometax.information"),
                success:function(){
    				Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.success"), WtfGlobal.getLocaleText("hrms.payroll.user.incometax.saved.successfully")); 
                },
                failure:function(f,a){
                	Wtf.notify.msg(WtfGlobal.getLocaleText("hrms.common.error"), WtfGlobal.getLocaleText("hrms.payroll.error.saving.user.incometax"));
                },
                scope:this
            });
    	}else{
    		ResponseAlert(152);
    	}
    },
    
    getComponent: function(obj, name, flag){
		var finalArray = [];
		var comp;
		var otherCompNumberField = [];
		var otherCompCheckBox = [];
		var subField = [];
		
		for(var i=0; i<obj.length; i++){
            if(obj[i].parent.length==0){
				if(obj[i].datatype==1){
                    comp = new Wtf.form.NumberField({
						fieldLabel:obj[i].name+WtfGlobal.addLabelHelp(obj[i].description),
						name:obj[i].id,
						maxLength:10,
						allowNegative:false,
						scope:this,
						id:obj[i].id+this.usrid,
						value:obj[i].value
					});
					otherCompNumberField.push(comp);
				}else if(obj[i].datatype==2){
					comp = new Wtf.form.Checkbox({
			            fieldLabel:obj[i].name+WtfGlobal.addLabelHelp(obj[i].description),
			            name:obj[i].id,
			            id:obj[i].id+this.usrid,
			            checked:obj[i].uniquecode==3?true:obj[i].value
			        });
					otherCompCheckBox.push(comp);
				}
			}else{
				var temp=[];
				for(var j=0; j<obj[i].parent.length; j++){

                    if(obj[i].parent[j].datatype==1){

                        var maxlength=10;

                        if(obj[i].parent[j].uniquecode>5 && obj[i].parent[j].uniquecode < 11){ // For Children Fields
                            maxlength = 2;
                        }

                        temp.push(new Wtf.form.NumberField({
							fieldLabel:obj[i].parent[j].name+WtfGlobal.addLabelHelp(obj[i].parent[j].description),
							name:obj[i].parent[j].id,
							maxLength:maxlength,
							allowNegative:false,
							scope:this,
							id:obj[i].parent[j].id+this.usrid,
							value:obj[i].parent[j].value
						}));
					}else if(obj[i].parent[j].datatype==2){
						temp.push(new Wtf.form.Checkbox({
				            fieldLabel:obj[i].parent[j].name+WtfGlobal.addLabelHelp(obj[i].parent[j].description),
				            name:obj[i].parent[j].id,
				            id:obj[i].parent[j].id+this.usrid,
				            checked:obj[i].parent[j].uniquecode==3?true:obj[i].parent[j].value
				        }));
					}
				}
				comp = new Wtf.form.FieldSet({
					title:obj[i].name+" "+name,
					style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
					width:'95%',
					autoHeight:true,
					items:temp
		        });
				subField.push(comp);
			}
		}
		
		var array = [];
		for(var i=0; i<otherCompCheckBox.length; i++){
			array.push(otherCompCheckBox[i]);
		}
		for(var i=0; i<otherCompNumberField.length; i++){
			array.push(otherCompNumberField[i]);
		}
				
		for(var i=0; i<subField.length; i++){
			finalArray.push(subField[i]);
		}
		
		if(array.length>0){
			comp = new Wtf.form.FieldSet({
				title:flag==0?("Other Details "+name):name,
				style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
				width:'95%',
				autoHeight:true,
				items:array
			});
			finalArray.push(comp);
		}
		return finalArray;
	},
	
	getUserInformationFieldSet : function(){

        var arr = [];

        this.empStatus = new Wtf.data.SimpleStore({
    		id    : 'empStatus',
    		fields: ['id','name'],
    		data: [["1", WtfGlobal.getLocaleText("hrms.payroll.resident")],
    	           ["2", WtfGlobal.getLocaleText("hrms.payroll.non.resident")],
    	           ["3", WtfGlobal.getLocaleText("hrms.payroll.returning.expert.program")],
                   ["4", WtfGlobal.getLocaleText("hrms.payroll.knowledge.worker")]
    	          ]
    	});

        this.empStatusCombo = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.status")+'*',
            hiddenName: 'empstatus',
            mode:"local",
            valueField:'id',
            displayField:'name',
            forceSelection:true,
            store:this.empStatus,
            width:230,
            typeAhead:true,
            value:this.userdata!=undefined?this.userdata.empstatus:"",
            allowBlank:false
        });
    	arr.push(this.empStatusCombo);

        this.categoryStore = new Wtf.data.SimpleStore({
    		id    : 'categoryStore',
    		fields: ['id','name'],
    		data: [["1", WtfGlobal.getLocaleText("hrms.common.Single")],
    	           ["2", WtfGlobal.getLocaleText("hrms.payroll.married.spouse.not.working")],
    	           ["3", WtfGlobal.getLocaleText("hrms.payroll.married.spouse.working")]
    	          ]
    	});
        
        this.categoryCombo = new Wtf.form.ComboBox({
            triggerAction:"all",
            fieldLabel:WtfGlobal.getLocaleText("hrms.common.category")+'*',
            hiddenName: 'category',
            mode:"local",
            valueField:'id',
            displayField:'name',
            forceSelection:true,
            store:this.categoryStore,
            width:230,
            typeAhead:true,
            value:this.userdata!=undefined?this.userdata.category:"",
            allowBlank:false
        });
    	arr.push(this.categoryCombo);

        var comp = new Wtf.form.FieldSet({
            title:WtfGlobal.getLocaleText("hrms.common.user.information"),
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'95%',
			autoHeight:true,
            items:arr
        });

        return comp;

    },

    getCurrentEmployerFieldSet : function(){

        var arr = [];

        var currentEPF = new Wtf.form.Checkbox({
            fieldLabel: WtfGlobal.getLocaleText("hrms.common.epf")+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("hrms.payroll.check.to.allow.deduction.epf")),
            scope:this,
            name:"current_epf",
            checked:this.userdata!=undefined?this.userdata.epf:false
        });
        arr.push(currentEPF);

        var currentLIC = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.life.insurance")+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("hrms.payroll.total.deduction.life.insurance.epf.6000.year")),
            scope:this,
            name:"current_lic",
            value:this.userdata!=undefined?this.userdata.lic:0
        });
        arr.push(currentLIC);

        var currentZakat = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.zakat.fees.levy.paid")+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("hrms.payroll.enter.amount.deducted.for.zakat")),
            scope:this,
            name:"current_zakat",
            value:this.userdata!=undefined?this.userdata.zakat:0
        });
        arr.push(currentZakat);

        var currentBIK = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.benefits.in.kind")+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("hrms.payroll.employee.include.bik.living.accommodation.tp2.form.employer")),
            scope:this,
            name:"current_bik",
            value:this.userdata!=undefined?this.userdata.bik:0
        });
        arr.push(currentBIK);

        var comp = new Wtf.form.FieldSet({
            title:WtfGlobal.getLocaleText("hrms.payroll.current.employer"),
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'95%',
			autoHeight:true,
            items:arr
        });


        return comp;

    },
    
    getPreviousEmployerFieldSet : function(){

        var arr = [];
        var previousEarning = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.net.accumulated.remuneration")+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("hrms.payroll.net.accumulated.remuneration.previous.employer.tp3.form")),
            scope:this,
            name:"previous_earning",
            value:this.userdata!=undefined?this.userdata.prevearning:0
        });
        arr.push(previousEarning);
        
        var previousIncomeTax = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.accumulated.mtd.paid")+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("hrms.payroll.net.accumulated.mtd.deduction.previous.employement.current.year")),
            scope:this,
            name:"previous_income_tax",
            value:this.userdata!=undefined?this.userdata.previncometax:0
        });
        arr.push(previousIncomeTax);
        
        var previousEPF = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.accumulated.epf")+" "+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("hrms.payroll.net.accumulated.epf.deduction.previous.employment.current.year")),
            scope:this,
            name:"previous_epf",
            value:this.userdata!=undefined?this.userdata.prevepf:0
        });
        arr.push(previousEPF);
        
        var previousLIC = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.accumulated.life.insurance")+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("hrms.payroll.net.accumulated.life.insurance.previous.employment.current.year")),
            scope:this,
            name:"previous_lic",
            value:this.userdata!=undefined?this.userdata.prevlic:0
        });
        arr.push(previousLIC);
        
        var previousZakat = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.total.accumulated.zakat.fees.levy.paid")+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("hrms.payroll.net.accumulated.zakat.previous.employment.current.year")),
            scope:this,
            name:"previous_zakat",
            value:this.userdata!=undefined?this.userdata.prevzakat:0
        });
        arr.push(previousZakat);

        var previousOtherDeduction = new Wtf.form.NumberField({
            fieldLabel: WtfGlobal.getLocaleText("hrms.payroll.accumulated.other.deduction")+WtfGlobal.addLabelHelp(WtfGlobal.getLocaleText("hrms.payroll.net.accumulated.other.deductions.previous.employment.current.year")),
            scope:this,
            name:"previous_otherdeduction",
            value:this.userdata!=undefined?this.userdata.prevotherdeduction:0
        });
        arr.push(previousOtherDeduction);
        
        var comp = new Wtf.form.FieldSet({
            title:WtfGlobal.getLocaleText("hrms.common.PreviousEmployer"),
            style:"padding-left:20px; padding-top:20px; padding-right: 20px; padding-bottom:20px; align:center;",
			width:'95%',
			autoHeight:true,
            items:arr
        });

        return comp;
    }
});
