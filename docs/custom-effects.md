# Creating Custom Effects
I can't predict what you'll want your text to do. To get your text moving the way you want, you might need the help of some custom effects!


!!! Note
    Check out the example on custom effects! To find out how to access the examples, see [Examples](examples.md).


## Getting Started

### Custom effect function
The first thing to do is create a custom effect function.
This will take in a SpicyTextChar (which holds information about the character the effect is being applied to), and a 
CharEffectParams object (which holds information about how the character will be displayed).

```java
// A custom effect function that takes in a SpicyTextChar and the CharEffectParams 
void myCustomEffect(SpicyTextChar spicyChar, CharEffectParams effectParams) {
    // your effect here
}
```

You might notice that it's a void function, so how do the effects get applied? You simply modify the values in the
CharEffectParams, as these are what are used to display a character.

### Registering the effect
We'll get back to the CharEffectParams and the custom effect function in a moment, first let's see how to register
the effect with the SpicyText library. This is done by simply calling the static `SpicyText.customEffect` function, 
and passing in the name of the effect, and a reference to the custom effect function we just created:

```java
SpicyText.customEffect("MY_EFFECT", this::myCustomEffect);
```

**Alternatively, the effect function can be made in-place:**
```java
SpicyText.customEffect("MY_EFFECT", (spicyChar, effectParams) -> {
    // your effect here    
});
```

### Using your effect
Now that the effect has been registered, we can use it in an `[EFFECT=...]` tag, in our case we would use `[EFFECT=MY_EFFECT]`.
You must use whatever name you put in the `SpicyText.customEffect` function to reference your effect in the `[EFFECT=...]` tag. 

!!! Note
    It is possible to replace effects by using an existing name when registering your effect. Use a unique name to avoid this!

## Custom Effect Functions
As promised, we'll return our attention to the Custom Effect Functions, since this is where the magic happens.

First things first, let's take a look at what data we've got to work with.

### SpicyTextChar
The SpicyTextChar stores the raw information for a processed character of a SpicyText String.
This information is used to populate the initial values of the CharEffectParams, which is in turn used for display.

Here's a full list of the properties available on the SpicyTextChar object:

| **Property**     | **Description**                                                                                                                                                                |
|------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `char c`         | The literal character to be displayed.                                                                                                                                         |
| `int colour`     | The text colour to be used (the colour at the top of the stack if `[COLOUR=...]` tags have been used, otherwise the text colour set by the theme.                              |
| `int background` | The background colour to be used (the background colour at the top of the stack if `[BACKGROUND=...]` tags have been used, otherwise `-1` and no background will be displayed. |
| `int width`      | The width of the character (in pixels) when displayed with the textSize and font described by the SpicyText object.                                                            |
| `int height`     | The height of the character (in pixels) when displayed with the textSize and font described by the SpicyText object.                                                           |
| `int index`      | The index of the character in the entire displayed text of the SpicyText.                                                                                                      |

### CharEffectParams
When a character is being displayed, a CharEffectParams object is initialised with values from the relevant SpicyTextChar
and fed through all the effect functions that are being applied to the given character.

The effect functions will manipulate the values in the CharEffectParams, and these values will then determine how the
character is displayed.

Here's a full list of the properties available on the SpicyTextChar object:

| **Property**     | **Description**                                                                                    |
|------------------|----------------------------------------------------------------------------------------------------|
| `int colour`     | The text colour to be used when displaying the character.                                          |
| `int background` | The background colour to be used (if `-1`, no background will be displayed).                       |
| `int time`       | The elapsed time of the sketch in milliseconds. This is useful for creating time-based animations. |
| `float x`        | The x position (in pixels) of the character, relative to the left-most character.                  |
| `float y`        | The y position (in pixels) of the character, relative to the top-most character.                   |
| `float rotation` | The rotation (in radians) of the character.                                                        |

### Example Use
To see how these are used, let's take a look at the built-in `WAVE` effect function:

```java
// Wave Effect function
void waveEffect(SpicyTextChar c, CharEffectParams params) {
    // Calculate a sin wave value, using the character's index and the animation time
    float off = PApplet.sin(c.index - params.time / 500f);

    // Change the y position using the animated value
    params.y -= off * c.height/4;
}
```

As you can see, it's actually quite simple! All we're doing is shifting the character's `y` position slightly up and down.

* By using the sin function, we're calculating a value that changes smoothly over time
(this is important for make nice animations!).
* By including `c.index` (not just `params.time`!), we're able to offset the animation slightly for each character,
meaning we get a wave, instead of each character moving up and down at the same time.
* You'll notice that we **MODIFY** the `y` position rather than **SET** it (using `-=` not `=`). This is because
the `params` object is shared for all effects on a given character. If we were to set the value, we'd be losing all
the work done by effects that came before this one. By modifying it instead, we can have multiple effects all combining
to produce results!

# Now it's your turn!
Hopefully you've got a better understanding of how to write your own custom effect functions and register them
with the SpicyText library!

Now it's your turn to create some funky text effects to add some spice to your text!

Again, check out the [example](examples.md) on custom effects for a good place to get started! 