package br.ufpe.cin.dass.soma.config;

import br.ufpe.cin.dass.soma.data.OntologyPair;
import br.ufpe.cin.dass.soma.data.Output;
import br.ufpe.cin.dass.soma.services.ExecutionService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.io.Writer;

@Configuration
public class BatchConfiguration {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<OntologyPair> reader() {
        FlatFileItemReader<OntologyPair> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(applicationConfig.getOntologyPairsFile()));
        reader.setLineMapper(new DefaultLineMapper<OntologyPair>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "id", "sourceOntology", "targetOntology" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<OntologyPair>() {{
                setTargetType(OntologyPair.class);
            }});
        }});
        return reader;
    }

    @Bean
    public ExecutionService processor() {
        return new ExecutionService();
    }

    @Bean
    public FlatFileItemWriter<Output> writer() {
        FlatFileItemWriter<Output> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("output.csv"));
        writer.setHeaderCallback(w -> {
            w.write("experimentId,pairId,totalClasses,totalProperties,classSegmentPairs,propertySegmentPairs,precision,recall,fMeasure,executionTime");
        });
        writer.setLineAggregator(new DelimitedLineAggregator<Output>(){{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<Output>() {{
                setNames(new String[] {"expId", "pairId", "totalClasses", "totalProperties",  "classSegmentPairs", "propertySegmentPairs", "precision", "recall", "fMeasure", "executionTime"});
            }});
        }});
        return writer;
    }

    @Bean
    public Step processingOntologies() {
        return stepBuilderFactory.get("processingOntologies")
                .<OntologyPair,Output> chunk(10)
                .reader(reader())
                .writer(writer())
                .processor(processor())
                .build();
    }

    @Bean
    public Job job(Step processingOntologies) throws Exception {
        return jobBuilderFactory.get("job1")
                .incrementer(new RunIdIncrementer())
                .start(processingOntologies)
                .build();
    }

}
