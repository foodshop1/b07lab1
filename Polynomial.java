
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class Polynomial {

    private double coefficients[];
    private int[] exponents;

    public Polynomial() {
        this.coefficients = new double[]{0.0};
        this.exponents = new int[]{0};
    }

    private Polynomial(double[] coeff, int[] exp) {
        this.coefficients = coeff;
        this.exponents = exp;
    }

    public Polynomial(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        br.close();

        // Defensive default if file is empty
        if (line == null || line.length() == 0) {
            this.coefficients = new double[]{0.0};
            this.exponents = new int[]{0};
            return;
        }

        // Insert a delimiter before each sign, then split on the delimiter
        line = line.replace("+", ",+");
        line = line.replace("-", ",-");
        String[] parts = line.split(",");

        ArrayList<Double> coeffList = new ArrayList<Double>();
        ArrayList<Integer> expList = new ArrayList<Integer>();

        for (int i = 0; i < parts.length; i++) {
            String term = parts[i];
            double coeff;
            int exp;

            int xPos = term.indexOf('x');
            if (xPos == -1) {
                // constant term (no 'x')
                coeff = Double.parseDouble(term);
                exp = 0;
            } else {
                // split into coefficient part and exponent part
                String coeffStr = term.substring(0, xPos);     // may be "", "+", "-", or a number
                String expStr = term.substring(xPos + 1);       // may be "" or digits

                // coefficient
                if (coeffStr.equals("") || coeffStr.equals("+")) {
                    coeff = 1.0;
                } else if (coeffStr.equals("-")) {
                    coeff = -1.0;
                } else {
                    coeff = Double.parseDouble(coeffStr);
                }

                // exponent
                if (expStr.equals("")) {
                    exp = 1; // plain "x" means x^1
                } else {
                    exp = Integer.parseInt(expStr);
                }
            }

            // keep sparse: only store non-zero coefficients
            if (coeff != 0.0) {
                coeffList.add(coeff);
                expList.add(exp);
            }
        }

        // If nothing parsed (or everything was zero), default to zero polynomial
        if (coeffList.size() == 0) {
            this.coefficients = new double[]{0.0};
            this.exponents = new int[]{0};
            return;
        }

        // Convert lists to arrays
        this.coefficients = new double[coeffList.size()];
        this.exponents = new int[expList.size()];
        for (int i = 0; i < coeffList.size(); i++) {
            this.coefficients[i] = coeffList.get(i);
            this.exponents[i] = expList.get(i);
        }
    }

    public Polynomial(double[] coeff) {
        if (coeff == null || coeff.length == 0) {
            this.coefficients = new double[]{0.0};
            this.exponents = new int[]{0};
        } else {
            int count = 0;
            for (int i = 0; i < coeff.length; i++) {
                if (coeff[i] != 0.0) {
                    count++;
                }
            }
            if (count == 0) {
                this.coefficients = new double[]{0.0};
                this.exponents = new int[]{0};
            } else {
                this.coefficients = new double[count];
                this.exponents = new int[count];
                int k = 0;
                for (int i = 0; i < coeff.length; i++) {
                    if (coeff[i] != 0.0) {
                        this.coefficients[k] = coeff[i];
                        this.exponents[k] = i; // exponent is index i
                        k++;
                    }
                }
            }
        }
    }

    public Polynomial add(Polynomial p) {

        // worst-case size (no shared exponents)
        double[] tmpC = new double[this.coefficients.length + p.coefficients.length];
        int[] tmpE = new int[this.coefficients.length + p.coefficients.length];

        int i = 0, j = 0, k = 0;

        while (i < this.coefficients.length && j < p.coefficients.length) {
            int e1 = this.exponents[i];
            int e2 = p.exponents[j];

            if (e1 == e2) {
                double s = this.coefficients[i] + p.coefficients[j];
                if (s != 0.0) {         // skip if they cancel
                    tmpC[k] = s;
                    tmpE[k] = e1;
                    k++;
                }
                i++;
                j++;
            } else if (e1 < e2) {
                tmpC[k] = this.coefficients[i];
                tmpE[k] = e1;
                k++;
                i++;
            } else { // e2 < e1
                tmpC[k] = p.coefficients[j];
                tmpE[k] = e2;
                k++;
                j++;
            }
        }

        // copy leftovers
        while (i < this.coefficients.length) {
            tmpC[k] = this.coefficients[i];
            tmpE[k] = this.exponents[i];
            k++;
            i++;
        }
        while (j < p.coefficients.length) {
            tmpC[k] = p.coefficients[j];
            tmpE[k] = p.exponents[j];
            k++;
            j++;
        }

        // handle zero result
        if (k == 0) {
            return new Polynomial();
        }

        return new Polynomial(tmpC, tmpE);

    }

    public double evaluate(double x) {
        double sum = 0.0;
        for (int i = 0; i < coefficients.length; i++) {
            sum += coefficients[i] * Math.pow(x, exponents[i]);
        }
        return sum;
    }

    public boolean hasRoot(double x) {
        return evaluate(x) == 0.0;

    }

    public Polynomial multiply(Polynomial p) {
        Polynomial result = new Polynomial(); // start with zero polynomial

        for (int i = 0; i < this.coefficients.length; i++) {
            for (int j = 0; j < p.coefficients.length; j++) {
                double c = this.coefficients[i] * p.coefficients[j];
                int e = this.exponents[i] + p.exponents[j];

                if (c != 0.0) {
                    // build a 1-term polynomial
                    double[] coeff = {c};
                    int[] exp = {e};
                    Polynomial term = new Polynomial(coeff, exp);

                    // combine like terms
                    result = result.add(term);
                }
            }
        }

        return result;
    }

    public void saveToFile(String filename) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        String polyStr = "";

        for (int i = 0; i < coefficients.length; i++) {
            double coeff = coefficients[i];
            int exp = exponents[i];

            if (coeff == 0.0) {
                continue;
            }

            // Add sign (not for the first term if positive)
            if (i > 0 && coeff > 0) {
                polyStr = polyStr + "+";
            }

            // Coefficient handling
            if (exp == 0) {
                if ((int) coeff == coeff) {
                    polyStr = polyStr + (int) coeff;
                } else {
                    polyStr = polyStr + coeff;
                }
            } else {
                if (coeff == 1.0) {
                    // skip "1"
                } else if (coeff == -1.0) {
                    polyStr = polyStr + "-";
                } else {
                    if ((int) coeff == coeff) {
                        polyStr = polyStr + (int) coeff;
                    } else {
                        polyStr = polyStr + coeff;
                    }
                }

                // Variable part
                polyStr = polyStr + "x";
                if (exp != 1) {
                    polyStr = polyStr + exp;
                }
            }
        }

        bw.write(polyStr);
        bw.close();
    }

}

class Driver {

    public static void main(String[] args) throws java.io.IOException {
        // Test default polynomial (zero)
        Polynomial p = new Polynomial();
        System.out.println("p(3) = " + p.evaluate(3));

        // Test constructor from array
        double[] c1 = {6, 0, 0, 5};   // 6 + 5x^3
        Polynomial p1 = new Polynomial(c1);
        double[] c2 = {0, -2, 0, 0, -9}; // -2x - 9x^4
        Polynomial p2 = new Polynomial(c2);

        // Test add
        Polynomial sum = p1.add(p2);
        System.out.println("sum(1) = " + sum.evaluate(1));

        // Test multiply
        Polynomial prod = p1.multiply(p2);
        System.out.println("prod(1) = " + prod.evaluate(1));

        // Test hasRoot
        if (sum.hasRoot(1)) {
            System.out.println("1 is a root of sum");
        } else {
            System.out.println("1 is not a root of sum");
        }

        // Test saveToFile
        p1.saveToFile("p1.txt");
        p2.saveToFile("p2.txt");
        sum.saveToFile("sum.txt");
        prod.saveToFile("prod.txt");
    }

}
