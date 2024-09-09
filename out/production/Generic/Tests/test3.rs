fn main() {
    let number = 7;
    match number {
        0|2|4|6|8 => {
            println!(0);
            let half_number = number / 2;
            match number {
                0|1|2|3|4 => println!(0);,
                5|6|7|8|9 => println!(1);,
                _ => println!(-1);
            }
        },
        1|3|5|7|9 => {
            println!(1);
            let half_number = number / 2;
            match number {
                0|1|2|3|4 => println!(0);,
                5|6|7|8|9 => println!(1);,
                _ => println!(-1);
            }
        },
        _ => println!(-1);
    }
}