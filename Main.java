package encryptdecrypt;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

interface CryptoAlgorithms{
    public String encode(String stringToEncode);

    public String decode(String stringToDecode);
}

enum CRYPTO_ALGORITHMS_TYPE{
    SHIFT, UNICODE;
}

class UnicodeCryptoAlgorithm implements CryptoAlgorithms{
    private int shift;

    UnicodeCryptoAlgorithm(int shift){
        this.shift = shift;
    }

    @Override
    public String encode(String stringToEncode){
        StringBuilder result = new StringBuilder();

        for (char c: stringToEncode.toCharArray()) {
            result.append((char) (c + shift));
        }

        return result.toString();
    }

    @Override
    public String decode(String stringToDecode){
        StringBuilder result = new StringBuilder();
        for (char c: stringToDecode.toCharArray()) {
            result.append((char) (c - shift));
        }
        return result.toString();
    }

}

class ShiftCryptoAlgorithm implements CryptoAlgorithms{

    private final int ALPHABET_SIZE = 26;

    private String smallAlphabet = "abcdefghijklmnopqrstuvwxyz";
    private String bigAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private int shift;

    ShiftCryptoAlgorithm(int shift){
        this.shift = shift;
    }

    @Override
    public String encode(String stringToEncode) {
        StringBuilder result = new StringBuilder("");

        for (char c: stringToEncode.toCharArray()){
            if (c >= 'a' && c <= 'z'){
                result.append(smallAlphabet.charAt((c - 'a' + this.shift) % ALPHABET_SIZE));
            }
            else if (c >= 'A' && c <= 'Z') {
                result.append(bigAlphabet.charAt((c - 'A' + this.shift) % ALPHABET_SIZE));
            }
            else {
                result.append(c);
            }
        }

        return result.toString();
    }

    @Override
    public String decode(String stringToDecode){
        StringBuilder result = new StringBuilder("");

        for (char c: stringToDecode.toCharArray()){
            if (c >= 'a' && c <= 'z'){
                result.append(smallAlphabet.charAt((ALPHABET_SIZE + c - 'a' - this.shift) % ALPHABET_SIZE));
            }
            else {
                result.append(bigAlphabet.charAt((ALPHABET_SIZE + c - 'A' - this.shift) % ALPHABET_SIZE));
            }
        }

        return result.toString();
    }
}

class AlgorithmFactory {
    public static CryptoAlgorithms getAlgorithm (CRYPTO_ALGORITHMS_TYPE type, int shift){
        switch (type){
            case UNICODE:
                return new UnicodeCryptoAlgorithm(shift);
            case SHIFT:
            default:
                return new ShiftCryptoAlgorithm(shift);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        boolean encode = true;
        String message = "";
        int shift = 0;
        String pathToInputFile = "";
        String pathToOutputFile = "";
        CRYPTO_ALGORITHMS_TYPE alg = CRYPTO_ALGORITHMS_TYPE.SHIFT;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-mode":
                    encode = "enc".equals(args[i + 1]);
                    break;
                case "-key":
                    shift = Integer.parseInt(args[i + 1]);
                    break;
                case "-data":
                    message = args[i + 1];
                    break;
                case "-in":
                    pathToInputFile = args[i + 1];

                    if (!message.equals(""))
                        break;

                    try {
                        message = Files.readString(Paths.get(pathToInputFile), StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        System.out.println("Error");
                    }

                    break;

                case "-out":
                    pathToOutputFile = args[i + 1];
                    break;

                case "-alg":
                    alg = args[i + 1].equals("shift") ? CRYPTO_ALGORITHMS_TYPE.SHIFT : CRYPTO_ALGORITHMS_TYPE.UNICODE;
                    break;

                default:
            }
        }

        CryptoAlgorithms algorithm  = AlgorithmFactory.getAlgorithm(alg, shift);
        String result = encode ? algorithm.encode(message) : algorithm.decode(message);

        if (pathToOutputFile.equals("")) {
            System.out.println(result);
        } else {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(pathToOutputFile));
                writer.write(result);
                writer.close();
            }
            catch (Exception e){
                System.out.println("Error occurred while writing info to a file");
            }
        }
    }


}
