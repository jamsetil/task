package org.example;

import org.example.config.AppConfig;
import org.example.model.Trainee;
import org.example.model.Training;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

public class Main {

    public static void main(String[] args) {


        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

    }
}