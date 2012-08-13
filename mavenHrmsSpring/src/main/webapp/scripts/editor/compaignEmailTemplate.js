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
Wtf.namespace('Wtf.ux');
Wtf.namespace('Wtf.ux.Wiz');
Wtf.namespace('Wtf.ux.layout');

Wtf.ux.Wiz = Wtf.extend(Wtf.Panel, {
	loadMaskConfig: {
		'default': WtfGlobal.getLocaleText("hrms.common.saving")
	},
	cards: null,
	previousButtonText: '&lt;&lt; Previous',
	nextButtonText: 'Next &gt;&gt;',
	cancelButtonText:  WtfGlobal.getLocaleText("hrms.common.cancel"),
	finishButtonText: 'Finish',
	headerConfig: {},
	cardPanelConfig: {},
	previousButton: null,
	nextButton: null,
	cancelButton: null,
	cardPanel: null,
	currentCard: -1,
	headPanel: null,
	cardCount: 0,

    initComponent: function(){
		this.initButtons();
		this.initPanels();
		var title = this.title || this.headerConfig.title;
		title = title || "";
		Wtf.apply(this, {
			title: title,
			layout: 'border',
			cardCount: this.cards.length,
			tbar: [this.previousButton, this.nextButton],
			items: [this.headPanel, this.cardPanel]
		});
		this.addEvents('cancel', 'finish', 'beforefinish', 'beforeNextcard');
		Wtf.ux.Wiz.superclass.initComponent.call(this);
	},
	getWizardData: function(){
		var formValues = {};
		var cards = this.cards;
		for (var i = 0, len = cards.length; i < len; i++) {
			if (cards[i].form) {
				formValues[cards[i].id] = cards[i].form.getValues(false);
			} else {
				formValues[cards[i].id] = {};
			}
		}
		return formValues;
	},
	switchDialogState: function(enabled, type){
		this.showLoadMask(!enabled, type);
		this.previousButton.setDisabled(!enabled);
		this.nextButton.setDisabled(!enabled);
		this.cancelButton.setDisabled(true);
		if (this.closable) {
			var ct = this.tools['close'];
			switch (enabled) {
				case true:
					this.tools['close'].unmask();
					break;
				default:
					this.tools['close'].mask();
					break;
			}
		}
	},
	showLoadMask: function(show, type){
		if (!type) {
			type = 'default';
		}
		if (show) {
			if (this.loadMask == null) {
				this.loadMask = new Wtf.LoadMask(this.body);
			}
			this.loadMask.msg = this.loadMaskConfig['type'];
			this.loadMask.show();
		} else {
			if (this.loadMask) {
				this.loadMask.hide();
			}
		}
	},
	initEvents: function(){
		Wtf.ux.Wiz.superclass.initEvents.call(this);
		var cards = this.cards;
		for (var i = 0, len = cards.length; i < len; i++) {
			cards[i].on('show', this.onCardShow, this);
			cards[i].on('hide', this.onCardHide, this);
			cards[i].on('clientvalidation', this.onClientValidation, this);
		}
	},
	initPanels: function(){
		var cards = this.cards;
		var cardPanelConfig = this.cardPanelConfig;
		Wtf.apply(this.headerConfig, {
			steps: cards.length
		});
		this.headPanel = new Wtf.ux.Wiz.Header(this.headerConfig);
		Wtf.apply(cardPanelConfig, {
			layout: new Wtf.ux.layout.CardLayout(),
			items: cards
		});
		Wtf.applyIf(cardPanelConfig, {
			region: 'center',
			border: false,
			activeItem: 0
		});
		this.cardPanel = new Wtf.Panel(cardPanelConfig);
	},
	initButtons: function(){
		this.previousButton = new Wtf.Button({
			text: this.previousButtonText,
			disabled: true,
			minWidth: 75,
			handler: this.onPreviousClick,
			scope: this
		});
		this.nextButton = new Wtf.Button({
			text: this.nextButtonText,
			minWidth: 75,
			handler: this.onNextClick,
			scope: this
		});
//		this.cancelButton = new Wtf.Button({
//			text: this.cancelButtonText,
//			handler: this.onCancelClick,
//			scope: this,
//			minWidth: 75
//		});
	},
	onClientValidation: function(card, isValid){
		if (!isValid) {
			this.nextButton.setDisabled(true);
		}
		else {
			this.nextButton.setDisabled(false);
		}
	},
	onCardHide: function(card){
		if (this.cardPanel.layout.activeItem.id === card.id) {
			this.nextButton.setDisabled(true);
		}
	},
	onCardShow: function(card){
		var parent = card.ownerCt;
		var items = parent.items;
		for (var i = 0, len = items.length; i < len; i++) {
			if (items.get(i).id == card.id) {
				break;
			}
		}
		this.currentCard = i;
		this.headPanel.updateStep(i, card.title);
		if (i == len - 1) {
			this.nextButton.setText(this.finishButtonText);
		}
		else {
			this.nextButton.setText(this.nextButtonText);
		}
		if (card.isValid()) {
			this.nextButton.setDisabled(false);
		}
		if (i == 0) {
			this.previousButton.setDisabled(true);
		}
		else {
			this.previousButton.setDisabled(false);
		}
	},
	onCancelClick: function(){
		if (this.fireEvent('cancel', this) !== false) {
			this.close();
		}
	},
	onFinish: function(){
		if (this.fireEvent('beforefinish', this, this.cards[this.currentCard]) !== false) {
//            for(var cnt = 0; cnt < this.cards.length; cnt++) {
//                this.cards[cnt].destroy();
//                this.cards[cnt].ownerCt.remove(this.cards[cnt], true);
//            }
//			this.ownerCt.remove(this, true);
//            this.fireEvent('finish', this);
		}
	},
    closePanel: function(){
        for(var cnt = 0; cnt < this.cards.length; cnt++) {
            this.cards[cnt].destroy();
            this.cards[cnt].ownerCt.remove(this.cards[cnt], true);
        }
        this.ownerCt.remove(this, true);
    },
	onPreviousClick: function(){
		if (this.currentCard > 0) {
			this.cardPanel.getLayout().setActiveItem(this.currentCard - 1);
		}
	},
	onNextClick: function(){
        if(this.fireEvent("beforeNextcard", this, this.currentCard)) {
            if (this.currentCard == this.cardCount - 1) {
                this.onFinish();
            }
            else {
                this.cardPanel.getLayout().setActiveItem(this.currentCard + 1);
            }
        }
	}
});

Wtf.ux.Wiz.Header = Wtf.extend(Wtf.BoxComponent, {
	height: 55,
	region: 'north',
	title: 'Wizard',
	steps: 0,
	stepText: "Step {0} of {1}: {2}",
	autoEl: {
		tag: 'div',
		cls: 'wtf-ux-wiz-Header',
		children: [{
			tag: 'div',
			cls: 'wtf-ux-wiz-Header-title'
		}, {
			tag: 'div',
			children: [{
				tag: 'div',
				cls: 'wtf-ux-wiz-Header-step'
			}, {
				tag: 'div',
				cls: 'wtf-ux-wiz-Header-stepIndicator-container'
			}]
		}]
	},
	titleEl: null,
	stepEl: null,
	imageContainer: null,
	indicators: null,
	stepTemplate: null,
	lastActiveStep: -1,
	updateStep: function(currentStep, title){
		var html = this.stepTemplate.apply({
			0: currentStep + 1,
			1: this.steps,
			2: title
		});
		this.stepEl.update(html);
		if (this.lastActiveStep != -1) {
			this.indicators[this.lastActiveStep].removeClass('wtf-ux-wiz-Header-stepIndicator-active');
		}
		this.indicators[currentStep].addClass('wtf-ux-wiz-Header-stepIndicator-active');
		this.lastActiveStep = currentStep;
	},
	onRender: function(ct, position){
		Wtf.ux.Wiz.Header.superclass.onRender.call(this, ct, position);
		this.indicators = [];
		this.stepTemplate = new Wtf.Template(this.stepText), this.stepTemplate.compile();
		var el = this.el.dom.firstChild;
		var ns = el.nextSibling;
		this.titleEl = new Wtf.Element(el);
		this.stepEl = new Wtf.Element(ns.firstChild);
		this.imageContainer = new Wtf.Element(ns.lastChild);
		this.titleEl.update(this.title);
		var image = null;
		for (var i = 0, len = this.steps; i < len; i++) {
			image = document.createElement('div');
			image.innerHTML = "&#160;";
			image.className = 'wtf-ux-wiz-Header-stepIndicator';
			this.indicators[i] = new Wtf.Element(image);
			this.imageContainer.appendChild(image);
		}
	}
});

