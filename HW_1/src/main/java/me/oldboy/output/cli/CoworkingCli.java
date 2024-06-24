package me.oldboy.output.cli;

import me.oldboy.input.controllers.CoworkingSpaceController;
import me.oldboy.input.controllers.UserController;
import me.oldboy.input.entity.Place;
import me.oldboy.input.entity.User;
import me.oldboy.input.repository.HallBase;
import me.oldboy.input.repository.ReserveBase;
import me.oldboy.input.repository.UserBase;
import me.oldboy.input.repository.WorkplaceBase;
import me.oldboy.output.dto.ReadAllPlacesDto;
import me.oldboy.output.dto.ReadAllReserveWithFilterDto;
import me.oldboy.output.dto.ReadFreeSlotsByDateDto;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CoworkingCli class for user communication.
 */
public class CoworkingCli {
    private static CoworkingCli instance;

    private CoworkingCli(){}
    private static HallBase hallBase = new HallBase();
    private static WorkplaceBase workplaceBase = new WorkplaceBase();
    private static UserBase userBase = new UserBase();
    private static ReserveBase reserveBase = new ReserveBase(userBase);
    private static UserController userController = new UserController(userBase);
    private static CoworkingSpaceController coworkingSpaceController =
            new CoworkingSpaceController(reserveBase, userBase);
    private static ReadAllReserveWithFilterDto readAllReserveWithFilterDto =
            new ReadAllReserveWithFilterDto(reserveBase);

    private static boolean repeatMenu = true;
    public static CoworkingCli getInstance(){
        if(instance == null){
            instance = new CoworkingCli();
            hallBase.initHallBase();
            workplaceBase.initPlaceBase();
            userBase.initBaseAdmin();
            hallBase.setReserveBase(reserveBase);
            workplaceBase.setReserveBase(reserveBase);
        }
        return instance;
    }

    /**
     * CLI main interface
     *
     */
    public static void getInterface(){
        Integer haveHalls = hallBase.getHallBase().size();
        Integer haveWorkplaces = workplaceBase.getWorkplaceBase().size();

        String greeting = "*** Добро пожаловать в наш коворкинг центр ***\n" +
                "На данный момент к вашим услугам: " + haveHalls + " Конференц-зала и " +
                haveWorkplaces + " рабочих мест.";

        String invite = "Введите логин (если вы у нас в первый раз, регистрация пройдет автоматически): ";

        System.out.println(greeting);
        Scanner scanner = new Scanner(System.in);
        System.out.println(invite);
        String enterLogin = scanner.nextLine().trim();

        if(!userBase.getUsersBase().containsKey(enterLogin)) {
            userController.createUser(enterLogin);
            System.out.println("Пользователь: " +
                    userBase.getUsersBase().get(enterLogin).getLogin() +
                    " создан.\n" +
                    "Новый пользователь вошел в систему.\n");
        } else {
            System.out.println("Вы успешно вошли в систему.\n");
            userController.login(enterLogin);
        }

        User userEnteredToSystem = userBase.getUsersBase().get(enterLogin);

        mainMenu(scanner, userEnteredToSystem);

        System.out.println("\n*** До новых встреч, мы будем ждать вас! ***");
        scanner.close();
    }

    /**
     * CLI main menu
     *
     * @param scanner    the keyboard scanner
     * @param userEnteredToSystem  the entering user
     */
    private static void mainMenu(Scanner scanner, User userEnteredToSystem) {
        do {
            String menu = "\nВам доступны следующие операции: \n" +
                    "1 - просмотр списка всех доступных рабочих мест и конференц-залов;\n" +
                    "2 - просмотр доступных слотов для бронирования на конкретную дату;\n" +
                    "3 - бронирование рабочего места или конференц-зала на определённое время и дату;\n" +
                    "4 - отмена бронирования;\n" +
                    "5 - просмотр всех бронирований;\n" +
                    "6 - просмотр всех бронирований по дате;\n" +
                    "7 - просмотр всех бронирований по пользователю;\n" +
                    "8 - просмотр всех бронирований по ресурсу;\n\n" +
                    "Администраторы имеют доступ к функционалу:\n" +
                    "9 - управление рабочими местами (CRUD);\n" +
                    "10 - управление конференц-залами (CRUD);\n" +
                    "11 - покинуть систему.\n\n" +
                    "Введите номер команды:";
            System.out.println(menu);

            String command = scanner.nextLine().trim();

            switch (command) {
                case "1":
                    viewAllPlaces(scanner);
                    break;
                case "2":
                    viewAllFreeSlotsByEnterDate(scanner);
                    break;
                case "3":
                    reservePlaceToEnterDateAndSlot(scanner, userEnteredToSystem);
                    break;
                case "4":
                    removeReserveFromSlot(scanner, userEnteredToSystem);
                    break;
                case "5":
                    viewAllReservePlaces(scanner);
                    break;
                case "6":
                    viewAllReserveByDate(scanner);
                    break;
                case "7":
                    readAllReserveWithFilterDto.viewAllReserveByUser(userEnteredToSystem);
                    break;
                case "8":
                    viewReserveByConcretePlaces(scanner);
                    break;
                case "9":
                    crudOperationForWorkplaces(userEnteredToSystem, scanner);
                    break;
                case "10":
                    crudOperationForHalls(userEnteredToSystem, scanner);
                    break;
                case "11":
                    exitMenu();
                    break;
                default:
                    break;
            }
        } while (repeatMenu);
    }

