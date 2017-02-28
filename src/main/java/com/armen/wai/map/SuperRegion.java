package com.armen.wai.map;

public class SuperRegion {
	
	private final Integer id;
	private final Integer armiesReward;

	public SuperRegion(Integer id, Integer armiesReward) {
		this.id = id;
		this.armiesReward = armiesReward;
	}

    public Integer getId() {
        return id;
    }

    public Integer getArmiesReward() {
        return armiesReward;
    }
}
