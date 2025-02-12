package com.example;

import java.util.function.DoubleUnaryOperator;
import org.ejml.simple.SimpleMatrix;


public class MathsModel {
    /**
     * Calculates the outflow from road 0 into roads 1, 2 and 3.
     * @param D_out The maximum outflow that the road can provide
     * @param S_1 The maximum inflow allowed into road 1
     * @param S_2 The maximum inflow allowed into road 2
     * @param S_3 The maximum inflow allowed into road 3
     * @param Q_1_P The proportion of traffic from road 0 that wishes to exit the junction via road 1
     * @param Q_2_P The proportion of traffic from road 0 that wishes to exit the junction via road 2
     * @param Q_3_P The proportion of traffic from road 0 that wishes to exit the junction via road 3
     * @return The actual outflow from road 0 into the junction
     */
    public static double ProportionalReduction(double D_out, double S_1, double S_2, double S_3, double Q_1_P, double Q_2_P, double Q_3_P) {
        // This is an implementation of the proportional reduction algorithm presented by Tadeusiak in
        // Traffic Flow Modelling: conceptual model and specific implementations
        // available at: https://api.semanticscholar.org/CorpusID:145036967
        SimpleMatrix D = new SimpleMatrix(new double[][] {{D_out}, {0}, {0}, {0}});
        SimpleMatrix S = new SimpleMatrix(new double[][] {{0, S_1, S_2, S_3}});

        SimpleMatrix ϕ = new SimpleMatrix(new double[][] {
            {0,     0, 0, 0},
            {Q_1_P, 1, 0, 0},
            {Q_2_P, 0, 1, 0},
            {Q_3_P, 0, 0, 1}
        });

        SimpleMatrix DD     = MathsModel.matrixElementWiseMultiplication(ϕ, D);
        SimpleMatrix Γ      = MathsModel.matrixElementWiseSafeDivision(DD, S.transpose());
        SimpleMatrix Γprime = MathsModel.mapMatrix(Γ, x->max(1,x));
        SimpleMatrix ϕbool  = MathsModel.mapMatrix(ϕ, x->(x==0)?0:1);
        SimpleMatrix ϕprime = MathsModel.matrixElementWiseMultiplication(ϕbool, Γprime);
        SimpleMatrix Γmax   = MathsModel.maxByColumn(ϕprime);
        SimpleMatrix ϕpr    = MathsModel.matrixElementWiseSafeDivision(ϕ, Γmax);
        SimpleMatrix Qpr    = MathsModel.matrixElementWiseMultiplication(ϕpr, D.transpose());

        double outflow = -1;
        for (int col=0; col<4; col++) {
            outflow += Qpr.get(0, col);
        }

        return outflow;
    }

    // returns the element-wise division of two matrices. Anything divided by 0 yields 0
    // given matrix a[i×j] and b[i×j]: the resulting matrix c is defined c[i,j]=a[i,j]/b[i,j]
    // given matrix a[i×j] and b[i×1]: the resulting matrix c is defined c[i,j]=a[i,j]/b[i,1]
    // given matrix a[i×j] and b[1×j]: the resulting matrix c is defined c[i,j]=a[i,j]/b[1,j]
    private static SimpleMatrix matrixElementWiseSafeDivision(SimpleMatrix a, SimpleMatrix b) {
        int rowsA = a.getNumRows();
        int colsA = a.getNumCols();
        int rowsB = b.getNumRows();
        int colsB = b.getNumCols();
        
        if (rowsA == rowsB && colsA == colsB) {
            SimpleMatrix out = new SimpleMatrix(rowsA, colsA);
            for (int row=0; row<rowsA; row++) {
                for (int col=0; col<colsA; col++) {
                    double elemA = a.get(row, col);
                    double elemB = b.get(row, col);
                    out.set(row, col, MathsModel.safeDiv(elemA, elemB));
                }
            }
            return out;
        }
        
        else if (rowsA == rowsB && colsB == 1) {
            SimpleMatrix out = new SimpleMatrix(rowsA, colsA);
            for (int row=0; row<rowsA; row++) {
                for (int col=0; col<colsA; col++) {
                    double elemA = a.get(row,col);
                    double elemB = b.get(row,0);
                    out.set(row, col, MathsModel.safeDiv(elemA, elemB));
                }
            }
            return out;
        }
        
        else if (colsA == colsB && rowsB == 1) {
            SimpleMatrix out = new SimpleMatrix(rowsA, colsA);
            for (int row=0; row<rowsA; row++) {
                for (int col=0; col<colsA; col++) {
                    double elemA = a.get(row,col);
                    double elemB = b.get(0,col);
                    out.set(row, col, MathsModel.safeDiv(elemA, elemB));
                }
            }
            return out;
        }

        else {
            throw new IllegalArgumentException("Matrices of incorrect size! a is of size " + rowsA + "×" + colsA + " so b should be of size " + rowsA + "×" + colsA + ", " + rowsA + "×" + 1 + ", " + 1 + "×" + colsA + ", but is of size " + rowsB + "×" + colsB);
        }

    }

