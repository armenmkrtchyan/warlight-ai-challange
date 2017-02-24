package com.armen.wai.analytics;

import com.armen.wai.util.SuperGraph;

/**
 * Created by Anushavan on 2/25/17.
 */
public class BattleAnalysisImpl implements BattleAnalysis {

    private final SuperGraph superGraph;

    public BattleAnalysisImpl(SuperGraph superGraph) {
        this.superGraph = superGraph;
    }
}
