package model.point;

import java.io.Serializable;

import model.equipment.weapons.Weapon;

public class Point  implements Serializable{

    private static final long serialVersionUID = 1L;

	private int x;
	private int y;
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int distanceFrom(Point other) {
        if (other == null) {
            throw new IllegalArgumentException("Other point cannot be null.");
        }
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y); // Distanza di Manhattan
    }
	
	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
}
