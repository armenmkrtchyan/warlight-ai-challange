package com.armen.wai.strategies;

import com.armen.wai.analytics.BattleAnalysis;
import com.armen.wai.analytics.MapAnalysis;
import com.armen.wai.move.Move;

import java.util.List;

/**
 * Created by Anushavan on 2/25/17.
 */
public class MoveStrategyImpl implements MoveStrategy {

    private final BattleAnalysis battleAnalysis;

    private final MapAnalysis mapAnalysis;

    public MoveStrategyImpl(BattleAnalysis battleAnalysis, MapAnalysis mapAnalysis) {
        this.battleAnalysis = battleAnalysis;
        this.mapAnalysis = mapAnalysis;
    }

    @Override
    public List<Move> getMoves() {
        return battleAnalysis.suggestMoves();
    }

}
