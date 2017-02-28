package com.armen.wai.analytics;

import com.armen.wai.map.Region;
import com.armen.wai.move.Deployment;
import com.armen.wai.move.Move;

import java.util.List;

/**
 * @author armen.mkrtchyan
 */
public interface BattleAnalysis {

    List<Deployment> suggestDeployment(List<Region> orderedRegions);

    List<Move> suggestMoves();
}
