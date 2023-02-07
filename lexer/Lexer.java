package lexer;

import java.io.IOException;
import java.util.Hashtable;

public class Lexer {

	public static int line = 1; //记录代码行号
	char peek = ' '; //获取输入字符
	Hashtable words = new Hashtable();

	void reserve(Word w) {
		words.put(w.lexeme, w);
	} //Hashtable put(key,value)

	public Lexer() { // 构造函数，预存保留字
		reserve(new Word("if", Tag.IF));
		reserve(new Word("else", Tag.ELSE));
		reserve(new Word("while", Tag.WHILE));
		reserve(new Word("do", Tag.DO));
		reserve(new Word("break", Tag.BREAK));
		reserve(new Word("for", Tag.FOR));
		reserve(Word.True);
		reserve(Word.False);
	}

	//读入decaf源码的字符
	public void readch() throws IOException {
		peek = (char) System.in.read();
		
	}

	//判定符号c是否与下一个读入符号匹配
	boolean readch(char c) throws IOException {
		readch();
		if (peek != c) {
			return false;
		}
		peek = ' ';
		return true;
	}

	public Token scan() throws IOException {
		//循环读入decaf源码的空白字符, 忽略
		for (;; readch()) {
			if (peek == ' ' || peek == '\t') // 空白符或者制表符，继续循环
				continue;
			else if (peek == '\n') { //换行 line++
				line += 1;
			} else {
				break;
			}
		}
		switch (peek) { // 判断逻辑和关系运算符号是否匹配，如果是逻辑和关系运算符号，输出该token，否则创建新token（报错）
		case '&':
			if (readch('&'))
				return Word.and;
			else
				return new Token('&');
		case '|':
			if (readch('|'))
				return Word.or;
			else
				return new Token('|');
		case '=':
			if (readch('='))
				return Word.eq;
			else
				return new Token('=');
		case '!':
			if (readch('='))
				return Word.ne;
			else
				return new Token('!');
		case '<':
			if (readch('='))
				return Word.le;
			else
				return new Token('<');
		case '>':
			if (readch('='))
				return Word.ge;
			else
				return new Token('>');
		}
		//如果peek是数字
		if (Character.isDigit(peek)) {
			int v = 0;
			do {
				v = 10 * v + Character.digit(peek, 10);//Character.digit(char ch, int radix) 返回值若是0~9之间，则参数ch必为0~9的数字字符串，返回值在10~35之间，则参数必为字母
				readch();
			} while (Character.isDigit(peek));
			if (peek != '.') //是不是小数点，如果是则 抽取float，否则 输出int token（Tag.NUM）
				return new Num(v);
			float x = v;
			float d = 10;
			for (;;) {
				readch();
				if (!Character.isDigit(peek))
					break;
				x = x + Character.digit(peek, 10) / d; //读取浮点数x后的小数部分
				d = d * 10;
			}
			return new Real(x);// 输出float token(Tag.REAL)
		}
		//如果peek是字母
		if (Character.isLetter(peek)) {
			StringBuffer b = new StringBuffer();
			do {
				b.append(peek);
				readch();
			} while (Character.isLetterOrDigit(peek));
			String s = b.toString();
			Word w = (Word) words.get(s); //串是否为预存的保留字（if else do while break）
			if (w != null)// 如果是保留字，输出
				return w;
			w = new Word(s, Tag.ID); //如果不是预存的保留字，则构建Word，Tag.ID
			words.put(s, w); //存入hashtable
			return w;
		}
		if (peek == '@' || peek == '#'){
			System.out.println("symbol error");
		}

		Token tok = new Token(peek);
		peek = ' ';
		return tok; //输出其它符号 +， - ，*， /，！，>, <, =, &, |
	}
	
	public void out() {
		System.out.println(words.size());
		
	}

	public char getPeek() {
		return peek;
	}

	public void setPeek(char peek) {
		this.peek = peek;
	}

}
