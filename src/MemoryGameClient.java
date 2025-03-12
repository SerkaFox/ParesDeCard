import javax.swing.*;
import java.io.*;
import java.net.*;

public class MemoryGameClient extends MemoryGame {
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private int jugadorID = -1;  // 🔹 Se inicializa en -1 para detectar errores
    private boolean esMiTurno = false;

    public MemoryGameClient() {
        super();
        setTitle("Juego de Memoria - Conectando...");
        setVisible(true);

        new Thread(this::conectarServidor).start();
    }

    private void conectarServidor() {
        try {
            socket = new Socket("192.168.1.54", 5000);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                String mensajeServidor = input.readLine();
                if (mensajeServidor == null) break;

                System.out.println("📡 Mensaje de servidor: " + mensajeServidor);

                SwingUtilities.invokeLater(() -> procesarMensaje(mensajeServidor));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void procesarMensaje(String mensajeServidor) {
        if (mensajeServidor.startsWith("Eres el jugador")) {
            String[] partes = mensajeServidor.split(" ");
            jugadorID = Integer.parseInt(partes[3]);
            System.out.println("🎮 Asignado jugadorID: " + jugadorID);
            setTitle("Juego de Memoria - Jugador " + jugadorID);
            actualizarEstadoJuego("Esperando al segundo jugador...");
        }
        else if (mensajeServidor.equals("ESPERA")) {
            actualizarEstadoJuego("Esperando al segundo jugador...");
        }
        else if (mensajeServidor.equals("INICIO")) {
            actualizarEstadoJuego("Turno del Jugador 1");
        }
        else if (mensajeServidor.startsWith("TURNO")) {
            if (jugadorID == -1) {
                System.err.println(" ERROR: Se recibió TURNO antes de asignar jugadorID.");
                return;
            }
            int turno = Integer.parseInt(mensajeServidor.split(",")[1]);
            esMiTurno = (turno == jugadorID);
            System.out.println("🟢 Turno del jugador: " + jugadorID + " Estado: " + esMiTurno);
            setTitle("Juego de Memoria - Turno del Jugador " + turno);
            actualizarEstadoJuego("Turno del Jugador " + turno);
        }
        else if (mensajeServidor.startsWith("TURN")) {
            String[] partes = mensajeServidor.split(",");
            int fila = Integer.parseInt(partes[1]);
            int columna = Integer.parseInt(partes[2]);
            int valor = Integer.parseInt(partes[3]);
            botones[fila][columna].setText(String.valueOf(valor));
        }
        else if (mensajeServidor.startsWith("PAREJA")) {
            String[] partes = mensajeServidor.split(",");
            int fila1 = Integer.parseInt(partes[1]);
            int col1 = Integer.parseInt(partes[2]);
            int fila2 = Integer.parseInt(partes[3]);
            int col2 = Integer.parseInt(partes[4]);
            botones[fila1][col1].setEnabled(false);
            botones[fila2][col2].setEnabled(false);
        }
        else if (mensajeServidor.startsWith("PUNTOS")) {
            String[] partes = mensajeServidor.split(",");
            int puntosJ1 = Integer.parseInt(partes[1]);
            int puntosJ2 = Integer.parseInt(partes[2]);
            actualizarPuntos(puntosJ1, puntosJ2);
        }
        else if (mensajeServidor.startsWith("RESET")) {
            String[] partes = mensajeServidor.split(",");
            int fila1 = Integer.parseInt(partes[1]);
            int col1 = Integer.parseInt(partes[2]);
            int fila2 = Integer.parseInt(partes[3]);
            int col2 = Integer.parseInt(partes[4]);

            Timer timer = new Timer(1000, e -> {
                botones[fila1][col1].setText("?");
                botones[fila2][col2].setText("?");
            });
            timer.setRepeats(false);
            timer.start();
        }
        else if (mensajeServidor.startsWith("FIN")) {
            JOptionPane.showMessageDialog(this, mensajeServidor.split(",")[1], "Fin del juego", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
        else {
            System.err.println("Respuesta desconocida del servidor: " + mensajeServidor);
        }
    }

    @Override
    protected void manejarClic(int fila, int columna) {
        if (esMiTurno && botones[fila][columna].getText().equals("?")) {
            System.out.println("🎮 Enviando movimiento: " + fila + "," + columna);
            output.println(fila + "," + columna);
        }
    }

    private void actualizarEstadoJuego(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            if (super.labelTurno != null) {
                super.labelTurno.setText(mensaje);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryGameClient::new);
    }
}
