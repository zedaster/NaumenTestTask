package example.bot;

import java.util.ArrayList;
import java.util.List;

/**
 * Фейковый класс бота для тестирования сообщений
 */
public class FakeBot implements Bot {
    /**
     * Список сообщений
     */
    private final List<Message> messages;

    public FakeBot() {
        this.messages = new ArrayList<>();
    }

    /**
     * Метод отправки добавляет сообщение в список
     */
    @Override
    public void sendMessage(Long chatId, String messageText) {
        Message msg = new Message(chatId, messageText);
        messages.add(msg);
    }

    /**
     * Получение отправленных ботом сообщений
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Запись, содержащая в себе chatId пользователя и текст сообщения
     */
    public record Message(Long chatId, String text) {

    }
}
