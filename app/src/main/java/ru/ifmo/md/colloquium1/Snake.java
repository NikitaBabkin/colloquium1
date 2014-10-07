package ru.ifmo.md.colloquium1;

/**
 * Created by Nikita on 07.10.14.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

class SnakeView extends SurfaceView implements Runnable {
    int [][] field = null;
    int [][] field2 = null;
    int [][] coord = new int[3000][2];

    int width = 40;
    int height = 60;
    float scaleW = (float)(1080 / width);
    float scaleH = (float)(1920 / height);

    int dir = 0; // direction of the snake: 0 - left, 1 - up; 2 - right; 3 - down;
    int chDir = 0; // change direction: 0 - cur, 1 - turn left, 2 - turn right;

    int hX = 0; // head position
    int hY = 0;
    int hL = 0; // length
    int tX = 0; // tail position
    int tY = 0;

    int score = 0;

    boolean fl = true; // flag of the end

    int[] colors = new int[height * width];
    int[] palette = {0xFF000000, 0xFFFF0000, 0xFF00FF00, 0xFFFFFFFF};
    //                 black        red         green       white

    SurfaceHolder holder;
    Thread thread = null;
    volatile boolean running = false;

    public SnakeView(Context context) {
        super(context);
        holder = getHolder();
    }

    public void resume() {
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException ignore) {}
    }

    public void run() {
        initField();
        Log.i("NICK: ", "EEE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1!!!!!!!!!!!!!!!!!!!!!!");

        while (running) {
            if (holder.getSurface().isValid()) {
                long startTime = System.nanoTime();
                Canvas canvas = holder.lockCanvas();
                canvas.scale(scaleW, scaleH);
                updateField();
                draw(canvas);
                holder.unlockCanvasAndPost(canvas);
                long finishTime = System.nanoTime();
                Log.i("TIME", "Circle: " + (finishTime - startTime) / 1000000);
                Log.i("FPS", "Circle: " + (double)1000 / (double)((finishTime - startTime) / 1000000));
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignore) {}
            }
        }
    }


    void initField() {
        field = new int[height][width];
        Random rand = new Random();

        int curX = 4 + rand.nextInt(width - 8);
        int curY = 4 + rand.nextInt(height - 8);

        hX = curX;
        hY = curY;
        tX = curX;
        tY = curY;

        hL = 3;

        dir = rand.nextInt(2);
        if (dir == 0) {
            field[curY][curX] = 2;      //vertical snake
            field[curY + 1][curX] = 2;
            field[curY + 2][curX] = 2;
            tY += 2;

            coord[0][0] = curX;
            coord[0][1] = curY;
            coord[1][0] = curX;
            coord[1][1] = curY + 1;
            coord[2][0] = curX;
            coord[2][1] = curY + 2;
        } else {
            field[curY][curX] = 2;
            field[curY][curX + 1] = 2;  //horizontal snake
            field[curY][curX + 2] = 2;
            tX += 2;

            coord[0][0] = curX;
            coord[0][1] = curY;
            coord[1][0] = curX + 1;
            coord[1][1] = curY;
            coord[2][0] = curX + 2;
            coord[2][1] = curY;
        }

        for (int i = 0; i < 200; i++) {
            while (field[curY][curX] == 1 || field[curY][curX] == 2) {
                curX = rand.nextInt(width);
                curY = rand.nextInt(height);
            }

            field[curY][curX] = 1;
        }
    }

    void finish() {
        field2 = new int[height][width];

        for (int i  = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                field2[i][j] = 0;
            }


    }

    void change() {
        for (int i = 1; i < hL; i++) {
            coord[i][0] = coord[i - 1][0];
            coord[i][1] = coord[i - 1][1];
        }
        coord[0][0] = hX;
        coord[0][1] = hY;

    }

    void check() {
        Log.i("!!!!!!!!!!!!!!!!!", Integer.toString(hX));

        Log.i("!!!!!!!!!!!!!!!!!", Integer.toString(hY));
        score++;
        hL++;
        if (field[hY][hX] == 2) {
            finish();
        } else {
            field2[coord[hL - 1][1]][coord[hL - 1][0]] = 0;
            change();
            if (field[hY][hX] != 1) {
                score--;
                hL--;
            }
        }
    }

    void updateField() {
        Random rand = new Random();

        field2 = new int[height][width];

        chDir = rand.nextInt(8);

        if (chDir == 1) {          // choose right direction
            dir = dir - 1;
            if (dir == -1)
                dir = 4;
        } else if (chDir == 2) {
            dir = dir + 1;
            if (dir == 5)
                dir = 1;
        }

        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {

                field2[y][x] = field[y][x];

                }
            }

        if (dir == 0) {
            hX = hX - 1;
            if (hX == -1)
                hX = width - 1;
            field2[hY][hX] = 2;
            check();

        } else if (dir == 1) {
            hY = hY - 1;
            if (hY == -1)
                hY = height - 1;
            field2[hY][hX] = 2;
            check();

        } else if (dir == 2) {
            hX = hX + 1;
            if (hX == width)
                hX = 0;
            field2[hY][hX] = 2;
            check();

        } else {
            hY = hY + 1;
            if (hY == height)
                hY = 0;
            field2[hY][hX] = 2;
            check();
        }

        if (!fl)
            finish();

        field = field2;
    }

    @Override
    public void draw(Canvas canvas) {
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                colors[x + y * width] = palette[field[y][x]];
            }
        }
        canvas.drawBitmap(colors, 0, width, 0, 0, width, height, false, null);
    }
}
