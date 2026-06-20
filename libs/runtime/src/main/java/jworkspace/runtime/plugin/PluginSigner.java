package jworkspace.runtime.plugin;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2026 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class PluginSigner {

    public static final String ED_25519 = "Ed25519";

    private PluginSigner() {}

    public static void signJar(String jarPath, byte[] privateKeyBytes, String outputPath) throws Exception {
        byte[] jarBytes = Files.readAllBytes(Paths.get(jarPath));

        // Load the Ed25519 Private Key
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance(ED_25519);
        PrivateKey privateKey = kf.generatePrivate(spec);

        // Sign the JAR
        Signature sig = Signature.getInstance(ED_25519);
        sig.initSign(privateKey);
        sig.update(jarBytes);
        byte[] signatureBytes = sig.sign();

        // Save signature as a Base64 string
        Files.writeString(Paths.get(outputPath), Base64.getEncoder().encodeToString(signatureBytes));
    }
}
