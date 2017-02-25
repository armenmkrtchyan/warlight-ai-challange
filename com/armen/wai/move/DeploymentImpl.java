package com.armen.wai.move;

import com.armen.wai.map.Region;

/**
 * @author armen.mkrtchyan
 */
public class DeploymentImpl implements Deployment {

    private Integer armies;

    private final Integer regions;

    public DeploymentImpl(Integer regions, Integer armies) {
        this.armies = armies;
        this.regions = regions;
    }

    @Override
    public Integer getArmies() {
        return armies;
    }

    @Override
    public Integer getRegion() {
        return regions;
    }

    public void setArmies(Integer armies) {
        this.armies = armies;
    }
}
