import java.io.*;
import java.util.Arrays;

/*============================================================================
| Assignment: pa01 - Encrypting a plaintext file using the Hill cipher
|
| Author: Your name here
| Language: c, c++, Java, go, python
| To Compile: javac pa01.java
|             gcc -o pa01 pa01.c
|             g++ -o pa01 pa01.cpp
|             go build pa01.go
|             rustc pa01.rs
| To Execute: java -> java pa01 kX.txt pX.txt
|          or c++ -> ./pa01 kX.txt pX.txt
|          or c -> ./pa01 kX.txt pX.txt
|          or go -> ./pa01 kX.txt pX.txt
|          or rust -> ./pa01 kX.txt pX.txt
|          or python -> python3 pa01.py kX.txt pX.txt
|                       where kX.txt is the keytext file
|                       and pX.txt is plaintext file
| Note:
|   All input files are simple 8 bit ASCII input
|   All execute commands above have been tested on Eustis
|
|      Class: CIS3360 - Security in Computing - Summer 2024
| Instructor: McAlpin
|   Due Date: per assignment
+===========================================================================*/

public class pa01 {

    // reads the key matrix from the input file
    public static int[][] readKeyMatrix(String filename) throws IOException {
        BufferedReader br = null;
        int[][] keyMatrix = null;

        try {
            br = new BufferedReader(new FileReader(new File(filename))); // open the file
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim(); // trim whitespace
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // skip empty lines or comments
                }
                int size = Integer.parseInt(line); // read the size of the matrix
                keyMatrix = new int[size][size]; // initialize the matrix
                for (int i = 0; i < size; i++) {
                    String[] values = br.readLine().trim().split("\\s+"); // read the row
                    for (int j = 0; j < size; j++) {
                        keyMatrix[i][j] = Integer.parseInt(values[j]); // parse the values
                    }
                }
                break;
            }
        } finally {
            if (br != null) {
                br.close(); // close the reader
            }
        }

        return keyMatrix;
    }

    // converts letters to lowercase
    public static char[] readLetters(String filename, int blockSize) throws IOException {
        char[] letters = new char[10000]; // max chars 10k
        int index = 0;

        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(new File(filename))); // open the file
            int c;
            while ((c = br.read()) != -1 && index < letters.length) {
                if (Character.isLetter(c)) {
                    letters[index++] = Character.toLowerCase((char) c); // convert to lowercase
                }
            }
        } finally {
            if (br != null) {
                br.close(); // close the reader
            }
        }

        // pad using x
        int paddedLength = (index + blockSize - 1) / blockSize * blockSize; // calculate padded length
        char[] paddedLetters = Arrays.copyOf(letters, paddedLength); // copy letters to new array
        for (int i = index; i < paddedLength; i++) {
            paddedLetters[i] = 'x';
        }

        return paddedLetters;
    }

    // encrypt the text
    public static char[] encrypt(char[] message, int[][] keyMatrix) {
        int matrixSize = keyMatrix.length;
        int paddedLength = message.length;
        char[] encryptedMessage = new char[paddedLength];
        for (int i = 0; i < paddedLength; i += matrixSize) {
            int[][] messageBlock = new int[matrixSize][1]; // create message block
            for (int j = 0; j < matrixSize; j++) {
                messageBlock[j][0] = message[i + j] - 'a'; // convert to int
            }
            // multiply with key matrix
            int[][] encryptedBlock = multiply(keyMatrix, messageBlock);
            for (int j = 0; j < matrixSize; j++) {
                encryptedMessage[i + j] = (char) ((encryptedBlock[j][0] % 26) + 'a'); // convert back to char
            }
        }

        return encryptedMessage;
    }

    // matrix multiplication
    public static int[][] multiply(int[][] A, int[][] B) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int rowsB = B.length;
        int colsB = B[0].length;

        if (colsA != rowsB) {
            throw new IllegalArgumentException("Incompatible dimensions for matrix multiplication.");
        }

        int[][] C = new int[rowsA][colsB]; // initialize result matrix

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                C[i][j] = 0;
                for (int k = 0; k < colsA; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }

        return C;
    }

    // prints arr with newline
    public static void printTextWithNewline(char[] text) {
        String textString = new String(text);
        int length = textString.length();
        int blockSize = 80; // 80 letters per row

        for (int i = 0; i < length; i += blockSize) {
            System.out.println(textString.substring(i, Math.min(i + blockSize, length))); // print block
        }
    }

    public static void main(String[] args) {
        // takes the key file arg
        String keyFilename = args[0];
        // takes the plaintext file arg
        String plaintextFilename = args[1]; 

        try {
            int[][] keyMatrix = readKeyMatrix(keyFilename); // read the key matrix from the file
            char[] plaintext = readLetters(plaintextFilename, keyMatrix.length); // read the plaintext and pad it
            char[] ciphertext = encrypt(plaintext, keyMatrix); // encrypt the plaintext using the key matrix

            // echo the key matrix
            System.out.println("\nKey matrix:");
            for (int[] row : keyMatrix) {
                for (int num : row) {
                    System.out.printf("%4d", num);
                }
                System.out.println();
            }

            // output the plaintext
            System.out.println("\nPlaintext:");
            printTextWithNewline(plaintext);

            // output the ciphertext
            System.out.println("\nCiphertext:");
            printTextWithNewline(ciphertext);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


/*=============================================================================
| I Christopher Stephenson (ch944543) affirm that this program is
| entirely my own work and that I have neither developed my code together with
| any another person, nor copied any code from any other person, nor permitted
| my code to be copied or otherwise used by any other person, nor have I
| copied, modified, or otherwise used programs created by others. I acknowledge
| that any violation of the above terms will be treated as academic dishonesty.
+=============================================================================*/