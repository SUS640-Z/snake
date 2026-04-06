package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;



public class GamePanel extends JPanel {
	Random rand = new Random();

	// Dimensiones del área de juego
	static final int WIDTH = 600;
	static final int HEIGHT = 600;

	// Tamaño de cada celda del tablero (grid)
	static final int UNIT_SIZE = 25;

	// Timer que controla el ciclo del juego
	Timer timer;
	
	Clip clip;

	// Coordenadas de la cabeza de la serpiente
	int snakeX;
	int snakeY;

	// Dirección actual de movimiento de la serpiente
	// U = Up, D = Down, L = Left, R = Right
	char direction;

	// Lista enlazada que almacena todas las posiciones del cuerpo de la serpiente
	LinkedList<Point> snakeBody;
	
	//Puntuacion 
	
	//Manaza
	Point manzana;
	boolean manzanaTocada=true;
	int manzanasComidas;
	
	//Coordenadas Manzana
	int manzanaX=25;
	int manzanaY=25;
	
	//Platano
	Point banana;
	boolean bananaTocada=true;
	int bananaX=25;
	int bananaY=25;
	
	//Puntuacion
	int puntos;
	
	// Fuente
	Font customFont;
	
	
	//Recursos utilizados por el juego
	private Image appleImage;
	Image bananaImage;
	private Image snakeHeadImage;
	private Image snakeBodyImage;
	private BufferedImage gridImage;
	
	//Modificar la velocidad de la serpiente
	private int tiempo;

	boolean enJuego = true;

	boolean pausado = false;
	
