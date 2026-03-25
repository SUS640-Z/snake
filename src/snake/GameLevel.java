package snake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class GameLevel extends JPanel {
	Image imagen;
	Font customFont;
	Clip clip;
	Timer blinkTimer;
	
	public GameLevel() {
		this.setPreferredSize(new Dimension(600, 600));
		playSound("/sound/init.wav");
		 setLayout(null);
		 //this.imagen = cargarImagen("/img/wallpaper.jpg");
	     this.customFont = cargarFuente("/fonts/depa.ttf", 50f);
         
		 JLabel init = new JLabel("Easy",JLabel.CENTER);
		 JLabel init2 = new JLabel("Medium",JLabel.CENTER);
		 JLabel init3 = new JLabel("Hard",JLabel.CENTER);
		 init.setForeground(new Color(32, 45, 27));
	     init.setFont(customFont);
	    init.setBounds(100,100,200,200);	
	     
	     init2.setForeground(new Color(32, 45, 27));
	     init2.setFont(customFont);
	     init2.setBounds(100,200,300,200);	  
	     
	     init3.setForeground(new Color(32, 45, 27));
	     init3.setFont(customFont);
	     init3.setBounds(100,300,300,200);	     
	     	
	     add(init); 
	     add(init2);
	     add(init3);
	     
	     
	}
	
	private Image cargarImagen(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url != null) {
            return new ImageIcon(url).getImage();
        } else {
            System.err.println("Error: No se encontró la imagen en " + ruta);
            return null;
        }
    }

    private Font cargarFuente(String ruta, float tamaño) {
        try {
            InputStream is = getClass().getResourceAsStream(ruta);
            if (is != null) {
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
                // Usamos .deriveFont con un float para definir el tamaño
                return baseFont.deriveFont(Font.BOLD, tamaño);
                
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la fuente: " + e.getMessage());
        }
        // Retorna una fuente de respaldo si algo sale mal
        return new Font("Arial", Font.BOLD, (int) tamaño);
    }
	
	protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagen != null) {
            // Esto hace que la imagen se estire al tamaño actual del panel
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }
	
	public void playSound(String soundFileName) {
	    try {
	        URL url = this.getClass().getResource(soundFileName);
	        AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
	        clip = AudioSystem.getClip();
	        clip.open(audioIn);
	        clip.start();
	    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
	        e.printStackTrace();
	    }
	}
}