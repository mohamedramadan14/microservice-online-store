package mo.mo.estore.productservice.core.errorhandling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class ErrorMessage {
    private final  String message;
    private final Integer httpStatus;
    private final Date timestamp;

}