	public GamePanel() {
		//setLayout(null);
		customFont= cargarFuente("/fonts/depa.ttf", 12f);
		appleImage = new ImageIcon(getClass().getResource("/img/aplee.png")).getImage();
		bananaImage=new ImageIcon(getClass().getResource("/img/banana.png")).getImage();
		snakeHeadImage = new ImageIcon(getClass().getResource("/img/cabeza3.png")).getImage();
		snakeBodyImage = new ImageIcon(getClass().getResource("/img/cuerpo2.png")).getImage();
		// Posición inicial de la cabeza de la serpiente
		snakeX = 250;
		snakeY = 100;

		// Dirección inicial de movimiento
		direction = 'R';

		// Crear la lista que almacenará el cuerpo de la serpiente
		snakeBody = new LinkedList<Point>();

		// Agregar los primeros segmentos de la serpiente
		snakeBody.add(new Point(250, 100));
		snakeBody.add(new Point(225, 100));
		snakeBody.add(new Point(200, 100));

		// Configurar tamaño del panel
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		// Color de fondo
		this.setBackground(Color.black);
		createRoundRectGrid();
		
		// Tiempo 
		tiempo=200;

		// Permitir que el panel reciba eventos de teclado
		this.setFocusable(true);

		// Evita que las teclas de navegación (como el TAB) cambien el foco entre componentes
		setFocusTraversalKeysEnabled(false);

		// Solicitar el foco cuando el panel ya esté visible
		SwingUtilities.invokeLater(() -> requestFocusInWindow());

		// Listener para detectar las teclas presionadas
		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
	                if (direction != 'R') direction = 'L'; 
	                break;

				case KeyEvent.VK_RIGHT:
					if (direction != 'L') direction = 'R'; 
					break;

				case KeyEvent.VK_UP:
					if (direction != 'D') direction = 'U'; 
					break;

				case KeyEvent.VK_DOWN:
					if (direction != 'U') direction = 'D'; 
					break;
					
				case KeyEvent.VK_P:
					if (enJuego) {
						pausado = !pausado;
						if (pausado) {
							timer.stop();
							playSound("/sound/pause.wav");
						} else {
							timer.start(); 
						}
						repaint(); 
					}
					break;

				case KeyEvent.VK_ENTER:
					if (!enJuego) {
						reiniciarJuego(200); 
					}
					break;
				}
			}
		});

		// Timer que ejecuta el ciclo del juego cada 150 ms
		timer = new Timer(tiempo, e -> {
			move();  // Actualiza la posición de la serpiente
			verificarColisiones(); 
			manzana();// Redibuja el panel (se ejecuta paintComponent)
			if(manzanasComidas>=5) {
				banana();
			}
			comida();
			repaint();
		});

		timer.start(); //Inicia el timer, para detenerlo se puede usar timer.stop();
		playSound("/sound/melody.wav");
		
		this.addFocusListener(new java.awt.event.FocusAdapter() {
		    @Override
		    public void focusLost(java.awt.event.FocusEvent e) {

		        if (enJuego && !pausado) {
		            pausado = true;
		            timer.stop(); 
		            repaint();    
		        }
		    }
		});
	}


	// Método encargado de dibujar los elementos del juego
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    
	    if (enJuego) {
		    /* 1. Fondo (Cuadrícula)
		    if (gridImage != null) {
		        g.drawImage(gridImage, 0, 0, null);
		    }
		    */
		    
		    g.setColor(new Color(40, 40, 40)); 
		    
		    for (int i = 0; i <= WIDTH; i += UNIT_SIZE) {
		        g.drawLine(i, 25, i, HEIGHT);
		    }
		    
		    // Dibujar líneas horizontales
		    for (int i = 25; i <= HEIGHT; i += UNIT_SIZE) {
		        g.drawLine(0, i, WIDTH, i);
		    }
	
		    // 2. Serpiente
		    for (int i = 0; i < snakeBody.size(); i++) {
		        int x = snakeBody.get(i).x;
		        int y = snakeBody.get(i).y;
	
		        if (i == 0) {
		            // CABEZA: Usamos la imagen específica de la cabeza
		            if (snakeHeadImage != null) {
		                g.drawImage(snakeHeadImage, x, y, UNIT_SIZE, UNIT_SIZE, this);
		            } else {
		                g.setColor(Color.YELLOW); // Fallback por si falla la imagen
		                g.fillRoundRect(x, y, UNIT_SIZE, UNIT_SIZE, 5, 5);
		            }
		        } else {
		            // CUERPO: Usamos la imagen del cuerpo
		            if (snakeBodyImage != null) {
		                g.drawImage(snakeBodyImage, x, y, UNIT_SIZE, UNIT_SIZE, this);
		            } else {
		                g.setColor(Color.GREEN); // Fallback
		                g.fillRoundRect(x, y, UNIT_SIZE, UNIT_SIZE, 5, 5);
		            }
		        }
		    }
		    
		    // 3. Manzana (Al final para que aparezca "sobre" el tablero)
		    if (appleImage != null) {
		        g.drawImage(appleImage, manzanaX, manzanaY, UNIT_SIZE, UNIT_SIZE, this);
		    }
		    
		    if(manzanasComidas>=5) {
			    if (appleImage != null) {
			        g.drawImage(bananaImage, bananaX, bananaY, UNIT_SIZE, UNIT_SIZE, this);
			    }
		    }
		    
		   customFont= cargarFuente("/fonts/depa.ttf", 15f);
		    g.setColor(Color.WHITE);
		    g.setFont(customFont);
		    g.drawString("Velocidad: " + (401-tiempo)+"km/h", 0, 15);
		    g.drawString("Puntuacion: " + puntos, 240, 15);
		    g.drawString("'P' para pausar", 445, 15);

		    if (pausado) {
		    		customFont= cargarFuente("/fonts/depa.ttf", 50f);
		        g.setColor(Color.WHITE);
		        g.setFont(customFont);
		        g.drawString("PAUSA", 210, 300);
		    }
		    
	    } else {
	    		customFont= cargarFuente("/fonts/depa.ttf", 60f);
		     g.setColor(Color.RED); // Define el color del texto
		     g.setFont(customFont); // Define fuente
		     g.drawString("GAME OVER", 120, 270);
		     
		     customFont= cargarFuente("/fonts/depa.ttf", 30f);
		     g.setColor(Color.WHITE);
		     g.setFont(customFont);
			 g.drawString("Puntuacion Final: " + puntos, 110, 320);
			 
			 customFont= cargarFuente("/fonts/depa.ttf", 20f);
			 g.setColor(Color.WHITE);
			 g.setFont(customFont);
			 g.drawString("Presiona ENTER para jugar de nuevo", 75, 380);
		    
	    }
	    
	}

	// Método que actualiza la posición de la serpiente
	public void move() {

		// Cambiar la posición de la cabeza dependiendo de la dirección
		switch (direction) {
		case 'U':
			snakeY -= UNIT_SIZE;
			break;

		case 'D':
			snakeY += UNIT_SIZE;
			break;

		case 'L':
			snakeX -= UNIT_SIZE;
			break;

		case 'R':
			snakeX += UNIT_SIZE;
			break;
		}

		// Agregar una nueva cabeza en la posición actual
		snakeBody.addFirst(new Point(snakeX, snakeY));

		// Eliminar el último elemento para mantener el mismo tamaño
		snakeBody.removeLast();
	}
	
	public void verificarColisiones() {
		//conn el mismo cuerpo 
		for(int i = 1; i < snakeBody.size(); i++) {
			if((snakeX == snakeBody.get(i).x) && (snakeY == snakeBody.get(i).y)) {
				playSound("/sound/muerte.wav");
				enJuego = false;
			}
		}
		//con las paredes
		if(snakeX < 0 || snakeX >= WIDTH) {
			playSound("/sound/muerte.wav");
			enJuego = false;
		}

		if(snakeY < 25 || snakeY >= HEIGHT) {
			playSound("/sound/muerte.wav");
			enJuego = false;
		}

		if(!enJuego) {
			timer.stop();
		}
	}
	
	public void reiniciarJuego(int tiempoDificultad) {
		snakeX = 250;
		snakeY = 100;
		direction = 'R';
		puntos = 0;
		tiempo = tiempoDificultad;
		
		snakeBody.clear();
		snakeBody.add(new Point(250, 100));
		snakeBody.add(new Point(225, 100));
		snakeBody.add(new Point(200, 100));
		
		manzanaTocada = true;
		bananaTocada=true;
		enJuego = true;
		pausado = false;
		manzanasComidas=0;
		timer.setDelay(tiempo);
		timer.start();
		repaint();
	}
	
	public void manzana() {
		boolean buenaPosicion=true;
			if(manzanaTocada) {
				do {
				buenaPosicion = true;
				int limiteX=WIDTH/25;
				int limiteY=WIDTH/25-1;
				manzanaX=rand.nextInt(limiteX);
				manzanaY=rand.nextInt(limiteY)+1;
				manzanaX=manzanaX*25;
				manzanaY=manzanaY*25;
				manzanaTocada=false;
				for (int i = 0; i < snakeBody.size(); i++) {
			        int x = snakeBody.get(i).x;
			        int y = snakeBody.get(i).y;
				    if(x == manzanaX && manzanaY==y) {
				    	 	buenaPosicion=false;
				    	 	break;
				    }
				}
				}while(!buenaPosicion);
			}
			manzana=new Point(manzanaX, manzanaY);
	}
	
	public void banana() {
	    boolean buenaPosicion = true;
	    if (bananaTocada) {
	        do {
	            buenaPosicion = true;
	            int limiteX = WIDTH / UNIT_SIZE; // Usa UNIT_SIZE en lugar de 25
	            int limiteY = (HEIGHT / UNIT_SIZE) - 1;
	            
	            bananaX = rand.nextInt(limiteX) * UNIT_SIZE;
	            bananaY = (rand.nextInt(limiteY) + 1) * UNIT_SIZE;
	            
	            // 1. Verificar que no aparezca sobre el cuerpo de la serpiente
	            for (Point p : snakeBody) {
	                if (p.x == bananaX && p.y == bananaY) {
	                    buenaPosicion = false;
	                    break;
	                }
	            }
	            
	            // 2. CORRECCIÓN: Verificar que no aparezca sobre la manzana
	            if (bananaX == manzanaX && bananaY == manzanaY) {
	                buenaPosicion = false;
	            }

	        } while (!buenaPosicion);
	        
	        bananaTocada = false; // Se marca como "ya colocada"
	        banana = new Point(bananaX, bananaY); // Actualizar el punto de la banana
	    }
	}
	
	public void comida() {
	    // Lógica de la Manzana
	    if(snakeX == manzanaX && snakeY == manzanaY) {
	        manzanaTocada = true;
	        puntos += 5;
	        manzanasComidas += 1;
	        tiempo-=1;
	        playSound("/sound/manzanaObtenida.wav");
			snakeBody.add(new Point(700,700));
			timer.setDelay(tiempo);
	        manzana(); // SOLO llamar cuando se come
	    }
	    
	    // Lógica de la Banana
	    // Solo checar colisión si la banana está activa (ej. manzanasComidas >= 5)
	    if(manzanasComidas >= 5) {
	        // Si la banana acaba de habilitarse y no tiene posición, la generamos una vez
	        if(bananaTocada) {
	            banana(); 
	        }

	        if(snakeX == bananaX && snakeY == bananaY) {
	            manzanasComidas = 0; // Se resetea el contador
	            bananaTocada = true; // Para que la próxima vez se genere en otro lugar
	            puntos += 10;
	            playSound("/sound/manzanaObtenida.wav");
				snakeBody.add(new Point(700,700));
				tiempo+=2;
				timer.setDelay(tiempo);
	            // IMPORTANTE: Mover la banana fuera del mapa o invalidarla
	            bananaX = -100; 
	            bananaY = -100;
	        }
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
	
	private void createRoundRectGrid() {
	    // Crea una imagen transparente del tamaño del panel
	    gridImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = gridImage.createGraphics();
	    
	    // Activar Anti-aliasing para que los bordes redondeados se vean suaves
	    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    
	    // Definir el color del borde (un gris muy oscuro y sutil)
	    g2d.setColor(new Color(40, 40, 40)); 

	    // Radio de la redondez (5-8 es un buen valor para celdas de 25)
	    int arc = 8; 
	    // Pequeño margen interno para que los cuadrados no se toquen totalmente
	    int padding = 2; 

	    // Dibujar la cuadrícula celda por celda
	    for (int x = 0; x < WIDTH; x += UNIT_SIZE) {
	        for (int y = 0; y < HEIGHT; y += UNIT_SIZE) {
	            // drawRoundRect(x, y, ancho, alto, arcAncho, arcAlto)
	            g2d.drawRoundRect(
	                x + padding, 
	                y + padding, 
	                UNIT_SIZE - (padding * 2), 
	                UNIT_SIZE - (padding * 2), 
	                arc, 
	                arc
	            );
	        }
	    }
	    g2d.dispose(); // Liberar recursos gráficos
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
	
	public void stopSound() {
        if (clip != null && clip.isRunning()) {
            clip.stop();  // Detiene la reproducción
            clip.close(); // Libera los recursos de memoria
        }
    }
	
}