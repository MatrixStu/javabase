package com.tustcs.base.utils;

/**
 * Created by yhy on 2017/4/15.
 */

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class JSTokener {
    private long character;
    private boolean eof;
    private long index;
    private long line;
    private char previous;
    private Reader reader;
    private boolean usePrevious;

    public JSTokener(Reader reader) {
        this.reader = (Reader)(reader.markSupported()?reader:new BufferedReader(reader));
        this.eof = false;
        this.usePrevious = false;
        this.previous = 0;
        this.index = 0L;
        this.character = 1L;
        this.line = 1L;
    }

    public JSTokener(InputStream inputStream) throws JSException {
        this((Reader)(new InputStreamReader(inputStream)));
    }

    public JSTokener(String s) {
        this((Reader)(new StringReader(s)));
    }

    public void back() throws JSException {
        if(!this.usePrevious && this.index > 0L) {
            --this.index;
            --this.character;
            this.usePrevious = true;
            this.eof = false;
        } else {
            throw new JSException("Stepping back two steps is not supported");
        }
    }

    public static int dehexchar(char c) {
        return c >= 48 && c <= 57?c - 48:(c >= 65 && c <= 70?c - 55:(c >= 97 && c <= 102?c - 87:-1));
    }

    public boolean end() {
        return this.eof && !this.usePrevious;
    }

    public boolean more() throws JSException {
        this.next();
        if(this.end()) {
            return false;
        } else {
            this.back();
            return true;
        }
    }

    public char next() throws JSException {
        int c;
        if(this.usePrevious) {
            this.usePrevious = false;
            c = this.previous;
        } else {
            try {
                c = this.reader.read();
            } catch (IOException var3) {
                throw new JSException(var3);
            }

            if(c <= 0) {
                this.eof = true;
                c = 0;
            }
        }

        ++this.index;
        if(this.previous == 13) {
            ++this.line;
            this.character = c == 10?0L:1L;
        } else if(c == 10) {
            ++this.line;
            this.character = 0L;
        } else {
            ++this.character;
        }

        this.previous = (char)c;
        return this.previous;
    }

    public char next(char c) throws JSException {
        char n = this.next();
        if(n != c) {
            throw this.syntaxError("Expected \'" + c + "\' and instead saw \'" + n + "\'");
        } else {
            return n;
        }
    }

    public String next(int n) throws JSException {
        if(n == 0) {
            return "";
        } else {
            char[] chars = new char[n];

            for(int pos = 0; pos < n; ++pos) {
                chars[pos] = this.next();
                if(this.end()) {
                    throw this.syntaxError("Substring bounds error");
                }
            }

            return new String(chars);
        }
    }

    public char nextClean() throws JSException {
        char c;
        do {
            c = this.next();
        } while(c != 0 && c <= 32);

        return c;
    }

    public String nextString(char quote) throws JSException {
        StringBuffer sb = new StringBuffer();

        while(true) {
            char c = this.next();
            switch(c) {
                case '\u0000':
                case '\n':
                case '\r':
                    throw this.syntaxError("Unterminated string");
                case '\\':
                    c = this.next();
                    switch(c) {
                        case '\"':
                        case '\'':
                        case '/':
                        case '\\':
                            sb.append(c);
                            continue;
                        case 'b':
                            sb.append('\b');
                            continue;
                        case 'f':
                            sb.append('\f');
                            continue;
                        case 'n':
                            sb.append('\n');
                            continue;
                        case 'r':
                            sb.append('\r');
                            continue;
                        case 't':
                            sb.append('\t');
                            continue;
                        case 'u':
                            sb.append((char)Integer.parseInt(this.next((int)4), 16));
                            continue;
                        default:
                            throw this.syntaxError("Illegal escape.");
                    }
                default:
                    if(c == quote) {
                        return sb.toString();
                    }

                    sb.append(c);
            }
        }
    }

    public String nextTo(char delimiter) throws JSException {
        StringBuffer sb = new StringBuffer();

        while(true) {
            char c = this.next();
            if(c == delimiter || c == 0 || c == 10 || c == 13) {
                if(c != 0) {
                    this.back();
                }

                return sb.toString().trim();
            }

            sb.append(c);
        }
    }

    public String nextTo(String delimiters) throws JSException {
        StringBuffer sb = new StringBuffer();

        while(true) {
            char c = this.next();
            if(delimiters.indexOf(c) >= 0 || c == 0 || c == 10 || c == 13) {
                if(c != 0) {
                    this.back();
                }

                return sb.toString().trim();
            }

            sb.append(c);
        }
    }

    public List getList(JSTokener x) throws JSException{
        List list = new ArrayList();
        if(x.nextClean() != '[') {
            throw x.syntaxError("A JSONArray text must start with \'[\'");
        } else if(x.nextClean() != ']') {
            x.back();

            while(true) {
                if(x.nextClean() == ',') {
                    x.back();
                    list.add(JSONObject.NULL);
                } else {
                    x.back();
                    list.add(x.nextValue());
                }

                switch(x.nextClean()) {
                    case ',':
                    case ';':
                        if(x.nextClean() == ']') {
                            return list;
                        }

                        x.back();
                        break;
                    case ']':
                        return list;
                    default:
                        throw x.syntaxError("Expected a \',\' or \']\'");
                }
            }
        }
        return list;
    }

    public Object nextValue() throws JSException {
        char c = this.nextClean();
        switch(c) {
            case '\"':
            case '\'':
                return this.nextString(c);
            case '[':
                this.back();
                return getList(this);
            case '{':
                this.back();
                return new JSObject(this);
            default:
                StringBuffer sb;
                for(sb = new StringBuffer(); c >= 32 && ",:]}/\\\"[{;=#".indexOf(c) < 0; c = this.next()) {
                    sb.append(c);
                }

                this.back();
                String string = sb.toString().trim();
                if("".equals(string)) {
                    throw this.syntaxError("Missing value");
                } else {
                    return JSONObject.stringToValue(string);
                }
        }
    }

    public char skipTo(char to) throws JSException {
        char c;
        try {
            long exc = this.index;
            long startCharacter = this.character;
            long startLine = this.line;
            this.reader.mark(1000000);

            do {
                c = this.next();
                if(c == 0) {
                    this.reader.reset();
                    this.index = exc;
                    this.character = startCharacter;
                    this.line = startLine;
                    return c;
                }
            } while(c != to);
        } catch (IOException var9) {
            throw new JSException(var9);
        }

        this.back();
        return c;
    }

    public JSException syntaxError(String message) {
        return new JSException(message + this.toString());
    }

    public String toString() {
        return " at " + this.index + " [character " + this.character + " line " + this.line + "]";
    }
}

