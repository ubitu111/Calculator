package ru.kireev.mir.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.EmptyStackException;
import java.util.regex.Pattern;

import ru.kireev.mir.calculator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

    }

    public void onClickCalculate(View view) {
        //убираем пробелы из выражения, заменяем запятые на точки (для дробных чисел)
        String expression = binding.editText.getText().toString().replaceAll("\\s", "").replaceAll(",", ".");

        //проверяем, что выражение содержит только валидные данные для рассчета (числа, скобки и знаки математических операций)
        //если нет, выводим тост с предупреждением
        if (Pattern.matches("[()+\\-/*.\\d]*", expression)){
            StringCalculator calculator = new StringCalculator(expression);
            String result;
            try {
                double resultDouble = calculator.getResult();
                if (Double.isInfinite(resultDouble)) {
                    result = "Деление на ноль! Проверьте выражение.";
                } else {
                    result = Double.toString(resultDouble);
                }

            } catch (EmptyStackException | WrongExpressionException e) {
                result = "Ошибка в выражении! Просьба проверить входные данные.";
            }
            binding.tvResult.setText(result);

        } else {
            Toast.makeText(this, "Неверное выражение!" +
                    "\nВы должны использовать только числа, скобки и знаки + - * /", Toast.LENGTH_SHORT).show();
        }
    }


}
