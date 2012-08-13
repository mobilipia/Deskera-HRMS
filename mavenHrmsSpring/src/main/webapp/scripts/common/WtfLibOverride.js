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

/*to seperate combined response and call respective callbacks*/
Wtf.override(Wtf.Element,{
    getHeight:function(contentHeight){
        try{
            var me = this;
            var dom = me.dom;
            var display =  me.getStyle("display");
            var hidden = Wtf.isIE && (display=="none");
            var h = Math.max(dom.offsetHeight, hidden ? 0 : dom.clientHeight) || 0;
            h = !contentHeight ? h : h - me.getBorderWidth("tb") - me.getPadding("tb");
            return h < 0 ? 0 : h;
        }catch(e){
            clog(e)
        }
    },
    getWidth:function(contentWidth){
        try{
            var me = this;
            var dom = me.dom;
            var display =  me.getStyle("display");
            var hidden = Wtf.isIE && (display=="none");
            var w = Math.max(dom.offsetWidth, hidden ? 0 : dom.clientWidth) || 0;
            w = !contentWidth ? w : w - me.getBorderWidth("tb") - me.getPadding("tb");
            return w < 0 ? 0 : w;
        }catch(e){
            clog(e)
        }
    }
})
Wtf.override(Wtf.grid.RowExpander,{
    getBodyContent : function(record, index){
        var content = this.tpl.apply(record.data);
        this.bodyContent[record.id] = content;
        return content;
    }
});

Wtf.override(Wtf.data.Connection,{
    handleResponse : function(response){
        try{
            this.transId = false;
            var json = response.responseText.trim();
            var obj = eval("("+json+")")
            var options;
            if(obj.grouper!=null&&obj.grouper!=undefined){
                for(var ctr=0;ctr<obj.data.length;ctr++){
                    response.responseText= Wtf.encode(obj.data[ctr].data);
                    var callobj =  callbackmap[obj.data[ctr].no];
                    delete callbackmap[obj.data[ctr].no];
                    options = callobj.argument.options;
                    this.fireEvent("requestcomplete", this, response, options);
                    Wtf.callback(options.success, options.scope, [response, options]);
                    Wtf.callback(options.callback, options.scope, [options, true, response]);
                }

            }else{
                options = response.argument.options;
                response.argument = options ? options.argument : null;
                this.fireEvent("requestcomplete", this, response, options);
                Wtf.callback(options.success, options.scope, [response, options]);
                Wtf.callback(options.callback, options.scope, [options, true, response]);
            }
        }catch(e){
                options = response.argument.options;
                response.argument = options ? options.argument : null;
                this.fireEvent("requestcomplete", this, response, options);
                Wtf.callback(options.success, options.scope, [response, options]);
                Wtf.callback(options.callback, options.scope, [options, true, response]);
        }

    }

})

/*try catch for onload function of store*/

Wtf.data.Store.prototype.oldhandler = Wtf.data.Store.prototype.loadRecords;
Wtf.override(Wtf.data.Store,{
    loadRecords:function(o, options, success){
        try{
            this.oldhandler(o, options, success)
        }catch(e){
            clog(e)
        }
    }
})
/*
 *  To Fix Bugs related to Chrome browser
 *  1) Link Button in htmleditor
 *  2) Select Font in htmlEditor
 *  3) Font increase and decrease
 *
 *  IE8 Bug for paging toolbar button (Disabled and enabled class).
 *
 */

