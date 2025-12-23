package com.vti.springdatajpa.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper initModelMapper() {
        // Thế:
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration().setSkipNullEnabled(true);
//        Converter<List<ProjectAccount>, List<AccountDtoInDepartment>> projectAccountConverter =
//                ctx -> ctx.getSource() == null ? null :
//                        ctx.getSource().stream()
//                                .map(pa -> {
//                                    AccountDtoInDepartment dto = new AccountDtoInDepartment();
//                                    dto.setId(pa.getAccount().getId());
//                                    dto.setUserName(pa.getAccount().getUsername());
//                                    dto.setEmail(pa.getAccount().getEmail());
//                                    dto.setFirstName(pa.getAccount().getFirstName());
//                                    dto.setLastName(pa.getAccount().getLastName());// nếu có field role
//                                    return dto;
//                                }).collect(Collectors.toList());
//
//        modelMapper.typeMap(Project.class, ProjectDto.class)
//                .addMappings(m -> m.using(projectAccountConverter)
//                        .map(Project::getProjectAccounts, ProjectDto::setAccount));

//        return modelMapper;

        // Duong: Tạo object và cấu hình
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }
}
