package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(final int[][] matrixA, final int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int[][] matrixBT = new int[matrixSize][matrixSize];
        List<Future<Boolean>> futures = new LinkedList<Future<Boolean>>();
        CompletionService<Boolean> completionService = new ExecutorCompletionService<Boolean>(executor);

        for (int i = 0; i < matrixSize; i++ ) {
            for (int j = 0; j < matrixSize; j++) {
                matrixBT[j][i] = matrixB[i][j];
            }
        }

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                final int row = i;
                final int column = j;

                futures.add(completionService.submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        int sum = 0;

                        for (int k = 0; k < matrixSize; k++) {
                            sum += matrixA[row][k] * matrixBT[column][k];
                        }

                        matrixC[row][column] = sum;

                        return true;
                    }
                }));
            }
        }

        while (!futures.isEmpty()) {
            Future<Boolean> completed = completionService.poll();
            if (completed != null) {
                futures.remove(completed);
            }
        }

        return matrixC;
    }

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
