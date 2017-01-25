package vision.tools;

/**
 * Created by Simon Rovder
 *
 * SDP2017NOTE - This class contains everything you could possibly need for your mathsy calculations.
 */
public class VectorGeometry {
    public double x;
    public double y;

    public VectorGeometry(){}

    public VectorGeometry(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public VectorGeometry clone(){
        return new VectorGeometry(this.x, this.y);
    }

    public double length(){
        return VectorGeometry.length(this.x, this.y);
    }

    @Override
    public String toString(){
        return "X: " + this.x + " Y: " + this.y;
    }

    public VectorGeometry copyInto(VectorGeometry v){
        v.x = this.x;
        v.y = this.y;
        return v;
    }

    public VectorGeometry plus(double x, double y){
        this.x = this.x + x;
        this.y = this.y + y;
        return this;
    }

    public VectorGeometry plus(VectorGeometry vector){
        return this.plus(vector.x, vector.y);
    }

    public VectorGeometry minus(double x, double y){
        this.x = this.x - x;
        this.y = this.y - y;
        return this;
    }

    public VectorGeometry factor(double factor){
        this.x = this.x*(1 + factor);
        this.y = this.y*(1 + factor);
        return this;
    }

    public VectorGeometry minus(VectorGeometry vector){
        return this.minus(vector.x, vector.y);
    }



    public VectorGeometry multiply(double factor){
        this.x = this.x * factor;
        this.y = this.y * factor;
        return this;
    }

    public double angle(){
        return VectorGeometry.angle(this.x, this.y);
    }

    public VectorGeometry add(double x, double y){
        this.x = this.x + x;
        this.y = this.y + y;
        return this;
    }

    public VectorGeometry add(VectorGeometry v){
        return this.add(v.x, v.y);
    }

    public VectorGeometry setLength(double len){
        double length = this.length();
        if(length == 0) return this;
        double factor = len / length;
        this.x = this.x * factor;
        this.y = this.y * factor;
        return this;
    }

    public VectorGeometry fromAngular(double d, double distance){
        this.x = distance*Math.cos(d);
        this.y = distance*Math.sin(d);
        return this;
    }

    public static double angle(VectorGeometry a, VectorGeometry b, VectorGeometry c){
        VectorGeometry ba = a.copyInto(new VectorGeometry()).minus(b);
        VectorGeometry bc = c.copyInto(new VectorGeometry()).minus(b);
        return VectorGeometry.angle(ba, bc);
    }

    public static double angle(VectorGeometry a, VectorGeometry b){
        return VectorGeometry.angle(a.x,a.y,b.x,b.y);
    }

    public double distance(double x, double y){
        return VectorGeometry.distance(this.x, this.y, x, y);
    }

    public double distance(VectorGeometry other){
        return this.distance(other.x, other.y);
    }

    public VectorGeometry rotate(double d){
        this.x = Math.cos(d)*this.x - Math.sin(d)*this.y;
        this.y = Math.sin(d)*this.x + Math.cos(d)*this.y;
        return this;
    }

    /**
     * This function superimposes a tilted coordinate system on top of the existing one and changes the vector
     * to have the X and Y coordinates of wherever it was pointing in the superimposed coordinate system.
     * @param phi Angle of the superimposed coordinate system.
     * @return
     */
    public VectorGeometry coordinateRotation(double phi){
        double length = this.length();
        this.x = Math.cos(phi)*length;
        this.y = Math.sin(phi)*length;
        return this;
    }

    public VectorGeometry transpose(double dx, double dy){
        return this.plus(dx, dy);
    }



    public static VectorGeometry fromAngular(double d, double distance, VectorGeometry vg){
        if(vg == null) vg = new VectorGeometry();
        return vg.fromAngular(d, distance);
    }


    /**
     * Don't touch this.
     * @param deltaX Magic
     * @param deltaY More magic
     * @return
     */
    public VectorGeometry tilt3D(double deltaX, double deltaY){
        this.x = this.x * (1 + deltaX*this.y);
        this.y = this.y * (1 + deltaY*this.x);
        return this;
    }


    /**
     * The barrel undistortion algorithm. Gets rid of that fish-eye thing that the camera does.
     * Google this if you want to know more.
     * @param barrelConstant Barrel constant
     * @return
     */
    public VectorGeometry barrelUndistort(double barrelConstant){
        double distortedDistance = this.length();
        double undistortedDistance = distortedDistance/(1-barrelConstant*distortedDistance*distortedDistance);
        this.fromAngular(this.angle(), undistortedDistance);
        return this;
    }

    public static double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt(squareDistance(x1, y1, x2, y2));
    }

