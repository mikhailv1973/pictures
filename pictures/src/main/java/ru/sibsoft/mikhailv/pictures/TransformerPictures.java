package ru.sibsoft.mikhailv.pictures;

import android.support.v4.view.ViewPager;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mikhail on 2/7/2016.
 */
public enum TransformerPictures {
    TRANS_1(null, "Transformer 1"),
    TRANS_2(null, "Transformer 2"),
    TRANS_3(null, "Transformer 3");

    public final static List<TransformerPictures> transformers = Arrays.asList(TRANS_1, TRANS_2, TRANS_3);

    private final ViewPager.PageTransformer transformer;
    private final String name;

    TransformerPictures(ViewPager.PageTransformer transformer, String name) {
        this.transformer = transformer;
        this.name = name;
    }
}
