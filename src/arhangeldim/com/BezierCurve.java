package arhangeldim.com;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class BezierCurve extends Curve {

	public BezierCurve() {
		super();
	}

	private List<Point2D.Double> divides = new ArrayList<Point2D.Double>();

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

		// Продожить деление
		//----------------------
		recursiveBezier(x1, y1, x12, y12, x123, y123, x1234, y1234);
		recursiveBezier(x1234, y1234, x234, y234, x34, y34, x4, y4);

	}


	private void bezierDivider(double x1, double y1, double x2, double y2,
							   double x3, double y3, double x4, double y4) {
		divides.add(new Point2D.Double(x1, y1));
		recursiveBezier(x1, y1, x2, y2, x3, y3, x4, y4);
		divides.add(new Point2D.Double(x4, y4));
	}

	private List<Point2D.Double> buildBezierCurve() {
		List<Point2D.Double> interpolation = new ArrayList<Point2D.Double>();
		if (controlPoints.size() < 2)
			return interpolation;
		interpolation.add(new Point2D.Double(controlPoints.get(0).getX(), controlPoints.get(0).getY()));
		for (double t = DELTA; t < 1; t += DELTA) {
			List<Point2D.Double> tmp = new ArrayList<Point2D.Double>();
			copyPoints(tmp, controlPoints);
			for (int i = 0; i < tmp.size(); i++) {
				for (int j = 0; j < (tmp.size() - i - 1); j++) {
					double x = tmp.get(j).getX() * (1 - t) + tmp.get(j + 1).getX() * t;
					double y = tmp.get(j).getY() * (1 - t) + tmp.get(j + 1).getY() * t;
					tmp.get(j).setLocation(x, y);
				}
			}
			interpolation.add(tmp.get(0));
		}
		interpolation.add(controlPoints.get(controlPoints.size() - 1));
		return interpolation;
	}

	@Override
	public void drawCurve(Graphics2D g, Color c) {
		//drawCurveByPoints(g, buildBezierCurve(), c);

		bezierDivider(50, 50, 100, 300, 400, 500, 500, 50);
		drawCurveByPoints(g, divides, c);
	}
}
