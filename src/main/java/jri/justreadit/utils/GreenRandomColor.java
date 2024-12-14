package jri.justreadit.utils;

import java.awt.Color;

public class GreenRandomColor {
    // 파스텔 초록 계열 랜덤 컬러 생성
    public static Color getGreenRandomColor() {
        // Hue(색상): 0.25 ~ 0.4 사이 (초록 계열)
        float hue = 0.2f + (float) Math.random() * 0.3f;

        // Saturation(채도): 0.4 ~ 0.7 사이 (파스텔 느낌)
        float saturation = 0.4f + (float) Math.random() * 0.2f;

        // Brightness(명도): 0.8 ~ 1.0 사이 (밝은 느낌)
        float brightness = 0.8f + (float) Math.random() * 0.1f;

        // HSB 기반 컬러 생성
        return Color.getHSBColor(hue, saturation, brightness);
    }
}
