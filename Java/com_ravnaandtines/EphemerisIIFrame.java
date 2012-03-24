package com_ravnaandtines;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

class E2WizBox extends JPanel implements ChangeListener {

    ////private final JSpinner scalebar;
    //private final JCheckBox nine;
    //private final JCheckBox yuthu;
    //private final JCheckBox variable;
    //private final JLabel tilted;
    //private final JSpinner stilt,  etilt,  wtilt;
    //private final JSpinner sday,  wday;
    private final JCheckBox uleria;
    private final JSpinner sharday;
    private final JSpinner sharhr;
    private final JSpinner twinday;
    private final JSpinner twinhr;
    private final JSpinner artiaday;
    private final JSpinner artiahr;

    public E2WizBox() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                
        JPanel p2 = new JPanel();
        p2.add(new JLabel("Dome size km"));
        final JSpinner scalebar = new JSpinner(new SpinnerNumberModel(20000, 20000, 250000, 500));
        scalebar.addChangeListener(this);
        p2.add(scalebar);
        add(p2);

        p2 = new JPanel();
        
        /*
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        nine = new JCheckBox("9/10.6 degree tilt", true);
        p2.add(nine);
        nine.addItemListener(new ItemListener() {

            public void itemStateChanged(final ItemEvent e) {
                E2Param.yuthu = yuthu.isSelected();
                if (nine.isSelected()) {
                    E2Param.reset();
                }
            }
        });

        yuthu = new JCheckBox("Overhead at Yuthuppa (3600km N of centre)", false);
        p2.add(yuthu);
        yuthu.addItemListener(new ItemListener() {

            public void itemStateChanged(final ItemEvent e) {
                E2Param.yuthu = yuthu.isSelected();
            }
        });
        tilted = new JLabel(tilt());
        p2.add(tilted);
        variable = new JCheckBox("Freehand control of tilt and day length", false);
        p2.add(variable);
        variable.addItemListener(new ItemListener() {

            public void itemStateChanged(final ItemEvent e) {
                E2Param.yuthu = yuthu.isSelected();
                E2Param.summerTilt = (Double)stilt.getValue();
                E2Param.winterTilt = (Double)wtilt.getValue();
                E2Param.equinoxTilt = (Double)etilt.getValue();
                E2Param.summerDay = ((Double) sday.getValue() - 12)/24.0;
                E2Param.winterDay = ((Double) wday.getValue() - 12)/24.0;
            }
        });
        */
        
        uleria = new JCheckBox("Uleria period 1/3 siderial day");
        p2.add(uleria);
        uleria.setSelected(false);
        uleria.addItemListener(new ItemListener() {

            public void itemStateChanged(final ItemEvent e) {
                E2Param.uleria = uleria.isSelected();
            }
        });
        add(p2);

        p2 = new JPanel();
        p2.setLayout(new GridBagLayout());
        final GridBagConstraints c = new GridBagConstraints();
        c.gridheight = c.gridwidth = 1;

        /*
        c.gridy = 0;
        stilt = AddSpinner(p2, c,
                "Summer tilt (deg)",
                new SpinnerNumberModel(9.0, 0.0, 90.0, 0.1));

        c.gridy = 1;
        etilt = AddSpinner(p2, c, 
               "Equinox tilt (deg)",
               new SpinnerNumberModel(0.0, -9.0, 10.6, 0.1));

        c.gridy = 2;
        wtilt = AddSpinner(p2, c, 
               "Winter tilt (deg)",
               new SpinnerNumberModel(-10.6, -90.0, 0.0, 0.1));

        c.gridy = 3;
        sday = AddSpinner(p2, c, 
               "Summer day length (hours/24)",
               new SpinnerNumberModel(14.4, 12.0, 24.0, 0.1));

        c.gridy = 4;
        wday = AddSpinner(p2, c, 
               "Winter day length (hours/24)",
               new SpinnerNumberModel(9.1, 0.0, 12.0, 0.1));
         */

        c.gridy = 5;
        sharday = addSpinner(p2, c, 
               "Shargash rises year 1 on day",
               new SpinnerNumberModel(1, 1, 28, 1));

        final String hour = "at hour (of 24)";
        c.gridy = 6;
        sharhr = addSpinner(p2, c, 
               hour,
               new SpinnerNumberModel(0.0, 0.0, 23.9, 0.1));

        c.gridy = 7;
        twinday = addSpinner(p2, c, 
               "Twinstar rises year 1 on day",
               new SpinnerNumberModel(1, 1, 6, 1));

