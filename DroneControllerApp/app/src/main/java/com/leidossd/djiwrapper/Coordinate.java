package com.leidossd.djiwrapper;

public class Coordinate {

    private float x;
    private float y;
    private float z;

    public Coordinate(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX(){
        return this.x;
    }

    public float getY(){
        return this.y;
    }

    public float getZ(){
        return this.z;
    }

    public Coordinate add(Coordinate other){
        return new Coordinate(x + other.x, y + other.y, z + other.z);
    }

    public Coordinate scale(float constant){
        return new Coordinate(x*constant, y*constant, z*constant);
    }

    public Coordinate unit(){
        if(this.magnitude() == 0)
            throw new IllegalArgumentException("Cannot create unit coordinate with 0 magnitude");
        return this.scale(1/this.magnitude());
    }

    public float magnitude(){
        return (float) Math.sqrt(x*x + y*y + z*z);
    }

    // a unit vector perpendicular to the current vector in the x,y plane
    public Coordinate perpendicularUnit(){
        if(x == 0 && y == 0)
            throw new IllegalArgumentException("Can't get a perpendicular unit vector from a 0 vector!");
        if(x == 0)
            return new Coordinate(1, -x/y, 0).unit();
        else
            return new Coordinate(y/x, -1, 0).unit();
    }

    public float dot(Coordinate other){
        return x*other.x + y*other.y + z*other.z;
    }

    // If the aircraft is rotated to angle theta, and I want to keep it rotated that way
    // for the flight, then I need to take the direction vector in the regular space, and
    // write it in the coordinate system of the aircraft.

    // To do that, we'll have to rotate the movement vector by negative theta

    public Coordinate rotateByAngle(float theta){
        // this is a cute way of multiplying by a rotation matrix with negative theta
        float newX = this.dot(new Coordinate((float) Math.cos(Math.toRadians(theta)),
                (float) Math.sin(Math.toRadians(theta)),0));
        float newY = this.dot(new Coordinate((float) -Math.sin(Math.toRadians(theta)),
                (float) Math.cos(Math.toRadians(theta)),0));

        return new Coordinate(newX, newY, this.z);
    }

    public float angleFacing(){
        float sin = this.x/this.magnitude();
        float angle = (float) (180*Math.asin(Math.abs(this.x/this.magnitude()))/Math.PI);
        if(this.x >= 0 && y >= 0)
            return (float) (180*Math.asin(this.x/this.magnitude())/Math.PI);
        else if(this.x >= 0)
            return (float) (90 + 180*Math.asin(-this.y/this.magnitude())/Math.PI);
        else if(this.y >= 0)
            return (float) (-180*Math.asin(-this.x/this.magnitude())/Math.PI);
        else
            return (float) (-90 - 180*Math.asin(-this.y/this.magnitude())/Math.PI);
    }

    // The angle between the given angle and the angle that the current vector points at
    public float angleBetween(float angle){
        float diff = angle - angleFacing();
        if(diff > 180)
            return 360 - diff;
        if(diff < -180)
            return 360 + diff;

        return diff;
    }

    // do not use
    // public float angleBetween(Coordinate other){
    //     return (float) (180*Math.acos(this.unit().dot(other.unit()))/Math.PI);
    // }

    // representation of a coordinate in a basis formed with x, y
    // we do not care about z-axis here
    public Coordinate inBasis(Coordinate x, Coordinate y){
        // picture a transformation matrix
        //
        //     | a  b |
        //     | c  d |
        //
        // for going from the destination to the standard basis
        // what we want is to go from the standard basis to the destination, so we need the inverse

        // this is going to look like
        //
        //     | d -b |
        //     |-c  a |
        //
        // scaled by 1/(ad-bc) (see inverse of a 2x2 matrix)

        float a = x.getX();
        float c = x.getY();
        float b = y.getX();
        float d = y.getY();
        float det = a*d - b*c;

        if(det < .01)
            throw new IllegalArgumentException("x and y cannot form a basis if parallel!" +
                    x + ", " + y);

        // this gives us rows of the inverse
        Coordinate row1 = new Coordinate(d, -b, 0).scale(1/det);
        Coordinate row2 = new Coordinate(-c, a, 0).scale(1/det);

        // simple matrix multiplication made to look complicated
        float newX = row1.dot(this);
        float newY = row2.dot(this);

        return new Coordinate(newX,newY,this.z);
    }

    @Override
    public boolean equals(Object other) {
        // If the object is compared with itself then return true
        if (other == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(other instanceof Coordinate)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Coordinate c = (Coordinate) other;

        // Compare the data members and return accordingly
        return (Math.abs(this.x - c.x) < .01 && Math.abs(this.y - c.y) < .01 && Math.abs(this.z - c.z) < .01);
    }

    @Override
    public String toString(){
        return String.format("(%.3f,%.3f,%.3f)", x,y,z);
    }
}
