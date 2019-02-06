package com.leidossd.dronecontrollerapp;

import com.leidossd.djiwrapper.Coordinate;

import org.junit.Test;
import static org.junit.Assert.*;

public class CoordinateUnitTest {
    @Test
    public void scale_Zero(){
        assertEquals(new Coordinate(2,2,2).scale(0), new Coordinate(0,0,0));
    }

    @Test
    public void scale_Five(){
        assertEquals(new Coordinate(2,2,2).scale(5), new Coordinate(10,10,10));
    }

    @Test
    public void scale_Negative(){
        assertEquals(new Coordinate(2,2,2).scale(-5), new Coordinate(-10, -10, -10));
    }

    @Test
    public void unit_Zero(){
        // need to figure out how to assert that it throws
    }

    @Test
    public void unit_FiveZeroZero(){
        assertEquals(new Coordinate(5,0,0).unit(), new Coordinate(1,0,0));
    }

    @Test
    public void unit_ZeroFiveZero(){
        assertEquals(new Coordinate(0,5,0).unit(), new Coordinate(0,1,0));
    }

    @Test
    public void unit_ThreeFourZero(){
        assertEquals(new Coordinate(3,4,0).unit(), new Coordinate((float) .6, (float) .8, 0));
    }

    @Test
    public void magnitude(){
        assertEquals(new Coordinate(3,4,0).magnitude(), 5, .01);
    }

    @Test
    public void perpendicular_OneZero(){
        assertEquals(new Coordinate(1,0,0).perpendicularUnit(), new Coordinate(0,-1,0));
    }

    @Test
    public void perpendicular_ZeroOne(){
        assertEquals(new Coordinate(0,1,0).perpendicularUnit(), new Coordinate(1,0,0));
    }

    @Test
    public void perpendicular_Complicated(){
        assertEquals(new Coordinate(5,5,0).perpendicularUnit(), new Coordinate((float) 1.41/2, (float) -1.41/2, 0));
    }

    @Test
    public void dot_Complicated(){
        assertEquals(new Coordinate(1,2,3).dot(new Coordinate(5,6,7)), 5+12+21, .01);
    }

    @Test
    public void add_Complicated(){
        assertEquals(new Coordinate(1, -5, 20).add(new Coordinate(-10,-10, -10)), new Coordinate(-9, -15, 10));
    }

    @Test
    public void angleBetweenCoordinate(){
        assertEquals(new Coordinate(1,0,0).angleBetween(new Coordinate(0,1,0)), 90, .01);
    }

    @Test
    public void inBasis_simple(){
        assertEquals(new Coordinate(5,5,0).inBasis(new Coordinate(-1, 0, 0), new Coordinate(0,-1,0)), new Coordinate(-5,-5,0));
    }

    @Test
    public void inBasis_Complicated(){
        assertEquals(new Coordinate(5,5,0).inBasis(new Coordinate(7,2,0), new Coordinate(-5, 3, 0)), new Coordinate((float) 1.2903, (float) 0.80645, 0));
    }
}
