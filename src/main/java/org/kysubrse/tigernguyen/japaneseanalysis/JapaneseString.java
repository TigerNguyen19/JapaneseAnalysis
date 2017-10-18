package org.kysubrse.tigernguyen.japaneseanalysis;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Utility functions for handling Japanese characters and strings.
 *
 * @author Michael Koch
 */
public class JapaneseString {
    /**
     * Returns the unicode block of a character. The test is optimized to work faster than
     * <CODE>Character.UnicodeBlock.of</CODE> for Japanese characters, but will work slower
     * for other scripts.
     */
    public static Character.UnicodeBlock unicodeBlockOf( char c) {
        if (c>=0x4e00 && c<0xa000) {
            return Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
        } else if (c>=0x30a0 && c<0x3100) {
            return Character.UnicodeBlock.KATAKANA;
        } else if (c>=0x3040) {
            return Character.UnicodeBlock.HIRAGANA;
        } else if (c>=0x3000) {
            return Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION;
        } else if (c < 0x80) {
            return Character.UnicodeBlock.BASIC_LATIN;
        } else {
            return Character.UnicodeBlock.of( c);
        }
    }

    public static boolean isKatakana( char c) {
        return (c>=0x30a0 && c<0x3100);
    }

    public static boolean isHiragana( char c) {
        return (c>=0x3040 && c<0x30a0);
    }

    public static boolean isKana( char c) {
        return (c>=0x3040 && c<0x3100);
    }

    public static boolean isCJKUnifiedIdeographs( char c) {
        return (c>=0x4e00 && c<0xa000);
    }

    public static boolean isCJKSymbolsAndPunctuation( char c) {
        return (c>=0x3000 && c<0x3040);
    }

    /**
     * Test if c is either in the character class of CJK unified ideographs or is the kanji repeat mark.
     */
    public static boolean isKanji( char c) {
        return (c>=0x4e00 && c<0xa000) || // CJK unified ideographs
            c == '\u3005'; // kanji repeat mark
    }

    public static String toHiragana( String s) {
        return toHiragana( s, true);
    }

    /**
     * Returns a new string with all katakana characters in the original string converted to
     * hiragana.
     */
    public static String toHiragana( String s, boolean ignoreSpecialChars) {
        StringBuilder out = null; // create only if needed
        for ( int i=0; i<s.length(); i++) {
            char c = s.charAt( i);
            if (isKatakana( c) && (!ignoreSpecialChars || !isKatakanaSpecialChar( c))) {
                if (out == null) {
                    out = new StringBuilder( s);
                }
                out.setCharAt( i, (char) (c-96));
            }
        }
        if (out == null) {
            return s;
        } else {
            return out.toString();
        }
    }
    
    /**
     * Determines if a char is a katakana special char, as used by <code>ignoreSpecialChars</code>
     * of {@link #toHiragana(String,boolean) toHiragana}.
     */
    private static boolean isKatakanaSpecialChar( char c) {
        return (c == 0x30fb /* centered dot */ ||
                c == 0x30fc /* dash */);
    }

    /**
     * Returns a new string with all hiragana characters in the original string converted to
     * katakana. Special characters will be ignored.
     */
    public static String toKatakana( String s) {
        return toKatakana( s, true);
    }
    
    /**
     * Returns a new string with all hiragana characters in the original string converted to
     * katakana.
     */
    public static String toKatakana( String s, boolean ignoreSpecialChars) {
        StringBuilder out = null; // create only if needed
        for ( int i=0; i<s.length(); i++) {
            char c = s.charAt( i);
            if (isHiragana( c) && (!ignoreSpecialChars || !isHiraganaSpecialChar( c))) {
                if (out == null) {
                    out = new StringBuilder( s);
                }
                out.setCharAt( i, (char) (c+96));
            }
        }
        if (out == null) {
            return s;
        } else {
            return out.toString();
        }
    }

    /**
     * Determines if a char is a hiragana special char, as used by <code>ignoreSpecialChars</code>
     * of {@link #toKatakana(String,boolean) toKatakana}.
     */
    private static boolean isHiraganaSpecialChar( char c) {
        return (c == 0x309b /* quotes */ ||
                c == 0x309c /* hollow dot */);
    }

