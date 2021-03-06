/*
 * This file is part of the Trickl Open Source Libraries.
 *
 * Trickl Open Source Libraries - http://open.trickl.com/
 *
 * Copyright (C) 2011 Tim Gee.
 *
 * Trickl Open Source Libraries are free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Trickl Open Source Libraries are distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.trickl.graph.xml.bind;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import java.util.Arrays;

public class DefaultNamespace extends NamespacePrefixMapper {

   public final static String PROPERTY_NAME = "com.sun.xml.bind.namespacePrefixMapper";
   private String defaultNamespaceUri = "";
   private String[] predeclaredNamespaceUris = new String[0];

   public DefaultNamespace() {
   }

   public DefaultNamespace(String defaultNamespaceUri) {
      this.defaultNamespaceUri = defaultNamespaceUri;
   }

   public DefaultNamespace(String... uris) {
      if (uris.length > 0) {
         this.defaultNamespaceUri = uris[0];
         if (uris.length > 1) {
            this.predeclaredNamespaceUris = Arrays.copyOfRange(uris, 1, uris.length);
         }
      }
   }

   public void setDefaultNamespaceUri(String defaultNamespaceUri) {
      this.defaultNamespaceUri = defaultNamespaceUri;
   }

   public void setPreDeclaredNamespaceUris(String[] predeclaredNamespaceUris) {
      this.predeclaredNamespaceUris = predeclaredNamespaceUris;
   }

   @Override
   public String[] getPreDeclaredNamespaceUris() {
      return predeclaredNamespaceUris;
   }

   @Override
   public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
      if (namespaceUri.equals(defaultNamespaceUri)) {
         return "";
      }

      for (WellKnownNamespace namespace : WellKnownNamespace.values()) {
         if (namespaceUri.equals(namespace.getURI())) {
            return namespace.getPrefix();
         }
      }

      return suggestion;
   }
}