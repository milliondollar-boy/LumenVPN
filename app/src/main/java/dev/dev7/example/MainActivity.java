package dev.dev7.example;

import static dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_CONNECTION_STATE_BROADCAST_EXTRA;
import static dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_DURATION_BROADCAST_EXTRA;
import static dev.dev7.lib.v2ray.utils.V2rayConstants.V2RAY_SERVICE_STATICS_BROADCAST_INTENT;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Map;
import java.util.Objects;

import dev.dev7.lib.v2ray.V2rayController;
import dev.dev7.lib.v2ray.utils.V2rayConstants;

public class MainActivity extends AppCompatActivity {


    private TextView connection_time;
    private Button ddd;
    private ImageButton connection;
    private SharedPreferences sharedPreferences;
    private BroadcastReceiver v2rayBroadCastReceiver;
    private final String tgLink = "https://t.me/sale_lumen_bot";

    @SuppressLint({"SetTextI18n", "UnspecifiedRegisterReceiverFlag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        V2rayController.init(this, R.drawable.ic_launcher, "V2ray Android");
        connection = findViewById(R.id.imageButton4);
        ddd = findViewById(R.id.button15);
        connection_time = findViewById(R.id.textView2);
        sharedPreferences = getSharedPreferences("conf", MODE_PRIVATE);


        connection.setOnClickListener(view -> {
            String savedConfig = sharedPreferences.getString("v2ray_config", getDefaultConfig());
            if (savedConfig.isEmpty()) {
                showCenteredOkDialog();
            } else {
                if (V2rayController.getConnectionState() == V2rayConstants.CONNECTION_STATES.DISCONNECTED) {
                    vibrate();
                    connection.setImageResource(R.drawable.disconnection);
                    V2rayController.startV2ray(this, "Test Server", savedConfig, null);
                } else {
                    vibrate();
                    V2rayController.stopV2ray(this);
                }
            }
        });




        v2rayBroadCastReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(Context context, Intent intent) {runOnUiThread(() -> {

                connection_time.setText("Время подключения : " + Objects.requireNonNull(intent.getExtras()).getString(SERVICE_DURATION_BROADCAST_EXTRA));


                switch ((V2rayConstants.CONNECTION_STATES) Objects.requireNonNull(Objects.requireNonNull(intent.getExtras()).getSerializable(SERVICE_CONNECTION_STATE_BROADCAST_EXTRA))) {
                    case CONNECTED:
                        ddd.setText("Подключено");
                        break;
                    case DISCONNECTED:
                        connection.setImageResource(R.drawable.connection);
                        ddd.setText("Отключено");
                        break;
                    case CONNECTING:
                        ddd.setText("CONNECTING");
                        break;
                    default:
                        break;

                }
            });
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(v2rayBroadCastReceiver, new IntentFilter(V2RAY_SERVICE_STATICS_BROADCAST_INTENT), RECEIVER_EXPORTED);
        } else {
            registerReceiver(v2rayBroadCastReceiver, new IntentFilter(V2RAY_SERVICE_STATICS_BROADCAST_INTENT));
        }
    }

    private void showCenteredOkDialog() {
        // Создаем Builder для AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Инфлейтим кастомный макет
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_center_button, null);
        builder.setView(dialogView);

        // Создаем AlertDialog
        AlertDialog dialog = builder.create();

        // Находим кнопку в кастомном макете и устанавливаем слушатель
        Button buttonOK = dialogView.findViewById(R.id.buttonOK);
        buttonOK.setOnClickListener(v -> dialog.dismiss());

        // Показываем диалог
        dialog.show();
    }

    private void vibrate() {
        // Получаем Vibrator из контекста
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Проверяем, доступна ли вибрация на устройстве
        if (vibrator != null && vibrator.hasVibrator()) {
            // Вызов вибрации на 100 миллисекунд (Android 8.0+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
    }


    public static String getDefaultConfig() {
        return "";
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (v2rayBroadCastReceiver != null){
            unregisterReceiver(v2rayBroadCastReceiver);
        }
    }

    private String extractIdFromConfig(String url) {
        String textAfterHash = extractTextAfterHash(url);
        if (textAfterHash.contains("_")) {
            String[] parts = textAfterHash.split("_");
            if (parts.length > 1) {
                return parts[1]; // Возвращаем часть после _
            }
        }
        return ""; // Если символа _ нет или нет текста после него, возвращаем пустую строку
    }

    private String extractTextAfterHash(String url) {
        if (url.contains("#")) {
            // Разделяем строку по символу #
            String[] parts = url.split("#");
            if (parts.length > 1) {
                return parts[1]; // Возвращаем текст после #
            }
        }
        return ""; // Если символа # нет или нет текста после него, возвращаем пустую строку
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.popup_menu, menu);

        MenuItem configurationsMenuItem = menu.findItem(R.id.configurations);
        MenuItem userId = menu.findItem(R.id.id);
        SubMenu configurationsSubMenu = configurationsMenuItem.getSubMenu();

        // Загружаем сохраненные конфигурации из SharedPreferences
        sharedPreferences = getSharedPreferences("conf", MODE_PRIVATE);
        Map<String, ?> allConfigs = sharedPreferences.getAll();



        if (allConfigs != null && !allConfigs.isEmpty()) {
            for (Map.Entry<String, ?> entry : allConfigs.entrySet()) {
                String configName = extractTextAfterHash(entry.getValue().toString()); // Имя конфигурации
                String configUrl = entry.getValue().toString(); // Ссылка конфигурации

                String configId = extractIdFromConfig(configUrl); // Извлекаем ID

                if(userId != null && configId != null) {
                    // Форматируем название для подменю
                    userId.setTitle("ID: " + configId);
                }

                // Добавляем элемент в подменю
                assert configurationsSubMenu != null;
                configurationsSubMenu.add(configName).setIcon(R.drawable.baseline_delete_forever_24)
                        .setOnMenuItemClickListener(item -> {
                            showConfigDialog(configName, configUrl); // Открываем диалог при нажатии
                            return true;
                        });
            }
        } else {
            assert configurationsSubMenu != null;
            configurationsSubMenu.add("Нет сохраненных конфигураций");
        }
        invalidateOptionsMenu();

        return true;
    }
    private void showConfigDialog(String configName, String configUrl) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(configName); // Устанавливаем название конфигурации

