package com.barneycodes.spicytext;

import processing.core.PApplet;
import processing.core.PFont;

/**
 * Holds values required for the drawing of a SpicyText object.
 * The SpicyTextTheme is initialised with default values, but these can be changed before being passed to the SpicyText
 * constructor
 * @see SpicyText#SpicyText(PApplet, String, int, SpicyTextTheme)
 * @see SpicyText#SpicyText(PApplet, String, int, int, SpicyTextTheme)
 */
public class SpicyTextTheme {

    /**
     * Base text colour
     */
    public int textColour = 0;

    /**
     * The font to draw the text in.
     * If no font is supplied, the default font will be used.
     * NOTE: Changing this AFTER a SpicyText has been created will have strange effects (unless setText is also called),
     * since the text is processed on initialisation with the original font (different fonts have different widths/heights).
     * SpicyText objects created AFTER this is changed, using this theme, will use the new font.
     */
    public PFont font = null;

    /**
     * Diagonal offset applied to shadow text. An offset of 0 will not draw any shadow.
     */
    public int dropShadowOffset = 2;

    /**
     * Opacity of the drop shadow. 0 = transparent; 1 = fully opaque
     */
    public float shadowOpacity = 0.5f;

    /**
     * Size of a margin to leave around a character when drawing its background
     */
    public int textBackgroundMargin = 4;

    /**
     * Corner radius size when drawing character backgrounds
     */
    public int cornerRadius = 4;


    /**
     * How much of a gap to leave between the bottom of the previous line and the top of the next line.
     * (The actual SIZE of the text is already taken into account, this value is JUST the gap!)
     * NOTE: Changing this AFTER a SpicyText has been created will have no effect (unless setText is called), since the
     * text is processed on initialisation. SpicyText objects created AFTER this is changed, using this theme, will
     * be effected.
     */
    public int newLineMargin = 4;


    /**
     * Gets the theme's font, returns the default font of the sketch if none has been supplied
     * @param parent the running sketch
     * @return the theme's font, or the sketch's default font
     */
    protected PFont getFont(PApplet parent) {
        if(font == null) {
            // Call textAscent to ensure font has been loaded!!!
            parent.g.textAscent();
            return parent.g.textFont;
        }

        return font;
    }

    /**
     * Measures the width of a given string at the given text size, regardless of what textSize
     * is set to in the sketch/PGraphics object.
     * New lines are taken into account, therefore in a multi-line string, the MAXIMUM width of all
     * the lines is returned, not the total length of all the lines.
     *
     * @param parent The parent Processing sketch
     * @param str The string to measure
     * @param textSize The textSize for the given text
     * @return The maximum line width of the given text and the given text size
     */
    public float textWidth(PApplet parent, String str, int textSize) {
        int length = str.length();

        char[] textWidthBuffer = new char[length + 10];

        str.getChars(0, length, textWidthBuffer, 0);
        float wide = 0.0F;
        int index = 0;

        int start;
        for (start = 0; index < length; ++index) {
            if (textWidthBuffer[index] == '\n') {
                wide = Math.max(wide, textWidthImpl(parent, textWidthBuffer, textSize, start, index));
                start = index + 1;
            }
        }

        if (start < length) {
            wide = Math.max(wide, textWidthImpl(parent, textWidthBuffer, textSize, start, index));
        }

        return wide;
    }

    private float textWidthImpl(PApplet parent, char[] buffer, int textSize, int start, int stop) {
        float wide = 0.0F;

        PFont font = getFont(parent);

        if (font == null) {
            return wide;
        }

        for (int i = start; i < stop; ++i) {
            wide += font.width(buffer[i]) * textSize;
        }

        return wide;
    }
}
