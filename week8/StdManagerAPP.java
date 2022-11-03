package week8;

import java.io.*;
import java.util.Scanner;

public class StdManagerAPP {
    public static void main(String[] args) throws IOException {
        StdManager stdManager = new StdManager();
        StdManager.run();
    }
}

class StdManager {
    private static Screen scr;
    private static Keyboard kbd;
    private static StudentList list;

    public StdManager() {
        scr = new Screen();
        kbd=new Keyboard();

        list=new StudentList();
    }

    public static void run() throws IOException {
        int menu;
        do {
            menu = selectMenu();
            if (menu == 7) break;
            switch (menu) {
                case 1: new loadFileControl().execute(list,scr,kbd);
                break;
                case 2: //전체 성적 화면 출력
                    new printAllControl().execute(list,scr);
                    break;
                case 3: //성적 입력
                    new addControl().execute(list,scr,kbd);
                    break;
                case 4: //학번으로 성적 조회
                    new searchNumControl().execute(list,scr,kbd);
                    break;
                case 5: //학번으로 성적 삭제
                    new deleteNumControl().execute(list,scr,kbd);
                    break;
                case 6: //성적 파일로 저장
                    new saveFileControl().execute(list,scr,kbd);
                    break;
                default:
                    break;
            }
        } while (true);
        System.out.println("***프로그램 종료***");
    }

    public static int selectMenu() {
        System.out.println("=====================");
        System.out.println("1. 파일로부터 성적 로딩");
        System.out.println("2. 전체 성적 화면 출력");
        System.out.println("3. 성적 입력");
        System.out.println("4. 학번으로 성적 조회");
        System.out.println("5. 학번으로 성적 삭제");
        System.out.println("6. 성적 파일로 저장");
        System.out.println("7. 프로그램 종료");
        System.out.println("=====================");
        System.out.print("원하는 메뉴를 선택하시오: ");
        return kbd.getSelection();
    }
}
// Boundary Classes
class Keyboard {
    private Scanner kbd = new Scanner(System.in);

    int getSelection() {
        return kbd.nextInt();
    }

    String getLoadFile() {
        return kbd.next();
    }
    String getSaveFile() {
            return kbd.next();
        }

    String getName() {
        return kbd.next();
    }
    int getNum(){
        return kbd.nextInt();
    }
    int getScore(){
        return kbd.nextInt();
    }
}

class Screen {
    void displayMessage(String m) {
        System.out.println(m);
    }

    void displayPrompt(String m) {
        System.out.print(m);
    }
}
// Entity Classes
class Student implements Serializable { //Serializable 객체 직렬화 통해 파일에 객체 넣을 때 필요
    private String stdName;
    private int id, kor, eng, math;

    public Student(int id, String name, int kor, int eng, int math) {
        this.id = id;
        stdName = name;
        this.kor = kor;
        this.eng = eng;
        this.math = math;
    }

    public String getName() {
        return stdName;
    }

    public int getId() {
        return id;
    }

    public int getKor() {
        return kor;
    }

    public int getEng() {
        return eng;
    }

    public int getMath() {
        return math;
    }

    public double getAvg() {
        return (kor + eng + math) / 3;
    }

    public void show() {
        System.out.println("\n" + getName() + " " + getId());
        System.out.println("국어성적: " + getKor());
        System.out.println("영어성적: " + getEng());
        System.out.println("수학성적: " + getMath());
        System.out.println("평균 :" + getAvg());
    }
}

    class StudentList {
        final int maxSize = 100;
        Student[] students = new Student[maxSize];
        int count; //현재 들어가 있는 학생 수

        StudentList() { //생성자. Student타입의 students배열을 100개 만듦
            count = 0;
            for (int i = 0; i < 100; i++) {
                students[i] = null;
            }
        }

        public void add(Student s) {
            students[count] = s;
            if (count != maxSize - 1)
                count++;
        }

        public void delete(int num) { //num : 학번
            for (int i = 0; i < 100; i++) {
                if (students[i] != null) {
                    if (num == students[i].getId()) {
                        students[i] = null;
                        count--;
                        reArrange();
                        return;
                    }
                }
            }
            System.out.println("존재하지 않는 학번입니다.");
        }

        public Student search(int num) {
            for (int i = 0; i < 100; i++) {
                if (students[i] != null) {
                    if (num == students[i].getId())
                        return students[i];
                }
            }
            return null;
        }

        public void printAll() {
            for (int i = 0; i < 100; i++) {
                if (students[i] == null) {
                    continue;
                } else
                    students[i].show();
            }
        }

        public void save(String fName) throws IOException { //파일에 저장
            File f = new File(fName);
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(students); //students 객체를 통째로 저장
            fos.write(count);
            oos.close();
        }

        public void load(String fName) throws IOException {//파일로 부터 불러오기
            File f = new File(fName);
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                this.students = (Student[]) ois.readObject();
                this.count = (int) fis.read();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ois.close();
        }

        public void reArrange() { //삭제된 students배열의 빈공간 채워넣기
            for (int i = 0; i < 99; i++) {
                if (students[i] == null) {
                    students[i] = students[i + 1];
                    students[i + 1] = null;
                }
            }
        }
    }
    // Control Classes
    class loadFileControl {
        public void execute(StudentList list, Screen scr, Keyboard kbd) throws IOException {
            scr.displayPrompt("성적을 로드할 파일을 입력하시오: ");
            String loadFile = kbd.getLoadFile();
            list.load(loadFile);
        }
    }

    class printAllControl {
        public void execute(StudentList list, Screen scr) {
            list.printAll();
            scr.displayMessage("출력완료");
        }
    }

    class addControl {
        public void execute(StudentList list, Screen scr, Keyboard kbd) {

            scr.displayPrompt("학생의 학번, 이름, 국어성적, 영어성적, 수학성적을 차례로 입력하시오: ");
            Student std = new Student(kbd.getNum(),kbd.getName(),kbd.getScore(),kbd.getScore(),kbd.getScore());
            list.add(std);
        }
    }

    class searchNumControl {
        public void execute(StudentList list, Screen scr, Keyboard kbd) {

            scr.displayPrompt("성적을 조회할 학생의 학번을 입력하시오: ");
            int searchNum =  kbd.getNum();
            (list.search(searchNum)).show();
        }
    }

    class deleteNumControl {
        public void execute(StudentList list, Screen scr, Keyboard kbd) {

            scr.displayPrompt("성적을 삭제할 학생의 학번을 입력하시오: ");
            int deleteNum = kbd.getNum();
            list.delete(deleteNum);
        }
    }

    class saveFileControl {
        public void execute(StudentList list, Screen scr, Keyboard kbd) throws IOException {

            scr.displayPrompt("성적을 저장할 파일을 입력하시오: ");
            String saveFile = kbd.getSaveFile();
            list.save(saveFile);
        }
    }