        c.gridy = 8;
        twinhr = addSpinner(p2, c,
               hour,
               new SpinnerNumberModel(0.0, 0.0, 23.9, 0.1));


        c.gridy = 9;
        artiaday = addSpinner(p2, c, 
               "Artia rises year 1 on day",
               new SpinnerNumberModel(1, 1, 112, 1));

        c.gridy = 10;
        artiahr = addSpinner(p2, c,
               hour,
               new SpinnerNumberModel(0.0, 0.0, 23.9, 0.1));
        add(p2);
    }

    private JSpinner addSpinner(final JPanel p2, final GridBagConstraints c,
            final String caption, final SpinnerNumberModel model) {
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        p2.add(new JLabel(caption), c);
        final JSpinner tmp = new JSpinner(model);
        tmp.addChangeListener(this);

        c.gridx = 1;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.HORIZONTAL;
        p2.add(tmp, c);
        return tmp;
    }

    /*private String tilt() {
        final double size = 3600.0 / (Integer) scalebar.getValue();
        final double t = Math.asin(size) * 180.0 / Math.PI;
        final double t2 = 10.6 * t / 9;
        java.text.NumberFormat nf = new java.text.DecimalFormat("###.##");

        return nf.format(t) + "/" + nf.format(t2) + " degree tilt";
    }*/

    public void stateChanged(final ChangeEvent evt) {
        /*if (evt.getSource() == scalebar) {
            tilted.setText(tilt());
            E2Param.domeRadius = ((Integer) scalebar.getValue()) /1000.0;
            return;
        } else if (evt.getSource() == etilt) {
            //(int value, int visible, int minimum, int maximum)
            ((SpinnerNumberModel)stilt.getModel()).setMinimum((Double)etilt.getValue());
            ((SpinnerNumberModel)wtilt.getModel()).setMaximum((Double)etilt.getValue());
            if (variable.isSelected()) {
                E2Param.equinoxTilt = (Double)etilt.getValue();
            }
            return;
        } else if (evt.getSource() == stilt) {
            ((SpinnerNumberModel)etilt.getModel()).setMinimum((Double)wtilt.getValue());
            ((SpinnerNumberModel)etilt.getModel()).setMaximum((Double)stilt.getValue());
            if (variable.isSelected()) {
                E2Param.summerTilt = (Double) stilt.getValue();
            }
            return;
        } else if (evt.getSource() == wtilt) {
            ((SpinnerNumberModel)etilt.getModel()).setMinimum((Double)wtilt.getValue());
            ((SpinnerNumberModel)etilt.getModel()).setMaximum((Double)stilt.getValue());
            if (variable.isSelected()) {
                E2Param.winterTilt = (Double) wtilt.getValue();
            }
            return;
        } else if (evt.getSource() == sday) {
            if (variable.isSelected()) {
                E2Param.summerDay = ((Double) sday.getValue() - 12)/24.0;
            }
            return;
        } else if (evt.getSource() == wday) {
            if (variable.isSelected()) {
                E2Param.winterDay = ((Double) wday.getValue() - 12)/24.0;
            }
            return;
        } else */ if (evt.getSource() == sharday || evt.getSource() == sharhr) {
            E2Param.shargashRise = (Integer)sharday.getValue() + (Double) sharhr.getValue() / 24.0;
            return;
        } else if (evt.getSource() == twinday || evt.getSource() == twinhr) {
            E2Param.twinRise = (Integer)twinday.getValue() + (Double) twinhr.getValue() / 24.0;
            return;
        } else if (evt.getSource() == artiaday || evt.getSource() == artiahr) {
            E2Param.artiaRise = (Integer)artiaday.getValue() + (Double) artiahr.getValue() / 24.0;
            return;
        }
    }
}

public class EphemerisIIFrame extends JFrame {
    /**
     * Builds a frame with minimal decoration
     * @param title String to use as Frame title
     */
    public EphemerisIIFrame(final String title, final boolean master) {
        super(title);


        if (master) {
            final JMenuBar mb = new JMenuBar();
            final JMenu file = new JMenu("File");
            mb.add(file);
            file.addSeparator();
            final JMenuItem exitMI = new JMenuItem("Exit");
            exitMI.addActionListener(
                    new ActionListener() {

                        public void actionPerformed(final ActionEvent evt) {
                            System.exit(0);
                        }
                    });
            file.add(exitMI);
            setJMenuBar(mb);
        }
        setDefaultCloseOperation(master
                ? JFrame.EXIT_ON_CLOSE : JFrame.DO_NOTHING_ON_CLOSE);

    }
}

/* end of file basicFrame.java */

