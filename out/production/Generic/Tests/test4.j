public static void main () {
    int a=0, b=5;
    int c = 11;
    while (c > 0) {
        if (a < b) {
            a = a + 1;
            c = c - 2;
        } else if (a > b) {
            b = b + 1;
            c = c / 2;
        } else {}
    }
    switch (c) {
        case 0: break;
        case 1: System.out.println(a + c);
        case 2: System.out.println(b * c);
        default:
            System.out.println(a);
            System.out.println(b);
            System.out.println(c);
    }
}