Wtf.ux.Wiz.Card = Wtf.extend(Wtf.Panel, {
	header: false,
	hideMode: 'display',
	initComponent: function(){
		this.addEvents('beforecardhide');
		Wtf.ux.Wiz.Card.superclass.initComponent.call(this);
	},
	isValid: function(){
		if (this.monitorValid) {
			return this.bindHandler();
		}
		return true;
	},
	bindHandler: function(){
		this.form.items.each(function(f){
			if (!f.isValid) {
				f.isValid = Wtf.emptyFn;
			}
		});
		Wtf.ux.Wiz.Card.superclass.bindHandler.call(this);
	},
	initEvents: function(){
		var old = this.monitorValid;
		this.monitorValid = false;
		Wtf.ux.Wiz.Card.superclass.initEvents.call(this);
		this.monitorValid = old;
		this.on('beforehide', this.bubbleBeforeHideEvent, this);
		this.on('beforecardhide', this.isValid, this);
		this.on('show', this.onCardShow, this);
		this.on('hide', this.onCardHide, this);
	},
	bubbleBeforeHideEvent: function(){
		var ly = this.ownerCt.layout;
		var activeItem = ly.activeItem;
		if (activeItem && activeItem.id === this.id) {
			return this.fireEvent('beforecardhide', this);
		}
		return true;
	},
	onCardHide: function(){
		if (this.monitorValid) {
			this.stopMonitoring();
		}
	},
	onCardShow: function(){
		if (this.monitorValid) {
			this.startMonitoring();
		}
	}
});
Wtf.ux.layout.CardLayout = Wtf.extend(Wtf.layout.CardLayout, {
	setActiveItem: function(item){
		item = this.container.getComponent(item);
		if (this.activeItem != item) {
			if (this.activeItem) {
				this.activeItem.hide();
			}
			if (this.activeItem && !this.activeItem.hidden) {
				return;
			}
			this.activeItem = item;
			item.show();
			this.layout();
		}
	}
});


//********************template control
Wtf.campaignMailTemplate = function(conf) {
    Wtf.apply(this, conf);
    Wtf.campaignMailTemplate.superclass.constructor.call(this, {
        layout: "fit",
        border: false
    });
}

Wtf.extend(Wtf.campaignMailTemplate, Wtf.Panel, {
    onRender: function(conf){
        Wtf.campaignMailTemplate.superclass.onRender.call(this, conf);
        var campaignRec = Wtf.data.Record.create([
            {name: "templateid"},
            {name: "templatename"},
            {name: "description"},
            {name: "subject"},
            {name: "thumbnail"},
//            {name: "bodyhtml"},
            {name: "craetedon"}
        ]);
        var mailTemplate = new Wtf.data.Store({
            url: Wtf.req.base + "campaign.jsp",
            baseParams: {
                flag: 1,
                templateList:true
            },
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            }, campaignRec)
        });
        mailTemplate.on("load", function(obj, recs){
            var _cobj = this.templateSelector.body.dom.childNodes;
            while(_cobj.length > 0){
                Wtf.get(_cobj[0].id).remove();
            }
            for(var cnt = 0; cnt < recs.length; cnt++){
                var flg = false;
                if(this.selectedTemplate !== null && recs[cnt].data["templateid"] == this.selectedTemplate){
                    flg = true;
                }
                var thubmnail="../../images/" + recs[cnt].data["thumbnail"];
                if(recs[cnt].data["thumbnail"]==""){
                    thubmnail="../../images/template-default-img.gif";
                }
                var temp = new Wtf.emailTemplateThumbnail({
                    height: 120,
                    width: 100,
                    cssStyle: "margin:5px 10px 0px 10px;",
                    imgWidth: 70,
                    imgHeight: 90,
                    tqtip: recs[cnt].data["description"],
                    id: "thumbnail_" + recs[cnt].data["templateid"],
                    tName: recs[cnt].data["templatename"],
                    thumbnail: thubmnail,
                    tempRec: recs[cnt],
                    scope: this,
                    selected: flg,
                    listeners: {
                        "templateSelected": this.selectTemplate
                    },
                    renderTo: this.templateSelector.body.dom
                });
                this.templateSelector.childArr.push(temp);
                if(flg)
                    this.selectedTemplate = temp;
            }
         }, this);

        this.crateNewTemplate= new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.administration.new.email.template"),
            scope:this,
            tooltip:{text:WtfGlobal.getLocaleText("hrms.administration.create.new.email.template")},
            iconCls:"pwndCRM templateEmailMarketing",
            handler:function() {
                var panel = Wtf.getCmp('template_wiz_win'+this.templateid);
                var tipTitle=WtfGlobal.getLocaleText("hrms.administration.new.template");
                var title = Wtf.util.Format.ellipsis(tipTitle,18);
                if(panel==null) {
                    panel=new Wtf.newEmailTemplate({
                        title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("hrms.featurelist.email.template")+"'>"+title+"</div>",
                        tipTitle:tipTitle,
                        mailTemplate:mailTemplate
                    })
                    this.mainTab.add(panel);
                }
                this.mainTab.setActiveTab(panel);
                this.mainTab.doLayout();
            }
        });
        this.templateSelector = new Wtf.Panel({
            border: false,
            bodyStyle: "background-color: white;",
            childArr: [],
            layout: "fit",
            tbar:['-',this.crateNewTemplate,'-']
        });

        mailTemplate.load();
        this.templateCont = this.add(this.templateSelector);
    },
    getSelectedTemplate: function(){
        return this.selectedTemplate;
    },
    selectTemplate: function(tempObj) {
        var templates = this.scope.templateSelector.childArr;
        for(var cnt = 0; cnt < templates.length; cnt++) {
            templates[cnt].deselectTemplate();
        }
        tempObj.selectTemplate();
        this.scope.selectedTemplate = tempObj;
    }
});



//********************Add/Edit template control
Wtf.addEmailMarketCmp = function (config){
    Wtf.apply(this, config);
    Wtf.addEmailMarketCmp.superclass.constructor.call(this,{
        border:false,
        layout: "fit"
    });
}

