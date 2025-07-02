package com.barneycodes.spicytext;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * The SpicyText class processes Strings containing [COLOUR=...], [BACKGROUND=...], and [EFFECT=...] tags for coloured
 * and animated text display within Processing sketches.
 * SpicyText is also capable of wrapping text to a given maximum width, which can be useful even when no "spicy" tags
 * are used (that is, a plain String).
 * A SpicyText object can be queried for its width and height, which will return the bounding box dimensions for the
 * text, and displayed with any text alignment.
 * Custom effects can be added to manipulate how characters display.
 *
 * @see SpicyText#width()
 * @see SpicyText#height()
 * @see SpicyText#customEffect(String, BiConsumer)
 */
public class SpicyText implements PConstants {

    /**
     * A hashmap to link effect names (used in [EFFECT=...] tags) to their corresponding functions.
     * The effect functions are BiConsumers which take in the SpicyTextChar they are operating on, as well as
     * the CharEffectParams that are manipulated by the effect, and define the characters display properties.
     */
    protected final static HashMap<String, BiConsumer<SpicyTextChar, CharEffectParams>> effects = new HashMap<>(Map.ofEntries(
            Map.entry("WAVE", SpicyText::waveEffect),
            Map.entry("BOUNCE", SpicyText::bounceEffect),
            Map.entry("JIGGLE", SpicyText::jiggleEffect)
    ));

    /**
     * Registers a custom effect name and corresponding function to apply the effect.
     *
     * @param name The name of the added effect. Use this name in the [EFFECT=name] tag to use.
     * @param effect A function that modifies the CharEffectParams to customise a given characters properties.
     */
    public static void customEffect(String name, BiConsumer<SpicyTextChar, CharEffectParams> effect) {
        effects.put(name, effect);
    }

    private final PApplet parent;

    private ArrayList<SpicyTextChar> chars;
    private ArrayList<Float> lineLengths;

    private String rawText;
    private final int textSize;
    private final int textHeight;
    private int maxWidth;
    private int width;
    private int height;

    private final SpicyTextTheme theme;

    private int offset = -1;

    private final int ascent;

    /**
     * Creates a SpicyText object with the given text at the given text size.
     *
     * @param parent the parent sketch
     * @param text the text to be processed into the Spicy Text
     * @param textSize the size the text should be displayed at (needed at initialisation for various sizing calculations)
     */
    public SpicyText(PApplet parent, String text, int textSize) {
        this(parent, text, textSize, new SpicyTextTheme());
    }

    /**
     * Creates a SpicyText object with the given text at the given text size, with text wrapping.
     * Wrapping occurs when a line exceeds the maximum width, the break will occur at the position of the last space.
     *
     * @param parent the parent sketch
     * @param text the text to be processed into the Spicy Text
     * @param textSize the size the text should be displayed at (needed at initialisation for various sizing calculations)
     * @param maxWidth the maximum line length before text wrapping occurs (in pixels).
     */
    public SpicyText(PApplet parent, String text, int textSize, int maxWidth) {
        this(parent, text, textSize, maxWidth, new SpicyTextTheme());
    }

    /**
     * Creates a SpicyText object with the given text at the given text size, with a custom theme.
     *
     * @param parent the parent sketch
     * @param text the text to be processed into the Spicy Text
     * @param textSize the size the text should be displayed at (needed at initialisation for various sizing calculations)
     * @param theme An instance of the SpicyTextTheme, which can change the default display behaviour
     * @see SpicyTextTheme
     */
    public SpicyText(PApplet parent, String text, int textSize, SpicyTextTheme theme) {
        this(parent, text, textSize, -1, theme);
    }

    /**
     * Creates a SpicyText object with the given text at the given text size,  with text wrapping and with a custom theme.
     *
     * @param parent the parent sketch
     * @param text the text to be processed into the Spicy Text
     * @param textSize the size the text should be displayed at (needed at initialisation for various sizing calculations)
     * @param maxWidth the maximum line length before text wrapping occurs (in pixels).
     * @param theme An instance of the SpicyTextTheme, which can change the default display behaviour
     * @see SpicyTextTheme
     */
    public SpicyText(PApplet parent, String text, int textSize, int maxWidth, SpicyTextTheme theme) {
        this.parent = parent;

        ascent = (int)(textSize * theme.getFont(parent).ascent());
        int descent = (int) (textSize * theme.getFont(parent).descent());

        this.textSize = textSize;
        this.textHeight = ascent + descent;

        this.maxWidth = maxWidth;
        this.theme = theme;

        setText(text);
    }

