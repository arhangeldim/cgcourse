package arhangeldim.com;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Curve {

	protected List<Point2D.Double> controlPoints;
	protected List<Velocity> velocities;

	protected final static double DELTA = 0.05;
	protected final static int VELOCITY_LIMIT = 10;

	protected boolean isCaptured;
	protected int markerIndex;

	public static final int POINT_MARKER_WIDTH = 8;
	public static final int POINT_MARKER_HEIGHT = 8;

	public Curve() {
		controlPoints = new ArrayList<Point2D.Double>();
	}

	private Velocity generateVelocity() {
		Random r = new Random();
		int dx = r.nextInt(VELOCITY_LIMIT);
		if (r.nextBoolean())
			dx *= -1;
		int dy = r.nextInt(VELOCITY_LIMIT);
		if (r.nextBoolean())
			dy *= -1;
		return new Velocity(dx, dy);
	}

	public void updateControlPointsPosition(int canvasWidth, int canvasHeight) {
		if (velocities == null) {
			velocities = new ArrayList<Velocity>(controlPoints.size());
			for (int i = 0; i < controlPoints.size(); i++) {

				velocities.add(i, generateVelocity());
			}
		} else if (velocities.size() < controlPoints.size()) {
			for (int i = velocities.size(); i < controlPoints.size(); i++)
				velocities.add(i, generateVelocity());
		}

		// If points was added after running, we check only old points
		for (int i = 0; i < velocities.size(); i++) {
			double x = controlPoints.get(i).getX();
			double y = controlPoints.get(i).getY();
			int dx = velocities.get(i).getDx();
			int dy = velocities.get(i).getDy();
			if (x + dx > canvasWidth) {
				x = 2 * canvasWidth - (x + dx);
				dx *= -1;
			} else if (x + dx < 0) {
				x = Math.abs(x - dx);
				dx *= -1;
			} else {
				x += dx;
			}
			if (y + dy > canvasHeight) {
				y = 2 * canvasHeight - (y + dy);
				dy *= -1;
			} else if (y + dy < 0) {
				y = Math.abs(y - dy);
				dy *= -1;
			} else {
				y += dy;
			}
			controlPoints.get(i).setLocation(x, y);
			velocities.get(i).setVelocity(dx, dy);
		}
	}

	protected void copyPoints(List<Point2D.Double> dst, List<Point2D.Double> src) {
		assert (dst.isEmpty()) : "Destination list is not empty.";
		for (Point2D.Double p : src)
			dst.add(new Point2D.Double(p.getX(), p.getY()));
	}

	protected void drawCurveByPoints(Graphics2D g, List<Point2D.Double> points, Color c) {
		if (points.size() < 2)
			return;

		g.setColor(c);
		for (int i = 0; i < points.size() - 1; i++)
			g.draw(new Line2D.Double(points.get(i), points.get(i + 1)));
	}

	protected void addPoint(Point2D.Double p) {
		controlPoints.add(p);
	}

	public void drawMarkers(Graphics2D g, Color c) {
		for (Point2D.Double p : controlPoints) {
			Rectangle2D.Double r = new Rectangle2D.Double(p.getX() - POINT_MARKER_WIDTH / 2, p.getY() - POINT_MARKER_HEIGHT / 2,
					POINT_MARKER_WIDTH, POINT_MARKER_HEIGHT);
			g.setColor(c);
			g.fill(r);
		}
	}

	public void drawBounds(Graphics2D g, Color c) {
		if (controlPoints.size() < 2)
			return;
		for (int i = 0; i < controlPoints.size() - 1; i++) {
			g.setColor(c);
			g.draw(new Line2D.Double(controlPoints.get(i), controlPoints.get(i + 1)));
		}
	}

	public void updateCurve(Graphics2D g)  {
		drawBounds(g, Color.gray);
		drawCurve(g, Color.green);
		drawMarkers(g, Color.red);
	}

	public boolean isCaptured() {
		return isCaptured;
	}

	public void setCaptured(boolean c) {
		isCaptured = c;
	}

	public void setCursorPosition(double x, double y) {
		if (isCaptured()) {
			controlPoints.get(markerIndex).setLocation(x, y);
		} else {
			for (Point2D.Double p : controlPoints) {
				if (Math.abs(p.getX() - x) <= POINT_MARKER_WIDTH / 2 && Math.abs(p.getY() - y) <= POINT_MARKER_HEIGHT / 2) {
					// Capture this node
					markerIndex = controlPoints.lastIndexOf(p);
					isCaptured = true;
					System.out.println("Capture point index = " + markerIndex);
				}
			}
		}
	}

	public abstract void drawCurve(Graphics2D g, Color c);



	class Velocity {

		int dx, dy;

		public Velocity(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}

		public int getDx() {
			return dx;
		}

		public void setDx(int dx) {
			this.dx = dx;
		}

		public int getDy() {
			return dy;
		}

		public void setDy(int dy) {
			this.dy = dy;
		}

		public void setVelocity(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}
	}

}
