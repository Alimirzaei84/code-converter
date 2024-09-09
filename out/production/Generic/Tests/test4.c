void main () {
    int x=0, y=5,z=11;
    while (z > 0) {
        if (x < y) {
            x = x + 1;
            z = z - 2;
        } else if (x > y) {
            y = y + 1;
            z = z / 2;
        } else {}
    }
    switch (z) {
        case 0: break;
        case 1: cout << x + z;
        case 2: cout << y * z;
        default: cout << x << y << z;
    }
}