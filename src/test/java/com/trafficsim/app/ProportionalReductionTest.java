package com.trafficsim.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.DoubleUnaryOperator;

import org.ejml.simple.SimpleMatrix;
import org.junit.Assert;

import com.example.MathsModel;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ProportionalReductionTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ProportionalReductionTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( ProportionalReductionTest.class );
    }

    public void testMatrixElementWiseSafeDivision()
    {
        Method matrixSafeDiv = PrivateAccessor.getPrivateMethod(MathsModel.class, "matrixElementWiseSafeDivision");

        // check if can divide safely by 0 and handle base-case: dividing m×n matrix by another m×n matrix
        SimpleMatrix a = new SimpleMatrix(new double[][] {{1,2},{3,0}});
        SimpleMatrix b = new SimpleMatrix(new double[][] {{0,2},{5,4}});

        SimpleMatrix outA = (SimpleMatrix) PrivateAccessor.runPrivateMethod(matrixSafeDiv, null, a, b);
        SimpleMatrix outE = new SimpleMatrix(new double[][] {{0,1},{3.0/5.0,0}});
        compareMatrices(outA, outE);

        // check if element-wise division works for matrices of size m×n and m×1
        a = new SimpleMatrix(new double[][] {{1,2},{3,0}});
        b = new SimpleMatrix(new double[][] {{0,2}});

        outA = (SimpleMatrix) PrivateAccessor.runPrivateMethod(matrixSafeDiv, null, a, b);
        outE = new SimpleMatrix(new double[][] {{0,1},{0,0}});
        compareMatrices(outA, outE);

        // check if element-wise division works for matrices of size m×n and 1×n
        a = new SimpleMatrix(new double[][] {{1,2},{3,0}});
        b = new SimpleMatrix(new double[][] {{0}, {2}});

        outA = (SimpleMatrix) PrivateAccessor.runPrivateMethod(matrixSafeDiv, null, a, b);
        outE = new SimpleMatrix(new double[][] {{0,0},{3.0/2.0,0}});
        compareMatrices(outA, outE);
    }

    // throw exception if matrices of invalid size
    public void testMatrixElementWiseSafeDivisionThrowsException()
    {
        Method matrixSafeDiv = PrivateAccessor.getPrivateMethod(MathsModel.class, "matrixElementWiseSafeDivision");
        
        SimpleMatrix a = new SimpleMatrix(new double[][] {{1,2},{3,0}});
        SimpleMatrix b = new SimpleMatrix(new double[][] {{0,3,5}, {2,4,1}});
        
        boolean exception = false;
        try {
            PrivateAccessor.runPrivateMethodUnsafe(matrixSafeDiv, null, a, b);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof IllegalArgumentException) {
                exception = true;
            }
        }

        assertTrue(exception);
    }

    public void testMatrixElementWiseMultiplication()
    {
        Method matrixSafeMult = PrivateAccessor.getPrivateMethod(MathsModel.class, "matrixElementWiseMultiplication");

        // base-case: multiplying m×n matrix by another m×n matrix
        SimpleMatrix a = new SimpleMatrix(new double[][] {{1,2},{3,0}});
        SimpleMatrix b = new SimpleMatrix(new double[][] {{0,2},{5,4}});

        SimpleMatrix outA = (SimpleMatrix) PrivateAccessor.runPrivateMethod(matrixSafeMult, null, a, b);
        SimpleMatrix outE = new SimpleMatrix(new double[][] {{0,4},{15,0}});
        compareMatrices(outA, outE);

        // check if element-wise multiplication works for matrices of size m×n and m×1
        a = new SimpleMatrix(new double[][] {{1,2},{3,0}});
        b = new SimpleMatrix(new double[][] {{0,2}});

        outA = (SimpleMatrix) PrivateAccessor.runPrivateMethod(matrixSafeMult, null, a, b);
        outE = new SimpleMatrix(new double[][] {{0,4},{0,0}});
        compareMatrices(outA, outE);

        // check if element-wise division works for matrices of size m×n and 1×n
        a = new SimpleMatrix(new double[][] {{1,2},{3,0}});
        b = new SimpleMatrix(new double[][] {{0}, {2}});

        outA = (SimpleMatrix) PrivateAccessor.runPrivateMethod(matrixSafeMult, null, a, b);
        outE = new SimpleMatrix(new double[][] {{0,0},{6,0}});
        compareMatrices(outA, outE);
    }

    // throw exception if matrices of invalid size
    public void testMatrixElementWiseMultiplicationThrowsException()
    {
        Method matrixSafeMult = PrivateAccessor.getPrivateMethod(MathsModel.class, "matrixElementWiseMultiplication");
        
        SimpleMatrix a = new SimpleMatrix(new double[][] {{1,2},{3,0}});
        SimpleMatrix b = new SimpleMatrix(new double[][] {{0,3,5}, {2,4,1}});
    
        boolean exception = false;
        try {
            PrivateAccessor.runPrivateMethodUnsafe(matrixSafeMult, null, a, b);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof IllegalArgumentException) {
                exception = true;
            }
        }

        assertTrue(exception);
    }

    public void testMapMatrix() {
        Method mapMatrix = PrivateAccessor.getPrivateMethod(MathsModel.class, "mapMatrix");
        SimpleMatrix a = new SimpleMatrix(new double[][] {{1,2},{3,4}});
        DoubleUnaryOperator f = x->x+2.0;
        
        SimpleMatrix outA = (SimpleMatrix) PrivateAccessor.runPrivateMethod(mapMatrix, null, a, f);
        SimpleMatrix outE = new SimpleMatrix(new double[][] {{3,4},{5,6}});
        compareMatrices(outA, outE);
    }

    public void testMaxByColumn() {
        SimpleMatrix a = new SimpleMatrix(new double[][] {{1,4},{3,2}});
        Method maxByColumn = PrivateAccessor.getPrivateMethod(MathsModel.class, "maxByColumn");

        SimpleMatrix outA = (SimpleMatrix) PrivateAccessor.runPrivateMethod(maxByColumn, null, a);
        SimpleMatrix outE = new SimpleMatrix(new double[][] {{3,4}});
        compareMatrices(outA, outE);
    }

    private void compareMatrices(SimpleMatrix actual, SimpleMatrix expected) {
        if (!actual.isIdentical(expected, 1e-10)) {
            Assert.fail("Expected matrix:\n" + expected.toString() + "\nActual matrix:\n" + actual.toString());
        }
    }
}
