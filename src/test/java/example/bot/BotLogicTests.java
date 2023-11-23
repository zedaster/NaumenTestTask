package example.bot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * Тесты для команд "/test", "/notify", "/repeat" в боте
 */
public class BotLogicTests {
    /**
     * Фейковый бот, который складывает все сообщения в коллекцию
     */
    private FakeBot bot;

    /**
     * Логика бота, которая тестируется
     */
    private BotLogic logic;

    /**
     * Тестовый пользователь
     */
    private User user;

    /**
     * Очищает и создает новые переменные под каждый тест
     */
    @Before
    public void cleanVariables() {
        this.bot = new FakeBot();
        this.logic = new BotLogic(bot);
        this.user = new User(1L);
    }

    /**
     * Тестирует первоначальное состояние пользователя
     */
    @Test
    public void initState() {
        Assert.assertEquals(State.INIT, user.getState());
    }

    /**
     * Проверяет тестирование: первый вопрос
     */
    @Test
    public void firstStepTestCommand() {
        logic.processCommand(user, "/test");
        Assert.assertEquals(State.TEST, user.getState());
        Assert.assertEquals(1, bot.getMessages().size());
        Assert.assertEquals(user.getChatId(), bot.getMessages().get(0).chatId());
        Assert.assertEquals("Вычислите степень: 10^2", bot.getMessages().get(0).text());
    }

    /**
     * Проверяет отправку ответа на первый вопрос теста и выдачу второго вопроса
     */
    @Test
    public void firstCorrectAnswerTestCommand() {
        logic.processCommand(user, "/test");
        logic.processCommand(user, "100");
        Assert.assertEquals(3, bot.getMessages().size());
        Assert.assertEquals("Правильный ответ!", bot.getMessages().get(1).text());
        Assert.assertEquals("Сколько будет 2 + 2 * 2", bot.getMessages().get(2).text());
        Assert.assertEquals(State.TEST, user.getState());
    }

    /**
     * Проверяет отправку ответа на второй вопрос и завершение теста.
     */
    @Test
    public void secondCorrectAnswerTestCommand() {
        logic.processCommand(user, "/test");
        logic.processCommand(user, "100");
        logic.processCommand(user, "6");
        Assert.assertEquals(5, bot.getMessages().size());
        Assert.assertEquals("Правильный ответ!", bot.getMessages().get(3).text());
        Assert.assertEquals("Тест завершен", bot.getMessages().get(4).text());
        Assert.assertEquals(State.INIT, user.getState());
    }

    /**
     * Проверяет отправку неверного ответа на первый вопрос команды /test
     */
    @Test
    public void firstIncorrectAnswerTestCommand() {
        logic.processCommand(user, "/test");
        logic.processCommand(user, "-1");
        Assert.assertEquals(3, bot.getMessages().size());
        Assert.assertEquals("Вы ошиблись, верный ответ: 100", bot.getMessages().get(1).text());
        Assert.assertEquals("Сколько будет 2 + 2 * 2", bot.getMessages().get(2).text());
        Assert.assertEquals(State.TEST, user.getState());
    }

    /**
     * Проверяет отправку неверного ответа на второй вопрос /test
     */
    @Test
    public void secondIncorrectAnswerTestCommand() {
        logic.processCommand(user, "/test");
        logic.processCommand(user, "-1");
        logic.processCommand(user, "-1");
        Assert.assertEquals(5, bot.getMessages().size());
        Assert.assertEquals("Вы ошиблись, верный ответ: 6", bot.getMessages().get(3).text());
        Assert.assertEquals("Тест завершен", bot.getMessages().get(4).text());
        Assert.assertEquals(State.INIT, user.getState());
    }

    /**
     * Проверяет установку напоминания через /notify
     */
    @Test
    public void notifyCommand() {
        logic.processCommand(user, "/notify");
        Assert.assertEquals(State.SET_NOTIFY_TEXT, user.getState());
        logic.processCommand(user, "some text");
        Assert.assertEquals(State.SET_NOTIFY_DELAY, user.getState());
        logic.processCommand(user, "1000");
        Assert.assertEquals(State.INIT, user.getState());
        Assert.assertEquals(3, bot.getMessages().size());
        Assert.assertEquals("Введите текст напоминания", bot.getMessages().get(0).text());
        Assert.assertEquals("Через сколько секунд напомнить?", bot.getMessages().get(1).text());
        Assert.assertEquals("Напоминание установлено", bot.getMessages().get(2).text());
    }