    /**
     * CLI menu for CRUD operation with Workplaces
     *
     * @param scanner    the keyboard scanner
     * @param userEnteredToSystem  the entering user
     */
    private static void crudOperationForWorkplaces(User userEnteredToSystem, Scanner scanner) {
        System.out.println("Вы можете создавать/изменять/удалять/читать рабочие места.\n" +
                           "На данный момент доступны рабочие места: " +
                            workplaceBase.stringViewWorkplaceBase() +
                           "\n\nВыберите действие:" +
                           "\n1 - создать не существующее рабочее место;\n" +
                           "2 - удалить не зарезервированное рабочее место;\n" +
                           "3 - изменить не зарезервированное рабочее место;\n" +
                           "4 - просмотреть выбранное рабочее место;\n" +
                           "Введите операцию и номер места через пробел, например (2 6): ");
        String makeWorkplaceOperation = scanner.nextLine().trim();
        String[] parsWorkplaceOperation = makeWorkplaceOperation.split("\s+");
        /* Проводится некая валидация */
        parsWorkplaceOperation = validateEnterCommandLine(scanner, parsWorkplaceOperation);

        switch (parsWorkplaceOperation[0]) {
            case "1": workplaceBase.createWorkPlace(userEnteredToSystem,
                                                    Integer.parseInt(parsWorkplaceOperation[1]));
                      break;
            case "2": workplaceBase.removeWorkPlace(userEnteredToSystem,
                                                    Integer.parseInt(parsWorkplaceOperation[1]));
                      break;
            case "3": System.out.println("Введите новый номер рабочего места на который хотите изменить: ");
                      String changeNumberTo = scanner.nextLine().trim();
                      workplaceBase.updateWorkPlace(userEnteredToSystem,
                                                    Integer.parseInt(parsWorkplaceOperation[1]),
                                                    Integer.parseInt(changeNumberTo));
                      break;
            case "4":
                      System.out.println(workplaceBase.readWorkPlace(Integer.parseInt(parsWorkplaceOperation[1])));
                      break;
            default:
                break;
        }
        readAndGoForward(scanner);
    }

    /**
     * CLI menu for CRUD operation with Halls
     *
     * @param scanner    the keyboard scanner
     * @param userEnteredToSystem  the entering user
     */
    private static void crudOperationForHalls(User userEnteredToSystem, Scanner scanner) {
        System.out.println("Вы можете создавать/изменять/удалять/читать конференц-зал. \n" +
                           "На данный момент доступны залы: " +
                           hallBase.stringViewHallBase() +
                           "\n\nВыберите действие:" +
                           "\n1 - создать не существующий зал;\n" +
                           "2 - удалить не зарезервированный зал;\n" +
                           "3 - изменить не зарезервированное зал;\n" +
                           "4 - просмотреть выбранный зал;\n" +
                           "Введите операцию и номер зала через пробел, например (1 3): ");
        String makeHallOperation = scanner.nextLine().trim();
        String[] parsHallOperation = makeHallOperation.split("\s+");
        /* Проводится некая валидация */
        parsHallOperation = validateEnterCommandLine(scanner, parsHallOperation);

        switch (parsHallOperation[0]) {
            case "1": hallBase.createHall(userEnteredToSystem, Integer.parseInt(parsHallOperation[1]));
                      break;
            case "2": hallBase.removeHall(userEnteredToSystem, Integer.parseInt(parsHallOperation[1]));
                      break;
            case "3": System.out.println("Введите новый номер зала на который хотите изменить: ");
                      String changeNumberTo = scanner.nextLine().trim();
                      hallBase.updateHall(userEnteredToSystem,
                                          Integer.parseInt(parsHallOperation[1]),
                                          Integer.parseInt(changeNumberTo));
                      break;
            case "4": System.out.println(hallBase.readHall(Integer.parseInt(parsHallOperation[1])));
                      break;
            default:
                break;
        }
        readAndGoForward(scanner);
    }

