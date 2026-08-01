package me.baldo3000.poool.view;

import me.baldo3000.poool.controller.GameEventListener;
import me.baldo3000.poool.model.utils.V2d;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ViewFrame extends JFrame {

    private static final Font SCORE_FONT =
            new Font(Font.SANS_SERIF, Font.PLAIN, 80);
    private static final Font LABEL_FONT =
            new Font(Font.SANS_SERIF, Font.BOLD, 15);
    private static final Font DEBUG_FONT =
            new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    private static final Stroke THIN_STROKE = new BasicStroke(1);
    private static final Stroke BALL_STROKE = new BasicStroke(2);

    private final ViewModel model;
    private final GameEventListener listener;
    private final VisualiserPanel panel;
    private boolean gameEnded = false;

    public ViewFrame(ViewModel model, GameEventListener listener, int w, int h) {
        this.model = model;
        this.listener = listener;

        setTitle("Poool");
        setSize(w, h + 25);
        setResizable(false);

        this.setFocusable(true);
        this.requestFocusInWindow();

        panel = new VisualiserPanel(w, h);
        getContentPane().add(panel);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                var input = Optional.ofNullable(switch (e.getKeyCode()) {
                    case KeyEvent.VK_W, KeyEvent.VK_UP -> new V2d(0, 1);
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN -> new V2d(0, -1);
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT -> new V2d(-1, 0);
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> new V2d(1, 0);
                    default -> null;
                });
                input.ifPresent(listener::notifyPlayerInput);
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(-1);
            }

            public void windowClosed(WindowEvent ev) {
                System.exit(-1);
            }
        });
    }

    public void render() {
        if (gameEnded) return;
        //long nf = sync.nextFrameToRender();
        //panel.repaint();
        checkWinner();
        try {
            SwingUtilities.invokeAndWait(() ->
                    panel.paintImmediately(0, 0, panel.getWidth(), panel.getHeight()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Rendering failed", e.getCause());
        }
//        try {
//            sync.waitForFrameRendered(nf);
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
    }

    private void checkWinner() {
        // We check if the balls are null (which happens in your Board logic)
        var pb = model.getPlayerBall();
        var cb = model.getCpuBall();
        var smallBalls = model.getBalls();

        if (pb == null) {
            showGameOverPopup("CPU WINS! The Player fell into a hole.");
        } else if (cb == null) {
            showGameOverPopup("PLAYER WINS! The CPU fell into a hole.");
        } else if (smallBalls.isEmpty()) {
            int pScore = model.getPlayerScore();
            int cScore = model.getCpuScore();

            if (pScore > cScore) {
                showGameOverPopup("PLAYER WINS! Final Score: " + pScore + " - " + cScore);
            } else if (cScore > pScore) {
                showGameOverPopup("CPU WINS! Final Score: " + cScore + " - " + pScore);
            } else {
                showGameOverPopup("TIE! Final Score: " + pScore + " - " + cScore);
            }
        }
    }

    private void showGameOverPopup(String message) {
        gameEnded = true;
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0); // This stops the entire application
        });
    }

    public class VisualiserPanel extends JPanel {
        private final int ox;
        private final int oy;
        private final int delta;

        public VisualiserPanel(int w, int h) {
            setSize(w, h + 25);
            ox = w / 2;
            oy = h / 2;
            delta = Math.min(ox, oy);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

            // Clear background
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Draw axis lines
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(THIN_STROKE);
            g2.drawLine(ox, 0, ox, oy * 2);
            g2.drawLine(0, oy, ox * 2, oy);

            // 1. DRAW HOLES
            g2.setColor(Color.BLACK);
            for (var h : model.getHoles()) {
                fillBall(g2, h);
            }

            // 2. DRAW POINTS
            g2.setColor(Color.BLUE);
            g2.setFont(SCORE_FONT);
            g2.drawString(String.valueOf(model.getPlayerScore()), ox - (int) (delta * 0.8), oy + (int) (delta * 0.4));
            g2.drawString(String.valueOf(model.getCpuScore()), ox + (int) (delta * 0.6), oy + (int) (delta * 0.4));

            // 3. DRAW SMALL BALLS
            g2.setColor(Color.BLACK);
            g2.setStroke(THIN_STROKE);
            for (var b : model.getBalls()) {
                drawBall(g2, b);
            }

            // 4. DRAW PLAYER BALLS
            g2.setStroke(BALL_STROKE);
            drawBallWithLabel(g2, model.getPlayerBall(), "P");
            drawBallWithLabel(g2, model.getCpuBall(), "C");

            // Debug Info
            g2.setFont(DEBUG_FONT);
            g2.setColor(Color.BLACK);
            g2.drawString("Num small balls: " + model.getBalls().size(), 20, 150);
            g2.drawString("Frame per sec: " + String.format("%.2f", model.getFramePerSec()), 20, 170);
        }

        // Helper to draw the outline
        private void drawBall(Graphics2D g2, BallViewInfo ball) {
            if (ball != null) {
                int[] coords = getBallCoords(ball);
                g2.drawOval(coords[0], coords[1], coords[2], coords[3]);
            }
        }

        private void fillBall(Graphics2D g2, BallViewInfo ball) {
            if (ball != null) {
                int[] coords = getBallCoords(ball);
                g2.fillOval(coords[0], coords[1], coords[2], coords[3]);
            }
        }

        private void drawBallWithLabel(Graphics2D g2, BallViewInfo ball, String label) {
            if (ball != null) {
                int[] coords = getBallCoords(ball);
                g2.drawOval(coords[0], coords[1], coords[2], coords[3]);

                g2.setFont(LABEL_FONT);
                FontMetrics fm = g2.getFontMetrics();
                int tx = coords[0] + (coords[2] - fm.stringWidth(label)) / 2;
                int ty = coords[1] + ((coords[3] - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(label, tx, ty);
            }
        }

        private int[] getBallCoords(BallViewInfo ball) {
            var p = ball.pos();
            int r = (int) (ball.radius() * delta);
            int x = (int) (ox + p.x() * delta) - r;
            int y = (int) (oy - p.y() * delta) - r;
            return new int[]{x, y, r * 2, r * 2};
        }
    }
}