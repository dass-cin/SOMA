package br.ufpe.cin.dass.soma.services;

import br.ufpe.cin.dass.soma.config.ApplicationConfig;
import br.ufpe.cin.dass.soma.data.Requirement;
import br.ufpe.cin.dass.soma.data.RequirementConstraint.ClassInformation;
import br.ufpe.cin.dass.soma.data.RequirementConstraint.PropertyInformation;
import br.ufpe.cin.dass.soma.model.OntologyClass;
import br.ufpe.cin.dass.soma.model.OntologyElement;
import br.ufpe.cin.dass.soma.model.PropertyModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.debatty.java.stringsimilarity.JaroWinkler;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service()
public class RequirementsService {

    private Logger log = LoggerFactory.getLogger(RequirementsService.class);

    @Autowired
    private ApplicationConfig config;

    private final TextNormalizationService normalizationService;

    private Set<Requirement> requirements = new HashSet<>();

    private JaroWinkler jaroWinkler = new JaroWinkler();

    public RequirementsService(TextNormalizationService normalizationService) {
        this.normalizationService = normalizationService;
    }

    @PostConstruct
    public void loadRequirements() throws IOException {
        log.info("Loading requirements");
        ObjectMapper jsonMapper = new ObjectMapper();
        Path json = Paths.get(config.getRequirementsFile());
        FileInputStream inputStream = new FileInputStream(json.toFile());
        List<Requirement> requirements = jsonMapper.readValue(inputStream, new TypeReference<List<Requirement>>() {});
        this.requirements.addAll(requirements);
    }