Wtf.override(Wtf.form.Field, {
    initEvents : function(){
        this.el.on(Wtf.isIE || Wtf.isSafari || Wtf.isChrome ? "keydown" : "keypress", this.fireKey,  this);
        this.el.on("focus", this.onFocus,  this);
        this.el.on("blur", this.onBlur,  this);
        this.originalValue = this.getValue();
//        var o = this.inEditor && Wtf.isWindows && Wtf.isGecko ? {buffer:10} : null;
//        this.el.on("blur", this.onBlur,  this, {buffer:10});

    }
});
function checkUA(pattern){
    ua = navigator.userAgent.toLowerCase();
    return pattern.test(ua);
}
    Wtf.isOpera = checkUA(/opera/),
    Wtf.isChrome = checkUA(/chrome/),
    Wtf.isWebKit = checkUA(/webkit/),
    Wtf.isSafari = !Wtf.isChrome && checkUA(/safari/),
    Wtf.isSafari2 =  Wtf.isSafari && checkUA(/applewebkit\/4/), // unique to Safari 2
    Wtf.isSafari3 =  Wtf.isSafari && checkUA(/version\/3/),
    Wtf.isSafari4 =  Wtf.isSafari && checkUA(/version\/4/),
    Wtf.isIE = !Wtf.isOpera && checkUA(/msie/),
    Wtf.isIE7 =  Wtf.isIE && checkUA(/msie 7/),
    Wtf.isIE8 =  Wtf.isIE && checkUA(/msie 8/),
    Wtf.isIE6 =  Wtf.isIE && !Wtf.isIE7 && !Wtf.isIE8,
    Wtf.isGecko = !Wtf.isWebKit && checkUA(/gecko/),
    Wtf.isGecko2 =  Wtf.isGecko && checkUA(/rv:1\.8/),
    Wtf.isGecko3 =  Wtf.isGecko && checkUA(/rv:1\.9/),
    Wtf.isBorderBox =  Wtf.isIE && !Wtf.isStrict,
    Wtf.isWindows = checkUA(/windows|win32/),
    Wtf.isMac = checkUA(/macintosh|mac os x/),
    Wtf.isAir = checkUA(/adobeair/),
    Wtf.isLinux = checkUA(/linux/),
    //Override onDisable and onEnable function to fix bug for paging toolbar button in IE8
    Wtf.override(Wtf.Button, {
        onDisable : function(){
            if(this.el){
                if(!Wtf.isIE6 || !this.text){
                    this.el.addClass("x-item-disabled");
                }
                this.el.dom.disabled = true;
            }
            this.disabled = true;
        },
        onEnable : function(){
            if(this.el){
                if(!Wtf.isIE6 || !this.text){
                    this.el.removeClass("x-item-disabled");
                }
                this.el.dom.disabled = false;
            }
            this.disabled = false;
        }
    });

function recycleCursor(){
    var fakeinput = Wtf.get("cursor_bin");
    fakeinput.show();
    fakeinput.focus();
    fakeinput.hide();
}

