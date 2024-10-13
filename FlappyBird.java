import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener{
    int screenWidth = 360;
    int screenHeight = 640;

    //Images
    Image backgroundImage;
    Image birdImage;
    Image bottomPipeImage;
    Image topPipeImage;

    //Bird
    int birdX = screenWidth/8;
    int birdY = screenHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird{
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;
        

        Bird(Image img){
            this.img = img;
        }
    }

    //Pipes
    int pipeX = screenWidth;
    int pipeY = 0;
    int pipeHeight = 512;
    int pipeWidth = 64;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;
        Pipe(Image img){
            this.img = img;
        }
    }

    //Game Logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;

    Timer gameLoop;
    Timer pipeyboys;

    boolean loss = false;

    double score = 0;
    

    FlappyBird(){
        //set background
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setFocusable(true);
        addKeyListener(this);

        //Load images
        backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        topPipeImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();

        //bird
        bird = new Bird(birdImage);

        //Pipes
        pipes = new ArrayList<Pipe>();
        Random random = new Random();


        //pipes timer
        pipeyboys = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        });
        pipeyboys.start();

        //Timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();




    }

    public void placePipes(){
        Pipe topPipe = new Pipe(topPipeImage);
        pipes.add(topPipe);
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*pipeHeight/2);
        topPipe.y = randomPipeY;

        int opening = (int) (screenHeight/4 - Math.random() * pipeHeight/6);
        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = topPipe.y + pipeHeight + opening;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        g.drawImage(backgroundImage, 0, 0, screenWidth, screenHeight, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
    
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        if (loss) {
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("GAME OVER!", screenWidth/2 - 110, screenHeight/2);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("[SPACE] to play again", screenWidth/2 - 140, screenHeight/2 + 100);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.setColor(Color.MAGENTA);
            g.drawString("SCORE: "+ (String.valueOf((int) score)), birdX, birdY + 50);
        }
        else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void movement(){
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            

            if (!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5;
            }
            if (collision(bird, pipe)) {
                loss = true;
            }
        }

        //Game Over
        if (bird.y > screenHeight) {
            loss = true;
            
        }

    }

    public boolean collision(Bird a, Pipe b){
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        movement();
        repaint();
        if (loss) {
            pipeyboys.stop();
            gameLoop.stop();       
        }
    }

   

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_SPACE){
            velocityY = -9;
            if (loss) {
                //Restart the game by resetting conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                loss = false;
                gameLoop.start();
                pipeyboys.start();
            }
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
        
    }
    @Override
    public void keyReleased(KeyEvent e) {
        
    }
}
