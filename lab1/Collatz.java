    /** Class that prints the Collatz sequence starting from a given number.
 *  @author kagayatsuki
 */
    public class Collatz {

        /** Returns the next number in the Collatz sequence. */
        public static int nextNumber(int n) {
            if (n <= 0) {
                throw new IllegalArgumentException("Input must be a positive integer");
            }
            if (n % 2 == 0) {
                return n / 2; // Even: divide by 2
            } else {
                return 3 * n + 1; // Odd: 3n + 1
            }
        }

        public static void main(String[] args) {
            int n = 5;
            System.out.print(n + " ");
            while (n != 1) {
                n = nextNumber(n);
                System.out.print(n + " ");
            }
            System.out.println();
        }
    }