if(Wtf.isIE7){
    Wtf.TabPanel.prototype.oldSetActiveTab = Wtf.TabPanel.prototype.setActiveTab;
    Wtf.override(Wtf.TabPanel,{
        setActiveTab:function(item){
        item = this.getComponent(item);
        if(this.activeTab){
            if(this.activeTab.findByType(Wtf.newHTMLEditor).length==1 || this.activeTab.findByType(Wtf.form.HtmlEditor).length==1){
                recycleCursor();

            }
        }
             this.oldSetActiveTab(item);
    }
    })
}
Wtf.override(Wtf.form.HtmlEditor,{
    createToolbar:function(editor){
 var tipsEnabled = Wtf.QuickTips && Wtf.QuickTips.isEnabled();

    
        function btn(id, toggle, handler){
            return {
                itemId : id,
                cls : 'x-btn-icon x-edit-'+id,
                enableToggle:toggle !== false,
                scope: editor,
                handler:handler||editor.relayBtnCmd,
                clickEvent:'mousedown',
                tooltip: tipsEnabled ? editor.buttonTips[id] || undefined : undefined,
                overflowText: editor.buttonTips[id].title || undefined,
                tabIndex:-1
            };
        }

        // build the toolbar
        var tb = new Wtf.Toolbar({
            renderTo:this.wrap.dom.firstChild
        });

        // stop form submits
        tb.on('click', function(e){
            e.preventDefault();
        });

        if(this.enableFont && !Wtf.isSafari2){
            this.fontSelect = tb.el.createChild({
                tag:'select',
                cls:'x-font-select',
                html: this.createFontOptions()
            });
            this.fontSelect.on('change', function(){
                var font = this.fontSelect.dom.value;
                this.relayCmd('fontname', font);
                this.deferFocus();
            }, this);

            tb.add(
                this.fontSelect.dom,
                '-'
            );
        }

        if(this.enableFormat){
            tb.add(
                btn('bold'),
                btn('italic'),
                btn('underline')
            );
        }

        if(this.enableFontSize){
            tb.add(
                '-',
                btn('increasefontsize', false, this.adjustFont),
                btn('decreasefontsize', false, this.adjustFont)
            );
        }

        if(this.enableColors){
            tb.add(
                '-', {
                    itemId:'forecolor',
                    cls:'x-btn-icon x-edit-forecolor',
                    clickEvent:'mousedown',
                    tooltip: tipsEnabled ? editor.buttonTips.forecolor || undefined : undefined,
                    tabIndex:-1,
                    menu : new Wtf.menu.ColorMenu({
                        allowReselect: true,
                        focus: Wtf.emptyFn,
                        value:'000000',
                        plain:true,
                        listeners: {
                            scope: this,
                            select: function(cp, color){
                                this.execCmd('forecolor', Wtf.isWebKit || Wtf.isIE ? '#'+color : color);
                                this.deferFocus();
                            }
                        },
                        clickEvent:'mousedown'
                    })
                }, {
                    itemId:'backcolor',
                    cls:'x-btn-icon x-edit-backcolor',
                    clickEvent:'mousedown',
                    tooltip: tipsEnabled ? editor.buttonTips.backcolor || undefined : undefined,
                    tabIndex:-1,
                    menu : new Wtf.menu.ColorMenu({
                        focus: Wtf.emptyFn,
                        value:'FFFFFF',
                        plain:true,
                        allowReselect: true,
                        listeners: {
                            scope: this,
                            select: function(cp, color){
                                if(Wtf.isGecko){
                                    this.execCmd('useCSS', false);
                                    this.execCmd('hilitecolor', color);
                                    this.execCmd('useCSS', true);
                                    this.deferFocus();
                                }else{
                                    this.execCmd(Wtf.isOpera ? 'hilitecolor' : 'backcolor', Wtf.isWebKit || Wtf.isIE ? '#'+color : color);
                                    this.deferFocus();
                                }
                            }
                        },
                        clickEvent:'mousedown'
                    })
                }
            );
        }

        if(this.enableAlignments){
            tb.add(
                '-',
                btn('justifyleft'),
                btn('justifycenter'),
                btn('justifyright')
            );
        }

        if(!Wtf.isSafari2){
            if(this.enableLinks){
                tb.add(
                    '-',
                    btn('createlink', false, this.createLink)
                );
            }

            if(this.enableLists){
                tb.add(
                    '-',
                    btn('insertorderedlist'),
                    btn('insertunorderedlist')
                );
            }
            if(this.enableSourceEdit){
                tb.add(
                    '-',
                    btn('sourceedit', true, function(btn){
                        this.toggleSourceEdit(!this.sourceEditMode);
                    })
                );
            }
        }

        this.tb = tb;
},
    insertAtCursor:function(B){
        if(!this.activated){
            return
        }if(Wtf.isIE){
            this.win.focus();var A=this.doc.selection.createRange();if(A){
                A.collapse(true);A.pasteHTML(B);this.syncValue();this.deferFocus()
                }
        }else{
            if(Wtf.isGecko||Wtf.isOpera){
                this.win.focus();this.execCmd("InsertHTML",B);this.deferFocus()
            }else{
                if(Wtf.isSafari || Wtf.isChrome){
                    this.execCmd("InsertHTML",B);this.deferFocus()
                }
            }
        }
    },
    getDoc : function(){
        return Wtf.isIE ? this.getWin().document : (this.iframe.contentDocument || this.getWin().document);
    },


    getWin : function(){
        return Wtf.isIE ? this.iframe.contentWindow : window.frames[this.iframe.name];
    },
    adjustFont: function(btn){
        var adjust = btn.getItemId() == 'increasefontsize' ? 1 : -1,
        doc = this.getDoc(),
        v = parseInt(doc.queryCommandValue('FontSize') || 2, 10);
        if((Wtf.isSafari && !Wtf.isSafari2) || Wtf.isChrome ){


            if(v <= 10){
                v = 1 + adjust;
            }else if(v <= 13){
                v = 2 + adjust;
            }else if(v <= 16){
                v = 3 + adjust;
            }else if(v <= 18){
                v = 4 + adjust;
            }else if(v <= 24){
                v = 5 + adjust;
            }else {
                v = 6 + adjust;
            }
            v = v.constrain(1, 6);
        }else{
            if(Wtf.isSafari){
                adjust *= 2;
            }
            v = Math.max(1, v+adjust) + (Wtf.isSafari ? 'px' : 0);
        }
        this.execCmd('FontSize', v);

    },
    fixKeys : function(){
        if(Wtf.isIE){
            return function(e){
                var k = e.getKey(), r;
                if(k == e.TAB){
                    e.stopEvent();
                    r = this.doc.selection.createRange();
                    if(r){
                        r.collapse(true);
                        r.pasteHTML('&nbsp;&nbsp;&nbsp;&nbsp;');
                        this.deferFocus();
                    }
                }else if(k == e.ENTER){
                    r = this.doc.selection.createRange();
                    if(r){
                        var target = r.parentElement();
                        if(!target || target.tagName.toLowerCase() != 'li'){
                            e.stopEvent();
                            r.pasteHTML('<br />');
                            r.collapse(false);
                            r.select();
                        }
                    }
                }
            };
        }else if(Wtf.isOpera){
            return function(e){
                var k = e.getKey();
                if(k == e.TAB){
                    e.stopEvent();
                    this.win.focus();
                    this.execCmd('InsertHTML','&nbsp;&nbsp;&nbsp;&nbsp;');
                    this.deferFocus();
                }
            };
        }else if(Wtf.isWebKit){
            return function(e){
                var k = e.getKey();
                if(k == e.TAB){
                    e.stopEvent();
                    this.execCmd('InsertText','\t');
                    this.deferFocus();
                }else if(k == e.ENTER){
                    e.stopEvent();
                    this.execCmd('InsertHtml','<br /><br />');
                    this.deferFocus();
                }
            };
        }
    }
});


