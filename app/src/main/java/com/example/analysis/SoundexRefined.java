package com.example.analysis;

public class SoundexRefined {

    public static String getCode(final String input) {
        if ((input == null) || input.trim().isEmpty()) {
            return "Z000";
        }

        int[] code =
            {
                0, 1, 3, 6, 0, 2, 4, 0, 0, 4, 3, 7, 8, 8, 0, 1, 5, 9, 3, 6, 0, 2,
                0, 5, 0, 5
            };

        /* a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z */
        char[] key = { 'Z', '0', '0', '0', '0' };
        char ch;
        int last;
        int count;
        int scount;

        String in =
            input.toUpperCase().replace('Ä', 'A').replace('Ö', 'O')
                 .replace('Ü', 'U').replace('ß', 's');

        try {
            key[0] = in.charAt(0);
            last = code[key[0] - 'A'];
            scount = 1;

            for (count = 1; (count < 5) && (scount < in.length()); ++scount) {
                ch = in.charAt(scount);

                if (last != code[ch - 'A']) {
                    last = code[ch - 'A'];

                    if (last != 0) {
                        key[count++] = (char) ('0' + last);
                    }
                }
            }
        } catch (final ArrayIndexOutOfBoundsException e) {
            // If we hit an unknown character return Z000.
            return "Z000";
        }

        return new String(key);
    }
}