    /**
     * Update the text of the SpicyText object, with a new text wrapping limit.
     * Set maxWidth to -1 to remove text wrapping.
     * This will also recalculate the width and height of the display text.
     *
     * @param text The new text for the SpicyText object
     * @param maxWidth The new text wrapping width limit (in pixels)
     */
    public void setText(String text, int maxWidth) {
        this.maxWidth = maxWidth;
        this.width = 0;
        // Reset the raw text so that setText doesn't skip it!
        rawText = "";
        setText(text);
    }

    /**
     * Update the text of the SpicyText object.
     * Any previously set text wrapping maximum width will remain the same.
     * This will also recalculate the width and height of the display text.
     *
     * @param text The new text for the SpicyText object
     */
    public void setText(String text) {
        if(Objects.equals(rawText, text)) {
            return;
        }
        rawText = text;
        processText();
    }

    /**
     * Gets the width of the displayed text in pixels.
     * This will take into account any new lines or text wrapping, therefore this is the width of the longest line of
     * text, NOT the total length of all the text.
     * NOTE: This does NOT take into account any backgrounds or effects that can cause the text to extend/move outside it's
     * original position. The width only takes into account the position of un-effected text!
     *
     * @return the width of the displayed text
     */
    public int width() {
        return width;
    }

    /**
     * Gets the height of the displayed text in pixels.
     * NOTE: This does NOT take into account any backgrounds or effects that can cause the text to extend/move outside it's
     * original position. The height only takes into account the position of un-effected text!
     *
     * @return the height of the displayed text
     */
    public int height() {
        return height;
    }

    /**
     * Displays the Spicy Text at the given position.
     * The text will be aligned so the given position is at the TOP LEFT of the text.
     *
     * @param x horizontal position in pixels
     * @param y vertical position in pixels
     */
    public void draw(float x, float y) {
        draw(parent.g, x, y);
    }

    /**
     * Displays the Spicy Text at the given position with custom alignment.
     * The alignments follow the Processing textAlign
     *
     * @param x horizontal position in pixels
     * @param y vertical position in pixels
     * @param alignH horizontal alignment (LEFT/CENTER/RIGHT)
     * @param alignV vertical alignment (TOP/CENTER/BOTTOM)
     * @see PGraphics#textAlign(int, int)
     */
    public void draw(float x, float y, int alignH, int alignV) {
        draw(parent.g, x, y, alignH, alignV);
    }

    /**
     * Displays the Spicy Text on the given PGraphics object at the given position.
     * The text will be aligned so the given position is at the TOP LEFT of the text.
     *
     * @param g the PGraphics object for the Spicy Text to be drawn onto
     * @param x horizontal position in pixels
     * @param y vertical position in pixels
     * @see PGraphics
     */
    public void draw(PGraphics g, float x, float y) {
        draw(g, x, y, LEFT, TOP);
    }

    /**
     * Displays the Spicy Text on the given PGraphics object at the given position.
     * The text will be aligned so the given position is at the TOP LEFT of the text.
     *
     * @param g the PGraphics object for the Spicy Text to be drawn onto
     * @param x horizontal position in pixels
     * @param y vertical position in pixels
     * @param alignH horizontal alignment (LEFT/CENTER/RIGHT)
     * @param alignV vertical alignment (TOP/CENTER/BOTTOM)
     * @see PGraphics
     * @see PGraphics#textAlign(int, int)
     */
    public void draw(PGraphics g, float x, float y, int alignH, int alignV) {

        if(offset == -1) {
            offset = (int)parent.random(1000);
        }

        g.push();

        g.textAlign(LEFT, BASELINE);
        g.translate(x, y + yAlignOffset(alignV, height));

        if(theme.font != null) {
            g.textFont(theme.font, textSize);
        }
        g.textSize(textSize);

        CharEffectParams[] params = new CharEffectParams[chars.size()];

        // Draw backgrounds first
        int lineNum = 0;
        float lastY = 0;
        float lineOffset = xAlignOffset(alignH, (int)(lineLengths.get(lineNum).floatValue()));

        for(int i = 0; i < chars.size(); i ++) {
            SpicyTextChar c = chars.get(i);
            params[i] = c.applyEffects( parent.millis() + offset);

            if(c.y != lastY) {
                lineNum += 1;
                lineOffset = xAlignOffset(alignH, (int)(lineLengths.get(lineNum).floatValue()));
                lastY = c.y;
            }

            if(params[i].background != -1) {
                g.push();

                g.translate(lineOffset + params[i].x + c.width/2, params[i].y + c.height/2);
                g.rotate(params[i].rotation);
                g.translate(-c.width/2, -c.height/2);


                g.fill(params[i].background);
                g.noStroke();
                g.rect(-theme.textBackgroundMargin, -theme.textBackgroundMargin, c.width + theme.textBackgroundMargin * 2, c.height + theme.textBackgroundMargin * 2, theme.cornerRadius);

                g.pop();
            }
        }

        lineNum = 0;
        lastY = 0;
        lineOffset = xAlignOffset(alignH, (int)(lineLengths.get(lineNum).floatValue()));
        for(int i = 0; i < chars.size(); i ++) {
            SpicyTextChar c = chars.get(i);

            if(c.y != lastY) {
                lineNum += 1;
                lineOffset = xAlignOffset(alignH, (int)(lineLengths.get(lineNum).floatValue()));
                lastY = c.y;
            }

            g.push();

            g.translate(lineOffset + params[i].x + c.width/2, params[i].y + c.height/2);
            g.rotate(params[i].rotation);
            g.translate(-c.width/2, -c.height/2);

            if(theme.dropShadowOffset != 0) {
                g.fill(0, 255 * theme.shadowOpacity);
                g.text(c.c, theme.dropShadowOffset, ascent + theme.dropShadowOffset);
            }

            g.fill(params[i].colour);
            g.text(c.c, 0, ascent);
            g.pop();
        }

        g.pop();
    }

