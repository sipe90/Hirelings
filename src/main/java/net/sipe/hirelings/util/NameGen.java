package net.sipe.hirelings.util;

import java.util.Random;

public class NameGen {

    private static final String[] FIRST_NAMES = {"Jack", "John" , "Joffrey", "Pasi", "Donald", "Jon", "Robert", "Sean",
            "Tyrion", "Jorah", "Samwell", "Jaime", "Bran", "Davos", "Hodor", "Tormund", "Theon", "Petyr", "Sandor",
            "Podrick", "Ramsay", "Jaqen", "Eddard", "Khal", "Walder", "Gregor"};

    private static final String[] LAST_NAMES = {"Black", "Carmack", "Romero", "Härme", "Trump", "Snow", "Baratheon",
            "Bean", "Lannister", "Tarly", "Mormont", "Tarly", "Stark", "Seaworth", "Hodor", "Giantsbane", "Greyjoy",
            "Baelish", "Clegane", "Payne", "Bolton", "H'ghar", "Drogo", "Frey"};

    private static final Random RND = new Random();

    public static String randomFirstName() {
        return FIRST_NAMES[RND.nextInt(FIRST_NAMES.length)];
    }

    public static String randomLastName() {
        return LAST_NAMES[RND.nextInt(LAST_NAMES.length)];
    }

    public static String randomName() {
        return randomFirstName() + " " + randomLastName();
    }

}
