package arhangeldim.com;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class BSplainCurve extends Curve {

	private static final int ORDER = 4;
	
	protected List<Point2D.Double> divides = new ArrayList<Point2D.Double>();
	protected List<Point2D.Double> polyline = new ArrayList<Point2D.Double>();
	
	
	@Override
	public void drawCurve(Graphics2D g, Color c) {
		if (controlPoints.size() < 5)
			return;
		else {
			polyline.clear();
			polyline.add(controlPoints.get(0));
			polyline.add(controlPoints.get(1));
			for (int i = 1; i <= getDividePointsCount(ORDER, controlPoints.size()); i++)
				deBure(ORDER, i, buildKnots(ORDER, controlPoints.size()), g);
			polyline.add(controlPoints.get(controlPoints.size() - 2));
			polyline.add(controlPoints.get(controlPoints.size() - 1));
			parsePolyline(g);
			
		}
	}

	private int getDividePointsCount(int order, int pointsCount) {
		return pointsCount - order;
	}
	
	private int[] buildKnots(int order, int pointsCount) {
		int n = pointsCount - 1;
		int umax = n - order + 2;
		int length = umax + 1 + 2 * (order - 1);
		int[] knots = new int[length];
		for (int i = 0; i < order; i++) {
			knots[i] = 0;
			knots[length - 1 - i] = umax;
		}
		for (int i = order, j = 1; i < length - order; i++, j++) {
			knots[i] = j;
		}
		return knots;
	}
	
	public Point2D.Double deBure(int order, int u, int[] knots, Graphics2D g) {
		int l = 0;
		/* Search for right position in knot vector */
		for (int i = 0; i < knots.length; i++) {
			if (u == knots[i]) {
				l = i;
				break;
			}
		}
		Point2D.Double[] A = new Point2D.Double[order];
		for (int i = 0; i < order; i++) {
			Point2D.Double p2d = controlPoints.get(l - order + 1 + i);
			A[i] = new Point2D.Double(p2d.getX(), p2d.getY());
		}

		/* r < order */
		for (int r = 1; r < order - 1; r++) {
			for (int j = order - 1; j >= r; j--) {
				int i = l - order + j + 1;
				double d1 = u - knots[i];
				double d2 = knots[i + order - r] - u;
				A[j].x = (d1 * A[j].x + d2 * A[j - 1].x) / (d1 + d2);
				A[j].y = (d1 * A[j].y + d2 * A[j - 1].y) / (d1 + d2);
			}
			
			if (drawDividingPoints && g != null && r == 2) {
				g.setColor(Color.orange);
				for (int p = 1; p <= order - 1; p++) {
					polyline.add(A[p]);
				}
				Rectangle2D.Double r2d = new Rectangle2D.Double(A[order - 2].x - 2, A[order - 2].y - 2, 4, 4);
				g.fill(r2d);
				
			}
		}
		return new Point2D.Double(A[order - 1].getX(), A[order - 1].getY());
	}
	
	private void parsePolyline(Graphics2D g2d) {
		for (int i = 0; i <= getDividePointsCount(ORDER, controlPoints.size()); i++) {
			divides.clear();
			
			bezierDivider(
					polyline.get(0 + i * 3).x,
					polyline.get(0 + i * 3).y,
					polyline.get(1 + i * 3).x,
					polyline.get(1 + i * 3).y,
					polyline.get(2 + i * 3).x,
					polyline.get(2 + i * 3).y,
					polyline.get(3 + i * 3).x,
					polyline.get(3 + i * 3).y);
			drawCurveByPoints(g2d, divides, Color.pink);
			
		}
	}
	
	/* Build bezier curve by 4 points */
	public void recursiveBezier(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3, Point2D.Double p4) {
		recursiveBezier(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY(), p4.getX(), p4.getY());
	}

	private void recursiveBezier(double x1, double y1, double x2, double y2,
								 double x3, double y3, double x4, double y4) {
		double x12  = (x1 + x2) / 2;
		double y12  = (y1 + y2) / 2;
		double x23  = (x2 + x3) / 2;
		double y23  = (y2 + y3) / 2;
		double x34  = (x3 + x4) / 2;
		double y34  = (y3 + y4) / 2;
		double x123  = (x12 + x23) / 2;
		double y123  = (y12 + y23) / 2;
		double x234  = (x23 + x34) / 2;
		double y234  = (y23 + y34) / 2;
		double x1234 = (x123 + x234) / 2;
		double y1234 = (y123 + y234) / 2;

		double dx = x4 - x1;
		double dy = y4 - y1;

		double d2 = Math.abs(((x2 - x4) * dy - (y2 - y4) * dx));
		double d3 = Math.abs(((x3 - x4) * dy - (y3 - y4) * dx));

		if((d2 + d3)*(d2 + d3) < 0.25 * (dx*dx + dy*dy))
		{
			divides.add(new Point2D.Double(x1234, y1234));
			return;
		}

		recursiveBezier(x1, y1, x12, y12, x123, y123, x1234, y1234);
		recursiveBezier(x1234, y1234, x234, y234, x34, y34, x4, y4);

	}


	private void bezierDivider(double x1, double y1, double x2, double y2,
							   double x3, double y3, double x4, double y4) {
		divides.add(new Point2D.Double(x1, y1));
		recursiveBezier(x1, y1, x2, y2, x3, y3, x4, y4);
		divides.add(new Point2D.Double(x4, y4));
	}
}
