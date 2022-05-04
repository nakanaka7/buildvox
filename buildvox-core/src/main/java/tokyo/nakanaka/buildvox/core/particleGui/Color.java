package tokyo.nakanaka.buildvox.core.particleGui;

/**
 * RGB Color. Each R, G, B component is between 0 and 255 (inclusive). Now this class is used only by spawning
 * particles feature.
 * @see <a href="https://www.rapidtables.com/web/color/RGB_Color.html">RapidTables, HOME>WEB>COLOR>RGB color</a> (last check 2022/02/27)
 */

public class Color {
    private int red;
    private int green;
    private int blue;
    /** red */
    public static final Color RED = new Color(0xff, 0x00, 0x00);
    /** lime */
    public static final Color LIME = new Color(0x00, 0xff, 0x00);
    /** blue */
    public static final Color BLUE = new Color(0x00, 0x00, 0xff);
    /** yellow */
    public static final Color YELLOW = new Color(0xff, 0xff, 0x00);
    /** cyan */
    public static final Color CYAN = new Color(0x00, 0xff, 0xff);
    /** magenta */
    public static final Color MAGENTA = new Color(0xff, 0x00, 0xff);

    /**
     * Construct a color. Each R, G, B component is between 0 and 255 (inclusive).
     * @param red the red(R) component
     * @param green the red(G) component
     * @param blue the red(B) component
     * @throws IllegalArgumentException if R, G, or B is less than 0 or larger than 255.
     */
    public Color(int red, int green, int blue) {
        if(red < 0 || green < 0 || blue < 0 || red >= 256 || green >= 256 || blue >= 256){
            throw new IllegalArgumentException();
        }
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Gets the red component.
     * @return the red component.
     */
    @SuppressWarnings("unused")
    public int red() {
        return red;
    }

    /**
     * Get the green component.
     * @return the green component.
     */
    @SuppressWarnings("unused")
    public int green() {
        return green;
    }

    /**
     * Get the blue component.
     * @return the blue component.
     */
    @SuppressWarnings("unused")
    public int blue() {
        return blue;
    }

}
