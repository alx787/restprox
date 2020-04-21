package ru.ath.alx.helpers;

// этот класс носит сугубо вспомогательный характер
// предназначен для того чтобы собрать в себе информацию
// для ответа на запрос о пробеге по одному объекту
//
// для каждого периода будет отдельный класс,
// периодов - классов будет по крайней мере два
// из-за того что время отличается от GMT на три часа

import java.util.Date;
import java.util.List;

public class TrackInfo {

    private Date period;
    private String regnom;
    private String wlnid;
    private String invnom;

    private float duration;
    private float motohours;
    private float mileage;

    private int cnt;

    private List<TrackInfoDetail> detailInfo;

}
