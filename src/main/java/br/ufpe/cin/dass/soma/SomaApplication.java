package br.ufpe.cin.dass.soma;
import br.ufpe.cin.dass.soma.config.ApplicationConfig;
import edu.smu.tspell.wordnet.WordNetDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/** Segmented Ontology Matching Algorithm
 * @author Diego Pessoa <derp@cin.ufpe.br>
 **/
@SpringBootApplication(scanBasePackageClasses = SomaApplication.class)
@EnableBatchProcessing
@EnableFeignClients
@EnableConfigurationProperties({ApplicationConfig.class})
public class SomaApplication {

    private Logger log = LoggerFactory.getLogger(SomaApplication.class);

    private final ApplicationConfig applicationConfig;

    public SomaApplication(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(SomaApplication.class, args);
    }
    @Bean
    public WordNetDatabase wordNetDatabase() {
        // Initialize the WordNet interface.

        WordNetDatabase wordNet = null;

        String wordnetdir = applicationConfig.getWordnetDir();

        System.setProperty("wordnet.database.dir", wordnetdir);
        // Instantiate wordnet.
        try {
            wordNet = WordNetDatabase.getFileInstance();
        } catch (Exception e) {
            log.error("Failed to start wordnet database");
        }

        log.info("Wordnet database initialized");

        return wordNet;
    }
}