Wtf.extend(Wtf.addEmailMarketCmp,Wtf.Panel,{
    onRender: function(config){
        Wtf.addEmailMarketCmp.superclass.onRender.call(this,config);
        this.targetRecord = new Wtf.data.Record.create([{
            name:'listid'
        },{
            name:'listname'
        }]);
        var targetReader = new Wtf.data.KwlJsonReader({
            root:'data',
            totalProperty:'totalCount'
        },this.targetRecord);
        this.targetStore = new Wtf.data.Store({
            url: Wtf.req.base + "campaign.jsp",
            baseParams:{
                flag:21,
                campID: this.campaignid,
                emailmarkid : this.mode==1 ? this.emailmarkid : ''
            },
            method:'post',
            reader:targetReader
        });
        this.targetStore.load();

        this.createTargetListBtn = new Wtf.Toolbar.Button({
            text:WtfGlobal.getLocaleText("hrms.campaigndetail.new.target.list"),
            scope:this,
            iconCls:"targetlistIcon",
            tooltip:{text:WtfGlobal.getLocaleText({key:"hrms.campaigndetail.create.target.list.campaign",params:[this.campaignname]})},
            handler: function() {
                var campId = 'campaigntargetdetail'+this.campaignid;
                var tipTitle =this.campaignname+"'s Targets";
                var title= Wtf.util.Format.ellipsis(tipTitle,17)
                var campComp = Wtf.getCmp(campId);
                if(campComp==null) {
                   campComp=new Wtf.campaignTargetList({
                        title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='"+WtfGlobal.getLocaleText("hrms.campaigndetail.campaign.targets")+"'>"+title+"</div>",
                        id:campId,
                        campaignid : this.campaignid,
                        newFlag:3,
                        arcFlag:1,
                        archivedFlag:1,
                        mainTab:this.mainTab,
                        storeTarget:this.targetStore
                    })
                   this.mainTab.add(campComp);
            }
            this.mainTab.setActiveTab(campComp);
            this.doLayout();
            }
        });
        this.activityform=new Wtf.form.FormPanel({
            autoScroll:true,
            border:false,
            items :[{
                border: false,
                defaults: {
                    border: false,
                    xtype: "fieldset",
                    autoHeight: true
                },
                items: [{
                    cls: "marketingFieldset",
                    title: "Marketing campaign setup",
                    layout:'column',
                    items: [{
                        columnWidth: 0.48,
                        layout: 'form',
                        border:false,
                        cls:'mailMarketingForm',
                        defaults: {
                            width: 430,
                            allowBlank : false,
                            labelStyle: "width: 100%;",
                            ctCls: "newTicketField"
                        },
                        items: [
                        this.name = new Wtf.form.TextField({
                            fieldLabel: 'Email Marketing Name*'
                        }),
                        this.userMailCombo = new Wtf.form.TextField({
                            fieldLabel: 'Sender Mail*',
                            vtype:'email'
                        })

                        ]
                    },{
                        columnWidth: 0.49,
                        cls: "marketingFieldsetRight",
                        layout: 'form',
                        border:false,
                        defaults: {
                            width: 430,
                            allowBlank : false,
                            labelStyle: "width: 100%;",
                            ctCls: "newTicketField"
                        },
                        items:[
                        this.fromname= new Wtf.form.TextField({
                            fieldLabel: 'From Name*'
                        }),
                        this.replayMail = new Wtf.form.TextField({
                            fieldLabel: 'Reply Mail*',
                            vtype:'email'
                        })
                        ]

                    }]
                }]
            }]
        });
        this.campTargetStore = new Wtf.data.Store({
            url: Wtf.req.base + "campaign.jsp",
            baseParams:{
                flag:5,
                emailmarkid : this.mode==1 ? this.emailmarkid : ''
            },
            method:'post',
            reader:targetReader
        });
        if(this.mode==1){
            this.fromname.setValue(this.recData.fromname);
            this.userMailCombo.setValue(this.recData.fromaddress);
            this.name.setValue(this.recData.name);
            this.replayMail.setValue(this.recData.replymail);
//            this.uSub.setValue(this.recData.unsub);
//            this.ffl.setValue(this.recData.fwdfriend);
//            this.arch.setValue(this.recData.archive);
//            this.up.setValue(this.recData.updatelink);
        } else if (this.mode==0) {
            this.fromname.setValue("Newsletters");
            this.userMailCombo.setValue("newsletters@deskera.com");
            this.name.setValue("");
            this.replayMail.setValue("newsletters@deskera.com");
        }
        this.campTargetStore.load();
        this.targetColumn = new Wtf.grid.ColumnModel([ new Wtf.grid.CheckboxSelectionModel(),
            {
                header:'Target List',
                dataIndex:'listname',
                renderer : function(val) {
                    return "<a href = '#' class='listofTargets'> "+val+"</a>";
                }
            }]);
        this.targetGrid = new Wtf.grid.GridPanel({
            store: this.targetStore,
            cm: this.targetColumn,
            sm : new Wtf.grid.CheckboxSelectionModel(),
            border : false,
            loadMask : true,
            viewConfig: {
                forceFit:true
            },
            tbar:['-',this.createTargetListBtn,'-']
        });
        this.add({
            region : 'center',
            border : false,
            layout : 'fit',
            items :[{
                layout :'border',
                border : false,
                defaults : {
                    border : false,
                    layout : 'fit'
                },
                items :[{
                    region : 'north',
                    cls: "panelCls",
                    bodyStyle : 'background:#f1f1f1;font-size:10px;padding:40px 10px 10px 25px;',
                    items : this.activityform
                },{
                    region : 'center',
                    bodyStyle : 'background:#f1f1f1;padding:0px 10px 10px 20px;',
                    items : this.targetGrid
                }]
            }]
        });
        this.targetGrid.on("cellclick",this.listofTargets,this);
        this.campTargetStore.load();
        this.targetStore.on('load',function(){
            for(var i=0 ;i < this.targetStore.getCount(); i++){
                for(var j=0 ;j < this.campTargetStore.getCount(); j++){
                    if(this.targetStore.data.items[i].data.listid==this.campTargetStore.data.items[j].data.listid){
                        this.targetGrid.getSelectionModel().selectRow(i,true);
                    }
                }
            }

        },this)

        this.campTargetStore.on('load',function(){
            this.targetStore.load();
        },this)
    },
    listofTargets:function(Grid,rowIndex,columnIndex, e){
        var event = e ;
        if(event.getTarget("a[class='listofTargets']")) {

            var targetlistId =this.targetStore.data.items[rowIndex].data.listid;
            var targetlistname=this.targetStore.data.items[rowIndex].data.listname;
            var tipTitle=targetlistname+"'s Targets";
            var title = Wtf.util.Format.ellipsis(tipTitle,19);
            var tlId = 'targetListsTargets'+this.id+targetlistId;
            var targetListTab = Wtf.getCmp(tlId );
            if(targetListTab == null) {
                targetListTab = new Wtf.targetListTargets({
                    id:'targetListsTargets'+this.id+targetlistId,
                    closable:true,
                    title:"<div wtf:qtip=\""+tipTitle+"\"wtf:qtitle='Target List'>"+title+"</div>",
                    layout:'fit',
                    border:false,
                    targetlistId:targetlistId,
                    iconCls:"pwndnewCRM targetlistTabicon"
                })
                this.mainTab.add(targetListTab);
            }
            this.mainTab.setActiveTab(targetListTab);
            this.mainTab.doLayout();
        }
    },
    getList: function(){
        var list = this.targetGrid.getSelectionModel().getSelections();
        return list;
    },
    getName: function(){
        return this.name.getValue().trim();
    },
    getSenderMail: function(){
        return this.userMailCombo.getValue().trim();
    },
    getFromName: function(){
        return this.fromname.getValue().trim();
    },
    getReplyMail: function(){
        return this.replayMail.getValue().trim();
    }
//    getUnsubscribeLink: function(){
//        return this.uSub.getValue().trim();
//    },
//    getForwardLink: function(){
//        return this.ffl.getValue().trim();
//    },
//    getArchiveLink: function(){
//        return this.arch.getValue().trim();
//    },
//    getUpdateLink: function(){
//        return this.up.getValue().trim();
//    }
});

Wtf.campaignMailEditor = function(conf) {
    Wtf.apply(this, conf);
    Wtf.campaignMailEditor.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.campaignMailEditor, Wtf.Panel, {
    layout: "fit",
    bodyStyle: "background-color: white;",
    border: false,
    onRender: function(conf){
        Wtf.campaignMailEditor.superclass.onRender.call(this, conf);
        this.templateEditor = new Wtf.Panel({
            region: "center",
            baseCls: "bodytest",
            split: true,
            tbar: [{
                text: "Send Test Mail",
                iconCls:"pwnd pmsgbtnicon",
                handler: this.previewTemplate,
                scope: this
            }],
            collapsible: false
        });
        this.themePanel = new Wtf.colorThemePanel({
            region: "east",
            layout: "fit",
            plugins: new Wtf.ux.collapsedPanelTitlePlugin(),
            title: "Design Themes",
//            bodyStyle: "background-color: rgb(232, 210, 184)",
            maxWidth: 300,
            width: 300,
            split: true,
            collapsible: true
        });
        this.themePanel.on("colorThemeSelect", this.applyColorTheme, this);
        this.editorCont = new Wtf.Panel({
            bodyStyle: "background-color: white",
            autoScroll: true,
            layout: "border",
            items: [this.templateEditor, this.themePanel],
            border: false
        });
        this.templateEditor.on("render", function(){
            var htmlCont = "";
            this.editorHtmlComp = new Wtf.emailTemplatePanel({
                renderTo: this.templateEditor.body.dom,
                bodyHtml: htmlCont
            });
            if(this.mode != 1)
                this.getTemplateContent();
//                this.editorHtmlComp.setBodyHtml(unescape(this.templateRec.data["bodyhtml"]));
            else
                this.getEmailMarketingContent();
        }, this);
        this.add(this.editorCont);
    },
    getEmailMarketingContent: function(){
        Wtf.Ajax.requestEx({
            url: Wtf.req.base + "campaign.jsp",
            params: {
                flag: 26,
                marketid: this.marketRec.id
            }
        }, this, function(action, response){
            if(action.success){
                this.editorHtmlComp.setBodyHtml(unescape(action.data.html));
                if(action.data.theme)
                    this.themePanel.getThemeFromId(action.data.theme)
            }
        }, function(){
            ResponseAlert(["Failure","Failed to Email Marketing content."]);
        });
    },
    getTemplateContent: function(){
        Wtf.Ajax.requestEx({
            url: Wtf.req.base + "campaign.jsp",
            params: {
                flag: 27,
                templateid: this.templateid
            }
        }, this, function(action, response){
            if(action.success){
                this.editorHtmlComp.setBodyHtml(unescape(action.data.html));
            }
        }, function(){
            ResponseAlert(["Failure","Failed to get Template content."]);
        });
    },
    getColorTheme: function(){
        return this.colortheme;
    },
    getPlainMessage: function(){
        return this.editorHtmlComp.getPlainText();
    },
    applyColorTheme: function(obj, theme){
        this.colortheme = theme.data["id"];
        this.editorHtmlComp.applyNewColorTheme(theme);
    },
    expandThemePanel: function(){
        var temp = this.themePanel.getSize();
        temp.width = 300;
        this.themePanel.setSize(temp);
    },
    previewTemplate: function(){
        Wtf.Ajax.requestEx({
            url: Wtf.req.base + "sendMail.jsp",
            params: {
//                name: "",
//                sender: "",
//                reply: "",
//                sub: "",
                tid: this.templateid,
                bodyhtml: this.editorHtmlComp.getPreviewHtml()
            }
        }, this, function(action, response){
            ResponseAlert([WtfGlobal.getLocaleText("hrms.common.success"),"Mail sent successfully."]);
        }, function(action, response){
            ResponseAlert(["Failure","Failed to send mail."]);
        });
    },
    changeTemplate: function(tempRec){
        if(tempRec !== undefined) {
            this.templateid = tempRec.data.templateid;
            this.templateRec = tempRec;
            this.getTemplateContent();
//            this.setTemplateHtml(unescape(tempRec.data["bodyhtml"]));
        }
    },
    getTemplateHtml: function(){
        return this.editorHtmlComp.getPreviewHtml();
    },
    setTemplateHtml: function(html){
        this.editorHtmlComp.setBodyHtml(html);
//        Wtf.Ajax.requestEx({
//            url: "",
//            params: {
//                templateid: "",
//                action: ""
//            }
//        }, this, function(request, response){
//            this.editorTemplate.append(this.editorCont.el.dom, {id: "someid", html: "test html"});
//        }, function(request, response){
//
//        });
    }
});


