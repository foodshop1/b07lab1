
class Polynomial {

    private double coefficients[];

    public Polynomial() {
        coefficients = new double[]{0};

    }

    public Polynomial(double coeff[]) {

        for (int i = 0; i < coeff.length; i++) {
            this.coefficients[i] = coeff[i];
        }

    }

    public Polynomial add(Polynomial p) {

        int length1 = coefficients.length;
        int length2 = p.coefficients.length;
        int maxlength;

        if (length1 > length2) {
            maxlength = length2;
        } else {
            maxlength = length1;

        }

        double res[] = new double[maxlength];

        for (int i = 0; i < maxLength; i++) {
            double a = 0;
            double b = 0;

            if (i < length1) {
                a = coefficients[i];
            }

            if (i < length2) {
                b = p.coefficients[i];
            }

            res[i] = a + b;
        }

        return new Polynomial(res);

    }

}

// public class Driver {
//     public static void main(String[] args) {
//         Polynomial p = new Polynomial();
//         System.out.println(p.evaluate(3));
//         double[] c1 = {6, 0, 0, 5};
//         Polynomial p1 = new Polynomial(c1);
//         double[] c2 = {0, -2, 0, 0, -9};
//         Polynomial p2 = new Polynomial(c2);
//         Polynomial s = p1.add(p2);
//         System.out.println("s(0.1) = " + s.evaluate(0.1));
//         if (s.hasRoot(1)) {
//             System.out.println("1 is a root of s"); 
//         }else {
//             System.out.println("1 is not a root of s");
//         }
//     }
// }
