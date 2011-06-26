package de.tud.stg.popart.builder.utils;
import java.io.Serializable;

import de.tud.stg.tigerseye.dslsupport.logger.DSLSupportLogger;

/**
 * {@link Pair} links an object of type X and an object of type Y to a pair.
 * 
 * @param <X>
 * @param <Y>
 */

public class Pair<X, Y> implements Serializable {

	private static final long serialVersionUID = -1343477089401295365L;

	private final X x;
	private final Y y;

	/**
	 * @param x
	 * @param y
	 */
	public Pair(X x, Y y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the x
	 */
	public X getX() {
		return this.x;
	}

	/**
	 * @return the y
	 */
	public Y getY() {
		return this.y;
	}

	@Override
	public String toString() {
		return "(" + this.getX() + ", " + this.getY() + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair<?, ?>) {
			Pair<?, ?> p = (Pair<?, ?>) obj;
			return (p.x.equals(this.x) && (p.y.equals(this.y)));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + this.x.hashCode();
		hash = 31 * hash + this.y.hashCode();
		return hash;
	}

	// public static void main(String[] args) {
	// Pair<Integer, Integer> p = new Pair<Integer, Integer>(4, 4);
	// Pair<Integer, Integer> q = new Pair<Integer, Integer>(4, 4);
	// logger.info(p.hashCode());
	// logger.info(q.hashCode());
	// }
}