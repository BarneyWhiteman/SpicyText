import com.barneycodes.spicytext.*;

SpicyText slide;
SpicyText flash;
SpicyText both;

// A string using the effect tag with the name of a custom effect
String slidingText = "This is my [EFFECT=SLIDE]SLIDING[END_EFFECT] effect!";
String flashingText = "This text [EFFECT=FLASH]FLASHES [COLOUR=255]RED![END_COLOUR][END_EFFECT]";
String bothText = "You can [EFFECT=FLASH]COMBINE your [EFFECT=SLIDE]EFFECTS![END_EFFECT][END_EFFECT]";

void setup() {
  size(1280, 720);

  // The most basic constructor just needs a reference to this sketch, a string, and a text size to get going!
  slide = new SpicyText(this, slidingText, 80);
  flash = new SpicyText(this, flashingText, 80);
  both = new SpicyText(this, bothText, 80);

  // Custom effects are added using the static customEffect method.
  // This will register an effect function under the given name.
  // This can be done at anytime (even after creating the SpicyText objects using the custom effect)
  // but will only produce a result if it's done BEFORE drawing!
  SpicyText.customEffect("SLIDE", this::myCustomSlideEffect);
  SpicyText.customEffect("FLASH", this::myCustomFlashEffect);
}

void draw() {
    background(200);

    // Draw the text with alignment
    slide.draw(width/2, 10, CENTER, TOP);
    flash.draw(width/2, height/2, CENTER, CENTER);
    both.draw(20, height - 20, LEFT, BOTTOM);
}


void myCustomSlideEffect(SpicyTextChar spicyChar, CharEffectParams effectParams) {
  // values fluctuates bettwen -1 and 1 based on animation time from CharEffectParams
  float xT = sin(effectParams.time/100f);
  float rotT = sin(effectParams.time/200f);

  // Params are re-calculated each frame BUT different effects can ...effect... the same value
  // So it's best to MODIFY the existing value, rather than overwrite it.
  // Otherwise you might impact other effects!
  effectParams.x += xT * spicyChar.height/8; // using height because it's the same for all characters. Character widths can vary, so you'll get different movement distances!
  effectParams.rotation += rotT * PI/8;
}

void myCustomFlashEffect(SpicyTextChar spicyChar, CharEffectParams effectParams) {
  // t value fluctuates between 0 and 1
  float t = sin(effectParams.time/200f) * 2;
  t = constrain(t, 0, 1);

  // Can use the inbuilt colour functions to create new colours
  // In this case we ARE writing over the colour value BUT we're also using the existing colour when our effect isn't being applied,
  // so other effects that change the colour can still be used!
  effectParams.colour = t == 1 ? color(255, 0, 0) : effectParams.colour;
}