    public Double constraintValidation(OntologyElement element, String constraintUri, String expectedValue) {
        double constraintValidation = 0.0;
        if (constraintUri.startsWith("class-info")) {
            OntologyClass ontologyClass = (OntologyClass) element;
            ClassInformation classInformation = ClassInformation.valueByURI(constraintUri);
            if (classInformation != null) {
                log.info(String.format("Validating class constraint %s", constraintUri));
                switch(classInformation) {
                    case NAME:
                        if (element.getName() != null) {
                            if (normalizationService.checkSynonyms(ontologyClass.getName(), expectedValue)) {
                                constraintValidation = 1;
                            } else {
                                constraintValidation = jaroWinkler.similarity(ontologyClass.getName(), expectedValue);
                            }
                        }
                        break;
                    case LABEL:
                        if (element.getLabel() != null) {
                            if (normalizationService.checkSynonyms(ontologyClass.getLabel(), expectedValue)) {
                                constraintValidation = 1;
                            } else {
                                constraintValidation = jaroWinkler.similarity(ontologyClass.getLabel(), expectedValue);
                            }
                        }
                        break;
                    case SUB_CLASS_OF:
                        for (Object o : ontologyClass.getSubs()) {
                            OntologyClass sub = (OntologyClass) o;
                            double subSimValue = 0;
                            if (normalizationService.checkSynonyms(sub.getName(), expectedValue)) {
                                subSimValue = 1;
                            } else {
                                subSimValue = jaroWinkler.similarity(sub.getName(), expectedValue);
                            }
                            if (subSimValue > constraintValidation) {
                                constraintValidation = subSimValue;
                            }
                        }
                        break;
                    case EQUIVALENT_CLASS:
                        for (Object o : ontologyClass.getEquiv()) {
                            OntologyClass eqv = (OntologyClass) o;
                            double eqvSimValue = 0;
                            if (normalizationService.checkSynonyms(eqv.getName(), expectedValue)) {
                                eqvSimValue = 1;
                            } else {
                                eqvSimValue = jaroWinkler.similarity(eqv.getName(), expectedValue);
                            }
                            if (eqvSimValue > constraintValidation) {
                                constraintValidation = eqvSimValue;
                            }
                        }
                        break;
                    case DISJOINT_WITH:
                        for (Object o : ontologyClass.getDisjointWith()) {
                            OntologyClass dis = (OntologyClass) o;
                            double disSimValue = 0;
                            if (normalizationService.checkSynonyms(dis.getName(), expectedValue)) {
                                disSimValue = 1;
                            } else {
                                disSimValue = jaroWinkler.similarity(dis.getName(), expectedValue);
                            }
                            if (disSimValue > constraintValidation) {
                                constraintValidation = disSimValue;
                            }
                        }
                        break;
                    case SEE_ALSO:
                        for (Object o : ontologyClass.getSeeAlso()) {
                            OntologyClass sa = (OntologyClass) o;
                            double saSimValue = 0;
                            if (normalizationService.checkSynonyms(sa.getName(), expectedValue)) {
                                saSimValue  = 1;
                            } else {
                                saSimValue = jaroWinkler.similarity(sa.getName(), expectedValue);
                            }
                            if (saSimValue > constraintValidation) {
                                constraintValidation = saSimValue;
                            }
                        }
                        break;
                    case IS_DEFINED_BY:
                        for (Object o : ontologyClass.getIsDefinedBy()) {
                            OntologyClass db = (OntologyClass) o;
                            double dbSimValue = 0;
                            if (normalizationService.checkSynonyms(db.getName(), expectedValue)) {
                                dbSimValue  = 1;
                            } else {
                                dbSimValue = jaroWinkler.similarity(db.getName(), expectedValue);
                            }
                            if (dbSimValue > constraintValidation) {
                                constraintValidation = dbSimValue;
                            }
                        }
                        break;
                    case COMMENT:
                        if (ontologyClass.getCommment() != null) {
                            if (normalizationService.checkSynonyms(ontologyClass.getCommment(), expectedValue)) {
                                constraintValidation = 1;
                            } else {
                                constraintValidation = jaroWinkler.similarity(ontologyClass.getCommment(), expectedValue);
                            }
                        }
                        break;
                }
                log.info(String.format("Obtained value %s for constraint %s", constraintValidation, constraintUri));

            } else {
                log.error(String.format("unknown URI for constraint %s", constraintUri));
            }

        } else if (constraintUri.startsWith("property-info")) {
            PropertyInformation propertyInformation = PropertyInformation.valueByURI(constraintUri);
            if (propertyInformation != null) {
                log.info("Validating property constraint %s", constraintUri);
                PropertyModel property = (PropertyModel) element;
                switch(propertyInformation) {
                    case NAME:
                        if (element.getName() != null) {
                            if (normalizationService.checkSynonyms(property.getName(), expectedValue)) {
                                constraintValidation = 1;
                            } else {
                                constraintValidation = jaroWinkler.similarity(property.getName(), expectedValue);
                            }
                        }
                        break;
                    case IS_TRANSITIVE:
                        if (property.isTransitive() && !expectedValue.equals("false")) {
                            constraintValidation = 1;
                        }
                        break;
                    case IS_FUNCTIONAL:
                        if (property.isFunctional() && !expectedValue.equals("false")) {
                            constraintValidation = 1;
                        }
                        break;
                    case IS_DATATYPE:
                        if (property.isDataTypeProperty() && !expectedValue.equals("false")) {
                            constraintValidation = 1;
                        }
                        break;
                    case IS_OBJECT:
                        if (property.isObjectProperty() && !expectedValue.equals("false")) {
                            constraintValidation = 1;
                        }
                        break;
                    case IS_SIMMETRIC:
                        if (property.isSymmetricProperty() && !expectedValue.equals("false")) {
                            constraintValidation = 1;
                        }
                        break;
                    case IS_INVERSE_FUNCTIONAL:
                        if (property.isInverseFunctionalProperty() && !expectedValue.equals("false")) {
                            constraintValidation = 1;
                        }
                        break;
                    case ALL_VALUES_FROM:
                        for(Object v : property.getValues()) {
                            RDFNode value = (RDFNode)v;
                            if (value.isLiteral()) {
                                if (normalizationService.checkSynonyms(value.asLiteral().toString(), expectedValue)) {
                                    constraintValidation += 1;
                                } else {
                                    constraintValidation += jaroWinkler.similarity(value.asLiteral().getString(), expectedValue);
                                }
                            }
                        }
                        //Avarage of all values
                        if (property.getValues() != null && property.getValues().size() > 0) {
                            constraintValidation = constraintValidation / property.getValues().size();
                        }
                    case SOME_VALUES_FROM:
                        for(Object v : property.getValues()) {
                            RDFNode value = (RDFNode)v;
                            if (value.isLiteral()) {
                                double someValueValidation = 0;
                                if (normalizationService.checkSynonyms(value.asLiteral().getString(), expectedValue)) {
                                    someValueValidation = 1;
                                } else {
                                    someValueValidation = jaroWinkler.similarity(value.asLiteral().getString(), expectedValue);
                                }
                                if (someValueValidation > constraintValidation)
                                    constraintValidation = someValueValidation;
                            }
                        }
                    case SUB_PROPERTY_OF:
                        for (Object p : property.getSubProperties()) {
                            PropertyModel sub = (PropertyModel) p;
                            double simValue = 0;
                            if (normalizationService.checkSynonyms(sub.getName(), expectedValue)) {
                                simValue = 1;
                            } else {
                                simValue = jaroWinkler.similarity(sub.getName(), expectedValue);
                            }
                            if (simValue > constraintValidation) {
                                constraintValidation = simValue;
                            }
                        }
                        break;
                    case EQUIVALENT_PROPERTY:
                        for (Object p : property.getEquivalentProperties()) {
                            PropertyModel eqv = (PropertyModel) p;
                            double simValue = 0;
                            if (normalizationService.checkSynonyms(eqv.getName(), expectedValue)) {
                                simValue = 1;
                            } else {
                                simValue = jaroWinkler.similarity(eqv.getName(), expectedValue);
                            }
                            if (simValue > constraintValidation) {
                                constraintValidation = simValue;
                            }
                        }
                        break;
                    case DOMAIN:
                        for (Object p : property.getDomains()) {
                            OntResource domain = (OntResource) p;
                            double simValue = 0.0;
                            if (domain.isOntology() && domain.asOntology().getLocalName() != null) {
                                if (normalizationService.checkSynonyms(domain.asOntology().getLocalName(), expectedValue)) {
                                    simValue = 1;
                                } else {
                                    simValue = jaroWinkler.similarity(domain.asOntology().getLocalName(), expectedValue);
                                }
                            } else if (domain.isClass() && domain.asClass().getLocalName() != null ) {
                                if (normalizationService.checkSynonyms(domain.asClass().getLocalName(), expectedValue)) {
                                    simValue = 1;
                                } else {
                                    simValue = jaroWinkler.similarity(domain.asClass().getLocalName(), expectedValue);
                                }
                            } else if (domain.isProperty() && domain.asProperty().getLocalName() != null) {
                                if (normalizationService.checkSynonyms(domain.asProperty().getLocalName(), expectedValue)) {
                                    simValue = 1;
                                } else {
                                    simValue = jaroWinkler.similarity(domain.asProperty().getLocalName(), expectedValue);
                                }
                            } else if (domain.isDatatypeProperty() && domain.asDatatypeProperty().getLocalName() != null) {
                                if (normalizationService.checkSynonyms(domain.asDatatypeProperty().getLocalName(), expectedValue)) {
                                    simValue = 1;
                                } else {
                                    simValue = jaroWinkler.similarity(domain.asDatatypeProperty().getLocalName(), expectedValue);
                                }
                            } else if (domain.isObjectProperty()  && domain.asObjectProperty().getLocalName() != null) {
                                if (normalizationService.checkSynonyms(domain.asObjectProperty().getLocalName(), expectedValue)) {
                                    simValue = 1;
                                } else {
                                    simValue = jaroWinkler.similarity(domain.asObjectProperty().getLocalName(), expectedValue);
                                }
                            } else if (domain.isAnnotationProperty() && domain.asAnnotationProperty().getLocalName() != null ) {
                                if (normalizationService.checkSynonyms(domain.asAnnotationProperty().getLocalName(), expectedValue)) {
                                    simValue = 1;
                                } else {
                                    simValue = jaroWinkler.similarity(domain.asAnnotationProperty().getLocalName(), expectedValue);
                                }
                            }
                            if (simValue > constraintValidation) {
                                constraintValidation = simValue;
                            }
                        }
                        break;
                    case RANGE:
                        for (Object p : property.getRanges()) {
                            OntResource range = (OntResource) p;
                            double simValue = 0;
                            if (range.isDataRange() && range.asDataRange().getLocalName() != null) {
                                if (normalizationService.checkSynonyms(range.asDataRange().getLocalName(), expectedValue)) {
                                    simValue = 1;
                                } else {
                                    simValue = jaroWinkler.similarity(range.asDataRange().getLocalName(), expectedValue);
                                }
                            } else if (range.isOntology() && range.asOntology().getLocalName() != null) {
                                if (normalizationService.checkSynonyms(range.asOntology().getLocalName(), expectedValue)) {
                                    simValue = 1;
                                } else {
                                    simValue = jaroWinkler.similarity(range.asOntology().getLocalName(), expectedValue);
                                }
                            } else if (range.isClass() && range.asClass().getLocalName() != null) {
                                if (normalizationService.checkSynonyms(range.asClass().getLocalName(), expectedValue)) {
                                    simValue = 1;
                                } else {
                                    simValue = jaroWinkler.similarity(range.asClass().getLocalName(), expectedValue);
                                }
                            } else if (range.isProperty() && range.asProperty().getLocalName() != null) {
                                if (normalizationService.checkSynonyms(range.asProperty().getLocalName(), expectedValue)) {
                                    simValue = 1;
                                } else {
                                    simValue = jaroWinkler.similarity(range.asProperty().getLocalName(), expectedValue);
                                }
                            } else if (range.isDatatypeProperty() && range.asDatatypeProperty().getLocalName() != null) {
                                if (normalizationService.checkSynonyms(range.asDatatypeProperty().getLocalName(), expectedValue)) {
                                    simValue = 1;
                                } else {
                                    simValue = jaroWinkler.similarity(range.asDatatypeProperty().getLocalName(), expectedValue);
                                }
                            } else if (range.isObjectProperty() && range.asObjectProperty().getLocalName() != null) {
                                if (normalizationService.checkSynonyms(range.asObjectProperty().getLocalName(), expectedValue)) {
                                    simValue = 1;
                                } else {
                                    simValue = jaroWinkler.similarity(range.asObjectProperty().getLocalName(), expectedValue);
                                }
                            } else if (range.isAnnotationProperty() && range.asAnnotationProperty().getLocalName() != null) {
                                if (normalizationService.checkSynonyms(range.asAnnotationProperty().getLocalName(), expectedValue)) {
                                    simValue = 1;
                                } else {
                                    simValue = jaroWinkler.similarity(range.asAnnotationProperty().getLocalName(), expectedValue);
                                }
                            }
                            if (simValue > constraintValidation) {
                                constraintValidation = simValue;
                            }
                        }
                        break;
                    case INVERSE_OF_PROPERTY:
                        for (Object p : property.getInverseOf()) {
                            PropertyModel inverseOf = (PropertyModel) p;
                            double simValue = jaroWinkler.similarity(inverseOf.getName(), expectedValue);
                            if (simValue > constraintValidation) {
                                constraintValidation = simValue;
                            }
                        }
                        break;
                }
                log.info(String.format("Obtained value %s for constraint %s", constraintValidation, constraintUri));
            } else {
                log.error("unknown URI for constraint %s", constraintUri);
            }
        }
        return constraintValidation;
    }

    public Set<Requirement> getRequirements() {
        return requirements;
    }

}
