import java.io.*;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws IOException {
        Field   field;
        int     numberOfMines;
        String  readString;
        int     res;

        res = 0;
        field = new Field();
        field.setField(9, 9, '.', 'X');
        System.out.println("How many mines do you want on the field?");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try (reader) {
            readString = reader.readLine();
            numberOfMines = Integer.parseInt(readString);
            field.setMines(numberOfMines);
            field.printField();
            while (true) {
                System.out.print("Set/unset mines marks or claim a cell as free: ");
                readString = reader.readLine();
                if (readString.substring(4).equals("mine")) {
                    field.setField((int) (readString.charAt(2) - '0') - 1, (int) (readString.charAt(0)) - '0' - 1);
                    field.printField();
                } else if (readString.substring(4).equals("free")) {
                    res = field.freeField((int) (readString.charAt(2) - '0') - 1, (int) (readString.charAt(0)) - '0' - 1);
                    if (res == -1) {
                        System.out.print("You stepped on a mine and failed!");
                        break;
                    }
                    field.printField();
                }
                if (field.checkFieldWithMines()) {
                    break ;
                }
            }
            if (res != -1)
                System.out.println("Congratulations! You found all mines!");
        } catch (Exception ex) {
            throw ex;
        }
    }
}

class Field {
    private char[][]    field;
    static  int[][]     fieldWithMines;
    private char        symOfEmpty;
    private char        symOfMine;
    private int         numberOfRows;
    private int         numberOfLines;

    public void setField(int numberOfRows, int numberOfLines, char symOfEmpty, char symOfMine) {
        field = new char[numberOfLines][numberOfRows];
        fieldWithMines = new int[numberOfLines + 2][numberOfRows + 2];
        this.symOfEmpty = symOfEmpty;
        this.symOfMine = symOfMine;
        this.numberOfLines = numberOfLines;
        this.numberOfRows = numberOfRows;
        for (int i = 0; i < numberOfLines; i++) {
            for (int j = 0; j < numberOfRows; j++) {
                field[i][j] = symOfEmpty;
            }
        }
    }

    public void setMines(int numOfMines) {
        Random random = new Random(System.currentTimeMillis() / 1000);
        int row;
        int line;

        while (numOfMines > 0) {
            row = random.nextInt(this.numberOfRows);
            line = random.nextInt(this.numberOfLines);
            if (this.field[line][row] == symOfEmpty) {
                numOfMines--;
                this.field[line][row] = symOfMine;
                fieldWithMines[line + 1][row + 1] = -1;
            }
        }

        for (int i = 0; i < numberOfLines; i++) {
            for (int j = 0; j < numberOfRows; j++) {
                if (fieldWithMines[i + 1][j + 1] != -1) {
                    if (fieldWithMines[i + 1][j] == -1)
                        fieldWithMines[i + 1][j + 1]++;
                    if (fieldWithMines[i + 1][j + 2] == -1)
                        fieldWithMines[i + 1][j + 1]++;
                    if (fieldWithMines[i][j + 2] == -1)
                        fieldWithMines[i + 1][j + 1]++;
                    if (fieldWithMines[i][j + 1] == -1)
                        fieldWithMines[i + 1][j + 1]++;
                    if (fieldWithMines[i][j] == -1)
                        fieldWithMines[i + 1][j + 1]++;
                    if (fieldWithMines[i + 2][j + 2] == -1)
                        fieldWithMines[i + 1][j + 1]++;
                    if (fieldWithMines[i + 2][j + 1] == -1)
                        fieldWithMines[i + 1][j + 1]++;
                    if (fieldWithMines[i + 2][j] == -1)
                        fieldWithMines[i + 1][j + 1]++;
                }
            }
        }
    }

    public void printField() {
        System.out.println(" │123456789│\n—│—————————│");
        for (int i = 0; i < numberOfLines; i++) {
            System.out.print( (i + 1) + "|");
            for (int j = 0; j < numberOfRows; j++) {
                if (field[i][j] == 'X') {
                    System.out.print('.');
                } else if (field[i][j] == '*' || field[i][j] == 'O') {
                    System.out.print('*');
                } else {
                    System.out.print(field[i][j]);
                }
            }
            System.out.println("|");
        }
        System.out.println("—│—————————│");
    }

    public boolean checkFieldWithMines(){
        int diff;

        diff = 0;
        for (int i = 0; i < this.numberOfLines; i++) {
            for (int j = 0; j < this.numberOfRows; j++) {
                if (field[i][j] == 'X' || field[i][j] == '*')
                    diff++;
            }
        }
        return (diff == 0);
    }

    public void setField(int line, int raw){
        if (this.field[line][raw]  == '.' || this.field[line][raw]  == '/') {
            this.field[line][raw] = '*';
        } else if (this.field[line][raw]  == 'X') {
            this.field[line][raw] = 'O';
        } else if (this.field[line][raw]  == 'O') {
            this.field[line][raw] = 'X';
        } else if (this.field[line][raw] == '*') {
            this.field[line][raw]  = '.';
        }
    }

    public int freeField(int line, int raw) {
        if (this.field[line][raw] == 'O' || this.field[line][raw] == 'X')
            return (-1);
        if (fieldWithMines[line + 1][raw + 1] > 0) {
            this.field[line][raw] = (char)(fieldWithMines[line + 1][raw + 1] + '0');
            return (1);
        } if (field[line][raw] == '/') {
            return (1);
        }
        field[line][raw] = '/';
        if (line != 0)
            freeField(line - 1, raw);
        if (line != numberOfLines - 1)
            freeField(line + 1, raw);
        if (raw != 0)
            freeField(line, raw - 1);
        if (raw != numberOfRows - 1)
            freeField(line , raw + 1);
        if (line != 0) {
            if (raw != 0) {
                freeField(line - 1, raw - 1);
            }
            if (raw != numberOfRows - 1) {
                freeField(line - 1, raw + 1);
            }
        }
        if (line != numberOfLines - 1) {
            if (raw != 0) {
                freeField(line + 1, raw - 1);
            }
            if (raw != numberOfRows - 1) {
                freeField(line + 1, raw + 1);
            }
        }
        return (1);
    }
}
