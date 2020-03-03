package ru.kireev.mir.calculator;

import androidx.annotation.NonNull;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class StringCalculator {
    //Математическое выражение для рассчета результата
    private String expression;

    //Индекс текущего элемента в выражении
    private int index;

    //Карта для хранения приоритета операций
    private Map<String, Integer> operationPriority;

    //Стек чисел, для проведения математической операции
    private Stack<Double> numbers;

    //Стек математических операций
    private Stack<String> operators;

    //Инициализируем поля класса, добавляем приоритеты операций в Map
    public StringCalculator(@NonNull String expression) {
        this.expression = expression;
        index = 0;
        numbers = new Stack<>();
        operators = new Stack<>();
        operationPriority = new HashMap<>();
        operationPriority.put("(", 0);
        operationPriority.put(")", 0);
        operationPriority.put("-", 1);
        operationPriority.put("+", 1);
        operationPriority.put("*", 2);
        operationPriority.put("/", 2);
    }

    //возвращаем результат вычисления, бросаем исключения в случае неверного выражения
    public Double getResult() throws EmptyStackException, WrongExpressionException {

        //пока есть элементы в выражении
        while (hasElements()) {
            //Берем элемент из выражения и пытаемся спарсить из строки число
            //В случае успеха добавляем число в стек чисел
            //В случае ошибки - ловим ее и парсим элемент
            String element = nextElement();

            try {
                double number = Double.parseDouble(element);
                numbers.push(number);
            } catch (NumberFormatException e) {

                switch (element) {
                    //если элемент открывающая скобка, добавляем ее в стек операторов
                    case "(":
                        operators.push(element);
                        break;

                    //если элемент закрывающая скобка, считаем выражения с помощью метода calculate(), до тех пор, пока не встретится открывающая скобка
                    //открывающую скобку убираем из стека операторов
                    case ")":
                        if (operators.peek().equals("(")) {
                            throw new WrongExpressionException();
                        }
                        while (!operators.peek().equals("(")) {
                            calculate();
                        }
                        operators.pop();
                        break;

                    default:

                        //если стек операторов пустой, то добавляем элемент в него
                        if (operators.empty()) {
                            operators.push(element);
                        } else {
                            Integer currentItemPriority = operationPriority.get(element);
                            Integer stackItemPriority = operationPriority.get(operators.peek());

                            if (currentItemPriority == null || stackItemPriority == null) {
                                throw new WrongExpressionException();
                            }
                            //иначе, пока стек операторов не пустой AND не дошли до скобок AND приоритет текущей операции меньше или равен верхнему в стеке операторов
                            //считаем выражение, затем вставляем элемент в стек операторов
                            while (!operators.empty() && !operators.peek().equals("(") && !operators.peek().equals(")") && currentItemPriority <= stackItemPriority){
                                calculate();
                            }

                            operators.push(element);
                        }
                        break;
                }
            }
        }
        //после парсинга проводим конечные расчеты
        while (!operators.empty()) {
            calculate();
        }
        //если стек с числами размером больше 1, бросаем исключение
        if (numbers.size() > 1) {
            throw new WrongExpressionException();
        }
        //иначе возвращаем результат операции
        return numbers.pop();
    }

    private boolean hasElements() {
        return !expression.isEmpty() && index < expression.length();
    }

    //разбивает выражение на элементы, возвращает число либо оператор
    private String nextElement() {
        StringBuilder operand = new StringBuilder();
        char element = expression.charAt(index);

        //проводит добавление в билдер до тех пор, пока элемент - число, точка, минус (в начале выражения, либо после открывающей скобки)
        //для склеивания одиночных символов в числа, дробные, минусовые
        while(Character.isDigit(element) || element == '.' || (index == 0 && element == '-') ||
                (index > 0 && expression.charAt(index - 1) == '(' && element == '-')) {
            operand.append(element);
            index++;

            //если дошли до конца выражения, то возвращаем строку из билдера
            if(index == expression.length()) {
                return operand.toString();
            }

            //берем следующий элемент выражения
            element = expression.charAt(index);
        }

        //если билдер не пустой (есть склеенное число) возвращаем строку из билдера
        if(operand.length() != 0)
            return operand.toString();

        //иначе увеличиваем индекс и возвращаем оператор
        index++;
        return Character.toString(element);
    }

    //Проводим математическую операцию (которая берется из стека операторов) с двумя верхними элементами стека чисел
    //Элементы, которые были взяты из стеков, удаляются из них
    //Бросаем исключение, если стеки пусты
    private void calculate() throws EmptyStackException, WrongExpressionException {
        String operator = operators.pop();
        Double secondNumber = numbers.pop();
        Double firstNumber = numbers.pop();
        double result;

        switch (operator) {
            case "+":
                result = firstNumber + secondNumber;
                break;
            case "-":
                result = firstNumber - secondNumber;
                break;
            case "*":
                result = firstNumber * secondNumber;
                break;
            case "/":
                result = firstNumber / secondNumber;
                break;
            default:
                throw new WrongExpressionException();
        }


        numbers.push(result);
    }




}
