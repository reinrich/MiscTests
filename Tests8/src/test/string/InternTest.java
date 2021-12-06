package test.string;

public class InternTest {

    public static void main(String[] args) {
        // This test verifies that interned strings are always
        // deduplicated when being interned, and never after
        // being interned.

        System.out.println("Begin: InternedTest");

        // Intern the new duplicate
        char[] letters = {'h', 'e', 'l','l', 'o', '!'};
        String notInterned;
        notInterned = new String(letters);
        String internedString = notInterned.intern();
        if (internedString != notInterned) {
            System.out.println("WARNING1: internedString != notInterned");
        }

        String internedString2 = internedString.intern();
        if (internedString != internedString2) {
            System.out.println("WARNING2: internedString != internedString2");
        }

        System.out.println("End: InternedTest");
    }
}