//IE 9 Wtf.qtip dashboard tooltips not displaying problem and tree structure problem solved.

//if(Wtf.IE9){
  Wtf.override(Wtf.Element, {
    
      getAttributeNS : function(ns, name){
          return this.getAttribute(name, ns);
      },
    
      getAttribute: (function(){
          var test = document.createElement('table'),
              isBrokenOnTable = false,
              hasGetAttribute = 'getAttribute' in test,
              unknownRe = /undefined|unknown/;
            
          if (hasGetAttribute) {
            
              try {
                  test.getAttribute('ext:qtip');
              } catch (e) {
                  isBrokenOnTable = true;
              }
            
              return function(name, ns) {
                  var el = this.dom,
                      value;
                
                  if (el.getAttributeNS) {
                      value  = el.getAttributeNS(ns, name) || null;
                  }
            
                  if (value == null) {
                      if (ns) {
                          if (isBrokenOnTable && el.tagName.toUpperCase() == 'TABLE') {
                              try {
                                  value = el.getAttribute(ns + ':' + name);
                              } catch (e) {
                                  value = '';
                              }
                          } else {
                              value = el.getAttribute(ns + ':' + name);
                          }
                      } else {
                          value = el.getAttribute(name) || el[name];
                      }
                  }
                  return value || '';
              };
          } else {
              return function(name, ns) {
                  var el = this.om,
                      value,
                      attribute;
                
                  if (ns) {
                      attribute = el[ns + ':' + name];
                      value = unknownRe.test(typeof attribute) ? undefined : attribute;
                  } else {
                      value = el[name];
                  }
                  return value || '';
              };
          }
          test = null;
      })()
  });
//}
