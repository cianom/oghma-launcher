package com.opusreverie.oghma.launcher.ui.component;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Draws oghons with a staggered transition effect on a canvas element.
 *
 * @author Cian.
 */
public class OghonDrawer {

    private static final String DEFAULT_OGHON = "decd87ffffff,d2a847ffe580,1fff6d5d2a847,1ffffffffeeaa,;1d2a847d38d5f,8000ff9955,1d2a847008000,1d2a847d2a847,;8000d2a847,225500ff00,100ff00003380,1d2a847008000,;10b1728ffffff,3380162d50,1dbdbe3002255,ffffff162d50,;";
    private static final int    CANVAS_SIZE   = 160;

    private final Canvas canvas;


    public OghonDrawer(Canvas canvas) {
        this.canvas = canvas;
    }

    public long[][] fromPicoString(String picoString) {
        long[][] result = new long[4][4];
        String[] lines = picoString.split(";");
        for (int y = 0; y < lines.length; y++) {
            String[] lineItems = lines[y].split(",");
            for (int x = 0; x < lineItems.length; x++) {
                long item = Long.parseUnsignedLong(lineItems[x], 16);
                result[x][y] = item;
            }
        }
        return result;
    }

    public void drawDefault() {
        drawPico(fromPicoString(DEFAULT_OGHON));
    }

    public void drawPico(final long[][] data) {
        final List<OghonTri> tris = new ArrayList<>();
        int xScale = CANVAS_SIZE / data.length;
        int yScale = CANVAS_SIZE / data[0].length;
        for (int x = 0; x < data.length; x++) {
            for (int y = 0; y < data[x].length; y++) {
                tris.add(new OghonTri(x, y, xScale, yScale, data[x][y]));
            }
        }
        while (!tris.isEmpty()) {
            int index = new Random().nextInt(tris.size());
            final OghonTri tri = tris.remove(index);
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(20 + new Random().nextInt(80));
                }
                catch (InterruptedException e) {
                    // Do nothing
                }
                Platform.runLater(() -> drawPicoSquare(tri));
            });

        }
    }

    private void drawPicoSquare(final OghonTri tri) {
        int xScale = tri.xScale;
        int yScale = tri.yScale;
        int xs = tri.x * xScale;
        int ys = tri.y * yScale;
        GraphicsContext gc = canvas.getGraphicsContext2D();

        double[] x1s = {xs, xs, xs + xScale};
        double[] y1s = tri.downSloped ? new double[]{ys, ys + yScale, ys + yScale} : new double[]{ys, ys + yScale, ys};

        double[] x2s = {xs, xs + xScale, xs + xScale};
        double[] y2s = tri.downSloped ? new double[]{ys, ys + yScale, ys} : new double[]{ys + yScale, ys + yScale, ys};

        gc.setFill(tri.c1);
        gc.fillPolygon(x1s, y1s, 3);

        gc.setFill(tri.c2);
        gc.fillPolygon(x2s, y2s, 3);
    }

    private static class OghonTri {
        private final int x, y, xScale, yScale;
        private final boolean downSloped;
        private final Color   c1, c2;

        private OghonTri(int x, int y, int xScale, int yScale, long data) {
            this.x = x;
            this.y = y;
            this.xScale = xScale;
            this.yScale = yScale;

            int r = (int) (data & 0xFF);
            int g = (int) (data >> 8) & 0xFF;
            int b = (int) (data >> 16) & 0xFF;
            this.c1 = Color.rgb(r, g, b);

            int r2 = (int) (data >> 24) & 0xFF;
            int g2 = (int) (data >> 32) & 0xFF;
            int b2 = (int) (data >> 40) & 0xFF;
            this.c2 = Color.rgb(r2, g2, b2);

            this.downSloped = ((data >> 48) & 1) == 1;
        }

    }


}
