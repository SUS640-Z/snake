package snake;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public class GameFrame extends JFrame {

    GameLevel panel;

    public GameFrame() {
        panel = new GameLevel();

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
		
		addWindowListener(new WindowAdapter() {
			public void windowsDesactived(WindowEvent e) {
				
			}
		
			
		});
    }
}