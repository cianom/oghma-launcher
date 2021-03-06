package com.opusreverie.oghma.launcher.ui;

import javafx.scene.paint.Color;

/**
 * Utility class for creating oghons in String format.
 * <p>
 * Copyright © 2016 Cian O'Mahony. All rights reserved.
 *
 * @author Cian O'Mahony
 */
public class OghonUtils {


    public static void main(String[] args) {
        System.out.println(genOghonNightlyString());
    }


    public static String genOghonNightlyString() {
        long[][] encoded = new long[4][4];
        encoded[0][0] = encodeData(false, Color.WHITE, Color.web("#672178ff"));
        encoded[1][0] = encodeData(true, Color.web("#440055ff"), Color.web("#2d1650ff"));
        encoded[2][0] = encodeData(false, Color.web("#11002bff"), Color.web("#440055ff"));
        encoded[3][0] = encodeData(true, Color.web("#550044ff"), Color.WHITE);

        encoded[0][1] = encodeData(true, Color.web("#225500ff"), Color.web("#d42affff"));
        encoded[1][1] = encodeData(true, Color.web("#bc5fd3ff"), Color.web("#aa00d4ff"));
        encoded[2][1] = encodeData(false, Color.web("#cc00ffff"), Color.web("#dd55ffff"));
        encoded[3][1] = encodeData(false, Color.web("#ff80e5ff"), Color.web("#112b00ff"));

        encoded[0][2] = encodeData(true, Color.web("#1a1a1aff"), Color.web("#0b2817ff"));
        encoded[1][2] = encodeData(true, Color.web("#002b00ff"), Color.web("#ff80e5ff"));
        encoded[2][2] = encodeData(false, Color.web("#e580ffff"), Color.web("#17280bff"));
        encoded[3][2] = encodeData(false, Color.web("#22280bff"), Color.web("#24221cff"));

        encoded[0][3] = encodeData(true, Color.WHITE, Color.web("#000000ff"));
        encoded[1][3] = encodeData(true, Color.web("#000000ff"), Color.web("#22241cff"));
        encoded[2][3] = encodeData(false, Color.web("#1a1a1aff"), Color.web("#000000ff"));
        encoded[3][3] = encodeData(false, Color.web("#1a1a1aff"), Color.WHITE);

        StringBuilder s = new StringBuilder();
        for (int y = 0; y < encoded[0].length; y++) {
            for (int x = 0; x < encoded.length; x++) {
                s.append(Long.toHexString(encoded[x][y]));
                s.append(',');
            }
            s.append(';');
        }
        return s.toString();
    }

    public static String genOghon020String() {
        long[][] encoded = new long[4][4];
        encoded[0][0] = encodeData(false, Color.WHITE, Color.web("#d5f6ffff"));
        encoded[1][0] = encodeData(false, Color.web("#aaeeffff"), Color.web("#55ddffff"));
        encoded[2][0] = encodeData(true, Color.web("#55ddffff"), Color.web("#d5e5ffff"));
        encoded[3][0] = encodeData(true, Color.web("#d5f6ffff"), Color.WHITE);

        encoded[0][1] = encodeData(true, Color.web("#ffd42aff"), Color.web("#00ffccff"));
        encoded[1][1] = encodeData(false, Color.web("#00aad4ff"), Color.web("#00aa88ff"));
        encoded[2][1] = encodeData(true, Color.web("#008066ff"), Color.web("#00d4aaff"));
        encoded[3][1] = encodeData(false, Color.web("#00ffccff"), Color.web("#ffdd55ff"));

        encoded[0][2] = encodeData(false, Color.web("#aa8800ff"), Color.web("#ffcc00ff"));
        encoded[1][2] = encodeData(false, Color.web("#d4aa00ff"), Color.web("#ffcc00ff"));
        encoded[2][2] = encodeData(true, Color.web("#d4aa00ff"), Color.web("#aa8800ff"));
        encoded[3][2] = encodeData(true, Color.web("#aa8800ff"), Color.web("#ffcc00ff"));

        encoded[0][3] = encodeData(true, Color.WHITE, Color.web("#784421ff"));
        encoded[1][3] = encodeData(false, Color.web("#786721ff"), Color.web("#552200ff"));
        encoded[2][3] = encodeData(true, Color.web("#803300ff"), Color.web("#806600ff"));
        encoded[3][3] = encodeData(false, Color.web("#784421ff"), Color.WHITE);

        StringBuilder s = new StringBuilder();
        for (int y = 0; y < encoded[0].length; y++) {
            for (int x = 0; x < encoded.length; x++) {
                s.append(Long.toHexString(encoded[x][y]));
                s.append(',');
            }
            s.append(';');
        }
        return s.toString();
    }

    public static String genOghon010String() {
        long[][] encoded = new long[4][4];
        encoded[0][0] = encodeData(false, Color.WHITE, Color.web("#87cddeff"));
        encoded[1][0] = encodeData(false, Color.web("#80e5ffff"), Color.web("#47a8d2ff"));
        encoded[2][0] = encodeData(true, Color.web("#47a8d2ff"), Color.web("#d5f6ffff"));
        encoded[3][0] = encodeData(true, Color.web("#aaeeffff"), Color.WHITE);
        encoded[0][1] = encodeData(true, Color.web("#5f8dd3ff"), Color.web("#47a8d2ff"));
        encoded[1][1] = encodeData(false, Color.web("#5599ffff"), Color.web("#008000ff"));
        encoded[2][1] = encodeData(true, Color.web("#008000ff"), Color.web("#47a8d2ff"));
        encoded[3][1] = encodeData(true, Color.web("#47a8d2ff"), Color.web("#47a8d2ff"));
        encoded[0][2] = encodeData(false, Color.web("#47a8d2ff"), Color.web("#008000ff"));
        encoded[1][2] = encodeData(false, Color.web("#00ff00ff"), Color.web("#552200ff"));
        encoded[2][2] = encodeData(true, Color.web("#803300ff"), Color.web("#00ff00ff"));
        encoded[3][2] = encodeData(true, Color.web("#008000ff"), Color.web("#47a8d2ff"));
        encoded[0][3] = encodeData(true, Color.WHITE, Color.web("#28170bff"));
        encoded[1][3] = encodeData(false, Color.web("#502d16ff"), Color.web("#803300ff"));
        encoded[2][3] = encodeData(true, Color.web("#552200ff"), Color.web("#e3dbdbff"));
        encoded[3][3] = encodeData(false, Color.web("#502d16ff"), Color.WHITE);

        StringBuilder s = new StringBuilder();
        for (int y = 0; y < encoded[0].length; y++) {
            for (int x = 0; x < encoded.length; x++) {
                s.append(Long.toHexString(encoded[x][y]));
                s.append(',');
            }
            s.append(';');
        }
        return s.toString();
    }

    private static long encodeData(boolean downSloped, Color a, Color b) {
        long encoded = downSloped ? (1L << 48) : 0;

        encoded |= ((long) (a.getRed() * 0xFF));
        encoded |= ((long) (a.getGreen() * 0xFF)) << 8;
        encoded |= ((long) (a.getBlue() * 0xFF)) << 16;

        encoded |= ((long) (b.getRed() * 0xFF)) << 24;
        encoded |= ((long) (b.getGreen() * 0xFF)) << 32;
        encoded |= ((long) (b.getBlue() * 0xFF)) << 40;

        return encoded;

    }

}
