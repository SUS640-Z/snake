package snake;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import javax.swing.Timer;

public class GameStar extends JPanel {
	Image imagen;
	Font customFont;
	Clip clip;
	Timer blinkTimer;
	
	public GameStar() {
		this.setPreferredSize(new Dimension(600, 600));
		playSound("/sound/init.wav");
		 setLayout(new BorderLayout());
		 this.imagen = cargarImagen("/img/wallpaper.jpg");
	     this.customFont = cargarFuente("/fonts/depa.ttf", 50f);
         
		 JLabel init = new JLabel("START",SwingConstants.CENTER);
		 init.setForeground(new Color(32, 45, 27));
	     init.setFont(customFont);
	     init.setBorder(new EmptyBorder(0, 0, 20, 0));
	     
	     blinkTimer = new Timer(300, new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                init.setVisible(!init.isVisible());
	            }
	        });
	     blinkTimer.start();

	     init.addMouseListener(new MouseAdapter() {
	            public void mouseClicked(MouseEvent e) {
	            		blinkTimer.stop();
	            		if (clip != null) clip.stop();
	            		JFrame ventana = (JFrame) SwingUtilities.getWindowAncestor(GameStar.this);

	                // 3. Quitamos este panel (el menú) y ponemos el del juego
	                ventana.getContentPane().removeAll(); // Limpiamos la ventana
	                ventana.add(new GamePanel());         // Añadimos el nuevo panel
	                
	                // 4. IMPORTANTE: Refrescar la ventana para que Swing dibuje el cambio
	                ventana.revalidate();
	                ventana.repaint();
	                
	                // 5. Darle el foco al nuevo panel (necesario para que funcionen las flechas del teclado)
	                ventana.getContentPane().getComponent(0).requestFocusInWindow();
	            }
	            
	            public void mouseEntered(MouseEvent e){
	            	init.setForeground(new Color(240, 240, 240));
	            	blinkTimer.stop();
	            	init.setVisible(true);
	            }
	    
	    		public void mouseExited(MouseEvent e){
	    			init.setForeground(new Color(32, 45, 27));
	    			blinkTimer.start();
	    			}
	        });
	     
	     	
	     	add(init,BorderLayout.SOUTH); 
	     
	     
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