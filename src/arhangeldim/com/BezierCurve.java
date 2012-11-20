package arhangeldim.com;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class BezierCurve extends Curve {

	public BezierCurve() {
		super();
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
		drawCurveByPoints(g, buildBezierCurve(), c);
	}
}
