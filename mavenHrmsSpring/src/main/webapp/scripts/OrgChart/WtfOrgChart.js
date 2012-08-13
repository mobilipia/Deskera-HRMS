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
    var orgChartTab = Wtf.getCmp("tabmyorganizationpanel");
    var projid=companyid;
    orgChartTab.add(new Wtf.Panel({
        id: projid + "orgChartContainer",
        layout: "border",
        border: false,
        items: [{
                region: "center",
                layout: "fit",
                border: false,
                cls: "zoom_lvl_3",
                items: new Wtf.ChartContainer({
                            layout : "fit",
                            id : projid + "chartContainer",
                            projid: projid,
                            bodyStyle: "background-color: white; position: absolute; overflow-x: auto; overflow-y: auto;",
                            autoscroll: true
                    })
            }, {
                title: WtfGlobal.getLocaleText("hrms.common.unassigned.users"),
                region: "south",
                layout: "fit",
                height: 130,
                split : true,
                border: false,
                items: new Wtf.UnmappedContainer({
                            id: projid + "unmappedContainer",
                            layout : "fit",
                            projid: projid,
                            bodyStyle: "background-color: white; overflow-x: hidden; overflow-y: auto;"
                    })
            }
        ]
    }));

    orgChartTab.doLayout();
