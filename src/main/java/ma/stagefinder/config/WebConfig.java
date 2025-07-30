package ma.stagefinder.config;

import ma.stagefinder.dtos.CandidatureDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

//@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig {
    @Bean
    public PagedResourcesAssembler<CandidatureDTO> pagedResourcesAssembler() {
        return new PagedResourcesAssembler<>(null, null);
    }
}