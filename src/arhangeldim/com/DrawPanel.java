package arhangeldim.com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener {

	public static final int WSIZE = 800;
	public static final int HSIZE = 600;

	public enum Curves {
		BEZIER,
		BSPLAIN,
	}

	private Curves curveType;

	private Curve curve;

	public static final double DELTA = 0.05;

	private BufferedImage buffer = null;
	private RenderingHints renderingHints;
	private ArrayList<Point2D.Double> points;

	private int markerIndex;

	private boolean isCaptured = false;

	private Thread drawingThread = null;

	private Point2D.Double[] kernelPoints = {
			new Point2D.Double(25, 73),
			new Point2D.Double(80, 101),
			new Point2D.Double(120, 20),
			new Point2D.Double(170, 80),
			new Point2D.Double(250, 100),
			new Point2D.Double(304, 30),
	};


	public DrawPanel() {
		setPreferredSize(new Dimension(WSIZE, HSIZE));
		setBackground(Color.WHITE);
		addMouseListener(this);
		addMouseMotionListener(this);
		Map<RenderingHints.Key, Object> hintsMap = new HashMap<RenderingHints.Key,Object>();
		hintsMap.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		hintsMap.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		hintsMap.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		hintsMap.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		renderingHints = new RenderingHints(hintsMap);
		points = new ArrayList<Point2D.Double>();
		curveType = Curves.BEZIER;
		curve = new BezierCurve();

	}

	class DrawingThread extends Thread {

		private int speed;

		public DrawingThread(int speed) {
			this.speed = speed;
		}

		@Override
		public void run() {
			try {
				while (!Thread.interrupted()) {
					curve.updateControlPointsPosition(WSIZE, HSIZE);
					repaint();
					Thread.sleep(1000 / speed);
				}
			} catch (InterruptedException e) {
				System.out.println("Interrupted: " + e.getMessage());
			} finally {
				interrupt();
			}
		}
	}

	public void run(int speed) {
		if (curve != null) {
			drawingThread = new DrawingThread(speed);
			drawingThread.start();
		}
	}

	public void stop() {
		if (drawingThread != null)
			drawingThread.interrupt();
	}

	public void setCurve(Curves c) {
		curveType = c;
		System.out.println("Set Curve: " + c);
		if (c == Curves.BSPLAIN) {
			curve = new BSplainCurve();

		} else if (c == Curves.BEZIER) {
			curve = new BezierCurve();
		}
		clear();
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (buffer == null) {
			rebuildBuffer();
		}
		Graphics2D g2d = buffer.createGraphics();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, WSIZE, HSIZE);
		g2d.addRenderingHints(renderingHints);

		curve.updateCurve(g2d);
		g.drawImage(buffer, 0, 0, this);
   	}

	public void clear() {
		if (buffer == null) {
			rebuildBuffer();
		}
		Graphics2D g2d = buffer.createGraphics();
		g2d.setColor(Color.white);
		g2d.fillRect(0, 0, WSIZE, HSIZE);
		if (curveType == Curves.BEZIER)
			curve = new BezierCurve();
		else
			curve = new BSplainCurve();
		repaint();
	}

	private void rebuildBuffer() {
		buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
	}


	/*
	private void drawScene() {
		if (points.size() > 1) {
			for (int i = 0; i < points.size() - 1; i++) {
				drawLine(points.get(i), points.get(i + 1));
			}
		}
		for (Point p : points) {
			drawPointMarker(p);
		}

	}

	private void deepCopy(ArrayList<Point> dst, ArrayList<Point> src) {
		for (Point p : src) {
			dst.add(new Point(p));
		}
	}

	private void buildBezierCurve() {
		if (points.size() < 2)
			return;
		ArrayList<Point> bezier = new ArrayList<Point>();
		bezier.add(points.get(0));
		for (double t = DELTA; t <= 1; t += DELTA) {

			ArrayList<Point> tmp = new ArrayList<Point>();
			deepCopy(tmp, points);
			for (int i = 0; i < tmp.size(); i++) {
				for (int j = 0; j < (tmp.size() - i - 1); j++) {
					double x = tmp.get(j).getX() * (1 - t) + tmp.get(j + 1).getX() * t;
					double y = tmp.get(j).getY() * (1 - t) + tmp.get(j + 1).getY() * t;
					tmp.get(j).setLocation(x, y);
				}
			}
			bezier.add(tmp.get(0));
			//System.out.println("Bezier " + t + ": " + tmp.get(0).toString());
		}
		bezier.add(points.get(points.size() - 1));
		for (int i = 0; i < bezier.size() - 1; i++ ) {
			drawLine(bezier.get(i), bezier.get(i + 1), Color.green);
		}

	}

	private void drawLine(Point start, Point end, Color c) {
		Graphics2D g2d = buffer.createGraphics();
		g2d.addRenderingHints(renderingHints);
		g2d.setColor(c);
		Line2D.Double line = new Line2D.Double(start.x, start.y, end.x, end.y);
		g2d.draw(line);
	}

	private void drawLine(Point start, Point end) {
		drawLine(start, end, Color.gray);
	}

	private void drawPointMarker(Point p) {
		Rectangle2D.Double r = new Rectangle2D.Double(p.getX() - POINT_MARKER_WIDTH / 2, p.getY() - POINT_MARKER_HEIGHT / 2,
				POINT_MARKER_WIDTH, POINT_MARKER_HEIGHT);
		Graphics2D g2d = buffer.createGraphics();
		g2d.addRenderingHints(renderingHints);
		g2d.setColor(Color.red);
		g2d.fill(r);
	}

	public int[] buildKnots(int order, int pointsCount) {
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
		//int k = 3; // order
		//double u = 2.5; // param
		//int l = 4; // t[l] <= u <= t[l+1]
		//int[] knots = {0, 0, 0, 1, 2, 3, 4, 4, 4};

		//param = 2.5;
		int l = 0;
		if (param < 1) {
			l = 2;
		} else if (param < 2) {
			l = 3;
		} else if (param < 3) {
			l = 4;
		} else if (param <= 4) {
			l = 5;
		} else {
			assert (l <= 4) : "Undefined param " + param;
		}

		Point2D.Double[] A = new Point2D.Double[order];
		for (int i = 0; i < order; i++) {
			Point2D.Double p2d = kernelPoints[l - order + 1 + i];
			A[i] = new Point2D.Double(p2d.getX(), p2d.getY());
		}

		for (int r = 1; r < order; r++) {
			for (int j = order - 1; j >= r; j--) {
				int i = l - order + j + 1;
				double d1 = param - knots[i];
				double d2 = knots[i + order - r] - param;
				A[j].x = (d1 * A[j].x + d2 * A[j - 1].x) / (d1 + d2);
				A[j].y = (d1 * A[j].y + d2 * A[j - 1].y) / (d1 + d2);
				//System.out.println("Point [" + i + "]: " + x + ", " + y);
				//for (int s = 0; s < 3; s++)
				//	System.out.println(A[s]);
			}
		}
		System.out.println("Result: " + A[order - 1].getX() + ", " + A[order - 1].getY());
		return new Point2D.Double(A[order - 1].getX(), A[order - 1].getY());
	}


	private void drawBSplain() {

		double d = 0.5;
		double u = 0;
		int[] knots = {0, 0, 0, 1, 2, 3, 4, 4, 4};
		ArrayList<Point2D.Double> bs = new ArrayList<Point2D.Double>();
		for (u = 0; u <= 4; u += d) {
			points.add(deBure(3, u, knots));
		}

		drawPoints(points);
		//repaint();
	}

	private void drawPoints(ArrayList<Point> p) {
		if (p.size() > 1) {
			for (int i = 0; i < p.size() - 1; i++) {
				drawLine(p.get(i), p.get(i + 1));
			}
		}
		for (Point it : p) {
			drawPointMarker(it);
		}
	}
    */
	@Override
	public void mouseClicked(MouseEvent e) {
		Point2D.Double p = new Point2D.Double(e.getX(), e.getY());
		curve.addPoint(p);
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		double x = e.getX();
		double y = e.getY();
		curve.setCursorPosition(x, y);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		curve.setCaptured(false);
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (curve.isCaptured()) {
			int x = e.getX();
			int y = e.getY();
			if (x < 0)
				x = 0;
			if (y < 0)
				y = 0;
			if (x > WSIZE)
				x = WSIZE;
			if (y > HSIZE)
				y = HSIZE;
			curve.setCursorPosition(x, y);
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
}
