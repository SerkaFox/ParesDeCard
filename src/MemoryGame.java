import javax.swing.*;
import java.awt.*;

public class MemoryGame extends JFrame {
    private final int filas = 6;
    private final int columnas = 5;
    public final JButton[][] botones = new JButton[filas][columnas];

    JLabel labelTurno;
    private JLabel labelPuntos;

    public MemoryGame() {
        setTitle("Juego de Memoria - 2 Jugadores");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelInfo = new JPanel();
        labelTurno = new JLabel("Esperando conexión...");
        labelPuntos = new JLabel("Puntos - Jugador 1: 0 | Jugador 2: 0");
        panelInfo.add(labelTurno);
        panelInfo.add(labelPuntos);
        add(panelInfo, BorderLayout.NORTH);

        JPanel panelJuego = new JPanel(new GridLayout(filas, columnas));
        add(panelJuego, BorderLayout.CENTER);

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                botones[i][j] = new JButton("?");
                botones[i][j].setFont(new Font("Arial", Font.BOLD, 20));
                int fila = i, columna = j;
                botones[i][j].addActionListener(e -> manejarClic(fila, columna));
                panelJuego.add(botones[i][j]);
            }
        }
    }

    protected void manejarClic(int fila, int columna) {
    }

    public void actualizarPuntos(int puntosJ1, int puntosJ2) {
        labelPuntos.setText("Puntos - Jugador 1: " + puntosJ1 + " | Jugador 2: " + puntosJ2);
    }

}
