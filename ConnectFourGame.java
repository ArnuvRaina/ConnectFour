import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ConnectFourGame extends JFrame {
    private JButton[][] buttons;
    private int[][] board;
    private int currentPlayer;

    public ConnectFourGame(int size) {
        setTitle("Connect Four");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(240, 11, 11));

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel dropPanel = new JPanel(new GridLayout(1, size));
        JPanel boardPanel = new JPanel(new GridLayout(size, size));

        buttons = new JButton[size][size];
        board = new int[size][size];
        currentPlayer = 1;

        for (int i = 0; i < size; i++) {
            JButton dropButton = new JButton("DROP");
            dropButton.setBorder(new LineBorder(Color.BLACK));
            dropButton.setBackground(new Color(255, 204, 102));
            dropButton.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            dropButton.addActionListener(new DropButtonListener(i));
            dropPanel.add(dropButton);
            dropPanel.setBorder(new EmptyBorder(30, 30, 10, 30));
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JButton button = new JButton();
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                button.setBackground(Color.WHITE);
                buttons[i][j] = button;
                boardPanel.add(button);
            }
        }

        boardPanel.setBorder(new EmptyBorder(10, 30, 10, 30));
        mainPanel.add(dropPanel, BorderLayout.NORTH);
        mainPanel.add(boardPanel, BorderLayout.CENTER);
        add(mainPanel);

        setVisible(true);
    }

    private class DropButtonListener implements ActionListener {
        private int col;
        private boolean animating;

        public DropButtonListener(int col) {
            this.col = col;
            this.animating = false;
        }

        public void actionPerformed(ActionEvent e) {
            if (animating) return;
            int row = dropToken(col);
            if (row != -1) {
                animating = true;
                new Thread(() -> {
                    dropAnimation(row, col);
                    SwingUtilities.invokeLater(() -> {
                        updateBoardAppearance();
                        repaint();
                        animating = false;
                    });
                }).start();

                if (checkWin(row, col)) {
                    JOptionPane.showMessageDialog(null, "Player " + currentPlayer + " wins!");
                    System.exit(0);
                }

                currentPlayer = currentPlayer == 1 ? 2 : 1;
            } else {
                JOptionPane.showMessageDialog(null, "Column is full. Choose another column.");
            }
        }
    }

    private void dropAnimation(int row, int col) {
        int player = currentPlayer == 1 ? 2 : 1;
        for (int r = 0; r <= row; r++) {
            buttons[r][col].setBackground(player == 1 ? Color.RED : Color.YELLOW);
            try {
                Thread.sleep(300);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (r > 0) {
                buttons[r - 1][col].setBackground(Color.WHITE);
            }
            if (r < row) {
                buttons[r][col].setBackground(Color.WHITE);
            }
        }
    }

    private int dropToken(int col) {
        for (int row = board.length - 1; row >= 0; row--) {
            if (board[row][col] == 0) {
                board[row][col] = currentPlayer;
                return row;
            }
        }
        return -1;
    }

    private boolean checkWin(int row, int col) {
        int player = board[row][col];
        for (int c = 0; c <= board[0].length - 4; c++) {
            if (board[row][c] == player && board[row][c+1] == player && board[row][c+2] == player && board[row][c+3] == player) {
                return true;
            }
        }
        for (int r = 0; r <= board.length - 4; r++) {
            if (board[r][col] == player && board[r+1][col] == player && board[r+2][col] == player && board[r+3][col] == player) {
                return true;
            }
        }
        for (int r = 0; r <= board.length - 4; r++) {
            for (int c = 0; c <= board[0].length - 4; c++) {
                if (board[r][c] == player && board[r+1][c+1] == player && board[r+2][c+2] == player && board[r+3][c+3] == player) {
                    return true;
                }
            }
        }
        for (int r = 0; r <= board.length - 4; r++) {
            for (int c = 3; c < board[0].length; c++) {
                if (board[r][c] == player && board[r+1][c-1] == player && board[r+2][c-2] == player && board[r+3][c-3] == player) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateBoardAppearance() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 1) {
                    buttons[i][j].setBackground(Color.RED);
                } else if (board[i][j] == 2) {
                    buttons[i][j].setBackground(Color.YELLOW);
                } else {
                    buttons[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }

    public static void main(String[] args) {
        int size = Integer.parseInt(JOptionPane.showInputDialog("Enter the size of the board (e.g., 5 for a 5x5 board):"));
        SwingUtilities.invokeLater(() -> new ConnectFourGame(size));
    }
}
