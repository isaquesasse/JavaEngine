import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class Game extends Canvas implements Runnable {

    public static JFrame frame;
    private Thread thread;
    private boolean isRunning = true;
    private final int WIDTH = 240, HEIGTH = 160, SCALE = 3;

    private BufferedImage image;

    private Spritesheet sheet;
    private BufferedImage[] player; //BufferedImage[] é para animar
    private double frames = 0, maxFrames = 10; //quanto menor o maxFrames = mais rápido
    private int curAnimation = 0, maxAnimation = 7; //maxAnimation é a quantidade de animações

    public Game() {
        sheet = new Spritesheet("/spritesheet.png");
        player = new BufferedImage[8]; // [2] é a quantidade de frames da animação
        player[0] = sheet.getSprite(0, 0, 32, 32);
        player[1] = sheet.getSprite(32, 0, 32, 32);
        player[2] = sheet.getSprite(64, 0, 32, 32);
        player[3] = sheet.getSprite(96, 0, 32, 32);
        player[4] = sheet.getSprite(128, 0, 32, 32);
        player[5] = sheet.getSprite(160, 0, 32, 32);
        player[6] = sheet.getSprite(192, 0, 32, 32);
        player[7] = sheet.getSprite(224, 0, 32, 32);
        setPreferredSize(new Dimension(WIDTH * SCALE, HEIGTH * SCALE));
        initFrame();
        image = new BufferedImage(WIDTH, HEIGTH, BufferedImage.TYPE_INT_RGB);
    }

    public void initFrame() {
        frame = new JFrame("Graphics");
        frame.add(this); //adiciona o this, que é o canvas. pega as propriedades das linhas de cima
        frame.setResizable(false); //não deixa redimencionar a janela
        frame.pack(); //necessário ficar após o canva para acionar algumas coisas
        frame.setLocationRelativeTo(null); //para a janela ficar no centro da tela
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //quando clicar para fechar, de fato fecha tudo
        frame.setVisible(true); //visiviel ao iniciar
    }

    public synchronized void start() {
        thread = new Thread(this); //this = mesma class, pois já está implementando o runnable
        isRunning = true;
        thread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }

    public void tick() { //tick = update do jogo
        frames++;
        if (frames > maxFrames) {
            frames = 0;
            curAnimation++;
            if (curAnimation > maxAnimation) {
                curAnimation = 0;
            }
        }
    }

    public void render() { //render = renderização do jogo
        BufferStrategy bs = this.getBufferStrategy(); //bufferStrategy é uma sequencia de buffers para otimizar a renderização
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = image.getGraphics();
        g.setColor(new Color(0, 200, 200));
        g.fillRect(0, 0, WIDTH, HEIGTH);

        // "Inicio" da renderização do jogo

        Graphics2D g2 = (Graphics2D) g; //objeto com gráfico 2D mas é igual varíavel G, que transforma em tipos de gráfico 2D. O nome disso ("(Graphics2D) g") é Cast
        g2.drawImage(player[curAnimation], 90, 90, null);
//        g2.setColor(new Color(0,0,0,100));
//        g2.fillRect(0,0,WIDTH,HEIGTH);
//        g2.rotate(Math.toRadians(0),90+8,90+8); //rotação "Math.toRadians(0" <-- número do angulo


        // "Fim" da renderização do jogo

        g.dispose(); //Para ajudar na otimização
        g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGTH * SCALE, null);
        bs.show();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime(); //pega o tempo em nanosegundos
        double amountOfTicks = 60.0; //frames por segundo
        double ns = 1000000000 / amountOfTicks; //dividindo 1 segundo em forma de nanosegundos pela quantidade de ticks
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                tick(); //sempre dar update antes de render
                render();
                frames++;
                delta--;
            }
            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }

        stop();
    }
}