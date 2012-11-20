package arhangeldim.com;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class BSplainCurve extends Curve {

	private static final int ORDER = 3;

	@Override
	public void drawCurve(Graphics2D g, Color c) {
		if (controlPoints.size() < 3)
			return;
		drawCurveByPoints(g, buildBSplain(), c);
	}

	private int maxParam(int order, int pointsCount) {
		return pointsCount - order + 1;
	}

	private int[] buildKnots(int order, int pointsCount) {
		int n = pointsCount - 1;
		int umax = n - order + 2;
		int length = umax + 1 + 2 * (order - 1);
		System.out.println("Length = " + length);
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

	public Point2D.Double deBure(int order, double param, int[] knots) {

		int l = (int) Math.round(param + 1.5);

		Point2D.Double[] A = new Point2D.Double[order];
		for (int i = 0; i < order; i++) {
			Point2D.Double p2d = controlPoints.get(l - order + 1 + i);
			A[i] = new Point2D.Double(p2d.getX(), p2d.getY());
		}

		for (int r = 1; r < order; r++) {
			for (int j = order - 1; j >= r; j--) {
				int i = l - order + j + 1;
				double d1 = param - knots[i];
				double d2 = knots[i + order - r] - param;
				A[j].x = (d1 * A[j].x + d2 * A[j - 1].x) / (d1 + d2);
				A[j].y = (d1 * A[j].y + d2 * A[j - 1].y) / (d1 + d2);
			}
		}
		return new Point2D.Double(A[order - 1].getX(), A[order - 1].getY());
	}


	private List<Point2D.Double> buildBSplain() {

		int[] knots = buildKnots(ORDER, controlPoints.size());
		List<Point2D.Double> interpolate = new ArrayList<Point2D.Double>();

		for (double u = 0; u <= maxParam(ORDER, controlPoints.size()); u += DELTA) {
			interpolate.add(deBure(ORDER, u, knots));
		}
		return interpolate;
	}
}
