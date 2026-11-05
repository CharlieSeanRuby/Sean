/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package schooleventregistration;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Sean Mino
 */
public class Sub {
    public void setImagetoLabel(JLabel label, String imagePath){
        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage();
        
        int labelWidth = label.getWidth();
        int labelHeight = label.getHeight();
        
        int iconWidth = icon.getIconWidth();
        int iconHeight = icon.getIconHeight();
        
        double scale = Math.min((double) labelWidth / iconWidth, (double) labelHeight / iconHeight);
        
        int newWidth = (int) (iconWidth * scale);
        int newHeight = (int) (iconHeight * scale);
        
        Image resizeImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        
        label.setIcon(new ImageIcon (resizeImage));

        
    }
}