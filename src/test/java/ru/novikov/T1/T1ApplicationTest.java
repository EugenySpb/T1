package ru.novikov.T1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = T1Application.class)
class T1ApplicationTest extends AbstractContainerBaseTest {

    @Test
    @DisplayName(value = "Контекст успешно инициализируется")
    void contextLoads() {
    }

}