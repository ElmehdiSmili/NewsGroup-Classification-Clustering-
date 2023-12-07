package Metrics;

import java.util.HashSet;

@SuppressWarnings("unused")
public class F1score {

    double precision;
    double rappel;
    double f1score;

    public F1score() {

    }

    public double calcul_p(Accuracy a, String classe) {
        HashSet<String> r1 = new HashSet<>(a.R_Sf.get(classe).get(0));
        HashSet<String> s1 = new HashSet<>(a.R_Sf.get(classe).get(1));
        HashSet<String> s2 = new HashSet<>(a.R_Sf.get(classe).get(1));

        r1.retainAll(s1);
        if (s2.size() > 0) {
            precision = (double) r1.size() / s2.size();
        } else {
            precision = 0.0; // Handle division by zero
        }
        return precision;
    }

    public double calcul_r(Accuracy a, String classe) {
        HashSet<String> r1 = new HashSet<>(a.R_Sf.get(classe).get(0));
        HashSet<String> s1 = new HashSet<>(a.R_Sf.get(classe).get(1));
        HashSet<String> r2 = new HashSet<>(a.R_Sf.get(classe).get(0));

        r1.retainAll(s1);
        if (r2.size() > 0) {
            rappel = (double) r1.size() / r2.size();
        } else {
            rappel = 0.0; // Handle division by zero
        }
        return rappel;
    }

    public double calcul_f(Accuracy a, String classe) {
        double P = calcul_p(a, classe);
        double R = calcul_r(a, classe);

        if (P + R > 0) {
            f1score = 2 * P * R / (P + R);
        } else {
            f1score = 0.0; // Handle division by zero
        }
        return f1score;
    }
}