Wtf.colorThemePanel = function(conf){
    Wtf.apply(this, conf);
    this.addEvents({
        "colorThemeSelect": true
    });
    Wtf.colorThemePanel.superclass.constructor.call(this, {
        layout: "column",
        autoHeight: true,
        border: false
    });
}

Wtf.extend(Wtf.colorThemePanel, Wtf.Panel, {
    onRender: function(conf){
        Wtf.colorThemePanel.superclass.onRender.call(this, conf);
        this.addCategoryGrid();
        this.addColorThemeGrid();
    },
    addColorThemeGrid: function(){
        this.ctRec = Wtf.data.Record.create([{
            name: "id"
        },{
            name: "theme"
        },{
            name: "background"
        },{
            name: "headerbackground"
        },{
            name: "headertext"
        },{
            name: "footerbackground"
        },{
            name: "footertext"
        },{
            name: "bodybackground"
        },{
            name: "bodytext"
        },{
            name: "groupid"
        }]);
        this.ctStore = new Wtf.data.Store({
            url: Wtf.req.base + "campaign.jsp",
            baseParams: {
                flag: 24
            },
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            }, this.ctRec)
        });
        this.ctStore.on("load", function(){
            this.themeLoaded = true;
        }, this);
        this.ctStore.load();
        var colorCM = new Wtf.grid.ColumnModel([{
            header: "Name",
            dataIndex: "theme",
            renderer: function(val, meta, rec){
                return "<div class='themeImage'>" +
                    "<div class='themeImgBox' style='background-color:#" + rec.data.background + "'></div>" +
                    "<div class='themeImgBox' style='background-color:#" + rec.data.headerbackground + "'></div>" +
                    "<div class='themeImgBox' style='background-color:#" + rec.data.headertext + "'></div>" +
                    "<div class='themeImgBox' style='background-color:#" + rec.data.footerbackground + "'></div>" +
//                    "<div class='themeImgBox' style='background-color:#" + rec.data.footertext + "'></div>" +
                    "<div class='themeImgBox' style='background-color:#" + rec.data.bodybackground + "'></div>" + "</div>" +
//                    "<div class='themeImgBox' style='background-color:#" + rec.data.bodytext + "'></div>" + "</div>" +
                    "<span style='margin-left: 8px;'>" + val + "</span>";
            }
        }]);
        this.colorSM = new Wtf.grid.RowSelectionModel({
            singleSelect: true
        });
        this.themeGrid = new Wtf.grid.GridPanel({
            cm: colorCM,
            cls: "noborderGrid themeGrid",
            title: "Themes",
            ds: this.ctStore,
            autoScroll: true,
            sm: this.colorSM,
            columnWidth: 1,
            height: 250,
//            bodyStyle: "margin-top: 50px",
            viewConfig: {
                forceFit: true
            }
        });
        this.colorSM.on("rowselect", this.changeColorTheme, this);
//        this.colorSM.on("rowdeselect", this.useDefaultColorTheme, this);
        this.add(this.themeGrid);
    },
    getThemeFromId: function(thm){
        if(this.themeLoaded){
            this.getTheme();
        } else {
            this.thm = thm;
            this.ctStore.on("load", this.getTheme, this);
        }
    },
    getTheme: function(){
        var _rI = this.ctStore.findBy(function(rec){
            if(rec.data.id == this.thm)
                return true;
            else
                return false;
        }, this);
//        var theme;
        if(_rI != -1)
            this.colorSM.selectRow(_rI);
//            theme = this.ctStore.getAt(_rI);
//        this.ownerCt.ownerCt.editorHtmlComp.applyNewColorTheme(theme);
    },
    addCategoryGrid: function(){
        var grpRec = Wtf.data.Record.create([{
            name: "id"
        }, {
            name: "groupname"
        }])
        this.categoryStore = new Wtf.data.Store({
            url: Wtf.req.base + "campaign.jsp",
            baseParams: {
                flag: 25
            },
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            }, grpRec)
        });
        this.categoryStore.load();
        var categoryCM = new Wtf.grid.ColumnModel([{
            header: "Name",
            dataIndex: "groupname"
        }]);
        this.categorySM = new Wtf.grid.RowSelectionModel({
            singleSelect: true
        });
        this.categoryGrid = new Wtf.grid.GridPanel({
            cm: categoryCM,
            cls: "noborderGrid themeGrid",
            ds: this.categoryStore,
            columnWidth: 1,
            sm: this.categorySM,
            height: 150,
            title: "Categories",
//            bodyStyle: "margin-top: 50px",
            viewConfig: {
                forceFit: true
            }
        });
        this.categorySM.on("rowselect", this.filterColorTheme, this);
        this.categorySM.on("rowdeselect", this.shoAllColorTheme, this);
        this.add(this.categoryGrid);
    },
    changeColorTheme: function(obj, ri, rec){
        this.fireEvent("colorThemeSelect", this, rec);
    },
    useDefaultColorTheme: function(obj, ri, rec){
        ResponseAlert(["Alert","Change to default color theme"]);
    },
    filterColorTheme: function(obj, ri, rec){
        this.ctStore.filter("groupid", rec.data["id"]);
    },
    shoAllColorTheme: function(obj, ri, rec){
//        if(obj.getSelections)
//        alert("show all color themes");
    }
});

Wtf.emailTemplatePanel = function(conf) {
    Wtf.apply(this, conf);
    Wtf.emailTemplatePanel.superclass.constructor.call(this, conf);
}

