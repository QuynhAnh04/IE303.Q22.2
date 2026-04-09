package org.example;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    // --- Bài 1: Cấu hình cửa sổ ---
    int boardWidth = 360;
    int boardHeight = 640;

    // Hình ảnh
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // --- Bài 2: Đối tượng Bird ---
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // --- Bài 3: Đối tượng Pipe ---
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Logic trò chơi
    Bird bird;
    int velocityX = -4; // Tốc độ ống di chuyển sang trái
    int velocityY = 0;  // Tốc độ nhảy của chim
    int gravity = 1;    // Trọng lực (Bài 2)

    ArrayList<Pipe> pipes;
    Timer gameLoop;
    Timer placePipesTimer;

    // --- Bài 4: Trạng thái Game ---
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Load hình ảnh
        try {
            backgroundImg = ImageIO.read(new File("flappybirdbg.png"));
            birdImg = ImageIO.read(new File("flappybird.png"));
            topPipeImg = ImageIO.read(new File("toppipe.png"));
            bottomPipeImg = ImageIO.read(new File("bottompipe.png"));
        } catch (IOException e) {
            System.out.println("Lỗi: Không tìm thấy file ảnh. Kiểm tra lại đường dẫn!");
            e.printStackTrace();
        }

        // Khởi tạo đối tượng
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        // Bài 3: Game Loop (60 FPS)
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

        // Timer xuất hiện ống (mỗi 1.5 giây)
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();
    }

    public void placePipes() {
        // Bài 3: Độ dài ngắn ngẫu nhiên của ống
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Bài 1: Vẽ nền
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        // Bài 2: Vẽ chim
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // Bài 3: Vẽ ống
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Bài 4: Hiển thị điểm / Game Over
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        if (gameOver) {
            g.drawString("GAME OVER: " + (int) score, 10, 35);
            g.drawString("Press 'R' to Restart", 10, 70);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        // Bài 2: Hiệu ứng rơi và di chuyển
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); // Giới hạn trần

        // Bài 3: Di chuyển ống
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            // Bài 4: Tính điểm
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // Mỗi cặp ống có 2 cái nên +0.5 x 2 = 1 điểm
            }

            // Bài 4: Kiểm tra va chạm (Game Over)
            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    // Cơ chế kiểm tra va chạm
    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Bài 2: Nhảy bằng Space/Enter
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
            velocityY = -9;
        }
        // Bài 4: Restart
        if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            bird.y = birdY;
            velocityY = 0;
            pipes.clear();
            score = 0;
            gameOver = false;
            gameLoop.start();
            placePipesTimer.start();
        }
    }

    // Các phương thức KeyListener không dùng đến
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(360, 640);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false); // Bài 1: Không thể thay đổi kích thước
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack(); // Đảm bảo kích thước chuẩn
        flappyBird.requestFocus();
        frame.setVisible(true);
    }
}