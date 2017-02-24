/**
 * Warlight AI Game Bot
 *
 * Last update: January 29, 2015
 *
 * @author Jim van Eeden
 * @version 1.1
 * @License MIT License (http://opensource.org/Licenses/MIT)
 */

package com.armen.wai.map;

public class Region {
	
	private Integer id;
	private Integer superRegionId;
	
	public Region(Integer id, Integer superRegionId) {
		this.id = id;
		this.superRegionId = superRegionId;
	}


    public Integer getId() {
        return id;
    }

    public Integer getSuperRegionId() {
        return superRegionId;
    }
}
