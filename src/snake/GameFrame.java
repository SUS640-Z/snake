package snake;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class GameFrame extends JFrame {

    GameStar panel;

    public GameFrame() {
        panel = new GameStar();

        add(panel);
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        //setBounds(0,0,620,620);
		setResizable(false);
		setLocationRelativeTo(null);
		
		JMenuBar menuBar = new JMenuBar();
        JMenu menuOpciones = new JMenu("Menu");
        JMenuItem dificultad1 = new JMenuItem("Facil");
        JMenuItem dificultad2 = new JMenuItem("Medio");
        	JMenuItem dificultad3 = new JMenuItem("Dificil");
        	menuOpciones.add(dificultad1);
        	menuOpciones.add(dificultad2);
        	menuOpciones.add(dificultad3);
        menuBar.add(menuOpciones);
        
        Color grisOscuro = new Color(45, 45, 45); 
        Color grisClaro = new Color(70, 70, 70);
        Color textoBlanco = Color.WHITE;
        
        menuBar.setBackground(grisOscuro);
        menuOpciones.setForeground(textoBlanco);
        
        
        dificultad1.addActionListener(e -> {
            Object panelActual = getContentPane().getComponent(0);
            if (panelActual instanceof GamePanel) {
                GamePanel snake = (GamePanel) panelActual;
                snake.reiniciarJuego(400); 
                snake.requestFocusInWindow();
            }
        });
        
        dificultad2.addActionListener(e -> {
            Object panelActual = getContentPane().getComponent(0);
            if (panelActual instanceof GamePanel) {
                GamePanel snake = (GamePanel) panelActual;
                snake.reiniciarJuego(200); 
                snake.requestFocusInWindow();
            }
        });
        
        
        dificultad3.addActionListener(e -> {
            Object panelActual = getContentPane().getComponent(0);
            if (panelActual instanceof GamePanel) {
                GamePanel snake = (GamePanel) panelActual;
                snake.reiniciarJuego(40); 
                snake.requestFocusInWindow();
            }
        });
        
        dificultad1.setBackground(grisClaro);
        dificultad1.setForeground(textoBlanco);
        
        dificultad2.setBackground(grisClaro);
        dificultad2.setForeground(textoBlanco);
        
        dificultad3.setBackground(grisClaro);
        dificultad3.setForeground(textoBlanco);
        setJMenuBar(menuBar);
    }
}