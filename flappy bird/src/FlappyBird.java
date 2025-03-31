import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardHeight = 640;

    // Imagini
    Image backgroundImage;
    Image birdImage;
    Image topPipeImage;
    Image bottomPipeImage;

    int pipeWidth = 64;
    int pipeHeight = 512;

    // Bird
    class Bird {
        int x, y, width, height;
        Image img;

        Bird(Image img, int startX, int startY) {
            this.x = startX;
            this.y = startY;
            this.width = 34;
            this.height = 24;
            this.img = img;
        }
    }

    // Pipe
    class Pipe {
        int x, y, width, height;
        Image img;
        boolean passed;

        Pipe(Image img, int startX, int startY) {
            this.x = startX;
            this.y = startY;
            this.width = pipeWidth;
            this.height = pipeHeight;
            this.img = img;
            this.passed = false;
        }
    }

    Bird bird;
    ArrayList<Pipe> pipes;
    Random rand = new Random();

    int velocityX = -3;
    int velocityY = 0;
    int gravity = 1;
    boolean gameOver = false;
    double score = 0;

    Timer gameLoop;
    Timer pipeTimer;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Încarcă imagini
        backgroundImage = new ImageIcon("src/flappybirdbg.png").getImage();
        birdImage = new ImageIcon("src/flappybird.png").getImage();
        topPipeImage = new ImageIcon("src/toppipe.png").getImage();
        bottomPipeImage = new ImageIcon("src/bottompipe.png").getImage();

        bird = new Bird(birdImage, boardWidth / 8, boardHeight / 2);
        pipes = new ArrayList<>();

        pipeTimer = new Timer(1600, e -> placePipe());
        pipeTimer.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void placePipe() {
        int minPipeHeight = 100;
        int maxPipeHeight = boardHeight / 2;
        int randPipeHeight = minPipeHeight + rand.nextInt(maxPipeHeight - minPipeHeight);

        int gap = 150; // distanța dintre țevi

        pipes.add(new Pipe(topPipeImage, boardWidth, randPipeHeight - pipeHeight)); // Țeava de sus
        pipes.add(new Pipe(bottomPipeImage, boardWidth, randPipeHeight + gap)); // Țeava de jos
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        if (gameOver) {
            g.drawString("Game Over! Score: " + (int) score, 50, 50);
        } else {
            g.drawString("Score: " + (int) score, 20, 40);
        }
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (Pipe pipe : pipes) {
            pipe.x += velocityX;
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }
            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            pipeTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -8;
            if (gameOver) {
                resetGame();
            }
        }
    }

    public void resetGame() {
        bird.y = boardHeight / 2;
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        score = 0;
        gameLoop.start();
        pipeTimer.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}