    public static double squareDistance(double x1, double y1, double x2, double y2){
        double x = x1 - x2;
        double y = y1 - y2;
        return x*x + y*y;
    }

    public static double squareLength(double x, double y){
        return squareDistance(0,0,x,y);
    }

    public static double length(double x, double y){
        return Math.sqrt(squareLength(x, y));
    }

    public static double angle(double x1, double y1, double x2, double y2){
        double cos = (x1*x2 + y1*y2)/(length(x1, y1)*length(x2, y2));
        if(cos > 1) return 0;
        if(cos < -1) return Math.PI;
        return Math.acos(cos);
    }

    public static double angle(double x, double y){
        double res = angle(1,0,x,y);
        if(y < 0) return -res;
        return res;
    }


    /**
     * If we had a line crossing through point 'base' and having the direction 'dir', this function returns the
     * positional vector of the closest point on the line to the point 'point'
     * @param base
     * @param dir
     * @param point
     * @return
     */
    public static VectorGeometry closestPointToLine(VectorGeometry base, VectorGeometry dir, VectorGeometry point){
        double numerator = dotProduct(point, dir) - dotProduct(base, dir);
        double denominator = dotProduct(dir, dir);
        double t = numerator/denominator;
        VectorGeometry vector = new VectorGeometry();
        vector.x = base.x + t*dir.x;
        vector.y = base.y + t*dir.y;
        return vector;
    }


    /**
     * If we had a line crossing through point 'base' and having the direction 'dir', this method returns true
     * if the point 'p' could be considered as being "In the general direction" of the line. Very relative to what
     * you consider "general direction", but works.
     * @param base
     * @param dir
     * @param point
     * @return
     */
    public static boolean isInGeneralDirection(VectorGeometry base, VectorGeometry dir, VectorGeometry point){
        VectorGeometry baseToPoint = new VectorGeometry(point.x - base.x, point.y - base.y);
        double angle = Math.abs(VectorGeometry.angle(dir.x, dir.y, baseToPoint.x, baseToPoint.y));
        return angle < 1;
    }

    /**
     * If we had a finite line from point 'a' to point 'b', this method returns the positional vector to the closest
     * point on this finite line to 'point'.
     * @param a
     * @param b
     * @param point
     * @return
     */
    public static VectorGeometry vectorToClosestPointOnFiniteLine(VectorGeometry a, VectorGeometry b, VectorGeometry point){
        VectorGeometry dir = new VectorGeometry();
        dir.x = b.x-a.x;
        dir.y = b.y-a.y;
        VectorGeometry closest = VectorGeometry.closestPointToLine(a, dir, point);
        if(VectorGeometry.isBetweenPoints(closest, a, b)){
            return closest;
        }
        if(VectorGeometry.distance(closest, a) < VectorGeometry.distance(closest,b)){
            a.copyInto(closest);
        } else {
            b.copyInto(closest);
        }
        return closest;
    }

