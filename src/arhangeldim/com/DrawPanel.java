package arhangeldim.com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class DrawPanel extends JPanel implements MouseListener, MouseMotionListener {

	public int WSIZE = 800;
	public int HSIZE = 600;

	public enum CurveType {
		BEZIER,
		BSPLAIN,
	}

	private CurveType curveType;
	private Curve curve;
	public static final double DELTA = 0.05;

	private BufferedImage buffer;
	private RenderingHints renderingHints;
	private Thread drawingThread;

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
		curveType = CurveType.BSPLAIN;
		curve = new BSplainCurve();
		curve.generateCurve(WSIZE, HSIZE);
	}
	
	public void setDimensionSize(Dimension d) {
		HSIZE = (int) d.getHeight();
		WSIZE = (int) d.getWidth();
		repaint();
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

	public void setCurve(CurveType c) {
		curveType = c;
		curve = new BSplainCurve();
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
		curve = new BSplainCurve();
		curve.generateCurve(WSIZE, HSIZE);
		repaint();
	}

	private void rebuildBuffer() {
		buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		Point2D.Double p = new Point2D.Double(e.getX(), e.getY());
		curve.addPoint(p);
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		double x = e.getX();
		double y = e.getY();
		curve.setCursorPosition(x, y);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
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
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
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
