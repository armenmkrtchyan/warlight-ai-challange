/* Copyright (C) 2009-2011  Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

/*  Refer: http://algowiki.net/wiki/index.php
 *  private boolean visited = false;   // used for Kosaraju's algorithm and Edmonds's algorithm
 *  private int lowlink = -1;          // used for Tarjan's algorithm
 *  private int index = -1;            // used for Tarjan's algorithm
 */
package com.armen.wai.util.helper;

/**
 *
 * @author Asad
 */
public class Node implements Comparable<Node>, Cloneable {

    private boolean visited = false;   // used for Kosaraju's algorithm and Edmonds's algorithm
    private int lowlink = -1;          // used for Tarjan's algorithm
    private int index = -1;            // used for Tarjan's algorithm
    private final int id;
    private OwnerType ownerType = OwnerType.Unknown;
    private int armies;

    /**
     * 
     * @param id
     */
    public Node(final int id) {
        this.id = id;
    }

    /**
     * 
     * @param argNode
     * @return
     */
    @Override
    public int compareTo(final Node argNode) {
        return argNode.id == this.id ? 0 : -1;
    }

    /**
     * 
     * @param otherObject
     * @return
     */
    @Override
    public boolean equals(Object otherObject) {
        // Not strictly necessary, but often a good optimization
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof Node)) {
            return false;
        }
        Node otherA = (Node) otherObject;
        return this.id == otherA.getId();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.id;
        return hash;
    }

    /**
     * @return the atom
     */
    public int getId() {
        return id;
    }

    /**
     * @return the visited
     */
    public boolean isVisited() {
        return visited;
    }

    /**
     * @param visited the visited to set
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    /**
     * @return the lowlink
     */
    public int getLowlink() {
        return lowlink;
    }

    /**
     * @param lowlink the lowlink to set
     */
    public void setLowlink(int lowlink) {
        this.lowlink = lowlink;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    public int getArmies() {
        return armies;
    }

    public void setArmies(int armies) {
        this.armies = armies;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        String s = "[Atom " + this.id
                + NEW_LINE + " index: " + this.index
                + NEW_LINE + " visited : " + this.visited
                + NEW_LINE + " lowlink: " + this.lowlink + "]" + NEW_LINE;
        result.append(s);
        return result.toString();
    }

    @Override
    public Node clone() {
        try {
            return (Node) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public OwnerType getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(OwnerType ownerType) {
        this.ownerType = ownerType;
    }
}