    /**
     * Test if a string contains any kanji characters. The test is done using the 
     * {@link #isKanji(char) isKanji} method.
     */
    public static boolean containsKanji( String word) {
        for ( int i=0; i<word.length(); i++) {
            if (isKanji( word.charAt( i))) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Test if a string contains any katanaka characters. The test is done using the 
     * {@link #isKatanana(char) isKatakana} method.
     */
    public static boolean containsKatakana( String word) {
        for ( int i=0; i<word.length(); i++) {
            if (isKatakana(word.charAt( i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * Split a kanji/kana compound word in kanji and kana parts. Calls 
     * {@link #splitWordReading(String,String,String) splitWordReading( word, word, reading)}.
     */
    public static String[][] splitWordReading( String word, String reading) {
        return splitWordReading( word, word, reading);
    }

    /**
     * Split a kanji/kana compound word in kanji and kana parts. Readings are added to the kanji
     * substrings. To decide which reading each kanji substring has, hiragana substrings in the
     * kanji/kana words are searched in the reading string, and the remaining parts are interpreted
     * as reading of the kanji substrings.
     *
     * @param inflectedWord Inflected form of the kanji/kana word. Everything after the last kanji
     *        character is treated as inflected form and added to the output array as last element.
     * @param baseWord Dictionary form of the kanji/kana word.
     * @param baseReading Reading (in hiragana) of the word in base form.
     * @return Array with the word split in kanji/kana substrings. For every kanji substring, a
     *         kanji/reading string pair is contained in the array. For every kana substring,
     *         a single string is contained.
     * @exception StringIndexOutOfBoundsException if the word/base/reading tuple is not parseable.
     */
    public static String[][] splitWordReading( String inflectedWord, String baseWord, String baseReading) {
         // to treat katakana and hiragana equal, translate katakana to hiragana in base word
        String baseWordH = toHiragana( baseWord);
        baseReading = toHiragana( baseReading);
        List<String[]> result = new ArrayList<String[]>( baseWord.length()/2);
        int hStart = 0; // hiragana start
        int hEnd; // hiragana end
        int kStart = 0; // kanji start
        int kStartReading = 0; // kanji start in reading string
        int hStartReading = 0; // hiragana start in reading
        do {
            // search start of hiragana substring
            // use isKana instead of isHiragana to correctly handle katakana dash, which is not
            // converted by isHiragana
            while (hStart<baseWord.length() && needsReading( baseWordH, hStart)) {
                hStart++;
            }
            hEnd = hStart + 1;
            // search end of hiragana substring
            while (hEnd<baseWord.length() && !needsReading( baseWordH, hEnd)) {
                hEnd++;
            }

            String kanji = inflectedWord.substring( kStart, hStart);
            if (kanji.length() > 0) {
                if (hStart < baseWord.length()) {
                    // Structure of word is some kanji characters followed by some hiragana characters
                    // followed by some kanji characters. Find hiragana character substring in reading.
                    // Characters before the substring must be reading of first kanji part of word.

                    String hiragana = baseWordH.substring( hStart, hEnd);
                    // For every kanji character there must be at least one reading character, so
                    // start search at index kStartReading + kanji.length. The search can still
                    // lead to false results if the hiragana string also appears in the reading
                    // for the kanji.
                    hStartReading = baseReading.indexOf( hiragana, kStartReading + kanji.length());
                    // if this is -1, the string is malformed and an exception will be thrown
                    if (hEnd == baseWord.length()) {
                        // Remainder of word is possibly inflected hiragana.
                        // The inflected form might have a different length from the form
                        // in baseWord, so adjust the length here. This is the last iteration.
                        hEnd = inflectedWord.length();
                    }
                    String kanjiReading = baseReading.substring( kStartReading, hStartReading);
                    result.add( new String[] { kanji, kanjiReading });
                    result.add( new String[] { inflectedWord.substring( hStart, hEnd) });
                    kStartReading = hStartReading + hiragana.length();
                }
                else {
                    // remainder of word string is kanji, remainder of reading string must be
                    // reading for this kanji
                    result.add( new String[] { kanji, baseReading.substring( kStartReading) });
                }
            }
            else if (hStart < baseWord.length()) {
                // Structure of word is hiragana, possibly followed by kanji characters.
                // reading.substring( kStartReading) must begin with the same prefix. This
                // is not tested here.
                if (hEnd == baseWord.length()) {
                    // Remainder of word is (possibly inflected) hiragana.
                    // The inflected form might have a different length from the form
                    // in baseWord, so adjust the length here. This is the last iteration.
                    hEnd = inflectedWord.length();
                }
                String kana = inflectedWord.substring( hStart, hEnd);
                result.add( new String[] { kana });
                kStartReading += kana.length();
            }

            kStart = hEnd;
            hStart = hEnd+1;
        } while (kStart<baseWord.length() && 
                 kStart<inflectedWord.length()); // inflected word might be shorter than base word

        String[][] out = result.toArray( new String[result.size()][]);
        
        return out;
    }

    /**
     * Test if a character needs a reading annotation.
     */
    private static boolean needsReading( String text, int pos) {
        char c = text.charAt( pos);
        return !isKana( c) ||
            // handle special case with infix katakana 'ke', which is read as 'ka' or 'ga'
            // when used as counter or in place names, as in ikkagetsu or Sakuragaoka
            (c=='\u30f6' || c=='\u30b1') && 
            pos>0 && isKanji( text.charAt( pos-1)) && 
            pos+1<text.length() && isKanji( text.charAt( pos+1));
    }

    /**
     * Character array containing the characters representing the numbers 0-15 in hexadecimal notation.
     */
    private static final char[] HEX_CHARS = new char[] { 
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * Return the unicode escape string for a character. The unicode escape string is
     * Backslash-u, followed by a 4-digit hexadecimal string representing the unicode value 
     * of the character.
     *
     * @see #unicodeUnescape(String)
     */
    public static String unicodeEscape( char c) {
        int v = c;

        char[] hex = new char[] { '\\', 'u', '0', '0', '0', '0' };
        int i=5;
        // fill hex array with chars back to front
        while (v > 0) {
            hex[i--] = HEX_CHARS[v&0xf];
            v >>>= 4;
        }

        return new String( hex);
    }

    /**
     * Returns a new string with all unicode escape sequences replaced with the character
     * represented by the sequence.
     *
     * @see #unicodeEscape(char)
     */
    public static String unicodeUnescape( String str) {
        StringBuilder buf = null; // create only if needed
        for ( int i=str.length()-6; i>=0; i--) {
            if (str.charAt( i)=='\\' && str.charAt( i+1)=='u') {
                // Possible unicode escape sequence.
                // The escape sequence is only valid if the following 4 characters are
                // hexadecimal values.
                int replacement = 0;
                for ( int j=i+2; j<i+6; j++) {
                    char c = str.charAt( j);
                    int v = 0;
                    if (c>='0' && c<='9') {
                        v = c - '0';
                    } else if (c>='a' && c<='f') {
                        v = c - 'a' + 10;
                    } else if (c>='A' && c<='F') {
                        v = c - 'A' + 10;
                    } else { // no hexadecimal value
                        replacement = -1;
                        break;
                    }
                    replacement = (replacement<<4) | v;
                }
                if (replacement >= 0) {
                    // valid escape sequence
                    if (buf == null) {
                        buf = new StringBuilder( str);
                    }
                    buf.delete( i, i+5);
                    buf.setCharAt( i, (char) replacement);
                }
            }
        }

        if (buf == null) {
            return str;
        } else {
            return buf.toString();
        }
    }

    /**
     * Appends a character to a regular expression pattern, escaping any special characters.
     */
    public static StringBuilder addToRegex( char c, StringBuilder regex) {
        // A \ escape char may be prepended to every non-aphabetical character,
        // even if it does not need escaping.
        // Do this to err on the safe side.
        if (!(c>='a' && c<='z' || c>='A' && c<='Z')) {
            regex.append( '\\');
        }

        regex.append( c);
        return regex;
    }

    /**
     * Appends a character sequence to a regular expression pattern, escaping any
     * special characters.
     */
    public static StringBuilder addToRegex( CharSequence text, StringBuilder regex) {
        for ( int i=0; i<text.length(); i++) {
            addToRegex( text.charAt( i), regex);
        }
        return regex;
    }

    public static Iterable<String> tokenize(final String string, final String delimiter) {
        return new Iterable<String>() {

            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {

                    private final StringTokenizer tokenizer = new StringTokenizer(string, delimiter);
                    
                    @Override
                    public boolean hasNext() {
                        return tokenizer.hasMoreTokens();
                    }

                    @Override
                    public String next() {
                        return tokenizer.nextToken();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("remote is not supported");
                    }
                    
                };
            }
            
        };
    }
    
    private JapaneseString() {
    }
}