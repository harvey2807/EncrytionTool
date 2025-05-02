package edu.hcmuaf.model.basic;

import java.io.*;
import java.util.Random;

public class HillCipher {
    private int[][] keyMatrix;
    private int[][] inverseKeyMatrix;
    private boolean addPadding = false;
    private int sizeKey = 2;
    static final String ALPHABET_VN = "0123456789aáàảãạăắằẳẵặâấầẩẫậbcdđeéèẻẽẹêếềểễệfghiíìỉĩịjklmnoóòỏõọôốồổỗộơớờởỡợpqrstuúùủũụưứừửữựvwxyýỳỷỹỵzÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬBCDĐÉÈẺẼẸÊẾỀỂỄỆFGHIÍÌỈĨỊJKLMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢPQRSTUÚÙỦŨỤƯỨỪỬỮỰVWXYÝỲỶỸỴZ";
    static final String ALPHABET_EN = "0123456789qwertyuiopasdfghjklzxcvbnmMKPLOIJNBHUVYGCTFXRDZESAWQ";
    private String ALPHABET = ALPHABET_VN;

    private int MODULO = ALPHABET.length();
    static final String userHome = System.getProperty("user.home");

    public HillCipher() {
    }

    public void setAlphabetVn() {
        this.ALPHABET = ALPHABET_VN;
        this.MODULO = ALPHABET.length();
    }

    public void setAlphabetEng() {
        this.ALPHABET = ALPHABET_EN;
        this.MODULO = ALPHABET.length();
    }

    //Phương thức chuyển đổi kí tự sang số
    public int charToNumber(char c) {
        if (Character.isUpperCase(c)) return c - 'A';
        if (Character.isLowerCase(c)) return c - 'a' + 26;
        throw new IllegalArgumentException("Ky tu khong hop le : " + c);
    }

    // Phương thức chuyển đổi số sang kí tự.
    public char numberToChar(int n) {
        if (n >= 0 && n < 26) return (char) (n + 'A');
        if (n >= 26 && n < 52) return (char) (n - 26 + 'a');
        throw new IllegalArgumentException("So khong hop le : " + n);
    }

    // Phương thức genKey() để tạo khóa ngẫu nhiên
    public String genKey() {
        keyMatrix = new int[sizeKey][sizeKey];
        Random random = new Random();
        while (true) {
            // Tạo ma trận khóa ngẫu nhiên
            for (int i = 0; i < sizeKey; i++) {
                for (int j = 0; j < sizeKey; j++) {
                    keyMatrix[i][j] = random.nextInt(MODULO);
                }
            }
            printMatrix(keyMatrix);
            System.out.println("--------------");
//            // Kiểm tra xem ma trận khóa có nghịch đảo được hay không
//            if (determinant(keyMatrix) != 0) {
//                break;  // Nếu ma trận có nghịch đảo, thoát vòng lặp
//            }
            if (modInverse(determinant(keyMatrix)) != -1) break;
        }
        String keyString = convertKeyToString(keyMatrix);
        return keyString;
    }

    // Phương thức load key lên
    public String loadKey(String fileKeyPath) {
        return loadKeyFile(fileKeyPath);
    }

    //Lưu khóa xuống file tại một vị trí cố định trong máy tính.
    public String saveKey(String key) {
        String folderPath = userHome + File.separator + "HillCryptKey";
        // Làm cho tên file lưu key khác nhau
        String diskey = charToNumber(key.charAt(0)) + key.substring(2, 3);
        String filePath = folderPath + File.separator + diskey + "key.txt";
        // tạo mới một thư mục để chứa file có ghi dữ liệu khóa.
        File folder = new File(folderPath);
        if (!folder.exists()) { //Kiểm tra thư mục có tồn tại trước đó hay chưa
            if (folder.mkdir()) { //Tạo một thư mục mới
                System.out.println("Create new folder successful");
            } else {
                System.out.println("Create new folder failed");
            }
        } else { //Nếu tồn tại rồi thì lưu file vào thư mục đó
            File file = new File(filePath);
            if (file.exists()) { //Nếu có file lưu khóa tồn tại rồi thí xóa file đó đi để tránh không tạo được file khóa
                if (file.delete()) System.out.println("Delete successful");
            }
        }
        //tạo file để ghi khóa vào
        File fileKey = new File(filePath);
        try {
            //Tạo file để lưu khóa
            if (fileKey.createNewFile()) {
                System.out.println("Key file created successfully: " + filePath);
            } else {
                System.out.println("Key file already exists");
            }
            //Ghi khóa vào file
            //dùng FileWrite để ghi dòng dữ liệu tốt hơn
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileKey));

