public static void main () {
    int h = 10, g=-10, f=1;
    while ((h > 0 && g < 0) || !f) {
        h = h - f;
        g = g + f;
        f = 2 * f;
    }
    System.out.println(f * (h + g));
    System.out.println(f * (h - g));
    System.out.println(f);
}