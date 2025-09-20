
class Polynomial {

    private double coefficients[];

    public Polynomial() {
        coefficients = new double[1];
        coefficients[0] = 0.0;

    }

    public Polynomial(double[] coeff) {
        if (coeff == null || coeff.length == 0) {
            this.coefficients = new double[1];
            this.coefficients[0] = 0.0;
        } else {
            this.coefficients = new double[coeff.length];
            for (int i = 0; i < coeff.length; i++) {
                this.coefficients[i] = coeff[i];
            }
        }
    }

    public Polynomial add(Polynomial p) {

        int length1 = coefficients.length;
        int length2 = p.coefficients.length;
        int maxlength;

        if (length1 > length2) {
            maxlength = length1;
        } else {
            maxlength = length2;

        }

        double res[] = new double[maxlength];

        for (int i = 0; i < maxlength; i++) {
            double a = 0;
            double b = 0;

            if (i < length1) {
                a = this.coefficients[i];
            }

            if (i < length2) {
                b = p.coefficients[i];
            }

            res[i] = a + b;
        }

        return new Polynomial(res);

    }

    public double evaluate(double x) {
        double sum = 0.0;
        double xPower = 1.0;
        for (int i = 0; i < coefficients.length; i++) {
            sum += coefficients[i] * xPower;
            xPower *= x;
        }
        return sum;
    }

    public boolean hasRoot(double x) {
        return evaluate(x) == 0.0;

    }

}

class Driver {

    public static void main(String[] args) {
        Polynomial p = new Polynomial();
        System.out.println(p.evaluate(3));
        double[] c1 = {6, 0, 0, 5};
        Polynomial p1 = new Polynomial(c1);
        double[] c2 = {0, -2, 0, 0, -9};
        Polynomial p2 = new Polynomial(c2);
        Polynomial s = p1.add(p2);
        System.out.println("s(0.1) = " + s.evaluate(0.1));
        if (s.hasRoot(1)) {
            System.out.println("1 is a root of s");
        } else {
            System.out.println("1 is not a root of s");
        }
    }
}
