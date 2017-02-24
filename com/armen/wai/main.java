package com.armen.wai;

import com.armen.wai.map.WarlightMap;
import com.armen.wai.map.WarlightMapImpl;

/**
 * Created by Anushavan on 2/24/17.
 */
public class Main {

    public static void main(String[] args) {
        WarlightMap warlightMap = new WarlightMapImpl();
        warlightMap.setup("neighbors", "1 2,3,4 2 3 4 5");
    }
}
