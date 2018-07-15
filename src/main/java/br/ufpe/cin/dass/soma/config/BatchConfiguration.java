package br.ufpe.cin.dass.soma.config;

import br.ufpe.cin.dass.soma.data.Input;
import br.ufpe.cin.dass.soma.data.Output;
import br.ufpe.cin.dass.soma.services.ExecutionService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
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

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Input> reader() {
        FlatFileItemReader<Input> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(applicationConfig.getOntologyPairsFile()));
        reader.setLineMapper(new DefaultLineMapper<Input>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setDelimiter(";");
                setNames(new String[] { "id", "sourceOntology", "targetOntology", "keywords", "matcher" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Input>() {{
                setTargetType(Input.class);
            }});
        }});
        return reader;
    }

    @Bean
    public ExecutionService processor() {
        return new ExecutionService();
    }

    @Bean
    public FlatFileItemWriter<Output> writerCSV() {
        FlatFileItemWriter<Output> writer = new FlatFileItemWriter<>();
        String outputFile = "output_"+applicationConfig.getExpId()+".csv";
        writer.setResource(new FileSystemResource(outputFile));
        writer.setHeaderCallback(w -> {
            w.write("experimentId,pairId,precision,recall,fMeasure,executionTime,matcher,truePositives,falsePositives,falseNegatives");
        });
        writer.setLineAggregator(new DelimitedLineAggregator<Output>(){{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<Output>() {{
                setNames(new String[] {"expId", "pairId", "precision", "recall", "fMeasure", "executionTime", "matcher","truePositives","falsePositives","falseNegatives"});
            }});
        }});
        return writer;
    }

//    @Bean
//    public JdbcBatchItemWriter<Output> writerJDBC(DataSource dataSource) {
//        return new JdbcBatchItemWriterBuilder<Output>()
//                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
//                .sql("INSERT INTO result (pair_id, exp_id, precision, recall, fmeasure, execution_time, matcher) VALUES (:pairId, :expId, :precision, :recall, :fMeasure, :executionTime, :matcher)")
//                .dataSource(dataSource)
//                .build();
//    }

    @Bean
    public Step processingOntologies() {
        return stepBuilderFactory.get("processingOntologies")
                .<Input,Output> chunk(10)
                .reader(reader())
                .writer(writerCSV())
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