Wtf.extend(Wtf.emailTemplatePanel, Wtf.Component, {
    onRender: function(conf){
        Wtf.emailTemplatePanel.superclass.onRender.call(this, conf);
        this.elDom = Wtf.get(this.renderTo).createChild({
            tag: "div",
            cls: "templateCompCont"
        });
        this.table1 = document.createElement("table");
        this.table1.setAttribute("cellspacing", 0);
        this.table1.setAttribute("width", "100%");
        this.table1.style.backgroundColor = "#FFFFCC";
        var tab1Body = document.createElement("tbody");
        var tab1Row = document.createElement("tr");
        var tab1Data = document.createElement("td");
        tab1Data.setAttribute("align", "center");
        tab1Row.appendChild(tab1Data);
        tab1Body.appendChild(tab1Row);
        this.table1.appendChild(tab1Body);
        var table2 = document.createElement("table");
        table2.setAttribute("cellspacing", 0);
        table2.setAttribute("cellpadding", 0);
        table2.setAttribute("width", "600");
        var tab2Body = document.createElement("tbody");
        var tab2Row = document.createElement("tr");
        this.contentHolder = document.createElement("td");
        tab2Row.appendChild(this.contentHolder);
        tab2Body.appendChild(tab2Row);
        table2.appendChild(tab2Body);
        tab1Data.appendChild(table2);
        this.contentHolder.innerHTML = this.bodyHtml;
        Wtf.get(this.contentHolder).addListener("click", this.contentClicked);
        this.elDom.appendChild(this.table1);
    },
    setBodyHtml: function(html){
        this.table1.style.backgroundColor = "#FFFFCC";
        this.contentHolder.innerHTML = html;
    },
    getPreviewHtml: function(){
        return this.elDom.dom.innerHTML;
    },
    contentClicked: function(e){
        var _to = e.target;
        if(_to.className.indexOf("tpl-content") != -1 || _to.className.indexOf("tpl-content-image") != -1){
            var _tw = new Wtf.editorWindow({
                headerImage: (_to.className == "tpl-content-image"),
                val: _to.innerHTML,
                parentCont: _to
            });
            _tw.on("okClicked", function(obj){
                var valObj = obj.getEditorVal();
                obj.parentCont.innerHTML = valObj.textVal;
                if(valObj.imageRec) {
                    obj.parentCont.parentNode.style.height = valObj.imageRec.data["height"] + "px";
                    obj.parentCont.parentNode.style.background = 'url(' + valObj.imageRec.data["url"] + ') no-repeat';
                }
            }, this);
            _tw.show();
        }
    },
    getPlainText: function(){
        var htm = this.elDom.dom.innerHTML;
        htm = htm.replace(/<p>/g, "");
        htm = htm.replace(/<\p>/g, "");
        htm = htm.replace(/<P>/g, "");
        htm = htm.replace(/<\P>/g, "");
        htm = htm.replace(/&nbsp;/g, "");
        htm = Wtf.util.Format.stripTags(htm);
        return htm;
    },
    applyNewColorTheme: function(theme){
        if(theme) {
            this.table1.style.backgroundColor = "#" + theme.data.background;
            var header = Wtf.select(".headerTop", true, this.table1);
            if(header.elements.length > 0){
                header = header.elements[0].dom;
                header.style.backgroundColor = "#" + theme.data.headerbackground;
                header.style.color = "#" + theme.data.headertext;
            }
            var footer = Wtf.select(".footerRow", true, this.table1);
            if(footer.elements.length > 0) {
                footer = footer.elements[0].dom;
                footer.style.backgroundColor = "#" + theme.data.footerbackground;
                var footerTxt = Wtf.select(".footerText", true, footer);
                if(footerTxt.elements.length != 0) {
                    footerTxt.elements[0].dom.style.color = "#" + theme.data.footertext;
                }
            }
            var body = Wtf.select(".defaultText", true, this.table1);
                if(body.elements.length > 0) {
                body = body.elements[0].dom;
                body.style.backgroundColor = "#" + theme.data.bodybackground;
                body.style.color = "#" + theme.data.bodytext;
            }
        }
    }
})

Wtf.editorWindow = function(conf) {
    Wtf.apply(this, conf);
    this.addEvents({
        "okClicked": true
    });
    Wtf.editorWindow.superclass.constructor.call(this, {
        width: 820,
        height: 580,
        resizable: false,
        layout: "fit",
        title: (this.title && this.title != "") ? this.title : "Edit Your Content",
        modal: true,
        buttons: [{
            text: "Ok",
            scope: this,
            handler: this.okClicked
        }, {
            text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
            scope: this,
            handler: this.cancelClicked
        }]
    });
}

Wtf.extend(Wtf.editorWindow, Wtf.Window, {
    onRender: function(conf) {
        Wtf.editorWindow.superclass.onRender.call(this, conf);
        var _iArr = [];
        this.careteEditor();
        if(this.headerImage) {
            var _iRec = Wtf.data.Record.create([{
                name: "id"
            },{
                name: "name"
            },{
                name: "url"
            },{
                name: "height"
            }]);
            var _is = new Wtf.data.Store({
                url: Wtf.req.base + "getFiles.jsp",
                baseParams: {
                    action: 3
                },
                reader: new Wtf.data.KwlJsonReader({
                    root: "data"
                }, _iRec)
            });
            _is.load();
            var _iCM = new Wtf.grid.ColumnModel([{
                header: "Name",
                dataIndex: "name"
            }]);
            var _iSM = new Wtf.grid.RowSelectionModel({
                singleSelect: true
            });
            _iSM.on("rowSelect", function(obj, ri, rec){
                document.getElementById("email_campaign_header_image").src = rec.data.url;
            }, this);
            this.imageGrid = new Wtf.grid.GridPanel({
                store: _is,
                cm: _iCM,
                height: 200,
                cls: "noborderGrid",
                layout: "fit",
                sm: _iSM,
                border: false,
                viewConfig: {
                    forceFit: true
                }
            });
            _iArr[_iArr.length] = new Wtf.Panel({
                layout: "column",
                region: "north",
                border: false,
                height: 200,
                items: [{
                    border: false,
                    height: 200,
                    columnWidth: 0.2,
                    items: this.imageGrid
                },{
                    columnWidth: 0.79,
                    autoScroll: true,
                    border: false,
                    height: 200,
                    bodyStyle: "text-align: center",
                    html: "<img id='email_campaign_header_image' style='margin-top: 5px' src='' />"
                }]
            });
        }
        _iArr[_iArr.length] = new Wtf.Panel({
            layout: "fit",
            region: "center",
            items: [this.mce]
        });
        this.add(new Wtf.Panel({
            layout: "border",
            items: _iArr
        }));
    },
    careteEditor: function(){
        this.mce = new Wtf.form.HtmlEditor({
            value: this.val,
            plugins: [
                new Wtf.ux.form.HtmlEditor.insertImage({
                    imageStoreURL: Wtf.req.base + "getFiles.jsp?action=1&type=img",
                    imageUploadURL: Wtf.req.base + "getFiles.jsp?action=2&type=img"
                }),
                new Wtf.ux.form.HtmlEditor.HR({}),
                new Wtf.ux.form.HtmlEditor.SpecialCharacters({})/*,
                new Wtf.ux.form.HtmlEditor.customButton({
                    buttonConf: {
                        iconCls: "taglist",
                        handler: this.showTagList,
                        scope: this,
                        tooltip: "Show tag list"
                    }
                })*/
            ]
        });
    },
    showTagList: function(){
        var tagStore = new Wtf.data.SimpleStore({
            fields: ["id", "value", "notation"],
            data: [["0", "Email Address", "{emailid}"], ["1", "First Name", "{firstname}"], ["2", "Last Name", "{lastname}"]/*,
                ["3", "Unsubscribe Link", "{unsubscribelink}"], ["4", "Forward To A Friend Link", "{fwdtofriendlink}"], ["5", "Update Profile Link", "{updateprofile}"]*/]
        });
        var tagCM = new Wtf.grid.ColumnModel([{
            header: "To Insert",
            dataIndex: "value"
        }, {
            header: "Use This",
            dataIndex: "notation"
        }]);
        var tagGrid = new Wtf.grid.GridPanel({
            layout: "fit",
            cm: tagCM,
            store: tagStore,
            viewConfig: {
                forceFit: true
            }
        });
        var metaTagList = new Wtf.Window({
            title: "Tag list",
            layout: "fit",
            modal: true,
            resizable: false,
            height: 250,
            width: 440,
            buttons: [{
                text:  WtfGlobal.getLocaleText("hrms.common.Close"),
                handler: function(){
                    metaTagList.close();
                }
            }],
            items: tagGrid
        });
        metaTagList.show();
    },

    okClicked: function(obj) {
        if(this.fireEvent("okClicked", this))
            this.close();
    },
    cancelClicked: function(obj) {
        this.close();
    },
    getEditorVal: function(){
        var valObj = {};
        valObj["textVal"] = this.mce.getValue();
        if(this.headerImage) {
            valObj["imageRec"] = this.imageGrid.getSelectionModel().getSelected();
        }
        return valObj;
    }
})
/*
Wtf.previewWindow = function(conf){
    Wtf.apply(this, conf);
    Wtf.previewWindow.superclass.constructor.call(this, {
        modal: true,
        autoScroll: true,
        resizable: false,
        buttons: [{
            text: "Send test mail",
            scope: this,
            handler: this.testMail
        },{
            text: "Close",
            scope: this,
            handler: this.close
        }]
    });
}

Wtf.extend(Wtf.previewWindow, Wtf.Window, {
    onRender: function(conf){
        Wtf.previewWindow.superclass.onRender.call(this, conf);
        this._pP = this.add(new Wtf.Panel({
            layout: "fit",
            html: this.bodyHtml
//            html: "<iframe height='100%' width='100%' />"
        }));
    },
    testMail: function(){
        alert("test mail");
    }
});

*/






