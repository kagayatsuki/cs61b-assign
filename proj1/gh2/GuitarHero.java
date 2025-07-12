package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A 37-note virtual guitar that maps the computer keyboard like a piano.
 *   q 2 w 3 e 4 r 5 t 6 y 7 u 8 i 9 o 0 p - [ = ...
 *  z x c v ...
 *  White keys on qwerty/zxcv rows, black keys on 123/asdf rows.
 */
public class GuitarHero {

    /* 37 keys, 37 strings */
    private static final String KEYBOARD =
            "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    /* 37 strings: index 0 -> 110 Hz, index 36 -> 880 Hz */
    private static final GuitarString[] STRINGS = new GuitarString[KEYBOARD.length()];

    /* Pre-compute frequencies: 440 * 2 ^ ((i - 24) / 12) */
    static {
        for (int i = 0; i < STRINGS.length; i++) {
            double freq = 440.0 * Math.pow(2, (i - 24) / 12.0);
            STRINGS[i] = new GuitarString(freq);
        }
    }

    public static void main(String[] args) {

        while (true) {

            /* 1. 检测按键并拨弦 */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = KEYBOARD.indexOf(key);   // -1 表示未找到
                if (index >= 0 && index < STRINGS.length) {
                    STRINGS[index].pluck();
                }
                /* 其它字符会被忽略，不会崩溃 */
            }

            /* 2. 叠加 37 根弦当前样本并播放 */
            double sample = 0.0;
            for (GuitarString gs : STRINGS) {
                sample += gs.sample();
            }
            StdAudio.play(sample);

            /* 3. 每个弦前进一个时间步 */
            for (GuitarString gs : STRINGS) {
                gs.tic();
            }
        }
    }
}