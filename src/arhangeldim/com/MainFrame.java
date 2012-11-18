package arhangeldim.com;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

	JPanel canvas;

	public MainFrame() {
		Container content = getContentPane();
		content.setLayout(new BorderLayout());

		canvas = new DrawPanel();
		content.add(canvas, BorderLayout.CENTER);
		setTitle("Bezier Curves");
		pack();

	}

	public static void main(String[] args) {
		MainFrame mf = new MainFrame();
		mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mf.setVisible(true);
	}
}
