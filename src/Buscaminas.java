import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.GroupLayout.Alignment;
import net.miginfocom.swing.MigLayout;

public class Buscaminas extends JFrame {
    private final int FILAS = 10;  // Número de filas
    private final int COLUMNAS = 10;  // Número de columnas
    private final int NUM_MINAS = 5;  // Número de minas
    private JButton[][] botones;  // Matriz de botones
    private boolean[][] esMina;  // Matriz que indica si hay una mina en cada celda
    private boolean[][] estaMarcada;  // Matriz que indica si la celda está marcada con una bandera
    private int[][] contadorMinasAdyacentes;  // Matriz que almacena las minas adyacentes a cada celda
    
    private JPanel PanelGame;
    private JPanel NavBar;
    
    private int ancho = 400;
    private int largo = 400;

    private void AjustarTamaños() {
    	
    }
    
    public Buscaminas() {
        botones = new JButton[FILAS][COLUMNAS];
        esMina = new boolean[FILAS][COLUMNAS];
        estaMarcada = new boolean[FILAS][COLUMNAS];
        contadorMinasAdyacentes = new int[FILAS][COLUMNAS];

        setTitle("Buscaminas");
        setSize(ancho, largo);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
        
        NavBar = new JPanel();
        NavBar.setBounds(0, 0, 584, 50);
        getContentPane().add(NavBar);
        NavBar.setLayout(new GridLayout(0, 3, 0, 0));
        
        JPanel panelContador = new JPanel();
        NavBar.add(panelContador);
        
        JLabel lblContadorBombas = new JLabel("New label");
        panelContador.add(lblContadorBombas);
        
        JPanel panelBtn = new JPanel();
        NavBar.add(panelBtn);
        panelBtn.setLayout(new GridLayout(0, 3, 0, 0));
        
        JPanel panel = new JPanel();
        panelBtn.add(panel);
        
        JButton btnDificultad = new JButton("");
        btnDificultad.setIcon(new ImageIcon(Buscaminas.class.getResource("/img/LogoCara.png")));
        panelBtn.add(btnDificultad);
        
        JPanel panel_1 = new JPanel();
        panelBtn.add(panel_1);
        
        JPanel panelTiempo = new JPanel();
        NavBar.add(panelTiempo);
        
        JLabel lblTiempo = new JLabel("New label");
        panelTiempo.add(lblTiempo);
        
        PanelGame = new JPanel();
        PanelGame.setBounds(0, 49, 584, 511);
        getContentPane().add(PanelGame);
        PanelGame.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        inicializarBotones();
        colocarMinas();
        contarMinasAdyacentes();
    }

    // Inicializa los botones y añade listeners
    private void inicializarBotones() {
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                JButton boton = new JButton();
                botones[fila][columna] = boton;
                PanelGame.add(boton);

                int filaFinal = fila;
                int conlumnaFinal = columna;
                
                // Agregar acción al botón cuando es presionado (clic izquierdo)
                boton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        revelarCelda(filaFinal, conlumnaFinal);
                    }
                });

                // Agregar un MouseListener para detectar clic derecho
                boton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            marcarCelda(filaFinal, conlumnaFinal);
                        }
                    }
                });
            }
        }
    }

    // Coloca minas aleatoriamente en el tablero
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

    // Cuenta las minas adyacentes para cada celda
    private void contarMinasAdyacentes() {
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                if (!esMina[fila][columna]) {
                    int contador = 0;

                    // Revisar todas las celdas adyacentes
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

    // Revela la celda seleccionada
    private void revelarCelda(int fila, int columna) {
        if (estaMarcada[fila][columna]) {
            return; // Si está marcada, no puede ser revelada
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

            // Si la celda no tiene minas adyacentes, revela celdas adyacentes
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
        }
    }

    // Marca o desmarca una celda como posible mina
    private void marcarCelda(int fila, int columna) {
        if (botones[fila][columna].isEnabled()) {
            if (estaMarcada[fila][columna]) {
                // Desmarcar celda
                botones[fila][columna].setText("");
                estaMarcada[fila][columna] = false;
            } else {
                // Marcar celda
                botones[fila][columna].setText("⚑");
                estaMarcada[fila][columna] = true;
            }
        }
    }

    // Reinicia el juego
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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Buscaminas juego = new Buscaminas();
            juego.setVisible(true);
        });
    }
}
