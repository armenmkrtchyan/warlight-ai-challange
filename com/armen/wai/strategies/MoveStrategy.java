package com.armen.wai.strategies;

import com.armen.wai.analytics.BattleAnalysis;
import com.armen.wai.analytics.MapAnalysis;
import com.armen.wai.move.Move;

import java.util.List;

/**
 * @author armen.mkrtchyan
 */
public interface MoveStrategy {

    List<Move> getMoves(BattleAnalysis battleAnalysis, MapAnalysis mapAnalysis);

}