            bufferedWriter.write(key);
            bufferedWriter.close();
            System.out.println("Đã lưu khóa thành công.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filePath;
    }

    //Load khóa từ file đã lưu lên
    public String loadKeyFile(String keyFilePath) {
        try (FileReader fileReader = new FileReader(keyFilePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader) //dùng BufferedReader để đọc khóa từ file
        ) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("Error reading key file: " + e.getMessage());
        }
        return null;
    }

    // Phương thức tính định thức của ma trận
    public int determinant(int[][] matrix) {
        int n = matrix.length;
        if (n == 1) return matrix[0][0];
        if (n == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

        int det = 0;
        for (int i = 0; i < n; i++) {
            det += (int) Math.pow(-1, i) * matrix[0][i] * determinant(minor(matrix, i));
        }
        return det;
    }

    // Tính ma trận minor
    private int[][] minor(int[][] matrix, int col) {
        int n = matrix.length;
        int[][] minor = new int[n - 1][n - 1];
        for (int i = 1; i < n; i++) {
            int minorCol = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) continue;
                minor[i - 1][minorCol++] = matrix[i][j];
            }
        }
        return minor;
    }

    // Tìm phần tử nghịch đảo modulo MODULO
    public int modInverse(int num) {
        num = num % MODULO;
        for (int x = 1; x < MODULO; x++) {
            if ((num * x) % MODULO == 1) {
                return x;
            }
        }
        return -1;
    }

    // Tính nghịch đảo của ma trận khóa 2x2 trong modulo 52
    public void inverseKeyMatrix(int[][] keyMatrix) {
        int det = determinant(keyMatrix);
        int detInverse = modInverse(det);

        if (detInverse == -1) {
            throw new ArithmeticException("Ma trận không khả nghịch trong modulo " + MODULO);
        }

        inverseKeyMatrix = new int[2][2];

        // Công thức tính ma trận nghịch đảo cho 2x2
        inverseKeyMatrix[0][0] = keyMatrix[1][1] * detInverse % MODULO;
        inverseKeyMatrix[1][1] = keyMatrix[0][0] * detInverse % MODULO;
        inverseKeyMatrix[0][1] = -keyMatrix[0][1] * detInverse % MODULO;
        inverseKeyMatrix[1][0] = -keyMatrix[1][0] * detInverse % MODULO;

        // Đảm bảo giá trị dương trong modulo 52
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (inverseKeyMatrix[i][j] < 0) {
                    inverseKeyMatrix[i][j] += MODULO;
                }
            }
        }
    }

    // Mã hóa văn bản
    public String encrypt(String plaintext, String keyText) {
        System.out.println(ALPHABET);
        int[][] key = convertKeyToMatrix(keyText);
//        plaintext = plaintext.replaceAll("[^A-Za-z]", ""); // Loại bỏ ký tự không phải chữ
        StringBuilder data = new StringBuilder();
        for (char c : plaintext.toCharArray()) {
            if (ALPHABET.indexOf(c) != -1) {
                data.append(c);
            }
        }
        if (data.length() % 2 != 0) {
            data.append("X"); // Thêm ký tự đệm nếu độ dài lẻ
            addPadding = true; // Đánh dấu là có thêm ký tự đệm
        }

        // tạo đối tượng StringBuilder để lưu dữ liệu được mã hóa
        StringBuilder ciphertext = new StringBuilder();

        // duyệt ma trận và mã hóa từng kí tự
        for (int i = 0; i < data.length(); i += 2) {
            int[] vector = {ALPHABET.indexOf(data.charAt(i)), ALPHABET.indexOf(data.charAt(i + 1))};

            int[] encryptedVector = new int[2];
            for (int row = 0; row < 2; row++) {
                encryptedVector[row] = (key[row][0] * vector[0] + key[row][1] * vector[1]) % MODULO;
            }
            // chuyển các số trong vector được mã hóa thành kí tự và tiến thành cộng chuỗi
            ciphertext.append(ALPHABET.charAt(encryptedVector[0]));
            ciphertext.append(ALPHABET.charAt(encryptedVector[1]));
        }
        return ciphertext.toString();
    }

    // Giải mã văn bản
    public String decrypt(String ciphertext, String key) {
        System.out.println(ALPHABET);
        int[][] keyToMatrix = convertKeyToMatrix(key);
        // Tính ma trận nghịch đảo của khóa
        inverseKeyMatrix(keyToMatrix);
        //Tạo đối tượng StringBuilder để lưu trữ dữ liệu được khôi phục
        StringBuilder plaintext = new StringBuilder();

        //
        for (int i = 0; i < ciphertext.length(); i += 2) {
            // Ánh xạ mỗi kí tự trong chuỗi với vị trí tương ứng trong bảng ALPHABET
            int[] vector = {ALPHABET.indexOf(ciphertext.charAt(i)), ALPHABET.indexOf(ciphertext.charAt(i + 1))};

            //Nhân ma trận nghịch đảo với vector đã mã hóa để giải mã.
            int[] decryptedVector = new int[2];
            for (int row = 0; row < 2; row++) {
                decryptedVector[row] = (inverseKeyMatrix[row][0] * vector[0] + inverseKeyMatrix[row][1] * vector[1]) % MODULO;
            }

            //chuyển các số trong vector sau giải mã thành kí tự tương ứng và cộng chuỗi lại
            plaintext.append(ALPHABET.charAt(decryptedVector[0]));
            plaintext.append(ALPHABET.charAt(decryptedVector[1]));
        }
        // Sao khi giải mã xong sẽ xóa kí tự đệm đi nếu có
        if (addPadding) plaintext.deleteCharAt(plaintext.length() - 1);
        return plaintext.toString();
    }

    public void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Phương thức giúp chuyển khóa từ một ma trận thành số thành dạng chuỗi để người dùng dễ lưu trữ
    public String convertKeyToString(int[][] keyMatrix) {
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < keyMatrix.length; i++) {
            for (int j = 0; j < keyMatrix[i].length; j++) {
                key.append(ALPHABET.charAt(keyMatrix[i][j]));
            }
        }
        return key.toString();
    }

    // Phương thức giúp chuyển khóa từ chuỗi sang ma trận để phù hợp cho việc giải mã.
    public int[][] convertKeyToMatrix(String key) {
        int[][] matrix = new int[sizeKey][sizeKey];
        int index = 0;
        for (int i = 0; i < keyMatrix.length; i++) {
            for (int j = 0; j < keyMatrix[i].length; j++) {
                int c = ALPHABET.indexOf(key.charAt(index));
                matrix[i][j] = c;
                index++;
            }
        }
        return matrix;
    }

    public static void main(String[] args) {
        String data = "Đặng Trần Tấn Lực";
        HillCipher hc = new HillCipher();

       String key =  hc.genKey();

        String encrypt = hc.encrypt(data, key);
        System.out.println(encrypt);

        //Khi giải mã cần load key lên trước

        String decrypt = hc.decrypt(encrypt,key );
        System.out.println(decrypt);
    }
}
