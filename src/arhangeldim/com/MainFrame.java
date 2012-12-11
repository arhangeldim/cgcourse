package arhangeldim.com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainFrame extends JFrame implements ComponentListener {

	private static final String CMD_CLEAR = "CMD_CLEAR";
	private static final String CMD_BEZIER = "CMD_BEZIER";
	private static final String CMD_BSPLAIN = "CMD_BSPLAIN";
	private static final String CMD_RUN = "CMD_RUN";

	private static final String DEFAULT_SPEED = "10";

	private DrawPanel canvas;
	private JPanel control;
	private ControlActionListener controlListener;

	private JButton runBtn;
	private JTextField speedFld;
	private int lastSpeed;

	private boolean isRunning = false;

	public MainFrame() {

		Container content = getContentPane();
		content.setLayout(new BorderLayout());

		controlListener = new ControlActionListener();

		canvas = new DrawPanel();
		content.add(canvas, BorderLayout.CENTER);

		control = new JPanel();
		JButton clrBtn = new JButton("Random");
		clrBtn.setActionCommand(CMD_CLEAR);
		clrBtn.addActionListener(controlListener);
		runBtn = new JButton("Run");
		runBtn.setActionCommand(CMD_RUN);
		runBtn.addActionListener(controlListener);

		JLabel speedLbl = new JLabel("SPEED: ");
		speedFld = new JTextField(DEFAULT_SPEED);
		speedFld.setColumns(2);
		speedFld.setEnabled(true);

		control.setLayout(new FlowLayout());
		control.add(clrBtn);
		control.add(runBtn);
		control.add(speedLbl);
		control.add(speedFld);
/*
		JRadioButton bezierRadioBtn = new JRadioButton("Bezier");
		bezierRadioBtn.setActionCommand(CMD_BEZIER);
		//bezierRadioBtn.setSelected(true);
		bezierRadioBtn.addActionListener(controlListener);

		JRadioButton bsplainRadioBtn = new JRadioButton("B-Splain");
		bsplainRadioBtn.setActionCommand(CMD_BSPLAIN);
		bsplainRadioBtn.setSelected(true);
		bsplainRadioBtn.addActionListener(controlListener);

		ButtonGroup group = new ButtonGroup();
		group.add(bezierRadioBtn);
		group.add(bsplainRadioBtn);
		control.add(bezierRadioBtn);
		control.add(bsplainRadioBtn);
*/

		content.add(control, BorderLayout.NORTH);
		setTitle("Bsplain");
		addComponentListener(this);

		pack();

	}

	private void setDefault() {
		runBtn.setText("Run");
		isRunning = false;
		canvas.stop();
		speedFld.setText(DEFAULT_SPEED);
		speedFld.setEnabled(true);
	}

	class ControlActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals(CMD_CLEAR)) {
				canvas.clear();
				setDefault();
			} else if (cmd.equals(CMD_BEZIER)) {
				canvas.setCurve(DrawPanel.CurveType.BEZIER);
				setDefault();
			} else if (cmd.equals(CMD_BSPLAIN)) {
				canvas.setCurve(DrawPanel.CurveType.BSPLAIN);
				setDefault();
			} else if (cmd.equals(CMD_RUN)) {
				if (isRunning) {
					// stop routine
					runBtn.setText("Run");
					isRunning = false;
					canvas.stop();
					//speedFld.setText(DEFAULT_SPEED);
					speedFld.setEnabled(true);
				} else {
					// start routine
					runBtn.setText("Stop");
					isRunning = true;
					speedFld.setEnabled(false);
					String speedStr = speedFld.getText();
					Pattern p = Pattern.compile("[0-9]{1,3}");
					Matcher m = p.matcher(speedStr);
					int speed = Integer.parseInt(speedStr);
					if (m.matches()) {
						lastSpeed = speed;
						canvas.run(speed);
					} else {
						canvas.run(Integer.parseInt(DEFAULT_SPEED));
						speedFld.setText(DEFAULT_SPEED);
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

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
