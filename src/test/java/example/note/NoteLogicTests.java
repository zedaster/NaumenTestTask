package example.note;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Тесты для NoteLogic
 */
public class NoteLogicTests {
    /**
     * Сообщение, которое выводиться при отсутствии заметок.
     */
    private static final String EMPTY_NOTES_RESPONSE = """
                Your notes:
                <no notes>""";

    /**
     * Экземпляр NoteLogic
     */
    private NoteLogic noteLogic;

    /**
     * Пересоздаем NoteLogic перед каждым тестом
     */
    @Before
    public void beforeEach() {
        noteLogic = new NoteLogic();
    }

    /**
     * Тест добавления корректной несуществующей заметки и ее вывода
     */
    @Test
    public void addCorrectNoteAndShow() {
        List<String> addValues = List.of("Заметка", "Несколько слов", "Тутчисла123");
        List<String> expectShows = List.of("""
                Your notes:
                1. Заметка""", """
                Your notes:
                1. Несколько слов""", """
                Your notes:
                1. Тутчисла123""");

        for (int i = 0; i < addValues.size(); i++) {
            noteLogic = new NoteLogic();
            String addResponse = noteLogic.handleMessage("/add " + addValues.get(i));
            Assert.assertEquals("Note added!", addResponse);
            String yourNotesResponse = noteLogic.handleMessage("/notes");
            Assert.assertEquals("""
                Your notes:
                1. %s""".formatted(expectShows.get(i)), yourNotesResponse);
        }
    }

    /**
     * Тест добавления некорректной заметки и пустого вывода
     */
    @Test
    public void addIncorrectNoteAndShowNothing() {
        List<String> addValues = List.of("", " ", "    ");

        for (String value : addValues) {
            noteLogic = new NoteLogic();
            String addResponse = noteLogic.handleMessage("/add " + value);
            Assert.assertEquals("Command is incorrect! Please enter /add <note name>", addResponse);
            String yourNotesResponse = noteLogic.handleMessage("/notes");
            Assert.assertEquals("You don't have any notes!", yourNotesResponse);
        }

    }

    /**
     * Тест добавления существующей заметки и попытки вывода
     */
    @Test
    public void addSameNotes() {
        noteLogic.handleMessage("/add Note");
        String addResponseTwo = noteLogic.handleMessage("/add Note");
        Assert.assertEquals("Note added!", addResponseTwo);
        String yourNotesResponse = noteLogic.handleMessage("/notes");
        Assert.assertEquals("""
                Your notes:
                1. Заметка
                2. Заметка""", yourNotesResponse);
    }

    /**
     * Тест на редактирование существующей заметки по ID
     */
    @Test
    public void editExistingNote() {
        List<String> editValues = List.of("Заметка", "Несколько слов", "Тутчисла123");
        List<String> expectShows = List.of("""
                Your notes:
                1. Заметка""", """
                Your notes:
                1. Несколько слов""", """
                Your notes:
                1. Тутчисла123""");

        for (int i = 0; i < editValues.size(); i++) {
            noteLogic = new NoteLogic();
            noteLogic.handleMessage("/add Something");
            String editResponse = noteLogic.handleMessage("/edit 1 " + editValues.get(i));
            Assert.assertEquals("Note edited!", editResponse);
            String yourNotesResponse = noteLogic.handleMessage("/notes");
            Assert.assertEquals("""
                Your notes:
                1. %s""".formatted(expectShows.get(i)), yourNotesResponse);
        }
    }

    /**
     * Тест на редактирование несуществующей заметки по ID
     */
    @Test
    public void editNonExistingNote() {
        noteLogic.handleMessage("/add Something");
        String editResponse = noteLogic.handleMessage("/edit 2 Edited");
        Assert.assertEquals("Note with id 2 doesn't exist!", editResponse);
    }

    /**
     * Тест на некорректную команду edit
     */
    @Test
    public void incorrectEditCommand() {
        List<String> incorrectCommands = List.of("/edit", "/edit ", "/edit  ", "/edit 1", "/edit 1  ");

        for (String editCommand : incorrectCommands) {
            noteLogic = new NoteLogic();
            noteLogic.handleMessage("/add Something");
            String editResponse = noteLogic.handleMessage(editCommand);
            Assert.assertEquals("Command is incorrect! Please enter /edit <id> <new content>", editResponse);
        }
    }



    /**
     * Тест на удаление существующей заметки
     */
    @Test
    public void deleteExistingNote() {
        noteLogic.handleMessage("/add Something");
        String deleteResponse = noteLogic.handleMessage("/del 1");
        Assert.assertEquals("Note deleted!", deleteResponse);
        String yourNotesResponse = noteLogic.handleMessage("/notes");
        Assert.assertEquals(EMPTY_NOTES_RESPONSE, yourNotesResponse);
    }

    /**
     * Тест на удаление несуществующей заметки
     */
    @Test
    public void deleteNonExistingNote() {
        noteLogic.handleMessage("/add Something");
        String deleteResponse = noteLogic.handleMessage("/del 2");
        Assert.assertEquals("Note with id 2 doesn't exist!", deleteResponse);
        String yourNotesResponse = noteLogic.handleMessage("/notes");
        Assert.assertEquals(EMPTY_NOTES_RESPONSE, yourNotesResponse);
    }

    /**
     * Тест на некорректную команду /del
     */
    @Test
    public void incorrectDeleteCommand() {
        List<String> incorrectCommands = List.of("/del", "/del ", "/del  ", "/del 1 1");

        for (String delCommand : incorrectCommands) {
            noteLogic = new NoteLogic();
            noteLogic.handleMessage("/add Something");
            String delResponse = noteLogic.handleMessage(delCommand);
            Assert.assertEquals("Command is incorrect! Please enter /del <id>", delResponse);
        }
    }

    /**
     * Тест на неизвестную команду
     */
    @Test
    public void incorrectCommand() {
        List<String> incorrectCommands = List.of("/incorrect", "/incorrect /add Something");

        for (String incorrectCommand : incorrectCommands) {
            noteLogic = new NoteLogic();
            String response = noteLogic.handleMessage(incorrectCommand);
            Assert.assertEquals("Unknown command", response);
            String yourNotesResponse = noteLogic.handleMessage("/notes");
            Assert.assertEquals(EMPTY_NOTES_RESPONSE, yourNotesResponse);
        }
    }
}
