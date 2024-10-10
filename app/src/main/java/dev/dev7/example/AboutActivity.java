package dev.dev7.example;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about); // Замените на ваш реальный файл макета

        // Инициализируем Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            // Включаем кнопку "Назад"
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Устанавливаем заголовок, если нужно
            getSupportActionBar().setTitle("О нас");
        }

        // Остальная инициализация активности
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обработка нажатия на кнопку "Назад" в Toolbar
        if (item.getItemId() == android.R.id.home) {
            // Завершение текущей активности и возврат к предыдущей
            finish(); // Или используйте onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
