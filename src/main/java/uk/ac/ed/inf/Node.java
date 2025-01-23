package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.Objects;

public class Node {
    private final LngLat coordinates;
    private Node parent;
    private double distanceStart,distanceHeuristic;
    private double distanceTotal;
    private double angle;

    public Node(LngLat coordinates) {
        this.coordinates = coordinates;
        parent = null;
        angle = 0;
        distanceStart = 0;
        distanceHeuristic = 0;
        distanceTotal = 0;
    }
    @Override
    public int hashCode(){
        return Objects.hash(coordinates.lat(), coordinates.lng());
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }

        if(obj == null || getClass() != obj.getClass()){
            return false;
        }

        Node other = (Node)obj;
        return (other.coordinates.lat() == coordinates.lat()) &&
                (other.coordinates.lng() == coordinates.lng());
    }
    public LngLat getCoordinates() {
        return coordinates;
    }

    public Node getParent() {
        return parent;
    }
    public double getAngle(){return angle;}

    public void setParent(Node parent) {
        this.parent = parent;
    }
    public void setAngle(double angle){this.angle = angle;}
    public double getDistanceStart() {
        return distanceStart;
    }
    public void setDistanceStart(double distanceStart) {
        this.distanceStart = distanceStart;
    }
    public double getDistanceHeuristic() {
        return distanceHeuristic;
    }
    public void setDistanceHeuristic(double distanceHeuristic) {
        this.distanceHeuristic = (distanceHeuristic);
    }
    public void setDistanceTotal(double distanceTotal){
        this.distanceTotal = (distanceTotal);
    }
    public double getDistanceTotal() {
        return distanceTotal;
    }
}