    private static class ProcessingParams {
        private float x = 0, y = 0;

        private final ArrayList<Integer> colourStack = new ArrayList<>();
        private final ArrayList<Integer> backgroundStack = new ArrayList<>();

        private final ArrayList<String> effectStack = new ArrayList<>();

        private int index = 0;
    }

    private void processText() {
        chars = new ArrayList<>();
        lineLengths = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(rawText);

        ProcessingParams params = new ProcessingParams();
        params.colourStack.add(theme.textColour);
        params.backgroundStack.add(-1);

        boolean inBrackets = rawText.startsWith("[");

        int lastSpace = 0;
        while(tokenizer.hasMoreTokens()) {
            String current;

            if(inBrackets) {
                current = tokenizer.nextToken("]");
                if(current.startsWith("[")) {
                    current = current.substring(1);
                }

                parseToken(current, params);

                inBrackets = false;
            } else {
                current = tokenizer.nextToken("[");
                if(current.startsWith("]")) {
                    current = current.substring(1);
                }

                char[] characters = current.toCharArray();


                for(char c : characters) {
                    int charWidth = (int)theme.textWidth(parent, String.valueOf(c), textSize);

                    if(c == ' ') {
                        lastSpace = params.index;
                    }

                    if(c == '\n') {
                        lastSpace = PApplet.max(lastSpace, params.index);
                        newLine(params);
                        continue;
                    }

                    chars.add(new SpicyTextChar(c, parent.color(params.colourStack.get(params.colourStack.size() - 1)), params.backgroundStack.get(params.backgroundStack.size() - 1), params.index, params.x, params.y, charWidth, textHeight, params.effectStack));

                    if((maxWidth != -1 && params.x + charWidth >= maxWidth)) {
                        if(lastSpace == params.index) {
                            newLine(params);
                        } else {
                            SpicyTextChar spaceChar = chars.get(lastSpace);

                            float offX = spaceChar.x + spaceChar.width;
                            float currentX = params.x;
                            params.x = offX;
                            newLine(params);

                            float diffX = currentX - offX;
                            for(int i = lastSpace + 1; i < chars.size(); i ++) {
                                SpicyTextChar ch = chars.get(i);
                                ch.x -= offX;
                                ch.y = params.y;
                            }

                            params.x = diffX;
                        }
                    }

                    params.index ++;
                    params.x += charWidth;
                }


                inBrackets = true;
            }
        }
        width = (int)PApplet.max(width, params.x);
        height = (int)params.y + textHeight;
        lineLengths.add(params.x);
    }

    private void newLine(ProcessingParams params) {
        lineLengths.add(params.x);
        width = (int)PApplet.max(width, params.x);
        params.x = 0;
        params.y += textHeight + theme.newLineMargin;
    }

    private void parseToken(String token, ProcessingParams params) {
        String[] parts = token.split("=");

        if(parts.length == 1) {

            String key = parts[0];

            switch(key) {
                case "END_COLOUR": if(params.colourStack.size() > 1) params.colourStack.remove(params.colourStack.size() - 1); break;

                case "END_BACKGROUND": if(params.backgroundStack.size() > 1) params.backgroundStack.remove(params.backgroundStack.size() - 1); break;

                case "END_EFFECT": if(!params.effectStack.isEmpty()) params.effectStack.remove(params.effectStack.size() - 1); break;
            }
        }


        if(parts.length == 2) {
            String key = parts[0];
            String value = parts[1];

            switch(key) {
                case "COLOUR": params.colourStack.add(getColour(value, theme.textColour)); break;

                case "BACKGROUND": params.backgroundStack.add(getColour(value, -1)); break;

                case "EFFECT": params.effectStack.add(value); break;
            }
        }
    }

