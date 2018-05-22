package br.ufpe.cin.dass.soma.util;

import org.apache.commons.io.FilenameUtils;

import java.net.URI;

public class Utils {

    public static String getOntologyNameFromURI(URI uri) {
        String ontologyName = FilenameUtils.getName(uri.toString()).replace("."+FilenameUtils.getExtension(uri.toString()), "");
        return ontologyName;
    }

    public static String extractElementName(String fullname) {
        if (fullname.contains("#")) {
            fullname = fullname.substring(
                    fullname.indexOf('#')+1,
                    fullname.lastIndexOf('>'));
        }
        return fullname;
    }

}
