package br.ufpe.cin.dass.soma.services;

import br.ufpe.cin.dass.soma.config.ApplicationConfig;
import br.ufpe.cin.dass.soma.data.OntologyPair;
import br.ufpe.cin.dass.soma.data.Requirement;
import br.ufpe.cin.dass.soma.data.SegmentPairs;
import br.ufpe.cin.dass.soma.model.OntologyClass;
import br.ufpe.cin.dass.soma.model.OntologyModel;
import br.ufpe.cin.dass.soma.model.PropertyModel;
import com.interdataworking.mm.alg.MapPair;
import com.wcohen.ss.JaroWinkler;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.ext.com.google.common.collect.Sets;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OntologySegmentationService {

    private final ApplicationConfig applicationConfig;

    private final RequirementsService requirementsService;

    private final OntologyService ontologyService;

    private Logger log = LoggerFactory.getLogger(OntologySegmentationService.class);

    public OntologySegmentationService(ApplicationConfig applicationConfig, RequirementsService requirementsService, OntologyService ontologyService) {
        this.applicationConfig = applicationConfig;
        this.requirementsService = requirementsService;
        this.ontologyService = ontologyService;
    }

    public SegmentPairs processOntologyPair(OntologyPair ontologyPair) throws IOException, OWLOntologyCreationException {

        log.info(String.format("processing ontology pair %s", ontologyPair.getId()));

        OntologyModel sourceOntology = ontologyService.loadOntologyModel(URI.create("file://"+ontologyPair.getSourceOntology()));
        OntologyModel targetOntology = ontologyService.loadOntologyModel(URI.create("file://"+ontologyPair.getTargetOntology()));
        Set<Requirement> requirements = requirementsService.getRequirements();

        int totalClassConstraints = 0;
        int totalPropertyConstraints = 0;
        for (Requirement requirement : requirements) {
            for (Map.Entry<String, String> entry : requirement.getConstraints().entrySet()) {
                log.info(String.format("validating constraint %s -> %s", entry.getKey(), entry.getValue()));
                String constraint = entry.getKey();
                String value = entry.getValue();
                if (constraint.startsWith("class-info")) {
                    totalClassConstraints++;
                    //Validate requirements for all classes in source ontology
                    sourceOntology.getClasses().entrySet().parallelStream().forEach((classEntry) -> {
                        double constraintValidation = requirementsService.constraintValidation(classEntry.getValue(), constraint, value);
                        if (constraintValidation >= Double.parseDouble(applicationConfig.getConstraintValidationThreshold()))
                            classEntry.getValue().getValidatedConstraints().put(constraint, constraintValidation);
                    });
                    //Validate requirements for all classes in target ontology
                    targetOntology.getClasses().entrySet().parallelStream().forEach((classEntry) -> {
                        double constraintValidation = requirementsService.constraintValidation(classEntry.getValue(), constraint, value);
                        if (constraintValidation >= Double.parseDouble(applicationConfig.getConstraintValidationThreshold()))
                            classEntry.getValue().getValidatedConstraints().put(constraint, constraintValidation);
                    });
                } else if (constraint.startsWith("property-info")) {
                    totalPropertyConstraints++;
                    //Validate requirements for all properties in source ontology
                    sourceOntology.getProperties().entrySet().parallelStream().forEach((propertyModelEntry) -> {
                        double constraintValidation = requirementsService.constraintValidation(propertyModelEntry.getValue(), constraint, value);
                        if (constraintValidation >= Double.parseDouble(applicationConfig.getConstraintValidationThreshold()))
                            propertyModelEntry.getValue().getValidatedConstraints().put(constraint, constraintValidation);
                    });
                    //Validate requirements for all properties in source ontology
                    targetOntology.getProperties().entrySet().parallelStream().forEach((propertyModelEntry) -> {
                        double constraintValidation = requirementsService.constraintValidation(propertyModelEntry.getValue(), constraint, value);
                        if (constraintValidation >= Double.parseDouble(applicationConfig.getConstraintValidationThreshold()))
                            propertyModelEntry.getValue().getValidatedConstraints().put(constraint, constraintValidation);
                    });
                }
            }
         }

        //Relevancia é usada para ordenar os segmentos - não é necessário
//         log.info("Calculating relevance");
//        //calculate relevance
//        LinkedHashMap<String, OntologyClass> classes = new LinkedHashMap<>();
//        classes.putAll(sourceOntology.getClasses());
//        classes.putAll(targetOntology.getClasses());
//        for(Map.Entry<String, OntologyClass> entry : classes.entrySet()) {
//            OntologyClass classEntry = entry.getValue();
//            if (classEntry.getValidatedConstraints() != null && classEntry.getValidatedConstraints().size() > 0) {
//                classEntry.setRelevance(
//                        classEntry.getValidatedConstraints().values().parallelStream().mapToDouble(Double::doubleValue).sum() / totalClassConstraints);
//            }
//        }
//        LinkedHashMap<String, PropertyModel> properties = new LinkedHashMap<>();
//        properties.putAll(sourceOntology.getProperties());
//        properties.putAll(targetOntology.getProperties());
//        for(Map.Entry<String, PropertyModel> entry : properties.entrySet()) {
//            PropertyModel propertyModel = entry.getValue();
//            if (propertyModel.getValidatedConstraints() != null && propertyModel.getValidatedConstraints().size() > 0) {
//                propertyModel.setRelevance(
//                        propertyModel.getValidatedConstraints().values().parallelStream().mapToDouble(Double::doubleValue).sum() / totalPropertyConstraints);
//            }
//        }

//        //Generate ontology segments files
//        log.info("Generating ontology segments files");
//        if (!Files.exists(Paths.get("segments/"+applicationConfig.getExpId()))) {
//            Files.createDirectory(Paths.get("segments/"+applicationConfig.getExpId()));
//        }
//        File file = new File("segments/"+applicationConfig.getExpId()+"/"+FilenameUtils.removeExtension(FilenameUtils.getName(sourceOntology.getUri().toString()))+".segment.owl");
//        FileOutputStream output = new FileOutputStream(file);
//        ontologyService.writeOntology(sourceOntology, output);
//        file = new File("segments/"+applicationConfig.getExpId()+"/"+FilenameUtils.removeExtension(FilenameUtils.getName(targetOntology.getUri().toString()))+".segment.owl");
//        output = new FileOutputStream(file);
//        ontologyService.writeOntology(targetOntology, output);



        log.info("Generating segment pairs");
        //Generate segment pairs
        Map<OntologyClass, OntologyClass> ontologyClassesSegmentPair = new HashMap<>();
        Map<PropertyModel, PropertyModel> propertiesSegmentPair = new HashMap<>();
        //generate segments

        sourceOntology.getClasses().entrySet().forEach(sourceOntologyEntry -> {
            targetOntology.getClasses().entrySet().forEach(targetOntologyEntry -> {

                Set<String> sourceSet = sourceOntologyEntry.getValue().getValidatedConstraints().keySet();
                Set<String> targetSet = targetOntologyEntry.getValue().getValidatedConstraints().keySet();

                //intersection
                Set<String> intersection = sourceSet.stream().filter(targetSet::contains).collect(Collectors.toSet());

                if (intersection.size() > 0) {
                    //elements have fulfilled same requirements
                    ontologyClassesSegmentPair.put(sourceOntologyEntry.getValue(), targetOntologyEntry.getValue());
                }
            });
        });


//        log.info("Sorting segment pairs by relevance");
//        Map<OntologyClass, OntologyClass> ontologySegmentPairsSortedByRelevance = new TreeMap((o1,o2) -> {
//            if (((OntologyClass)o1).getRelevance() >= ((OntologyClass)o2).getRelevance()) {
//                return -1;
//            } else {
//                return 1;
//            }
//        });
//
//        ontologySegmentPairsSortedByRelevance.putAll(ontologyClassesSegmentPair);

        Map<String, String> finalOntologySegmentPairs = new HashMap<>();
        finalOntologySegmentPairs.putAll(ontologyClassesSegmentPair.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getURI(), e -> e.getValue().getURI())));

        //Import ontologies to structural comparison
        IRI sourceIri = IRI.create(URI.create("file://"+ontologyPair.getSourceOntology()));
        OWLOntology sourceOwl = OWLManager.createOWLOntologyManager().loadOntology(sourceIri);

        IRI targetIri = IRI.create(URI.create("file://"+ontologyPair.getTargetOntology()));
        OWLOntology targetOwl = OWLManager.createOWLOntologyManager().loadOntology(targetIri);

        List<MapPair> initialMapping = createInitialMappingBasedOnLexicalMatching(sourceOwl, targetOwl);


        sourceOntology.getProperties().entrySet().forEach(sourceOntologyEntry -> {
            targetOntology.getProperties().entrySet().forEach(targetOntologyEntry -> {
                Set intersection = Sets.intersection(sourceOntologyEntry.getValue().getValidatedConstraints().keySet(),
                        targetOntologyEntry.getValue().getValidatedConstraints().keySet());
                if (intersection.size() > 0) {
                    //elements have fulfilled same requirements
                    //TODO check structural matching - for this, I have to import the ontology as graph.

                    propertiesSegmentPair.put(sourceOntologyEntry.getValue(), targetOntologyEntry.getValue());
                }
            });
        });

