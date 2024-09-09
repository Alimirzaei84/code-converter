void main () {
    int n = -1000;
    int p = 1;
    while (1) {
        if (n % 2) {
            n = n + p;
            p = p + 3;
            continue;
        }
        else if (n > 100000) break;
        else {
            int counter = -n;
            while (counter) {
                cout << counter;
                counter = counter / 2;
            }
        }
    }
}