    /**
     * If we had an infinite line passing through point 'base' in the direction of 'dir', this method returns the
     * positional vector to the point where this line intersects the finite line between points 'a' and 'b'. Very useful
     * for working with the goal posts and robots' aim.
     * @param base
     * @param dir
     * @param a
     * @param b
     * @return
     */
    public static VectorGeometry intersectionWithFiniteLine(VectorGeometry base, VectorGeometry dir, VectorGeometry a, VectorGeometry b){
        VectorGeometry intersection = intersectionOfLines(base, dir, a, VectorGeometry.fromTo(a, b));
        if(intersection == null) return null;
        if(isBetweenPoints(intersection, a, b)){
            return intersection;
        }
        return closerOfTwo(intersection, a, b);
    }

    public static VectorGeometry closerOfTwo(VectorGeometry point, VectorGeometry a, VectorGeometry b){
        if(VectorGeometry.distance(point, a) > VectorGeometry.distance(point, b)){
            return b;
        } else {
            return a;
        }
    }


    public static double distanceFromLine(VectorGeometry base, VectorGeometry dir, VectorGeometry point){
        VectorGeometry vector = closestPointToLine(base, dir, point);
        return vector.distance(vector.x, vector.y);
    }

    public static double dotProduct(VectorGeometry v1, VectorGeometry v2){
        return v1.x*v2.x + v1.y*v2.y;
    }

    /**
     * If we had a line base2 + t*dir2 and a line base2 + k*dir2 (for all t and k), this method returns the point
     * of their intersection (if there is one).
     * @param base1
     * @param dir1
     * @param base2
     * @param dir2
     * @return
     */
    public static VectorGeometry intersectionOfLines(VectorGeometry base1, VectorGeometry dir1, VectorGeometry base2, VectorGeometry dir2){
        double discriminant = dir1.y*dir2.x - dir1.x*dir2.y;
        if(discriminant == 0) return null;
        double t = (-dir2.y*(base2.x-base1.x) + dir2.x*(base2.y-base1.y))/discriminant;
        VectorGeometry res = new VectorGeometry();
        res.x = base1.x + t*dir1.x;
        res.y = base1.y + t*dir1.y;
        return res;
    }

    public static double distance(VectorGeometry a, VectorGeometry b){
        return VectorGeometry.distance(a.x,a.y,b.x,b.y);
    }

    public static boolean isBetweenPoints(VectorGeometry point, VectorGeometry a, VectorGeometry b){
        return a.distance(b) + 1 > point.distance(a) + point.distance(b);
    }

    public static void main(String[] args){

        VectorGeometry v = new VectorGeometry(1,7);



//        System.out.println(VectorGeometry.intersectionWithFiniteLine(
//                new VectorGeometry(0,0),
//                new VectorGeometry(-1,2),
//                new VectorGeometry(0,2),
//                new VectorGeometry(2,0)
//        ));
        System.out.println(VectorGeometry.signedAngle(new VectorGeometry(1,0), new VectorGeometry(0,1)));
        System.out.println(VectorGeometry.signedAngle(new VectorGeometry(0,1), new VectorGeometry(1,0)));
    }

    public static boolean crossProductDirection(VectorGeometry a, VectorGeometry b){
        double res = a.x*b.y - a.y*b.x;
        return res < 0;
    }


    /**
     * Returns angle from b to a. Signed.
     * @param a
     * @param b
     * @return
     */
    public static double signedAngle(VectorGeometry a, VectorGeometry b){
        double angle = VectorGeometry.angle(a,b);
        boolean sign = crossProductDirection(a, b);
        return angle * (sign ? 1 : -1);
    }

    public static VectorGeometry fromTo(VectorGeometry a, VectorGeometry b){
        return b.copyInto(new VectorGeometry()).minus(a);
    }

    public static boolean isInRectangle(VectorGeometry point, double x1, double y1, double x2, double y2){
        return point.x > x1 && point.y > y1 && point.x < x2 && point.y < y2;
    }

    public static VectorGeometry fromTo(double x, double y, int x1, int y1) {
        return fromTo(new VectorGeometry(x, y), new VectorGeometry(x1, y1));
    }
}
