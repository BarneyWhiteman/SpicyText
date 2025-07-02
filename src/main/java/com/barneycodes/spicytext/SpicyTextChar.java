package com.barneycodes.spicytext;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.function.BiConsumer;

/**
 * Stores the raw information for a processed character of a SpicyText String.
 * This information is used to populate the CharEffectParams used to apply effects and define how this character is
 * displayed when drawn by the parent SpicyText object.
 * @see CharEffectParams
 * @see SpicyText#draw(float, float)
 */
public class SpicyTextChar {

    /**
     * The literal character
     */
    public final char c;


    /**
     * Base (no effects added) colour of the text
     */
    public final int colour;


    /**
     * Base (no effects added) colour of the background
     */
    public final int background;

    /**
     * Width and height of the character
     */
    public final int width, height;

    /**
     * Index of the character in the entire displayed text
     */
    public final int index;

    /**
     * List of all the effects that this character has
     */
    private final ArrayList<String> effects;

    /**
     * position of the character in the entire displayed text
     */
    protected float x, y;

    /**
     * Store processed information for a given character in a SpicyText object.
     * @param c the character literal
     * @param colour the text colour to be displayed
     * @param background the background colour to be used (ignored if -1)
     * @param index the index of the character in the displayed SpicyText text
     * @param x the x position in pixels of this character, relative to the origin (the top-left of the SpicyText)
     * @param y the y position in pixels of this character, relative to the origin (the top-left of the SpicyText)
     * @param width width of the character in pixels
     * @param height height of the character in pixels
     * @param effects a list of the names of all the effects to be applied to this character
     * @see SpicyText#effects
     */
    SpicyTextChar(char c, int colour, int background, int index, float x, float y, int width, int height, ArrayList<String> effects) {
        this.c = c;
        this.colour = colour;
        this.background = background;
        this.height = height;
        this.width = width;
        this.index = index;

        this.x = x;
        this.y = y;

        this.effects = new ArrayList<>();

        this.effects.addAll(effects);
    }

    /**
     * Creates a CharEffectParams object with all effects for this character applied.
     * The returned object is used to know where and how to display this character on the screen.
     * @param time timing offset for animations
     * @return CharEffectParams with all effects applied
     */
    protected CharEffectParams applyEffects(int time) {
        CharEffectParams params = new CharEffectParams(this, time);

        for(String effectName : effects) {
            BiConsumer<SpicyTextChar, CharEffectParams> effect = SpicyText.effects.get(effectName);
            if(effect == null) {
                continue;
            }
            effect.accept(this, params);
        }

        return params;
    }
}