Wtf.ns('Wtf.ux.form.HtmlEditor');

Wtf.ux.form.HtmlEditor.MidasCommand = Wtf.extend(Wtf.util.Observable, {
    init: function(cmp){
        this.cmp = cmp;
        this.btns = [];
        this.cmp.on('render', this.onRender, this);
        this.cmp.on('initialize', this.onInit, this, {
            delay: 100,
            single: true
        });
    },
    onInit: function(){
        Wtf.EventManager.on(this.cmp.getDoc(), {
            'mousedown': this.onEditorEvent,
            'dblclick': this.onEditorEvent,
            'click': this.onEditorEvent,
            'keyup': this.onEditorEvent,
            buffer: 100,
            scope: this
        });
    },
    onRender: function(){
        var midasCmdButton, tb = this.cmp.getToolbar(), btn;
        Wtf.each(this.midasBtns, function(b){
            if (Wtf.isObject(b)) {
                midasCmdButton = {
                    iconCls: 'x-edit-' + b.cmd,
                    handler: function(){
                        this.cmp.relayCmd(b.cmd);
                    },
                    scope: this,
                    tooltip: b.tooltip ||
                    {
                        title: b.title
                    },
                    overflowText: b.overflowText || b.title
                };
            } else {
                midasCmdButton = new Wtf.Toolbar.Separator();
            }
            btn = tb.addButton(midasCmdButton);
            if (b.enableOnSelection) {
                btn.disable();
            }
            this.btns.push(btn);
        }, this);
    },
    onEditorEvent: function(){
        var doc = this.cmp.getDoc();
        Wtf.each(this.btns, function(b, i){
            if (this.midasBtns[i].enableOnSelection || this.midasBtns[i].disableOnSelection) {
                if (doc.getSelection) {
                    if ((this.midasBtns[i].enableOnSelection && doc.getSelection() !== '') || (this.midasBtns[i].disableOnSelection && doc.getSelection() === '')) {
                        b.enable();
                    } else {
                        b.disable();
                    }
                } else if (doc.selection) {
                    if ((this.midasBtns[i].enableOnSelection && doc.selection.createRange().text !== '') || (this.midasBtns[i].disableOnSelection && doc.selection.createRange().text === '')) {
                        b.enable();
                    } else {
                        b.disable();
                    }
                }
            }
            if (this.midasBtns[i].monitorCmdState) {
                b.toggle(doc.queryCommandState(this.midasBtns[i].cmd));
            }
        }, this);
    }
});

Wtf.ux.form.HtmlEditor.Divider = Wtf.extend(Wtf.util.Observable, {
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    onRender: function(){
        this.cmp.getToolbar().addButton([new Wtf.Toolbar.Separator()]);
    }
});

Wtf.ux.form.HtmlEditor.IndentOutdent = Wtf.extend(Wtf.ux.form.HtmlEditor.MidasCommand, {
    midasBtns: ['|', {
        cmd: 'indent',
        tooltip: {
            title: 'Indent Text'
        },
        overflowText: 'Indent Text'
    }, {
        cmd: 'outdent',
        tooltip: {
            title: 'Outdent Text'
        },
        overflowText: 'Outdent Text'
    }]
});
Wtf.ux.form.HtmlEditor.RemoveFormat = Wtf.extend(Wtf.ux.form.HtmlEditor.MidasCommand, {
    midasBtns: ['|', {
        enableOnSelection: true,
        cmd: 'removeFormat',
        tooltip: {
            title: 'Remove Formatting'
        },
        overflowText: 'Remove Formatting'
    }]
});

Wtf.ux.form.HtmlEditor.SubSuperScript = Wtf.extend(Wtf.ux.form.HtmlEditor.MidasCommand, {
    midasBtns: ['|', {
        enableOnSelection: true,
        cmd: 'subscript',
        tooltip: {
            title: 'Subscript'
        },
        overflowText: 'Subscript'
    }, {
        enableOnSelection: true,
        cmd: 'superscript',
        tooltip: {
            title: 'Superscript'
        },
        overflowText: 'Superscript'
    }]
});



Wtf.ux.form.HtmlEditor.SpecialCharacters = Wtf.extend(Wtf.util.Observable, {
    specialChars: [],
    charRange: [160, 256],
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    onRender: function(){
        var cmp = this.cmp;
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-char',
            handler: function(){
                if (this.specialChars.length == 0) {
                    Wtf.each(this.specialChars, function(c, i){
                        this.specialChars[i] = ['&#' + c + ';'];
                    }, this);
                    for (i = this.charRange[0]; i < this.charRange[1]; i++) {
                        this.specialChars.push(['&#' + i + ';']);
                    }
                }
                var charStore = new Wtf.data.SimpleStore({
                    fields: ['char'],
                    data: this.specialChars
                });
                this.charWindow = new Wtf.Window({
                    title: 'Insert Special Character',
                    iconCls:getButtonIconCls(Wtf.btype.winicon),
                    width: 436,
                    resizable: false,
                    modal: true,
                    autoHeight: true,
                    layout: 'fit',
                    items: [this.charView = new Wtf.DataView({
                        style: "background-color:white;",
                        store: charStore,
                        autoHeight: true,
                        multiSelect: true,
                        tpl: new Wtf.XTemplate('<tpl for="."><div class="char-item">{char}</div></tpl><div class="x-clear"></div>'),
                        overClass: 'char-over',
                        itemSelector: 'div.char-item',
                        listeners: {
                            dblclick: function(t, i, n, e){
                                this.insertChar(t.getStore().getAt(i).get('char'));
                                this.charWindow.close();
                            },
                            scope: this
                        }
                    })],
                    buttons: [{
                        text: 'Insert',
                        handler: function(){
                            Wtf.each(this.charView.getSelectedRecords(), function(rec){
                                var c = rec.get('char');
                                this.insertChar(c);
                            }, this);
                            this.charWindow.close();
                        },
                        scope: this
                    }, {
                        text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
                        handler: function(){
                            this.charWindow.close();
                        },
                        scope: this
                    }]
                });
                this.charWindow.show();
            },
            scope: this,
            tooltip: {
                title: 'Insert Special Character'
            },
            overflowText: 'Special Characters'
        });
    },
    insertChar: function(c){
        if (c) {
            this.cmp.insertAtCursor(c);
        }
    }
});


Wtf.ux.form.HtmlEditor.customButton = function(conf) {
    Wtf.apply(this, conf);
}

Wtf.ux.form.HtmlEditor.customButton = Wtf.extend(Wtf.ux.form.HtmlEditor.customButton, Wtf.util.Observable, {
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on("render", this.onRender, this);
    },
    onRender: function(){
        if(!this.buttonConf.scope){
            this.buttonConf.scope = this;
        }
        this.btn = this.cmp.getToolbar().addButton(this.buttonConf);
    }
});




