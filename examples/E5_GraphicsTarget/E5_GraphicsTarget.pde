import com.barneycodes.spicytext.*;

SpicyText spicy;

// A differnt PGraphics object to use when displaying the text
PGraphics otherGraphics;

// Some different styled text strings to use
String text = "This is some [COLOUR=#FFFF0000][EFFECT=BOUNCE]STYLED[END_EFFECT] text[END_COLOUR]!";

void setup() {
  size(1280, 720);


  // Initialise the graphics object we want to draw to
  otherGraphics = createGraphics(512, 512);

  // We can make sure the text stays on the screen by using the text wrapping feature
  spicy = new SpicyText(this, text, 50, otherGraphics.width/2);

}

void draw() {
  background(200);

  // must call begin/end draw on the graphic we want to draw to
  otherGraphics.beginDraw();
  // Pass the graphic we want to draw to as the first argument
  // you can still use alignment as usual!
  otherGraphics.background(255);
  spicy.draw(otherGraphics, otherGraphics.width/2, otherGraphics.height/2, CENTER, CENTER);
  otherGraphics.endDraw();


  // display the other graphic on the screen
  image(otherGraphics, mouseX, mouseY);
}