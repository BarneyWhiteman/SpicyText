package com.barneycodes.spicytext;

/**
 * Holds the parameters of how a SpicyTextChar will be displayed.
 * This object gets passed to and manipulated by any effect functions that apply to a given char.
 * After all effects have been processed, these values are used within the SpicyText draw function to display
 * the character onto the screen.
 * @see SpicyText#draw(float, float)
 */
public class CharEffectParams {
    /**
     * Text colour value
     */
    public int colour;

    /**
     * Background colour value (ignored if -1)
     */
    public int background;

    /**
     * Time elapsed in milliseconds (useful for animated effects)
     */
    public final int time;

    /**
     * Position and rotation of the character
     */
    public float x, y, rotation;

    /**
     * Initialises the default values from the given SpicyTextChar.
     * @param c the SpicyTextChar that the effects will be applied to
     * @param time the animation time for the current effect (in milliseconds)
     */
    protected CharEffectParams(SpicyTextChar  c, int time) {
        this.colour = c.colour;
        this.background = c.background;
        this.x = c.x;
        this.y = c.y;

        this.time = time;

        this.rotation = 0;
    }
}
