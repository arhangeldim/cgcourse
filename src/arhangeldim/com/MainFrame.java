package arhangeldim.com;

import com.sun.org.apache.xpath.internal.SourceTree;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainFrame extends JFrame {

	private static final String CMD_CLEAR = "CMD_CLEAR";
	private static final String CMD_BEZIER = "CMD_BEZIER";
	private static final String CMD_BSPLAIN = "CMD_BSPLAIN";
	private static final String CMD_RUN = "CMD_RUN";

	private DrawPanel canvas;
	private JPanel control;
	private ControlActionListener controlListener;

	private JButton runBtn;
	private JTextField speedFld;

	private boolean isRunning = false;

	public MainFrame() {

		Container content = getContentPane();
		content.setLayout(new BorderLayout());

		controlListener = new ControlActionListener();

		canvas = new DrawPanel();
		content.add(canvas, BorderLayout.CENTER);

		control = new JPanel();
		JButton clrBtn = new JButton("Clear");
		clrBtn.setActionCommand(CMD_CLEAR);
		clrBtn.addActionListener(controlListener);
		runBtn = new JButton("Run");
		runBtn.setActionCommand(CMD_RUN);
		runBtn.addActionListener(controlListener);

		speedFld = new JTextField("1");
		speedFld.setColumns(2);

		control.setLayout(new FlowLayout());
		control.add(clrBtn);
		control.add(runBtn);
		control.add(speedFld);

		JRadioButton bezierRadioBtn = new JRadioButton("Bezier");
		bezierRadioBtn.setActionCommand(CMD_BEZIER);
		bezierRadioBtn.setSelected(true);
		bezierRadioBtn.addActionListener(controlListener);

		JRadioButton bsplainRadioBtn = new JRadioButton("B-Splain");
		bsplainRadioBtn.setActionCommand(CMD_BSPLAIN);
		bsplainRadioBtn.addActionListener(controlListener);

		ButtonGroup group = new ButtonGroup();
		group.add(bezierRadioBtn);
		group.add(bsplainRadioBtn);
		control.add(bezierRadioBtn);
		control.add(bsplainRadioBtn);


		content.add(control, BorderLayout.NORTH);
		setTitle("Bezier Curves");
		pack();

	}

	class ControlActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals(CMD_CLEAR)) {
				canvas.clear();
			} else if (cmd.equals(CMD_BEZIER)) {
				canvas.setCurve(DrawPanel.Curves.BEZIER);
			} else if (cmd.equals(CMD_BSPLAIN)) {
				canvas.setCurve(DrawPanel.Curves.BSPLAIN);
			} else if (cmd.equals(CMD_RUN)) {
				if (isRunning) {
					runBtn.setText("Run");
					isRunning = false;
					canvas.stop();
					speedFld.setText("1");
				} else {
					runBtn.setText("Stop");
					isRunning = true;
					String speedStr = speedFld.getText();
					Pattern p = Pattern.compile("[0-9]{1,2}");
					Matcher m = p.matcher(speedStr);
					int speed = Integer.parseInt(speedStr);
					if (m.matches()) {
						canvas.run(speed);
					} else {
						canvas.run(1);
						speedFld.setText("1");
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		MainFrame mf = new MainFrame();
		mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mf.setVisible(true);

	}
}
