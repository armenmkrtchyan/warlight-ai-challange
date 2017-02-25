package com.armen.wai.move;

/**
 * Created by Anushavan on 2/25/17.
 */
public class MoveImpl implements Move {

    private Integer startRegionId;
    private Integer endRegionId;
    private Integer armies;

    public MoveImpl(Integer startRegionId, Integer endRegionId, Integer armies) {
        this.startRegionId = startRegionId;
        this.endRegionId = endRegionId;
        this.armies = armies;
    }

    @Override
    public Integer getStartRegionId() {
        return startRegionId;
    }

    @Override
    public Integer getEndRegionId() {
        return endRegionId;
    }

    @Override
    public Integer getArmies() {
        return armies;
    }

}
