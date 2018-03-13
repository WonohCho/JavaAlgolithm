package Algorithm;

/**
 * Created by Cho on 2018-03-12.
 */
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Calculator {
    //�����ڰ� �ƴ� ��ȣ
    private final String[] OPERATION0 = { "(", ")", "," };
    //�� �� ���� �ʿ��� �����ȣ(���� ���ʿ� ��ġ)
    private final String[] OPERATION1 = { "!" };
    //�� �� ���� �ʿ��� �����ȣ(���� �翷�� ��ġ) - ���ʿ��� ���������� ����Ѵ�.
    //��) 1 + 2 = 3, 6 / 3 = 2, 2 ^ 3 = 8..
    private final String[] OPERATION2 = { "+", "-", "*", "/", "^", "%" };
    //���� �ʿ���� ���� �����ȣ
    private final String[] WORD_OPERATION1 = { "pi", "e" };
    //�� �� ���� �ʿ��� ���� �����ȣ(��ȣ�� �����Ѵ�.)
    private final String[] WORD_OPERATION2 = { "sin", "sinh", "asin", "cos", "cosh", "acos", "tan", "tanh", "atan",
            "sqrt", "exp", "abs", "log", "ceil", "floor", "round" };
    //�� �� ���� �ʿ��� ���� �����ȣ(��ȣ, �޸��� �����Ѵ�.)
    private final String[] WORD_OPERATION3 = { "pow" };
    //�������� �� �ݿø� �ڸ���
    private int HARF_ROUND_UP = 6;

    //�̱���
    private static Calculator instance = null;
    /**
     * ������
     */
    private Calculator() {	}
    /**
     * �̱���
     */
    private static Calculator getInstance() {
        if (instance == null) {
            instance = new Calculator();
        }
        return instance;
    }
    /**
     * �ܺο��� ��û �� ��� �Լ�
     * @param data ����
     * @return
     */
    public static BigDecimal Calculate(String data) {
        return Calculator.getInstance().calc(data);
    }
    /**
     * ���ο��� �Ҹ��� ��� �Լ�
     * @param data ����
     * @return ��� ��
     */
    private BigDecimal calc(String data) {
        //��� �� ���� ��ĭ�� ���ش�.
        data = data.replace(" ", "");
        //��ū���� ����,�� ���еǴ� ��, ������ ��� ����
        // ��) (10+2)*(3+4)�� ���� (, 10, +, 2, ), *, (, 3, +, 4, )�� ���� �ȴ�.
        List< Object > tokenStack = makeTokens(data);
        // ���� ǥ������� ��ȯ�Ѵ�.
        // ��) (, 10, +, 2, ), *, (, 3, +, 4, )�� ���� 10 2 + 3 4 + * �� ����
        tokenStack = convertPostOrder(tokenStack);
        Stack< Object > calcStack = new Stack< Object >();
        // ���� ǥ��� ���
        // List�������� tocken�� ���� ������ ����, �����ڰ� ������ ����� �մϴ�.
        // 10 2 + ��� 12
        // 12 3 4 + ��� 12 7
        // 12 * 7 ��� 84
        //
        for (Object tocken : tokenStack) {
            calcStack.push(tocken);
            calcStack = calcPostOrder(calcStack);
        }
        //���ÿ� ���� ������ ����
        if (calcStack.size() != 1) {
            throw new RuntimeException("Calulator Error");
        }
        return (BigDecimal) calcStack.pop();
    }
    /**
     * ���� ǥ��� ���
     * @param calcStack ���ÿ� ����� �ִ� ��
     * @return
     */
    private Stack< Object > calcPostOrder(Stack< Object > calcStack) {
        //������ ���� ���� ���� ���� ��� ����
        if (calcStack.lastElement().getClass().equals(BigDecimal.class)) {
            return calcStack;
        }
        BigDecimal op1 = null;
        BigDecimal op2 = null;
        String opcode = null;
        //������ ���� ���ÿ� �ּ� 2�� �̻�
        if (calcStack.size() >= 2) {
            //������ ���� ���� ������
            opcode = (String) calcStack.pop();
            //���� ���� ��
            op1 = (BigDecimal) calcStack.pop();
            //�����ڰ� ���� 1�� �ʿ����� 2�� �ʿ����� üũ
            if (!opCodeCheck(opcode)) {
                op2 = (BigDecimal) calcStack.pop();
            }
            //���
            BigDecimal result = calculateByOpCode(op1, op2, opcode);
            calcStack.push(result);
        }
        return calcStack;
    }
    /**
     * �����ڰ� �ʿ��� ���� ����
     * @param opcode ������
     * @return �����ڰ� ���� 1�� �ʿ��ϸ� true, �����ڰ� ���� 2�� �ʿ��ϸ� false
     */
    private boolean opCodeCheck(String opcode) {
        return containWord(opcode, WORD_OPERATION2) || containWord(opcode, OPERATION1);
    }
    /**
     * �� �������� ��� �Լ�
     * @param op1 ��1
     * @param op2 ��2
     * @param opcode ������
     * @return
     */
    private BigDecimal calculateByOpCode(BigDecimal op1, BigDecimal op2, String opcode) {
        if (OPERATION2[0].equals(opcode)) {
            //���ϱ�
            return op1.add(op2);
        } else if (OPERATION2[1].equals(opcode)) {
            //����
            return op1.subtract(op2);
        } else if (OPERATION2[2].equals(opcode)) {
            //���ϱ�
            return op1.multiply(op2);
        } else if (OPERATION2[3].equals(opcode)) {
            //������, �ݿø��� ������ ��
            return op2.divide(op1, HARF_ROUND_UP, BigDecimal.ROUND_HALF_UP);
        } else if (OPERATION2[4].equals(opcode)) {
            //����
            return op2.pow(op1.intValue());
        } else if (OPERATION2[5].equals(opcode)) {
            //������
            return op2.remainder(op1);
        } else if (OPERATION1[0].equals(opcode)) {
            //���丮��
            return Factorial(op1);
        } else if (WORD_OPERATION2[0].equals(opcode)) {
            return BigDecimal.valueOf(Math.sin(op1.doubleValue()));
        } else if (WORD_OPERATION2[1].equals(opcode)) {
            return BigDecimal.valueOf(Math.sinh(op1.doubleValue()));
        } else if (WORD_OPERATION2[2].equals(opcode)) {
            return BigDecimal.valueOf(Math.asin(op1.doubleValue()));
        } else if (WORD_OPERATION2[3].equals(opcode)) {
            return BigDecimal.valueOf(Math.cos(op1.doubleValue()));
        } else if (WORD_OPERATION2[4].equals(opcode)) {
            return BigDecimal.valueOf(Math.cosh(op1.doubleValue()));
        } else if (WORD_OPERATION2[5].equals(opcode)) {
            return BigDecimal.valueOf(Math.acos(op1.doubleValue()));
        } else if (WORD_OPERATION2[6].equals(opcode)) {
            return BigDecimal.valueOf(Math.tan(op1.doubleValue()));
        } else if (WORD_OPERATION2[7].equals(opcode)) {
            return BigDecimal.valueOf(Math.tanh(op1.doubleValue()));
        } else if (WORD_OPERATION2[8].equals(opcode)) {
            return BigDecimal.valueOf(Math.atan(op1.doubleValue()));
        } else if (WORD_OPERATION2[9].equals(opcode)) {
            return BigDecimal.valueOf(Math.sqrt(op1.doubleValue()));
        } else if (WORD_OPERATION2[10].equals(opcode)) {
            return BigDecimal.valueOf(Math.exp(op1.doubleValue()));
        } else if (WORD_OPERATION2[11].equals(opcode)) {
            return BigDecimal.valueOf(Math.abs(op1.doubleValue()));
        } else if (WORD_OPERATION2[12].equals(opcode)) {
            return BigDecimal.valueOf(Math.log(op1.doubleValue()));
        } else if (WORD_OPERATION2[13].equals(opcode)) {
            return BigDecimal.valueOf(Math.ceil(op1.doubleValue()));
        } else if (WORD_OPERATION2[14].equals(opcode)) {
            return BigDecimal.valueOf(Math.floor(op1.doubleValue()));
        } else if (WORD_OPERATION2[15].equals(opcode)) {
            return BigDecimal.valueOf(Math.round(op1.doubleValue()));
        } else if (WORD_OPERATION3[0].equals(opcode)) {
            return op2.pow(op1.intValue());
        }
        throw new RuntimeException("Operation Error");
    }
    /**
     * ���丮�� �˰�����(��ͷ� ����)
     * @param input
     * @return
     */
    private BigDecimal Factorial(BigDecimal input) {
        if (BigDecimal.ONE.equals(input)) {
            return BigDecimal.ONE;
        }
        return Factorial(input.subtract(BigDecimal.ONE)).multiply(input);
    }
    /**
     * ����ǥ������� ��ȯ �Լ�
     * @param tokenList ��ū ����Ʈ
     * @return
     */
    private List< Object > convertPostOrder(List< Object > tokenList) {
        List< Object > postOrderList = new ArrayList<>();
        Stack<String> exprStack = new Stack<>();
        Stack<String> wordStack = new Stack<>();
        for (Object token : tokenList) {
            if (BigDecimal.class.equals(token.getClass())) {
                //���� �״�� �Է�
                postOrderList.add(token);
            } else {
                //������ ó��
                exprAppend((String) token, exprStack, wordStack, postOrderList);
            }
        }
        String item = null;
        //���� ������ �ֱ�
        while (!exprStack.isEmpty()) {
            item = exprStack.pop();
            postOrderList.add(item);
        }
        return postOrderList;
    }
    /**
     * ���� ������ ������ ����ó��
     * @param token ��ū
     * @param exprStack ������ ����(��ȣ��)
     * @param wordStack ������ ����(������)
     * @param postOrderList ���� ��� ����Ʈ(������)
     * @return
     */
    private void exprAppend(String token, Stack<String> exprStack, Stack<String> wordStack,
                            List< Object > postOrderList) {
        //��ū�� ������ ���ó��
        if (isWordOperation(token)) {
            //PI, E�� ��
            BigDecimal wordValue = ConverterWordResult(token);
            if (wordValue != null) {
                postOrderList.add(wordValue);
            } else {
                wordStack.push(token);
            }
        } else if (OPERATION0[0].equals(token)) {
            //���� ��ȣ( 
            exprStack.push(token);
        } else if (OPERATION0[1].equals(token)) {
            //������ ��ȣ) 
            String opcode = null;
            while (true) {
                //���� ������ ���� �� ����
                if (wordStack.size() > 0) {
                    //��ȣ�� ���ÿ��� �����´�.
                    opcode = exprStack.pop();
                    //���� ��ȣ(�� ������ �ۼ� �� 
                    if (OPERATION0[0].equals(opcode)) {
                        opcode = wordStack.pop();
                        postOrderList.add(opcode);
                        break;
                    }
                    //���� ������ ���� ��� ����Ʈ�� ���� �ִ´�.
                    postOrderList.add(opcode);
                } else {
                    //���� ������ ������ ����
                    if (exprStack.size() < 1) {
                        break;
                    }
                    opcode = exprStack.pop();
                    //���� ��ȣ(�� ������ �ۼ� �� 
                    if (OPERATION0[0].equals(opcode)) {
                        break;
                    }
                    postOrderList.add(opcode);
                }
            }
        } else if (OPERATION0[2].equals(token)) {
            //�޸� ó��
            //�޸��� ���� �����ڿ� ���� ����ϹǷ� �޸� �����ڰ� ���Դµ� ���� �����ڰ� ������ ����
            if (wordStack.size() < 1) {
                throw new RuntimeException("data error");
            }
            String opcode = null;
            while (true) {
                //���� ������ ������ ����
                if (exprStack.size() < 1) {
                    break;
                }
                //���� ��ȣ�� ����
                if (OPERATION0[0].equals(exprStack.lastElement())) {
                    break;
                }
                opcode = exprStack.pop();
                postOrderList.add(opcode);
            }
        } else if (isOperation(token)) {
            //������ ó��
            String opcode = null;
            while (true) {
                //�����ڰ� ������ �Է�
                if (exprStack.isEmpty()) {
                    exprStack.push(token);
                    break;
                }
                //�����ڰ� ������
                opcode = exprStack.pop();
                //������ �켱���� üũ + * �� ������ *��� ����(���ÿ� �ʰ� ���� �� FIFO��Ģ���� ���� ����)
                if (exprOrder(opcode) <= exprOrder(token)) {
                    exprStack.push(opcode);
                    exprStack.push(token);
                    break;
                }
                postOrderList.add(opcode);
            }
        }
    }
    /**
     * ��ū ����� �Լ�
     * @param inputData
     * @return
     */
    private List< Object > makeTokens(String inputData) {
        List< Object > tokenStack = new ArrayList<>();
        StringBuffer numberTokenBuffer = new StringBuffer();
        StringBuffer wordTokenBuffer = new StringBuffer();
        int argSize = inputData.length();
        char token;
        for (int i = 0; i < argSize; i++) {
            //char�������� ����
            token = inputData.charAt(i);
            //�� ��ū
            if (!isOperation(token)) {
                //���ڿ��� ������ �ִ´�.
                setWordOperation(tokenStack, wordTokenBuffer);
                numberTokenBuffer.append(token);
                if (i == argSize - 1) {
                    setNumber(tokenStack, numberTokenBuffer);
                }
            } else {
                //�����ڸ� ������ ���� �Է�
                setNumber(tokenStack, numberTokenBuffer);
                if (setOperation(tokenStack, token)) {
                    continue;
                }
                //��ȣ �����ڰ� �ƴϸ� ���� ������
                wordTokenBuffer.append(token);
                setWordOperation(tokenStack, wordTokenBuffer);
            }
        }
        return tokenStack;
    }
    /**
     * ��ȣ ������ �Է� 
     * @param tokenStack
     * @param token
     * @return
     */
    private boolean setOperation(List< Object > tokenStack, char token) {
        String tokenBuffer = Character.toString(token);
        if (containWord(tokenBuffer, OPERATION2) || containWord(tokenBuffer, OPERATION1)
                || containWord(tokenBuffer, OPERATION0)) {
            tokenStack.add(tokenBuffer);
            return true;
        }
        return false;
    }
    /**
     * ���� ������ �Է�
     * @param tokenStack
     * @param tokenBuffer
     */
    private void setWordOperation(List< Object > tokenStack, StringBuffer tokenBuffer) {
        if (isWordOperation(tokenBuffer)) {
            tokenStack.add(tokenBuffer.toString());
            tokenBuffer.setLength(0);
        }
    }
    /**
     * ���� �Է�
     * @param tokenStack
     * @param tokenBuffer
     */
    private void setNumber(List< Object > tokenStack, StringBuffer tokenBuffer) {
        if (tokenBuffer.length() > 0) {
            BigDecimal number = new BigDecimal(tokenBuffer.toString());
            tokenStack.add(number);
            tokenBuffer.setLength(0);
        }
    }
    /**
     * ������ üũ �Լ�
     * @param token
     * @param check
     * @return
     */
    private boolean containWord(String token, String[] check) {
        if (token == null) {
            return false;
        }
        for (String word : check) {
            if (word.equals(token)) {
                return true;
            }
        }
        return false;
    }
    /**
     * ���� ������ ���� üũ
     * @param wordTokenBuffer
     * @return
     */
    private boolean isWordOperation(StringBuffer wordTokenBuffer) {
        String wordToken = wordTokenBuffer.toString();
        return isWordOperation(wordToken);
    }
    /**
     * ���� ������ ���� üũ
     * @param wordTokenBuffer
     * @return
     */
    private boolean isWordOperation(String wordToken) {
        return containWord(wordToken, WORD_OPERATION3) || containWord(wordToken, WORD_OPERATION2)
                || containWord(wordToken, WORD_OPERATION1);
    }
    /**
     * ���� �ʿ���� �������� ���� ���� �����´�.(PI, E)
     * @param wordToken
     * @return
     */
    private BigDecimal ConverterWordResult(String wordToken) {
        if (containWord(wordToken, WORD_OPERATION1)) {
            if (WORD_OPERATION1[0].equals(wordToken.toLowerCase())) {
                return BigDecimal.valueOf(Math.PI);
            } else if (WORD_OPERATION1[1].equals(wordToken.toLowerCase())) {
                return BigDecimal.valueOf(Math.E);
            }
        }
        return null;
    }
    /**
     * ��ȣ ���������� üũ
     * @param token
     * @return
     */
    private boolean isOperation(String token) {
        return containWord(token, OPERATION2) || containWord(token, OPERATION1);
    }
    /**
     * ��ȣ ���������� üũ
     * @param token
     * @return
     */
    private boolean isOperation(char token) {
        if ((token >= 48 && token <= 57) || token == 46) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * ��ȣ �켱���� ��
     * @param s
     * @return
     */
    private int exprOrder(String s) {
        if (s == null)
            throw new NullPointerException();
        int order = -1;
        if ("-".equals(s) || "+".equals(s)) {
            order = 0;
        } else if ("*".equals(s) || "/".equals(s) || "%".equals(s)) {
            order = 1;
        } else if ("^".equals(s) || "!".equals(s)) {
            order = 2;
        }
        return order;
    }
}