    /**
     * Universal menu validator for CRUD operation with Workplaces and Halls
     *
     * @param scanner    the keyboard scanner
     * @param parsEnterOperation  the array of entering command and place parameter
     */
    private static String[] validateEnterCommandLine(Scanner scanner, String[] parsEnterOperation) {
        String makeAnOperation;
        boolean haveRightEnter = true;
        do{
            if(!(parsEnterOperation.length == 2) ||
                    !parsEnterOperation[0].matches("\\d+") ||
                    !parsEnterOperation[1].matches("\\d+")){
                System.out.println("\nВы ввели команду не верно, ну, попробуйте еще раз, вы сможете:");
                makeAnOperation = scanner.nextLine().trim();
                parsEnterOperation = makeAnOperation.split("\s+");
            } else {
                haveRightEnter = false;
            }
        } while (haveRightEnter);
        return parsEnterOperation;
    }

    /**
     * String view of reserve Workplaces or Halls
     *
     * @param scanner    the keyboard scanner
     */
    private static void viewReserveByConcretePlaces(Scanner scanner) {
        System.out.println("Бронь по каким ресурсам хотите изучить, введите " +
                           "'зал' - если \"Конференц-залы\", и 'место' - если \"Рабочие места\": ");
        /* Валидация не проводится, допускаем, что пользователь крайне дисциплинирован */
        String getMeReserveByPlace = scanner.nextLine().trim();
        if(getMeReserveByPlace.equals("зал")){
              readAllReserveWithFilterDto.viewAllReserveHall();
        } else if(getMeReserveByPlace.equals("место")){
              readAllReserveWithFilterDto.viewAllReserveWorkplace();
        }
        readAndGoForward(scanner);
    }

    /**
     * String view all reservation Workplaces and Halls by fix date
     *
     * @param scanner    the keyboard scanner
     */
    private static void viewAllReserveByDate(Scanner scanner) {
        System.out.println("На какую дату хотите посмотреть все забронированные " +
                           "места, формат (yyyy-mm-dd): ");
        /* Валидация не проводится, допускаем, что пользователь крайне дисциплинирован */
        String viewAllResByDate = scanner.nextLine().trim();
        readAllReserveWithFilterDto.viewAllReserveSlotsByDate(LocalDate.parse(viewAllResByDate));
        readAndGoForward(scanner);
    }

    /**
     * String view all reservation Workplaces and Halls without filter
     *
     * @param scanner    the keyboard scanner
     */
    private static void viewAllReservePlaces(Scanner scanner) {
        readAllReserveWithFilterDto.viewAllReserveHall();
        readAllReserveWithFilterDto.viewAllReserveWorkplace();
        readAndGoForward(scanner);
    }

    /**
     * Remove slot command
     *
     * @param scanner    the keyboard scanner
     * @param userEnteredToSystem    the entering user
     */
    private static void removeReserveFromSlot(Scanner scanner, User userEnteredToSystem) {
        System.out.println("С чего вы хотите снять бронь, введите 'зал' или 'место', без кавычек.\n" +
                           "Далее через пробел номер зала или места.\n");
        /* Валидация не проводится, допускаем, что пользователь крайне дисциплинирован */
        String removeReserve = scanner.nextLine().trim();
        String[] rmvRsv = removeReserve.split("\s+");
        Place removePlace = null;
        if(rmvRsv[0].equals("зал")){
              removePlace = hallBase.readHall(Integer.parseInt(rmvRsv[1]));
        } else if(rmvRsv[0].equals("место")){
              removePlace = workplaceBase.readWorkPlace(Integer.parseInt(rmvRsv[1]));
        }
        System.out.println("Введите дату в формате (yyyy-mm-dd):");
        /* Валидация не проводится, допускаем, что пользователь крайне дисциплинирован */
        String rmvRsvDate = scanner.nextLine().trim();
        System.out.println("Выберите номер слота для снятия резервирования: \n");
        /* Валидация не проводится, допускаем, что пользователь крайне дисциплинирован */
        String rmvRsvSlot = scanner.nextLine().trim();
        coworkingSpaceController.removeReserveSlot(userEnteredToSystem,
                                                   removePlace,
                                                   LocalDate.parse(rmvRsvDate),
                                                   Integer.parseInt(rmvRsvSlot));
        readAndGoForward(scanner);
    }

