package org.uma.jmetalsp.problem.tsp.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Jose Andres
 */
public class ParsedNode {
    
    private Integer id;
    private int position;
    private double speed;
    private int travelTime;
    private int distance;
    private boolean status;
    private String polyLine;
    private String name;
    private boolean distanceUpdated;
    private boolean costUpdated;
    private List<Coord> coords;
    private Set<ParsedNode> nodes;

    public ParsedNode(Integer id, double speed, int travelTime, boolean status, String polyLine, String name, List<Coord> coords) {
        this.id = id;
        this.speed = speed;
        this.travelTime = travelTime;
        this.status = status;
        this.polyLine = polyLine;
        this.coords = coords;
        this.name = name;
        nodes = new HashSet<ParsedNode>();
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(int travelTime) {
        this.travelTime = travelTime;
    }
    
    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getPolyLine() {
        return polyLine;
    }

    public void setPolyLine(String polyLine) {
        this.polyLine = polyLine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Coord> getCoords() {
        return coords;
    }

    public void setCoords(List<Coord> coords) {
        this.coords = coords;
    }

    public Set<ParsedNode> getNodes() {
        return nodes;
    }

    public void addNode(ParsedNode node) {
        nodes.add(node);
    }

    public boolean isCostUpdated() {
        return costUpdated;
    }

    public void setCostUpdated(boolean updated) {
        this.costUpdated = updated;
    }
    
    public boolean isDistanceUpdated() {
        return distanceUpdated;
    }

    public void setDistanceUpdated(boolean updated) {
        this.distanceUpdated = updated;
    }

    @Override
    public String toString() {
        return "ParsedNode{" + "id=" + id + ", speed=" + speed + ", travelTime=" + travelTime + ", status=" + status + ", coords=" + coords.size() + ", nodes=" + nodes.size() + '}';
    }
    
}
