fn main () {
    let x = 0;
    let y = 1;
    let index = 10;
    while (! index == 0) {
        let temp = x + y;
        x = y;
        y = temp;
        index = index - 1;
    }
    println! (y);
}