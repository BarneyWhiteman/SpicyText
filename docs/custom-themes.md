# Using Custom Themes
To get your text looking *just right* you probably want to try using a custom theme!

This lets you change the defaults for how the SpicyText is displayed.

!!! Note
    Check out the example on custom themes! To find out how to access the examples, see [Examples](examples.md).


## Getting Started
The first thing to do is create a custom theme object:

```java
SpicyTextTheme myTheme = new SpicyTextTheme(); // create a theme object
```

The `SpicyTextTheme` initialises with the default settings, so you don't have to change anything before you can use it.

**BUT** the reason we're here is to change the default look, so let's do something nice and straightforward!

```java
myTheme.textColour = color(255, 0, 255); // set the default text colour to magenta!
```

Once you've set up your theme, you can use it in the SpicyText constructor:

```java
// Use the theme in the basic SpicyText constructor
SpicyText mySpicyText = new SpicyText(this, "This will be magenta", textSize, myTheme);

// There's also a constructor that takes a max width AND a custom theme
SpicyText wrappedText = new SpicyText(this, "Wrapped Text", textSize, maxWidth, myTheme);
```

## Properties
Of course, you can use the `SpicyTextTheme` object to change MORE than just the text colour!

Here's a full list of the properties:

| **Property**           | **Description**                                                                                                                                                                                                                                                                                                                  | **Default Value**                            |
|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------|
| `textColour`           | The default text colour, used when no `[COLOUR=...]` tags have been used OR all colours have been cleared with `[END_COLOUR]` tags.                                                                                                                                                                                              | `0` - black                                  |
| `font`                 | The font to be used for the SpicyText. This can be from one of the fonts installed on your computer using [createFont](https://processing.org/reference/createFont_.html) or loaded from a file using [loadFont](https://processing.org/reference/loadFont_.html).                                                               | `null` - uses default Processing Sketch font |
| `dropShadowOffset`     | The distance (in pixels) to offset the drop shadow. Positive values move the drop shadow towards the bottom right. A value of 0 will not produce any drop shadow.                                                                                                                                                                | `2`                                          |
| `shadowOpacity`        | The opacity of the drop shadow text. A value of `0` will make the shadow fully transparent, while a value of `1` will make it fully opaque.                                                                                                                                                                                      | `0.5`                                        |
| `textBackgroundMargin` | The distance (in pixels) to extend a characters background past it's border. Larger values will create a bigger background.                                                                                                                                                                                                      | `4`                                          |
| `cornerRadius`         | The radius of the corners of the background rectangle. A value of `0` will have sharp corners. Larger values produce more rounded corners.                                                                                                                                                                                       | `4`                                          |
| `newLineMargin`        | The distance (in pixels) to be left in between lines of the SpicyText (measured from the bottom of the previous line to the top of the new line). In practice the gap can be larger than the provided value, since the "bottom" is the bottom of hanging letters (e.g. `p`) and the "top" is the top of tall letters (e.g. `d`). | `4`                                          |
 

## Gotchas
In most cases, you'll probably create a theme, set up the values, and then pass it into the constructor of one or many SpicyText objects.

If you change the values of the theme AFTER a SpicyText object has been made using it, some unexpected behaviour can occur
(unless you call `setText` after changing the theme).

!!! Note
    Calling `setText(String newText);` will re-processes the layout **IF** the text being set is **DIFFERENT** from its original text.
    You can force the update by calling `setText(String newText, int maxWidth);` with a `maxWidth` value (use `-1` in the case that you don't want any text wrapping).  

This is because the SpicyText object processes the layout of the text when it's initialised (not every frame), but is obviously
still drawn every frame.
Therefore, any theme values used *ONLY* for display can be changed without calling `setText`, but those that impact the layout
(namely `font` and `newLineMargin`) will require calling `setText` before any changes will be visible.