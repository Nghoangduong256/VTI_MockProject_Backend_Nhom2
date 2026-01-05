package com.vti.springdatajpa.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper initModelMapper() {

        ModelMapper modelMapper = new ModelMapper();
        return modelMapper;
    }
}
