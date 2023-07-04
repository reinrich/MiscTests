package rr.tools.timer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Timer{
        JButton button;
        public static void main(String[] args){
            try {
                int minutes = Integer.parseInt(args[0]);
                Thread.sleep(1000*60*minutes);
                JOptionPane.showMessageDialog(null, "Klingeling!!!!!!!!\nKlingeling!!!!!!!!\nKlingeling!!!!!!!!\nKlingeling!!!!!!!!\nKlingeling!!!!!!!!\nKlingeling!!!!!!!!\nKlingeling!!!!!!!!\nKlingeling!!!!!!!!\nKlingeling!!!!!!!!\nKlingeling!!!!!!!!\n", "Timer", 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
}