// Coordinate.java
// Dalton Burke
// Coordinate class, used to indicate position, and to do
// linear algebra for dead reckoning calculations

package com.leidossd.djiwrapper;

// Coordinate needs to be parcelable to be handed between activities and services (for missions)
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

public class Coordinate implements Parcelable {

    private float x;
    private float y;
    private float z;

    public Coordinate(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public Coordinate add(Coordinate other) {
        return new Coordinate(x + other.x, y + other.y, z + other.z);
    }

    public Coordinate scale(float constant) {
        return new Coordinate(x * constant, y * constant, z * constant);
    }

    public Coordinate unit() {
        if (this.magnitude() == 0)
            throw new IllegalArgumentException("Cannot create unit coordinate with 0 magnitude");
        return this.scale(1 / this.magnitude());
    }

    // xy functions do the math for the xy plane (rotation complicates only xy)
    public Coordinate xyUnit() {
        return new Coordinate(this.x, this.y, 0).unit();
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public float xyMagnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    // a unit vector perpendicular to the current vector in the x,y plane
    public Coordinate perpendicularUnit() {
        return this.rotateByAngle(90);
//        if (x == 0 && y == 0)
//            throw new IllegalArgumentException("Can't get a perpendicular unit vector from a 0 vector!");
//        if (x == 0)
//            return new Coordinate(1, -x / y, 0).unit();
//        else
//            return new Coordinate(y / x, -1, 0).unit();
    }

    public float dot(Coordinate other) {
        return x * other.x + y * other.y + z * other.z;
    }

    // If the aircraft is rotated to angle theta, and I want to keep it rotated that way
    // for the flight, then I need to take the direction vector in the regular space, and
    // write it in the coordinate system of the aircraft.

    // To do that, we'll have to rotate the movement vector by negative theta

    public Coordinate rotateByAngle(float theta) {
        // this is a cute way of multiplying by a rotation matrix with negative theta
        float newX = this.dot(new Coordinate((float) Math.cos(Math.toRadians(theta)),
                (float) Math.sin(Math.toRadians(theta)), 0));
        float newY = this.dot(new Coordinate((float) -Math.sin(Math.toRadians(theta)),
                (float) Math.cos(Math.toRadians(theta)), 0));

        return new Coordinate(newX, newY, this.z);
    }

    // sine cosine math to determine which way the drone is
    // facing with respect to the positive y axis, between -180 and 180
    public float angleFacing() {
        if (this.x >= 0 && y >= 0)
            return (float) (180 * Math.asin(this.x / this.magnitude()) / Math.PI);
        else if (this.x >= 0)
            return (float) (90 + 180 * Math.asin(-this.y / this.magnitude()) / Math.PI);
        else if (this.y >= 0)
            return (float) (-180 * Math.asin(-this.x / this.magnitude()) / Math.PI);
        else
            return (float) (-90 - 180 * Math.asin(-this.y / this.magnitude()) / Math.PI);
    }

    // The angle between the given angle and the angle that the current vector points at
    public float angleBetween(float angle) {
        float diff = (angle - angleFacing()) % 360;
        // this is because java % can give negatives (WHY?!)
        if(diff < 0)
            diff += 360;
        // diff should be between 0 and 360 now, if it's more than 180 we want it to go the other way
        if(diff > 180)
            return diff - 360;
        else
            return diff;
    }

    // representation of a coordinate in a basis formed with x, y
    // we do not care about z-axis here
    public Coordinate inBasis(Coordinate x, Coordinate y) {
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
        float det = a * d - b * c;

        if (Math.abs(det) < .01)
            throw new IllegalArgumentException("x and y cannot form a basis if parallel!" +
                    x + ", " + y);

        // this gives us rows of the inverse
        Coordinate row1 = new Coordinate(d, -b, 0).scale(1 / det);
        Coordinate row2 = new Coordinate(-c, a, 0).scale(1 / det);

        // simple matrix multiplication made to look complicated
        float newX = row1.dot(this);
        float newY = row2.dot(this);

        return new Coordinate(newX, newY, this.z);
    }

    public static Coordinate sum(Coordinate c1, Coordinate c2) {
        return new Coordinate(c1.x + c2.x, c1.y + c2.y, c1.z + c2.z);
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
    public String toString() {
        return String.format(Locale.getDefault(), "(%.2f,%.2f,%.2f)", x, y, z);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeFloat(z);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Creator<Coordinate> CREATOR = new Creator<Coordinate>() {
        @Override
        public Coordinate createFromParcel(Parcel source) {
            float x = source.readFloat();
            float y = source.readFloat();
            float z = source.readFloat();
            return new Coordinate(x, y, z);
        }

        @Override
        public Coordinate[] newArray(int size) {
            return new Coordinate[0];
        }
    };
}
