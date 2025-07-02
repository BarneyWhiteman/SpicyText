import com.barneycodes.spicytext.*;

SpicyText text1;
SpicyText text2;
SpicyText text3;

// Custom theme objects for the themed SpicyText
SpicyTextTheme customTheme1;
SpicyTextTheme customTheme2;


// Info for wrapping the text to a given length
int longTextDisplayOffset = 20;
int wrapWidth = 0;


// A simple styled string
String notThemedText = "This text is using the [BACKGROUND=255]default[END_BACKGROUND] theme!";
String themedText = "This text is using a [BACKGROUND=255]different[END_BACKGROUND] theme!";

void setup() {
  size(1280, 720);

  // Default constructor, just using the sketch, string, and text size
  text1 = new SpicyText(this, notThemedText, 50);




  // This is just a way to make sure to use an installed font
  // but you can use a font name in the createFont() function below, or use loadFont to use a font from your data folder!
  String[] fontList = PFont.list();
  String font1 = fontList[0];
  String font2 = fontList[fontList.length - 1];


  // To use a custom theme, you need your own theme object.
  // It comes with default values so you don't have to set everything if you don't want to!
  customTheme1 = new SpicyTextTheme();
  // Use a different font
  customTheme1.font = createFont(font1, 50);
  // Change the text colour
  customTheme1.textColour = color(128, 0, 128);
  // Change how much margin is given around a character when drawing the background
  customTheme1.textBackgroundMargin = 20;
  // Change how rounded the corners of the backgrounds are
  customTheme1.cornerRadius = 20;

  // Simply pass the custom theme into the constructor!
  text2 = new SpicyText(this, themedText, 50, customTheme1);

  // We can do other things with the theme too!
  customTheme2 = new SpicyTextTheme();
  // Use a different font
  customTheme2.font = createFont(font2, 50);
  // Change the text colour
  customTheme2.textColour = color(0, 128, 0);
  // Change how offset the shadow is and make it dimmer
  customTheme2.dropShadowOffset = 10;
  customTheme2.shadowOpacity = 0.1; // Shadow opacity goes between 0 (invisible) and 1 (fully opaque)

  text3 = new SpicyText(this, themedText, 50, customTheme2);

}

void draw() {
  background(200);

  // Draw the text with alignment
  text1.draw(width/2, height/4, CENTER, CENTER);
  text2.draw(width/2, height/2, CENTER, CENTER);
  text3.draw(width/2, height/4 * 3, CENTER, CENTER);
}