Wtf.ux.form.HtmlEditor.Table = Wtf.extend(Wtf.util.Observable, {
    cmd: 'table',
    tableBorderOptions: [['none', 'None'], ['1px solid #000', 'Sold Thin'], ['2px solid #000', 'Solid Thick'], ['1px dashed #000', 'Dashed'], ['1px dotted #000', 'Dotted']],
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    onRender: function(){
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-table',
            handler: function(){
                if (!this.tableWindow){
                    this.tableWindow = new Wtf.Window({
                        title: 'Insert Table',
                        closeAction: 'hide',
                        items: [{
                            itemId: 'insert-table',
                            xtype: 'form',
                            border: false,
                            plain: true,
                            bodyStyle: 'padding: 10px;',
                            labelWidth: 60,
                            labelAlign: 'right',
                            items: [{
                                xtype: 'numberfield',
                                allowBlank: false,
                                allowDecimals: false,
                                fieldLabel: 'Rows',
                                name: 'row',
                                width: 60
                            }, {
                                xtype: 'numberfield',
                                allowBlank: false,
                                allowDecimals: false,
                                fieldLabel: 'Columns',
                                name: 'col',
                                width: 60
                            }, {
                                xtype: 'combo',
                                fieldLabel: 'Border',
                                name: 'border',
                                forceSelection: true,
                                mode: 'local',
                                store: new Wtf.data.ArrayStore({
                                    autoDestroy: true,
                                    fields: ['spec', 'val'],
                                    data: this.tableBorderOptions
                                }),
                                triggerAction: 'all',
                                value: 'none',
                                displayField: 'val',
                                valueField: 'spec',
                                width: 90
                            }]
                        }],
                        buttons: [{
                            text: 'Insert',
                            handler: function(){
                                var frm = this.tableWindow.getComponent('insert-table').getForm();
                                if (frm.isValid()) {
                                    var border = frm.findField('border').getValue();
                                    var rowcol = [frm.findField('row').getValue(), frm.findField('col').getValue()];
                                    if (rowcol.length == 2 && rowcol[0] > 0 && rowcol[0] < 10 && rowcol[1] > 0 && rowcol[1] < 10) {
                                        var html = "<table>";
                                        for (var row = 0; row < rowcol[0]; row++) {
                                            html += "<tr>";
                                            for (var col = 0; col < rowcol[1]; col++) {
                                                html += "<td width='20%' style='border: " + border + ";'>" + row + "-" + col + "</td>";
                                            }
                                            html += "</tr>";
                                        }
                                        html += "</table>";
                                        this.cmp.insertAtCursor(html);
                                    }
                                    this.tableWindow.hide();
                                }else{
                                    if (!frm.findField('row').isValid()){
                                        frm.findField('row').getEl().frame();
                                    }else if (!frm.findField('col').isValid()){
                                        frm.findField('col').getEl().frame();
                                    }
                                }
                            },
                            scope: this
                        }, {
                            text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
                            handler: function(){
                                this.tableWindow.hide();
                            },
                            scope: this
                        }]
                    });

                }else{
                    this.tableWindow.getEl().frame();
                }
                this.tableWindow.show();
            },
            scope: this,
            tooltip: {
                title: 'Insert Table'
            },
            overflowText: 'Table'
        });
    }
});


Wtf.ux.form.HtmlEditor.Word = Wtf.extend(Wtf.util.Observable, {
	curLength: 0,
	lastLength: 0,
	lastValue: '',
	wordPasteEnabled: true,
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
		this.cmp.on('initialize', this.onInit, this, {delay:100, single: true});
    },
	onInit: function(){
    	Wtf.EventManager.on(this.cmp.getDoc(), {
            'keyup': this.checkIfPaste,
            scope: this
        });
		this.lastValue = this.cmp.getValue();
		this.curLength = this.lastValue.length;
		this.lastLength = this.lastValue.length;
	},
	checkIfPaste: function(e){
		var diffAt = 0;
		this.curLength = this.cmp.getValue().length;
		if (e.V == e.getKey() && e.ctrlKey && this.wordPasteEnabled){
			this.cmp.suspendEvents();
			diffAt = this.findValueDiffAt(this.cmp.getValue());
			var parts = [
				this.cmp.getValue().substr(0, diffAt),
				this.fixWordPaste(this.cmp.getValue().substr(diffAt, (this.curLength - this.lastLength))),
				this.cmp.getValue().substr((this.curLength - this.lastLength)+diffAt, this.curLength)
			];
			this.cmp.setValue(parts.join(''));
			this.cmp.resumeEvents();
		}
		this.lastLength = this.cmp.getValue().length;
		this.lastValue = this.cmp.getValue();
	},
	findValueDiffAt: function(val){
		for (i=0;i<this.curLength;i++){
			if (this.lastValue[i] != val[i]){
				return i;
			}
		}
	},
    fixWordPaste: function(wordPaste) {
        var removals = [/&nbsp;/ig, /[\r\n]/g, /<(xml|style)[^>]*>.*?<\/\1>/ig, /<\/?(meta|object|span)[^>]*>/ig,
			/<\/?[A-Z0-9]*:[A-Z]*[^>]*>/ig, /(lang|class|type|href|name|title|id|clear)=\"[^\"]*\"/ig, /style=(\'\'|\"\")/ig, /<![\[-].*?-*>/g,
			/MsoNormal/g, /<\\?\?xml[^>]*>/g, /<\/?o:p[^>]*>/g, /<\/?v:[^>]*>/g, /<\/?o:[^>]*>/g, /<\/?st1:[^>]*>/g, /&nbsp;/g,
            /<\/?SPAN[^>]*>/g, /<\/?FONT[^>]*>/g, /<\/?STRONG[^>]*>/g, /<\/?H1[^>]*>/g, /<\/?H2[^>]*>/g, /<\/?H3[^>]*>/g, /<\/?H4[^>]*>/g,
            /<\/?H5[^>]*>/g, /<\/?H6[^>]*>/g, /<\/?P[^>]*><\/P>/g, /<!--(.*)-->/g, /<!--(.*)>/g, /<!(.*)-->/g, /<\\?\?xml[^>]*>/g,
            /<\/?o:p[^>]*>/g, /<\/?v:[^>]*>/g, /<\/?o:[^>]*>/g, /<\/?st1:[^>]*>/g, /style=\"[^\"]*\"/g, /style=\'[^\"]*\'/g, /lang=\"[^\"]*\"/g,
            /lang=\'[^\"]*\'/g, /class=\"[^\"]*\"/g, /class=\'[^\"]*\'/g, /type=\"[^\"]*\"/g, /type=\'[^\"]*\'/g, /href=\'#[^\"]*\'/g,
            /href=\"#[^\"]*\"/g, /name=\"[^\"]*\"/g, /name=\'[^\"]*\'/g, / clear=\"all\"/g, /id=\"[^\"]*\"/g, /title=\"[^\"]*\"/g,
            /<span[^>]*>/g, /<\/?span[^>]*>/g, /class=/g];
        Wtf.each(removals, function(s){
            wordPaste = wordPaste.replace(s, "");
        });
        wordPaste = wordPaste.replace(/<div[^>]*>/g, "<p>");
        wordPaste = wordPaste.replace(/<\/?div[^>]*>/g, "</p>");
        return wordPaste;

    },
    onRender: function() {
        this.cmp.getToolbar().add({
            iconCls: 'x-edit-wordpaste',
            pressed: true,
            handler: function(t){
                t.toggle(!t.pressed);
                this.wordPasteEnabled = !this.wordPasteEnabled;
            },
            scope: this,
            tooltip: {
                text: 'Cleanse text pasted from Word or other Rich Text applications'
            }
        });
    }
});

Wtf.ux.form.HtmlEditor.HR = Wtf.extend(Wtf.util.Observable, {
    cmd: 'hr',
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    onRender: function(){
        var cmp = this.cmp;
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-hr',
            handler: function(){
                if (!this.hrWindow){
                    this.hrWindow = new Wtf.Window({
                        width:200,
                        title: 'Insert Rule',
                        closeAction: 'hide',
                        iconCls:getButtonIconCls(Wtf.btype.winicon),
                        items: [{
                            itemId: 'insert-hr',
                            xtype: 'form',
                            border: false,
                            plain: true,
                            bodyStyle: 'padding: 10px;',
                            labelWidth: 60,
                            labelAlign: 'right',
                            items: [{
                                xtype: 'textfield',
                                maskRe: /[0-9]|%/,
                                regex: /^[1-9][0-9%]{1,3}/,
                                fieldLabel: 'Width',
                                name: 'hrwidth',
                                width: 60,
                                 listeners: {
                                    specialkey: function(f, e){
                                        if ((e.getKey() == e.ENTER || e.getKey() == e.RETURN) && f.isValid()) {
                                            this.doInsertHR();
                                        }else{
                                            f.getEl().frame();
                                        }
                                    },
                                    scope: this
                                }
                            }]
                        }],
                        buttons: [{
                            text: 'Insert',
                            handler: function(){
                                var frm = this.hrWindow.getComponent('insert-hr').getForm();
                                if (frm.isValid()){
                                    this.doInsertHR();
                                }else{
                                    frm.findField('hrwidth').getEl().frame();
                                }
    						},
                            scope: this
                        }, {
                            text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
                            handler: function(){
                                this.hrWindow.hide();
                            },
                            scope: this
                        }]
                    });
                }else{
                    this.hrWindow.getEl().frame();
                }
                this.hrWindow.show();
            },
            scope: this,
            tooltip: {
                title: 'Insert Horizontal Rule'
            },
            overflowText: 'Horizontal Rule'
        });
    },
    doInsertHR: function(){
        var frm = this.hrWindow.getComponent('insert-hr').getForm();
        if (frm.isValid()) {
            var hrwidth = frm.findField('hrwidth').getValue();
            if (hrwidth) {
                this.insertHR(hrwidth);
            } else {
                this.insertHR('100%');
            }
            frm.reset();
            this.hrWindow.hide();
        }
    },
    insertHR: function(w){
        this.cmp.insertAtCursor('<hr width="' + w + '">');
    }
});


