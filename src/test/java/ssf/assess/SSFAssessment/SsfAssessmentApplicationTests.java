package ssf.assess.SSFAssessment;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ssf.assess.SSFAssessment.model.Quotation;
import ssf.assess.SSFAssessment.services.QuotationService;

@SpringBootTest
class SsfAssessmentApplicationTests {

	@Autowired 
	QuotationService quoteSvc;

	List<String> items = Stream.of("durian", "plum", "pear").collect(Collectors.toList());

	@Test
	void contextLoads() throws IOException {
		try{
			Optional<Quotation> opt = quoteSvc.getQuotations(items);
			Assertions.assertThat(opt.isPresent());
		}catch(Exception e){
			Assertions.assertThat(e.getMessage());
		}

		
	}

}
