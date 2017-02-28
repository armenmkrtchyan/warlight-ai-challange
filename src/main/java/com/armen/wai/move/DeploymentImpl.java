package com.armen.wai.move;

/**
 * @author armen.mkrtchyan
 */
public class DeploymentImpl implements Deployment {

    private Integer armies;

    private final Integer region;

    public DeploymentImpl(Integer region, Integer armies) {
        this.armies = armies;
        this.region = region;
    }

    @Override
    public Integer getArmies() {
        return armies;
    }

    @Override
    public Integer getRegion() {
        return region;
    }

    public void setArmies(Integer armies) {
        this.armies = armies;
    }
}
