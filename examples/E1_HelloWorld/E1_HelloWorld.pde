import com.barneycodes.spicytext.*;

SpicyText text1;
SpicyText text2;
SpicyText text3;

// The basic colour tags are COLOUR and BACKGROUND
// They both take a colour as a parameter, this can be in #, 0x or just a regular int format, all of which you can see below.
// When you want a colour/background to end, simply use [END_COLOUR] or [END_BACKGROUND] respectively.
// If multiple colours or backgrounds have been set, using END_COLOUR/BACKGROUND will go back to the previously set colour/background
// If there are no previously set colours, it will revert to the default colour, and if there are no previous backgrounds, the background will be removed.
// As you can see with the "world" text below, colours and backgrounds can be nested
String helloWorld = "[COLOUR=#FFC6505A]HELLO[END_COLOUR] [BACKGROUND=0xFF6EB8A8][COLOUR=255]WORLD![END_COLOUR][END_BACKGROUND]";

// Spicy text also has the EFFECT tag, which takes the name of the effect.
// The built-in effects are WAVE, BOUNCE, and JIGGLE
// You can create your own effects as well, have a look at the "CustomEffects" example to see how.
// Effects can be stacked together to combine the effects.
// Just like with the colours and backgrounds, use [END_EFFECT] to stop the last set effect. All previously set effects will still apply
// This string has a new line in it (\n) which is not a problem!
String effects = "You can add [EFFECT=WAVE]EFFECTS[END_EFFECT] to text. [EFFECT=BOUNCE]New lines are [EFFECT=JIGGLE]NO PROBLEM[END_EFFECT] too![END_EFFECT]";

// You can of course combine all of the different tags however you like.
// You don't HAVE to end the colours, backgrounds, or effects before the end of the string. Any un-ended tags will stay in place for the rest of the string
String theLot = "[BACKGROUND=0xFFEE9C5D][COLOUR=255]Altogether now! [END_BACKGROUND][BACKGROUND=0]I hope you like [EFFECT=BOUNCE][COLOUR=0xFF74A33F]SPICY [COLOUR=0xFFEE9C5D]TEXT!";

void setup() {
  size(1280, 720);

  // The most basic constructor just needs a reference to this sketch, a string, and a text size to get going!
  text1 = new SpicyText(this, helloWorld, 80);

  // If you've got a long string that you want to fit a specific width, you can pass in the max width (in this case, width/2)
  // This will cause the text to wrap if it gets too wide.
  // You can also put new lines (\n) into your string, and it will be handled correctly.
  text2 = new SpicyText(this, effects, 60, width/2);

  text3 = new SpicyText(this, theLot, 60);
}

void draw() {
    background(200);

    // By default, the draw function aligns the text to the LEFT and TOP
    text1.draw(20, 20);

    // If you want to align it differently, you can specify alignment using:
    // LEFT/CENTER/RIGHT for horizontal
    // TOP/CENTER/BOTTOM for vertical
    text2.draw(width/2, height/2, CENTER, CENTER);
    text3.draw(width - 20, height - 20, RIGHT, BOTTOM);
}