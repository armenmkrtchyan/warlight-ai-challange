package com.armen.wai.util.algorithm;

import com.armen.wai.util.SuperGraph;
import com.armen.wai.util.helper.Edge;
import com.armen.wai.util.helper.Node;
import com.armen.wai.util.helper.OwnerType;
import com.armen.wai.util.helper.Pair;

import java.util.*;

/**
 * Created by Hayk on 25/02/2017.
 */
public class BFS {

    /**
     * Get closest enemy node object and distance
     */
    private Pair<Node,Integer> closestEnemy(Node node, SuperGraph graph){

        Queue<Node> queue = new LinkedList<>();
        Map<Node, Boolean> visited = new HashMap<>();

        queue.add(node);
        Map<Node,Integer> dist = new HashMap<>();
        dist.put(node,0);
        visited.put(node,true);

        while (!queue.isEmpty()){

            Node temp = queue.poll();
            List<Edge> adjacent = graph.getAdjacent(temp);
            for (Edge edge : adjacent) {
                final Node to = edge.getTo();
                if (!visited.get(to)){
                    dist.put(to, dist.get(temp) + 1);
                    if (to.getOwnerType() == OwnerType.Enemy){
                        return new Pair<>(to, dist.get(to));
                    }

                    visited.put(to, true);
                    queue.add(to);
                }
            }
        }

        return new Pair<>(node,0);
    }

}
