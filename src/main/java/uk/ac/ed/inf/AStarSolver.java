package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.util.*;

public class AStarSolver {
    // {0,22.5,45,67.5,90,112.5,135,157.5,180,202.5,225
    //    ,247.5,270,292.5,315,337.5}
    //{0,45,90,135,180,225,270,315}
    private static final double[] DIRS = {0,45,90,135,180,225,270,315}; // 16 movement directions wouldn't work whilst 8 did
    static PriorityQueue<Node> openSet;     // frontier
    static HashSet<Node> closedSet;         // visited
    static List<Move> path; //resulting path

    public static List<Move> findShortestPath(Node start, Node goal, NamedRegion[] noFlyZones){
        // need for making moves and finding if in no-fly zone
        LngLatHandling lngLatHandling = new LngLatHandler();
        openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getDistanceTotal));
        closedSet = new HashSet<>();

        openSet.add(start);

        while (!openSet.isEmpty()){
            //get the node with the smallest cost
            Node current = openSet.poll();

            //mark the node to be visited
            closedSet.add(current);

            //if current node is close enough to the goal then find the path using parent and exit
            if (lngLatHandling.isCloseTo(current.getCoordinates(),goal.getCoordinates())){
                path = new ArrayList<>();
                while (current.getParent() != null){
                    //might only need to add LngLat to the path
                    Node parent = current.getParent();
                    Move flightPath = new Move(parent.getCoordinates().lng(),parent.getCoordinates().lat()
                    ,current.getAngle(),current.getCoordinates().lng(),current.getCoordinates().lat());
                    path.add(flightPath);
                    current = parent;
                }
                //add hover move
                Move lastMove = path.get(0);
                Collections.reverse(path);
                path.add(new Move(lastMove.getToLongitude(),lastMove.getToLatitude(),999
                        , lastMove.getToLongitude(), lastMove.getToLatitude()));

                return path;
            }

            //search all the neighbours of the current node
            for (double dir : DIRS){
                //neighbour coordinates
                LngLat newCoordinates = lngLatHandling.nextPosition(current.getCoordinates(),dir);

                if (isMovable(noFlyZones,newCoordinates) && !closedSet.contains(new Node(newCoordinates))){
                    //movement should be equal to DRONE_MOVE_DISTANCE
                    double distanceStart = current.getDistanceStart() + SystemConstants.DRONE_MOVE_DISTANCE;

                    Node existing_neighbour = findNeighbor(newCoordinates);

                    if(existing_neighbour != null){
                        // Check if this path is better than any previously generated path to the neighbor
                        if(distanceStart < existing_neighbour.getDistanceStart()){
                            //update cost ,parent information and angle
                            existing_neighbour.setAngle(dir);
                            existing_neighbour.setParent(current);
                            existing_neighbour.setDistanceStart(distanceStart);
                            existing_neighbour.setDistanceHeuristic(lngLatHandling.distanceTo(existing_neighbour.getCoordinates()
                                    ,goal.getCoordinates()));
                            existing_neighbour.setDistanceTotal(existing_neighbour.getDistanceStart()+ existing_neighbour.getDistanceHeuristic());
                            openSet.remove(existing_neighbour);
                            openSet.add(existing_neighbour);
                        }
                    }
                    else {
                        //Or add this node to the frontier
                        Node neighbour = new Node(newCoordinates);
                        neighbour.setAngle(dir);
                        neighbour.setParent(current);
                        neighbour.setDistanceStart(distanceStart);
                        neighbour.setDistanceHeuristic(lngLatHandling.distanceTo(neighbour.getCoordinates(),
                                goal.getCoordinates()));
                        neighbour.setDistanceTotal(neighbour.getDistanceStart()+ neighbour.getDistanceHeuristic());
                        openSet.add(neighbour);
                    }
                }
            }
        }
        return null;
    }

    public static boolean isMovable(NamedRegion[] noFlyZones,LngLat coordinates){
        LngLatHandling handler = new LngLatHandler();
        for (NamedRegion noFlyZone: noFlyZones){
            if (handler.isInRegion(coordinates,noFlyZone)){
                return false;
            }
        }
        return true;
    }

    private static Node findNeighbor(LngLat coordinates){
        if(openSet.isEmpty()){
            return null;
        }

        Iterator<Node> iterator = openSet.iterator();

        Node find = null;
        while (iterator.hasNext()) {
            Node next = iterator.next();
            if((next.getCoordinates().lng() == coordinates.lng())&&
                    (next.getCoordinates().lat() == coordinates.lat())){
                find = next;
                break;
            }
        }
        return find;
    }


}
