package io.openjraw;

import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class OpenjrawFxApplication extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        System.out.println(
            getClass().getResource("application.yml")
        );
    
        context = new SpringApplicationBuilder(OpenjrawApplication.class)
                // .properties(
                //     "spring.main.web-application-type=none",
                //     "spring.ai.openai.api-key=dummy"
                // )
                .web(org.springframework.boot.WebApplicationType.NONE)
                .run();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("OpenJraw");
        stage.show();
    }

    @Override
    public void stop() {
        context.close();
    }
}
