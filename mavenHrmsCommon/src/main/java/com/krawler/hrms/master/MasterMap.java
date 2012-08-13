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

package com.krawler.hrms.master;

/**
 *
 * @author krawler
 */
public class MasterMap {
    private String id;
    private String masterdataid1;
    private String masterdataid2;
    private MasterData MasterDataId1;
    private MasterData MasterDataId2;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the masterdataid1
     */
    public String getMasterdataid1() {
        return masterdataid1;
    }

    /**
     * @param masterdataid1 the masterdataid1 to set
     */
    public void setMasterdataid1(String masterdataid1) {
        this.masterdataid1 = masterdataid1;
    }

    /**
     * @return the masterdataid2
     */
    public String getMasterdataid2() {
        return masterdataid2;
    }

    /**
     * @param masterdataid2 the masterdataid2 to set
     */
    public void setMasterdataid2(String masterdataid2) {
        this.masterdataid2 = masterdataid2;
    }

    /**
     * @return the MasterDataId1
     */
    public MasterData getMasterDataId1() {
        return MasterDataId1;
    }

    /**
     * @param MasterDataId1 the MasterDataId1 to set
     */
    public void setMasterDataId1(MasterData MasterDataId1) {
        this.MasterDataId1 = MasterDataId1;
    }

    /**
     * @return the MasterDataId2
     */
    public MasterData getMasterDataId2() {
        return MasterDataId2;
    }

    /**
     * @param MasterDataId2 the MasterDataId2 to set
     */
    public void setMasterDataId2(MasterData MasterDataId2) {
        this.MasterDataId2 = MasterDataId2;
    }

}