        // Создаем TextView для отображения ссылки
        TextView linkView = new TextView(this);
        linkView.setText(configUrl); // Отображаем конфигурационную ссылку
        linkView.setPadding(40, 40, 40, 40); // Добавляем отступы для красоты

        // Устанавливаем View в диалог
        builder.setView(linkView);

        // Добавляем кнопку "Скопировать"
        builder.setPositiveButton("Скопировать", (dialog, which) -> {
            copyToClipboard(configUrl); // Копируем ссылку в буфер обмена
        });

        // Добавляем кнопку "Удалить"
        builder.setNegativeButton("Удалить", (dialog, which) -> {
            showDeleteConfirmationDialog(configName); // Удаление конфигурации
        });

        // Добавляем кнопку "Отмена"
        builder.setNeutralButton("Отмена", (dialog, which) -> dialog.cancel());

        // Показываем диалог
        builder.show();
    }
    private void copyToClipboard(String text) {
        // Используем систему для копирования текста в буфер обмена
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("VPN Configuration", text);
        clipboard.setPrimaryClip(clip);

        // Показываем сообщение, что ссылка скопирована
        Toast.makeText(this, "Ссылка скопирована в буфер обмена", Toast.LENGTH_SHORT).show();
    }



    private void showDeleteConfirmationDialog(String configName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удалить конфигурацию?");
        builder.setMessage("Вы уверены, что хотите удалить конфигурацию \"" + configName + "\"?");
        builder.setPositiveButton("Удалить", (dialog, which) -> {
            resetConfig(); // Удаляем конфигурацию
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();


        if(id == R.id.vvod){
            showInputDialog(); // Показываем диалог для ввода конфигурации
        }
        if(id == R.id.bupher){
            importConfigFromClipboard();
        }
        if(id == R.id.tgBot){
            openTelegramBot();
        }
        if(id == R.id.share){
            shareApp();
        }

        if(id == R.id.about){
            aboutUs();
        }

        return super.onOptionsItemSelected(item);

    }



    private void showInputDialog() {
        // Создаем AlertDialog для ввода конфигурации
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите конфигурационную ссылку");

        // Создаем EditText для ввода
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT); // Указываем, что это текстовое поле
        builder.setView(input);

        // Устанавливаем кнопки
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String config = input.getText().toString().trim();

            // Проверка на корректность конфигурации
            if (!config.startsWith("vless://")) {
                Toast.makeText(this, "Некорректная конфигурация. Проверьте введенную ссылку.", Toast.LENGTH_SHORT).show();
                return; // Не сохраняем, если конфигурация некорректна
            }

            saveConfig(config); // Сохраняем введенную конфигурацию
        });
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());

        builder.show(); // Показываем диалог
    }


    private String getClipboardText() { // ввод из буфера обмена
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).getText().toString();
        }
        return null;
    }

    private void importConfigFromClipboard() { // ввод из буфера обмена
        String config = getClipboardText();
        if (config != null && !config.isEmpty()) {
            // Проверка на корректность конфигурации
            if (config.startsWith("vless://")) {
                saveConfig(config); // Сохраняем конфигурацию
                Toast.makeText(this, "Конфигурация успешно импортирована.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Некорректная конфигурация. Проверьте буфер обмена.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Буфер обмена пуст.", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveConfig(String config){
        sharedPreferences.edit().putString("v2ray_config", config).apply(); // Сохраняем конфигурацию
    }



    private void resetConfig() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("v2ray_config"); // Удаляем сохранённую конфигурацию
        editor.apply();
        V2rayController.stopV2ray(this);

        // Здесь можно добавить уведомление о том, что конфигурация сброшена
        Toast.makeText(this, "Конфигурация сброшена, VPN отключен", Toast.LENGTH_SHORT).show();
    }

    public void shareApp(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Привет! Попробуй этот VPN: " + tgLink);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Поделиться ботом через"));
    }

    public void aboutUs(){
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    public void openTelegramBot(){
        Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse(tgLink));
        startActivity(telegram);
    }

}