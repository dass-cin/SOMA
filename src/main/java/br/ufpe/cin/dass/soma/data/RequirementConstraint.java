package br.ufpe.cin.dass.soma.data;

import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.Levenshtein;
import org.springframework.cglib.core.ClassInfo;

public interface RequirementConstraint {

    public String getUri();

    public enum ClassInformation implements RequirementConstraint {

        NAME("class-info/name"),
        SUB_CLASS_OF("class-info/axioms/sub-class-of"),
        EQUIVALENT_CLASS("class-info/axioms/equivalent-class"),
        DISJOINT_WITH("class-info/axioms/disjoint-with"),
        LABEL("class-info/annotation/label"),
        COMMENT("class-info/annotation/comment"),
        SEE_ALSO("class-info/annotation/see-also"),
        IS_DEFINED_BY("class-info/annotation/is-defined-by");

        private String uri;
        ClassInformation(String uri) {
            this.uri = uri;
        }
        public String getUri() {
            return uri;
        }

        public static ClassInformation valueByURI(String uri) {
            if (NAME.uri.equals(uri)) {
                return NAME;
            } else if (SUB_CLASS_OF.uri.equals(uri)){
                return SUB_CLASS_OF;
            } else if (EQUIVALENT_CLASS.uri.equals(uri)) {
                return EQUIVALENT_CLASS;
            } else if (DISJOINT_WITH.uri.equals(uri)) {
                return DISJOINT_WITH;
            } else if (LABEL.uri.equals(uri)) {
                return LABEL;
            } else if (COMMENT.uri.equals(uri)) {
                return COMMENT;
            } else if (SEE_ALSO.uri.equals(uri)) {
                return SEE_ALSO;
            } else if (IS_DEFINED_BY.uri.equals(uri)) {
                return IS_DEFINED_BY;
            }
            return null;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

    }

    public enum PropertyInformation implements  RequirementConstraint {

        NAME("property-info/name"),
        SUB_PROPERTY_OF("property-info/sub-property-of"),
        DOMAIN("property-info/domain"),
        RANGE("property-info/range"),
        EQUIVALENT_PROPERTY("property-info/equivalent-property"),
        INVERSE_OF_PROPERTY("property-info/inverse-of-property"),
        IS_FUNCTIONAL("property-info/is-functional"),
        IS_INVERSE_FUNCTIONAL("property-info/is-inverse-functional"),
        IS_TRANSITIVE("property-info/is-transitive"),
        IS_SIMMETRIC("property-info/is-symmetric"),
        IS_DATATYPE("property-info/is-datatype"),
        IS_OBJECT("property-info/is-object"),
        ALL_VALUES_FROM("property-info/all-values-from"),
        SOME_VALUES_FROM("property-info/some-values-from");

        public static PropertyInformation valueByURI(String uri) {
            if (NAME.uri.equals(uri)) {
                return NAME;
            } else if (SUB_PROPERTY_OF.uri.equals(uri)){
                return SUB_PROPERTY_OF;
            } else if (DOMAIN.uri.equals(uri)) {
                return DOMAIN;
            } else if (RANGE.uri.equals(uri)) {
                return RANGE;
            } else if (EQUIVALENT_PROPERTY.uri.equals(uri)) {
                return EQUIVALENT_PROPERTY;
            } else if (INVERSE_OF_PROPERTY.uri.equals(uri)) {
                return INVERSE_OF_PROPERTY;
            } else if (IS_FUNCTIONAL.uri.equals(uri)) {
                return IS_FUNCTIONAL;
            } else if (IS_INVERSE_FUNCTIONAL.uri.equals(uri)) {
                return IS_INVERSE_FUNCTIONAL;
            } else if (IS_TRANSITIVE.uri.equals(uri)) {
                return IS_TRANSITIVE;
            } else if (IS_SIMMETRIC.uri.equals(uri)) {
                return IS_SIMMETRIC;
            } else if (IS_DATATYPE.uri.equals(uri)) {
                return IS_DATATYPE;
            } else if (IS_OBJECT.uri.equals(uri)) {
                return IS_OBJECT;
            } else if (ALL_VALUES_FROM.uri.equals(uri)) {
                return ALL_VALUES_FROM;
            } else if (SOME_VALUES_FROM.uri.equals(uri)) {
                return SOME_VALUES_FROM;
            }
            return null;
        }

        private String uri;
        PropertyInformation(String uri) {
            this.uri = uri;
        }
        public String getUri() {
            return uri;
        }
        public void setUri(String uri) {
            this.uri = uri;
        }
    }

}
