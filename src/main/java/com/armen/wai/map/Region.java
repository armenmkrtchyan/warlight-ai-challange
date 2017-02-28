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

import com.armen.wai.util.helper.OwnerType;

public class Region {
	
	private final Integer id;
	private final Integer superRegionId;
    private Integer deployedArmies;
    private OwnerType owner;

    public Region(Integer id, Integer superRegionId) {
		this.id = id;
		this.superRegionId = superRegionId;
        this.owner = OwnerType.Unknown;
        this.deployedArmies = 2;
    }

    public Integer getDeployedArmies() {
        return deployedArmies;
    }

    public OwnerType getOwner() {
        return owner;
    }

    public void setDeployedArmies(Integer deployedArmies) {
        this.deployedArmies = deployedArmies;
    }

    public void setOwner(OwnerType owner) {
        this.owner = owner;
    }

    public Integer getId() {
        return id;
    }

    public Integer getSuperRegionId() {
        return superRegionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Region region = (Region) o;

        return id != null ? id.equals(region.id) : region.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Region{" +
                "id=" + id +
                ", superRegionId=" + superRegionId +
                ", deployedArmies=" + deployedArmies +
                ", owner=" + owner +
                '}';
    }
}