    /**
     * Проверяет отправку напоминания, установленного через /notify
     */
    @Test
    public void checkDelayedMessage() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        logic.processCommand(user, "/notify");
        logic.processCommand(user, "some notification");
        logic.processCommand(user, "0");

        new Timer().schedule(new TimerTask() {
            public void run() {
                latch.countDown();
            }
        }, 50);

        latch.await();
        Assert.assertEquals(4, bot.getMessages().size());
        Assert.assertEquals("Сработало напоминание: 'some notification'", bot.getMessages().get(3).text());
    }

    /**
     * Проверяет установку напоминания с некорректным текстом (например, пустым символом).
     */
    @Test
    public void notifyWrongText() {
        List<String> wrongCases = List.of("", " ", "ㅤ");
        for (String wrongText : wrongCases) {
            logic.processCommand(user, "/notify");
            logic.processCommand(user, wrongText);
            Assert.assertEquals(2, bot.getMessages().size());
            Assert.assertEquals("Вы ввели некорректный текст!", bot.getMessages().get(1).text());
            Assert.assertEquals(State.SET_NOTIFY_TEXT, user.getState());
            bot.getMessages().clear();
        }
    }

    /**
     * Проверяет установку напоминания с некорректным ожиданием (например, пустым символом или -1).
     */
    @Test
    public void notifyWrongDelay() {
        List<String> wrongCases = List.of("", " ", "ㅤ", "-1");
        for (String wrongDelay : wrongCases) {
            logic.processCommand(user, "/notify");
            logic.processCommand(user, "some notification");
            logic.processCommand(user, wrongDelay);
            Assert.assertEquals(3, bot.getMessages().size());
            Assert.assertEquals("Пожалуйста, введите целое число", bot.getMessages().get(2).text());
            Assert.assertEquals(State.SET_NOTIFY_DELAY, user.getState());
            bot.getMessages().clear();
        }
    }

    /**
     * Проверяет вызов команды /repeat, если пользователю повторять нечего
     */
    @Test
    public void nothingToRepeat() {
        logic.processCommand(user, "/repeat");
        Assert.assertEquals(State.INIT, user.getState());
        Assert.assertEquals(1, bot.getMessages().size());
        Assert.assertEquals(user.getChatId(), bot.getMessages().get(0).chatId());
        Assert.assertEquals("Нет вопросов для повторения", bot.getMessages().get(0).text());
    }

    /**
     * Проверяет вызов команды /repeat, если нужно повторить один вопрос
     */
    @Test
    public void oneToRepeat() {
        logic.processCommand(user, "/test");
        logic.processCommand(user, "100");
        logic.processCommand(user, "-1");
        logic.processCommand(user, "/repeat");
        Assert.assertEquals(State.REPEAT, user.getState());
        Assert.assertEquals(6, bot.getMessages().size());
        Assert.assertEquals("Сколько будет 2 + 2 * 2", bot.getMessages().get(5).text());
        logic.processCommand(user, "6");
        Assert.assertEquals(8, bot.getMessages().size());
        Assert.assertEquals("Правильный ответ!", bot.getMessages().get(6).text());
        Assert.assertEquals("Тест завершен", bot.getMessages().get(7).text());
        Assert.assertEquals(State.INIT, user.getState());
    }

    /**
     * Проверяет вызов команды /repeat, если нужно повторить два вопроса
     */
    @Test
    public void twoToRepeat() {
        logic.processCommand(user, "/test");
        logic.processCommand(user, "-1");
        logic.processCommand(user, "-1");
        logic.processCommand(user, "/repeat");
        logic.processCommand(user, "100");
        logic.processCommand(user, "6");
        Assert.assertEquals(10, bot.getMessages().size());
        Assert.assertEquals("Вычислите степень: 10^2", bot.getMessages().get(5).text());
        Assert.assertEquals("Правильный ответ!", bot.getMessages().get(6).text());
        Assert.assertEquals("Сколько будет 2 + 2 * 2", bot.getMessages().get(7).text());
        Assert.assertEquals("Правильный ответ!", bot.getMessages().get(8).text());
        Assert.assertEquals("Тест завершен", bot.getMessages().get(9).text());
    }
}