    // safe division. Anything divided by 0 yields 0
    private static double safeDiv(double a, double b) {
        if (b == 0) { return 0; }
        else { return a/b; }
    }

    // returns the element-wise multiplication of two matrices
    // given matrix a[i×j] and b[i×j]: the resulting matrix c is defined c[i,j]=a[i,j]×b[i,j]
    // given matrix a[i×j] and b[i×1]: the resulting matrix c is defined c[i,j]=a[i,j]×b[i,1]
    // given matrix a[i×j] and b[1×j]: the resulting matrix c is defined c[i,j]=a[i,j]×b[1,j]
    private static SimpleMatrix matrixElementWiseMultiplication(SimpleMatrix a, SimpleMatrix b) {
        int rowsA = a.getNumRows();
        int colsA = a.getNumCols();
        int rowsB = b.getNumRows();
        int colsB = b.getNumCols();
        
        if (rowsA == rowsB && colsA == colsB) {
            return a.elementMult(b);
        }
        
        else if (rowsA == rowsB && colsB == 1) {
            SimpleMatrix out = new SimpleMatrix(rowsA, colsA);
            for (int row=0; row<rowsA; row++) {
                for (int col=0; col<colsA; col++) {
                    double elemA = a.get(row,col);
                    double elemB = b.get(row,0);
                    out.set(row, col, elemA*elemB);
                }
            }
            return out;
        }
        
        else if (colsA == colsB && rowsB == 1) {
            SimpleMatrix out = new SimpleMatrix(rowsA, colsA);
            for (int row=0; row<rowsA; row++) {
                for (int col=0; col<colsA; col++) {
                    double elemA = a.get(row,col);
                    double elemB = b.get(0,col);
                    out.set(row, col, elemA*elemB);
                }
            }
            return out;
        }

        else {
            throw new IllegalArgumentException("Matrices of incorrect size! a is of size " + rowsA + "×" + colsA + " so b should be of size " + rowsA + "×" + colsA + ", " + rowsA + "×" + 1 + ", " + 1 + "×" + colsA + ", but is of size " + rowsB + "×" + colsB);
        }
    }

    // applies a function to every element in a matrix, returns the new, updated matrix
    private static SimpleMatrix mapMatrix(SimpleMatrix m, DoubleUnaryOperator f) {
        int rows = m.getNumRows();
        int cols = m.getNumCols();
        SimpleMatrix out = new SimpleMatrix(rows, cols);

        for (int row=0; row<rows; row++) {
            for (int col=0; col<cols; col++) {
                double elem = m.get(row, col);
                out.set(row, col, f.applyAsDouble(elem));
            }
        }

        return out;
    }

    // calculates the max of two values
    private static double max(double a, double b) {
        return (a>b)?a:b;
    }

    // returns an n×1 matrix a' given an n×m matrix a.
    // Each element in a' is the maximum value of every column in a
    private static SimpleMatrix maxByColumn(SimpleMatrix m) {
        int rows = m.getNumRows();
        int cols = m.getNumCols();
        SimpleMatrix out = new SimpleMatrix(1, cols);

        for (int col=0; col<cols; col++) {
            double max = -1;
            for (int row=0; row<rows; row++) {
                double elem = m.get(row, col);
                if (elem > max) { max = elem; }
            }
            out.set(0, col, max);
        }

        return out;
    }
}
