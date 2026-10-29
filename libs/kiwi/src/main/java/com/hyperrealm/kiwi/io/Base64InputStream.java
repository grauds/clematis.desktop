package com.hyperrealm.kiwi.io;
/* ----------------------------------------------------------------------------
   The Kiwi Toolkit - A Java Class Library
   Copyright (C) 1998-2008 Mark A. Lindner

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public License as
   published by the Free Software Foundation; either version 2 of the
   License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this library; if not, see <http://www.gnu.org/licenses/>.
   ----------------------------------------------------------------------------
*/
import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import edu.umd.cs.findbugs.annotations.NonNull;
/**
 * This class implements a Base-64 decoding filter. It reads Base-64 encoded
 * data from its input stream and outputs the original (decoded) form of the
 * data, strictly ignoring any non-Base-64 characters.
 *
 * @author Anton Troshin
 * @since Kiwi 2.5.0
 */
@SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MagicNumber"})
public class Base64InputStream extends FilterInputStream {

    private final InputStream originalIn;
    private boolean initialized = false;

    public Base64InputStream(InputStream in) {
        super(in);
        this.originalIn = in;
    }

    private void initialize() throws IOException {
        if (initialized) {
            return;
        }

        // Read all raw bytes from the underlying stream
        byte[] allRawBytes = originalIn.readAllBytes();

        // Filter out non-Base64 characters manually to build a clean Base64 string
        String cleaned = clean(allRawBytes);

        // Pass the safely structured data to the standard JDK MIME decoder
        byte[] decodedBytes;
        try {
            decodedBytes = Base64.getMimeDecoder().decode(cleaned.getBytes());
        } catch (IllegalArgumentException e) {
            // Ultimate fallback for extreme malformed cases
            decodedBytes = new byte[0];
        }

        this.in = new ByteArrayInputStream(decodedBytes);
        this.initialized = true;
    }

    @NonNull
    private String clean(byte[] allRawBytes) {
        StringBuilder sb = new StringBuilder(allRawBytes.length);
        for (byte b : allRawBytes) {
            char c = (char) (b & 0xFF);
            if (isBase64Character(c)) {
                sb.append(c);
            }
        }

        String cleaned = sb.toString();

        // Fix structural padding errors caused by random noise stripping
        // Standard Base64 strings must be a multiple of 4 in length
        int mod = cleaned.length() % 4;
        if (mod > 0) {
            // Strip any misaligned trailing pads first to prevent padding exceptions
            while (cleaned.endsWith("=")) {
                cleaned = cleaned.substring(0, cleaned.length() - 1);
            }
            // Recalculate and safely pad out to a multiple of 4
            mod = cleaned.length() % 4;

            if (mod == 1) {
                cleaned = cleaned.substring(0, cleaned.length() - 1);
                mod = cleaned.length() % 4;
            }

            if (mod == 2) {
                cleaned += "==";
            } else if (mod == 3) {
                cleaned += "=";
            }
        }
        return cleaned;
    }

    @Override
    public int read() throws IOException {
        initialize();
        return in.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        initialize();
        return in.read(b, off, len);
    }

    private boolean isBase64Character(char c) {
        return (c >= 'A' && c <= 'Z')
            || (c >= 'a' && c <= 'z')
            || (c >= '0' && c <= '9')
            || c == '+' || c == '/';
    }
}

