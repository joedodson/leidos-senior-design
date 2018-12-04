package com.leidossd.djiwrapper;

public class Coordinate {

    private double x;
    private double y;
    private double z;

    public Coordinate(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public double getZ(){
        return this.z;
    }

    public Coordinate add(Coordinate other){
        return new Coordinate(x + other.x, y + other.y, z + other.z);
    }

    public Coordinate scale(double constant){
        return new Coordinate(x*constant, y*constant, z*constant);
    }

    public Coordinate unit(){
        return this.scale(1/this.magnitude());
    }

    public double magnitude(){
        return Math.sqrt(x*x + y*y + z*z);
    }

    public double dot(Coordinate other){
        return x*other.x + y*other.y + z*other.z;
    }

    // If the aircraft is rotated to angle theta, and I want to keep it rotated that way
    // for the flight, then I need to take the direction vector in the regular space, and
    // write it in the coordinate system of the aircraft.

    // To do that, we'll have to rotate the movement vector by negative theta

    public Coordinate rotateByAngle(double theta){
        // this is a cute way of multiplying by a rotation matrix
        double newX = this.dot(new Coordinate(Math.cos(Math.toRadians(theta)),
                                             -Math.sin(Math.toRadians(theta)),0));
        double newY = this.dot(new Coordinate(Math.sin(Math.toRadians(theta)),
                                              Math.cos(Math.toRadians(theta)),0));

        return new Coordinate(newX, newY, this.z);
    }


    // The angle between the current angle of the aircraft and the current coordinate
    public double angleBetween(double angle){
        Coordinate dir = new Coordinate(Math.cos(Math.toRadians(angle)),
                                        Math.sin(Math.toRadians(angle)), 0);

        return Math.acos(this.unit().dot(dir));
    }
}
