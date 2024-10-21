
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class Buscaminas extends JFrame {

    private int FILAS = 9;
    private int COLUMNAS = 9;
    private int NUM_MINAS = 5;
    private int nivel = 1;
    private final int[] FILAS_NIVELES = {10, 20, 20};
    private final int[] COLUMNAS_NIVELES = {10, 20, 20};
    private final int[] MINAS_NIVELES = {5, 20, 45};
    private final int[] ANCHOS_VENTANA = {450, 900, 1000};
    private final int[] ALTOS_VENTANA = {450, 900, 1000};

    private JButton[][] botones;
    private boolean[][] esMina;
    private boolean[][] estaMarcada;
    private int[][] contadorMinasAdyacentes;
    private JPanel PanelGame;
    private JPanel NavBar;
    private JLabel lblContadorBombas;
    private JButton btnDificultad;
    private int contadorMinas = NUM_MINAS;
    private JLabel lblTiempo;
    private Timer timer;
    private int tiempoSegundos;

    public Buscaminas() {
        botones = new JButton[FILAS][COLUMNAS];
        esMina = new boolean[FILAS][COLUMNAS];
        estaMarcada = new boolean[FILAS][COLUMNAS];
        contadorMinasAdyacentes = new int[FILAS][COLUMNAS];
        tiempoSegundos = 0;

        setTitle("Buscaminas");
        setSize(ANCHOS_VENTANA[0], ALTOS_VENTANA[0]);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        // Crear NavBar
        NavBar = new JPanel(new GridLayout(1, 3));
        lblContadorBombas = new JLabel("Minas: " + contadorMinas);
        btnDificultad = new JButton();
        btnDificultad.setText("Nivel: " + nivel);
        btnDificultad.addActionListener(e -> cambiarDificultad());
        lblTiempo = new JLabel("Tiempo: 0");

        NavBar.add(lblContadorBombas);
        NavBar.add(lblTiempo);
        NavBar.add(btnDificultad);
        getContentPane().add(NavBar, BorderLayout.NORTH);

        //Panel del Juego
        PanelGame = new JPanel(new GridLayout(FILAS, COLUMNAS));
        inicializarBotones();
        getContentPane().add(PanelGame, BorderLayout.CENTER);

        colocarMinas();
        contarMinasAdyacentes();

        iniciarTiempo();
    }

    private void cambiarDificultad() {
        nivel = (nivel % 3) + 1;
        FILAS = FILAS_NIVELES[nivel - 1];
        COLUMNAS = COLUMNAS_NIVELES[nivel - 1];
        NUM_MINAS = MINAS_NIVELES[nivel - 1];
        contadorMinas = NUM_MINAS;

        setSize(ANCHOS_VENTANA[nivel - 1], ALTOS_VENTANA[nivel - 1]);
        btnDificultad.setText("Nivel: " + nivel);

        getContentPane().remove(PanelGame);
        PanelGame = new JPanel(new GridLayout(FILAS, COLUMNAS));
        inicializarBotones();
        getContentPane().add(PanelGame, BorderLayout.CENTER);
        revalidate();
        repaint();

        reiniciarJuego();
    }

    private boolean verificarVictoria() {
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                if (esMina[fila][columna] && !estaMarcada[fila][columna]) {
                    return false; // Si hay una mina que no ha sido marcada, no has ganado.
                }
                if (!esMina[fila][columna] && (estaMarcada[fila][columna] || botones[fila][columna].isEnabled())) {
                    return false; // Si una celda no es mina y está marcada o no ha sido revelada, no has ganado.
                }
            }
        }
        return true;
    }


    private void inicializarBotones() {
        PanelGame.removeAll();
        botones = new JButton[FILAS][COLUMNAS];
        esMina = new boolean[FILAS][COLUMNAS];
        estaMarcada = new boolean[FILAS][COLUMNAS];
        contadorMinasAdyacentes = new int[FILAS][COLUMNAS];

        int buttonWidth = ANCHOS_VENTANA[nivel - 1] / COLUMNAS;
        int buttonHeight = (ALTOS_VENTANA[nivel - 1] - 50) / FILAS;

        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                JButton boton = new JButton("");
                boton.setBounds(columna * buttonWidth, fila * buttonHeight, buttonWidth, buttonHeight);

                // Ajustar el tamaño de la fuente
                float fontSize = Math.min(buttonWidth, buttonHeight) / 5f;
                boton.setFont(boton.getFont().deriveFont(fontSize));

                botones[fila][columna] = boton;
                PanelGame.add(boton);
                int filaFinal = fila;
                int columnaFinal = columna;

                boton.addActionListener(e -> revelarCelda(filaFinal, columnaFinal));
                boton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            marcarCelda(filaFinal, columnaFinal);
                        }
                    }
                });
            }
        }
    }

    private void colocarMinas() {
        Random random = new Random();
        int minasColocadas = 0;
        while (minasColocadas < NUM_MINAS) {
            int fila = random.nextInt(FILAS);
            int columna = random.nextInt(COLUMNAS);
            if (!esMina[fila][columna]) {
                esMina[fila][columna] = true;
                minasColocadas++;
            }
        }
    }

    private void contarMinasAdyacentes() {
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                if (!esMina[fila][columna]) {
                    int contador = 0;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int nuevaFila = fila + i;
                            int nuevaColumna = columna + j;
                            if (nuevaFila >= 0 && nuevaFila < FILAS && nuevaColumna >= 0 && nuevaColumna < COLUMNAS) {
                                if (esMina[nuevaFila][nuevaColumna]) {
                                    contador++;
                                }
                            }
                        }
                    }
                    contadorMinasAdyacentes[fila][columna] = contador;
                }
            }
        }
    }

    private void revelarCelda(int fila, int columna) {
        if (estaMarcada[fila][columna]) {
            return;
        }

        if (esMina[fila][columna]) {
            botones[fila][columna].setText("💣");
            botones[fila][columna].setBackground(Color.RED);
            JOptionPane.showMessageDialog(this, "¡Has perdido!");
            reiniciarJuego();
        } else {
            int minasAdyacentes = contadorMinasAdyacentes[fila][columna];
            botones[fila][columna].setText(minasAdyacentes == 0 ? "" : String.valueOf(minasAdyacentes));
            botones[fila][columna].setEnabled(false);
            if (minasAdyacentes == 0) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int nuevaFila = fila + i;
                        int nuevaColumna = columna + j;
                        if (nuevaFila >= 0 && nuevaFila < FILAS && nuevaColumna >= 0 && nuevaColumna < COLUMNAS) {
                            if (botones[nuevaFila][nuevaColumna].isEnabled()) {
                                revelarCelda(nuevaFila, nuevaColumna);
                            }
                        }
                    }
                }
            }

            if (verificarVictoria()) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "¡Has ganado!");
                reiniciarJuego();
            }
        }
    }


    private void iniciarTiempo() {
        timer = new Timer(1000, e -> {
            tiempoSegundos++;
            lblTiempo.setText("Tiempo: " + tiempoSegundos);
        });
        timer.start();
    }

    private void marcarCelda(int fila, int columna) {
        if (botones[fila][columna].isEnabled()) {
            if (estaMarcada[fila][columna]) {
                botones[fila][columna].setText("");
                estaMarcada[fila][columna] = false;
                contadorMinas++;
            } else {
                botones[fila][columna].setText("⚑");
                estaMarcada[fila][columna] = true;
                contadorMinas--;
            }
            lblContadorBombas.setText("Minas: " + contadorMinas);

            if (verificarVictoria()) {
                timer.stop(); // Detener el temporizador
                JOptionPane.showMessageDialog(this, "¡Has ganado!");
                reiniciarJuego();
            }
        }
    }

    private void reiniciarJuego() {
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                botones[fila][columna].setText("");
                botones[fila][columna].setEnabled(true);
                botones[fila][columna].setBackground(null);
                esMina[fila][columna] = false;
                estaMarcada[fila][columna] = false;
                contadorMinasAdyacentes[fila][columna] = 0;
            }
        }
        colocarMinas();
        contarMinasAdyacentes();
        tiempoSegundos = 0;
        lblTiempo.setText("Tiempo: 0");
        lblContadorBombas.setText("Minas: " + NUM_MINAS);
        timer.restart();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Buscaminas juego = new Buscaminas();
            juego.setVisible(true);
        });
    }
}
