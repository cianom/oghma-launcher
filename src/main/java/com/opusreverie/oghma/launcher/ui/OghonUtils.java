package com.opusreverie.oghma.launcher.ui;

import javafx.scene.paint.Color;

/**
 * Created by keen on 07/02/16.
 */
public class OghonUtils {


    public static void main(String[] args) {
        System.out.println(genOghon020String());
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
