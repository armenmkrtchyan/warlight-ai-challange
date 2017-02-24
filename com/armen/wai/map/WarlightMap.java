package com.armen.wai.map;

/**
 * @author armen.mkrtchyan
 */
public interface WarlightMap {

    void setup(String configKey, String config);

    void finalSetup();

    void update(String configKey, String config);

    void settings(String[] configLine);

}
