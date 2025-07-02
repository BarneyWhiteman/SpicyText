import com.barneycodes.spicytext.*;

SpicyText changed;
SpicyText wrapped;

// Info for wrapping the text to a given length
int longTextDisplayOffset = 20;
int wrapWidth = 0;

// Some different styled text strings to use
String text1 = "Click to change the [BACKGROUND=255][COLOUR=#FFFF0000]FIRST[END_COLOUR] bit of text[END_BACKGROUND]";
String text2 = "[BACKGROUND=#FF0000FF][COLOUR=255]THIS[END_COLOUR][END_BACKGROUND] is the [EFFECT=BOUNCE]SECOND![END_EFFECT] bit of text!";
String longText = "This is just a really [COLOUR=255][EFFECT=BOUNCE]long[END_EFFECT][END_COLOUR] bit of text that should probably be [BACKGROUND=255]wrapped[END_BACKGROUND] to make it more readable!";

void setup() {
  size(1280, 720);

  // The most basic constructor just needs a reference to this sketch, a string, and a text size to get going!
  changed = new SpicyText(this, text1, 50);

  // Passing in a max width (in pixels) will make the displayed text take up multiple lines if it gets too long!
  wrapWidth = width/6;
  wrapped = new SpicyText(this, longText, 50, wrapWidth);

}

void draw() {
    background(200);

    // Draw a line at the max width cut off
    int x = longTextDisplayOffset + wrapWidth;
    int y = height/2 - wrapped.height()/2;
    line(x, y, x, y + wrapped.height());
    textSize(30);
    text("Drag this line!", x + 10, height/2);


    // Draw the text with alignment
    changed.draw(width - 20, 20, RIGHT, TOP);
    wrapped.draw(longTextDisplayOffset, height/2, LEFT, CENTER);
}


void mousePressed() {
  // Set the text to the second text when the mouse is pressed
  changed.setText(text2);
}

void mouseReleased() {
  // Reset the text to the first text when the mouse is released
  changed.setText(text1);
}

void mouseDragged() {
  // When dragging the mouse, calculate the new width
  wrapWidth = max(width/6, mouseX - longTextDisplayOffset);
  // Update the SpicyText object to use the new wrapping width
  wrapped.setText(longText, wrapWidth);
}