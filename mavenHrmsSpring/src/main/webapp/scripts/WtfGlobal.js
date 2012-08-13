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
WtfGlobal = {
	getCookie: function(c_name){
		if (document.cookie.length > 0) {
			c_start = document.cookie.indexOf(c_name + "=");
			if (c_start != -1) {
				c_start = c_start + c_name.length + 1;
				c_end = document.cookie.indexOf(";", c_start);
				if (c_end == -1)
					c_end = document.cookie.length;
				return unescape(document.cookie.substring(c_start, c_end));
			}
		}
		return "";
	},
	nameRenderer: function(value){
		var resultval = value.substr(0, 1);
		var patt1 = new RegExp("^[a-zA-Z]");
		if (patt1.test(resultval)) {
			return resultval.toUpperCase();
		}
		else
			return "Others";
	},
        
	sizeRenderer: function(value){
		var sizeinKB = value
		if (sizeinKB >= 1 && sizeinKB < 1024) {
			text = "Small";
		} else if (sizeinKB > 1024 && sizeinKB < 102400) {
			text = "Medium";
		} else if (sizeinKB > 102400 && sizeinKB < 1048576) {
			text = "Large";
		} else {
			text = "Gigantic";
		}
		return text;
	},
    replaceAll : function(txt, replace, with_this) {
        return txt.replace(new RegExp(replace, 'g'),with_this);
    },
	dateFieldRenderer: function(value){
		var text = "";
		if (value) {
			var dt = new Date();
			if ((value.getMonth() == dt.getMonth()) && (value.getYear() == dt.getYear())) {
				if (dt.getDate() == value.getSeperatorPos()) {
					text = "Today";
				} else if (value.getDate() == (dt.getDate() - 1))
					text = "Yesterday";
				else if (value.getDate() <= (dt.getDate() - 7) && value.getDate() > (dt.getDate() - 14))
					text = "Last Week";
			} else if ((value.getMonth() == (dt.getMonth() - 1)) && (value.getYear() == dt.getYear()))
				text = "Last Month";
			else if ((value.getYear() == (dt.getYear() - 1)))
				text = "Last Year";
			else
				text = "Older";
		} else
			text = "None";
		return text;
	},
        commentRenderer:function(value){
            var valTip = WtfGlobal.replaceAll(value, '"', "&#34;");
            return '<div wtf:qtip=\"'+valTip+'\">'+value+'</div>';
        },
        addLabelHelp:function(HelpText){
            return "<span wtf:qtip=\""+HelpText+"\" class=\"formHelpButton\">&nbsp;&nbsp;&nbsp;&nbsp;</span>";
        },
        addCommentIcon:function(HelpText){
            return "<span wtf:qtip=\""+HelpText+"\" class=\"gridCommentButton\">&nbsp;&nbsp;&nbsp;&nbsp;</span>";
        },

	permissionRenderer: function(value, rec){
		var text = value.toLowerCase();
		switch (text) {
			case "everyone":
				text = "Everyone on deskEra";
				break;
			case "connections":
				text = "All Connections";
				break;
			case "none":
				text = "Private";
				break;
			default:
				text = "Selected Connections";
				break;
		}
		return text;
	},

	isSuperUser:function(){
		return (WtfGlobal.getCookie("superuser")=="true");
	},


	HTMLStripper: function(val){
		var str = Wtf.util.Format.stripTags(val);
		return str.replace(/"/g, '').trim();
	},

	ScriptStripper: function(str){
		str = Wtf.util.Format.stripScripts(str);
		if (str)
			return str.replace(/"/g, '');
		else
			return str;
	},

	URLDecode: function(str){
		str=str.replace(new RegExp('\\+','g'),' ');
		return unescape(str);
	},

	getDateFormat: function() {
		return Wtf.pref.DateFormat;
	},

    getSeperatorPos: function() {
        return Wtf.pref.seperatorpos;
    },

    getOnlyDateFormat: function() {
        var pos=WtfGlobal.getSeperatorPos();
        var fmt=WtfGlobal.getDateFormat();
        if(pos<=0)
            return "Y-m-d";
        return fmt.substring(0,pos);
    },

    getOnlyTimeFormat: function() {
        var pos=WtfGlobal.getSeperatorPos();
        var fmt=WtfGlobal.getDateFormat();
        if(pos>=fmt.length)
            return "H:i:s";
        return fmt.substring(pos);
    },


    getonlyDateFormat: function() {
		return "l, F d, Y";
	},

	dateRenderer: function(v) {
		if(!v) return v;
		return '<div align="center">'+v.format(WtfGlobal.getDateFormat())+'</div>';
	},

    dateonlyRenderer: function(v) {
		if(!v) return v;
		return v.format(WtfGlobal.getonlyDateFormat());
	},

    onlyTimeRenderer: function(v) {
        if(!v) return v;
        return v.format(WtfGlobal.getOnlyTimeFormat());
    },

    onlyDateRenderer: function(v) {
        if(!v) return v;
        return "<div align=center>"+v.format(WtfGlobal.getOnlyDateFormat())+"</div>";
    }, 
    onlySinglequoateRenderer: function(v) {
        if(!v) return v;
        return v.replace(/'/g,'\\\'');
    },  

    linkRenderer: function(value) {
        return "<a class='jumplink' href='#'>"+value+"</a>";
    },

    enableDisableBtnArr:function(btnArr,grid,singleSelectArr,multiSelectArr){
        var multi = !grid.getSelectionModel().hasSelection();
        var single = (grid.getSelectionModel().getCount()!=1);
        for(var i=0;i<multiSelectArr.length;i++)
            btnArr[multiSelectArr[i]].setDisabled(multi);
        for(i=0;i<singleSelectArr.length;i++)
            btnArr[singleSelectArr[i]].setDisabled(single);
    },

	convertToGenericDate:function(value){
		if(!value) return value;
		return value.format("M d, Y h:i:s A");
	},
        getLongForLocale:function(value){
            if(!value) return value;
		return value.getTime();
        },

	getTimeZone: function() {
		return Wtf.pref.Timezone;
	},

	getCurrencyName: function() {
		return Wtf.pref.CurrencyName;
	},

	getCurrencySymbol: function() {
		return Wtf.pref.CurrencySymbol;
	},
	
    showmasterWindow:function(confid,combostore,action){
      new Wtf.AddEditMasterData({
            layout:"fit",
            title: WtfGlobal.getLocaleText("hrms.common.AddSubField"),
            modal:true,
            configid:confid,
            width:400,
            height:230,
            iconCls:'WinIcon',
            action:action,
            store:combostore
        }).show();
    },
    getXtype : function(flag){
      switch(flag)  {
          case 4:
              return "numberfield";
              break;
          default :
              return "textfield";
      }
    },
    closeProgressbar:function(){
            Wtf.MessageBox.hide();
    },

	currencyRenderer: function(value) {
		var v=parseFloat(value);
		if(isNaN(v)) return "";
		v = (Math.round((v-0)*100))/100;
		v = (v == Math.floor(v)) ? v + ".00" : ((v*10 == Math.floor(v*10)) ? v + "0" : v);
		v = String(v);
		var ps = v.split('.');
		var whole = ps[0];
		var sub = ps[1] ? '.'+ ps[1] : '.00';
		var r = /(\d+)(\d{3})/;
		while (r.test(whole)) {
			whole = whole.replace(r, '$1' + ',' + '$2');
		}
		v = whole + sub;
		if(v.charAt(0) == '-'){
			v= '-'+ WtfGlobal.getCurrencySymbol() +" "+ v.substr(1);
		}else{
			v= WtfGlobal.getCurrencySymbol()+" "+ v;
		}
		return '<div class="currency">'+v+'</div>';
	},
    currencyRendererEPF: function(value) {
		var v=WtfGlobal.currencyRendererEPFOnly(value);
//		if(v.charAt(0) == '-'){
//			v= '-'+ WtfGlobal.getCurrencySymbol() +" "+ v.substr(1);
//		}else{
//			v= WtfGlobal.getCurrencySymbol()+" "+ v;
//		}
		return '<div class="currency"><b>Total: </b>'+v+'</div>';
	},
    currencyRendererEPFOnly: function(value) {
		var v=parseFloat(value);
		if(isNaN(v)) return "";
		v = (Math.round((v-0)*100))/100;
		v = (v == Math.floor(v)) ? v + ".00" : ((v*10 == Math.floor(v*10)) ? v + "0" : v);
		v = String(v);
		var ps = v.split('.');
		var whole = ps[0];
		var sub = ps[1] ? '.'+ ps[1] : '.00';
		var r = /(\d+)(\d{3})/;
		while (r.test(whole)) {
			whole = whole.replace(r, '$1' + ',' + '$2');
		}
        v = whole + sub;
		return v;
	},
	payrollcurrencyRenderer: function(value) {
		var v=parseFloat(value);
		if(isNaN(v)) return "";
		v = (Math.round((v-0)*100))/100;
		v = (v == Math.floor(v)) ? v + ".00" : ((v*10 == Math.floor(v*10)) ? v + "0" : v);
		v = String(v);
		var ps = v.split('.');
		var whole = ps[0];
		var sub = ps[1] ? '.'+ ps[1] : '.00';
		var r = /(\d+)(\d{3})/;
		while (r.test(whole)) {
			whole = whole.replace(r, '$1' + ',' + '$2');
		}
		v = whole + sub;
		if(v.charAt(0) == '-'){
			v= '-'+ WtfGlobal.getCurrencySymbol() +" "+ v.substr(1);
		}else{
			v= WtfGlobal.getCurrencySymbol()+" "+ v;
		}
		return '<div class="payrollcurrency">'+v+'</div>';
	},
    currencyRenderer2: function(value) {
		var v=parseFloat(value);
		if(isNaN(v)) return "";
		v = (Math.round((v-0)*100))/100;
		v = (v == Math.floor(v)) ? v + ".00" : ((v*10 == Math.floor(v*10)) ? v + "0" : v);
		v = String(v);
		var ps = v.split('.');
		var whole = ps[0];
		var sub = ps[1] ? '.'+ ps[1] : '.00';
		var r = /(\d+)(\d{3})/;
		while (r.test(whole)) {
			whole = whole.replace(r, '$1' + ',' + '$2');
		}
		v = whole + sub;
		if(v.charAt(0) == '-'){
			v= '-'+v.substr(1);
		}else{
			v= v;
		}
		return v;
	},
   percentageRegex:function(){
       var regex=/^(100(?:\.0{1,2})?|0*?\.\d{1,2}|\d{1,2}(?:\.\d{1,2})?)$/;
       return regex;
   }, 
  currencySummaryRenderer: function(value) {
        return '<div align="right" style="font-family:Lucida Sans Unicode;"><font size=2 color="red">'+WtfGlobal.currencyRenderer(value)+'<font></div>';
    },

    totalSummaryRenderer: function(value) {
        return '<div align="left"><b>'+WtfGlobal.getLocaleText("hrms.timesheet.TotalForDay")+'</b></div>';
    },
    total: function(value) {
        return '<div align="left"><b>'+WtfGlobal.getLocaleText("hrms.timesheet.total")+'</b></div>';
    },
    timeSummaryRenderer: function(value) {
        if(value<=1){
        return '<div align="center">'+value+'00:00 hrs</div>';
        }else{
            return '<div align="center">'+value+' hrs</div>';
        }
    },


	 validateEmail: function(value){
		return Wtf.ValidateMailPatt.test(value);
	},

	renderEmailTo: function(value,p,record){
		return "<div class='mailTo'><a href=mailto:"+value+">"+value+"</a></div>";
	},

	 validateHTField:function(value){
      return Wtf.validateHeadTitle.test(value.trim());
    },
    validateImageFile:function(value){
        return Wtf.validateImageFile.test(value.trim());
    },

	renderContactToSkype: function(value,p,record){
		return "<div class='mailTo'><a href=skype:"+value+"?call>"+value+"</a></div>";
	},

	validateUserid: function(value){
		return Wtf.ValidateUserid.test(value);
	},

	validateUserName: function(value){
		return Wtf.ValidateUserName.test(value.trim());
	},

        validatePhoneNum: function(value){
		return Wtf.ValidatePhoneNo.test(value.trim());
	},

	getInstrMsg: function(msg){
		return "<span style='font-size:10px !important;color:gray !important;'>"+msg+"</span>"
	},
    numericRenderer:function(v){
        return '<div class="currency">'+v+'</div>';
    },
     emptyGridRenderer: function(value) {
        return "<div class='grid-link-text'>"+value+"</div>";
    },
    numericPrecisionRenderer:function(v){
         if(v>0){
                        return'<div class="currency">'+(parseFloat(v).toFixed(2))+'</div>';
                    }else{
                        return'<div class="currency">'+0+'</div>';
                    }
    },
    percentageRenderer:function(v){
         if(v>0){
                        return'<div class="currency">'+(parseFloat(v).toFixed(2))+'%</div>';
                    }else{
                        return'<div class="currency">'+0+'%</div>';
                    }
    },
	chkFirstRun: function(){
		return WtfGlobal.getCookie("lastlogin") == "1990-01-01 00:00:00.0";
	},
    highLightRow: function(EditorGrid,color,duration,row) {
      var rowEl = EditorGrid.getView().getRow(row);
        Wtf.fly(rowEl).highlight(color
            ,{attr: "background-color",
                 easing: 'easeIn',
                 duration: duration,
                 endColor: "ffffff"
        });
    },
	EnableDisable: function(userpermcode, permcode){
		if(permcode==null){
			clog("Some Permission are undefined.\n"+userpermcode+"\n"+showCallStack());
            
		}
		if (userpermcode && permcode) {
			if ((userpermcode & permcode) == permcode)
				return false;
		}
		return true;
	},
    CmpEnableDisable: function(actRec, index){
		if((Math.pow(2,parseInt(index))&actRec)==Math.pow(2,parseInt(index))){
            return true;
        }else{
            return false;
        }
	},

	loadScript: function(src){
		var scriptTag = document.createElement("script");
		scriptTag.type = "text/javascript";
		scriptTag.src = src;
		document.getElementsByTagName("head")[0].appendChild(scriptTag);
	},

	loadStyleSheet: function(ref){
		var styleTag = document.createElement("link");
		styleTag.setAttribute("rel", "stylesheet");
		styleTag.setAttribute("type", "text/css");
		styleTag.setAttribute("href", ref);
		document.getElementsByTagName("head")[0].appendChild(styleTag);
	},
    delaytasks:function(editorstore,params){
        var delayTask = new Wtf.util.DelayedTask(function(){
            if(params){
             editorstore.load({
                params:params
            });
            }else{
            editorstore.load();
            }
        },this);
        delayTask.delay(1000);
    },
    noBlankCheck:function(val){
        if(val.trim()==""){
            return "This field cannot be blank";
        } else {
            return true;
        }
    },
    validateNameFields:function(val){
        var errorcheck = WtfGlobal.noBlankCheck(val);
        if(errorcheck==true) {
            if(val.search(/^[ '_]+$/)!=-1) { // This check eliminates values containing only space,_ and ' values
                return "Invalid name";
            } else if(val.search(/^[A-Za-z '_]+$/)!=-1) {
                return true;
            } else {
                return "Field can contain only alphabets, _ and ' values";
            }
        } else {
            return errorcheck;
        }
    },
    validateDropDowns:function(val){
        var errorcheck = WtfGlobal.noBlankCheck(val);
        if(errorcheck==true) {
            if(this.getValue()==""||this.getValue()==null||this.getValue()==undefined) {
                return "Invalid value";
            } else {
                return true;
            }
        } else {
            return errorcheck;
        }
    },
    
    getLocaleText:function(key, basename, def){
        var base=window[basename||"messages"];
        var params=[].concat(key.params||[]);
        key = key.key||key;
        if(base){
            if(base[key]){
                    params.splice(0, 0, base[key]);
                    return String.format.apply(this,params);
            }else{
                    clog("Locale spacific text not found for ["+key+"]");
            }
        }else{
        	if(basename!=undefined){
        		clog("Locale spacific base ("+basename+") not available");
        	}
        }
        return def||key;
    },

    loadScript: function(src, callback, scope){
        var scriptTag = document.createElement("script");
        scriptTag.type = "text/javascript";
        if(typeof callback == "function"){
        	scriptTag.onreadystatechange= function () {
        		      if (this.readyState == 'complete') 
        		    	  callback.call(scope || this || window);
        		   }
        	scriptTag.onload= callback.createDelegate(scope || this || window);        	
        }
        scriptTag.src = src;
        document.getElementsByTagName("head")[0].appendChild(scriptTag);
    }
};


/*  WtfHTMLEditor: Start    */
Wtf.newHTMLEditor = function(config){
	Wtf.apply(this, config);
	this.createLinkText = 'Please enter the URL for the link:';
	this.defaultLinkValue = 'http:/'+'/';
	this.smileyel = null;
	this.SmileyArray = [" ", ":)", ":(", ";)", ":D", ";;)", ">:D<", ":-/", ":x", ":>>", ":P", ":-*", "=((", ":-O", "X(", ":>", "B-)", ":-S", "#:-S", ">:)", ":((", ":))", ":|", "/:)", "=))", "O:-)", ":-B", "=;", ":-c", ":)]", "~X("];
	this.tpl = new Wtf.Template('<div id="{curid}smiley{count}" style="float:left; height:20px; width:20px; background: #ffffff;padding-left:4px;padding-top:4px;"  ><img id="{curid}smiley{count}" src="{url}" style="height:16px; width:16px"></img></div>');
	this.tbutton = new Wtf.Toolbar.Button({
		minWidth: 30,
		disabled:true,
		enableToggle: true,
		iconCls: 'smiley'
	});
	this.eventSetFlag=false;
	this.tbutton.on("click", this.handleSmiley, this);
	this.smileyWindow = new Wtf.Window({
		width: 185,
		height: 116,
		minWidth: 200,
		plain: true,
		cls: 'replyWind',
		shadow: false,
		buttonAlign: 'center',
		draggable: false,
		header: false,
		closable  : true,
		closeAction : 'hide',
		resizable: false
	});
	this.smileyWindow.on("deactivate", this.closeSmileyWindow, this);
	Wtf.newHTMLEditor.superclass.constructor.call(this, {});
	this.on("render", this.addSmiley, this);
	this.on("activate", this.enableSmiley, this);
	this.on("hide", this.hideSmiley, this);
}

Wtf.extend(Wtf.newHTMLEditor, Wtf.form.HtmlEditor, {
	enableSmiley:function(){
		this.tbutton.enable();
	},
	hideSmiley: function(){
		//        alert("hide");
		if(this.smileyWindow !== undefined && this.smileyWindow.el !== undefined)
			this.smileyWindow.hide();
	},
	addSmiley: function(editorObj){
		editorObj.getToolbar().addSeparator();
		editorObj.getToolbar().addButton(this.tbutton);

	},
	createLink : function(){
		var url = prompt(this.createLinkText, this.defaultLinkValue);
		if(url && url != 'http:/'+'/'){
			var tmpStr = url.substring(0,7);
			if(tmpStr!='http:/'+'/')
				url = 'http:/'+'/'+url;
			this.win.focus();
			var selTxt = this.doc.getSelection().trim();
			selTxt = selTxt =="" ? url : selTxt;
			if(this.SmileyArray.join().indexOf(selTxt)==-1) {
				this.insertAtCursor("<a href = '"+url+"' target='_blank'>"+selTxt+" </a>");
				this.deferFocus();
			} else {
				msgBoxShow(170,1);
			}
		}
	},
	//  FIXME: ravi: When certain smilies are used in a pattern, the resultant from this function does not conform to regex used to decode smilies in messenger.js.

	writeSmiley: function(e){
		var obj=e;
		this.insertAtCursor(this.SmileyArray[obj.target.id.substring(this.id.length + 6)]+" ");
		this.smileyWindow.hide();
		this.tbutton.toggle(false);
	},

	handleSmiley: function(buttonObj, e){
		if(this.tbutton.pressed) {
			this.smileyWindow.setPosition(e.getPageX(), e.getPageY());
			this.smileyWindow.show();
			if(!this.eventSetFlag){
				for (var i = 1; i < 29; i++) {
					var divObj = {
						url: '../../images/smiley' + i + '.gif',
						count: i,
						curid: this.id
					};
					this.tpl.append(this.smileyWindow.body, divObj);
					this.smileyel = Wtf.get(this.id + "smiley" + i);
					this.smileyel.on("click", this.writeSmiley, this);
					this.eventSetFlag=true;
				}
			}
		} else {
			this.smileyWindow.hide();
			this.tbutton.toggle(false);
		}
	},

	closeSmileyWindow: function(smileyWindow){
		this.smileyWindow.hide();
		this.tbutton.toggle(false);
	}
});

// Call stack code
function showCallStack(){
	var f=showCallStack,result="Call stack:\n";

	while((f=f.caller)!==null){
		var sFunctionName = f.toString().match(/^function (\w+)\(/)
		sFunctionName = (sFunctionName) ? sFunctionName[1] : 'anonymous function';
		result += sFunctionName;
		result += getArguments(f.toString(), f.arguments);
		result += "\n";

	}
	return result;
}

function getArguments(sFunction, a) {
	var i = sFunction.indexOf(' ');
	var ii = sFunction.indexOf('(');
	var iii = sFunction.indexOf(')');
	var aArgs = sFunction.substr(ii+1, iii-ii-1).split(',')
	var sArgs = '';
	for(var i=0; i<a.length; i++) {
		var q = ('string' == typeof a[i]) ? '"' : '';
		sArgs+=((i>0) ? ', ' : '')+(typeof a[i])+' '+aArgs[i]+':'+q+a[i]+q+'';
	}
	return '('+sArgs+')';
}

function headerCheck(header) {
    var indx=header.indexOf('(');
    if(indx!=-1) {
        indx=header.indexOf("&#");
        if(indx!=-1)
            header=header.substring(0,header.indexOf('('));
    }
    return header;
}

Wtf.Button.override({
    setTooltip: function(qtipText) {
        if(this.getEl() != undefined) {
            var btnEl = this.getEl().child(this.buttonSelector)
            Wtf.QuickTips.register({
                target: btnEl.id,
                text: qtipText
            });
        }
    }
});


Wtf.form.LabelField = function(config){
    Wtf.form.LabelField.superclass.constructor.call(this, config);
};

Wtf.extend(Wtf.form.LabelField, Wtf.form.Field,  {
    defaultAutoCreate : {tag: "span"},
    labelSeparator:'',
    hideLabel:true,
    fieldClass: 'x-form-extend-label',
    value: '',
    setValue:function(val) {
	    if(this.rendered){
	         this.el.update(val);
	    }
    }
});