//        Map<PropertyModel, PropertyModel> propertySegmentPairsSortedByRelevance = new TreeMap((o1, o2) -> {
//            if (((PropertyModel)o1).getRelevance() >= ((PropertyModel)o2).getRelevance()) {
//                return -1;
//            } else {
//                return 1;
//            }
//        });

        Map<String, String> finalPropertySegmentPairs = new HashMap<>();
        finalPropertySegmentPairs.putAll(propertiesSegmentPair.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getURI(), e -> e.getValue().getURI())));

        log.info("Returning final segment pairs");
        SegmentPairs segmentPairs = new SegmentPairs(finalOntologySegmentPairs, finalPropertySegmentPairs);
        segmentPairs.setTotalOriginalClasses(sourceOntology.getClasses().size() + targetOntology.getClasses().size());
        segmentPairs.setTotalOriginalProperties(sourceOntology.getProperties().size() + targetOntology.getProperties().size());
        segmentPairs.setTotalSegmentClasses(finalOntologySegmentPairs.size() * 2);
        segmentPairs.setTotalSegmentProperties(finalPropertySegmentPairs.size() * 2);

        //Gerar arquivo descrevendo os segmentos
        try {
            if (!Files.exists(Paths.get("segments/"+applicationConfig.getExpId()))) {
                Files.createDirectory(Paths.get("segments/+applicationConfig.getExpId()"));
            }
            File file = new File("segments/"+applicationConfig.getExpId()+"/"+ontologyPair.getId()+".owl");
            FileOutputStream outputStream = new FileOutputStream(file);
            OntModelSpec s = new OntModelSpec( OntModelSpec.OWL_MEM_RULE_INF );
            OntModel model = ModelFactory.createOntologyModel();
            String ns = "http://cin.ufpe.br/dass/soma/segments/#";
            OntClass segment = model.createClass(ns + "segment");
            model.setNsPrefix("s", ns);
            int classSegmentPairId = 1;
            for (Map.Entry<String, String> map : segmentPairs.getOntologySegmentPairs().entrySet()) {
                OntClass segmentPair = model.createClass(ns + "segmentPair");
                segmentPair.addSuperClass(segment);
                Individual individual = segment.createIndividual(ns + "classSegmentPairSource");
                Property source = model.createProperty(ns + "source");
                Property target = model.createProperty(ns + "target");
                individual.addProperty(source, ns + map.getKey());
                individual.addProperty(target, ns + map.getValue());
            }
            int propertySegmentPairId = 1;

            //repertir para segmentos
            model.write(new PrintWriter(System.out));
            // Get model inside the transaction
            model.write(outputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return segmentPairs;
    }

    public List<MapPair> createInitialMappingBasedRequirementValidation(OWLOntology source, OWLOntology target) {

        List<MapPair> intialMapping = new ArrayList<MapPair>();

         source.getDatatypesInSignature().forEach(owlDatatype -> {
            target.getDatatypesInSignature().forEach(owlDatatype2 -> {
                if (owlDatatype.equals(owlDatatype2)) {
                    intialMapping.add(new MapPair(owlDatatype.getIRI().toString(), owlDatatype2.getIRI().toString(), 1.0));
                }
                ;
            });
        });



        source.getClassesInSignature().forEach(owlClass1 -> {
            IRI owl1_iri = owlClass1.getIRI();
            target.getClassesInSignature().forEach(owlClass2 -> {
                JaroWinkler jaro = new JaroWinkler();
                double score = jaro.score(owl1_iri.getShortForm(), owlClass2.getIRI().getShortForm());
                intialMapping.add(new MapPair(owl1_iri.toString(), owlClass2.getIRI(), 1 - score));
            });
        });


    }


    public static void main(String[] args) throws IOException {
        System.out.println( FilenameUtils.removeExtension(FilenameUtils.getName("file:///Users/diego/teste.owl")));

//        //Gerar arquivo descrevendo os segmentos
//        try {
//            if (!Files.exists(Paths.get("segments/exp1/"))) {
//                Files.createDirectory(Paths.get("segments/exp1/"));
//            }
//            File file = new File("segments/exp1/teste.rdf");
//            FileOutputStream outputStream = new FileOutputStream(file);
//            Map<String, String> segmentPairs = new HashMap<>();
//            segmentPairs.put("bla", "bli");
//            segmentPairs.put("cla", "cli");
//            segmentPairs.put("dla", "dli");
//            segmentPairs.put("fla", "fli");
//            segmentPairs.put("gla", "gli");
//
//            String ns = "http://segments/#";
//            Model model = ModelFactory.createOntologyModel();
//            model.setNsPrefix("s", ns);
//            segmentPairs.forEach((source, target) -> {
//                Resource pairSource = model.createResource(ns + source);
//                Property related= model.createProperty(ns + "related");
//                pairSource.addLiteral(model.createProperty(ns + "constraintValidationValue"), 0.5);
//                Resource pairTarget = model.createResource(ns + target);
//                pairSource.addProperty(related, pairTarget);
//            });
//            model.write(new PrintWriter(System.out));
//
//            RDFDataMgr.write(outputStream, model, RDFFormat.RDFXML) ;
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

    }

}
