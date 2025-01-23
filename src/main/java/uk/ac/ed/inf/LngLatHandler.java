package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LngLatHandler implements LngLatHandling {
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        double lngDif = startPosition.lng() - endPosition.lng();
        double latDif = startPosition.lat() - endPosition.lat();

        //uses Euclidean distance equation to find distance between points sqrt((x1-x2)^2 + (y1-y2)^2))
        return Math.sqrt((lngDif*lngDif) + (latDif*latDif));
    }

    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        //finds if the distance between two points is closer than a set constant
        double distance = distanceTo(startPosition,otherPosition);
        return distance < SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
        /*Use Ray-casting algorithm to find if point is inside closed polygon
        this is where you draw a horizontal line to the right from a point
        then count the number of times the line intersects with polygon edges
        the point will be inside the polygon if the number of intersections is odd
        or the point is on the edge of the polygon, if neither of these are true
        then it is outside*/
        double lngPosition = position.lng(); // x
        double latPosition = position.lat(); // y
        int numberIntersections = 0;

        LngLat[] vertices = region.vertices();
        int n = vertices.length;

        for (int i = 0;i < n;i++){
            double x1 = vertices[i].lng();
            double y1 = vertices[i].lat();
            //if the position is equal to a vertices then it is in the region
            if ((lngPosition == x1) && (latPosition == y1)){return true;}
            //%n wraps around the polygon vertices (first and last are same for no fly-zones but not central area)
            double x2 = vertices[(i+1)%n].lng();
            double y2 = vertices[(i+1)%n].lat();

            boolean latInLineYRange = (y1 <= latPosition && latPosition <= y2) || (y2 <= latPosition && latPosition < y1);
            //edge case if x1 and x2 are equal (can't calculate gradient) then just
            // check whether the lng is less than x1 and if y is between the two points
            // if lng is equal to x1 and x2 then also check whether y is between the two points if it
            //is in region is true as it lies on the edge
            if ((x2-x1)==0){
                if ((lngPosition - x1) == 0){
                    if (latInLineYRange){
                        return true;
                    }
                    continue;
                }
                else if ((lngPosition < x1) &&
                        (latInLineYRange)){
                    numberIntersections ++;
                    continue;
                }
            }

            //calculate gradient and intercept for line between two points of the polygon
            double gradient = (y2-y1)/(x2-x1);
            double intercept = y1-(gradient*x1);

            if (latInLineYRange) {
                //if lng of the point is less than or equal to the max x value of the line than it might intersect
                //otherwise it doesn't
                if (lngPosition <= Math.max(x1, x2)) {
                    //if the lng is also greater than the min x value of the line then must find whether it is less
                    //than the x value on the line at the lat if not it will be on the right side and not intersect
                    if (lngPosition > Math.min(x1,x2)) {
                        double lineX = (latPosition - intercept) / gradient;
                        if (lngPosition == lineX) {
                            //if lngPosition is equal to the lineX then the position lies on the line and so is in region
                            return true;
                        }
                        if (lngPosition < (lineX)) {
                            numberIntersections++;
                        }
                    }
                    else{
                        numberIntersections++;
                    }}}}

        return (numberIntersections%2)==1;
    }

    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        //angle = angle %360; //not needed but makes it easier to figure out if next position is correct
        //get angle to the nearest 22.5
       // double roundedAngle = Math.round(angle/22.5) * 22.5;
        double radiansAngle = Math.toRadians(angle);
        //using trig find new position, need to convert to radians as well conversion to radians isn't
        //100% accurate
        double newLng = (startPosition.lng() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(radiansAngle)));
        double newLat = (startPosition.lat() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(radiansAngle)));
        return (new LngLat(newLng,newLat));
    }
}
