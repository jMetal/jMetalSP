package org.uma.jmetalsp.problem.tsp.data;

public class Coord {
    
    private Double x;
    private Double y;

    public Coord(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
    
    public Double distance(Coord c) {
        double a = c.x - x;
        double b = c.y - y;
        return Math.sqrt(a*a + b*b);
    }
    
    @Override
    public String toString() {
        return "Coord {" + x + "," + y + "}";
    }
}