Wtf.ux.form.HtmlEditor.insertImage = Wtf.extend(function(conf){
        Wtf.apply(this, conf);
    },Wtf.util.Observable, {
    init: function(cmp){
        this.cmp = cmp;
        this.uType = "upload";
        this.cmp.on('render', this.onRender, this);
    },
    onRender: function(){
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-image',
            handler: this.uploadImage,
            scope: this,
            tooltip: {
                title: 'Insert image'
            }
        });
    },
    uploadImage: function(){
        var imgRec = new Wtf.data.Record.create([{
            name: "id"
        },{
            name: "description"
        },{
            name: "imgname"
        },{
            name: "url"
        }]);
        this.imageStore = new Wtf.data.Store({
            url: this.imageStoreURL,
            reader: new Wtf.data.KwlJsonReader({
                root: "data"
            }, imgRec)
        });
        this.imageStore.on("load", function(obj, recs){
            var _cobj = this.imageGrid.body.dom.childNodes;
            while(_cobj.length > 0){
                Wtf.get(_cobj[0].id).remove();
            }
            for(var cnt = 0; cnt < recs.length; cnt++) {
                new Wtf.emailTemplateThumbnail({
                    height: 75,
                    width: 72,
                    imgWidth: 40,
                    imgHeight: 40,
                    tqtip: recs[cnt].data["description"],
                    tName: recs[cnt].data["imgname"],
                    thumbnail: recs[cnt].data["url"],
                    tempRec: recs[cnt],
                    scope: this,
                    listeners: {
                        "templateSelected": this.selectTemplate
                    },
                    renderTo: this.imageGrid.body.dom
                });
            }
        }, this);
        this.imageStore.load();
        this.imageGrid = new Wtf.Panel({
            autoScroll: true,
            height: 200,
            style: "margin-bottom: 10px;",
            layout: "fit"
        });
        this.uploadFieldSet = new Wtf.Panel({
            disabledClass: "fieldsetDisabled",
            cls: "uploadImagePanel",
            bodyStyle: "border: 1px solid #B5B8C8; padding: 10px 10px 6px 10px;",
            layout: "form",
            autoHeight: true,
            items: [new Wtf.Button({
                        text: "Delete File",
                        scope: this,
                        style: 'margin-bottom:10px;',
                        handler: function(){
                            if(this.selectedTemplate){
                                 var selectImg = this.selectedTemplate.tempRec.data.id;
                                 Wtf.Ajax.requestEx({
                                    url: Wtf.req.base + "getFiles.jsp?action=4&type=img",
                                    params: {
                                        tempid: selectImg
                                    }
                                }, this, function(action, response){
                                    var resultobj = eval( '(' + action.data + ')');
                                    if(resultobj.success == false){
                                        calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),"Please select image."], 2);
                                    } else {
                                        this.imageStore.reload();
                                    }
                                }, function(){
                                    ResponseAlert(["Failure","Failed to Delete file"]);
                                });
                            } else {
                                calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),"Please select image."], 2);
                            }
                        }
                    }),this.imageGrid, this.uploadForm = new Wtf.form.FormPanel({
                border: false,
                cls: "upploadForm",
                fileUpload: true,
                layout: "column",
                url: this.imageUploadURL,
                labelWidth: 50,
                items: [{
                    columnWidth: 0.83,
                    border: false,
                    layout: "form",
                    items: [{
                        xtype: "textfield",
                        width: 180,
                        allowBlank: false,
                        id: "imageField",
                        fieldLabel: "Image",
                        inputType: "file",
                        validator: WtfGlobal.validateImageFile,
                        invalidText:'File format must be .jpg/.jpeg/.gif/.bmp/.png etc.'
                    }]
                },{
                    columnWidth: 0.17,
                    border: false,
                    items: [new Wtf.Button({
                        text: "Upload",
                        scope: this,
                        handler: function(){
                            if(!Wtf.getCmp("imageField").isValid()){
                                calMsgBoxShow([ WtfGlobal.getLocaleText("hrms.common.warning"),"Please upload valid image file."], 2);
                                return;
                            }
                            this.uploadForm.form.submit({
                                scope: this,
                                success: function(action, response){
                                    this.imageStore.reload();
                                    Wtf.getCmp("imageField").reset();
                                },
                                failure: function(action, response){
                                    ResponseAlert(["Failure","Failed to upload file"]);
                                }
                            });
                        }
                    })]
                }]
            })]
        });
        this.newImage = new Wtf.Panel({
            border: false,
            items: [{
                xtype: "fieldset",
                layout: "column",
                autoHeight: true,
                items: [new Wtf.Panel({
                    columnWidth: 0.5,
                    layout: "form",
                    labelWidth: 110,
                    border: false,
                    items: [{
                        xtype: "radio",
                        scope: this,
                        uType: "upload",
                        listeners: {
                            "check": this.radioChanged
                        },
                        checked: true,
                        id: "uploadlocal",
                        cls: "uploadTypeRadio",
                        name: "uploadtype",
                        fieldLabel: "Upload new Image"
                    }]
                }),new Wtf.Panel({
                    columnWidth: 0.45,
                    layout: "form",
                    labelWidth: 90,
                    border: false,
                    items: [{
                        xtype: "radio",
                        uType: "url",
                        scope: this,
                        listeners: {
                            "check": this.radioChanged
                        },
                        cls: "uploadTypeRadio",
                        id: "uploadremote",
                        name: "uploadtype",
                        fieldLabel: "Use Web URL"
                    }]
                })]
            },this.uploadFieldSet, this.urlFieldSet = new Wtf.Panel({
                cls: "uploadImagePanel",
                bodyStyle: "border: 1px solid #B5B8C8; padding: 10px 10px 6px 10px;",
                layout: "form",
                labelWidth: 70,
                disabledClass: "fieldsetDisabled",
                autoHeight: true,
                items: [this.urlField = new Wtf.form.TextField({
                    width: 280,
                    fieldLabel: "Image URL"
                })]
            })]
        });
        this.uploadImg = new Wtf.Window({
            title: "Upload Image",
            bodyStyle: "background-color:#FFFFFF; padding:12px;",
            iconCls:getButtonIconCls(Wtf.btype.winicon),
            modal: true,
            resizable: false,
            height: 478,
            width: 450,
            items: this.newImage,
            buttons: [{
                text: "OK",
                scope: this,
                handler: this.uploadNewImage
            },{
                text:  WtfGlobal.getLocaleText("hrms.common.cancel"),
                scope: this,
                handler: function(){
                    this.uploadImg.close();
                }
            }]
        });
        this.selectedTemplate = null;
        this.uploadImg.show();
    },
    selectTemplate: function(tempObj) {
        if(this.scope.selectedTemplate) {
            this.scope.selectedTemplate.deselectTemplate();
        }
        tempObj.selectTemplate();
        this.scope.selectedTemplate = tempObj;
    },
    radioChanged: function(obj, val){
        if(val) {
            if(Wtf.getCmp("uploadremote").rendered && Wtf.getCmp("uploadlocal").rendered){
                Wtf.getCmp("uploadremote").onClick();
                Wtf.getCmp("uploadlocal").onClick();
            }
            if(obj.uType == "upload") {
                this.scope.uType = "upload";
                this.scope.uploadFieldSet.setVisible(true);
                this.scope.urlFieldSet.setVisible(false);
            } else {
                this.scope.uType = "url";
                this.scope.uploadFieldSet.setVisible(false);
                this.scope.urlFieldSet.setVisible(true);
            }
        }
    },
    uploadNewImage: function(){
        var closeFlg = false;
        if(this.uType == "upload") {
            if(this.selectedTemplate){
                this.insertImage(this.selectedTemplate.thumbnail);
                closeFlg = true;
            }
        } else if(this.urlField.getValue().trim() != "") {
            this.insertImage(this.urlField.getValue().trim());
            closeFlg = true;
        }
        if(closeFlg)
            this.uploadImg.close();
    },

    insertImage: function(c){
        if(c) {
            this.cmp.insertAtCursor("<img src='" + c +"' />");
        }
    }
});
