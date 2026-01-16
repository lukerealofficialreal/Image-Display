package bmp;

public record BmpColor(int red, int green, int blue, int alpha) {
    public BmpColor(int red, int green, int blue) {
        this(red, green, blue, 255);
    }
    public int[] RGBArray() {
        return new int[] {red, green, blue, alpha};
    }

    @Override
    public String toString() {
        return "R=" + red + ", G=" + green + ", B=" + blue + ", A=" + alpha;
    }
}