    /**
     * Reserve place command
     *
     * @param scanner    the keyboard scanner
     * @param userEnteredToSystem    the entering user
     */
    private static void reservePlaceToEnterDateAndSlot(Scanner scanner, User userEnteredToSystem) {
        System.out.println("Что вы хотите забронировать, введите 'зал' или 'место', без кавычек.\n" +
                "Далее через пробел номер зала или места.\n" +
                "Существующие на данный момент места и залы можно посмотреть в пункте меню 1.\n");
        /* Валидация не проводится, допускаем, что пользователь крайне дисциплинирован */
        String placeAndNumber = scanner.nextLine().trim();
        String[] strData = placeAndNumber.split("\s+");
        Place enterPlace = null;
        if(strData[0].equals("зал")){
            enterPlace = hallBase.readHall(Integer.parseInt(strData[1]));
        } else if(strData[0].equals("место")){
            enterPlace = workplaceBase.readWorkPlace(Integer.parseInt(strData[1]));
        }
        System.out.println("Введите дату в формате (yyyy-mm-dd):");
        /* Проверка валидации не проводится, допускаем, что пользователь крайне дисциплинирован */
        String reserveDate = scanner.nextLine().trim();
        System.out.println("Выберите номер слота для резервирования из списка: \n" +
                "10 - 10:00 - 11:00\n" +
                "11 - 11:00 - 12:00\n" +
                "12 - 12:00 - 13:00\n" +
                "13 - 13:00 - 14:00\n" +
                "14 - 14:00 - 15:00\n" +
                "15 - 15:00 - 16:00\n" +
                "16 - 16:00 - 17:00\n" +
                "17 - 17:00 - 18:00\n" +
                "18 - 18:00 - 19:00\n");
        String reserveSlot = scanner.nextLine().trim();
        coworkingSpaceController.reserveSlot(userEnteredToSystem,
                                             enterPlace,
                                             LocalDate.parse(reserveDate),
                                             Integer.parseInt(reserveSlot));
        readAndGoForward(scanner);
    }

    /**
     * String view of all free slots filter by date
     *
     * @param scanner    the keyboard scanner
     */
    private static void viewAllFreeSlotsByEnterDate(Scanner scanner) {
        ReadFreeSlotsByDateDto readFreeSlotsByDateDto =
                                  new ReadFreeSlotsByDateDto(reserveBase, hallBase, workplaceBase);
        System.out.println("Введите дату в формате (yyyy-mm-dd): ");
        /* Проверка валидации не проводится, допускаем, что пользователь крайне дисциплинирован */
        String enterDate = scanner.nextLine().trim();
        readFreeSlotsByDateDto.viewFreeSlots(LocalDate.parse(enterDate));
        readAndGoForward(scanner);
    }

    /**
     * String view of all places of our coworking center
     *
     * @param scanner    the keyboard scanner
     */
    private static void viewAllPlaces(Scanner scanner) {
        ReadAllPlacesDto readAllPlacesDto =
                                  new ReadAllPlacesDto(hallBase, workplaceBase);
        readAllPlacesDto.getAllPlaces();
        readAndGoForward(scanner);
    }

    private static void readAndGoForward(Scanner scanner){
        System.out.println("\nЕсли изучили информацию нажмите Enter для продолжения.");
        scanner.nextLine();
    }

    /**
     * Exit menu
     *
     */
    private static boolean exitMenu(){
        System.out.print("\nПродолжить работу (Yes/No): ");
        Scanner scanner = new Scanner(System.in);
        String yesOrNoAnswer = scanner.nextLine().trim();

        Pattern yesPattern = Pattern.compile("Yes|yes");
        Matcher yesMatcher = yesPattern.matcher(yesOrNoAnswer);
        Pattern noPattern = Pattern.compile("No|no");
        Matcher noMatcher = noPattern.matcher(yesOrNoAnswer);

        if (yesMatcher.matches()) {
            repeatMenu = true;
        }
        else if (noMatcher.matches()) {
            repeatMenu = false;
        }
        else exitMenu() ;

        return repeatMenu;
    }
}