    private int getColour(String colour, int defaultColour) {
        colour = colour.toUpperCase();

        if(colour.startsWith("0X")) {
            colour = colour.replace("0X", "");
            return (int)Long.parseLong(colour, 16);
        }

        if(colour.startsWith("#")) {
            colour = colour.replace("#", "");
            return (int)Long.parseLong(colour, 16);
        }

        try {
            return Integer.parseInt(colour);
        } catch (NumberFormatException e) {
            return defaultColour;
        }
    }


    private static void waveEffect(SpicyTextChar c, CharEffectParams params) {
        float off = PApplet.sin(c.index - params.time / 500f);

        params.y -= off * c.height/4;
    }

    private static void bounceEffect(SpicyTextChar c, CharEffectParams params) {
        float off = PApplet.max(0, PApplet.sin(c.index - params.time /500f));

        params.y -= off * c.height/4;
    }

    private static void jiggleEffect(SpicyTextChar c, CharEffectParams params) {
        float rot = PApplet.sin(c.index - params.time /97f);
        params.rotation += rot * 0.4f;
    }

    /**
     * Wraps the given text in [COLOUR=...] and [END_COLOUR] tags, where the colour is set to the given colour.
     * The colour is given as an int, so it can come straight from any of the various PGraphics.color functions.
     * It can also be given in the 0x... hexadecimal format.
     *
     * @param text The text to be wrapped in the colour tags
     * @param colour the colour, as an integer, to be used in the colour tags
     * @return the original text wrapped in colour tags of the given colour
     * @see PGraphics#color(int, int, int)
     */
    public static String withColour(String text, int colour) {
        return withColour(text, String.valueOf(colour));
    }

    /**
     * Wraps the given text in [COLOUR=...] and [END_COLOUR] tags, where the colour is set to the given colour.
     * The colour is given as a String of the colour code.
     * For example "255", "0xFFFFFFFF" and "#FFFFFFFF" are all valid and give the colour of white.
     *
     * @param text The text to be wrapped in the colour tags
     * @param colour the colour, as a String, to be used in the colour tags
     * @return the original text wrapped in colour tags of the given colour
     */
    public static String withColour(String text, String colour) {
        return String.format("[COLOUR=%s]%s[END_COLOUR]", colour, text );
    }

    /**
     * Wraps the given text in [BACKGROUND=...] and [END_BACKGROUND] tags, where the background is set to the given colour.
     * The colour is given as an int, so it can come straight from any of the various PGraphics.color functions.
     * It can also be given in the 0x... hexadecimal format.
     *
     * @param text The text to be wrapped in the background tags
     * @param colour the colour, as an integer, to be used in the background tags
     * @return the original text wrapped in background tags of the given colour
     * @see PGraphics#color(int, int, int)
     */
    public static String withBackground(String text, int colour) {
        return withBackground(text, String.valueOf(colour));
    }

    /**
     * Wraps the given text in [BACKGROUND=...] and [END_BACKGROUND] tags, where the background is set to the given colour.
     * The colour is given as a String of the colour code.
     * For example "255", "0xFFFFFFFF" and "#FFFFFFFF" are all valid and give the colour of white.
     *
     * @param text The text to be wrapped in the background tags
     * @param colour the colour, as a String, to be used in the background tags
     * @return the original text wrapped in background tags of the given colour
     */
    public static String withBackground(String text, String colour) {
        return String.format("[BACKGROUND=%s]%s[END_BACKGROUND]", colour, text );
    }

    /**
     * Wraps the given text in [EFFECT=...] and [END_EFFECT] tags, where the effect is set to the given effect name.
     *
     * @param text the text to be wrapped in the effect tags
     * @param effect the name of the effect to be used in the effect tags
     * @return the original text wrapped in effect tags with the given effect name
     */
    public static String withEffect(String text, String effect) {
        return String.format("[EFFECT=%s]%s[END_EFFECT]", effect, text );
    }

    private static int xAlignOffset(int alignH, int w) {
        int offset = 0;
        switch (alignH) {
            case CENTER -> offset -= w/2;
            case RIGHT -> offset -= w;
        }

        return offset;
    }

    private static int yAlignOffset(int alignV, int h) {
        int offset = 0;
        switch (alignV) {
            case CENTER -> offset -= h/2;
            case BOTTOM -> offset -= h;
        }
        return offset;
    }
}
