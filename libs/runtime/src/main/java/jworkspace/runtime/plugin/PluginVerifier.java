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
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class PluginVerifier {

    public static final String ED_25519 = "Ed25519";

    private PluginVerifier() {}
    /**
     * Verifies if a downloaded JAR matches the pinned public key of the author.
     */
    public static boolean verifyPlugin(String jarPath, String signaturePath, byte[] pinnedPublicKeyBytes) {
        try {
            byte[] jarBytes = Files.readAllBytes(Paths.get(jarPath));
            String sigString = Files.readString(Paths.get(signaturePath)).trim();
            byte[] signatureBytes = Base64.getDecoder().decode(sigString);

            // Load the pinned Ed25519 Public Key
            X509EncodedKeySpec spec = new X509EncodedKeySpec(pinnedPublicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance(ED_25519);
            PublicKey publicKey = kf.generatePublic(spec);

            // Verify the signature
            Signature sig = Signature.getInstance(ED_25519);
            sig.initVerify(publicKey);
            sig.update(jarBytes);

            return sig.verify(signatureBytes);
        } catch (Exception e) {
            // Log security exception, do not load the JAR
            return false;
        }
    }
}