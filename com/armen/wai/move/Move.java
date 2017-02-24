package com.armen.wai.move;

import com.armen.wai.map.Region;

/**
 * Created by Anushavan on 2/25/17.
 */
public interface Move {

    Region getStartRegion();

    Region getEndRegion();

    Integer getArmies();

}
