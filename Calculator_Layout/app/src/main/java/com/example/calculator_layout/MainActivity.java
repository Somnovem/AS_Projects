package com.example.calculator_layout;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {
    StringBuilder currentInput = new StringBuilder();
    Operator currentOperator = Operator.NONE;
    BigDecimal operand1 = null;
    enum Operator {
        NONE, ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setNumberButtonClickListeners();
        setOperatorButtonClickListeners();

        findViewById(R.id.btnEqual).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateResult();
            }
        });

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInput();
            }
        });

        findViewById(R.id.allClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allClearInput();
            }
        });

        findViewById(R.id.btnBackspace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBackspace();
            }
        });
    }

    private void setNumberButtonClickListeners() {
        int[] numberButtonIds = {R.id.btnZero, R.id.btnOne, R.id.btnTwo, R.id.btnThree, R.id.btnFour,
                R.id.btnFive, R.id.btnSix, R.id.btnSeven, R.id.btnEight, R.id.btnNine, R.id.btnDot};

        for (int id : numberButtonIds) {
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button = (Button) v;
                    appendNumber(button.getText().toString());
                }
            });
        }
    }

    private void setOperatorButtonClickListeners() {
        int[] operatorButtonIds = {R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide};

        for (int id : operatorButtonIds) {
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Operator operator = Operator.NONE;
                    if (v.getId() == R.id.btnPlus) operator = Operator.ADD;
                    else if (v.getId() == R.id.btnMinus) operator = Operator.SUBTRACT;
                    else if (v.getId() == R.id.btnMultiply) operator = Operator.MULTIPLY;
                    else operator = Operator.DIVIDE;
                    setOperator(operator);
                }
            });
        }
    }

    private void appendNumber(String number) {
        currentInput.append(number);
        updateDisplay();
    }

    private void handleBackspace() {
        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
            updateDisplay();
        }
    }

    private void setOperator(Operator operator) {
        if (operand1 == null) {
            operand1 = new BigDecimal(currentInput.toString());
            currentInput.setLength(0);
        }
        ((TextView) findViewById(R.id.tvOldInput)).setText(operand1.toString());
        ((TextView) findViewById(R.id.tvInput)).setText("");
        currentOperator = operator;
        ((TextView) findViewById(R.id.tvCurrentOperand)).setText(operatorToString(operator));
    }

    private String operatorToString(Operator operator) {
        switch (operator) {
            case ADD:
                return "+";
            case SUBTRACT:
                return "-";
            case MULTIPLY:
                return "×";
            case DIVIDE:
                return "÷";
            case NONE:
            default:
                return "";
        }
    }

    private void calculateResult() {
        BigDecimal operand2 = new BigDecimal(currentInput.toString());
        BigDecimal result = null;
        Log.d("CalculatorApp", "operand1= " + operand1);
        Log.d("CalculatorApp", "operand2= " + operand2);
        Log.d("CalculatorApp", "currentInput= " + currentInput.toString());

        switch (currentOperator) {
            case ADD:
                result = operand1.add(operand2);
                break;
            case SUBTRACT:
                result = operand1.subtract(operand2);
                break;
            case MULTIPLY:
                result = operand1.multiply(operand2);
                break;
            case DIVIDE:
                if (!operand2.equals(BigDecimal.ZERO)) {
                    result = operand1.divide(operand2, 10, BigDecimal.ROUND_HALF_UP);
                }
                break;
            case NONE:
                result = operand2;
                break;
        }

        if (result != null) {
            ((TextView) findViewById(R.id.tvOldInput)).setText(operand1 + operatorToString(currentOperator) + operand2);
            ((TextView) findViewById(R.id.tvInput)).setText(result.toString());
            operand1 = result;
        }
    }

    private void allClearInput() {
        currentInput.setLength(0);
        operand1 = null;
        currentOperator = Operator.NONE;
        ((TextView) findViewById(R.id.tvOldInput)).setText("");
        ((TextView) findViewById(R.id.tvInput)).setText("0");
        ((TextView) findViewById(R.id.tvCurrentOperand)).setText("");
    }

    private void clearInput() {
        currentInput.setLength(0);
        currentOperator = Operator.NONE;
        ((TextView) findViewById(R.id.tvInput)).setText("0");
        operand1 = null;
    }

    private void updateDisplay() {
        ((TextView) findViewById(R.id.tvInput)).setText(currentInput.toString());
    }
}
