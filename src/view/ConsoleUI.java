package view;

import model.human.Human;
import model.human.Sex;
import presenter.Presenter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class ConsoleUI implements View {
    private boolean run;
    private Scanner scanner;
    private Presenter<Human> presenter;
    private MainMenu mainMenu;
    private final String ERR_INPUT = "Неверное значение. ";
    private final String CANCEL_INPUT = "Ввод прерван. ";

    public ConsoleUI() {
        run = true;
        scanner = new Scanner(System.in);
        presenter = new Presenter<>(this);
        mainMenu = new MainMenu(this);
    }

    @Override
    public void printAnswer(String text) {
        System.out.println(text);
    }

    @Override
    public void start() {
        while (run) {
            printMenu();
            execute();
        }
    }

    private void printMenu() {
        System.out.println(mainMenu.getMenu());
    }

    private void execute() {
        int choice = choiceMainMenu();
        if (choice > 0) {
            mainMenu.execute(choice);
        }
    }

    private int choiceMainMenu() {
        try {
            String input = scanner.nextLine();
            int choice = Integer.parseInt(input);
            if (choice > 0 && choice <= mainMenu.getSize()) {
                return choice;
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            System.out.println(ERR_INPUT);
            return -1;
        }
    }

    public void add() {
        System.out.println("Добавление нового человека в семейное древо.");
        String name = inputString("Введите имя: ");
        if (name == null || name.isBlank()) {
            System.out.println(CANCEL_INPUT);
            return;
        }
        Sex sex = inputSex();
        if (sex == null) {
            System.out.println(CANCEL_INPUT);
            return;
        }
        Human newHuman = new Human(name, sex);
        LocalDate birthDate = inputDate("Введите дату рождения: ");
        if (birthDate != null)
            newHuman.setBirthDate(birthDate);
        LocalDate deathDate = inputDate("Введите дату смерти: ");
        if (deathDate != null)
            newHuman.setDeathDate(deathDate);
        Human father = inputParent("Введите ID отца:", "Отец должен быть мужчиной.", Sex.MALE);
        Human mother = inputParent("Введите ID матери:", "Мать должна быть женщиной.", Sex.FEMALE);
        newHuman.setFamilyTies(father, mother); // Обработка нулевых значений предусмотрена в методе.
        System.out.println();
        presenter.add(newHuman);
    }

    private String inputString(String mess) {
        System.out.println("При отсутствии данных введите пустую строку.");
        System.out.print(mess);
        return scanner.nextLine();
    }

    private Sex inputSex() {
        while (true) {
            String inp = inputString("Введите пол человека (м/ж): ");
            switch (inp) {
                case "м":
                    return Sex.MALE;
                case "ж":
                    return Sex.FEMALE;
                case "":
                    return null;
                default:
                    System.out.println(ERR_INPUT);
            }
        }

    }

    private LocalDate inputDate(String mess) {
        while (true) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d.M.yyyy");
            String date = inputString("Формат ввода даты дд.мм.ггг \n" + mess);
            if (date.isBlank())
                return null;
            try {
                return LocalDate.parse(date, dtf);
            } catch (DateTimeParseException e) {
                System.out.println(ERR_INPUT);
            }
        }
    }

    private Human inputParent(String mess, String err, Sex sex) {
        while (true) {
            try {
                Human human = inputHuman(mess);
                if (human == null)
                    return null;
                if (human.getSex() != sex) {
                    System.out.println(err);
                } else {
                    return human;
                }
            } catch (NumberFormatException e) {
                System.out.println(ERR_INPUT);
            }
        }
    }

    private Human inputHuman(String mess) {
        while (true) {
            getFamilyTreeInfo(); // Вывод древа пользователю для определения id человека.
            try {
                String inp = inputString(mess);
                if (inp.isBlank())
                    return null;
                int id = Integer.parseInt(inp);
                Human human = presenter.findById(id);
                if (human == null)
                    throw new NullPointerException();
                else
                    return human;
            } catch (NumberFormatException | NullPointerException e) {
                System.out.println(ERR_INPUT);
            }
        }
    }

    public void getFamilyTreeInfo() {
        presenter.getFamilyTree();
    }

    public void setBirthDate() {
        Human human = inputHuman("Введите ID человека для редактирования:");
        if (human == null) {
            System.out.println(CANCEL_INPUT);
            return;
        }
        LocalDate birthDate = inputDate("Введите дату рождения: ");
        if (birthDate != null) {
            human.setBirthDate(birthDate);
            presenter.updateItem(human);
        }
        else {
            System.out.println(CANCEL_INPUT);
        }
    }

    public void setDeathDate() {
        Human human = inputHuman("Введите ID человека для редактирования:");
        if (human == null) {
            System.out.println(CANCEL_INPUT);
            return;
        }
        LocalDate deathDate = inputDate("Введите дату смерти: ");
        if (deathDate != null) {
            human.setDeathDate(deathDate);
            presenter.updateItem(human);
        }
        else {
            System.out.println(CANCEL_INPUT);
        }
    }

    public void setParents() {
        Human human = inputHuman("Введите ID человека для редактирования:");
        if (human == null) {
            System.out.println(CANCEL_INPUT);
            return;
        }
        Human father = inputParent("Введите ID отца:", "Отец должен быть мужчиной.", Sex.MALE);
        Human mother = inputParent("Введите ID матери:", "Мать должна быть женщиной.", Sex.FEMALE);
        human.setFamilyTies(father, mother); // Обработка нулевых значений предусмотрена в методе.
        presenter.updateItem(human);
    }

    public void findByName() {
        System.out.print("Введите имя для поиска: ");
        String name = scanner.nextLine();
        presenter.findByName(name);
    }

    public void sortByName() {
        presenter.sortByName();
    }

    public void sortByBirthDate() {
        presenter.sortByBirthDate();
    }

    public void sortByAge() {
        presenter.sortByAge();
    }

    public void finish() {
        System.out.println("Выход...");
        run = false;
    }
}