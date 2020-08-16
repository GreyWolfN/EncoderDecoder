package correcter;
import java.io.*;
import java.util.*;

public class Main {

    private static final String sourceFileName = "send.txt";
    private static final String encodedFileName = "encoded.txt";
    private static final String receivedFileName = "received.txt";
    private static final String decodedFileName = "decoded.txt";

    public static void main(String[] args) {
        ArrayList<Integer> received = new ArrayList<>();
        System.out.println("Write a mode: ");
        Scanner scan = new Scanner(System.in);
        String mode = scan.nextLine();
        switch (mode) {
            case "encode":
                StringBuilder expand = new StringBuilder();
                String message = getMessage(sourceFileName);
                char[] ar = message.toCharArray();
                System.out.println(sourceFileName + ":");
                System.out.println("text view: " + message);
                System.out.print("hex view: ");
                for (char c : ar) {
                    System.out.print(Integer.toHexString(c) + " ");
                }
                System.out.println();
                System.out.print("bin view: ");
                for (char c : ar) {
                    System.out.print(String.format("%8S", Integer.toBinaryString(c)).replaceAll(" ", "0") + " ");
                }
                System.out.println("\n");
                System.out.println(encodedFileName + ":");
                int a;
                for (char c : ar) {
                    String binaryChar = (String.format("%8S", Integer.toBinaryString(c)));
                    binaryChar = binaryChar.replaceAll(" ", "0");

                    for (int i = 0; i < 8; i++) {
                        for (a = 0; a < 18; a++) {
                            if (a == 0 || a == 1 || a == 3 || a == 7 ||
                                    a == 9 || a == 10 || a == 12 || a == 16) {
                                expand.append(".");
                                continue;
                            }
                            if (a == 8 || a == 17) {
                                expand.append(" ");
                                continue;
                            }
                            expand.append(binaryChar.charAt(i));
                            if (i < 7) {
                                i++;
                            }
                        }
                    }
                }
                if (expand.length() % 9 != 0) {
                    int expandLength = expand.length();
                    expand.append(".".repeat(8 - expandLength % 9));
                }
                System.out.print("expand: ");
                System.out.println(expand.toString());
                System.out.print("parity: ");
                for (int i = 7; i < expand.length() - 1; i += 9) {
                    if (expand.charAt(i) == '.') {
                        expand.setCharAt(i, '0');
                    }
                }
                for (int i = 6; i < expand.length(); i += 9) {
                    int b = expand.charAt(i - 4);
                    int c = expand.charAt(i - 2);
                    int d = expand.charAt(i - 1);
                    int e = expand.charAt(i);
                    expand.setCharAt(i - 6, (char) (b ^ c ^ e));
                    expand.setCharAt(i - 5, (char) (b ^ d ^ e));
                    expand.setCharAt(i - 3, (char) (c ^ d ^ e));
                }
                System.out.println(expand.toString());

                System.out.print("hex view: ");
                try (FileOutputStream fileOutputStream = new FileOutputStream(encodedFileName)) {
                    for (String s : expand.toString().split(" ")) {
                        int by = Integer.parseInt(s, 2);
                        fileOutputStream.write(by);
                        String hex = String.format("%2S", Integer.toHexString(by));
                        System.out.print(hex.replaceAll(" ", "0") + " ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "send":
                ArrayList<Integer> encoded = new ArrayList<>();
                try (FileInputStream fileInputStream = new FileInputStream(encodedFileName)) {
                    while (fileInputStream.available() > 0) {
                        encoded.add(fileInputStream.read());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(encodedFileName + ":");
                System.out.print("hex view: ");
                for (int i : encoded) {
                    String hex = String.format("%02X", i);
                    System.out.print(hex + " ");
                }
                System.out.println();
                System.out.print("bin view: ");
                for (int i : encoded) {
                    String hex = String.format("%8S", Integer.toBinaryString(i));
                    System.out.print(hex.replaceAll(" ", "0") + " ");
                }
                System.out.println();
                sendFromFileToFile(encodedFileName, receivedFileName);

                try (FileInputStream fileInputStream = new FileInputStream(receivedFileName)) {
                    while (fileInputStream.available() > 0) {
                        received.add(fileInputStream.read());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println();
                System.out.println(receivedFileName + ":");
                System.out.print("bin view: ");
                for (int i : received) {
                    String hex = String.format("%8S", Integer.toBinaryString(i));
                    System.out.print(hex.replaceAll(" ", "0") + " ");
                }

                System.out.println();
                System.out.print("hex view: ");
                for (int i : received) {
                    String hex = String.format("%02X", i);
                    System.out.print(hex + " ");
                }
                System.out.println();
                break;
            case "decode":
                System.out.println(receivedFileName + ":");
                try (FileInputStream fileInputStream = new FileInputStream(receivedFileName)) {
                    while (fileInputStream.available() > 0) {
                        received.add(fileInputStream.read());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print("hex view: ");
                for (int i : received) {
                    String hex = String.format("%02X", i);
                    System.out.print(hex + " ");
                }
                ArrayList<String> corrected = new ArrayList<>();
                System.out.println();
                System.out.print("bin view: ");
                for (int i : received) {
                    String bin = (String.format("%8S", Integer.toBinaryString(i))).replaceAll(" ", "0");
                    System.out.print(bin + " ");
                    corrected.add(correct(bin));
                }
                System.out.println();
                System.out.println();
                System.out.println(decodedFileName + ":");
                System.out.print("correct: ");
                StringBuilder decoded = new StringBuilder();
                for (String i : corrected) {
                    System.out.print(i + " ");
                    decoded.append(i);
                }
                System.out.println();
                System.out.print("decode: ");
                for (int i = 7; i < decoded.length(); i += 8) {
                    decoded.setCharAt(i, 'x');
                }
                StringBuilder decoded1 = new StringBuilder();
                for (String s : decoded.toString().split("x")) {
                    decoded1.append(s.charAt(2));
                    decoded1.append(s.charAt(4));
                    decoded1.append(s.charAt(5));
                    decoded1.append(s.charAt(6));
                }
                ArrayList<String> decodedAl = new ArrayList<>();
                for (int i = 0; i < decoded1.length() - 7; i += 8) {
                    String s = decoded1.substring(i, i + 8);
                    decodedAl.add(s);
                    System.out.print(s + " ");
                }
                System.out.println(decoded1.substring(decoded1.length() - decoded1.length() % 8));
                System.out.print("hex view: ");
                try (FileOutputStream fileOutputStream = new FileOutputStream(decodedFileName)) {
                    for (String s : decodedAl) {
                        int i = Integer.valueOf(s, 2);
                        fileOutputStream.write(i);
                        System.out.print(String.format("%2H", i) + " ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println();
                System.out.print("text view: ");
                try (FileInputStream fileInputStream = new FileInputStream(decodedFileName)) {
                    while (fileInputStream.available() > 0) {
                        System.out.print((char) fileInputStream.read());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private static String correct(String bin) {
        StringBuilder byt = new StringBuilder(bin);
        int positionOfBadBit = 0;
        int b = byt.charAt(2);
        int c = byt.charAt(4);
        int d = byt.charAt(5);
        int e = byt.charAt(6);
        if (byt.charAt(0) != (char) (b ^ c ^ e)) {
            positionOfBadBit += 1;
        }
        if (byt.charAt(1) != (char) (b ^ d ^ e)) {
            positionOfBadBit += 2;
        }
        if (byt.charAt(3) != (char) (c ^ d ^ e)) {
            positionOfBadBit += 4;
        }
        if (positionOfBadBit > 0) {
            positionOfBadBit--;
        }
        if (byt.charAt(positionOfBadBit) == '0' && positionOfBadBit != 0) {
            byt.setCharAt(positionOfBadBit, '1');
        } else if (positionOfBadBit != 0) {
            byt.setCharAt(positionOfBadBit, '0');
        }
        return byt.toString();
    }

    private static void sendFromFileToFile(String encodedFileName, String receivedFileName) {
        ArrayList<Integer> sourceMessage = new ArrayList<>();
        Random rnd = new Random();
        try (FileInputStream fileInputStream = new FileInputStream(encodedFileName)) {
            while (fileInputStream.available() > 0) {
                sourceMessage.add(fileInputStream.read());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(receivedFileName)) {
            for (int i : sourceMessage) {
                String bin = String.format("%8s", Integer.toBinaryString(i));
                StringBuilder binary = new StringBuilder(bin.replaceAll(" ", "0"));
                int indexOfError = rnd.nextInt(8);
                char badBit = binary.charAt(indexOfError);
                if (badBit == '1') {
                    badBit = '0';
                } else badBit = '1';
                binary.setCharAt(indexOfError, badBit);
                fileOutputStream.write(Integer.parseInt(binary.toString(), 2));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getMessage(String filePath) {
        File file = new File(filePath);
        String message = null;
        try {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                message = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

}
