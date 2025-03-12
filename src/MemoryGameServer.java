import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class MemoryGameServer {
    private static final int PORT = 5000;
    private static List<Socket> jugadores = new ArrayList<>();

    private static int[][] cartas;
    private static boolean[][] emparejadas;
    private static int jugadorActual = 1;
    private static int[] ultimaCarta = {-1, -1};
    public static int puntosJugador1 = 0;
    public static int puntosJugador2 = 0;

    // GUI
    private static JFrame frame;
    private static JLabel statusLabel;
    private static JTextArea logArea;

    public static void main(String[] args) {
        crearInterfazServidor();

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                log("Servidor iniciado. Esperando jugadores...");
                inicializarCartas();

                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        jugadores.add(socket);
                        log("Jugador " + jugadores.size() + " conectado.");
                        actualizarEstado("Jugadores conectados: " + jugadores.size() + "/2");

                        new Thread(new ClienteHandler(socket, jugadores.size())).start();

                        if (jugadores.size() == 1) {
                            enviarMensajeATodos("ESPERA");
                        } else if (jugadores.size() == 2) {
                            enviarMensajeATodos("INICIO");
                            enviarMensajeATodos("TURNO," + jugadorActual);
                            log("La partida ha comenzado! Turno del jugador " + jugadorActual);
                            actualizarEstado("La partida ha comenzado! Turno del jugador " + jugadorActual);
                        }
                    } catch (IOException e) {
                        log("⚠ Error aceptando conexión: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                log("⚠ Error crítico en el servidor: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }




    private static void inicializarCartas() {
        List<Integer> valores = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            valores.add(i);
            valores.add(i);
        }
        Collections.shuffle(valores);

        cartas = new int[6][5];
        emparejadas = new boolean[6][5];

        int index = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                cartas[i][j] = valores.get(index++);
                emparejadas[i][j] = false;
            }
        }
    }

    private static void enviarMensajeATodos(String mensaje) {
        for (Socket jugador : jugadores) {
            try {
                PrintWriter out = new PrintWriter(jugador.getOutputStream(), true);
                out.println(mensaje);
            } catch (IOException e) {
                log("Error enviando mensaje: " + e.getMessage());
            }
        }
    }

    private static class ClienteHandler implements Runnable {
        private Socket socket;
        private int jugador;

        public ClienteHandler(Socket socket, int jugador) {
            this.socket = socket;
            this.jugador = jugador;
        }

        @Override
        public void run() {
            try (
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true)
            ) {
                output.println("Eres el jugador " + jugador);

                while (true) {
                    String mensaje = input.readLine();
                    if (mensaje == null) {
                        System.out.println("Jugador " + jugador + " se ha desconectado.");
                        manejarDesconexion();
                        break;
                    }

                    String[] partes = mensaje.split(",");
                    int fila = Integer.parseInt(partes[0]);
                    int columna = Integer.parseInt(partes[1]);

                    if (jugador == jugadorActual && !emparejadas[fila][columna]) {
                        enviarMensajeATodos("TURN," + fila + "," + columna + "," + cartas[fila][columna]);

                        if (ultimaCarta[0] == -1) {
                            ultimaCarta[0] = fila;
                            ultimaCarta[1] = columna;
                        } else {
                            if (cartas[ultimaCarta[0]][ultimaCarta[1]] == cartas[fila][columna]) {
                                emparejadas[ultimaCarta[0]][ultimaCarta[1]] = true;
                                emparejadas[fila][columna] = true;
                                if (jugadorActual == 1) puntosJugador1++;
                                else puntosJugador2++;

                                enviarMensajeATodos("PAREJA," + ultimaCarta[0] + "," + ultimaCarta[1] + "," + fila + "," + columna);
                                enviarMensajeATodos("PUNTOS," + puntosJugador1 + "," + puntosJugador2);
                            } else {
                                enviarMensajeATodos("RESET," + ultimaCarta[0] + "," + ultimaCarta[1] + "," + fila + "," + columna);
                                cambiarTurno();
                            }
                            ultimaCarta[0] = -1;
                            ultimaCarta[1] = -1;
                        }

                        if (juegoTerminado()) {
                            String mensajeFinal;
                            if (puntosJugador1 > puntosJugador2) mensajeFinal = "FIN,Jugador 1 gana!";
                            else if (puntosJugador2 > puntosJugador1) mensajeFinal = "FIN,Jugador 2 gana!";
                            else mensajeFinal = "FIN,Empate!";
                            enviarMensajeATodos(mensajeFinal);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Jugador " + jugador + " perdió conexión.");
                manejarDesconexion();
            }
        }

        private void manejarDesconexion() {
            jugadores.remove(socket);
            if (jugadores.size() < 2) {
                System.out.println("Esperando nuevos jugadores...");
                enviarMensajeATodos("ESPERA");
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void cambiarTurno() {
            jugadorActual = (jugadorActual == 1) ? 2 : 1;
            enviarMensajeATodos("TURNO," + jugadorActual);
            actualizarEstado("Turno del jugador " + jugadorActual);
        }

        private boolean juegoTerminado() {
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    if (!emparejadas[i][j]) return false;
                }
            }
            return true;
        }
    }

    // === GUI ===
    private static void crearInterfazServidor() {
        frame = new JFrame("Servidor - Juego de Memoria");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        statusLabel = new JLabel("Servidor no iniciado", SwingConstants.CENTER);
        frame.add(statusLabel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton stopButton = new JButton("Detener servidor");
        stopButton.addActionListener(e -> System.exit(0));
        frame.add(stopButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    private static void actualizarEstado(String estado) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(estado));
    }
}
